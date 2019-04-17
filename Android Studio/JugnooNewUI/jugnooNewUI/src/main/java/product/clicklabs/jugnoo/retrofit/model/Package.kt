package product.clicklabs.jugnoo.retrofit.model

import android.content.Context
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Utils


class Package {
    @SerializedName("package_id") var packageId: Int? = null
    @SerializedName("fare_fixed") var fareFixed: Double? = null
    @SerializedName("fare_per_km") var farePerKm: Double? = null
    @SerializedName("fare_threshold_distance") var fareThresholdDistance: Double? = null
    @SerializedName("fare_per_km_threshold_distance") var farePerKmThresholdDistance: Double? = null
    @SerializedName("fare_per_km_after_threshold") var farePerKmAfterThreshold: Double? = null
    @SerializedName("fare_per_km_before_threshold") var farePerKmBeforeThreshold: Double? = null
    @SerializedName("fare_per_min") var farePerMin: Double? = null
    @SerializedName("fare_threshold_time") var fareThresholdTime: Double? = null
    @SerializedName("fare_per_waiting_min") var farePerWaitingMin: Double? = null
    @SerializedName("fare_threshold_waiting_time") var fareThresholdWaitingTime: Double? = null
    @SerializedName("start_time") var startTime: String? = null
    @SerializedName("end_time") var endTime: String? = null
    @SerializedName("vehicle_type") var vehicleType: Int? = null
    @SerializedName("ride_type") var rideType: Int? = null
    @SerializedName("fare_minimum") var fareMinimum: Double? = null
    @SerializedName("operator_id") var operatorId: Int? = null
    @SerializedName("fare_per_baggage") var farePerBaggage: Double? = null
    @SerializedName("region_id") var regionId: Int? = null
    var selected:Boolean = false

    fun getPackageName(context: Context, currency:String, distanceUnit: String):String{
        return context.getString(R.string.fare_fixed) + " " + Utils.formatCurrencyValue(currency, fareFixed!!, false) +
                " " + context.getString(R.string.for_str) + " " + Utils.getDecimalFormat2Decimal().format(farePerKmThresholdDistance) + distanceUnit +
                " " + context.getString(R.string.then) + " " + Utils.formatCurrencyValue(currency, farePerKm!!, false) +
                context.getString(R.string.per_format, distanceUnit)
    }
}