package com.sabkuchfresh.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
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

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppPackage;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;


public class Utils {

//    public static final int SCALE_FACTOR = 30;
//
//    public static void hideShowViewByScale(final View view, final int icon) {
//
//        ViewPropertyAnimator propertyAnimator = view.animate().setStartDelay(SCALE_FACTOR)
//                .scaleX(0).scaleY(0);
//
//        propertyAnimator.setDuration(300);
//        propertyAnimator.start();
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                ((FloatingActionButton) view).setBackgroundResource(icon);
////                ((FloatingActionButton) view).setImageResource(icon);
//                showViewByScale(view);
//            }
//        }, 300);
//
//    }
//
//    public static void showViewByScale(View view) {
//
//        ViewPropertyAnimator propertyAnimator = view.animate().setStartDelay(SCALE_FACTOR)
//                .scaleX(1).scaleY(1);
//
//        propertyAnimator.setDuration(300);
//
//        propertyAnimator.start();
//    }
//
//    public static void hideViewByScale(final View view) {
//        ViewPropertyAnimator propertyAnimator = view.animate().setStartDelay(SCALE_FACTOR)
//                .scaleX(0).scaleY(0);
//        propertyAnimator.setDuration(300);
//        propertyAnimator.start();
//    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

	public static void showToast(Context context, String string){
		showToast(context, string, Toast.LENGTH_SHORT);
	}
	public static void showToast(Context context, String string, int duration){
		try {
			if(MyApplication.getInstance().getToast() != null){
				MyApplication.getInstance().getToast().cancel();
			}
			MyApplication.getInstance().setToast(Toast.makeText(context, string, duration));
			MyApplication.getInstance().getToast().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cancelToast(){
		try {
			if(MyApplication.getInstance().getToast() != null){
				MyApplication.getInstance().getToast().cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * Method used to hide keyboard if outside touched.
     *
     * @param view
     */
    public static void setupUI(View view, final Activity mContext) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText) && !(view instanceof ImageView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
                    }catch(Exception e) {

                    }
                    return false;
                }

            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, mContext);
            }
        }
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


    public static boolean validPhoneNumber(String phoneNo){
		return product.clicklabs.jugnoo.utils.Utils.validPhoneNumber(phoneNo);
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
		return product.clicklabs.jugnoo.utils.Utils.isPhoneValid(phone);
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

	private static DecimalFormat decimalFormat2Decimal;
	public static DecimalFormat getDecimalFormat2Decimal(){
		if(decimalFormat2Decimal == null){
			decimalFormat2Decimal = new DecimalFormat("0.00");
		}
		return decimalFormat2Decimal;
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





    public static boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
                .getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(context.getPackageName()))
            return true;
        return false;
    }


	public static int dpToPx(Context context, int dp) {
    	return product.clicklabs.jugnoo.utils.Utils.dpToPx(context, (float)dp);
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

	public static void openUrl(Activity context, String url){
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

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

    public static void showPaystorePopup(final Activity activity, final String title, String message) {
        DialogPopup.alertPopupWithListener(activity, title, message, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openPlayStore(activity);
            }
        });
    }

    public static void openPlayStore(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static String retrieveOTPFromSMS(String message){
        String[] arr = message.split("\\ ");
        for(String iarr : arr){
            iarr = iarr.replace(".", "");
            if(iarr.length() >= 4 && checkIfOnlyDigits(iarr)){
                return iarr;
            }
        }
        return "";
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

	public static String getDoubleTwoDigits(Double amount){
		String finalVal;
		if(amount % 1 == 0)
			finalVal = String.valueOf(amount.intValue());
		else
			finalVal = Utils.getMoneyDecimalFormat().format(amount);

		return finalVal;
	}

	public static void hideKeyboard(Activity activity){
		View view = activity.getCurrentFocus();
		if (view != null) {
		    try {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            catch (Exception e){
            }

		}
	}

	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html) {
		Spanned result;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
		} else {
			result = Html.fromHtml(html);
		}
		return result;
	}

	public static CharSequence trimHTML(CharSequence s){
		return product.clicklabs.jugnoo.utils.Utils.trimHTML(s);
	}

	public static void setTextUnderline(TextView textView, String text){
		product.clicklabs.jugnoo.utils.Utils.setTextUnderline(textView, text);
	}

	public static String capEachWord(String source) {
		return product.clicklabs.jugnoo.utils.Utils.capEachWord(source);
	}

	public static void openMapsDirections(Context context, LatLng source, LatLng dest){
		product.clicklabs.jugnoo.utils.Utils.openMapsDirections(context, source, dest);
	}

	public static void openMapsDirections(Context context, LatLng source){
		product.clicklabs.jugnoo.utils.Utils.openMapsDirections(context, source);
	}

    public static void addCapitaliseFilterToEditText(EditText editText){
        InputFilter[] editFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        editText.setFilters(newFilters);
    }


    public static void setMaxHeightToDropDown(View spinner,int height){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(height);
        }
        catch (Exception e) {
            // silently fail...
        }

    }

    public static String formatCurrencyAmount(final double amount, final String currencyCode, final String currency){
    	try {
    		if(!TextUtils.isEmpty(currencyCode)){
				String itemPrice = product.clicklabs.jugnoo.utils.Utils.formatCurrencyValue(currencyCode,
						amount, false);

				if (itemPrice.contains(currencyCode)) {
					return String.format("%s%s", !TextUtils.isEmpty(currency) ? currency : currencyCode, product.clicklabs.jugnoo.utils.Utils.getMoneyDecimalFormat().format(amount));
				} else {
					return itemPrice;
				}
			}else {
				return String.format(MyApplication.getInstance().getResources().getString(R.string.rupees_value_format),
						product.clicklabs.jugnoo.utils.Utils.getMoneyDecimalFormat().format(amount));
			}
		} catch (Exception e){
    		return String.format(MyApplication.getInstance().getResources().getString(R.string.rupees_value_format),
					product.clicklabs.jugnoo.utils.Utils.getMoneyDecimalFormat().format(amount));
		}
	}
}


