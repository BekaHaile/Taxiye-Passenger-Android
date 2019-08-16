package product.clicklabs.jugnoo.datastructure

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class PagerInfo(
        @SerializedName("title")
        var title:String?,
        @SerializedName("description")
        var message:String?,
        @SerializedName("image_url")
        var image:String?)

class TutorialDataResponse(
        @SerializedName("data")
        var tutorialsData:MutableList<PagerInfo>

) : FeedCommonResponse()