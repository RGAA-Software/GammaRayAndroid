package com.tc.client;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ControlLayerParamLoader {
    private static final String TAG = "Controller";
    private Context mContext;

    public ControlLayerParamLoader(Context context) {
        mContext = context;
    }

    public ControlLayerParam getDefaultLayerParam() {
        float density = Resources.getSystem().getDisplayMetrics().density;
        int screenWidthDp = (int) (Resources.getSystem().getDisplayMetrics().widthPixels / density);
        int screenHeightDp = (int) (Resources.getSystem().getDisplayMetrics().heightPixels / density);

        // left
        int marginLeftDp = 20;
        int thumbSizeDp = 180;

        ControlLayerParam param = new ControlLayerParam();
        param.leftThumbLeft = marginLeftDp;
        param.leftThumbTop = marginLeftDp;
        param.leftThumbSize = thumbSizeDp;

        // right
        int marginRightDp = 90;
        int marginBottomDp = marginLeftDp;
        param.rightThumbLeft = screenWidthDp - thumbSizeDp - marginRightDp;
        param.rightThumbTop = screenHeightDp - thumbSizeDp - marginBottomDp;
        param.rightThumbSize = thumbSizeDp;

        param.dpadButtonSize = 60;

        int abxyMarginRightDp = marginLeftDp;
        param.buttonGroupSize = 160;
        param.abxyGroupLeft = screenWidthDp - param.buttonGroupSize - abxyMarginRightDp;
        param.abxyGroupTop = abxyMarginRightDp;

        param.dpadGroupLeft = marginRightDp;
        param.dpadGroupTop = screenHeightDp - param.buttonGroupSize - marginBottomDp;

        return  param;
    }

}
