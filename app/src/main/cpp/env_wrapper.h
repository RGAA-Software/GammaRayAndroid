//
// Created by hy on 2024/1/24.
//

#ifndef TC_CLIENT_ANDROID_ENV_WRAPPER_H
#define TC_CLIENT_ANDROID_ENV_WRAPPER_H

#include <jni.h>
#include <memory>

#include "tc_common_new/log.h"

namespace tc
{

    class EnvWrapper {
    public:

        static std::shared_ptr<EnvWrapper> Make(JavaVM* vm) {
            return std::make_shared<EnvWrapper>(vm);
        }

        explicit EnvWrapper(JavaVM* vm) {
            vm_ = vm;
            if (vm_->GetEnv((void **)&env_, JNI_VERSION_1_6) == JNI_EDETACHED) {
                if (vm_->AttachCurrentThread(&env_, NULL) == 0) {
                    is_attached_ = true;
                } else {
                    LOGE("JNI AttachCurrentThread failed.");
                }
            } else {
                LOGE("JNI GetEnv failed.");
            }
        }

        ~EnvWrapper() {
            if (is_attached_) {
                if(vm_->DetachCurrentThread() != 0) {
                    LOGE("JNI DetachCurrentThread failed");
                }
            }
        }

        JNIEnv* Env() {
            return env_;
        }

    private:
        JavaVM* vm_ = nullptr;
        JNIEnv* env_ = nullptr;
        bool is_attached_ = false;
    };

}

#endif //TC_CLIENT_ANDROID_ENV_WRAPPER_H
