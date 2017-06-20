package com.sabkuchfresh.pros.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.pros.utils.TimePickerFragment;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.DateOperations;

/**
 * Created by shankar on 19/06/17.
 */

public class ProsCheckoutFragment extends Fragment {

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
				Dialog dialogOrderComplete = new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
					@Override
					public void onDismiss() {
						activity.orderComplete();
					}
				}).show(String.valueOf(2334),
						"02:00PM - 06:00PM", "24 June,17", true, "",
						null, AppConstant.ApplicationType.PROS);
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
			tvSelectDate.setText(DateOperations.getDateFormatted(year+"-"+(month+1)+"-"+dayOfMonth));
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
			imageViewDeliveryAddressForward.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_back_pay_selector));
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

}
