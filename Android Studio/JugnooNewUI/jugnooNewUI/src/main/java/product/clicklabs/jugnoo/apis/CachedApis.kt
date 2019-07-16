package product.clicklabs.jugnoo.apis

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.MapUtils
import retrofit.mime.TypedByteArray

object CachedApis {

    val gson = Gson()
    private const val JUNGOO_APP_PRODUCT_ID = 2
    private const val TYPE_REVERSE_GEOCODING = "reverse_geocoding"


    suspend fun geocode(latLng: LatLng, userId: String) : String?{

        return withContext(Dispatchers.Main) {
            var address: String? = null
            try {
                val response = hitCachingGeocode(latLng, userId)
                if(response != null) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)

                    val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).getString("json_data")
                    val googleGeocodeResponse = gson.fromJson(JSONObject(responseCached).toString(), GoogleGeocodeResponse::class.java)
                    if (googleGeocodeResponse.results != null && googleGeocodeResponse.results.size > 0) {
                        val gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse)
                        address = gapiAddress.formattedAddress
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val response = hitGoogleGeocode(latLng)
                if (response != null) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val googleGeocodeResponse = gson.fromJson(responseStr, GoogleGeocodeResponse::class.java)
                    if (googleGeocodeResponse.results != null && googleGeocodeResponse.results.size > 0) {
                        val gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse)
                        address = gapiAddress.formattedAddress

                        val params = HashMap<String, Any>()
                        params["product_id"] = JUNGOO_APP_PRODUCT_ID
                        params["type"] = TYPE_REVERSE_GEOCODING

                        params["address"] = gapiAddress.formattedAddress
                        params["jsonData"] = jsonObject
                        params["user_id"] = userId
                        params["lat"] = latLng.latitude
                        params["lng"] = latLng.longitude

                        insertCache(params)
                    }
                }
            }
            address
        }

    }



    private suspend fun hitCachingGeocode(latLng: LatLng, userId:String): retrofit.client.Response?{
        return withContext(Dispatchers.IO) {
            try {
                RestClient.getMapsCachingService().getReverseGeocode(latLng.latitude, latLng.longitude, 2, userId)
            } catch (e: Exception) {
                null
            }
        }
    }
    private suspend fun hitGoogleGeocode(latLng: LatLng): retrofit.client.Response?{
        return withContext(Dispatchers.IO) {
            try {
                GoogleRestApis.geocode(latLng.latitude.toString()+","+latLng.longitude, "EN")
            } catch (e: Exception) {
                null
            }
        }
    }
    private fun insertCache(params: HashMap<String, Any>){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                RestClient.getMapsCachingService().insert(params)
            } catch (ignored: Exception) {
            }
        }
    }




}