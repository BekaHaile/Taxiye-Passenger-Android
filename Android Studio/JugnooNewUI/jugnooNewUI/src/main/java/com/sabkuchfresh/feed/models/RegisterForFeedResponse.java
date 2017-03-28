package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.SerializedName;
import com.jugnoo.pay.models.CommonResponse;
import com.sabkuchfresh.retrofit.model.feed.FeedCommonResponse;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class RegisterForFeedResponse extends FeedCommonResponse {

    @SerializedName("feed_rank")
    private int userRank;

    public int getUserRank() {
        return userRank;
    }

}
