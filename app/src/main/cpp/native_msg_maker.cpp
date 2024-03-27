//
// Created by hy on 2024/3/27.
//

#include "native_msg_maker.h"
#include "tc_3rdparty/json/json.hpp"

using namespace nlohmann;

namespace tc
{

    std::string NativeMsgMaker::MakeFrameInfoMessage(int width, int height, int format) {
        json msg;
        msg["type"] = "frame";
        msg["width"] = width;
        msg["height"] = height;
        msg["format"] = format;
        return msg.dump();
    }

}