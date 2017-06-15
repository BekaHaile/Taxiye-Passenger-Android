package com.sabkuchfresh.retrofit.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 15/06/17.
 */

public class IciciPaymentRequestStatus {


    @SerializedName("flag")
    public int flag;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("error")
    private String error;

    public int getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
