package com.tc.client;

import static android.opengl.GLES20.glVertexAttribPointer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.tc.client.impl.ThunderSdk;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FrameRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "FrameRender";

    private ThunderSdk mThunderSdk;


    private Context mContext;

    private final float[] vertexData = {
            -1f, -1f, 0f, 1f,
            1f, -1f,  1f, 1f,
            -1f, 1f,  0f, 0f,
            1f, 1f,   1f, 0f

    };

    private FloatBuffer vertexBuffer;

    private Shader mShader;
    private int samplerOES_mediacodec;
    private int mOESTexId;
    private SurfaceTexture mOESSurfaceTexture;
    private Surface mOESSurface;

    private VAO mVideoVAO;
    private Director mDirector;
    private Sprite mCursor;

    public FrameRender(Context ctx, ThunderSdk sdk) {
        mContext = ctx;
        mThunderSdk = sdk;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        Log.i(TAG, "onSurfaceCreated from render.......");

        mDirector = new Director(mContext);

        initRenderMediacodec();

        mThunderSdk.init(false, "10.0.0.16", 9002, "/media", mOESSurface, true, true, mOESTexId);
//        mThunderSdk.init(false, "192.168.31.5", 9002, "/media", null, false, false);
        mThunderSdk.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mDirector.init(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mOESSurfaceTexture.updateTexImage();
        onRenderTick();
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
        this.nativeCreate();
    }
    public void onResume() {
        this.nativeResume();
    }
    public void onPause() {
        this.nativePause();
    }
    public void onDestroy() {
        this.nativeDestroy();
    }
    public void onRenderTick() {
        this.nativeRenderTick();
    }

    public native void nativeCreate();
    public native void nativeResume();
    public native void nativePause();
    public native void nativeDestroy();
    public native void nativeRenderTick();

}
