package com.tc.client.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.tc.client.Settings;
import com.tc.client.impl.CursorInfo;
import com.tc.client.impl.ThunderApp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FrameRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "FrameRender";
    private final ThunderApp mThunderApp;
    private final Context mContext;

    private int mOESTexId;
    private SurfaceTexture mOESSurfaceTexture;
    private Surface mOESSurface;

    private Director mDirector;
    private Sprite mCursor;

    public FrameRender(Context ctx, ThunderApp sdk) {
        mContext = ctx;
        mThunderApp = sdk;
        mThunderApp.registerCursorInfoCallback(info -> {
            if (mCursor != null) {
                Image image = new Image(info.getWidth(), info.getHeight(), 4, info.getBitmap());
                mCursor.updateImagePosition(info.getX(), info.getY());
                mCursor.updateImage(image);
            }
        });
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mDirector = new Director(mContext);
        initRenderMediacodec();
        mThunderApp.init(false,  mOESSurface, true, true, mOESTexId,
                Settings.Companion.getInstance().getDeviceId(),
                mThunderApp.getStreamId());
        mThunderApp.start();

        mCursor = Sprite.Make(mDirector);
        mCursor.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mDirector.init(width, height);
        GLES32.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT|GLES32.GL_DEPTH_BUFFER_BIT);
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mOESSurfaceTexture.updateTexImage();
        onRenderTick();

        if (mCursor != null && Settings.Companion.getInstance().getShowCursor()) {
            mCursor.render(0);
        }
    }

    private void initRenderMediacodec() {
        int[] ids = new int[1];
        GLES32.glGenTextures(1, ids, 0);
        mOESTexId = ids[0];

        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);

        mOESSurfaceTexture = new SurfaceTexture(mOESTexId);
        mOESSurface = new Surface(mOESSurfaceTexture);
        mOESSurfaceTexture.setOnFrameAvailableListener(this);
    }


    public void onCreate() {
        mThunderApp.nativeCreate();
    }

    public void onResume() {
        mThunderApp.nativeResume();
    }

    public void onPause() {
        mThunderApp.nativePause();
    }

    public void onDestroy() {
        mThunderApp.nativeDestroy();
    }

    public void onRenderTick() {
        mThunderApp.nativeRenderTick();
    }

}
