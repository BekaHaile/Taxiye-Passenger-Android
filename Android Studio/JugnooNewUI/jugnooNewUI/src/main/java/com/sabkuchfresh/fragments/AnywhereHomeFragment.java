package com.sabkuchfresh.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.models.TimeDisplay;
import com.sabkuchfresh.pros.ui.adapters.ProsTimeSelectorAdapter;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;

/**
 * Created by Parminder Saini on 09/10/17.
 */

public class AnywhereHomeFragment extends Fragment {

    @Bind(R.id.edt_task_description)
    EditText edtTaskDescription;
    @Bind(R.id.tv_pickup_address)
    TextView tvPickupAddress;
    @Bind(R.id.tv_delivery_address)
    TextView tvDeliveryAddress;
    @Bind(R.id.rb_asap)
    RadioButton rbAsap;
    @Bind(R.id.rb_st)
    RadioButton rbSt;
    @Bind(R.id.llPayViewContainer)
    LinearLayout llPayViewContainer;
    @Bind(R.id.cv_pickup_address)
    CardView cvPickupAddress;
    @Bind(R.id.cv_delivery_address)
    CardView cvDeliveryAddress;
    @Bind(R.id.viewAlpha)
    View viewAlpha;
    @Bind(R.id.tvSlide)
    TextView tvSlide;
    @Bind(R.id.relativeLayoutSlider)
    RelativeLayout relativeLayoutSlider;
    @Bind(R.id.sliderText)
    TextView sliderText;
    @Bind(R.id.rlSliderContainer)
    RelativeLayout rlSliderContainer;
    @Bind(R.id.buttonPlaceOrder)
    Button buttonPlaceOrder;
    @Bind(R.id.rg_time_slot)
    RadioGroup rgTimeSlot;
    private PaySlider paySlider;
    private FreshActivity activity;
    private boolean isPickUpAddressRequested;
    private SearchResult pickUpAddress;
    private SearchResult deliveryAddress;
    private boolean isAsapSelected;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FreshActivity) context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anywhere_home, container, false);
        activity.fragmentUISetup(this);
        ButterKnife.bind(this, rootView);
        ASSL.DoMagic(llPayViewContainer);
        paySlider = new PaySlider(llPayViewContainer) {
            @Override
            public void onPayClick() {
                try {
                    String taskDetails = edtTaskDescription.getText().toString().trim();
                    if(taskDetails.length() == 0){
						Utils.showToast(activity, activity.getString(R.string.please_enter_some_desc));
						throw new Exception();
					}
                    if(deliveryAddress == null){
                        Utils.showToast(activity, activity.getString(R.string.please_select_a_delivery_address));
                        throw new Exception();
                    }
                    if(!isAsapSelected){
                        if(TextUtils.isEmpty(selectedDate)){
                            Utils.showToast(activity, activity.getString(R.string.please_select_date));
                            throw new Exception();
                        } else if(TextUtils.isEmpty(selectedTime)){
                            Utils.showToast(activity, activity.getString(R.string.please_select_time));
                            throw new Exception();
                        }
                    }
                    placeOrderApi(taskDetails);
                } catch (Exception e) {
                    paySlider.setSlideInitial();
                }
            }
        };

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.cv_pickup_address, R.id.tv_delivery_address, R.id.rb_asap, R.id.rb_st})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_pickup_address:
                isPickUpAddressRequested = true;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
                break;
            case R.id.tv_delivery_address:
                isPickUpAddressRequested = false;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
                break;
            case R.id.rb_asap:
                isAsapSelected = true;
                break;
            case R.id.rb_st:
                if(selectedDate==null||selectedTime==null){
                    rgTimeSlot.check(R.id.rb_asap);

                }
                getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    public void setRequestedAddress(SearchResult searchResult) {
        if (searchResult != null) {
            if (isPickUpAddressRequested) {
                pickUpAddress = searchResult;
                tvPickupAddress.setText(searchResult.getName());
            } else {
                deliveryAddress = searchResult;
                tvDeliveryAddress.setText(searchResult.getName());
            }
        }
    }

    private DatePickerFragment datePickerFragment;

    private DatePickerFragment getDatePickerFragment() {
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }
        return datePickerFragment;
    }

    private String selectedDate;
    private String selectedTime;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            if (validateDateTime(date, selectedTime)) {
                selectedDate = date;
//                tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
                getTimePickerFragment().show(getChildFragmentManager(), "timePicker", onTimeSetListener);

            } else {
                Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            }
        }
    };


    private TimePickerFragment timePickerFragment;

    private TimePickerFragment getTimePickerFragment() {
        if (timePickerFragment == null) {
            timePickerFragment = new TimePickerFragment();
        }
        return timePickerFragment;
    }

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(setTimeToVars(hourOfDay + ":" + minute + ":00", hourOfDay + ":" + minute + ":00")){

            }
        }
    };

    private boolean setTimeToVars(String time, String display) {
        if (validateDateTime(selectedDate, time)) {
            selectedTime = time;
            /*tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
            tvSelectTimeSlot.setText(display);*/
            rgTimeSlot.check(R.id.rb_st);
            isAsapSelected = false;
            rbSt.setText("Schedule Time " + "( " + DateOperations.getDateFormatted(selectedDate) + " : " + display + " )");
            return true;
        } else {
            Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            return false;
        }
    }

    private boolean validateDateTime(String date, String time) {
        String currentTimePlus24Hrs = DateOperations.getDaysAheadTime(DateOperations.getCurrentTime(), 2);
        return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0
                &&
                DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
                        DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, 31, Calendar.DAY_OF_MONTH)) < 0;
    }

    private Dialog timeSelectorDialog;

    private Dialog getTimeSelectorDialog() {
        if (timeSelectorDialog == null) {
            timeSelectorDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            timeSelectorDialog.setContentView(R.layout.dialog_pros_time_selector);
            timeSelectorDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            WindowManager.LayoutParams layoutParams = timeSelectorDialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            timeSelectorDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            timeSelectorDialog.setCancelable(true);
            timeSelectorDialog.setCanceledOnTouchOutside(true);

            RecyclerView rvTime = (RecyclerView) timeSelectorDialog.findViewById(R.id.rvTime);
            rvTime.setLayoutManager(new LinearLayoutManager(activity));
            rvTime.setItemAnimator(new DefaultItemAnimator());
            ProsTimeSelectorAdapter prosTimeSelectorAdapter = new ProsTimeSelectorAdapter(getTimeDisplayArray(), rvTime,
                    new ProsTimeSelectorAdapter.Callback() {
                        @Override
                        public boolean onTimeDisplaySelected(TimeDisplay timeDisplay) {
                            if (setTimeToVars(timeDisplay.getValue(), timeDisplay.getDisplay())) {
                                if (timeSelectorDialog != null) {
                                    timeSelectorDialog.dismiss();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
            rvTime.setAdapter(prosTimeSelectorAdapter);

            timeSelectorDialog.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeSelectorDialog != null) {
                        timeSelectorDialog.dismiss();
                    }
                }
            });

            timeSelectorDialog.findViewById(R.id.llInner).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


        }
        return timeSelectorDialog;
    }

    private ArrayList<TimeDisplay> getTimeDisplayArray() {
        ArrayList<TimeDisplay> timeDisplays = new ArrayList<>();
        for (int i = 8; i < 21; i++) {
            int j = i + 1;
            TimeDisplay timeDisplay = new TimeDisplay((i > 12 ? (i - 12) : i) + " " + (i > 11 ? "PM" : "AM")
                    + " - " + (j > 12 ? (j - 12) : j) + " " + (j > 11 ? "PM" : "AM"),
                    i + ":00:00");
            timeDisplays.add(timeDisplay);
        }
        return timeDisplays;
    }

    private String getFormattedDateTime(String selectedDate, String selectedTime, boolean addHours) {
        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)) {
            Calendar calendar = Calendar.getInstance();
            if (TextUtils.isEmpty(selectedTime)) {
                calendar.add(Calendar.HOUR_OF_DAY, addHours ? 2 : -1);
                selectedTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00";
            }
            if (TextUtils.isEmpty(selectedDate)) {
                selectedDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            }
        }
        return DateOperations.addCalendarFieldValueToDateTime(selectedDate + " " + selectedTime, 0, Calendar.HOUR);
    }


    public void placeOrderApi(String taskDetails) {
        HashMap<String, String> params = new HashMap<>();
        params.put("details", taskDetails);
        if(pickUpAddress != null) {
            params.put("from_address", pickUpAddress.getAddress());
            params.put("from_latitude", String.valueOf(pickUpAddress.getLatitude()));
            params.put("from_longitude", String.valueOf(pickUpAddress.getLongitude()));
        } else {
            params.put("from_address", "Anywhere");
            params.put("from_latitude", "0");
            params.put("from_longitude", "0");
        }
        params.put("to_address", deliveryAddress.getAddress());
        params.put("to_latitude", String.valueOf(deliveryAddress.getLatitude()));
        params.put("to_longitude", String.valueOf(deliveryAddress.getLongitude()));
        params.put("is_immediate", isAsapSelected ? "1" : "0");
        if(isAsapSelected){
            String finalDateTime = getFormattedDateTime(selectedDate, selectedTime, true);
            params.put("time", finalDateTime);
        }

        new ApiCommon<>(activity).showLoader(false).execute(params, ApiName.ANYWHERE_PLACE_ORDER,
                new APICommonCallback<FeedCommonResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return true;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        return true;
                    }

                    @Override
                    public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return true;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        return true;
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
    }



}
