package product.clicklabs.jugnoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.Header;
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
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashNewActivity extends Activity implements LocationUpdate{
	
	
	LinearLayout relative;
	
	ImageView jugnooImg;
	ImageView jugnooTextImg;
	ProgressBar progressBar1;
	
	Button buttonLogin, buttonRegister;
	
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
		
		Data.SERVER_URL = Data.DEFAULT_SERVER_URL;
		
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
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}
		
		
		loginDataFetched = false;
		loginFailed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);
		
		
		jugnooImg = (ImageView) findViewById(R.id.jugnooImg);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg.setVisibility(View.GONE);
		
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin); buttonLogin.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		buttonRegister = (Button) findViewById(R.id.buttonRegister); buttonRegister.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		
		buttonLogin.setVisibility(View.GONE);
		buttonRegister.setVisibility(View.GONE);
		
		
		buttonLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegisterScreen.facebookLogin = false;
				startActivity(new Intent(SplashNewActivity.this, RegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		jugnooTextImg.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this);
				return false;
			}
		});
		
		
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = pInfo.versionCode;
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
		
		gcm = GoogleCloudMessaging.getInstance(this);
	    regid = getRegistrationId(this);
	    Data.deviceToken = regid;

	    Log.i("deviceToken", Data.deviceToken + "..");
	    
	    noNetFirstTime = false;
		noNetSecondTime = false;
	    
		if(getIntent().hasExtra("no_anim")){
			jugnooImg.clearAnimation();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.setMargins(0, (int)(150 * ASSL.Yscale()), 0, 0);
			jugnooImg.setLayoutParams(layoutParams);
			jugnooTextImg.setVisibility(View.VISIBLE);
			callFirstAttempt();
		}
		else{
			Animation animation = new TranslateAnimation(0, 0, 0, (int)(438*ASSL.Yscale()));
			animation.setFillAfter(false);
			animation.setDuration(650);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.setAnimationListener(new ShowAnimListener());
			jugnooImg.startAnimation(animation);
		}
		
		
		
		
		
		jugnooImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!loginDataFetched){
					noNetFirstTime = false;
					noNetSecondTime = false;
					callFirstAttempt();
				}
			}
		});
		
		
		
		
		
	    
	}
	
	
	@Override
	protected void onResume() {
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			new DialogPopup().showGooglePlayErrorAlert(SplashNewActivity.this);
		}
		else{
			new DialogPopup().showLocationSettingsAlert(SplashNewActivity.this);
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
			layoutParams.setMargins(0, (int)(150 * ASSL.Yscale()), 0, 0);
			jugnooImg.setLayoutParams(layoutParams);
			
			jugnooTextImg.setVisibility(View.VISIBLE);
			
//			if(SplashNewActivity.isLastLocationUpdateFine(SplashNewActivity.this)){
			callFirstAttempt();
//			}
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	
	
	public void callFirstAttempt(){
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
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		final String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		final String id = pref.getString(Data.SP_ID_KEY, "");
		if(!"".equalsIgnoreCase(accessToken)){
			buttonLogin.setVisibility(View.GONE);
			buttonRegister.setVisibility(View.GONE);
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
			
				//start_app_using_access_token
				//access_token
				
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/start_app_using_access_token", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
								DialogPopup.dismissLoadingDialog();
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.e("Server response of access_token", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity);
									
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
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
		else{
			buttonLogin.setVisibility(View.VISIBLE);
			buttonRegister.setVisibility(View.VISIBLE);
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
				String resp = new JSONParser().parseAccessTokenLoginData(activity, response, accessToken, id);
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				return HttpRequester.SERVER_TIMEOUT;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				loginDataFetched = false;
				new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			}
			else{

				noNetFirstTime = false;
				noNetSecondTime = false;
				
				loginDataFetched = true;
				
			}
			

			DialogPopup.dismissLoadingDialog();
		}
		
	}
	
	
	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception{
//		"popup": {
//	        "title": "Update Version",
//	        "text": "Update app with new version!",
//	        "cur_version": 116,			// could be used for local check
//	        "is_force": 1				// 1 for forced, 0 for not forced
//	}
		if(!jObj.isNull("popup")){
			try{
				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup"); 
				String title = jupdatePopupInfo.getString("title");
				String text = jupdatePopupInfo.getString("text");
				int currentVersion = jupdatePopupInfo.getInt("cur_version");
				int isForce = jupdatePopupInfo.getInt("is_force");
				
				if(Data.appVersion >= currentVersion){
					return false;
				}
				else{
					SplashNewActivity.appUpdatePopup(title, text, isForce, activity);
					if(isForce == 1){
						return true;
					}
					else{
						return false;
					}
				}
			} catch(Exception e){
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
	public static void appUpdatePopup(String title, String message, final int isForced, final Activity activity) {
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
					if(isForced == 1){
						activity.finish();
					}
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
	
	
	
	
	
	
	
	//TODO debug code confirm popup
		public void confirmDebugPasswordPopup(final Activity activity){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.otp_confirm_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.regularFont(activity));
				
				textHead.setText("Confirm Debug Password");
				textMessage.setText("Please enter password to continue.");
				
				textHead.setVisibility(View.GONE);
				textMessage.setVisibility(View.GONE);
				
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.regularFont(activity));
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(Data.DEBUG_PASSWORD.equalsIgnoreCase(code)){
								dialog.dismiss();
								changeServerLinkPopup(activity);
							}
							else{
								etCode.requestFocus();
								etCode.setError("Code not matched.");
							}
						}
					}
					
				});
				
				
				etCode.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
						int result = actionId & EditorInfo.IME_MASK_ACTION;
						switch (result) {
							case EditorInfo.IME_ACTION_DONE:
								btnConfirm.performClick();
							break;

							case EditorInfo.IME_ACTION_NEXT:
							break;

							default:
						}
						return true;
					}
				});
				
				crossbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	
	
		//TODO change server link popup
		void changeServerLinkPopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.custom_three_btn_dialog);

					FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					
					
					frameLayout.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					
					RelativeLayout innerRl = (RelativeLayout) dialog.findViewById(R.id.innerRl);
					innerRl.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						}
					});
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
					
					
					SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
					String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
					
					if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
						textMessage.setText("Current server is SALES.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
						textMessage.setText("Current server is LIVE.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
						textMessage.setText("Current server is DEV.\nChange to:");
					}
					
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
					btnOk.setText("LIVE");
					
					Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral); btnNeutral.setTypeface(Data.regularFont(activity));
					btnNeutral.setText("DEV");
					
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
					btnCancel.setText("SALES");
					
					Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(activity));
					crossbtn.setVisibility(View.VISIBLE);
					
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.LIVE_SERVER_URL);
							editor.commit();
							
							Data.SERVER_URL = Data.LIVE_SERVER_URL;
							
							dialog.dismiss();
						}
					});
					
					btnNeutral.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.DEV_SERVER_URL);
							editor.commit();
							
							Data.SERVER_URL = Data.DEV_SERVER_URL;
							
							dialog.dismiss();
						}
					});
					
					btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.TRIAL_SERVER_URL);
							editor.commit();
							
							Data.SERVER_URL = Data.TRIAL_SERVER_URL;
							
							dialog.dismiss();
						}
					});

					
					crossbtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
					});
					
					
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
    			SingleLocationSender.this.location = loc;
    			new DriverLocationDispatcher().saveLocationToDatabase(SplashNewActivity.this, loc);
    			new DriverLocationDispatcher().sendLocationToServer(SplashNewActivity.this, "LocationReciever");
    		}

    		public void onProviderDisabled(String provider) {
    		}

    		public void onProviderEnabled(String provider) {
    		}

    		public void onStatusChanged(String provider, int status, Bundle extras) {
    		}
    }
    }


	@Override
	public void onLocationChanged(Location location, int priority) {
		new DriverLocationDispatcher().saveLocationToDatabase(SplashNewActivity.this, location);
	}
	
	
	public static boolean isLastLocationUpdateFine(Activity activity){
		try {
			Database2 database2 = new Database2(activity);
			String userMode = database2.getUserMode();
			String driverScreenMode = database2.getDriverScreenMode();
			long lastLocationUpdateTime = database2.getDriverLastLocationTime();
			database2.close();
			
			long currentTime = System.currentTimeMillis();
			
			if(lastLocationUpdateTime == 0){
				lastLocationUpdateTime = System.currentTimeMillis();
			}
			
			long systemUpTime = SystemClock.uptimeMillis();
			
//			Log.e("isLastLocationUpdateFine lastLocationUpdateTime", "="+(currentTime - (lastLocationUpdateTime + HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT)));
//			Log.e("isLastLocationUpdateFine systemUpTime", "="+systemUpTime);
//			Log.e("isLastLocationUpdateFine userMode", "="+userMode);
//			Log.e("isLastLocationUpdateFine driverScreenMode", "="+driverScreenMode);
			
			
			if(systemUpTime > HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT){
//				Log.i("systemUpTime", "greater");
				if(Database2.UM_DRIVER.equalsIgnoreCase(userMode) && 
						(currentTime >= (lastLocationUpdateTime + HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT))){
					if(Database2.VULNERABLE.equalsIgnoreCase(driverScreenMode)){
						showRestartPhonePopup(activity);
						return false;
					}
					else{
						dismissRestartPhonePopup();
						return true;
					}
				}
				else{
					dismissRestartPhonePopup();
					return true;
				}
			}
			else{
				dismissRestartPhonePopup();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			dismissRestartPhonePopup();
			return true;
		}
	}
	
	
	public static Dialog restartPhoneDialog;
	public static void showRestartPhonePopup(final Activity activity){
		try {
			if(restartPhoneDialog == null || !restartPhoneDialog.isShowing()){
				restartPhoneDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				restartPhoneDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				restartPhoneDialog.setContentView(R.layout.no_driver_dialog);
	
				FrameLayout frameLayout = (FrameLayout) restartPhoneDialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
	
				WindowManager.LayoutParams layoutParams = restartPhoneDialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				restartPhoneDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				restartPhoneDialog.setCancelable(false);
				restartPhoneDialog.setCanceledOnTouchOutside(false);
	
				TextView textHead = (TextView) restartPhoneDialog.findViewById(R.id.textHead);
				textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
				textHead.setVisibility(View.GONE);
				TextView textMessage = (TextView) restartPhoneDialog.findViewById(R.id.textMessage);
				textMessage.setTypeface(Data.regularFont(activity));
	
				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
				
				textMessage.setText("Network Problem. Please Switch OFF and Switch ON your phone and wait for 5 minutes to continue using Jugnoo.");
				
	
				Button btnOk = (Button) restartPhoneDialog.findViewById(R.id.btnOk);
				btnOk.setTypeface(Data.regularFont(activity));
				Button crossbtn = (Button) restartPhoneDialog
						.findViewById(R.id.crossbtn);
				crossbtn.setTypeface(Data.regularFont(activity));
				crossbtn.setVisibility(View.GONE);
	
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						restartPhoneDialog.dismiss();
						activity.finish();
					}
				});
	
				restartPhoneDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void dismissRestartPhonePopup(){
		try{
			if(restartPhoneDialog != null && restartPhoneDialog.isShowing()){
				restartPhoneDialog.dismiss();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
