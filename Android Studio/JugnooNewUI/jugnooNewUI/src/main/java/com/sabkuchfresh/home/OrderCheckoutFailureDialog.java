package com.sabkuchfresh.home;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 3/4/16.
 */
public class OrderCheckoutFailureDialog {

	private final String TAG = OrderCheckoutFailureDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;

	public OrderCheckoutFailureDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(String titleStr) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_order_checkout_failure);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			if(titleStr == null || titleStr.equalsIgnoreCase("")){
				titleStr = activity.getString(R.string.pay_unsuccessful_message);
			}

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView tvTitle = (TextView)dialog.findViewById(R.id.tvTitle); tvTitle.setTypeface(Fonts.mavenRegular(activity));
			tvTitle.setText(titleStr);


			Button buttonRechargeNow = (Button) dialog.findViewById(R.id.buttonRechargeNow);
			buttonRechargeNow.setTypeface(Fonts.mavenRegular(activity));
			Button buttonPayViaOther = (Button) dialog.findViewById(R.id.buttonPayViaOther);
			buttonPayViaOther.setTypeface(Fonts.mavenRegular(activity));

			buttonRechargeNow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onRetryClicked();
					dialog.dismiss();
				}
			});

			buttonPayViaOther.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onChangePaymentClicked();
					dialog.dismiss();
				}
			});

			linearLayoutInner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback{
		void onRetryClicked();
		void onChangePaymentClicked();
	}

}
