package product.clicklabs.jugnoo.apis

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import product.clicklabs.jugnoo.retrofit.model.PlaceDetailsResponse
import product.clicklabs.jugnoo.retrofit.model.PlacesAutocompleteResponse
import product.clicklabs.jugnoo.retrofit.model.Prediction
import product.clicklabs.jugnoo.utils.GoogleRestApis
import retrofit.client.Response
import retrofit.mime.TypedByteArray

object GoogleAPICoroutine {

    private val gson = Gson()

    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location:String, radius:String, callback:PlacesCallback){
        GlobalScope.launch(Dispatchers.Main){
            val response:Response? = withContext(Dispatchers.IO) {
                GoogleRestApis.getAutoCompletePredictions(input, sessiontoken, components, location, radius)
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


    fun getPlaceById(input:String, sessiontoken:String, callback: PlaceDetailCallback){
        GlobalScope.launch(Dispatchers.Main){
            val response:Response? = withContext(Dispatchers.IO){
                GoogleRestApis.getPlaceDetails(input, sessiontoken)
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

}

interface PlacesCallback{
    fun onAutocompletePredictionsReceived(predictions:MutableList<Prediction>)
    fun onAutocompleteError()
}
interface PlaceDetailCallback{
    fun onPlaceDetailReceived(placeDetailsResponse: PlaceDetailsResponse)
    fun onPlaceDetailError()
}