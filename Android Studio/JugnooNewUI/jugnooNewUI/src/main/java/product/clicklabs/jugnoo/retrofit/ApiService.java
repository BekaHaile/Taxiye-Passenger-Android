package product.clicklabs.jugnoo.retrofit;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 *Define all server calls here
 */
public interface ApiService {

//    /**
//     *
//     * @param accessToken
//     * @param deviceToken
//     * @param regType
//     * @param callback
//     */
//    @FormUrlEncoded
//    @POST("/login_through_accesstoken")
//    public void login(@Field("access_token") String accessToken, @Field("device_token") String deviceToken, @Field("registration_type") String regType, Callback<String> callback);


    @FormUrlEncoded
    @POST("/login_using_access_token")
    public void loginUsingAccessToken(@Field("access_token") String accessToken,
                      @Field("device_token") String deviceToken,
                      @Field("latitude") String latitude,
                      @Field("longitude") String longitude,
                      @Field("app_version") String appVersion,
                      @Field("device_type") String deviceType,
                      @Field("unique_device_id") String uniqueDeviceId,
                      @Field("client_id") String clientId,
                      @Field("is_access_token_new") String isAccessTokenNew,
                      Callback<String> callback);

}