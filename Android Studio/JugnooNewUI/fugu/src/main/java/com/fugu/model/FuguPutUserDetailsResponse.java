package com.fugu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPutUserDetailsResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

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

        @SerializedName("is_whitelabel")
        @Expose
        private Boolean isWhiteLabel;
        @SerializedName("en_user_id")
        @Expose
        private String en_user_id;
        @SerializedName("user_id")
        @Expose
        private Long userId;
        @SerializedName("user_unique_key")
        @Expose
        private String userUniqueKey;
        @SerializedName("business_id")
        @Expose
        private Integer businessId;
        @SerializedName("business_name")
        @Expose
        private String businessName;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("app_secret_key")
        @Expose
        private String appSecretKey;
        @SerializedName("conversations")
        @Expose
        private List<FuguConversation> fuguConversations = null;
        @SerializedName("grouping_tags")
        @Expose
        private List<GroupingTag> groupingTags = null;
        @SerializedName("in_app_support_panel_version")
        @Expose
        private Integer inAppSupportVersion;
        @SerializedName("is_faq_enabled")
        @Expose
        private Integer isFAQEnabled;
        @SerializedName("is_video_call_enabled")
        @Expose
        private Integer isVideoCallEnabled;
        @SerializedName("is_audio_call_enabled")
        @Expose
        private Integer isAudioCallEnabled;

        public boolean isAudioCallEnabled() {
            try {
                return isAudioCallEnabled == 1;
            } catch (Exception e) {
                return false;
            }
        }

        public void setisAudioCallEnabled(Integer isAudioCallEnabled) {
            this.isAudioCallEnabled = isAudioCallEnabled;
        }

        public boolean isVideoCallEnabled() {
            try {
                return isVideoCallEnabled == 1;
            } catch (Exception e) {
                return false;
            }
        }

        public void setIsVideoCallEnabled(Integer isVideoCallEnabled) {
            this.isVideoCallEnabled = isVideoCallEnabled;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEn_user_id() {
            return en_user_id;
        }

        public String getBusinessName() {
            if (businessName == null) {
                businessName = "";
            }
            return businessName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getAppSecretKey() {
            return appSecretKey;
        }

        public void setAppSecretKey(String appSecretKey) {
            this.appSecretKey = appSecretKey;
        }

        public List<FuguConversation> getFuguConversations() {
            return fuguConversations;
        }

        public Boolean getWhiteLabel() {
            if (isWhiteLabel == null) {
                isWhiteLabel = false;
            }
            return isWhiteLabel;
        }

        public void setWhiteLabel(Boolean whiteLabel) {
            isWhiteLabel = whiteLabel;
        }

        public List<GroupingTag> getGroupingTags() {
            return groupingTags;
        }

        public void setGroupingTags(List<GroupingTag> groupingTags) {
            this.groupingTags = groupingTags;
        }
        public Integer getInAppSupportVersion() {
            return inAppSupportVersion;
        }

        public void setInAppSupportVersion(Integer inAppSupportVersion) {
            this.inAppSupportVersion = inAppSupportVersion;
        }

        public boolean isFAQEnabled() {
            try {
                return isFAQEnabled == 1;
            } catch (Exception e) {
                return false;
            }
        }
    }
}