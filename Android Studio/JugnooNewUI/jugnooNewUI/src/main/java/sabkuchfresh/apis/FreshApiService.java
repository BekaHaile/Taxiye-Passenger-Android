package sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.LoginResponse;
import com.sabkuchfresh.retrofit.model.NotificationInboxResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.ReferralResponse;
import com.sabkuchfresh.retrofit.model.SettleUserDebt;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by shankar on 4/7/16.
 */
public interface FreshApiService {


    @FormUrlEncoded
    @POST("/v1/customer/login_using_access_token")
    void loginUsingAccessToken(@FieldMap Map<String, String> params,
                               Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/login_using_email_or_phone_no")
    void loginUsingEmailOrPhoneNo(@FieldMap Map<String, String> params,
                                  Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/register_using_email")
    void registerUsingEmail(@FieldMap Map<String, String> params,
                            Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/v1/customer/register_using_facebook")
    void registerUsingFacebook(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/v1/customer/login_using_facebook")
    void loginUsingFacebook(@FieldMap Map<String, String> params,
                            Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/login_using_google")
    void loginUsingGoogle(@FieldMap Map<String, String> params,
                          Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/register_using_google")
    void registerUsingGoogle(@FieldMap Map<String, String> params,
                             Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/v1/customer/verify_otp")
    void verifyOtp(@FieldMap Map<String, String> params,
                   Callback<LoginResponse> callback);


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
                      Callback<OrderHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_order")
	void cancelOrder(@FieldMap Map<String, String> params,
                     Callback<OrderHistoryResponse> callback);

    @FormUrlEncoded
    @POST("/v1/customer/fetch_pushes")
    void notificationInbox(@FieldMap Map<String, String> params,
                           Callback<NotificationInboxResponse> callback);

    @FormUrlEncoded
    @GET("/v1/customer/fetch_code")
    void referServerCall(@FieldMap Map<String, String> params,
                         Callback<ReferralResponse> callback);


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
}
