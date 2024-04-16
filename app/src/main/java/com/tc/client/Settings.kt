package com.tc.client

import android.content.Context
import android.util.Log
import com.tc.client.db.DBServer
import com.tc.client.events.OnServerAvailable
import com.tc.client.events.OnServerEmpty
import com.tc.client.events.OnServerOffline
import com.tc.client.ui.steam.SteamAppFragment
import com.tc.client.util.SpUtils
import okhttp3.internal.wait
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class Settings {

    private val TAG = "Main";

    public var currentServer: DBServer = DBServer()

    companion object {
        private val settings = Settings()
        fun getInstance(): Settings {
            return settings
        }
    }

    fun loadConfig(ctx: Context) {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerAvailableEvent(event: OnServerAvailable) {
        Log.i(SteamAppFragment.TAG, "onServerAvailableEvent in Settings.");
        currentServer = event.server
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerOfflineEvent(event: OnServerOffline) {
        if (currentServer.serverIp == event.server.serverIp) {
            currentServer = DBServer()
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerEmptyEvent(event: OnServerEmpty) {
        currentServer = DBServer()
    }

    fun getServerIp(): String {
        return currentServer.serverIp
    }

    fun getHttpServerPort(): Int {
        return currentServer.httpServerPort
    }

    fun getWsServerPort(): Int {
        return currentServer.wsServerPort
    }

    fun getUdpCastServerPort(): Int {
        return currentServer.udpCastServerPort
    }

    fun getApiBaseUrl(): String {
        return "http://${currentServer.serverIp}:${currentServer.httpServerPort}";
    }

    fun parseScanInfo(info: String): ScanInfo {
        val scanInfo = ScanInfo();
        try {
            val obj = JSONObject(info);
            scanInfo.sysUniqueId = obj.getString("sys_unique_id");
            scanInfo.iconIndex = obj.getInt("icon_idx");
            scanInfo.httpServerPort = obj.getInt("http_server_port");
            scanInfo.wsServerPort = obj.getInt("ws_server_port");
            scanInfo.udpServerPort = obj.getInt("udp_server_port");
            scanInfo.streamWsPort = obj.getInt("stream_ws_port");
            val ips = obj.getJSONArray("ips");
            for (i in 0 until ips.length()) {
                val ipInfo = ScanInfo.IpInfo();
                val ipInfoObj = ips.getJSONObject(i);
                ipInfo.ip = ipInfoObj.getString("ip");
                ipInfo.type = ipInfoObj.getString("type");
                scanInfo.ipInfo.add(ipInfo);
            }
            Log.i(TAG, "scanInfo: $scanInfo")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "parse scan info failed: " + e.message)
        }
        return scanInfo;
    }

    fun dump() {
        Log.i(TAG, "Settings------------------------")
        Log.i(TAG, "Settings------------------------")
    }
}