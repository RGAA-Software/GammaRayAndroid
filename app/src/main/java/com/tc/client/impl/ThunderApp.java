package com.tc.client.impl;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.tc.client.Statistics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThunderApp {

    private static final String TAG = "Main";

    private final String mIp;
    private final int mPort;
    private OnFrameChangedCallback mFrameChangedCallback;
    private final boolean mOnlyAudio;
    private final boolean mEnableController;
    private final List<Double> mLeftSpectrum = new ArrayList<>();
    private final List<Double> mRightSpectrum = new ArrayList<>();
    private long mAudioSpectrumCount = 0;

    public ThunderApp(String ip, int port, boolean onlyAudio, boolean enableController) {
        mIp = ip;
        mPort = port;
        mOnlyAudio = onlyAudio;
        mEnableController = enableController;
    }
    public void init(boolean ssl, Surface surface, boolean hwCodec, boolean useOES, int oesTexId) {
        this.init(ssl, mOnlyAudio, mEnableController, mIp, mPort, "/media", surface, hwCodec, useOES, oesTexId);
    }
    public native int init(boolean ssl, boolean onlyAudio, boolean enableController, String ip, int port,
                           String path, Surface surface, boolean hwCodec, boolean useOES, int oesTexId);
    public native int start();
    public native int stop();
    public native void sendGamepadState(int buttons, int leftTrigger, int rightTrigger, int thumbLX, int thumbLY, int thumbRX, int thumbRY);

    // register callbacks
    public void registerFrameChangedCallback(OnFrameChangedCallback cbk) {
        mFrameChangedCallback = cbk;
    }

    public synchronized List<Double> getLeftSpectrum() {
        return mLeftSpectrum;
    }

    public synchronized List<Double> getRightSpectrum() {
        return mRightSpectrum;
    }

    // callbacks
    public interface OnFrameChangedCallback {
        void onFrameChanged(int width, int height);
    }

    public native void nativeCreate();
    public native void nativeResume();
    public native void nativePause();
    public native void nativeDestroy();
    public native void nativeRenderTick();

    public void onNativeMessage(String msg) {
        try {
            JSONObject obj = new JSONObject(msg);
            String type = obj.getString("type");
            //Log.i(TAG, "onNativeMessage type: " + type);
            if (TextUtils.equals(type, "frame")) {
                int width = obj.getInt("width");
                int height = obj.getInt("height");
                if (mFrameChangedCallback != null) {
                    mFrameChangedCallback.onFrameChanged(width, height);
                }
            } else if (TextUtils.equals(type, "spectrum")) {
                synchronized (this) {
                    if (mAudioSpectrumCount++ % 3 != 0) {
                        return;
                    }
                    JSONArray leftSpectrum = obj.getJSONArray("left_spectrum");
                    JSONArray rightSpectrum = obj.getJSONArray("right_spectrum");
                    if (mLeftSpectrum.size() != leftSpectrum.length()) {
                        mLeftSpectrum.clear();
                    }
                    if (mRightSpectrum.size() != rightSpectrum.length()) {
                        mRightSpectrum.clear();
                    }
                    boolean leftFullSpectrum = mLeftSpectrum.size() == leftSpectrum.length();
                    boolean rightFullSpectrum = mRightSpectrum.size() == rightSpectrum.length();
                    for (int i = 0; i < leftSpectrum.length(); i++) {
                        if (leftFullSpectrum) {
                            mLeftSpectrum.set(i, leftSpectrum.getDouble(i));
                        } else {
                            mLeftSpectrum.add(leftSpectrum.getDouble(i));
                        }
                    }
                    for (int i = 0; i < rightSpectrum.length(); i++) {
                        if (rightFullSpectrum) {
                            mRightSpectrum.set(i, rightSpectrum.getDouble(i));
                        } else {
                            mRightSpectrum.add(rightSpectrum.getDouble(i));
                        }
                    }

                    // !! deprecated !!
                    //Statistics.INSTANCE.updateSpectrum(mLeftSpectrum, mRightSpectrum);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Parse native message failed: " + msg + ", e: " + e.getMessage());
        }
    }
}
