package com.sabkuchfresh.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.R;
import com.sabkuchfresh.TokenGenerator.HomeUtil;
import com.sabkuchfresh.apis.ApiPaytmCheckBalance;
import com.sabkuchfresh.datastructure.AddPaymentPath;
import com.sabkuchfresh.datastructure.PaytmPaymentState;
import com.sabkuchfresh.home.BaseFragmentActivity;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Prefs;
import com.sabkuchfresh.utils.Utils;


/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity {

    private final String TAG = PaymentActivity.class.getSimpleName();

    public int addPaymentPathInt = AddPaymentPath.WALLET.getOrdinal();
    public String amountToPreFill = "";

    public int paymentPath = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        new ASSL(this, (ViewGroup) findViewById(R.id.mainHomelayout), 1134, 720, false);

        addPaymentPathInt = getIntent().getIntExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
        if (getIntent().hasExtra(Constants.KEY_PAYMENT_PATH)) {
            paymentPath = getIntent().getIntExtra(Constants.KEY_PAYMENT_PATH, 0);
        }

        if (AddPaymentPath.WALLET.getOrdinal() == addPaymentPathInt) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(R.id.fragLayout, new WalletFragment(), WalletFragment.class.getName())
                    .addToBackStack(WalletFragment.class.getName())
                    .commitAllowingStateLoss();
        } else if (AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == addPaymentPathInt) {
            if (getIntent().getExtras().containsKey(Constants.KEY_PAYMENT_RECHARGE_VALUE)) {
                amountToPreFill = getIntent().getStringExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE);
            }
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(R.id.fragLayout, new PaytmRechargeFragment(), PaytmRechargeFragment.class.getName())
                    .addToBackStack(PaytmRechargeFragment.class.getName())
                    .commitAllowingStateLoss();
        } else if (AddPaymentPath.ADD_PAYTM.getOrdinal() == addPaymentPathInt) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
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
                    && ((PaytmRechargeFragment) fragment).getButtonRemoveWalletVisiblity() == View.VISIBLE) {
                ((PaytmRechargeFragment) fragment).performBackPressed();
            } else {
                goBack();
            }

        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private void goBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, PaymentActivity.class.getName());
        Utils.enableSMSReceiver(this);
        HomeUtil.checkForAccessTokenChange(this);
        if (Data.paytmPaymentState != PaytmPaymentState.SUCCESS) {
            getBalance("Refresh");
        } else {
            Data.paytmPaymentState = PaytmPaymentState.INIT;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, "");
        Utils.disableSMSReceiver(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        retrieveOTPFromSMS(intent);
        super.onNewIntent(intent);
    }

    private void retrieveOTPFromSMS(Intent intent) {
        try {
            String otp = "";
            if (intent.hasExtra("message")) {
                String message = intent.getStringExtra("message");
                otp = Utils.retrieveOTPFromSMS(message);
            }

            if (Utils.checkIfOnlyDigits(otp)) {
                if (!"".equalsIgnoreCase(otp)) {
                    Fragment currFrag = getSupportFragmentManager().findFragmentByTag(AddPaytmFragment.class.getName());
                    if (currFrag != null) {
                        ((AddPaytmFragment) currFrag).receiveOtp(otp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWalletFragment() {
        try {
            Fragment currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
            if (currFrag != null) {
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
            if (apiPaytmCheckBalance == null) {
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
                });
            }
            apiPaytmCheckBalance.getBalance(Data.userData.paytmEnabled, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performGetBalanceSuccess(String fragName) {
        try {
            Fragment currFrag = null;
            if (fragName.equalsIgnoreCase(PaytmRechargeFragment.class.getName())) {
                currFrag = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
                if (currFrag != null) {
                    currFrag.onResume();
                    ((PaytmRechargeFragment) currFrag).performBackPressed();
                }
            } else if (fragName.equalsIgnoreCase(AddPaytmFragment.class.getName())) {
                currFrag = getSupportFragmentManager().findFragmentByTag(AddPaytmFragment.class.getName());
                if (currFrag != null) {
                    ((AddPaytmFragment) currFrag).performBackPressed();
                }
            }
            currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
            if (currFrag != null) {
                currFrag.onResume();
            }

            if (AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == addPaymentPathInt) {
                currFrag = getSupportFragmentManager().findFragmentByTag(PaytmRechargeFragment.class.getName());
                if (currFrag != null) {
                    currFrag.onResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(String message, final String fragName) {
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
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, false, false);
    }

}
