package com.sabkuchfresh.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fugu.FuguConfig;
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;

import com.sabkuchfresh.home.FreshOrderCompleteDialog;

import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.retrofit.model.feed.OrderAnywhereResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;

/**
 * Created by Parminder Saini on 09/10/17.
 */

public class AnywhereHomeFragment extends Fragment {

    public static final RelativeSizeSpan RELATIVE_SIZE_SPAN = new RelativeSizeSpan(1.15f);
    @Bind(R.id.ivPickUpAddressType)
    ImageView ivPickUpAddressType;
    @Bind(R.id.ivDelAddressType)
    ImageView ivDelAddressType;
    private ForegroundColorSpan textHintColorSpan;
    private ForegroundColorSpan textColorSpan;
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

    public boolean isPickUpAddressRequested() {
        return isPickUpAddressRequested;
    }

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
        textColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color));
        textHintColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color_light));
        rgTimeSlot.check(R.id.rb_asap);
        isAsapSelected = true;
        ASSL.DoMagic(llPayViewContainer);
        setCurrentAddressToDelivery();
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

    private void setCurrentAddressToDelivery() {
        SearchResult searchResult =   HomeUtil.getNearBySavedAddress(activity,new LatLng(Data.latitude,Data.longitude), Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION,false);
        setAddress(true,searchResult);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void setAddress(boolean isDeliveryAddress,SearchResult searchResult) {

        TextView textViewToSet;
        ImageView imageViewToSet;
        if (isDeliveryAddress) {
            textViewToSet = tvDeliveryAddress;
            imageViewToSet = ivDelAddressType;
            deliveryAddress = searchResult;

        } else {
            textViewToSet = tvPickupAddress;
            imageViewToSet = ivPickUpAddressType;
            pickUpAddress = searchResult;

        }



        if (searchResult!=null && !TextUtils.isEmpty(searchResult.getName())) {
            textViewToSet.setVisibility(View.VISIBLE);
//          tvNoAddressAlert.setVisibility(View.GONE);
            String addressType;
            if (searchResult.getName().equalsIgnoreCase(activity.getString(R.string.home))) {
                imageViewToSet.setImageResource(R.drawable.ic_home);
                addressType = activity.getString(R.string.home);
            } else if (searchResult.getName().equalsIgnoreCase(activity.getString(R.string.work))) {
                imageViewToSet.setImageResource(R.drawable.ic_work);
                addressType = activity.getString(R.string.work);
            } else {
                imageViewToSet.setImageResource(R.drawable.ic_loc_other);
                addressType = searchResult.getName();
            }

            SpannableString spannableString = new SpannableString(addressType + "\n" + searchResult.getAddress());
            spannableString.setSpan(textHintColorSpan, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(RELATIVE_SIZE_SPAN, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(textColorSpan, spannableString.length() - searchResult.getAddress().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            textViewToSet.setText(spannableString);

        } else {
            imageViewToSet.setImageResource(R.drawable.ic_loc_other);
            if(isDeliveryAddress){
                deliveryAddress= null;
                textViewToSet.setText(activity.getResources().getString(R.string.add_delivery_address));

            }else{
                pickUpAddress = null;
                textViewToSet.setText(activity.getResources().getString(R.string.label_anywhere));

            }




        }


    }


    @OnClick({R.id.cv_pickup_address, R.id.cv_delivery_address, R.id.rb_asap, R.id.rb_st})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_pickup_address:
                isPickUpAddressRequested = true;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
                break;
            case R.id.cv_delivery_address:
                isPickUpAddressRequested = false;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
                break;
            case R.id.rb_asap:
                isAsapSelected = true;
                break;
            case R.id.rb_st:
                if (selectedDate == null || selectedTime == null) {
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
            setAddress(!isPickUpAddressRequested,searchResult);

    }

    private DatePickerFragment datePickerFragment;

    private DatePickerFragment getDatePickerFragment() {
        if (datePickerFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DatePickerFragment.ADD_DAYS,false);
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setArguments(bundle);
        }
        return datePickerFragment;
    }

    private String selectedDate;
    private String selectedTime;
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


    private TimePickerFragment timePickerFragment;

    private TimePickerFragment getTimePickerFragment() {
        if (timePickerFragment == null) {
            timePickerFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(TimePickerFragment.ADDITIONAL_TIME_MINUTES,35);
            timePickerFragment.setArguments(bundle);
        }
        return timePickerFragment;
    }

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            setTimeToVars(hourOfDay + ":" + minute + ":00");
        }
    };

    private boolean setTimeToVars(String time) {
        if (validateDateTime(selectedDate, time)) {
            selectedTime = time;
            String display = DateOperations.convertDayTimeAPViaFormat(time);
            rgTimeSlot.check(R.id.rb_st);
            isAsapSelected = false;
            rbSt.setText("Schedule Time " + DateOperations.getDateFormatted(selectedDate) + " " + display );
            return true;
        } else {
            Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            return false;
        }
    }

    private boolean validateDateTime(String date, String time) {
        String currentTimePlus24Hrs = DateOperations.addCalendarFieldValueToDateTime(DateOperations.getCurrentTime(),30,Calendar.MINUTE);
        return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0
                &&
                DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
                        DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, 31, Calendar.DAY_OF_MONTH)) < 0;
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

        if(paySlider.isSliderInIntialStage())
            paySlider.fullAnimate();

        final HashMap<String, String> params = new HashMap<>();
        params.put("details", taskDetails);
        if(pickUpAddress != null) {
            params.put(Constants.KEY_FROM_ADDRESS, pickUpAddress.getAddress());
            params.put(Constants.KEY_FROM_LATITUDE, String.valueOf(pickUpAddress.getLatitude()));
            params.put(Constants.KEY_FROM_LONGITUDE, String.valueOf(pickUpAddress.getLongitude()));
        } else {
            params.put(Constants.KEY_FROM_ADDRESS, "Anywhere");
            params.put(Constants.KEY_FROM_LATITUDE, "0");
            params.put(Constants.KEY_FROM_LONGITUDE, "0");
        }
        params.put(Constants.KEY_TO_ADDRESS, deliveryAddress.getAddress());
        params.put(Constants.KEY_TO_LATITUDE, String.valueOf(deliveryAddress.getLatitude()));
        params.put(Constants.KEY_TO_LONGITUDE, String.valueOf(deliveryAddress.getLongitude()));
        params.put(Constants.KEY_IS_IMMEDIATE, isAsapSelected ? "1" : "0");
        if(!isAsapSelected){
            String finalDateTime = getFormattedDateTime(selectedDate, selectedTime, true);
            params.put(Constants.KEY_DELIVERY_TIME, finalDateTime);
        }

        new ApiCommon<OrderAnywhereResponse>(activity).showLoader(true).execute(params, ApiName.ANYWHERE_PLACE_ORDER,
                new APICommonCallback<OrderAnywhereResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        paySlider.setSlideInitial();
                        return false;

                    }

                    @Override
                    public void onSuccess(final OrderAnywhereResponse orderAnywhereResponse, String message, int flag) {
                        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                            @Override
                            public void onDismiss() {
                                resetUI();
                                try {
                                    if (orderAnywhereResponse != null && !TextUtils.isEmpty(orderAnywhereResponse.getFuguChannelId())) {
                                        FuguConfig.getInstance().openChatByTransactionId(orderAnywhereResponse.getFuguChannelId(), String.valueOf(Data.getFuguUserData().getUserId()),
                                                orderAnywhereResponse.getFuguChannelName(), orderAnywhereResponse.getFuguTags());
                                    } else {
                                        FuguConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show(String.valueOf(orderAnywhereResponse.getOrderId()),
                                isAsapSelected?"Asap":selectedTime,
                                isAsapSelected?"":selectedDate, false, "",
                                null, AppConstant.ApplicationType.FEED, orderAnywhereResponse.getMessage());
                    }

                    @Override
                    public boolean onError(OrderAnywhereResponse feedCommonResponse, String message, int flag) {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {
                        paySlider.setSlideInitial();

                    }
                });
    }

    private void resetUI() {
        paySlider.setSlideInitial();
        selectedTime=null;
        selectedDate=null;
        edtTaskDescription.setText(null);
        rgTimeSlot.check(R.id.rb_asap);
        isAsapSelected= true;
        rbSt.setText(R.string.label_rb_schedule_time);
        setCurrentAddressToDelivery();
        setAddress(false,null);
        timePickerFragment=null;

    }


}
