//
// Created by hy on 2024/1/24.
//

#ifndef TC_CLIENT_ANDROID_FRAME_RENDER_H
#define TC_CLIENT_ANDROID_FRAME_RENDER_H

#include <memory>
#include <mutex>

#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <jni.h>

#include "raw_image.h"

namespace tc
{

    class AppContext;
    class MessageListener;

    class FrameRender {
    public:

        static std::shared_ptr<FrameRender> Make(const std::shared_ptr<AppContext>& ctx);

        explicit FrameRender(const std::shared_ptr<AppContext>& ctx);

        void Init(JNIEnv* env, jobject surface, bool hw_codec);
        void UpdateYUVImage(const std::shared_ptr<RawImage>& image);
        void TickRefresh();
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

        RawImageFormat raw_image_format_;

        GLuint program_;

        // another texture for decoder
        GLuint decode_texture_ = 0;
        ANativeWindow* decode_win_surface_ = nullptr;
        bool use_oes_ = false;

        // I420
        GLuint img_textures_[3] = {0};

        // NV12
        GLuint y_texture_id_ = 0;
        GLuint uv_texture_id_ = 0;

        EGLDisplay display_{};
        EGLSurface win_surface_{};
        EGLContext egl_context_{};
        ANativeWindow* native_win_ = nullptr;

        std::mutex raw_image_mtx_;
        std::shared_ptr<RawImage> current_raw_image_ = nullptr;

        bool need_init_texture_ = false;

        jobject mSurfaceTextureObj = nullptr;
        jmethodID mSurfaceTextureUpdateTexImageMID = nullptr;
        jmethodID mSurfaceGetTransformMatrixMID = nullptr;
        jmethodID mSurfaceTextureReleaseMID = nullptr;
    };

}

#endif //TC_CLIENT_ANDROID_FRAME_RENDER_H
