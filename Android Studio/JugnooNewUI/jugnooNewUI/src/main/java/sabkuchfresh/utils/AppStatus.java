package sabkuchfresh.utils;

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
			if (connectManager != null) {
				NetworkInfo[] info = connectManager.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			System.out.println("CheckConnectivity Exception: " + e.getMessage());
		}
		return connected;
	}

}
