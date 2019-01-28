
package com.fugu.agent.model.createConversation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("label")
    @Expose
    private String label;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
