package product.clicklabs.jugnoo.wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.PaytmPaymentState;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity{

    public String txnId = "";
    public String uName = "";
    public String uProdInfo = "";
    public String uEmail = "";
    public String hash = "";
    public String hashSdk = "";
    public String payUOffer = "";

    // for wallet cash
    public String enterAmount = "";
    public String pgName = "";
    public String bankCode = "";
    public String ccNum = "";
    public String ccName = "";
    public String ccvv = "";
    public String ccexpmon = "";
    public String ccexpyr = "";

    public static AddPaymentPath addPaymentPath = AddPaymentPath.FROM_WALLET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

		new ASSL(this, (ViewGroup) findViewById(R.id.mainHomelayout), 1134, 720, false);
        

//        if(AddPaymentPath.FROM_WALLET == addPaymentPath) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fragLayout, new WalletFragment(), "WalletFragment").addToBackStack("WalletFragment")
                .commitAllowingStateLoss();
//        } else {
//            getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragLayout, new WalletAddPaymentFragment(), "WalletAddPaymentFragment").addToBackStack("WalletAddPaymentFragment")
//                .commitAllowingStateLoss();
//        }

		Data.paytmPaymentState = PaytmPaymentState.INIT;
    }


    @Override
    public void onBackPressed() {
		try {
			if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                 finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Fragment paytmRechargeFragment = getSupportFragmentManager().findFragmentByTag("PaytmRechargeFragment");
                if(paytmRechargeFragment != null && paytmRechargeFragment.isVisible()
                    && paytmRechargeFragment instanceof PaytmRechargeFragment){
                    ((PaytmRechargeFragment)paytmRechargeFragment).performBackPressed();
                } else{
                    super.onBackPressed();
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
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
			Fragment currFrag = getSupportFragmentManager().findFragmentByTag("WalletFragment");
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
					RequestParams params = new RequestParams();
					params.put("access_token", Data.userData.accessToken);
					params.put("client_id", Config.getClientId());
					params.put("is_access_token_new", "1");

					AsyncHttpClient client = Data.getClient();
					client.post(Config.getTXN_URL() + "/paytm/check_balance", params, new CustomAsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							Log.i("request succesfull", "response = " + response);
							try {
								JSONObject jObj = new JSONObject(response.toString());
								JSONParser.parsePaytmBalanceStatus(PaymentActivity.this, jObj);
								performGetBalanceSuccess(fragName);
							} catch (Exception e) {
								e.printStackTrace();
								retryDialog(Data.SERVER_ERROR_MSG, fragName);
							}
							DialogPopup.dismissLoadingDialog();
						}

						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							retryDialog(Data.SERVER_NOT_RESOPNDING_MSG, fragName);
						}
					});
				} else {
					retryDialog(Data.CHECK_INTERNET_MSG, fragName);
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
				currFrag = getSupportFragmentManager().findFragmentByTag("PaytmRechargeFragment");
				if(currFrag != null){
					currFrag.onResume();
					((PaytmRechargeFragment) currFrag).performBackPressed();
				}
			}
			else if(fragName.equalsIgnoreCase(AddPaytmFragment.class.getName())){
				currFrag = getSupportFragmentManager().findFragmentByTag("AddPaytmFragment");
				if(currFrag != null){
					((AddPaytmFragment) currFrag).performBackPressed();
				}
			}
			currFrag = getSupportFragmentManager().findFragmentByTag("WalletFragment");
			if(currFrag != null){
				currFrag.onResume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(String message, final String fragName){
		DialogPopup.alertPopupTwoButtonsWithListeners(PaymentActivity.this, "", message, "Retry", "Cancel",
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
