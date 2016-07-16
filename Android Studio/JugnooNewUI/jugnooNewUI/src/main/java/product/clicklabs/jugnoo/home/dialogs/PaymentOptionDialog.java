package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.http.HEAD;

/**
 * Created by shankar on 5/2/16.
 */
public class PaymentOptionDialog implements View.OnClickListener {

	private final String TAG = PaymentOptionDialog.class.getSimpleName();
	private HomeActivity activity;
	private Callback callback;
	private Dialog dialog = null;

	private LinearLayout linearLayoutWalletContainer;
	private RelativeLayout relativeLayoutPaytm;
	private RelativeLayout relativeLayoutMobikwik;
	private LinearLayout linearLayoutCash;
	private ImageView radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash;
	private TextView textViewPaytm, textViewPaytmValue, textViewMobikwik, textViewMobikwikValue;

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

			linearLayoutWalletContainer = (LinearLayout) dialog.findViewById(R.id.linearLayoutWalletContainer);
			relativeLayoutPaytm = (RelativeLayout)dialog.findViewById(R.id.relativeLayoutPaytm);
			relativeLayoutMobikwik = (RelativeLayout)dialog.findViewById(R.id.relativeLayoutMobikwik);
			linearLayoutCash = (LinearLayout)dialog.findViewById(R.id.linearLayoutCash);
			radioBtnPaytm = (ImageView)dialog.findViewById(R.id.radio_paytm);
			imageViewRadioMobikwik = (ImageView)dialog.findViewById(R.id.imageViewRadioMobikwik);
			radioBtnCash = (ImageView)dialog.findViewById(R.id.radio_cash);

			textViewPaytmValue = (TextView)dialog.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenLight(activity));
			textViewPaytm = (TextView)dialog.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenLight(activity));
			textViewMobikwikValue = (TextView)dialog.findViewById(R.id.textViewMobikwikValue);textViewMobikwikValue.setTypeface(Fonts.mavenLight(activity));
			textViewMobikwik = (TextView)dialog.findViewById(R.id.textViewMobikwik); textViewMobikwik.setTypeface(Fonts.mavenLight(activity));
			((TextView)dialog.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));

			relativeLayoutPaytm.setOnClickListener(this);
			relativeLayoutMobikwik.setOnClickListener(this);
			linearLayoutCash.setOnClickListener(this);

			orderPaymentModes();

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
						activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_METHOD_SELECTED, null);
                        callback.onPaymentModeUpdated();
                    }
					else if(Data.userData.getPaytmBalance() < 0){
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else{
                        if(Data.userData.getPaytmEnabled() == 1) {
                            DialogPopup.alertPopupWithListener(activity, "",
                                    activity.getResources().getString(R.string.paytm_no_cash),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(activity, PaymentActivity.class);
											intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
											intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
                                            activity.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        }
                                    });
                        }
                        else{
							MyApplication.getInstance().getWalletCore()
									.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.PAYTM.getOrdinal());
                        }
                    }
                    break;

				case R.id.relativeLayoutMobikwik:
					if(Data.userData.getMobikwikBalance() > 0) {
						Data.pickupPaymentOption = PaymentOption.MOBIKWIK.getOrdinal();
						activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
						NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_MOBIKWIK_METHOD_SELECTED, null);
						callback.onPaymentModeUpdated();
					}
					else if(Data.userData.getMobikwikBalance() < 0){
						DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
					} else{
						if(Data.userData.getMobikwikEnabled() == 1) {
							DialogPopup.alertPopupWithListener(activity, "",
									activity.getResources().getString(R.string.mobikwik_no_cash),
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Intent intent = new Intent(activity, PaymentActivity.class);
											intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
											intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MOBIKWIK.getOrdinal());
											activity.startActivity(intent);
											activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
										}
									});
						}
						else{
							MyApplication.getInstance().getWalletCore()
									.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.MOBIKWIK.getOrdinal());
						}
					}
					break;

                case R.id.linearLayoutCash:
                    if(Data.pickupPaymentOption == PaymentOption.PAYTM.getOrdinal()){
                        FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
                    }
                    Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
					activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_CASH_METHOD_SELECTED, null);
                    callback.onPaymentModeUpdated();
                    break;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSelectedPaymentOptionUI(){
		try {
			Data.pickupPaymentOption = MyApplication.getInstance().getWalletCore()
					.getPaymentOptionAccAvailability(Data.pickupPaymentOption);
			if(PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption){
				paymentSelection(radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash);
			} else if(PaymentOption.MOBIKWIK.getOrdinal() == Data.pickupPaymentOption){
				paymentSelection(imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash);
			} else{
				paymentSelection(radioBtnCash, radioBtnPaytm, imageViewRadioMobikwik);
			}
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void paymentSelection(ImageView selected, ImageView unSelected, ImageView unSelected2){
		try {
			selected.setImageResource(R.drawable.ic_radio_button_selected);
			unSelected.setImageResource(R.drawable.ic_radio_button_normal);
			unSelected2.setImageResource(R.drawable.ic_radio_button_normal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatePreferredPaymentOptionUI(){
		try{
			Data.pickupPaymentOption = MyApplication.getInstance().getWalletCore()
					.getPaymentOptionAccAvailability(Data.pickupPaymentOption);

			textViewPaytmValue.setText(String.format(activity.getResources()
					.getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
			textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

			textViewMobikwikValue.setText(String.format(activity.getResources()
					.getString(R.string.rupees_value_format_without_space), Data.userData.getMobikwikBalanceStr()));
			textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));

			if(Data.userData.getPaytmEnabled() == 1){
				textViewPaytmValue.setVisibility(View.VISIBLE);
				textViewPaytm.setText(activity.getResources().getString(R.string.paytm_wallet));
			} else{
				textViewPaytmValue.setVisibility(View.GONE);
				textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
			}
			if(Data.userData.getMobikwikEnabled() == 1){
				textViewMobikwikValue.setVisibility(View.VISIBLE);
				textViewMobikwik.setText(activity.getResources().getString(R.string.mobikwik_wallet));
			} else{
				textViewMobikwikValue.setVisibility(View.GONE);
				textViewMobikwik.setText(activity.getResources().getString(R.string.add_mobikwik_wallet));
			}

			setSelectedPaymentOptionUI();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void orderPaymentModes(){
		try{
			if(MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas() != null
					&& MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas().size() > 0){
				linearLayoutWalletContainer.removeAllViews();
				for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore()
						.getPaymentModeConfigDatas()){
					if(paymentModeConfigData.getEnabled() == 1) {
						if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
								&& Data.userData.getPaytmEnabled() == 1) {
							linearLayoutWalletContainer.addView(relativeLayoutPaytm);
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
								&& Data.userData.getMobikwikEnabled() == 1) {
							linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
						}
					}
				}

				for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore()
						.getPaymentModeConfigDatas()){
					if(paymentModeConfigData.getEnabled() == 1) {
						if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
								&& Data.userData.getPaytmEnabled() != 1) {
							linearLayoutWalletContainer.addView(relativeLayoutPaytm);
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
								&& Data.userData.getMobikwikEnabled() != 1) {
							linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
						}
					}
				}
			}
		} catch (Exception e){
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