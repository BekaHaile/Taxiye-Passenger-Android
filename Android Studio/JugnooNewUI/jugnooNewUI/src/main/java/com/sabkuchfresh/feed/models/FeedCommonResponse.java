package com.sabkuchfresh.feed.models;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 26/03/17.
 */

public class FeedCommonResponse {



    @SerializedName("flag")
    @Expose
    public int flag;
    @SerializedName("user_debt")
    @Expose
    public double userDebt;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("error")
    @Expose
    public String error;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public double getUserDebt() {
        return userDebt;
    }

    public void setUserDebt(double userDebt) {
        this.userDebt = userDebt;
    }
}
