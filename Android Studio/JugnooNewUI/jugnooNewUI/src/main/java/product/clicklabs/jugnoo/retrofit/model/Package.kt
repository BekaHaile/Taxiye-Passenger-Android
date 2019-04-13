package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName


class Package {
    @SerializedName("package_id") var packageId: Int? = null
    @SerializedName("fare_fixed") var fareFixed: Int? = null
    @SerializedName("fare_per_km") var farePerKm: Int? = null
    @SerializedName("fare_threshold_distance") var fareThresholdDistance: Int? = null
    @SerializedName("fare_per_km_threshold_distance") var farePerKmThresholdDistance: Int? = null
    @SerializedName("fare_per_km_after_threshold") var farePerKmAfterThreshold: Int? = null
    @SerializedName("fare_per_km_before_threshold") var farePerKmBeforeThreshold: Int? = null
    @SerializedName("fare_per_min") var farePerMin: Int? = null
    @SerializedName("fare_threshold_time") var fareThresholdTime: Int? = null
    @SerializedName("fare_per_waiting_min") var farePerWaitingMin: Int? = null
    @SerializedName("fare_threshold_waiting_time") var fareThresholdWaitingTime: Int? = null
    @SerializedName("start_time") var startTime: String? = null
    @SerializedName("end_time") var endTime: String? = null
    @SerializedName("vehicle_type") var vehicleType: Int? = null
    @SerializedName("ride_type") var rideType: Int? = null
    @SerializedName("fare_minimum") var fareMinimum: Int? = null
    @SerializedName("operator_id") var operatorId: Int? = null
    @SerializedName("fare_per_baggage") var farePerBaggage: Int? = null
    @SerializedName("region_id") var regionId: Int? = null
}