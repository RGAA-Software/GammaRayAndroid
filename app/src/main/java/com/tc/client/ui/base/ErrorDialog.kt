package com.tc.client.ui.base

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.tc.client.R

class ErrorDialog(val ctx: Context) : AlertDialog(ctx) {

    companion object {
        fun createDialog(c: Context, title: String, msg: String) : ErrorDialog {
            val dialog = ErrorDialog(c)
            dialog.title = title
            dialog.msg = msg
            return dialog
        }
    }

    private var title: String = ""
    private var msg: String = ""
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = View.inflate(ctx, R.layout.dialog_error, null)
        setContentView(view)
        setCanceledOnTouchOutside(false)

        view.findViewById<Button>(R.id.id_sure).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.id_cancel).setOnClickListener {
            dismiss()
        }

    }

    override fun show() {
        super.show()
        view.findViewById<TextView>(R.id.id_title).text = title
        view.findViewById<TextView>(R.id.id_message).text = msg
        val density = Resources.getSystem().displayMetrics.density
        window?.setLayout((density*350).toInt(), (density* 280).toInt());
    }

}