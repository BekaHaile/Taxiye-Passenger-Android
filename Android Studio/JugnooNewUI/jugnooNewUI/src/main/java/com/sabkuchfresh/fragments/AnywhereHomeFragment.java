package com.sabkuchfresh.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fugu.FuguConfig;
import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.FatafatImageAdapter;
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
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
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
import product.clicklabs.jugnoo.retrofit.model.FatafatUploadImageInfo;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;

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
    @Bind(R.id.tv_promo_label)
    TextView tvPromoLabel;
    @Bind(R.id.edtPromo)
    EditText edtPromo;
    @Bind(R.id.tv_apply)
    Button tvApplyPromo;
    @Bind(R.id.tv_promo_error)
    TextView tvPromoError;
    @Bind(R.id.rvPromo)
    RelativeLayout relativeLayout;
    @Bind(R.id.cv_promo)
    CardView cvPromo;
    @Bind(R.id.sv_anywhere)
    ScrollView svAnywhere;
    @Bind(R.id.cvUploadImages)
    CardView cvUploadImages;
    @Bind(R.id.cvImages)
    CardView cvImages;
    @Bind(R.id.rvImages)
    RecyclerView rvImages;
    @Bind(R.id.ivUploadImage)
    ImageView ivUploadImage;
    @Bind(R.id.svImages)
    HorizontalScrollView svImages;

    private ForegroundColorSpan textHintColorSpan;

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
    private DynamicDeliveryResponse.ReferalCode currentPromoApplied;
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
    private boolean promoBoxEnabled;

    private String[] permissionsRequest;
    private static final int REQUEST_CODE_SELECT_IMAGES=99;
    private Picker picker;
    private ArrayList<Object> imageObjectList = new ArrayList<>();
    private FatafatImageAdapter fatafatImageAdapter;
    private int maxNoImages;
    private ImageCompression imageCompressionTask;
    private FreshActivity.OrderViaChatData orderViaChatData;

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
                    final String taskDetails = edtTaskDescription.getText().toString().trim();
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


                    // if we have images to upload attach them with params
                    if(imageObjectList!=null && imageObjectList.size()>0){

                        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

                        //Compress Images if any new added
                        ArrayList<String> imageEntries =null;
                        for(Object image:imageObjectList){
                            if(image instanceof ImageEntry){
                                if(imageEntries==null)
                                    imageEntries= new ArrayList<>();

                                imageEntries.add(((ImageEntry) image).path);
                            }
                        }

                        if(imageEntries!=null){
                            //upload feedback with new Images
                            imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                                @Override
                                public void processFinish(ImageCompression.CompressedImageModel[] output) {

                                    if(output!=null){
                                        for(ImageCompression.CompressedImageModel file:output)
                                        {
                                            if(file!=null){
                                                multipartTypedOutput.addPart(Constants.KEY_ORDER_IMAGES,new TypedFile("image/*",file.getFile()));
                                            }
                                        }

                                    }
                                    //place order with images
                                    placeOrderApi(taskDetails,multipartTypedOutput);
                                }

                                @Override
                                public  void onError(){
                                    DialogPopup.dismissLoadingDialog();

                                }
                            },activity);
                            imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
                        }

                    }
                    else {
                        placeOrderApi(taskDetails, new MultipartTypedOutput());
                    }

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
                if (getView()!=null && edtPromo.hasFocus()) {
                    svAnywhere.fullScroll(ScrollView.FOCUS_DOWN);
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
            orderViaChatData = activity.getOrderViaChat();

            if (orderViaChatData.getCartText() != null) {
                isOrderViaCheckoutFragment = true;
                edtTaskDescription.setText(orderViaChatData.getCartText());
            } else {
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
        fetchDynamicDeliveryCharges(false, false, false);
        tvPromoLabel.setVisibility(Data.getFeedData()!=null && Data.getFeedData().showPromoBox() ? View.VISIBLE : View.GONE);
        cvPromo.setVisibility(Data.getFeedData()!=null && Data.getFeedData().showPromoBox() ? View.VISIBLE : View.GONE);
        promoBoxEnabled =    Data.getFeedData()!=null && Data.getFeedData().showPromoBox();
        edtPromo.addTextChangedListener(new PromoTextWatcher(tvPromoError, edtPromo));
        tvPromoError.setVisibility(View.GONE);
        Utils.addCapitaliseFilterToEditText(edtPromo);

        // decide whether to show upload image layout
        if(Data.getFeedData()!=null && Data.getFeedData().getFatafatUploadImageInfo()!=null){
            FatafatUploadImageInfo fatafatUploadImageInfo = Data.getFeedData().getFatafatUploadImageInfo();
            if(fatafatUploadImageInfo.getShowImageBox()==1){
                cvUploadImages.setVisibility(View.VISIBLE);
                maxNoImages = fatafatUploadImageInfo.getImageLimit();
                cvImages.setVisibility(View.GONE);
                rvImages.setNestedScrollingEnabled(false);
            }
            else {
                cvUploadImages.setVisibility(View.GONE);
                cvImages.setVisibility(View.GONE);
            }
        } else {
            cvUploadImages.setVisibility(View.GONE);
            cvImages.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void setCurrentSelectedAddressToDelivery() {
        SearchResult searchResult = HomeUtil.getNearBySavedAddress(activity, activity.getSelectedLatLng(), Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
        setAddress(true, searchResult);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    @OnClick({R.id.cv_pickup_address, R.id.cv_delivery_address, R.id.rb_asap, R.id.rb_st, R.id.rlDeliveryCharge, R.id.tv_apply,
    R.id.cvUploadImages,R.id.ivUploadImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_pickup_address:
                isPickUpAddressRequested = true;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer(), true);
                break;
            case R.id.cv_delivery_address:
                isPickUpAddressRequested = false;
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer(), false);
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
                } else {
                    fetchDynamicDeliveryCharges(true, true, false);
                }
                break;
            case R.id.tv_apply:

                if (currentPromoApplied != null) {
                    showRemoveCouponPopup();
                } else {
                    if (edtPromo.getText().toString().trim().length() > 0) {
                        if (tvPromoError.getVisibility() != View.VISIBLE) {
                            fetchDynamicDeliveryCharges(false, true, true);
                        }
                    } else {
                        Utils.showToast(activity, activity.getString(R.string.please_enter_code));
                    }
                }


                break;

            case R.id.ivUploadImage:
            case R.id.cvUploadImages:

                pickImages();

                break;
        }
    }

    /**
     * Allows image selection
     */
    private void pickImages() {

        if(PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED)
        {
            if (permissionsRequest==null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                permissionsRequest = new String[2];
                permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                permissionsRequest[1]=Manifest.permission.READ_EXTERNAL_STORAGE;
            } else {
                permissionsRequest = new String[1];
                permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
            }
        }

            (AnywhereHomeFragment.this).requestPermissions(permissionsRequest, 20);
            return;
        }

        int alreadyPresent = imageObjectList == null ? 0 : imageObjectList.size();
        if(picker==null){
            picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
        }

        picker.setLimit(maxNoImages -alreadyPresent);
        picker.startActivity(AnywhereHomeFragment.this,activity,REQUEST_CODE_SELECT_IMAGES);

    }

    private void setImageAdapter(final ArrayList<Object> objectList) {

        if (objectList == null || objectList.size() == 0) {
            cvImages.setVisibility(View.GONE);
            cvUploadImages.setVisibility(View.VISIBLE);
            return;
        }

        if (fatafatImageAdapter == null) {
            fatafatImageAdapter = new FatafatImageAdapter(activity, objectList, new FatafatImageAdapter.Callback() {
                @Override
                public void onImageClick(Object object) {
                    //View full Image
                }

                @Override
                public void onDelete(Object object) {
                    objectList.remove(object);
                    ivUploadImage.setVisibility(objectList.size()<maxNoImages?View.VISIBLE:View.GONE);
                    if(objectList.size()==0){
                        cvImages.setVisibility(View.GONE);
                        fatafatImageAdapter=null;
                        cvUploadImages.setVisibility(View.VISIBLE);
                    }
                }
            }, rvImages);
            rvImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            rvImages.setAdapter(fatafatImageAdapter);
        } else {
            fatafatImageAdapter.setList(objectList);
        }

        ivUploadImage.setVisibility(objectList.size()<maxNoImages?View.VISIBLE:View.GONE);

        if(objectList.size()>0){
            svImages.post(new Runnable() {
                @Override
                public void run() {
                    svImages.fullScroll(View.FOCUS_RIGHT);
                }
            });

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_SELECT_IMAGES && resultCode==RESULT_OK){
            if(data!=null && data.getSerializableExtra("imagesList")!=null)
            {
                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {
                    imageObjectList.addAll(images);
                    cvUploadImages.setVisibility(View.GONE);
                    cvImages.setVisibility(View.VISIBLE);
                    setImageAdapter(imageObjectList);
                }
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.registerForKeyBoardEvent(mKeyBoardStateHandler);
            activity.fragmentUISetup(this);
        } else {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            activity.unRegisterKeyBoardListener();
        }
    }

    public void setRequestedAddress(SearchResult searchResult) {
        setAddress(!isPickUpAddressRequested, searchResult);
        fetchDynamicDeliveryCharges(false, false, false);

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


    public void placeOrderApi(final String taskDetails, final MultipartTypedOutput params) {

        if (paySlider.isSliderInIntialStage())
            paySlider.fullAnimate();

        params.addPart("details", new TypedString(taskDetails));
        if (pickUpAddress != null) {
            params.addPart(Constants.KEY_FROM_ADDRESS, new TypedString(pickUpAddress.getAddress()));
            params.addPart(Constants.KEY_FROM_LATITUDE, new TypedString(String.valueOf(pickUpAddress.getLatitude())));
            params.addPart(Constants.KEY_FROM_LONGITUDE, new TypedString(String.valueOf(pickUpAddress.getLongitude())));
        } else {
            params.addPart(Constants.KEY_FROM_ADDRESS, new TypedString("Anywhere"));
            params.addPart(Constants.KEY_FROM_LATITUDE, new TypedString("0"));
            params.addPart(Constants.KEY_FROM_LONGITUDE, new TypedString("0"));
        }
        if (isOrderViaCheckoutFragment || isOrderViaRestaurantDetail) {
            params.addPart(Constants.CATEGORY, new TypedString("1"));
            if(orderViaChatData!=null)params.addPart(Constants.KEY_RESTAURANT_ID,new TypedString(String.valueOf(orderViaChatData.getRestaurantId())));
        }

        params.addPart(Constants.KEY_TO_ADDRESS, new TypedString(deliveryAddress.getAddress()));
        params.addPart(Constants.KEY_TO_LATITUDE, new TypedString(String.valueOf(deliveryAddress.getLatitude())));
        params.addPart(Constants.KEY_TO_LONGITUDE, new TypedString(String.valueOf(deliveryAddress.getLongitude())));
        params.addPart(Constants.KEY_IS_IMMEDIATE, new TypedString(isAsapSelected ? "1" : "0"));
        params.addPart(Constants.KEY_USER_IDENTIFIER, new TypedString(String.valueOf(Data.userData.userIdentifier)));
        if (currentPromoApplied != null) {
            params.addPart(Constants.PROMO_CODE, new TypedString(String.valueOf(currentPromoApplied.getReferalName())));
            params.addPart(Constants.KEY_ORDER_OFFER_ID, new TypedString(String.valueOf(currentPromoApplied.getId())));
        }

        String finalDateTime = null;
        if (!isAsapSelected) {
            finalDateTime = getFormattedDateTime(selectedDate, selectedTime, true);
            params.addPart(Constants.KEY_DELIVERY_TIME, new TypedString(DateOperations.localToUTC(finalDateTime)));
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
                                intent.putExtra(Constants.KEY_ORDER_ID, orderAnywhereResponse.getOrderId());
                                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.FEED.getOrdinal());
                                intent.putExtra(Constants.KEY_FUGU_CHANNEL_ID, orderAnywhereResponse.getFuguChannelId());
                                intent.putExtra(Constants.KEY_FUGU_CHANNEL_NAME, orderAnywhereResponse.getFuguChannelName());
                                intent.putStringArrayListExtra(Constants.KEY_FUGU_TAGS, orderAnywhereResponse.getFuguTags());
                                intent.putExtra(Constants.KEY_MESSAGE, fuguMessage);

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
     *
     * @param showFareBreakUp whether to show fare breakup after calculation
     * @param showLoader      whether to show loader
     * @param applyPromoApi
     */
    private void fetchDynamicDeliveryCharges(final boolean showFareBreakUp, final boolean showLoader, boolean applyPromoApi) {

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


            String promoName = null;
            if (applyPromoApi) {
                if (currentPromoApplied == null) {
                    promoName = edtPromo.getText().toString().trim();
                }
            } else if (currentPromoApplied != null) {
                promoName = currentPromoApplied.getReferalName();
            }
            if (promoName != null) {
                params.put(Constants.KEY_PROMO_CODE, promoName);

            }


            new ApiCommon<DynamicDeliveryResponse>(activity).showLoader(showLoader).execute(params, ApiName.ANYWHERE_DYNAMIC_DELIVERY,
                    new APICommonCallback<DynamicDeliveryResponse>() {
                        @Override
                        public boolean onNotConnected() {
                            // we return false if showLoader is true otherwise true
                            return !showLoader;

                        }

                        @Override
                        public boolean onException(Exception e) {
                            resetDeliveryViews();
                            e.printStackTrace();
                            return false;

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

                                    if (showFareBreakUp && activity != null && !activity.isFinishing()) {
                                        anywhereDeliveryChargesDialog.show();
                                    }


                                } else {
                                    resetDeliveryViews();
                                }

                                if (dynamicDeliveryResponse.getReferalCode() != null && dynamicDeliveryResponse.getReferalCode().size() > 0) {
                                    setPromoView(dynamicDeliveryResponse.getReferalCode().get(0));

                                } else {
                                    setPromoView(null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public boolean onError(DynamicDeliveryResponse dynamicDeliveryResponse, String message, int flag) {
                            resetDeliveryViews();
                            setPromoView(null);
                            return false;
                        }


                        @Override
                        public boolean onFailure(RetrofitError error) {

                            return !showLoader;
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
        } else {
            if (showLoader) {
                Utils.showToast(activity, getString(R.string.add_delivery_address));

            }

        }

    }

    private boolean shouldSendPromoCodeParams() {
        return (currentPromoApplied == null && edtPromo.getText().toString().trim().length() > 0) /*removecase*/;
    }

    private void setPromoView(DynamicDeliveryResponse.ReferalCode referalCode) {
        if (promoBoxEnabled) {
            if (referalCode != null) {

                if (referalCode.isPromoApplied()) {
                    currentPromoApplied = referalCode;
                    edtPromo.setText(referalCode.getReferalName());
                    tvPromoError.setTextColor(ContextCompat.getColor(activity, R.color.text_green_color));
                    tvApplyPromo.setText(R.string.label_remove);
                    edtPromo.setEnabled(false);

                } else {
                    currentPromoApplied = null;
                    edtPromo.setEnabled(true);
                    tvPromoError.setTextColor(ContextCompat.getColor(activity, R.color.red_dark));
                    tvApplyPromo.setText(R.string.label_apply);
                }
                tvPromoError.setText(referalCode.getMessage());
                tvPromoError.setVisibility(referalCode.getMessage() == null ? View.GONE : View.VISIBLE);

            } else {
                currentPromoApplied = null;
                edtPromo.setEnabled(true);
                edtPromo.setText(null);
                tvPromoError.setVisibility(View.GONE);
                tvApplyPromo.setText(R.string.label_apply);


            }
        }
    }

    private void resetDeliveryViews() {
        labelDeliveryValue.setText(R.string.no_value_delivery_charges);
        labelDeliveryInfo.setText(R.string.estimated_charges);
        anywhereDeliveryChargesDialog = null;
    }

    /**
     * Shows fatafat tutorial
     */
    public void showFatafatTutorial() {

        if (mFatafatTutorialDialog != null) {
            mFatafatTutorialDialog.showDialog();
        } else {
            if (Data.getFeedData().getFatafatTutorialData() != null &&
                    Data.getFeedData().getFatafatTutorialData().size() != 0) {
                mFatafatTutorialDialog = new FatafatTutorialDialog(activity, Data.getFeedData().getFatafatTutorialData());
                mFatafatTutorialDialog.showDialog();
            }
        }
    }

    private class PromoTextWatcher implements TextWatcher {
        private TextView textView;
        private EditText editText;

        public PromoTextWatcher(TextView textView, EditText editText) {
            this.textView = textView;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (textView.getVisibility() == View.VISIBLE) {
                textView.setVisibility(View.GONE);
            }
            if (s.length() == 0 && currentPromoApplied == null) {
                tvApplyPromo.setEnabled(false);
            } else {
                tvApplyPromo.setEnabled(true);
            }

        }
    }

    public void showRemoveCouponPopup() {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", activity.getString(R.string.remove_popup_message),
                activity.getString(R.string.yes),
                activity.getString(R.string.no),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fetchDynamicDeliveryCharges(false, true, true);

                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true, false);
    }


}
