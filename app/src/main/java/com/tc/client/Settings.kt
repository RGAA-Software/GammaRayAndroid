package com.tc.client

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.tc.client.db.DBServer
import com.tc.client.events.OnServerAvailable
import com.tc.client.events.OnServerDeleted
import com.tc.client.events.OnServerEmpty
import com.tc.client.events.OnServerOffline
import com.tc.client.events.OnServerScanned
import com.tc.client.ui.steam.SteamAppFragment
import com.tc.client.util.SpUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class Settings {

    private val TAG = "Main";

    var currentServer: DBServer = DBServer()
    var showVirtualGamepad: Boolean = false
    var invertJoystickYAxis: Boolean = false
    var showCursor: Boolean = false
    var fullscreen: Boolean = false
    var deviceId: String = ""

    companion object {
        const val KEY_SHOW_VIRTUAL_GAMEPAD = "show_virtual_gamepad"
        const val KEY_INVERT_JOYSTICK_Y_AXIS = "invert_joystick_y_axis"
        const val KEY_SHOW_CURSOR = "show_cursor"
        const val KEY_FULLSCREEN = "fullscreen"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_STREAM_ID = "stream_id"

        private val settings = Settings()
        fun getInstance(): Settings {
            return settings
        }
    }

    fun loadConfig(ctx: Context) {
        showVirtualGamepad = SpUtils.getInstance(ctx).getBoolean(KEY_SHOW_VIRTUAL_GAMEPAD)
        invertJoystickYAxis = SpUtils.getInstance(ctx).getBoolean(KEY_INVERT_JOYSTICK_Y_AXIS)
        showCursor = SpUtils.getInstance(ctx).getBoolean(KEY_SHOW_CURSOR)
        fullscreen = SpUtils.getInstance(ctx).getBoolean(KEY_FULLSCREEN)
        deviceId = SpUtils.getInstance(ctx).getString(KEY_DEVICE_ID)
        if (TextUtils.isEmpty(deviceId)) {
            val androidId: String =
                android.provider.Settings.System.getString(ctx.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            deviceId = androidId
            SpUtils.getInstance(ctx).put(KEY_DEVICE_ID, deviceId)
        }
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
    fun onServerDeletedEvent(event: OnServerDeleted) {
        if (currentServer.serverIp == event.server.serverIp) {
            currentServer = DBServer()
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerEmptyEvent(event: OnServerEmpty) {
        currentServer = DBServer()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onMessageEvent(event: OnServerScanned) {
       if (currentServer.serverId == event.server.serverId) {
           currentServer.serverIp = event.server.serverIp
           currentServer.httpServerPort = event.server.httpServerPort
           currentServer.wsServerPort = event.server.wsServerPort
           currentServer.streamWsPort = event.server.streamWsPort
       }
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
            //scanInfo.udpServerPort = obj.getInt("udp_server_port");
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

    fun setShowVirtualGamepad(ctx: Context, state: Boolean) {
        showVirtualGamepad = state
        SpUtils.getInstance(ctx).put(KEY_SHOW_VIRTUAL_GAMEPAD, state)
    }

    fun setInvertJoystickYAxis(ctx: Context, state: Boolean) {
        invertJoystickYAxis = state
        SpUtils.getInstance(ctx).put(KEY_INVERT_JOYSTICK_Y_AXIS, state)
    }

    fun setShowCursor(ctx: Context, state: Boolean) {
        showCursor = state
        SpUtils.getInstance(ctx).put(KEY_SHOW_CURSOR, state)
    }

    fun setFullscreen(ctx: Context, state: Boolean) {
        fullscreen = state
        SpUtils.getInstance(ctx).put(KEY_FULLSCREEN, state)
    }

    fun dump() {
        Log.i(TAG, "Settings------------------------")
        Log.i(TAG, "Show virtual GamePad: $showVirtualGamepad")
        Log.i(TAG, "Invert Joystick Y Axis: $invertJoystickYAxis")
        Log.i(TAG, "Show cursor: $showCursor")
        Log.i(TAG, "Settings------------------------")
    }
}