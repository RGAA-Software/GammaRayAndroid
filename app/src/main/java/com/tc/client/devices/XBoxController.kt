package com.tc.client.devices

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.os.SystemClock
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class XBoxController(private var context: Context,
                     private var usbDevice: UsbDevice,
                     private var usbConn: UsbDeviceConnection) {

    companion object {
        const val TAG = "USB"
    }

    protected var inEndpt: UsbEndpoint? = null
    protected var outEndpt:UsbEndpoint? = null
    private var stopped = false

    fun start() {
        // Force claim all interfaces

        // Force claim all interfaces
        for (i in 0 until usbDevice.getInterfaceCount()) {
            val iface: UsbInterface = usbDevice.getInterface(i)
            if (!usbConn.claimInterface(iface, true)) {
                Log.e(TAG, "Failed to claim interfaces")
                return
            }
        }

        // Find the endpoints
        val iface: UsbInterface = usbDevice.getInterface(0)
        for (i in 0 until iface.endpointCount) {
            val endpt = iface.getEndpoint(i)
            if (endpt.direction == UsbConstants.USB_DIR_IN) {
                if (inEndpt != null) {
                    Log.e(TAG, "Found duplicate IN endpoint")
                    return
                }
                inEndpt = endpt
            } else if (endpt.direction == UsbConstants.USB_DIR_OUT) {
                if (outEndpt != null) {
                    Log.e(TAG, "Found duplicate OUT endpoint")
                    return
                }
                outEndpt = endpt
            }
        }

        // Make sure the required endpoints were present
        if (inEndpt == null || outEndpt == null) {
            Log.e(TAG, "Missing required endpoint")
            return
        }

        createInputThread()?.start()

    }

    private fun createInputThread(): Thread? {
        return object : Thread() {
            override fun run() {
                try {
                    // Delay for a moment before reporting the new gamepad and
                    // accepting new input. This allows time for the old InputDevice
                    // to go away before we reclaim its spot. If the old device is still
                    // around when we call notifyDeviceAdded(), we won't be able to claim
                    // the controller number used by the original InputDevice.
                    sleep(1000)
                } catch (e: InterruptedException) {
                    return
                }

                // Report that we're added _before_ reporting input
                // notifyDeviceAdded()
                while (!isInterrupted && !stopped) {
                    val buffer = ByteArray(64)
                    var res: Int

                    //
                    // There's no way that I can tell to determine if a device has failed
                    // or if the timeout has simply expired. We'll check how long the transfer
                    // took to fail and assume the device failed if it happened before the timeout
                    // expired.
                    //
                    do {
                        // Read the next input state packet
                        val lastMillis = SystemClock.uptimeMillis()
                        res = usbConn.bulkTransfer(inEndpt, buffer, buffer.size, 3000)

                        // If we get a zero length response, treat it as an error
                        if (res == 0) {
                            res = -1
                        }
                        if (res == -1 && SystemClock.uptimeMillis() - lastMillis < 1000) {
                            Log.e(TAG, "Detected device I/O error")
                            //todo: stop()
                            break
                        }
                    } while (res == -1 && !isInterrupted && !stopped)
                    if (res == -1 || stopped) {
                        break
                    }
                    // todo:
                    if (handleRead(ByteBuffer.wrap(buffer, 0, res).order(ByteOrder.LITTLE_ENDIAN))) {
                        // Report input if handleRead() returns true
                        // todo: reportInput()
                    }
                }
            }
        }
    }

    protected fun handleRead(buffer: ByteBuffer): Boolean {
//        if (buffer.remaining() < 14) {
//            Log.e(TAG, "Read too small: " + buffer.remaining())
//            return false
//        }

        // Skip first short

        // Skip first short
//        buffer.position(buffer.position() + 2)
        buffer.position(0)
        val array = buffer.array()
        array.forEachIndexed { index, byte ->
            Log.i(TAG, "$index -> $byte")
        }
        Log.i(TAG, "-----")

        // DPAD
        //val b = buffer.get()
//        Log.i(TAG, "b1: ${buffer.get().toInt()}")
//        Log.i(TAG, "b2: ${buffer.get().toInt()}")
//        Log.i(TAG, "b3: ${buffer.get().toInt()}")
//        setButtonFlag(ControllerKeys.LEFT_FLAG, b.toInt() and 0x04)
//        setButtonFlag(ControllerKeys.RIGHT_FLAG, b.toInt() and 0x08)
//        setButtonFlag(ControllerKeys.UP_FLAG, b.toInt() and 0x01)
//        setButtonFlag(ControllerKeys.DOWN_FLAG, b.toInt() and 0x02)
        return true
    }

}