package com.jugnoo.pay.models;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class LoginRequest {
   private String phone_no;

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

    public String getDeviceType() {
        return device_type;
    }

    public void setDeviceType(String device_type) {
        this.device_type = device_type;
    }

    private String  password;
    private String  latitude;
    private String  longitude;
    private String unique_device_id;
    private String device_token;
    private String device_type;
}
