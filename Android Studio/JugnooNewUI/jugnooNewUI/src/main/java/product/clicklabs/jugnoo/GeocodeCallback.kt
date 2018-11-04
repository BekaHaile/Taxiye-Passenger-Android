package product.clicklabs.jugnoo

import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Prefs
import retrofit.Callback
import retrofit.client.Response

abstract class GeocodeCallback(private val googleApiClient : GeoDataClient) : Callback<GoogleGeocodeResponse>{

    companion object {
        const val TYPE_ESTABLISHMENT = "establishment"
        const val TYPE_POINT_OF_INTEREST = "point_of_interest"
    }

    override fun success(t: GoogleGeocodeResponse?, response: Response?) {
        if(googleApiClient != null && Prefs.with(googleApiClient.applicationContext).getBoolean(Constants.KEY_HIT_PLACE_DETAILS_AFTER_GEOCODE, false)
                && t?.results != null && t.results.size > 0
                && !t.results[0].placeId.isEmpty()
                && (t.results[0].types.contains(TYPE_ESTABLISHMENT) || t.results[0].types.contains(TYPE_POINT_OF_INTEREST))){
            googleApiClient.getPlaceById(t.results[0].placeId).addOnCompleteListener { task->
                var myPlace: Place? = null
                try {
                    if (task.isSuccessful) {
                        myPlace = task.result.get(0)
                        t.results[0].placeName = (myPlace.name as String).split(",")[0]
                        Log.e("SearchListAdapter", "getPlaceById from poi response=${t.results[0].placeName}")
                    }
                    task.result.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                onSuccess(t, response)
                if(myPlace != null && myPlace.latLng != null) {
                    GoogleRestApis.logGoogleRestAPIC(myPlace.latLng.latitude.toString(), myPlace.latLng.longitude.toString(), GoogleRestApis.API_NAME_PLACES)
                }
            }.addOnFailureListener {
                onSuccess(t, response)
            }
        } else {
            onSuccess(t, response)
        }
    }

    abstract fun onSuccess(t: GoogleGeocodeResponse?, response: Response?)

}