package product.clicklabs.jugnoo;

import java.util.Locale;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SplashLogin extends Activity{
	
	EditText emailEt, passwordEt;
	Button signInBtn, forgotPasswordBtn, signupBtn, facebookSignInBtn;
	TextView extraTextForScroll;
	
	LinearLayout relative;
	
	boolean loginDataFetched = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_login);
		
		loginDataFetched = false;
		
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
				startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
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
			
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
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
			Log.i("device_token", "=" + Data.deviceToken);
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
									
									
									JSONObject userData = jObj.getJSONObject("user_data");
									
									Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
											userData.getString("user_image"));
									
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		else{
			
		}
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}
