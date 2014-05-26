package product.clicklabs.jugnoo;

import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegisterScreen extends Activity{
	
	EditText firstNameEt, lastNameEt, emailIdEt, phoneNoEt, passwordEt, confirmPasswordEt;
	Button signUpBtn;
	TextView extraTextForScroll;
	
	LinearLayout relative;
	
	String firstName = "", lastName = "", emailId = "", phoneNo = "", password = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RegisterScreen.this, relative, 1134, 720, false);
		
		firstNameEt = (EditText) findViewById(R.id.firstNameEt);
		lastNameEt = (EditText) findViewById(R.id.lastNameEt);
		emailIdEt = (EditText) findViewById(R.id.emailIdEt);
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		passwordEt = (EditText) findViewById(R.id.passwordEt);
		confirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt);
		
		signUpBtn = (Button) findViewById(R.id.signUpBtn);
		
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
				String phoneNo = phoneNoEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				String confirmPassword = confirmPasswordEt.getText().toString().trim();
				
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
													Toast.makeText(getApplicationContext(), "bingo", Toast.LENGTH_SHORT).show();
													sendSignupValues(RegisterScreen.this, firstName, lastName, emailId, phoneNo, password, "");
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
			
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/customer_registeration", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@SuppressWarnings("unused")
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(2 == flag){ // {"error": "email already registered","flag":2}/error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(0 == flag){ // {"error": 'Please enter otp',"flag":0} //error
										RegisterScreen.this.firstName = firstName;
										RegisterScreen.this.lastName = lastName;
										RegisterScreen.this.emailId = emailId;
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										confirmOTPPopup(activity);
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(1 == flag){ // {"error": 'Incorrect verification code',"flag":1}
										RegisterScreen.this.firstName = firstName;
										RegisterScreen.this.lastName = lastName;
										RegisterScreen.this.emailId = emailId;
										RegisterScreen.this.phoneNo = phoneNo;
										RegisterScreen.this.password = password;
										confirmOTPPopup(activity);
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
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
					sendSignupValues(RegisterScreen.this, firstName, lastName, emailId, phoneNo, password, etCode.getText().toString());
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
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}