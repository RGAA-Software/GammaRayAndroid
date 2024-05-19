package com.tc.client.ui.effects

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.tc.client.App
import com.tc.client.AppContext
import com.tc.client.R
import com.tc.client.impl.ThunderApp
import java.util.Timer
import java.util.TimerTask

class EffectActivity : Activity(),  AndroidFragmentApplication.Callbacks {

    companion object {
        const val TAG = "Effect";
    }

    private lateinit var srvIp: String
    private var srvPort: Int = 0
    private lateinit var appContext: AppContext
    private lateinit var thunderApp: ThunderApp
    private var renderTimer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE

        setContentView(R.layout.activity_effect)
        srvIp = intent.getStringExtra("ip")!!
        srvPort = intent.getIntExtra("port", 20371)

        appContext = (application as App).appContext

        thunderApp = ThunderApp(srvIp, srvPort, true)
        thunderApp.init(false, null, false, false, 0);
        thunderApp.start()

        renderTimer.schedule(object: TimerTask() {
            override fun run() {
                Log.i(TAG, "leftSpectrum size: " + thunderApp.leftSpectrum.size)
            }
        }, 100, 16);
    }

    override fun onResume() {
        super.onResume()
        thunderApp.nativeResume()
    }

    override fun onPause() {
        super.onPause()
        thunderApp.nativePause()
    }

    override fun onDestroy() {
        super.onDestroy()
        thunderApp.nativeDestroy()
    }

    override fun exit() {

    }

}