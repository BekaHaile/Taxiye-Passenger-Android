package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FetchCorporatesResponse(
        @SerializedName("data") @Expose var corporates : List<Corporate>

): FeedCommonResponse()
class Corporate(
        @SerializedName("business_id") @Expose var businessId: Int,
        @SerializedName("partner_name") @Expose var partnerName: String,
        var selected:Boolean
)
