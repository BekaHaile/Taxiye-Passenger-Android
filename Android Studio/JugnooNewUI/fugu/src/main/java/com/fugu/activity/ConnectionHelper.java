package com.fugu.activity;

import android.os.Handler;
import android.os.Looper;

import com.fugu.FuguConfig;
import com.fugu.interfaces.ConnectionResponderListener;
import com.fugu.interfaces.HelperConnectionListener;

import org.json.JSONObject;

import faye.FayeClient;
import faye.FayeClientListener;

/**
 * Created by gurmail on 24/08/18.
 *
 * @author gurmail
 */

public class ConnectionHelper implements FayeClientListener, HelperConnectionListener {
    private static final String TAG = ConnectionHelper.class.getSimpleName();
    public static ConnectionHelper instance;
    public FayeClient fayeClient = FuguConfig.getClient();
    private static final long TIME_DURATION = 10 * 1000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ConnectionResponderListener responderListener;

    protected ConnectionHelper(ConnectionResponderListener responderListener) {
        this.responderListener = responderListener;
    }

    @Override
    public void connect() {
        if (!fayeClient.isFayeConnected()) {
            fayeClient.connectServer();
        } else {
            responderListener.connectionEstablished();
        }
    }

    @Override
    public void connectionCheck(CheckListener listener) {
        listener.status(fayeClient.isConnectedServer());
    }

    public void fayeReconnect() {
        handler.postDelayed(runnable, TIME_DURATION);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            responderListener.fayeStatus(1);
            connect();
        }
    };

    @Override
    public void onConnectedServer(FayeClient fc) {
        responderListener.connectionEstablished();
        responderListener.fayeStatus(2);
    }

    @Override
    public void onDisconnectedServer(FayeClient fc) {
        responderListener.fayeStatus(0);
    }

    @Override
    public void onReceivedMessage(FayeClient fc, String msg, String channel) {
        responderListener.onMessageReceived(msg);
    }

    @Override
    public void onPongReceived() {
        responderListener.onPongReceived();
    }

    @Override
    public void onWebSocketError() {
        responderListener.fayeStatus(0);
    }

    @Override
    public void subscribeChannel(String subscribe) {
        if (fayeClient.isConnectedServer()) {
            fayeClient.subscribeChannel(subscribe);
        }
    }

    @Override
    public void unsubscribe(String subscribe) {
        fayeClient.unsubscribeChannel(subscribe);
    }

    @Override
    public void unsubscribeAll() {
        fayeClient.unsubscribeAll();
    }

    @Override
    public void publish(String channel, JSONObject data) {
        if (fayeClient.isConnectedServer()) {
            fayeClient.publish(channel, data);
        } else {
            fayeReconnect();
        }
    }

    @Override
    public void disconnect() {
        handler.removeCallbacks(runnable);
    }

    private synchronized void sendMessages() {
        if(fayeClient.isConnectedServer()) {

        } else {
            fayeReconnect();
        }
    }
}
