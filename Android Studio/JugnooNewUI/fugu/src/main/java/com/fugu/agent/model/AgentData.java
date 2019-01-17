package com.fugu.agent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */
public class AgentData {

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @SerializedName("messages")
    @Expose
    private ArrayList<Message> messages = new ArrayList<>();

    @SerializedName("tags")
    @Expose
    private ArrayList<TagData> tags = new ArrayList<>();
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("customer_phone")
    @Expose
    private String customerPhone;
    @SerializedName("customer_email")
    @Expose
    private String customerEmail;
    @SerializedName("customer_address")
    @Expose
    private String customerAddress;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("agent_name")
    @Expose
    private String agentName;
    @SerializedName("is_feedback_ask")
    @Expose
    private int isFeedbackAsk;

    @SerializedName("is_customer_email_present")
    @Expose
    private int isCustomerEmailPresent;

    public boolean isDisableReply() {
        return disableReply != null && disableReply == 1;
    }

    public void setDisableReply(Integer disableReply) {
        this.disableReply = disableReply;
    }

    @SerializedName("disable_reply")
    @Expose
    private Integer disableReply;


    public int getIsFeedbackAsk() {
        return isFeedbackAsk;
    }

    public void setIsFeedbackAsk(int isFeedbackAsk) {
        this.isFeedbackAsk = isFeedbackAsk;
    }

    public boolean isFeedbackAskPending() {
        return isFeedbackAsk == 0;
    }

    public int getIsCustomerEmailPresent() {
        return isCustomerEmailPresent;
    }

    public void setIsCustomerEmailPresent(int isCustomerEmailPresent) {
        this.isCustomerEmailPresent = isCustomerEmailPresent;
    }

    public boolean isCustomerEmailPresent() {
        return isCustomerEmailPresent == 1;
    }

    public ArrayList<TagData> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagData> tags) {
        this.tags = tags;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setOnSubscribe(int onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(Integer channelStatus) {
        this.channelStatus = channelStatus;
    }

    @SerializedName("channel_status")
    @Expose
    private Integer channelStatus;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("on_subscribe")
    @Expose
    private int onSubscribe;
    @SerializedName("page_size")
    @Expose
    private int pageSize;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("user_id")
    @Expose
    private int userId;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @SerializedName("owner_id")
    @Expose
    private int ownerId;

    public void setStatus(Integer status) {
        this.status = status;
    }

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("business_name")
    @Expose
    private String businessName;

    @SerializedName("line_before_feedback")
    @Expose
    String lineBeforeFeedback;
    @SerializedName("line_after_feedback_1")
    @Expose
    String lineAfterFeedback_1;
    @SerializedName("line_after_feedback_2")
    @Expose
    String lineAfterFeedback_2;
    @SerializedName("allow_video_call")
    @Expose
    private Integer allowVideoCall;
    @SerializedName("allow_audio_call")
    @Expose
    private Integer allowAudioCall;

    public boolean isAllowAudioCall() {
        try {
            return allowAudioCall == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAllowAudioCall(Integer allowAudioCall) {
        this.allowAudioCall = allowAudioCall;
    }

    public boolean isAllowVideoCall() {
        try {
            return allowVideoCall == 1;
        } catch (Exception e) {
            return false;
        }
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public String getLabel() {
        return label;
    }

    public String getFullName() {
        return fullName;
    }

    public int getOnSubscribe() {
        return onSubscribe;
    }

    public int getPageSize() {
        return pageSize;
    }


    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getStatus() {
        return status;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getLineBeforeFeedback() {
        return lineBeforeFeedback;
    }

    public void setLineBeforeFeedback(String lineBeforeFeedback) {
        this.lineBeforeFeedback = lineBeforeFeedback;
    }

    public String getLineAfterFeedback_1() {
        return lineAfterFeedback_1;
    }

    public void setLineAfterFeedback_1(String lineAfterFeedback_1) {
        this.lineAfterFeedback_1 = lineAfterFeedback_1;
    }

    public String getLineAfterFeedback_2() {
        return lineAfterFeedback_2;
    }

    public void setLineAfterFeedback_2(String lineAfterFeedback_2) {
        this.lineAfterFeedback_2 = lineAfterFeedback_2;
    }

}
