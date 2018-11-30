package com.fugu.agent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class FuguAgentGetMessageResponse {
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AgentData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AgentData getData() {
        return data;
    }

    public void setData(AgentData data) {
        this.data = data;
    }


}

