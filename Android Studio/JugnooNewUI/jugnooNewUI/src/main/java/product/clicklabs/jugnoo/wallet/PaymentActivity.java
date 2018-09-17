package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.stripe.StripeAddCardFragment;
import product.clicklabs.jugnoo.stripe.StripeCardsStateListener;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.fragments.AddWalletFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletRechargeFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletTransactionsFragment;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;


/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity implements StripeCardsStateListener{

	private final String TAG = PaymentActivity.class.getSimpleName();

	public int paymentActivityPathInt = PaymentActivityPath.WALLET.getOrdinal();
	public String amountToPreFill = "";
	private WalletAddMoneyState walletAddMoneyState;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

		new ASSL(this, (ViewGroup) findViewById(R.id.fragLayout), 1134, 720, false);

		paymentActivityPathInt = getIntent()
				.getIntExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());

		if(PaymentActivityPath.WALLET.getOrdinal() == paymentActivityPathInt){
			int openWalletTransactions = getIntent().getIntExtra(Constants.KEY_WALLET_TRANSACTIONS, 0);
			if(openWalletTransactions == 1){
				newIntentReceived = true;
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragLayout, WalletTransactionsFragment.newInstance(0), WalletTransactionsFragment.class.getName())
						.addToBackStack(WalletTransactionsFragment.class.getName())
						.commit();
			} else {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragLayout, WalletFragment.newInstance(), WalletFragment.class.getName())
						.addToBackStack(WalletFragment.class.getName())
						.commitAllowingStateLoss();
			}

		}
		else if(PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal() == paymentActivityPathInt){
			if(getIntent().hasExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE)){
				amountToPreFill = getIntent().getStringExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE);
			}
			int walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, WalletRechargeFragment.newInstance(walletType), WalletRechargeFragment.class.getName())
					.addToBackStack(WalletRechargeFragment.class.getName())
					.commitAllowingStateLoss();
		}
		else if(PaymentActivityPath.ADD_WALLET.getOrdinal() == paymentActivityPathInt){
			int walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
			if(walletType==PaymentOption.STRIPE_CARDS.getOrdinal() || walletType ==PaymentOption.ACCEPT_CARD.getOrdinal()){
				PaymentOption paymentOption = walletType==PaymentOption.STRIPE_CARDS.getOrdinal()?PaymentOption.STRIPE_CARDS:PaymentOption.ACCEPT_CARD;
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragLayout,StripeAddCardFragment.newInstance(paymentOption), StripeAddCardFragment.class.getName())
						.addToBackStack(StripeAddCardFragment.class.getName())
						.commitAllowingStateLoss();
			}else{
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragLayout, AddWalletFragment.newInstance(walletType), AddWalletFragment.class.getName())
						.addToBackStack(AddWalletFragment.class.getName())
						.commitAllowingStateLoss();
			}

		}

		setWalletAddMoneyState(WalletAddMoneyState.INIT);
    }


    @Override
    public void onBackPressed() {
		try {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(WalletRechargeFragment.class.getName());
			if (fragment != null
					&& fragment.isVisible()
					&& fragment instanceof WalletRechargeFragment
					&& ((WalletRechargeFragment)fragment).getButtonRemoveWalletVisiblity() == View.VISIBLE) {
				((WalletRechargeFragment) fragment).performBackPressed();
			} else {
				goBack();
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.onBackPressed();
		}
	}

	public void goBack(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		} else {
			super.onBackPressed();
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
		if(!newIntentReceived && getWalletAddMoneyState() != WalletAddMoneyState.SUCCESS) {
			getBalance("Refresh", PaymentOption.CASH.getOrdinal());
		} else{
			setWalletAddMoneyState(WalletAddMoneyState.INIT);
		}
		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, PaymentActivity.class.getName());
		Utils.enableSMSReceiver(this);
		newIntentReceived = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, "");
		Utils.disableSMSReceiver(this);
	}

	boolean newIntentReceived = false;
	@Override
	protected void onNewIntent(Intent intent) {
		newIntentReceived = true;
		retrieveOTPFromSMS(intent);
		super.onNewIntent(intent);
	}

	private void retrieveOTPFromSMS(Intent intent){
		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				otp = Utils.retrieveOTPFromSMS(message);
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
					Fragment currFrag = getSupportFragmentManager().findFragmentByTag(AddWalletFragment.class.getName());
					if(currFrag != null){
						((AddWalletFragment)currFrag).receiveOtp(otp);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void updateWalletFragment(){
		try {
			Fragment currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
			if(currFrag != null){
				currFrag.onResume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private ApiFetchWalletBalance apiFetchWalletBalance = null;
	private String fragName = "Refresh";
	private int paymentOption = PaymentOption.CASH.getOrdinal();
	public void getBalance(String fragName,int paymentOption) {
		try {
			this.fragName = fragName;
			this.paymentOption = paymentOption;
			if(apiFetchWalletBalance == null){
				apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
					@Override
					public void onSuccess() {
						performGetBalanceSuccess(PaymentActivity.this.fragName,PaymentActivity.this.paymentOption);
					}

					@Override
					public void onFailure() {
//						getBalance(PaymentActivity.this.fragName);
					}

					@Override
					public void onFinish() {

					}

					@Override
					public void onRetry(View view) {
						getBalance(PaymentActivity.this.fragName, PaymentActivity.this.paymentOption);
					}

					@Override
					public void onNoRetry(View view) {

					}
				});
			}
			apiFetchWalletBalance.getBalance(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performGetBalanceSuccess(String fragName,int paymentOption){
		Intent intent = new Intent(Constants.INTENT_ACTION_WALLET_UPDATE);
		LocalBroadcastManager.getInstance(PaymentActivity.this).sendBroadcast(intent);
		try {
			Fragment currFrag = null;
			if(fragName.equalsIgnoreCase(WalletRechargeFragment.class.getName())) {
				MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(paymentOption);
				currFrag = getSupportFragmentManager().findFragmentByTag(WalletRechargeFragment.class.getName());
				if(currFrag != null){
					((WalletRechargeFragment) currFrag).onResume();
					((WalletRechargeFragment) currFrag).performBackPressed();
				}
			}
			else if(fragName.equalsIgnoreCase(AddWalletFragment.class.getName())){
				MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(paymentOption);
				goBack();
			}
			currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
			if(currFrag != null){
				currFrag.onResume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WalletAddMoneyState getWalletAddMoneyState() {
		return walletAddMoneyState;
	}

	public void setWalletAddMoneyState(WalletAddMoneyState walletAddMoneyState) {
		this.walletAddMoneyState = walletAddMoneyState;
	}

	@Override
	public void onCardsUpdated(ArrayList<StripeCardData> stripeCardData, String message, final boolean cardAdded,PaymentOption paymentOption) {


		PaymentModeConfigData configData;
		if(paymentOption.getOrdinal()==PaymentOption.PAY_STACK_CARD.getOrdinal()){
			onBackPressed();
			//getBalance(WalletFragment.class.getName(), Data.autoData.getPickupPaymentOption());
			return;
		}else if(paymentOption.getOrdinal()==PaymentOption.STRIPE_CARDS.getOrdinal()){
			configData = MyApplication.getInstance().getWalletCore().updateStripeCards(stripeCardData);
		}else{
			configData = MyApplication.getInstance().getWalletCore().updateAcceptCards(stripeCardData);
		}

		if (getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName()) != null) {

			WalletFragment walletFragment = ((WalletFragment) getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName()));
			walletFragment.setCardsPaymentUI(configData);

		}
		if (!cardAdded) {
			if (Data.autoData != null && (Data.autoData.getPickupPaymentOption() == configData.getPaymentOption())) {
				MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
			}
		}

		DialogPopup.alertPopupWithListener(PaymentActivity.this, "", message, new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (cardAdded && getIntent().getBooleanExtra(Constants.KEY_ADD_CARD_DRIVER_TIP, false)) {
					setResult(Activity.RESULT_OK);
					finish();
				}else{
					onBackPressed();
				}
			}


		});

	}




}
