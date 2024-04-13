package com.tc.client.steam;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;


public class JavaWSClient {

    private static final String TAG = "Main";

    private final String mUrl;
    //private final ProtoMessageProcessor mMsgProcessor;
    WebSocketClient mWebSocketClient;

    public JavaWSClient(String ip, int port) {
        mUrl = "ws://" + ip + ":" + port;
        //mMsgProcessor = new ProtoMessageProcessor(renderer);
    }

    public void start() throws Exception {
        mWebSocketClient = new WebSocketClient(URI.create(mUrl)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "onOpen: ");
            }

            @Override
            public void onMessage(String message) {
                //Log.i(TAG, "recv message: " + message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                //Log.i(TAG, "onMessage: " + payload.length());
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

    public void sendMessage(String msg) {
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

    public boolean isOpen() {
        return mWebSocketClient != null && mWebSocketClient.isOpen();
    }
}
