#include <jni.h>
#include <string>

#include "thunder_sdk.h"
#include "log.h"

using namespace tc;

std::shared_ptr<ThunderSdk> g_tc_sdk_ = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderSdk_init(JNIEnv *env, jobject thiz) {
    g_tc_sdk_ = ThunderSdk::Make();
    g_tc_sdk_->Init(ThunderSdkParams {
        .ssl_ = false,
        .ip_ = "10.0.0.16",
        .port_ = 9002,
        .req_path_ = "/media",
    });
    ALOGI("After initial。。。。");
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderSdk_start(JNIEnv *env, jobject thiz) {
    if (g_tc_sdk_) {
        g_tc_sdk_->Start();
        ALOGI("After start。。。。");
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tc_client_impl_ThunderSdk_stop(JNIEnv *env, jobject thiz) {
    if (g_tc_sdk_) {
        g_tc_sdk_->Exit();
    }
    return 0;
}