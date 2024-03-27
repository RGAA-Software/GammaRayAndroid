//
// Created by hy on 2024/3/27.
//

#ifndef TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H
#define TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H

#include <string>

namespace tc
{
    class NativeMsgMaker {
    public:

        static std::string MakeFrameInfoMessage(int width, int height, int format);

    };

}

#endif //TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H
