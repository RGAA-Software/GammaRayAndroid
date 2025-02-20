package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.tc.client.impl.ThunderApp

class BarLine(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

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

    val fromColor = Color(0x72EDF2ff)
    val toColor = Color(0x5151E5ff)

    override fun render() {
        super.render()
//        spriteBatch.begin()
//        spriteBatch.draw(bgTexture, 0.0f, 0.0f)
//        spriteBatch.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1.0f)

        val xGap = 0;//2.0f;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size * 1.2f
        val xStep = itemWidth - xGap;

        leftSpectrum.forEachIndexed{idx, value ->
            val xLeft = idx * (xStep + xGap);
            val barValue = value.toFloat() * 3.6f
            shapeRenderer.rect(xLeft, 0.0f, xStep, barValue,
                fromColor,
                fromColor,
                toColor,
                toColor)
            shapeRenderer.rect(xLeft, barValue, xStep, Gdx.graphics.height-barValue,
                toColor,
                toColor,
                fromColor,
                fromColor
                )
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        super.dispose()
    }

}