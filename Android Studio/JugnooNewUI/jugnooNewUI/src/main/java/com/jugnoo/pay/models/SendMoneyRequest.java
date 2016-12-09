package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SendMoneyRequest implements Serializable {
    @SerializedName("access_token")
    @Expose
    private String access_token;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("phone_no")
    @Expose
    private String phone_no;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order_id")
    @Expose
    private String order_id;
    @SerializedName("vpa")
    @Expose
    private String vpa;
    @SerializedName("has_vpa")
    @Expose
    private int has_vpa;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public int getHas_vpa() {
        return has_vpa;
    }

    public void setHas_vpa(int has_vpa) {
        this.has_vpa = has_vpa;
    }
}
