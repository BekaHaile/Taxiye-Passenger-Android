package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;

import java.net.URLDecoder;
import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PaytmPaymentState;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
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


	public static String PAYTM_STATUS_ACTIVE = "ACTIVE",
						PAYTM_STATUS_INACTIVE = "INACTIVE";

	public static boolean linkFoundOnce = false;



    public static final String INVALID_ACCESS_TOKEN = "invalid access token";
	public static final String NO_PROMO_APPLIED = "No Promo Code applied";

	public static final String SHARED_PREF_NAME = "myPref", SETTINGS_SHARED_PREF_NAME = "settingsPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token",
			
			SP_TOTAL_DISTANCE = "total_distance", 
			SP_WAIT_TIME = "wait_time",
			SP_RIDE_TIME = "ride_time", 
			SP_RIDE_START_TIME = "ride_start_time", 
			SP_LAST_LATITUDE = "last_latitude",
			SP_LAST_LONGITUDE = "last_longitude"

			
			;
	
	
	public static final String SP_SERVER_LINK = "sp_server_link";
	
	
	
	
	
	
	public static final String LANGUAGE_SELECTED = "language_selected";
	
	
	public static String D_START_RIDE = "D_START_RIDE", D_IN_RIDE = "D_IN_RIDE";
	public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_DRIVER_ARRIVED = "P_DRIVER_ARRIVED",  P_REQUEST_FINAL = "P_REQUEST_FINAL",
			P_ASSIGNING = "P_ASSIGNING";
	
	
	public static LatLng startRidePreviousLatLng;
	
	
	
	

	
	
	
	
	
	
	
	// dev review http://107.21.79.63:4001
	//Dev staged :  "http://54.81.229.172:7000";
	
	// Dev staged :   http://54.81.229.172:8000
	
	// Dev Trial :   http://54.81.229.172:8001
	
	// live 1st:    http://dev.jugnoo.in:3000
	// live 2nd:    http://dev.jugnoo.in:4000
	// live 3rd:    http://dev.jugnoo.in:4002
	// review 3:    http://dev.jugnoo.in:4003
	// live 4th:    http://dev.jugnoo.in:4004
	// live 6th:    https://dev.jugnoo.in:4006
	// live 8th:    https://dev.jugnoo.in:4008
	// live 10th:    https://dev.jugnoo.in:4010
	// live 12th:    https://dev.jugnoo.in:4012     app versions: 126, 127, 128, 129, 130
	// live 13th:    https://dev.jugnoo.in:4013
	
	//iOS 4012
	//
	// Dev new dispatcher :   https://54.81.229.172:8012
	
	//https://test.jugnoo.in:8012 to http://54.173.65.120:9000


	
	public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
	public static final String CHECK_INTERNET_TITLE = "CONNECTION LOST";
	public static final String CHECK_INTERNET_MSG = "Oops! Your Internet in not working.\nPlease try again.";
	
	
	


	public static double latitude = 0, longitude = 0;
	public static double loginLatitude = 0, loginLongitude = 0;

	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	
	
	
	public static UserData userData;
    public static ArrayList<EmergencyContact> emergencyContactsList = new ArrayList<>();
	
	public static LocationFetcher locationFetcher;

	public static PaytmPaymentState paytmPaymentState;
	

	public static final String DEVICE_TYPE = "0";
	public static String deviceToken = "", country = "", deviceName = "", osVersion = "", uniqueDeviceId = "";
	public static int appVersion;

	public static String getDeviceToken(){
		if(deviceToken.equalsIgnoreCase("")){
			deviceToken = "not_found";
		}
		return deviceToken;
	}
	
	
	
	public static String cEngagementId = "", cDriverId = "", cSessionId = "";
	public static DriverInfo assignedDriverInfo;
	
	
	


	
	public static EndRideData endRideData;
	
	public static int customerRateAppFlag = 0;
	
	
	public static LatLng pickupLatLng, dropLatLng;
	public static int pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();

	public static FacebookUserData facebookUserData;
	public static GoogleSignInAccount googleSignInAccount;
	
	
	
	public static FareStructure fareStructure;
	
	public static CancelOptionsList cancelOptionsList;
    public static ArrayList<FeedbackReason> feedbackReasons = new ArrayList<>();;

	public static ReferralMessages referralMessages;

    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

    public static String deepLinkClassName = "", deepLinkReferralCode = "";
	public static int deepLinkIndex;
	public static int deepLinkPickup = -1;
	public static double deepLinkPickupLatitude, deepLinkPickupLongitude;

	public static String knowlarityMissedCallNumber = "";
	public static int otpViaCallEnabled = 1;

	public static boolean supportFeedbackSubmitted = false, locationSettingsNoPressed = false;

	public static LatLng lastRefreshLatLng;

	public static final long BRANCH_LINK_TIME_DIFF = 7 * 24 * 60 * 60 * 1000;



	public static int TRANSFER_FROM_JEANIE = 0;


	
	public static void clearDataOnLogout(Context context){
		try{
			driverInfos = new ArrayList<DriverInfo>();
            emergencyContactsList = new ArrayList<>();
			userData = null;
            endRideData = null;
            customerRateAppFlag = 0;
			locationFetcher = null;
			deviceToken = ""; country = ""; deviceName = ""; appVersion = 0; osVersion = "";
			cEngagementId = ""; cDriverId = ""; cSessionId = "";
			assignedDriverInfo = null;
			pickupLatLng = null;
			facebookUserData = null;
            fareStructure = null;
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
			Prefs.with(context).remove(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME);
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

			if(data.getQueryParameter("deepindex") != null){
				Data.deepLinkIndex = Integer.parseInt(data.getQueryParameter("deepindex"));
			}
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

				if(dataTarget.getQueryParameter("deepindex") != null){
					Data.deepLinkIndex = Integer.parseInt(dataTarget.getQueryParameter("deepindex"));
				}
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

		Log.e("Deeplink =", "=" + Data.deepLinkIndex);
		Log.e("deepLinkReferralCode =", "=" + Data.deepLinkReferralCode);
	}

	
}
