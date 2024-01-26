package com.tc.client;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tc.client.impl.ThunderSdk;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FrameRenderView extends GLSurfaceView implements SurfaceHolder.Callback, GLSurfaceView.Renderer {

    private static final String TAG = "FrameRenderView";

    private ThunderSdk mThunderSdk;

    public FrameRenderView(Context context) {
        this(context, null);
    }

    public FrameRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);//设置openGLES的版本号
        mThunderSdk = new ThunderSdk();
        Log.i(TAG, "FrameRenderView.");

        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mThunderSdk.init(false, "10.0.0.16", 9002, "/media", getHolder().getSurface(), true);
        mThunderSdk.start();
        Log.i(TAG, "onSurfaceCreated.");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        nativeRenderTick();
    }

    public void onCreate() {
        nativeCreate();
    }

    public void onResume() {
        nativeResume();
    }

    public void onPause() {
        nativePause();
    }

    public void onDestroy() {
        nativeDestroy();
    }

    private native void nativeCreate();
    private native void nativeResume();
    private native void nativePause();
    private native void nativeDestroy();
    private native void nativeRenderTick();
}
