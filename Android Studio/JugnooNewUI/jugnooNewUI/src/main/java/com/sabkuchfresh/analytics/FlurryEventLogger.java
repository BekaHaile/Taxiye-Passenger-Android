package com.sabkuchfresh.analytics;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.List;

import product.clicklabs.jugnoo.MyApplication;

public class FlurryEventLogger {

	public static void eventGA(String category, String action, String label){
		try{ MyApplication.getInstance().trackEvent(category, action, label);} catch(Exception e){e.printStackTrace();}
	}

	public static void eventGA(String category, String action, String label, long value){
		try{ MyApplication.getInstance().trackEvent(category, action, label, value);} catch(Exception e){e.printStackTrace();}
	}
	
    public static void checkoutTrackEvent(int position, List<Product> product) {
        try {
            MyApplication.getInstance().eventTracker(position, product);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void trancastionCompleteEvent(List<Product> product, ProductAction productAction){
        try {
            MyApplication.getInstance().transactionEvent(product, productAction);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
