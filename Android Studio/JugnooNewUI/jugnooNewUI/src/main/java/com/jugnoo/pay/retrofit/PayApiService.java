package com.jugnoo.pay.retrofit;

import com.jugnoo.pay.models.AccessTokenRequest;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.FetchPayDataResponse;
import com.jugnoo.pay.models.FetchPaymentAddressResponse;
import com.jugnoo.pay.models.GenerateTokenRequest;
import com.jugnoo.pay.models.LoginRequest;
import com.jugnoo.pay.models.SendMoneyCallbackResponse;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.models.SendMoneyResponse;
import com.jugnoo.pay.models.TokenGeneratedResponse;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.models.TransactionSummaryResponse;
import com.jugnoo.pay.models.VerifyUserRequest;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by cl-macmini-38 on 18/05/16.
 */
public interface PayApiService {

    // generate token

    @POST("/generate_token")
    void generateToken(@Body() GenerateTokenRequest request, Callback<TokenGeneratedResponse> callback);

    // login user

    @POST("/login_using_phone")
    void loginUser(@Body() LoginRequest request, Callback<CommonResponse> callback);

    @FormUrlEncoded
    @POST("/forgot_password")
    void forgotPassword(@FieldMap Map<String, String> params, Callback<CommonResponse> callback);

    @FormUrlEncoded
    @POST("/reset_password")
    void resetPassword(@FieldMap Map<String, String> params, Callback<CommonResponse> callback);

    // register user
    @POST("/verify_and_register")
    void verifyUser(@Body() VerifyUserRequest request, Callback<FetchPayDataResponse> callback);


    @POST("/login_using_access_token")
    void getAccessTokenLogin(@Body() AccessTokenRequest request, Callback<CommonResponse> callback);

    // api to send money to specific user
    @POST("/send_money")
    void sendMoney(@Body() SendMoneyRequest request, Callback<SendMoneyResponse> callback);

    // api to send details after getting response from bank api
    @FormUrlEncoded
    @POST("/send_money_callback")
    void sendMoneyCallback(@FieldMap Map<String, String> params, Callback<SendMoneyCallbackResponse> callback);

    @FormUrlEncoded
    @POST("/set_mpin")
    void setMPIN(@FieldMap Map<String, String> params, Callback<SendMoneyResponse> callback);


    @FormUrlEncoded
    @POST("/set_mpin_callback")
    void setMPINCallback(@FieldMap Map<String, String> params, Callback<CommonResponse> callback);

    // api to send money to specific user
    @POST("/request_money")
    void requestMoney(@Body() SendMoneyRequest request, Callback<CommonResponse> callback);

    // api to get all transactions history
    @POST("/get_transaction_history")
    void getTranscHistory(@Body() AccessTokenRequest token, Callback<TransacHistoryResponse> callback);

    // api to cancel a particular transaction
    @POST("/cancel_request")
    void cancelTransc(@Body() AccessTokenRequest token, Callback<TransacHistoryResponse> callback);

    // api to decline a particular transaction
    @POST("/decline_request")
    void declineTransc(@Body() AccessTokenRequest token, Callback<TransacHistoryResponse> callback);

    // api to sign out user from app
    @POST("/logout")
    void logout(@Body() AccessTokenRequest token, Callback<CommonResponse> callback);


    // api to sign out user from app
    @POST("/update_user_information")
    void updateProfile(@Body() AccessTokenRequest token, Callback<CommonResponse> callback);


    // added on 28-11-2016 ------------------------

    // api to get parameters need to call Account Management service
    @FormUrlEncoded
    @POST("/account_mgmt")
    void accountManagement(@FieldMap Map<String, String> param, Callback<AccountManagementResponse> callback);


    @FormUrlEncoded
    @POST("/fetch_pay_data")
    void fetchPayData(@FieldMap Map<String, String> params,
                      Callback<FetchPayDataResponse> callback);

    @FormUrlEncoded
    @POST("/add_vpa")
    void addPaymentAddress(@FieldMap Map<String, String> params,
                      Callback<AccountManagementResponse> callback);

    @FormUrlEncoded
    @POST("/delete_vpa")
    void deletePaymentAddress(@FieldMap Map<String, String> params,
                           Callback<AccountManagementResponse> callback);

    @FormUrlEncoded
    @POST("/fetch_vpa")
    void fetchPaymentAddress(@FieldMap Map<String, String> params,
                              Callback<FetchPaymentAddressResponse> callback);

    @FormUrlEncoded
    @POST("/remind_user")
    void remindUser(@FieldMap Map<String, String> params,
                      Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/get_transaction_summary")
    void getTransactionSummary(@FieldMap Map<String, String> params,
                    Callback<TransactionSummaryResponse> callback);

    @FormUrlEncoded
    @POST("/get_transaction_history")
    void getTransactionHistory(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);
}
