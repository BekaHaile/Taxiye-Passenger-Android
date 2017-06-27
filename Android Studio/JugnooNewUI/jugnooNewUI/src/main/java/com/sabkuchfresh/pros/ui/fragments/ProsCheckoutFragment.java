package com.sabkuchfresh.pros.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.pros.models.CreateTaskData;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
	@Bind(R.id.editTextDeliveryInstructions)
	EditText editTextDeliveryInstructions;
	@Bind(R.id.tvRateCard)
	TextView tvRateCard;
	@Bind(R.id.imageViewAddressType)
	ImageView imageViewAddressType;
	@Bind(R.id.textViewAddressName)
	TextView textViewAddressName;
	@Bind(R.id.textViewAddressValue)
	TextView textViewAddressValue;
	@Bind(R.id.relativeLayoutDeliveryAddress)
	RelativeLayout relativeLayoutDeliveryAddress;
	@Bind(R.id.imageViewDeliveryAddressForward)
	ImageView imageViewDeliveryAddressForward;
	@Bind(R.id.tvNoAddressAlert)
	TextView tvNoAddressAlert;
	@Bind(R.id.tvSelectDate)
	TextView tvSelectDate;
	@Bind(R.id.tvSelectTimeSlot)
	TextView tvSelectTimeSlot;
	@Bind(R.id.imageViewCashRadio)
	ImageView imageViewCashRadio;
	@Bind(R.id.relativeLayoutCash)
	RelativeLayout relativeLayoutCash;
	@Bind(R.id.llPaymentOptions)
	LinearLayout llPaymentOptions;
	@Bind(R.id.scrollView)
	ScrollView scrollView;
	@Bind(R.id.linearLayoutRoot)
	RelativeLayout linearLayoutRoot;
	@Bind(R.id.tvProductName)
	TextView tvProductName;
	private FreshActivity activity;
	private ProsProductData.ProsProductDatum prosProductDatum;
	private Bus mBus;
	private String selectedDate, selectedTime;

	public static ProsCheckoutFragment newInstance(ProsProductData.ProsProductDatum prosProductDatum) {
		ProsCheckoutFragment fragment = new ProsCheckoutFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.KEY_PRODUCT_DATUM, prosProductDatum);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
		prosProductDatum = (ProsProductData.ProsProductDatum) getArguments().getSerializable(Constants.KEY_PRODUCT_DATUM);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pros_product_checkout, container, false);

		activity = (FreshActivity) getActivity();
		mBus = activity.getBus();
		activity.fragmentUISetup(this);
		parseArguments();
		ButterKnife.bind(this, rootView);

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
				String finalDateTime = selectedDate+" "+selectedTime;
				apiCreateTask(editTextDeliveryInstructions.getText().toString().trim(),
						finalDateTime, DateOperations.addHoursToDateTime(finalDateTime, 1),
						String.valueOf(-1*DateOperations.getTimezoneDiffWithUTC()));
			}
		});

		updateAddressView();

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.fragmentUISetup(this);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		activity.bRequestBooking.setOnClickListener(null);
	}

	@OnClick({R.id.tvRateCard, R.id.relativeLayoutDeliveryAddress,
			R.id.tvSelectDate, R.id.tvSelectTimeSlot, R.id.relativeLayoutCash})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tvRateCard:
				break;
			case R.id.relativeLayoutDeliveryAddress:
				activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
				break;
			case R.id.tvSelectDate:
				getDatePickerFragment().show(getChildFragmentManager(), "datePicker", onDateSetListener);
				break;
			case R.id.tvSelectTimeSlot:
				getTimePickerFragment().show(getChildFragmentManager(), "timePicker", onTimeSetListener);
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
			selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;
			tvSelectDate.setText(DateOperations.getDateFormatted(selectedDate));
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
			selectedTime = hourOfDay+":"+minute+":00";
			tvSelectTimeSlot.setText(DateOperations.convertDayTimeAPViaFormat(hourOfDay+":"+minute+":00"));
		}
	};

	public boolean addressSelectedNotValid(){
		return TextUtils.isEmpty(activity.getSelectedAddressType());
	}

	public void updateAddressView(){
		imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
		imageViewAddressType.setPadding(0,0,0,0);
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
			imageViewAddressType.setPadding(padding,padding,padding,padding);
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
				params.put(Constants.KEY_JOB_DESCRIPTION, jobDescription);
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
											// TODO: 27/06/17 revert this
//											activity.getSupportFragmentManager().popBackStack(ProsProductsFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
											activity.getTransactionUtils().addProsOrderStatusFragment(activity, activity.getRelativeLayoutContainer(), productsResponse.getData().getJobId());
										}
									}).show(String.valueOf(productsResponse.getData().getJobId()),
											tvSelectTimeSlot.getText().toString(),
											tvSelectDate.getText().toString(), true, "",
											null, AppConstant.ApplicationType.PROS);
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

}
