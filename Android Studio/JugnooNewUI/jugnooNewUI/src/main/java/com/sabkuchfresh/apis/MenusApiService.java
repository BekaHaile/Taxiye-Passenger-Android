package com.sabkuchfresh.apis;

import com.jugnoo.pay.models.SendMoneyCallbackResponse;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.retrofit.model.OrderCancelReasonsResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;
import com.sabkuchfresh.retrofit.model.menus.CuisineResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.RestaurantSearchResponse;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by shankar on 4/7/16.
 */
public interface MenusApiService {

	@FormUrlEncoded
	@POST("/nearby_restaurants_v2")
	void nearbyRestaurants(@FieldMap Map<String, String> params,
						   Callback<MenusResponse> callback);
	@FormUrlEncoded
	@POST("/nearby_cuisines")
	void nearbyCuisines(@FieldMap Map<String, String> params,
						   Callback<CuisineResponse> callback);

	@FormUrlEncoded
	@POST("/v2/restaurant_menu")
	void restaurantMenu(@FieldMap Map<String, String> params,
						Callback<VendorMenuResponse> callback);

	@FormUrlEncoded
	@POST("/user_checkout_data")
	void userCheckoutData(@FieldMap Map<String, String> params,
						  Callback<UserCheckoutResponse> callback);

	@FormUrlEncoded
	@POST("/place_order")
	void placeOrder(@FieldMap Map<String, String> params,
					Callback<PlaceOrderResponse> callback);

	@FormUrlEncoded
	@POST("/v2/order_history")
	void orderHistory(@FieldMap Map<String, String> params,
					  Callback<HistoryResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_order")
	void cancelOrder(@FieldMap Map<String, String> params,
					 Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/fetch_cancellation_reasons")
	void fetchCancellationReasons(@FieldMap Map<String, String> params,
								  Callback<OrderCancelReasonsResponse> callback);

	@FormUrlEncoded
	@POST("/v1/customer/submit_feedback")
	void orderFeedback(@FieldMap Map<String, String> params,
					   Callback<OrderHistoryResponse> callback);



	@POST("/v1/customer/submit_feedback")
	void orderFeedback(@Body MultipartTypedOutput params,
					   Callback<OrderHistoryResponse> callback);

	@POST("/v1/customer/edit_feedback")
	void editFeedback(@Body MultipartTypedOutput params,
					   Callback<OrderHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/place_order_callback")
	void placeOrderCallback(@FieldMap Map<String, String> params,
							Callback<SendMoneyCallbackResponse> callback);

	@FormUrlEncoded
	@POST("/menus/check_payment_status")
	void checkPaymentStatus(@FieldMap Map<String, String> params,
							Callback<IciciPaymentRequestStatus> callback);


	@FormUrlEncoded
	@POST("/fetch_restaurant_via_search")
	void fetchRestaurantViaSearch(@FieldMap Map<String, String> params,
							Callback<RestaurantSearchResponse> callback);

	@FormUrlEncoded
	@POST("/v1/customer/suggest_restaurant")
	void suggestRestaurant(@FieldMap Map<String, String> params,
								  Callback<SettleUserDebt> callback);


	@POST("/v1/customer/suggest_restaurant")
	void suggestStore(@Body MultipartTypedOutput params,
								  Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/v1/restaurant/fetch_feedbacks")
	void restaurantFetchFeedbacks(@FieldMap Map<String, String> params,
						   Callback<FetchFeedbackResponse> callback);

	@FormUrlEncoded
	@POST("/v1/customer/like_share_feedback")
	void customerLikeShareFeedback(@FieldMap Map<String, String> params,
								  Callback<FetchFeedbackResponse> callback);

	@FormUrlEncoded
	@POST("/razorpay_place_order_callback")
	Response razorpayPlaceOrderCallback(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/confirm_delivery_by_user")
	void confirmDeliveryByUser(@FieldMap Map<String, String> params,
						   Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/fetch_restaurant_via_search_v2")
	void fetchRestaurantViaSearchV2(@FieldMap Map<String, String> params,
						   Callback<MenusResponse> callback);

	@FormUrlEncoded
	@POST("/user_click_events")
	void userClickEvents(@FieldMap Map<String, String> params,
							   Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/user_category_logs")
	void userCategoryLogs(@FieldMap Map<String, String> params,
						 Callback<FeedCommonResponse> callback);


}
