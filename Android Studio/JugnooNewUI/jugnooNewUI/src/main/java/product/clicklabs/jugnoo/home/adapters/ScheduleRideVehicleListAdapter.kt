package product.clicklabs.jugnoo.home.adapters

import android.graphics.Typeface.BOLD
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_schedule_ride_vehicles.view.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.models.Region
import product.clicklabs.jugnoo.retrofit.model.Package
import product.clicklabs.jugnoo.retrofit.model.ServiceTypeValue
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils

class ScheduleRideVehicleListAdapter(val activity: HomeActivity, val vehicleList: ArrayList<Region>, val selectedCallback: OnSelectedCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemCount(): Int {
        return vehicleList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderVehicle(LayoutInflater.from(activity).inflate(R.layout.list_item_schedule_ride_vehicles, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolderVehicle -> holder.bind(position)
        }


    }

    inner class ViewHolderVehicle(view: View) : RecyclerView.ViewHolder(view) {

        init {
            (view.findViewById(R.id.tvVehicleName) as TextView).setTypeface(Fonts.mavenMedium(activity),BOLD)
            (view.findViewById(R.id.tvBaseFare) as TextView).typeface = Fonts.mavenRegular(activity)
            (view.findViewById(R.id.tvFarePerMinute) as TextView).typeface = Fonts.mavenRegular(activity)
            (view.findViewById(R.id.tvFarePerMile) as TextView).typeface = Fonts.mavenRegular(activity)
        }
        fun bind(position: Int) {
            for (i in vehicleList.indices) {
                if (activity.selectedIdForScheduleRide == vehicleList[position].regionId) {
                    itemView.ivSelected?.visibility = View.VISIBLE
                } else {
                    itemView.ivSelected?.visibility = View.GONE
                }
            }
            itemView.tvVehicleName?.text = vehicleList[position].regionName
            if(vehicleList[position].rideType == ServiceTypeValue.RENTAL.type
                    && vehicleList[position].packages != null
                    && vehicleList[position].packages.size > 0){
                var packageSelected = if (selectedCallback.getPackageSelected() != null) selectedCallback.getPackageSelected() else vehicleList[position].packages[0]
                var match = false
                for(pc in vehicleList[position].packages){
                    if(packageSelected!!.packageId == pc.packageId){
                        match = true
                        break
                    }
                }
                packageSelected = if (match) packageSelected else vehicleList[position].packages[0]

                itemView.tvBaseFare?.text = activity.getString(R.string.package_rate_format, " " + Utils.formatCurrencyValue(Data.autoData.currency,
                        packageSelected!!.fareFixed!!))
                itemView.tvFarePerMinute?.text = activity.getString(R.string.additional_per_min) + ": " + Utils.formatCurrencyValue(Data.autoData.currency, packageSelected.farePerMin!!, false)
                itemView.tvFarePerMile?.text = activity.getString(R.string.additional_per_km_fare, Utils.getDistanceUnit(Data.autoData.distanceUnit)) + ": " + Utils.formatCurrencyValue(Data.autoData.currency, packageSelected.farePerKmAfterThreshold!!, false)
            } else {
                itemView.tvBaseFare?.text = activity.getString(R.string.base_fare_format, " " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency,
                        vehicleList[position].fareStructure.getDisplayBaseFare(activity)))
                itemView.tvFarePerMinute?.text = activity.getString(R.string.nl_per_min) + ": " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency, vehicleList[position].fareStructure.farePerMin, false)
                itemView.tvFarePerMile?.text = activity.getString(R.string.per_format, Utils.getDistanceUnit(vehicleList[position].fareStructure.distanceUnit)) + ": " + Utils.formatCurrencyValue(vehicleList[position].fareStructure.currency, vehicleList[position].fareStructure.farePerKm, false)
            }

            Picasso.with(activity)
                    .load(vehicleList[position].images.tabHighlighted)
                    .into(itemView.ivVehicleImage)

            itemView.clRoot.tag = position
            itemView.clRoot.setOnClickListener {
                val pos:Int = it.tag as Int
                    vehicleList[pos].run {
                    activity.selectedIdForScheduleRide = regionId!!
                    activity.selectedRideTypeForScheduleRide = rideType!!
                    activity.selectedRegionForScheduleRide = this
                        selectedCallback.onItemSelected(this)
                    notifyDataSetChanged()
                }

            }
        }
    }


    interface OnSelectedCallback{
        fun onItemSelected(selectedRegion: Region)
        fun getPackageSelected(): Package?
    }
}

