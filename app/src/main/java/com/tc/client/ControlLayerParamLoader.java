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
        int thumbSizeDp = 160;

        ControlLayerParam param = new ControlLayerParam();
        param.leftThumbLeft = marginLeftDp;
        param.leftThumbTop = marginLeftDp;
        param.leftThumbSize = thumbSizeDp;

        // right
        int marginRightDp = 110;
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

        // function button size
        param.funcButtonWidth = 60;
        param.funcButtonHeight = 60;

        // ls
        param.lsLeft = marginLeftDp;
        param.lsTop = marginLeftDp + thumbSizeDp + 10;

        // rs
        param.rsLeft = param.rightThumbLeft + param.leftThumbSize + 20;
        param.rsTop = param.rightThumbTop + param.rightThumbSize/2 - param.funcButtonHeight/2;


        return  param;
    }

}
