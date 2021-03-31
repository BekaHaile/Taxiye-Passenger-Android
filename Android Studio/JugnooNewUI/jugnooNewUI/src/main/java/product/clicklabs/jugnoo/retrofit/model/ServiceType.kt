package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName

class ServiceType(
        @SerializedName("name") var name: String,
        @SerializedName("images") var images: String,
        @SerializedName("info") var info: String,
        @SerializedName("is_visible") var isVisible: Int,
        @SerializedName("supported_ride_type") var supportedRideTypes: MutableList<Int>?,
        @SerializedName("supported_vehicle_type") var supportedVehicleTypes: MutableList<Int>?,
        @SerializedName("tags") var tags: MutableList<String>?,
        @SerializedName("description") var description: String?,
        @SerializedName("schedule_available") var scheduleAvailable: Int = 0,
        var selected: Boolean
){

    fun isOnDemand():Boolean{
        return supportedRideTypes?.run{
            contains(ServiceTypeValue.NORMAL.type) || contains(ServiceTypeValue.POOL.type)
        } ?: false
    }
    fun isRentalOrOutstation():Boolean{
        return supportedRideTypes?.run{
            contains(ServiceTypeValue.RENTAL.type) || contains(ServiceTypeValue.OUTSTATION.type)
        } ?: false
    }
    fun isRental():Boolean{
        return supportedRideTypes?.run{
            contains(ServiceTypeValue.RENTAL.type)
        } ?: false
    }
    fun isOutStation():Boolean{
        return supportedRideTypes?.run{
            contains(ServiceTypeValue.OUTSTATION.type)
        } ?: false
    }

}

enum class ServiceTypeValue(
        val type: Int
){
    NORMAL(0), POOL(2), BIKE_RENTAL(5), RENTAL(6), OUTSTATION(7)
}