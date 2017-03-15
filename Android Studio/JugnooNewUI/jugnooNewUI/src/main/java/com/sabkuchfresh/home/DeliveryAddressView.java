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

	public DeliveryAddressView(Context context, View rootView){
		init(context, rootView);
	}

	private void init(Context context, View rootView){
		llLocation = (LinearLayout) rootView.findViewById(R.id.llLocation);
		llLocation.setVisibility(View.VISIBLE);
		tvDeliveryAddress = (TextView) rootView.findViewById(R.id.tvDeliveryAddress); tvDeliveryAddress.setTypeface(Fonts.mavenMedium(context));
		tvLocation = (TextView) rootView.findViewById(R.id.tvLocation); tvLocation.setTypeface(Fonts.mavenMedium(context));
	}

	public void scaleView(){
		ASSL.DoMagic(llLocation);
	}

}
