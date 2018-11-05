package product.clicklabs.jugnoo.home.schedulerides

import com.fugu.utils.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse
import product.clicklabs.jugnoo.utils.DateOperations

/**
 * Created by Parminder Saini on 17/10/18.
 */
data class UpcomingRide(@Expose @SerializedName("pickup_id") var engagementId:String,
                        @Expose @SerializedName("pickup_location_address")  var pickUpAddress:String,
                        @Expose @SerializedName("drop_location_address")  var destinationAddress:String,
                        @Expose @SerializedName("vehicle_name")  var vehicleName: String,
                        @Expose @SerializedName("pickup_time")  val pickUpTime:String?,
                        @Expose @SerializedName("status")  val status:Int=3){

       fun getRideStatus(): String {
              return when (status) {
                     0 -> "Ride Scheduled"
                     1 -> "In Progress"
                     2 -> "Processed"
                     else -> "Cancelled"
              }
       }

       fun getDisplayTime():String{
            return  pickUpTime?.run {
                     DateOperations.convertDateViaFormatTZ(this)
              }?:""
       }
}


class UpcomingRideResponse(@Expose @SerializedName("data") var list: MutableList<UpcomingRide>)
       :FeedCommonResponse()



