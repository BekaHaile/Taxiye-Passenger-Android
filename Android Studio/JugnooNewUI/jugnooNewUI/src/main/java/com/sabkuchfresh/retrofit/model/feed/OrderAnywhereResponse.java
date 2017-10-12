package com.sabkuchfresh.retrofit.model.feed;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

/**
 * Created by Parminder Saini on 10/10/17.
 */

public class OrderAnywhereResponse extends FeedCommonResponse {

    @SerializedName("order_id")
    private int orderId;

    public int getOrderId() {
        return orderId;
    }

    @SerializedName("fugu_channel_name")
    private String fuguChannelName;
    @SerializedName("fugu_channel_id")
    private String fuguChannelId;
    @SerializedName("fugu_tags")
    private ArrayList<String> fuguTags;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getFuguChannelName() {
        return fuguChannelName;
    }

    public void setFuguChannelName(String fuguChannelName) {
        this.fuguChannelName = fuguChannelName;
    }

    public String getFuguChannelId() {
        return fuguChannelId;
    }

    public void setFuguChannelId(String fuguChannelId) {
        this.fuguChannelId = fuguChannelId;
    }

    public ArrayList<String> getFuguTags() {
        return fuguTags;
    }

    public void setFuguTags(ArrayList<String> fuguTags) {
        this.fuguTags = fuguTags;
    }
}
