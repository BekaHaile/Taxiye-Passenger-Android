package com.sabkuchfresh.TokenGenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import com.sabkuchfresh.config.Config;
import com.sabkuchfresh.utils.Utils;

public class AccessTokenGenerator {

	
	public static final String LOGOUT = "logout";

    public static final String AUTOS_PACKAGE = "product.clicklabs.jugnoo", AUTOS_CLIENT_ID = "EEBUOvQq7RRJBxJm";
	public static final String MEALS_PACKAGE = "com.cdk23.nlk", MEALS_CLIENT_ID = "QNrWRzMToQNnxrQ5";
	public static final String FATAFAT_PACKAGE = "com.cdk23.nlkf", FATAFAT_CLIENT_ID = "g3Ql58Kx2VCDYVk3";
    public static final String SABKUSH_PACKAGE = "com.sabkuchfresh", SABKUSH_CLIENT_ID = "FHkmrtv6zn0KuGcW";

    private static final String AUTH_SP = "shared_auth", AUTH_KEY = "authKey", FRESH_INSTALL = "freshInstall";




	private static final String[] OTHER_JUGNOO_APP_PACKAGES = new String[]{ MEALS_PACKAGE, FATAFAT_PACKAGE, AUTOS_PACKAGE };
	

	public static void saveAuthKey(Context context, String authKey) {
		AuthKeySaver.writeAuthToFile(authKey);
		SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(AUTH_KEY, authKey);
		editor.apply();
	}
	
	public static void saveLogoutToken(Context context) {
		AuthKeySaver.writeAuthToFile(LOGOUT);
		SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(AUTH_KEY, "");
		editor.apply();
	}
	
	public static void updateFreshInstall(Context context) {
		SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(FRESH_INSTALL, "no");
		editor.apply();
	}
	
	
	public static Pair<String, Integer> getAccessTokenPair(Context context) {

		Pair<String, Integer> pair = getAccessTokenPairPre(context);

		updateFreshInstall(context);

		return pair;
	}


	@SuppressWarnings("deprecation")
	public static Pair<String, Integer> getAccessTokenPairPre(Context context) {

		Pair<String, Integer> pair = new Pair<String, Integer>("", 1);

		String authKey = "";
		authKey = AuthKeySaver.readAuthFromFile();

		if(AuthKeySaver.NOT_FOUND.equalsIgnoreCase(authKey)){
			SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
			authKey = pref.getString(AUTH_KEY, "");
			if("".equalsIgnoreCase(authKey)){																		// SP returns empty
				return pair;
			}
			else{																									// SP return
				saveAuthKey(context, authKey);
			}
		}
		else if(LOGOUT.equalsIgnoreCase(authKey)){
			authKey = "";
            SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
            Editor editor = pref.edit();
            editor.putString(AUTH_KEY, "");
            editor.apply();
//			saveAuthKey(context, authKey);
		}
//		else if("".equalsIgnoreCase(authKey)) {                                                                            // file returns empty
//            SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
//            authKey = pref.getString(AUTH_KEY, "");
//            if (!"".equalsIgnoreCase(authKey)) {
//                saveAuthKey(context, authKey);
//            }
//        }
        else{
			SharedPreferences pref = context.getSharedPreferences(AUTH_SP, Context.MODE_PRIVATE);
			String freshInstall = pref.getString(FRESH_INSTALL, "");

			if("".equalsIgnoreCase(freshInstall)){
				boolean otherAppsInstalled = false;
				for(String appPackage : OTHER_JUGNOO_APP_PACKAGES){
					if(Utils.appInstalledOrNot(context, appPackage)){
						otherAppsInstalled = true;
						break;
					}
				}

				if(!otherAppsInstalled){
					authKey = "";
					saveAuthKey(context, authKey);
				}
			}

		}

		if("".equalsIgnoreCase(authKey)){																			// no auth key
			pair = new Pair<String, Integer>("", 1);
			return pair;
		}
		try {
			String authSecret = authKey + Config.getClientSharedSecret();
			String accessToken = SHA256Convertor.getSHA256String(authSecret);

			pair = new Pair<String, Integer>(accessToken, 1);
			return pair;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pair;
	}
	
	
}
