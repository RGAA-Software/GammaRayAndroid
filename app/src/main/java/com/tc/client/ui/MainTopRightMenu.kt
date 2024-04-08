package com.tc.client.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.tc.client.R
import com.tc.client.activity.ManualInputActivity
import com.tc.client.activity.QRCodeScanActivity

class MainTopRightMenu(val activity: Activity) {

    private lateinit var popupWindow: PopupWindow;

    init {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_top_right, null);
        popupWindow = PopupWindow(view,  ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.isTouchable = true;
        popupWindow.setBackgroundDrawable(ColorDrawable(0xffffff));
        popupWindow.elevation = Resources.getSystem().displayMetrics.density * 10;

        view.findViewById<View>(R.id.id_scan_server).setOnClickListener {
            activity.startActivityForResult(Intent(activity, QRCodeScanActivity::class.java), 100);
            popupWindow.dismiss();
        }

        view.findViewById<View>(R.id.id_manual_input).setOnClickListener {
            activity.startActivity(Intent(activity, ManualInputActivity::class.java));
            popupWindow.dismiss();
        }
    }

    public fun show(anchor: View) {
        popupWindow.showAsDropDown(anchor,
            -(Resources.getSystem().displayMetrics.density * 135).toInt(), 15)
        popupWindow.contentView.layoutParams.width =
            (Resources.getSystem().displayMetrics.density * 180).toInt();
    }

}