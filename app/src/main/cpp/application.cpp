//
// Created by hy on 2024/1/24.
//

#include "application.h"
#include "env_wrapper.h"
#include "frame_render.h"
#include "app_context.h"

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

    void Application::Init(const ThunderSdkParams& params, JNIEnv* env, jobject surface, bool hw_codec, bool use_oes) {
        app_context_ = AppContext::Make();
        frame_render_ = FrameRender::Make(app_context_);
        frame_render_->Init(env, surface, hw_codec);

        thunder_sdk_ = ThunderSdk::Make(app_context_->GetMessageNotifier());
        thunder_sdk_->Init(params, use_oes ? frame_render_->GetNativeWindow() : nullptr, hw_codec);
        thunder_sdk_->RegisterOnVideoFrameDecodedCallback([=](const std::shared_ptr<RawImage>& image) {
            frame_render_->UpdateYUVImage(image);
        });

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

}