package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName

class ReferralData(
        @SerializedName("referrals_count")
        var referralsCount:Int?,
        @SerializedName("earned_total")
        var earnedTotal:Double?,
        @SerializedName("earned_today")
        var earnedToday:Int?,
        @SerializedName("referral_info")
        var referralInfo:String?,
        @SerializedName("referral_details")
        var referralDetails:String?,
        @SerializedName("images")
        var imagesList: MutableList<MediaInfo>?

){}

class MediaInfo(
        @SerializedName("is_video")
        var isVideo:Int?,
        @SerializedName("url")
        var url:String?

) {}