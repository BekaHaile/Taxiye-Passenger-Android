package product.clicklabs.jugnoo.home.schedulerides

/**
 * Created by Parminder Saini on 17/10/18.
 */
data class UpcomingRide(var engagementId:String,
                   var pickUpAddress:String,
                   var destinationAddress:String,
                   val pickUpTime:String)