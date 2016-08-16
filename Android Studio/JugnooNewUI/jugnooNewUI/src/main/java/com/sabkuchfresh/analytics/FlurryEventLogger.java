package com.sabkuchfresh.analytics;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import product.clicklabs.jugnoo.MyApplication;

public class FlurryEventLogger {


	public static void checkServerPressed(String accessToken){
//		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("access_token", accessToken);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");
//		} catch (Exception e){}
	}

    public static void event(String screenName) {
        try{ MyApplication.getInstance().trackScreenView(screenName);
        }catch (Exception e){}
    }

    public static void event(String category, String action, String label) {
        try{ MyApplication.getInstance().trackEvent(category, action, label);
        } catch (Exception e){}
    }
	
	
	public static void facebookLoginClicked(String fbId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("fb_id", fbId);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	

	public static void userLogin(String userID, String eventName, String eventAction) {
        HashMap<String, String> map = new HashMap<>();
        try{ MyApplication.getInstance().trackEvent(userID, "App Analytics", eventName, eventAction, map);} catch(Exception e){e.printStackTrace();}
    }

    public static void checkoutTrackEvent(int position, List<Product> product) {
        try {
            MyApplication.getInstance().eventTracker(position, product);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void orderedProduct(List<Product> product, ProductAction productAction){
        try {
            MyApplication.getInstance().transactions(product, productAction);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
