package com.tc.client

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.tc.client.steam.SteamAppManager
import java.util.Timer
import java.util.TimerTask

class AppContext(private var context: Context) {

    private var handlerThread: HandlerThread = HandlerThread("bg");
    private lateinit var handler: Handler;
    private var mainHandler = Handler(context.mainLooper);
    public var steamManager = SteamAppManager(context)

    private var timer = Timer()
    private var timer1SCallbacks = mutableMapOf<String, Runnable>()

    init {
        handlerThread.start();
        handler = Handler(handlerThread.looper);

        timer.schedule(object: TimerTask() {
            override fun run() {
                timer1SCallbacks.forEach {
                    it.value.run()
                }
            }
        }, 100, 1000);
    }

    public fun postTask(task: Runnable) {
        handler.post(task);
    }

    public fun postDelayTask(task: Runnable, time: Long) {
        handler.postDelayed(task, time);
    }

    public fun postUITask(task: Runnable) {
        mainHandler.post(task);
    }

    public fun register1STimer(name: String, t: Runnable) {
        timer1SCallbacks[name] = t;
    }

    public fun remove1STimer(name: String) {
        timer1SCallbacks.remove(name)
    }

}