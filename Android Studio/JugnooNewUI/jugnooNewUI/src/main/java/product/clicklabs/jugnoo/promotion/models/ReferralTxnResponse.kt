package product.clicklabs.jugnoo.promotion.models

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class ReferralTxnResponse (
        @SerializedName("data")
        var referralTxns: MutableList<ReferralTxn>?
) : FeedCommonResponse()