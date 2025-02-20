package com.tc.client

import android.text.TextUtils
import android.util.Log
import com.tc.client.db.DBServer
import com.tc.client.util.HttpUtil
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Executable


class NetworkChecker(val appContext: AppContext) {

    companion object {
        const val TAG = "Main"
    }

    interface OnScanInfoCheckAvailableCallback {
        fun onCheck(scanInfo: ScanInfo);
    }

    interface OnDBServerCheckAvailableCallback {
        fun onCheck(s: DBServer, originAvailable: Boolean);
    }

    fun checkScanInfoAvailable(scanInfo: ScanInfo, cbk: OnScanInfoCheckAvailableCallback) {
        appContext.spawnInNewThread {
            scanInfo.ipInfo.forEach {
                val url = "http://" + it.ip + ":" + scanInfo.httpServerPort + ServerApi.API_PING;
                Log.i(TAG, "check available: $url")
                val resp = HttpUtil.reqUrl(url)
                var data = "";
                try {
                    val jsonObj = JSONObject(resp)
                    data = jsonObj.getString("data");
                } catch (e: Exception) {
                    scanInfo.targetIp = ""
                    cbk.onCheck(scanInfo)
                    return@forEach
                }
                if (data == "Pong") {
                    Log.i(TAG, "find the ip: ${it.ip}")
                    scanInfo.targetIp = it.ip;
                    scanInfo.targetIpType = it.type;
                    cbk.onCheck(scanInfo);
                    return@spawnInNewThread;
                }
                scanInfo.targetIp = ""
                cbk.onCheck(scanInfo)
            }
        }
    }

    fun checkDBServerAvailable(srv: DBServer, cbk: OnDBServerCheckAvailableCallback) {
        if (TextUtils.isEmpty(srv.serverIp) || TextUtils.isEmpty(srv.serverId.trim())) {
            srv.available = false
            cbk.onCheck(srv, false)
            return;
        }
        appContext.spawnInNewThread {
//            val pingBeg = System.currentTimeMillis()
//            val available = pingIP(srv.serverIp)
//            val pingEnd = System.currentTimeMillis()
//            Log.i(TAG, "available for: ${srv.serverIp}, result: $available, used time: ${pingEnd - pingBeg}")

            val beg = System.currentTimeMillis();
            val url = "http://" + srv.serverIp + ":" + srv.httpServerPort + ServerApi.API_PING;
            //Log.i(TAG, "check db server available: $url")
            val resp = HttpUtil.reqUrl(url)
            val end = System.currentTimeMillis();
            Log.i(TAG, "request for: $url, used: ${end-beg}");
            var data = ""
            try {
                val jsonObj = JSONObject(resp)
                data = jsonObj.getString("data")
            } catch (e: Exception) {
                srv.available = false
                cbk.onCheck(srv, false)
                return@spawnInNewThread
            }
            val originAvailable = srv.available
            if (data == "Pong") {
                Log.i(TAG, "find the ip: ${srv.serverIp}")
                srv.available = true
                cbk.onCheck(srv, originAvailable);
                return@spawnInNewThread;
            }
            srv.available = false
            cbk.onCheck(srv, originAvailable)
        }
    }

    fun pingIP(ipAddress: String): Boolean {
        return try {
            val command = "ping -c 1 $ipAddress"
            val runtime = Runtime.getRuntime()
            val process = runtime.exec(command)
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}