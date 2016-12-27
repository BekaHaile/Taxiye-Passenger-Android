package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.dialogs.WalletSelectionErrorDialog;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by shankar on 7/4/16.
 */
public class WalletCore {

	private Context context;

	private ArrayList<PaymentModeConfigData> paymentModeConfigDatas;

	public WalletCore(Context context){
		this.context = context;
	}

	public void addMoneyFlurryEvent(int walletType, String amount){
		if(walletType == PaymentOption.PAYTM.getOrdinal()) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.ADD_PAYTM_CASH, bundle);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Add Paytm Cash " + amount);
		}
		else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.ADD_MOBIKWIK_CASH, bundle);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Add Mobikwik Cash " + amount);
		}
		else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
			Bundle bundle = new Bundle();
			bundle.putString("amount", amount);
			MyApplication.getInstance().logEvent(Constants.REVENUE+"_"+ FirebaseEvents.FREECHARGE_WALLET+"_"+FirebaseEvents.ADD_FREECHARGE_CASH, bundle);
        }
	}

	public void editWalletFlurryEvent(int walletType){
		if(walletType == PaymentOption.PAYTM.getOrdinal()) {
            Bundle bundle = new Bundle();
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.EDIT, bundle);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Edit");
		}
		else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
            Bundle bundle = new Bundle();
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.EDIT, bundle);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Edit");
		} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
			Bundle bundle = new Bundle();
			MyApplication.getInstance().logEvent(Constants.REVENUE+"_"+ FirebaseEvents.FREECHARGE_WALLET+"_"+FirebaseEvents.EDIT, bundle);
        }
	}

	public void removeWalletFlurryEvent(int walletType){
		if(walletType == PaymentOption.PAYTM.getOrdinal()) {
            Bundle bundle = new Bundle();
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.REMOVE_WALLET, bundle);
			FlurryEventLogger.event(context, FlurryEventNames.CLICKS_ON_REMOVE_WALLET);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Remove Wallet");
		}
		else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
            Bundle bundle = new Bundle();
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.REMOVE_WALLET, bundle);
			FlurryEventLogger.event(context, FlurryEventNames.CLICKS_ON_REMOVE_WALLET_MOBIKWIK);
			FlurryEventLogger.eventGA(Constants.REVENUE, "Mobikwik Wallet", "Remove Wallet");
		} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
			Bundle bundle = new Bundle();
			MyApplication.getInstance().logEvent(Constants.REVENUE+"_"+ FirebaseEvents.FREECHARGE_WALLET+"_"+FirebaseEvents.REMOVE_WALLET, bundle);
        }
	}


	public String getWalletBalanceStr(int walletType){
		try {
			if(walletType == PaymentOption.PAYTM.getOrdinal()) {
				return Data.userData.getPaytmBalanceStr();
			} else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
				return Data.userData.getMobikwikBalanceStr();
			} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
                return Data.userData.getFreeChargeBalanceStr();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "--";
	}

	public int getWalletBalanceColor(int walletType){
		try {
			if(walletType == PaymentOption.PAYTM.getOrdinal()) {
				return Data.userData.getPaytmBalanceColor(context);
			}
			else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
				return Data.userData.getMobikwikBalanceColor(context);
			}
			else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
                return Data.userData.getFreeChargeBalanceColor(context);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getColor(R.color.text_color);
	}

	public void deleteWalletFlurryEvent(int walletType){
		if(walletType == PaymentOption.PAYTM.getOrdinal()) {
		}
		else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()){
		} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
            //TOdo: NudgeClient tracker event here
        }
	}

	public void deleteWallet(int walletType){
		try {
			if(walletType == PaymentOption.PAYTM.getOrdinal()){
				Data.userData.deletePaytm();
			} else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()){
				Data.userData.deleteMobikwik();
			} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
                Data.userData.deleteFreeCharge();
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
				intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
			}
			else if(PaymentOption.MOBIKWIK.getOrdinal() == preferredPaymentMode) {
				if (Data.userData.getMobikwikEnabled() == 1) {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
				} else {
					intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
				}
				intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MOBIKWIK.getOrdinal());
			} else if(preferredPaymentMode == PaymentOption.FREECHARGE.getOrdinal()) {
                if(Data.userData.getFreeChargeEnabled() == 1) {
                    intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
                } else {
                    intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
                }
                intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.FREECHARGE.getOrdinal());
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
			if (paymentOption == PaymentOption.PAYTM.getOrdinal()) {
				if (Data.userData.getPaytmBalance() > 0) {
					callRequestRide = true;
					if (Data.autoData.getFareStructure() != null && Data.userData.getPaytmBalance() < Data.autoData.getFareStructure().getFixedFare()) {
						DialogPopup.dialogBanner(activity, context.getResources().getString(R.string.paytm_low_cash));
					}
				} else {
					callRequestRide = false;
					if(Data.userData.getPaytmBalance() < 0){
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.paytm_error_case_select_cash), true);
					} else{
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								MyApplication.getInstance().getWalletCore().addMoneyToWalletIntent(activity, paymentOption);
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.paytm_no_cash), false);
					}
				}
			}
			else if (paymentOption == PaymentOption.MOBIKWIK.getOrdinal()) {
				if (Data.userData.getMobikwikBalance() > 0) {
					callRequestRide = true;
					if (Data.autoData.getFareStructure() != null && Data.userData.getMobikwikBalance() < Data.autoData.getFareStructure().getFixedFare()) {
						DialogPopup.dialogBanner(activity, context.getResources().getString(R.string.mobikwik_low_cash));
					}
				} else {
					callRequestRide = false;
					if(Data.userData.getMobikwikBalance() < 0){
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.mobikwik_error_select_cash), true);
					} else{
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								MyApplication.getInstance().getWalletCore().addMoneyToWalletIntent(activity, paymentOption);
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.mobikwik_no_cash), false);
					}
				}
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()) {
				if (Data.userData.getFreeChargeBalance() > 0) {
					callRequestRide = true;
					if (Data.autoData.getFareStructure() != null && Data.userData.getFreeChargeBalance() < Data.autoData.getFareStructure().getFixedFare()) {
						DialogPopup.dialogBanner(activity, context.getResources().getString(R.string.freecharge_low_cash));
					}
				} else {
					callRequestRide = false;
					if(Data.userData.getFreeChargeBalance() < 0){
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.freecharge_error_case_select_cash), true);
					} else{
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								MyApplication.getInstance().getWalletCore().addMoneyToWalletIntent(activity, paymentOption);
							}

							@Override
							public void onNegativeClick() {
							}
						}).show(activity.getResources().getString(R.string.freecharge_no_cash), false);
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
            Bundle bundle = new Bundle();
            bundle.putString(Constants.HOME_SCREEN, Constants.KEY_PAYTM);
//            MyApplication.getInstance().logEvent(Constants.REVENUE +
//                    Constants.SLASH + Constants.ACTIVATION + Constants.SLASH +
//                    Constants.RETENTION, bundle);

            FlurryEventLogger.event(FlurryEventNames.PAYTM_SELECTED_WHEN_REQUESTING);
            FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, tag, "paytm");
		}
		else if (paymentOption == PaymentOption.MOBIKWIK.getOrdinal()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.HOME_SCREEN, Constants.KEY_MOBIKWIK);
//            MyApplication.getInstance().logEvent(Constants.REVENUE +
//                    Constants.SLASH + Constants.ACTIVATION + Constants.SLASH +
//                    Constants.RETENTION, bundle);

			FlurryEventLogger.event(FlurryEventNames.MOBIKWIK_SELECTED_WHEN_REQUESTING);
			FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, tag, "mobikwik");
		}
		else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()) {
			Bundle bundle = new Bundle();
			bundle.putString(Constants.HOME_SCREEN, Constants.KEY_FREECHARGE);
			MyApplication.getInstance().logEvent(Constants.REVENUE +
					Constants.SLASH + Constants.ACTIVATION + Constants.SLASH +
					Constants.RETENTION, bundle);
        }
		else {
//            Bundle bundle = new Bundle();
//            bundle.putString(Constants.HOME_SCREEN, "cash");
//            MyApplication.getInstance().logEvent(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, bundle);

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
			} else if(preferredPaymentMode == PaymentOption.FREECHARGE.getOrdinal()) {
                if(Data.userData.getFreeChargeEnabled() == 1) {
                    return context.getResources().getString(R.string.add_freecharge_cash);
                } else {
                    return context.getResources().getString(R.string.add_freecharge_wallet);
                }
            }
		} catch(Exception e){
		}return context.getResources().getString(R.string.add_wallet);
	}


	public int getPaymentOptionAccAvailability(int previousPaymentOption){
		try {
			if(PaymentOption.PAYTM.getOrdinal() == previousPaymentOption
					&& Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > -1){
				return PaymentOption.PAYTM.getOrdinal();
			}
			else if(PaymentOption.MOBIKWIK.getOrdinal() == previousPaymentOption
					&& Data.userData.getMobikwikEnabled() == 1 && Data.userData.getMobikwikBalance() > -1){
				return PaymentOption.MOBIKWIK.getOrdinal();
			}
			else if(previousPaymentOption == PaymentOption.FREECHARGE.getOrdinal()
					&& Data.userData.getFreeChargeEnabled() == 1 && Data.userData.getFreeChargeBalance() > -1) {
                return PaymentOption.FREECHARGE.getOrdinal();
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
		} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
            return R.drawable.ic_freecharge_small;
        } else {
			return R.drawable.ic_cash_small;
		}
	}

	public int getPaymentOptionIconBig(int paymentOption){
		if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
			return R.drawable.ic_paytm_big;
		} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
			return R.drawable.ic_mobikwik_big;
		} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
			return R.drawable.ic_freecharge_big;
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
						Data.userData.getMobikwikBalanceStr());
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
                return String.format(context.getResources().getString(R.string.rupees_value_format_without_space),
                        Data.userData.getFreeChargeBalanceStr());
            } else {
				return context.getResources().getString(R.string.cash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getString(R.string.cash);
	}

	public String getPaymentOptionName(int paymentOption){
		try {
			if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
				return context.getResources().getString(R.string.paytm);
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				return context.getResources().getString(R.string.mobikwik);
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
                return context.getResources().getString(R.string.freecharge);
            } else {
				return context.getResources().getString(R.string.cash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getString(R.string.cash);
	}

	public String getPaymentOptionNameWallet(int paymentOption){
		try {
			if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
				return context.getResources().getString(R.string.paytm_wallet);
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				return context.getResources().getString(R.string.mobikwik_wallet);
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
				return context.getResources().getString(R.string.freecharge_wallet);
			} else {
				return context.getResources().getString(R.string.cash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getString(R.string.cash);
	}

	public int couponOfWhichWallet(PromoCoupon pc){
		if(pc.getTitle().toLowerCase(Locale.ENGLISH)
				.contains(context.getString(R.string.paytm).toLowerCase(Locale.ENGLISH))) {
			return PaymentOption.PAYTM.getOrdinal();
		} else if(pc.getTitle().toLowerCase(Locale.ENGLISH)
				.contains(context.getString(R.string.mobikwik).toLowerCase(Locale.ENGLISH))) {
			return PaymentOption.MOBIKWIK.getOrdinal();
		} else if(pc.getTitle().toLowerCase(Locale.ENGLISH)
                .contains(context.getString(R.string.freecharge).toLowerCase(Locale.ENGLISH))) {
            return PaymentOption.FREECHARGE.getOrdinal();
        }
		return PaymentOption.CASH.getOrdinal();
	}


	public boolean displayAlertAndCheckForSelectedWalletCoupon(final Activity activity, int paymentOption,
															   PromoCoupon promoCoupon) {
		try {
			final int couponOfWallet = couponOfWhichWallet(promoCoupon);
			if ((couponOfWallet == PaymentOption.PAYTM.getOrdinal() && PaymentOption.PAYTM.getOrdinal() != paymentOption)
					|| (couponOfWallet == PaymentOption.MOBIKWIK.getOrdinal() && PaymentOption.MOBIKWIK.getOrdinal() != paymentOption)
                    || (couponOfWallet == PaymentOption.FREECHARGE.getOrdinal() && PaymentOption.FREECHARGE.getOrdinal() != paymentOption)) {
				View.OnClickListener onClickListenerPaymentOption = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openPaymentActivityInCaseOfWalletNotAdded(activity, couponOfWallet);
					}
				};
				View.OnClickListener onClickListenerCancel = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				};
				if(couponOfWallet == PaymentOption.PAYTM.getOrdinal()){
					if (Data.userData.getPaytmEnabled() == 1) {
						DialogPopup.alertPopupWithListener(activity, "",
								activity.getResources().getString(R.string.paytm_coupon_selected_but_paytm_option_not_selected),
								onClickListenerCancel);
					} else {
						DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
								activity.getResources().getString(R.string.paytm_coupon_selected_but_paytm_not_added),
								activity.getResources().getString(R.string.ok),
								activity.getResources().getString(R.string.cancel),
								onClickListenerPaymentOption,
								onClickListenerCancel,
								true, false);
					}
				} else if(couponOfWallet == PaymentOption.MOBIKWIK.getOrdinal()){
					if (Data.userData.getMobikwikEnabled() == 1) {
						DialogPopup.alertPopupWithListener(activity, "",
								activity.getResources().getString(R.string.mobikwik_coupon_selected_but_mobikwik_option_not_selected),
								onClickListenerCancel);
					} else {
						DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
								activity.getResources().getString(R.string.mobikwik_coupon_selected_but_mobikwik_not_added),
								activity.getResources().getString(R.string.ok),
								activity.getResources().getString(R.string.cancel),
								onClickListenerPaymentOption,
								onClickListenerCancel,
								true, false);
					}
				} else if(couponOfWallet == PaymentOption.FREECHARGE.getOrdinal()) {
                    if (Data.userData.getFreeChargeEnabled() == 1) {
                        DialogPopup.alertPopupWithListener(activity, "",
                                activity.getResources().getString(R.string.freecharge_coupon_selected_but_freecharge_option_not_selected),
                                onClickListenerCancel);
                    } else {
                        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                                activity.getResources().getString(R.string.freecharge_coupon_selected_but_freecharge_not_added),
                                activity.getResources().getString(R.string.ok),
                                activity.getResources().getString(R.string.cancel),
                                onClickListenerPaymentOption,
                                onClickListenerCancel,
                                true, false);
                    }
                }
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

    public void openPaymentActivityInCaseOfWalletNotAdded(Activity activity, int paymentOption) {
		try {
			if (Data.userData.getPaytmEnabled() != 1
					|| Data.userData.getMobikwikEnabled() != 1
					 || Data.userData.getFreeChargeEnabled() != 1) {
				Intent intent = new Intent(activity, PaymentActivity.class);
				intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
				intent.putExtra(Constants.KEY_WALLET_TYPE, paymentOption);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(FlurryEventNames.WALLET_BEFORE_REQUEST_RIDE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parsePaymentModeConfigDatas(JSONObject jObj){
		try{
			JSONArray jsonArray = jObj.getJSONArray(Constants.KEY_PAYMENT_MODE_CONFIG_DATA);
			paymentModeConfigDatas = new ArrayList<>();
			for(int i=0; i<jsonArray.length(); i++){
				JSONObject ji = jsonArray.getJSONObject(i);
				PaymentModeConfigData paymentModeConfigData = new PaymentModeConfigData(ji.getString(Constants.KEY_NAME),
						ji.getInt(Constants.KEY_ENABLED));
				paymentModeConfigDatas.add(paymentModeConfigData);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

    //Todo: change logic or ask shankar
	public ArrayList<PaymentModeConfigData> getPaymentModeConfigDatas(UserData userData) {

//		for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
//			paymentModeConfigData.setPriority(0);
//			if(paymentModeConfigData.getEnabled() == 1){
//				if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
//						&& userData.getPaytmEnabled() == 1) {
//					paymentModeConfigData.incrementPriority();
//					if(userData.getPaytmBalance() > 0){
//						paymentModeConfigData.incrementPriority();
//						if(userData.getPaytmBalance() > userData.getMobikwikBalance()
//								&& userData.getPaytmBalance() > userData.getFreeChargeBalance()){
//							paymentModeConfigData.incrementPriority();
//							paymentModeConfigData.incrementPriority();
//						} else if(userData.getPaytmBalance() > userData.getMobikwikBalance()
//								|| userData.getPaytmBalance() > userData.getFreeChargeBalance()){
//							paymentModeConfigData.incrementPriority();
//						}
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_ADDED_WALLET, 0) == PaymentOption.PAYTM.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_USED_WALLET, 0) == PaymentOption.PAYTM.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_MONEY_ADDED_WALLET, 0) == PaymentOption.PAYTM.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//				}
//				else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
//						&& userData.getMobikwikEnabled() == 1) {
//					paymentModeConfigData.incrementPriority();
//					if(userData.getMobikwikBalance() > 0){
//						paymentModeConfigData.incrementPriority();
//						if(userData.getMobikwikBalance() > userData.getPaytmBalance()
//								&& userData.getMobikwikBalance() > userData.getFreeChargeBalance()){
//							paymentModeConfigData.incrementPriority();
//							paymentModeConfigData.incrementPriority();
//						} else if(userData.getMobikwikBalance() > userData.getPaytmBalance()
//								|| userData.getMobikwikBalance() > userData.getFreeChargeBalance()){
//							paymentModeConfigData.incrementPriority();
//						}
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_ADDED_WALLET, 0) == PaymentOption.MOBIKWIK.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_USED_WALLET, 0) == PaymentOption.MOBIKWIK.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_MONEY_ADDED_WALLET, 0) == PaymentOption.MOBIKWIK.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//				}
//				else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()
//						&& userData.getFreeChargeEnabled() == 1) {
//					paymentModeConfigData.incrementPriority();
//					if(userData.getFreeChargeBalance() > 0){
//						paymentModeConfigData.incrementPriority();
//						if(userData.getFreeChargeBalance() > userData.getPaytmBalance()
//								&& userData.getFreeChargeBalance() > userData.getMobikwikBalance()){
//							paymentModeConfigData.incrementPriority();
//							paymentModeConfigData.incrementPriority();
//						} else if(userData.getFreeChargeBalance() > userData.getPaytmBalance()
//								|| userData.getFreeChargeBalance() > userData.getMobikwikBalance()){
//							paymentModeConfigData.incrementPriority();
//						}
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_ADDED_WALLET, 0) == PaymentOption.FREECHARGE.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_USED_WALLET, 0) == PaymentOption.FREECHARGE.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//					if(Prefs.with(context).getInt(Constants.SP_LAST_MONEY_ADDED_WALLET, 0) == PaymentOption.FREECHARGE.getOrdinal()){
//						paymentModeConfigData.incrementPriority();
//					}
//				}
//			}
//		}
//
//		Collections.sort(paymentModeConfigDatas, new Comparator<PaymentModeConfigData>() {
//			@Override
//			public int compare(PaymentModeConfigData lhs, PaymentModeConfigData rhs) {
//				if(lhs.getPriority() == rhs.getPriority()){
//					return 0;
//				}
//				else if(lhs.getPriority() > rhs.getPriority()){
//					return -1;
//				}
//				else{
//					return 1;
//				}
//			}
//		});
//		Log.e("paymentModeConfigDatas", ">"+paymentModeConfigDatas);

		return paymentModeConfigDatas;
	}

	public void setDefaultPaymentOption(){
		try{
			Data.autoData.setPickupPaymentOption(getDefaultPaymentOption().getOrdinal());
			Log.e("pickupPaymentOption", ">"+Data.autoData.getPickupPaymentOption());
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	public PaymentOption getDefaultPaymentOption() {
		PaymentOption paymentOption = PaymentOption.CASH;
		try {
			PaymentModeConfigData paymentModeConfigDataDefault = null;
			for (PaymentModeConfigData paymentModeConfigData : getPaymentModeConfigDatas(Data.userData)) {
				if (paymentModeConfigData.getEnabled() == 1) {
					if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
							&& Data.userData.getPaytmEnabled() == 1
							&& Data.userData.getPaytmBalance() >= 1) {
						paymentModeConfigDataDefault = paymentModeConfigData;
						break;
					} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
                            && Data.userData.getMobikwikEnabled() == 1
                            && Data.userData.getMobikwikBalance() >= 1) {
                        paymentModeConfigDataDefault = paymentModeConfigData;
                        break;
                    } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()
                            && Data.userData.getFreeChargeEnabled() == 1
                            && Data.userData.getFreeChargeBalance() >= 1) {
                        paymentModeConfigDataDefault = paymentModeConfigData;
                        break;
                    }
				}
			}
			if (paymentModeConfigDataDefault != null) {
				paymentOption = getPaymentOptionFromInt(paymentModeConfigDataDefault.getPaymentOption());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paymentOption;
	}

	public PaymentOption getPaymentOptionFromInt(int paymentOption){
		if(PaymentOption.PAYTM.getOrdinal() == paymentOption){
			return PaymentOption.PAYTM;
		} else if(PaymentOption.MOBIKWIK.getOrdinal() == paymentOption){
			return PaymentOption.MOBIKWIK;
		}  else if(PaymentOption.FREECHARGE.getOrdinal() == paymentOption){
			return PaymentOption.FREECHARGE;
		} else{
			return PaymentOption.CASH;
		}
	}


	public void paymentOptionSelectionBeforeRequestRide(final HomeActivity activity, PaymentOption paymentOption){
		try {
			if(paymentOption == PaymentOption.PAYTM){
				if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > 0) {
					Data.autoData.setPickupPaymentOption(PaymentOption.PAYTM.getOrdinal());
					activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
				}
				else if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() < 0){
					new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
						@Override
						public void onPositiveClick() {

						}

						@Override
						public void onNegativeClick() {

						}
					}).show(activity.getResources().getString(R.string.paytm_error_case_select_cash), true);
				} else{
					if(Data.userData.getPaytmEnabled() == 1) {
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								try {
									Intent intent = new Intent(activity, PaymentActivity.class);
									intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
									intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getPaymentOptionDialog().dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onNegativeClick() {
								try {
									if(Data.userData.getMobikwikEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1){
										Data.autoData.setPickupPaymentOption(PaymentOption.CASH.getOrdinal());
										activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
										activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getPaymentOptionDialog().dismiss();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show(activity.getResources().getString(R.string.paytm_no_cash), false);
					}
					else{
						MyApplication.getInstance().getWalletCore()
								.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.PAYTM.getOrdinal());
					}
				}
			}
			else if(paymentOption == PaymentOption.MOBIKWIK){
				if(Data.userData.getMobikwikEnabled() == 1 && Data.userData.getMobikwikBalance() > 0) {
					Data.autoData.setPickupPaymentOption(PaymentOption.MOBIKWIK.getOrdinal());
					activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
				}
				else if(Data.userData.getMobikwikEnabled() == 1 && Data.userData.getMobikwikBalance() < 0){
					new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
						@Override
						public void onPositiveClick() {

						}

						@Override
						public void onNegativeClick() {

						}
					}).show(activity.getResources().getString(R.string.mobikwik_error_select_cash), true);
				} else{
					if(Data.userData.getMobikwikEnabled() == 1) {
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								try {
									Intent intent = new Intent(activity, PaymentActivity.class);
									intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
									intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MOBIKWIK.getOrdinal());
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getPaymentOptionDialog().dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onNegativeClick() {
								try {
									if(Data.userData.getPaytmEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1){
										Data.autoData.setPickupPaymentOption(PaymentOption.CASH.getOrdinal());
										activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
										activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getPaymentOptionDialog().dismiss();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show(activity.getResources().getString(R.string.mobikwik_no_cash), false);
					}
					else{
						MyApplication.getInstance().getWalletCore()
								.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.MOBIKWIK.getOrdinal());
					}
				}
			} else if(paymentOption == PaymentOption.FREECHARGE){
                if(Data.userData.getFreeChargeEnabled() == 1 && Data.userData.getFreeChargeBalance() > 0) {
					Data.autoData.setPickupPaymentOption(PaymentOption.FREECHARGE.getOrdinal());
                    activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                }
				else if(Data.userData.getFreeChargeEnabled() == 1 && Data.userData.getFreeChargeBalance() < 0){
                    new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
                        @Override
                        public void onPositiveClick() {

                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    }).show(activity.getResources().getString(R.string.freecharge_error_case_select_cash), true);
                } else{
                    if(Data.userData.getFreeChargeEnabled() == 1) {
                        new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
                            @Override
                            public void onPositiveClick() {
                                Intent intent = new Intent(activity, PaymentActivity.class);
                                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
                                intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.FREECHARGE.getOrdinal());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            }

                            @Override
                            public void onNegativeClick() {
                                try {
                                    if(Data.userData.getPaytmEnabled() != 1 && Data.userData.getMobikwikEnabled() != 1){
										Data.autoData.setPickupPaymentOption(PaymentOption.CASH.getOrdinal());
                                        activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                                        activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getPaymentOptionDialog().dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show(activity.getResources().getString(R.string.freecharge_no_cash), false);
                    }
                    else{
                        MyApplication.getInstance().getWalletCore()
                                .openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.FREECHARGE.getOrdinal());
                    }
                }

            }
			else if(paymentOption == PaymentOption.CASH){
				if(Data.autoData.getPickupPaymentOption() == PaymentOption.PAYTM.getOrdinal()){
					FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
				}
				Data.autoData.setPickupPaymentOption(PaymentOption.CASH.getOrdinal());
				activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
				FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_payment_mode", "cash");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public void paymentOptionSelectionAtFreshCheckout(final FreshActivity activity, final PaymentOption paymentOption, final CallbackPaymentOptionSelector callbackPaymentOptionSelector){
		try {
			if(paymentOption == PaymentOption.PAYTM){
				if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > 0) {
					callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.PAYTM);
					FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_payment_mode", "paytm");
				}
				else if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() < 0){
					new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
						@Override
						public void onPositiveClick() {

						}

						@Override
						public void onNegativeClick() {

						}
					}).show(activity.getResources().getString(R.string.paytm_error_case_select_cash), true);
				} else{
					if(Data.userData.getPaytmEnabled() == 1) {
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								try {
									Intent intent = new Intent(activity, PaymentActivity.class);
									intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
									intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									callbackPaymentOptionSelector.onWalletAdd(PaymentOption.PAYTM);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onNegativeClick() {
								try {
									if(Data.userData.getMobikwikEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1){
										callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.CASH);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show(activity.getResources().getString(R.string.paytm_no_cash), false);
					}
					else{
						MyApplication.getInstance().getWalletCore()
								.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.PAYTM.getOrdinal());
						callbackPaymentOptionSelector.onWalletAdd(PaymentOption.PAYTM);
					}
				}
			}
			else if(paymentOption == PaymentOption.MOBIKWIK){
				if(Data.userData.getMobikwikEnabled() == 1 && Data.userData.getMobikwikBalance() > 0) {
					callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.MOBIKWIK);
				}
				else if(Data.userData.getMobikwikEnabled() == 1 && Data.userData.getMobikwikBalance() < 0){
					new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
						@Override
						public void onPositiveClick() {

						}

						@Override
						public void onNegativeClick() {

						}
					}).show(activity.getResources().getString(R.string.mobikwik_error_select_cash), true);
				} else{
					if(Data.userData.getMobikwikEnabled() == 1) {
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								try {
									Intent intent = new Intent(activity, PaymentActivity.class);
									intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
									intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MOBIKWIK.getOrdinal());
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									callbackPaymentOptionSelector.onWalletAdd(PaymentOption.MOBIKWIK);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onNegativeClick() {
								try {
									if(Data.userData.getPaytmEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1){
										callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.CASH);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show(activity.getResources().getString(R.string.mobikwik_no_cash), false);
					}
					else{
						MyApplication.getInstance().getWalletCore()
								.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.MOBIKWIK.getOrdinal());
						callbackPaymentOptionSelector.onWalletAdd(PaymentOption.MOBIKWIK);
					}
				}
			}
			else if(paymentOption == PaymentOption.FREECHARGE){
				if(Data.userData.getFreeChargeEnabled() == 1 && Data.userData.getFreeChargeBalance() > 0) {
					callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.FREECHARGE);
				}
				else if(Data.userData.getFreeChargeEnabled() == 1 && Data.userData.getFreeChargeBalance() < 0){
					new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
						@Override
						public void onPositiveClick() {

						}

						@Override
						public void onNegativeClick() {

						}
					}).show(activity.getResources().getString(R.string.freecharge_error_case_select_cash), true);
				} else{
					if(Data.userData.getFreeChargeEnabled() == 1) {
						new WalletSelectionErrorDialog(activity, new WalletSelectionErrorDialog.Callback() {
							@Override
							public void onPositiveClick() {
								try {
									Intent intent = new Intent(activity, PaymentActivity.class);
									intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
									intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.FREECHARGE.getOrdinal());
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									callbackPaymentOptionSelector.onWalletAdd(PaymentOption.FREECHARGE);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onNegativeClick() {
								try {
									if(Data.userData.getPaytmEnabled() != 1 && Data.userData.getMobikwikEnabled() != 1){
										callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.CASH);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show(activity.getResources().getString(R.string.freecharge_no_cash), false);
					}
					else{
						MyApplication.getInstance().getWalletCore()
								.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.FREECHARGE.getOrdinal());
						callbackPaymentOptionSelector.onWalletAdd(PaymentOption.FREECHARGE);
					}
				}
			}
			else if(paymentOption == PaymentOption.CASH){
				if(Data.autoData.getPickupPaymentOption() == PaymentOption.PAYTM.getOrdinal()){
					FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
				}
				callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.CASH);
				FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_payment_mode", "cash");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void faEventAddWallet(int paymentOption, String suffix){
		try {
			if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
				MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.PAYTM_WALLET+"_"+suffix, new Bundle());
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.MOBIKWIK_WALLET+"_"+suffix, new Bundle());
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
				MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.FREECHARGE_WALLET+"_"+suffix, new Bundle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
