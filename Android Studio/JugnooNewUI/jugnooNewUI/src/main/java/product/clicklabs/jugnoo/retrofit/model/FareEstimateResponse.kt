package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FareEstimateResponse (
        @SerializedName("old_fare")
        var oldFare:Double?,
        @SerializedName("fare")
        var fare:Double?
):FeedCommonResponse()