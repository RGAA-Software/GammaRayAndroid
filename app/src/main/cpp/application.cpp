//
// Created by hy on 2024/1/24.
//

#include "application.h"
#include "env_wrapper.h"
#include "frame_render.h"
#include "app_context.h"
#include "audio_player.h"
#include "tc_client_sdk_new/video_decoder_factory.h"
#include "tc_message_new/proto_message_maker.h"
#include "native_msg_maker.h"

namespace tc
{

    std::shared_ptr<Application> Application::Make(const JavaVM* vm) {
        return std::make_shared<Application>(vm);
    }

    Application::Application(const JavaVM* vm) {
        vm_ = const_cast<JavaVM*>(vm);
    }

    std::shared_ptr<EnvWrapper> Application::ObtainEnvWrapper() {
        return EnvWrapper::Make(vm_);
    }

    void Application::Init(const ThunderSdkParams& params, JNIEnv* env, jobject surface, bool hw_codec, bool use_oes, int oes_tex_id) {
        app_context_ = AppContext::Make();
        frame_render_ = FrameRender::Make(app_context_);
        auto drt = [&]() -> DecoderRenderType {
            if (!hw_codec) {
                return DecoderRenderType::kFFmpegI420;
            }
            else if (hw_codec) {
                if (use_oes) {
                    return DecoderRenderType::kMediaCodecSurface;
                }
                else {
                    return DecoderRenderType::kMediaCodecNv21;
                }
            }
            else {
                return DecoderRenderType::kMediaCodecSurface;
            }
        }();
        frame_render_->Init(env, surface, drt, oes_tex_id);

        thunder_sdk_ = ThunderSdk::Make(app_context_->GetMessageNotifier());
        thunder_sdk_->Init(params, use_oes ? frame_render_->GetNativeWindow() : nullptr, drt);
        thunder_sdk_->RegisterOnVideoFrameDecodedCallback([=, this](const std::shared_ptr<RawImage>& image) {
            if (drt != DecoderRenderType::kMediaCodecSurface && image->img_buf) {
                frame_render_->UpdateYUVImage(image);
            }

            if (frame_width_ != image->img_width || frame_height_ != image->img_height) {
                if (native_msg_cbk_) {
                    auto frame_change_msg = NativeMsgMaker::MakeFrameInfoMessage(image->img_width, image->img_height, image->img_format);
                    native_msg_cbk_(frame_change_msg);
                }
                frame_width_ = image->img_width;
                frame_height_ = image->img_height;
            }
        });

        thunder_sdk_->RegisterOnAudioFrameDecodedCallback([=, this](const std::shared_ptr<Data>& data, int samples, int channels, int bits) {
            if (!audio_player_) {
                audio_player_ = AudioPlayer::Make();
                audio_player_->Init(samples, channels);
            }
            audio_player_->Write(data);
        });

        LOGI("hw codec:{}, use oes: {}, oes tex id: {}", hw_codec, use_oes, oes_tex_id);
    }

    void Application::Start() {
        thunder_sdk_->Start();
    }

    void Application::Exit() {
        thunder_sdk_->Exit();
    }

    std::shared_ptr<FrameRender> Application::GetFrameRender() {
        return frame_render_;
    }

    void Application::OnRenderTick(JNIEnv* env) {
        if (frame_render_) {
            frame_render_->TickRefresh(env);
        }
    }

    void Application::OnCreate() {
        if (frame_render_) {
            frame_render_->OnCreate();
        }
    }

    void Application::OnResume() {
        if (frame_render_) {
            frame_render_->OnResume();
        }
    }

    void Application::OnPause() {
        if (frame_render_) {
            frame_render_->OnPause();
        }
    }

    void Application::OnDestroy() {
        if (thunder_sdk_) {
            thunder_sdk_->Exit();
        }
        if (frame_render_) {
            frame_render_->OnDestroy();
        }
    }

    void Application::SendGamepadState(int32_t buttons, int32_t left_trigger,int32_t right_trigger,
                                       int32_t thumb_lx, int32_t thumb_ly, int32_t thumb_rx, int32_t thumb_ry) {
        auto msg = ProtoMessageMaker::MakeGamepadState(buttons, left_trigger, right_trigger, thumb_lx, thumb_ly, thumb_rx, thumb_ry);
        if (thunder_sdk_) {
            thunder_sdk_->PostBinaryMessage(msg);
        }
    }

}