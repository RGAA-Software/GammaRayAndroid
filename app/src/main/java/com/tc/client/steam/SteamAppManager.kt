package com.tc.client.steam

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.tc.client.Result
import com.tc.client.ServerApi
import com.tc.client.Settings
import com.tc.client.util.HttpUtil
import org.json.JSONObject

class SteamAppManager(val context: Context) {

    val TAG = "Steam";

    fun requestSteamApps(): Result<List<SteamApp>> {
        val url = Settings.getInstance().apiBaseUrl + ServerApi.apps
        val resp = HttpUtil.reqUrl(url)
        var result = mutableListOf<SteamApp>();
        if (TextUtils.isEmpty(resp)) {
            return Result(Result.ERR, result);
        }
        try {
            val obj = JSONObject(resp);
            val code = obj.getInt("code");
            val msg = obj.getString("message");
            if (obj.has("data")) {
                val data = obj.getJSONArray("data");
                for (i in 0 until data.length()) {
                    val app = SteamApp();
                    val item = data.getJSONObject(i);
                    app.appId = item.getInt("app_id");
                    app.coverName = item.getString("cover_name");
                    app.appName = item.getString("name");
                    app.engine = item.getString("engine");
                    result.add(app)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "steam app parse json error: ${e.message}");
            return Result(Result.ERR, result);
        }
        return Result(Result.OK, result);
    }

    fun startRemoteApplication(): Result<String> {
        val url = Settings.getInstance().apiBaseUrl + ServerApi.startApp
        val resp = HttpUtil.reqUrl(url)
        if (TextUtils.isEmpty(resp)) {
            return Result(Result.ERR, "start failed");
        }

        return Result(Result.OK, "OK")
    }

}