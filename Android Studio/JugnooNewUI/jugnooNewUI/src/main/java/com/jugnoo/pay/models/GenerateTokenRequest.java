package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenerateTokenRequest {
    @SerializedName("user_email")
    @Expose
    private String user_email;
    @SerializedName("device_token")
    @Expose
    private String device_token;
    @SerializedName("unique_device_id")
    @Expose
    private String unique_device_id;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("phone_number")
    @Expose
    private String phone_number;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     *
     * @return
     * The userEmail
     */
    public String getUserEmail() {
        return user_email;
    }

    /**
     *
     * @param userEmail
     * The user_email
     */
    public void setUserEmail(String userEmail) {
        this.user_email = userEmail;
    }

    /**
     *
     * @return
     * The deviceToken
     */
    public String getDeviceToken() {
        return device_token;
    }

    /**
     *
     * @param deviceToken
     * The device_token
     */
    public void setDeviceToken(String deviceToken) {
        this.device_token = deviceToken;
    }

    /**
     *
     * @return
     * The uniqueDeviceId
     */
    public String getUniqueDeviceId() {
        return unique_device_id;
    }

    /**
     *
     * @param uniqueDeviceId
     * The unique_device_id
     */
    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.unique_device_id = uniqueDeviceId;
    }

    /**
     *
     * @return
     * The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}