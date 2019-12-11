package com.sabkuchfresh.pros.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.pros.models.CreateTaskData;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.models.TimeDisplay;
import com.sabkuchfresh.pros.ui.adapters.ProsTimeSelectorAdapter;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 19/06/17.
 */

public class ProsCheckoutFragment extends Fragment {

	private final String TAG = ProsCheckoutFragment.class.getSimpleName();
	@BindView(R.id.editTextDeliveryInstructions)
	EditText editTextDeliveryInstructions;
	@BindView(R.id.tvRateCard)
	TextView tvRateCard;
	@BindView(R.id.imageViewAddressType)
	ImageView imageViewAddressType;
	@BindView(R.id.textViewAddressName)
	TextView textViewAddressName;
	@BindView(R.id.textViewAddressValue)
	TextView textViewAddressValue;
	@BindView(R.id.relativeLayoutDeliveryAddress)
	RelativeLayout relativeLayoutDeliveryAddress;
	@BindView(R.id.imageViewDeliveryAddressForward)
	ImageView imageViewDeliveryAddressForward;
	@BindView(R.id.tvNoAddressAlert)
	TextView tvNoAddressAlert;
	@BindView(R.id.tvSelectDate)
	TextView tvSelectDate;
	@BindView(R.id.tvSelectTimeSlot)
	TextView tvSelectTimeSlot;
	@BindView(R.id.imageViewCashRadio)
	ImageView imageViewCashRadio;
	@BindView(R.id.relativeLayoutCash)
	RelativeLayout relativeLayoutCash;
	@BindView(R.id.llPaymentOptions)
	LinearLayout llPaymentOptions;
	@BindView(R.id.scrollView)
	ScrollView scrollView;
	@BindView(R.id.linearLayoutRoot)
	RelativeLayout linearLayoutRoot;
	@BindView(R.id.tvProductName)
	TextView tvProductName;
	@BindView(R.id.tvTerms)
	TextView tvTerms;
	private FreshActivity activity;
	private ProsProductData.ProsProductDatum prosProductDatum;
	private Bus mBus;
	private String selectedDate, selectedTime;
	private boolean openTimeDialogAfter;

	public static ProsCheckoutFragment newInstance(ProsProductData.ProsProductDatum prosProductDatum) {
		ProsCheckoutFragment fragment = new ProsCheckoutFragment();
		Bundle bundle = new Bundle();

		Gson gson = new Gson();
		bundle.putString(Constants.KEY_PRODUCT_DATUM, gson.toJson(prosProductDatum, ProsProductData.ProsProductDatum.class));

		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
		Gson gson = new Gson();
		prosProductDatum = gson.fromJson(getArguments().getString(Constants.KEY_PRODUCT_DATUM), ProsProductData.ProsProductDatum.class);
	}

	Unbinder unbinder;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pros_product_checkout, container, false);

		activity = (FreshActivity) getActivity();
		mBus = activity.getBus();
		activity.fragmentUISetup(this);
		parseArguments();
		unbinder = ButterKnife.bind(this, rootView);

		tvProductName.setText(prosProductDatum.getName());

		activity.bRequestBooking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addressSelectedNotValid() || TextUtils.isEmpty(activity.getSelectedAddress())) {
					Utils.showToast(activity, activity.getString(R.string.please_select_a_delivery_address));
					return;
				} else if(TextUtils.isEmpty(selectedDate)){
					Utils.showToast(activity, activity.getString(R.string.please_select_date));
					return;
				} else if(TextUtils.isEmpty(selectedTime)){
					Utils.showToast(activity, activity.getString(R.string.please_select_time));
					return;
				}
				String finalDateTime = getFormattedDateTime(selectedDate, selectedTime, true);
				apiCreateTask(editTextDeliveryInstructions.getText().toString().trim(),
						finalDateTime,
						DateOperations.addCalendarFieldValueToDateTime(finalDateTime, 1, Calendar.HOUR),
						String.valueOf(-1*DateOperations.getTimezoneDiffWithUTC()));
			}
		});

		updateAddressView();
		tvTerms.setTypeface(tvTerms.getTypeface(), Typeface.ITALIC);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.fragmentUISetup(this);
		}
	}

	private String getFormattedDateTime(String selectedDate, String selectedTime, boolean addHours){
		if(TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime)){
			Calendar calendar = Calendar.getInstance();
			if(TextUtils.isEmpty(selectedTime)){
				calendar.add(Calendar.HOUR_OF_DAY, addHours ? 2 : -1);
				selectedTime = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":00";
			}
			if(TextUtils.isEmpty(selectedDate)){
				selectedDate = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
			}
		}
		return DateOperations.addCalendarFieldValueToDateTime(selectedDate+" "+selectedTime, 0, Calendar.HOUR);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		activity.bRequestBooking.setOnClickListener(null);
	}

	@OnClick({R.id.tvRateCard, R.id.relativeLayoutDeliveryAddress,
			R.id.tvSelectDate, R.id.tvSelectTimeSlot, R.id.relativeLayoutCash})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tvRateCard:
				break;
			case R.id.relativeLayoutDeliveryAddress:
				activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer(), false);
				break;
			case R.id.tvSelectDate:
				getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
				break;
			case R.id.tvSelectTimeSlot:
				if(TextUtils.isEmpty(selectedDate)){
					getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
					openTimeDialogAfter = true;
				} else {
//					getTimePickerFragment().show(getChildFragmentManager(), "timePicker", onTimeSetListener);
					getTimeSelectorDialog().show();
					openTimeDialogAfter = false;
				}
				break;
			case R.id.relativeLayoutCash:
				break;
		}
	}


	private DatePickerFragment datePickerFragment;
	private DatePickerFragment getDatePickerFragment(){
		if(datePickerFragment == null){
			datePickerFragment = new DatePickerFragment();
		}
		return datePickerFragment;
	}
	private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
			String date = year + "-" + (month + 1) + "-" + dayOfMonth;
			if(validateDateTime(date, selectedTime)) {
				selectedDate = date;
				tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
				if(openTimeDialogAfter){
//					getTimePickerFragment().show(getChildFragmentManager(), "timePicker", onTimeSetListener);
					getTimeSelectorDialog().show();
				}
				openTimeDialogAfter = false;
			} else {
				Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
			}
		}
	};


	private TimePickerFragment timePickerFragment;
	private TimePickerFragment getTimePickerFragment(){
		if(timePickerFragment == null){
			timePickerFragment = new TimePickerFragment();
		}
		return timePickerFragment;
	}
	private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setTimeToVars(hourOfDay+":"+minute+":00", hourOfDay+":"+minute+":00");
		}
	};

	private boolean setTimeToVars(String time, String display){
		if(validateDateTime(selectedDate, time)) {
			selectedTime = time;
			tvSelectTimeSlot.setText(display);
			return true;
		} else {
			Utils.showToast(activity, activity.getString(R.string.please_select_appropriate_time));
			return false;
		}
	}

	public boolean addressSelectedNotValid(){
		return TextUtils.isEmpty(activity.getSelectedAddressType());
	}

	public void updateAddressView(){
		imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
		imageViewAddressType.setPaddingRelative(0,0,0,0);
		textViewAddressName.setVisibility(View.GONE);
		textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color));
		if(!addressSelectedNotValid() && !TextUtils.isEmpty(activity.getSelectedAddress())) {
			textViewAddressValue.setVisibility(View.VISIBLE);          tvNoAddressAlert.setVisibility(View.GONE);
			imageViewDeliveryAddressForward.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_arrow_grey));
			imageViewDeliveryAddressForward.setVisibility(View.VISIBLE);
			textViewAddressValue.setText(activity.getSelectedAddress());
			imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
			if(!TextUtils.isEmpty(activity.getSelectedAddressType())){
				textViewAddressName.setVisibility(View.VISIBLE);
				textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color_light));
				if(activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.home))){
					imageViewAddressType.setImageResource(R.drawable.ic_home);
					textViewAddressName.setText(activity.getString(R.string.home));
				}
				else if(activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.work))){
					imageViewAddressType.setImageResource(R.drawable.ic_work);
					textViewAddressName.setText(activity.getString(R.string.work));
				}
				else {
					imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
					textViewAddressName.setText(activity.getSelectedAddressType());
				}
			}
		} else {
			textViewAddressValue.setText(activity.getResources().getString(R.string.add_address));
			imageViewAddressType.setImageResource(R.drawable.ic_exclamation_address);
			imageViewDeliveryAddressForward.getDrawable().mutate().setColorFilter(ContextCompat.getColor(activity,R.color.red_alert_no_address), PorterDuff.Mode.SRC_ATOP);
			int padding = activity.getResources().getDimensionPixelSize(R.dimen.dp_2);
			imageViewAddressType.setPaddingRelative(padding,padding,padding,padding);
			textViewAddressValue.setVisibility(View.GONE);
			tvNoAddressAlert.setVisibility(View.VISIBLE);
		}
	}

	@Subscribe
	public void onUpdateListEvent(AddressAdded event) {
		if (event.flag) {
			updateAddressView();
		}
	}

	@Override
	public void onStart() {
		mBus.register(this);
		super.onStart();
	}

	@Override
	public void onStop() {
		mBus.unregister(this);
		super.onStop();
	}

	public void apiCreateTask(final String jobDescription, final String jobPickupDateTime, final String jobDeliveryDateTime,
							  final String timeZoneDiff) {
		try {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
				params.put(Constants.KEY_CUSTOMER_ADDRESS, activity.getSelectedAddress());
				params.put(Constants.KEY_CUSTOMER_EMAIL, Data.userData.userEmail);
				params.put(Constants.KEY_CUSTOMER_USERNAME, Data.userData.userName);
				params.put(Constants.KEY_CUSTOMER_PHONE, Data.userData.phoneNo);
				params.put(Constants.KEY_JOB_DESCRIPTION, prosProductDatum.getName()
						+ (!TextUtils.isEmpty(jobDescription) ? (Constants.SPLITTER_PRODUCT_NAME_DESCRIPTION+
						jobDescription):""));
				params.put(Constants.KEY_JOB_PICKUP_DATETIME, jobPickupDateTime);
				params.put(Constants.KEY_JOB_DELIVERY_DATETIME, jobDeliveryDateTime);
				params.put(Constants.KEY_TIMEZONE, timeZoneDiff);
				params.put(Constants.KEY_CUSTOM_FIELD_TEMPLATE, Constants.KEY_JUGNOO_PROS);

				JSONArray jsonArray = new JSONArray();
				JSONObject jObj1 = new JSONObject();
				jObj1.put(Constants.KEY_LABEL, Constants.KEY_PRODUCT_ID);
				jObj1.put(Constants.KEY_DATA, prosProductDatum.getProductId());
				jsonArray.put(jObj1);
				JSONObject jObj2 = new JSONObject();
				jObj2.put(Constants.KEY_LABEL, Constants.KEY_PRODUCT_NAME);
				jObj2.put(Constants.KEY_DATA, prosProductDatum.getName());
				jsonArray.put(jObj2);
				JSONObject jObj3 = new JSONObject();
				jObj3.put(Constants.KEY_LABEL, Constants.KEY_JOB_AMOUNT);
				jObj3.put(Constants.KEY_DATA, prosProductDatum.getPrice());
				jsonArray.put(jObj3);
				params.put(Constants.KEY_META_DATA, String.valueOf(jsonArray));

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().createTaskViaVendor(params, new Callback<CreateTaskData>() {
					@Override
					public void success(final CreateTaskData productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						try {
							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
								if (productsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
									new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
										@Override
										public void onDismiss() {
											activity.setProsTaskCreated(true);
											activity.getSupportFragmentManager().popBackStack(ProsProductsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//											activity.getTransactionUtils().addProsOrderStatusFragment(activity, activity.getRelativeLayoutContainer(), productsResponse.getData().getJobId());
										}
									}).show(String.valueOf(productsResponse.getData().getJobId()),
											tvSelectTimeSlot.getText().toString(),
											tvSelectDate.getText().toString(), true, "",
											null, AppConstant.ApplicationType.PROS, productsResponse.getMessage());
								} else {
									DialogPopup.alertPopup(activity, "", productsResponse.getMessage());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}


						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST, jobDescription, jobPickupDateTime, jobDeliveryDateTime, timeZoneDiff);
					}
				});
			} else {
				retryDialog(DialogErrorType.NO_NET, jobDescription, jobPickupDateTime, jobDeliveryDateTime, timeZoneDiff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType, final String jobDescription, final String jobPickupDateTime, final String jobDeliveryDateTime, final String timeZoneDiff) {
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						apiCreateTask(jobDescription, jobPickupDateTime, jobDeliveryDateTime, timeZoneDiff);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
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
							if(setTimeToVars(timeDisplay.getValue(), timeDisplay.getDisplay())){
								if(timeSelectorDialog != null) {
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
					if(timeSelectorDialog != null) {
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
		for(int i=8; i<21; i++){
			int j = i+1;
			TimeDisplay timeDisplay = new TimeDisplay((i>12?(i-12):i)+" "+(i>11?"PM":"AM")
					+" - "+(j>12?(j-12):j)+" "+(j>11?"PM":"AM"),
					i+":00:00");
			timeDisplays.add(timeDisplay);
		}
		return timeDisplays;
	}

	private boolean validateDateTime(String date, String time){
		String currentTimePlus24Hrs = DateOperations.getDaysAheadTime(DateOperations.getCurrentTime(), 2);
		return DateOperations.getTimeDifference(getFormattedDateTime(date, time, true), currentTimePlus24Hrs) > 0
				&&
				DateOperations.getTimeDifference(getFormattedDateTime(date, time, false),
						DateOperations.addCalendarFieldValueToDateTime(currentTimePlus24Hrs, 31, Calendar.DAY_OF_MONTH)) < 0;
	}

}
