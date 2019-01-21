package com.fugu.agent.model.getConversationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 04/07/18.
 *
 * @author gurmail
 */

public class CustomerUniqueKey {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
}
