package com.fugu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguGetMessageResponse {

    public FuguGetMessageResponse() {
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data = new Data();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        public Data() {
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @SerializedName("messages")
        @Expose
        private ArrayList<Message> messages = new ArrayList<>();
        @SerializedName("label")
        @Expose
        private String label;

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

        public void setStatus(Integer status) {
            this.status = status;
        }

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("business_name")
        @Expose
        private String businessName;

        public boolean isDisableReply() {
            return disableReply != null && disableReply == 1;
        }

        public void setDisableReply(Integer disableReply) {
            this.disableReply = disableReply;
        }

        @SerializedName("disable_reply")
        @Expose
        private Integer disableReply;

        @SerializedName("chat_type")
        @Expose
        private Integer chatType;
        @SerializedName("agent_name")
        @Expose
        private String agentName;
        @SerializedName("user_id")
        @Expose
        private Long agentId;
        @SerializedName("other_users")
        @Expose
        private List<OtherUser> otherUsers = new ArrayList<OtherUser>();
        @SerializedName("allow_video_call")
        @Expose
        private Integer allowVideoCall;
        @SerializedName("allow_audio_call")
        @Expose
        private Integer allowAudioCall;

        public boolean isAllowVideoCall() {
            try {
                return allowVideoCall == 1;
            } catch (Exception e) {
                return false;
            }
        }

        public void setAllowVideoCall(Integer allowVideoCall) {
            this.allowVideoCall = allowVideoCall;
        }

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

        public Integer getChatType() {
            return chatType;
        }

        public void setChatType(Integer chatType) {
            this.chatType = chatType;
        }

        public List<OtherUser> getOtherUsers() {
            return otherUsers;
        }

        public void setOtherUsers(List<OtherUser> otherUsers) {
            this.otherUsers = otherUsers;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public Long getAgentId() {
            return agentId;
        }

        public void setAgentId(Long agentId) {
            this.agentId = agentId;
        }
    }

}