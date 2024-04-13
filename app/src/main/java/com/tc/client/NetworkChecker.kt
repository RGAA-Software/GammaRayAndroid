package com.tc.client

import android.text.TextUtils
import android.util.Log
import com.tc.client.db.DBServer
import com.tc.client.util.HttpUtil

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
        appContext.postTask{
            scanInfo.ipInfo.forEach {
                val url = "http://" + it.ip + ":" + scanInfo.httpServerPort + ServerApi.ping;
                val resp = HttpUtil.reqUrl(url)
                if (!TextUtils.isEmpty(resp) && resp == "Pong") {
                    Log.i(TAG, "find the ip: ${it.ip}")
                    scanInfo.targetIp = it.ip;
                    scanInfo.targetIpType = it.type;
                    cbk.onCheck(scanInfo);
                    return@postTask;
                }
                scanInfo.targetIp = ""
                cbk.onCheck(scanInfo)
            }
        }
    }

    fun checkDBServerAvailable(srv: DBServer, cbk: OnDBServerCheckAvailableCallback) {
        appContext.postTask{
            val url = "http://" + srv.serverIp + ":" + srv.httpServerPort + ServerApi.ping;
            val resp = HttpUtil.reqUrl(url)
            val originAvailable = srv.available
            if (!TextUtils.isEmpty(resp) && resp == "Pong") {
                Log.i(TAG, "find the ip: ${srv.serverIp}")
                srv.available = true
                cbk.onCheck(srv, originAvailable);
                return@postTask;
            }
            srv.available = false
            cbk.onCheck(srv, originAvailable)
        }
    }

}