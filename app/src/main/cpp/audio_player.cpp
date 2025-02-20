//
// Created by hy on 2024/2/28.
//

#include "audio_player.h"
#include "tc_common_new/log.h"
#include "tc_common_new/data.h"

namespace tc
{

    std::shared_ptr<AudioPlayer> AudioPlayer::Make() {
        return std::make_shared<AudioPlayer>();
    }

    AudioPlayer::AudioPlayer() {

    }

    AudioPlayer::~AudioPlayer() {

    }

    void AudioPlayer::Init(int samples, int channels) {
        oboe::AudioStreamBuilder builder = oboe::AudioStreamBuilder();
        builder.setDirection(oboe::Direction::Output);
        builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
        builder.setSharingMode(oboe::SharingMode::Exclusive);
        builder.setFormat(oboe::AudioFormat::I16);
        builder.setSampleRate(samples);
        //builder.setAudioApi(oboe::AudioApi::AAudio);
        if (channels == 2) {
            builder.setChannelCount(oboe::ChannelCount::Stereo);
        } else if (channels == 1) {
            builder.setChannelCount(oboe::ChannelCount::Mono);
        }
        builder.setErrorCallback(this);
        auto result = builder.openStream(audio_stream_);
        LOGI("AudioPlayer openStream result: {}", (int)result);
        if (result != oboe::Result::OK || audio_stream_ == nullptr) {
            return;
        }

        auto frames_per_burst  = audio_stream_->getFramesPerBurst();
        LOGI("Frames per burst: {}", frames_per_burst);

        audio_stream_->requestStart();
    }

    void AudioPlayer::Write(const std::shared_ptr<Data>& data) {
        if (exit_) {
            return;
        }
        if (audio_stream_) {
            int frames = data->Size() / 2 / 2;
            audio_stream_->write(data->CStr(), frames, 10000000); // 10ms
        }
    }

//    oboe::DataCallbackResult AudioPlayer::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
//        LOGI("onAudioReady, size: {}", numFrames);
//        return oboe::DataCallbackResult::Continue;
//    }

    bool AudioPlayer::onError(oboe::AudioStream* , oboe::Result result) {
        LOGI("AudioPlayer onError: {}", (int)result);
        return true;
    }

    void AudioPlayer::onErrorBeforeClose(oboe::AudioStream*, oboe::Result result) {
        LOGI("AudioPlayer onErrorBeforeClose: {}", (int)result);
    }

    void AudioPlayer::onErrorAfterClose(oboe::AudioStream*, oboe::Result result) {
        LOGI("AudioPlayer onErrorAfterClose: {}", (int)result);
    }

    void AudioPlayer::Exit() {
        exit_ = true;
        audio_stream_->stop();
        audio_stream_->release();
    }
}