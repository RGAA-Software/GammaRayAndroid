package com.tc.client.effects

import com.tc.client.R

class EffectDefinition {

    companion object {
        const val EFFECT_BAR_LINE = 1
        const val EFFECT_HEXAGON_BLOCK = 2
        const val EFFECT_RECTANGLE_BLOCK = 3
        const val EFFECT_BAR_TIME = 4
        const val EFFECT_CIRCLE_IMAGES = 5
        const val EFFECT_BAR_LINE2 = 6
    }

    class EffectInfo(var idx: Int, var name: String, var iconResId: Int) {

    }

    val effects: MutableList<EffectInfo> = mutableListOf()

    fun init() {
        effects.apply {
            add(EffectInfo(EFFECT_BAR_LINE, "Bar Line", R.drawable.effect_bar_line))
            add(EffectInfo(EFFECT_HEXAGON_BLOCK, "Hexagon Block", R.drawable.effect_hexagon))
            add(EffectInfo(EFFECT_RECTANGLE_BLOCK, "Rectangle Block", R.drawable.effect_rectangle))
            add(EffectInfo(EFFECT_BAR_TIME, "Bar Time", R.drawable.effect_bar_time))
            add(EffectInfo(EFFECT_CIRCLE_IMAGES, "Circle Images", R.drawable.effect_circle_images))
            add(EffectInfo(EFFECT_BAR_LINE2, "Bar Line2", R.drawable.effect_bar_line2))
        }
    }

}