package com.sabkuchfresh.home;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.utils.Utils;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshWalletBalanceLowDialog {

	private final String TAG = FreshWalletBalanceLowDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;

	public FreshWalletBalanceLowDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(int messageRes, String amount, String currencyCode, String currency, int walletIcon) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_fresh_wallet_balance_low);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView)dialog.findViewById(R.id.textViewBalanceRunningLow)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

			ImageView imageViewWalletIcon = (ImageView) dialog.findViewById(R.id.imageViewWalletIcon);
			imageViewWalletIcon.setImageResource(walletIcon);

			TextView textViewLessAmount = (TextView) dialog.findViewById(R.id.textViewLessAmount);
			textViewLessAmount.setTypeface(Fonts.mavenRegular(activity));
			textViewLessAmount.setText(messageRes);

			TextView textViewLessAmountValue = (TextView) dialog.findViewById(R.id.textViewLessAmountValue);
			textViewLessAmountValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			try {
				if(Double.parseDouble(amount)<0) {
					textViewLessAmountValue.setText("-" + Utils.formatCurrencyAmount(Double.parseDouble(amount), currencyCode, currency).replaceAll("-", ""));
				} else {
					textViewLessAmountValue.setText(amount);
				}
			} catch (Exception e) {
				e.printStackTrace();
				textViewLessAmountValue.setText(amount);
			}

			Button buttonRechargeNow = (Button) dialog.findViewById(R.id.buttonRechargeNow);
			buttonRechargeNow.setTypeface(Fonts.mavenRegular(activity));
			Button buttonPayViaOther = (Button) dialog.findViewById(R.id.buttonPayViaOther);
			buttonPayViaOther.setTypeface(Fonts.mavenRegular(activity));

			buttonRechargeNow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onRechargeNowClicked();
					dialog.dismiss();
				}
			});

			buttonPayViaOther.setOnClickListener(new View.OnClickListener() {
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
