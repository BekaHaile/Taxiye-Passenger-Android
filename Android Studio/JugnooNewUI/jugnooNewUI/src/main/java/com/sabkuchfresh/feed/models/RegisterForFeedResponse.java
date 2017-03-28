package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;
import com.jugnoo.pay.models.CommonResponse;
import com.sabkuchfresh.retrofit.model.feed.FeedCommonResponse;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class RegisterForFeedResponse extends FeedCommonResponse {

    @SerializedName("feed_rank")
    private long userRank;

    public long getUserRank() {
        return userRank;
    }
    @SerializedName("user_ahead_count")
    private long userAheadCount;



    public long getUserAheadCount() {
        return userAheadCount;
    }
}
