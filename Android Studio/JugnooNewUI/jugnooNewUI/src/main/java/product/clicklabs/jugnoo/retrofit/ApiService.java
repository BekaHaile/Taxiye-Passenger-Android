package product.clicklabs.jugnoo.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.retrofit.model.ShowPromotionsResponse;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
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
    @POST("/find_a_driver")
    void findADriverCall(@FieldMap Map<String, String> params,
                                       Callback<FindADriverResponse> callback);

    @FormUrlEncoded
    @POST("/show_available_promotions")
    void showAvailablePromotionsCall(@FieldMap Map<String, String> params,
                         Callback<ShowPromotionsResponse> callback);

    @FormUrlEncoded
    @POST("/paytm/wallet/adjust_money")
    void adjustUserDebt(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);


    @FormUrlEncoded
    @POST("/verify_otp")
    void verifyOtp(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/show_panel")
    void showPanel(@FieldMap Map<String, String> params,
                   Callback<ShowPanelResponse> callback);

    @FormUrlEncoded
    @POST("/get_ride_summary")
    void getRideSummary(@FieldMap Map<String, String> params,
                   Callback<GetRideSummaryResponse> callback);

    @FormUrlEncoded
    @POST("/generate_support_ticket")
    void generateSupportTicket(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/login_using_access_token")
    void loginUsingAccessToken(@FieldMap Map<String, String> params,
                   Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/login_using_email_or_phone_no")
    void loginUsingEmailOrPhoneNo(@FieldMap Map<String, String> params,
                                  Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/login_using_facebook")
    void loginUsingFacebook(@FieldMap Map<String, String> params,
                                  Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/login_using_google")
    void loginUsingGoogle(@FieldMap Map<String, String> params,
                            Callback<SettleUserDebt> callback);

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
    @POST("/paytm/check_balance")
    void paytmCheckBalance(@FieldMap Map<String, String> params,
                           Callback<SettleUserDebt> callback);


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

    @FormUrlEncoded
    @POST("/emergency/alert")
    void emergencyAlert(@FieldMap Map<String, String> params,
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
    @POST("/find_sharing_autos_nearby")
    void findSharingAutosNearby(@FieldMap Map<String, String> params,
                          Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/end_sharing_ride")
    void endSharingRide(@FieldMap Map<String, String> params,
                                Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/send_otp_via_call")
    void sendOtpViaCall(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_coupons_and_promotions")
    void getCouponsAndPromotions(@FieldMap Map<String, String> params,
                        Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/enter_code")
    void enterCode(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/cancel_ride_by_customer")
    void cancelRideByCustomer(@FieldMap Map<String, String> params,
                   Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_recent_rides")
    void getRecentRides(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

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
    @POST("/add_home_and_work_address")
    void addHomeAndWorkAddress(@FieldMap Map<String, String> params,
                    Callback<SettleUserDebt> callback);

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
    @POST("/emergency/contacts/edit")
    void emergencyContactsEdit(@FieldMap Map<String, String> params,
                              Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/delete")
    void emergencyContactsDelete(@FieldMap Map<String, String> params,
                               Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/emergency/contacts/request_verification")
    void emergencyContactsRequestVerification(@FieldMap Map<String, String> params,
                                 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/get_current_user_status")
    Response getCurrentUserStatus(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/upload_analytics_messages")
    void uploadAnalyticsMessages(@FieldMap Map<String, String> params,
                                              Callback<SettleUserDebt> callback);


}