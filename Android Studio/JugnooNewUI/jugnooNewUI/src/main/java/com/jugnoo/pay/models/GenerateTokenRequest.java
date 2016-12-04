package com.jugnoo.pay.models;

public class GenerateTokenRequest {

    private String user_email;
    private String device_token;
    private String unique_device_id;
    private String latitude;
    private String longitude;
    private String phone_number;
    private String device_type;

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

    public String getDeviceType() {
        return device_type;
    }

    public void setDeviceType(String device_type) {
        this.device_type = device_type;
    }
}