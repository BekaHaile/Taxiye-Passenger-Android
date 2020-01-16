package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FilterActiveUsersResponse(
        @SerializedName("filtered_users") val filteredUsers:MutableList<UserDatum>
) : FeedCommonResponse()

class UserDatum(
        @SerializedName("user_id") val userId:Long,
        @SerializedName("user_phone_no") val userPhoneNo:String,
        @SerializedName("user_image") val userImage:String,
        @SerializedName("user_name") val userName:String
){
    var isSelected = false
}