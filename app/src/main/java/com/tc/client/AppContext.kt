package com.tc.client

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.tc.client.db.DBManager
import com.tc.client.steam.SteamAppManager
import java.util.Timer
import java.util.TimerTask

class AppContext(private var context: Context) {

    private var handlerThread: HandlerThread = HandlerThread("bg");
    private lateinit var handler: Handler;
    private var mainHandler = Handler(context.mainLooper);
    var steamManager = SteamAppManager(context)

    private var networkThread: HandlerThread = HandlerThread("network")
    private var networkHandler: Handler

    private var timer1S = Timer()
    private var timer1SCallbacks = mutableMapOf<String, Runnable>()

    private var timer2S = Timer()
    private var timer2SCallback = mutableMapOf<String, Runnable>()

    val dbManager: DBManager = DBManager(context)

    init {
        handlerThread.start();
        handler = Handler(handlerThread.looper);

        networkThread.start()
        networkHandler = Handler(networkThread.looper)

        timer1S.schedule(object: TimerTask() {
            override fun run() {
                timer1SCallbacks.forEach {
                    it.value.run()
                }
            }
        }, 100, 1000);

        timer2S.schedule(object: TimerTask() {
            override fun run() {
                timer2SCallback.forEach { (_, v) ->
                    v.run()
                }
            }
        }, 100, 2000)
    }

    fun postTask(task: Runnable) {
        handler.post(task);
    }

    fun postNetworkTask(task: Runnable) {
        networkHandler.post(task)
    }

    fun spawnInNewThread(task: Runnable) {
        Thread(task).start()
    }

    fun postDelayTask(task: Runnable, time: Long) {
        handler.postDelayed(task, time);
    }

    fun postUITask(task: Runnable) {
        mainHandler.post(task);
    }

    fun postUIDelayTask(task: Runnable, time: Long) {
        mainHandler.postDelayed(task, time)
    }

    fun register1STimer(name: String, t: Runnable) {
        timer1SCallbacks[name] = t;
    }

    fun remove1STimer(name: String) {
        timer1SCallbacks.remove(name)
    }

    fun register2STimer(name: String, t: Runnable) {
        timer2SCallback[name] = t
    }

    fun remove2STimer(name: String) {
        timer2SCallback.remove(name)
    }

}