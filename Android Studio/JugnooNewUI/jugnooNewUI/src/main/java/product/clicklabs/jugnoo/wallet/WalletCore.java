package product.clicklabs.jugnoo.wallet;

import android.content.Context;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.NudgeClient;
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

}
