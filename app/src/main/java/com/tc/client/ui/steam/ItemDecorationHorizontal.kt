package com.tc.client.ui.steam

import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationHorizontal(private var dpSize: Int) : RecyclerView.ItemDecoration() {

    private val density = Resources.getSystem().displayMetrics.density;
    //private val pixSize = (density * dpSize).toInt();
    private var itemFactor = 1.66;
    private var itemWidth = 100;
    private var itemHeight = 150;

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state);
        var pixSize = 0;
        var itemCount = 4
        if (view.layoutParams != null) {
//            view.layoutParams.width = (density * itemWidth * itemFactor).toInt();
            val itemWidth = density * itemWidth * itemFactor
            view.layoutParams.height = (density * itemHeight * itemFactor).toInt();
            pixSize = ((Resources.getSystem().displayMetrics.widthPixels - itemWidth*itemCount)/(itemCount+1)).toInt();
            Log.i("Test", "width: " + Resources.getSystem().displayMetrics.widthPixels + ", " + pixSize)
        }

        outRect.top = (20*density).toInt();
        if (parent.getChildAdapterPosition(view) % itemCount == 0) {
            outRect.left = pixSize
            outRect.right = pixSize/2
        } else if (parent.getChildAdapterPosition(view) % itemCount == 1) {
            outRect.left = pixSize/2
            outRect.right = pixSize/2
        } else if (parent.getChildAdapterPosition(view) % itemCount == 2) {
            outRect.left = pixSize/2
            outRect.right = pixSize/2
        } else if (parent.getChildAdapterPosition(view) % itemCount == 3) {
            outRect.left = pixSize/2
            outRect.right = pixSize
        }
    }
}