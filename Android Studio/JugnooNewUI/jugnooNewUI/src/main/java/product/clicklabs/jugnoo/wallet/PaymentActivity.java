package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.ui.fragments.FeedHomeFragment;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.fragments.MealFragment;
import com.sabkuchfresh.fragments.MenusFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.OrderStatus;
import com.sabkuchfresh.pros.ui.fragments.ProsHomeFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsProductsFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.model.AddCardPayStackModel;
import product.clicklabs.jugnoo.stripe.StripeAddCardFragment;
import product.clicklabs.jugnoo.stripe.StripeCardsStateListener;
import product.clicklabs.jugnoo.stripe.StripeViewCardFragment;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.fragments.AddWalletFragment;
import product.clicklabs.jugnoo.wallet.fragments.PayStackAddCardFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletRechargeFragment;
import product.clicklabs.jugnoo.wallet.fragments.WalletTransactionsFragment;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;


/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity implements StripeCardsStateListener, WalletFragment.WalletFragmentListener {

    private final String TAG = PaymentActivity.class.getSimpleName();

    public int paymentActivityPathInt = PaymentActivityPath.WALLET.getOrdinal();
    public String amountToPreFill = "";
    private WalletAddMoneyState walletAddMoneyState;
    private boolean isFromDelete = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        new ASSL(this, (ViewGroup) findViewById(R.id.fragLayout), 1134, 720, false);

        paymentActivityPathInt = getIntent()
                .getIntExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());

        if (PaymentActivityPath.WALLET.getOrdinal() == paymentActivityPathInt) {
            int openWalletTransactions = getIntent().getIntExtra(Constants.KEY_WALLET_TRANSACTIONS, 0);
            if (openWalletTransactions == 1) {
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

        } else if(PaymentActivityPath.WALLET.getOrdinal() == paymentActivityPathInt&&getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MPESA.getOrdinal())==PaymentOption.MPESA.getOrdinal()){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.fragLayout, WalletRechargeFragment.newInstance(PaymentOption.MPESA.getOrdinal()), WalletRechargeFragment.class.getName())
                    .addToBackStack(WalletRechargeFragment.class.getName())
                    .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                            .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commit();
        }else if (PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal() == paymentActivityPathInt) {
            if (getIntent().hasExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE)) {
                amountToPreFill = getIntent().getStringExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE);
            }
            int walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragLayout, WalletRechargeFragment.newInstance(walletType), WalletRechargeFragment.class.getName())
                    .addToBackStack(WalletRechargeFragment.class.getName())
                    .commitAllowingStateLoss();
        } else if (PaymentActivityPath.ADD_WALLET.getOrdinal() == paymentActivityPathInt) {
            int walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
            if (walletType == PaymentOption.STRIPE_CARDS.getOrdinal() || walletType == PaymentOption.ACCEPT_CARD.getOrdinal()) {
                PaymentOption paymentOption = walletType == PaymentOption.STRIPE_CARDS.getOrdinal() ? PaymentOption.STRIPE_CARDS : PaymentOption.ACCEPT_CARD;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragLayout, StripeAddCardFragment.newInstance(paymentOption), StripeAddCardFragment.class.getName())
                        .addToBackStack(StripeAddCardFragment.class.getName())
                        .commitAllowingStateLoss();
            } else if (walletType == PaymentOption.PAY_STACK_CARD.getOrdinal()) {

                openPayStackAddCardFragment();

            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragLayout, AddWalletFragment.newInstance(walletType), AddWalletFragment.class.getName())
                        .addToBackStack(AddWalletFragment.class.getName())
                        .commitAllowingStateLoss();
            }

        }
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter(Data.LOCAL_BROADCAST));
        } catch (Exception e) {

        }
        setWalletAddMoneyState(WalletAddMoneyState.INIT);
    }


    @Override
    protected void onDestroy() {
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(WalletRechargeFragment.class.getName());
            Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(StripeViewCardFragment.class.getName());
            if (fragment != null
                    && fragment.isVisible()
                    && fragment instanceof WalletRechargeFragment
                    && ((WalletRechargeFragment) fragment).getButtonRemoveWalletVisiblity() == View.VISIBLE) {
                ((WalletRechargeFragment) fragment).performBackPressed();
            } else if (fragment1 != null
                    && fragment1.isVisible()
                    && fragment1 instanceof StripeViewCardFragment && isFromDelete) {
                isFromDelete = false;
            } else {
                goBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void goBack() {
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
        if (!newIntentReceived && getWalletAddMoneyState() != WalletAddMoneyState.SUCCESS) {
            getBalance("Refresh", PaymentOption.CASH.getOrdinal());
        } else {
            setWalletAddMoneyState(WalletAddMoneyState.INIT);
        }
        Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, PaymentActivity.class.getName());
        newIntentReceived = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, "");
    }

    boolean newIntentReceived = false;

    @Override
    protected void onNewIntent(Intent intent) {
        newIntentReceived = true;
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
                    Fragment currFrag = getSupportFragmentManager().findFragmentByTag(AddWalletFragment.class.getName());
                    if (currFrag != null) {
                        ((AddWalletFragment) currFrag).receiveOtp(otp);
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


    private ApiFetchWalletBalance apiFetchWalletBalance = null;
    private String fragName = "Refresh";
    private int paymentOption = PaymentOption.CASH.getOrdinal();

    public void getBalance(String fragName, int paymentOption) {
        try {
            this.fragName = fragName;
            this.paymentOption = paymentOption;
            if (apiFetchWalletBalance == null) {
                apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        performGetBalanceSuccess(PaymentActivity.this.fragName, PaymentActivity.this.paymentOption);
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

    public void performGetBalanceSuccess(String fragName, int paymentOption) {
        Intent intent = new Intent(Constants.INTENT_ACTION_WALLET_UPDATE);
        LocalBroadcastManager.getInstance(PaymentActivity.this).sendBroadcast(intent);
        try {
            Fragment currFrag = null;
            if (fragName.equalsIgnoreCase(WalletRechargeFragment.class.getName())) {
                MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(paymentOption);
                currFrag = getSupportFragmentManager().findFragmentByTag(WalletRechargeFragment.class.getName());
                if (currFrag != null) {
                    ((WalletRechargeFragment) currFrag).onResume();
                    ((WalletRechargeFragment) currFrag).performBackPressed();
                }
            } else if (fragName.equalsIgnoreCase(AddWalletFragment.class.getName())) {
                MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(paymentOption);
                goBack();
            }
            currFrag = getSupportFragmentManager().findFragmentByTag(WalletFragment.class.getName());
            if (currFrag != null) {
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
    public void onCardsUpdated(ArrayList<StripeCardData> stripeCardData, String message, final boolean cardAdded, PaymentOption paymentOption) {


        PaymentModeConfigData configData;
        if (paymentOption.getOrdinal() == PaymentOption.PAY_STACK_CARD.getOrdinal()) {
            onBackPressed();
            getBalance(WalletFragment.class.getName(), PaymentOption.PAY_STACK_CARD.getOrdinal());
            return;
        } else if (paymentOption.getOrdinal() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
            configData = MyApplication.getInstance().getWalletCore().updateStripeCards(stripeCardData);
            if (stripeCardData.size() > 0) {
                StripeViewCardFragment pfragment = (StripeViewCardFragment) getSupportFragmentManager().findFragmentByTag(StripeViewCardFragment.class.getName());
                if (pfragment != null && pfragment.isAdded()) {
                    pfragment.notifyAdapter(stripeCardData);
                }

                MyApplication.getInstance().getWalletCore().getConfigData(PaymentOption.STRIPE_CARDS.getOrdinal()).setCardsData(stripeCardData);
            }
        } else {
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
            if (stripeCardData.size() > 0) {
                isFromDelete = true;
            } else {
                isFromDelete = false;
            }

        }

        DialogPopup.alertPopupWithListener(PaymentActivity.this, "", message, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cardAdded && getIntent().getBooleanExtra(Constants.KEY_ADD_CARD_DRIVER_TIP, false)) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {

                    onBackPressed();
                }
            }


        });

    }


    @Override
    public void openPayStackAddCardFragment() {

        HashMap<String, String> params = new HashMap<>();

        new ApiCommon<AddCardPayStackModel>(this).showLoader(true).execute(params, ApiName.ADD_CARD_PAYSTACK
                , new APICommonCallback<AddCardPayStackModel>() {
                    @Override
                    public void onSuccess(AddCardPayStackModel addCardPayStackModel, String message, int flag) {
                        if (!isFinishing()) {

                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                                    .add(R.id.fragLayout, PayStackAddCardFragment.newInstance(addCardPayStackModel.getUrl()), PayStackAddCardFragment.class.getName())
                                    .addToBackStack(PayStackAddCardFragment.class.getName());
                            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                transaction.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
                            }
                            transaction.commitAllowingStateLoss();
                        }


                    }

                    @Override
                    public boolean onError(AddCardPayStackModel addCardPayStackModel, String message, int flag) {
                        if (!isFinishing()) {
                            DialogPopup.alertPopupWithListener(PaymentActivity.this, "", message, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isFinishing()) {
                                        PaymentActivity.this.onBackPressed();
                                    }
                                }
                            });

                        }
                        return true;
                    }
                });
    }

    public interface UpdateCardsCallback{
        void onNewCardAdded();
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            // Get extra data included in the Intent
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        switch (intent.getAction()) {
                            case Data.LOCAL_BROADCAST:
                                int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                                if (PushFlags.MPESA_PAYMENT_SUCCESS.getOrdinal() == flag) {
                                    DialogPopup.dismissLoadingDialog();
                                    DialogPopup.alertPopup(PaymentActivity.this, "", intent.getStringExtra("message"));
                                    getBalance(WalletRechargeFragment.class.getName(), PaymentOption.CASH.getOrdinal());

                                    } else if (PushFlags.MPESA_PAYMENT_FAILURE.getOrdinal() == flag) {
                                    DialogPopup.dismissLoadingDialog();
                                        DialogPopup.alertPopup(PaymentActivity.this, "", intent.getStringExtra("message"));
                                    }
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}


