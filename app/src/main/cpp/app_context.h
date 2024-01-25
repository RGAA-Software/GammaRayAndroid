//
// Created by hy on 2024/1/25.
//

#ifndef TC_CLIENT_ANDROID_APP_CONTEXT_H
#define TC_CLIENT_ANDROID_APP_CONTEXT_H

#include <memory>


namespace tc
{

    class MessageNotifier;
    class MessageListener;

    class AppContext {
    public:

        static std::shared_ptr<AppContext> Make();

        AppContext();

        std::shared_ptr<MessageNotifier> GetMessageNotifier();
        std::shared_ptr<MessageListener> ObtainMessageListener();

        template<typename T>
        void SendAppMessage(const T& m);

    private:

        std::shared_ptr<MessageNotifier> msg_notifier_ = nullptr;

    };

}

#endif //TC_CLIENT_ANDROID_APP_CONTEXT_H
