package product.clicklabs.jugnoo.fragments

import com.google.android.gms.maps.model.LatLng
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.apis.CachedApis

object GoogleCachingApiKT {


    fun hitGeocode(latLng: LatLng, callback: GeocodeCachingCallback): Job {

        return GlobalScope.launch(Dispatchers.Main) {
            val address = CachedApis.geocode(latLng, Data.userData.userId)
            callback.geocodeAddressFetched(address)
        }

    }


}
interface GeocodeCachingCallback{
    fun geocodeAddressFetched(address:GoogleGeocodeResponse?)
}