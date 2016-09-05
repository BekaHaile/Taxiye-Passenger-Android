package product.clicklabs.jugnoo.utils;

import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.MyApplication;

/**
 * Created by gurmail on 02/09/16.
 */
public class CleverTapUtils {

    public void rideRequested(int vehicle_type, int type, double surge, String offerCode, String eta) {

        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.VEHICLE_TYPE, vehicle_type);
        prodViewedAction.put(Events.ETA, eta);
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

    public void setCoupons() {
        try {
            ArrayList<String> coupons = new ArrayList<>();
            if(Data.userData != null && Data.userData.getPromoCoupons() != null) {
                for(int i=0;i<Data.userData.getPromoCoupons().size();i++) {
                    coupons.add(Data.userData.getPromoCoupons().get(i).getTitle());
                }
            }
            if(Data.getFreshData() != null && Data.getFreshData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getFreshData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getFreshData().getPromoCoupons().get(i).getTitle());
                }
            }
            if(Data.getMealsData() != null && Data.getMealsData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getMealsData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getMealsData().getPromoCoupons().get(i).getTitle());
                }
            }

            MyApplication.getInstance().udpateUserData(Events.COUPONS, coupons);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setWalletData() {
        try {
            ArrayList<String> wallet = new ArrayList<>();
            if(Data.userData != null) {
                if(Data.userData.getPaytmEnabled() == 1) {
                    wallet.add("Paytm");
                }
                if(Data.userData.getMobikwikEnabled() == 1) {
                    wallet.add("Mobikwik");
                }
            }
            MyApplication.getInstance().udpateUserData(Events.WALLET, wallet);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
