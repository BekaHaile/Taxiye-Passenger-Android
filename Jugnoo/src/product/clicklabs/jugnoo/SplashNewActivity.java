package product.clicklabs.jugnoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

public class SplashNewActivity extends Activity implements LocationUpdate{
	
	
	LinearLayout relative;
	
	ImageView jugnooImg;
	ImageView jugnooTextImg;
	ProgressBar progressBar1;
	
	boolean loginDataFetched = false, loginFailed = false;
	
	GoogleCloudMessaging gcm;
	String regid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		
//		SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
//		String languageSelected = preferences.getString(Data.LANGUAGE_SELECTED, "default");
//		if(!"default".equalsIgnoreCase(languageSelected)){
//			Locale locale = new Locale(languageSelected); 
//		    Locale.setDefault(locale);
//		    Configuration config = new Configuration();
//		    config.locale = locale;
//		    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//		}
		
		
		SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
		
		if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
			Data.SERVER_URL = Data.TRIAL_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
			Data.SERVER_URL = Data.LIVE_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
			Data.SERVER_URL = Data.DEV_SERVER_URL;
		}
		
		
		
		Locale locale = new Locale("en"); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		
		setContentView(R.layout.splash_new);
		
		
		loginDataFetched = false;
		loginFailed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);
		
		
		jugnooImg = (ImageView) findViewById(R.id.jugnooImg);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg.setVisibility(View.GONE);
		
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
		
		
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = ""+pInfo.versionCode;
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
			Log.i("deviceName", Data.deviceName + "..");
			
			Data.deviceToken = getRegistrationId(this);
			
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		
		
		Animation animation = new TranslateAnimation(0, 0, 0, (int)(550*ASSL.Yscale()));
		animation.setFillAfter(false);
		animation.setDuration(650);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setAnimationListener(new ShowAnimListener());
		jugnooImg.startAnimation(animation);
		
		noNetFirstTime = false;
		
		
		
		
		
		gcm = GoogleCloudMessaging.getInstance(this);
	    regid = getRegistrationId(this);
	    Data.deviceToken = regid;

	    Log.i("deviceToken", Data.deviceToken + "..");
	    
	    PicassoTools.clearCache(Picasso.with(this));
		
	}
	
	
	@Override
	protected void onResume() {
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}
		
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		try{
			Data.locationFetcher.destroy();
			Data.locationFetcher = null;
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
	}
	
	
	
	
	boolean noNetFirstTime = false, noNetSecondTime = false;
	
	Handler checkNetHandler = new Handler();
	Runnable checkNetRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						noNetSecondTime = false;
						if (regid.isEmpty()){
					        registerInBackground();
					    }
					    else{
					    	accessTokenLogin(SplashNewActivity.this);
					    }
					}
					else{
						new DialogPopup().alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
						noNetSecondTime = true;
					}
					
				}
			});
			
		}
	};
	
	
	
	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i("dfs", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i("sdfs", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private void registerInBackground() {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
	    new AsyncTask<String, Integer, String>() {

	        @Override
	    	protected void onPreExecute() {
	    		progressBar1.setVisibility(View.VISIBLE);
	    	};
	    	
	        @Override
	        protected String doInBackground(String... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(SplashNewActivity.this);
	                }
	                regid = gcm.register(Data.GOOGLE_PROJECT_ID);
	                Data.deviceToken = regid;
	                msg = "Device registered, registration ID=" + regid;
	                
	                setRegistrationId(SplashNewActivity.this, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	Log.e("msg  ===== ","="+msg);
	    		progressBar1.setVisibility(View.GONE);
	    		accessTokenLogin(SplashNewActivity.this);
	        	//=Device registered, registration ID=APA91bHaLnaJLjUGLXDKcW39Gke0eK78tFRe1ByJsj8rmFS2boJ2_HNzvxkS39tfo0z6IahCUPyV49gpHx-2M3WzWmpHv4u4O0cGuYxN-aKuPx1SG4Gy-2WHBg8o3sSP_GtJgfThb3G36miecVxQ1xGafeKMgbV2sO9EP1aaVDyXI3t6bgS7gmQ
	        }
	    }.execute(null, null, null);
		}
		else{
			new DialogPopup().alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	private void setRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, getAppVersion(context));
	    editor.commit();
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(SplashLogin.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	
	class ShowAnimListener implements AnimationListener{

		
		public ShowAnimListener(){
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
			jugnooImg.clearAnimation();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.setMargins(0, (int)(262 * ASSL.Yscale()), 0, 0);
			jugnooImg.setLayoutParams(layoutParams);
			
			jugnooTextImg.setVisibility(View.VISIBLE);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
								noNetFirstTime = false;
								Log.e("regid.isEmpty()", "+"+regid.isEmpty());
								if (regid.isEmpty()){
							        registerInBackground();
							    }
							    else{
							    	accessTokenLogin(SplashNewActivity.this);
							    }
							}
							else{
								new DialogPopup().alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
								noNetFirstTime = true;
							}
						}
					});
				}
			}, 2000);
			
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		final String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		final String id = pref.getString(Data.SP_ID_KEY, "");
		if(!"".equalsIgnoreCase(accessToken)){
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				if(Data.locationFetcher != null){
					Data.latitude = Data.locationFetcher.getLatitude();
					Data.longitude = Data.locationFetcher.getLongitude();
				}
				
				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);
				params.put("device_token", Data.deviceToken);
				params.put("latitude", ""+Data.latitude);
				params.put("longitude", ""+Data.longitude);
				params.put("app_version", ""+Data.appVersion);
				params.put("device_type", "0");

				new SingleLocationSender(SplashNewActivity.this, accessToken, Data.deviceToken, Data.SERVER_URL);
				
				Log.i("accessToken", "=" + accessToken);
				Log.i("device_token", Data.deviceToken);
				Log.i("latitude", ""+Data.latitude);
				Log.i("longitude", ""+Data.longitude);
				Log.i("app_version", ""+Data.appVersion);
			
				AsyncHttpClient client = Data.getClient();
				client.setTimeout(Data.SERVER_TIMEOUT);
				client.post(Data.SERVER_URL + "/access_token", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
		
							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
									
									if(!newUpdate){
										if(!jObj.isNull("error")){
											
											
	//										{"error":"some parameter missing","flag":0}//error
	//										{"error":"invalid access token","flag":1}//error
	
											int flag = jObj.getInt("flag");	
											String errorMessage = jObj.getString("error");
											if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
												HomeActivity.logoutUser(activity);
											}
											else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
												new DialogPopup().alertPopup(activity, "", errorMessage);
											}
											else if(1 == flag){ // {"error":"email not  registered","flag":1}//error
												new DialogPopup().alertPopup(activity, "", errorMessage);
											}
											else if(8 == flag){ // {"error":"email not  registered","flag":1}//error
	//											new DialogPopup().alertPopup(activity, "", errorMessage);
												noNetFirstTime = false;
												noNetSecondTime = false;
												
												loginFailed = true;
											}
											else{
												new DialogPopup().alertPopup(activity, "", errorMessage);
											}
											DialogPopup.dismissLoadingDialog();
										}
										else{
											
											new AccessTokenDataParseAsync(activity, response, accessToken, id).execute();
											
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									DialogPopup.dismissLoadingDialog();
								}
		
							}
		
							@Override
							public void onFailure(Throwable arg0) {
								Log.e("request fail", arg0.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
		else{
			startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}

	}
	
	
	class AccessTokenDataParseAsync extends AsyncTask<String, Integer, String>{
		
		Activity activity;
		String response, accessToken, id;
		
		public AccessTokenDataParseAsync(Activity activity, String response, String accessToken, String id){
			this.activity = activity;
			this.response = response;
			this.accessToken = accessToken;
			this.id = id;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				new JSONParser().parseAccessTokenLoginData(activity, response, accessToken, id);
			} catch (Exception e) {
				e.printStackTrace();
				return "excep";
			}
			
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(result.equalsIgnoreCase("excep")){
				new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			}
			else{

				noNetFirstTime = false;
				noNetSecondTime = false;
				
				loginDataFetched = true;
				
				
//				DialogPopup.showLoadingDialog(activity, "Loading...");
			}

			DialogPopup.dismissLoadingDialog();
		}
		
	}
	
	
	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception{
		
		if(!jObj.isNull("popup")){
			try{
				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup"); 
				String title = jupdatePopupInfo.getString("title");
				String text = jupdatePopupInfo.getString("text");
				
				SplashNewActivity.appUpdatePopup(title, text, activity);
				return true;
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		else{
			return false;
		}
		
	}
	
	
	
	/**
	 * Displays appUpdatePopup dialog
	 */
	public static void appUpdatePopup(String title, String message, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.app_update_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
//			textHead.setText(title);
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					activity.finish();
				}
			});
			
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=product.clicklabs.jugnoo"));
					activity.startActivity(intent);
					activity.finish();
				}
				
			});
			

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && noNetFirstTime){
			noNetFirstTime = false;
			checkNetHandler.postDelayed(checkNetRunnable, 4000);
		}
		else if(hasFocus && noNetSecondTime){
			noNetSecondTime = false;
			finish();
		}
		else if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			startActivity(new Intent(SplashNewActivity.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && loginFailed){
			loginFailed = false;
			startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
    class SingleLocationSender {

    	public SingleLocationListener listener;
    	public LocationManager locationManager;
    	public Location location;
    	public String accessToken, deviceToken, SERVER_URL;
    	
    	/**
    	 * Initialize location fetcher object with selected listeners
    	 * @param context
    	 */
		public SingleLocationSender(Context context, String accessToken, String deviceToken, String SERVER_URL) {
			this.accessToken = accessToken;
			this.deviceToken = deviceToken;
			this.SERVER_URL = SERVER_URL;
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				listener = new SingleLocationListener();
				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
			}
		}
    	
    	public void destroy(){
    		try{
    			locationManager.removeUpdates(listener);
    		}catch(Exception e){
    		}
    	}

    	

    	class SingleLocationListener implements LocationListener {

    		public void onLocationChanged(Location loc) {
    			Log.e("************************************** custom", "Location changed "+loc);
    			SingleLocationSender.this.location = loc;
    			sendLocationToServer(location);
    		}

    		public void onProviderDisabled(String provider) {
    		}

    		public void onProviderEnabled(String provider) {
    		}

    		public void onStatusChanged(String provider, int status, Bundle extras) {
    		}
    	
    	public void sendLocationToServer(final Location location){
    		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
						nameValuePairs.add(new BasicNameValuePair("latitude", ""+location.getLatitude()));
						nameValuePairs.add(new BasicNameValuePair("longitude", ""+location.getLongitude()));
						nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
						
						Log.e("nameValuePairs in fast","="+nameValuePairs);
						
						SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
						String result = simpleJSONParser.getJSONFromUrlParams(SERVER_URL+"/update_driver_location", nameValuePairs);
						
						Log.e("SingleLocationListener    result","="+result);
						
						simpleJSONParser = null;
						nameValuePairs = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
    	}
    }
    }


	@Override
	public void onLocationChanged(Location location, int priority) {
		// TODO Auto-generated method stub
		
	}
	
	
}
