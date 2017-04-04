package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class CountNotificationResponse extends FeedCommonResponse {

    @SerializedName("count_notification")
    private long countNotification;

    public long getCountNotification() {
        return countNotification;
    }

    public void setCountNotification(long countNotification) {
        this.countNotification = countNotification;
    }
}
