package com.tc.client.util

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
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

        fun postUrl(url: String, args: Map<String, String>): String? {
            val client = OkHttpClient()
            val mediaType = "application/json;charset=utf-8".toMediaTypeOrNull()!!
            val jsonBody = JSONObject()
            args.forEach { (k, v) ->
                jsonBody.put(k, v)
            }
            val requestBody: RequestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                val resp = client.newCall(request).execute()
                if (resp.code != 200) {
                    return null;
                }
                val value = resp.body?.string();
                return value
            } catch (e: Exception) {
                Log.i(TAG, "Failed to execute request: ${e.message}")
                return ""
            }
//            client.newCall(request).enqueue(object : okhttp3.Callback {
//                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                    if (response.isSuccessful) {
//                        val responseString = response.body?.string()
//                        Log.i(TAG, "Response is: $responseString")
//                    } else {
//                        Log.i(TAG, "Failed to fetch data: ${response.code}")
//                    }
//                    response.close() // Important to close the response body!
//                }
//
//                override fun onFailure(call: okhttp3.Call, e: IOException) {
//                    Log.i(TAG, "Failed to execute request: ${e.message}")
//                }
//            })
        }
    }
}