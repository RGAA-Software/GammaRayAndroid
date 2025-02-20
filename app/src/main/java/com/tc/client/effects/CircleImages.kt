package com.tc.client.effects

import android.content.Context
import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.tc.client.impl.ThunderApp
import kotlin.math.cos
import kotlin.math.sin


class CircleImages(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var circleImageShader: ShaderProgram
    private lateinit var leftImage: Texture
    private lateinit var rightImage: Texture
    private var rotateAngle = 0.0f
    private val fromColor: Color = Color.valueOf("#F31B20")
    private val toColor: Color = Color.valueOf("#FFE4C2")

    override fun create() {
        super.create()
        shapeRenderer = ShapeRenderer();
        spriteBatch = SpriteBatch()
        leftImage = Texture("images/rhythm_master.jpg")
        rightImage = Texture("images/gamma_ray.jpg")

        circleImageShader = ShaderProgram(Gdx.files.internal("shaders/circle_image_vs.glsl"),
            Gdx.files.internal("shaders/circle_image_fs.glsl"));
        if (!circleImageShader.isCompiled) {
            Log.i(TAG, "Shader compile error: " + circleImageShader.getLog());
        } else {
            Log.i(TAG, "shader ok.")
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render() {
        super.render()

        // 1.
        val targetImageWidth = leftImage.width * 2.0f
        val targetImageHeight = leftImage.height * 2.0f
        val targetImageY = (Gdx.graphics.height - targetImageHeight)/2
        val targetLeftImageOffsetX = targetImageWidth * 2/3
        val targetRightImageOffsetX = Gdx.graphics.width - targetImageWidth - targetLeftImageOffsetX
        rotateAngle += Gdx.graphics.deltaTime*8
        spriteBatch.begin();
        spriteBatch.setShader(circleImageShader);
        spriteBatch.draw(leftImage, targetLeftImageOffsetX, targetImageY, targetImageWidth/2, targetImageHeight/2,
            targetImageWidth, targetImageHeight,
            1.0f, 1.0f, rotateAngle,
            0, 0,
            leftImage.getWidth(), leftImage.getHeight(),
            false, false)

        spriteBatch.draw(rightImage, targetRightImageOffsetX, targetImageY, targetImageWidth/2, targetImageHeight/2,
            targetImageWidth, targetImageHeight,
            1.0f, 1.0f, -rotateAngle,
            0, 0,
            leftImage.getWidth(), leftImage.getHeight(),
            false, false)

        spriteBatch.end();
        spriteBatch.setShader(null)

        // 2.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f)

        val xGap = 0;//2.0f;
        val itemWidth = Gdx.graphics.width * 1.0f / leftSpectrum.size * 1.2f
        val xStep = itemWidth - xGap;

        val numberOfLines = 100
        val angleStep: Float = 360.0f / numberOfLines
        val leftCenterX = targetLeftImageOffsetX + targetImageWidth/2
        val leftCenterY = targetImageY + targetImageHeight/2
        val lineWidth = 10.0f
        val rightCenterX = targetRightImageOffsetX + targetImageWidth/2
        val rightCenterY = leftCenterY

        leftSpectrum.forEachIndexed{idx, value ->
            if (idx >= numberOfLines) {
                return@forEachIndexed
            }
            val targetValue = value * 2.3f
            val leftAngle: Float = angleStep * idx
            val radiusOffset = 10
            val leftCosValue = cos(Math.toRadians(leftAngle.toDouble()))
            val leftSinAngle = sin(Math.toRadians(leftAngle.toDouble()))
            val leftStartX: Double = leftCenterX + (targetImageWidth/2 + radiusOffset) * leftCosValue
            val leftStartY: Double = leftCenterY + (targetImageHeight/2 + radiusOffset) * leftSinAngle
            val leftEndX: Double = leftStartX + leftCosValue * (targetValue)
            val leftEndY: Double = leftStartY + leftSinAngle * (targetValue)
            shapeRenderer.identity();
            shapeRenderer.rectLine(
                leftStartX.toFloat(),
                leftStartY.toFloat(),
                leftEndX.toFloat(),
                leftEndY.toFloat(),
                lineWidth
            )

            val rightAngle: Float = 180 - angleStep * idx
            val rightCosValue = cos(Math.toRadians(rightAngle.toDouble()))
            val rightSinAngle = sin(Math.toRadians(rightAngle.toDouble()))
            val rightStartX: Double = rightCenterX +  (targetImageHeight/2 + radiusOffset) * rightCosValue
            val rightStartY: Double = rightCenterY +  (targetImageHeight/2 + radiusOffset) * rightSinAngle
            val rightEndX: Double = rightStartX + rightCosValue * (targetValue)
            val rightEndY: Double = rightStartY + rightSinAngle * (targetValue)

            shapeRenderer.identity();
            shapeRenderer.translate(rightStartX.toFloat(), rightStartY.toFloat(), 0f)
            shapeRenderer.rotate(0.0f, 0f, 1.0f ,rightAngle)
            shapeRenderer.rect(0f, -lineWidth/2,
                targetValue.toFloat(), lineWidth,
                fromColor, toColor,
                toColor, fromColor )
        }

        shapeRenderer.end()
    }

    override fun dispose() {
        super.dispose()
    }

}