package com.sabkuchfresh.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 2/6/18.
 */

public class UserContactObject {

    @SerializedName("user_image")
    private String userImage;

    @SerializedName("phone_no")
    private String phoneNumber;

    @SerializedName("user_name")
    private String userName="";

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(final String userImage) {
        this.userImage = userImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
