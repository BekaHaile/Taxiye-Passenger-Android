package product.clicklabs.jugnoo.utils;

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

	public static void setGAUserId(String mUserId) {
		try{ MyApplication.getInstance().setGAUserId(mUserId);} catch (Exception e){}
	}

	public static void transactionCompleteEvent(List<Product> product, ProductAction productAction){
		try {
			MyApplication.getInstance().transactionEvent(product, productAction);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
