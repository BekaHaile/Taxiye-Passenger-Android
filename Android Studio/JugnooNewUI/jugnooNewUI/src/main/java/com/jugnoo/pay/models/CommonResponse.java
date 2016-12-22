package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonResponse implements Serializable{

    private Integer flag;
    private String access_token;
    private String message;
    private String order_id,faq_link,support_link;
    private UserDetails user_details;
    @SerializedName("date")
    @Expose
    private String date;

    public UserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(UserDetails user_details) {
        this.user_details = user_details;
    }

    public String getFaq_link() {
        return faq_link;
    }

    public void setFaq_link(String faq_link) {
        this.faq_link = faq_link;
    }

    public String getSupport_link() {
        return support_link;
    }

    public void setSupport_link(String support_link) {
        this.support_link = support_link;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public class UserDetails{
        private String user_name,user_email,phone_no;

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(String phone_no) {
            this.phone_no = phone_no;
        }
    }

}
