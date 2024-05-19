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

    std::string NativeMsgMaker::MakeSpectrumMessage(const tc::ServerAudioSpectrum& spectrum) {
        json msg;
        msg["type"] = "spectrum";
        auto left_spectrum = json::array();
        auto right_spectrum = json::array();
        for (int i = 0; i < spectrum.left_spectrum_size(); i++) {
            left_spectrum.push_back(spectrum.left_spectrum(i));
        }
        for (int i = 0; i < spectrum.right_spectrum_size(); i++) {
           right_spectrum.push_back(spectrum.right_spectrum(i));
        }
        msg["left_spectrum"] = left_spectrum;
        msg["right_spectrum"] = right_spectrum;
        return msg.dump();
    }

}