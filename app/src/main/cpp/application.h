//
// Created by hy on 2024/1/24.
//

#ifndef TC_CLIENT_ANDROID_APPLICATION_H
#define TC_CLIENT_ANDROID_APPLICATION_H

#include <jni.h>
#include <memory>
#include <functional>

#include "thunder_sdk.h"

namespace tc
{

    class EnvWrapper;
    class FrameRender;
    class AppContext;
    class AudioPlayer;
    class Statistics;

    using OnNativeMessageCallback = std::function<void(const std::string&)>;

    class Application {
    public:
        static std::shared_ptr<Application> Make(const JavaVM* vm);

        Application(const JavaVM* vm);
        std::shared_ptr<EnvWrapper> ObtainEnvWrapper();

        void Init(const std::shared_ptr<ThunderSdkParams>& params, JNIEnv* env, jobject surface, bool hw_codec, bool use_oes, int oes_tex_id);
        void Start();
        void Exit();

        std::shared_ptr<FrameRender> GetFrameRender();
        void OnRenderTick(JNIEnv* env);

        void OnCreate();
        void OnResume();
        void OnPause();
        void OnDestroy();

        void SendGamepadState(int32_t buttons, int32_t left_trigger, int32_t right_trigger, int32_t thumb_lx,
                              int32_t thumb_ly, int32_t thumb_rx, int32_t thumb_ry);

        void SendMouseEvent(int32_t event, float x_ratio, float y_ratio);

        void RegisterNativeMessageCallback(OnNativeMessageCallback&& cbk) { native_msg_cbk_ = cbk; }
        void RegisterCursorInfoCallback(OnCursorInfoSyncMsgCallback&& cbk) { cursor_info_cbk_ = cbk; }

        const SdkCaptureMonitorInfo& GetCapMonitorInfo() const;

    private:
        JavaVM* vm_ = nullptr;
        std::shared_ptr<ThunderSdk> thunder_sdk_ = nullptr;
        std::shared_ptr<FrameRender> frame_render_ = nullptr;
        std::shared_ptr<AppContext> app_context_ = nullptr;
        std::shared_ptr<AudioPlayer> audio_player_ = nullptr;
        std::mutex native_msg_cbk_mtx_;
        OnNativeMessageCallback native_msg_cbk_ = nullptr;
        OnCursorInfoSyncMsgCallback  cursor_info_cbk_ = nullptr;

        int frame_width_ = 0;
        int frame_height_ = 0;
        SdkCaptureMonitorInfo cap_mon_info_;

        SdkStatistics* statistics_ = nullptr;
        std::shared_ptr<ThunderSdkParams> sdk_params_ = nullptr;

    };

}

#endif //TC_CLIENT_ANDROID_APPLICATION_H
