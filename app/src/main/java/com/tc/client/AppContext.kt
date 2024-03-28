package com.tc.client

import android.content.Context
import android.os.Handler
import android.os.HandlerThread

class AppContext(private var context: Context) {

    private var handlerThread: HandlerThread = HandlerThread("bg");
    private lateinit var handler: Handler;
    private var mainHandler = Handler(context.mainLooper);

    init {
        handlerThread.start();
        handler = Handler(handlerThread.looper);
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

}