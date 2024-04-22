package com.tc.client;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

// ONLY FOR TEST
public class TestMediaClient {

    private static final String TAG = "Main";

    private final String mUrl;
    private WebSocketClient mWebSocketClient;
    private long lastMessageTime;

    public TestMediaClient(String ip, int port) {
        mUrl = "ws://" + ip + ":" + port + "/media";
        Log.i(TAG, "url: " + mUrl);
    }

    public void start() throws Exception {

        mWebSocketClient = new WebSocketClient(URI.create(mUrl)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "onOpen: ");
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                long currentTime = System.currentTimeMillis();
                if (lastMessageTime == 0) {
                    lastMessageTime = currentTime;
                }
                long diff = currentTime - lastMessageTime;
                lastMessageTime = currentTime;
                Log.i(TAG, "onMessage: " + diff);
            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i(TAG, "onClose: ");
            }
            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError: ");
            }
        };
        mWebSocketClient.connect();

    }

    public void sendMessage(byte[] msg, boolean binary) {
        if (mWebSocketClient == null || !mWebSocketClient.isOpen()) {
            return;
        }
        try {
            mWebSocketClient.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Send message via network error.");
        }
    }

    public void stop() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }
}