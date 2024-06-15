package com.tc.client.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Image {

    private int mWidth;
    private int mHeight;
    private int mChannels;
    private ByteBuffer mData;

    public Image(int width, int height, int channels, ByteBuffer data) {
        mWidth = width;
        mHeight = height;
        mChannels = channels;
        mData = data;
    }

    public Image(int width, int height, int channels, byte[] data) {
        mWidth = width;
        mHeight = height;
        mChannels = channels;
        mData = ByteBuffer.allocateDirect(data.length).order(ByteOrder.nativeOrder()).put(data);
        mData.position(0);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getChannels() {
        return mChannels;
    }

    public ByteBuffer getData() {
        return mData;
    }

    public void putData(byte[] data) {
        mData.position(0);
        mData.put(data);
        mData.position(0);
    }

}
