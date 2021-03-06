package product.clicklabs.jugnoo.home.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.analytics.GACategory
import com.sabkuchfresh.analytics.GAUtils
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import com.sabkuchfresh.pros.utils.DatePickerFragment
import com.sabkuchfresh.pros.utils.TimePickerFragment
import kotlinx.android.synthetic.main.fragment_schedule_ride.*
import product.clicklabs.jugnoo.*
import product.clicklabs.jugnoo.Constants.SCHEDULE_CURRENT_TIME_DIFF
import product.clicklabs.jugnoo.Constants.SCHEDULE_DAYS_LIMIT
import product.clicklabs.jugnoo.adapters.RentalPackagesAdapter
import product.clicklabs.jugnoo.apis.ApiFareEstimate
import product.clicklabs.jugnoo.datastructure.CouponInfo
import product.clicklabs.jugnoo.datastructure.MapsApiSources
import product.clicklabs.jugnoo.datastructure.PromoCoupon
import product.clicklabs.jugnoo.datastructure.SearchResult
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.home.adapters.ScheduleRideVehicleListAdapter
import product.clicklabs.jugnoo.home.dialogs.PaymentOptionDialog
import product.clicklabs.jugnoo.home.models.Region
import product.clicklabs.jugnoo.retrofit.model.Package
import product.clicklabs.jugnoo.retrofit.model.ServiceType
import product.clicklabs.jugnoo.retrofit.model.ServiceTypeValue
import product.clicklabs.jugnoo.utils.*
import java.util.*
import kotlin.collections.ArrayList


class ScheduleRideFragment : Fragment(), Constants, ScheduleRideVehicleListAdapter.OnSelectedCallback {

    private val TAG = ScheduleRideFragment::class.java.simpleName


    internal var oneWayPackages = ArrayList<Package>()
    internal var roundTripPackages = ArrayList<Package>()
    private var rootView: View? = null
    private var datePickerFragment: DatePickerFragment? = null
    private var timePickerFragment: TimePickerFragment? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private val scheduleRideVehicleListAdapter by lazy { ScheduleRideVehicleListAdapter(getActivity() as HomeActivity, Data.autoData.regions, this) }
    private var packagesAdapter: RentalPackagesAdapter? = null
    internal var searchResultPickup: SearchResult? = null
    internal var searchResultDestination: SearchResult? = null
    var selectedPackage: Package? = null
    var selectedRegion: Region? = null
    var minBufferTimeCurrent = 30
    var scheduleDaysLimit = 2
    private var isOneWay : Int = -1
    private var apiFareEstimate : ApiFareEstimate? = null
    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute -> setTimeToVars(hourOfDay.toString() + ":" + minute + ":00") }
    private val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
        if (validateDateTime(date, null)) {
            selectedDate = date
            //                tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
            getTimePickerFragment().show(childFragmentManager, "timePicker", onTimeSetListener)

        } else {
            Utils.showToast(activity, activity!!.getString(R.string.incorrect_schedule_time, minBufferTimeCurrent, scheduleDaysLimit))
        }
    }

    private var interactionListener: InteractionListener? = null
    private var serviceType: ServiceType? = null
    private var openSchedule:Boolean = false

    override fun onAttach(context: Context) {
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

        if (arguments != null
                && arguments!!.containsKey(Constants.KEY_SERVICE_TYPE)) {
            val str = arguments?.getString(Constants.KEY_SERVICE_TYPE)
            val gson = Gson()
            serviceType = gson.fromJson(str, ServiceType::class.java)
        }
        if(arguments != null) {
            openSchedule = arguments!!.getBoolean(Constants.KEY_SCHEDULE_RIDE, false)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(rootView) {
            (activity as HomeActivity).selectedIdForScheduleRide = 0
            (activity as HomeActivity).selectedRegionForScheduleRide = null
            rvVehiclesList.layoutManager = LinearLayoutManager(activity)
            rvVehiclesList.isNestedScrollingEnabled = false
            tvPickup.typeface = Fonts.mavenRegular(activity)
            tvScheduleMessage.typeface = Fonts.mavenRegular(activity)
            tvDestination.typeface = Fonts.mavenRegular(activity)
            tvPickupDateTime.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvSelectDateTime.typeface = Fonts.mavenMedium(activity)
            tvOneWay.typeface = Fonts.mavenMedium(activity)
            tvRoundTrip.typeface = Fonts.mavenMedium(activity)
            btSchedule.typeface = Fonts.mavenMedium(activity)
            tvSelectPayment.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvDropLocation.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvNote.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvNote.visibility = View.GONE
            textViewPaymentModeValueConfirm.typeface = Fonts.mavenRegular(activity)
            tvSelectPackage.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvSelectRoute.setTypeface(Fonts.mavenMedium(activity), BOLD)
            tvSelectVehicleType.setTypeface(Fonts.mavenMedium(activity), BOLD)
            rvPackages.layoutManager = LinearLayoutManager(activity)
            rvPackages.isNestedScrollingEnabled = false

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
                getPaymentOptionDialog()?.show(-1, null)
            }

            tvOneWay.setOnClickListener {
                isOneWay = 1

                tvOneWay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_radio_button_checked,
                        0, 0, 0)
                tvRoundTrip.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_radio_button_unchecked,
                        0, 0, 0)
                packagesAdapter!!.setList(getOneWayPackages(selectedRegion), Data.autoData.currency, Data.autoData.distanceUnit)
                getFareEstimate()
//                updatePackagesAccRegionSelected(selectedRegion)
            }

            tvRoundTrip.setOnClickListener {

                isOneWay = 0
                tvOneWay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_radio_button_unchecked,
                        0, 0, 0)
                tvRoundTrip.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_radio_button_checked,
                        0, 0, 0)
                packagesAdapter!!.setList(getRoundTripPackages(selectedRegion), Data.autoData.currency, Data.autoData.distanceUnit)
                getFareEstimate()
            }

            btSchedule.setOnClickListener {
                try {

                    if (TextUtils.isEmpty(tvPickup.text.toString())) {
                        Utils.showToast(activity, activity!!.getString(R.string.enter_pickup))
                        throw Exception()
                    } else if (!Data.autoData.getServiceTypeSelected().isRental()
                            && TextUtils.isEmpty(tvDestination.text.toString())) {
                        Utils.showToast(activity, activity!!.getString(R.string.enter_destination))
                        throw Exception()
                    } else if (openSchedule
                            && TextUtils.isEmpty(selectedDate)) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_date))
                        throw Exception()
                    } else if (openSchedule
                            && TextUtils.isEmpty(selectedTime)) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_time))
                        throw Exception()
                    } else if (!TextUtils.isEmpty(Data.autoData.getFarAwayCity())) {
                        Utils.showToast(activity, Data.autoData.getFarAwayCity())
                        throw Exception()
                    } else if ((activity as HomeActivity).selectedIdForScheduleRide <= 0) {
                        Utils.showToast(activity, activity!!.getString(R.string.please_select_vehicle))
                        throw Exception()
                    } else if(selectedPackage == null&&!(activity as HomeActivity).scheduleRideOpen) {
                        if(isOneWay == 1) {
                            if(getOneWayPackages(selectedRegion).size > 0) {
                                Utils.showToast(activity, activity!!.getString(R.string.pls_select_a_package))
                            } else {
                                Utils.showToast(activity, activity!!.getString(R.string.packages_not_available))
                            }
                        } else if(isOneWay == 0) {
                            if(getRoundTripPackages(selectedRegion).size > 0) {
                                Utils.showToast(activity, activity!!.getString(R.string.pls_select_a_package))
                            } else {
                                Utils.showToast(activity, activity!!.getString(R.string.packages_not_available))
                            }
                        }
                        throw Exception()
                    } else {
                        if(isOneWay == 1) {
                            (activity as HomeActivity).setNotes("One Way")
                        } else if(isOneWay == 0) {
                            (activity as HomeActivity).setNotes("Round Trip")
                        } else {
                            (activity as HomeActivity).setNotes("")
                        }
                        val proceed = (activity as HomeActivity).slidingBottomPanel.getRequestRideOptionsFragment()
                                .displayAlertAndCheckForSelectedWalletCoupon()
                        if (proceed) {
                            val callRequestRide = MyApplication.getInstance().walletCore
                                    .requestWalletBalanceCheck((activity as HomeActivity), Data.autoData.pickupPaymentOption)
                            MyApplication.getInstance().walletCore.requestRideWalletSelectedFlurryEvent(Data.autoData.pickupPaymentOption, TAG)

                            if (callRequestRide) {
                                openFareEstimate()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            minBufferTimeCurrent = Prefs.with(requireContext()).getInt(SCHEDULE_CURRENT_TIME_DIFF, 30);
            scheduleDaysLimit = Prefs.with(requireContext()).getInt(SCHEDULE_DAYS_LIMIT, 2);

            val regions = Data.autoData.regions
            if(Data.autoData != null && regions.size > 0) {
                selectedRegion = regions[0]
            }
            setSelectedRegionData(true)
            setScheduleRideVehicleListAdapter()
            setPickupAndDropAddress()

            tvFareEstimate.visibility = View.GONE
            viewInnerDrop.visibility = View.GONE

            val visibilityNotRental = if (openSchedule) View.VISIBLE else View.GONE
            tvDestination.visibility = if (Data.autoData.getServiceTypeSelected().isRental()) View.GONE else View.VISIBLE
            llDropLocation.visibility = if (Data.autoData.getServiceTypeSelected().isRental()) View.GONE else View.VISIBLE
            tvDropLocation.visibility = if (Data.autoData.getServiceTypeSelected().isRental()) View.GONE else View.VISIBLE
            tvPickupDateTime.visibility = visibilityNotRental
            tvSelectDateTime.visibility = visibilityNotRental
//            tvFareEstimate.visibility = if (Data.autoData.getServiceTypeSelected().isOutstation()) View.VISIBLE else View.GONE
//            rvVehiclesList.visibility = if (Data.autoData.getServiceTypeSelected().isOutstation()) View.GONE else View.VISIBLE
//            tvSelectVehicleType.visibility = if (Data.autoData.getServiceTypeSelected().isOutstation()) View.GONE else View.VISIBLE
            tvSelectRoute.visibility = if (Data.autoData.getServiceTypeSelected().isOutStation()) View.VISIBLE else View.GONE
            tvOneWay.visibility = if (Data.autoData.getServiceTypeSelected().isOutStation()) View.VISIBLE else View.GONE
            isOneWay = if (Data.autoData.getServiceTypeSelected().isOutStation()) 1 else -1
            tvRoundTrip.visibility = if (Data.autoData.getServiceTypeSelected().isOutStation()) View.VISIBLE else View.GONE
            tvSelectPackage.visibility = if (Data.autoData.getServiceTypeSelected().isOnDemand()) View.GONE else View.VISIBLE
            rvPackages.visibility = if (Data.autoData.getServiceTypeSelected().isOnDemand()) View.GONE else View.VISIBLE
            updatePackagesAccRegionSelected(null)
            btSchedule.setText(if (!openSchedule) R.string.book else R.string.schedule)
            tvScheduleMessage.text = if (serviceType != null) Utils.trimHTML(Utils.fromHtml(serviceType!!.info)) else requireActivity().getString(R.string.schedule_ride_alert)
            if(serviceType != null && serviceType!!.info.isNotEmpty()) {
                tvNote.visibility = View.VISIBLE
            } else {
                tvNote.visibility = View.GONE
            }
        }

        updatePaymentOption()
    }

    private fun updatePackagesAccRegionSelected(regionS: Region?) {
//        if (!serviceTypeNotRental()) {
        (requireActivity() as HomeActivity).getSlidingBottomPanel().requestRideOptionsFragment.regionSelected = selectedRegion
        var region = if (regionS == null) (requireActivity() as HomeActivity).getSlidingBottomPanel().requestRideOptionsFragment.regionSelected else regionS
        if (region!!.packages != null
                && region.packages.size > 0) {
            if((Data.autoData == null || Data.autoData.serviceTypeSelected == null
                    || !Data.autoData.serviceTypeSelected.isOutStation())) {
                this@ScheduleRideFragment.selectedPackage = region.packages[0]
            }
        }
        if (packagesAdapter == null) {
            packagesAdapter = RentalPackagesAdapter(activity as Context,
                    getOneWayPackages(region), Data.autoData.currency, Data.autoData.distanceUnit,
                    rvPackages,
                    Fonts.mavenRegular(activity),
                    object : RentalPackagesAdapter.OnSelectedCallback {
                        override fun onItemSelected(selectedPackage: Package) {
                            this@ScheduleRideFragment.selectedPackage = selectedPackage
                            scheduleRideVehicleListAdapter.notifyDataSetChanged()
                            getFareEstimate()
                        }

                        override fun getSelectedRentalPackage(): Package? {
                            return this@ScheduleRideFragment.selectedPackage
                        }
                    })
            rvPackages.adapter = packagesAdapter
        } else {
            val package1 : List<Package>
            if(isOneWay == 1){
                 package1 = getOneWayPackages(region)
            }else if(isOneWay == 0) {
                package1 = getRoundTripPackages(region)
            } else {
                package1 = region.packages
            }
            packagesAdapter!!.setList(package1 as ArrayList<Package>, Data.autoData.currency, Data.autoData.distanceUnit)
        }
    }

    private fun setPickupAndDropAddress() {
        if (Data.autoData != null) {
            if (Data.autoData.pickupLatLng != null) {
                val searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.pickupLatLng,
                        true)
                if (searchResult != null) {
                    searchResultPickup = searchResult
                } else {
                    searchResultPickup = Data.autoData.pickupSearchResult
                }
                searchResultReceived(searchResultPickup!!, PlaceSearchListFragment.PlaceSearchMode.PICKUP)
            }
            if (Data.autoData.dropLatLng != null) {
                val searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.dropLatLng,
                        true)
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
            bundle.putSerializable(DatePickerFragment.MAX_LIMIT_DAYS, scheduleDaysLimit)
            datePickerFragment = DatePickerFragment()
            datePickerFragment!!.arguments = bundle
        }
        return datePickerFragment!!
    }

    private fun getTimePickerFragment(): TimePickerFragment {
        if (timePickerFragment == null) {
            timePickerFragment = TimePickerFragment()
            val bundle = Bundle()
            bundle.putInt(TimePickerFragment.ADDITIONAL_TIME_MINUTES, minBufferTimeCurrent + BUFFER_TIME_TO_SELECT_MINS)
            timePickerFragment!!.arguments = bundle
        }
        return timePickerFragment!!
    }

    private fun validateDateTime(date: String?, time: String?): Boolean {
        val currentTimePlus24Hrs = DateOperations.addCalendarFieldValueToDateTime(DateOperations.getCurrentTime(), minBufferTimeCurrent, Calendar.MINUTE)
        return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0 &&
                DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
                        DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, scheduleDaysLimit, Calendar.DAY_OF_MONTH)) < 0
    }


    private fun getFormattedDateTime(selectedDate1: String?, selectedTime1: String?, addHours: Boolean): String {
        var selectedDate = selectedDate1
        var selectedTime = selectedTime1
        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)) {
            val calendar = Calendar.getInstance()
            if (TextUtils.isEmpty(selectedTime)) {
                calendar.add(Calendar.MINUTE, if (addHours) minBufferTimeCurrent + BUFFER_TIME_TO_SELECT_MINS else 0)
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
            Utils.showToast(activity, activity!!.getString(R.string.incorrect_schedule_time, minBufferTimeCurrent, scheduleDaysLimit))
            return false
        }
    }

    fun searchResultReceived(searchResult: SearchResult, placeSearchMode: PlaceSearchListFragment.PlaceSearchMode?) {
        if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
            tvPickup.text = searchResult.getNameForText(activity)
            searchResultPickup = searchResult
        } else {
            tvDestination.text = searchResult.getNameForText(activity)
            searchResultDestination = searchResult
        }
//        getFareEstimate()
    }

    private fun getDirectionsAndComputeFare(sourceLatLng: LatLng, sourceAddress: String, destLatLng: LatLng, destAddress: String) {
        try {
            if(selectedPackage == null) {
                return
            }
            val region = (requireActivity() as HomeActivity).getSlidingBottomPanel().requestRideOptionsFragment.regionSelected
            selectedPackage?.isRoundTrip = if(isOneWay == 1) 0 else 1
            if(apiFareEstimate == null) {
                apiFareEstimate = ApiFareEstimate(context, object : ApiFareEstimate.Callback {
                    override fun onSuccess(list: List<LatLng>, distanceText: String,
                                           timeText: String, distanceValue: Double, timeValue: Double, promoCoupon: PromoCoupon) {

                    }

                    override fun onFareEstimateSuccess(currency: String, minFare: String, maxFare: String, convenienceCharge: Double, tollCharge: Double) {

                        tvFareEstimate.visibility = View.VISIBLE
                        viewInnerDrop.visibility = View.VISIBLE
                        tvFareEstimate.text = getString(R.string.fare_estimate).plus(": ")
                                .plus(Utils.formatCurrencyValue(currency, if(isOneWay == 1) minFare else (2 * minFare.toDouble()).toString())
                                        .plus(" - ")
                                        .plus(Utils.formatCurrencyValue(currency, if(isOneWay == 1) maxFare else (2 * maxFare.toDouble()).toString())))
                        if (Prefs.with(context).getInt(Constants.KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE, 0) == 1) {
                            tvFareEstimate.append(" ")
                            tvFareEstimate.append(getString(R.string.bracket_in_format, currency))
                        }

//                    if (convenienceCharge > 0) {
//                        textViewConvenienceCharge.setText(getString(R.string.convenience_charge_colon) + " " + Utils.formatCurrencyValue(currency, convenienceCharge))
//                    } else {
//                        textViewConvenienceCharge.setText("")
//                    }
//                    setTextTollCharges(currency, tollCharge)
                    }

                    override fun onPoolSuccess(currency: String, fare: Double, rideDistance: Double, rideDistanceUnit: String,
                                               rideTime: Double, rideTimeUnit: String, poolFareId: Int, convenienceCharge: Double,
                                               text: String, tollCharge: Double) {
                        tvFareEstimate.visibility = View.VISIBLE
                        viewInnerDrop.visibility = View.VISIBLE
                        tvFareEstimate.text = getString(R.string.fare_estimate).plus(": ")
                                .plus(Utils.formatCurrencyValue(currency, if(isOneWay == 1) fare.toString() else (2 * fare).toString()))
                        if (Prefs.with(context).getInt(Constants.KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE, 0) == 1) {
                            tvFareEstimate.append(" ")
                            tvFareEstimate.append(getString(R.string.bracket_in_format, currency))
                        }
                    }

                    override fun onNoRetry() {
                        tvFareEstimate?.visibility = View.GONE
                        viewInnerDrop?.visibility = View.VISIBLE
                    }

                    override fun onRetry() {
                        tvFareEstimate?.visibility = View.GONE
                        viewInnerDrop?.visibility = View.VISIBLE
                    }

                    override fun onFareEstimateFailure() {
                        tvFareEstimate?.visibility = View.GONE
                        viewInnerDrop?.visibility = View.VISIBLE
                    }

                    override fun onDirectionsFailure() {
                        tvFareEstimate?.visibility = View.GONE
                        viewInnerDrop?.visibility = View.VISIBLE
                    }
                })
            }
            apiFareEstimate?.getDirectionsAndComputeFare(sourceLatLng, destLatLng, 0, true, selectedRegion, CouponInfo(-1, ""), selectedPackage?: if(region != null) region.packages[0] else null,
                    MapsApiSources.CUSTOMER_FARE_ESTIMATE_SCHEDULE)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun getFareEstimate() {
        if(searchResultPickup != null && searchResultDestination != null && selectedPackage != null && Data.autoData.getServiceTypeSelected().isOutStation()) {
//            tvFareEstimate.visibility = View.VISIBLE
//            viewInnerDrop.visibility = View.VISIBLE
            getDirectionsAndComputeFare(searchResultPickup!!.latLng, searchResultPickup!!.address, searchResultDestination!!.latLng, searchResultDestination!!.address)
        } else {
            tvFareEstimate.visibility = View.GONE
            viewInnerDrop.visibility = View.GONE
        }
    }

    interface InteractionListener {
        fun onAttachScheduleRide()

        fun onDestroyScheduleRide()

        fun callRentalOutstationRequestRide(serviceType: ServiceType?, region: Region, selectedPackage: Package?, searchResultPickup: SearchResult,
                                            searchResultDestination: SearchResult?, dateTime: String?)
    }

    fun openFareEstimate() {
        Data.autoData.selectedPackage = selectedPackage

        if (searchResultPickup == null) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_pickup_location))
            return
        } else if ((!Data.autoData.getServiceTypeSelected().isRental() && searchResultDestination == null)) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_destination_location))
            return
        }
        if (!openSchedule && interactionListener != null) {
            interactionListener!!.callRentalOutstationRequestRide(serviceType, (getActivity() as HomeActivity).selectedRegionForScheduleRide,
                    selectedPackage,
                    searchResultPickup!!, null, null)
            return
        }
//        if (serviceType == null && (Data.autoData.getServiceTypeSelected().isOutstation() || Data.autoData.getServiceTypeSelected().isRental())) {
        if (openSchedule && (Data.autoData.serviceTypeSelected.isRentalOrOutstation())) {

            scheduleRide((activity as HomeActivity).selectedRegionForScheduleRide)

            return
        }
        val intent = Intent(activity, FareEstimateActivity::class.java)
        intent.putExtra(Constants.KEY_REGION, (getActivity() as HomeActivity).gson.toJson((getActivity() as HomeActivity).selectedRegionForScheduleRide, Region::class.java))
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

    private fun serviceTypeNotRental() =
//            (serviceType == null || serviceType!!.isOutstation())
            (serviceType == null)

    companion object {

        val BUFFER_TIME_TO_SELECT_MINS = 5

        fun newInstance(serviceType: ServiceType?, openSchedule:Boolean): ScheduleRideFragment {
            val bundle = Bundle()
            val gson = Gson()
            val fragment = ScheduleRideFragment()
            if (serviceType != null) {
                bundle.putString(Constants.KEY_SERVICE_TYPE, gson.toJson(serviceType, ServiceType::class.java))
            }
            bundle.putBoolean(Constants.KEY_SCHEDULE_RIDE, openSchedule)
            fragment.arguments = bundle
            return fragment
        }
    }


    private var paymentOptionDialog: PaymentOptionDialog? = null
    fun getPaymentOptionDialog(): PaymentOptionDialog? {
        if (paymentOptionDialog == null) {
            paymentOptionDialog = PaymentOptionDialog(activity, (requireActivity() as HomeActivity).getCallbackPaymentOptionSelector(), object : PaymentOptionDialog.Callback {
                override fun getSelectedPaymentOption() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDialogDismiss() {

                }

                override fun onPaymentModeUpdated() {
                    (requireActivity() as HomeActivity).updateConfirmedStatePaymentUI()
                    try {
                        GAUtils.event(GACategory.RIDES, GAAction.HOME + GAAction.WALLET + GAAction.SELECTED, MyApplication.getInstance().walletCore
                                .getPaymentOptionName(Data.autoData.pickupPaymentOption, requireContext()))
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

    fun updatePaymentOption() {
        if (view != null) {
            imageViewPaymentModeConfirm.setImageResource(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()))
            textViewPaymentModeValueConfirm.text = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption(),activity)

        }

    }

    fun updateVehicleAdapter() {
        if (view != null) {
            setSelectedRegionData(false)
            scheduleRideVehicleListAdapter.notifyDataSetChanged()

            this@ScheduleRideFragment.selectedPackage = null

            tvFareEstimate.visibility = View.GONE
            viewInnerDrop.visibility = View.GONE
        }
    }

    private fun setSelectedRegionData(isFromThisFragment: Boolean) {
        var regionSelected = (activity as HomeActivity).selectedRegionForScheduleRide

        val regions = Data.autoData.regions
        if (regions.size > 0) {

            var matched = false
            if (regionSelected != null) {
                for (i in 0 until regions.size) {
                    if (regions[i].regionId == regionSelected.getRegionId()) {
                        regionSelected = regions[i]
                        matched = true
                        break
                    }
                }
            }

            if (!matched) {
                regionSelected = regions[0]
            }

            with(regionSelected) {
                (activity as HomeActivity).selectedRegionForScheduleRide = this
                (activity as HomeActivity).selectedIdForScheduleRide = regionId!!
                (activity as HomeActivity).selectedRideTypeForScheduleRide = rideType!!
                if(!isFromThisFragment) {
                    selectedRegion = this
                    if(getOneWayPackages(selectedRegion).size>0) {
                        isOneWay = 1

                        tvOneWay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_radio_button_checked,
                                0, 0, 0)
                        tvRoundTrip.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_radio_button_unchecked,
                                0, 0, 0)
                        packagesAdapter!!.setList(getOneWayPackages(selectedRegion), Data.autoData.currency, Data.autoData.distanceUnit)
                        selectedPackage = getOneWayPackages(selectedRegion).get(0)
                    }
                    else if(getRoundTripPackages(selectedRegion).size>0){
                        isOneWay = 0
                        tvOneWay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_radio_button_unchecked,
                                0, 0, 0)
                        tvRoundTrip.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_radio_button_checked,
                                0, 0, 0)
                        packagesAdapter!!.setList(getRoundTripPackages(selectedRegion), Data.autoData.currency, Data.autoData.distanceUnit)
                        selectedPackage = getRoundTripPackages(selectedRegion).get(0)
                    }
                }
            }


        } else {
            (activity as HomeActivity).selectedIdForScheduleRide = -1
            (activity as HomeActivity).selectedRideTypeForScheduleRide = -1
            (activity as HomeActivity).selectedRegionForScheduleRide = null
        }
    }

    override fun onItemSelected(selectedRegion: Region) {
        if (this.selectedRegion != null && selectedRegion.regionId != this.selectedRegion!!.regionId) {
            selectedPackage = null
        }
        this.selectedRegion = selectedRegion
        updatePackagesAccRegionSelected(selectedRegion)
    }

    override fun getPackageSelected(): Package? {
        return selectedPackage
    }

    private fun scheduleRide(region: Region) {
        val params = HashMap<String, String>()
        params[Constants.KEY_REGION_ID] = region.regionId.toString() + ""
        params[Constants.KEY_PICKUP_TIME] = DateOperations.localToUTC(getFormattedDateTime(selectedDate, selectedTime, true))
        params[Constants.KEY_CUSTOMER_NOTE] = Data.autoData.serviceTypeSelected!!.info
        params[Constants.KEY_LATITUDE] = searchResultPickup!!.latitude.toString()
        params[Constants.KEY_LONGITUDE] = searchResultPickup!!.longitude.toString()
        if (!searchResultPickup!!.address.equals(Constants.UNNAMED)) {
            params[Constants.KEY_PICKUP_LOCATION_ADDRESS] = searchResultPickup!!.address
        } else {
            params[Constants.KEY_PICKUP_LOCATION_ADDRESS] = ""
        }
//        if (!dropAddress.equals(Constants.UNNAMED, ignoreCase = true)) {
//            params[Constants.KEY_DROP_LOCATION_ADDRESS] = dropAddress
//        } else {
//            params[Constants.KEY_DROP_LOCATION_ADDRESS] = ""
//        }

//        params["op_drop_latitude"] = dropLatLng.latitude.toString()
//        params["op_drop_longitude"] = dropLatLng.longitude.toString()
        params["vehicle_type"] = region.getVehicleType().toString()
        if (Data.autoData.selectedPackage != null) {
            params[Constants.KEY_PACKAGE_ID] = Data.autoData.selectedPackage.packageId.toString()
        }
        params[Constants.KEY_PREFERRED_PAYMENT_MODE] = "" + Data.autoData.pickupPaymentOption


        ApiCommon<FeedCommonResponse>(activity).showLoader(true).execute(params, ApiName.SCHEDULE_RIDE,
                object : APICommonCallback<FeedCommonResponse>() {

                    override fun onSuccess(response: FeedCommonResponse, message: String?, flag: Int) {
                        if(!message.isNullOrBlank()){
                            DialogPopup.alertPopupWithListener(activity, "", message) { (activity as HomeActivity).onBackPressed() }
                        } else {
                            DialogPopup.alertPopupWithListener(activity, "", getString(R.string.booking_scheduled_successfully)) { (activity as HomeActivity).onBackPressed() }
                        }
                    }

                    override fun onError(feedCommonResponse: FeedCommonResponse, message: String?, flag: Int): Boolean {
                        return false
                    }

                })


    }

    fun getOneWayPackages(selectedRegion:Region?): ArrayList<Package> {
        oneWayPackages.clear()
        if(selectedRegion != null && selectedRegion.packages !=null ) {
            for (i in 0 until selectedRegion!!.packages.size) {
                if (selectedRegion!!.packages.get(i).returnTrip == 0) {
                    val pck = Package()

                    pck.packageId = selectedRegion.packages.get(i).packageId
                    pck.fareFixed = selectedRegion.packages.get(i).fareFixed
                    pck.farePerKm = selectedRegion.packages.get(i).farePerKm
                    pck.fareThresholdDistance = selectedRegion.packages.get(i).fareThresholdDistance
                    pck.farePerKmThresholdDistance = selectedRegion.packages.get(i).farePerKmThresholdDistance
                    pck.farePerKmAfterThreshold = selectedRegion.packages.get(i).farePerKmAfterThreshold
                    pck.farePerKmBeforeThreshold = selectedRegion.packages.get(i).farePerKmBeforeThreshold
                    pck.farePerMin = selectedRegion.packages.get(i).farePerMin
                    pck.fareThresholdTime = selectedRegion.packages.get(i).fareThresholdTime
                    pck.farePerWaitingMin = selectedRegion.packages.get(i).farePerWaitingMin
                    pck.fareThresholdWaitingTime = selectedRegion.packages.get(i).fareThresholdWaitingTime
                    pck.startTime = selectedRegion.packages.get(i).startTime
                    pck.endTime = selectedRegion.packages.get(i).endTime
                    pck.vehicleType = selectedRegion.packages.get(i).vehicleType
                    pck.rideType = selectedRegion.packages.get(i).rideType
                    pck.fareMinimum = selectedRegion.packages.get(i).fareMinimum
                    pck.operatorId = selectedRegion.packages.get(i).operatorId
                    pck.farePerBaggage = selectedRegion.packages.get(i).farePerBaggage
                    pck.regionId = selectedRegion.packages.get(i).regionId
                    pck.cityName = selectedRegion.packages.get(i).cityName
                    pck.cityId = selectedRegion.packages.get(i).cityId
                    pck.returnTrip = selectedRegion.packages.get(i).returnTrip
                    pck.packageName = selectedRegion.packages.get(i).packageName

                    oneWayPackages.add(pck)
                }
            }
        }
        if(oneWayPackages.size > 0
                && (Data.autoData == null || Data.autoData.serviceTypeSelected == null
                        || !Data.autoData.serviceTypeSelected.isOutStation())){
            selectedPackage = oneWayPackages[0]
            scheduleRideVehicleListAdapter.notifyDataSetChanged()
        } else {
            selectedPackage = null
        }
        return oneWayPackages
    }
    fun getRoundTripPackages(selectedRegion:Region?): ArrayList<Package> {
        roundTripPackages.clear()
        if(selectedRegion != null) {
            for (i in 0 until selectedRegion.packages.size) {
                if (selectedRegion.packages.get(i).returnTrip == 1) {
                    val pck = Package()

                    pck.packageId = selectedRegion.packages.get(i).packageId
                    pck.fareFixed = selectedRegion.packages.get(i).fareFixed
                    pck.farePerKm = selectedRegion.packages.get(i).farePerKm
                    pck.fareThresholdDistance = selectedRegion.packages.get(i).fareThresholdDistance
                    pck.farePerKmThresholdDistance = selectedRegion.packages.get(i).farePerKmThresholdDistance
                    pck.farePerKmAfterThreshold = selectedRegion.packages.get(i).farePerKmAfterThreshold
                    pck.farePerKmBeforeThreshold = selectedRegion.packages.get(i).farePerKmBeforeThreshold
                    pck.farePerMin = selectedRegion.packages.get(i).farePerMin
                    pck.fareThresholdTime = selectedRegion.packages.get(i).fareThresholdTime
                    pck.farePerWaitingMin = selectedRegion.packages.get(i).farePerWaitingMin
                    pck.fareThresholdWaitingTime = selectedRegion.packages.get(i).fareThresholdWaitingTime
                    pck.startTime = selectedRegion.packages.get(i).startTime
                    pck.endTime = selectedRegion.packages.get(i).endTime
                    pck.vehicleType = selectedRegion.packages.get(i).vehicleType
                    pck.rideType = selectedRegion.packages.get(i).rideType
                    pck.fareMinimum = selectedRegion.packages.get(i).fareMinimum
                    pck.operatorId = selectedRegion.packages.get(i).operatorId
                    pck.farePerBaggage = selectedRegion.packages.get(i).farePerBaggage
                    pck.regionId = selectedRegion.packages.get(i).regionId
                    pck.cityName = selectedRegion.packages.get(i).cityName
                    pck.cityId = selectedRegion.packages.get(i).cityId
                    pck.returnTrip = selectedRegion.packages.get(i).returnTrip
                    pck.packageName = selectedRegion.packages.get(i).packageName

                    roundTripPackages.add(pck)
                }
            }
        }
        if(roundTripPackages.size > 0
                && (Data.autoData == null || Data.autoData.serviceTypeSelected == null
                        || !Data.autoData.serviceTypeSelected.isOutStation())){
            selectedPackage = roundTripPackages[0]
            scheduleRideVehicleListAdapter.notifyDataSetChanged()
        } else {
            selectedPackage = null
        }
        return roundTripPackages
    }
}
