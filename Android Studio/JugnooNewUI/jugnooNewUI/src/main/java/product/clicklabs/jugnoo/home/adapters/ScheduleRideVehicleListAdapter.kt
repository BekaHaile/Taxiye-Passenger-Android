package product.clicklabs.jugnoo.home.adapters

import android.graphics.Typeface.BOLD
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_fare_detail_vehicle.view.*
import kotlinx.android.synthetic.main.list_item_schedule_ride_vehicles.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.models.Region
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils

class ScheduleRideVehicleListAdapter(val activity: HomeActivity, val vehicleList: ArrayList<Region>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return vehicleList.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderVehicle(LayoutInflater.from(activity).inflate(R.layout.list_item_schedule_ride_vehicles, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // holder?.tvAnimalType?.text = items.get(position)

        when (holder) {
            is ViewHolderVehicle -> holder.bind(position)
        }


    }

    inner class ViewHolderVehicle(view: View) : RecyclerView.ViewHolder(view) {

        init {
            (view.findViewById(R.id.tvVehicleName) as TextView).setTypeface(Fonts.mavenRegular(activity),BOLD)
            (view.findViewById(R.id.tvBaseFare) as TextView).typeface = Fonts.mavenRegular(activity)
            (view.findViewById(R.id.tvFarePerMinute) as TextView).typeface = Fonts.mavenRegular(activity)
            (view.findViewById(R.id.tvFarePerMile) as TextView).typeface = Fonts.mavenRegular(activity)
        }
        fun bind(position: Int) {
            //   itemView.tvHeader.text = vehicleName

            for (i in vehicleList.indices) {
                if (activity.selectedIdForScheduleRide == vehicleList[position].regionId) {
                    itemView.ivSelected?.visibility = View.VISIBLE
                } else {
                    itemView.ivSelected?.visibility = View.GONE
                }
            }
            itemView.tvVehicleName?.text = vehicleList[position].regionName
            itemView.tvBaseFare?.text = activity.getString(R.string.base_fare_format, " " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency, vehicleList[position].fareStructure.getDisplayBaseFare(activity)))
            itemView.tvFarePerMinute?.text = "Per Min: " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency, vehicleList[position].fareStructure.farePerMin, false)
            itemView.tvFarePerMile?.text = activity.getString(R.string.per_format, Utils.getDistanceUnit(vehicleList[position].fareStructure.distanceUnit)) + ": " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency, vehicleList[position].fareStructure.farePerKm, false)

            Picasso.with(activity)
                    .load(vehicleList[position].images.rideNowNormal)
                    .into(itemView.ivVehicleImage)
            itemView.clRoot.setOnClickListener {
                activity.selectedIdForScheduleRide = vehicleList[position].regionId!!
                activity.selectedRideTypeForScheduleRide = vehicleList[position].rideType!!
                activity.selectedRegionForScheduleRide = vehicleList[position]
                notifyDataSetChanged()
            }
        }
    }
}

//
//class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
//    // Holds the TextView that will add each animal to
//    val tvVehicleName= view.tvVehicleName
//    val tvBaseFare=view.tvBaseFare
//    val tvFarePerMinute=view.tvFarePerMinute
//    val tvFarePerMile= view.tvFarePerMile
//    val ivVehicleImage=view.ivVehicleImage
//    val ivSelected=view.ivSelected
//    val clRoot=view.clRoot
class VehicleItem(var position: Int)
//}