package com.tc.client.impl;

import android.view.Surface;

public class ThunderSdk {
    public ThunderSdk() {

    }
    public native int init(boolean ssl, String ip, int port, String path, Surface surface, boolean hwCodec, boolean useOES);
    public native int start();
    public native int stop();

}
