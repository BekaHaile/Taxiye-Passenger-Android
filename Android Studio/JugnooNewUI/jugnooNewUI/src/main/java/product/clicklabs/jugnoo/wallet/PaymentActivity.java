package product.clicklabs.jugnoo.wallet;

import android.os.Bundle;
import android.os.Handler;

import com.payu.sdk.ClearFragment;
import com.payu.sdk.Constants;
import com.payu.sdk.GetResponseTask;
import com.payu.sdk.PayU;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.ProcessPaymentActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by socomo30 on 7/8/15.
 */
public class PaymentActivity extends BaseFragmentActivity implements PaymentListener, ClearFragment {

    public String txnId = "";
    public String uName = "";
    public String uProdInfo = "";
    public String uEmail = "";
    public String hash = "";
    public String hashSdk = "";

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

        
        ProcessPaymentActivity.clearFrag = this;

        if(AddPaymentPath.FROM_WALLET == addPaymentPath) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fragLayout, new WalletFragment(), "WalletFragment").addToBackStack("WalletFragment")
                .commitAllowingStateLoss();

        } else {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fragLayout, new WalletAddPaymentFragment(), "WalletAddPaymentFragment").addToBackStack("WalletAddPaymentFragment")
                .commitAllowingStateLoss();
        }
        
    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
             finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void finishCalled(final int value) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(value == CommonFlags.PAYMENT_BACKPRESSED.getOrdinal()) {
                    new DialogPopup().alertPopup(PaymentActivity.this, "", ""+getResources().getString(R.string.close_payment_screen));
                }
                else if(value == CommonFlags.PAYMENT_SUCCESS.getOrdinal()) {

                    try {
                        getSupportFragmentManager().popBackStack("WalletAddPaymentFragment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
                    }catch(Exception e){
                        getSupportFragmentManager().popBackStack();
                    }

                    WalletFragment frag = (WalletFragment) getSupportFragmentManager().findFragmentByTag("WalletFragment");
                    if (frag != null) {
                        frag.updateStatus("success", enterAmount);
                    } else {
                        new DialogPopup().dialogBanner(PaymentActivity.this, "Payment successful, Added Rs. " + enterAmount);
                        Data.userData.jugnooBalance += Double.parseDouble(enterAmount);
                        getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragLayout, new WalletFragment(), "WalletFragment").addToBackStack("WalletFragment")
                            .commitAllowingStateLoss();
                    }

                    if(AddPaymentPath.FROM_IN_RIDE == addPaymentPath){
                        HomeActivity.rechargedOnce = true;
                    }



                }else if(value == CommonFlags.PAYMENT_FAILED.getOrdinal()) {
                    new DialogPopup().dialogBanner(PaymentActivity.this, ""+getResources().getString(R.string.trans_failed));

                    WalletFragment frag = (WalletFragment) getSupportFragmentManager().findFragmentByTag("WalletFragment");
                    if (frag == null) {
                        getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragLayout, new WalletFragment(), "WalletFragment").addToBackStack("WalletFragment")
                            .commitAllowingStateLoss();
                    }

                }else if(value == CommonFlags.PAYMENT_ERROR.getOrdinal()) {
                    new DialogPopup().dialogBanner(PaymentActivity.this, ""+getResources().getString(R.string.trans_failed));

                    WalletFragment frag = (WalletFragment) getSupportFragmentManager().findFragmentByTag("WalletFragment");
                    if (frag == null) {
                        getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragLayout, new WalletFragment(), "WalletFragment").addToBackStack("WalletFragment")
                            .commitAllowingStateLoss();
                    }
                }
            }
        }, 200);

        Log.e("value", "finish value = " + value);
    }

//    public static JSONArray availableBanks;
//
//    public void getListBanks() {
//
//        availableBanks = new JSONArray();
//
//        List<NameValuePair> postParams = null;
//
//        HashMap varList = new HashMap();
//
//        varList.put(Constants.VAR1, Constants.DEFAULT);
//
//        try {
//            postParams = PayU.getInstance(PaymentActivity.this).getParams(Constants.PAYMENT_RELATED_DETAILS, varList);
//            GetResponseTask getResponse = new GetResponseTask(PaymentActivity.this);
//            getResponse.execute(postParams);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {
//
//    }
//
//    @Override
//    public void onGetResponse(String responseMessage) {
//
//    }
//
//    @Override
//    public void onBankDetails(JSONArray availableBanksData) {
//        availableBanks = availableBanksData;
//        AddJugnooCashFragment frag = (AddJugnooCashFragment) getSupportFragmentManager().findFragmentByTag("AddJugnooCashFragment");
//        if(PayU.availableBanks!= null) {
//            if (frag != null) {
//                frag.setupAdapter();
//            }
//        } else {
//            Toast.makeText(this, "Null array", Toast.LENGTH_SHORT).show();
//            Log.e("availableBanksData", "availableBanksData");
//            Log.e("availableBanksData", "availableBanksData = "+ availableBanksData.length());
//
//            PayU.availableBanks = availableBanksData;
//            if (frag != null) {
//                //frag.setupAdapter();
//            }
//        }
//    }


    public static JSONArray availableBanks;

    public void getListBanks() {

        availableBanks = new JSONArray();

        List<NameValuePair> postParams = null;
        HashMap varList = new HashMap();
        varList.put(Constants.VAR1, Constants.DEFAULT);

        try {
            postParams = PayU.getInstance(PaymentActivity.this).getParams(Constants.PAYMENT_RELATED_DETAILS, varList);
            GetResponseTask getResponse = new GetResponseTask(PaymentActivity.this);
            getResponse.execute(postParams);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        fetchStoredCards();

    }

    public void fetchStoredCards() {
        List<NameValuePair> postParams = null;
        HashMap varList = new HashMap();
        Log.e("PayU.userCredentials", "PayU.userCredentials = "+PayU.userCredentials);
        varList.put(Constants.VAR1, PayU.userCredentials); // this will return the storedCards as well


        try {
            postParams = PayU.getInstance(this).getParams(Constants.GET_USER_CARDS, varList);
            android.util.Log.e("postParams", "postParams = " + postParams);
            GetResponseTask getStoredCards = new GetResponseTask(this);
            getStoredCards.execute(postParams);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void deleteCard(String token) {
        List<NameValuePair> postParams = null;
        HashMap varList = new HashMap();
        varList.put(Constants.VAR1, PayU.userCredentials); // this will return the storedCards as well
        varList.put(Constants.VAR2, token);


        try {
            postParams = PayU.getInstance(this).getParams(Constants.DELETE_USER_CARD, varList);
            android.util.Log.e("postParams", "postParams = " + postParams);
            GetResponseTask getStoredCards = new GetResponseTask(this);
            getStoredCards.execute(postParams);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBankDetails(JSONArray availableBanksData) {

        availableBanks = availableBanksData;
        AddJugnooCashFragment frag = (AddJugnooCashFragment) getSupportFragmentManager().findFragmentByTag("AddJugnooCashFragment");
        if(PayU.availableBanks!= null) {
            if (frag != null) {
                frag.setupAdapter();
            }
        } else {
            Log.e("availableBanksData", "availableBanksData");
            Log.e("availableBanksData", "availableBanksData = " + availableBanksData.length());

            PayU.availableBanks = availableBanksData;
            if (frag != null) {
                //frag.setupAdapter();
            }
        }
    }

    @Override
    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {

    }

    @Override
    public void onGetResponse(String responseMessage) {

        Log.e("responseMessage", "responseMessage = "+responseMessage);
        AddJugnooCashFragment frag = (AddJugnooCashFragment) getSupportFragmentManager().findFragmentByTag("AddJugnooCashFragment");
        if(responseMessage != null) {

            if(frag != null) {
                if(responseMessage.contains("Card not found")) {
                    PayU.storedCards = new JSONArray(new ArrayList<String>());
                    frag.notifyStoreCard();
                } else if (PayU.storedCards != null) {
                    frag.notifyStoreCard();
                } else {
                    frag.stopLoading();
                }
            }

            if (responseMessage.contains("deleted successfully")) {
                Log.e("responseMessage", "responseMessage = 1");
                fetchStoredCards();
                if(frag != null) {
                    Log.e("responseMessage", "responseMessage = 2");
                    frag.removeCard();
                }
            }

        }
    }
}
