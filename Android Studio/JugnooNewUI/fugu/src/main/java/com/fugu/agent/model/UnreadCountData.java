package com.fugu.agent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 27/06/18.
 *
 * @author gurmail
 */

public class UnreadCountData {

    @SerializedName("user_id")
    @Expose
    private int hippoUserId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("count")
    @Expose
    private int count;

    public int getHippoUserId() {
        return hippoUserId;
    }

    public void setHippoUserId(int hippoUserId) {
        this.hippoUserId = hippoUserId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return ((UnreadCountData) obj).getUserUniqueKey().equalsIgnoreCase(getUserUniqueKey());
        } catch (Exception e) {
            return false;
        }
    }
}
