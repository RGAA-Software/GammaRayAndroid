package com.tc.client;

import static com.tc.client.render.GamepadButtons.*;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tc.client.impl.ThunderApp;
import com.tc.client.widgets.DPadButton;
import com.tc.client.widgets.FunctionButton;
import com.tc.client.widgets.RockerView;

public class ControlLayer extends FrameLayout {
    private static final String TAG = "Controller";

    private boolean mInit;
    private DPadButton mDpadX;
    private DPadButton mDpadY;
    private DPadButton mDpadA;
    private DPadButton mDpadB;
    private DPadButton mDpadLeft;
    private DPadButton mDpadUp;
    private DPadButton mDpadDown;
    private DPadButton mDpadRight;
    private RockerView mLeftThumb;
    private RockerView mRightThumb;
    private DpadButtonGroup mAbxyButtonGroup;
    private DpadButtonGroup mDpadButtonGroup;
    private FunctionButton mLs;
    private FunctionButton mRs;
    private FunctionButton mStart;
    private FunctionButton mBack;
    private FunctionButton mXbox;
    private FunctionButton mLT;
    private FunctionButton mLB;
    private FunctionButton mRT;
    private FunctionButton mRB;

    private ThunderApp mThunderApp;

    private ControlLayerParamLoader mParamLoader;
    private ControlLayerParam mControlParam;

    public ControlLayer(@NonNull Context context) {
        this(context, null);
    }

    public ControlLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mParamLoader = new ControlLayerParamLoader(context);
        mControlParam = mParamLoader.getDefaultLayerParam();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        mDpadX = findViewById(R.id.id_dpad_x);
        mDpadY = findViewById(R.id.id_dpad_y);
        mDpadA = findViewById(R.id.id_dpad_a);
        mDpadB = findViewById(R.id.id_dpad_b);

        mDpadLeft = findViewById(R.id.id_dpad_left);
        mDpadUp = findViewById(R.id.id_dpad_up);
        mDpadDown = findViewById(R.id.id_dpad_down);
        mDpadRight = findViewById(R.id.id_dpad_right);

        mDpadX.setText("X");
        mDpadY.setText("Y");
        mDpadA.setText("A");
        mDpadB.setText("B");

        mLeftThumb = findViewById(R.id.id_rocker_left);
        mRightThumb = findViewById(R.id.id_rocker_right);

        mAbxyButtonGroup = findViewById(R.id.id_abxy_parent);
        mDpadButtonGroup = findViewById(R.id.id_dpad_parent);

        mLs = findViewById(R.id.id_ls);
        mRs = findViewById(R.id.id_rs);
        mLs.setText("LS");
        mRs.setText("RS");

        mStart = findViewById(R.id.id_start);
        mBack = findViewById(R.id.id_back);
        mStart.setText("START");
        mBack.setText("BACK");

        mXbox = findViewById(R.id.id_xbox);

        mLT = findViewById(R.id.id_lt);
        mLB = findViewById(R.id.id_lb);
        mLT.setText("LT");
        mLB.setText("LB");

        mRT = findViewById(R.id.id_rt);
        mRB = findViewById(R.id.id_rb);
        mRT.setText("RT");
        mRB.setText("RB");

        mInit = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float density = Resources.getSystem().getDisplayMetrics().density;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // left thumb
        //mLeftThumb.getLayoutParams().width = mControlParam.leftThumbSize * density;
        //mLeftThumb.getLayoutParams().height = mControlParam.leftThumbSize * density;
        mLeftThumb.setSize((int) (mControlParam.leftThumbSize*density));

        // right thumb
        //mRightThumb.getLayoutParams().width = mControlParam.rightThumbSize * density;
        //mRightThumb.getLayoutParams().height = mControlParam.rightThumbSize * density;
        mRightThumb.setSize((int) (mControlParam.rightThumbSize*density));

        // dpad size
        int dpadGroupSize = (int) (mControlParam.buttonGroupSize * density);
        setViewSize(mAbxyButtonGroup, dpadGroupSize, dpadGroupSize);
        setViewSize(mDpadButtonGroup, dpadGroupSize, dpadGroupSize);

        int dpadButtonSize = (int) (mControlParam.dpadButtonSize * density);
        setViewSize(mDpadA, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadB, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadX, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadY, dpadButtonSize, dpadButtonSize);

        setViewSize(mDpadLeft, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadUp, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadDown, dpadButtonSize, dpadButtonSize);
        setViewSize(mDpadRight, dpadButtonSize, dpadButtonSize);

        float funcButtonWidth = mControlParam.funcButtonWidth * density;
        float funcButtonHeight = mControlParam.funcButtonHeight * density;
        setViewSize(mLs, funcButtonWidth, funcButtonHeight);
        setViewSize(mRs, funcButtonWidth, funcButtonHeight);

        float backStartWidth = mControlParam.backStartWidth * density;
        float backStartHeight = mControlParam.backStartHeight * density;
        setViewSize(mStart, backStartWidth, backStartHeight);
        setViewSize(mBack, backStartWidth, backStartHeight);

        setViewSize(mXbox, mControlParam.xboxWidth*density, mControlParam.xboxHeight*density);

        setViewSize(mLT, dpadButtonSize, dpadButtonSize);
        setViewSize(mLB, dpadButtonSize, dpadButtonSize);

        setViewSize(mRT, dpadButtonSize, dpadButtonSize);
        setViewSize(mRB, dpadButtonSize, dpadButtonSize);

        Log.i(TAG, "func button width: " + funcButtonWidth + ", func button height: " + funcButtonHeight);

        setMeasuredDimension(screenWidth, screenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        float density = Resources.getSystem().getDisplayMetrics().density;

        setTranslation(mLeftThumb, mControlParam.leftThumbLeft * density, mControlParam.leftThumbTop * density);
        setTranslation(mRightThumb, mControlParam.rightThumbLeft * density, mControlParam.rightThumbTop * density);
        setTranslation(mAbxyButtonGroup, mControlParam.abxyGroupLeft * density, mControlParam.abxyGroupTop * density);
        setTranslation(mDpadButtonGroup, mControlParam.dpadGroupLeft * density, mControlParam.dpadGroupTop * density);
        setTranslation(mLs, mControlParam.lsLeft*density, mControlParam.lsTop*density);
        setTranslation(mRs, mControlParam.rsLeft*density, mControlParam.rsTop*density);
        setTranslation(mStart, mControlParam.startLeft*density, mControlParam.startTop*density);
        setTranslation(mBack, mControlParam.backLeft* density, mControlParam.backTop*density);
        setTranslation(mXbox, mControlParam.xboxLeft*density, mControlParam.xboxTop*density);
        setTranslation(mLB, mControlParam.ltLeft*density, mControlParam.ltTop*density);
        setTranslation(mLT, mControlParam.lbLeft*density, mControlParam.lbTop*density);
        setTranslation(mRT, mControlParam.rtLeft*density, mControlParam.rtTop*density);
        setTranslation(mRB, mControlParam.rbLeft*density, mControlParam.rbTop*density);
    }

    private void setViewSize(View view, float width, float height) {
        if (view == null || view.getLayoutParams() == null) {
            Log.e(TAG, "view is null...");
            return;
        }
        view.getLayoutParams().width = (int) width;
        view.getLayoutParams().height = (int) height;
    }

    private void setTranslation(View view, float x, float y) {
        if (view == null) {
            Log.e(TAG, "view is null when set translation");
            return;
        }
        view.setTranslationX(x);
        view.setTranslationY(y);
    }

    public void setThunderApp(ThunderApp app) {
        mThunderApp = app;
    }

    public void onEventTick() {
        if (!mInit) {
            return;
        }
        int leftThumbX = mLeftThumb.getCurrentThumbX();
        int leftThumbY = mLeftThumb.getCurrentThumbY();
        int rightThumbX = mRightThumb.getCurrentThumbX();
        int rightThumbY = mRightThumb.getCurrentThumbY();
        int buttons = 0;

        if (mDpadX.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_X;
        }
        if (mDpadY.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_Y;
        }
        if (mDpadA.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_A;
        }
        if (mDpadB.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_B;
        }

        if (mDpadLeft.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_DPAD_LEFT;
        }
        if (mDpadRight.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_DPAD_RIGHT;
        }
        if (mDpadUp.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_DPAD_UP;
        }
        if (mDpadDown.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_DPAD_DOWN;
        }

        if (mLs.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_LEFT_THUMB;
        }
        if (mRs.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_RIGHT_THUMB;
        }

        if (mStart.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_START;
        }
        if (mBack.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_BACK;
        }

        if (mXbox.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAT_XBOX;
        }

        if (mLB.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_LEFT_SHOULDER;
        }
        if (mRB.isButtonPressed()) {
            buttons |= GP_XINPUT_GAMEPAD_RIGHT_SHOULDER;
        }

        int leftTrigger = 0;
        if (mLT.isButtonPressed()) {
            leftTrigger = 255;
        }
        int rightTrigger = 0;
        if (mRT.isButtonPressed()) {
            rightTrigger = 255;
        }
        //Log.i(TAG, "x: " + mDpadX.isButtonPressed() + ", y: " + mDpadY.isButtonPressed() + ", a: " + mDpadA.isButtonPressed() + ", b: " + mDpadB.isButtonPressed());
        mThunderApp.sendGamepadState(buttons, leftTrigger, rightTrigger, leftThumbX, leftThumbY, rightThumbX, rightThumbY);
    }
}
