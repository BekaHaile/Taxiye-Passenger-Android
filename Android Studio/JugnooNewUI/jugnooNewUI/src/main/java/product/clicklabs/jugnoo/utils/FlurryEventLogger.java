package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import product.clicklabs.jugnoo.MyApplication;

public class FlurryEventLogger {

    public static void event(String eventName){
//        try{ FlurryAgent.logEvent(eventName); } catch(Exception e){ e.printStackTrace(); }
		//try{ MyApplication.getInstance().trackEvent("App Analytics", eventName, eventName);} catch(Exception e){e.printStackTrace();}
    }

	public static void eventGA(String category, String action, String label){
		try{ MyApplication.getInstance().trackEvent(category, action, label);} catch(Exception e){e.printStackTrace();}
	}

	public static void eventGA(String category, String action, String label, long value){
		try{ MyApplication.getInstance().trackEvent(category, action, label, value);} catch(Exception e){e.printStackTrace();}
	}

	public static void event(String eventName, Map<String, String> map){
//		try{ FlurryAgent.logEvent(eventName, map); } catch(Exception e){ e.printStackTrace(); }
		//try{ MyApplication.getInstance().trackEvent("App Analytics", eventName, eventName, map);} catch(Exception e){e.printStackTrace();}
	}

	public static void event(Context context, String eventName){
//		HashMap<String, String> map = new HashMap<>();
//		map.put(Constants.KEY_USER_ID, Prefs.with(context).getString(Constants.SP_USER_ID, ""));
//		try{ FlurryAgent.logEvent(eventName, map); } catch(Exception e){ e.printStackTrace(); }
		//try{ MyApplication.getInstance().trackEvent("App Analytics", eventName, eventName, map);} catch(Exception e){e.printStackTrace();}
	}

	public static void event(Context context, String eventName, Map<String, String> map){
//		map.put(Constants.KEY_USER_ID, Prefs.with(context).getString(Constants.SP_USER_ID, ""));
//		try{ FlurryAgent.logEvent(eventName, map); } catch(Exception e){ e.printStackTrace(); }
		//try{ MyApplication.getInstance().trackEvent("App Analytics", eventName, eventName, map);} catch(Exception e){e.printStackTrace();}
	}

	public static void eventApiResponseTime(String apiName, long startTime){
		long responseTime = System.currentTimeMillis() - startTime;
		HashMap<String, String> map = new HashMap<>();
		map.put("response_time_millis", String.valueOf(responseTime));
		event(apiName, map);
	}

	public static void eventWithSessionOpenAndClose(Context context, String eventName){
		try{
//			FlurryAgent.init(context, Config.getFlurryKey());
//			FlurryAgent.onStartSession(context, Config.getFlurryKey());
//			event(eventName);
//			FlurryAgent.onEndSession(context);
		} catch(Exception e){e.printStackTrace();}
	}

	public static void eventWithSessionOpenAndCloseMap(Context context, String eventName){
		try{
//			FlurryAgent.init(context, Config.getFlurryKey());
//			FlurryAgent.onStartSession(context, Config.getFlurryKey());
//			event(context, eventName);
//			FlurryAgent.onEndSession(context);
		} catch(Exception e){e.printStackTrace();}
	}

	public static void appStarted(String deviceToken){
        try{
//            Map<String, String> articleParams = new HashMap<String, String>();
//            articleParams.put("device_token", deviceToken);
//            FlurryAgent.logEvent("App started", articleParams);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	


	public static void checkServerPressed(String accessToken){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("access_token", accessToken);
//			FlurryAgent.logEvent("Check server link pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		//try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch (Exception e){}
	}


	
	public static void helpScreenOpened(String accessToken){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("access_token", accessToken);
//			FlurryAgent.logEvent("Help screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		//try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	
	



	
	public static void couponInfoOpened(String accessToken, int couponType){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("access_token", accessToken);
//			articleParams.put("coupon_type", ""+couponType);
//			FlurryAgent.logEvent("Coupon info opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		//try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	


	public static void registerViaFBClicked(String fbId){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("fb_id", fbId);
//			FlurryAgent.logEvent("Facebook button pressed from Register screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		//try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	
	
	
	public static void facebookLoginClicked(String fbId){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("fb_id", fbId);
//			FlurryAgent.logEvent("Facebook button pressed from Login screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		//try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	


	
	public static void connectionFailure(String error){
		try{
//			Map<String, String> articleParams = new HashMap<String, String>();
//			articleParams.put("error_description", error);
//			FlurryAgent.logEvent("Connection Failure", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}

	/**
	 *
	 * @param mUserId
	 */
	public static void setGAUserId(String mUserId) {
		try{ MyApplication.getInstance().setGAUserId(mUserId);} catch (Exception e){}
	}

	public static void orderedProduct(List<Product> product, ProductAction productAction){
		try {
			MyApplication.getInstance().transactions(product, productAction);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
