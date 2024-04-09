package com.tc.client.steam;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpBroadcastReceiver extends Thread {
    private static final String TAG = "Udp";

    private boolean running;
    private DatagramSocket socket;
    private byte[] buf = new byte[2048];
    private int port = 21034;

    public UdpBroadcastReceiver() {
        try {
            socket = new DatagramSocket(port);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                Log.i(TAG, "Broadcast message received: " + received);
            } catch (Exception e) {
                e.printStackTrace();
                running = false;
            }
        }
        socket.close();
    }

    public void stopReceiving() {
        running = false;
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
