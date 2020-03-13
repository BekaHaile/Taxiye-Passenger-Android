package com.sabkuchfresh.fatafatchatpay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.feed.models.FetchOrderStatusResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import androidx.annotation.Nullable;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RazorpayBaseActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by cl-macmini-01 on 12/18/17.
 */

public class FatafatChatPayActivity extends RazorpayBaseActivity implements View.OnClickListener {

    private int orderId;
    private double amount;
    private boolean isUPIOrderPending;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            // check for upi pending, if yes open checkout
            if (extras.containsKey(Constants.KEY_IS_UPI_PENDING) &&
                    Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED) != null) {
                PlaceOrderResponse placeOrderResponse = Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED);
                orderId = placeOrderResponse.getOrderId();
                amount = placeOrderResponse.getAmount();
                isUPIOrderPending = true;
                startCheckout();
            } else {
                // we come from broadcast from fugu, check for order status
                orderId = extras.getInt(Constants.KEY_ORDER_ID);
                amount = extras.getDouble(Constants.KEY_AMOUNT);
                checkOrderStatus();
            }

        } else {
            finish();
        }

    }

    /**
     * Gets the check order status api
     */
    private void checkOrderStatus() {

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));

        new ApiCommon<FetchOrderStatusResponse>(this).showLoader(true).execute(params, ApiName.FEED_FETCH_ORDER_STATUS,
                new APICommonCallback<FetchOrderStatusResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return false;
                    }

                    @Override
                    public boolean onException(final Exception e) {
                        return false;
                    }

                    @Override
                    public void onSuccess(final FetchOrderStatusResponse fetchOrderStatusResponse, final String message, final int flag) {

                        if (!FatafatChatPayActivity.this.isFinishing()) {

                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                                if (fetchOrderStatusResponse.getIsPaid() == 1) {
                                    showAlreadyPaidAlert(message);
                                } else {
                                    // show the checkout screen
                                    startCheckout();
                                }

                            } else {
                                if (!FatafatChatPayActivity.this.isFinishing() && message != null) {
                                    DialogPopup.alertPopup(FatafatChatPayActivity.this, "", message);
                                }
                            }
                        }
                    }

                    @Override
                    public boolean onError(final FetchOrderStatusResponse fetchOrderStatusResponse, final String message, final int flag) {
                        return false;
                    }

                    @Override
                    public boolean onFailure(final Exception error) {
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {

                        //exit
                        finish();
                    }
                });

    }

    /**
     * Shows the checkout screen
     */
    private void startCheckout() {

        setContentView(R.layout.activity_fatafat_chat_pay);

        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        new ASSL(this, rlMain, 1134, 720, false);

        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(R.id.rlFragment, StarSubscriptionCheckoutFragment.newInstance((amount), orderId, isUPIOrderPending))
                .commit();
    }

    /**
     * Show order already placed / paid alert
     */
    private void showAlreadyPaidAlert(String message) {

        DialogPopup.alertPopupWithListener(this, "", message,
                getString(android.R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // exit
                        finish();
                    }
                }, false);

    }

    @Override
    public void onClick(final View v) {

        if (v.getId() == R.id.imageViewBack) {
            finish();
        }
    }
}
