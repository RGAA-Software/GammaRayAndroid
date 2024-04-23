package com.tc.client.ui.effects

import android.text.TextUtils
import android.util.Log
import com.tc.client.AppContext
import com.tc.reading.ui.day.DaySentence
import com.tc.client.util.DateUtil
import com.tc.client.util.HttpUtil
import org.json.JSONObject

class DaySentenceManager(private var appContext: AppContext) {

    companion object {
        const val TAG = "Day";
    }

    public fun requestTodaySentence(callback: (st: DaySentence) -> Unit) {
        appContext.postTask{
            val today = DateUtil.fmtCurrentDay();
            // youdao

            // shanbay
            val shanbay_url = "https://apiv3.shanbay.com/weapps/dailyquote/quote/?date=$today"
            val resp = HttpUtil.reqUrl(shanbay_url);
            Log.i(TAG, "resp:$resp");
            if (!TextUtils.isEmpty(resp)) {
                val sentence = DaySentence("shanbay");
                val obj = JSONObject(resp!!);
                if (obj.has("author")) {
                    sentence.author = obj.getString("author");
                }
                if (obj.has("content")) {
                    sentence.content = obj.getString("content");
                }
                if (obj.has("translation")) {
                    sentence.translation = obj.getString("translation");
                }
                if (obj.has("origin_img_urls")) {
                    val array = obj.getJSONArray("origin_img_urls");
                    if (array.length() > 0) {
                        sentence.imageUrl = array.getString(0);
                    }
                }
                callback(sentence);
            }
        };
    }

}