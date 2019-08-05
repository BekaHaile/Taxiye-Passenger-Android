package product.clicklabs.jugnoo.retrofit.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.utils.Utils
import kotlin.math.roundToInt


class Package {
    @SerializedName("package_id")
    var packageId: Int? = null
    @SerializedName("fare_fixed")
    var fareFixed: Double? = null
    @SerializedName("fare_per_km")
    var farePerKm: Double? = null
    @SerializedName("fare_threshold_distance")
    var fareThresholdDistance: Double? = null
    @SerializedName("fare_per_km_threshold_distance")
    var farePerKmThresholdDistance: Double? = null
    @SerializedName("fare_per_km_after_threshold")
    var farePerKmAfterThreshold: Double? = null
    @SerializedName("fare_per_km_before_threshold")
    var farePerKmBeforeThreshold: Double? = null
    @SerializedName("fare_per_min")
    var farePerMin: Double? = null
    @SerializedName("fare_threshold_time")
    var fareThresholdTime: Double? = null
    @SerializedName("fare_per_waiting_min")
    var farePerWaitingMin: Double? = null
    @SerializedName("fare_threshold_waiting_time")
    var fareThresholdWaitingTime: Double? = null
    @SerializedName("package_name")
    var packageName: String? = null
    @SerializedName("start_time")
    var startTime: String? = null
    @SerializedName("end_time")
    var endTime: String? = null
    @SerializedName("vehicle_type")
    var vehicleType: Int? = null
    @SerializedName("ride_type")
    var rideType: Int? = null
    @SerializedName("fare_minimum")
    var fareMinimum: Double? = null
    @SerializedName("operator_id")
    var operatorId: Int? = null
    @SerializedName("fare_per_baggage")
    var farePerBaggage: Double? = null
    @SerializedName("region_id")
    var regionId: Int? = null
    @SerializedName("city_name")
    var cityName: String? = null
    @SerializedName("city_id")
    var cityId: Int? = null
    @SerializedName("return_trip")
    var returnTrip: Int? = null
    var selected: Boolean = false

    //NOTE: remember to initialize any new member variables in ScheduleRideFragment one way and round trip functions also

    fun getPackageName(distanceUnit: String?): String {
        if(!TextUtils.isEmpty(packageName)){
            return packageName!!;
        }
        val fareThrTime: String
        if (fareThresholdTime!!.roundToInt() >= 60) {
            if (fareThresholdTime!!.roundToInt() == 60) {
                fareThrTime = "1 hr"
            } else {
                fareThrTime= (fareThresholdTime!!.roundToInt()/60).toString() +" hrs"
            }
        } else {
            if (fareThresholdTime!!.roundToInt() <= 1) {
                fareThrTime = (fareThresholdTime!!.roundToInt()).toString()+" min"
            }
            else{
                fareThrTime = (fareThresholdTime!!.roundToInt()).toString()+" mins"
            }
        }

        return fareThrTime +" "+ fareThresholdDistance!!.roundToInt() +" "+ Utils.getDistanceUnit(distanceUnit)
    }
    fun getCity(): String? {
        return cityName
    }
}

