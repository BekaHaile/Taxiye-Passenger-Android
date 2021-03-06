package product.clicklabs.jugnoo.home.schedulerides

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.databinding.ListItemUpcomingRideBinding
import product.clicklabs.jugnoo.utils.Fonts

/**
 * Created by Parminder Saini on 17/10/18.
 */
class UpcomingRidesAdapter(val context: Context,val presenter: UpcomingRidesPresenter) :
        ListAdapter<UpcomingRide, UpcomingRidesAdapter.ViewHolder>(UpcomingRideDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:ListItemUpcomingRideBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.list_item_upcoming_ride, parent, false)
        binding.presenter = presenter
        binding.tvVehicleType.typeface = Fonts.mavenRegular(context)
        binding.tvDate.typeface = Fonts.mavenRegular(context)
        binding.tvDropAddress.typeface = Fonts.mavenRegular(context)
        binding.tvPickupAddress.typeface = Fonts.mavenRegular(context)
        binding.tvRideStatus.typeface = Fonts.mavenRegular(context)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { upcomingRide ->
            with(holder) {
                itemView.tag = upcomingRide
                bind(upcomingRide)
            }

        }
    }



    class ViewHolder(var binding: ListItemUpcomingRideBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(upcomingRide: UpcomingRide) {
            with(binding) {
                binding.upcomingRide = upcomingRide
                executePendingBindings()
            }
        }


    }
}

