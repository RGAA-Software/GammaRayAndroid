package com.tc.client.render;

import static android.opengl.GLES20.glUniformMatrix4fv;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import java.nio.FloatBuffer;

public class Shader {

    private static final String TAG = "Shader";

    private int mProgram;

    public Shader(String vs, String fs) {
        initWithShaderSource(vs, fs);
    }

    public Shader(String vs, String gs, String fs) {

    }

    public void use() {
        GLES32.glUseProgram(mProgram);
    }

    public void unused() {
        GLES32.glUseProgram(0);
    }

    public int getAttribLocation(String name) {
        return GLES32.glGetAttribLocation(mProgram, name);
    }

    public int getUniformLocation(String name) {
        return GLES32.glGetUniformLocation(mProgram, name);
    }

    public void setUniformFloat(String name, float value) {
        GLES32.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniformInt(String name, int value) {
        GLES32.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniformMatrix4fv(String name, float[] matrix) {
        GLES32.glUniformMatrix4fv(getUniformLocation(name), 1, false, matrix, 0);
    }

    public void setUniformMatrix4fv(String name, FloatBuffer buffer) {
        buffer.position(0);
        glUniformMatrix4fv(getUniformLocation(name),
                1,
                false,
                buffer);
    }

    public void setUniformVec2(String name, float x, float y) {
        GLES32.glUniform2fv(getUniformLocation(name), 1, new float[]{x, y}, 0);
    }

    public void setUniformVec3(String name, float x, float y, float z) {
        GLES32.glUniform3fv(getUniformLocation(name), 1, new float[]{x, y, z}, 0);
    }

    public int getProgram() {
        return mProgram;
    }

    private void initWithShaderSource(String vs, String fs) {
        int vtShader = genShader(vs, GLES32.GL_VERTEX_SHADER);
        int fsShader = genShader(fs, GLES32.GL_FRAGMENT_SHADER);

        mProgram = GLES32.glCreateProgram();
        GLES32.glAttachShader(mProgram, vtShader);
        GLES32.glAttachShader(mProgram, fsShader);
        GLES32.glLinkProgram(mProgram);
        int[] linkStatus = new int[1];

        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES32.GL_TRUE) {
            Log.e(TAG, "link error!");
            Log.e(TAG, GLES20.glGetProgramInfoLog(mProgram));
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

        GLES32.glDeleteShader(vtShader);
        GLES32.glDeleteShader(fsShader);
    }

    private int genShader(String source, int type) {
        int shader = GLES32.glCreateShader(type);
        GLES32.glShaderSource(shader, source);
        GLES32.glCompileShader(shader);

        int[] ret = new int[1];
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, ret, 0);

        if (ret[0] != GLES32.GL_TRUE) {
            String shaderLog = GLES32.glGetShaderInfoLog(shader);
            Log.e(TAG, "shader : " + source + " \nErr : " + shaderLog);
        }
        return shader;
    }
}
