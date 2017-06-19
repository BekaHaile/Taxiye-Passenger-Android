package com.sabkuchfresh.pros.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;

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
	private View rootView;
	private FreshActivity activity;
	private SubItem subItem;

	public static ProsCheckoutFragment newInstance(SubItem subItem) {
		ProsCheckoutFragment fragment = new ProsCheckoutFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.KEY_SUB_ITEM, subItem);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
		subItem = (SubItem) getArguments().getSerializable(Constants.KEY_SUB_ITEM);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_pros_product_checkout, container, false);

		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);
		parseArguments();
		ButterKnife.bind(this, rootView);

		tvProductName.setText(subItem.getSubItemName());

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
				break;
			case R.id.tvSelectTimeSlot:
				break;
			case R.id.relativeLayoutCash:
				break;
		}
	}
}
