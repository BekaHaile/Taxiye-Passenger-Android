package product.clicklabs.jugnoo.home.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.Constants

class SafetyInfoData (
        @SerializedName("list")
        @Expose
        var list:ArrayList<SafetyPoint>?,
        @SerializedName("title")
        @Expose
        var title:String?,
        @SerializedName(Constants.KEY_IMAGE_URL)
        @Expose
        var image:String?,
        @SerializedName(Constants.KEY_IMAGE_URL_STRIP)
        @Expose
        var imageUrlStrip:String?
){
}
class SafetyPoint(
        @SerializedName("is_mandatory")
        @Expose
        var isMandatory:Int?,
        @SerializedName("text")
        @Expose
        var text:String?
)