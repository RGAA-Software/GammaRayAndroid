//
// Created by hy on 2024/1/24.
//

#ifndef TC_CLIENT_ANDROID_APPLICATION_H
#define TC_CLIENT_ANDROID_APPLICATION_H

#include <jni.h>
#include <memory>

#include "thunder_sdk.h"

namespace tc
{

    class EnvWrapper;
    class FrameRender;
    class AppContext;

    class Application {
    public:
        static std::shared_ptr<Application> Make(const JavaVM* vm);

        Application(const JavaVM* vm);
        std::shared_ptr<EnvWrapper> ObtainEnvWrapper();

        void Init(const ThunderSdkParams& params, JNIEnv* env, jobject surface, bool hw_codec, bool use_oes, int oes_tex_id);
        void Start();
        void Exit();

        std::shared_ptr<FrameRender> GetFrameRender();
        void OnRenderTick(JNIEnv* env);

        void OnCreate();
        void OnResume();
        void OnPause();
        void OnDestroy();

    private:
        JavaVM* vm_ = nullptr;
        std::shared_ptr<ThunderSdk> thunder_sdk_ = nullptr;
        std::shared_ptr<FrameRender> frame_render_ = nullptr;
        std::shared_ptr<AppContext> app_context_ = nullptr;
    };

}

#endif //TC_CLIENT_ANDROID_APPLICATION_H
