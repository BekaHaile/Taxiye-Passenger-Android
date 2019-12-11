package product.clicklabs.jugnoo.home.schedulerides

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Parminder Saini on 17/10/18.
 */
object UpcomingRideDiffCallback: DiffUtil.ItemCallback<UpcomingRide>(){

    override fun areItemsTheSame(oldItem: UpcomingRide, newItem: UpcomingRide): Boolean {
        return oldItem.engagementId == newItem.engagementId
    }

    override fun areContentsTheSame(oldItem: UpcomingRide, newItem: UpcomingRide): Boolean {
       return oldItem ==newItem
    }

}