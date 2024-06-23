package com.tc.client.ui.server

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ServerItemDecorationHorizontal(private var itemCount: Int) : RecyclerView.ItemDecoration() {

    private val density = Resources.getSystem().displayMetrics.density;
    private var sizeRatio = 100.0f/120

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state);
        var itemGapPixelSize = 0;
        val itemGap = 20

        if (view.layoutParams != null) {
            itemGapPixelSize = (density * itemGap).toInt()
            val totalGapPixelSize = itemGapPixelSize * itemCount
            val itemWidth = (Resources.getSystem().displayMetrics.widthPixels - totalGapPixelSize)/itemCount
            view.layoutParams.height = (itemWidth/sizeRatio).toInt()
        }

        outRect.top = (20*density).toInt()
        outRect.right = itemGapPixelSize
    }
}