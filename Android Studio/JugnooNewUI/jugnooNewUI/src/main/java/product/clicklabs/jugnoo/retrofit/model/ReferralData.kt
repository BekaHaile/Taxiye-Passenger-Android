package product.clicklabs.jugnoo.retrofit.model

import android.net.Uri
import android.text.TextUtils
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

) {

    fun getFileName():String{
        return if(url != null) {
            val arr = url!!.split("/")
            arr[arr.size-1].split(".")[0]
        } else {
            ""
        }
    }

    fun checkIsYoutubeVideo():Boolean{
        return if(url != null) {
            url!!.contains("https://www.youtube.com/watch?v=") || url!!.contains("https://youtu.be/")
        } else false
    }

    fun getYoutubeId():String?{
        val uri = Uri.parse(url!!)
        var videoId = uri.getQueryParameter("v")
        if(TextUtils.isEmpty(videoId)){
            val arr = url!!.split("/")
            videoId = arr[arr.size-1]
        }
        return videoId
    }

}