package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.WalletType;

/**
 * Created by shankar on 7/4/16.
 */
public class WalletCore {

	private Context context;

	public WalletCore(Context context){
		this.context = context;
	}

	public void addMoneyFlurryEvent(int walletType, String amount){
		if(walletType == WalletType.PAYTM.getOrdinal()) {
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_ADD_MONEY_CLICKED, null);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Add Paytm Cash " + amount);
		}
		else if(walletType == WalletType.MOBIKWIK.getOrdinal()) {
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_MOBIKWIK_ADD_MONEY_CLICKED, null);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Add Mobikwik Cash " + amount);
		}
	}

	public void editWalletFlurryEvent(int walletType){
		if(walletType == WalletType.PAYTM.getOrdinal()) {
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_EDIT_PAYTM_CLICKED, null);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Edit");
		}
		else if(walletType == WalletType.MOBIKWIK.getOrdinal()) {
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_EDIT_MOBIKWIK_CLICKED, null);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Edit");
		}
	}

	public void removeWalletFlurryEvent(int walletType){
		if(walletType == WalletType.PAYTM.getOrdinal()) {
			FlurryEventLogger.event(context, FlurryEventNames.CLICKS_ON_REMOVE_WALLET);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Remove Wallet");
		}
		else if(walletType == WalletType.MOBIKWIK.getOrdinal()) {
			FlurryEventLogger.event(context, FlurryEventNames.CLICKS_ON_REMOVE_WALLET_MOBIKWIK);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Remove Wallet");
		}
	}


	public String getWalletBalanceStr(int walletType){
		try {
			if(walletType == WalletType.PAYTM.getOrdinal()) {
				return Data.userData.getPaytmBalanceStr();
			}
			else if(walletType == WalletType.MOBIKWIK.getOrdinal()) {
				return Data.userData.getMobikwikBalanceStr();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "--";
	}

	public int getWalletBalanceColor(int walletType){
		try {
			if(walletType == WalletType.PAYTM.getOrdinal()) {
				return Data.userData.getPaytmBalanceColor(context);
			}
			else if(walletType == WalletType.MOBIKWIK.getOrdinal()) {
				return Data.userData.getMobikwikBalanceColor(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getColor(R.color.text_color);
	}

	public void deleteWalletFlurryEvent(int walletType){
		if(walletType == WalletType.PAYTM.getOrdinal()) {
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_PAYTM_WALLET_REMOVED, null);
		}
		else if(walletType == WalletType.MOBIKWIK.getOrdinal()){
			NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_MOBIKWIK_WALLET_REMOVED, null);
		}
	}

	public void deleteWallet(int walletType){
		try {
			if(walletType == WalletType.PAYTM.getOrdinal()){
				Data.userData.deletePaytm();
			} else if(walletType == WalletType.MOBIKWIK.getOrdinal()){
				Data.userData.deleteMobikwik();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addMoneyToWalletIntent(Activity activity, int preferredPaymentMode){
		try{
			Intent intent = new Intent(activity, PaymentActivity.class);
			if(PaymentOption.PAYTM.getOrdinal() == preferredPaymentMode) {
				if (Data.userData.getPaytmEnabled() == 1) {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
				} else {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
				}
				intent.putExtra(Constants.KEY_WALLET_TYPE, WalletType.PAYTM.getOrdinal());
			}
			else if(PaymentOption.MOBIKWIK.getOrdinal() == preferredPaymentMode) {
				if (Data.userData.getMobikwikEnabled() == 1) {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
				} else {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
				}
				intent.putExtra(Constants.KEY_WALLET_TYPE, WalletType.MOBIKWIK.getOrdinal());
			}
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public boolean requestWalletBalanceCheck(final Activity activity, final int paymentOption){
		boolean callRequestRide = true;
		try {
			View.OnClickListener onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.getInstance().getWalletCore().addMoneyToWalletIntent(activity, paymentOption);
				}
			};

			if (paymentOption == PaymentOption.PAYTM.getOrdinal()) {
				if (Data.userData.getPaytmBalance() > 0) {
					callRequestRide = true;
					if (Data.fareStructure != null && Data.userData.getPaytmBalance() < Data.fareStructure.fixedFare) {
						DialogPopup.dialogBanner(activity, context.getResources().getString(R.string.paytm_low_cash));
					}
				} else {
					callRequestRide = false;
					if(Data.userData.getPaytmBalance() < 0){
						DialogPopup.alertPopup(activity, "", context.getResources().getString(R.string.paytm_error_cash_select_cash));
					} else{
						DialogPopup.alertPopupWithListener(activity, "",
								context.getResources().getString(R.string.paytm_no_cash),
								onClickListener);
					}
				}
			}
			else if (paymentOption == PaymentOption.MOBIKWIK.getOrdinal()) {
				if (Data.userData.getMobikwikBalance() > 0) {
					callRequestRide = true;
					if (Data.fareStructure != null && Data.userData.getMobikwikBalance() < Data.fareStructure.fixedFare) {
						DialogPopup.dialogBanner(activity, context.getResources().getString(R.string.mobikwik_low_cash));
					}
				} else {
					callRequestRide = false;
					if(Data.userData.getMobikwikBalance() < 0){
						DialogPopup.alertPopup(activity, "", context.getResources().getString(R.string.mobikwik_error_select_cash));
					} else{
						DialogPopup.alertPopupWithListener(activity, "",
								context.getResources().getString(R.string.mobikwik_no_cash),
								onClickListener);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callRequestRide;
	}

	public void requestRideWalletSelectedFlurryEvent(int paymentOption, String tag){
		if (paymentOption == PaymentOption.PAYTM.getOrdinal()) {
			FlurryEventLogger.event(FlurryEventNames.PAYTM_SELECTED_WHEN_REQUESTING);
			FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, tag, "paytm");
		}
		else if (paymentOption == PaymentOption.MOBIKWIK.getOrdinal()) {
			FlurryEventLogger.event(FlurryEventNames.MOBIKWIK_SELECTED_WHEN_REQUESTING);
			FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, tag, "mobikwik");
		}
		else {
			FlurryEventLogger.event(FlurryEventNames.CASH_SELECTED_WHEN_REQUESTING);
			FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, tag, "cash");
		}
	}


	public String addMoneyToWalletTextDuringRide(int preferredPaymentMode){
		try{
			if(PaymentOption.PAYTM.getOrdinal() == preferredPaymentMode) {
				if (Data.userData.getPaytmEnabled() == 1) {
					return context.getResources().getString(R.string.add_paytm_cash);
				} else {
					return context.getResources().getString(R.string.nl_add_paytm_wallet);
				}
			} else if(PaymentOption.MOBIKWIK.getOrdinal() == preferredPaymentMode){
				if (Data.userData.getMobikwikEnabled() == 1) {
					return context.getResources().getString(R.string.add_mobikwik_cash);
				} else {
					return context.getResources().getString(R.string.add_mobikwik_wallet);
				}
			}
		} catch(Exception e){
		}return context.getResources().getString(R.string.add_wallet);
	}


	public int getPaymentOptionAccAvailability(int previousPaymentOption){
		try {
			if(Data.userData.getPaytmEnabled() == 1 && PaymentOption.PAYTM.getOrdinal() == previousPaymentOption){
				return PaymentOption.PAYTM.getOrdinal();
			}
			else if(Data.userData.getMobikwikEnabled() == 1 && PaymentOption.MOBIKWIK.getOrdinal() == previousPaymentOption){
				return PaymentOption.MOBIKWIK.getOrdinal();
			}
			else {
				return PaymentOption.CASH.getOrdinal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PaymentOption.CASH.getOrdinal();
	}

	public int getPaymentOptionIconSmall(int paymentOption){
		if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
			return R.drawable.ic_paytm_small;
		} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
			return R.drawable.ic_mobikwik_small;
		} else {
			return R.drawable.ic_cash_small;
		}
	}

	public String getPaymentOptionBalanceText(int paymentOption){
		try {
			if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
				return String.format(context.getResources().getString(R.string.rupees_value_format_without_space),
						Data.userData.getPaytmBalanceStr());
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				return String.format(context.getResources().getString(R.string.rupees_value_format_without_space),
						Data.userData.getMobikwikBalance());
			} else {
				return context.getResources().getString(R.string.cash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getString(R.string.cash);
	}

}
