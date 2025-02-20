package com.tc.client.steam

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.tc.client.util.Result
import com.tc.client.ServerApi
import com.tc.client.Settings
import com.tc.client.util.HttpUtil
import org.json.JSONObject

class SteamAppManager(val context: Context) {

    val TAG = "Steam";

    fun requestSteamGames(): Result<List<SteamGame>> {
        val url = Settings.getInstance().getApiBaseUrl() + ServerApi.API_GAMES
        Log.i(TAG, "request steam games: $url")
        val resp = HttpUtil.reqUrl(url)
        val result = mutableListOf<SteamGame>();
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
                    val app = SteamGame();
                    val item = data.getJSONObject(i);
                    app.gameId = item.getInt("app_id");
                    app.coverName = item.getString("cover_name");
                    app.gameName = item.getString("name");
                    app.engine = item.getString("engine");
                    app.steamUrl = item.getString("steam_url")
                    if (item.has("exes")) {
                        val exeArray = item.getJSONArray("exes")
                        if (exeArray.length() > 0) {
                            app.exePath = exeArray.getString(0)
                        }
                    }
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

    fun startGame(gamePath: String): Result<String> {
        val url = Settings.getInstance().getApiBaseUrl() + ServerApi.API_START_GAME
        val params = mutableMapOf<String, String>()
        params["game_path"] = gamePath;
        val resp = HttpUtil.postUrl(url, params)
        if (TextUtils.isEmpty(resp)) {
            return Result(Result.ERR, "start failed");
        }
        return Result(Result.OK, "OK")
    }

    fun stopGame(gameId: String): Result<String> {
        val url = Settings.getInstance().getApiBaseUrl() + ServerApi.API_STOP_GAME
        val params = mutableMapOf<String, String>()
        params["game_id"] = gameId;
        val resp = HttpUtil.postUrl(url, params)
        if (TextUtils.isEmpty(resp)) {
            return Result(Result.ERR, "start failed");
        }
        return Result(Result.OK, "OK")
    }

    fun requestRunningGames(): Result<List<RunningGame>> {
        val url = Settings.getInstance().getApiBaseUrl() + ServerApi.API_RUNNING_GAMES
        val resp = HttpUtil.reqUrl(url)
        val result = mutableListOf<RunningGame>();
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
                    val rg = RunningGame();
                    val item = data.getJSONObject(i);
                    rg.gameId = item.getInt("game_id");
                    rg.gameExes = item.getString("game_exes")
                    result.add(rg)
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "requestRunningGame failed: ${e.message} ")
            return Result(Result.ERR, result);
        }
        return Result(Result.OK, result);
    }

}