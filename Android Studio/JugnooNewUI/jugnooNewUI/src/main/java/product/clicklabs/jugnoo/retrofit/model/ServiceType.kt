package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName

class ServiceType(
        @SerializedName("name") var name:String,
        @SerializedName("images") var images:String,
        @SerializedName("info") var info:String,
        @SerializedName("is_visible") var isVisible:Int,
        @SerializedName("supported_ride_type") var supportedRideTypes:MutableList<Int>?,
        @SerializedName("supported_vehicle_types") var supportedVehicleTypes:MutableList<Int>?,
        @SerializedName("tags") var tags:MutableList<String>?,
        @SerializedName("description") var description:String?,
        @SerializedName("schedule_available") var scheduleAvailable:Int = 0,
        var selected:Boolean
){}

enum class ServiceTypeValue(
        val type:Int
){
    NORMAL(0), POOL(2), RENTAL(6), OUTSTATION(7)
}