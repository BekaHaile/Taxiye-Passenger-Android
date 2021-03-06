package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class AccessTokenRequest {
    @SerializedName("access_token")
    @Expose
    private String  access_token;
    @SerializedName("device_token")
    @Expose
    private String device_token;
    @SerializedName("is_pending")
    @Expose
    private int is_pending;
    @SerializedName("order_id")
    @Expose
    private int order_id;
    @SerializedName("updated_user_name")
    @Expose
    private String updated_user_name;
    @SerializedName("updated_user_email")
    @Expose
    private String updated_user_email;
    @SerializedName("old_password")
    @Expose
    private String old_password;
    @SerializedName("new_password")
    @Expose
    private String new_password;
    @SerializedName("unique_device_id")
    @Expose
    private String unique_device_id;

    public String getUpdated_user_name() {
        return updated_user_name;
    }

    public void setUpdated_user_name(String updated_user_name) {
        this.updated_user_name = updated_user_name;
    }

    public String getUpdated_user_email() {
        return updated_user_email;
    }

    public void setUpdated_user_email(String updated_user_email) {
        this.updated_user_email = updated_user_email;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getIs_pending() {
        return is_pending;
    }

    public void setIs_pending(int is_pending) {
        this.is_pending = is_pending;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUnique_device_id() {
        return unique_device_id;
    }

    public void setUnique_device_id(String unique_device_id) {
        this.unique_device_id = unique_device_id;
    }

}
