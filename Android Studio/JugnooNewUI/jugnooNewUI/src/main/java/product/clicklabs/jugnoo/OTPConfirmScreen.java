package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
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
	ImageView imageViewYellowLoadingBar, imageViewWalletIcon;

	Button buttonVerify, buttonOtpViaCall;
	TextView textViewOr;
	LinearLayout linearLayoutOtherOptions, linearLayoutGiveAMissedCall;
	private Animation tweenAnimation;

	RelativeLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll, textViewSkip;
	
	boolean loginDataFetched = false;
	private int linkedWallet = 0;
	//private int userVerified = 0;
	private String linkedWalletErrorMsg = "";
	
	public static boolean intentFromRegister = true, backFromMissedCall;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;
	public static GoogleRegisterData googleRegisterData;
	private boolean giveAMissedCall;
	private Handler handler = new Handler();
	private String signupBy = "", email = "", password = "";
	private RelativeLayout rlProgress;
	private ProgressWheel progressBar;
	private boolean runAfterDelay;
	private TextView tvProgress;
	private boolean onlyDigits, openHomeSwitcher = false;


	@Override
	protected void onStart() {
		super.onStart();
//		FlurryAgent.init(this, Config.getFlurryKey());
//		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
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

		loginDataFetched = false;
		openHomeSwitcher = false;

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

                if(email.length() > 0){
                    onlyDigits = Utils.checkIfOnlyDigits(email);
                }

            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*if(getIntent().hasExtra(USER_VERIFIED)){
			userVerified = getIntent().getIntExtra(USER_VERIFIED, 0);
		}*/


		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenRegular(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);

		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);
		imageViewWalletIcon = (ImageView) findViewById(R.id.imageViewWalletIcon);

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
		textViewSkip = (TextView)findViewById(R.id.textViewSkip); textViewSkip.setTypeface(Fonts.mavenRegular(this));
		rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
		tvProgress = (TextView) findViewById(R.id.tvProgress); tvProgress.setTypeface(Fonts.mavenRegular(this));
		progressBar = (ProgressWheel) findViewById(R.id.progressBar);

		/*if(userVerified == 1){
			textViewSkip.setVisibility(View.VISIBLE);
		} else{
			textViewSkip.setVisibility(View.GONE);
		}*/

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

		textViewSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFacebookLoginValues(OTPConfirmScreen.this);
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
					rlProgress.setVisibility(View.GONE);
					if (SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
						/*if(userVerified == 1){
							sendOTP(otpCode);
						} else {
							verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
						}*/

					} else if (SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
						verifyOtpViaGoogle(OTPConfirmScreen.this, otpCode, linkedWallet);
					} else {
						verifyOtpViaEmail(OTPConfirmScreen.this, otpCode, linkedWallet);
					}
					FlurryEventLogger.event(OTP_VERIFIED_WITH_SMS);
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.VERIFY_ME, bundle);
					FlurryEventLogger.eventGA(ACQUISITION, TAG, "Verify me");
				} else {
					editTextOTP.requestFocus();
					editTextOTP.setError("OTP can't be empty");
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
					rlProgress.setVisibility(View.GONE);
					if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()
							|| linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()
							|| linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
						generateOTP(getLoggedInAccesToken(), linkedWallet);
					} else{
						if (1 == Prefs.with(OTPConfirmScreen.this).getInt(SP_OTP_VIA_CALL_ENABLED, 1)) {
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
					rlProgress.setVisibility(View.GONE);
					linearLayoutGiveAMissedCall.clearAnimation();
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
										FlurryEventLogger.event(GIVE_MISSED_CALL);
                                        Bundle bundle = new Bundle();
                                        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.GIVE_A_MISS_CALL, bundle);
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
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.EDIT_PHONE_NUMBER, bundle);

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

		long timerDuration = 20000;
		textViewCounter.setText("0:20");

		if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_paytm_big);
			buttonOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_mobikwik_big);
			buttonOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_freecharge_big);
			buttonOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else{
			imageViewWalletIcon.setVisibility(View.GONE);
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
			if(!"".equalsIgnoreCase(Prefs.with(OTPConfirmScreen.this).getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
				linearLayoutGiveAMissedCall.setVisibility(View.VISIBLE);
			}
			else{
				linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			}

			if(1 == Prefs.with(OTPConfirmScreen.this).getInt(SP_OTP_VIA_CALL_ENABLED, 1)
					|| linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()
					|| linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()
					|| linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()) {
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
						rlProgress.setVisibility(View.GONE);
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
					rlProgress.setVisibility(View.GONE);
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

		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, OTPConfirmScreen.class.getName());
		Utils.enableSMSReceiver(this);

		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(OTPConfirmScreen.this, 1000);
		}
		HomeActivity.checkForAccessTokenChange(this);

		try {
			if(giveAMissedCall) {
				giveAMissedCall = false;
				//buttonVerify.performClick();
				rlProgress.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				tvProgress.setText(getResources().getString(R.string.trying_to_verify));
				progressBar.spin();
				if (signupBy.equalsIgnoreCase("email")) {
					if (onlyDigits) {
						email = "+91" + email;
						sendLoginValues(OTPConfirmScreen.this, email, password, true);
					} else {
						sendLoginValues(OTPConfirmScreen.this, email, password, false);
					}
				} else if (signupBy.equalsIgnoreCase("facebook")) {
					sendFacebookLoginValues(OTPConfirmScreen.this);
				} else if (signupBy.equalsIgnoreCase("google")) {
					sendGoogleLoginValues(OTPConfirmScreen.this);
				}
				// api call
				handler.postDelayed(runnable, 5000);
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
					sendLoginValues(OTPConfirmScreen.this, email, password, true);
				} else{
					sendLoginValues(OTPConfirmScreen.this, email, password, false);
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
		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, "");
		Utils.disableSMSReceiver(this);
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

	private void showErrorOnMissedCallBack(){
		if(runAfterDelay) {
			runAfterDelay = false;
			rlProgress.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tvProgress.setText(getResources().getString(R.string.we_could_not_verify));
		}
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
				params.put("device_token", MyApplication.getInstance().getDeviceToken());
                params.put("device_name", MyApplication.getInstance().deviceName());
                params.put("os_version", MyApplication.getInstance().osVersion());
                params.put("country", MyApplication.getInstance().country());
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getAutosClientId());
                params.put("otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
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
												loginResponse, LoginVia.EMAIL_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
										Database.getInstance(OTPConfirmScreen.this).close();
										loginDataFetched = true;
										firebaseEventWalletAtSignup();
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

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
                params.put("device_name", MyApplication.getInstance().deviceName());
                params.put("os_version", MyApplication.getInstance().osVersion());
                params.put("country", MyApplication.getInstance().country());
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getAutosClientId());
                params.put("otp", otp);
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
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.FACEBOOK_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
										Database.getInstance(OTPConfirmScreen.this).insertEmail(facebookRegisterData.fbUserEmail);
										Database.getInstance(OTPConfirmScreen.this).close();
										firebaseEventWalletAtSignup();
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

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
				params.put("device_name", MyApplication.getInstance().deviceName());
				params.put("os_version", MyApplication.getInstance().osVersion());
				params.put("country", MyApplication.getInstance().country());
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getAutosClientId());
				params.put("otp", otp);
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
								} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
									if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.GOOGLE_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
										Database.getInstance(OTPConfirmScreen.this).insertEmail(googleRegisterData.email);
										Database.getInstance(OTPConfirmScreen.this).close();
										firebaseEventWalletAtSignup();
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
					Data.splashIntentUri, new LatLng(Data.loginLatitude, Data.loginLongitude), false);
//			Intent intent = new Intent(OTPConfirmScreen.this, HomeActivity.class);
//			intent.setData(Data.splashIntentUri);
//			startActivity(intent);
//			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			ActivityCompat.finishAffinity(this);
		}
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	public void performBackPressed(){
        Bundle bundle = new Bundle();
        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.BACK, bundle);
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
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
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


	@Override
	public void onLocationChanged(Location location) {
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
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
					editTextOTP.setText(otp);
					editTextOTP.setSelection(editTextOTP.getText().length());
					buttonVerify.performClick();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void generateOTP(final String accessToken, final int linkedWallet) {
		try {
			if(AppStatus.getInstance(OTPConfirmScreen.this).isOnline(OTPConfirmScreen.this)) {
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
			if(AppStatus.getInstance(OTPConfirmScreen.this).isOnline(OTPConfirmScreen.this)) {
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
						try {
							textViewSkip.performClick();
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
						}
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

	private void firebaseEventWalletAtSignup(){
		if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.PAYTM, new Bundle());
		} else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.MOBIKWIK, new Bundle());
		} else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.FREECHARGE, new Bundle());
		}
	}

	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			//resetFlags();
			//DialogPopup.showLoadingDialog(activity, "Trying to verify through missed call...");

			HashMap<String, String> params = new HashMap<>();

			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

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
			String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
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
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.EMAIL,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									Database.getInstance(OTPConfirmScreen.this).insertEmail(emailId);
									loginDataFetched = true;
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
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}


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
			String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
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
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login with facebook");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.FACEBOOK,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									loginDataFetched = true;

									Database.getInstance(OTPConfirmScreen.this).insertEmail(Data.facebookUserData.userEmail);
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
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

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
			String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
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
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.GOOGLE,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Login with Google");
									loginDataFetched = true;

									Database.getInstance(OTPConfirmScreen.this).insertEmail(Data.googleSignInAccount.getEmail());
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

}





