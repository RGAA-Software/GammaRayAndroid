package com.tc.client.effects

import android.content.Context
import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.tc.client.impl.ThunderApp
import java.util.Random

class RectangleBlock(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var shapeRenderer: ShapeRenderer
    private val random = Random()
    private val randomIndices = mutableMapOf<Int, Int>()

    override fun create() {
        super.create()
        shapeRenderer = ShapeRenderer()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render() {
        super.render()
        if (leftSpectrum.isEmpty()) {
            return
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val verticalBlockCount: Int = 10
        val padding = 3;
        val blockSize: Int = Gdx.graphics.height / verticalBlockCount
        val horizontalBlockCount: Int = Gdx.graphics.width / blockSize;
        // Log.i(TAG, "randomIndices size: ${randomIndices.size}, target size: ${(verticalBlockCount*horizontalBlockCount).toInt()}")
        for (column in 0 .. horizontalBlockCount) {
            for (row in 0 .. verticalBlockCount) {
                val index = (column * verticalBlockCount + row)
//                if (randomIndices.size != verticalBlockCount*horizontalBlockCount) {
//                    randomIndices[index] = (random.nextFloat()*leftSpectrum.size/3).toInt()
//                }
//                var targetIndex = randomIndices[index]
//                if (targetIndex == null) {
//                    targetIndex = 0
//                }
//                targetIndex %= leftSpectrum.size
                val targetIndex = index % 50
                //Log.i(TAG, "targetIndex: $targetIndex")
                val factor: Float = (leftSpectrum[targetIndex]*1.0f / 250.0f).toFloat()
                shapeRenderer.setColor(factor * 2.2f, factor*1.6f, factor*0.8f, 1.0f)
                shapeRenderer.rect(column*blockSize*1.0f + padding, row*blockSize*1.0f + padding, blockSize*1.0f - padding * 2, blockSize*1.0f - padding*2)

            }
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        super.dispose()
    }
}