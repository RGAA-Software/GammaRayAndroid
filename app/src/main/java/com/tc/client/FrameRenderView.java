package com.tc.client;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.tc.client.impl.ThunderSdk;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class FrameRenderView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "FrameRenderView";

    private ThunderSdk mThunderSdk;

    private Surface mSurface;

    public FrameRenderView(Context context) {
        this(context, null);
    }

    public FrameRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mThunderSdk = new ThunderSdk();
        getHolder().addCallback(this);
//        getHolder().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mSurface = holder.getSurface();
        mThunderSdk.init(false, "10.0.0.16", 9002, "/media", mSurface, true, false);
        mThunderSdk.start();
        Log.i(TAG, "surfaceCreated, width: " + width + " height: " + height + " format: " + format);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

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

    public void onRenderTick() {
        nativeRenderTick();
    }

    private native void nativeCreate();
    private native void nativeResume();
    private native void nativePause();
    private native void nativeDestroy();
    private native void nativeRenderTick();
}
