package com.sabkuchfresh.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Font;
import product.clicklabs.jugnoo.utils.Fonts;


public class OrderCompleteReferralDialog {

	private final String TAG = OrderCompleteReferralDialog.class.getSimpleName();
	private Context context;
	private Callback callback;
	private Dialog dialog = null;

	public OrderCompleteReferralDialog(Context context, Callback callback) {
		this.context = context;
		this.callback = callback;
	}


	public Dialog show(boolean showOrderDetails, String orderTime, String orderDay,
					   PlaceOrderResponse.ReferralPopupContent referralPopupContent) {
		try {
			Font fonts = new Font();
			dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogDown;
			dialog.setContentView(R.layout.dialog_order_complete_referral);

			RelativeLayout rlRoot = (RelativeLayout) dialog.findViewById(R.id.rlRoot);
			new ASSL(context, rlRoot, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			LinearLayout llOrderDetails = (LinearLayout) dialog.findViewById(R.id.llOrderDetails);
			if(showOrderDetails) {
				llOrderDetails.setVisibility(View.VISIBLE);
				TextView tvThankyou = (TextView) dialog.findViewById(R.id.tvThankyou);
				tvThankyou.setTypeface(fonts.mavenMedium(context), Typeface.BOLD);
				TextView tvYourOrder = (TextView) dialog.findViewById(R.id.tvYourOrder);
				tvYourOrder.setTypeface(fonts.mavenRegular(context));
				TextView tvOrderTimeVal = (TextView) dialog.findViewById(R.id.tvOrderTimeVal);
				tvOrderTimeVal.setTypeface(fonts.mavenMedium(context));
				TextView tvOrderDayVal = (TextView) dialog.findViewById(R.id.tvOrderDayVal);
				tvOrderDayVal.setTypeface(fonts.mavenMedium(context));

				tvOrderTimeVal.setText(orderTime);
				tvOrderDayVal.setText(orderDay);
			}

			ImageView ivReferralImage = (ImageView)dialog.findViewById(R.id.ivReferralImage);
			TextView tvReferralHeading = (TextView) dialog.findViewById(R.id.tvReferralHeading); tvReferralHeading.setTypeface(fonts.mavenMedium(context), Typeface.BOLD);
			TextView tvReferralText = (TextView) dialog.findViewById(R.id.tvReferralText); tvReferralText.setTypeface(fonts.mavenMedium(context));

			Button bLater = (Button) dialog.findViewById(R.id.bLater);bLater.setTypeface(Fonts.mavenRegular(context));
			Button bSendGift = (Button) dialog.findViewById(R.id.bSendGift);bSendGift.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);


			if(!TextUtils.isEmpty(referralPopupContent.getImageUrl())) {
				Picasso.with(context).load(referralPopupContent.getImageUrl())
						.placeholder(R.drawable.ic_notification_placeholder)
						.into(ivReferralImage);
			} else {
				ivReferralImage.setVisibility(View.GONE);
			}
			tvReferralHeading.setText(referralPopupContent.getHeading());
			tvReferralText.setText(referralPopupContent.getText());
			bSendGift.setText(referralPopupContent.getButtonText());

			bSendGift.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onConfirmed();
				}
			});

			bLater.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onDialogDismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dialog;
	}


	public interface Callback{
		void onDialogDismiss();
		void onConfirmed();
	}

}