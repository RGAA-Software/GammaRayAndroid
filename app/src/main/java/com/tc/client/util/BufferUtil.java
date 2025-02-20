package com.tc.client.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtil {

    public static Buffer createBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).position(0);
    }

    public static FloatBuffer createFloatBuffer(int capacity) {
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity*4);
        bb.order(ByteOrder.nativeOrder());
        bb.position(0);
        return bb.asFloatBuffer();
    }

    public static FloatBuffer createFloatBuffer(int capacity, float[] data) {
        FloatBuffer fb = createFloatBuffer(capacity);
        fb.put(data);
        fb.position(0);
        return fb;
    }

    public static FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer fb = createFloatBuffer(data.length);
        fb.put(data);
        fb.position(0);
        return fb;
    }

    public static IntBuffer createIntBuffer(int capacity) {
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity*4);
        bb.order(ByteOrder.nativeOrder());
        bb.position(0);
        return bb.asIntBuffer();
    }

    public static IntBuffer createIntBuffer(int capacity, int[] data) {
        IntBuffer ib = createIntBuffer(capacity);
        ib.put(data);
        ib.position(0);
        return ib;
    }

}
