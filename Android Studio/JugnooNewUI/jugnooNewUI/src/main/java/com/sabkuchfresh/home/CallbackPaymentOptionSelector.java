package com.sabkuchfresh.home;

import product.clicklabs.jugnoo.datastructure.PaymentOption;

/**
 * Created by socomo on 12/27/16.
 */

public interface CallbackPaymentOptionSelector{
    void onPaymentOptionSelected(PaymentOption paymentOption);
    void onWalletAdd(PaymentOption paymentOption);
}
