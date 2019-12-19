package product.clicklabs.jugnoo.home.models

import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.Constants

class HippoTicketModel(
        @SerializedName(Constants.KEY_USER_ID)
        var userId:Int?,
        @SerializedName(Constants.KEY_REG_AS)
        var regAs:Int?
)

class HippoTicketRideModel (
        @SerializedName(Constants.KEY_USER_ID)
        var userId:Int?,
        @SerializedName(Constants.KEY_REG_AS)
        var regAs:Int?,
        @SerializedName(Constants.KEY_DRIVER_ID)
        var driverId:Int?,
        @SerializedName(Constants.KEY_ENGAGEMENT_ID)
        var engagementId:Int?
)

