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
        int marginBorder = 5;
        int thumbSizeDp = 160;

        ControlLayerParam param = new ControlLayerParam();
        param.leftThumbLeft = marginBorder;
        param.leftThumbTop = marginBorder;
        param.leftThumbSize = thumbSizeDp;

        // right
        int marginRightDp = 110;
        int marginBottomDp = marginBorder;
        param.rightThumbLeft = screenWidthDp - thumbSizeDp - marginRightDp;
        param.rightThumbTop = screenHeightDp - thumbSizeDp - marginBottomDp;
        param.rightThumbSize = thumbSizeDp;

        param.dpadButtonSize = 60;

        int abxyMarginRightDp = marginBorder;
        param.buttonGroupSize = 160;
        param.abxyGroupLeft = screenWidthDp - param.buttonGroupSize - abxyMarginRightDp;
        param.abxyGroupTop = abxyMarginRightDp;

        param.dpadGroupLeft = marginRightDp;
        param.dpadGroupTop = (int) (screenHeightDp - param.buttonGroupSize - marginBottomDp);

        // function button size
        param.funcButtonWidth = 56;
        param.funcButtonHeight = 56;

        // ls
        param.lsLeft = marginBorder + thumbSizeDp + marginBorder;
        param.lsTop = marginBorder;

        // rs
        param.rsLeft = param.rightThumbLeft + param.leftThumbSize + 20;
        param.rsTop = param.rightThumbTop + param.rightThumbSize/2 - param.funcButtonHeight;

        param.backStartWidth = 80;
        param.backStartHeight = 45;

        // start
        param.startLeft = screenWidthDp - param.backStartWidth - marginBorder;
        param.startTop = screenHeightDp - param.backStartHeight - marginBorder;

        // back
        param.backLeft = marginBorder;
        param.backTop = param.startTop;

        // xbox
        param.xboxWidth = 45;
        param.xboxHeight = 45;
        param.xboxLeft = screenWidthDp/2 - param.xboxWidth/2;
        param.xboxTop = screenHeightDp - param.xboxHeight - marginBorder;

        // lt
        param.ltLeft = marginBorder;
        param.ltTop = marginBorder + thumbSizeDp + 10;

        // lb
        param.lbLeft = marginBorder + param.dpadButtonSize + 20;
        param.lbTop = param.ltTop;

        // rt
        param.rtLeft = param.rightThumbLeft;
        param.rtTop = param.rightThumbTop - param.dpadButtonSize - 10;

        // rb
        param.rbLeft = param.rightThumbLeft + param.dpadButtonSize + 20;
        param.rbTop = param.rightThumbTop - param.dpadButtonSize - 10;

        return  param;
    }

}
