package com.tc.client.render;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.GL_DEPTH;
import static android.opengl.GLES30.glBindVertexArray;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.content.Context;
import android.opengl.GLES32;

import com.tc.client.util.AssetsUtil;
import com.tc.client.util.BufferUtil;
import com.tc.client.util.GLUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Sprite extends Renderer {

    private static final String TAG = "Sprite";

    private final Object mImageLock = new Object();
    private Image mImage;
    private int mTextureId;
    private float mPosX;
    private float mPosY;

    public static Sprite Make(Director director) {
        Context context = director.getContext();
        String vsPath = "base_vertex.glsl";
        String fsPath = "base_tex_fragment.glsl";
        String vs = AssetsUtil.readAssetFileAsString(context, vsPath);
        String fs = AssetsUtil.readAssetFileAsString(context, fsPath);
        return new Sprite(context, director, vs, fs);
    }

    public Sprite(Context context, Director director, String vs, String fs) {
        super(context, director, vs, fs);
    }

    @Override
    public void init() {
        super.init();

        mShader.use();
        float[] vertices = {
                0,  0,  0,  1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
                50, 0,  0,  0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
                50, 50, 0,  0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
                0,  50, 0,  1.0f, 1.0f, 0.0f,   0.0f, 1.0f
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        int stride = 8 * 4;
        FloatBuffer verticesBuffer = BufferUtil.createFloatBuffer(vertices.length, vertices);

        int vertexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vertexArray);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, verticesBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        //
        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        //
        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1, buffer);
        buffer.position(0);
        mTextureId = buffer.get(0);

        glBindTexture(GL_TEXTURE_2D, mTextureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GLES32.glEnable(GL_BLEND);
        GLES32.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mModel.identity();
        mModel.translate(mPosX, mPosY, 1);
        convertModelMatrix();
        mShader.setUniformMatrix4fv("model", mModelBuffer);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glUniform1i(mShader.getUniformLocation("image"), 0);
        synchronized (mImageLock) {
            if (mImage != null) {
                glTexImage2D(GL_TEXTURE_2D,
                        0,
                        GL_RGBA,
                        mImage.getWidth(),
                        mImage.getHeight(),
                        0,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        mImage.getData());
            }
        }
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

    }

    public void updateImage(Image image) {
        synchronized (mImageLock) {
            mImage = image;
        }
    }

    public void updateImagePosition(float x, float y) {
        mPosX = mDirector.getViewPortWidth() * x;
        mPosY = mDirector.getViewPortHeight() * y;
    }

    @Override
    public void release() {
        super.release();
    }
}
