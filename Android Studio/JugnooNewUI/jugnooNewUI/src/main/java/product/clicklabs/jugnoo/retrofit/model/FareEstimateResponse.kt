package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FareEstimateResponse (
        @SerializedName("oldFare")
        var oldFare:Double?,
        @SerializedName("newFare")
        var newFare:Double?,
        @SerializedName("pool_fare_id")
        var poolFareId:Int?
):FeedCommonResponse()