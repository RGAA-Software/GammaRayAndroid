package com.tc.client.ui.effects

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.backends.android.AndroidFragmentApplication

class EffectFragment : AndroidFragmentApplication(), InputProcessor {

    override fun keyDown(keycode: Int): Boolean {
        return false;
    }

    override fun keyUp(keycode: Int): Boolean {
        return false;
    }

    override fun keyTyped(character: Char): Boolean {
        return false;
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false;
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false;
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false;
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        TODO("Not yet implemented")
    }
}