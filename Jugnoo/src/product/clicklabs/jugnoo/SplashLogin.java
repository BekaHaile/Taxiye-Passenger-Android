package product.clicklabs.jugnoo;

import java.util.Arrays;
import java.util.Locale;

import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashLogin extends Activity{
	
	EditText emailEt, passwordEt;
	Button signInBtn, forgotPasswordBtn, signupBtn, facebookSignInBtn;
	TextView extraTextForScroll;
	
	LinearLayout relative;
	
	boolean loginDataFetched = false, facebookRegister = false;
	

	public static Session session;
	
	
	public static boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_login);
		
		loginDataFetched = false;
		facebookRegister = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);
		
		emailEt = (EditText) findViewById(R.id.emailEt);
		passwordEt = (EditText) findViewById(R.id.passwordEt);
		
		signInBtn = (Button) findViewById(R.id.signInBtn);
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
		signupBtn = (Button) findViewById(R.id.signupBtn);
		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn);
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);
		
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
							sendLoginValues(SplashLogin.this, email, password);
						}
						else{
							emailEt.requestFocus();
							emailEt.setError("Please enter valid email");
						}
					}
				}
				
//				
				
			}
		});
		
		
		
		signupBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegisterScreen.facebookLogin = false;
				startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
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
				
				
				if (!AppStatus.getInstance(SplashLogin.this).isOnline(
						SplashLogin.this)) {
					new DialogPopup().alertPopup(SplashLogin.this, "", Data.CHECK_INTERNET_MSG);
				} else {
					Log.i(" connection", " connection");
					session = new Session(SplashLogin.this);
					Session.setActiveSession(session);
					Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);

					Session.OpenRequest openRequest = null;
					openRequest = new Session.OpenRequest(SplashLogin.this);
					openRequest.setPermissions(Arrays.asList("email", "user_friends", "user_photos"));

					try {
						if (SplashLogin.isSystemPackage(getPackageManager().getPackageInfo("com.facebook.katana", 0))) {
							openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
						} else {
							openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}

					openRequest.setCallback(new Session.StatusCallback() {
						@Override
						public void call(Session session, SessionState state, Exception exception) {

							if (session.isOpened()) {
								Session.openActiveSession(SplashLogin.this, true, new Session.StatusCallback() {
											@Override
											public void call(final Session session, SessionState state, Exception exception) {
												Log.v("session.isOpened()", "" + session.isOpened());
												Log.v("app id", "" + session.getApplicationId());
												if (session.isOpened()) {
													Log.e("heyyy", "Logged in..." + session.getAccessToken());

													Data.fbAccessToken = session.getAccessToken();
													Log.e("fbAccessToken===", "="+Data.fbAccessToken);
											    	
													
													DialogPopup.showLoadingDialog(SplashLogin.this, "Loading...");
													

													Request.executeMeRequestAsync(session,
															new Request.GraphUserCallback() {
																@Override
																public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook

																	DialogPopup.dismissLoadingDialog();
																	
																	if (user != null) {
																		Log.i("data", "username" + user.getName() + "fbid!" + user.getId() + " firstname "
																						+ user.getFirstName() + " lastname " + user.getLastName() + "  ");
																		Log.i("res", response.toString());
																		Log.i("user", "User " + user);
																		
																		Data.fbId = user.getId();
																		Data.fbFirstName = user.getFirstName();
																		Data.fbLastName = user.getLastName();
																		Data.fbUserName = user.getUsername();
																		
																		try {
																			Data.fbUserEmail = ((String)user.asMap().get("email"));
																			if("".equalsIgnoreCase(Data.fbUserEmail)){
																				Data.fbUserEmail = user.getUsername() + "@facebook.com";
																			}
																		} catch (Exception e2) {
																			Data.fbUserEmail = user.getUsername() + "@facebook.com";
																			e2.printStackTrace();
																		}

																		Log.e("Data.fbId","="+Data.fbId);
																		Log.e("Data.fbFirstName","="+Data.fbFirstName);
																		Log.e("Data.fbLastName","="+Data.fbLastName);
																		Log.e("Data.fbUserName","="+Data.fbUserName);
																		Log.e("Data.userEmail","="+Data.fbUserEmail);

																		
																		sendFacebookLoginValues(SplashLogin.this, "");
																		
																	}
																	else{
																		new DialogPopup().alertPopup(SplashLogin.this, "", "Error: Error in fetching information from Facebook.");
																	}
																	

																}
															});
												}
												else if (session.isClosed()) {
													Log.e("heyy", "Logged out...");

													DialogPopup.dismissLoadingDialog();
												}
											}
										});

							} else if (session.isClosed()) {
								
							}
							
							
						}
					});
					session.openForRead(openRequest);
				}
				
				
				
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

							params_12.height = (int)(370.0f*ASSL.Yscale());

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
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		emailEt.setText("tirthankar@clicklabs.in");
		passwordEt.setText("tirthankar");
		
		
		Data.locationFetcher = new LocationFetcher(SplashLogin.this);
		
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
			
			Data.registerForGCM(SplashLogin.this);
			
			Data.generateKeyHash(SplashLogin.this);
			
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		
		
		accessTokenLogin(SplashLogin.this);
		
		
	}
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		final String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		if(!"".equalsIgnoreCase(accessToken)){
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);

				Log.i("accessToken", "=" + accessToken);
			
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(Data.SERVER_TIMEOUT);
				client.post(Data.SERVER_URL + "/access_token", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
		
							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										
//										{"error":"some parameter missing","flag":0}//error
//										{"error":"invalid access token","flag":1}//error

										int flag = jObj.getInt("flag");	
										String errorMessage = jObj.getString("error");
										
										if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(1 == flag){ // {"error":"email not  registered","flag":1}//error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
										new JSONParser().parseAccessTokenLoginData(response, accessToken);
										
										loginDataFetched = true;
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
		
							@Override
							public void onFailure(Throwable arg0) {
								Log.e("request fail", arg0.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}

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
			
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/email_login", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(1 == flag){ // {"error":"email not  registered","flag":1}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(2 == flag){ // {"error":"incorrect password","flag":2}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									new JSONParser().parseLoginData(activity, response);
									
									loginDataFetched = true;
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	void confirmOTPPopup(Activity activity){

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
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode);
			
			
			Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn);
			
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					sendFacebookLoginValues(SplashLogin.this, etCode.getText().toString());
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
			

			Log.i("user_fb_id", "="+Data.fbId);
			Log.i("user_fb_name", "="+Data.fbFirstName + " " + Data.fbLastName);
			Log.i("fb_access_token", "="+Data.fbAccessToken);
			Log.i("username", "="+Data.fbUserName);
			Log.i("fb_mail", "="+Data.fbUserEmail);
			Log.i("latitude", "="+Data.latitude);
			Log.i("longitude", "="+Data.longitude);
			Log.i("device_token", "="+Data.deviceToken);
			Log.i("country", "="+Data.country);
			Log.i("app_version", "="+Data.appVersion);
			Log.i("os_version", "="+Data.osVersion);
			Log.i("device_name", "="+Data.deviceName);
			Log.i("device_type", "="+"0");
			
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/customer_fb_registeration_form", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
//									{"error": 'Some parameter missing',"flag":0} //error
//									{"error": 'Not An Authenticated User!',"flag":1}
//									{"error": 'Please enter otp',"flag":2}  
//							{"error": 'Please enter details',"flag":3}
//								{"error": 'Message sending failed',"flag":4}
//								{"error": 'User not registered',"flag":5}
//								{"error": 'Incorrect verification code',"flag":6}
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(2 == flag){ // {"error": 'Please enter otp',"flag":2}  
										confirmOTPPopup(activity);
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
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
			loginDataFetched = false;
			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		else if(hasFocus && facebookRegister){
			facebookRegister = false;
			RegisterScreen.facebookLogin = true;
			startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
			
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
//		try {	
//			Session session = new Session(SplashLogin.this);
//			Session.setActiveSession(session);	
//			session.closeAndClearTokenInformation();	
//		}
//		catch(Exception e) {
//			Log.v("Logout", "Error"+e);	
//		}
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}
