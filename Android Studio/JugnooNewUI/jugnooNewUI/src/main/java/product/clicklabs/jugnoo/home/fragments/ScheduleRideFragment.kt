package product.clicklabs.jugnoo.home.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.analytics.GACategory
import com.sabkuchfresh.analytics.GAUtils
import com.sabkuchfresh.pros.utils.DatePickerFragment
import com.sabkuchfresh.pros.utils.TimePickerFragment
import com.sabkuchfresh.utils.Utils
import kotlinx.android.synthetic.main.fragment_schedule_ride.*
import product.clicklabs.jugnoo.*
import product.clicklabs.jugnoo.datastructure.SearchResult
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.home.adapters.ScheduleRideVehicleListAdapter
import product.clicklabs.jugnoo.home.dialogs.PaymentOptionDialog
import product.clicklabs.jugnoo.home.models.Region
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.Fonts
import java.util.*


class ScheduleRideFragment : Fragment(), Constants {

    private val TAG = ScheduleRideFragment::class.java.simpleName


    private var rootView: View? = null
    private var datePickerFragment: DatePickerFragment? = null
    private var timePickerFragment: TimePickerFragment? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private val scheduleRideVehicleListAdapter by lazy{ ScheduleRideVehicleListAdapter(getActivity() as HomeActivity, Data.autoData.regions) }
    internal var searchResultPickup: SearchResult? = null
    internal var searchResultDestination: SearchResult? = null

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> setTimeToVars(hourOfDay.toString() + ":" + minute + ":00") }
    private val onDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
        if (validateDateTime(date, null)) {
            selectedDate = date
            //                tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
            getTimePickerFragment().show(childFragmentManager, "timePicker", onTimeSetListener)

        } else {
            Utils.showToast(activity, activity!!.getString(R.string.incorrect_schedule_time,MIN_BUFFER_TIME_MINS))
        }
    }

    private var interactionListener: InteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            interactionListener = context
            interactionListener!!.onAttachScheduleRide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (interactionListener != null) {
            interactionListener!!.onDestroyScheduleRide()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_schedule_ride, container, false)



        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(rootView) {

            rvVehiclesList.layoutManager = LinearLayoutManager(activity)
            rvVehiclesList.isNestedScrollingEnabled = false
            tvPickup.typeface = Fonts.mavenRegular(activity)
            tvDestination.typeface = Fonts.mavenRegular(activity)
            tvPickupDateTime.setTypeface(Fonts.mavenRegular(activity),BOLD)
            tvSelectDateTime.typeface = Fonts.mavenMedium(activity)
            btSchedule.typeface = Fonts.mavenRegular(activity)
            tvSelectPayment.setTypeface(Fonts.mavenRegular(activity),BOLD)
            textViewPaymentModeValueConfirm.typeface = Fonts.mavenRegular(activity)

            tvPickup.setOnClickListener { (getActivity() as HomeActivity).openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.PICKUP) }
            tvDestination.setOnClickListener { (getActivity() as HomeActivity).openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.DROP) }
            tvSelectDateTime.setOnClickListener {
                try {
                    getDatePickerFragment().show(childFragmentManager, "datePicker", onDateSetListener)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            llPaymentOption.setOnClickListener {
                getPaymentOptionDialog()?.show()
            }
            btSchedule.setOnClickListener {
                try {

                    if (TextUtils.isEmpty(tvPickup.text.toString())) {
                        Utils.showToast(activity, activity!!.getString(R.string.enter_pickup))
                        throw Exception()
                    } else if (TextUtils.isEmpty(tvDestination.text.toString())) {
                        Utils.showToast(activity, activity!!.getString(R.string.enter_destination))
                        throw Exception()
                    } else if (TextUtils.isEmpty(selectedDate)) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_date))
                        throw Exception()
                    } else if (TextUtils.isEmpty(selectedTime)) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_time))
                        throw Exception()
                    } else if (!TextUtils.isEmpty(Data.autoData.getFarAwayCity())) {
                        Utils.showToast(activity, Data.autoData.getFarAwayCity())
                        throw Exception()
                    } else if ((activity as HomeActivity).selectedIdForScheduleRide <= 0) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_vehicle))
                        throw Exception()
                    } else {
                        val proceed = (activity as HomeActivity).slidingBottomPanel.getRequestRideOptionsFragment()
                                .displayAlertAndCheckForSelectedWalletCoupon()
                        if (proceed) {
                            val callRequestRide = MyApplication.getInstance().walletCore
                                    .requestWalletBalanceCheck((activity as HomeActivity), Data.autoData.pickupPaymentOption)
                            MyApplication.getInstance().walletCore.
                                    requestRideWalletSelectedFlurryEvent(Data.autoData.pickupPaymentOption, TAG)

                            if(callRequestRide){
                                openFareEstimate()
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
            setSelectedRegionData()
            setScheduleRideVehicleListAdapter()
            setPickupAndDropAddress()
        }

        updatePaymentOption()
    }

    private fun setPickupAndDropAddress() {
        if (Data.autoData != null) {
            if (Data.autoData.pickupLatLng != null) {
                val searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.pickupLatLng,
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false)
                if (searchResult != null) {
                    searchResultPickup = searchResult
                } else {
                    searchResultPickup = SearchResult("", Data.autoData.getPickupAddress(Data.autoData.pickupLatLng), "",
                            Data.autoData.pickupLatLng.latitude, Data.autoData.pickupLatLng.longitude)
                }
                searchResultReceived(searchResultPickup!!, PlaceSearchListFragment.PlaceSearchMode.PICKUP)
            }
            if (Data.autoData.dropLatLng != null) {
                val searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.dropLatLng,
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false)
                if (searchResult != null) {
                    searchResultDestination = searchResult
                } else {
                    searchResultDestination = SearchResult("", Data.autoData.dropAddress, "",
                            Data.autoData.dropLatLng.latitude, Data.autoData.dropLatLng.longitude)
                }
                searchResultReceived(searchResultDestination!!, PlaceSearchListFragment.PlaceSearchMode.DROP)
            }
        }
    }

    private fun setScheduleRideVehicleListAdapter() {
        rvVehiclesList.adapter = scheduleRideVehicleListAdapter
    }

    private fun getDatePickerFragment(): DatePickerFragment {
        if (datePickerFragment == null) {
            val bundle = Bundle()
            bundle.putSerializable(DatePickerFragment.ADD_DAYS, false)
            datePickerFragment = DatePickerFragment()
            datePickerFragment!!.arguments = bundle
        }
        return datePickerFragment!!
    }

    private fun getTimePickerFragment(): TimePickerFragment {
        if (timePickerFragment == null) {
            timePickerFragment = TimePickerFragment()
            val bundle = Bundle()
            bundle.putInt(TimePickerFragment.ADDITIONAL_TIME_MINUTES, MIN_BUFFER_TIME_MINS + BUFFER_TIME_TO_SELECT_MINS)
            timePickerFragment!!.arguments = bundle
        }
        return timePickerFragment!!
    }

    private fun validateDateTime(date: String?, time: String?): Boolean {
        val currentTimePlus24Hrs = DateOperations.addCalendarFieldValueToDateTime(DateOperations.getCurrentTime(), MIN_BUFFER_TIME_MINS, Calendar.MINUTE)
        return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0 && DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
                DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, 31, Calendar.DAY_OF_MONTH)) < 0
    }


    private fun getFormattedDateTime(selectedDate: String?, selectedTime: String?, addHours: Boolean): String {
        var selectedDate = selectedDate
        var selectedTime = selectedTime
        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)) {
            val calendar = Calendar.getInstance()
            if (TextUtils.isEmpty(selectedTime)) {
                calendar.add(Calendar.MINUTE, if (addHours) MIN_BUFFER_TIME_MINS + BUFFER_TIME_TO_SELECT_MINS else 0)
                selectedTime = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE) + ":00"
            }
            if (TextUtils.isEmpty(selectedDate)) {
                selectedDate = calendar.get(Calendar.YEAR).toString() + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
            }
        }
        return DateOperations.addCalendarFieldValueToDateTime("$selectedDate $selectedTime", 0, Calendar.HOUR)
    }

    private fun setTimeToVars(time: String): Boolean {
        if (validateDateTime(selectedDate, time)) {
            selectedTime = time
            val display = DateOperations.convertDayTimeAPViaFormat(time, true)
            tvSelectDateTime.text = DateOperations.getDateFormatted(selectedDate) + " " + display
            return true
        } else {
            Utils.showToast(activity, activity!!.getString(R.string.incorrect_schedule_time,MIN_BUFFER_TIME_MINS))
            return false
        }
    }

    fun searchResultReceived(searchResult: SearchResult, placeSearchMode: PlaceSearchListFragment.PlaceSearchMode) {
        if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
            tvPickup.text = searchResult.nameForText
            searchResultPickup = searchResult
        } else {
            tvDestination.text = searchResult.nameForText
            searchResultDestination = searchResult
        }
    }

    interface InteractionListener {
        fun onAttachScheduleRide()

        fun onDestroyScheduleRide()
    }

    fun openFareEstimate() {
        if (searchResultPickup == null) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_pickup_location))
            return
        } else if (searchResultDestination == null) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_destination_location))
            return
        }
        val intent = Intent(activity, FareEstimateActivity::class.java)
        intent.putExtra(Constants.KEY_REGION, (getActivity() as HomeActivity).gson.toJson((getActivity() as HomeActivity).selectedRegionForScheduleRide, Region::class.java))
        //        intent.putExtra(Constants.KEY_COUPON_SELECTED, getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon());
        intent.putExtra(Constants.KEY_RIDE_TYPE, (getActivity() as HomeActivity).selectedRideTypeForScheduleRide)
        intent.putExtra(Constants.KEY_PICKUP_LATITUDE, searchResultPickup!!.latitude)
        intent.putExtra(Constants.KEY_PICKUP_LONGITUDE, searchResultPickup!!.longitude)
        intent.putExtra(Constants.KEY_PICKUP_LOCATION_ADDRESS, searchResultPickup!!.address)
        intent.putExtra(Constants.KEY_DROP_LATITUDE, searchResultDestination!!.latitude)
        intent.putExtra(Constants.KEY_DROP_LONGITUDE, searchResultDestination!!.longitude)
        intent.putExtra(Constants.KEY_DROP_LOCATION_ADDRESS, searchResultDestination!!.address)
        intent.putExtra(Constants.KEY_SCHEDULE_RIDE, true)
        intent.putExtra(Constants.KEY_SCHEDULE_RIDE_FORMATED_DATE_TIME, getFormattedDateTime(selectedDate, selectedTime, true))
        intent.putExtra(Constants.KEY_SCHEDULE_RIDE_SELECTED_REGION_ID, (getActivity() as HomeActivity).selectedIdForScheduleRide)

        (activity as HomeActivity).startActivityForResult(intent, (activity as HomeActivity).FARE_ESTIMATE)
        activity!!.overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    companion object {
        val MIN_BUFFER_TIME_MINS = 30
        val BUFFER_TIME_TO_SELECT_MINS = 5

        fun newInstance(): ScheduleRideFragment {
            val bundle = Bundle()
            val fragment = ScheduleRideFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    private var paymentOptionDialog: PaymentOptionDialog? = null
    fun getPaymentOptionDialog(): PaymentOptionDialog? {
        if (paymentOptionDialog == null) {
            paymentOptionDialog = PaymentOptionDialog(activity, (requireActivity() as HomeActivity).getCallbackPaymentOptionSelector(),                 object : PaymentOptionDialog.Callback {
                override fun onDialogDismiss() {

                }

                override fun onPaymentModeUpdated() {
                    (requireActivity() as HomeActivity).updateConfirmedStatePaymentUI()
                    try {
                        GAUtils.event(GACategory.RIDES, GAAction.HOME + GAAction.WALLET + GAAction.SELECTED, MyApplication.getInstance().walletCore
                                .getPaymentOptionName(Data.autoData.pickupPaymentOption))
                    } catch (e: Exception) {
                    }

                }
            })
        }
        return paymentOptionDialog
    }

    fun dismissPaymentDialog() {
        if (paymentOptionDialog != null) {
            paymentOptionDialog!!.dismiss()
        }

    }

    fun updatePaymentOption(){
        if (view!=null) {
            imageViewPaymentModeConfirm.setImageResource(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()))
            textViewPaymentModeValueConfirm.text = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption())

        }

    }

    fun updateVehicleAdapter() {
        if (view != null) {
            setSelectedRegionData()
            scheduleRideVehicleListAdapter.notifyDataSetChanged()
        }
    }

    private fun setSelectedRegionData() {
        val regionSelected = (activity as HomeActivity).selectedRegionForScheduleRide

        if (Data.autoData.regions.size > 0) {

            var matched = false
            if (regionSelected != null) {
                for (i in 0 until Data.autoData.regions.size) {
                    if (Data.autoData.regions[i].operatorId == regionSelected.getOperatorId()
                            && Data.autoData.regions[i].regionId == regionSelected.getRegionId()
                            && Data.autoData.regions[i].vehicleType == regionSelected.getVehicleType()) {
                        (activity as HomeActivity).selectedRegionForScheduleRide = Data.autoData.regions[i]
                        matched = true
                        break
                    }
                }
            }

            if (!matched) {
                (activity as HomeActivity).selectedRegionForScheduleRide = Data.autoData.regions[0]
            }

            with(regionSelected) {
                (activity as HomeActivity).selectedIdForScheduleRide = regionId!!
                (activity as HomeActivity).selectedRideTypeForScheduleRide = rideType!!
            }


        } else {
            (activity as HomeActivity).selectedIdForScheduleRide = -1
            (activity as HomeActivity).selectedRideTypeForScheduleRide = -1
            (activity as HomeActivity).selectedRegionForScheduleRide = null
        }
    }


}
