package product.clicklabs.jugnoo.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.config.Config;

/**
 * Created by gurmail on 02/09/16.
 */
public class CleverTapUtils {

    public void rideRequested(int vehicle_type, int type, double surge, String offerCode, String eta, String city) {

        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.VEHICLE_TYPE, vehicle_type);
        prodViewedAction.put(Events.ETA, eta);
        prodViewedAction.put(Events.SURGE, surge);
        prodViewedAction.put(Events.TYPE, type);
        prodViewedAction.put(Events.OFFER_CODE, offerCode);
        prodViewedAction.put(Events.CITY, city);

        MyApplication.getInstance().sendCleverTapEvent(Events.RIDE_REQUESTED, prodViewedAction);
    }

    public void signUp(String loginVia, String walletSelected, String referralCodeEntered,
                       String jugnooCash, String city) {
        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.CHANNEL, ""+loginVia);
        prodViewedAction.put(Events.WALLET, ""+walletSelected);
        prodViewedAction.put(Events.PROMO_CODE, referralCodeEntered);

        prodViewedAction.put(Events.JUGNOO_CASH, Double.parseDouble(jugnooCash));
        ArrayList<String> coupons = getCouponsArray();
        String maxCouponValue = "0", couponsStr = "";
        if(coupons.size()>0){
            maxCouponValue = coupons.remove(coupons.size()-1);
            couponsStr = coupons.toString().replace("[", "").replace("]", "");
        }
        prodViewedAction.put(Events.MAX_COUPON_VALUE, Double.parseDouble(maxCouponValue));
        prodViewedAction.put(Events.COUPONS, couponsStr);
        prodViewedAction.put(Events.CITY, city);

        MyApplication.getInstance().sendCleverTapEvent(Events.SIGNED_UP, prodViewedAction);
    }

    public void addToCart(String productName, int productId, int qty, double amount, String clientId, String city) {
        HashMap<String, Object> prodViewedAction = new HashMap<>();
        prodViewedAction.put(Events.PRODUCT_NAME, productName);
        prodViewedAction.put(Events.PRODUCT_ID, productId);
        prodViewedAction.put(Events.QUANTITY, qty);
        prodViewedAction.put(Events.TOTAL_AMOUNT, amount);
        prodViewedAction.put(Events.CITY, city);
        if(clientId.equalsIgnoreCase(Config.getMealsClientId())) {
            MyApplication.getInstance().sendCleverTapEvent(Events.MEALS_ADDED_TO_CART, prodViewedAction);
        } else if(clientId.equalsIgnoreCase(Config.getGroceryClientId())) {
            MyApplication.getInstance().sendCleverTapEvent(Events.GROCERY_ADDED_TO_CART, prodViewedAction);
        } else if(clientId.equalsIgnoreCase(Config.getMenusClientId())) {
            MyApplication.getInstance().sendCleverTapEvent(Events.MENUS_ADDED_TO_CART, prodViewedAction);
        } else {
            MyApplication.getInstance().sendCleverTapEvent(Events.FRESH_ADDED_TO_CART, prodViewedAction);
        }
    }

    public ArrayList<String> getCouponsArray(){
        ArrayList<String> coupons = new ArrayList<>();
        try {
            double maxValue = 0.0;
            if(Data.userData != null && Data.userData.getPromoCoupons() != null) {
                for(int i=0;i<Data.userData.getPromoCoupons().size();i++) {
                    coupons.add(Data.userData.getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.userData.getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.autoData != null && Data.autoData.getPromoCoupons() != null) {
                for(int i=0;i<Data.autoData.getPromoCoupons().size();i++) {
                    coupons.add(Data.autoData.getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.autoData.getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.getFreshData() != null && Data.getFreshData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getFreshData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getFreshData().getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.getFreshData().getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.getMealsData() != null && Data.getMealsData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getMealsData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getMealsData().getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.getMealsData().getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.getGroceryData() != null && Data.getGroceryData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getGroceryData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getGroceryData().getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.getGroceryData().getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.getMenusData() != null && Data.getMenusData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getMenusData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getMenusData().getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.getMenusData().getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            if(Data.getPayData() != null && Data.getPayData().getPromoCoupons() != null) {
                for(int i=0;i<Data.getPayData().getPromoCoupons().size();i++) {
                    coupons.add(Data.getPayData().getPromoCoupons().get(i).getTitle());
                    String value = getCouponValue(Data.getPayData().getPromoCoupons().get(i).getTitle());
                    if(value.length()>0) {
                        coupons.add(value);
                        maxValue = getCouponMaxValue(maxValue, value);
                    }
                }
            }
            Log.d("TAG", "Coupon counts = "+coupons.size());

            DecimalFormat df = new DecimalFormat("#.##");
            coupons.add(df.format(maxValue));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return coupons;
    }

    public void setCoupons() {
        try {
            ArrayList<String> coupons = getCouponsArray();
            String maxValue = "0";
            if(coupons.size()>0){
                maxValue = coupons.remove(coupons.size()-1);
            }
            MyApplication.getInstance().udpateUserData(Events.COUPONS, coupons);

            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put(Events.MAX_COUPON_VALUE, maxValue);
            MyApplication.getInstance().getCleverTap().profile.push(profileUpdate);

        } catch(Exception e) {
        }
    }

    public String getCouponValue(String coupon) {
        String[] promo = coupon.split(" ");
        for(int j=0;j<promo.length;j++) {
            String s = promo[j];
            s = s.replaceAll("\\D+","");
            if(s.length()>0) {
                return s;
            }
        }
        return "";
    }

    public double getCouponMaxValue(double maxValue, String value){
        double dValue = 0.0;
        try{dValue = Double.parseDouble(value);} catch (Exception e){}
        if(dValue > maxValue){
            maxValue = dValue;
        }
        return maxValue;
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
