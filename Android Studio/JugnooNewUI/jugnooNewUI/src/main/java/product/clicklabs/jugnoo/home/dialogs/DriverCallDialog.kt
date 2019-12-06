package product.clicklabs.jugnoo.home.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.sabkuchfresh.feed.models.FeedCommonResponse
import kotlinx.android.synthetic.main.dialog_no_rides_found_drivers.view.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.DriverInfo
import product.clicklabs.jugnoo.datastructure.PaymentOption
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.home.adapters.DriverListAdapter
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.RequestRideConfirm
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class DriverCallDialog : DialogFragment() {
    private lateinit var rootView : View
    private var isCallDriverVisible : Boolean = false
    private var isTipVisible : Boolean = false

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
        fun newInstance(requestRide : RequestRideConfirm): DriverCallDialog {
            val driverCallDialog = DriverCallDialog()
            val bundle = Bundle()
            bundle.putParcelable("requestRide", requestRide)
            driverCallDialog.arguments = bundle
            return driverCallDialog
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
        rootView.tvCallDriver.setTypeface(Fonts.mavenMedium(rootView.context), BOLD)
        rootView.tvTipMessage.setTypeface(Fonts.mavenMedium(rootView.context), BOLD)
        rootView.tvListOfSomeDriversNearby.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvFare.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvVehicleName.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvTotalFareLabel.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvTotalFare.typeface = Fonts.mavenMedium(rootView.context)
        rootView.tvTipLabel.typeface = Fonts.mavenRegular(rootView.context)
        rootView.etAdditionalFare.typeface = Fonts.mavenRegular(rootView.context)
    }

    /**
     *
     */
    private fun setData() {
        var requestRide : RequestRideConfirm? = null
        var addedTip = Data.autoData.noDriverFoundTip
        var isTotalInRange = false

        if(arguments != null) {
            requestRide  = arguments?.getParcelable("requestRide")!!
            rootView.tvVehicleName.text = requestRide.vehicleName


            if(requestRide.fare != 0.0) {
                isTotalInRange = false
                rootView.tvFare.text = product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide.currency, requestRide.fare)
            } else if(requestRide.fare == 0.0 && requestRide.minFare != 0.0 && requestRide.maxFare != 0.0) {
                isTotalInRange = true
                rootView.tvFare.text = product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide.currency, requestRide.minFare).plus(" - ")
                        .plus(product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide.currency, requestRide.maxFare))
            } else {
                isTotalInRange = false
                rootView.tvFare.visibility = View.GONE
                rootView.tvFare.visibility = View.GONE
            }

            setTotalFare(requestRide, addedTip, isTotalInRange)

            if(!requestRide.vehicleIcon.isNullOrEmpty()) {
                Glide.with(rootView.context).load(requestRide.vehicleIcon?.replace("http:", "https:"))
                        .into(rootView.ivVehicle)
            }
        }
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
        if(driverList.isNotEmpty()) {
            rootView.rvNearbyDrivers.visibility = View.VISIBLE
            rootView.tvNoDriverNearBy.visibility = View.GONE
            rootView.rvNearbyDrivers.adapter = DriverListAdapter(activity, driverList, object : DriverListAdapter.DriverContactListener {
                override fun onCallClicked(driverInfo: DriverInfo) {
                    if (!driverInfo.phoneNumber.isNullOrEmpty()) {
                        Utils.openCallIntent(activity, driverInfo.phoneNumber)
                        logDriverCall(driverInfo.userId)
                    }
                }
            })
        } else {
            rootView.rvNearbyDrivers.visibility = View.GONE
            rootView.tvNoDriverNearBy.visibility = View.VISIBLE
        }
        rootView.btnCancel.setOnClickListener {
            (context as CallDriverListener).onCancelClicked()
            dismiss()
        }

        rootView.btnOk.setOnClickListener {
            if(addedTip > 0.0) {
                Data.autoData.noDriverFoundTip = addedTip
                (context as CallDriverListener).onCallDriverOkClicked()
                dismiss()
            }
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

        rootView.groupCallDriver.visibility = View.VISIBLE
        rootView.groupTip.visibility = View.GONE
        isCallDriverVisible = true

        if(!requestRide?.showTip!!) {
            rootView.groupTip.visibility = View.GONE
            rootView.tvTipMessage.visibility = View.GONE
            rootView.btnOk.visibility = View.GONE
        }

        rootView.tvCallDriver.setOnClickListener {
            if(isCallDriverVisible) {
                rootView.groupCallDriver.visibility = View.GONE
                isCallDriverVisible = false
            } else {
                rootView.groupCallDriver.visibility = View.VISIBLE
                rootView.groupTip.visibility = View.GONE
                rootView.etAdditionalFare.clearFocus()
                hideKeyboard()
                isCallDriverVisible = true
                isTipVisible = false
            }
        }

        rootView.tvTipMessage.setOnClickListener {
            if(isTipVisible) {
                rootView.groupTip.visibility = View.GONE
                rootView.etAdditionalFare.clearFocus()
                hideKeyboard()
                isTipVisible = false
            } else {
                rootView.groupTip.visibility = View.VISIBLE
                rootView.groupCallDriver.visibility = View.GONE
                isCallDriverVisible = false
                isTipVisible = true
            }
        }

        rootView.etAdditionalFare.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty() && p0.toString() != ".") {
                    addedTip = p0.toString().toDouble()
                    setTotalFare(requestRide, addedTip, isTotalInRange)
                } else {
                    addedTip = 0.0
                    setTotalFare(requestRide, addedTip, isTotalInRange)
                }
            }

            var countBeforeChange: Int? = 0
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countBeforeChange = p0.toString().length
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty() && countBeforeChange == 0) {
                    rootView.etAdditionalFare.hint = null
                    if (rootView.etAdditionalFare.textDrawable == null) {
                        rootView.etAdditionalFare.setPrefix(Utils.getCurrencySymbol(requestRide?.currency))

                    } else {
                        rootView.etAdditionalFare.setCompoundDrawables(rootView.etAdditionalFare.textDrawable, null, null, null)
                    }
                } else if (p0.toString().isEmpty()) {
                    rootView.etAdditionalFare.setHint(R.string.hint_tip_amount)
                    rootView.etAdditionalFare.setCompoundDrawables(null, null, null, null)
                }
            }

        })

        if(addedTip > 0.0){
            rootView.etAdditionalFare.setText(addedTip.toInt().toString())
            rootView.etAdditionalFare.setPrefix(Utils.getCurrencySymbol(requestRide?.currency))
        }

    }

    private fun logDriverCall(userId: String) {
        if(Data.autoData != null) {
            val params = HashMap<String, String>()
            params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
            params[Constants.KEY_SESSION_ID] = Data.autoData.getcSessionId()
            params[Constants.KEY_DRIVER_ID] = userId
            HomeUtil.addDefaultParams(params)
            RestClient.getApiService().logDriverCall(params, object : Callback<FeedCommonResponse> {
                override fun success(t: FeedCommonResponse?, response: Response?) {

                }

                override fun failure(error: RetrofitError?) {

                }

            })
        }
    }

    private fun hideKeyboard() {
        val imm = rootView.etAdditionalFare.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) imm.hideSoftInputFromWindow(rootView.etAdditionalFare.windowToken, 0)
    }

    private fun setTotalFare(requestRide: RequestRideConfirm?, addedTip: Double, totalInRange: Boolean) {
        if (totalInRange) {
            rootView.tvTotalFare.text = product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.minFare!! + addedTip))
                    .plus(" - ")
                    .plus(product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide.currency, (requestRide.maxFare + addedTip)))
        } else {
            rootView.tvTotalFare.text = product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(requestRide?.currency, (requestRide?.fare!! + addedTip))
        }
    }

    interface CallDriverListener {
        fun onCallDriverOkClicked()
        fun onCancelClicked()
    }
}