package product.clicklabs.jugnoo.retrofit;

import com.sabkuchfresh.retrofit.model.PurchaseSubscriptionResponse;

import java.util.Map;

import product.clicklabs.jugnoo.datastructure.NotificationSettingResponseModel;
import product.clicklabs.jugnoo.datastructure.PromCouponResponse;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogReponse;
import product.clicklabs.jugnoo.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.FindPokestopResponse;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.t20.models.MatchScheduleResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 *Define all server calls here
 */
public interface ApiService {


    @FormUrlEncoded
    @POST("/leaderboard/referrals/get_leaderboards")
    void leaderboardServerCall(@Field("access_token") String accessToken,
                      @Field("client_id") String clientId,
                      Callback<LeaderboardResponse> callback);

    @FormUrlEncoded
    @POST("/leaderboard/referrals/get_activity")
    void leaderboardActivityServerCall(@Field("access_token") String accessToken,
                               @Field("client_id") String clientId,
                               Callback<LeaderboardActivityResponse> callback);

    @FormUrlEncoded
    @POST("/v2/customer/find_a_driver")
    void findADriverCall(@FieldMap Map<String, String> params,
                                       Callback<FindADriverResponse> callback);

    @FormUrlEncoded
    @POST("/paytm/wallet/adjust_money")
    void settleUserDebt(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/v3/customer/verify_otp")
    void verifyOtp(@FieldMap Map<String, String> params,
                        Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/show_panel")
    void showPanel(@FieldMap Map<String, String> params,
                   Callback<ShowPanelResponse> callback);

    @FormUrlEncoded
    @POST("/get_ride_summary")
    void getRideSummary(@FieldMap Map<String, String> params,
                   Callback<ShowPanelResponse> callback);

    @FormUrlEncoded
    @POST("/generate_support_ticket")
    void generateSupportTicket(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/v3/customer/login_using_access_token")
    void loginUsingAccessToken(@FieldMap Map<String, String> params,
                               Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v3/customer/login_using_email_or_phone_no")
    void loginUsingEmailOrPhoneNo(@FieldMap Map<String, String> params,
                                  Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v3/customer/login_using_facebook")
    void loginUsingFacebook(@FieldMap Map<String, String> params,
                                  Callback<LoginResponse> callback);

    @FormUrlEncoded
    @POST("/v3/customer/login_using_google")
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
    @POST("/get_fare_estimate")
    void getFareEstimate(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/request_ride")
    Response requestRide(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("/get_driver_current_location")
    Response getDriverCurrentLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/rate_the_driver")
    void rateTheDriver(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/skip_rating_by_customer")
    void skipRatingByCustomer(@FieldMap Map<String, String> params,
                       Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/skip_rating_by_customer")
    Response skipRatingByCustomerSync(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/submit_feedback")
    void submitFeedback(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_active_app_list")
    Response getActiveAppList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/update_user_installed_app")
    Response updateUserInstalledApp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/get_information")
    void getInformation(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/cancel_the_request")
    void cancelTheRequest(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/accept_app_rating_request")
    void acceptAppRatingRequest(@FieldMap Map<String, String> params,
                          Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/add_drop_location")
    void addDropLocation(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @GET("/get_ongoing_ride_path")
    Response getOngoingRidePath(@QueryMap Map<String, String> params);

    @GET("/allowed_auth_channels")
    void getAllowedAuthChannels(@QueryMap Map<String, String> params,
                                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/alert")
    void emergencyAlert(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/disable")
    void emergencyDisable(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/alert")
    Response emergencyAlertSync(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/refer_all_contacts")
    void referAllContacts(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/refer_all_contacts")
    Response referAllContactsSync(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/send_otp_via_call")
    void sendOtpViaCall(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_coupons_and_promotions")
    void getCouponsAndPromotions(@FieldMap Map<String, String> params,
                        Callback<PromCouponResponse> callback);

    @FormUrlEncoded
    @POST("/enter_code")
    void enterCode(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/cancel_ride_by_customer")
    void cancelRideByCustomer(@FieldMap Map<String, String> params,
                   Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/autos_integrated_order_history")
    void getRecentRides(@FieldMap Map<String, String> params,
                              Callback<HistoryResponse> callback);

    @FormUrlEncoded
    @POST("/fare_estimate_for_jeanie")
    void fareEstimateForJeanie(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/paytm/request_otp")
    void paytmRequestOtp(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/paytm/login_with_otp")
    void paytmLoginWithOtp(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/paytm/delete_paytm")
    void paytmDeletePaytm(@FieldMap Map<String, String> params,
                       Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_transaction_history")
    void getTransactionHistory(@FieldMap Map<String, String> params,
                          Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/update_user_profile")
    void updateUserProfile(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/reload_my_profile")
    void reloadMyProfile(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/send_verify_email_link")
    void sendVerifyEmailLink(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/logout_user")
    void logoutUser(@FieldMap Map<String, String> params,
                             Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/v2/add_home_and_work_address")
    void addHomeAndWorkAddress(@FieldMap Map<String, String> params,
                    Callback<FetchUserAddressResponse> callback);

    @FormUrlEncoded
    @POST("/update_device_token")
    Response updateDeviceToken(@FieldMap Map<String, String> params);

    @GET("/emergency/contacts/list")
    void emergencyContactsList(@QueryMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/add")
    void emergencyContactsAdd(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/add_multiple")
    void emergencyContactsAddMultiple(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/edit")
    void emergencyContactsEdit(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/delete")
    void emergencyContactsDelete(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/send_ride_status_message")
    void emergencySendRideStatusMessage(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/request_verification")
    void emergencyContactsRequestVerification(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/v2/customer/get_current_user_status")
    Response getCurrentUserStatus(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/analytics")
    void uploadAnalytics(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/analytics")
    Response uploadAnalytics(@FieldMap Map<String, String> params);


    @Headers("Content-Encoding: gzip")
    @POST("/analytics")
    void uploadAnalytics(@Body TypedInput typedInput,
                         Callback<SettleUserDebt> callback);



    @FormUrlEncoded
    @POST("/fetch_T20_schedule_and_user_predictions")
    void fetchT20ScheduleAndUserPrediction(@FieldMap Map<String, String> params,
                                           Callback<MatchScheduleResponse> callback);

    @FormUrlEncoded
    @POST("/insert_user_T20_prediction")
    void insertUserT20Prediction(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/complete_user_paytm_recharge")
    void paytmAuthenticateRecharge(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/update_customer_ride_location")
    void updateCustomerRideLocation(@FieldMap Map<String, String> params,
                                   Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/save_customer_emergency_location")
    void saveCustomerEmergencyLocation(@FieldMap Map<String, String> params,
                                Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/refer_a_driver")
    void referDriver(@FieldMap Map<String, String> params,
                     Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/fetch_pushes_for_user")
    void notificationInbox(@FieldMap Map<String, String> params,
                           Callback<NotificationInboxResponse> callback);

    @FormUrlEncoded
    @POST("/customer/avail_campaign")
    void availCampaign(@FieldMap Map<String, String> params,
                                   Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/customer/cancel_campaign")
    void cancelCampaign(@FieldMap Map<String, String> params,
                       Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/mobikwik/generate_otp")
    void mobikwikRequestOtp(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/mobikwik/login_with_otp")
    void mobikwikLoginWithOtp(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/mobikwik/add_money")
    void mobikwikAddMoney(@FieldMap Map<String, String> params,
                       Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/fetch_wallet_balance")
    void fetchWalletBalance(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/mobikwik/unlink")
    void mobikwikUnlink(@FieldMap Map<String, String> params,
                            Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/refresh_device_token")
    Response refreshDeviceToken(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/catch_em_all")
    void findPokestop(@FieldMap Map<String, String> params,
                        Callback<FindPokestopResponse> callback);


    @FormUrlEncoded
    @POST("/integrated_fetch_push_preference")
    void getNotificationPreference(@FieldMap Map<String, String> params,
                           Callback<NotificationSettingResponseModel> callback);

    @FormUrlEncoded
    @POST("/update_push_preference")
    void updateNotificationPreference(@FieldMap Map<String, String> params,
                           Callback<NotificationInboxResponse> callback);

    @FormUrlEncoded
    @POST("/update_client_id")
    void updateClientId(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    //    FreeCharge APIs
    @FormUrlEncoded
    @POST("/freecharge/generate_otp")
    void freeChargeRequestOtp(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/freecharge/login_with_otp")
    void freeChargeLoginWithOtp(@FieldMap Map<String, String> params,
                                Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/freecharge/add_money")
    void freechargeAddMoney(@FieldMap Map<String, String> params,
                          Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/freecharge/unlink")
    void freechargeUnlink(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);


    @Multipart
    @POST("/customer/upload_ride_log")
    Response customerUploadRideLog(@Part("log_file") TypedFile file,
                               @PartMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/thumbs_up_clicked")
    void thumbsUpClicked(@FieldMap Map<String, String> params,
                         Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/customer/fetch_ride_log")
    void customerFetchRideLog(@FieldMap Map<String, String> params,
                          Callback<TrackingLogReponse> callback);

    @FormUrlEncoded
    @POST("/topup_customer_jc")
    void topupCustomerJC(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/customer/fetch_user_address")
    void customerFetchUserAddress(@FieldMap Map<String, String> params,
                            Callback<FetchUserAddressResponse> callback);

    @FormUrlEncoded
    @POST("/cancel_subscription")
    void cancelSubscription(@FieldMap Map<String, String> params,
                                  Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/purchase_subscription")
    void purchaseSubscription(@FieldMap Map<String, String> params,
                            Callback<PurchaseSubscriptionResponse> callback);

}