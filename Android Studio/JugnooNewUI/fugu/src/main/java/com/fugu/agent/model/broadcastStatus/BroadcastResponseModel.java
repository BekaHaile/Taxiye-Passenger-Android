package com.fugu.agent.model.broadcastStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 30/07/18.
 *
 * @author gurmail
 */

public class BroadcastResponseModel {

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

        @SerializedName("broadcast_info")
        @Expose
        private List<BroadcastInfo> broadcastInfo = null;

        public List<BroadcastInfo> getBroadcastInfo() {
            return broadcastInfo;
        }

        public void setBroadcastInfo(List<BroadcastInfo> broadcastInfo) {
            this.broadcastInfo = broadcastInfo;
        }

    }
}
