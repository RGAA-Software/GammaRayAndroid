package com.tc.client.render;

import static android.opengl.GLES32.GL_LINEAR;
import static android.opengl.GLES32.GL_REPEAT;
import static android.opengl.GLES32.GL_RGBA;
import static android.opengl.GLES32.GL_TEXTURE0;
import static android.opengl.GLES32.GL_TEXTURE_2D;
import static android.opengl.GLES32.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES32.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES32.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES32.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES32.GL_UNSIGNED_BYTE;
import static android.opengl.GLES32.glActiveTexture;
import static android.opengl.GLES32.glBindTexture;
import static android.opengl.GLES32.glDeleteTextures;
import static android.opengl.GLES32.glGenTextures;
import static android.opengl.GLES32.glGenerateMipmap;
import static android.opengl.GLES32.glTexImage2D;
import static android.opengl.GLES32.glTexParameteri;

import java.nio.IntBuffer;

public class Texture {

    private int mTextureId = 0;
    private boolean mDisposed = false;
    private int mWidth;
    private int mHeight;

    public Texture(Director director, String path) {
        this(director, path, true);
    }

    public Texture(Director director, String path, boolean assets) {
//        ImageLoader imageLoader = director.getImageLoader();
//        Image image = null;
//        if (assets) {
//            image = imageLoader.loadFromAssets(path);
//        } else {
//            image = imageLoader.loadFromInternalStorage(path);
//        }
//        initWithImage(image);
    }

    public Texture(Image image) {
        initWithImage(image);
    }

    public Texture(int texId, int width, int height) {
        mTextureId = texId;
        mWidth = width;
        mHeight = height;
    }

    private void initWithImage(Image image) {
        mWidth = image.getWidth();
        mHeight = image.getHeight();

        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1, buffer);
        buffer.position(0);
        mTextureId = buffer.get(0);

        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA,
                image.getWidth(),
                image.getHeight(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image.getData());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void active(Shader shader, int id) {
        glActiveTexture(GL_TEXTURE0+id);
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        shader.setUniformInt("image", id);
    }

    public int getTextureId() {
        return mTextureId;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isDisposed() {
        return mDisposed;
    }

    public void dispose() {
        glDeleteTextures(1, new int[]{mTextureId}, 0);
        mDisposed = true;
    }

}
