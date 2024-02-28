//
// Created by hy on 2024/2/28.
//

#include "audio_player.h"

namespace tc
{

    std::shared_ptr<AudioPlayer> AudioPlayer::Make() {
        return std::make_shared<AudioPlayer>();
    }

    oboe::DataCallbackResult AudioPlayer::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {

        return oboe::DataCallbackResult::Continue;
    }
}