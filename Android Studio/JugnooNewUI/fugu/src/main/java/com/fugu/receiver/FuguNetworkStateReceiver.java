package com.fugu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.fugu.activity.FuguChannelsActivity;
import com.fugu.activity.FuguChannelsActivityNew;
import com.fugu.activity.FuguChatActivity;
import com.fugu.adapter.EventItem;
import com.fugu.adapter.HeaderItem;
import com.fugu.adapter.ListItem;
import com.fugu.agent.AgentChatActivity;
import com.fugu.agent.AgentConnectionManager;
import com.fugu.agent.AgentListActivity;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.model.Message;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.MultipartParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import faye.FayeClient;
import faye.FayeClientListener;
import faye.MetaMessage;
import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 23/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguNetworkStateReceiver extends BroadcastReceiver implements FuguAppConstant, FayeClientListener {

    private static final String TAG = FuguNetworkStateReceiver.class.getSimpleName();
    @NonNull
    private HashMap<Long, LinkedHashMap<String, JSONObject>> unsentMessageMap = new HashMap<>();

    private Long channelId;
    private HashMap<Long, LinkedHashMap<String, JSONObject>> allUnsentMessageMap = new HashMap<>();
    private LinkedHashMap<String, JSONObject> sendingMessages = new LinkedHashMap<>();

    private String tempDate = "";
    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";

    public static HashMap<Long, LinkedHashMap<String, ListItem>> UNSENT_MESSAGES = new HashMap<>();
    private LinkedHashMap<String, ListItem> sendingMessagesList = new LinkedHashMap<>();
    private LinkedHashMap<String, ListItem> sentMessages = new LinkedHashMap<>();

    // for agent
    public static HashMap<Long, LinkedHashMap<String, com.fugu.agent.model.ListItem>> AGENTUNSENT_MESSAGES = new HashMap<>();
    private LinkedHashMap<String, com.fugu.agent.model.ListItem> agentsendingMessagesList = new LinkedHashMap<>();
    private LinkedHashMap<String, com.fugu.agent.model.ListItem> agentsentMessages = new LinkedHashMap<>();

    // Initial Meta FuguMessage
    private static MetaMessage meta = new MetaMessage();
    private FayeClient mClient;
    private Context mContext;

    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        int status = NetworkUtil.getConnectivityStatusString(context);
        try {
            FuguChannelsActivity.changeStatus(status);
        } catch (Exception e) {

        }
//        try {
//            FuguChatActivity.changeStatus(status);
//        } catch (Exception e) {
//
//        }
//        try {
//            AgentListActivity.changeStatus(status);
//        } catch (Exception e) {
//
//        }
//        try {
//            AgentChatActivity.changeStatus(status);
//        } catch (Exception e) {
//
//        }


        try {
            FuguLog.d("app", "Network connectivity change");
            if (intent.getExtras() != null) {
                boolean isEnabled;
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    isEnabled = true;
                    if (!CommonData.getServerUrl().isEmpty()) {
                        if (mClient == null) {
                            mClient = new FayeClient(CommonData.getServerUrl() + "/faye", meta);
                        }
                        FuguLog.i("server url == ", CommonData.getServerUrl());

                        if (AgentCommonData.isAgentFlow()) {
                            if (AgentCommonData.getUnsentMessageMap() != null) {
                                unsentMessageMap = AgentCommonData.getUnsentMessageMap();
                                AGENTUNSENT_MESSAGES = AgentCommonData.getUnsentMessages();
                                setUpFayeConnection();
                            }
                        } else {
                            if (CommonData.getUnsentMessageMap() != null) {
                                unsentMessageMap = CommonData.getUnsentMessageMap();
                                UNSENT_MESSAGES = CommonData.getUnsentMessages();
                                setUpFayeConnection();
                            }
                        }
                    }
                    //AgentConnectionManager.getInstance().setNetworkStatus(true);
                } else {
                    isEnabled = false;
                }

                Intent mIntent = new Intent(NETWORK_STATE_INTENT);
                mIntent.putExtra("isConnected", isEnabled);
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

            }
        } catch (Exception e) {

        }
    }

    private void setUpFayeConnection() throws Exception {
        // Set FayeClient listener
        mClient.setNetworkListener(this);
        // Connect to server
        if (!mClient.isConnectedServer()) {
            mClient.connectServer();
        }
    }

    @Override
    public void onConnectedServer(FayeClient fc) {
        try {
            if(AgentCommonData.isAgentFlow()) {
                setAgentDateExpireDate();
            } else {
                setDateExpireDate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectedServer(FayeClient fc) {

    }

    @Override
    public void onReceivedMessage(FayeClient fc, String msg, String channel) {
        if (AgentCommonData.isAgentFlow()) {
            UserData userData = AgentCommonData.getUserData();
            if(userData != null && channel.substring(1).equals(userData.getUserChannel())) {
                return;
            }
            onAgentMessageReceived(msg, channel);
        } else {
            onMessageReceived(msg, channel);
        }

        /*Long channelid = null;
        channelid = Long.parseLong(channel.substring(1));
        try {
            final JSONObject messageJson = new JSONObject(msg);

            if ((messageJson.has("message") && !messageJson.getString("message").isEmpty()) ||
                    (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty())) {

                String muid = messageJson.getString("muid");

                Long currentChannelId = -1L;
                if (AgentCommonData.isAgentFlow()) {
                    currentChannelId = AgentChatActivity.currentChannelId;
                } else {
                    currentChannelId = FuguChatActivity.currentChannelId;
                }

                if (currentChannelId != channelid) {

                    if (channelid != channelId && messageJson.optInt("message_type") == 10) {
                        // TODO: 08/08/18 Update the particular channel
                        return;
                    }

                    ListItem listItem = sendingMessagesList.get(messageJson.getString("muid"));
                    ((EventItem) listItem).getEvent().setMessageStatus(MESSAGE_SENT);


                    String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                    String localDate = DateUtils.getInstance().convertToLocal(time, inputFormat, outputFormat);
                    if (!tempDate.equalsIgnoreCase(localDate)) {
                        sentMessages.put(localDate, new HeaderItem(localDate));
                        //fuguMessageList.add(new HeaderItem(localDate));
                    }


                    sentMessages.put(messageJson.getString("muid"), listItem);

                    sendingMessages.remove(muid);
                    if (AgentCommonData.isAgentFlow()) {
                        AGENTUNSENT_MESSAGES.remove(muid);
                    } else {
                        UNSENT_MESSAGES.remove(muid);
                    }
                    unsentMessageMap.remove(muid);
                    sendingMessages();
                } else {
                    if (sentMessages != null || sentMessages.size() > 0) {
                        if (AgentCommonData.isAgentFlow()) {
                            AgentCommonData.addExistingMessages(channelid, sentMessages);
                        } else {
                            CommonData.addExistingMessages(channelid, sentMessages);
                        }
                    }
                    allUnsentMessageMap.remove(channelid);
                    sendMessages();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void onMessageReceived(String msg, String channel) {
        Long channelid = null;
        channelid = Long.parseLong(channel.substring(1));
        try {
            final JSONObject messageJson = new JSONObject(msg);

            if ((messageJson.has("message") && !messageJson.getString("message").isEmpty()) ||
                    (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty())) {

                String muid = messageJson.getString("muid");

                if (FuguChatActivity.currentChannelId != channelid) {

                    if (channelid != channelId && messageJson.optInt("message_type") == 10) {
                        // TODO: 08/08/18 Update the particular channel
                        return;
                    }

                    ListItem listItem = sendingMessagesList.get(messageJson.getString("muid"));
                    ((EventItem) listItem).getEvent().setMessageStatus(MESSAGE_SENT);


                    String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                    String localDate = DateUtils.getInstance().convertToLocal(time, inputFormat, outputFormat);
                    if (!tempDate.equalsIgnoreCase(localDate)) {
                        sentMessages.put(localDate, new HeaderItem(localDate));
                    }


                    sentMessages.put(messageJson.getString("muid"), listItem);

                    sendingMessages.remove(muid);
                    UNSENT_MESSAGES.remove(muid);
                    unsentMessageMap.remove(muid);
                    sendingMessages();
                } else {
                    if (sentMessages != null || sentMessages.size() > 0) {
                        CommonData.addExistingMessages(channelid, sentMessages);
                    }
                    allUnsentMessageMap.remove(channelid);
                    sendMessages();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAgentMessageReceived(String msg, String channel) {
        Long channelid = null;
        channelid = Long.parseLong(channel.substring(1));
        try {
            final JSONObject messageJson = new JSONObject(msg);

            if ((messageJson.has("message") && !messageJson.getString("message").isEmpty()) ||
                    (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty())) {

                String muid = messageJson.getString("muid");
                if (AgentChatActivity.currentChannelId != channelid) {

                    if (channelid != channelId && messageJson.optInt("message_type") == 10) {
                        // TODO: 08/08/18 Update the particular channel
                        return;
                    }
                    com.fugu.agent.model.ListItem listItem = agentsendingMessagesList.get(messageJson.getString("muid"));
                    ((com.fugu.agent.model.EventItem) listItem).getEvent().setMessageStatus(MESSAGE_SENT);

                    String time = ((com.fugu.agent.model.EventItem) listItem).getEvent().getSentAtUtc();
                    String localDate = DateUtils.getInstance().convertToLocal(time, inputFormat, outputFormat);
                    if (!tempDate.equalsIgnoreCase(localDate)) {
                        agentsentMessages.put(localDate, new com.fugu.agent.model.HeaderItem(localDate));
                    }

                    agentsentMessages.put(messageJson.getString("muid"), listItem);
                    sendingMessages.remove(muid);
                    AGENTUNSENT_MESSAGES.remove(muid);
                    unsentMessageMap.remove(muid);
                    sendingAgentMessages();
                } else {
                    if (agentsentMessages != null || agentsentMessages.size() > 0) {
                        AgentCommonData.addExistingMessages(channelid, agentsentMessages);
                    }
                    allUnsentMessageMap.remove(channelid);
                    sendAgentMessages();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPongReceived() {

    }

    @Override
    public void onWebSocketError() {

    }

    private void setAgentDateExpireDate() throws Exception {
        Long currentChannelId = 0l;
        currentChannelId = AgentChatActivity.currentChannelId;

        for (Long channelId : unsentMessageMap.keySet()) {
            if (currentChannelId.compareTo(channelId) != 0) {
                LinkedHashMap<String, com.fugu.agent.model.ListItem> unsentMessage = AGENTUNSENT_MESSAGES.get(channelId);
                LinkedHashMap<String, JSONObject> unsentMessageObj = unsentMessageMap.get(channelId);
                if (unsentMessageObj != null && unsentMessageObj.size() == 0) {
                    AgentCommonData.removeUnsentMessageChannel(channelId);
                    AgentCommonData.removeUnsentMessageMapChannel(channelId);
                    continue;
                }
                LinkedHashMap<String, JSONObject> unsentMessageMapNew = new LinkedHashMap<>();
                for (String key : unsentMessage.keySet()) {
                    com.fugu.agent.model.ListItem listItem = unsentMessage.get(key);
                    String time = ((com.fugu.agent.model.EventItem) listItem).getEvent().getSentAtUtc();
                    int expireTimeCheck = ((com.fugu.agent.model.EventItem) listItem).getEvent().getIsMessageExpired();
                    if (expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                        ((com.fugu.agent.model.EventItem) listItem).getEvent().setIsMessageExpired(1);
                        try {
                            JSONObject messageJson = unsentMessageObj.get(key);
                            messageJson.put("is_message_expired", 1);
                            unsentMessageObj.put(key, messageJson);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else {
                        unsentMessageMapNew.put(key, unsentMessageObj.get(key));
                    }
                }

                allUnsentMessageMap.put(channelId, unsentMessageMapNew);

                if (AgentChatActivity.currentChannelId.compareTo(channelId) != 0) {
                    AGENTUNSENT_MESSAGES.put(channelId, unsentMessage);
                    unsentMessageMap.put(channelId, unsentMessageObj);
                    AgentCommonData.setUnsentMessageByChannel(channelId, unsentMessage);
                    AgentCommonData.setUnsentMessageMapByChannel(channelId, unsentMessageObj);
                }
            }
        }
        agentsentMessages.clear();
        sendAgentMessages();
    }

    private void setDateExpireDate() throws Exception {
        Long currentChannelId = 0l;
        if (AgentCommonData.isAgentFlow()) {
            currentChannelId = AgentChatActivity.currentChannelId;
        } else {
            currentChannelId = FuguChatActivity.currentChannelId;
        }

        for (Long channelId : unsentMessageMap.keySet()) {
            if (currentChannelId.compareTo(channelId) != 0) {
                LinkedHashMap<String, ListItem> unsentMessage = UNSENT_MESSAGES.get(channelId);
                LinkedHashMap<String, JSONObject> unsentMessageObj = unsentMessageMap.get(channelId);
                if (unsentMessageObj != null && unsentMessageObj.size() == 0) {
                    CommonData.removeUnsentMessageChannel(channelId);
                    CommonData.removeUnsentMessageMapChannel(channelId);
                    continue;
                }
                LinkedHashMap<String, JSONObject> unsentMessageMapNew = new LinkedHashMap<>();
                for (String key : unsentMessage.keySet()) {
                    ListItem listItem = unsentMessage.get(key);
                    Message message = ((EventItem) listItem).getEvent();
                    String time = message.getSentAtUtc();
                    int expireTimeCheck = message.getIsMessageExpired();

                    if (message.getMessageType() != IMAGE_MESSAGE && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                        message.setIsMessageExpired(1);
                        try {
                            JSONObject messageJson = unsentMessageMapNew.get(key);
                            if(messageJson != null) {
                                messageJson.put("is_message_expired", 1);
                                unsentMessageMapNew.put(key, messageJson);
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else if(message.getMessageType() == IMAGE_MESSAGE) {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        if(messageJson == null) {
                            message.setMessageStatus(MESSAGE_IMAGE_RETRY);
                        }
                    }
                    /*if (expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                        ((EventItem) listItem).getEvent().setIsMessageExpired(1);
                        try {
                            JSONObject messageJson = unsentMessageObj.get(key);
                            messageJson.put("is_message_expired", 1);
                            unsentMessageObj.put(key, messageJson);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else {
                        unsentMessageMapNew.put(key, unsentMessageObj.get(key));
                    }*/
                }

                allUnsentMessageMap.put(channelId, unsentMessageMapNew);

                if (FuguChatActivity.currentChannelId.compareTo(channelId) != 0) {
                    UNSENT_MESSAGES.put(channelId, unsentMessage);
                    unsentMessageMap.put(channelId, unsentMessageObj);
                    CommonData.setUnsentMessageByChannel(channelId, unsentMessage);
                    CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageObj);
                }
            }
        }
        sentMessages.clear();
        sendMessages();


    }

    private void sendAgentMessages() throws Exception {
        if (allUnsentMessageMap != null && allUnsentMessageMap.size() > 0) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (final Long key : allUnsentMessageMap.keySet()) {
                        channelId = key;
                        if (AgentChatActivity.currentChannelId.compareTo(key) != 0) {
                            //publishUnsentMessages(key, allUnsentMessageMap.get(key));
                            channelId = key;
                            if (AgentChatActivity.currentChannelId.compareTo(channelId) != 0)
                                mClient.subscribeChannel("/" + String.valueOf(key));

                            sendingMessages = allUnsentMessageMap.get(key);
                            agentsendingMessagesList = AGENTUNSENT_MESSAGES.get(key);

                            List<String> reverseOrderedKeys = new ArrayList<>(agentsentMessages.keySet());
                            Collections.reverse(reverseOrderedKeys);
                            for (String key1 : reverseOrderedKeys) {
                                if (agentsentMessages.get(key1) instanceof com.fugu.agent.model.HeaderItem) {
                                    tempDate = key1;
                                    break;
                                }
                            }

                            try {
                                sendingAgentMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            allUnsentMessageMap.remove(channelId);
                            try {
                                sendAgentMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }, 1000);
        } else {
            if (mClient != null)
                mClient.setNetworkListener(null);
        }
    }

    private void sendMessages() throws Exception {
        if (allUnsentMessageMap != null && allUnsentMessageMap.size() > 0) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (final Long key : allUnsentMessageMap.keySet()) {
                        channelId = key;
                        if (FuguChatActivity.currentChannelId.compareTo(key) != 0) {
                            //publishUnsentMessages(key, allUnsentMessageMap.get(key));
                            channelId = key;
                            if (FuguChatActivity.currentChannelId.compareTo(channelId) != 0)
                                mClient.subscribeChannel("/" + String.valueOf(key));

                            sendingMessages = allUnsentMessageMap.get(key);
                            sendingMessagesList = UNSENT_MESSAGES.get(key);

                            List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                            Collections.reverse(reverseOrderedKeys);
                            for (String key1 : reverseOrderedKeys) {
                                if (sentMessages.get(key1) instanceof HeaderItem) {
                                    tempDate = key1;
                                    break;
                                }
                            }


                            try {
                                sendingMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            allUnsentMessageMap.remove(channelId);
                            try {
                                sendMessages();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }, 1000);
        } else {
            if (mClient != null)
                mClient.setNetworkListener(null);
        }
    }

    private void sendingAgentMessages() throws Exception {
        if (sendingMessages.size() == 0) {
            allUnsentMessageMap.remove(channelId);
            mClient.unsubscribeChannel(String.valueOf(channelId));
            AgentCommonData.removeUnsentMessageMapChannel(channelId);
            AgentCommonData.removeUnsentMessageChannel(channelId);
            if (agentsentMessages != null && agentsentMessages.size() > 0) {
                AgentCommonData.addExistingMessages(channelId, agentsentMessages);
            }
            sendAgentMessages();
        } else {
            for (String key : sendingMessages.keySet()) {
                JSONObject messageJson = sendingMessages.get(key);
                if (messageJson.has("local_url") && !TextUtils.isEmpty(messageJson.optString("local_url", ""))) {
                    uploadFileServerCall(messageJson.optString("local_url", ""), "image/*", channelId, messageJson);
                    continue;
                } else {
                    FuguLog.e(TAG, "**************");
                    mClient.publish("/" + String.valueOf(channelId), messageJson);
                }
                break;
            }
        }

    }

    private void sendingMessages() throws Exception {
        if (sendingMessages.size() == 0) {
            allUnsentMessageMap.remove(channelId);
            mClient.unsubscribeChannel(String.valueOf(channelId));
            CommonData.removeUnsentMessageMapChannel(channelId);
            CommonData.removeUnsentMessageChannel(channelId);
            if (sentMessages != null && sentMessages.size() > 0) {
                CommonData.addExistingMessages(channelId, sentMessages);
            }
            sendMessages();
        } else {
            for (String key : sendingMessages.keySet()) {
                JSONObject messageJson = sendingMessages.get(key);
                if (messageJson.has("local_url") && !TextUtils.isEmpty(messageJson.optString("local_url", ""))) {
                    uploadFileServerCall(messageJson.optString("local_url", ""), "image/*", channelId, messageJson);
                    continue;
                } else if(messageJson.optInt("is_message_expired", 0) == 0) {
                    FuguLog.e(TAG, "**************");
                    mClient.publish("/" + String.valueOf(channelId), messageJson);
                }
                break;
            }
        }

    }

    private void uploadFileServerCall(String file, String fileType, final Long channelId, final JSONObject messageJson) throws Exception {

        MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
        MultipartParams multipartParams;

        if(AgentCommonData.isAgentFlow()) {
            UserData userData = AgentCommonData.getUserData();
            if(userData == null)
                return;

            multipartParams = multipartBuilder
                    .add(FuguAppConstant.ACCESS_TOKEN, userData.getAccessToken())
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .addFile("file", new File(file))
                    .add("file_type", fileType).build();

        } else {
            multipartParams = multipartBuilder
                    .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .addFile("file", new File(file))
                    .add("file_type", fileType).build();
        }

        FuguLog.v("map = ", multipartParams.getMap().toString());
        RestClient.getApiInterface()
                .uploadFile(multipartParams.getMap())
                .enqueue(new ResponseResolver<FuguUploadImageResponse>() {
                    @Override
                    public void success(FuguUploadImageResponse fuguUploadImageResponse) {

                        try {
                            messageJson.remove("image_path");
                            messageJson.put("image_url", fuguUploadImageResponse.getData().getUrl());
                            messageJson.put("thumbnail_url", fuguUploadImageResponse.getData().getThumbnailUrl());


                            mClient.publish("/" + String.valueOf(channelId), messageJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        if (unsentMessageMap != null) {
                            if (unsentMessageMap.get(channelId).size() == 1) {
                                unsentMessageMap.remove(channelId);
                                if (AgentCommonData.isAgentFlow()) {
                                    AgentCommonData.removeUnsentMessageMapChannel(channelId);
                                } else {
                                    CommonData.removeUnsentMessageMapChannel(channelId);
                                }

                            } else {
                                try {
                                    unsentMessageMap.get(channelId).remove(messageJson.getString("muid"));
                                    if (AgentCommonData.isAgentFlow()) {
                                        AgentCommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
                                    } else {
                                        CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }
}
