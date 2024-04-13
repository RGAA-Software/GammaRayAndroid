package com.tc.client.ui.machine

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.tc.client.R

class MachineOpDialog(var ctx: Context) : AlertDialog(ctx) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(context, R.layout.dialog_op_machine, null)
        setContentView(view)

        view.findViewById<View>(R.id.id_op_all_apps).setOnClickListener {

        }

        view.findViewById<View>(R.id.id_op_delete_server).setOnClickListener {

        }
    }

    override fun show() {
        super.show()

        window?.decorView?.layoutParams?.height?.let {
            window?.setLayout((Resources.getSystem().displayMetrics.density* 350).toInt(), it)
        }
    }

}