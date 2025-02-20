//
// Created by hy on 2024/2/28.
//

#ifndef TC_CLIENT_ANDROID_AUDIO_PLAYER_H
#define TC_CLIENT_ANDROID_AUDIO_PLAYER_H

#include <oboe/Oboe.h>

namespace tc
{

    class Data;

    // see: https://github.com/google/oboe/blob/main/docs/GettingStarted.md
    class AudioPlayer : /*public oboe::AudioStreamDataCallback,*/
            public oboe::AudioStreamErrorCallback  {
    public:

        static std::shared_ptr<AudioPlayer> Make();
        AudioPlayer();
        ~AudioPlayer();

        void Init(int samples, int channels);

        // 当前没有使用callback模式，是直接写入模式
        //oboe::DataCallbackResult onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;
        bool onError(oboe::AudioStream* , oboe::Result) override;
        void onErrorBeforeClose(oboe::AudioStream*, oboe::Result) override;
        void onErrorAfterClose(oboe::AudioStream*, oboe::Result) override;

        void Write(const std::shared_ptr<Data>& data);
        void Exit();

    private:
        std::shared_ptr<oboe::AudioStream> audio_stream_ = nullptr;
        bool exit_ = false;
    };

}

#endif //TC_CLIENT_ANDROID_AUDIO_PLAYER_H
