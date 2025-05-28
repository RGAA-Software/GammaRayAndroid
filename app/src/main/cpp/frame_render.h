//
// Created by hy on 2024/1/24.
//

#ifndef TC_CLIENT_ANDROID_FRAME_RENDER_H
#define TC_CLIENT_ANDROID_FRAME_RENDER_H

#include <memory>
#include <mutex>

#include <android/native_window_jni.h>
#include <jni.h>

#include "tc_client_sdk_new/gl/raw_image.h"
#include "tc_client_sdk_new/sdk_decoder_render_type.h"
#include "gl/shader_program.h"

namespace tc
{

    class AppContext;
    class MessageListener;

    class FrameRender {
    public:

        static std::shared_ptr<FrameRender> Make(const std::shared_ptr<AppContext>& ctx);

        explicit FrameRender(const std::shared_ptr<AppContext>& ctx);

        void Init(JNIEnv* env, jobject surface, const DecoderRenderType& drt, int oes_tex_id);
        void UpdateYUVImage(const std::shared_ptr<RawImage>& image);
        void TickRefresh(JNIEnv* env);
        ANativeWindow* GetNativeWindow();

        void OnCreate();
        void OnResume();
        void OnPause();
        void OnDestroy();

    private:

        void RegisterListeners();

    private:

        std::shared_ptr<AppContext> app_context_ = nullptr;
        std::shared_ptr<MessageListener> bus_listener_ = nullptr;

        DecoderRenderType decoder_render_type_;

        GLuint program_;

        // another texture for decoder
        GLuint video_vao_ = 0;
        GLuint oes_tex_id_ = 0;
        ANativeWindow* decode_win_surface_ = nullptr;
        bool use_oes_ = false;

        // I420
        GLuint img_textures_[3] = {0};

        // NV12
        GLuint y_texture_id_ = 0;
        GLuint uv_texture_id_ = 0;

        std::mutex raw_image_mtx_;
        std::shared_ptr<RawImage> current_raw_image_ = nullptr;

        bool need_init_texture_ = false;
        bool is_gl_inited_ = false;
        bool exit_ = false;
    };

}

#endif //TC_CLIENT_ANDROID_FRAME_RENDER_H
