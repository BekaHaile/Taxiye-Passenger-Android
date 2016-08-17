package com.sabkuchfresh.home;

import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.utils.Fonts;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshPaytmBalanceLowDialog {

	private final String TAG = FreshPaytmBalanceLowDialog.class.getSimpleName();
	private FreshActivity activity;
	private Callback callback;
	private String amount;
	private Dialog dialog;

	public FreshPaytmBalanceLowDialog(FreshActivity activity, String amount, Callback callback) {
		this.activity = activity;
		this.amount = amount;
		this.callback = callback;
	}

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_fresh_paytm_balance_low);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView)dialog.findViewById(R.id.textViewBalanceRunningLow)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			((TextView)dialog.findViewById(R.id.textViewLessAmount)).setTypeface(Fonts.mavenRegular(activity));

			TextView textViewLessAmountValue = (TextView) dialog.findViewById(R.id.textViewLessAmountValue);
			textViewLessAmountValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			textViewLessAmountValue.setText(amount);

			Button buttonRechargeNow = (Button) dialog.findViewById(R.id.buttonRechargeNow);
			buttonRechargeNow.setTypeface(Fonts.mavenRegular(activity));
			Button buttonPayViaCash = (Button) dialog.findViewById(R.id.buttonPayViaCash);
			buttonPayViaCash.setTypeface(Fonts.mavenRegular(activity));

			buttonRechargeNow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onRechargeNowClicked();
					dialog.dismiss();
				}
			});

			buttonPayViaCash.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onPayByCashClicked();
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
		void onRechargeNowClicked();
		void onPayByCashClicked();
	}

}
