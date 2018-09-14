package product.clicklabs.jugnoo.home.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.utils.Utils;

import java.util.Calendar;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.ScheduleRideVehicleListAdapter;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;


public class ScheduleRideFragment extends Fragment implements Constants {

    private final String TAG = ScheduleRideFragment.class.getSimpleName();

    TextView tvPickup, tvDestination, tvSelectDateTime;
    RecyclerView rvVehiclesList;
    Button btSchedule;

    private View rootView;
    private FragmentActivity activity;
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    public static final int MIN_BUFFER_TIME_MINS = 30;
    public static final int BUFFER_TIME_TO_SELECT_MINS = 5;
    private String selectedDate;
    private String selectedTime;
    ScheduleRideVehicleListAdapter scheduleRideVehicleListAdapter;
    SearchResult searchResultPickup;
    SearchResult searchResultDestination;

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            setTimeToVars(hourOfDay + ":" + minute + ":00");
        }
    };
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            if (validateDateTime(date, null)) {
                selectedDate = date;
//                tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
                getTimePickerFragment().show(getChildFragmentManager(), "timePicker", onTimeSetListener);

            } else {
                Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            }
        }
    };

    public ScheduleRideFragment() {
    }

    public static ScheduleRideFragment newInstance() {
        Bundle bundle = new Bundle();
        ScheduleRideFragment fragment = new ScheduleRideFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private InteractionListener interactionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            interactionListener = (InteractionListener) context;
            interactionListener.onAttachScheduleRide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (interactionListener != null) {
            interactionListener.onDestroyScheduleRide();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule_ride, container, false);


        activity = getActivity();

        rvVehiclesList = (RecyclerView) rootView.findViewById(R.id.rvVehiclesList);
        rvVehiclesList.setLayoutManager(new LinearLayoutManager(activity));
        rvVehiclesList.setItemAnimator(new DefaultItemAnimator());
        rvVehiclesList.setHasFixedSize(false);

        tvPickup = (TextView) rootView.findViewById(R.id.tvPickup);
        tvDestination = (TextView) rootView.findViewById(R.id.tvDestination);
        tvSelectDateTime = (TextView) rootView.findViewById(R.id.tvSelectDateTime);
        tvPickup.setTypeface(Fonts.mavenRegular(activity));
        tvDestination.setTypeface(Fonts.mavenRegular(activity));
        tvSelectDateTime.setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.tvPickupDateTime)).setTypeface(Fonts.mavenMedium(activity));
        btSchedule = (Button) rootView.findViewById(R.id.btSchedule);

        tvPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((HomeActivity) getActivity()).onClickOfPickupElse();
            }
        });
        tvDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).onClickOfDestinationElse();
            }
        });
        tvSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (TextUtils.isEmpty(tvPickup.getText().toString())) {
                        Utils.showToast(activity, activity.getString(R.string.enter_pickup));
                        throw new Exception();
                    } else if (TextUtils.isEmpty(tvDestination.getText().toString())) {
                        Utils.showToast(activity, activity.getString(R.string.enter_destination));
                        throw new Exception();
                    } else if (TextUtils.isEmpty(selectedDate)) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_date));
                        throw new Exception();
                    } else if (TextUtils.isEmpty(selectedTime)) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_time));
                        throw new Exception();
                    }
                    else if (((HomeActivity)activity).selectedIdForScheduleRide==0) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_vehicle));
                        throw new Exception();
                    }
                    else {
                        openFareEstimate();
                    }
                } catch (Exception e) {

                }
            }
        });
        setScheduleRideVehicleListAdapter();

        if(Data.autoData != null){
            if(Data.autoData.getPickupLatLng() != null){
                SearchResult searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.getPickupLatLng(),
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                if(searchResult != null){
                    searchResultPickup = searchResult;
                } else {
                    searchResultPickup = new SearchResult("", Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()), "",
                            Data.autoData.getPickupLatLng().latitude, Data.autoData.getPickupLatLng().longitude);
                }
                searchResultReceived(searchResultPickup, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
            }
            if(Data.autoData.getDropLatLng() != null){
                SearchResult searchResult = HomeUtil.getNearBySavedAddress(getActivity(), Data.autoData.getDropLatLng(),
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                if(searchResult != null){
                    searchResultDestination = searchResult;
                } else {
                    searchResultDestination = new SearchResult("", Data.autoData.getDropAddress(), "",
                            Data.autoData.getDropLatLng().latitude, Data.autoData.getDropLatLng().longitude);
                }
                searchResultReceived(searchResultDestination, PlaceSearchListFragment.PlaceSearchMode.DROP);
            }
        }

        return rootView;
    }

    private void setScheduleRideVehicleListAdapter() {
        scheduleRideVehicleListAdapter = new ScheduleRideVehicleListAdapter(((HomeActivity) getActivity()), Data.autoData.getRegions());
        rvVehiclesList.setAdapter(scheduleRideVehicleListAdapter);
    }

    private DatePickerFragment getDatePickerFragment() {
        if (datePickerFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DatePickerFragment.ADD_DAYS, false);
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setArguments(bundle);
        }
        return datePickerFragment;
    }

    private TimePickerFragment getTimePickerFragment() {
        if (timePickerFragment == null) {
            timePickerFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(TimePickerFragment.ADDITIONAL_TIME_MINUTES, MIN_BUFFER_TIME_MINS + BUFFER_TIME_TO_SELECT_MINS);
            timePickerFragment.setArguments(bundle);
        }
        return timePickerFragment;
    }

    private boolean validateDateTime(String date, String time) {
        String currentTimePlus24Hrs = DateOperations.addCalendarFieldValueToDateTime(DateOperations.getCurrentTime(), MIN_BUFFER_TIME_MINS, Calendar.MINUTE);
        return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0
                &&
                DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
                        DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, 31, Calendar.DAY_OF_MONTH)) < 0;
    }


    private String getFormattedDateTime(String selectedDate, String selectedTime, boolean addHours) {
        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)) {
            Calendar calendar = Calendar.getInstance();
            if (TextUtils.isEmpty(selectedTime)) {
                calendar.add(Calendar.MINUTE, addHours ? MIN_BUFFER_TIME_MINS + BUFFER_TIME_TO_SELECT_MINS : 0);
                selectedTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00";
            }
            if (TextUtils.isEmpty(selectedDate)) {
                selectedDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            }
        }
        return DateOperations.addCalendarFieldValueToDateTime(selectedDate + " " + selectedTime, 0, Calendar.HOUR);
    }

    private boolean setTimeToVars(String time) {
        if (validateDateTime(selectedDate, time)) {
            selectedTime = time;
            String display = DateOperations.convertDayTimeAPViaFormat(time, true);
            tvSelectDateTime.setText(getString(R.string.schedule_time_format, DateOperations.getDateFormatted(selectedDate) + " " + display));
            return true;
        } else {
            Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            return false;
        }
    }

    public void searchResultReceived(SearchResult searchResult, PlaceSearchListFragment.PlaceSearchMode placeSearchMode) {
        if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
            tvPickup.setText(searchResult.getAddress());
            searchResultPickup = searchResult;
        } else {
            tvDestination.setText(searchResult.getAddress());
            searchResultDestination = searchResult;
        }
        //Toast.makeText(activity,placeSearchMode+" yo" +tvSelectDateTime.getText().toString(),Toast.LENGTH_LONG).show();
    }

    public interface InteractionListener {
        void onAttachScheduleRide();

        void onDestroyScheduleRide();
    }

    public void openFareEstimate() {
        if (searchResultPickup == null) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_pickup_location));
            return;
        } else if (searchResultDestination == null) {
            product.clicklabs.jugnoo.utils.Utils.showToast(activity, getString(R.string.set_your_destination_location));
            return;
        }
        Intent intent = new Intent(activity, FareEstimateActivity.class);
        intent.putExtra(Constants.KEY_REGION, ((HomeActivity)getActivity()).gson.toJson(((HomeActivity)getActivity()).selectedRegionForScheduleRide, Region.class));
//        intent.putExtra(Constants.KEY_COUPON_SELECTED, getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon());
        intent.putExtra(KEY_RIDE_TYPE, ((HomeActivity)getActivity()).selectedRideTypeForScheduleRide);
        intent.putExtra(KEY_PICKUP_LATITUDE, searchResultPickup.getLatitude());
        intent.putExtra(KEY_PICKUP_LATITUDE, searchResultPickup.getLatitude());
        intent.putExtra(KEY_PICKUP_LONGITUDE, searchResultDestination.getLongitude());
        intent.putExtra(KEY_PICKUP_LOCATION_ADDRESS, searchResultPickup.getAddress());
        intent.putExtra(KEY_DROP_LATITUDE, searchResultDestination.getLatitude());
        intent.putExtra(KEY_DROP_LONGITUDE, searchResultDestination.getLongitude());
        intent.putExtra(KEY_DROP_LOCATION_ADDRESS, searchResultDestination.getAddress());
        intent.putExtra(KEY_SCHEDULE_RIDE, true);

        startActivityForResult(intent, ((HomeActivity) activity).FARE_ESTIMATE);
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
