package com.tc.client.effects.particle

import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.tc.client.effects.SpreadTriangle
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.random.Random

class SpreadParticle {

    companion object {
        const val TAG = "SpreadParticle"
    }

    class Particle {
        var x: Float = 0.0f
        var y: Float = 0.0f
        var color: Color = Color.CYAN
        var scale: Float = 1.0f
        var speedX: Float = 0.0f
        var speedY: Float = 0.0f
        var rotate: Float = 0.0f
        var rotateSpeed: Float = 0.0f

        companion object {
            fun make(x: Float, y: Float) : Particle {
                val p = Particle()
                p.x = x
                p.y = y
                p.speedX = Random.nextFloat() * 180 * (if (Random.nextFloat() > 0.5f) 1.0f else -1.0f) + 20
                p.speedY = Random.nextFloat() * 120 * (if (Random.nextFloat() > 0.5f) 1.0f else -1.0f) + 20
                p.scale = Random.nextFloat() * 80.8f + 10
                p.rotate = Random.nextFloat() * 360.0f
                p.rotateSpeed = Random.nextFloat() * 1.6f
                p.color = Color(Random.nextFloat(), 0.5f+Random.nextFloat(), 0.5f+Random.nextFloat(), 1.0f)
                return p
            }
        }

        fun update() {
            x += Gdx.graphics.deltaTime * speedX
            y += Gdx.graphics.deltaTime * speedY
            rotate += Gdx.graphics.deltaTime * rotateSpeed
        }

        fun isOffScreen() : Boolean {
            val offset = 100
            return x >= (Gdx.graphics.width + offset) || y >= (Gdx.graphics.height + offset) || x <= -offset || y <= -offset;
        }
    }

    var particles = mutableListOf<Particle>()
    var maxParticles: Int = 0
    var timeDuration: Float = 0.0f

    constructor(max: Int) {
        maxParticles = max
    }

    fun count() : Int {
        return particles.size
    }

    fun add(x: Float, y: Float) {
        if (particles.size > maxParticles) {
            return
        }
        particles.add(Particle.make(x, y))
    }

    fun render(renderer: ShapeDrawer) {
        timeDuration += Gdx.graphics.deltaTime
        if (timeDuration > 0.016* 5) {
            add(Gdx.graphics.width/2.0f, Gdx.graphics.height/2.0f)
        }

        particles.removeIf {
            it.isOffScreen()
        }
        particles.forEach {
            it.update()
            renderer.setColor(it.color)
            renderer.filledPolygon(
                it.x, it.y,
                3,
                it.scale, it.scale,
                it.rotate
            );

        }
    }

}