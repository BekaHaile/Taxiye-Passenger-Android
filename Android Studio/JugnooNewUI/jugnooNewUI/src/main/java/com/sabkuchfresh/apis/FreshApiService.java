package com.sabkuchfresh.apis;

import com.jugnoo.pay.models.SendMoneyCallbackResponse;
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
public interface FreshApiService {

	@FormUrlEncoded
	@POST("/get_all_products")
	void getAllProducts(@FieldMap Map<String, String> params,
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
    @POST("/v1/customer/submit_support_query")
    void supportQuery(@FieldMap Map<String, String> params,
                      Callback<OrderHistoryResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/submit_feedback")
    void orderFeedback(@FieldMap Map<String, String> params,
                       Callback<OrderHistoryResponse> callback);


    @FormUrlEncoded
    @POST("/validate_promo_code")
    void applyPromo(@FieldMap Map<String, String> params,
                    Callback<PlaceOrderResponse> callback);

	@FormUrlEncoded
	@POST("/place_order_callback")
	void placeOrderCallback(@FieldMap Map<String, String> params,
							Callback<SendMoneyCallbackResponse> callback);

}
