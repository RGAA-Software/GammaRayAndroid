package com.tc.client.impl;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.tc.client.Settings;
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
    private OnCursorInfoCallback mCursorInfoCallback;
    private final boolean mEnableAudio;
    private final boolean mEnableVideo;
    private final boolean mEnableController;
    private final List<Double> mLeftSpectrum = new ArrayList<>();
    private final List<Double> mRightSpectrum = new ArrayList<>();
    private long mAudioSpectrumCount = 0;
    private String mStreamId;

    public ThunderApp(String ip, int port, boolean enableAudio, boolean enableVideo, boolean enableController, String streamId) {
        mIp = ip;
        mPort = port;
        mEnableAudio = enableAudio;
        mEnableVideo = enableVideo;
        mEnableController = enableController;
        mStreamId = streamId;
    }
    public void init(boolean ssl, Surface surface, boolean hwCodec, boolean useOES, int oesTexId, String deviceId, String streamId) {
        String targetPath = "/media?device_id=" + deviceId + "&stream_id=" + streamId;
        this.init(ssl, mEnableAudio, mEnableVideo, mEnableController, mIp, mPort, targetPath, surface, hwCodec, useOES, oesTexId, deviceId, streamId);
    }
    public native int init(boolean ssl, boolean enableAudio, boolean enableVideo, boolean enableController, String ip, int port,
                           String path, Surface surface, boolean hwCodec, boolean useOES, int oesTexId, String deviceId, String streamId);
    public native int start();
    public native int stop();
    public native void sendGamepadState(int buttons, int leftTrigger, int rightTrigger, int thumbLX, int thumbLY, int thumbRX, int thumbRY);
    public native void sendMouseEvent(int event, float xRatio, float yRatio);

    // register callbacks
    public void registerFrameChangedCallback(OnFrameChangedCallback cbk) {
        mFrameChangedCallback = cbk;
    }

    public void registerCursorInfoCallback(OnCursorInfoCallback cbk) {
        mCursorInfoCallback = cbk;
    }

    public synchronized List<Double> getLeftSpectrum() {
        return mLeftSpectrum;
    }

    public synchronized List<Double> getRightSpectrum() {
        return mRightSpectrum;
    }

    public String getStreamId() {
        return mStreamId;
    }

    // callbacks
    public interface OnFrameChangedCallback {
        void onFrameChanged(int width, int height);
    }

    public interface OnCursorInfoCallback {
        void onCursorInfo(CursorInfo info);
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
            if (TextUtils.equals(type, "frame") && !Settings.Companion.getInstance().getFullscreen()) {
                Log.i(TAG, "frame resize : " + msg);
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

    public void onCursorInfo(float x, float y, int hotspotX, int hotspotY, int width, int height, boolean visible, byte[] data) {
        if (mCursorInfoCallback == null) {
            return;
        }
        CursorInfo cursorInfo = new CursorInfo();
        cursorInfo.setX(x);
        cursorInfo.setY(y);
        cursorInfo.setHotspotX(hotspotX);
        cursorInfo.setHotspotY(hotspotY);
        cursorInfo.setWidth(width);
        cursorInfo.setHeight(height);
        cursorInfo.setVisible(visible);
        cursorInfo.setBitmap(data);
        mCursorInfoCallback.onCursorInfo(cursorInfo);
    }
}
