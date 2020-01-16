package product.clicklabs.jugnoo.retrofit.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class FilterActiveUsersResponse(
        @SerializedName("filtered_users") val filteredUsers:MutableList<FilteredUserDatum>?
) : FeedCommonResponse()

class FilteredUserDatum(
        @SerializedName("user_id") val userId:Long?,
        @SerializedName("user_phone_no") val userPhoneNo:String?,
        @SerializedName("user_image") var userImage:String?,
        @SerializedName("user_name") var userName:String?
){
    var isSelected = false
    var imageUri: Uri? = null
}