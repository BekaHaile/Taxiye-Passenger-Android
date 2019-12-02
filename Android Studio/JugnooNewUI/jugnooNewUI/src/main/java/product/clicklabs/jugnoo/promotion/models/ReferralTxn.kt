package product.clicklabs.jugnoo.promotion.models

import com.google.gson.annotations.SerializedName

class ReferralTxn (
        @SerializedName("date")
        var date:String?,
        @SerializedName("amount")
        var amount:Double?,
        @SerializedName("message")
        var message:String?,
        @SerializedName("type")
        var type:Int?
)