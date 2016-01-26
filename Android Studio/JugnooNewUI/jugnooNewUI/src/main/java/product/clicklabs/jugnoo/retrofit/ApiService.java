package product.clicklabs.jugnoo.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.retrofit.model.ShowPromotionsResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

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

}