package com.tc.client

import android.text.TextUtils
import android.util.Log
import com.tc.client.util.HttpUtil

class NetworkChecker(val appContext: AppContext) {

    companion object {
        const val TAG = "Main"
    }

    interface OnCheckAvailableCallback {
        fun onCheck(scanInfo: ScanInfo);
    }

    fun checkAvailableServer(scanInfo: ScanInfo, cbk: OnCheckAvailableCallback) {
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

}