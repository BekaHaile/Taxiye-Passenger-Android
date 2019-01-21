package com.fugu.agent.model.unreadResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 27/06/18.
 *
 * @author gurmail
 */

public class UnreadCountResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("user_unread_count")
        @Expose
        private List<UserUnreadCount> userUnreadCount = null;

        public List<UserUnreadCount> getUserUnreadCount() {
            return userUnreadCount;
        }

        public void setUserUnreadCount(List<UserUnreadCount> userUnreadCount) {
            this.userUnreadCount = userUnreadCount;
        }
    }
}
