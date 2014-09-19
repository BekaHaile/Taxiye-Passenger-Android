package product.clicklabs.jugnoo;

import org.apache.http.Header;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegisterScreen extends Activity implements LocationUpdate{
	
	EditText firstNameEt, lastNameEt, emailIdEt, phoneNoEt, passwordEt, confirmPasswordEt;
	Button signUpBtn;
	TextView extraTextForScroll;
	
	LinearLayout relative;
	
	String firstName = "", lastName = "", emailId = "", phoneNo = "", password = "";
	
	public static boolean facebookLogin = false;
	boolean loginDataFetched = false, showOtpDialog = false;
	String otpAlertString = "";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		
		loginDataFetched = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RegisterScreen.this, relative, 1134, 720, false);
		
		firstNameEt = (EditText) findViewById(R.id.firstNameEt); firstNameEt.setTypeface(Data.regularFont(getApplicationContext()));
		lastNameEt = (EditText) findViewById(R.id.lastNameEt); lastNameEt.setTypeface(Data.regularFont(getApplicationContext()));
		emailIdEt = (EditText) findViewById(R.id.emailIdEt); emailIdEt.setTypeface(Data.regularFont(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt); phoneNoEt.setTypeface(Data.regularFont(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.regularFont(getApplicationContext()));
		confirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt); confirmPasswordEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		signUpBtn = (Button) findViewById(R.id.signUpBtn); signUpBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);

		
		

		firstNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				firstNameEt.setError(null);
			}
		});
		
		lastNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				lastNameEt.setError(null);
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
				
				String firstName = firstNameEt.getText().toString().trim();
				if(firstName.length() > 0){
					firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1, firstName.length());
				}
				String lastName = lastNameEt.getText().toString().trim();
				String emailId = emailIdEt.getText().toString().trim();
				boolean noFbEmail = false;
				
				if(facebookLogin && emailId.equalsIgnoreCase("")){
					emailId = "n@n.c";
					noFbEmail = true;
				}
				
				
				
				String phoneNo = phoneNoEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				String confirmPassword = confirmPasswordEt.getText().toString().trim();
				
				//TODO remove extra characters phoneNo
				try{
					//     (/)N,*;#-.
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
					
					
					if(phoneNo.length() > 2){
						if(phoneNo.charAt(0) != '+'){
							if(phoneNo.charAt(0) == '0' && phoneNo.charAt(1) == '0'){
								phoneNo = "+" + GetCountryZipCode() + phoneNo.substring(2);
							}
							else if(phoneNo.charAt(0) == '0'){
								phoneNo = "+" + GetCountryZipCode() + phoneNo.substring(1);
							}
							else{
								phoneNo = "+" + GetCountryZipCode() + phoneNo;
							}
						}
					}
					
				} catch(Exception e){
					e.printStackTrace();
				}
				
				
				if("".equalsIgnoreCase(firstName)){
					firstNameEt.requestFocus();
					firstNameEt.setError("Please enter firstname");
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
														sendFacebookSignupValues(RegisterScreen.this, "", phoneNo, password);
													}
													else{
														sendSignupValues(RegisterScreen.this, firstName, lastName, emailId, phoneNo, password, "");
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
				}
				
			}
		});
		
		
		confirmPasswordEt.setOnEditorActionListener(new OnEditorActionListener() {

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
			firstNameEt.setText(Data.fbFirstName);
			lastNameEt.setText(Data.fbLastName);
			emailIdEt.setText(Data.fbUserEmail);
			
			firstNameEt.setEnabled(false);
			lastNameEt.setEnabled(false);
			emailIdEt.setEnabled(false);
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
		

		
//		phoneNoEt.setText("+"+GetCountryZipCode());
		
//		Toast.makeText(getApplicationContext(), ""+GetCountryZipCode(), Toast.LENGTH_LONG).show();
		
		
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
	public void sendSignupValues(final Activity activity, final String firstName, final String lastName, 
			final String emailId, final String phoneNo, final String password, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_name", firstName + " " + lastName);
			params.put("ph_no", phoneNo);
			params.put("email", emailId);
			params.put("password", password);
			params.put("otp", otp);
			params.put("device_type", "0");
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);

			Log.i("user_name", "=" + firstName + " " + lastName);
			Log.i("ph_no", "=" + phoneNo);
			Log.i("email", "=" + emailId);
			Log.i("password", "=" + password);
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
										RegisterScreen.this.firstName = firstName;
										RegisterScreen.this.lastName = lastName;
										RegisterScreen.this.emailId = emailId;
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										otpAlertString = errorMessage;
										showOtpDialog = true;
									}
									else if(1 == flag){ // {"error": 'Incorrect verification code',"flag":1}
										RegisterScreen.this.firstName = firstName;
										RegisterScreen.this.lastName = lastName;
										RegisterScreen.this.emailId = emailId;
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										otpAlertString = errorMessage;
										showOtpDialog = true;
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
						if(facebookLogin){
							sendFacebookSignupValues(RegisterScreen.this, code, phoneNo, password);
						}
						else{
							sendSignupValues(RegisterScreen.this, firstName, lastName, emailId, phoneNo, password, code);
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
	 * ASync for login from server
	 */
	public void sendFacebookSignupValues(final Activity activity, String otp, final String phoneNo, final String password) {
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
			params.put("ph_no", phoneNo);
			params.put("password", password);
			

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
			Log.i("otp", "="+otp);
			Log.i("ph_no", "="+phoneNo);
			Log.i("password", "="+password);
			
			
		
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
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								

								boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
								
								if(!newUpdate){
								
								if(!jObj.isNull("error")){
									
//									{"error": 'Some parameter missing',"flag":0} //error
//									{"error": 'Not An Authenticated User!',"flag":1}
//									{"error": 'Please enter otp',"flag":2}  
//									{"error": 'Please enter details',"flag":3}
//								{"error": 'Message sending failed',"flag":4}
//								{"error": 'User not registered',"flag":5}
//								{"error": 'Incorrect verification code',"flag":6}
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(2 == flag){ // {"error": 'Please enter otp',"flag":2}  
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										otpAlertString = errorMessage;
										showOtpDialog = true;
									}
									else if(6 == flag){ // {"error": 'Incorrect verification code',"flag":6}
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										otpAlertString = errorMessage;
										showOtpDialog = true;
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
	
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			startActivity(new Intent(RegisterScreen.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		else if(hasFocus && showOtpDialog){
			showOtpDialog = false;
			confirmOTPPopup(RegisterScreen.this);
			new DialogPopup().alertPopup(RegisterScreen.this, "", otpAlertString);
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
		startActivity(new Intent(RegisterScreen.this, SplashLogin.class));
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		finish();
		super.onBackPressed();
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
		
	}
	
}
