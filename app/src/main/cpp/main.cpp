#include <jni.h>
#include <string>

#include "log.h"
#include "application.h"
#include "frame_render.h"

using namespace tc;

std::shared_ptr<Application> g_app = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_app = Application::Make(vm);
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderApp_init(JNIEnv *env, jobject thiz, jboolean ssl, jstring ip, jint port,
                                        jstring path, jobject surface, jboolean hw_codec, jboolean use_oes,
                                        jint oes_tex_id) {
    const char* ip_str = env->GetStringUTFChars(ip, nullptr);
    const char* path_str = env->GetStringUTFChars(path, nullptr);

    g_app->Init(ThunderSdkParams {
            .ssl_ = (bool)ssl,
            .ip_ = ip_str,
            .port_ = port,
            .req_path_ = path_str,
    }, env, surface, (bool)hw_codec, (bool)use_oes, oes_tex_id);

    env->ReleaseStringUTFChars(ip, ip_str);
    env->ReleaseStringUTFChars(path, path_str);
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
Java_com_tc_client_FrameRender_nativeCreate(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnCreate();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_FrameRender_nativeResume(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnResume();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_FrameRender_nativePause(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnPause();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_FrameRender_nativeDestroy(JNIEnv *env, jobject thiz) {
    if (g_app) {
        g_app->OnDestroy();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_tc_client_FrameRender_nativeRenderTick(JNIEnv *env, jobject thiz) {
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