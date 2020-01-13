package product.clicklabs.jugnoo.retrofit.apis

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService2 {

    //	https://test.jugnoo.in:8052/create_user_stream
    @Streaming
    @GET("create_user_stream")
    fun createUserStream(@Query("id") id:Int): Observable<ResponseBody>


    @Streaming
    @POST("get_driver_current_location_v2")
    fun getDriverCurrentLocation(@FieldMap params:HashMap<String, String>): Observable<ResponseBody>

}