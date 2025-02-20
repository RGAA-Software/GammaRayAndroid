package com.tc.client.render;

import android.util.Log;
import android.util.Pair;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Map;

public class Dpad {
    private static final String TAG = "Dpad";

    final static int UP       = 0;
    final static int LEFT     = 1;
    final static int RIGHT    = 2;
    final static int DOWN     = 3;
    final static int CENTER   = 4;

    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;

    public boolean getDirectionPressed(InputEvent event) {
        if (!isDpadDevice(event)) {
            return false;
        }

        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {
            // Use the hat axis value to find the D-pad direction
            MotionEvent motionEvent = (MotionEvent) event;
            float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

            leftPressed = false;
            rightPressed = false;
            if (Float.compare(xaxis, -1.0f) == 0) {
                leftPressed = true;
                return true;
            } else if (Float.compare(xaxis, 1.0f) == 0) {
                rightPressed = true;
                return true;
            }

            upPressed = false;
            downPressed = false;
            if (Float.compare(yaxis, -1.0f) == 0) {
                upPressed = true;
                return true;
            } else if (Float.compare(yaxis, 1.0f) == 0) {
                downPressed = true;
                return true;
            }
        } else if (event instanceof KeyEvent) {
            Log.i(TAG, "getDirectionPressed KeyEvent: " + ((KeyEvent)event).getAction());
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {

            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {

            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {

            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {

            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {

            }
        }

        return false;
    }

    public static boolean isDpadDevice(InputEvent event) {
        // Check that input comes from a device with directional pads.
        if ((event.getSource() & InputDevice.SOURCE_DPAD)
                != InputDevice.SOURCE_DPAD) {
            return true;
        } else {
            return false;
        }
    }
}