package com.sabkuchfresh.pros.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 19/06/17.
 */

public class ProsOrderStatusFragment extends Fragment {

	@Bind(R.id.tvServiceType)
	TextView tvServiceType;
	@Bind(R.id.tvServiceTime)
	TextView tvServiceTime;
	@Bind(R.id.ivDeliveryPlace)
	ImageView ivDeliveryPlace;
	@Bind(R.id.tvDeliveryPlace)
	TextView tvDeliveryPlace;
	@Bind(R.id.llDeliveryPlace)
	LinearLayout llDeliveryPlace;
	@Bind(R.id.tvDeliveryToVal)
	TextView tvDeliveryToVal;
	@Bind(R.id.tvAmountValue)
	TextView tvAmountValue;
	@Bind(R.id.ivPaidVia)
	ImageView ivPaidVia;
	@Bind(R.id.tvPaidViaValue)
	TextView tvPaidViaValue;
	@Bind(R.id.bNeedHelp)
	Button bNeedHelp;
	private FreshActivity activity;

	public static ProsOrderStatusFragment newInstance() {
		ProsOrderStatusFragment fragment = new ProsOrderStatusFragment();
		Bundle bundle = new Bundle();

		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pros_order_status, container, false);

		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);
		parseArguments();


		ButterKnife.bind(this, rootView);

		activity.getTopBar().title.setText(activity.getString(R.string.order_id_format, String.valueOf(123)));

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.fragmentUISetup(this);
			activity.getTopBar().title.setText(activity.getString(R.string.order_id_format, String.valueOf(123)));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick({R.id.bNeedHelp})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.bNeedHelp:

				break;
		}
	}
}
