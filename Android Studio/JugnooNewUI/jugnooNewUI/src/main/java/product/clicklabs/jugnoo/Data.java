package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.net.URLDecoder;
import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.DeliveryData;
import product.clicklabs.jugnoo.datastructure.FreshData;
import product.clicklabs.jugnoo.datastructure.MealsData;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {

	public static final String DRIVER_APP_PACKAGE = "product.clicklabs.jugnoo.driver";


	public static boolean linkFoundOnce = false;



    public static final String INVALID_ACCESS_TOKEN = "invalid access token";
	public static final String NO_PROMO_APPLIED = "No Promo Code applied";

	public static final String SHARED_PREF_NAME = "myPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token",
			
			SP_TOTAL_DISTANCE = "total_distance", 
			SP_WAIT_TIME = "wait_time",
			SP_RIDE_TIME = "ride_time", 
			SP_RIDE_START_TIME = "ride_start_time", 
			SP_LAST_LATITUDE = "last_latitude",
			SP_LAST_LONGITUDE = "last_longitude"

			
			;
	
	
	public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_DRIVER_ARRIVED = "P_DRIVER_ARRIVED",  P_REQUEST_FINAL = "P_REQUEST_FINAL",
			P_ASSIGNING = "P_ASSIGNING";
	
	

	

	
	
	
	
	

	
	public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
	public static final String CHECK_INTERNET_TITLE = "CONNECTION LOST";
	public static final String CHECK_INTERNET_MSG = "Oops! Your Internet is not working.\nPlease try again.";
	
	
	


	public static double latitude = 0, longitude = 0;
	public static double loginLatitude = 0, loginLongitude = 0;


	
	
	
	public static UserData userData;
	public static AutoData autoData;
	private static FreshData freshData;
	private static MealsData mealsData;
	private static DeliveryData DeliveryData;

	
	public static LocationFetcher locationFetcher;


	public static final String DEVICE_TYPE = "0";
	public static String country = "", deviceName = "", osVersion = "", uniqueDeviceId = "";
	public static int appVersion;


	public static FacebookUserData facebookUserData;
	public static GoogleSignInAccount googleSignInAccount;
	public static String webActivityTitle = "";
	




    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

    public static String deepLinkClassName = "", deepLinkReferralCode = "";
	public static int deepLinkIndex;
	public static int deepLinkPickup = -1;
	public static double deepLinkPickupLatitude, deepLinkPickupLongitude;
	public static boolean locationSettingsNoPressed = false, locationAddressSettingsNoPressed = false;




	public static final long BRANCH_LINK_TIME_DIFF = 7 * 24 * 60 * 60 * 1000;



	public static int TRANSFER_FROM_JEANIE = 0;
	public static int AppType = 1;
	public static boolean isOrderCancelled = false;
	public static boolean isSupportRideIssueUpdated = false;
	public static String currentActivity = null;


	public static void clearDataOnLogout(Context context){
		try{
			userData = null;
			autoData = null;
			freshData = null;
			mealsData = null;
			DeliveryData = null;

			locationFetcher = null;
			country = ""; deviceName = ""; appVersion = 0; osVersion = "";
			facebookUserData = null;
            previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
			
			AccessTokenGenerator.saveLogoutToken(context);
			clearSPLabelPrefs(context);

		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private static void clearSPLabelPrefs(Context context) {
		try {
			Prefs.with(context).remove(SPLabels.REFERRAL_OPEN_DATE_MILLIS);
			Prefs.with(context).remove(SPLabels.REFERRAL_TRANSACTION_COUNT);
			Prefs.with(context).remove(SPLabels.REFERRAL_APP_OPEN_COUNT);
			Prefs.with(context).remove(SPLabels.USER_IDENTIFIER);
			Prefs.with(context).remove(SPLabels.BRANCH_LINK_DESCRIPTION);
			Prefs.with(context).remove(SPLabels.BRANCH_LINK_IMAGE);
			Prefs.with(context).remove(SPLabels.BRANCH_SMS_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_WHATSAPP_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_FACEBOOK_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_EMAIL_LINK);
			Prefs.with(context).remove(SPLabels.ADD_HOME);
			Prefs.with(context).remove(SPLabels.ADD_WORK);
			Prefs.with(context).remove(SPLabels.ADD_GYM);
			Prefs.with(context).remove(SPLabels.ADD_FRIEND);
			Prefs.with(context).remove(SPLabels.NOTIFICATION_UNREAD_COUNT);

			Prefs.with(context).remove(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME);
			Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);
			Prefs.with(context).remove(Constants.SP_USER_ID);

			Prefs.with(context).remove(SPLabels.UPLOAD_CONTACT_NO_THANKS);
			Prefs.with(context).remove(SPLabels.APP_MONITORING_TRIGGER_TIME);
			Prefs.with(context).remove(SPLabels.UPLOAD_CONTACTS_ERROR);
			Prefs.with(context).remove(SPLabels.CHECK_BALANCE_LAST_TIME);
			Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE);
			Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA);

			Prefs.with(context).remove(SPLabels.BRANCH_DESKTOP_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_ANDROID_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_IOS_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_FALLBACK_URL);

			Prefs.with(context).remove(SPLabels.ENTERED_DESTINATION);
			Prefs.with(context).remove(SPLabels.LAST_PICK_UP);
			Prefs.with(context).remove(SPLabels.LAST_DESTINATION);

			Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);

			Prefs.with(context).remove(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION);
			Prefs.with(context).remove(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED);
			Prefs.with(context).remove(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED);

			Prefs.with(context).remove(Constants.SP_CURRENT_STATE);
			Prefs.with(context).remove(Constants.SP_CURRENT_ENGAGEMENT_ID);
			Prefs.with(context).remove(Constants.SP_LAST_PUSH_RECEIVED_TIME);
			Prefs.with(context).remove(Constants.SP_REFERRAL_CODE);
			Prefs.with(context).remove(Constants.SP_PUSH_DIALOG_CONTENT);
			Prefs.with(context).remove(Constants.KEY_SP_FUGU_CAMPAIGN_NAME);
			Prefs.with(context).remove(Constants.SP_POOL_INTRO_SHOWN);

			Prefs.with(context).remove(Constants.SP_LAST_ADDED_WALLET);
			Prefs.with(context).remove(Constants.SP_LAST_USED_WALLET);
			Prefs.with(context).remove(Constants.SP_LAST_MONEY_ADDED_WALLET);
            Prefs.with(context).remove(Constants.SP_POKESTOP_ENABLED_BY_USER);

			Prefs.with(context).remove(SPLabels.REFERRAL_OPEN_DATE_MILLIS);
			Prefs.with(context).remove(SPLabels.REFERRAL_TRANSACTION_COUNT);
			Prefs.with(context).remove(SPLabels.REFERRAL_APP_OPEN_COUNT);
			Prefs.with(context).remove(SPLabels.USER_IDENTIFIER);
			Prefs.with(context).remove(SPLabels.BRANCH_LINK_DESCRIPTION);
			Prefs.with(context).remove(SPLabels.BRANCH_LINK_IMAGE);
			Prefs.with(context).remove(SPLabels.BRANCH_SMS_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_WHATSAPP_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_FACEBOOK_LINK);
			Prefs.with(context).remove(SPLabels.BRANCH_EMAIL_LINK);
			Prefs.with(context).remove(SPLabels.ADD_HOME);
			Prefs.with(context).remove(SPLabels.ADD_WORK);
			Prefs.with(context).remove(SPLabels.ADD_GYM);
			Prefs.with(context).remove(SPLabels.ADD_FRIEND);
			Prefs.with(context).remove(SPLabels.NOTIFICATION_UNREAD_COUNT);

			Prefs.with(context).remove(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME);
			Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);
			Prefs.with(context).remove(Constants.SP_USER_ID);

			Prefs.with(context).remove(SPLabels.UPLOAD_CONTACT_NO_THANKS);
			Prefs.with(context).remove(SPLabels.APP_MONITORING_TRIGGER_TIME);
			Prefs.with(context).remove(SPLabels.UPLOAD_CONTACTS_ERROR);
			Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE);
			Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA);

			Prefs.with(context).remove(SPLabels.BRANCH_DESKTOP_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_ANDROID_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_IOS_URL);
			Prefs.with(context).remove(SPLabels.BRANCH_FALLBACK_URL);

			Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);

			Prefs.with(context).remove(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION);
			Prefs.with(context).remove(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED);
			Prefs.with(context).remove(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED);


			Prefs.with(context).remove(Constants.SP_FRESH_CART);
			Prefs.with(context).remove(Constants.SP_MEAL_CART);
			Prefs.with(context).remove(context.getResources().getString(R.string.pref_address_selected));
			Prefs.with(context).remove(context.getResources().getString(R.string.pref_local_address));
			Prefs.with(context).remove(context.getResources().getString(R.string.pref_loc_lati));
			Prefs.with(context).remove(context.getResources().getString(R.string.pref_loc_longi));


//            TODO ask gurmail if(!BuildConfig.DEBUG_MODE)
//			Prefs.with(context).removeAll();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	





	public static final String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";
	public static final String REGISTRATION_FAILED = "REGISTRATION_FAILED";
	public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
	public static final String ERROR = "ERROR";
	public static final String INTENT_CLASS_NAME = "intentClassName";

	public static Uri splashIntentUri;


	public static void getDeepLinkIndexFromIntent(Context context, Intent newIntent) {
		Data.deepLinkIndex = -1;
		Data.deepLinkPickup = -1;
		Data.deepLinkReferralCode = "";
		try {
			Intent intent = newIntent;
			String action = intent.getAction();
			Uri data = intent.getData();
			Log.e("action", "=" + action);
			Log.e("data", "=" + data);

			if(data.getQueryParameter(Constants.KEY_REFERRAL_CODE) != null){
				Data.deepLinkReferralCode = data.getQueryParameter(Constants.KEY_REFERRAL_CODE);
			}

			if(intent.hasExtra(Constants.KEY_PUSH_CLICKED)){
				FlurryEventLogger.event(context, FlurryEventNames.WHO_CLICKED_THE_PUSH);
			}

			if(data.getQueryParameter(Constants.KEY_DEEPINDEX) != null){
				Data.deepLinkIndex = Integer.parseInt(data.getQueryParameter(Constants.KEY_DEEPINDEX));
			}

			if(intent.hasExtra(Constants.KEY_TAB_INDEX)){
				Data.tabLinkIndex = intent.getIntExtra(Constants.KEY_TAB_INDEX, 0);
			}

//			if(intent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)){
//				Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//			}

			else if(data.getQueryParameter("pickup_lat") != null && data.getQueryParameter("pickup_lng") != null){
				Data.deepLinkPickup = 1;
				Data.deepLinkPickupLatitude = Double.parseDouble(data.getQueryParameter("pickup_lat"));
				Data.deepLinkPickupLongitude = Double.parseDouble(data.getQueryParameter("pickup_lng"));

				Log.e("deepLinkPickup =", "=" + Data.deepLinkPickup);
				Log.e("deepLinkPickupLatitude =", "=" + Data.deepLinkPickupLatitude);
				Log.e("deepLinkPickupLongitude =", "=" + Data.deepLinkPickupLongitude);
			}
			else{
				throw new Exception();
			}

		} catch (Exception e) {
//			e.printStackTrace();
			//jungooautos://open?link_click_id=link-178470536899245547&target_url=http%3A%2F%2Fshare.jugnoo.in%2Fm%2F7MPH22Lyln%3Fdeepindex%3D0
			try {
				Intent intent = newIntent;
				Uri data = intent.getData();
				Log.e("data", "=" + data);

				String targetUrl = URLDecoder.decode(data.getQueryParameter("target_url"), "UTF-8");
				Uri dataTarget = Uri.parse(targetUrl);

				if(dataTarget.getQueryParameter(Constants.KEY_REFERRAL_CODE) != null){
					Data.deepLinkReferralCode = dataTarget.getQueryParameter(Constants.KEY_REFERRAL_CODE);
				}

				if(dataTarget.getQueryParameter(Constants.KEY_DEEPINDEX) != null){
					Data.deepLinkIndex = Integer.parseInt(dataTarget.getQueryParameter(Constants.KEY_DEEPINDEX));
				}

				if(intent.hasExtra(Constants.KEY_TAB_INDEX)){
					Data.tabLinkIndex = intent.getIntExtra(Constants.KEY_TAB_INDEX, 0);
				}

//				if(intent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)){
//					Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//					Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//				}

				else if(dataTarget.getQueryParameter("pickup_lat") != null && dataTarget.getQueryParameter("pickup_lng") != null){
					Data.deepLinkPickup = 1;
					Data.deepLinkPickupLatitude = Double.parseDouble(dataTarget.getQueryParameter("pickup_lat"));
					Data.deepLinkPickupLongitude = Double.parseDouble(dataTarget.getQueryParameter("pickup_lng"));

					Log.e("deepLinkPickup =", "=" + Data.deepLinkPickup);
					Log.e("deepLinkPickupLatitude =", "=" + Data.deepLinkPickupLatitude);
					Log.e("deepLinkPickupLongitude =", "=" + Data.deepLinkPickupLongitude);
				}
			} catch (Exception e1) {
//				e1.printStackTrace();
			}

		}

		try {
			if(AppLinkIndex.FRESH_PAGE.getOrdinal() == Data.deepLinkIndex){
				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
			}
			else if(AppLinkIndex.MEAL_PAGE.getOrdinal() == Data.deepLinkIndex){
				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMealsClientId());
			}
			else if(AppLinkIndex.DELIVERY_PAGE.getOrdinal() == Data.deepLinkIndex){
				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getDeliveryClientId());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Log.e("Deeplink =", "=" + Data.deepLinkIndex);
		Log.e("deepLinkReferralCode =", "=" + Data.deepLinkReferralCode);

		try {
			if(newIntent.getExtras() != null && newIntent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)) {
				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, newIntent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public static final String LOCAL_BROADCAST  = "fatafat-cart-broadcast";
	public static final String LATLNG = "latlng";
	public static String PAYTM_STATUS_ACTIVE = "ACTIVE",
			PAYTM_STATUS_INACTIVE = "INACTIVE";


	//public static int isfatafat = 0;


	public static int tabLinkIndex = 0;


	public static FreshData getFreshData(){
		return freshData;
	}

	public static void setFreshData(FreshData freshData){
		Data.freshData = freshData;
	}

	public static MealsData getMealsData() {
		return mealsData;
	}

	public static void setMealsData(MealsData mealsData) {
		Data.mealsData = mealsData;
	}


	public static DeliveryData getDeliveryData() {
		return DeliveryData;
	}

	public static void setDeliveryData(DeliveryData deliveryData) {
		DeliveryData = deliveryData;
	}



}
