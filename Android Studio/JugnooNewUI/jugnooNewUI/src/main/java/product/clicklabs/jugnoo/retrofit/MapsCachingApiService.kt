package product.clicklabs.jugnoo.retrofit

import product.clicklabs.jugnoo.Constants
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
    fun getReverseGeocode(@Field(Constants.KEY_LAT) lat: Double,
                          @Field(Constants.KEY_LNG) lng: Double,
                          @Field(Constants.KEY_PRODUCT_ID) productId: Int,
                          @Field(Constants.KEY_USER_ID) userId: String): Response

    @FormUrlEncoded
    @POST("/maps/get_autocomplete_data")
    fun getAutocompleteData(@Field(Constants.KEY_ADDRESS) address: String,
                            @Field(Constants.KEY_LAT) lat: Double,
                            @Field(Constants.KEY_LNG) lng: Double,
                            @Field(Constants.KEY_PRODUCT_ID) productId: Int,
                            @Field(Constants.KEY_USER_ID) userId: String): Response
}