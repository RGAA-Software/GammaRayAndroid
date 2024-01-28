package com.tc.client;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

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
    private int textureId_mediacodec;
    private SurfaceTexture surfaceTexture;
    private Surface mSurface;

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

        mThunderSdk.init(false, "192.168.31.5", 9002, "/media", mSurface, true, true);
//        mThunderSdk.init(false, "192.168.31.5", 9002, "/media", null, false, false);
        mThunderSdk.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mDirector.init(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        renderMediacodec();
        surfaceTexture.updateTexImage();

        onRenderTick();

    }

    private void initRenderMediacodec() {
//        String vertexSource = AssetsUtil.readAssetFileAsString(mContext, "video_vertex.glsl");
//        String fragmentSource = AssetsUtil.readAssetFileAsString(mContext, "video_fragment.glsl");
//        mShader = new Shader(vertexSource, fragmentSource);
//        mShader.use();
//        mVideoVAO = new VAO();
//        mVideoVAO.use();
//
//        int stride = 4 * 4;
//        FloatBuffer verticesBuffer = BufferUtil.createFloatBuffer(vertexData.length, vertexData);
//
//        int vertexArray = GLUtil.glGenBuffer();
//        glBindBuffer(GL_ARRAY_BUFFER, vertexArray);
//        glBufferData(GL_ARRAY_BUFFER, vertexData.length*4, verticesBuffer, GL_DYNAMIC_DRAW);
//
//        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
//        glEnableVertexAttribArray(0);
//
//        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2*4);
//        glEnableVertexAttribArray(1);
//
//        samplerOES_mediacodec = GLES32.glGetUniformLocation(mShader.getProgram(), "sTexture");

        int[] textureids = new int[1];
        GLES32.glGenTextures(1, textureids, 0);
        textureId_mediacodec = textureids[0];

        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId_mediacodec);
        mSurface = new Surface(surfaceTexture);
        surfaceTexture.setOnFrameAvailableListener(this);

//        mShader.unused();
//        mVideoVAO.unused();
    }

    private void renderMediacodec() {
        surfaceTexture.updateTexImage();
        mShader.use();
        mVideoVAO.use();

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId_mediacodec);
        GLES32.glUniform1i(samplerOES_mediacodec, 0);

        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, 4);

        mVideoVAO.unused();


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
