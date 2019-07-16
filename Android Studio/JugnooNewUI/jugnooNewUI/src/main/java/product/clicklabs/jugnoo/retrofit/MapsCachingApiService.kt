package product.clicklabs.jugnoo.retrofit

import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FieldMap
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface MapsCachingApiService {

    @FormUrlEncoded
    @POST("/maps/insert")
    fun insert(@FieldMap params: HashMap<String, Any>): Response

    @FormUrlEncoded
    @POST("/maps/get_reverse_geocoding_data")
    fun getReverseGeocode(@Field("lat") lat: Double,
                          @Field("lng") lng: Double,
                          @Field("product_id") productId: Int,
                          @Field("user_id") userId: String): Response
}