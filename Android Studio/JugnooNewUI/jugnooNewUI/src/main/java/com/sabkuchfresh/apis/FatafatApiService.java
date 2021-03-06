package com.sabkuchfresh.apis;

import com.sabkuchfresh.feed.models.ContactResponseModel;
import com.sabkuchfresh.feed.models.FetchOrderStatusResponse;
import com.sabkuchfresh.retrofit.model.OrderCancelReasonsResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.feed.DynamicDeliveryResponse;
import com.sabkuchfresh.retrofit.model.feed.NearbyDriversResponse;
import com.sabkuchfresh.retrofit.model.feed.OrderAnywhereResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.CreateChatResponse;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.PaymentResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by shankar on 4/7/16.
 */
public interface FatafatApiService {

    @POST("/place_order")
    void anywherePlaceOrder(@Body MultipartTypedOutput params,
                            Callback<OrderAnywhereResponse> callback);

    @FormUrlEncoded
    @POST("/order_history")
    void getCustomOrderHistory(@FieldMap Map<String, String> params,
                               Callback<HistoryResponse> callback);


    @GET("/get_all_details")
    void dynamicDeliveryCharges(@QueryMap Map<String, String> params, Callback<DynamicDeliveryResponse> callback);

    @FormUrlEncoded
    @POST("/fetch_order_status")
    void fetchOrderStatus(@FieldMap Map<String, String> params, Callback<FetchOrderStatusResponse> callback);

    @FormUrlEncoded
    @POST("/pay_for_order")
    void payForOrder(@FieldMap Map<String, String> params, Callback<PaymentResponse> callback);

    @FormUrlEncoded
    @POST("/cancel_payment")
    void cancelPayment(@FieldMap Map<String, String> params,
                     Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/chat/create_chat")
    void createChat(@FieldMap Map<String, String> params,
                     Callback<CreateChatResponse> callback);



    @FormUrlEncoded
    @POST("/chat/fetch_contacts")
    void fetchContacts(@FieldMap Map<String, String> params, Callback<ContactResponseModel> callback);


    @FormUrlEncoded
    @POST("/submit_feedback")
    void orderFeedback(@FieldMap Map<String, String> params,
                       Callback<OrderHistoryResponse> callback);

    @GET("/nearby_agents")
    void nearbyAgents(@QueryMap Map<String, String> params, Callback<NearbyDriversResponse> callback);

    @FormUrlEncoded
    @POST("/cancel_order")
    void cancelOrder(@FieldMap Map<String, String> params,
                     Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/razorpay/payment_callback")
    Response razorpayPaymentCallback(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/fetch_cancellation_reasons")
    void fetchCancellationReasons(@FieldMap Map<String, String> params,
                                  Callback<OrderCancelReasonsResponse> callback);

}
