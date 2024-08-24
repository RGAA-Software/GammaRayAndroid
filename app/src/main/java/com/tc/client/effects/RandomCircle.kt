package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.effects.CrtEffect
import com.crashinvaders.vfx.effects.FilmGrainEffect
import com.crashinvaders.vfx.effects.FxaaEffect
import com.crashinvaders.vfx.effects.GaussianBlurEffect
import com.crashinvaders.vfx.effects.LensFlareEffect
import com.crashinvaders.vfx.effects.OldTvEffect
import com.crashinvaders.vfx.effects.WaterDistortionEffect
import com.tc.client.impl.ThunderApp
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.Random
import java.util.Vector


class RandomCircle(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var stage: Stage
    private lateinit var shapeDrawer: ShapeDrawer
    private lateinit var texture: Texture

    private lateinit var vfxManager: VfxManager
    private val vfxEffects = mutableListOf<ChainVfxEffect>()
    private val random = Random()
    private val randomSegmentCount = 36 + random.nextInt(30)
    private var renderRotation = 0.0f
    private val circleCenters = mutableListOf<CircleInfo>()

    class CircleInfo {
        var center: Vector2 = Vector2(0.0f, 0.0f)
        var color: Color = Color.CORAL
        var radius = 20.0f
    }

    override fun create() {
        super.create()
        stage = Stage()
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        texture = Texture(pixmap)
        pixmap.dispose()
        val region: TextureRegion = TextureRegion(texture, 0, 0, 1, 1)
        shapeDrawer = ShapeDrawer(stage.batch, region)

        vfxManager = VfxManager(Pixmap.Format.RGBA8888)
        //vfxEffects.add(GaussianBlurEffect())
        //vfxEffects.add(OldTvEffect())
        vfxEffects.add(LensFlareEffect())
        vfxEffects.add(FxaaEffect())
        vfxEffects.forEach {
            vfxManager.addEffect(it)
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        vfxManager.resize(width, height);
    }

    override fun render() {
        val totalCircleSize = 70
        if (leftSpectrum.size < totalCircleSize) {
            super.render()
            return
        }
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        super.render()
        renderRotation += Gdx.graphics.deltaTime
        stage.batch.begin()
        for (idx in 0 until totalCircleSize) {
            var circleInfo: CircleInfo
            if (circleCenters.size != totalCircleSize) {
                val cx = random.nextFloat() * Gdx.graphics.width
                val cy = random.nextFloat() * Gdx.graphics.height
                val center = Vector2(cx, cy)

                circleInfo = CircleInfo()
                circleInfo.center = center
                circleInfo.radius = (random.nextFloat() + 0.5f) * 80
                circleCenters.add(circleInfo)
            } else {
                circleInfo = circleCenters[idx]
            }

            val spectrumFactor: Float = leftSpectrum[idx].toFloat() / 100.0f
            val radius: Float = 0.8f + circleInfo.radius * spectrumFactor
            shapeDrawer.filledCircle(circleInfo.center, radius, circleInfo.color)
            shapeDrawer.circle(circleInfo.center.x, circleInfo.center.y, radius+5, 4.0f)
        }
        stage.batch.end()

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
    }

    override fun dispose() {
        super.dispose()
        vfxManager.dispose();
        vfxEffects.forEach {
            it.dispose()
        }
        stage.dispose()
        texture.dispose()
    }

}