package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.effects.CrtEffect
import com.crashinvaders.vfx.effects.LensFlareEffect
import com.crashinvaders.vfx.effects.OldTvEffect
import com.tc.client.impl.ThunderApp
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.Random


class HexagonBlock(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var stage: Stage
    private lateinit var shapeDrawer: ShapeDrawer
    private lateinit var texture: Texture

    private lateinit var vfxManager: VfxManager
    private val vfxEffects = mutableListOf<ChainVfxEffect>()
    private val random = Random()
    private val randomSegmentCount = 36 + random.nextInt(30)
    private var renderRotation = 0.0f

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

        // VfxManager is a host for the effects.
        // It captures rendering into internal off-screen buffer and applies a chain of defined effects.
        // Off-screen buffers may have any pixel format, for this example we will use RGBA8888.
        vfxManager = VfxManager(Pixmap.Format.RGBA8888)

        // Create and add an effect.
        // VfxEffect derivative classes serve as controllers for the effects.
        // They provide public properties to configure and control them.
        vfxEffects.add(CrtEffect())
        vfxEffects.add(OldTvEffect())
        vfxEffects.add(LensFlareEffect())
//        vfxEffects.add(FilmGrainEffect())
//        vfxEffects.add(WaterDistortionEffect(0.0f, 0.0f))
        vfxEffects.forEach {
            vfxManager.addEffect(it)
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        vfxManager.resize(width, height);
    }

    override fun render() {
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        super.render()

        renderRotation += Gdx.graphics.deltaTime

        val verticalCount = 8
        val scaleSize = Gdx.graphics.height/verticalCount/2;
        val horizontalCount = Gdx.graphics.width/scaleSize/2;
        stage.batch.begin()
        for (column in 0 .. horizontalCount) {
            for (row in 0 .. verticalCount) {
                val index = (column * verticalCount + row)
                val targetIndex = index % randomSegmentCount;//50
                //Log.i(TAG, "targetIndex: $targetIndex")
                val factor: Float = (leftSpectrum[targetIndex]*1.0f / 250.0f).toFloat()
                shapeDrawer.setColor(Math.min(1.0f, 0.05f + factor*2), 0.13f + factor*1.2f, 0.35f + factor, 1.0f)
                shapeDrawer.filledPolygon(
                    2 * column * scaleSize.toFloat(),
                    2 * row * scaleSize.toFloat(),
                    7,
                    scaleSize.toFloat(),
                    scaleSize.toFloat(),
                    renderRotation
//                    index* 5.0f
                );
            }
        }
        stage.batch.end()

        // End render to an off-screen buffer.
        vfxManager.endInputCapture();

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        vfxManager.applyEffects();

        // Render result to the screen.
        vfxManager.renderToScreen();
    }

    override fun dispose() {
        super.dispose()
        vfxManager.dispose();
        vfxEffects.forEach {
            it.dispose()
        }
    }

}