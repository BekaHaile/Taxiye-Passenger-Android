package product.clicklabs.jugnoo.promotion.models

import com.google.gson.annotations.SerializedName

class ReferralTxn (
        @SerializedName("referral_id")
        var referralId:Int?,
        @SerializedName("amount")
        var amount:Double?,
        @SerializedName("credited_on")
        var creditedOn:String?,
        @SerializedName("user_name")
        var userName:String?,
        @SerializedName("text")
        var text:String?,
        @SerializedName("is_today")
        var isToday:Int?
)
class ReferralUser (
        @SerializedName("referral_id")
        var referralId:Int?,
        @SerializedName("amount")
        var amount:Double?,
        @SerializedName("referred_on")
        var referredOn:String?,
        @SerializedName("user_name")
        var userName:String?
)