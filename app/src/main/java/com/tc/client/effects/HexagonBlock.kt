package com.tc.client.effects

import android.content.Context
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tc.client.impl.ThunderApp
import space.earlygrey.shapedrawer.ShapeDrawer


class HexagonBlock(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var stage: Stage
    private lateinit var shapeDrawer: ShapeDrawer
    private lateinit var texture: Texture

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
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render() {
        super.render()

        val verticalCount = 8
        val scaleSize = Gdx.graphics.height/verticalCount/2;
        val horizontalCount = Gdx.graphics.width/scaleSize/2;
        stage.batch.begin()
        for (column in 0 .. horizontalCount) {
            for (row in 0 .. verticalCount) {
                val index = (column * verticalCount + row)
                val targetIndex = index % 50
                //Log.i(TAG, "targetIndex: $targetIndex")
                val factor: Float = (leftSpectrum[targetIndex]*1.0f / 250.0f).toFloat()
                shapeDrawer.setColor(0.05f + factor, 0.13f + factor*1.2f, 0.35f + factor, 1.0f)
                shapeDrawer.filledPolygon(
                    2 * column * scaleSize.toFloat(),
                    2 * row * scaleSize.toFloat(),
                    7,
                    scaleSize.toFloat(),
                    scaleSize.toFloat(),
                    index* 5.0f
                );
            }
        }
        stage.batch.end()

    }

    override fun dispose() {
        super.dispose()
    }

}