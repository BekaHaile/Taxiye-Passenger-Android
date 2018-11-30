package com.fugu.agent;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.fugu.FuguConfig;
import com.fugu.agent.listeners.AgentConnectionListener;
import com.fugu.agent.listeners.AgentManagerListener;
import com.fugu.agent.listeners.OnUserChannelListener;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.utils.FuguLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import faye.FayeAgentListener;
import faye.FayeClient;
import faye.MetaMessage;

import static com.fugu.constant.FuguAppConstant.AGENT_BETA_LIVE_SERVER;
import static com.fugu.constant.FuguAppConstant.AGENT_BETA_SERVER;
import static com.fugu.constant.FuguAppConstant.AGENT_LIVE_SERVER;
import static com.fugu.constant.FuguAppConstant.AGENT_TEST_SERVER;
import static com.fugu.constant.FuguAppConstant.NOTIFICATION_TYPE;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class AgentConnectionManager implements FayeAgentListener {
    private static final String TAG = AgentConnectionManager.class.getSimpleName();
    public static AgentConnectionManager instance;
    private UserData userData;
    private MetaMessage meta = new MetaMessage();
    private FayeClient mClient;
    private AgentConnectionListener connectionListener;

    private Handler handler = new Handler();
    private final static Integer RECONNECTION_TIME = 2500;
    private boolean networkStatus = true;

    public void setConnectionListener(AgentConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    private AgentConnectionManager() {
    }

    protected static AgentConnectionManager getInstance() {
        if (instance == null) {
            synchronized (AgentConnectionManager.class) {
                if (instance == null)
                    instance = new AgentConnectionManager();
            }
        }
        return instance;
    }

    public void onDestroy() {
        stopFayeClient();
        userData = null;
        firstTimeSkip = false;
    }

    public void stopFayeClient() {
        try {
            HandlerThread thread = new HandlerThread("TerminateThread");
            thread.start();
            new Handler(thread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    handler.removeCallbacks(runnable);
                    if (mClient != null && mClient.isConnectedServer()) {
                        mClient.disconnectServer();
                    }
                    mClient = null;

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public void setUpFayeConnection() {
        if (mClient == null) {

            meta = new MetaMessage();
            JSONObject jsonExt = new JSONObject();
            try {
                if (userData != null) {
                    jsonExt.put("user_id", userData.getUserId());
                    jsonExt.put("device_type", 1);
                    jsonExt.put("source", 2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            meta.setAllExt(jsonExt.toString());

            if (CommonData.getServerUrl().equals(AGENT_LIVE_SERVER)) {
                mClient = new FayeClient(CommonData.getServerUrl() + ":3002/faye", meta);
            } else if (CommonData.getServerUrl().equals(AGENT_TEST_SERVER) || CommonData.getServerUrl().equals(AGENT_BETA_LIVE_SERVER)) {
                mClient = new FayeClient("https://hippo-api-dev.fuguchat.com:3012/faye", meta);
            } else if(CommonData.getServerUrl().equals(AGENT_BETA_SERVER)) {
                mClient = new FayeClient("https://beta-hippo.fuguchat.com:3001/faye", meta);
            } else {
                mClient = new FayeClient(AGENT_LIVE_SERVER + ":3002/faye", meta);
            }
        }
        mClient.setAgentListener(this);
        mClient.connectServer();
    }

    boolean firstTimeSkip = false;
    @Override
    public void onConnectedServer(FayeClient fc) {
        FuguLog.v(TAG, "******************** in onConnectedServer ***********");
        handler.removeCallbacks(runnable);
        if(userData != null) {
            if(!TextUtils.isEmpty(userData.getUserChannel()))
                fc.subscribeChannel("/" + String.valueOf(userData.getUserChannel()));
            if(!TextUtils.isEmpty(userData.getAppSecretKey()))
                fc.subscribeChannel("/" + String.valueOf(userData.getAppSecretKey()) + "/agentsRefresh");
        }
        if(connectionListener != null)
            connectionListener.onConnectionStatus("Connected", 0);


        if(firstTimeSkip)
            for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                if (listener != null)
                    listener.onRefreshData();
            }
        firstTimeSkip = true;
    }

    @Override
    public void onDisconnectedServer(FayeClient fc) {
        fayeDisconnect = true;
        try {
            if (getNetworkStatus() && fayeConnecting)
                handler.postDelayed(runnable, RECONNECTION_TIME);
            if(connectionListener != null)
                connectionListener.onConnectionStatus("Server disconnected", 2);
        } catch (Exception e) {

        }
    }

    @Override
    public void onReceivedMessage(FayeClient fc, String msg, String channel) {
        try {
            FuguLog.v(TAG, "User channel Message: "+msg);
            JSONObject jsonObject = new JSONObject(msg);
            if(jsonObject.optInt(NOTIFICATION_TYPE) == FuguAppConstant.AGENT_TEXT_MESSAGE) {
                for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                    if (listener != null)
                        listener.onControlChannelData(jsonObject);
                    FuguLog.v(TAG, "msg = "+msg);
                }
            } else if (jsonObject.optInt(NOTIFICATION_TYPE) == FuguAppConstant.ASSIGN_CHAT) {
                for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                    if (listener != null)
                        listener.onAssignChat(jsonObject);
                }
            } else if (jsonObject.optInt(NOTIFICATION_TYPE) == FuguAppConstant.AGENT_STATUS_CHANGED || jsonObject.optInt(NOTIFICATION_TYPE) == FuguAppConstant.NEW_AGENT_ADDED) {
                // for get agent data
                onRefreshData();
            } else if(jsonObject.optInt(NOTIFICATION_TYPE) == FuguAppConstant.AGENT_REALALL) {
                /*for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                    if (listener != null)
                        listener.onReadAll(jsonObject);
                }*/
            }

            /*if (jsonObject.optInt("isTyping", TypingMode.TYPING_START.getOrdinal()) == TypingMode.TYPED.getOrdinal()) {
                for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                    if (listener != null)
                        listener.onControlChannelData(jsonObject);
                    FuguLog.v(TAG, "msg = "+msg);
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPongReceived() {
        if(connectionListener != null)
            connectionListener.onConnectionStatus("Connected", 0);
    }

    @Override
    public void onWebSocketError() {
        fayeDisconnect = true;
        try {
            if (getNetworkStatus() && fayeConnecting)
                handler.postDelayed(runnable, RECONNECTION_TIME);
            if(connectionListener != null)
                connectionListener.onConnectionStatus("Server disconnected", 2);
        } catch (Exception e) {

        }
    }

    public void onRefreshData() {
        for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
            if (listener != null)
                listener.onRefreshData();
        }
    }

    boolean fayeDisconnect = true;
    boolean fayeConnecting = false;

    private void connectAgainToServer() throws Exception {
        if(!fayeConnecting) {
            fayeConnecting = true;
            handler.removeCallbacks(runnable);
            mClient.setAgentListener(this);
            mClient.connectServer();
        }

        HandlerThread thread = new HandlerThread("FayeReconnect");
        thread.start();

        try {
            new Handler(thread.getLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    fayeConnecting = false;
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            fayeConnecting = false;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(fayeDisconnect) {
                    //btnRetry.setText("Server connecting... ");
                    connectAgainToServer();
                    if(connectionListener != null)
                        connectionListener.onConnectionStatus("Server connecting...", 1);
                }
            } catch (Exception e) {

            }
        }
    };


    public void setNetworkStatus(boolean networkStatus) {
        this.networkStatus = networkStatus;
    }

    public boolean getNetworkStatus() {
        return networkStatus;
    }
}
