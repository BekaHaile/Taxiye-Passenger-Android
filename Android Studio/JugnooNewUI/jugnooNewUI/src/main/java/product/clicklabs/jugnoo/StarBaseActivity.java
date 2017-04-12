package product.clicklabs.jugnoo;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.RazorpayCallbackService;
import com.sabkuchfresh.retrofit.model.PurchaseSubscriptionResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import io.paperdb.Paper;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by ankit on 07/04/17.
 */

public class StarBaseActivity extends BaseFragmentActivity implements PaymentResultWithDataListener {

    // razor pay callbacks
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        String paymentId = paymentData.getPaymentId();
        String signature = paymentData.getSignature();
        razorpayCallbackIntentService(paymentId, signature);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        razorpayCallbackIntentService("-1", "-1");
    }

    public void startRazorPayPayment(JSONObject options, boolean isUPA) {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.jugnoo_icon);
        try {
            options.remove(Constants.KEY_AUTH_ORDER_ID);

            options.put(Constants.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(Constants.KEY_USER_EMAIL).toString());
            options.put(Constants.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(Constants.KEY_PHONE_NO).toString());
            options.put(Constants.KEY_RAZORPAY_THEME_COLOR, "#FD7945");
            if(isUPA){
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "upi"); // "upi", ""
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : ""); // "upi", ""
            } else{
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "");
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : "");
            }

            Log.i("StarBaseActivity", "startRazorPayPayment options="+options);
            checkout.setFullScreenDisable(true);

            checkout.open(this, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }

    public int getAppType() {
        return Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
    }

    // razor pay callback intent service
    public void razorpayCallbackIntentService(String paymentId, String signature){
        try {
            Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
            Intent intent = new Intent(this, RazorpayCallbackService.class);
            intent.putExtra(Constants.KEY_APP_TYPE, getAppType());
            intent.putExtra(Constants.KEY_ACCESS_TOKEN, pair.first);
            intent.putExtra(Constants.KEY_RAZORPAY_PAYMENT_ID, paymentId);
            intent.putExtra(Constants.KEY_RAZORPAY_SIGNATURE, signature);
            intent.putExtra(Constants.KEY_ORDER_ID, getPurchaseSubscriptionResponse().getOrderId().intValue());
            intent.putExtra(Constants.KEY_AUTH_ORDER_ID, getPurchaseSubscriptionResponse().getRazorPaymentObject().getAuthOrderId().intValue());
            startService(intent);
            DialogPopup.showLoadingDialog(this, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //placeOrderResponse cached for PAY and RAZORPAY payment callbacks
    private PurchaseSubscriptionResponse purchaseSubscriptionResponse;
    public void setPurchaseSubscriptionResponse(PurchaseSubscriptionResponse purchaseSubscriptionResponse){
        this.purchaseSubscriptionResponse = purchaseSubscriptionResponse;
        if(purchaseSubscriptionResponse != null) {
            Paper.book().write(PaperDBKeys.DB_STAR_PURCHASE_RESP, purchaseSubscriptionResponse);
        } else {
            Paper.book().delete(PaperDBKeys.DB_STAR_PURCHASE_RESP);
        }
    }

    public PurchaseSubscriptionResponse getPurchaseSubscriptionResponse(){
        if(purchaseSubscriptionResponse == null){
            purchaseSubscriptionResponse = Paper.book().read(PaperDBKeys.DB_STAR_PURCHASE_RESP);
        }
        return purchaseSubscriptionResponse;
    }
}
