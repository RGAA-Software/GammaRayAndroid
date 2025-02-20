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

open class EffectView(var context: Context, var thunderApp: ThunderApp) : ApplicationListener {

    companion object {
        public const val TAG = "Effect"
    }

    private lateinit var camera: OrthographicCamera
    protected val leftSpectrum = mutableListOf<Double>()
    protected val rightSpectrum = mutableListOf<Double>()
    protected lateinit var bgTexture: Texture

    override fun create() {
        camera = OrthographicCamera()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fallDown()
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
            var targetValue = oldValue + diff / 3.0f
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