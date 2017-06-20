package com.sabkuchfresh.pros.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.utils.DatePickerFragment;
import com.sabkuchfresh.utils.AppConstant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;

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
				break;
			case R.id.tvSelectDate:
				getDatePickerFragment().show(getChildFragmentManager(), "datePicker", new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						tvSelectDate.setText(year+":"+(month+1)+":"+dayOfMonth);
					}
				});
				break;
			case R.id.tvSelectTimeSlot:
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
}
