package product.clicklabs.jugnoo.retrofit

import com.sabkuchfresh.feed.models.FeedCommonResponse
import retrofit.Callback
import retrofit.client.Response
import retrofit.http.*

interface MapsCachingApiService {

    @POST("/maps/insert")
    fun insertMapData(@QueryMap params: HashMap<String,String>): Response

    @FormUrlEncoded
    @POST("/maps/get_reverse_geocoding_data")
    fun getReverseGeocode(@Field("lat") lat: Double,
                          @Field("lng") lng: Double,
                          @Field("product_id") productId: Int,
                          @Field("user_id") userId: String,callback: Callback<FeedCommonResponse>)
}