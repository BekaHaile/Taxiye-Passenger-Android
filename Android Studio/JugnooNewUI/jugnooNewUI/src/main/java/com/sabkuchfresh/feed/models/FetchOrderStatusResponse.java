package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/20/17.
 */

public class FetchOrderStatusResponse extends FeedCommonResponse {

    @SerializedName("is_paid")
    private int isPaid;

    public int getIsPaid() {
        return isPaid;
    }
}
