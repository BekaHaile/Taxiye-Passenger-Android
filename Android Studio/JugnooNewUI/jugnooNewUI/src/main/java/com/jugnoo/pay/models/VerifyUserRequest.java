package com.jugnoo.pay.models;

/**
 * Created by cl-macmini-38 on 9/20/16.
 */
public class VerifyUserRequest {

    private String user_email;
    private String device_token;
    private String unique_device_id;
    private String password;
    private String user_name;
    private String latitude="0";
    private String longitude="0",phone_no,token;
    private String vpa;
    private String device_type;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public String getDeviceType() {
        return device_type;
    }

    public void setDeviceType(String device_type) {
        this.device_type = device_type;
    }
}
