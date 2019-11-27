package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.sabkuchfresh.utils.Utils
import kotlinx.android.synthetic.main.dialog_no_rides_found.view.tvLabel
import kotlinx.android.synthetic.main.dialog_no_rides_found_drivers.view.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.DriverInfo
import product.clicklabs.jugnoo.datastructure.PaymentOption
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.adapters.DriverListAdapter
import product.clicklabs.jugnoo.utils.Fonts
import android.os.Handler
import android.util.DisplayMetrics


class DriverCallDialog : DialogFragment() {
    private lateinit var rootView : View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(): DriverCallDialog {
            return DriverCallDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawableResource(R.color.black_translucent)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_no_rides_found_drivers, container, false)
        setFonts()
        setData()
        return rootView
    }

    private fun setFonts() {
        rootView.tvLabel.setTypeface(Fonts.mavenMedium(rootView.context), BOLD)
        rootView.tvListOfSomeDriversNearby.typeface = Fonts.mavenRegular(rootView.context)
    }

    /**
     *
     */
    private fun setData() {
        rootView.rvNearbyDrivers.layoutManager = LinearLayoutManager(activity)
        rootView.rvNearbyDrivers.isNestedScrollingEnabled = false
        val driverList : ArrayList<DriverInfo> = ArrayList()
        if(activity is HomeActivity) {
            val region = (activity as HomeActivity).slidingBottomPanel.requestRideOptionsFragment.regionSelected
            for (i in 0 until Data.autoData.driverInfos.size) {
                val driver = Data.autoData.driverInfos[i]
                if (driver.operatorId == region.operatorId && driver.regionIds.contains(region.regionId)
                        && (driver.paymentMethod == DriverInfo.PaymentMethod.BOTH.getOrdinal() || driver.paymentMethod == 0
                                || Data.autoData.pickupPaymentOption == PaymentOption.CASH.getOrdinal())) {
                    driver.setVehicleIconSet(region.vehicleIconSet.getName())
                    driverList.add(driver)
                }
            }
        }
        rootView.rvNearbyDrivers.adapter = DriverListAdapter(activity, driverList, object : DriverListAdapter.DriverContactListener {
            override fun onCallClicked(driverInfo: DriverInfo) {
                if(!driverInfo.phoneNumber.isNullOrEmpty()) {
                    Utils.openCallIntent(activity, driverInfo.phoneNumber)
                    dismiss()
                }
            }
        })
        rootView.btnCancel.setOnClickListener {
            (context as CallDriverListener).onCancelClicked()
            dismiss()
        }

        Handler().postDelayed(Runnable {
            try {
                val displayMetrics = DisplayMetrics()
                activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                rootView.programmaticScrollView.maxHeight = rootView.height - (rootView.height * (30 / 100.0f)) as Int
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 1000)
    }

    interface CallDriverListener {
        fun onCancelClicked()
    }
}