package product.clicklabs.jugnoo.promotion.models

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class ReferralTxnResponse (
        @SerializedName("data")
        var referralData: ReferralTxnData?
) : FeedCommonResponse()

class ReferralTxnData(
        @SerializedName("txns")
        var txns: MutableList<ReferralTxn>?,
        @SerializedName("referrals")
        var referrals: MutableList<ReferralUser>?
)