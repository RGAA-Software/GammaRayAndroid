package com.tc.client.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class HttpUtil {

    companion object {

        fun reqUrl(url: String): String? {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build()
            try {
                val request = Request.Builder()
                    .url(url)
                    .build();
                val resp = client.newCall(request).execute();
                if (resp.code != 200) {
                    return null;
                }
                return resp.body?.string();
            } catch (e: Exception) {
                return null;
            }
        }

    }
}