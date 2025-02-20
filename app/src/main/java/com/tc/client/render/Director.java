package com.tc.client.render;

import android.content.Context;

import com.tc.client.util.AssetsUtil;

import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Director {

    private int mWidth;
    private int mHeight;
    private Context mContext;
//    private ImageLoader mImageLoader;

    private Matrix4f mOrthographicMatrix = new Matrix4f();
    private Matrix4f mViewMatrix = new Matrix4f();

    private final FloatBuffer mOrthographicBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private final FloatBuffer mViewBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();


    public Director(Context context) {
        mContext = context;
//        mImageLoader = new ImageLoader(context);
    }

    public void init(int width, int height) {
        mWidth = width;
        mHeight = height;
        mViewMatrix.identity();
        mOrthographicMatrix.identity();
        mOrthographicMatrix.ortho(0, width, 0, height, -1, 10);
    }

    public FloatBuffer getOrthographicMatrix() {
        mOrthographicBuffer.position(0);
        mOrthographicMatrix.get(mOrthographicBuffer);
        mOrthographicBuffer.position(0);
        return mOrthographicBuffer;
    }

    public FloatBuffer getViewMatrix() {
        mViewBuffer.position(0);
        mViewMatrix.get(mViewBuffer);
        mViewBuffer.position(0);
        return mViewBuffer;
    }

    public String loadFileFromAssets(String path) {
        return AssetsUtil.readAssetFileAsString(mContext, path);
    }

//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }

    public Context getContext() {
        return mContext;
    }

    public int getViewPortWidth() {
        return mWidth;
    }

    public int getViewPortHeight() {
        return mHeight;
    }
}
