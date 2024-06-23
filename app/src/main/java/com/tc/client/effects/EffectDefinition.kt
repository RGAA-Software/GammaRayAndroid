package com.tc.client.effects

import com.tc.client.R

class EffectDefinition {

    companion object {
        const val EFFECT_BAR_LINE = 1
        const val EFFECT_HEXAGON_BLOCK = 2
        const val EFFECT_RECTANGLE_BLOCK = 3
    }

    class EffectInfo(var idx: Int, var name: String, var iconResId: Int) {

    }

    val effects: MutableList<EffectInfo> = mutableListOf()

    fun init() {
        effects.apply {
            add(EffectInfo(EFFECT_BAR_LINE, "Bar Line", R.drawable.effect_bar_line))
            add(EffectInfo(EFFECT_HEXAGON_BLOCK, "Hexagon Block", R.drawable.effect_hexagon))
            add(EffectInfo(EFFECT_RECTANGLE_BLOCK, "Rectangle Block", R.drawable.effect_rectangle))
        }
    }

}