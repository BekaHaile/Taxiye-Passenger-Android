package product.clicklabs.jugnoo.apis

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import kotlinx.coroutines.*
import org.json.JSONObject
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.PlaceDetailsResponse
import product.clicklabs.jugnoo.retrofit.model.PlacesAutocompleteResponse
import product.clicklabs.jugnoo.retrofit.model.Prediction
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.MapUtils
import product.clicklabs.jugnoo.utils.Prefs
import retrofit.client.Response
import retrofit.mime.TypedByteArray

object GoogleAPICoroutine {

    private val gson = Gson()

    private fun isGoogleCachingEnabled():Boolean{
        return Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_GOOGLE_CACHING_ENABLED, 1) == 1
    }

    //Api for text input autocomplete Place search
    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location:String, radius:String, callback:PlacesCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            val response:Response? = withContext(Dispatchers.IO) {
                try { GoogleRestApis.getAutoCompletePredictions(input, sessiontoken, components, location, radius) } catch (e: Exception) { null }
            }
            try {
                val responseStr = String((response!!.body as TypedByteArray).bytes)
                val placesResponse: PlacesAutocompleteResponse? = gson.fromJson(responseStr, PlacesAutocompleteResponse::class.java)
                callback.onAutocompletePredictionsReceived(placesResponse!!.predictions!!)
            } catch (e: Exception) {
                callback.onAutocompleteError()
            }
        }
    }


    //Ai for finding place details by place Id
    fun getPlaceById(input:String, sessiontoken:String, callback: PlaceDetailCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            val response:Response? = withContext(Dispatchers.IO){
                try { GoogleRestApis.getPlaceDetails(input, sessiontoken) } catch (e: Exception) { null }
            }
            try {
                val responseStr = String((response!!.body as TypedByteArray).bytes)
                val placesResponse: PlaceDetailsResponse? = gson.fromJson(responseStr, PlaceDetailsResponse::class.java)
                callback.onPlaceDetailReceived(placesResponse!!)
            } catch (e: Exception) {
                callback.onPlaceDetailError()
            }
        }
    }

    fun hitGeocode(latLng: LatLng, callback: GeocodeCachingCallback): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var address: GoogleGeocodeResponse? = null
            try {
                if(!isGoogleCachingEnabled()){
                    throw Exception()
                }
                val response = withContext(Dispatchers.IO) {
                    try {RestClient.getMapsCachingService().getReverseGeocode(latLng.latitude, latLng.longitude,
                            JUNGOO_APP_PRODUCT_ID, Data.userData.userId) } catch (e: Exception) {null}
                }
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
                val response = withContext(Dispatchers.IO) {
                    try {GoogleRestApis.geocode(latLng.latitude.toString()+","+latLng.longitude, "EN")} catch (e: Exception) {null}
                }
                if (response != null) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val googleGeocodeResponse = gson.fromJson(responseStr, GoogleGeocodeResponse::class.java)
                    if (googleGeocodeResponse.results != null && googleGeocodeResponse.results.size > 0) {
                        val gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse)
                        address = googleGeocodeResponse

                        if(isGoogleCachingEnabled()) {
                            val params = HashMap<String, Any>()
                            params[Constants.KEY_PRODUCT_ID] = JUNGOO_APP_PRODUCT_ID
                            params[Constants.KEY_TYPE] = TYPE_REVERSE_GEOCODING
                            params[Constants.KEY_ADDRESS] = gapiAddress.formattedAddress
                            params[Constants.KEY_JSONDATA] = jsonObject
                            params[Constants.KEY_USER_ID] = Data.userData.userId
                            params[Constants.KEY_LAT] = latLng.latitude
                            params[Constants.KEY_LNG] = latLng.longitude
                            insertCache(params)
                        }
                    }
                }
            }
            callback.geocodeAddressFetched(address)
        }
    }





    private const val JUNGOO_APP_PRODUCT_ID = 2
    private const val TYPE_REVERSE_GEOCODING = "reverse_geocoding"



    private fun insertCache(params: HashMap<String, Any>){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insert(params)} catch (ignored: Exception) {}
        }
    }
}

interface PlacesCallback{
    fun onAutocompletePredictionsReceived(predictions:MutableList<Prediction>)
    fun onAutocompleteError()
}
interface PlaceDetailCallback{
    fun onPlaceDetailReceived(placeDetailsResponse: PlaceDetailsResponse)
    fun onPlaceDetailError()
}
interface GeocodeCachingCallback{
    fun geocodeAddressFetched(address: GoogleGeocodeResponse?)
}