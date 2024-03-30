package com.tc.client

import android.content.Context
import android.util.Log
import com.tc.client.util.SpUtils

class Settings {
    private val TAG = "Main";

    private val KEY_IP = "key_ip";
    private val KEY_PORT = "key_port";

    public var serverIp = "";
    public var serverPort = 0;

    public var apiBaseUrl = "";

    companion object {
        private val settings = Settings()
        fun getInstance(): Settings {
            return settings
        }
    }

    fun loadConfig(ctx: Context) {
        //serverIp = SpUtils.getInstance(ctx).getString(KEY_IP, "10.0.0.16");
        serverIp = SpUtils.getInstance(ctx).getString(KEY_IP, "192.168.31.5");
        serverPort = SpUtils.getInstance(ctx).getInt(KEY_PORT, 20368);

        apiBaseUrl = "http://$serverIp:$serverPort";
    }

    fun dump() {
        Log.i(TAG, "Settings------------------------")
        Log.i(TAG, "Server ip: $serverIp")
        Log.i(TAG, "Server port: $serverPort")
        Log.i(TAG, "Settings------------------------")
    }
}