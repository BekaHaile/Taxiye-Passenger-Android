package product.clicklabs.jugnoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashLogin extends Activity implements LocationUpdate{
	
	TextView title;
	Button backBtn;
	AutoCompleteTextView emailEt;
	EditText passwordEt;
	Button signInBtn, forgotPasswordBtn, facebookSignInBtn;
	TextView extraTextForScroll;
	TextView orText;
	
	LinearLayout needHelpLinearLayout;
	TextView needHelpText, callUsText;
	
	
	
	LinearLayout relative;
	
	boolean loginDataFetched = false, facebookRegister = false;
	

	
	public static boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}
	
	
	
	GoogleCloudMessaging gcm;
	
	String enteredEmail = "";
	
	String jugnooPhoneNumber = "+918556921929";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_login);
		
		loginDataFetched = false;
		facebookRegister = false;
		
		enteredEmail = "";
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);
		
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		emailEt = (AutoCompleteTextView) findViewById(R.id.emailEt); emailEt.setTypeface(Data.regularFont(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		signInBtn = (Button) findViewById(R.id.signInBtn); signInBtn.setTypeface(Data.regularFont(getApplicationContext()));
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn); forgotPasswordBtn.setTypeface(Data.regularFont(getApplicationContext()));
		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn); facebookSignInBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);
		
		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Data.regularFont(getApplicationContext()));
		
		needHelpLinearLayout = (LinearLayout) findViewById(R.id.needHelpLinearLayout);
		needHelpText = (TextView) findViewById(R.id.needHelpText); needHelpText.setTypeface(Data.regularFont(SplashLogin.this));
		callUsText = (TextView) findViewById(R.id.callUsText); callUsText.setTypeface(Data.regularFont(SplashLogin.this));
		
		callUsText.setText("Call us now "+jugnooPhoneNumber);
		
		
		
		
		Database database = new Database(SplashLogin.this);													// getting already logged in email strings for drop down
		String[] emails = database.getEmails();
		database.close();
		
		Database2 database2 = new Database2(SplashLogin.this);
		database2.updateDriverServiceFast("no");
		database2.close();
		
		ArrayAdapter<String> adapter;
		
		if (emails == null) {																			// if emails from database are not null
			emails = new String[]{};
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		} else {																						// else reinitializing emails to be empty
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		}
		
		adapter.setDropDownViewResource(R.layout.dropdown_textview);					// setting email array to EditText DropDown list
		emailEt.setAdapter(adapter);
		
		
		
		signInBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = emailEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				if("".equalsIgnoreCase(email)){
					emailEt.requestFocus();
					emailEt.setError("Please enter email");
				}
				else{
					if("".equalsIgnoreCase(password)){
						passwordEt.requestFocus();
						passwordEt.setError("Please enter password");
					}
					else{
						if(isEmailValid(email)){
							enteredEmail = email;
							sendLoginValues(SplashLogin.this, email, password);
						}
						else{
							emailEt.requestFocus();
							emailEt.setError("Please enter valid email");
						}
					}
				}
			}
		});
		
		
		
		
		forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ForgotPasswordScreen.emailAlready = emailEt.getText().toString();
				startActivity(new Intent(SplashLogin.this, ForgotPasswordScreen.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		needHelpLinearLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+jugnooPhoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		
		passwordEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						signInBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new FacebookLogin().openFacebookSession(SplashLogin.this, facebookLoginCallback);
			}
		});
		
		
		
		
		final View activityRootView = findViewById(R.id.mainLinear);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							/************** Adapter for the parent List *************/

							ViewGroup.LayoutParams params_12 = extraTextForScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							extraTextForScroll.setLayoutParams(params_12);
							extraTextForScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = extraTextForScroll
									.getLayoutParams();
							params.height = 0;
							extraTextForScroll.setLayoutParams(params);
							extraTextForScroll.requestLayout();

						}
					}
				});
		
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = pInfo.versionCode;
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = getApplicationContext().getResources().getConfiguration().locale.getCountry();
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
			Log.i("deviceName", Data.deviceName + "..");
			gcm = GoogleCloudMessaging.getInstance(this);
			Data.deviceToken = getRegistrationId(this);
	
		    Log.i("deviceToken", Data.deviceToken + "..");
		    
		    if (Data.deviceToken.isEmpty()) {
		        registerInBackground();
		    }
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
	}
	
	FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
		@Override
		public void facebookLoginDone() {
			sendFacebookLoginValues(SplashLogin.this, "");
		}
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashLogin.this, 1000, 1);
		}
		
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			new DialogPopup().showGooglePlayErrorAlert(SplashLogin.this);
		}
		else{
			new DialogPopup().showLocationSettingsAlert(SplashLogin.this);
		}
		
	}
	
	@Override
	protected void onPause() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
		
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(Session.getActiveSession() != null){
						Session.getActiveSession().closeAndClearTokenInformation();
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		Intent intent = new Intent(SplashLogin.this, SplashNewActivity.class);
		intent.putExtra("no_anim", "yes");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
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
	
	
	
	
	private void registerInBackground() {
	    new AsyncTask<String, Integer, String>() {
	        @Override
	        protected String doInBackground(String... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(SplashLogin.this);
	                }
	                Data.deviceToken = gcm.register(Data.GOOGLE_PROJECT_ID);
	                msg = "Device registered, registration ID=" + Data.deviceToken;
	                
	                setRegistrationId(SplashLogin.this, Data.deviceToken);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	Log.e("msg  ===== ","="+msg);
	        }
	    }.execute(null, null, null);
	}
	

	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}
			
			params.put("email", emailId);
			params.put("password", password);
			params.put("device_type", "0");
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);

			Log.i("email", "=" + emailId);
			Log.i("password", "=" + password);
			Log.e("device_token", "=" + Data.deviceToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			Log.i("country", "=" + Data.country);
			Log.i("device_name", "=" + Data.deviceName);
			Log.i("app_version", "=" + Data.appVersion);
			Log.i("os_version", "=" + Data.osVersion);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/email_login", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
								
								if(!newUpdate){
									
									if(!jObj.isNull("error")){
										DialogPopup.dismissLoadingDialog();
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
										else if(2 == flag){ // {"error":"incorrect password","flag":2}//error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(3 == flag){ // {"error":"enter otp","flag":2}//error
											confirmOTPPopup(activity, 1);
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										
									}
									else{
										
										new JSONParser().parseLoginData(activity, response);
										
										Database database22 = new Database(SplashLogin.this);
										database22.insertEmail(emailId);
										database22.close();
										
										loginDataFetched = true;
										
										DialogPopup.dismissLoadingDialog();
										
									}
								}
								else{
									DialogPopup.dismissLoadingDialog();
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
	
	
	void confirmOTPPopup(Activity activity, final int flag){

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
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(getApplicationContext()));
			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.regularFont(getApplicationContext()));
			
			
			final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.regularFont(getApplicationContext()));
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(getApplicationContext()));
			
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String code = etCode.getText().toString().trim();
					if("".equalsIgnoreCase(code)){
						etCode.requestFocus();
						etCode.setError("Code can't be empty.");
					}
					else{
						dialog.dismiss();
						if(flag == 0){
							sendFacebookLoginValues(SplashLogin.this, code);
						}
						else{
							sendSignupValues(SplashLogin.this, enteredEmail, code);
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
	
	
	
	/**
	 * ASync for register from server
	 */
	public void sendSignupValues(final Activity activity, final String emailId, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_name", "");
			params.put("ph_no", "");
			params.put("email", emailId);
			params.put("password", "");
			params.put("otp", otp);
			params.put("device_type", "0");
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("referral_code", "");

			Log.i("email", "=" + emailId);
			Log.i("device_token", "=" + Data.deviceToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			Log.i("country", "=" + Data.country);
			Log.i("device_name", "=" + Data.deviceName);
			Log.i("app_version", "=" + Data.appVersion);
			Log.i("os_version", "=" + Data.osVersion);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/customer_registeration", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
								
								if(!newUpdate){
									
									if(!jObj.isNull("error")){
										
										int flag = jObj.getInt("flag");	
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else if(2 == flag){ // {"error": "email already registered","flag":2}/error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(0 == flag){ // {"error": 'Please enter otp',"flag":0} //error
											confirmOTPPopup(activity, 1);
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(1 == flag){ // {"error": 'Incorrect verification code',"flag":1}
											confirmOTPPopup(activity, 1);
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										DialogPopup.dismissLoadingDialog();
									}
									else{
										new JSONParser().parseLoginData(activity, response);
										loginDataFetched = true;
										
										DialogPopup.dismissLoadingDialog();
									}
								}
								else{
									DialogPopup.dismissLoadingDialog();
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
	
	
	/**
	 * ASync for login from server
	 */
	public void sendFacebookLoginValues(final Activity activity, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude(); 
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_fb_id", Data.fbId);
			params.put("user_fb_name", Data.fbFirstName + " " + Data.fbLastName);
			params.put("fb_access_token", Data.fbAccessToken);
			params.put("username", Data.fbUserName);
			params.put("fb_mail", Data.fbUserEmail);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("device_token", Data.deviceToken);
			params.put("country", Data.country);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("device_name", Data.deviceName);
			params.put("device_type", "0");
			params.put("otp", otp);
			params.put("ph_no", "");
			params.put("password", "");
			params.put("referral_code", "");
			
			
			

			Log.i("user_fb_id", "="+Data.fbId);
			Log.i("user_fb_name", "="+Data.fbFirstName + " " + Data.fbLastName);
			Log.i("fb_access_token", "="+Data.fbAccessToken);
			Log.i("username", "="+Data.fbUserName);
			Log.i("fb_mail", "="+Data.fbUserEmail);
			Log.i("latitude", "="+Data.latitude);
			Log.i("longitude", "="+Data.longitude);
			Log.i("device_token in fb login", "="+Data.deviceToken);
			Log.i("country", "="+Data.country);
			Log.i("app_version", "="+Data.appVersion);
			Log.i("os_version", "="+Data.osVersion);
			Log.i("device_name", "="+Data.deviceName);
			Log.i("device_type", "="+"0");
			Log.i("Server link", "="+Data.SERVER_URL + "/customer_fb_registeration_form");
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/customer_fb_registeration_form", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.e("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);

								boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
								
								if(!newUpdate){
										
									if(!jObj.isNull("error")){
										DialogPopup.dismissLoadingDialog();
	//									{"error": 'Some parameter missing',"flag":0} //error
	//									{"error": 'Not An Authenticated User!',"flag":1}
	//									{"error": 'Please enter otp',"flag":2}  
	//								{"error": 'Please enter details',"flag":3}
	//								{"error": 'Message sending failed',"flag":4}
	//								{"error": 'User not registered',"flag":5}
	//								{"error": 'Incorrect verification code',"flag":6}
										
										int flag = jObj.getInt("flag");	
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else if(2 == flag){ // {"error": 'Please enter otp',"flag":2}  
											confirmOTPPopup(activity, 0);
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(3 == flag){ // {"error": 'Please enter details',"flag":3}
											facebookRegister = true;
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										
									}
									else{
										
										new JSONParser().parseLoginData(activity, response);
										loginDataFetched = true;
										
										DialogPopup.dismissLoadingDialog();
										
									}
								}
								else{
									DialogPopup.dismissLoadingDialog();
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
	
	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			
			Database2 database2 = new Database2(SplashLogin.this);
	        database2.updateDriverLastLocationTime();
	        database2.close();
	        
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("username", Data.userData.userName);
			FlurryAgent.logEvent("App Login", articleParams);
			
			loginDataFetched = false;
//			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			if(Data.termsAgreed == 1){
				startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			}
			else{
				startActivity(new Intent(SplashLogin.this, TermsConditionsActivity.class));
			}
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && facebookRegister){
			facebookRegister = false;
			RegisterScreen.facebookLogin = true;
			startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
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
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Application started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onLocationChanged(Location location, int priority) {
		new DriverLocationDispatcher().saveLocationToDatabase(SplashLogin.this, location);
	}
	
}
