package product.clicklabs.jugnoo;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

public class FlurryEventLogger {

	public static void requestPushReceived(String engagementId, String startTime, String receivedTime){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("engagement_id", engagementId);
			articleParams.put("start_time", startTime);
			articleParams.put("received_time", receivedTime);
			FlurryAgent.logEvent("Request push received", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void requestRidePressed(String accessToken, LatLng latLng){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("latitude", ""+latLng.latitude);
			articleParams.put("longitude", "" + latLng.longitude);
			FlurryAgent.logEvent("Call an auto pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void cancelRequestPressed(String accessToken, String sessionId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("session_id", sessionId);
			FlurryAgent.logEvent("Cancel request pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void callDriverPressed(String accessToken, String userId, String phoneNo){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("user_id", userId);
			articleParams.put("phone_no", phoneNo);
			FlurryAgent.logEvent("Call driver pressed", articleParams);
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
	}
	
	
	public static void debugPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Debug pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void switchedToDriverMode(String accessToken, int flag){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("driver_mode", ""+flag);
			FlurryAgent.logEvent("Switch to driver mode pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void shareScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Share screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void couponsScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Coupon screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void helpScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Help screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void rideScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Ride screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void logoutPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Logout pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void logoutPressedBetweenRide(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Logout pressed between ride", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void reviewSubmitted(String accessToken, String engagementId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("engagement_id", engagementId);
			FlurryAgent.logEvent("Review submitted", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void promoCodeTried(String accessToken, String promoCode){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("promo_code", promoCode);
			FlurryAgent.logEvent("Promo code tried", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaFacebook(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Facebook", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaWhatsapp(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Whatsapp", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaEmail(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Email", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaSMS(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via SMS", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void mailToSupportPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Mail to support pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void callToSupportPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Call to support pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void particularHelpOpened(String helpName, String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent(helpName + " opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
