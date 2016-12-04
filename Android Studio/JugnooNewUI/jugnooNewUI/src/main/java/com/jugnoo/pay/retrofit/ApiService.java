package com.jugnoo.pay.retrofit;

import com.jugnoo.pay.models.LoginResponse;
import com.jugnoo.pay.models.SettleUserDebt;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 *Define all server calls here
 */
public interface ApiService {



    @FormUrlEncoded
    @POST("/verify_otp")
    void verifyOtp(@FieldMap Map<String, String> params,
                   Callback<LoginResponse> callback);


    @FormUrlEncoded
    @POST("/login_using_access_token")
    void loginUsingAccessToken(@FieldMap Map<String, String> params,
                               Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/login_using_email_or_phone_no")
    void loginUsingEmailOrPhoneNo(@FieldMap Map<String, String> params,
                                  Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/login_using_facebook")
    void loginUsingFacebook(@FieldMap Map<String, String> params,
                            Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/login_using_google")
    void loginUsingGoogle(@FieldMap Map<String, String> params,
                          Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/register_using_email")
    void registerUsingEmail(@FieldMap Map<String, String> params,
                            Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/register_using_facebook")
    void registerUsingFacebook(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/register_using_google")
    void registerUsingGoogle(@FieldMap Map<String, String> params,
                             Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/request_dup_registration")
    void requestDupRegistration(@FieldMap Map<String, String> params,
                                Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/verify_my_contact_number")
    void verifyMyContactNumber(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/send_new_number_otp_via_call")
    void sendNewNumberOtpViaCall(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/forgot_password")
    void forgotPassword(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/send_otp_via_call")
    void sendOtpViaCall(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/send_verify_email_link")
    void sendVerifyEmailLink(@FieldMap Map<String, String> params,
                             Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/logout")
    void logoutUser(@FieldMap Map<String, String> params,
                    Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/update_device_token")
    Response updateDeviceToken(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/update_user_profile")
    void updateUserProfile(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);

    @GET("/allowed_auth_channels")
    void getAllowedAuthChannels(@QueryMap Map<String, String> params,
                                Callback<SettleUserDebt> callback);

}