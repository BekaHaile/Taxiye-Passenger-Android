package com.sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by shankar on 4/7/16.
 */
public interface MenusApiService {

	@FormUrlEncoded
	@POST("/nearby_restaurants")
	void nearbyRestaurants(@FieldMap Map<String, String> params,
						   Callback<MenusResponse> callback);

	@FormUrlEncoded
	@POST("/restaurant_menu")
	void restaurantMenu(@FieldMap Map<String, String> params,
						   Callback<ProductsResponse> callback);

	@FormUrlEncoded
	@POST("/user_checkout_data")
	void userCheckoutData(@FieldMap Map<String, String> params,
						  Callback<UserCheckoutResponse> callback);

	@FormUrlEncoded
	@POST("/place_order")
	void placeOrder(@FieldMap Map<String, String> params,
					Callback<PlaceOrderResponse> callback);

	@FormUrlEncoded
	@POST("/order_history")
	void orderHistory(@FieldMap Map<String, String> params,
					  Callback<HistoryResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_order")
	void cancelOrder(@FieldMap Map<String, String> params,
					 Callback<OrderHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/v1/customer/submit_feedback")
	void orderFeedback(@FieldMap Map<String, String> params,
					   Callback<OrderHistoryResponse> callback);

}
