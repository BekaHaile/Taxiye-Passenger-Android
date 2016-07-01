package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.wallet.PaymentActivity;

/**
 * Created by shankar on 5/2/16.
 */
public class PaymentOptionDialog implements View.OnClickListener {

	private final String TAG = PaymentOptionDialog.class.getSimpleName();
	private HomeActivity activity;
	private Callback callback;
	private Dialog dialog = null;

	private LinearLayout linearLayoutCash;
	private ImageView radioBtnPaytm, radioBtnCash;
	private TextView textViewPaytm, textViewPaytmValue;
	private RelativeLayout relativeLayoutPaytm;
	private ProgressWheel progressBarPaytm;

	public PaymentOptionDialog(HomeActivity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PaymentOptionDialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_payment_option);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView) dialog.findViewById(R.id.textViewPayForRides)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			relativeLayoutPaytm = (RelativeLayout)dialog.findViewById(R.id.relativeLayoutPaytm);
			linearLayoutCash = (LinearLayout)dialog.findViewById(R.id.linearLayoutCash);
			radioBtnPaytm = (ImageView)dialog.findViewById(R.id.radio_paytm);
			radioBtnCash = (ImageView)dialog.findViewById(R.id.radio_cash);
			textViewPaytmValue = (TextView)dialog.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenLight(activity));
			textViewPaytm = (TextView)dialog.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenLight(activity));
			((TextView)dialog.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));
			progressBarPaytm = (ProgressWheel) dialog.findViewById(R.id.progressBarPaytm);

			relativeLayoutPaytm.setOnClickListener(this);
			linearLayoutCash.setOnClickListener(this);

			updatePreferredPaymentOptionUI();

			ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
			imageViewClose.setOnClickListener(new View.OnClickListener() {
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

			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.onDialogDismiss();
				}
			});


			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()){
                case R.id.relativeLayoutPaytm:
                    if(Data.userData.getPaytmBalance() > 0) {
                        Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                        setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_METHOD_SELECTED, null);
                        callback.onPaymentModeUpdated();
                    } else if(Data.userData.getPaytmError() == 1){
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else{
                        if(Data.userData.paytmEnabled == 1
                                && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                            DialogPopup.alertPopupWithListener(activity, "",
                                    activity.getResources().getString(R.string.paytm_no_cash),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(activity, PaymentActivity.class);
                                            if(Data.userData.paytmEnabled == 1) {
                                                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
                                            } else {
                                                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
                                            }
                                            activity.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        }
                                    });
                        }
                        else{
                            activity.getSlidingBottomPanel().getRequestRideOptionsFragment().openPaymentActivityInCaseOfPaytmNotAdded();
                        }
                    }
                    break;

                case R.id.linearLayoutCash:
                    if(Data.pickupPaymentOption == PaymentOption.PAYTM.getOrdinal()){
                        FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
                    }
                    Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                    setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_CASH_METHOD_SELECTED, null);
                    callback.onPaymentModeUpdated();
                    break;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSelectedPaymentOptionUI(int pickupPaymentOption){
		try {
			if(PaymentOption.PAYTM.getOrdinal() == pickupPaymentOption){
				paymentSelection(radioBtnPaytm, radioBtnCash);
			} else{
				paymentSelection(radioBtnCash, radioBtnPaytm);
			}
            activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void paymentSelection(ImageView selected, ImageView unSelected){
		try {
			selected.setImageResource(R.drawable.ic_radio_button_selected);
			unSelected.setImageResource(R.drawable.ic_radio_button_normal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatePreferredPaymentOptionUI(){
		try{
			int preferredPaymentOption = Data.pickupPaymentOption;
			if(PaymentOption.PAYTM.getOrdinal() == preferredPaymentOption){
				if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
					Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
					progressBarPaytm.setVisibility(View.GONE);
				}
				else if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)){
					Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
					progressBarPaytm.setVisibility(View.GONE);
				}
				else{
					Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
					progressBarPaytm.setVisibility(View.VISIBLE);
				}
			}
			else{
				Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
				progressBarPaytm.setVisibility(View.GONE);
			}

			textViewPaytmValue.setText(String.format(activity.getResources()
					.getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));

			if(Data.userData.paytmEnabled == 1 && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
				textViewPaytmValue.setVisibility(View.VISIBLE);
				textViewPaytm.setText(activity.getResources().getString(R.string.nl_paytm_wallet));
			}
			else{
				textViewPaytmValue.setVisibility(View.GONE);
				textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
			}

			if(Data.userData.getPaytmError() == 1){
				Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
				relativeLayoutPaytm.setVisibility(View.GONE);
			}
			else{
				relativeLayoutPaytm.setVisibility(View.VISIBLE);
			}

			setSelectedPaymentOptionUI(Data.pickupPaymentOption);

			textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setPaytmLoadingVisiblity(int visiblity){
		try {
			progressBarPaytm.setVisibility(visiblity);
			if(visiblity == View.VISIBLE) {
				textViewPaytmValue.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dialog getDialog(){
		return dialog;
	}


	public interface Callback{
		void onDialogDismiss();
		void onPaymentModeUpdated();
	}

}