package com.tc.client.steam;

import android.util.Log;

import com.tc.client.Statistics;
import com.tc.client.events.OnRunningGames;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

import tc.TcMessage;


public class JavaWSClient {

    private static final String TAG = "Main";

    private final String mUrl;
    private WebSocketClient mWebSocketClient;

    public JavaWSClient(String ip, int port) {
        mUrl = "ws://" + ip + ":" + port + "/panel";
        Log.i(TAG, "====> WS URL:" + mUrl);
    }

    public void start() throws Exception {
        mWebSocketClient = new WebSocketClient(URI.create(mUrl)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "onOpen: " + serverHandshake.getHttpStatus());
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                processMessage(bytes);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i(TAG, "ws close: " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "Java WS Error: " + e.getMessage());
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

    private void processMessage(ByteBuffer message) {
        try {
            TcMessage.Message tcMsg = TcMessage.Message.parseFrom(message);
            if (tcMsg.getType() == TcMessage.MessageType.kOnlineGames) {
                List<TcMessage.OnlineGame> onlineGames = tcMsg.getOnlineGamesList();
                OnRunningGames runningGames = new OnRunningGames();
                runningGames.runningGames = onlineGames;
                EventBus.getDefault().post(runningGames);

            } else if (tcMsg.getType() == TcMessage.MessageType.kServerAudioSpectrum) {
                // !! deprecated !!
                TcMessage.ServerAudioSpectrum spectrum = tcMsg.getServerAudioSpectrum();
                int specCounts = spectrum.getLeftSpectrumCount();
                Log.i(TAG, "spectrumSize from JavaWS socket." + specCounts);
                Statistics.INSTANCE.updateSpectrum(spectrum.getLeftSpectrumList(), spectrum.getRightSpectrumList());
            }
        } catch (Exception e) {
            Log.e(TAG, "parse message failed." + e.getMessage());
        }
    }

}
