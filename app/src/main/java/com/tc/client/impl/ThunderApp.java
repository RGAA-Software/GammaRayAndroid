package com.tc.client.impl;

import android.view.Surface;

public class ThunderApp {
    public ThunderApp() {

    }
    public native int init(boolean ssl, String ip, int port, String path, Surface surface, boolean hwCodec, boolean useOES, int oesTexId);
    public native int start();
    public native int stop();
    public native void sendGamepadState(int buttons, int leftTrigger, int rightTrigger, int thumbLX, int thumbLY, int thumbRX, int thumbRY);

}
