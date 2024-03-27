package com.tc.client.util;

import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES30.glGenVertexArrays;

import com.tc.client.util.BufferUtil;

import java.nio.IntBuffer;

public class GLUtil {

    public static int glGenVertexArray() {
        IntBuffer buffer = BufferUtil.createIntBuffer(1);
        glGenVertexArrays(1, buffer);
        buffer.position(0);
        return buffer.get(0);
    }

    public static int glGenBuffer() {
        IntBuffer buffer = BufferUtil.createIntBuffer(1);
        glGenBuffers(1, buffer);
        buffer.position(0);
        return buffer.get(0);
    }

    public static int genFrameBuffer() {
        IntBuffer buffer = BufferUtil.createIntBuffer(1);
        glGenFramebuffers(1, buffer);
        buffer.position(0);
        return buffer.get(0);
    }

    public static int genRenderObject() {
        IntBuffer buffer = BufferUtil.createIntBuffer(1);
        glGenRenderbuffers(1, buffer);
        buffer.position(0);
        return buffer.get(0);
    }

    public static int genTexture() {
        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1, buffer);
        buffer.position(0);
        return buffer.get(0);
    }

}