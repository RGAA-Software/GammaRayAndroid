package com.tc.client.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tc.client.Settings;
import com.tc.client.impl.ThunderApp;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import static com.tc.client.render.GamepadButtons.*;

public class FrameRenderView extends GLSurfaceView {

    private static final String TAG = "FrameRenderView";

    private ThunderApp mThunderApp;
    private FrameRender mRender;
    private final Dpad mDpad = new Dpad();
    private final XInputGamepad mXInputGamepad = new XInputGamepad();

    private boolean buttonAPressed = false;
    private boolean buttonBPressed = false;
    private boolean buttonXPressed = false;
    private boolean buttonYPressed = false;
    private boolean buttonStartPressed = false;
    private boolean buttonSelectPressed = false;
    private boolean buttonLBPressed = false;
    private boolean buttonRBPressed = false;
    private boolean buttonLThumbPressed = false;
    private boolean buttonRThumbPressed = false;

    private float leftThumbX = 0.0f;
    private float leftThumbY = 0.0f;
    private float rightThumbX = 0.0f;
    private float rightThumbY = 0.0f;

    private float leftTrigger = 0.0f;
    private float rightTrigger = 0.0f;

    public FrameRenderView(Context context) {
        this(context, null);
    }
    public FrameRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(ThunderApp app) {
        setEGLContextClientVersion(3);
        setEGLConfigChooser((egl, display) -> {
            int[] attributes = {
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 24,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                //EGL10.EGL_SAMPLES, 4,
                EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attributes, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                return null;
            } else {
                return configs[0];
            }
        });

        mThunderApp = app;
        mRender = new FrameRender(getContext(), mThunderApp);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public ThunderApp getThunderApp() {
        return mThunderApp;
    }

    public void onCreate() {
        mRender.onCreate();
    }

    public void onResume() {
        mRender.onResume();
    }

    public void onPause() {
        mRender.onPause();
    }

    public void onDestroy() {
        mRender.onDestroy();
    }

    public void onEventTick() {
        //mRender.onRenderTick();
        sendGamepadState();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check if this event if from a D-pad and process accordingly.
        if (Dpad.isDpadDevice(event)) {
            boolean processedDirection = mDpad.getDirectionPressed(event);
            if (processedDirection) {
                return true;
            }
        }

        // Check if this event is from a joystick movement and process accordingly.
        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();
            Log.i(TAG, "History joystick size: " + historySize );

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {
        InputDevice inputDevice = event.getDevice();
        if (inputDevice == null) {
            return;
        }
        float lx = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos);
        float ly = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos);
        leftThumbX = lx;
        leftThumbY = ly;

        float rx = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos);
        float ry = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos);
        rightThumbX = rx;
        rightThumbY = ry;

        float lt = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float rt = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        leftTrigger = lt;
        rightTrigger = rt;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = true;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            Log.i(TAG, "repeatCount: " + event.getRepeatCount() + ", action: " + event.getAction() + ", code: " + event.getKeyCode());
            if (event.getRepeatCount() == 0) {
                if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                    buttonAPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                    buttonBPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                    buttonXPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                    buttonYPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                    buttonStartPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_SELECT) {
                    buttonSelectPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
                    buttonLBPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
                    buttonRBPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                    buttonLThumbPressed = true;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                    buttonRThumbPressed = true;
                } else {
                    handled = false;
                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean handled = true;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            Log.i(TAG, "repeatCount: " + event.getRepeatCount() + ", action: " + event.getAction());
            if (event.getRepeatCount() == 0) {
                if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                    buttonAPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                    buttonBPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                    buttonXPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                    buttonYPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                    buttonStartPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_SELECT) {
                    buttonSelectPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
                    buttonLBPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
                    buttonRBPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                    buttonLThumbPressed = false;
                } else if (keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                    buttonRThumbPressed = false;
                } else {
                    handled = false;
                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    private void sendGamepadState() {
        if (mThunderApp == null) {
            return;
        }
        mXInputGamepad.wButtons = 0;

        if (mDpad.leftPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_DPAD_LEFT;
        }
        if (mDpad.rightPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_DPAD_RIGHT;
        }
        if (mDpad.upPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_DPAD_UP;
        }
        if (mDpad.downPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_DPAD_DOWN;
        }

        if (buttonAPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_A;
        }
        if (buttonBPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_B;
        }
        if (buttonXPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_X;
        }
        if (buttonYPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_Y;
        }

        if (buttonStartPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_START;
        }
        if (buttonSelectPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_BACK;
        }

        if (buttonLBPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_LEFT_SHOULDER;
        }
        if (buttonRBPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_RIGHT_SHOULDER;
        }

        if (buttonLThumbPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_LEFT_THUMB;
        }
        if (buttonRThumbPressed) {
            mXInputGamepad.wButtons |= GP_XINPUT_GAMEPAD_RIGHT_THUMB;
        }

        int maxThumbValue = 32767;
        mXInputGamepad.sThumbLX = (int) (leftThumbX * maxThumbValue);
        mXInputGamepad.sThumbLY = (int) (leftThumbY * maxThumbValue);
        mXInputGamepad.sThumbRX = (int) (rightThumbX * maxThumbValue);
        mXInputGamepad.sThumbRY = (int) (rightThumbY * maxThumbValue);

        if (Settings.Companion.getInstance().getInvertJoystickYAxis()) {
            mXInputGamepad.sThumbLY *= -1;
            mXInputGamepad.sThumbRY *= -1;
        }

        int maxTriggerValue = 255;
        mXInputGamepad.bLeftTrigger = (int) (leftTrigger * maxTriggerValue);
        mXInputGamepad.bRightTrigger = (int) (rightTrigger * maxTriggerValue);

        mThunderApp.sendGamepadState(mXInputGamepad.wButtons,
                mXInputGamepad.bLeftTrigger,
                mXInputGamepad.bRightTrigger,
                mXInputGamepad.sThumbLX,
                mXInputGamepad.sThumbLY,
                mXInputGamepad.sThumbRX,
                mXInputGamepad.sThumbRY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_CANCEL) {

            int targetAction = action;
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                targetAction = MotionEvent.ACTION_UP;
            }

            mThunderApp.sendMouseEvent(targetAction, event.getX()/this.getWidth(), event.getY()/this.getHeight());
            if (action == MotionEvent.ACTION_DOWN) {
                // Move the cursor to current position
                mThunderApp.sendMouseEvent(MotionEvent.ACTION_MOVE, event.getX()/this.getWidth(), event.getY()/this.getHeight());
            }
        }
        return super.onTouchEvent(event);
    }
}
