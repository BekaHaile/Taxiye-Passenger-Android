package com.fugu.agent.model.unreadResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 27/06/18.
 *
 * @author gurmail
 */


public class UserUnreadCount {

    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("unread_count")
    @Expose
    private Integer unreadCount;

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

}
