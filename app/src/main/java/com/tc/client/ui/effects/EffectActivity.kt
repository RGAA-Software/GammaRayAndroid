package com.tc.client.ui.effects

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.tc.client.App
import com.tc.client.AppContext
import com.tc.client.R
import com.tc.client.impl.ThunderApp

class EffectActivity : Activity() {

    private lateinit var srvIp: String
    private var srvPort: Int = 0
    private lateinit var appContext: AppContext
    private lateinit var thunderApp: ThunderApp

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
    }

}