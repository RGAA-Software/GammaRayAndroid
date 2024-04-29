package com.tc.client.ui.steam

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.tc.client.R

class GameOpDialog(var ctx: Context) : AlertDialog(ctx) {

    lateinit var onStartGameClicked: View.OnClickListener
    lateinit var onStopGameClicked: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(context, R.layout.dialog_op_game, null)
        setContentView(view)

        view.findViewById<View>(R.id.id_op_all_apps).setOnClickListener {
            onStartGameClicked.onClick(it)
            this.dismiss()
        }

        view.findViewById<View>(R.id.id_op_delete_server).setOnClickListener {
            onStopGameClicked.onClick(it)
            this.dismiss()
        }
    }

    override fun show() {
        super.show()

        window?.decorView?.layoutParams?.height?.let {
            window?.setLayout((Resources.getSystem().displayMetrics.density* 350).toInt(), it)
        }
    }

}