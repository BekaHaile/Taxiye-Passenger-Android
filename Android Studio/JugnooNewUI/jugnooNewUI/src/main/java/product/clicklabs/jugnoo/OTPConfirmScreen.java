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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.LinkedWalletStatus;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class OTPConfirmScreen extends BaseActivity implements LocationUpdate, FlurryEventNames, Constants{

	private final String TAG = "OTP screen";

	ImageView imageViewBack;
	TextView textViewTitle;


	TextView textViewOtpNumber;
	ImageView imageViewChangePhoneNumber;
	EditText editTextOTP;

	LinearLayout linearLayoutWaiting;
	TextView textViewCounter;
	ImageView imageViewYellowLoadingBar, imageViewPaytmIcon;

	Button buttonVerify, buttonOtpViaCall;
	TextView textViewOr;
	LinearLayout linearLayoutOtherOptions, linearLayoutGiveAMissedCall;
	private Animation tweenAnimation;

	RelativeLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;
	
	boolean loginDataFetched = false;
	private int linkedWallet = 0;
	private String linkedWalletErrorMsg = "";
	
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
		retrieveOTPFromSMS(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);

		Utils.enableSMSReceiver(this);

		loginDataFetched = false;

		if(getIntent().hasExtra(LINKED_WALLET)){
			linkedWallet = getIntent().getIntExtra(LINKED_WALLET, 0);
			linkedWalletErrorMsg = getIntent().getStringExtra(LINKED_WALLET_MESSAGE);
			if((!"".equalsIgnoreCase(linkedWalletErrorMsg)) && (linkedWalletErrorMsg != null)){
				DialogPopup.dialogBanner(OTPConfirmScreen.this, linkedWalletErrorMsg);
			}
		}


		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenRegular(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);

		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);
		imageViewPaytmIcon = (ImageView) findViewById(R.id.imageViewPaytmIcon);

		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		((TextView)findViewById(R.id.textViewWaiting)).setTypeface(Fonts.mavenRegular(this));
		textViewCounter = (TextView) findViewById(R.id.textViewCounter); textViewCounter.setTypeface(Fonts.mavenRegular(this));
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);

		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.mavenMedium(this));

		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.mavenRegular(this));

		linearLayoutOtherOptions = (LinearLayout) findViewById(R.id.linearLayoutOtherOptions);
		buttonOtpViaCall = (Button) findViewById(R.id.buttonOtpViaCall); buttonOtpViaCall.setTypeface(Fonts.mavenRegular(this));
		textViewOr = (TextView) findViewById(R.id.textViewOr); textViewOr.setTypeface(Fonts.mavenLight(this));
		linearLayoutGiveAMissedCall = (LinearLayout) findViewById(R.id.linearLayoutGiveAMissedCall);
		((TextView) findViewById(R.id.textViewGiveAMissedCall)).setTypeface(Fonts.mavenRegular(this));


		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		tweenAnimation = AnimationUtils.loadAnimation(OTPConfirmScreen.this, R.anim.tween);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(ACQUISITION, TAG, "Back");
				performBackPressed();
			}
		});

		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextOTP.setError(null);
			}
		});
		
		buttonVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String otpCode = editTextOTP.getText().toString().trim();
				if (otpCode.length() > 0) {
					if (SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
					} else if (SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
						verifyOtpViaGoogle(OTPConfirmScreen.this, otpCode, linkedWallet);
					} else {
						verifyOtpViaEmail(OTPConfirmScreen.this, otpCode, linkedWallet);
					}
					FlurryEventLogger.event(OTP_VERIFIED_WITH_SMS);
					FlurryEventLogger.eventGA(ACQUISITION, TAG, "Verify me");
				} else {
					editTextOTP.requestFocus();
					editTextOTP.setError("Code can't be empty");
				}
			}
		});


		editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				buttonVerify.performClick();
				return true;
			}
		});

		buttonOtpViaCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					editTextOTP.setError(null);
					if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
						// Resend OTP call to Paytm server...
						generateOTP(getLoggedInAccesToken());
					} else{
						if (1 == Data.otpViaCallEnabled) {
							if (SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
								initiateOTPCallAsync(OTPConfirmScreen.this, facebookRegisterData.phoneNo);
							} else if (SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
								initiateOTPCallAsync(OTPConfirmScreen.this, googleRegisterData.phoneNo);
							} else {
								initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
							}
						}
					}

				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		editTextOTP.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					editTextOTP.setTextSize(20);
				} else {
					editTextOTP.setTextSize(15);
				}
			}
		});


		editTextOTP.setOnFocusChangeListener(onFocusChangeListener);
		editTextOTP.setOnClickListener(onClickListener);


		linearLayoutGiveAMissedCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					editTextOTP.setError(null);
					tweenAnimation.cancel();
					linearLayoutGiveAMissedCall.clearAnimation();
					if(!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
						DialogPopup.alertPopupTwoButtonsWithListeners(OTPConfirmScreen.this, "",
								getResources().getString(R.string.give_missed_call_dialog_text),
								getResources().getString(R.string.call_us),
								getResources().getString(R.string.cancel),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Utils.openCallIntent(OTPConfirmScreen.this, Data.knowlarityMissedCallNumber);
										FlurryEventLogger.event(GIVE_MISSED_CALL);
										FlurryEventLogger.eventGA(ACQUISITION, TAG, "Give a miss call");
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
		});


		imageViewChangePhoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				editTextOTP.setError(null);
                FlurryEventLogger.event(CHANGE_PHONE_OTP_NOT_RECEIVED);
				FlurryEventLogger.eventGA(ACQUISITION, TAG, "Edit phone number");
				Intent intent = new Intent(OTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class);
				intent.putExtra(LINKED_WALLET, linkedWallet);
				startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
				}
				else if((""+SplashNewActivity.RegisterationType.GOOGLE).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					googleRegisterData = gson.fromJson(registerData, GoogleRegisterData.class);
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
				}
				else if((""+SplashNewActivity.RegisterationType.EMAIL).equalsIgnoreCase(registrationMode)
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
				if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "" + SplashNewActivity.RegisterationType.FACEBOOK);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(facebookRegisterData, FacebookRegisterData.class));
				}
				else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.GOOGLE);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(googleRegisterData, GoogleRegisterData.class));
				}
				else{
					textViewOtpNumber.setText(emailRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.EMAIL);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(emailRegisterData, EmailRegisterData.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		long timerDuration = 30000;
		if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
			imageViewPaytmIcon.setVisibility(View.VISIBLE);
			textViewCounter.setText("0:60");
			timerDuration = 60000;
			buttonOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		} else{
			imageViewPaytmIcon.setVisibility(View.GONE);
			textViewCounter.setText("0:30");
			buttonOtpViaCall.setText(getResources().getString(R.string.receive_otp_via_call));
		}

		try{
			if(getIntent().getIntExtra("show_timer", 0) == 1){
				linearLayoutWaiting.setVisibility(View.VISIBLE);
				linearLayoutOtherOptions.setVisibility(View.GONE);
				CustomCountDownTimer customCountDownTimer = new CustomCountDownTimer(timerDuration, 5);
				customCountDownTimer.start();
			}
			else{
				throw new Exception();
			}
		} catch(Exception e){
			linearLayoutWaiting.setVisibility(View.GONE);
			linearLayoutOtherOptions.setVisibility(View.VISIBLE);

		}

		try{
			if(!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
				linearLayoutGiveAMissedCall.setVisibility(View.VISIBLE);
			}
			else{
				linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			}

			if(1 == Data.otpViaCallEnabled || linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()) {
				buttonOtpViaCall.setVisibility(View.VISIBLE);
			}
			else{
				buttonOtpViaCall.setVisibility(View.GONE);
			}
			if(linearLayoutGiveAMissedCall.getVisibility() == View.VISIBLE
					&& buttonOtpViaCall.getVisibility() == View.VISIBLE){
				textViewOr.setVisibility(View.VISIBLE);
			} else{
				textViewOr.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
			linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			buttonOtpViaCall.setVisibility(View.GONE);
			textViewOr.setVisibility(View.GONE);
		}

		new DeviceTokenGenerator().generateDeviceToken(this, new IDeviceTokenReceiver() {

			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver" +
						"", Data.deviceToken + "..");
			}
		});

		OTP_SCREEN_OPEN = "yes";




		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				retrieveOTPFromSMS(getIntent());
			}
		}, 100);

	}

	private String getLoggedInAccesToken(){
		if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
			return facebookRegisterData.accessToken;
		}
		else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
			return googleRegisterData.accessToken;
		}
		else{
			return emailRegisterData.accessToken;
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
			linearLayoutWaiting.setVisibility(View.GONE);
			linearLayoutOtherOptions.setVisibility(View.VISIBLE);
		}
	}


	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(linearLayoutWaiting.getVisibility() == View.VISIBLE){
							scrollView.smoothScrollTo(0, editTextOTP.getBottom());
						} else {
							scrollView.smoothScrollTo(0, buttonVerify.getTop());
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
					if(linearLayoutWaiting.getVisibility() == View.VISIBLE){
						scrollView.smoothScrollTo(0, editTextOTP.getBottom());
					} else {
						scrollView.smoothScrollTo(0, buttonVerify.getTop());
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
	public void verifyOtpViaEmail(final Activity activity, String otp, final int linkedWallet) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

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
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params.toString());

				RestClient.getApiServices().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
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
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.EMAIL_OTP);
										Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
										Database.getInstance(OTPConfirmScreen.this).close();
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
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							DialogPopup.dismissLoadingDialog();
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


	public void verifyOtpViaFB(final Activity activity, String otp, final int linkedWallet) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

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
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params);


				RestClient.getApiServices().verifyOtp(params, new Callback<LoginResponse>() {
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
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.FACEBOOK_OTP);
										loginDataFetched = true;
										Database.getInstance(OTPConfirmScreen.this).insertEmail(facebookRegisterData.fbUserEmail);
										Database.getInstance(OTPConfirmScreen.this).close();
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
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();

				if (Data.locationFetcher != null) {
					Data.loginLatitude = Data.locationFetcher.getLatitude();
					Data.loginLongitude = Data.locationFetcher.getLongitude();
				}

				params.put("user_google_id", googleRegisterData.id);
				params.put("email", googleRegisterData.email);
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
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params);

				RestClient.getApiServices().verifyOtp(params, new Callback<LoginResponse>() {
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
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.GOOGLE_OTP);
										loginDataFetched = true;
										Database.getInstance(OTPConfirmScreen.this).insertEmail(googleRegisterData.email);
										Database.getInstance(OTPConfirmScreen.this).close();
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
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put("phone_no", phoneNo);
			Log.i("phone_no", ">" + phoneNo);

			RestClient.getApiServices().sendOtpViaCall(params, new Callback<SettleUserDebt>() {
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
			Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
			intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.SIGNUP.getOrdinal());
			intent.putExtra(KEY_BACK_FROM_OTP, true);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
			intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.LOGIN.getOrdinal());
			intent.putExtra(KEY_BACK_FROM_OTP, true);
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


	private void retrieveOTPFromSMS(Intent intent){
		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");

				if(message.toLowerCase().contains("paytm")){
					otp = message.split("\\ ")[0];
				} else{
					String[] arr = message.split("Your\\ One\\ Time\\ Password\\ is\\ ");
					otp = arr[1];
					otp = otp.replaceAll("\\.", "");
				}
			} else if(intent.hasExtra(KEY_OTP)){
				otp = intent.getStringExtra(KEY_OTP);
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
	}

	public void generateOTP(final String accessToken) {
		try {
			if(AppStatus.getInstance(OTPConfirmScreen.this).isOnline(OTPConfirmScreen.this)) {
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				RestClient.getApiServices().paytmRequestOtp(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "paytmRequestOtp response = " + responseStr);
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
						Log.e(TAG, "paytmRequestOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
					}
				});
			} else{
				DialogPopup.dialogNoInternet(OTPConfirmScreen.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								generateOTP(accessToken);
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

}





