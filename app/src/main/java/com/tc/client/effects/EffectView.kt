package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class EffectView(var context: Context) : ApplicationListener {

    private lateinit var spriteBatch: SpriteBatch
    private lateinit var bgTexture: Texture
    private lateinit var camera: OrthographicCamera

    override fun create() {
        spriteBatch = SpriteBatch()
        camera = OrthographicCamera()
        bgTexture = Texture(Gdx.files.internal("box2d/star.png"))

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        spriteBatch.begin()
        spriteBatch.draw(bgTexture, 0.0f, 0.0f)
        spriteBatch.end()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

}