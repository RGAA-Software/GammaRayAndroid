package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.tc.client.impl.ThunderApp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask


class BarTime(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var currentTime: String
    private lateinit var layout: GlyphLayout

    override fun create() {
        super.create()
        shapeRenderer = ShapeRenderer();
        batch = SpriteBatch()

        val generator: FreeTypeFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/LLDISCO-1.ttf"))
        val parameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 300
        font = generator.generateFont(parameter)
        generator.dispose()

        layout = GlyphLayout()
        currentTime = getCurrentTime()

        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                currentTime = getCurrentTime()
            }

        }, 100, 1000)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    private val fromColor = Color.valueOf("#FAB2FF")
    private val toColor = Color.valueOf("#1904E5")

    override fun render() {
        super.render()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1.0f)

        val xGap = 0;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size * 1.0f
        val xStep = itemWidth - xGap;
        val barScale = 2.3f

        leftSpectrum.forEachIndexed{idx, value ->
            val xLeft = idx * (xStep + xGap);
            val barValue = value.toFloat() * barScale
            shapeRenderer.rect(xLeft, 0.0f, xStep, barValue,
                fromColor,
                fromColor,
                toColor,
                toColor)
        }
        leftSpectrum.forEachIndexed { idx, _ ->
            val value = leftSpectrum[leftSpectrum.size-1-idx]
            val xLeft = idx * (xStep + xGap);
            val barValue = value.toFloat() * barScale
            shapeRenderer.rect(xLeft, Gdx.graphics.height-barValue, xStep, barValue,
                toColor,
                toColor,
                fromColor,
                fromColor
            )
        }
        shapeRenderer.end()

        batch.begin()
        layout.setText(font, currentTime)
        val textWidth = layout.width
        val textHeight = layout.height
        val x = (Gdx.graphics.width - textWidth) / 2
        val y = (Gdx.graphics.height + textHeight) / 2
        font.draw(batch, currentTime, x, y);
        batch.end()
    }

    override fun dispose() {
        super.dispose()
    }

    private fun getCurrentTime(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedDate = now.format(formatter)
        return formattedDate
    }

}