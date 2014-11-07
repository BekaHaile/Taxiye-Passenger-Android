package product.clicklabs.jugnoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	
	AutoCompleteTextView emailEt;
	EditText passwordEt;
	Button signInBtn, forgotPasswordBtn, facebookSignInBtn;
	TextView extraTextForScroll;
	ImageView jugnooLogoBig;
	TextView orText;
	
	LinearLayout needHelpLinearLayout;
	TextView needHelpText, callUsText;
	
	RelativeLayout signupRl;
	TextView newToJugnooText, signupText;
	
	
	
	LinearLayout relative;
	
	boolean loginDataFetched = false, facebookRegister = false;
	

	public static Session session;
	
	
	public static boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}
	
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
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
		
		emailEt = (AutoCompleteTextView) findViewById(R.id.emailEt); emailEt.setTypeface(Data.regularFont(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		signInBtn = (Button) findViewById(R.id.signInBtn); signInBtn.setTypeface(Data.regularFont(getApplicationContext()));
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn); forgotPasswordBtn.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn); facebookSignInBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);
		
		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Data.regularFont(getApplicationContext()));
		
		needHelpLinearLayout = (LinearLayout) findViewById(R.id.needHelpLinearLayout);
		needHelpText = (TextView) findViewById(R.id.needHelpText); needHelpText.setTypeface(Data.regularFont(SplashLogin.this), Typeface.BOLD);
		callUsText = (TextView) findViewById(R.id.callUsText); callUsText.setTypeface(Data.regularFont(SplashLogin.this), Typeface.BOLD);
		
		callUsText.setText("Call us now "+jugnooPhoneNumber);
		
		
		
		signupRl = (RelativeLayout) findViewById(R.id.signupRl);
		newToJugnooText = (TextView) findViewById(R.id.newToJugnooText); newToJugnooText.setTypeface(Data.regularFont(SplashLogin.this));
		signupText = (TextView) findViewById(R.id.signupText); signupText.setTypeface(Data.regularFont(SplashLogin.this), Typeface.BOLD);
		
		
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
		
		
		
		signupRl.setOnClickListener(new View.OnClickListener() {
			
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
											@SuppressWarnings("deprecation")
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
																			Log.e("Data.userEmail before","="+Data.fbUserEmail);
																			if(Data.fbUserEmail == null && Data.fbUserName != null){
																				Data.fbUserEmail = Data.fbUserName + "@facebook.com";
																			}
																		} catch (Exception e2) {
																			e2.printStackTrace();
																		}
																		
																		if(Data.fbUserName == null){
																			Data.fbUserName = "";
																		}
																		
																		if(Data.fbUserEmail == null){
																			Data.fbUserEmail = "";
																		}
																		Log.e("Data.fbId","="+Data.fbId);
																		Log.e("Data.fbFirstName","="+Data.fbFirstName);
																		Log.e("Data.fbLastName","="+Data.fbLastName);
																		Log.e("Data.fbUserName","="+Data.fbUserName);
																		Log.e("Data.userEmail","="+Data.fbUserEmail);
																		sendFacebookLoginValues(SplashLogin.this, "");
																	}
																	else{
																		new DialogPopup().alertPopup(SplashLogin.this, "Facebook Error", "Error in fetching information from Facebook.");
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
		
		jugnooLogoBig = (ImageView) findViewById(R.id.jugnooLogoBig);
		jugnooLogoBig.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashLogin.this);
				return false;
			}
		});
		
		jugnooLogoBig.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashLogin.this, 1000, 1);
		}
		
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			showGooglePlayErrorAlert(SplashLogin.this);
		}
		else{
			showLocationSettingsAlert(SplashLogin.this);
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
	
	
	
	AlertDialog alertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showGooglePlayErrorAlert(final Activity mContext){
		try{
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Google Play Services Error");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Google Play services not found or outdated. Please install Google Play Services?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	dialog.dismiss();
		            	Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.google.android.gms"));
						mContext.startActivity(intent);
		            }
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	mContext.finish();
		            }
		        });
		 
		        alertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        alertDialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}
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
//											confirmOTPPopup(activity, 1);
//											new DialogPopup().alertPopup(activity, "", errorMessage);
											sendSignupValues(SplashLogin.this, enteredEmail, "4444");
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
//											confirmOTPPopup(activity, 0);
//											new DialogPopup().alertPopup(activity, "", errorMessage);
											sendFacebookLoginValues(SplashLogin.this, "4444");
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
	
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	@SuppressWarnings("unused")
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("Splash login ", "This device is not supported.");
	        }
	        return false;
	    }
	    return true;
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
	
	
	
	
			
	static AlertDialog locationAlertDialog;
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public static void showLocationSettingsAlert(final Context mContext){
		try{
			if(!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					&&
					!((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(locationAlertDialog != null && locationAlertDialog.isShowing()){
				locationAlertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Loaction Settings");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Location is not enabled. Do you want to enable it from settings menu?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		            	mContext.startActivity(intent);
		            	dialog.dismiss();
		            }
		        });
		 
		        locationAlertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        locationAlertDialog.show();
			}
		} catch(Exception e){
			e.printStackTrace();
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
