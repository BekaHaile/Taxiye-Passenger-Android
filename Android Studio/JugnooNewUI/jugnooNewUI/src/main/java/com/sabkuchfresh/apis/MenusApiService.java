package com.sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;

import java.util.Map;

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
	@POST("/vendor_menu")
	void vendorMenu(@FieldMap Map<String, String> params,
						   Callback<ProductsResponse> callback);

	@FormUrlEncoded
	@POST("/user_checkout_data")
	void userCheckoutData(@FieldMap Map<String, String> params,
						  Callback<UserCheckoutResponse> callback);


	@FormUrlEncoded
	@POST("/place_order")
	void placeOrder(@FieldMap Map<String, String> params,
					Callback<PlaceOrderResponse> callback);

}
