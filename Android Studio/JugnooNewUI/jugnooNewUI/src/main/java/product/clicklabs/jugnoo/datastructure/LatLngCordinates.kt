package product.clicklabs.jugnoo.datastructure

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LatLngCoordinates(
        @Expose @SerializedName("latitude") var latitude:Double,
        @Expose @SerializedName("longitude") var longitude:Double
){

    fun getLatLng() : LatLng{
        return LatLng(latitude, longitude)
    }

}