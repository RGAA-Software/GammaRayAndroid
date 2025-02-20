package com.tc.client.ui.server

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ServerItemDecoration : RecyclerView.ItemDecoration() {

    private val density = Resources.getSystem().displayMetrics.density
    private var itemFactor = 1.66;
    private var itemWidth = 100;
    private var itemHeight = 120;

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state);
        var pixSize = 0;
        if (view.layoutParams != null) {
            view.layoutParams.width = (density * itemWidth * itemFactor).toInt();
            view.layoutParams.height = (density * itemHeight * itemFactor).toInt();
            pixSize = ((Resources.getSystem().displayMetrics.widthPixels - view.layoutParams.width*2)/4).toInt();
        }

        outRect.top = (15*density).toInt();
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = pixSize
            outRect.right = pixSize/2
        } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
            outRect.left = pixSize/2
            outRect.right = pixSize
        }
    }
}