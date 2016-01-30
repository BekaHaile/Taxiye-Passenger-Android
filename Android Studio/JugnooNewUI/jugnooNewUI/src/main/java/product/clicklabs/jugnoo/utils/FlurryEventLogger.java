package product.clicklabs.jugnoo.utils;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.MyApplication;

public class FlurryEventLogger {

    public static void event(String eventName){
        try{ FlurryAgent.logEvent(eventName); } catch(Exception e){ e.printStackTrace(); }
		try{ MyApplication.getInstance().trackEvent("App Analytics", eventName, eventName);} catch(Exception e){e.printStackTrace();}
    }


	public static void appStarted(String deviceToken){
        try{
            Map<String, String> articleParams = new HashMap<String, String>();
            articleParams.put("device_token", deviceToken);
            FlurryAgent.logEvent("App started", articleParams);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	


	public static void checkServerPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Check server link pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");
		} catch (Exception e){}
	}


	
	public static void helpScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Help screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	
	



	
	public static void couponInfoOpened(String accessToken, int couponType){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("coupon_type", ""+couponType);
			FlurryAgent.logEvent("Coupon info opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	


	public static void registerViaFBClicked(String fbId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("fb_id", fbId);
			FlurryAgent.logEvent("Facebook button pressed from Register screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	
	
	
	public static void facebookLoginClicked(String fbId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("fb_id", fbId);
			FlurryAgent.logEvent("Facebook button pressed from Login screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	


	
	public static void connectionFailure(String error){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("error_description", error);
			FlurryAgent.logEvent("Connection Failure", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
		try{ MyApplication.getInstance().trackEvent("App Analytics", "Check server link pressed", "Check server link pressed");} catch(Exception e){}
	}
	


}
