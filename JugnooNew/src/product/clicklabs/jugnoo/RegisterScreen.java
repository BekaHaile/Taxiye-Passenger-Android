package product.clicklabs.jugnoo;

import org.apache.http.Header;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegisterScreen extends Activity implements LocationUpdate{
	
	Button backBtn;
	TextView title;
	
	Button registerWithFacebookBtn;
	TextView orText;
	
	EditText nameEt, referralCodeEt, emailIdEt, phoneNoEt, passwordEt, confirmPasswordEt;
	Button signUpBtn;
	TextView extraTextForScroll;
	ScrollView scroll;
	
	LinearLayout relative;
	
	String name = "", referralCode = "", emailId = "", phoneNo = "", password = "";
	
	public static boolean facebookLogin = false;
	boolean loginDataFetched = false, sendToOtpScreen = false;
	int otpFlag = 0;
	
	public void resetFlags(){
		loginDataFetched = false;
		sendToOtpScreen = false;
		otpFlag = 0;
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Register started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		
		loginDataFetched = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RegisterScreen.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.regularFont(getApplicationContext()));
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		registerWithFacebookBtn = (Button) findViewById(R.id.registerWithFacebookBtn); registerWithFacebookBtn.setTypeface(Data.regularFont(getApplicationContext()));
		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Data.regularFont(getApplicationContext()));
		
		scroll = (ScrollView) findViewById(R.id.scroll);
		
		nameEt = (EditText) findViewById(R.id.nameEt); nameEt.setTypeface(Data.regularFont(getApplicationContext()));
		referralCodeEt = (EditText) findViewById(R.id.referralCodeEt); referralCodeEt.setTypeface(Data.regularFont(getApplicationContext()));
		emailIdEt = (EditText) findViewById(R.id.emailIdEt); emailIdEt.setTypeface(Data.regularFont(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt); phoneNoEt.setTypeface(Data.regularFont(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.regularFont(getApplicationContext()));
		confirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt); confirmPasswordEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		signUpBtn = (Button) findViewById(R.id.signUpBtn); signUpBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);

		
		

		registerWithFacebookBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new FacebookLogin().openFacebookSession(RegisterScreen.this, facebookLoginCallback, true);
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				nameEt.setError(null);
				
			}
		});
		
		
		emailIdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				emailIdEt.setError(null);
			}
		});
		
		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

		passwordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				passwordEt.setError(null);
			}
		});

		confirmPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				confirmPasswordEt.setError(null);
			}
		});
		
		
		
		
		signUpBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String name = nameEt.getText().toString().trim();
				if(name.length() > 0){
					name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
				}
				String referralCode = referralCodeEt.getText().toString().trim();
				String emailId = emailIdEt.getText().toString().trim();
				boolean noFbEmail = false;
				
				if(facebookLogin && emailId.equalsIgnoreCase("")){
					emailId = "n@n.c";
					noFbEmail = true;
				}
				
				
				
				String phoneNo = phoneNoEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				String confirmPassword = confirmPasswordEt.getText().toString().trim();

				
				
				if("".equalsIgnoreCase(name)){
					nameEt.requestFocus();
					nameEt.setError("Please enter name");
				}
				else{
					if("".equalsIgnoreCase(emailId)){
						emailIdEt.requestFocus();
						emailIdEt.setError("Please enter email id");
					}
					else{
						if("".equalsIgnoreCase(phoneNo)){
							phoneNoEt.requestFocus();
							phoneNoEt.setError("Please enter phone number");
						}
						else{
							//TODO remove extra characters phoneNo
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
								if(phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")){
									phoneNoEt.requestFocus();
									phoneNoEt.setError("Please enter valid phone number");
								}
								else{
									phoneNo = "+91" + phoneNo;
									
									if("".equalsIgnoreCase(password)){
										passwordEt.requestFocus();
										passwordEt.setError("Please enter password");
									}
									else{
										if("".equalsIgnoreCase(confirmPassword)){
											confirmPasswordEt.requestFocus();
											confirmPasswordEt.setError("Please confirm password");
										}
										else{
											if(isEmailValid(emailId)){
												if(isPhoneValid(phoneNo)){
													if(password.equals(confirmPassword)){
														if(password.length() >= 6){
															
															if(facebookLogin){
																if(noFbEmail){
																	emailId = "";
																}
																sendFacebookSignupValues(RegisterScreen.this, referralCode, phoneNo, password);
																FlurryEventLogger.facebookSignupClicked(Data.fbUserEmail);
																if(!"".equalsIgnoreCase(referralCode)){
																	FlurryEventLogger.referralCodeAtFBSignup(Data.fbUserEmail, referralCode);
																}
															}
															else{
																sendSignupValues(RegisterScreen.this, name, referralCode, emailId, phoneNo, password);
																FlurryEventLogger.emailSignupClicked(emailId);
																if(!"".equalsIgnoreCase(referralCode)){
																	FlurryEventLogger.referralCodeAtEmailSignup(emailId, referralCode);
																}
															}
															
															
														}
														else{
															passwordEt.requestFocus();
															passwordEt.setError("Password must be of atleast six characters");
														}
													}
													else{
														passwordEt.requestFocus();
														passwordEt.setError("Passwords does not match");
													}
												}
												else{
													phoneNoEt.requestFocus();
													phoneNoEt.setError("Please enter valid phone number");
												}
											}
											else{
												emailIdEt.requestFocus();
												emailIdEt.setError("Please enter valid email id");
											}
										}
									}
								}
							}
							else{
								phoneNoEt.requestFocus();
								phoneNoEt.setError("Please enter valid phone number");
							}
						}
					}
				}
				
			}
		});
		
		
		referralCodeEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						signUpBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		if(facebookLogin){
			nameEt.setText(Data.fbFirstName + " " + Data.fbLastName);
			nameEt.setEnabled(false);
			if("".equalsIgnoreCase(Data.fbUserEmail)){
				emailIdEt.setText("");
				emailIdEt.setEnabled(true);
			}
			else{
				emailIdEt.setText(Data.fbUserEmail);
				emailIdEt.setEnabled(false);
			}
		}
		
		try {
			if(getIntent().hasExtra("back_from_otp")){
				if(facebookLogin){
					referralCodeEt.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
					phoneNoEt.setText(OTPConfirmScreen.facebookRegisterData.phoneNo);
					passwordEt.setText(OTPConfirmScreen.facebookRegisterData.password);
					confirmPasswordEt.setText(OTPConfirmScreen.facebookRegisterData.password);
				}
				else{
					nameEt.setText(OTPConfirmScreen.emailRegisterData.name);
					emailIdEt.setText(OTPConfirmScreen.emailRegisterData.emailId);
					referralCodeEt.setText(OTPConfirmScreen.emailRegisterData.referralCode);
					phoneNoEt.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
					passwordEt.setText(OTPConfirmScreen.emailRegisterData.password);
					confirmPasswordEt.setText(OTPConfirmScreen.emailRegisterData.password);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
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
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		

		
//		nameEt.setText("Test");
//		lastNameEt.setText("Passenger84");
//		emailIdEt.setText("passenger84@click-labs.com");
//		phoneNoEt.setText("9999999999");
//		passwordEt.setText("passenger");
//		confirmPasswordEt.setText("passenger");
		
//		phoneNoEt.setText("+"+GetCountryZipCode());
		
//		Toast.makeText(getApplicationContext(), ""+GetCountryZipCode(), Toast.LENGTH_LONG).show();
		
		
	}
	
	FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
		@Override
		public void facebookLoginDone() {
			facebookLogin = true;
			nameEt.setText(Data.fbFirstName + " " + Data.fbLastName);
			emailIdEt.setText(Data.fbUserEmail);
			
			nameEt.setEnabled(false);
			emailIdEt.setEnabled(false);
			FlurryEventLogger.registerViaFBClicked(Data.fbId);
		}
	};

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
	
	
	String GetCountryZipCode() {

		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		Log.e("CountryID", "="+CountryID);
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				return CountryZipCode;
			}
		}
		return "";
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(RegisterScreen.this, 1000, 1);
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
	
	
	
	
	/**
	 * ASync for register from server
	 */
	public void sendSignupValues(final Activity activity, final String name, final String referralCode, 
			final String emailId, final String phoneNo, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_name", name);
			params.put("ph_no", phoneNo);
			params.put("email", emailId);
			params.put("password", password);
			params.put("otp", "");
			params.put("device_type", "0");
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("referral_code", referralCode);
			

			Log.i("user_name", "=" + name);
			Log.i("ph_no", "=" + phoneNo);
			Log.i("email", "=" + emailId);
			Log.i("password", "=" + password);
			Log.i("otp", "=" + "");
			Log.i("device_token", "=" + Data.deviceToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			Log.i("country", "=" + Data.country);
			Log.i("device_name", "=" + Data.deviceName);
			Log.i("app_version", "=" + Data.appVersion);
			Log.i("os_version", "=" + Data.osVersion);
			Log.i("referral_code", "="+referralCode);
			
			
		
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
							Log.i("Server response", "response = " + response);
	
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
									else if(0 == flag){ // {"error": 'Please enter otp',"flag":0} //error
										RegisterScreen.this.name = name;
										RegisterScreen.this.referralCode = referralCode;
										RegisterScreen.this.emailId = emailId;
										RegisterScreen.this.phoneNo = jObj.getString("phone_no");;
										RegisterScreen.this.password = password;
										otpFlag = 0;
										sendToOtpScreen = true;
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									new JSONParser().parseLoginData(activity, response);
									
									Database database22 = new Database(RegisterScreen.this);
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
	
	

	/**
	 * ASync for login from server
	 */
	public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
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
			params.put("otp", "");
			params.put("ph_no", phoneNo);
			params.put("password", password);
			params.put("referral_code", referralCode);
			

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
			Log.i("otp", "="+"");
			Log.i("ph_no", "="+phoneNo);
			Log.i("password", "="+password);
			Log.i("referral_code", "="+referralCode);
			
			
		
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
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							String response = new String(arg2);
							Log.i("Server response", "response = " + response);
	
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
										else if(2 == flag){ // {"error": 'Please enter otp',"flag":2} 
											RegisterScreen.this.referralCode = referralCode;
											RegisterScreen.this.phoneNo = jObj.getString("phone_no");
											RegisterScreen.this.password = password;
											otpFlag = 1;
											sendToOtpScreen = true;
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
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	/**
	 * Send intent to otp screen by making required data objects
	 *  flag 0 for email, 1 for Facebook
	 */
	public void sendIntentToOtpScreen(){
		if(0 == otpFlag){
			RegisterScreen.facebookLogin = false;
			OTPConfirmScreen.intentFromRegister = true;
			OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode);
			startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(1 == otpFlag){
			RegisterScreen.facebookLogin = true;
			OTPConfirmScreen.intentFromRegister = true;
			OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(phoneNo, password, referralCode);
			startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Database2 database2 = new Database2(RegisterScreen.this);
	        database2.updateDriverLastLocationTime();
	        database2.close();
//			startActivity(new Intent(RegisterScreen.this, HomeActivity.class));
			if(Data.termsAgreed == 1){
				startActivity(new Intent(RegisterScreen.this, HomeActivity.class));
			}
			else{
				startActivity(new Intent(RegisterScreen.this, TermsConditionsActivity.class));
			}
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		else if(hasFocus && sendToOtpScreen){
			sendIntentToOtpScreen();
		}
		
	}
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	
	boolean isPhoneValid(CharSequence phone) {
		return android.util.Patterns.PHONE.matcher(phone).matches();
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
		Intent intent = new Intent(RegisterScreen.this, SplashNewActivity.class);
		intent.putExtra("no_anim", "yes");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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


	@Override
	public void onLocationChanged(Location location, int priority) {
		// TODO Auto-generated method stub
		new DriverLocationDispatcher().saveLocationToDatabase(RegisterScreen.this, location);
	}
	
}
