package product.clicklabs.jugnoo.home.schedulerides

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sabkuchfresh.feed.models.FeedCommonResponse
import product.clicklabs.jugnoo.Data.context
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.DateOperations

/**
 * Created by Parminder Saini on 17/10/18.
 */
data class UpcomingRide(@Expose @SerializedName("pickup_id") var engagementId:String,
                        @Expose @SerializedName("pickup_location_address")  var pickUpAddress:String,
                        @Expose @SerializedName("drop_location_address")  var destinationAddress:String,
                        @Expose @SerializedName("vehicle_name")  var vehicleName: String,
                        @Expose @SerializedName("pickup_time")  val pickUpTime:String?,
                        @Expose @SerializedName("modifiable")  val isModifiable:Int,
                        @Expose @SerializedName("status")  val status:Int=3){

       fun getRideStatus(): String {
              return when (status) {
                     0 -> MyApplication.getInstance().getmActivity().resources.getString(R.string.ride_scheduled_status)
                     1 -> MyApplication.getInstance().getmActivity().resources.getString(R.string.in_progress_status)
                     2 -> MyApplication.getInstance().getmActivity().resources.getString(R.string.processed_status)
                     else -> MyApplication.getInstance().getmActivity().resources.getString(R.string.cancelled_status)
              }
       }

       fun getDisplayTime():String{
            return  pickUpTime?.run {
                DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(this))
              }?:""
       }

       fun isEditable():Boolean{
           return  isModifiable==1
       }
}


class UpcomingRideResponse(@Expose @SerializedName("data") var list: MutableList<UpcomingRide>)
       :FeedCommonResponse()



