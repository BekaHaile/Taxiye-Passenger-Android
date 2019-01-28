package faye;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fugu.FuguConfig;
import com.fugu.utils.FuguLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */


public class WebSocket extends WebSocketClient {

    private static final String LOG_TAG = WebSocket.class.getSimpleName();

    public static final int ON_OPEN = 1;
    public static final int ON_CLOSE = 2;
    public static final int ON_MESSAGE = 3;
    public static final int ON_ERROR = 4;
    public static final int ON_PONG = 5;

    private Handler messageHandler;

    public WebSocket(URI serverUri, Handler handler) {
        super(serverUri);
        messageHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        messageHandler.sendMessage(Message.obtain(messageHandler, ON_OPEN));
    }

    @Override
    public void onMessage(String s) {
        messageHandler.sendMessage(Message.obtain(messageHandler, ON_MESSAGE, s));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        FuguLog.i(LOG_TAG, "code: " + code + ", reason: "
                + reason + ", remote: " + remote);
        messageHandler.sendMessage(
                Message.obtain(messageHandler, ON_CLOSE));
    }

    @Override
    public void onError(Exception e) {
        FuguLog.e(LOG_TAG, "On WebSocket Error:" + e);
        try {
            Looper.prepare();
            final FayeClient mClient = FuguConfig.getClient();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FuguLog.e(LOG_TAG, "On WebSocket Connecting:");
                    mClient.connectServer();
                }
            },500);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void setConnectionLostTimeout(int connectionLostTimeout) {
        super.setConnectionLostTimeout(connectionLostTimeout);
        FuguLog.i(LOG_TAG, "connectionLostTimeout: " + connectionLostTimeout);
    }

    @Override
    protected void startConnectionLostTimer() {
        super.startConnectionLostTimer();
        FuguLog.e(LOG_TAG, "On startConnectionLostTimer");
    }


    @Override
    public boolean reconnectBlocking() throws InterruptedException {
        return super.reconnectBlocking();

    }

    @Override
    public void onWebsocketPong(org.java_websocket.WebSocket conn, Framedata f) {
        super.onWebsocketPong(conn, f);
        messageHandler.sendMessage(Message.obtain(messageHandler, ON_PONG, f));
        FuguLog.e(LOG_TAG, "On onWebsocketPong");
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(org.java_websocket.WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {
        super.onWebsocketHandshakeReceivedAsClient(conn, request, response);
        FuguLog.e(LOG_TAG, "On onWebsocketHandshakeReceivedAsClient");
    }
}
