package com.sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.feed.DynamicDeliveryResponse;
import com.sabkuchfresh.retrofit.model.feed.OrderAnywhereResponse;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by shankar on 4/7/16.
 */
public interface FatafatApiService {

	@FormUrlEncoded
	@POST("/place_order")
	void anywherePlaceOrder(@FieldMap Map<String, String> params,
							Callback<OrderAnywhereResponse> callback);

	@FormUrlEncoded
	@POST("/order_history")
	void getCustomOrderHistory(@FieldMap Map<String, String> params,
							   Callback<HistoryResponse> callback);


	@GET("/get_all_details")
    void dynamicDeliveryCharges(@QueryMap Map<String, String> params, Callback<DynamicDeliveryResponse> callback);
}
