package product.clicklabs.jugnoo;

import java.util.Locale;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.UniqueIMEIID;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class SplashNewActivity extends Activity implements LocationUpdate{
	
	LinearLayout relative;
	
	ImageView imageViewJugnooLogo;
	
	ProgressBar progressBar;
	
	RelativeLayout relativeLayoutLoginSignupButtons;
	Button buttonLogin, buttonRegister;
	
	LinearLayout linearLayoutNoNet;
	TextView textViewNoNet;
	Button buttonNoNetCall;
	
	boolean loginDataFetched = false, resumed = false;
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Splash started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	public static void initializeServerURL(Context context){
		SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
		
		Data.SERVER_URL = Data.DEFAULT_SERVER_URL;
		
		if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
			Data.SERVER_URL = Data.TRIAL_SERVER_URL.substring(0, Data.TRIAL_SERVER_URL.length()-4) + Database2.getInstance(context).getSalesPortNumber();
			Data.FLURRY_KEY = "abcd";
		}
		else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
			Data.SERVER_URL = Data.DEV_SERVER_URL.substring(0, Data.DEV_SERVER_URL.length()-4) + Database2.getInstance(context).getDevPortNumber();
			Data.FLURRY_KEY = "abcd";
		}
		else{
			Data.SERVER_URL = Data.LIVE_SERVER_URL.substring(0, Data.LIVE_SERVER_URL.length()-4) + Database2.getInstance(context).getLivePortNumber();
			Data.FLURRY_KEY = Data.STATIC_FLURRY_KEY;
		}
		Log.e("link", "="+link);
	}
	
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
		
		
		
//		try {
//			Uri targetUri = getIntent().getData();
//			Log.e("targetUri =======********", "="+targetUri);
//			if(targetUri != null){
//				String autoShare = targetUri.getQueryParameter("autoshare");
//				if("1".equalsIgnoreCase(autoShare)){
//					Data.autoShare = 1;
//				}
//				else{
//					Data.autoShare = 0;
//				}
//			}
//			else{
//				Data.autoShare = 0;
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			Data.autoShare = 0;
//		}
		
		Data.userData = null;
		
		initializeServerURL(this);
		
		
		Locale locale = new Locale("en"); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		
		setContentView(R.layout.activity_splash_new);
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}
		
		
		loginDataFetched = false;
		resumed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);
		
		
		imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		
		relativeLayoutLoginSignupButtons = (RelativeLayout) findViewById(R.id.relativeLayoutLoginSignupButtons);
		buttonLogin = (Button) findViewById(R.id.buttonLogin); buttonLogin.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		buttonRegister = (Button) findViewById(R.id.buttonRegister); buttonRegister.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		linearLayoutNoNet = (LinearLayout) findViewById(R.id.linearLayoutNoNet);
		textViewNoNet = (TextView) findViewById(R.id.textViewNoNet); textViewNoNet.setTypeface(Data.latoRegular(this));
		buttonNoNetCall = (Button) findViewById(R.id.buttonNoNetCall); buttonNoNetCall.setTypeface(Data.latoRegular(this));
		
		
		
		relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
		linearLayoutNoNet.setVisibility(View.GONE);
		
		
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
		
		buttonNoNetCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.openCallIntent(SplashNewActivity.this, Data.SUPPORT_NUMBER);
			}
		});
		
		relative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!loginDataFetched){
					getDeviceToken();
				}
			}
		});
		
		imageViewJugnooLogo.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this);
				FlurryEventLogger.debugPressed("no_token");
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
			Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
			Log.e("Data.uniqueDeviceId = ", "="+Data.uniqueDeviceId);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		

		
	    
	    
		if(getIntent().hasExtra("no_anim")){
			imageViewJugnooLogo.clearAnimation();
			getDeviceToken();
		}
		else{
			Animation animation = new AlphaAnimation(0, 1);
			animation.setFillAfter(true);
			animation.setDuration(1000);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.setAnimationListener(new ShowAnimListener());
			imageViewJugnooLogo.startAnimation(animation);
		}
		
		
		
	    
	}
	
	public void getDeviceToken(){
	    relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
	    linearLayoutNoNet.setVisibility(View.GONE);
	    if (AppStatus.getInstance(this).isOnline(this)) {
	    	progressBar.setVisibility(View.VISIBLE);
			new DeviceTokenGenerator(SplashNewActivity.this).generateDeviceToken(SplashNewActivity.this, new IDeviceTokenReceiver() {
				
				@Override
				public void deviceTokenReceived(final String regId) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Data.deviceToken = regId;
							Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
							accessTokenLogin(SplashNewActivity.this);
							progressBar.setVisibility(View.GONE);
						}
					});
					
				}
			});
	    }
	    else{
	    	linearLayoutNoNet.setVisibility(View.VISIBLE);
	    }
	}
	
	
	
	@Override
	protected void onResume() {
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		}
		else{
			DialogPopup.showLocationSettingsAlert(SplashNewActivity.this);
		}
		
		
		super.onResume();
		DialogPopup.dismissAlertPopup();
		retryAccessTokenLogin();
		resumed = true;
	}
	
	
	public void retryAccessTokenLogin(){
		if(resumed){
			relative.performClick();
		}
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
			imageViewJugnooLogo.clearAnimation();
			getDeviceToken();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	
	
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
		
		relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
		linearLayoutNoNet.setVisibility(View.GONE);
		
		if(!"".equalsIgnoreCase(pair.first)){
			String accessToken = pair.first;
			
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
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("client_id", Data.CLIENT_ID);
				params.put("is_access_token_new", ""+pair.second);

				Log.i("accessToken", "=" + accessToken);
				Log.i("device_token", Data.deviceToken);
				Log.i("latitude", ""+Data.latitude);
				Log.i("longitude", ""+Data.longitude);
				Log.i("app_version", ""+Data.appVersion);
				Log.i("unique_device_id", "=" + Data.uniqueDeviceId);
				
				Log.e("params", "="+params);
			
				
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/login_using_access_token", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
								DialogPopup.dismissLoadingDialog();
								linearLayoutNoNet.setVisibility(View.VISIBLE);
							}

							@Override
							public void onSuccess(String response) {
								Log.e("Server response of access_token", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									int flag = jObj.getInt("flag");
									
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
											DialogPopup.dismissLoadingDialog();
										}
										else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
											DialogPopup.dismissLoadingDialog();
										}
										else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
											if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
												new AccessTokenDataParseAsync(activity, response).execute();
												
												SharedPreferences pref1 = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
												Editor editor = pref1.edit();
												editor.putString(Data.SP_ACCESS_TOKEN_KEY, "");
												editor.commit();
											}
											else{
												DialogPopup.dismissLoadingDialog();
											}
										}
										else{
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
											DialogPopup.dismissLoadingDialog();
										}
									}
									else{
										DialogPopup.dismissLoadingDialog();
									}
									
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									DialogPopup.dismissLoadingDialog();
								}
		
							}
						});
			}
			else {
				linearLayoutNoNet.setVisibility(View.VISIBLE);
			}
		}
		else{
			relativeLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
		}

	}
	
	
	class AccessTokenDataParseAsync extends AsyncTask<String, Integer, String>{
		
		Activity activity;
		String response;
		
		public AccessTokenDataParseAsync(Activity activity, String response){
			this.activity = activity;
			this.response = response;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				String resp = new JSONParser().parseAccessTokenLoginData(activity, response);
				Log.e("AccessTokenDataParseAsync resp", "="+resp);
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				return HttpRequester.SERVER_TIMEOUT;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("AccessTokenDataParseAsync result", "="+result);
			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				loginDataFetched = false;
				DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			}
			else{
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
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			textHead.setVisibility(View.VISIBLE);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
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
	
	public static boolean checkIfTrivialAPIErrors(Activity activity, JSONObject jObj){
		try {
			int flag = jObj.getInt("flag");
			if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				HomeActivity.logoutUser(activity);
				return true;
			}
			else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String errorMessage = jObj.getString("error");
				DialogPopup.alertPopup(activity, "", errorMessage);
				return true;
			}
			else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String message = jObj.getString("message");
				DialogPopup.alertPopup(activity, "", message);
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			Log.e("e", "="+e);
		}
		return false;
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			startActivity(new Intent(SplashNewActivity.this, HomeActivity.class));
			ActivityCompat.finishAffinity(this);
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	
	public static void sendToCustomerAppPopup(String title, String message, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			textHead.setVisibility(View.VISIBLE);
			
			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			btnOk.setText("Go to PlayStore");
			
			frameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
			
			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					CustomAppLauncher.launchApp(activity, "product.clicklabs.jugnoo");
					activity.finish();
				}
			});
			

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
		public void confirmDebugPasswordPopup(final Activity activity){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_edittext_confirm);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.latoRegular(activity));
				
				textHead.setText("Confirm Debug Password");
				textMessage.setText("Please enter password to continue.");
				
				textHead.setVisibility(View.GONE);
				textMessage.setVisibility(View.GONE);
				
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.latoRegular(activity));
				
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
				
				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					}
				});

				frameLayout.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	
	
		void changeServerLinkPopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.dialog_custom_three_buttons);

					FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(true);
					
					
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
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
					
					
					SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
					String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
					
					if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
						textMessage.setText("Current server is SALES \n"+Data.SERVER_URL+".\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
						textMessage.setText("Current server is LIVE \n"+Data.SERVER_URL+".\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
						textMessage.setText("Current server is DEV \n"+Data.SERVER_URL+".\nChange to:");
					}
					
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
					btnOk.setText("LIVE");
					
					Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral); btnNeutral.setTypeface(Data.latoRegular(activity));
					btnNeutral.setText("DEV");
					
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
					btnCancel.setText("SALES");
					
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.LIVE_SERVER_URL);
							editor.commit();
							
							initializeServerURL(activity);
							
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
							
							initializeServerURL(activity);

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
							
							initializeServerURL(activity);

							dialog.dismiss();
						}
					});

					dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						}
					});

					frameLayout.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					
					
					
					dialog.show();
					Toast.makeText(activity, "SERVER_URL = "+Data.SERVER_URL, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
	
	

	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}
	
}
