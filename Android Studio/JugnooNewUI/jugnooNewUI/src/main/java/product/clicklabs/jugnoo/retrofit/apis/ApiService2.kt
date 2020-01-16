package product.clicklabs.jugnoo.retrofit.apis

import com.sabkuchfresh.feed.models.FeedCommonResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService2 {

    //	https://test.jugnoo.in:8052/create_user_stream
    @Streaming
    @GET("create_user_stream")
    fun createUserStream(@Query("id") id:Int): Observable<ResponseBody>


    @Streaming
    @FormUrlEncoded
    @POST("v2/get_driver_current_location")
    fun getDriverCurrentLocation(@FieldMap params:HashMap<String, String>): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("filter_active_users")
    fun filterActiveUsers(@FieldMap params:HashMap<String, String>): Call<FeedCommonResponse>

}