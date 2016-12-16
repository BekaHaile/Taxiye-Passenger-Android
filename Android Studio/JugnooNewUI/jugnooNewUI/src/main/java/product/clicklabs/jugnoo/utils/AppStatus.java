package product.clicklabs.jugnoo.utils;

/**
 * This file is used by all the other classes to check Internet connection 
 * 
 * Project Name: - Word derby
 * Developed by ClickLabs. Developer: Raman goyal 
 * Link: http://www.click-labs.com/
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class AppStatus {

	private static AppStatus instance = new AppStatus();
	static Context context;
	ConnectivityManager connectManager;
	NetworkInfo wifiInfo, mobileInfo;
	boolean connected = false;

	public static AppStatus getInstance(Context ctx) {
		context = ctx;
		return instance;
	}

	public boolean isOnline(Context con) {
		try {
			connectManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();
			if (activeNetwork != null) { // connected to the internet
				if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
					// connected to wifi
					return true;
				} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
					// connected to the mobile provider's data plan
					return true;
				}
			} else {
				// not connected to the internet
				return false;
			}
			FlurryEventLogger.event(FlurryEventNames.ERROR_NO_INTERNET);
			return false;
		} catch (Exception e) {
			System.out.println("CheckConnectivity Exception: " + e.getMessage());
		}
		FlurryEventLogger.event(FlurryEventNames.ERROR_NO_INTERNET);
		return connected;
	}

}
