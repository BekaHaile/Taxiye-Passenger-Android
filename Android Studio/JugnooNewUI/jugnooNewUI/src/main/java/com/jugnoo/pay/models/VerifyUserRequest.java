package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-38 on 9/20/16.
 */
public class VerifyUserRequest {

    @SerializedName("user_email")
    @Expose
    private String user_email;
    @SerializedName("device_token")
    @Expose
    private String device_token;
    @SerializedName("unique_device_id")
    @Expose
    private String unique_device_id;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("user_name")
    @Expose
    private String user_name;
    @SerializedName("latitude")
    @Expose
    private String latitude="0";
    @SerializedName("longitude")
    @Expose
    private String longitude="0";
    @SerializedName("phone_no")
    @Expose
    private String phone_no;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("vpa")
    @Expose
    private String vpa;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("autos_user_id")
    @Expose
    private String autos_user_id;
    @SerializedName("access_token")
    @Expose
    private String access_token;

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

    public String getAutos_user_id() {
        return autos_user_id;
    }

    public void setAutos_user_id(String autos_user_id) {
        this.autos_user_id = autos_user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
