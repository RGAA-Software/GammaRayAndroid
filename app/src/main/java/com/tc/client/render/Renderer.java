package com.tc.client.render;

import android.content.Context;

import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Renderer {

    protected Shader mShader;
    protected VAO mVAO;
    protected Context mContext;
    protected Matrix4f mModel;
    protected Director mDirector;

   protected final FloatBuffer mModelBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    public Renderer(Context context, Director director, String vs, String fs) {
        mContext = context;
        mDirector = director;
        mShader = new Shader(vs, fs);
        mModel = new Matrix4f();
        mModel = mModel.identity();
    }

    public void init() {
        mVAO = new VAO();
        mVAO.use();
    }

    public void render(float delta) {
        mShader.use();
        if (mVAO != null) {
            mVAO.use();
        }

        mShader.setUniformMatrix4fv("projection", mDirector.getOrthographicMatrix());
        mShader.setUniformMatrix4fv("view", mDirector.getViewMatrix());
        convertModelMatrix();
        mShader.setUniformMatrix4fv("model", mModelBuffer);

    }

    public void convertModelMatrix() {
        mModelBuffer.position(0);
        mModel.get(mModelBuffer);
        mModelBuffer.position(0);
    }

    public void release() {

    }

}
