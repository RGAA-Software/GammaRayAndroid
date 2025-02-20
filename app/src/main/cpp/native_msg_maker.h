//
// Created by hy on 2024/3/27.
//

#ifndef TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H
#define TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H

#include <string>
#include <vector>
#include "tc_message.pb.h"

namespace tc
{
    class NativeMsgMaker {
    public:

        static std::string MakeFrameInfoMessage(int width, int height, int format,
                                                const std::string& mon_name, int mon_left,
                                                int mon_top, int mon_right, int mon_bottom);
        static std::string MakeSpectrumMessage(const tc::ServerAudioSpectrum& spectrum);

    };

}

#endif //TC_CLIENT_ANDROID_NATIVE_MSG_MAKER_H
