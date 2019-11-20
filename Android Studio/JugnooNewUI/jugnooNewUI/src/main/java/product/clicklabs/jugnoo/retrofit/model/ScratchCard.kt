package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class ScratchCard(
        @SerializedName("cashback_success_message") var cashbackSuccessMessage:String,
        @SerializedName("zilch") var zilch : Int
) : FeedCommonResponse()