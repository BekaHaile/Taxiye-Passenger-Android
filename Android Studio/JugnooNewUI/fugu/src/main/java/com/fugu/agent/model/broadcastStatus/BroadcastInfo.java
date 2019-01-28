package com.fugu.agent.model.broadcastStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 30/07/18.
 *
 * @author gurmail
 */
public class BroadcastInfo {

    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("broadcast_id")
    @Expose
    private Integer broadcastId;
    @SerializedName("agent_id")
    @Expose
    private Integer agentId;
    @SerializedName("broadcast_title")
    @Expose
    private String broadcastTitle;
    @SerializedName("user_first_message")
    @Expose
    private String userFirstMessage;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(Integer broadcastId) {
        this.broadcastId = broadcastId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getBroadcastTitle() {
        return broadcastTitle;
    }

    public void setBroadcastTitle(String broadcastTitle) {
        this.broadcastTitle = broadcastTitle;
    }

    public String getUserFirstMessage() {
        return userFirstMessage;
    }

    public void setUserFirstMessage(String userFirstMessage) {
        this.userFirstMessage = userFirstMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
