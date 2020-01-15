package product.clicklabs.jugnoo.home.adapters

import android.app.Activity
import android.graphics.Typeface.BOLD
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_driver_contact.view.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.DriverInfo
import product.clicklabs.jugnoo.utils.ASSL
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.MapUtils
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class DriverListAdapter(val activity: Activity?, val driverList: ArrayList<DriverInfo>, val driverContactListener: DriverContactListener) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    override fun getItemCount(): Int {
        return driverList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolderVehicle(LayoutInflater.from(activity).inflate(R.layout.list_item_driver_contact, parent, false))
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolderVehicle -> holder.bind(position)
        }


    }

    inner class ViewHolderVehicle(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            (view.findViewById(R.id.tvDriverName) as TextView).setTypeface(Fonts.mavenMedium(activity),BOLD)
        }

        fun bind(position: Int) {
            val driver = driverList[position]
            val decimalFormat = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH))
            val distance = (MapUtils.distance(Data.autoData.pickupLatLng, driver.latLng) * 1.4) / 1000
            itemView.tvDriverName.text = driver.name.plus("\n").plus(activity?.getString(R.string.distance_km_away_format, decimalFormat.format(distance)))
            val minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale())
            if(!driver.image.isNullOrEmpty()) {
                Picasso.with(activity).load(driver.image)
                        .placeholder(ContextCompat.getDrawable(itemView.ivDriverImage.context, R.drawable.ic_profile_img_placeholder))
                        .transform(CircleTransform())
                        .error(ContextCompat.getDrawable(itemView.ivDriverImage.context, R.drawable.ic_profile_img_placeholder)!!)
                        .resize((160f * minRatio).toInt(), (160f * minRatio).toInt()).centerCrop()
                        .into(itemView.ivDriverImage)
            } else {
                itemView.ivDriverImage.setImageResource(R.drawable.ic_profile_img_placeholder)
            }
            itemView.ivCallDriver.setOnClickListener {
                driverContactListener.onCallClicked(driverList[position])
            }
        }
    }


    interface DriverContactListener{
        fun onCallClicked(driverInfo: DriverInfo)
    }
}

