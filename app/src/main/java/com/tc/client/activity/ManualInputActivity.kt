package com.tc.client.activity

import android.R.attr
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.tc.client.R
import com.tc.client.databinding.ActivityManualInputBinding


class ManualInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualInputBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.root.findViewById<TextView>(R.id.id_title_bar_text).text =
            getString(R.string.manual_input_server)
        binding.root.findViewById<ImageView>(R.id.id_back).setOnClickListener {
            finish()
        }

        val ipInputText = binding.root.findViewById<TextInputEditText>(R.id.id_ip_input)
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

}