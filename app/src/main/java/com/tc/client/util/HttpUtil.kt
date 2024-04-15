package com.tc.client.util

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class HttpUtil {

    companion object {
        const val TAG = "http"

        fun reqUrl(url: String): String? {
            val client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build()
            try {
                val request = Request.Builder()
                    .url(url)
                    .build();
                val resp = client.newCall(request).execute();
                if (resp.code != 200) {
                    return null;
                }
                val value = resp.body?.string();
                return value
            } catch (e: Exception) {
                Log.i(TAG, "reqUrl failed: ${e.message}")
                return null;
            }
        }

    }
}