//
// Created by hy on 2024/1/24.
//

#include "frame_render.h"

#include "tc_common/log.h"
#include "tc_common/message_notifier.h"
#include "tc_client_sdk/raw_image.h"
#include "app_context.h"
#include "sdk_messages.h"

static const char *vertexShader = R"(
        attribute vec4 aPosition;
        attribute vec2 aTextCoord;
        varying vec2 vTextCoord;
        void main() {
            //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）
            vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
            gl_Position = aPosition;
        }
)";

static const char *fragYUV420P = R"(
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

GLuint init_shader(const char *source, GLenum type) {
    GLuint sh = glCreateShader(type);
    if (sh == 0) {
        LOGI("glCreateShader %d failed", type);
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
        LOGI("glCompileShader {} failed", type);
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

    void FrameRender::Init(JNIEnv* env, jobject surface) {
        //1.获取原始窗口
        //be sure to use ANativeWindow_release()
        // * when done with it so that it doesn't leak.
        //ANativeWindow *nwin = ANativeWindow_fromSurface(env, surface);
//        ANativeWindow *nwin = ANativeWindow_fromSurface(env, reinterpret_cast<jobject>(surface));
//        //获取Display
//        //EGLDisplay
//        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
//        if (display == EGL_NO_DISPLAY) {
//            LOGI("egl display failed");
//            return;
//        }
//        //2.初始化egl，后两个参数为主次版本号
//        if (EGL_TRUE != eglInitialize(display, 0, 0)) {
//            LOGI("eglInitialize failed");
//            return;
//        }
//
//        //3.1 surface配置，可以理解为窗口
//        EGLConfig eglConfig;
//        EGLint configNum;
//        const EGLint confAttr[] =
//        {
//        EGL_SURFACE_TYPE,EGL_PBUFFER_BIT,//EGL_WINDOW_BIT EGL_PBUFFER_BIT we will create a pixelbuffer surface
//        EGL_RED_SIZE,   8,
//        EGL_GREEN_SIZE, 8,
//        EGL_BLUE_SIZE,  8,
//        EGL_ALPHA_SIZE, 8,// if you need the alpha channel
//        EGL_DEPTH_SIZE, 16,// if you need the depth buffer
//        EGL_STENCIL_SIZE,8,
//        EGL_NONE
//        };
//
//        EGLint attribList[] = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE };
//
//        if (EGL_TRUE != eglChooseConfig(display, confAttr, &eglConfig, 1, &configNum)) {
//            LOGI("eglChooseConfig failed");
//            return;
//        }
//
//        //3.2创建surface(egl和NativeWindow进行关联。最后一个参数为属性信息，0表示默认版本)
//        //EGLSurface
//        winSurface = eglCreateWindowSurface(display, eglConfig, nwin, attribList);
//        if (winSurface == EGL_NO_SURFACE) {
//            LOGI("eglCreateWindowSurface failed {:x}", eglGetError());
//            return;
//        }
//
//        //4 创建关联上下文
//        const EGLint ctxAttr[] = {
//                EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
//        };
//        //EGL_NO_CONTEXT表示不需要多个设备共享上下文
//        EGLContext context = eglCreateContext(display, eglConfig, EGL_NO_CONTEXT, ctxAttr);
//        if (context == EGL_NO_CONTEXT) {
//            LOGI("eglCreateContext failed");
//            return;
//        }
//        //将egl和opengl关联
//        //两个surface一个读一个写。第二个一般用来离线渲染？
//        if (EGL_TRUE != eglMakeCurrent(display, winSurface, winSurface, context)) {
//            LOGI("eglMakeCurrent failed");
//            return;
//        }

        GLuint vsh = init_shader(vertexShader, GL_VERTEX_SHADER);
        GLuint fsh = init_shader(fragYUV420P, GL_FRAGMENT_SHADER);

        GLuint program = glCreateProgram();
        LOGI("program id: {}", program);
        if (program == 0) {
            LOGI("glCreateProgram failed");
            return;
        }

        glAttachShader(program, vsh);
        glAttachShader(program, fsh);

        glLinkProgram(program);
        GLint status = 0;
        glGetProgramiv(program, GL_LINK_STATUS, &status);
        if (status == 0) {
            LOGI("glLinkProgram failed");
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

        glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
        glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
        glUniform1i(glGetUniformLocation(program, "vTexture"), 2);

        glGenTextures(3, texts);
        glBindTexture(GL_TEXTURE_2D, texts[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, texts[1]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, texts[2]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    void FrameRender::UpdateImage(const std::shared_ptr<RawImage>& image) {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        raw_image_ = image->Clone();
    }

    void FrameRender::TickRefresh() {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        if (!raw_image_) {
            return;
        }
        int width = raw_image_->img_width;
        int height = raw_image_->img_height;

        auto y = raw_image_->Data();
        auto u = y + width*height;
        auto v = u + width*height/4;

        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.2, 0.3, 0.4, 1.0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texts[0]);
        if (need_init_texture_) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, y);
        }
        else {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE, GL_UNSIGNED_BYTE, y);
        }

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texts[1]);
        if (need_init_texture_) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width/2, height/2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, u);
        }
        else {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width/2, height/2, GL_LUMINANCE, GL_UNSIGNED_BYTE, u);
        }

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, texts[2]);
        if (need_init_texture_) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width/2, height/2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, v);
        }
        else {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width/2, height/2, GL_LUMINANCE, GL_UNSIGNED_BYTE, v);
        }

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (need_init_texture_) {
            need_init_texture_ = false;
        }
    }

    void FrameRender::RegisterListeners() {
        bus_listener_ = app_context_->ObtainMessageListener();
        bus_listener_->Listen<MsgFirstFrameDecoded>([=](const auto& msg) {
            this->need_init_texture_ = true;
            LOGI("Need to init texture...");
        });
    }

    void FrameRender::OnCreate() {

    }

    void FrameRender::OnResume() {

    }

    void FrameRender::OnPause() {

    }

    void FrameRender::OnDestroy() {

    }

}
