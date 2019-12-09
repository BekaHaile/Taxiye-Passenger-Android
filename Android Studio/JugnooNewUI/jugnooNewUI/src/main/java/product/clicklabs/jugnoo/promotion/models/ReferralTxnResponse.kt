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
        var referrals: MutableList<ReferralUser>?,
        @SerializedName("referral_count")
        var referralsCount: Double? = 0.0,
        @SerializedName("referral_earned_total")
        var referralEarnedTotal: Double? = 0.0,
        @SerializedName("referral_earned_today")
        var referralEarnedToday: Double? = 0.0
)