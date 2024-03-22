//
// Created by hy on 2024/1/25.
//

#include "app_context.h"
#include "tc_common_new/message_notifier.h"

namespace tc
{

    std::shared_ptr<AppContext> AppContext::Make() {
        return std::make_shared<AppContext>();
    }

    AppContext::AppContext() {
        msg_notifier_ = std::make_shared<MessageNotifier>();
    }

    std::shared_ptr<MessageNotifier> AppContext::GetMessageNotifier() {
        return msg_notifier_;
    }

    std::shared_ptr<MessageListener> AppContext::ObtainMessageListener() {
        return msg_notifier_->CreateListener();
    }

    template<typename T>
    void AppContext::SendAppMessage(const T& m) {
        if (msg_notifier_) {
            msg_notifier_->SendAppMessage(m);
        }
    }

}