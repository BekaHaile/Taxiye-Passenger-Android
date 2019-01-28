package com.fugu.agent.model.getConversationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 20/06/18.
 *
 * @author gurmail
 */
public class Conversation {

    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("bot_channel_name")
    @Expose
    private String botChannelName;
    @SerializedName("message_id")
    @Expose
    private Integer messageId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("agent_id")
    @Expose
    private Integer agentId;
    @SerializedName("agent_name")
    @Expose
    private String agentName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("unread_count")
    @Expose
    private Integer unreadCount = 0;
    @SerializedName("last_sent_by_id")
    @Expose
    private Integer last_sent_by_id = 0;
    @SerializedName("last_sent_by_full_name")
    @Expose
    private String last_sent_by_full_name = "";
    @SerializedName("default_message")
    @Expose
    private String default_message = "";
    @SerializedName("last_message_status")
    @Expose
    private Integer last_message_status = 2;
    @SerializedName("last_sent_by_user_type")
    @Expose
    private Integer last_sent_by_user_type = 0;
    @SerializedName("last_updated_at")
    @Expose
    private String lastUpdatedAt;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("customer_unique_keys")
    @Expose
    private List<CustomerUniqueKey> customerUniqueKeys;
    @SerializedName("disable_reply")
    @Expose
    private Integer disableReply;
    @SerializedName("message_state")
    @Expose
    private Integer messageState;
    @SerializedName("message_type")
    @Expose
    private int message_type;
    @SerializedName("call_type")
    @Expose
    private String callType;

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


    public Integer getMessageState() {
        return messageState;
    }
    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public void setMessageState(Integer messageState) {
        this.messageState = messageState;
    }

    public boolean isDisableReply() {
        return disableReply != null && disableReply == 1;
    }

    public void setDisableReply(Integer disableReply) {
        this.disableReply = disableReply;
    }

    private Integer overlay;
    private int isTimeSet = 0;

    public String getUserUniqueKeys() {
        return userUniqueKeys;
    }

    public void setUserUniqueKeys(String userUniqueKeys) {
        this.userUniqueKeys = userUniqueKeys;
    }

    private String userUniqueKeys;

    public String getDefault_message() {
        return default_message;
    }

    public void setDefault_message(String default_message) {
        this.default_message = default_message;
    }

    public Integer getLast_message_status() {
        return last_message_status;
    }

    public void setLast_message_status(Integer last_message_status) {
        this.last_message_status = last_message_status;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBotChannelName() {
        return botChannelName;
    }

    public void setBotChannelName(String botChannelName) {
        this.botChannelName = botChannelName;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getUnreadCount() {
        if (unreadCount == null)
            unreadCount = 0;
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getDateTime() {
        return dateTime;
        // return createdAt;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getOverlay() {
        return overlay;
    }

    public void setOverlay(Integer overlay) {
        this.overlay = overlay;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Integer getLast_sent_by_id() {
        return last_sent_by_id;
    }

    public void setLast_sent_by_id(Integer last_sent_by_id) {
        this.last_sent_by_id = last_sent_by_id;
    }

    public String getLast_sent_by_full_name() {
        return last_sent_by_full_name;
    }

    public void setLast_sent_by_full_name(String last_sent_by_full_name) {
        this.last_sent_by_full_name = last_sent_by_full_name;
    }

    public Integer getLast_sent_by_user_type() {
        return last_sent_by_user_type;
    }

    public void setLast_sent_by_user_type(Integer last_sent_by_user_type) {
        this.last_sent_by_user_type = last_sent_by_user_type;
    }

    public int getIsTimeSet() {
        return isTimeSet;
    }

    public void setIsTimeSet(int isTimeSet) {
        this.isTimeSet = isTimeSet;
    }

    public List<CustomerUniqueKey> getCustomerUniqueKeys() {
        return customerUniqueKeys;
    }

    public void setCustomerUniqueKeys(List<CustomerUniqueKey> customerUniqueKeys) {
        this.customerUniqueKeys = customerUniqueKeys;
    }
}
