package com.sabkuchfresh.apis;

import com.sabkuchfresh.feed.models.CountNotificationResponse;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.models.FeedNotificationsResponse;
import com.sabkuchfresh.feed.models.RegisterForFeedResponse;
import com.sabkuchfresh.feed.models.feedcitiesresponse.FeedCityResponse;
import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedDetailResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.mime.MultipartTypedOutput;


/**
 * Created by Parminder Singh on 3/11/17.
 */

public interface FeedApiService {

	@FormUrlEncoded
	@POST("/feeds/v1/generate_feed")
	void generateFeed(@FieldMap Map<String, String> params, Callback<FeedListResponse> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/generate_feed")
	<T extends FeedCommonResponse> void testAPI(@FieldMap Map<String, String> params, Callback<T> callback);


	@FormUrlEncoded
	@POST("/feeds/v1/fetch_feed_details")
	void fetchFeedDetails(@FieldMap Map<String, String> params, Callback<FeedDetailResponse> callback);


	@POST("/feeds/v1/post")
	void postFeed(@Body MultipartTypedOutput multipartTypedOutput, Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/comment")
	void commentOnFeed(@FieldMap Map<String, String> params, Callback<FeedDetailResponse> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/like")
	void likeFeed(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/unlike")
	void unlikeFeed(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/delete_post")
	void deleteFeed(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/suggest")
	void suggestRestaurant(@FieldMap Map<String, String> params, Callback<SuggestRestaurantQueryResp> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/sync_contacts")
	Response syncContacts(@FieldMap Map<String, String> params);

	@POST("/feeds/v1/edit_post")
	void editFeed(@Body MultipartTypedOutput multipartTypedOutput, Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/delete_comment")
	void deleteComment(@FieldMap Map<String, String> params, Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/register_feeds")
	void registerForFeed(@FieldMap Map<String, String> params, Callback<RegisterForFeedResponse> callback);


	@FormUrlEncoded
	@POST("/feeds/v1/get_notification")
	void getNotifications(@FieldMap Map<String, String> params, Callback<FeedNotificationsResponse> callback);


	@FormUrlEncoded
	@POST("/feeds/v1/update_notification")
	void updateNotification(@FieldMap Map<String, String> params, Callback<FeedNotificationsResponse> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/count_notification")
	void countNotification(@FieldMap Map<String, String> params, Callback<CountNotificationResponse> callback);


   @FormUrlEncoded
	@POST("/feeds/v1/get_city_info")
	void getCityInfo(@FieldMap Map<String, String> params, Callback<FeedCityResponse> callback);

	@FormUrlEncoded
	@POST("/feeds/v1/set_user_handle")
	void setUserHandle(@FieldMap Map<String, String> params, Callback<FeedCommonResponse> callback);



}
