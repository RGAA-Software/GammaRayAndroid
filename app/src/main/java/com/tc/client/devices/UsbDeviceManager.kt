package com.tc.client.devices

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

class UsbDeviceManager(var context: Context) {

    companion object {
        const val TAG = "USB"
        const val ACTION_USB_PERM = "action.usb.permission"
    }

    private var usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var receiver = UsbEventReceiver(this)
    private lateinit var controller: XBoxController
    private var usbDeviceConn: UsbDeviceConnection? = null
    private var usbDevice: UsbDevice? = null

    class UsbEventReceiver(private var usbDeviceManager: UsbDeviceManager) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action;
            Log.i(TAG, "action: $action")
            if (TextUtils.equals(action, UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                usbDeviceManager.usbDevice = intent?.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                usbDeviceManager.handleUsbDeviceState(usbDeviceManager.usbDevice!!)
            } else if (action == UsbDeviceManager.ACTION_USB_PERM) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)

                // Permission dialog is now closed
                //if (stateListener != null) {
                //    stateListener.onUsbPermissionPromptCompleted()
                //}

                // If we got this far, we've already found we're able to handle this device
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    usbDeviceManager.handleUsbDeviceState(device!!)
                }
            }
        }
    }

    fun start() {

        // Register for USB attach broadcasts and permission completions
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter.addAction(UsbDeviceManager.ACTION_USB_PERM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        // Enumerate existing devices

        // Enumerate existing devices
        for (dev in usbManager.deviceList.values) {
            Log.i(TAG, "dev: " + dev.deviceName + " " + dev.manufacturerName)
            handleUsbDeviceState(dev!!)
        }
    }

    private fun canWeProcessTheDevice(device: UsbDevice): Boolean {
        return true;
    }

    private fun handleUsbDeviceState(device: UsbDevice) {
        // Are we able to operate it?
        if (canWeProcessTheDevice(device)) {
            // Do we have permission yet?
            if (!usbManager.hasPermission(device)) {
                // Let's ask for permission
                try {
                    // Tell the state listener that we're about to display a permission dialog
//                    if (stateListener != null) {
//                        stateListener.onUsbPermissionPromptStarting()
//                    }
                    var intentFlags = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // This PendingIntent must be mutable to allow the framework to populate EXTRA_DEVICE and EXTRA_PERMISSION_GRANTED.
                        intentFlags = intentFlags or PendingIntent.FLAG_MUTABLE
                    }

                    // This function is not documented as throwing any exceptions (denying access
                    // is indicated by calling the PendingIntent with a false result). However,
                    // Samsung Knox has some policies which block this request, but rather than
                    // just returning a false result or returning 0 enumerated devices,
                    // they throw an undocumented SecurityException from this call, crashing
                    // the whole app. :(

                    // Use an explicit intent to activate our unexported broadcast receiver, as required on Android 14+
                    val intent = Intent(UsbDeviceManager.ACTION_USB_PERM)
                    intent.setPackage(context.packageName)
                    usbManager.requestPermission(device, PendingIntent.getBroadcast(context, 100, intent, intentFlags))
                } catch (e: SecurityException) {
                    Toast.makeText(
                        context,
                        "USB exception: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            // Open the device
            val connection: UsbDeviceConnection = usbManager.openDevice(device)
            if (connection == null) {
                Log.i(TAG, "connect to usb device failed.")
                return
            }
            Log.i(TAG, "connect to usb device success.")
            usbDevice = device
            controller = XBoxController(context, usbDevice!!, connection)
            controller.start()

//            val controller: AbstractController
//            if (XboxOneController.canClaimDevice(device)) {
//                controller = XboxOneController(device, connection, nextDeviceId++, this)
//            } else if (Xbox360Controller.canClaimDevice(device)) {
//                controller = Xbox360Controller(device, connection, nextDeviceId++, this)
//            } else if (Xbox360WirelessDongle.canClaimDevice(device)) {
//                controller = Xbox360WirelessDongle(device, connection, nextDeviceId++, this)
//            } else {
//                // Unreachable
//                return
//            }

            // Start the controller
//            if (!controller.start()) {
//                connection.close()
//                return
//            }

            // Add this controller to the list
            // controllers.add(controller)
        }
    }

}