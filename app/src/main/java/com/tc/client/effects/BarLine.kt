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

    override fun render() {
        super.render()
//        spriteBatch.begin()
//        spriteBatch.draw(bgTexture, 0.0f, 0.0f)
//        spriteBatch.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1.0f)

        val xGap = 2.0f;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size
        val xStep = itemWidth - xGap;

        leftSpectrum.forEachIndexed{idx, value ->
            val xLeft = idx * (xStep + xGap);
            shapeRenderer.rect(xLeft, 0.0f, xStep, value.toFloat() * 3,
                Color.LIME,
                Color.LIME,
                Color.WHITE,
                Color.WHITE)
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        super.dispose()
    }

}