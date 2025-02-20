package com.tc.client.effects

import android.content.Context
import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tc.client.effects.particle.SpreadParticle
import com.tc.client.impl.ThunderApp
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.cos
import kotlin.math.sin


class SpreadTriangle(var ctx: Context, var app: ThunderApp) : EffectView(ctx, app) {

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var circleImageShader: ShaderProgram
    private lateinit var rightImage: Texture
    private var rotateAngle = 0.0f
    private val fromColor: Color = Color.valueOf("#307dff")
    private val toColor: Color = Color.valueOf("#46ecfa")
    private val spreadParticle = SpreadParticle(40)

    private lateinit var stage: Stage
    private lateinit var shapeDrawer: ShapeDrawer
    private lateinit var texture: Texture

    override fun create() {
        super.create()
        shapeRenderer = ShapeRenderer();
        spriteBatch = SpriteBatch()
        rightImage = Texture("images/gamma_ray.jpg")

        circleImageShader = ShaderProgram(Gdx.files.internal("shaders/circle_image_vs.glsl"),
            Gdx.files.internal("shaders/circle_image_fs.glsl"));
        if (!circleImageShader.isCompiled) {
            Log.i(TAG, "Shader compile error: " + circleImageShader.getLog());
        } else {
            Log.i(TAG, "shader ok.")
        }

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

        stage.batch.begin()
        shapeDrawer.setColor(1.0f, 1.0f, 1.0f, 1.0f)
        spreadParticle.render(shapeDrawer)
        stage.batch.end()

        // 1.
        val targetImageWidth = rightImage.width * 2.0f
        val targetImageHeight = rightImage.height * 2.0f
        val targetImageY = (Gdx.graphics.height - targetImageHeight)/2
        val targetRightImageOffsetX = (Gdx.graphics.width - targetImageWidth)/2
        rotateAngle += Gdx.graphics.deltaTime*8

        spriteBatch.begin();
        spriteBatch.setShader(circleImageShader);

        spriteBatch.draw(rightImage, targetRightImageOffsetX, targetImageY, targetImageWidth/2, targetImageHeight/2,
            targetImageWidth, targetImageHeight,
            1.0f, 1.0f, -rotateAngle,
            0, 0,
            rightImage.getWidth(), rightImage.getHeight(),
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
        val leftCenterY = targetImageY + targetImageHeight/2
        val lineWidth = 10.0f
        val rightCenterX = targetRightImageOffsetX + targetImageWidth/2
        val rightCenterY = leftCenterY

        leftSpectrum.forEachIndexed{idx, value ->
            if (idx >= numberOfLines) {
                return@forEachIndexed
            }
            val targetValue = value * 0.8f
            val leftAngle: Float = angleStep * idx
            val radiusOffset = 10

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
        shapeRenderer.dispose()
        spriteBatch.dispose()
        circleImageShader.dispose()
        rightImage.dispose()
        stage.dispose()
        texture.dispose()
    }

}