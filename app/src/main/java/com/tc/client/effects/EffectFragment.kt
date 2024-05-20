package com.tc.client.effects

import android.annotation.TargetApi
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.tc.client.R
import com.tc.client.effects.base.InterceptableViewGroup
import com.tc.client.effects.spine.LibgdxSpineFragment
import com.tc.client.impl.ThunderApp

class EffectFragment(var thunderApp: ThunderApp) : AndroidFragmentApplication(), InputProcessor {

    private lateinit var rootView: View
    private lateinit var container: InterceptableViewGroup
    private lateinit var effectView: EffectView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.effect_layout, null)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildEffectView()
    }

    private fun buildEffectView() {
        container = rootView.findViewById<InterceptableViewGroup>(R.id.container)
        effectView = EffectView(requireContext(), thunderApp)
        val eView: View = CreateGLAlpha(effectView)
        container.addView(eView)
        container.setIntercept(true)
        Gdx.input.inputProcessor = this
        Gdx.input.isCatchBackKey = true
    }

    private fun CreateGLAlpha(application: ApplicationListener): View {
        val cfg = AndroidApplicationConfiguration()
        cfg.a = 8
        cfg.b = cfg.a
        cfg.g = cfg.b
        cfg.r = cfg.g
        val view = initializeForView(application, cfg)
        if (view is SurfaceView) {
            val glView = graphics.view as GLSurfaceView
            glView.holder.setFormat(PixelFormat.TRANSLUCENT)
            glView.setZOrderMediaOverlay(true)
            glView.setZOrderOnTop(true)
        }
        return view
    }

    fun onRefresh() {

    }

    fun distroy() {

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        container.removeAllViews()
        buildEffectView()
    }

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
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false;
    }
}