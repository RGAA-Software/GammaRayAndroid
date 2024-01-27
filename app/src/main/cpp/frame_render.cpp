//
// Created by hy on 2024/1/24.
//

#include "frame_render.h"

#include "tc_common/log.h"
#include "tc_common/message_notifier.h"
#include "tc_client_sdk/raw_image.h"
#include "app_context.h"
#include "sdk_messages.h"

#include <android/native_window.h>
#include <android/native_activity.h>
#include <android/native_window_jni.h>
#include <android/rect.h>
#include <android/surface_control.h>
#include <android/window.h>
#include <GLES2/gl2ext.h>

static const char *kVertexShader = R"(
        attribute vec4 aPosition;
        attribute vec2 aTextCoord;
        varying vec2 vTextCoord;
        void main() {
            //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）
            vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
            gl_Position = aPosition;
        }
)";

static const char *kFragYUV420P = R"(
        precision mediump float;
        varying vec2 vTextCoord;

        uniform sampler2D yTexture;
        uniform sampler2D uTexture;
        uniform sampler2D vTexture;
        void main() {
            vec3 yuv;
            vec3 rgb;

            yuv.x = texture2D(yTexture, vTextCoord).g;
            yuv.y = texture2D(uTexture, vTextCoord).g - 0.5;
            yuv.z = texture2D(vTexture, vTextCoord).g - 0.5;
            rgb = mat3(
                    1.0, 1.0, 1.0,
                    0.0, -0.39465, 2.03211,
                    1.13983, -0.5806, 0.0
            ) * yuv;

            gl_FragColor = vec4(rgb, 1.0);
        }
)";

static const char* kFragRGB = "#extension GL_OES_EGL_image_external : require\n"
                                 "precision mediump float;\n"
                                 "uniform mat4 tx_matrix;\n"
                                 "uniform samplerExternalOES tex_y;\n"
                                 "varying vec2 tx;\n"
                                 "void main(){\n"
                                 "    vec2 tx_transformed = (tx_matrix * vec4(tx, 0, 1.0)).xy;\n"
                                 "    gl_FragColor = texture2D(tex_y, tx_transformed);\n"
                                 "}\n";

static const char* kFragNV12 = R"(

    precision highp float;
    #extension GL_OES_EGL_image_external : require
    uniform samplerExternalOES image;

    varying vec2 vTextCoord;

    uniform sampler2D image1;
    uniform sampler2D image2;

//    const vec3 delyuv = vec3(0, -128.0/255.0,-128.0/255.0);
//    const vec3 matYUVRGB1 = vec3(1.164, 0.0, 1.596);
//    const vec3 matYUVRGB2 = vec3(1.164, -0.391, -0.813);
//    const vec3 matYUVRGB3 = vec3(1.164, 2.018, 0.0);

    //out vec4 FragColor;

    void main()
    {
        vec4 yColor = texture2D(image1, vTextCoord);
        vec4 uvColor = texture2D(image2, vTextCoord);

        highp vec3 yuv;
        //vec3 CurResult;

        yuv.x = yColor.r;
        yuv.y = uvColor.r - 0.5;
        yuv.z = uvColor.a - 0.5;

//        yuv += delyuv;

//        CurResult.x = dot(yuv,matYUVRGB1);
//        CurResult.y = dot(yuv,matYUVRGB2);
//        CurResult.z = dot(yuv,matYUVRGB3);
//
//        highp vec3 rgb = mat3(1.164, 1.164, 1.164,
//							0, 		 -0.392, 	2.017,
//							1.596,   -0.813,    0.0) * yuv;
        highp vec3 rgb =mat3( 1.0,       1.0,           1.0,
                0.0,         -0.344,     1.770,
                1.403,  -0.714,       0.0) * yuv;
        gl_FragColor = vec4(rgb, 1);
    }

)";

GLuint init_shader(const char *source, GLenum type) {
    GLuint sh = glCreateShader(type);
    if (sh == 0) {
        LOGI("glCreateShader {} failed", type);
        return 0;
    }

    glShaderSource(sh,
                   1,
                   &source,
                   0);
    glCompileShader(sh);

    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if (status == 0) {
        char check_info[1024] = {0};
        glGetShaderInfoLog(sh, 1024, NULL, check_info);
        LOGI("shader error: {}", check_info);
        return 0;
    }
    return sh;
}

namespace tc
{

    std::shared_ptr<FrameRender> FrameRender::Make(const std::shared_ptr<AppContext>& ctx) {
        return std::make_shared<FrameRender>(ctx);
    }

    FrameRender::FrameRender(const std::shared_ptr<AppContext>& ctx) {
        app_context_ = ctx;
        RegisterListeners();
    }

    void FrameRender::Init(JNIEnv* env, jobject surface, bool hw_codec) {
        raw_image_format_ = hw_codec ? RawImageFormat::kNV12 : RawImageFormat::kI420;
        if (native_win_) {
            ANativeWindow_release(native_win_);
            native_win_ = nullptr;
        }
        native_win_ = ANativeWindow_fromSurface(env, reinterpret_cast<jobject>(surface));
        LOGI("native win: {}", (void*)native_win_);

        display_ = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (display_ == EGL_NO_DISPLAY) {
            LOGI("egl display failed");
            return;
        }
        //2.初始化egl，后两个参数为主次版本号
        if (EGL_TRUE != eglInitialize(display_, 0, 0)) {
            LOGI("eglInitialize failed");
            return;
        }

        //3.1 surface配置，可以理解为窗口
        EGLConfig eglConfig;
        EGLint configNum;
        const EGLint attribs[] = {EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                                  EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                                  EGL_BLUE_SIZE, 8, EGL_GREEN_SIZE, 8,
                                  EGL_RED_SIZE, 8, EGL_ALPHA_SIZE, 8,
                                  EGL_DEPTH_SIZE, 0, EGL_STENCIL_SIZE, 0,
                                  EGL_NONE};

        if (EGL_TRUE != eglChooseConfig(display_, attribs, &eglConfig, 1, &configNum)) {
            LOGI("eglChooseConfig failed");
            return;
        }

        //3.2创建surface(egl和NativeWindow进行关联。最后一个参数为属性信息，0表示默认版本)
        win_surface_ = eglCreateWindowSurface(display_, eglConfig, native_win_, nullptr);
        if (win_surface_ == EGL_NO_SURFACE) {
            LOGI("eglCreateWindowSurface failed {:x}", eglGetError());
            return;
        }

        //4 创建关联上下文
        const EGLint ctxAttr[] = {
                EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE
        };

        egl_context_ = eglCreateContext(display_, eglConfig, EGL_NO_CONTEXT, ctxAttr);
        if (egl_context_ == EGL_NO_CONTEXT) {
            LOGI("eglCreateContext failed");
            return;
        }
        //将egl和opengl关联
        //两个surface一个读一个写。第二个一般用来离线渲染？
        if (EGL_TRUE != eglMakeCurrent(display_, win_surface_, win_surface_, egl_context_)) {
            LOGI("eglMakeCurrent failed");
            return;
        }

        /// Another texture beg
        glGenTextures(1, &decode_texture_);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, decode_texture_);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        jclass clazzIdSurfaceTexture = env->FindClass("android/graphics/SurfaceTexture");
        if(!clazzIdSurfaceTexture)
            return;
        jmethodID surfaceTextureConstructionId = env->GetMethodID(clazzIdSurfaceTexture,"<init>","(I)V");
        if(!surfaceTextureConstructionId)
            return;
        mSurfaceTextureUpdateTexImageMID = env->GetMethodID(clazzIdSurfaceTexture, "updateTexImage", "()V");
        if(!mSurfaceTextureUpdateTexImageMID)
            return;
        mSurfaceGetTransformMatrixMID = env->GetMethodID(clazzIdSurfaceTexture,"getTransformMatrix", "([F)V");
        if(!mSurfaceGetTransformMatrixMID)
            return;
        jobject surfaceTextureObj = env->NewObject(clazzIdSurfaceTexture,surfaceTextureConstructionId,(jint)decode_texture_);
        if(!surfaceTextureObj)
            return;
        mSurfaceTextureObj = env->NewGlobalRef(surfaceTextureObj);
        if(!mSurfaceTextureObj)
            return;
        mSurfaceTextureReleaseMID = env->GetMethodID(clazzIdSurfaceTexture, "release", "()V");
        if (mSurfaceTextureReleaseMID == nullptr){
            return;
        }
        jclass clazzIdSurface = env->FindClass("android/view/Surface");
        if(!clazzIdSurface)
            return;
        jmethodID surfaceConstructionId = env->GetMethodID(clazzIdSurface,"<init>", "(Landroid/graphics/SurfaceTexture;)V");
        if(!surfaceConstructionId)
            return;
        jobject surfaceObj = env->NewObject(clazzIdSurface,surfaceConstructionId,mSurfaceTextureObj);
        if(!surfaceObj)
            return;
        decode_win_surface_ = ANativeWindow_fromSurface(env, surfaceObj);

        /// another texture end

        GLuint vsh = init_shader(kVertexShader, GL_VERTEX_SHADER);
        GLuint fsh = 0;
        if (raw_image_format_ == RawImageFormat::kI420) {
            fsh = init_shader(kFragYUV420P, GL_FRAGMENT_SHADER);
        }
        else if (raw_image_format_ == RawImageFormat::kNV12) {
            fsh = init_shader(kFragNV12, GL_FRAGMENT_SHADER);
        }

        GLuint program = glCreateProgram();

        glAttachShader(program, vsh);
        glAttachShader(program, fsh);

        glLinkProgram(program);
        GLint status = 0;
        glGetProgramiv(program, GL_LINK_STATUS, &status);
        if (status == 0) {
            char check_info[1024];
            glGetProgramInfoLog(program, 1024, NULL, check_info);
            LOGI("glLinkProgram failed: {}", check_info);
            return;
        }
        glUseProgram(program);

        LOGI("glLinkProgram success");

        static float ver[] = {
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f
        };

        auto apos = static_cast<GLuint>(glGetAttribLocation(program, "aPosition"));
        glEnableVertexAttribArray(apos);
        glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 0, ver);

        static float fragment[] = {
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };
        auto aTex = static_cast<GLuint>(glGetAttribLocation(program, "aTextCoord"));
        glEnableVertexAttribArray(aTex);
        glVertexAttribPointer(aTex, 2, GL_FLOAT, GL_FALSE, 0, fragment);

        if (raw_image_format_ == RawImageFormat::kI420) {
            glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
            glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
            glUniform1i(glGetUniformLocation(program, "vTexture"), 2);

            glGenTextures(3, img_textures_);
            glBindTexture(GL_TEXTURE_2D, img_textures_[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glBindTexture(GL_TEXTURE_2D, img_textures_[1]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glBindTexture(GL_TEXTURE_2D, img_textures_[2]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        else if (raw_image_format_ == RawImageFormat::kNV12) {
            glUniform1i(glGetUniformLocation(program, "image1"), 0);
            glUniform1i(glGetUniformLocation(program, "image2"), 1);

            glGenTextures(1, &y_texture_id_);
            glBindTexture(GL_TEXTURE_2D, y_texture_id_);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glGenTextures(1, &uv_texture_id_);
            glBindTexture(GL_TEXTURE_2D, uv_texture_id_);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
    }

    void FrameRender::UpdateYUVImage(const std::shared_ptr<RawImage>& image) {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        if (!current_raw_image_) {
            current_raw_image_ = image->Clone();
        }
        else {
            if (current_raw_image_->Size() != image->Size()) {
                current_raw_image_ = image->Clone();
            } else {
                image->CopyTo(current_raw_image_);
            }
        }
    }

    void FrameRender::TickRefresh() {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        if (!current_raw_image_) {
            return;
        }
        int width = current_raw_image_->img_width;
        int height = current_raw_image_->img_height;

        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.2, 0.3, 0.4, 1.0);

        if (raw_image_format_ == RawImageFormat::kI420) {
            auto y = current_raw_image_->Data();
            auto u = y + width*height;
            auto v = u + width*height/4;

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, img_textures_[0]);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, y);
            } else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE,
                                GL_UNSIGNED_BYTE, y);
            }

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, img_textures_[1]);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, u);
            } else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                                GL_UNSIGNED_BYTE, u);
            }

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, img_textures_[2]);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, v);
            } else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                                GL_UNSIGNED_BYTE, v);
            }
        }
        else if (raw_image_format_ == RawImageFormat::kNV12) {
            auto y = current_raw_image_->Data();
            auto uv = y + width*height;

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, y_texture_id_);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, y);
            }
            else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE, GL_UNSIGNED_BYTE, y);
            }

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, uv_texture_id_);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE_ALPHA, height / 2, height / 2, 0, GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, uv);
            }
            else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width/2, height/2, GL_LUMINANCE, GL_UNSIGNED_BYTE, uv);
            }
        }

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (need_init_texture_) {
            need_init_texture_ = false;
        }

        if (win_surface_) {
            eglSwapBuffers(display_, win_surface_);
        }
    }

    void FrameRender::RegisterListeners() {
        bus_listener_ = app_context_->ObtainMessageListener();
        bus_listener_->Listen<MsgFirstFrameDecoded>([=](const auto& msg) {
            this->need_init_texture_ = true;
            LOGI("Need to init texture...");
        });
    }

    ANativeWindow* FrameRender::GetNativeWindow() {
        return decode_win_surface_;
    }

    void FrameRender::OnCreate() {

    }

    void FrameRender::OnResume() {

    }

    void FrameRender::OnPause() {

    }

    void FrameRender::OnDestroy() {
        if (native_win_) {
            ANativeWindow_release(native_win_);
        }
        eglDestroySurface(display_, win_surface_);
        eglDestroyContext(display_, egl_context_);
        eglTerminate(display_);
    }

}
