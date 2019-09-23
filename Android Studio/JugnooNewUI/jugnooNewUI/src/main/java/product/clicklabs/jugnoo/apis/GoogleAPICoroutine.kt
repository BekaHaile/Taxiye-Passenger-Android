package product.clicklabs.jugnoo.apis

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
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

    private fun getUserId():String{
        return if(Data.userData != null) Data.userData.userId else Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_USER_ID, "0")
    }

    //Api for text input autocomplete Place search
    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location:String, radius:String, callback:PlacesCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            var predictions: MutableList<Prediction>? = null
            val arr = location.split(",")
            try{
                try {
                    if (!isGoogleCachingEnabled()) {
                        throw Exception()
                    }
                    val response = withContext(Dispatchers.IO) {
                        try { RestClient.getMapsCachingService().getAutocompleteData(input, arr[0].toDouble(), arr[1].toDouble(),
                                    JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val responseCached = jsonObject.getJSONArray("data").toString()
                    predictions = gson.fromJson(responseCached, object : TypeToken<MutableList<Prediction>>() {}.type)
                } catch (e: Exception) {
                    val response: Response? = withContext(Dispatchers.IO) {
                        try { GoogleRestApis.getAutoCompletePredictions(input, sessiontoken, components, location, radius) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    val placesResponse = gson.fromJson(responseStr, PlacesAutocompleteResponse::class.java)
                    predictions = placesResponse.predictions

                    if(isGoogleCachingEnabled()
                            && placesResponse.predictions != null && placesResponse.predictions!!.size > 0) {
                        val param = InsertAutocomplete(JUNGOO_APP_PRODUCT_ID, TYPE_AUTO_COMPLETE, input, getUserId(),
                                arr[0].toDouble(), arr[1].toDouble(), placesResponse)
                        insertPlaceAutocompleteCache(param)
                    }

                }
                callback.onAutocompletePredictionsReceived(predictions!!)
            } catch (e: Exception) {
                callback.onAutocompleteError()
            }

        }
    }


    //Ai for finding place details by place Id
    fun getPlaceById(placeId:String, placeAddress:String, callback: PlaceDetailCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            var placesResponse: PlaceDetailsResponse? = null
            try {
                try{
                    if (!isGoogleCachingEnabled()) {
                        throw Exception()
                    }
                    val response = withContext(Dispatchers.IO) {
                        try { RestClient.getMapsCachingService().getGeocodingData(placeId, JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).toString()
                    val results: PlaceDetailsResponse = gson.fromJson(responseCached, PlaceDetailsResponse::class.java)
                    placesResponse = results
                } catch(e:Exception){
                    val response:Response? = withContext(Dispatchers.IO){
                        try { GoogleRestApis.getPlaceDetails(placeId) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    placesResponse = gson.fromJson(responseStr, PlaceDetailsResponse::class.java)

                    if(isGoogleCachingEnabled()
                            && placesResponse.results != null && placesResponse.results!!.size > 0) {
                        val param = InsertPlaceDetail(JUNGOO_APP_PRODUCT_ID, TYPE_GEOCODING, placeAddress, getUserId(),
                                placeId, placesResponse)
                        insertPlaceDetailCache(param)
                    }
                }
                callback.onPlaceDetailReceived(placesResponse!!)
            } catch (e: Exception) {
                callback.onPlaceDetailError()
            }
        }
    }


    //api for address from LatLng
    fun hitGeocode(latLng: LatLng, callback: GeocodeCachingCallback): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var address: GoogleGeocodeResponse? = null
            try {
                if(!isGoogleCachingEnabled()){
                    throw Exception()
                }
                val response = withContext(Dispatchers.IO) {
                    try {RestClient.getMapsCachingService().getReverseGeocode(latLng.latitude, latLng.longitude,
                            JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) {null}
                }
                val responseStr = String((response!!.body as TypedByteArray).bytes)
                val jsonObject = JSONObject(responseStr)

                val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).getString("json_data")
                val googleGeocodeResponse = gson.fromJson(responseCached, GoogleGeocodeResponse::class.java)
                address = googleGeocodeResponse
            } catch (e: Exception) {
                val response = withContext(Dispatchers.IO) {
                    try {GoogleRestApis.geocode(latLng.latitude.toString()+","+latLng.longitude, "EN")} catch (e: Exception) {null}
                }
                if (response != null) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    val googleGeocodeResponse = gson.fromJson(responseStr, GoogleGeocodeResponse::class.java)
                    if (googleGeocodeResponse.results != null && googleGeocodeResponse.results.size > 0) {
                        address = googleGeocodeResponse

                        if(isGoogleCachingEnabled()) {
                            val gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse)
                            val body = InsertGeocode(JUNGOO_APP_PRODUCT_ID, TYPE_REVERSE_GEOCODING, gapiAddress.formattedAddress, getUserId(),
                                    latLng.latitude, latLng.longitude, googleGeocodeResponse)
                            insertGeocodeCache(body)
                        }
                    }
                }
            }
            callback.geocodeAddressFetched(address)
        }
    }





    private const val JUNGOO_APP_PRODUCT_ID = 2
    private const val TYPE_REVERSE_GEOCODING = "reverse_geocoding"
    private const val TYPE_AUTO_COMPLETE = "auto_complete"
    private const val TYPE_GEOCODING = "geocoding"



    private fun insertGeocodeCache(params: InsertGeocode){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertGeocode(params)} catch (ignored: Exception) {}
        }
    }
    private fun insertPlaceAutocompleteCache(params: InsertAutocomplete){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertAutoComplete(params)} catch (ignored: Exception) {}
        }
    }
    private fun insertPlaceDetailCache(params: InsertPlaceDetail){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertPlaceDetail(params)} catch (ignored: Exception) {}
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

class InsertGeocode(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_LAT)
        val lat:Double,
        @SerializedName(Constants.KEY_LNG)
        val lng:Double,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData:GoogleGeocodeResponse
)
class InsertAutocomplete(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_LAT)
        val lat:Double,
        @SerializedName(Constants.KEY_LNG)
        val lng:Double,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData:PlacesAutocompleteResponse
)
class InsertPlaceDetail(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_PLACEID)
        val placeId:String,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData:PlaceDetailsResponse
)