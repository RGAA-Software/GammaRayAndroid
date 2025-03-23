//
// Created by hy on 2024/1/24.
//

#include "frame_render.h"

#include "tc_common_new/log.h"
#include "tc_common_new/message_notifier.h"
#include "tc_common_new/time_ext.h"
#include "tc_client_sdk_new/gl/raw_image.h"
#include "app_context.h"
#include "sdk_messages.h"

#include <android/native_window.h>
#include <android/native_activity.h>
#include <android/native_window_jni.h>
#include <android/rect.h>
#include <android/surface_control.h>
#include <android/window.h>
#include <GLES2/gl2ext.h>

static const char *kVertexShader = R"(#version 320 es
        in vec4 aPosition;
        in vec2 aTextCoord;
        out vec2 vTextCoord;
        void main() {
            //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）
            vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
            gl_Position = aPosition;
        }
)";

static const char *kFragYUV420P = R"(#version 320 es
        precision mediump float;
        in vec2 vTextCoord;

        uniform sampler2D yTexture;
        uniform sampler2D uTexture;
        uniform sampler2D vTexture;

        out vec4 FragColor;
        void main() {
            vec3 yuv;
            vec3 rgb;

            yuv.x = texture(yTexture, vTextCoord).g;
            yuv.y = texture(uTexture, vTextCoord).g - 0.5;
            yuv.z = texture(vTexture, vTextCoord).g - 0.5;
            rgb = mat3(
                    1.0, 1.0, 1.0,
                    0.0, -0.39465, 2.03211,
                    1.13983, -0.5806, 0.0
            ) * yuv;

            FragColor = vec4(rgb, 1.0);
        }
)";

static const char* kFragOES = R"(#version 320 es
    #extension GL_OES_EGL_image_external_essl3 : require

    precision mediump float;
    in vec2 vTextCoord;
    uniform samplerExternalOES sTexture;

    out vec4 FragColor;

    void main() {
        FragColor = texture(sTexture, vTextCoord);
        //FragColor = vec4(1.0, 0.0, 1.0, 1.0);
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

    void FrameRender::Init(JNIEnv* env, jobject surface, const DecoderRenderType& drt, int oes_tex_id) {
        decoder_render_type_ = drt;
        oes_tex_id_ = oes_tex_id;
        if (surface) {
            decode_win_surface_ = ANativeWindow_fromSurface(env, surface);
        }

        GL_FUNC glGenVertexArrays(1, &video_vao_);
        GL_FUNC glBindVertexArray(video_vao_);

        GLuint vsh = init_shader(kVertexShader, GL_VERTEX_SHADER);
        GLuint fsh = 0;
        if (drt == DecoderRenderType::kFFmpegI420) {
            fsh = init_shader(kFragYUV420P, GL_FRAGMENT_SHADER);
        }
        else if (drt == DecoderRenderType::kMediaCodecSurface) {
            fsh = init_shader(kFragOES, GL_FRAGMENT_SHADER);
        }

        program_ = glCreateProgram();

        glAttachShader(program_, vsh);
        glAttachShader(program_, fsh);

        glLinkProgram(program_);
        GLint status = 0;
        glGetProgramiv(program_, GL_LINK_STATUS, &status);
        if (status == 0) {
            char check_info[1024];
            glGetProgramInfoLog(program_, 1024, NULL, check_info);
            LOGI("glLinkProgram failed: {}", check_info);
            return;
        }

        glUseProgram(program_);
        LOGI("glLinkProgram success");

        {
            static float ver[] = {
                    1.0f, -1.0f, 0.0f,
                    -1.0f, -1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f
            };

            GLuint vbo;
            glGenBuffers(1, &vbo);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, sizeof(ver), ver, GL_DYNAMIC_DRAW);

            int posLoc = glGetAttribLocation(program_, "aPosition");
            GL_FUNC glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 0, 0);
            GL_FUNC glEnableVertexAttribArray(posLoc);
        }

        {
            static float fragment[] = {
                    1.0f, 0.0f,
                    0.0f, 0.0f,
                    1.0f, 1.0f,
                    0.0f, 1.0f
            };

            GLuint vbo_tex_coord;
            glGenBuffers(1, &vbo_tex_coord);
            glBindBuffer(GL_ARRAY_BUFFER, vbo_tex_coord);
            glBufferData(GL_ARRAY_BUFFER, sizeof(fragment), fragment, GL_DYNAMIC_DRAW);

            int tex_coord_loc = glGetAttribLocation(program_, "aTextCoord");
            GL_FUNC glVertexAttribPointer(tex_coord_loc, 2, GL_FLOAT, false, 0, 0);
            GL_FUNC glEnableVertexAttribArray(tex_coord_loc);
        }

        if (decoder_render_type_ == DecoderRenderType::kFFmpegI420) {
            glUniform1i(glGetUniformLocation(program_, "yTexture"), 0);
            glUniform1i(glGetUniformLocation(program_, "uTexture"), 1);
            glUniform1i(glGetUniformLocation(program_, "vTexture"), 2);

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
        else if (drt == DecoderRenderType::kMediaCodecSurface) {
            /// Another texture beg
//            glGenTextures(1, &decode_texture_);
//            glBindTexture(GL_TEXTURE_EXTERNAL_OES, decode_texture_);
//            glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//            glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        GL_FUNC glBindVertexArray(0);
        is_gl_inited_ = true;
    }

    void FrameRender::UpdateYUVImage(const std::shared_ptr<RawImage>& image) {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        if (!current_raw_image_ && image) {
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

    void FrameRender::TickRefresh(JNIEnv* env) {
        std::lock_guard<std::mutex> guard(raw_image_mtx_);
        if (!is_gl_inited_ || exit_) {
            return;
        }

        glUseProgram(program_);
        glBindVertexArray(video_vao_);

        if (decoder_render_type_ == DecoderRenderType::kFFmpegI420 && current_raw_image_) {
            int width = current_raw_image_->img_width;
            int height = current_raw_image_->img_height;
            auto y = current_raw_image_->Data();
            auto u = y + width * height;
            auto v = u + width * height / 4;
            auto beg = TimeExt::GetCurrentTimestamp();
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
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0,
                             GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, u);
            } else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                                GL_UNSIGNED_BYTE, u);
            }

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, img_textures_[2]);
            if (need_init_texture_) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0,
                             GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, v);
            } else {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                                GL_UNSIGNED_BYTE, v);
            }

            auto end = TimeExt::GetCurrentTimestamp();
            LOGI("upload to gpu used: {}ms", (end-beg));
        }
        else if (decoder_render_type_ == DecoderRenderType::kMediaCodecSurface) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, oes_tex_id_);
            glUniform1i(glGetUniformLocation(program_, "sTexture"), 0);
            //LOGI("Program image location: {} texture id: {}", glGetUniformLocation(program_, "sTexture"), oes_tex_id_);
        }

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (need_init_texture_) {
            need_init_texture_ = false;
        }

        glUseProgram(0);
        glBindVertexArray(0);

    }

    void FrameRender::RegisterListeners() {
        bus_listener_ = app_context_->ObtainMessageListener();
        bus_listener_->Listen<SdkMsgFirstVideoFrameDecoded>([=, this](const auto& msg) {
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
        exit_ = true;
    }

}
