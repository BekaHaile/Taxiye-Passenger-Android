package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sabkuchfresh.home.RazorpayCallbackService;

import org.json.JSONObject;

import product.clicklabs.jugnoo.retrofit.model.PaymentResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by ankit on 07/04/17.
 */

public class RazorpayBaseActivity extends BaseAppCompatActivity implements PaymentResultWithDataListener {

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

    public void startRazorPayPayment(PaymentResponse.RazorpayData options, boolean isUPI) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.KEY_ORDER_ID, options.getOrderId());
            jsonObject.put(Constants.KEY_PHONE_NO, options.getPhoneNo());
            jsonObject.put(Constants.KEY_USER_EMAIL, options.getUserEmail());
            jsonObject.put(Constants.KEY_DESCRIPTION, options.getDescription());
            jsonObject.put(Constants.KEY_AUTH_ORDER_ID, options.getAuthOrderId());
            jsonObject.put(Constants.KEY_AMOUNT, options.getAmount());
            jsonObject.put(Constants.KEY_CURRENCY, options.getCurrency());
            jsonObject.put(Constants.KEY_NAME, options.getName());
            startRazorPayPayment(jsonObject, isUPI);
        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            JSONObject jObj = new JSONObject();
            try {
                jObj = new JSONObject(gson.toJson(options, PaymentResponse.RazorpayData.class));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            startRazorPayPayment(jObj, isUPI);
        }
    }

    public void startRazorPayPayment(JSONObject options, boolean isUPI) {
        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);
        try {
            options.remove(Constants.KEY_AUTH_ORDER_ID);

            options.put(Constants.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(Constants.KEY_USER_EMAIL).toString());
            options.put(Constants.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(Constants.KEY_PHONE_NO).toString());
            options.put(Constants.KEY_RAZORPAY_THEME_COLOR, "#FD7945");
            if(isUPI){
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "upi"); // "upi", ""
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : ""); // "upi", ""
            } else{
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "");
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : "");
            }

            Log.i("RazorpayBaseActivity", "startRazorPayPayment options="+options);
            checkout.setFullScreenDisable(true);

            checkout.open(this, options);
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }

    public int getAppType() {
        return Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
    }

    // razor pay callback intent service
    private void razorpayCallbackIntentService(String paymentId, String signature){
        try {
            Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
            Intent intent = new Intent(this, RazorpayCallbackService.class);
            intent.putExtra(Constants.KEY_ACCESS_TOKEN, pair.first);
            intent.putExtra(Constants.KEY_RAZORPAY_PAYMENT_ID, paymentId);
            intent.putExtra(Constants.KEY_RAZORPAY_SIGNATURE, signature);
            intent.putExtra(Constants.KEY_ORDER_ID, getOrderId());
            intent.putExtra(Constants.KEY_AUTH_ORDER_ID, getAuthOrderId());
            startService(intent);
            DialogPopup.showLoadingDialog(this, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPurchaseSubscriptionResponse(int orderId, int authOrderId){
        Prefs.with(this).save(Constants.SP_RZP_ORDER_ID, orderId);
        Prefs.with(this).save(Constants.SP_RZP_AUTH_ORDER_ID, authOrderId);
    }

    private int getOrderId(){
        return Prefs.with(this).getInt(Constants.SP_RZP_ORDER_ID, -1);
    }
    private int getAuthOrderId(){
        return Prefs.with(this).getInt(Constants.SP_RZP_AUTH_ORDER_ID, -1);
    }

    private Handler handler;
    public Handler getHandler(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }
}
