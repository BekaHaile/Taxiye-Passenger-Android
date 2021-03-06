package product.clicklabs.jugnoo.retrofit.apis

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.QueryMap

interface JungleMapsApi {

    @GET("/directions?driving_mode=car")
    fun directions(@QueryMap params: Map<String, String>): Response

    @GET("/distancematrix")
    fun distancematrix(@QueryMap params: Map<String, String>): Response

    @GET("/search_reverse?zoom=18")
    fun searchReverse(@QueryMap params: Map<String, String>): Response

    @GET("/search")
    fun search(@QueryMap params: Map<String, String>): Response

    @GET("/geocode")
    fun geocodePlaceById(@QueryMap params: Map<String, String>): Response

}