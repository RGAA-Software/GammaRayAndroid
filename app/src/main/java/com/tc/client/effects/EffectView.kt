package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.tc.client.impl.ThunderApp

class EffectView(var context: Context, var thunderApp: ThunderApp) : ApplicationListener {

    private lateinit var spriteBatch: SpriteBatch
    private lateinit var bgTexture: Texture
    private lateinit var camera: OrthographicCamera
    private lateinit var shapeRenderer: ShapeRenderer
    protected val leftSpectrum = mutableListOf<Double>()
    protected val rightSpectrum = mutableListOf<Double>()

    override fun create() {
        spriteBatch = SpriteBatch()
        camera = OrthographicCamera()
        bgTexture = Texture(Gdx.files.internal("box2d/star.png"))
        shapeRenderer = ShapeRenderer();
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        spriteBatch.begin()
        spriteBatch.draw(bgTexture, 0.0f, 0.0f)
        spriteBatch.end()

        fallDown()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1.0f)

        val xGap = 2.0f;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size
        val xStep = itemWidth - xGap;

        leftSpectrum.forEachIndexed{idx, value ->
            val xLeft = idx * (xStep + xGap);
            shapeRenderer.rect(xLeft, 0.0f, xStep, value.toFloat() * 3,
                Color.SALMON,
                Color.SALMON,
                Color.ORANGE,
                Color.ORANGE)
        }
        shapeRenderer.end()
    }

    protected fun fallDown() {
        if (leftSpectrum.size != thunderApp.leftSpectrum.size) {
            thunderApp.leftSpectrum.forEach {
                leftSpectrum.add(it)
            }
        }
        if (rightSpectrum.size != thunderApp.rightSpectrum.size) {
            thunderApp.rightSpectrum.forEach {
                rightSpectrum.add(it)
            }
        }

        val leftNewSpectrum = thunderApp.leftSpectrum
        leftNewSpectrum.forEachIndexed { index, newValue ->
            val oldValue = leftSpectrum[index]
            val diff = newValue - oldValue
            var targetValue = oldValue + diff / 3.5
            if (targetValue < 0) {
                targetValue = 0.0
            }
            leftSpectrum[index] = targetValue
        }
    }

    fun onRefresh() {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

}