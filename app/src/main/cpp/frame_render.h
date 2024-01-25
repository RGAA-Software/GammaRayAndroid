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

namespace tc
{

    class RawImage;

    class FrameRender {
    public:

        static std::shared_ptr<FrameRender> Make();

        explicit FrameRender();

        void Init(JNIEnv* env, jobject surface);
        void UpdateImage(const std::shared_ptr<RawImage>& image);

        void TickRefresh();

        void OnCreate();
        void OnResume();
        void OnPause();
        void OnDestroy();

    private:

        GLuint texts[3] = {0};
        EGLDisplay display;
        EGLSurface winSurface;

        std::mutex raw_image_mtx_;
        std::shared_ptr<RawImage> raw_image_ = nullptr;
    };

}

#endif //TC_CLIENT_ANDROID_FRAME_RENDER_H
