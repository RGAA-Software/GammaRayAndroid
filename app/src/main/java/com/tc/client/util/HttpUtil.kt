package com.tc.client.util

import okhttp3.OkHttpClient
import okhttp3.Request

class HttpUtil {

    companion object {

        fun reqUrl(url: String): String? {
            val client = OkHttpClient();
            val request = Request.Builder()
                .url(url)
                .build();
            try {
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