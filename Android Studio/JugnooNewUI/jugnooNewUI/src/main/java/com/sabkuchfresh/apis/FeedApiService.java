package com.sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedDetailResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Parminder Singh on 3/11/17.
 */

public interface FeedApiService {

    @FormUrlEncoded
    @POST("/feeds/v1/generate_feed")
    void generateFeed(@FieldMap Map<String, String> params, Callback<FeedListResponse> callback);


    @FormUrlEncoded
    @POST("/feeds/v1/fetch_feed_details")
    void fetchFeedDetails(@FieldMap Map<String, String> params, Callback<FeedDetailResponse> callback);

    @FormUrlEncoded
    @POST("/feeds/v1/comment")
    void commentOnFeed(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/feeds/v1/like")
    void likeFeed(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

    @GET("/suggest/{query}")
    void suggestRestaurant(@Path("query") String query, Callback<SuggestRestaurantQueryResp> callback);

    @FormUrlEncoded
    @POST("/feeds/v1/sync_contacts")
    Response syncContacts(@FieldMap Map<String, String> params);

}
