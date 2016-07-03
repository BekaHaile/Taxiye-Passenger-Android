package product.clicklabs.jugnoo.wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiPaytmCheckBalance;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.wallet.fragments.AddWalletFragment;
import product.clicklabs.jugnoo.wallet.fragments.PaytmRechargeFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletFragment;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;
import product.clicklabs.jugnoo.wallet.models.WalletType;


/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity{

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
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new WalletFragment(), WalletFragment.class.getName())
					.addToBackStack(WalletFragment.class.getName())
					.commitAllowingStateLoss();
		}
		else if(PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal() == paymentActivityPathInt){
			if(getIntent().hasExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE)){
				amountToPreFill = getIntent().getStringExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE);
			}
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new PaytmRechargeFragment(), PaytmRechargeFragment.class.getName())
					.addToBackStack(PaytmRechargeFragment.class.getName())
					.commitAllowingStateLoss();
		}
		else if(PaymentActivityPath.ADD_WALLET.getOrdinal() == paymentActivityPathInt){
			int walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, WalletType.PAYTM.getOrdinal());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new AddWalletFragment(walletType), AddWalletFragment.class.getName())
					.addToBackStack(AddWalletFragment.class.getName())
					.commitAllowingStateLoss();
		}

		setWalletAddMoneyState(WalletAddMoneyState.INIT);
    }


    @Override
    public void onBackPressed() {
		try {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
			if (fragment != null
					&& fragment.isVisible()
					&& fragment instanceof PaytmRechargeFragment
					&& ((PaytmRechargeFragment)fragment).getButtonRemoveWalletVisiblity() == View.VISIBLE) {
				((PaytmRechargeFragment) fragment).performBackPressed();
			} else {
				goBack();
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.onBackPressed();
		}
	}

	private void goBack(){
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
		if(getWalletAddMoneyState() != WalletAddMoneyState.SUCCESS) {
			getBalance("Refresh");
		} else{
			setWalletAddMoneyState(WalletAddMoneyState.INIT);
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


	private ApiPaytmCheckBalance apiPaytmCheckBalance = null;
	private String fragName = "Refresh";
	public void getBalance(String fragName) {
		try {
			this.fragName = fragName;
			if(apiPaytmCheckBalance == null){
				apiPaytmCheckBalance = new ApiPaytmCheckBalance(this, new ApiPaytmCheckBalance.Callback() {
					@Override
					public void onSuccess() {
						performGetBalanceSuccess(PaymentActivity.this.fragName);
					}

					@Override
					public void onFailure() {
						getBalance(PaymentActivity.this.fragName);
					}

					@Override
					public void onFinish() {

					}

					@Override
					public void onRetry(View view) {
						getBalance(PaymentActivity.this.fragName);
					}

					@Override
					public void onNoRetry(View view) {

					}

					@Override
					public void paytmDisabled() {

					}
				});
			}
			apiPaytmCheckBalance.getBalance(1, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performGetBalanceSuccess(String fragName){
		try {
			Fragment currFrag = null;
			if(fragName.equalsIgnoreCase(PaytmRechargeFragment.class.getName())) {
				currFrag = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
				if(currFrag != null){
					((PaytmRechargeFragment) currFrag).onResume();
					((PaytmRechargeFragment) currFrag).performBackPressed();
				}
			}
			else if(fragName.equalsIgnoreCase(AddWalletFragment.class.getName())){
				currFrag = getSupportFragmentManager().findFragmentByTag(AddWalletFragment.class.getName());
				if(currFrag != null){
					((AddWalletFragment) currFrag).performBackPressed();
				}
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

}
