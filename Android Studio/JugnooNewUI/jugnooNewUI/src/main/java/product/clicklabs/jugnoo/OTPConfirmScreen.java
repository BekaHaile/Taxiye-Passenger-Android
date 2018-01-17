package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.LinkedWalletStatus;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PinEditTextLayout;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class OTPConfirmScreen extends BaseActivity implements  Constants{

	private final String TAG = "OTP screen";

	ImageView imageViewBack;

	TextView textViewOtpNumber, tvOtpPhoneNumber;

	LinearLayout linearLayoutWaiting;
	TextView textViewCounter;
	ImageView imageViewYellowLoadingBar;
	RelativeLayout relative, rlOTPTimer;

	TextView textViewScroll;
	
	boolean loginDataFetched = false;
	private int linkedWallet = 0;
	//private int userVerified = 0;
	private String linkedWalletErrorMsg = "";

	public static boolean intentFromRegister = true, backFromMissedCall;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;
	public static GoogleRegisterData googleRegisterData;
	private boolean giveAMissedCall;
	private String signupBy = "", email = "", password = "";

	private RelativeLayout rlOTPContainer;
	private boolean runAfterDelay;
	private boolean onlyDigits;
	public static boolean phoneNoLogin = false;
	int duration = 500, otpLength = 4;
	private ProgressDialog missedCallDialog;

	private LinearLayout llEditText;
	private PinEditTextLayout pinEditTextLayout;
	private TextView tvResendCode, tvCallMe;
	private Handler handler = new Handler();

	@Override
	protected void onNewIntent(Intent intent) {
		retrieveOTPFromSMS(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);

		loginDataFetched = false;

		try {
			if(getIntent().hasExtra(LINKED_WALLET)){
                linkedWallet = getIntent().getIntExtra(LINKED_WALLET, 0);
                linkedWalletErrorMsg = getIntent().getStringExtra(LINKED_WALLET_MESSAGE);
                if((!"".equalsIgnoreCase(linkedWalletErrorMsg)) && (linkedWalletErrorMsg != null)){
                    DialogPopup.dialogBanner(OTPConfirmScreen.this, linkedWalletErrorMsg);
                }
                signupBy = getIntent().getStringExtra("signup_by");
                email = getIntent().getStringExtra("email");
                password = getIntent().getStringExtra("password");
				if(getIntent().hasExtra("otp_length")){
					otpLength = Integer.parseInt(getIntent().getStringExtra("otp_length"));
				}


                if(email.length() > 0){
                    onlyDigits = Utils.checkIfOnlyDigits(email);
                }

            }
		} catch (Exception e) {
			e.printStackTrace();
		}



		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		llEditText = (LinearLayout) findViewById(R.id.llEditText);
		pinEditTextLayout = new PinEditTextLayout(llEditText, new PinEditTextLayout.Callback() {
			@Override
			public void onOTPComplete(String otp, EditText editText) {
				verifyClick(otp, editText);
			}
		});

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenRegular(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
		tvOtpPhoneNumber = (TextView) findViewById(R.id.tvOtpPhoneNumber); tvOtpPhoneNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);

		rlOTPContainer = (RelativeLayout) findViewById(R.id.rlOTPContainer);

		rlOTPTimer = (RelativeLayout) findViewById(R.id.rlOTPTimer);
		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		textViewCounter = (TextView) findViewById(R.id.textViewCounter); textViewCounter.setTypeface(Fonts.mavenRegular(this));
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);

		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

		tvResendCode = (TextView) findViewById(R.id.tvResendCode);
		tvCallMe = (TextView) findViewById(R.id.tvCallMe);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		tvResendCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Utils.disableSMSReceiver(OTPConfirmScreen.this);
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
					apiGenerateLoginOtp(OTPConfirmScreen.this, email);
					startTimerForRetry();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			}
		}, 500);

		try {
			//jungooautos-verify://app?otp=1234
			Uri data = getIntent().getData();
			String host = "app";
			Gson gson = new Gson();

			if(data != null && data.getHost().equalsIgnoreCase(host)) {
				String otp = data.getQueryParameter("otp");

				String registrationMode = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE,
						"" + SplashNewActivity.registerationType);
				String registerData = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA, "");

				if((""+SplashNewActivity.RegisterationType.FACEBOOK).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					facebookRegisterData = gson.fromJson(registerData, FacebookRegisterData.class);
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
					tvOtpPhoneNumber.setText(facebookRegisterData.phoneNo);
				}
				else if((""+SplashNewActivity.RegisterationType.GOOGLE).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					googleRegisterData = gson.fromJson(registerData, GoogleRegisterData.class);
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
					tvOtpPhoneNumber.setText(googleRegisterData.phoneNo);
				}
				else if((""+SplashNewActivity.RegisterationType.EMAIL).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					emailRegisterData = gson.fromJson(registerData, EmailRegisterData.class);
					textViewOtpNumber.setText(emailRegisterData.phoneNo);
					tvOtpPhoneNumber.setText(emailRegisterData.phoneNo);
				}
				if(otp != null && !"".equalsIgnoreCase(otp)){
					pinEditTextLayout.setOTPDirectly(otp);
				}
			}
			else{
				if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
					tvOtpPhoneNumber.setText(facebookRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "" + SplashNewActivity.RegisterationType.FACEBOOK);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(facebookRegisterData, FacebookRegisterData.class));
				}
				else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
					tvOtpPhoneNumber.setText(googleRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.GOOGLE);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(googleRegisterData, GoogleRegisterData.class));
				}
				else{
					textViewOtpNumber.setText(email);
					tvOtpPhoneNumber.setText(email);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.EMAIL);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(emailRegisterData, EmailRegisterData.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewCounter.setText("0:10");

		startOTPTimer();

		try{
			if(!"".equalsIgnoreCase(Prefs.with(OTPConfirmScreen.this).getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
			}
			else{
			}

			if(Prefs.with(this).getInt(SP_OTP_VIA_CALL_ENABLED, 0) == 1){
				tvCallMe.setVisibility(View.VISIBLE);
			} else {
				tvCallMe.setVisibility(View.GONE);
			}

		} catch(Exception e){
			e.printStackTrace();
			tvCallMe.setVisibility(View.GONE);
		}

		rlOTPTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setRlOTPTimerVisibility(View.GONE);
			}
		});

		tvCallMe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initiateOTPCallAsync(OTPConfirmScreen.this, textViewOtpNumber.getText().toString());
				startTimerForRetry();
			}
		});
		startTimerForRetry();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				retrieveOTPFromSMS(getIntent());
			}
		}, 100);

	}

	private void startOTPTimer(){
		try{
			long timerDuration = 10000;
			if(getIntent().getIntExtra("show_timer", 0) == 1){
				setRlOTPTimerVisibility(View.VISIBLE);
				CustomCountDownTimer customCountDownTimer = new CustomCountDownTimer(timerDuration, 5);
				customCountDownTimer.start();
				Utils.hideKeyboard(this);
			}
			else{
				throw new Exception();
			}
		} catch(Exception e){
			setRlOTPTimerVisibility(View.GONE);
		}
	}

	class CustomCountDownTimer extends CountDownTimer {

		private final long mMillisInFuture;
		public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mMillisInFuture = millisInFuture;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			double percent = (((double)millisUntilFinished) * 100.0) / mMillisInFuture;

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
			setRlOTPTimerVisibility(View.GONE);
		}
	}


	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(missedCallDialog != null) {
							missedCallDialog.dismiss();
						}
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
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
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

		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, OTPConfirmScreen.class.getName());
		Utils.enableSMSReceiver(this);

		MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 1000);
		HomeActivity.checkForAccessTokenChange(this);

		try {
			if(giveAMissedCall) {
				giveAMissedCall = false;
				//buttonVerify.performClick();
				missedCallDialog = DialogPopup.showLoadingDialogNewInstance(OTPConfirmScreen.this, "Loading...");
				//rlProgress.setVisibility(View.VISIBLE);
				if (signupBy.equalsIgnoreCase("email")) {
					if (onlyDigits) {
						email = "+91" + email;
						//sendLoginValues(OTPConfirmScreen.this, email, password, true, false);
						apiLoginUsingOtp(OTPConfirmScreen.this, "99999", email);
					} else {
						sendLoginValues(OTPConfirmScreen.this, email, password, false, false);
					}
				} else if (signupBy.equalsIgnoreCase("facebook")) {
					sendFacebookLoginValues(OTPConfirmScreen.this);
				} else if (signupBy.equalsIgnoreCase("google")) {
					sendGoogleLoginValues(OTPConfirmScreen.this);
				}
				// api call
				//handler.postDelayed(runnable, 5000);
			}
			if(backFromMissedCall){
				backFromMissedCall = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			runAfterDelay = true;
			if(signupBy.equalsIgnoreCase("email")){
				if(onlyDigits){
					email = "+91"+email;
					//sendLoginValues(OTPConfirmScreen.this, email, password, true, false);
					apiLoginUsingOtp(OTPConfirmScreen.this, "99999", email);
				} else{
					sendLoginValues(OTPConfirmScreen.this, email, password, false, false);
				}
			} else if(signupBy.equalsIgnoreCase("facebook")){
				sendFacebookLoginValues(OTPConfirmScreen.this);
			} else if(signupBy.equalsIgnoreCase("google")){
				sendGoogleLoginValues(OTPConfirmScreen.this);
			}
		}
	};


    public static boolean checkIfRegisterDataNull(Activity activity){
        try {
            if(emailRegisterData == null && facebookRegisterData == null && googleRegisterData == null){
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
		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, "");
		Utils.disableSMSReceiver(this);
		try{
			MyApplication.getInstance().getLocationFetcher().destroy();
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
	}

	private void showErrorOnMissedCallBack(){
		if(runAfterDelay) {
			runAfterDelay = false;
			if(missedCallDialog != null){
				missedCallDialog.dismiss();
			}
			DialogPopup.alertPopup(OTPConfirmScreen.this, "", getResources().getString(R.string.we_could_not_verify));
			//rlProgress.setVisibility(View.VISIBLE);
			//progressBar.setVisibility(View.GONE);
			//tvProgress.setText(getResources().getString(R.string.we_could_not_verify));
		}
	}

	private void apiLoginUsingOtp(final Activity activity, String otp, final String phoneNumber){
			if(MyApplication.getInstance().isOnline()){
				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

				params.put("phone_no", "+91"+Utils.retrievePhoneNumberTenChars(phoneNumber));
				params.put("device_token", MyApplication.getInstance().getDeviceToken());
				params.put("device_name", MyApplication.getInstance().deviceName());
				params.put("os_version", MyApplication.getInstance().osVersion());
				params.put("country", MyApplication.getInstance().country());
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getAutosClientId());
				params.put("login_otp", otp);
				params.put("login_type", "0");
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				} else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params.toString());

				new HomeUtil().putDefaultParams(params);

				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						DialogPopup.dismissLoadingDialog();
						if(missedCallDialog != null){
							missedCallDialog.dismiss();
						}
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "verifyOtp response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);

							int flag = jObj.getInt("flag");

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
									Data.kitPhoneNumber = jObj.optString("kit_phone_no");
									SplashNewActivity.parseDataSendToMultipleAccountsScreen(activity, jObj,
											emailRegisterData.name, emailRegisterData.emailId,
											phoneNumber, emailRegisterData.password,
											emailRegisterData.referralCode, emailRegisterData.accessToken);
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										goToLoginOrOnboarding(jObj, responseStr, loginResponse, LoginVia.EMAIL);
									}
								} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_ALREADY_VERIFIED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								}else {
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "loginUsingOtp error="+error.toString());
						if(missedCallDialog != null){
							missedCallDialog.dismiss();
						}
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else{
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}

	private void apiGenerateLoginOtp(final Activity activity, final String phoneNumber){
		if(MyApplication.getInstance().isOnline()){
			DialogPopup.showLoadingDialog(activity, "Loading...");
			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

			params.put("phone_no", "+91"+phoneNumber);
			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());
			params.put("login_type", "0");

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "=" + params);
			new HomeUtil().putDefaultParams(params);

			RestClient.getApiService().generateLoginOtp(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "generateLoginOtp response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dismissLoadingDialog();
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									Utils.enableSMSReceiver(OTPConfirmScreen.this);
									startOTPTimer();
								}
							} else {
								DialogPopup.alertPopup(activity, "", jObj.optString("error"));
							}
							DialogPopup.dismissLoadingDialog();
						}


					} catch (Exception e){
						e.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		} else{
			DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.CHECK_INTERNET_MSG);
		}
	}


	public void verifyOtpViaFB(final Activity activity, String otp, final int linkedWallet) {
        if(!checkIfRegisterDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();


                params.put("user_fb_id", facebookRegisterData.fbId);
                params.put("user_fb_name", facebookRegisterData.fbName);
                params.put("fb_access_token", facebookRegisterData.fbAccessToken);
                params.put("fb_mail", facebookRegisterData.fbUserEmail);
                params.put("username", facebookRegisterData.fbUserName);
				params.put("phone_no", "+91"+Utils.retrievePhoneNumberTenChars(facebookRegisterData.phoneNo));

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
                params.put("device_name", MyApplication.getInstance().deviceName());
                params.put("os_version", MyApplication.getInstance().osVersion());
                params.put("country", MyApplication.getInstance().country());
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getAutosClientId());
                params.put("login_otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				} else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params);


				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.v(TAG, "verifyOtp response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);

							int flag = jObj.getInt("flag");

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
									Data.kitPhoneNumber = jObj.optString("kit_phone_no");
									SplashNewActivity.parseDataSendToMultipleAccountsScreen(activity, jObj,
											facebookRegisterData.fbName, facebookRegisterData.fbUserEmail,
											facebookRegisterData.phoneNo, facebookRegisterData.password,
											facebookRegisterData.referralCode, facebookRegisterData.accessToken);
								} else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.FACEBOOK_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "verifyOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}


	public void verifyOtpViaGoogle(final Activity activity, String otp, final int linkedWallet) {
		if(!checkIfRegisterDataNull(activity)) {
			if (MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

				params.put("user_google_id", googleRegisterData.id);
				params.put("email", googleRegisterData.email);
				params.put("google_access_token", googleRegisterData.googleAccessToken);
				params.put("phone_no", "+91"+Utils.retrievePhoneNumberTenChars(googleRegisterData.phoneNo));

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
				params.put("device_name", MyApplication.getInstance().deviceName());
				params.put("os_version", MyApplication.getInstance().osVersion());
				params.put("country", MyApplication.getInstance().country());
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getAutosClientId());
				params.put("login_otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params);

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.v(TAG, "verifyOtp response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);

							int flag = jObj.getInt("flag");

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
									Data.kitPhoneNumber = jObj.optString("kit_phone_no");
									SplashNewActivity.parseDataSendToMultipleAccountsScreen(activity, jObj,
											googleRegisterData.name, googleRegisterData.email,
											googleRegisterData.phoneNo, googleRegisterData.password,
											googleRegisterData.referralCode, googleRegisterData.accessToken);
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.GOOGLE_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
										MyApplication.getInstance().getDatabase().close();
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "verifyOtp errror="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put(KEY_PHONE_NO, phoneNo);
			Log.i("phone_no", ">" + phoneNo);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().sendOtpViaCall(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "sendOtpViaCall response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							int flag = jObj.getInt("flag");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", errorMessage);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
							DialogPopup.dismissLoadingDialog();
						} else {
							String message = jObj.getString("message");
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							}
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "sendOtpViaCall error="+error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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

			MyApplication.getInstance().getAppSwitcher().switchApp(OTPConfirmScreen.this,
					Prefs.with(OTPConfirmScreen.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
					Data.splashIntentUri, new LatLng(Data.loginLatitude, Data.loginLongitude), SplashNewActivity.openHomeSwitcher);
//			Intent intent = new Intent(OTPConfirmScreen.this, HomeActivity.class);
//			intent.setData(Data.splashIntentUri);
//			startActivity(intent);
//			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			ActivityCompat.finishAffinity(this);
		} else if(hasFocus && backToSplashOboarding){
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}


	public void performBackPressed(){
//			if (intentFromRegister) {
//				Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
//				intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.SIGNUP.getOrdinal());
//				intent.putExtra(KEY_BACK_FROM_OTP, true);
//				startActivity(intent);
//			} else {
//				Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
//				intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.LOGIN.getOrdinal());
//				intent.putExtra(KEY_BACK_FROM_OTP, true);
//				startActivity(intent);
//			}
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	protected void onDestroy() {
		try{
			if(handler != null){
				handler.removeCallbacks(runnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}



	private void retrieveOTPFromSMS(Intent intent){
		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				otp = Utils.retrieveOTPFromSMS(message);
			} else if(intent.hasExtra(KEY_OTP)){
				otp = intent.getStringExtra(KEY_OTP);
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
					//editTextOTP.setText(otp);
					//editTextOTP.setSelection(editTextOTP.getText().length());
//					txtPinEntry.setText(otp);
//					txtPinEntry.setSelection(txtPinEntry.getText().length());

					//buttonVerify.performClick();

					pinEditTextLayout.setOTPDirectly(otp);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void generateOTP(final String accessToken, final int linkedWallet) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(KEY_ACCESS_TOKEN, accessToken);
				params.put(KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(KEY_IS_ACCESS_TOKEN_NEW, "1");

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, linkedWallet+"walletRequestOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(OTPConfirmScreen.this, message);
							} else if (ApiResponseFlags.PAYTM_INVALID_EMAIL.getOrdinal() == flag) {
								DialogPopup.alertPopup(OTPConfirmScreen.this, "", message);
							} else {
								DialogPopup.alertPopup(OTPConfirmScreen.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, linkedWallet+"walletRequestOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
					}
				};

				new HomeUtil().putDefaultParams(params);
				if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().paytmRequestOtp(params, callback);
				}
				else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().mobikwikRequestOtp(params, callback);
				}
				else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().freeChargeRequestOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(OTPConfirmScreen.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								generateOTP(accessToken, linkedWallet);
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendOTP(final String otp) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
				params.put(Constants.KEY_OTP, otp);

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "loginWithOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "loginWithOtp error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
					}
				};
				new HomeUtil().putDefaultParams(params);
				if(linkedWallet == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getApiService().paytmLoginWithOtp(params, callback);
				} else if(linkedWallet == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiService().mobikwikLoginWithOtp(params, callback);
				} else if(linkedWallet == PaymentOption.FREECHARGE.getOrdinal()) {
					RestClient.getApiService().freeChargeLoginWithOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(OTPConfirmScreen.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								sendOTP(otp);
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber, boolean showDialog) {
		if (MyApplication.getInstance().isOnline()) {
			//resetFlags();
			if(showDialog){
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
			}
			//DialogPopup.showLoadingDialog(activity, "Trying to verify through missed call...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

			if (isPhoneNumber) {
				params.put("phone_no", emailId);
			} else {
				params.put("email", emailId);
			}
			params.put("password", password);
			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "=" + params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingEmailOrPhoneNo(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingEmailOrPhoneNo response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								showErrorOnMissedCallBack();
								/*if (isPhoneNumber) {
									enteredEmail = jObj.getString("user_email");
								} else {
									enteredEmail = emailId;
								}*/
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									goToLoginOrOnboarding(jObj, responseStr, loginResponse, LoginVia.EMAIL);
									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
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

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	public void sendFacebookLoginValues(final Activity activity) {
		if (MyApplication.getInstance().isOnline()) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();


			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "" + params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingFacebook(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingFacebook response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								linkedWallet = jObj.optInt("reg_wallet_type");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									goToLoginOrOnboarding(jObj, responseStr, loginResponse, LoginVia.FACEBOOK);

									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
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

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingFacebook error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	public void sendGoogleLoginValues(final Activity activity) {
		if (MyApplication.getInstance().isOnline()) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

			params.put("google_access_token", Data.googleSignInAccount.getIdToken());

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "" + params);


			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingGoogle(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingGoogle response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
							if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
								linkedWallet = jObj.optInt("reg_wallet_type");
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
								if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
									goToLoginOrOnboarding(jObj, responseStr, loginResponse, LoginVia.GOOGLE);

									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							}
							else{
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
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

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingGoogle error="+error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	private LocationUpdate locationUpdate = new LocationUpdate() {
		@Override
		public void onLocationChanged(Location location) {
			Data.loginLatitude = location.getLatitude();
			Data.loginLongitude = location.getLongitude();
		}
	};


	private void verifyClick(String otpCode, EditText editTextOTP){
		if (otpCode.length() > 0) {
			if(missedCallDialog != null) {
				missedCallDialog.dismiss();
			}
			if (SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
				verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
			} else if (SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
				verifyOtpViaGoogle(OTPConfirmScreen.this, otpCode, linkedWallet);
			} else {
				apiLoginUsingOtp(OTPConfirmScreen.this, otpCode, email);
			}
		} else {
			editTextOTP.requestFocus();
			editTextOTP.setError("OTP can't be empty");
		}
	}

	private void setRlOTPTimerVisibility(int visibility){
		if(visibility == View.GONE){
			pinEditTextLayout.tapOnEditText();
			rlOTPTimer.setVisibility(View.GONE);
		} else {
			rlOTPTimer.setVisibility(View.VISIBLE);
		}
	}


	public static boolean backToSplashOboarding = false;
	public static String accessTokenOnBoarding = "";
	private void goToLoginOrOnboarding(JSONObject jObj, String responseStr, LoginResponse loginResponse,
										LoginVia loginVia) throws Exception {
		if(jObj.optJSONObject("user_data").optInt("signup_onboarding", 0) == 1){
			String authKey = jObj.optJSONObject("user_data").optString("auth_key", "");
			AccessTokenGenerator.saveAuthKey(this, authKey);
			String authSecret = authKey + Config.getClientSharedSecret();
			accessTokenOnBoarding = SHA256Convertor.getSHA256String(authSecret);
			backToSplashOboarding = true;
			SplashNewActivity.loginResponseStr = responseStr;
			SplashNewActivity.loginResponseData = loginResponse;
		} else{
			new JSONParser().parseAccessTokenLoginData(this, responseStr,
					loginResponse, loginVia,
					new LatLng(Data.loginLatitude, Data.loginLongitude));
			loginDataFetched = true;
		}
	}


	private void giveAMissCall(){
		try {
			Utils.disableSMSReceiver(OTPConfirmScreen.this);
			if(missedCallDialog != null) {
				missedCallDialog.dismiss();
			}
			if(!"".equalsIgnoreCase(Prefs.with(OTPConfirmScreen.this).getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
				DialogPopup.alertPopupTwoButtonsWithListeners(OTPConfirmScreen.this, "",
						getResources().getString(R.string.give_missed_call_dialog_text),
						getResources().getString(R.string.call_us),
						getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								giveAMissedCall = true;
								Utils.openCallIntent(OTPConfirmScreen.this, Prefs.with(OTPConfirmScreen.this)
										.getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""));
								backFromMissedCall = true;
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, false, false
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Runnable runnableRetryBlock;
	private int secondsLeftForRetry = 15;
	private void startTimerForRetry(){
		if(runnableRetryBlock == null) {
			runnableRetryBlock = new Runnable() {
				@Override
				public void run() {
					if(secondsLeftForRetry > 0){
						tvResendCode.setText(getString(R.string.resend_code_in, (secondsLeftForRetry > 9?"0:":"0:0")+secondsLeftForRetry));
						tvCallMe.setText(getString(R.string.call_me_in, (secondsLeftForRetry > 9?"0:":"0:0")+secondsLeftForRetry));
						tvResendCode.setEnabled(false);
						tvCallMe.setEnabled(false);
						secondsLeftForRetry--;
						handler.postDelayed(runnableRetryBlock, 1000);
					} else {
						tvResendCode.setText(R.string.resend_code);
						tvCallMe.setText(R.string.call_me);
						tvResendCode.setEnabled(true);
						tvCallMe.setEnabled(true);
						handler.removeCallbacks(runnableRetryBlock);
					}
				}
			};
		}
		secondsLeftForRetry = 15;
		handler.post(runnableRetryBlock);
	}

}





