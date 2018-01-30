package com.fugu.model;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by bhavya on 22/08/17.
 */

public class FuguCreateConversationParams {

    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("label_id")
    @Expose
    private Long labelId = -1l;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("other_user_unique_key")
    @Expose
    private JsonArray otherUserUniqueKeys;
    @SerializedName("chat_type")
    @Expose
    private int chatType = 0;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
    @SerializedName("tags")
    @Expose
    private JsonArray tags = null;
    @SerializedName("user_first_messages")
    @Expose
    private String[] user_first_messages = null;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private String appVersion = BuildConfig.VERSION_NAME;

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, String userUniqueKey, JsonArray otherUserUniqueKeys,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.otherUserUniqueKeys = otherUserUniqueKeys;
        this.deviceType = 1;
        this.enUserId=enUserId;
        this.appVersion = BuildConfig.VERSION_NAME;
        if (otherUserUniqueKeys != null) {
            if (otherUserUniqueKeys.size() > 1) {
                this.chatType = 2;
            } else {
                this.chatType = 1;
            }
        } else {
            this.chatType = 0;
        }

        this.channelName = channelName;
        this.tags = tags;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        this.tags = tags;
        this.enUserId=enUserId;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId, String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.enUserId = enUserId;
    }

    @Override
    public String toString() {
        return appSecretKey + ", " + labelId + ", " + userId + ", " + chatType;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags, String[] user_first_messages,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        this.tags = tags;
        this.enUserId=enUserId;
        this.user_first_messages = user_first_messages;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
