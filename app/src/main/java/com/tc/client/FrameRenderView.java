package com.tc.client;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.tc.client.impl.ThunderApp;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class FrameRenderView extends GLSurfaceView {

    private static final String TAG = "FrameRenderView";

    private ThunderApp mThunderApp;
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
//                        EGL10.EGL_SAMPLES, 4,
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

        mThunderApp = new ThunderApp();
        mRender = new FrameRender(getContext(), mThunderApp);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void onCreate() {
        mRender.onCreate();
    }

    public void onResume() {
        mRender.onResume();
    }

    public void onPause() {
        mRender.onPause();
    }

    public void onDestroy() {
        mRender.onDestroy();
    }

    public void onRenderTick() {
        mRender.onRenderTick();
    }
}
