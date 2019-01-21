package com.fugu.agent.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class Message {

    @SerializedName("full_name")
    @Expose
    private String fromName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    private ArrayList<Integer> taggedUsers = new ArrayList<>();

    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    @SerializedName("user_type")
    @Expose
    private Integer userType;

    @SerializedName("date_time")
    @Expose
    private String sentAtUtc = "";
    @SerializedName("message")
    @Expose
    private String message;

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;
    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    private int timeIndex = 0;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    @SerializedName("message_type")
    @Expose
    private Integer messageType;

    private int messageIndex = 0;
    private boolean isSelf;

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getfromName() {
        return fromName;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSentAtUtc() {
        return sentAtUtc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @SerializedName("is_rating_given")
    @Expose
    private int isRatingGiven;
    @SerializedName("total_rating")
    @Expose
    private int totalRating;
    @SerializedName("rating_given")
    @Expose
    private int ratingGiven;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("line_before_feedback")
    @Expose
    private String lineBeforeFeedback;
    @SerializedName("line_after_feedback_1")
    @Expose
    private String lineAfterFeedback_1;
    @SerializedName("line_after_feedback_2")
    @Expose
    private String lineAfterFeedback_2;

    @SerializedName("values")
    @Expose
    private ArrayList<String> values;
    @SerializedName("content_value")
    @Expose
    private List<ContentValue> contentValue = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("video_call_duration")
    @Expose
    private int videoCallDuration;
    @SerializedName("message_state")
    @Expose
    private Integer messageState;

    @SerializedName("call_type")
    @Expose
    private String callType;

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


    private int isMessageExpired = 0;
    private String localImagePath;

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public int getIsMessageExpired() {
        return isMessageExpired;
    }

    public void setIsMessageExpired(int isMessageExpired) {
        this.isMessageExpired = isMessageExpired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<ContentValue> getContentValue() {
        return contentValue;
    }

    public void setContentValue(List<ContentValue> contentValue) {
        this.contentValue = contentValue;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public int getRatingGiven() {
        return ratingGiven;
    }

    public void setRatingGiven(int ratingGiven) {
        this.ratingGiven = ratingGiven;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public void setIsRatingGiven(int isRatingGiven) {
        this.isRatingGiven = isRatingGiven;
    }

    public int getIsRatingGiven() {
        return isRatingGiven;
    }

    public boolean isRatingGiven() {
        return isRatingGiven == 1;
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

    public int getVideoCallDuration() {
        return videoCallDuration;
    }

    public void setVideoCallDuration(int videoCallDuration) {
        this.videoCallDuration = videoCallDuration;
    }

    public Integer getMessageState() {
        return messageState;
    }

    public void setMessageState(Integer messageState) {
        this.messageState = messageState;
    }

    public void setTaggedUsers(ArrayList<Integer> taggedUsers) {
        this.taggedUsers = taggedUsers;
    }

    public Message() {
    }

    public Message(String fromName, Integer userId, String sentAtUtc, String message,
                   Integer messageStatus, String imageUrl, String thumbnailUrl, Integer messageType, Integer userType) {
        this.fromName = fromName;
        this.userId = userId;
        this.sentAtUtc = sentAtUtc;
        this.message = message;
        this.messageStatus = messageStatus;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.userType = userType;
    }


    public Message(String fromName, Integer userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, Integer messageType, Integer userType) {
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.messageType = messageType;
        this.userType = userType;
    }

    public Message(String fromName, Integer userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, String imageUrl, String thumbnailUrl,
                   Integer messageType, Integer userType) {

        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.userType = userType;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public boolean isSelf() {
        return isSelf;
    }

//    public boolean isRating() {
//        return messageType == FEEDBACK_MESSAGE;
//    }

    public Integer getUserType() {
        return userType;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }


     class ContentValue {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("bot_id")
        @Expose
        private String bot_id;
        @SerializedName("data_type")
        @Expose
        private ArrayList<String> data_type;
        @SerializedName("questions")
        @Expose
        private ArrayList<String> questions;
        @SerializedName("params")
        @Expose
        private ArrayList<String> params;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBot_id() {
            return bot_id;
        }

        public void setBot_id(String bot_id) {
            this.bot_id = bot_id;
        }

        public ArrayList<String> getData_type() {
            return data_type;
        }

        public void setData_type(ArrayList<String> data_type) {
            this.data_type = data_type;
        }

        public ArrayList<String> getQuestions() {
            return questions;
        }

        public void setQuestions(ArrayList<String> questions) {
            this.questions = questions;
        }

        public ArrayList<String> getParams() {
            return params;
        }

        public void setParams(ArrayList<String> params) {
            this.params = params;
        }

    }

}
