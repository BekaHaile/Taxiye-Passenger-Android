package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FareDetailsResponse (
    @SerializedName("data") @Expose var regions: List<Region>,
    @SerializedName("currency") @Expose var currency: String,
    @SerializedName("distance_unit") @Expose var distanceUnit: String,
    @SerializedName("rate_card_info") @Expose var rateCardInfo: String
) : FeedCommonResponse()

class Fare(
        @SerializedName("fare_fixed") @Expose var fareFixed: Double,
        @SerializedName("fare_per_km") @Expose var farePerKm: Double,
        @SerializedName("fare_per_min") @Expose var farePerMin: Double,
        @SerializedName("fare_per_waiting_min") @Expose var farePerWaitingMin: Double
)

class Region(
        @SerializedName("region_name") @Expose var regionName: String,
        @SerializedName("max_people") @Expose var maxPeople: Int,
        @SerializedName("fares") @Expose var fare: Fare
)
