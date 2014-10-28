package product.clicklabs.jugnoo;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {
	
	public static String FLURRY_KEY = "H8Y94ND8GPQTKKG5R2VY";
	
	public static final String INVALID_ACCESS_TOKEN = "invalid access token";
	
	public static final String DEBUG_PASSWORD = "8080";
	
	public static final String SHARED_PREF_NAME = "myPref", SETTINGS_SHARED_PREF_NAME = "settingsPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token", SP_ID_KEY = "session_id",
			
			SP_TOTAL_DISTANCE = "total_distance", 
			SP_WAIT_TIME = "wait_time",
			SP_RIDE_TIME = "ride_time", 
			SP_LAST_LATITUDE = "last_latitude",
			SP_LAST_LONGITUDE = "last_longitude",
			
			SP_DRIVER_SCREEN_MODE = "driver_screen_mode", 
			
			SP_D_ENGAGEMENT_ID = "d_engagement_id", 
			SP_D_LATITUDE = "d_latitude",
			SP_D_LONGITUDE = "d_longitude",
			SP_D_CUSTOMER_ID = "d_customer_id",
			SP_D_CUSTOMER_NAME = "d_customer_name", 
			SP_D_CUSTOMER_IMAGE = "d_customer_image", 
			SP_D_CUSTOMER_PHONE = "d_customer_phone", 
			SP_D_CUSTOMER_RATING = "d_customer_rating", 
			
			
			
			
			SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
			
			SP_C_SESSION_ID = "c_session_id",
			SP_C_ENGAGEMENT_ID = "c_engagement_id",
			SP_C_DRIVER_ID = "c_driver_id",
			SP_C_LATITUDE = "c_latitude",
			SP_C_LONGITUDE = "c_longitude",
			SP_C_DRIVER_NAME = "c_driver_name",
			SP_C_DRIVER_IMAGE = "c_driver_image",
			SP_C_DRIVER_CAR_IMAGE = "c_driver_car_image",
			SP_C_DRIVER_PHONE = "c_driver_phone",
			SP_C_DRIVER_RATING = "c_driver_rating",
			SP_C_DRIVER_DISTANCE = "c_driver_distance",
			SP_C_DRIVER_DURATION = "c_driver_duration",
			
			SP_C_TOTAL_DISTANCE = "c_total_distance",
			SP_C_TOTAL_FARE = "c_total_fare",
			SP_C_WAIT_TIME = "c_wait_time"
			
			;
	
	
	public static final String SP_SERVER_LINK = "sp_server_link";
	
	
	
	
	
	
	public static final String LANGUAGE_SELECTED = "language_selected";
	
	
	public static String D_START_RIDE = "D_START_RIDE", D_IN_RIDE = "D_IN_RIDE";
	public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_REQUEST_FINAL = "P_REQUEST_FINAL", 
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
	// live 13th:    https://dev.jugnoo.in:4013
	
	//iOS 4012
	//
	// Dev new dispatcher :   https://54.81.229.172:8012
	
	
	
	public static final String DEV_SERVER_URL = "https://54.81.229.172:8012";
	public static final String LIVE_SERVER_URL = "https://dev.jugnoo.in:4013";
	public static final String TRIAL_SERVER_URL = "http://54.81.229.172:8001";
	
	public static final String DEFAULT_SERVER_URL = DEV_SERVER_URL;
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String SERVER_URL = DEFAULT_SERVER_URL;
	
	
	
	public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
	public static final String CHECK_INTERNET_MSG = "Check your internet connection.";
	
	
	
	public static final String GOOGLE_PROJECT_ID = "506849624961";

	public static final String MAPS_BROWSER_KEY = "AIzaSyAPIQoWfHI2iRZkSV8jU4jT_b9Qth4vMdY";
	
	public static final String FACEBOOK_APP_ID = "782131025144439";
	
	
	
	
	public static double latitude, longitude;
	
	public static int termsAgreed = 0;  // 0: not agreed,  1: agreed
	
	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	public static ArrayList<FavoriteLocation> favoriteLocations = new ArrayList<FavoriteLocation>();
	
	public static ArrayList<RideInfo> rides = new ArrayList<RideInfo>();
	
	public static ArrayList<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
	public static ArrayList<FriendInfo> friendInfosDuplicate = new ArrayList<FriendInfo>();
	
	
	
	
	public static UserData userData;
	
	public static LocationFetcher locationFetcher;
	

	public static String deviceToken = "", country = "", deviceName = "", osVersion = "";
	public static int appVersion;
	
	
	
	public static String cEngagementId = "", cDriverId = "", cSessionId = "";
	public static DriverInfo assignedDriverInfo;
	
	
	
	public static String dEngagementId = "", dCustomerId = "";
	public static LatLng dCustLatLng;
	
	public static ArrayList<DriverRideRequest> driverRideRequests = new ArrayList<DriverRideRequest>();
	
	public static CustomerInfo assignedCustomerInfo;
	
	public static boolean driversRefreshedFirstTime = false;
	
	
	public static double totalDistance = 0, totalFare = 0;
	public static String waitTime = "";
	
	
	public static LatLng pickupLatLng;

	public static String fbAccessToken = "", fbId = "", fbFirstName = "", fbLastName = "", fbUserName = "", fbUserEmail = "";
	
	
	public static ArrayList<DriverType> driverTypes = new ArrayList<DriverType>();
	
	
	
	
	
	public static void clearDataOnLogout(Context context){
		try{
			driverInfos = new ArrayList<DriverInfo>();
			favoriteLocations = new ArrayList<FavoriteLocation>();
			friendInfos = new ArrayList<FriendInfo>();
			friendInfosDuplicate = new ArrayList<FriendInfo>();
			userData = null;
			locationFetcher = null;
			deviceToken = ""; country = ""; deviceName = ""; appVersion = 0; osVersion = "";
			cEngagementId = ""; cDriverId = "";
			assignedDriverInfo = null;
			pickupLatLng = null;
			fbAccessToken = ""; fbId = ""; fbFirstName = ""; fbLastName = ""; fbUserName = ""; fbUserEmail = "";
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			Editor editor = pref.edit();
			editor.putString(Data.SP_ACCESS_TOKEN_KEY, "");
			editor.clear();
			editor.commit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	public static void generateKeyHash(Context context){
		try { // single sign-on for fb application
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"product.clicklabs.jugnoo",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", ","
								+ Base64.encodeToString(md.digest(),
										Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			Log.e("error:", "," + e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("error:", "," + e.toString());
		}
	}
	
	
	
	public static Typeface regular;																// fonts declaration
	

	public static Typeface regularFont(Context appContext) {											// accessing fonts functions
		if (regular == null) {
			regular = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_regular.ttf");
		}
		return regular;
	}
	
	
	
	public static AsyncHttpClient mainClient;
	
	public static final int SOCKET_TIMEOUT = 10000;
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int MAX_RETRIES = 0;
	public static final int RETRY_TIMEOUT = 1000;
	
	public static AsyncHttpClient getClient() {
		if (mainClient == null) {
			mainClient = new AsyncHttpClient();
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				mainClient.setSSLSocketFactory(sf);
			} catch (Exception e) {
				Log.e("exception in https hostname", "="+e.toString());
			}
			mainClient.setConnectTimeout(CONNECTION_TIMEOUT);
			mainClient.setResponseTimeout(SOCKET_TIMEOUT);
			mainClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_TIMEOUT);
		}
		return mainClient;
	}
	
	
}
