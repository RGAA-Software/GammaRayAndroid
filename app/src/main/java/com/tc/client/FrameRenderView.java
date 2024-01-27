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

public class FrameRenderView extends GLSurfaceView {

    private static final String TAG = "FrameRenderView";

    private ThunderSdk mThunderSdk;
    private FrameRender mRender;

    public FrameRenderView(Context context) {
        this(context, null);
    }
    public FrameRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setEGLContextClientVersion(3);
        setEGLConfigChooser(new EGLConfigChooser() {
            @Override
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] attributes = {
                        EGL10.EGL_LEVEL, 0,
                        EGL10.EGL_RENDERABLE_TYPE, 4,
                        EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_DEPTH_SIZE, 24,
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_SAMPLES, 4,
                        EGL10.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] configCounts = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, configCounts);

                if (configCounts[0] == 0) {
                    return null;
                } else {
                    return configs[0];
                }
            }
        });

        mThunderSdk = new ThunderSdk();
        mRender = new FrameRender(getContext(), mThunderSdk);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
