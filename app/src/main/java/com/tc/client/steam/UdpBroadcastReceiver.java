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
    private byte[] buf = new byte[2048]; // 调整缓冲区大小根据需要
    private int port = 21034; // 定义监听的端口号

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
                socket.receive(packet); // 接收数据包

                // 处理接收到的数据
                String received = new String(packet.getData(), 0, packet.getLength());
                Log.i(TAG, "Broadcast message received: " + received);

                // 示例代码中简单地打印了接收到的消息，实际应用中可根据需要进行更复杂的处理
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
        socket.close();
    }

    // 添加一个停止接收的方法
    public void stopReceiving() {
        running = false;
        socket.close(); // 关闭socket会导致socket.receive()抛出异常从而退出循环
    }
}
