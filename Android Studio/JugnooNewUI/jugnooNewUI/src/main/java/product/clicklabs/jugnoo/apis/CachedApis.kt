package product.clicklabs.jugnoo.apis

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.MapUtils
import retrofit.mime.TypedByteArray

object CachedApis {

    val gson = Gson()
    private const val JUNGOO_APP_PRODUCT_ID = 2
    private const val TYPE_REVERSE_GEOCODING = "reverse_geocoding"


    suspend fun geocode(latLng: LatLng, userId: String) : GoogleGeocodeResponse?{

        return withContext(Dispatchers.Main) {
            var address: GoogleGeocodeResponse? = null
            try {
                val response = hitCachingGeocode(latLng, userId)
                if(response != null) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)

                    val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).getString("json_data")
                    val googleGeocodeResponse = gson.fromJson(JSONObject(responseCached).toString(), GoogleGeocodeResponse::class.java)
                    if (googleGeocodeResponse.results != null && googleGeocodeResponse.results.size > 0) {
                        address = googleGeocodeResponse
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
                        address = googleGeocodeResponse

                        val params = HashMap<String, Any>()
                        params[Constants.KEY_PRODUCT_ID] = JUNGOO_APP_PRODUCT_ID
                        params[Constants.KEY_TYPE] = TYPE_REVERSE_GEOCODING

                        params[Constants.KEY_ADDRESS] = gapiAddress.formattedAddress
                        params[Constants.KEY_JSONDATA] = jsonObject
                        params[Constants.KEY_USER_ID] = userId
                        params[Constants.KEY_LAT] = latLng.latitude
                        params[Constants.KEY_LNG] = latLng.longitude

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
                RestClient.getMapsCachingService().getReverseGeocode(latLng.latitude, latLng.longitude, JUNGOO_APP_PRODUCT_ID, userId)
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