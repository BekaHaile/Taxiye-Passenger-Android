package com.fugu.agent.model.LoginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class UserData {

    @SerializedName("user_properties")
    @Expose
    private UserProperties userProperties;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("user_channel")
    @Expose
    private String userChannel;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("channel_filter")
    @Expose
    private List<ChannelFilter> channelFilter = null;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("agent_type")
    @Expose
    private Integer agentType;
    @SerializedName("online_status")
    @Expose
    private String onlineStatus;
    @SerializedName("business_property")
    @Expose
    private BusinessProperty businessProperty;
    @SerializedName("billing_plan")
    @Expose
    private Boolean billingPlan;
    @SerializedName("is_boarding_complete")
    @Expose
    private Integer isBoardingComplete;
    @SerializedName("is_boarding_skip")
    @Expose
    private Integer isBoardingSkip;
    @SerializedName("version")
    @Expose
    private Version version;
    @SerializedName("terms_and_condition")
    @Expose
    private Integer termsAndCondition;
    @SerializedName("agent_onboarding_completed")
    @Expose
    private Integer agentOnboardingComplete;
    @SerializedName("is_video_call_enabled")
    @Expose
    private Integer isVideoCallEnabled;
    @SerializedName("is_audio_call_enabled")
    @Expose
    private Integer isAudioCallEnabled;

    public UserProperties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<ChannelFilter> getChannelFilter() {
        return channelFilter;
    }

    public void setChannelFilter(List<ChannelFilter> channelFilter) {
        this.channelFilter = channelFilter;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public BusinessProperty getBusinessProperty() {
        return businessProperty;
    }

    public void setBusinessProperty(BusinessProperty businessProperty) {
        this.businessProperty = businessProperty;
    }

    public Boolean getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(Boolean billingPlan) {
        this.billingPlan = billingPlan;
    }

    public Integer getIsBoardingComplete() {
        return isBoardingComplete;
    }

    public void setIsBoardingComplete(Integer isBoardingComplete) {
        this.isBoardingComplete = isBoardingComplete;
    }

    public Integer getIsBoardingSkip() {
        return isBoardingSkip;
    }

    public void setIsBoardingSkip(Integer isBoardingSkip) {
        this.isBoardingSkip = isBoardingSkip;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Integer getTermsAndCondition() {
        return termsAndCondition;
    }

    public void setTermsAndCondition(Integer termsAndCondition) {
        this.termsAndCondition = termsAndCondition;
    }

    public Integer getAgentOnboardingComplete() {
        return agentOnboardingComplete;
    }

    public void setAgentOnboardingComplete(Integer agentOnboardingComplete) {
        this.agentOnboardingComplete = agentOnboardingComplete;
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

}
