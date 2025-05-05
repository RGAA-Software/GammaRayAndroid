package com.tc.client.effects

import android.app.Activity
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.tc.client.App
import com.tc.client.AppContext
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.impl.ThunderApp
import java.util.Timer
import java.util.TimerTask

class EffectActivity : FragmentActivity(),  AndroidFragmentApplication.Callbacks {

    companion object {
        const val TAG = "Effect";
    }

    private lateinit var srvIp: String
    private var srvPort: Int = 0
    private lateinit var streamId: String
    private lateinit var remoteDeviceId: String
    private lateinit var appContext: AppContext
    private lateinit var thunderApp: ThunderApp
    private var renderTimer: Timer = Timer()
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var effectFragment: EffectFragment
    private var wakeLock: WakeLock? = null

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
        fragmentContainer = findViewById(R.id.id_fragment_container)
        srvIp = intent.getStringExtra("ip")!!
        srvPort = intent.getIntExtra("port", 20371)
        streamId = intent.getStringExtra("streamId")!!
        remoteDeviceId = intent.getStringExtra("remoteDeviceId")!!
        val effectIdx = intent.getIntExtra("idx", 1)

        appContext = (application as App).appContext

        thunderApp = ThunderApp(srvIp, srvPort, true, false, false, streamId, remoteDeviceId)
        thunderApp.init(false, null, false, false, 0, Settings.getInstance().deviceId, streamId)
        thunderApp.start()

        effectFragment = EffectFragment(thunderApp, effectIdx)

        renderTimer.schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    effectFragment.onRefresh()
                }
            }
        }, 100, 16);

        supportFragmentManager.beginTransaction().add(R.id.id_fragment_container, effectFragment).commit()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "GammaRay:WakeLock")
    }

    override fun onResume() {
        super.onResume()
        thunderApp.nativeResume()
        if (wakeLock != null) {
            wakeLock?.acquire(120*60*1000L)
        }
    }

    override fun onPause() {
        super.onPause()
        thunderApp.nativePause()
        if (wakeLock != null) {
            wakeLock?.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        thunderApp.nativeDestroy()
    }

    override fun exit() {

    }

}