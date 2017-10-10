package com.sabkuchfresh.apis;

import com.jugnoo.pay.models.SendMoneyCallbackResponse;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.retrofit.model.FreshSearchResponse;
import com.sabkuchfresh.retrofit.model.OrderCancelReasonsResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
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

	@FormUrlEncoded
	@POST("/order_status")
	void checkPaymentStatus(@FieldMap Map<String, String> params,
							Callback<IciciPaymentRequestStatus> callback);


	@FormUrlEncoded
	@POST("/get_super_categories")
	void getSuperCategories(@FieldMap Map<String, String> params,
							Callback<SuperCategoriesData> callback);

	@FormUrlEncoded
	@POST("/get_item_search")
	void getItemSearch(@FieldMap Map<String, String> params,
							Callback<FreshSearchResponse> callback);

	@FormUrlEncoded
	@POST("/razorpay_place_order_callback")
	Response razorpayPlaceOrderCallback(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/update_like_count")
	void markMealAsFavourite(@FieldMap Map<String, String> params,
					   Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/custom_orders/place_order")
	void anywherePlaceOrder(@FieldMap Map<String, String> params,
						 Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/custom_orders/get_custom_order_history")
	void getCustomOrderHistory(@FieldMap Map<String, String> params,
					  Callback<HistoryResponse> callback);

}
