package com.tc.client.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.tc.client.R

class MainTopRightMenu(val context: Context) {

    private lateinit var popupWindow: PopupWindow;

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.item_top_right, null);
        popupWindow = PopupWindow(view,  ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.isTouchable = true;
        popupWindow.setBackgroundDrawable(ColorDrawable(0xffffff));
        popupWindow.elevation = Resources.getSystem().displayMetrics.density * 10;
    }

    public fun show(anchor: View) {
        popupWindow.showAsDropDown(anchor,
            -(Resources.getSystem().displayMetrics.density * 135).toInt(), 15)
        popupWindow.contentView.layoutParams.width =
            (Resources.getSystem().displayMetrics.density * 180).toInt();
    }

}