package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.tc.client.impl.ThunderApp


class BarLine2(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var spriteBatch: SpriteBatch

    override fun create() {
        super.create()
        shapeRenderer = ShapeRenderer();
        spriteBatch = SpriteBatch()
        bgTexture = Texture(Gdx.files.internal("box2d/star.png"))
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    private val fromColor: Color = Color.valueOf("#F31B20")
    private val toColor: Color = Color.valueOf("#FFE4C2")
    private val transformMatrix = Matrix4()

    override fun render() {
        super.render()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1.0f)

        val xGap = 0;//2.0f;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size * 1.1f
        val xStep = itemWidth - xGap;

        val angle = Math.toDegrees(Math.atan(Gdx.graphics.height * 1.0 / Gdx.graphics.width * 1.0)).toFloat()

        leftSpectrum.forEachIndexed{idx, value ->
            val xLeft = idx * (xStep + xGap);
            val barValue = value.toFloat() * 3.0f

            transformMatrix.idt().translate((0.0f), (0.0f), 0f)
                .rotate(0f, 0f, 1f, angle)
            shapeRenderer.transformMatrix = transformMatrix

            shapeRenderer.rect(xLeft, 0.0f, xStep, barValue,
                fromColor,
                fromColor,
                toColor,
                toColor)

            transformMatrix.idt().translate((0.0f), (0.0f), 0f)
                .rotate(0f, 0.0f, 1f, angle)
                .scale(1.0f, -1.0f, 1.0f)
            shapeRenderer.transformMatrix = transformMatrix
            val mirrorBarValue = leftSpectrum[leftSpectrum.size-1-idx] * 2.0f
            shapeRenderer.rect(xLeft, 0.0f, xStep, mirrorBarValue.toFloat(),
                fromColor,
                fromColor,
                toColor,
                toColor)
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        super.dispose()
    }

}