package com.tc.client.impl;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import org.json.JSONException;
import org.json.JSONObject;

public class ThunderApp {
    private static final String TAG = "Main";
    private String mIp;
    private int mPort;
    private OnFrameChangedCallback mFrameChangedCallback;

    public ThunderApp(String ip, int port) {
        mIp = ip;
        mPort = port;
    }
    public void init(boolean ssl, Surface surface, boolean hwCodec, boolean useOES, int oesTexId) {
        this.init(ssl, mIp, mPort, "/media", surface, hwCodec, useOES, oesTexId);
    }
    public native int init(boolean ssl, String ip, int port, String path, Surface surface, boolean hwCodec, boolean useOES, int oesTexId);
    public native int start();
    public native int stop();
    public native void sendGamepadState(int buttons, int leftTrigger, int rightTrigger, int thumbLX, int thumbLY, int thumbRX, int thumbRY);
    public void onNativeMessage(String msg) {
        Log.i(TAG, "onNativeMessage: " + msg);
        try {
            JSONObject obj = new JSONObject(msg);
            String type = obj.getString("type");
            if (TextUtils.equals(type, "frame")) {
                int width = obj.getInt("width");
                int height = obj.getInt("height");
                if (mFrameChangedCallback != null) {
                    mFrameChangedCallback.onFrameChanged(width, height);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Parse native message failed: " + msg + ", e: " + e.getMessage());
        }
    }

    // register callbacks
    public void registerFrameChangedCallback(OnFrameChangedCallback cbk) {
        mFrameChangedCallback = cbk;
    }

    // callbacks
    public interface OnFrameChangedCallback {
        void onFrameChanged(int width, int height);
    }
}