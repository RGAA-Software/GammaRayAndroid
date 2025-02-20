package com.tc.client.activity

import android.R.attr
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.tc.client.App
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.databinding.ActivityManualInputBinding
import com.tc.client.events.OnAddScanInfo
import com.tc.client.ui.base.CustomAlertDialog
import com.tc.client.util.HttpUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject


class ManualInputActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Main"
    }

    private lateinit var binding: ActivityManualInputBinding
    private lateinit var ipInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualInputBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.root.findViewById<TextView>(R.id.id_title_bar_text).text =
            getString(R.string.manual_input_server)
        binding.root.findViewById<ImageView>(R.id.id_back).setOnClickListener {
            finish()
        }
        binding.root.findViewById<Button>(R.id.id_add_server).setOnClickListener {
            addServer()
        }

        ipInputText = binding.root.findViewById<TextInputEditText>(R.id.id_ip_input)
        ipInputText.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                if (attr.end > start) {
                    val destTxt = dest.toString()
                    val resultingTxt = (destTxt.substring(0, dstart)
                            + source?.subSequence(start, end)
                            ) + destTxt.substring(dend)
                    if (!resultingTxt.matches(
                            Regex(
                                "^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(:(\\d{1,5})?)?)?)?)?)?)?)?"
                            )
                        )
                    ) return ""
                }
                return source ?: ""
            }

        })
    }

    private fun addServer() {
        val ip = ipInputText.text.toString()
        val url = "http://${ip}:20369/v1/simple/info"
        (application as App).appContext.postTask {
            val resp = HttpUtil.reqUrl(url)
            val errorMsg = "Connect IP failed, please input the correct IP address.";
            if (resp == null) {
                tip("failed to request /v1/simple/info")
                errorDialog(errorMsg)
                return@postTask
            }
            try {
                val obj = JSONObject(resp)
                if (obj.getInt("code") != 200) {
                    tip("json error code")
                    errorDialog(errorMsg)
                    return@postTask
                }

                val info = obj.getJSONObject("data");
                val scanInfo = Settings.getInstance().parseScanInfo(info.toString())
                Log.i(TAG, "scan info after request: ${scanInfo}")
                if (!scanInfo.valid()) {
                    tip("not a valid scaninfo")
                    errorDialog(errorMsg)
                    return@postTask
                }

                val msg = OnAddScanInfo();
                msg.scanInfo = scanInfo
                EventBus.getDefault().post(msg)

                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "parse failed: ${e.message}")
            }
        }
    }

    private fun tip(m: String) {
        this.runOnUiThread {
            Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
        }
    }

    private fun errorDialog(m: String) {
        this.runOnUiThread {
            CustomAlertDialog.createDialog(this, "ERROR", m).show()
        }
    }

}