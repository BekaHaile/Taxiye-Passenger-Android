package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class OTPConfirmScreen extends BaseActivity implements LocationUpdate, FlurryEventNames{

	private final String TAG = "OTPConfirmScreen";

	ImageView imageViewBack;
	TextView textViewTitle;


	//new start
	TextView textViewOtpNumber, textViewEnterOTP;
	ImageView imageViewSep, imageViewChangePhoneNumber;
	EditText editTextOTP;

	LinearLayout linearLayoutWaiting;
	TextView textViewCounter;
	ImageView imageViewYellowLoadingBar;
	//new end

	Button buttonVerify;

	LinearLayout linearLayoutOTPOptions;
	RelativeLayout relativeLayoutOTPThroughCall, relativeLayoutMissCall, relativeLayoutOr;
	TextView textViewOTPNotReceived, textViewMissCall;


	LinearLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;
	
	boolean loginDataFetched = false;
	
	public static boolean intentFromRegister = true;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;
	public static GoogleRegisterData googleRegisterData;

	public static String OTP_SCREEN_OPEN = null;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				String[] arr = message.split("Your\\ One\\ Time\\ Password\\ is\\ ");
				otp = arr[1];
				otp = otp.replaceAll("\\.", "");
			} else if(intent.hasExtra("otp")){
				otp = intent.getStringExtra("otp");
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
					editTextOTP.setText(otp);
					editTextOTP.setSelection(editTextOTP.getText().length());
					buttonVerify.performClick();
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}

		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);

		Utils.enableSMSReceiver(this);

		loginDataFetched = false;


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		//new start
		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.latoRegular(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		imageViewSep = (ImageView) findViewById(R.id.imageViewSep);
		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);

		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		((TextView)findViewById(R.id.textViewWaiting)).setTypeface(Fonts.latoRegular(this));
		textViewCounter = (TextView) findViewById(R.id.textViewCounter); textViewCounter.setTypeface(Fonts.latoRegular(this));
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);
		textViewEnterOTP = (TextView)findViewById(R.id.textViewEnterOTP); textViewEnterOTP.setTypeface(Fonts.latoRegular(this));
		//new end
		
		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.latoRegular(this));
		
		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.latoRegular(this));


		linearLayoutOTPOptions = (LinearLayout) findViewById(R.id.linearLayoutOTPOptions);
		relativeLayoutOTPThroughCall = (RelativeLayout) findViewById(R.id.relativeLayoutOTPThroughCall);
		textViewOTPNotReceived = (TextView) findViewById(R.id.textViewOTPNotReceived); textViewOTPNotReceived.setTypeface(Fonts.latoLight(this));
		relativeLayoutMissCall = (RelativeLayout) findViewById(R.id.relativeLayoutMissCall);
		textViewMissCall = (TextView) findViewById(R.id.textViewMissCall); textViewMissCall.setTypeface(Fonts.latoLight(this));
		relativeLayoutOr = (RelativeLayout) findViewById(R.id.relativeLayoutOr);


		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		

		
		buttonVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String otpCode = editTextOTP.getText().toString().trim();
				if (otpCode.length() > 0) {
					if (RegisterScreen.RegisterationType.FACEBOOK == RegisterScreen.registerationType) {
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode);
					}
					else if(RegisterScreen.RegisterationType.GOOGLE == RegisterScreen.registerationType){
						verifyOtpViaGoogle(OTPConfirmScreen.this, otpCode);
					}
					else {
						verifyOtpViaEmail(OTPConfirmScreen.this, otpCode);
					}
					FlurryEventLogger.event(OTP_VERIFIED_WITH_SMS);
				} else {
					editTextOTP.requestFocus();
					editTextOTP.setError("Code can't be empty");
				}

			}
		});

		textViewEnterOTP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.showSoftKeyboard(OTPConfirmScreen.this, editTextOTP);
				editTextOTP.requestFocus();
			}
		});

		editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonVerify.performClick();
						break;

					case EditorInfo.IME_ACTION_NEXT:
						break;

					default:
				}
				return true;
			}
		});


		editTextOTP.setOnFocusChangeListener(onFocusChangeListener);
		editTextOTP.setOnClickListener(onClickListener);


		relativeLayoutOTPThroughCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (RegisterScreen.RegisterationType.FACEBOOK == RegisterScreen.registerationType) {
					initiateOTPCallAsync(OTPConfirmScreen.this, facebookRegisterData.phoneNo);
				}
				else if (RegisterScreen.RegisterationType.GOOGLE == RegisterScreen.registerationType) {
					initiateOTPCallAsync(OTPConfirmScreen.this, googleRegisterData.phoneNo);
				}
				else {
					initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
				}
				FlurryEventLogger.event(CALL_ME_OTP);
			}
		});

		relativeLayoutMissCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
					Utils.openCallIntent(OTPConfirmScreen.this, Data.knowlarityMissedCallNumber);
					FlurryEventLogger.event(GIVE_MISSED_CALL);
				}
			}
		});


		imageViewChangePhoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(CHANGE_PHONE_OTP_NOT_RECEIVED);
                startActivity(new Intent(OTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//new start
		try {
			//jungooautos-verify://app?otp=1234
			Uri data = getIntent().getData();
			String host = "app";
			Gson gson = new Gson();

			if(data != null && data.getHost().equalsIgnoreCase(host)) {
				String otp = data.getQueryParameter("otp");

				String registrationMode = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE,
						"" + RegisterScreen.registerationType);
				String registerData = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA, "");

				if((""+RegisterScreen.RegisterationType.FACEBOOK).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					facebookRegisterData = gson.fromJson(registerData, FacebookRegisterData.class);
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
				}
				else if((""+RegisterScreen.RegisterationType.GOOGLE).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					googleRegisterData = gson.fromJson(registerData, GoogleRegisterData.class);
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
				}
				else if((""+RegisterScreen.RegisterationType.EMAIL).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					emailRegisterData = gson.fromJson(registerData, EmailRegisterData.class);
					textViewOtpNumber.setText(emailRegisterData.phoneNo);
				}
				if(otp != null && !"".equalsIgnoreCase(otp)){
					editTextOTP.setText(otp);
					editTextOTP.setSelection(editTextOTP.getText().length());
					buttonVerify.performClick();
				}
			}
			else{
				if(RegisterScreen.RegisterationType.FACEBOOK == RegisterScreen.registerationType){
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "" + RegisterScreen.RegisterationType.FACEBOOK);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(facebookRegisterData, FacebookRegisterData.class));
				}
				else if(RegisterScreen.RegisterationType.GOOGLE == RegisterScreen.registerationType){
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+RegisterScreen.RegisterationType.GOOGLE);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(googleRegisterData, GoogleRegisterData.class));
				}
				else{
					textViewOtpNumber.setText(emailRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+RegisterScreen.RegisterationType.EMAIL);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(emailRegisterData, EmailRegisterData.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		try{
			if(getIntent().getIntExtra("show_timer", 0) == 1){
				linearLayoutWaiting.setVisibility(View.VISIBLE);
				linearLayoutOTPOptions.setVisibility(View.GONE);
				textViewCounter.setText("0:30");
				countDownTimer.start();
			}
			else{
				throw new Exception();
			}
		} catch(Exception e){
			linearLayoutWaiting.setVisibility(View.GONE);
			linearLayoutOTPOptions.setVisibility(View.VISIBLE);
		}

		try{
			if(Data.otpViaCallEnabled == 1){
				relativeLayoutOTPThroughCall.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutOTPThroughCall.setVisibility(View.GONE);
			}
			if(!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
				if(Data.otpViaCallEnabled == 1){
					relativeLayoutOr.setVisibility(View.VISIBLE);
				} else{
					relativeLayoutOr.setVisibility(View.GONE);
				}
				relativeLayoutMissCall.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutOr.setVisibility(View.GONE);
				relativeLayoutMissCall.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
			relativeLayoutOr.setVisibility(View.GONE);
			relativeLayoutMissCall.setVisibility(View.GONE);
		}
		//new end
		
		new DeviceTokenGenerator().generateDeviceToken(this, new IDeviceTokenReceiver() {
			
			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver" +
						"" +
						"" +
						"", Data.deviceToken + "..");
			}
		});

		OTP_SCREEN_OPEN = "yes";


//		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
//			@Override
//			public void keyboardOpened() {
//
//			}
//
//			@Override
//			public void keyBoardClosed() {
//
//			}
//		}));



	}


	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, buttonVerify.getTop());
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
					scrollView.smoothScrollTo(0, buttonVerify.getTop());
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
			Data.locationFetcher = new LocationFetcher(OTPConfirmScreen.this, 1000, 1);
		}
		HomeActivity.checkForAccessTokenChange(this);

//        checkIfRegisterDataNull(this);

	}



    public static boolean checkIfRegisterDataNull(Activity activity){
        try {
            if(emailRegisterData == null && facebookRegisterData == null && googleRegisterData == null){
                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
	 * ASync for confirming otp from server
	 */
	public void verifyOtpViaEmail(final Activity activity, String otp) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                if (Data.locationFetcher != null) {
                    Data.loginLatitude = Data.locationFetcher.getLatitude();
                    Data.loginLongitude = Data.locationFetcher.getLongitude();
                }

                params.put("email", emailRegisterData.emailId);
                params.put("password", emailRegisterData.password);
                params.put("device_token", Data.getDeviceToken());
                params.put("device_type", Data.DEVICE_TYPE);
                params.put("device_name", Data.deviceName);
                params.put("app_version", "" + Data.appVersion);
                params.put("os_version", Data.osVersion);
                params.put("country", Data.country);
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getClientId());
                params.put("otp", otp);

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params.toString());


                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/verify_otp", params,
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

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                            new JSONParser().parseAccessTokenLoginData(activity, response);
                                            Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
                                            Database.getInstance(OTPConfirmScreen.this).close();
                                            loginDataFetched = true;
											BranchMetricsUtils.logEvent(activity, BRANCH_EVENT_REGISTRATION, false);
											FbEvents.logEvent(activity, FB_EVENT_REGISTRATION, false);
                                        }
                                    } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                } else {
                                    DialogPopup.dismissLoadingDialog();
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                DialogPopup.dismissLoadingDialog();
                            }


                        }
                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }

	}
	
	
	
	

	public void verifyOtpViaFB(final Activity activity, String otp) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                if (Data.locationFetcher != null) {
                    Data.loginLatitude = Data.locationFetcher.getLatitude();
                    Data.loginLongitude = Data.locationFetcher.getLongitude();
                }


                params.put("user_fb_id", facebookRegisterData.fbId);
                params.put("user_fb_name", facebookRegisterData.fbName);
                params.put("fb_access_token", facebookRegisterData.accessToken);
                params.put("fb_mail", facebookRegisterData.fbUserEmail);
                params.put("username", facebookRegisterData.fbUserName);

                params.put("device_token", Data.getDeviceToken());
                params.put("device_type", Data.DEVICE_TYPE);
                params.put("device_name", Data.deviceName);
                params.put("app_version", "" + Data.appVersion);
                params.put("os_version", Data.osVersion);
                params.put("country", Data.country);
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getClientId());
                params.put("otp", otp);

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params);


                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/verify_otp", params,
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
                            Log.v("Server response", "response = " + response);

                            try {
                                jObj = new JSONObject(response);

                                int flag = jObj.getInt("flag");

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                            new JSONParser().parseAccessTokenLoginData(activity, response);
                                            loginDataFetched = true;
                                            Database.getInstance(OTPConfirmScreen.this).insertEmail(facebookRegisterData.fbUserEmail);
                                            Database.getInstance(OTPConfirmScreen.this).close();
											BranchMetricsUtils.logEvent(activity, BRANCH_EVENT_REGISTRATION, false);
											FbEvents.logEvent(activity, FB_EVENT_REGISTRATION, false);
                                        }
                                    } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                } else {
                                    DialogPopup.dismissLoadingDialog();
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.dismissLoadingDialog();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}



	public void verifyOtpViaGoogle(final Activity activity, String otp) {
		if(!checkIfRegisterDataNull(activity)) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				RequestParams params = new RequestParams();

				if (Data.locationFetcher != null) {
					Data.loginLatitude = Data.locationFetcher.getLatitude();
					Data.loginLongitude = Data.locationFetcher.getLongitude();
				}

				params.put("user_google_id", googleRegisterData.id);
				params.put("user_google_name", googleRegisterData.name);
				params.put("user_google_mail", googleRegisterData.email);
				params.put("user_google_image", googleRegisterData.image);
				params.put("google_access_token", googleRegisterData.accessToken);

				params.put("device_token", Data.getDeviceToken());
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("device_name", Data.deviceName);
				params.put("app_version", "" + Data.appVersion);
				params.put("os_version", Data.osVersion);
				params.put("country", Data.country);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getClientId());
				params.put("otp", otp);

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params);


				AsyncHttpClient client = Data.getClient();
				client.post(Config.getServerUrl() + "/verify_otp", params,
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
								Log.v("Server response", "response = " + response);

								try {
									jObj = new JSONObject(response);

									int flag = jObj.getInt("flag");

									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
										if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
										} else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
										} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
											if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
												new JSONParser().parseAccessTokenLoginData(activity, response);
												loginDataFetched = true;
												Database.getInstance(OTPConfirmScreen.this).insertEmail(googleRegisterData.email);
												Database.getInstance(OTPConfirmScreen.this).close();
												BranchMetricsUtils.logEvent(activity, BRANCH_EVENT_REGISTRATION, false);
											}
										} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
										} else {
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
										DialogPopup.dismissLoadingDialog();
									} else {
										DialogPopup.dismissLoadingDialog();
									}

								} catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.dismissLoadingDialog();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
							}
						});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
	}

	
	/**
	 * ASync for initiating OTP Call from server
	 */
	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("phone_no", phoneNo);
			Log.i("phone_no", ">"+phoneNo);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/send_otp_via_call", params,
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

								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									int flag = jObj.getInt("flag");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									String message = jObj.getString("message");
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", message);
									}
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
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Database2.getInstance(OTPConfirmScreen.this).updateDriverLastLocationTime();
			Database2.getInstance(OTPConfirmScreen.this).close();
			Intent intent = new Intent(OTPConfirmScreen.this, HomeActivity.class);
			intent.setData(Data.splashIntentUri);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			ActivityCompat.finishAffinity(this);
		}
	}
	

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		if(intentFromRegister){
			Intent intent = new Intent(OTPConfirmScreen.this, RegisterScreen.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(OTPConfirmScreen.this, SplashLogin.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		Utils.disableSMSReceiver(this);
		OTP_SCREEN_OPEN = null;
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
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
	}





	CountDownTimer countDownTimer = new CountDownTimer(30000, 5) {

		@Override
		public void onTick(long millisUntilFinished) {
			double percent = (((double)millisUntilFinished) * 100.0) / 30000.0;

			double widthToSet = percent * ((double) (ASSL.Xscale() * 530)) / 100.0;

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewYellowLoadingBar.getLayoutParams();
			params.width = (int) widthToSet;
			imageViewYellowLoadingBar.setLayoutParams(params);


			long seconds = (long) Math.ceil(((double)millisUntilFinished) / 1000.0d);
			String text = seconds < 10 ? "0:0"+seconds : "0:"+seconds;
			textViewCounter.setText(text);
		}

		@Override
		public void onFinish() {
			linearLayoutWaiting.setVisibility(View.GONE);
			linearLayoutOTPOptions.setVisibility(View.VISIBLE);
		}
	};


	
}





