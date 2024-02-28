//
// Created by hy on 2024/2/28.
//

#ifndef TC_CLIENT_ANDROID_AUDIO_PLAYER_H
#define TC_CLIENT_ANDROID_AUDIO_PLAYER_H

#include <oboe/Oboe.h>

namespace tc
{

    // see: https://github.com/google/oboe/blob/main/docs/GettingStarted.md
    class AudioPlayer : public oboe::AudioStreamDataCallback {
    public:

        static std::shared_ptr<AudioPlayer> Make();

        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;

    private:
        std::shared_ptr<oboe::AudioStream> audio_stream_ = nullptr;
    };

}

#endif //TC_CLIENT_ANDROID_AUDIO_PLAYER_H
