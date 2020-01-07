package product.clicklabs.jugnoo.retrofit.apis

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService2 {

    //	https://test.jugnoo.in:8052/create_user_stream
    @Streaming
    @GET("create_user_stream")
    fun createUserStream(@Query("id") id:Int): Observable<ResponseBody>


}