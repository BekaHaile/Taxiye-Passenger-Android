package product.clicklabs.jugnoo.home.dialogs


import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_vehicle_fare_estimate.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.models.Region
import product.clicklabs.jugnoo.home.models.RideTypeValue
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Prefs
import product.clicklabs.jugnoo.utils.SelectorBitmapLoader
import product.clicklabs.jugnoo.utils.Utils

/**
 * Created by shankar on 5/2/16.
 */
class VehicleFareEstimateDialog {

    fun show(activity: Activity, region : Region) {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.setContentView(R.layout.dialog_vehicle_fare_estimate)
            with(dialog){
                val layoutParams = window!!.attributes
                layoutParams.dimAmount = 0.6f
                window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCancelable(true)
                setCanceledOnTouchOutside(true)

                tvVehicle.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD)
                tvEstimate.typeface = Fonts.mavenMedium(activity)
                tvInfo.typeface = Fonts.mavenMedium(activity)
                tvFareDetails.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD)
                tvBaseFare.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD)
                tvBaseFareValue.typeface = Fonts.mavenMedium(activity)
                tvPerKm.typeface = Fonts.mavenMedium(activity)
                tvPerKmValue.typeface = Fonts.mavenMedium(activity)
                tvPerMin.typeface = Fonts.mavenMedium(activity)
                tvPerMinValue.typeface = Fonts.mavenMedium(activity)
                textViewThreshold.typeface = Fonts.mavenMedium(activity)
                textViewPoolMessage.typeface = Fonts.mavenMedium(activity)
                imageViewClose.setOnClickListener { dialog.dismiss() }
                relative.setOnClickListener { dialog.dismiss() }
                relative.setOnClickListener { dialog.dismiss() }

                SelectorBitmapLoader(activity)
                        .loadSelector(ivVehicleType, region.images.rideNowNormal, region.images.rideNowHighlighted, {}, true)

                tvVehicle.text = region.regionName
                tvEstimate.text = region.regionFare.getFareText(region.fareMandatory)
                tvInfo.text = Prefs.with(activity).getString(Constants.KEY_CUSTOMER_VEHICLE_FARE_ESTIMATE_ALERT,
                        activity.getString(R.string.vehicle_fare_estimate_alert))
                tvBaseFareValue.text = Utils.fromHtml(region.fareStructure.getDisplayBaseFare(activity))
                tvPerKmValue.text = Utils.formatCurrencyValue(region.fareStructure.currency,
                        region.fareStructure.farePerKm, false)
                tvPerMinValue.text = Utils.formatCurrencyValue(region.fareStructure.currency, region.fareStructure.farePerMin, false)
                tvPerKm.text = activity.getString(R.string.per_format, Utils.getDistanceUnit(region.fareStructure.distanceUnit))

                if (Data.autoData !=  null && region.rideType == RideTypeValue.POOL.getOrdinal()
                        && !"".equals(Data.autoData.baseFarePoolText, ignoreCase = true)) {
                    textViewPoolMessage.visibility = View.VISIBLE
                    textViewPoolMessage.text = Data.autoData.baseFarePoolText
                } else {
                    textViewPoolMessage.visibility = View.GONE
                }
                if (!"".equals(region.fareStructure.getDisplayFareText(activity), ignoreCase = true)) {
                    textViewThreshold.visibility = View.VISIBLE
                    textViewThreshold.text = region.fareStructure.getDisplayFareText(activity)
                } else {
                    textViewThreshold.visibility = View.GONE
                }

            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}