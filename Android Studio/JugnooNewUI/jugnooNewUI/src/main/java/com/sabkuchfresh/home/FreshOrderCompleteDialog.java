package com.sabkuchfresh.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.utils.AppConstant;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshOrderCompleteDialog extends OrderCompletDialog {

	private final String TAG = FreshOrderCompleteDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;

	public FreshOrderCompleteDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(String orderId, String deliverySlot, String deliveryDay, boolean showDeliverySlot,
					   String restaurantName, PlaceOrderResponse placeOrderResponse, int appType, String prosTaskCreatedMessage) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_fresh_order_complete);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView textView = (TextView) dialog.findViewById(R.id.textViewThankYou); textView.setTypeface(Fonts.mavenRegular(activity));
			RelativeLayout rlStarContainer = (RelativeLayout) dialog.findViewById(R.id.rlStarContainer);
			TextView tvDidYou = (TextView) dialog.findViewById(R.id.tvDidYou); tvDidYou.setTypeface(Fonts.mavenMedium(activity));
			TextView tvDescription = (TextView) dialog.findViewById(R.id.tvDescription); tvDescription.setTypeface(Fonts.mavenRegular(activity));
			TextView tvClickToFindOut = (TextView) dialog.findViewById(R.id.tvClickToFindOut); tvClickToFindOut.setTypeface(Fonts.mavenMedium(activity));

			if(Data.userData.getShowSubscriptionData() == 1
					&& placeOrderResponse != null
					&& placeOrderResponse.getSubscriptionMessage() != null){
				rlStarContainer.setVisibility(View.VISIBLE);
				tvDidYou.setText(placeOrderResponse.getSubscriptionMessage().getHeading());
				tvDescription.setText(placeOrderResponse.getSubscriptionMessage().getContent());
				tvClickToFindOut.setText(placeOrderResponse.getSubscriptionMessage().getLinkText());
			} else {
				rlStarContainer.setVisibility(View.GONE);
			}


			if(appType == AppConstant.ApplicationType.MEALS) {
				if(placeOrderResponse == null || TextUtils.isEmpty(placeOrderResponse.getOrderPlacedMessage())) {
					textView.setText(activity.getString(R.string.thank_you_for_placing_order_meals, activity.getString(R.string.app_name)));

				} else {
					textView.setText(Utils.trimHTML(Utils.fromHtml(placeOrderResponse.getOrderPlacedMessage())));
				}
			}
			else if(appType == AppConstant.ApplicationType.GROCERY) {
				textView.setText(activity.getString(R.string.thank_you_for_placing_order_grocery, activity.getString(R.string.app_name)));
			}
			else if(appType == AppConstant.ApplicationType.MENUS || appType == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
				if(placeOrderResponse == null || TextUtils.isEmpty(placeOrderResponse.getOrderPlacedMessage())) {
					textView.setText(activity.getResources().getString(R.string.thank_you_for_placing_order_menus_format, restaurantName));
				} else {
					textView.setText(Utils.trimHTML(Utils.fromHtml(placeOrderResponse.getOrderPlacedMessage())));
				}
			}
			else if(appType == AppConstant.ApplicationType.PROS) {
				if(TextUtils.isEmpty(prosTaskCreatedMessage)) {
					textView.setText(activity.getResources().getString(R.string.your_service_request_confirm_message));
				} else {
					textView.setText(Utils.trimHTML(Utils.fromHtml(prosTaskCreatedMessage)));
				}
			}else if(appType==AppConstant.ApplicationType.FEED){
				if(TextUtils.isEmpty(prosTaskCreatedMessage)) {
					textView.setText(activity.getResources().getString(R.string.your_service_request_confirm_message));
				} else {
					textView.setText(Utils.trimHTML(Utils.fromHtml(prosTaskCreatedMessage)));
				}
			}

			TextView textViewOrderId = (TextView) dialog.findViewById(R.id.textViewOrderId);
			textViewOrderId.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewOrderDeliverySlot = (TextView) dialog.findViewById(R.id.textViewOrderDeliverySlot);
			textViewOrderDeliverySlot.setTypeface(Fonts.mavenRegular(activity));

			if(appType == AppConstant.ApplicationType.PROS){
				textViewOrderId.setText(activity.getResources().getString(R.string.service_id));
				textViewOrderId.append(" ");
				final SpannableStringBuilder sb = new SpannableStringBuilder(orderId);
				sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewOrderId.append(sb);

				if (showDeliverySlot) {
					final SpannableStringBuilder sb1 = new SpannableStringBuilder(activity.getString(R.string.date_and_time));
					sb1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewOrderDeliverySlot.setText(sb1);
					textViewOrderDeliverySlot.append("\n");
					textViewOrderDeliverySlot.append(deliverySlot);
					textViewOrderDeliverySlot.append("\n");
					textViewOrderDeliverySlot.append(deliveryDay);
				}

			} else {
				if (showDeliverySlot) {
					textViewOrderDeliverySlot.setText(deliverySlot);
					textViewOrderDeliverySlot.append("\n");
					textViewOrderDeliverySlot.append(deliveryDay);
				}

				textViewOrderId.setText(activity.getResources().getString(R.string.your_order_id));

				final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
				final SpannableStringBuilder sb = new SpannableStringBuilder(orderId);
				sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewOrderId.append(sb);
				if (showDeliverySlot) {
					textViewOrderId.append("\n");
					textViewOrderId.append(activity.getResources().getString(R.string.will_be_delivered_between));
				}
			}

			RelativeLayout relativeslot = (RelativeLayout) dialog.findViewById(R.id.relativeslot);
			if(!showDeliverySlot){
				relativeslot.setVisibility(View.GONE);
			}

			Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
			buttonOk.setTypeface(Fonts.mavenRegular(activity));

			buttonOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			linearLayoutInner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});

			rlStarContainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					activity.startActivity(new Intent(activity, JugnooStarActivity.class));
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.onDismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

    public Dialog showNoDeliveryDialog() {
        try {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_fresh_not_available);

            RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);


            TextView textViewOrderId = (TextView) dialog.findViewById(R.id.textViewtop);
            textViewOrderId.setTypeface(Fonts.mavenRegular(activity));

            textViewOrderId.setText(activity.getResources().getString(R.string.delivery_pop_text));


            Button buttonOk = (Button) dialog.findViewById(R.id.btnOk);
            buttonOk.setTypeface(Fonts.mavenRegular(activity));

            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    callback.onDismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

	@Override
	public Dialog getDialog() {
		return dialog;
	}


	public interface Callback{
		void onDismiss();
	}

}
