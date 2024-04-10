package com.tc.client

import android.content.Context
import android.util.Log
import com.tc.client.util.SpUtils
import okhttp3.internal.wait
import org.json.JSONObject

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
//        serverIp = SpUtils.getInstance(ctx).getString(KEY_IP, "10.0.0.16");
        serverIp = SpUtils.getInstance(ctx).getString(KEY_IP, "192.168.31.5");
        serverPort = SpUtils.getInstance(ctx).getInt(KEY_PORT, 20368);

        apiBaseUrl = "http://$serverIp:$serverPort";
    }



    fun parseScanInfo(info: String): ScanInfo {
        val scanInfo = ScanInfo();
        try {
            val obj = JSONObject(info);
            scanInfo.sysUniqueId = obj.getString("sys_unique_id");
            scanInfo.httpServerPort = obj.getInt("http_server_port");
            scanInfo.wsServerPort = obj.getInt("ws_server_port");
            scanInfo.udpServerPort = obj.getInt("udp_server_port");
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
        Log.i(TAG, "Server ip: $serverIp")
        Log.i(TAG, "Server port: $serverPort")
        Log.i(TAG, "Settings------------------------")
    }
}