package product.clicklabs.jugnoo;

import java.util.ArrayList;

import android.content.Context;

import com.androidquery.callback.ImageOptions;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.model.LatLng;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {
	
	public static final int SERVER_TIMEOUT = 60000;

	public static final String SERVER_URL = "http://54.81.229.172:7000";
	
	public static final String SERVER_ERROR_MSG = "Server error. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Oops!! Server not responding. Please try again later.";
	public static final String CHECK_INTERNET_MSG = "Check your internet connection.";
	
	
	
	public static final String GOOGLE_PROJECT_ID = "506849624961";

	public static final String MAPS_BROWSER_KEY = "AIzaSyAHVDCyeC13xO_GxG5zE8_wbRJolqkBg90";
	
	
	
	public static double latitude = 30.7500, longitude = 76.7800;
	
	public static LatLng chandigarhLatLng = new LatLng(30.7500, 76.7800);
	
	
	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	public static ArrayList<FavoriteLocation> favoriteLocations = new ArrayList<FavoriteLocation>();
	
	public static ArrayList<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
	public static ArrayList<FriendInfo> friendInfosDuplicate = new ArrayList<FriendInfo>();
	
	
	
	
	public static UserData userData;
	
	public static LocationFetcher locationFetcher;
	

	public static String deviceToken = "", country = "", deviceName = "", appVersion = "", osVersion = "";
	
	
	
	public static String engagementId = "", driverId = "";
	
	public static DriverInfo assignedDriverInfo;
	
	
	
	public static LatLng mapTarget;
	
	
	public static LatLng getChandigarhLatLng(){
		if(chandigarhLatLng == null){
			chandigarhLatLng = new LatLng(30.7500, 76.7800);
		}
		return chandigarhLatLng;
	}
	
	
	
	
	public static ImageOptions imageOptionsRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 10;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptionsFullRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 500;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptions(){
		ImageOptions options = new ImageOptions();
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	
	/**
	 * Function to register device with Google Cloud Messaging Services and receive Device Token
	 * @param context application context
	 */
	public static void registerForGCM(Context context){
		try { // registering GCM services
			GCMRegistrar.checkManifest(context);
			Data.deviceToken = GCMRegistrar.getRegistrationId(context);
			if (Data.deviceToken.equals("")) {
				GCMRegistrar.register(context, Data.GOOGLE_PROJECT_ID);
				Data.deviceToken = GCMRegistrar.getRegistrationId(context);
				Log.i("deviceToken in if", ">" + Data.deviceToken);
			} else {
				Log.i("GCM", "Already registered");
				Log.i("deviceToken....in else", ">" + Data.deviceToken);
				Log.i("deviceToken....length", ">"+Data.deviceToken.length());
			}
		} catch (Exception e) {
			Log.e("exception GCM", ""+e.toString());
		}
	}
	
	
	
}
