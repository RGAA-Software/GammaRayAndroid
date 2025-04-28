#include <jni.h>
#include <string>

#include "log.h"
#include "application.h"
#include "frame_render.h"
#include "tc_common_new/log.h"
#include "env_wrapper.h"

using namespace tc;

std::shared_ptr<Application> g_app = nullptr;
jobject g_java_app = nullptr;
jclass g_java_class = nullptr;
jmethodID g_cbk_methodID = nullptr;
jmethodID g_cbk_cursor_methodID = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    LOGI("JNI onload");
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderApp_init(JNIEnv *env, jobject thiz, jboolean ssl, jboolean enable_audio, jboolean enable_video, jboolean enable_controller, jstring ip,
                                        jint port, jstring path, jobject surface, jboolean hw_codec, jboolean use_oes, jint oes_tex_id, jstring device_id, jstring stream_id) {
    const char* ip_str = env->GetStringUTFChars(ip, nullptr);
    const char* path_str = env->GetStringUTFChars(path, nullptr);
    const char* device_id_str = env->GetStringUTFChars(device_id, nullptr);
    const char* stream_id_str = env->GetStringUTFChars(stream_id, nullptr);

    g_java_app = env->NewGlobalRef(thiz);
    g_java_class = env->GetObjectClass(thiz);
    g_cbk_methodID = env->GetMethodID(g_java_class, "onNativeMessage", "(Ljava/lang/String;)V");
    g_cbk_cursor_methodID = env->GetMethodID(g_java_class, "onCursorInfo", "(FFIIIIZ[B)V");
    JavaVM* vm;
    env->GetJavaVM(&vm);
    g_app = Application::Make(vm);
    // Really hate to write so many JNI methods, use the same method to pass json-format message
    g_app->RegisterNativeMessageCallback([=](const std::string& msg) {
        auto env_wrapper = g_app->ObtainEnvWrapper();
        if (!env_wrapper || !env_wrapper->Env()) {
            LOGE("RegisterNativeMessageCallback; Can't get a env wrapper.");
            return;
        }
        auto jstr_msg = env_wrapper->Env()->NewStringUTF(msg.c_str());
        env_wrapper->Env()->CallVoidMethod(g_java_app, g_cbk_methodID, jstr_msg);
    });

    g_app->RegisterCursorInfoCallback([=](const tc::CursorInfoSync& cursor) {
        auto env_wrapper = g_app->ObtainEnvWrapper();
        if (!env_wrapper || !env_wrapper->Env()) {
            LOGE("RegisterCursorInfoCallback; Can't get a env wrapper.");
            return;
        }

        const SdkCaptureMonitorInfo& cap_mon_info = g_app->GetCapMonitorInfo();
        if (cap_mon_info.frame_width_ <= 0 || cap_mon_info.frame_height_ <= 0) {
            return;
        }
        auto env = env_wrapper->Env();
        jfloat x = (cursor.x() - cap_mon_info.mon_left_)*1.0f/cap_mon_info.frame_width_;
        jfloat y = (cursor.y() - cap_mon_info.mon_top_)*1.0f/cap_mon_info.frame_height_;
        //LOGI("x ratio: {}, y ratio: {}, cursor x: ({},{}), {}x{} left: {}, right: {}",
        //     x, y, cursor.x(), cursor.y(), cursor.width(), cursor.height(), cap_mon_info.mon_left_, cap_mon_info.mon_right_);
        jint hotspot_x = cursor.hotspot_x();
        jint hotspot_y = cursor.hotspot_y();
        jint width = cursor.width();
        jint height = cursor.height();
        jboolean visible = cursor.visible();
        auto bitmap = cursor.bitmap();
        auto cursorArray = env->NewByteArray(bitmap.size());
        if (!cursorArray) {
            return;
        }
        env->SetByteArrayRegion(cursorArray, 0, static_cast<jsize>(bitmap.size()), reinterpret_cast<const jbyte*>(bitmap.data()));
        //int x, int y, int hotspotX, int hotspotY, int width, int height, boolean visible, byte[] data
        env->CallVoidMethod(g_java_app, g_cbk_cursor_methodID, x, y, hotspot_x, hotspot_y, width, height, visible, cursorArray);
    });

    // todo: Params
    g_app->Init(std::make_shared<ThunderSdkParams>(ThunderSdkParams{
            .ssl_ = (bool)ssl,
            .enable_audio_ = (bool) enable_audio,
            .enable_video_ = (bool) enable_video,
            .enable_controller_ = (bool)enable_controller,
            .ip_ = ip_str,
            .port_ = port,
            .device_id_ = device_id_str,
            .stream_id_ = stream_id_str,
    }), env, surface, (bool)hw_codec, (bool)use_oes, oes_tex_id);

    env->ReleaseStringUTFChars(ip, ip_str);
    env->ReleaseStringUTFChars(path, path_str);
    env->ReleaseStringUTFChars(device_id, device_id_str);
    env->ReleaseStringUTFChars(stream_id, stream_id_str);
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderApp_start(JNIEnv *env, jobject thiz) {
    g_app->Start();
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderApp_stop(JNIEnv *env, jobject thiz) {
    g_app->Exit();
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_nativeCreate(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnCreate();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_nativeResume(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnResume();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_nativePause(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnPause();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_nativeDestroy(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnDestroy();
        g_app.reset();
        g_app = nullptr;
    }
    if (g_java_app) {
        env->DeleteGlobalRef(g_java_app);
        g_java_app = nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_nativeRenderTick(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnRenderTick(env);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_sendGamepadState(JNIEnv *env, jobject thiz, jint buttons,
                                                    jint left_trigger, jint right_trigger,
                                                    jint thumb_lx, jint thumb_ly, jint thumb_rx,
                                                    jint thumb_ry) {
    if (g_app) {
        g_app->SendGamepadState(buttons, left_trigger, right_trigger, thumb_lx, thumb_ly, thumb_rx, thumb_ry);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_impl_ThunderApp_sendMouseEvent(JNIEnv *env, jobject thiz, jint event, jfloat x_ratio, jfloat y_ratio) {
    if (g_app) {
        g_app->SendMouseEvent(event, x_ratio, y_ratio);
    }
}
