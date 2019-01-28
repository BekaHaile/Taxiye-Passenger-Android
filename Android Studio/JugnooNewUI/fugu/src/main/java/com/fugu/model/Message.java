package com.fugu.model;

import com.fugu.constant.FuguAppConstant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.fugu.constant.FuguAppConstant.FEEDBACK_MESSAGE;

/**
 * Created by Bhavya Rattan on 12/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class Message {
    @SerializedName("full_name")
    @Expose
    private String fromName;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("date_time")
    @Expose
    private String sentAtUtc = "";
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;
    @SerializedName("message_state")
    @Expose
    private Integer messageState;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("image_url")
    @Expose
    private String url = "";
    @SerializedName("message_type")
    @Expose
    private int messageType = FuguAppConstant.TEXT_MESSAGE;
    @SerializedName("custom_action")
    private CustomAction customAction;
    @SerializedName("file_name")
    @Expose
    String fileName = "";
    @SerializedName("file_size")
    @Expose
    String fileSize = "";
    @SerializedName("file_extension")
    @Expose
    String fileExtension = "";
    @SerializedName("file_path")
    @Expose
    String filePath = "";

    private int messageIndex = 0;
    private int timeIndex = 0;
    private boolean isSelf;
    private String localImagePath;

    @SerializedName("is_rating_given")
    @Expose
    int isRatingGiven;
    @SerializedName("total_rating")
    @Expose
    int totalRating;
    @SerializedName("rating_given")
    @Expose
    int ratingGiven;
    @SerializedName("muid")
    @Expose
    String muid;
    @SerializedName("comment")
    @Expose
    String comment;
    @SerializedName("line_before_feedback")
    @Expose
    String lineBeforeFeedback;
    @SerializedName("line_after_feedback_1")
    @Expose
    String lineAfterFeedback_1;
    @SerializedName("line_after_feedback_2")
    @Expose
    String lineAfterFeedback_2;
    private Long message_id;
    @SerializedName("values")
    @Expose
    private ArrayList<String> values;
    @SerializedName("content_value")
    @Expose
    private List<ContentValue> contentValue = new ArrayList<>();
    @SerializedName("default_action_id")
    @Expose
    private String defaultActionId;
    @SerializedName("video_call_duration")
    @Expose
    private int videoCallDuration;

    public void setCallType(String callType) {
        this.callType = callType;
    }

    @SerializedName("call_type")
    @Expose
    private String callType;

    public String getCallType() {
        return callType;
    }
    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void setCustomAction(final CustomAction customAction) {
        this.customAction = customAction;
    }

    public CustomAction getCustomAction() {
        return customAction;
    }

    public Long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }

    private Long downloadId;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getfromName() {
        return fromName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public int getMessageType() {
        return messageType;
    }

    public Long getId() {
        if (id == null) {
            id = -1l;
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message() {
    }


    public Message(String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, int messageType, String muid) {
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.messageType = messageType;
        this.muid = muid;
    }

    public Message(long id, String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, String url, String thumbnailUrl, int messageType,
                   String muid) {
        this.id = id;
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.url = url;
        this.muid = muid;
    }

    public Message(String message) {
        this.message = message;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public boolean isRating() {
        return messageType == FEEDBACK_MESSAGE;
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

    public void setMessageId(Long message_id) {
        this.message_id = message_id;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public List<ContentValue> getContentValue() {
        return contentValue;
    }

    public void setContentValue(List<ContentValue> contentValue) {
        this.contentValue = contentValue;
    }

    public String getDefaultActionId() {
        return defaultActionId;
    }

    public void setDefaultActionId(String defaultActionId) {
        this.defaultActionId = defaultActionId;
    }

    public int getIsMessageExpired() {
        return isMessageExpired;
    }

    public void setIsMessageExpired(int isMessageExpired) {
        this.isMessageExpired = isMessageExpired;
    }

    private int isMessageExpired = 0;

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
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

}
