//
// Created by hy on 2024/3/27.
//

#include "native_msg_maker.h"
#include "tc_3rdparty/json/json.hpp"
#include "tc_common_new/audio_filter.h"

using namespace nlohmann;

namespace tc
{

    std::string NativeMsgMaker::MakeFrameInfoMessage(int width, int height, int format,
                                                     const std::string& mon_name, int mon_left,
                                                     int mon_top, int mon_right, int mon_bottom) {
        json msg;
        msg["type"] = "frame";
        msg["width"] = width;
        msg["height"] = height;
        msg["format"] = format;
        msg["mon_name"] = mon_name;
        msg["mon_left"] = mon_left;
        msg["mon_top"] = mon_top;
        msg["mon_right"] = mon_right;
        msg["mon_bottom"] = mon_bottom;
        return msg.dump();
    }

    std::string NativeMsgMaker::MakeSpectrumMessage(const tc::ServerAudioSpectrum& spectrum) {
        json msg;
        msg["type"] = "spectrum";
        auto left_spectrum = json::array();
        auto right_spectrum = json::array();

        std::vector<double> left_spectrum_value;
        for (int i = 0; i < spectrum.left_spectrum_size(); i++) {
            left_spectrum_value.push_back(spectrum.left_spectrum(i));
        }
        std::vector<double> right_spectrum_value;
        for (int i = 0; i < spectrum.right_spectrum_size(); i++) {
            right_spectrum_value.push_back(spectrum.right_spectrum(i));
        }

        MonsterCatFilter::FilterBars(left_spectrum_value);
        MonsterCatFilter::FilterBars(right_spectrum_value);
        for (auto& value : left_spectrum_value) {
            left_spectrum.push_back(value);
        }
        for (auto& value : right_spectrum_value) {
            right_spectrum.push_back(value);
        }

        msg["left_spectrum"] = left_spectrum;
        msg["right_spectrum"] = right_spectrum;
        return msg.dump();
    }

}