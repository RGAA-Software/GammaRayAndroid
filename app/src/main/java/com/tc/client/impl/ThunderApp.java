package com.tc.client.impl;

import android.view.Surface;

public class ThunderApp {
    private String mIp;
    private int mPort;

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

}
