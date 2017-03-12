package com.sabkuchfresh.apis;

import com.jugnoo.pay.models.CommonResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Parminder Singh on 3/11/17.
 */

public interface FeedApiService {



    @FormUrlEncoded
    @POST("/v1/generate_feed")
    void getAllFeeds(@FieldMap Map<String, String> params, Callback<FeedListResponse> callback);

}
