package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName

class ServiceType(
        @SerializedName("ride_type") val rideType:Int,
        @SerializedName("name") val name:String,
        @SerializedName("images") val images:String,
        @SerializedName("info") val info:String,
        @SerializedName("is_visible") val isVisible:Int,
        var selected:Boolean
){}

enum class ServiceTypeValue(
        val type:Int
){
    NORMAL(0), POOL(2), RENTAL(6), OUTSTATION(7)
}