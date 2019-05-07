package product.clicklabs.jugnoo.datastructure

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

class LatLngCoordinates(
        @SerializedName("latitude") val latitude:Double,
        @SerializedName("longitude") val longitude:Double
){

    fun getLatLng() : LatLng{
        return LatLng(latitude, longitude)
    }

}