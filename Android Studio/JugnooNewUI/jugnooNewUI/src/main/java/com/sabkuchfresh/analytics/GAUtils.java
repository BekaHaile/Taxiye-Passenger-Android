package com.sabkuchfresh.analytics;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.List;

import product.clicklabs.jugnoo.MyApplication;

/**
 * Created by shankar on 3/2/17.
 */

public class GAUtils {

	public static void event(String category, String action, String label){
		try{ MyApplication.getInstance().trackEvent(category, action, label);} catch(Exception e){}
	}

	public static void event(String category, String action, String label, long value){
		try{ MyApplication.getInstance().trackEvent(category, action, label, value);} catch(Exception e){}
	}

	public static void trackScreenView(String screenName) {
		try{ MyApplication.getInstance().trackScreenView(screenName);}catch (Exception e){}
	}

	public static void setUserId(String mUserId) {
		try{ MyApplication.getInstance().setGAUserId(mUserId);} catch (Exception e){}
	}

	public static void checkoutTrackEvent(int position, List<Product> product) {
		try {MyApplication.getInstance().eventTracker(position, product);} catch(Exception e) {}
	}

	public static void transactionCompleteEvent(List<Product> product, ProductAction productAction){
		try {MyApplication.getInstance().transactionEvent(product, productAction);} catch(Exception e) {}
	}

	public static void setGAUserId(String mUserId) {
		try{ MyApplication.getInstance().setGAUserId(mUserId);} catch (Exception e){}
	}

}
