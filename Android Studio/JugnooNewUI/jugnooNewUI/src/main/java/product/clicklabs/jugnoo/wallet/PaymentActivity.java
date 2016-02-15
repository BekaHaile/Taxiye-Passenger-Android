package product.clicklabs.jugnoo.wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.PaytmPaymentState;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity{

	private final String TAG = PaymentActivity.class.getSimpleName();

	public int addPaymentPathInt = AddPaymentPath.WALLET.getOrdinal();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

		new ASSL(this, (ViewGroup) findViewById(R.id.mainHomelayout), 1134, 720, false);

		addPaymentPathInt = getIntent()
				.getIntExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());

		if(AddPaymentPath.WALLET.getOrdinal() == addPaymentPathInt){
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new WalletFragment(), WalletFragment.class.getName())
					.addToBackStack(WalletFragment.class.getName())
					.commitAllowingStateLoss();
		}
		else if(AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == addPaymentPathInt){
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new PaytmRechargeFragment(), PaytmRechargeFragment.class.getName())
					.addToBackStack(PaytmRechargeFragment.class.getName())
					.commitAllowingStateLoss();
		}
		else if(AddPaymentPath.ADD_PAYTM.getOrdinal() == addPaymentPathInt){
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragLayout, new AddPaytmFragment(), AddPaytmFragment.class.getName())
					.addToBackStack(AddPaytmFragment.class.getName())
					.commitAllowingStateLoss();
		}

		Data.paytmPaymentState = PaytmPaymentState.INIT;
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

//			if(AddPaymentPath.WALLET.getOrdinal() == addPaymentPathInt){
//				goBack();
//			}
//			else if(AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == addPaymentPathInt){
//				Fragment fragment = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
//				if (fragment != null && fragment.isVisible() && fragment instanceof PaytmRechargeFragment) {
//					((PaytmRechargeFragment) fragment).performBackPressed();
//				} else{
//					goBack();
//				}
//			}
//			else if(AddPaymentPath.ADD_PAYTM.getOrdinal() == addPaymentPathInt){
//				Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddPaytmFragment.class.getName());
//				if (fragment != null && fragment.isVisible() && fragment instanceof AddPaytmFragment) {
//					((AddPaytmFragment) fragment).performBackPressed();
//				} else{
//					goBack();
//				}
//			}
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
		if(Data.paytmPaymentState != PaytmPaymentState.SUCCESS) {
			getBalance("Refresh");
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


	public void getBalance(final String fragName) {
		try {
			if(1 == Data.userData.paytmEnabled) {
				if (AppStatus.getInstance(this).isOnline(this)) {
					DialogPopup.showLoadingDialog(this, "Loading...");

					HashMap<String, String> params = new HashMap<>();
					params.put("access_token", Data.userData.accessToken);
					params.put("client_id", Config.getClientId());
					params.put("is_access_token_new", "1");

					RestClient.getApiServices().paytmCheckBalance(params, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "paytmCheckBalance response = " + responseStr);
							try {
								JSONObject jObj = new JSONObject(responseStr);
								JSONParser.parsePaytmBalanceStatus(PaymentActivity.this, jObj);
								performGetBalanceSuccess(fragName);
							} catch (Exception e) {
								e.printStackTrace();
								retryDialog(Data.SERVER_ERROR_MSG, fragName);
							}
							DialogPopup.dismissLoadingDialog();
						}

						@Override
						public void failure(RetrofitError error) {
							try {
								Log.e(TAG, "paytmCheckBalance error="+error.toString());
								DialogPopup.dismissLoadingDialog();
								retryDialog(Data.SERVER_NOT_RESOPNDING_MSG, fragName);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				} else {
					//retryDialog(Data.CHECK_INTERNET_MSG, fragName);
					DialogPopup.dialogNoInternet(PaymentActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View v) {
							getBalance(fragName);
						}

						@Override
						public void neutralClick(View v) {

						}

						@Override
						public void negativeClick(View v) {

						}
					});
				}
			}
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
					currFrag.onResume();
					((PaytmRechargeFragment) currFrag).performBackPressed();
				}
			}
			else if(fragName.equalsIgnoreCase(AddPaytmFragment.class.getName())){
				currFrag = getSupportFragmentManager().findFragmentByTag(AddPaytmFragment.class.getName());
				if(currFrag != null){
					((AddPaytmFragment) currFrag).performBackPressed();
				}
			}
			currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
			if(currFrag != null){
				currFrag.onResume();
			}

			if(AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == addPaymentPathInt){
				currFrag = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
				if(currFrag != null){
					currFrag.onResume();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(String message, final String fragName){
		DialogPopup.alertPopupTwoButtonsWithListeners(PaymentActivity.this, "", message,
				getResources().getString(R.string.retry), getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getBalance(fragName);
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.left_in, R.anim.left_out);
					}
				}, false, false);
	}

}
