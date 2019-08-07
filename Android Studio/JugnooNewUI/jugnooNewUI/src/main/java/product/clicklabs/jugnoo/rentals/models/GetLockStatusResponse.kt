package product.clicklabs.jugnoo.rentals.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse

class GetLockStatusResponse (
    @SerializedName("gps_lock_status")
    @Expose
    var gpsLockStatus: Int
):FeedCommonResponse()
