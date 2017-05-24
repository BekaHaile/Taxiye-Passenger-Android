package com.sabkuchfresh.home;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 1/26/17.
 */

public class DeliveryAddressView {

	public LinearLayout llLocation;
	public TextView tvDeliveryAddress, tvLocation;
	public View shadowTop;
	private TextView tvConfirmAddress;
	private View viewArrowConfirmAddress;

	public DeliveryAddressView(Context context, View rootView){
		init(context, rootView);
	}

	private void init(Context context, View rootView){
		llLocation = (LinearLayout) rootView.findViewById(R.id.llLocation);
		llLocation.setVisibility(View.VISIBLE);
		tvDeliveryAddress = (TextView) rootView.findViewById(R.id.tvDeliveryAddress); tvDeliveryAddress.setTypeface(Fonts.mavenMedium(context));
		tvLocation = (TextView) rootView.findViewById(R.id.tvLocation); tvLocation.setTypeface(Fonts.mavenMedium(context));
		tvConfirmAddress = (TextView) rootView.findViewById(R.id.tv_confirm_address);
		viewArrowConfirmAddress = (View) rootView.findViewById(R.id.arrow_down);
		shadowTop = (View) rootView.findViewById(R.id.shadow_top);
	}

	public void scaleView(){
		ASSL.DoMagic(llLocation);
	}

	public TextView getTvLocation() {
		return tvLocation;
	}

	public View getShadowTop() {
		return shadowTop;
	}

	public void showConfirmAddressBar(boolean show){

		tvConfirmAddress.setVisibility(show?View.VISIBLE:View.GONE);
		viewArrowConfirmAddress.setVisibility(show?View.VISIBLE:View.GONE);
	}
}
