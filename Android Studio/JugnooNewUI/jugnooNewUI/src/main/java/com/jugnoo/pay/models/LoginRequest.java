package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class LoginRequest {
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUnique_device_id() {
        return unique_device_id;
    }

    public void setUnique_device_id(String unique_device_id) {
        this.unique_device_id = unique_device_id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    @SerializedName("phone_no")
    @Expose
    private String phone_no;
    @SerializedName("password")
    @Expose
    private String  password;
    @SerializedName("latitude")
    @Expose
    private String  latitude;
    @SerializedName("longitude")
    @Expose
    private String  longitude;
    @SerializedName("unique_device_id")
    @Expose
    private String unique_device_id;
    @SerializedName("device_token")
    @Expose
    private String device_token;
}
