package com.sabkuchfresh.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fugu.FuguConfig;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.dialogs.AnywhereDeliveryChargesDialog;
import com.sabkuchfresh.dialogs.FatafatTutorialDialog;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.retrofit.model.feed.DynamicDeliveryResponse;
import com.sabkuchfresh.retrofit.model.feed.OrderAnywhereResponse;
import com.sabkuchfresh.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;

/**
 * Created by Parminder Saini on 09/10/17.
 */

public class AnywhereHomeFragment extends Fragment implements GACategory, GAAction {

    public static final RelativeSizeSpan RELATIVE_SIZE_SPAN = new RelativeSizeSpan(1.15f);
    public static final int MIN_BUFFER_TIME_MINS = 30;
    public static final int BUFFER_TIME_TO_SELECT_MINS = 5;
    @Bind(R.id.llRoot)
    LinearLayout llRoot;
    @Bind(R.id.ivPickUpAddressType)
    ImageView ivPickUpAddressType;
    @Bind(R.id.ivDelAddressType)
    ImageView ivDelAddressType;
    @Bind(R.id.switchDeliveryTime)
    SwitchCompat switchDeliveryTime;
    @Bind(R.id.label_delivery_info)
    TextView labelDeliveryInfo;
    @Bind(R.id.label_delivery_value)
    TextView labelDeliveryValue;
    @Bind(R.id.edt_task_description)
    EditText edtTaskDescription;
    @Bind(R.id.tv_pickup_address)
    TextView tvPickupAddress;
    @Bind(R.id.tv_delivery_address)
    TextView tvDeliveryAddress;
    @Bind(R.id.rb_asap)
    TextView rbAsap;
    @Bind(R.id.rb_st)
    TextView rbSt;
    @Bind(R.id.cv_pickup_address)
    CardView cvPickupAddress;
    @Bind(R.id.cv_delivery_address)
    CardView cvDeliveryAddress;
    @Bind(R.id.rlDeliveryCharge)
    RelativeLayout rlDeliveryCharge;
    AnywhereDeliveryChargesDialog anywhereDeliveryChargesDialog;
    private ForegroundColorSpan textHintColorSpan;

    // TODO: 28/11/17 Slider stuck on fatafat error
    private ForegroundColorSpan textColorSpan;
    private PaySlider paySlider;
    private FreshActivity activity;
    private boolean isPickUpAddressRequested;
    private boolean isOrderViaCheckoutFragment;
    private boolean isOrderViaRestaurantDetail;
    private CompoundButton.OnCheckedChangeListener switchListenerTime;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;
    private SearchResult pickUpAddress;
    private SearchResult deliveryAddress;
    private boolean isAsapSelected;
    private Runnable enableStRbRunnable = new Runnable() {
        @Override
        public void run() {
            rbSt.setEnabled(true);
        }
    };
    private DatePickerFragment datePickerFragment;
    private String selectedDate;
    private String selectedTime;
    private TimePickerFragment timePickerFragment;
    private FatafatTutorialDialog mFatafatTutorialDialog;
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

    public boolean isPickUpAddressRequested() {
        return isPickUpAddressRequested;
    }

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
        try {
            product.clicklabs.jugnoo.utils.Utils.hideSoftKeyboard(activity, edtTaskDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        textColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color));
        textHintColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color_hint));
        isAsapSelected = true;
        setCurrentSelectedAddressToDelivery();
        paySlider = new PaySlider(activity.llPayViewContainer) {
            @Override
            public void onPayClick() {
                try {
                    String taskDetails = edtTaskDescription.getText().toString().trim();
                    if (taskDetails.length() == 0) {
                        Utils.showToast(activity, activity.getString(R.string.please_enter_some_desc));
                        throw new Exception();
                    }
                    if (deliveryAddress == null) {
                        Utils.showToast(activity, activity.getString(R.string.please_select_a_delivery_address));
                        throw new Exception();
                    }
                    if (!isAsapSelected) {
                        if (TextUtils.isEmpty(selectedDate)) {
                            Utils.showToast(activity, activity.getString(R.string.please_select_date));
                            throw new Exception();
                        } else if (TextUtils.isEmpty(selectedTime)) {
                            Utils.showToast(activity, activity.getString(R.string.please_select_time));
                            throw new Exception();
                        }
                    }
                    placeOrderApi(taskDetails);
                    GAUtils.event(activity.getGaCategory(), HOME, ORDER_PLACED);
                } catch (Exception e) {
                    paySlider.setSlideInitial();
                }
            }
        };


        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                if (!activity.isDeliveryOpenInBackground()) {
                    activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                }
            }

            @Override
            public void keyBoardClosed() {
                if (!activity.isDeliveryOpenInBackground()) {
                    if (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }
                }
            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);

        GAUtils.trackScreenView(activity.getGaCategory() + HOME);


        if (activity.getOrderViaChat() != null) {
            FreshActivity.OrderViaChatData orderViaChatData = activity.getOrderViaChat();

            if (orderViaChatData.getCartText() != null) {
                isOrderViaCheckoutFragment = true;
                edtTaskDescription.setText(orderViaChatData.getCartText());
                edtTaskDescription.setEnabled(false);
            } else {
                ((TextView)rootView.findViewById(R.id.tv_label_edt)).setText(R.string.txt_what_do_you_need);
                isOrderViaRestaurantDetail = true;
                setMaxLength(edtTaskDescription, 1000);
                edtTaskDescription.setHint(R.string.anywhere_hint_order_via_chat);

            }

            setAddress(false, new SearchResult("", orderViaChatData.getRestaurantName() + "\n" + orderViaChatData.getDestinationAddress(), "", orderViaChatData.getDestinationlatLng().latitude, orderViaChatData.getDestinationlatLng().longitude));
            activity.setOrderViaChatData(null);
            tvPickupAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            cvPickupAddress.setEnabled(false);
        } else {
            setMaxLength(edtTaskDescription, 1000);
            edtTaskDescription.setHint(R.string.anywhere_hint);

        }
        paySlider.setSlideInitial();
        activity.showPaySliderEnabled(true);
        switchListenerTime = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    isAsapSelected = true;
                    selectedTime = null;
                    selectedDate = null;
                    rbSt.setVisibility(View.GONE);
                } else {
                    try {
                        getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
                        GAUtils.event(activity.getGaCategory(), HOME, SCHEDULE + CLICKED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    buttonView.setOnCheckedChangeListener(null);
                    buttonView.setChecked(true);
                    buttonView.setOnCheckedChangeListener(this);
                }


            }
        };
        switchDeliveryTime.setOnCheckedChangeListener(switchListenerTime);
        switchDeliveryTime.setChecked(true);
        fetchDynamicDeliveryCharges(false,false);

        return rootView;
    }

    private void setCurrentSelectedAddressToDelivery() {
        SearchResult searchResult = HomeUtil.getNearBySavedAddress(activity, activity.getSelectedLatLng(), Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
        setAddress(true, searchResult);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activity != null) {
            activity.unRegisterKeyBoardListener();
        }
        ButterKnife.unbind(this);
    }

    private void setAddress(boolean isDeliveryAddress, SearchResult searchResult) {

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


        if (searchResult != null && searchResult.getName() != null) {
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


            addressType = addressType.length() == 0 ? addressType : addressType + "\n";
            SpannableString spannableString = new SpannableString(addressType + searchResult.getAddress());
            spannableString.setSpan(textHintColorSpan, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(RELATIVE_SIZE_SPAN, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(textColorSpan, spannableString.length() - searchResult.getAddress().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            textViewToSet.setText(spannableString);

        } else {
            imageViewToSet.setImageResource(R.drawable.ic_loc_other);
            if (isDeliveryAddress) {
                deliveryAddress = null;
                textViewToSet.setText(activity.getResources().getString(R.string.add_delivery_address));

            } else {
                pickUpAddress = null;
                textViewToSet.setText(activity.getResources().getString(R.string.label_anywhere));

            }


        }


    }

    @OnClick({R.id.cv_pickup_address, R.id.cv_delivery_address, R.id.rb_asap, R.id.rb_st, R.id.rlDeliveryCharge})
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
               /* isAsapSelected = true;
                GAUtils.event(activity.getGaCategory(), HOME, ASAP + CLICKED);*/
                break;
            case R.id.rb_st:
                rbSt.setEnabled(false);
               /* if (selectedDate == null || selectedTime == null) {
                    rgTimeSlot.check(R.id.rb_asap);

                }*/

                activity.getHandler().postDelayed(enableStRbRunnable, 300);
                try {
                    getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
                    GAUtils.event(activity.getGaCategory(), HOME, SCHEDULE + CLICKED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rlDeliveryCharge:

                if (anywhereDeliveryChargesDialog != null) {
                    anywhereDeliveryChargesDialog.show();
                }
                else {
                    fetchDynamicDeliveryCharges(true,true);
                }
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.registerForKeyBoardEvent(mKeyBoardStateHandler);
            activity.fragmentUISetup(this);
        } else {
            activity.unRegisterKeyBoardListener();
        }
    }

    public void setRequestedAddress(SearchResult searchResult) {
        setAddress(!isPickUpAddressRequested, searchResult);
        fetchDynamicDeliveryCharges(false,true);

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

    private boolean setTimeToVars(String time) {
        if (validateDateTime(selectedDate, time)) {
            selectedTime = time;
            String display = DateOperations.convertDayTimeAPViaFormat(time, true);
            switchDeliveryTime.setOnCheckedChangeListener(null);
            switchDeliveryTime.setChecked(false);
            rbSt.setVisibility(View.VISIBLE);
            switchDeliveryTime.setOnCheckedChangeListener(switchListenerTime);
            isAsapSelected = false;
            rbSt.setText("Schedule Time " + DateOperations.getDateFormatted(selectedDate) + " " + display);
            return true;
        } else {
            Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
            return false;
        }
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


    public void placeOrderApi(final String taskDetails) {

        if (paySlider.isSliderInIntialStage())
            paySlider.fullAnimate();

        final HashMap<String, String> params = new HashMap<>();
        params.put("details", taskDetails);
        if (pickUpAddress != null) {
            params.put(Constants.KEY_FROM_ADDRESS, pickUpAddress.getAddress());
            params.put(Constants.KEY_FROM_LATITUDE, String.valueOf(pickUpAddress.getLatitude()));
            params.put(Constants.KEY_FROM_LONGITUDE, String.valueOf(pickUpAddress.getLongitude()));
        } else {
            params.put(Constants.KEY_FROM_ADDRESS, "Anywhere");
            params.put(Constants.KEY_FROM_LATITUDE, "0");
            params.put(Constants.KEY_FROM_LONGITUDE, "0");
        }
        if (isOrderViaCheckoutFragment) {
            params.put(Constants.CATEGORY, "1");
        }

        params.put(Constants.KEY_TO_ADDRESS, deliveryAddress.getAddress());
        params.put(Constants.KEY_TO_LATITUDE, String.valueOf(deliveryAddress.getLatitude()));
        params.put(Constants.KEY_TO_LONGITUDE, String.valueOf(deliveryAddress.getLongitude()));
        params.put(Constants.KEY_IS_IMMEDIATE, isAsapSelected ? "1" : "0");

        String finalDateTime = null;
        if (!isAsapSelected) {
            finalDateTime = getFormattedDateTime(selectedDate, selectedTime, true);
            params.put(Constants.KEY_DELIVERY_TIME, DateOperations.localToUTC(finalDateTime));
        }

        final String finalDateTime1 = finalDateTime;
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

                        try {

                            String deliveryTime = finalDateTime1 == null ? "ASAP" : DateOperations.convertDateViaFormat(finalDateTime1);
                            String pickupAddress = pickUpAddress != null ? pickUpAddress.getAddress() : "Anywhere";
                            String fuguMessage = "I need:\n" +
                                    taskDetails + "\n" +
                                    "\n" +
                                    "From:\n" +
                                    pickupAddress + "\n" +
                                    "\n" +
                                    "To:\n" +
                                    deliveryAddress.getAddress() + "\n" +
                                    "\n" +
                                    "When:\n" +
                                    deliveryTime;

                            resetUI();
                            if (isOrderViaCheckoutFragment) {
                                activity.clearAllCartAtOrderComplete(activity.lastAppTypeOpen);
                                activity.clearFragmentStackTillLast();
                            } else if (isOrderViaRestaurantDetail) {
                                activity.clearFragmentStackTillLast();
                            } else {
                                activity.clearFragmentStackTillLast();

                            }
                            activity.getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (activity.getMenusFragment() != null) {
                                        activity.getMenusFragment().getAllMenus(true, activity.getSelectedLatLng(), true, null, MenusFragment.TYPE_API_MENUS_ADDRESS_CHANGE);

                                    }
                                }
                            }, 1000);



                            if (orderAnywhereResponse != null && !TextUtils.isEmpty(orderAnywhereResponse.getFuguChannelId())) {

                                // start ride transaction with indication to start fugu chat
                                Intent intent = new Intent(activity, RideTransactionsActivity.class);
                                intent.putExtra(Constants.KEY_ORDER_ID,orderAnywhereResponse.getOrderId());
                                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.FEED.getOrdinal());
                                intent.putExtra(Constants.KEY_FUGU_CHANNEL_ID,orderAnywhereResponse.getFuguChannelId());
                                intent.putExtra(Constants.KEY_FUGU_CHANNEL_NAME,orderAnywhereResponse.getFuguChannelName());
                                intent.putStringArrayListExtra(Constants.KEY_FUGU_TAGS,orderAnywhereResponse.getFuguTags());
                                intent.putExtra(Constants.KEY_MESSAGE,fuguMessage);

                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.hold, R.anim.hold);

                            } else {
                                FuguConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
                            }


                            String action;
                            if (isOrderViaCheckoutFragment) {
                                action = GAAction.ACTION_FATAFAT_ORDER_CONFIRMED_CHECKOUT;
                            } else if (isOrderViaRestaurantDetail) {
                                action = GAAction.ACTION_FATAFAT_ORDER_CONFIRMED_RESTAURANT_DETAIL;
                            } else {
                                action = ACTION_FATAFAT_ORDER_CONFIRMED_RESTAURANT_CUSTOM_ORDER;
                            }
                            GAUtils.event(GACategory.FATAFAT3, action, GAAction.LABEL_FATAFAT_ORDER_CONFIRMED);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public boolean onError(OrderAnywhereResponse feedCommonResponse, String message, int flag) {
                        Utils.showToast(activity, message);
                        paySlider.setSlideInitial();
                        return true;
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
        selectedTime = null;
        selectedDate = null;
        edtTaskDescription.setText(null);
        switchDeliveryTime.setChecked(true);
//        rgTimeSlot.check(R.id.rb_asap);
        isAsapSelected = true;
        rbSt.setText(R.string.label_rb_schedule_time);
        setCurrentSelectedAddressToDelivery();
        setAddress(false, null);
        timePickerFragment = null;

    }

    private void setMaxLength(EditText edtText, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        edtText.setFilters(fArray);
    }

    /**
     * Calculates and shows the delivery charges
     * @param showFareBreakUp whether to show fare breakup after calculation
     * @param showLoader whether to show loader
     */
    private void fetchDynamicDeliveryCharges(final boolean showFareBreakUp, final boolean showLoader) {

        if (deliveryAddress != null) {
            final HashMap<String, String> params = new HashMap<>();
            if (pickUpAddress != null) {
                params.put(Constants.KEY_FROM_LATITUDE, String.valueOf(pickUpAddress.getLatitude()));
                params.put(Constants.KEY_FROM_LONGITUDE, String.valueOf(pickUpAddress.getLongitude()));
            } else {
                params.put(Constants.KEY_FROM_ADDRESS, "Anywhere");
                params.put(Constants.KEY_FROM_LATITUDE, "0");
                params.put(Constants.KEY_FROM_LONGITUDE, "0");
            }

            params.put(Constants.KEY_TO_LATITUDE, String.valueOf(deliveryAddress.getLatitude()));
            params.put(Constants.KEY_TO_LONGITUDE, String.valueOf(deliveryAddress.getLongitude()));


            new ApiCommon<DynamicDeliveryResponse>(activity).showLoader(showLoader).execute(params, ApiName.ANYWHERE_DYNAMIC_DELIVERY,
                    new APICommonCallback<DynamicDeliveryResponse>() {
                        @Override
                        public boolean onNotConnected() {
                            resetDeliveryViews();
                            // we return false if showLoader is true otherwise true
                            return !showLoader;

                        }

                        @Override
                        public boolean onException(Exception e) {
                            resetDeliveryViews();
                            e.printStackTrace();
                            return !showLoader;

                        }

                        @Override
                        public void onSuccess(DynamicDeliveryResponse dynamicDeliveryResponse, String message, int flag) {
                            try {
                                String label = dynamicDeliveryResponse.getDeliveryCharges().getDeliveryLabel();
                                if (!TextUtils.isEmpty(dynamicDeliveryResponse.getDeliveryCharges().getEstimatedDistance())) {
                                    label += " (" + dynamicDeliveryResponse.getDeliveryCharges().getEstimatedDistance() + ")";
                                }
                                labelDeliveryInfo.setText(label);
                                labelDeliveryValue.setText(String.format("%s%s", activity.getString(R.string.rupee), product.clicklabs.jugnoo.utils.Utils.getMoneyDecimalFormat().format(dynamicDeliveryResponse.getDeliveryCharges().getEstimatedCharges())));
                                if (dynamicDeliveryResponse.getDeliveryCharges() != null && dynamicDeliveryResponse.getDeliveryCharges().getPopupData() != null) {
                                    anywhereDeliveryChargesDialog = new AnywhereDeliveryChargesDialog(activity, new AnywhereDeliveryChargesDialog.Callback() {
                                        @Override
                                        public void onDialogDismiss() {

                                        }
                                    }, dynamicDeliveryResponse.getDeliveryCharges().getPopupData(), dynamicDeliveryResponse.getDeliveryCharges().getEstimatedCharges(), dynamicDeliveryResponse.getDeliveryCharges().getTandC());

                                    if(showFareBreakUp && activity!=null && !activity.isFinishing()){
                                        anywhereDeliveryChargesDialog.show();
                                    }

                                } else {
                                    resetDeliveryViews();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public boolean onError(DynamicDeliveryResponse dynamicDeliveryResponse, String message, int flag) {
                            resetDeliveryViews();
                            return !showLoader;
                        }


                        @Override
                        public boolean onFailure(RetrofitError error) {
                            resetDeliveryViews();
                            return !showLoader;
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
        }else{
            Utils.showToast(activity,getString(R.string.add_delivery_address));

        }

    }

    private void resetDeliveryViews() {
        labelDeliveryValue.setText(R.string.no_value_delivery_charges);
        labelDeliveryInfo.setText(R.string.estimated_delivery);
        anywhereDeliveryChargesDialog = null;
    }

    /**
     * Shows fatafat tutorial
     */
    public void showFatafatTutorial() {

        if(mFatafatTutorialDialog!=null){
            mFatafatTutorialDialog.showDialog();
        }
        else {
            if (Data.getFeedData().getFatafatTutorialData() != null &&
                    Data.getFeedData().getFatafatTutorialData().size() != 0) {
                mFatafatTutorialDialog = new FatafatTutorialDialog(activity, Data.getFeedData().getFatafatTutorialData());
                mFatafatTutorialDialog.showDialog();
            }
        }
    }
}
