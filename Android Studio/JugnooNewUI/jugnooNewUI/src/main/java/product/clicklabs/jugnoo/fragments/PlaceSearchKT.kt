package product.clicklabs.jugnoo.fragments

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.apis.CachedApis

object PlaceSearchKT {


    fun hitGeocode(latLng: LatLng, callback: PlaceSearchCallback){

        GlobalScope.launch(Dispatchers.Main) {
            val address = CachedApis.geocode(latLng, Data.userData.userId)
            callback.geocodeAddressFetched(address)
        }

    }


}
interface PlaceSearchCallback{
    fun geocodeAddressFetched(address:String?)
}