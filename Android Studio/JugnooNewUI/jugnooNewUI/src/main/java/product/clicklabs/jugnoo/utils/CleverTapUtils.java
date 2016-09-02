package product.clicklabs.jugnoo.utils;

import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.MyApplication;

/**
 * Created by gurmail on 02/09/16.
 */
public class CleverTapUtils {

    public void rideRequested(int vehicle_type, int type, double surge, String offerCode) {

        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.VEHICLE_TYPE, vehicle_type);
        //prodViewedAction.put(Events.ETA, eta);
        prodViewedAction.put(Events.SURGE, surge);
        prodViewedAction.put(Events.TYPE, type);
        prodViewedAction.put(Events.OFFER_CODE, offerCode);

        MyApplication.getInstance().sendCleverTapEvent(Events.RIDE_REQUESTED, prodViewedAction);
    }

    public void signUp(String loginVia, String walletSelected, String referralCodeEntered) {
        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.CHANNEL, ""+loginVia);
        prodViewedAction.put(Events.WALLET, ""+walletSelected);
        prodViewedAction.put(Events.PROMO_CODE, referralCodeEntered);
        MyApplication.getInstance().sendCleverTapEvent(Events.SIGNED_UP, prodViewedAction);
    }

    public void addToCart(String productName, int productId, int qty, double amount, int type) {
        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.PRODUCT_NAME, productName);
        prodViewedAction.put(Events.PRODUCT_ID, productId);
        prodViewedAction.put(Events.QUANTITY, qty);
        prodViewedAction.put(Events.TOTAL_AMOUNT, amount);
        if(type == AppConstant.ApplicationType.MEALS)
            MyApplication.getInstance().sendCleverTapEvent(Events.MEALS_ADDED_TO_CART, prodViewedAction);
        else
            MyApplication.getInstance().sendCleverTapEvent(Events.FRESH_ADDED_TO_CART, prodViewedAction);
    }

    public void charged(ArrayList<String> productName, ArrayList<String> productId, ArrayList<String> qty, double totalAmount, double discountAmount,
                        String typeName, String chargedId, String promoCode, String startTime, String endTime, int type) {
        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.PRODUCT_NAME, productName);
        prodViewedAction.put(Events.PRODUCT_ID, productId);
        prodViewedAction.put(Events.QUANTITY, qty);
        prodViewedAction.put(Events.TOTAL_AMOUNT, totalAmount);
        prodViewedAction.put(Events.DISCOUNT_AMOUNT, discountAmount);
        prodViewedAction.put(Events.TYPE, typeName);
        prodViewedAction.put(Events.CHARGED_ID, chargedId);
        prodViewedAction.put(Events.PROMOCODE, promoCode);
        prodViewedAction.put(Events.START_TIME, startTime);
        prodViewedAction.put(Events.END_TIME, endTime);

        if(type == AppConstant.ApplicationType.MEALS)
            MyApplication.getInstance().sendCleverTapEvent(Events.MEALS_CHARGED, prodViewedAction);
        else
            MyApplication.getInstance().sendCleverTapEvent(Events.FRESH_CHARGED, prodViewedAction);

    }


}
