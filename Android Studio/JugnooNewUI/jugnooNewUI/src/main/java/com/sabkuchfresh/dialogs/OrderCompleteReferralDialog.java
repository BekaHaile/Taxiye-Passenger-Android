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

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Font;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class OrderCompleteReferralDialog {

	private Context context;
	private Callback callback;
	private Dialog dialog;
	private PlaceOrderResponse.ReferralPopupContent referralPopupContent;
	private int engagementId;
	private int orderId;
	private int productType;

	public OrderCompleteReferralDialog(Context context, Callback callback) {
		this.context = context;
		this.callback = callback;
	}


	public Dialog show(boolean showOrderDetails, String orderTime, String orderDay, String orderText,
					   PlaceOrderResponse.ReferralPopupContent referralPopupContent,
					   int engagementId, int orderId, int productType) {
		try {
			this.engagementId = engagementId;
			this.orderId = orderId;
			this.productType = productType;
			this.referralPopupContent = referralPopupContent;
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
				RelativeLayout rlOrderDay = (RelativeLayout) dialog.findViewById(R.id.rlOrderDay);
				TextView tvOrderTimeVal = (TextView) dialog.findViewById(R.id.tvOrderTimeVal);
				tvOrderTimeVal.setTypeface(fonts.mavenMedium(context));
				TextView tvOrderDayVal = (TextView) dialog.findViewById(R.id.tvOrderDayVal);
				tvOrderDayVal.setTypeface(fonts.mavenMedium(context));

				if(productType == ProductType.MENUS.getOrdinal()){
					rlOrderDay.setVisibility(View.GONE);
					tvOrderTimeVal.setVisibility(View.GONE);
					tvYourOrder.setText(orderText);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvYourOrder.getLayoutParams();
					params.setMargins(0, 0, 0, (int) (ASSL.Yscale() * 26f));
					tvYourOrder.setLayoutParams(params);
				} else {
					tvOrderTimeVal.setText(orderTime);
					tvOrderDayVal.setText(orderDay);
				}
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
					apiReferralUserEvent(OrderCompleteReferralDialog.this.engagementId,
							OrderCompleteReferralDialog.this.orderId,
							OrderCompleteReferralDialog.this.productType,
							OrderCompleteReferralDialog.this.referralPopupContent.getButtonId());
				}
			});

			bLater.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onDialogDismiss();
					apiReferralUserEvent(OrderCompleteReferralDialog.this.engagementId,
							OrderCompleteReferralDialog.this.orderId,
							OrderCompleteReferralDialog.this.productType,
							0);

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



	private void apiReferralUserEvent(int engagementId, int orderId, int productType, int event) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			if (productType == ProductType.AUTO.getOrdinal()) {
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
			} else {
				params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));
			}
			params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(productType));
			params.put(Constants.KEY_EVENT, String.valueOf(event));

			retrofit.Callback<SettleUserDebt> callback = new retrofit.Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt historyResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i("referralUserEvent Server response", "response = " + responseStr);
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("TAG", "referralUserEvent error=" + error.toString());
				}
			};

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().referralUserEvent(params, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}