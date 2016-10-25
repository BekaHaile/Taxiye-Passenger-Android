package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.IncomingSmsReceiver;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppPackage;


public class Utils {
	
	/**
	 * Compares two double values with epsilon precision
	 * @param d1 double value 1
	 * @param d2 double value 2
	 * @return 1 if d1 > d2, 
	 * -1 if d1 < d2 & 
	 * 0 if d1 == d2
	 */
	public static int compareDouble(double d1, double d2){
		if(d1 == d2){
			return 0;
		}
		else{
			double epsilon = 0.0000001;
			if((d1 - d2) > epsilon){
				return 1;
			}
			else if((d1 - d2) < epsilon){
				return -1;
			}
			else{
				return 0;
			}
		}
	}

	/**
	 * Compares two float values with epsilon precision
	 * @param f1 float value 1
	 * @param f2 float value 2
	 * @return 1 if d1 > d2,
	 * -1 if d1 < d2 &
	 * 0 if d1 == d2
	 */
	public static int compareFloat(float f1, float f2){
		if(f1 == f2){
			return 0;
		}
		else{
			float epsilon = 0.0000001f;
			if((f1 - f2) > epsilon){
				return 1;
			}
			else if((f1 - f2) < epsilon){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
	
	/**
	 * Expands ListView for fixed height of item inside a ScrollView
	 */
	public static void expandListForFixedHeight(ListView list) {
		try {
			if (list.getCount() > 0) {
				ListAdapter listAdap = list.getAdapter();
				int totalHeight = 0;
				
				View listItem = listAdap.getView(0, null, list);
				listItem.measure(0, 0);
				int singleHeight = listItem.getMeasuredHeight();
				totalHeight = singleHeight * list.getCount();
				
//				for (int i = 0; i < listAdap.getCount(); i++) {
//					View listItem = listAdap.getView(i, null, list);
//					listItem.measure(0, 0);
//					totalHeight += listItem.getMeasuredHeight();
//				}
				ViewGroup.LayoutParams params = list.getLayoutParams();
				params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
				list.setLayoutParams(params);
				list.requestLayout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Expands ListView for variable height of item inside a ScrollView
	 */
	public static void expandListForVariableHeight(ListView list) {
		try {
			if (list.getCount() > 0) {
				ListAdapter listAdap = list.getAdapter();
				int totalHeight = 0;
				
				for (int i = 0; i < listAdap.getCount(); i++) {
					View listItem = listAdap.getView(i, null, list);
					listItem.measure(0, 0);
					totalHeight += listItem.getMeasuredHeight();
				}
				ViewGroup.LayoutParams params = list.getLayoutParams();
				params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
				list.setLayoutParams(params);
				list.requestLayout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public static void hideSoftKeyboard(Activity activity, View searchET) {
		try {
			InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void showSoftKeyboard(Activity activity, View searchET){
	    try {
	    	InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		    keyboard.showSoftInput(searchET, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	public static void openCallIntent(Activity activity, String phoneNumber){
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        activity.startActivity(callIntent);
	}

    public static void openSMSIntent(Activity activity, String numbers, String message){
        try {
            Uri sms_uri = Uri.parse("smsto:"+numbers);
            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sms_intent.putExtra("sms_body", message);
            activity.startActivity(sms_intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	
	public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean appInstalled = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }
        catch (Exception e) {
            appInstalled = false;
        }
        return appInstalled;
    }

	public static void showToast(Context context, String string){
		try {
			if(Data.toast != null){
                Data.toast.cancel();
            }
			Data.toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
			Data.toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	



    public static String hideEmailString(String email){
        String returnEmail = "";
        if(email.length() > 0 && email.contains("@")){
            int charLength = email.indexOf('@');
            int stars = (charLength - 4) > 0 ? (charLength - 4) : 4;
            stars = stars < 4 ? 4 : stars;
            int starsToShow = stars > charLength ? charLength : stars;
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<starsToShow; i++){
                stringBuilder.append("*");
            }
            returnEmail = stringBuilder.toString() + email.substring(starsToShow, email.length());
        }
        else if(email.length() > 0){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<email.length(); i++){
                stringBuilder.append("*");
            }
            returnEmail = stringBuilder.toString();
        }
        return returnEmail;
    }


    public static String hidePhoneNoString(String phoneNo){
        String returnPhoneNo = "";
        if(phoneNo.length() > 0){
            int charLength = phoneNo.length();
            int stars = (charLength < 3) ? 0 : (charLength - 3);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<stars; i++){
                stringBuilder.append("*");
            }
            returnPhoneNo = stringBuilder.toString() + phoneNo.substring(stars, phoneNo.length());
        }
        return returnPhoneNo;
    }


    public static String retrievePhoneNumberTenChars(String phoneNo){
        phoneNo = phoneNo.replace(" ", "");
        phoneNo = phoneNo.replace("(", "");
        phoneNo = phoneNo.replace("/", "");
        phoneNo = phoneNo.replace(")", "");
        phoneNo = phoneNo.replace("N", "");
        phoneNo = phoneNo.replace(",", "");
        phoneNo = phoneNo.replace("*", "");
        phoneNo = phoneNo.replace(";", "");
        phoneNo = phoneNo.replace("#", "");
        phoneNo = phoneNo.replace("-", "");
        phoneNo = phoneNo.replace(".", "");
        if(phoneNo.length() >= 10){
            phoneNo = phoneNo.substring(phoneNo.length()-10, phoneNo.length());
        }
        return phoneNo;
    }

    public static boolean validPhoneNumber(String phoneNo){
        if(phoneNo.length() >= 10){
            if(phoneNo.charAt(0) == '0' || phoneNo.contains("+")){
                return false;
            }
            else{
                return isPhoneValid(phoneNo);
            }
        }
        else{
            return false;
        }
    }


    public static boolean checkIfOnlyDigits(String strTocheck){
        String regex = "[0-9+]+";
        if(strTocheck.matches(regex)){
            return true;
        }
        else{
            return false;
        }
    }


    public static boolean isPhoneValid(CharSequence phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    public static String getCountryZipCode(Context context) {

        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        Log.e("CountryID", "=" + CountryID);
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                return CountryZipCode;
            }
        }
        return "";
    }




    public static void generateKeyHash(Context context){
        try { // single sign-on for fb application
            PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(),
                PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", ","
                    + Base64.encodeToString(md.digest(),
                    Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("error", "," + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("error", "," + e.toString());
        }
    }




    public static boolean hasAlphabets(String name){
        return name.matches(".*[a-zA-Z]+.*");
    }

	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
    public static boolean isLocationEnabled(Context context) {
        try{
            ContentResolver contentResolver = context.getContentResolver();
            boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
            boolean netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);
            return gpsStatus || netStatus;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


	public static HashMap<String, String> convertQueryToNameValuePairArr(String query)
			throws UnsupportedEncodingException {
		HashMap<String, String> nameValuePairs = new HashMap<>();
		String[] pairs = query.substring(2, query.length()-2).split(", ");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			nameValuePairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return nameValuePairs;
	}


    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                Log.e("service already running", "="+service.service.getClassName());
                return true;
            }
        }
        return false;
    }



	public static void enableSMSReceiver(Context context){
		try {
			ComponentName receiver = new ComponentName(context, IncomingSmsReceiver.class);
			PackageManager pm = context.getPackageManager();
			pm.setComponentEnabledSetting(receiver,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void disableSMSReceiver(Context context){
		try {
			ComponentName receiver = new ComponentName(context, IncomingSmsReceiver.class);
			PackageManager pm = context.getPackageManager();
			pm.setComponentEnabledSetting(receiver,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void enableReceiver(Context context, Class classT, boolean enable){
		try {
			ComponentName receiver = new ComponentName(context, classT);
			PackageManager pm = context.getPackageManager();
			if(enable) {
				pm.setComponentEnabledSetting(receiver,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			} else{
				pm.setComponentEnabledSetting(receiver,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static DecimalFormat decimalFormatMoney;
	public static DecimalFormat getMoneyDecimalFormat(){
		if(decimalFormatMoney == null){
			decimalFormatMoney = new DecimalFormat("#.##");
		}
		return decimalFormatMoney;
	}

	private static DecimalFormat decimalFormatMoneyWithoutFloat;
	public static DecimalFormat getMoneyDecimalFormatWithoutFloat(){
		if(decimalFormatMoneyWithoutFloat == null){
			decimalFormatMoneyWithoutFloat = new DecimalFormat("#");
		}
		return decimalFormatMoneyWithoutFloat;
	}



	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}


	public static boolean isDeviceRooted() {
		return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
	}

	private static boolean checkRootMethod1() {
		String buildTags = android.os.Build.TAGS;
		return buildTags != null && buildTags.contains("test-keys");
	}

	private static boolean checkRootMethod2() {
		String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
				"/system/bin/failsafe/su", "/data/local/su" };
		for (String path : paths) {
			if (new File(path).exists()) return true;
		}
		return false;
	}

	private static boolean checkRootMethod3() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (in.readLine() != null) return true;
			return false;
		} catch (Throwable t) {
			return false;
		} finally {
			if (process != null) process.destroy();
		}
	}




	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return "127.0.0.1";
	}


    public static void notificationManager(Context context, String message, int NOTIFICATION_ID) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Autos");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);


//            if (ring) {
//                builder.setLights(Color.GREEN, 500, 500);
//            } else {
                builder.setDefaults(Notification.DEFAULT_ALL);
//            }

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);


            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);

//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
                .getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(context.getPackageName()))
            return true;
        return false;
    }

	public static void checkAppsArrayInstall(Context context, ArrayList<AppPackage> appPackages) {
		int flags = PackageManager.GET_META_DATA |
				PackageManager.GET_SHARED_LIBRARY_FILES |
				PackageManager.GET_UNINSTALLED_PACKAGES;
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
		for(int i=0; i< appPackages.size(); i++){
			for (ApplicationInfo appInfo : applications) {
				if(appInfo.packageName.equalsIgnoreCase(appPackages.get(i).getPackageName())){
					appPackages.get(i).setInstalled(1);
					break;
				}
			}
		}
		return;
	}



	public static int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public static int pxToDp(Context context, int px) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	public interface AlertCallBackWithButtonsInterface {
		void positiveClick(View view);

		void neutralClick(View view);

		void negativeClick(View view);
	}

	public static void openUrl(Context context, String url){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

	public static List<String> splitEqually(String text, int size) {
		// Give the list the right capacity to start with. You could use an array
		// instead if you wanted.
		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}


	public static byte[] compressToBytesData(String string) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	public static boolean mockLocationEnabled(Location location) {
		try {
			if (Config.getDefaultServerUrl().equalsIgnoreCase(Config.getLiveServerUrl())) {
				boolean isMockLocation = false;
				if(location != null){
					Bundle extras = location.getExtras();
					isMockLocation = extras != null && extras.getBoolean(FusedLocationProviderApi.KEY_MOCK_LOCATION, false);
				}
				return isMockLocation;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static Shader textColorGradient(Context context, TextView textView){
		textView.measure(0, 0);
		int mWidth = textView.getMeasuredWidth();
		Shader shader;
		Shader.TileMode tile_mode = Shader.TileMode.CLAMP; // or TileMode.REPEAT;
		LinearGradient lin_grad = new LinearGradient(0, 0, (int)(mWidth/1.3), 0,
				context.getResources().getColor(R.color.theme_color_start),
				context.getResources().getColor(R.color.theme_color_end), tile_mode);
		shader = lin_grad;

		return shader;
	}

	public static StateListDrawable getSelector(Context context, int normalState, int pressedState){
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
				context.getResources().getDrawable(pressedState));
		stateListDrawable.addState(new int[]{},
				context.getResources().getDrawable(normalState));
		return stateListDrawable;
	}


	public static String retrieveOTPFromSMS(String message){
		String[] arr = message.split("\\ ");
		for(String iarr : arr){
			iarr = iarr.replace(".", "");
			if(iarr.length() >= 3 && checkIfOnlyDigits(iarr)){
				return iarr;
			}
		}
		return "";
	}

    /**
     * Push an "openScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushOpenScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", screenName));
    }

    /**
     * Push a "closeScreen" event with the given screen name. Tags that match that event will fire.
     */
    public static void pushCloseScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("closeScreen", DataLayer.mapOf("screenName", screenName));
    }

	public static String firstCharCapital(String str){
		String[] strArray = str.split(" ");
		StringBuilder builder = new StringBuilder();
		for (String s : strArray) {
			String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
			builder.append(cap + " ");
		}
		return builder.toString();
	}

	public static int dpToPx(Context context, float dp) {
		int temp = (int)dp;
		final float scale = context.getResources().getDisplayMetrics().density;
		return Math.round(dp * scale);
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

}


