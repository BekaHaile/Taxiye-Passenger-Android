package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jugnoo.pay.activities.MainActivity;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.retrofit.model.PaymentGatewayModeConfig;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.dialogs.WalletSelectionErrorDialog;
import product.clicklabs.jugnoo.home.fragments.RequestRideOptionsFragment;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

import static product.clicklabs.jugnoo.Constants.KEY_DISPLAY_NAME;
import static product.clicklabs.jugnoo.Constants.KEY_JUGNOO_VPA_HANDLE;
import static product.clicklabs.jugnoo.Constants.KEY_OFFER_TEXT;
import static product.clicklabs.jugnoo.Constants.KEY_UPI_HANDLE;

/**
 * Created by shankar on 7/4/16.
 */
public class WalletCore {

	private Context context;

	private ArrayList<PaymentModeConfigData> paymentModeConfigDatas;
	private ArrayList<PaymentGatewayModeConfig> paymentGatewayModeConfigs;

	public WalletCore(Context context){
		this.context = context;
	}

	public void addMoneyFlurryEvent(int walletType, String amount){
	}

	public void editWalletFlurryEvent(int walletType){
	}

	public void removeWalletFlurryEvent(int walletType){
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
								openSlidePanelOfHomeActivity(activity);
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
								openSlidePanelOfHomeActivity(activity);
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
								openSlidePanelOfHomeActivity(activity);
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
								openSlidePanelOfHomeActivity(activity);

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
								openSlidePanelOfHomeActivity(activity);


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
								openSlidePanelOfHomeActivity(activity);
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

	private void openSlidePanelOfHomeActivity(Activity activity) {
		try {
			if(activity instanceof HomeActivity){
				HomeActivity homeActivity = (HomeActivity) activity;


				RequestRideOptionsFragment requestRideOptionsFragment = homeActivity.slidingBottomPanel.getRequestRideOptionsFragment();
				if(requestRideOptionsFragment.getRegionSelected().getRideType()!= RideTypeValue.POOL.getOrdinal()){
					homeActivity.slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

					if(Data.autoData.getRegions().size()>1){
						requestRideOptionsFragment.getPaymentOptionDialog().show();
					}else{
						homeActivity.slidingBottomPanel.getViewPager().setCurrentItem(0);
					}

				}else{
					requestRideOptionsFragment.getPaymentOptionDialog().show();
				}



            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void requestRideWalletSelectedFlurryEvent(int paymentOption, String tag){
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
			else if(previousPaymentOption == PaymentOption.JUGNOO_PAY.getOrdinal()
					&& Data.getPayData() != null && Data.getPayData().getPay().getHasVpa() == 1) {
				return PaymentOption.JUGNOO_PAY.getOrdinal();
			}
			else {
				return previousPaymentOption;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return previousPaymentOption;
	}

	public int getPaymentOptionIconSmall(int paymentOption){
		if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
			return R.drawable.ic_paytm_small;
		} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
			return R.drawable.ic_mobikwik_small;
		} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
            return R.drawable.ic_freecharge_small;
        } else if(paymentOption == PaymentOption.RAZOR_PAY.getOrdinal()){
            return R.drawable.ic_cards_grey;
        }else if(paymentOption == PaymentOption.MPESA.getOrdinal()){
            return R.drawable.ic_mpesa_small;
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
				return String.format(context.getResources().getString(R.string.rupees_value_format),
						Data.userData.getPaytmBalanceStr());
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				return String.format(context.getResources().getString(R.string.rupees_value_format),
						Data.userData.getMobikwikBalanceStr());
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
                return String.format(context.getResources().getString(R.string.rupees_value_format),
                        Data.userData.getFreeChargeBalanceStr());
            } else if(paymentOption == PaymentOption.RAZOR_PAY.getOrdinal()){
				return getRazorpayName(context);
            }else if(paymentOption == PaymentOption.MPESA.getOrdinal()){
				return getMPesaName(context);
            } else {
				return context.getResources().getString(R.string.cash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getResources().getString(R.string.cash);
	}

	public String getRazorpayName(Context context) {
		String name = context.getString(R.string.card);
		for(PaymentModeConfigData configData : getPaymentModeConfigDatas()){
			if(configData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()){
				name = configData.getDisplayName();
				break;
			}
		}
		return name;
	}
	public String getMPesaName(Context context) {
		String name = context.getString(R.string.mpesa);
		for(PaymentModeConfigData configData : getPaymentModeConfigDatas()){
			if(configData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()){
				name = configData.getDisplayName();
				break;
			}
		}
		return name;
	}

	public String getPaymentOptionName(int paymentOption){
		try {
			if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
				return context.getResources().getString(R.string.paytm);
			} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
				return context.getResources().getString(R.string.mobikwik);
			} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
                return context.getResources().getString(R.string.freecharge);
            } else if(paymentOption == PaymentOption.RAZOR_PAY.getOrdinal()){
                return getPaymentOptionBalanceText(paymentOption);
            } else if(paymentOption == PaymentOption.MPESA.getOrdinal()){
                return context.getString(R.string.mpesa);
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
						DialogPopup.alertPopup(activity, "",
								activity.getResources().getString(R.string.paytm_coupon_selected_but_paytm_option_not_selected));
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
						DialogPopup.alertPopup(activity, "",
								activity.getResources().getString(R.string.mobikwik_coupon_selected_but_mobikwik_option_not_selected));
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
                        DialogPopup.alertPopup(activity, "",
                                activity.getResources().getString(R.string.freecharge_coupon_selected_but_freecharge_option_not_selected));
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
			if(paymentOption == PaymentOption.JUGNOO_PAY.getOrdinal()
					&& Data.getPayData() != null && Data.getPayData().getPay().getHasVpa() == 0){
				Intent intent = new Intent(activity, MainActivity.class);
				intent.putExtra(Constants.KEY_GO_BACK, 1);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else if ((paymentOption == PaymentOption.PAYTM.getOrdinal()
					|| paymentOption == PaymentOption.MOBIKWIK.getOrdinal()
					|| paymentOption == PaymentOption.FREECHARGE.getOrdinal())
					&&
					(Data.userData.getPaytmEnabled() != 1
					|| Data.userData.getMobikwikEnabled() != 1
					|| Data.userData.getFreeChargeEnabled() != 1)) {
				Intent intent = new Intent(activity, PaymentActivity.class);
				intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.ADD_WALLET.getOrdinal());
				intent.putExtra(Constants.KEY_WALLET_TYPE, paymentOption);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
						ji.getInt(Constants.KEY_ENABLED), ji.optString(KEY_OFFER_TEXT, null), ji.optString(KEY_DISPLAY_NAME, null),
						ji.optString(KEY_UPI_HANDLE, null),ji.optString(KEY_JUGNOO_VPA_HANDLE,null),
						ji.optString(Constants.KEY_UPI_CASHBACK_VALUE, ""));
				paymentModeConfigDatas.add(paymentModeConfigData);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void updatePaymentModeConfigDatas(JSONObject jObj){
		try{
			JSONArray jsonArray = jObj.getJSONArray(Constants.KEY_PAYMENT_MODE_CONFIG_DATA);
			if(jsonArray.length() == 0){
				return;
			}
			for(int i=0; i<jsonArray.length(); i++){
				JSONObject ji = jsonArray.getJSONObject(i);
				PaymentModeConfigData paymentModeConfigData = new PaymentModeConfigData(ji.getString(Constants.KEY_NAME),
						ji.getInt(Constants.KEY_ENABLED), ji.optString(KEY_OFFER_TEXT, null), ji.optString(KEY_DISPLAY_NAME, null),
						ji.optString(KEY_UPI_HANDLE, null),ji.optString(KEY_JUGNOO_VPA_HANDLE,null),
						ji.optString(Constants.KEY_UPI_CASHBACK_VALUE, ""));
				paymentModeConfigDatas.set(paymentModeConfigDatas.indexOf(paymentModeConfigData), paymentModeConfigData);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}



	public ArrayList<PaymentModeConfigData> getPaymentModeConfigDatas() {
		return paymentModeConfigDatas;
	}

	public ArrayList<PaymentGatewayModeConfig> getPaymentGatewayModeConfigs(){
		return paymentGatewayModeConfigs;
	}

	public void parsePaymentGatewayModeConfigs(List<PaymentGatewayModeConfig> paymentGatewayModeConfigs){
		this.paymentGatewayModeConfigs = new ArrayList<>();
		if(paymentGatewayModeConfigs != null) {
			this.paymentGatewayModeConfigs.addAll(paymentGatewayModeConfigs);
			for(PaymentGatewayModeConfig config : paymentGatewayModeConfigs){
				if(config!=null){
					if(Data.userData != null && !TextUtils.isEmpty(config.getUpiHandle())){
						Data.userData.setUpiHandle(config.getUpiHandle());
						break;
					}
				}

			}
		}
	}

	public void setDefaultPaymentOption(Integer paymentOption){
		try{
			Data.autoData.setPickupPaymentOption(paymentOption==null?getDefaultPaymentOption().getOrdinal():paymentOption);
			Log.e("pickupPaymentOption", ">"+Data.autoData.getPickupPaymentOption());
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	public PaymentOption getDefaultPaymentOption() {
		PaymentOption paymentOption = PaymentOption.CASH;
		try {
			PaymentModeConfigData paymentModeConfigDataDefault = null;
			for (PaymentModeConfigData paymentModeConfigData : getPaymentModeConfigDatas()) {
				if (paymentModeConfigData.getEnabled() == 1) {
					if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
							&& Data.userData.getPaytmEnabled() == 1
							&& Data.userData.getPaytmBalance() >= 0) {
						paymentModeConfigDataDefault = paymentModeConfigData;
						break;
					} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
                            && Data.userData.getMobikwikEnabled() == 1
                            && Data.userData.getMobikwikBalance() >= 0) {
                        paymentModeConfigDataDefault = paymentModeConfigData;
                        break;
                    } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()
                            && Data.userData.getFreeChargeEnabled() == 1
                            && Data.userData.getFreeChargeBalance() >= 0) {
                        paymentModeConfigDataDefault = paymentModeConfigData;
                        break;
                    } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.JUGNOO_PAY.getOrdinal()
							&& Data.getPayData() != null
							&& Data.getPayData().getPay().getHasVpa() == 1) {
						paymentModeConfigDataDefault = paymentModeConfigData;
						break;
					}  else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
							|| paymentModeConfigData.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()
							|| paymentModeConfigData.getPaymentOption() == PaymentOption.ICICI_UPI.getOrdinal()
							|| paymentModeConfigData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()
							|| paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()
							) {
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

	/**
	 * Function to check for Rides offering that is the given payment option is valid or not
	 * @return payment option with highest order coming from server in case of payment option not known to rides
	 */
	public int validatePaymentOptionForRidesOffering(int paymentOptionInt) {
		if (paymentOptionInt == PaymentOption.PAYTM.getOrdinal()
				&& paymentOptionInt == PaymentOption.MOBIKWIK.getOrdinal()
				&& paymentOptionInt == PaymentOption.FREECHARGE.getOrdinal()) {
			try {
				PaymentModeConfigData paymentModeConfigDataDefault = null;
				for (PaymentModeConfigData paymentModeConfigData : getPaymentModeConfigDatas()) {
					if (paymentModeConfigData.getEnabled() == 1) {
						if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
								&& Data.userData.getPaytmEnabled() == 1
								&& Data.userData.getPaytmBalance() >= 0) {
							paymentModeConfigDataDefault = paymentModeConfigData;
							break;
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
								&& Data.userData.getMobikwikEnabled() == 1
								&& Data.userData.getMobikwikBalance() >= 0) {
							paymentModeConfigDataDefault = paymentModeConfigData;
							break;
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()
								&& Data.userData.getFreeChargeEnabled() == 1
								&& Data.userData.getFreeChargeBalance() >= 0) {
							paymentModeConfigDataDefault = paymentModeConfigData;
							break;
						}
					}
				}
				if (paymentModeConfigDataDefault != null) {
					return paymentModeConfigDataDefault.getPaymentOption();
				} else {
					return paymentOptionInt;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return paymentOptionInt;
			}
		} else {
			return paymentOptionInt;
		}
	}

	public PaymentOption getPaymentOptionFromInt(int paymentOption){
		if(PaymentOption.PAYTM.getOrdinal() == paymentOption){
			return PaymentOption.PAYTM;
		} else if(PaymentOption.MOBIKWIK.getOrdinal() == paymentOption){
			return PaymentOption.MOBIKWIK;
		} else if(PaymentOption.FREECHARGE.getOrdinal() == paymentOption){
			return PaymentOption.FREECHARGE;
		} else if(PaymentOption.JUGNOO_PAY.getOrdinal() == paymentOption){
			return PaymentOption.JUGNOO_PAY;
		} else if(PaymentOption.RAZOR_PAY.getOrdinal() == paymentOption){
			return PaymentOption.RAZOR_PAY;
		} else if(PaymentOption.UPI_RAZOR_PAY.getOrdinal() == paymentOption){
			return PaymentOption.UPI_RAZOR_PAY;
		} else if(PaymentOption.ICICI_UPI.getOrdinal() == paymentOption){
			return PaymentOption.ICICI_UPI;
		} else if(PaymentOption.MPESA.getOrdinal() == paymentOption){
			return PaymentOption.MPESA;
		}else{
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
			else if(paymentOption == PaymentOption.MPESA){
//				if(Data.autoData.getPickupPaymentOption() == PaymentOption.PAYTM.getOrdinal()){
//					FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
//				}
				Data.autoData.setPickupPaymentOption(PaymentOption.MPESA.getOrdinal());
				activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
//				FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_payment_mode", "cash");
			}
			else {
				Data.autoData.setPickupPaymentOption(paymentOption.getOrdinal());
				activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public void paymentOptionSelectionAtFreshCheckout(final Activity activity, final PaymentOption paymentOption, final CallbackPaymentOptionSelector callbackPaymentOptionSelector){
		try {
			if(paymentOption == PaymentOption.PAYTM){
				if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > 0) {
					callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.PAYTM);
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
									intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE, callbackPaymentOptionSelector.getAmountToPrefill());
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
									if(Data.userData.getMobikwikEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1
											&& (Data.getPayData() == null || Data.getPayData().getPay().getHasVpa() != 1)){
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
									intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE, callbackPaymentOptionSelector.getAmountToPrefill());
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
									if(Data.userData.getPaytmEnabled() != 1 && Data.userData.getFreeChargeEnabled() != 1
											&& (Data.getPayData() == null || Data.getPayData().getPay().getHasVpa() != 1)){
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
									intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE, callbackPaymentOptionSelector.getAmountToPrefill());
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
			else if(paymentOption == PaymentOption.JUGNOO_PAY){
				if(Data.getPayData() != null && Data.getPayData().getPay().getHasVpa() == 1) {
					callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.JUGNOO_PAY);
				} else {
					MyApplication.getInstance().getWalletCore()
							.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.JUGNOO_PAY.getOrdinal());
					callbackPaymentOptionSelector.onWalletAdd(PaymentOption.JUGNOO_PAY);
				}
			}
			else if(paymentOption == PaymentOption.CASH){
				callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.CASH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void faEventAddWallet(int paymentOption, String suffix){
	}

}
