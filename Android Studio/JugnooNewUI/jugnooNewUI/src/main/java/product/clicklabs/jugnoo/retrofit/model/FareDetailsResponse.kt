package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

data class FareDetailsResponse (
    @SerializedName("regions") @Expose val regions: List<Region>,
    @SerializedName("currency") @Expose val currency: String,
    @SerializedName("distance_unit") @Expose val distanceUnit: String
) : FeedCommonResponse()

data class Fare(
        @SerializedName("fare_fixed") @Expose var fareFixed: Double,
        @SerializedName("fare_per_km") @Expose var farePerKm: Double,
        @SerializedName("fare_per_min") @Expose var farePerMin: Double,
        @SerializedName("display_base_fare") @Expose var displayBaseFare: String,
        @SerializedName("display_fare_text") @Expose var displayFareText: String
)

data class Region(
        @SerializedName("region_name") @Expose var regionName: String,
        @SerializedName("max_people") @Expose var maxPeople: Int,
        @SerializedName("fare") @Expose var fare: Fare
)
