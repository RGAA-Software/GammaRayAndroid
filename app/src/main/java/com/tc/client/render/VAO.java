package com.tc.client.render;

import android.opengl.GLES32;
import android.util.Log;

public class VAO {

    private static final String TAG = "VAO";

    private final int mVAO;

    public VAO() {
        int[] vao = new int[1];
        GLES32.glGenVertexArrays(1, vao, 0);
        mVAO = vao[0];
        Log.i(TAG, "VAO : " + mVAO);
    }

    public void use() {
        GLES32.glBindVertexArray(mVAO);
    }

    public void unused() {
        GLES32.glBindVertexArray(0);
    }

    public int getVAO() {
        return mVAO;
    }

}
