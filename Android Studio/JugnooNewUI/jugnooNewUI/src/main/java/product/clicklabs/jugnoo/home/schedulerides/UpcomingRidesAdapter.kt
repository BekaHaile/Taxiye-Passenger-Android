package product.clicklabs.jugnoo.home.schedulerides

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.databinding.ListItemUpcomingRideBinding

/**
 * Created by Parminder Saini on 17/10/18.
 */
class UpcomingRidesAdapter(val context: Context,val presenter: UpcomingRidesPresenter) :
        ListAdapter<UpcomingRide, UpcomingRidesAdapter.ViewHolder>(UpcomingRideDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:ListItemUpcomingRideBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.list_item_upcoming_ride, parent, false)
        binding.presenter = presenter
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

    class ViewHolder(var binding: ListItemUpcomingRideBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(upcomingRide: UpcomingRide) {
            with(binding) {
                binding.upcomingRide = upcomingRide
                executePendingBindings()
            }
        }


    }
}

