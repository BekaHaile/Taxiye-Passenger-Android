package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class SplashLogin extends BaseActivity implements LocationUpdate, FlurryEventNames{

    RelativeLayout topRl;
	TextView textViewTitle;
	ImageView imageViewBack;
	
	Button buttonFacebookLogin;
	
	TextView orText;
	
	AutoCompleteTextView editTextEmail;
	EditText editTextPassword;
	
	Button buttonEmailLogin;
	TextView textViewForgotPassword;

	LinearLayout relative;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;




    CallbackManager callbackManager;
    FacebookLoginHelper facebookLoginHelper;


	
	boolean loginDataFetched = false, facebookRegister = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	int otpFlag = 0; 
	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "", accessToken = "";




	
	
	String enteredEmail = "";

	public static boolean phoneNoLogin = false;
	
	
	public void resetFlags(){
		loginDataFetched = false;
		facebookRegister = false;
		sendToOtpScreen = false;
		otpFlag = 0;
		phoneNoOfUnverifiedAccount = "";
		otpErrorMsg = "";
		notRegisteredMsg = "";
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
		FlurryAgent.onEvent("Login started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_login);
		
		resetFlags();
		
		enteredEmail = "";
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);


        topRl = (RelativeLayout) findViewById(R.id.topRl);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin); buttonFacebookLogin.setTypeface(Fonts.latoRegular(this));

		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Fonts.latoRegular(this));
		
		editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Fonts.latoRegular(this));
		editTextPassword = (EditText) findViewById(R.id.editTextPassword); editTextPassword.setTypeface(Fonts.latoRegular(this));
		
		buttonEmailLogin = (Button) findViewById(R.id.buttonEmailLogin); buttonEmailLogin.setTypeface(Fonts.latoRegular(getApplicationContext()));
		textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword); textViewForgotPassword.setTypeface(Fonts.latoRegular(getApplicationContext()));


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);


		
		String[] emails = Database.getInstance(this).getEmails();
		Database.getInstance(this).close();
		
		Database2.getInstance(SplashLogin.this).updateDriverServiceFast("no");
		Database2.getInstance(SplashLogin.this).close();
		
		ArrayAdapter<String> adapter;
		
		if (emails == null) {																			// if emails from database are not null
			emails = new String[]{};
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		} else {																						// else reinitializing emails to be empty
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		}
		
		adapter.setDropDownViewResource(R.layout.dropdown_textview);					// setting email array to EditText DropDown list
		editTextEmail.setAdapter(adapter);
		
		
		
		buttonEmailLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if ("".equalsIgnoreCase(email)) {
                    editTextEmail.requestFocus();
                    editTextEmail.setError("Please enter email or phone number");
                } else {
                    if ("".equalsIgnoreCase(password)) {
                        editTextPassword.requestFocus();
                        editTextPassword.setError("Please enter password");
                    } else {
                        boolean onlyDigits = Utils.checkIfOnlyDigits(email);
                        if (onlyDigits) {
                            email = Utils.retrievePhoneNumberTenChars(email);
                            if (!Utils.validPhoneNumber(email)) {
                                editTextEmail.requestFocus();
                                editTextEmail.setError("Please enter valid phone number");
                            } else {
                                email = "+91" + email;
                                sendLoginValues(SplashLogin.this, email, password, true);
								phoneNoLogin = true;
                            }
                        } else {
                            if (isEmailValid(email)) {
                                enteredEmail = email;
                                sendLoginValues(SplashLogin.this, email, password, false);
								phoneNoLogin = false;
                            } else {
                                editTextEmail.requestFocus();
                                editTextEmail.setError("Please enter valid email");
                            }
                        }

                        FlurryEventLogger.event(LOGIN_VIA_EMAIL);
                    }
                }
            }
        });


        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Utils.checkIfOnlyDigits(s.toString())) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(10);
                    editTextEmail.setFilters(fArray);
                } else {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(1000);
                    editTextEmail.setFilters(fArray);
                }
            }
        });
		
		
		
		
		textViewForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
                ForgotPasswordScreen.emailAlready = editTextEmail.getText().toString();
                startActivity(new Intent(SplashLogin.this, ForgotPasswordScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
                FlurryEventLogger.event(FORGOT_PASSWORD);
            }
        });
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
                performBackPressed();
            }
        });


        editTextEmail.setOnFocusChangeListener(onFocusChangeListener);
        editTextPassword.setOnFocusChangeListener(onFocusChangeListener);

        editTextEmail.setOnClickListener(onClickListener);
        editTextPassword.setOnClickListener(onClickListener);

        editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        editTextPassword.requestFocus();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        editTextPassword.requestFocus();
                        break;

                    default:
                }
                return true;
            }
        });

		editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonEmailLogin.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });
		
		
		buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(LOGIN_VIA_FACEBOOK);
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
                loginDataFetched = false;
                facebookLoginHelper.openFacebookSession();
            }
        });



        callbackManager = CallbackManager.Factory.create();

        facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
                Data.facebookUserData = facebookUserData;
                sendFacebookLoginValues(SplashLogin.this);
                FlurryEventLogger.facebookLoginClicked(Data.facebookUserData.fbId);
            }

            @Override
            public void facebookLoginError(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        linearLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
            }
        });
        topRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SplashLogin.this, editTextEmail);
            }
        });
		
		

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		try {
			if(getIntent().hasExtra("back_from_otp") && !RegisterScreen.facebookLogin){
				if(phoneNoLogin) {
					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
				}
				else{
					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		new DeviceTokenGenerator().generateDeviceToken(this, new IDeviceTokenReceiver() {
			
			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
			}
		});



        try {
            if (getIntent().hasExtra("previous_login_email")) {
                String previousLoginEmail = getIntent().getStringExtra("previous_login_email");
                editTextEmail.setText(previousLoginEmail);
                editTextEmail.setSelection(editTextEmail.getText().length());
                fromPreviousAccounts = true;
            }
            else{
                fromPreviousAccounts = false;
            }
        } catch(Exception e){
            e.printStackTrace();
            fromPreviousAccounts = false;
        }

        try {
            if (getIntent().hasExtra("forgot_login_email")) {
                String forgotLoginEmail = getIntent().getStringExtra("forgot_login_email");
                editTextEmail.setText(forgotLoginEmail);
                editTextEmail.setSelection(editTextEmail.getText().length());
            }
        } catch(Exception e){
            e.printStackTrace();
        }


        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {

            }

            @Override
            public void keyBoardClosed() {

            }
        }));
		
	}

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            if (hasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, buttonEmailLogin.getTop());
                    }
                }, 200);
            } else {
                try {
                    ((EditText)v).setError(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((EditText)v).setError(null);
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					scrollView.smoothScrollTo(0, buttonEmailLogin.getTop());
				}
			}, 200);
			try {
				if(v.getId() == R.id.editTextEmail) {
					((AutoCompleteTextView) v).showDropDown();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			DialogPopup.showGooglePlayErrorAlert(SplashLogin.this);
		}

		HomeActivity.checkForAccessTokenChange(this);
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
	}
	
	
	public void performBackPressed(){
        if(fromPreviousAccounts){
            Intent intent = new Intent(SplashLogin.this, MultipleAccountsActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
        else {
            FacebookLoginHelper.logoutFacebook();
            Intent intent = new Intent(SplashLogin.this, SplashNewActivity.class);
            intent.putExtra("no_anim", "yes");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
	}

	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

            if(isPhoneNumber){
                params.put("phone_no", emailId);
            }
            else{
                params.put("email", emailId);
            }
			params.put("password", password);
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("client_id", Config.getClientId());
			

			Log.i("email", emailId);
			Log.i("password", password);
			Log.i("device_token", Data.deviceToken);
			Log.i("device_type", Data.DEVICE_TYPE);
			Log.i("device_name", Data.deviceName);
			Log.i("app_version", ""+Data.appVersion);
			Log.i("os_version", Data.osVersion);
			Log.i("country", Data.country);
			Log.i("unique_device_id", Data.uniqueDeviceId);
			Log.i("latitude", ""+Data.latitude);
			Log.i("longitude", ""+Data.longitude);
			Log.i("client_id", Config.getClientId());
			
			String url = Config.getServerUrl() + "/login_using_email_or_phone_no";

			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/login_using_email_or_phone_no", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
                                        if(isPhoneNumber){
                                            enteredEmail = jObj.getString("user_email");;
                                        }
                                        else{
                                            enteredEmail = emailId;
                                        }
										phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
                                        accessToken = jObj.getString("access_token");
										Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
										otpErrorMsg = jObj.getString("error");
										otpFlag = 0;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											Database.getInstance(SplashLogin.this).insertEmail(emailId);
											loginDataFetched = true;
										}
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
									DialogPopup.dismissLoadingDialog();
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
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	
	/**
	 * ASync for login from server
	 */
	public void sendFacebookLoginValues(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude(); 
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);
			
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("client_id", Config.getClientId());
			

			Log.i("user_fb_id", Data.facebookUserData.fbId);
			Log.i("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			Log.i("fb_access_token", Data.facebookUserData.accessToken);
			Log.i("fb_mail", Data.facebookUserData.userEmail);
			Log.i("username", Data.facebookUserData.userName);
			
			Log.i("device_token", Data.deviceToken);
			Log.i("device_type", Data.DEVICE_TYPE);
			Log.i("device_name", Data.deviceName);
			Log.i("app_version", ""+Data.appVersion);
			Log.i("os_version", Data.osVersion);
			Log.i("country", Data.country);
			Log.i("unique_device_id", Data.uniqueDeviceId);
			Log.i("latitude", ""+Data.latitude);
			Log.i("longitude", ""+Data.longitude);
			Log.i("client_id", Config.getClientId());
			
			String url = Config.getServerUrl() + "/login_using_facebook";
		
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/login_using_facebook", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);

								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
										String error = jObj.getString("error");
										facebookRegister = true;
										notRegisteredMsg = error;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
										phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
                                        accessToken = jObj.getString("access_token");
										Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
										otpErrorMsg = jObj.getString("error");
										otpFlag = 1;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											loginDataFetched = true;
											
											Database.getInstance(SplashLogin.this).insertEmail(Data.facebookUserData.userEmail);
										}
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
									DialogPopup.dismissLoadingDialog();
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
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	/**
	 * Send intent to otp screen by making required data objects
	 *  flag 0 for email, 1 for Facebook
	 */
	public void sendIntentToOtpScreen(){
		DialogPopup.alertPopupWithListener(SplashLogin.this, "", otpErrorMsg, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogPopup.dismissAlertPopup();
				if(0 == otpFlag){
					RegisterScreen.facebookLogin = false;
					OTPConfirmScreen.intentFromRegister = false;
					OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "", accessToken);
				}
				else if(1 == otpFlag){
					RegisterScreen.facebookLogin = true;
					OTPConfirmScreen.intentFromRegister = false;
					OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
                        Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
                        Data.facebookUserData.accessToken,
                        Data.facebookUserData.userEmail,
                        Data.facebookUserData.userName,
                        phoneNoOfUnverifiedAccount, "", "", accessToken);
				}
				Intent intent = new Intent(SplashLogin.this, OTPConfirmScreen.class);
				intent.putExtra("show_timer", 0);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}
	
	
	public void sendIntentToRegisterScreen(){
		DialogPopup.alertPopupWithListener(this, "", notRegisteredMsg, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegisterScreen.facebookLogin = true;
				startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			
			Database2.getInstance(SplashLogin.this).updateDriverLastLocationTime();
			Database2.getInstance(SplashLogin.this).close();
	        
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("username", Data.userData.userName);
			FlurryAgent.logEvent("App Login", articleParams);
			
			loginDataFetched = false;
			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			ActivityCompat.finishAffinity(this);
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && facebookRegister){
			facebookRegister = false;
			sendIntentToRegisterScreen();
		}
		else if(hasFocus && sendToOtpScreen){
			sendIntentToOtpScreen();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	

	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}
	
}
