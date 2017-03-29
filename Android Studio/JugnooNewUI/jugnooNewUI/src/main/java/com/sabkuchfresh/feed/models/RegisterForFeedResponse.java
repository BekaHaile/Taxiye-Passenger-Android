package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.retrofit.model.feed.FeedCommonResponse;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class RegisterForFeedResponse extends FeedCommonResponse {

    @SerializedName("feed_rank")
    private long feedRank;

    public long getFeedRank() {
        return feedRank;
    }

    public void setFeedRank(long feedRank) {
        this.feedRank = feedRank;
    }
}
