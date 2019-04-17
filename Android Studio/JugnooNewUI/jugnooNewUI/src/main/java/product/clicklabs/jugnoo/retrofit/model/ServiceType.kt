package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName

class ServiceType(
        @SerializedName("name") var name:String,
        @SerializedName("images") var images:String,
        @SerializedName("info") var info:String,
        @SerializedName("is_visible") var isVisible:Int,
        @SerializedName("supported_ride_type") var supportedRideTypes:MutableList<Int>?,
        @SerializedName("tags") var tags:MutableList<String>?,
        @SerializedName("description") var description:String?,
        var selected:Boolean
){}

enum class ServiceTypeValue(
        val type:Int
){
    NORMAL(0), POOL(2), RENTAL(6), OUTSTATION(7)
}