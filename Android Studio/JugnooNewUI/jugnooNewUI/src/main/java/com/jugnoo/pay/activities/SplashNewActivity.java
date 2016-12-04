package com.jugnoo.pay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;
import com.github.jksiezni.permissive.PermissiveMessenger;
import com.github.jksiezni.permissive.Rationale;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jugnoo.pay.R;
import com.jugnoo.pay.apis.ApiLoginUsingAccessToken;
import com.jugnoo.pay.config.Config;
import com.jugnoo.pay.datastructure.EmailRegisterData;
import com.jugnoo.pay.datastructure.FacebookRegisterData;
import com.jugnoo.pay.datastructure.GoogleRegisterData;
import com.jugnoo.pay.datastructure.LinkedWalletStatus;
import com.jugnoo.pay.datastructure.LoginVia;
import com.jugnoo.pay.datastructure.PreviousAccountInfo;
import com.jugnoo.pay.location.LocationFetcher;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.LoginResponse;
import com.jugnoo.pay.models.SettleUserDebt;
import com.jugnoo.pay.models.VerifyRegisterResponse;
import com.jugnoo.pay.models.VerifyUserRequest;
import com.jugnoo.pay.retrofit.RestClient;
import com.jugnoo.pay.retrofit.RetrofitClient;
import com.jugnoo.pay.retrofit.WebApi;
import com.jugnoo.pay.services.GCMIntentService;
import com.jugnoo.pay.utils.ASSL;
import com.jugnoo.pay.utils.AccessTokenGenerator;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.AppStatus;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.Constants;
import com.jugnoo.pay.utils.Data;
import com.jugnoo.pay.utils.Database;
import com.jugnoo.pay.utils.DialogPopup;
import com.jugnoo.pay.utils.FacebookLoginCallback;
import com.jugnoo.pay.utils.FacebookLoginHelper;
import com.jugnoo.pay.utils.FacebookUserData;
import com.jugnoo.pay.utils.Fonts;
import com.jugnoo.pay.utils.GoogleSigninActivity;
import com.jugnoo.pay.utils.JSONParser;
import com.jugnoo.pay.utils.KeyboardLayoutListener;
import com.jugnoo.pay.utils.MyApplication;
import com.jugnoo.pay.utils.Prefs;
import com.jugnoo.pay.utils.SHA256Convertor;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.UniqueIMEIID;
import com.jugnoo.pay.utils.Utils;
import com.yesbank.Registration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SplashNewActivity extends BaseActivity implements Constants, LocationFetcher.LocationUpdate {

	//adding drop location

	RelativeLayout root;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	private final String TAG = "Splash Screen";

	ImageView viewInitJugnoo, viewInitLS, viewInitSplashJugnoo;
	RelativeLayout relativeLayoutJugnooLogo;
	ImageView imageViewBack, imageViewJugnooLogo;
	ImageView imageViewDebug1, imageViewDebug2, imageViewDebug3;

	RelativeLayout relativeLayoutLS;
	LinearLayout linearLayoutLoginSignupButtons;
	Button buttonLogin, buttonRegister;
	TextView textViewTerms;
	LinearLayout linearLayoutNoNet;
	TextView textViewNoNet;
	Button buttonNoNetCall, buttonRefresh;

	LinearLayout linearLayoutLogin;
	AutoCompleteTextView editTextEmail;
	EditText editTextPassword;
	TextView textViewEmailRequired, textViewPasswordRequired, textViewForgotPassword;
	Button buttonEmailLogin, buttonFacebookLogin, buttonGoogleLogin;

	RelativeLayout relativeLayoutSignup, relativeLayoutScrollStop;
	EditText editTextSName, editTextSEmail, editTextSPhone, editTextSPassword, editTextSPromo;
	TextView textViewSNameRequired, textViewSEmailRequired, textViewSPhoneRequired, textViewSPasswordRequired;
	Button buttonEmailSignup, buttonFacebookSignup, buttonGoogleSignup;
	TextView textViewSTerms;

	LinearLayout linearLayoutWalletContainer, linearLayoutWalletContainerInner,
			linearLayoutPaytm, linearLayoutMobikwik, linearLayoutFreeCharge, linearLayoutNone;
	ImageView imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, imageViewRadioNone;


	boolean loginDataFetched = false, resumed = false, newActivityStarted = false;

	int debugState = 0;
	boolean hold1 = false, hold2 = false;
	boolean holdForBranch = false;
	int clickCount = 0, linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
	private String phoneNumber = "";

	private State state = State.SPLASH_LS;


	CallbackManager callbackManager;
	FacebookLoginHelper facebookLoginHelper;

	boolean emailRegister = false, facebookRegister = false, googleRegister = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "", accessToken = "",
			emailNeedRegister = "", linkedWalletErrorMsg = "";
	private String enteredEmail = "";
	public static boolean phoneNoLogin = false;
	private static final int GOOGLE_SIGNIN_REQ_CODE_LOGIN = 1124;

	public void resetFlags() {
		loginDataFetched = false;
		emailRegister = false;
		facebookRegister = false;
		googleRegister = false;
		sendToOtpScreen = false;
		phoneNoOfUnverifiedAccount = "";
		otpErrorMsg = "";
		notRegisteredMsg = "";
		emailNeedRegister = "";
	}


	String name = "", referralCode = "", emailId = "", phoneNo = "", password = "";
	public static RegisterationType registerationType = RegisterationType.EMAIL;
	public static JSONObject multipleCaseJSON;
	private LocationFetcher locationFetcher;


	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
	}


	@Override
	public void onStart() {
		super.onStart();


//		FlurryAgent.init(this, Config.getFlurryKey());
//		FlurryAgent.onStartSession(this, Config.getFlurryKey());
//		FlurryAgent.onEvent("Splash started");

		firstTimeEvents();
	}


	private void fetchPhoneNoOtpFromBranchParams(JSONObject referringParams){
		try {
			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
			if ("".equalsIgnoreCase(pair.first)) {
				String email = referringParams.optString(KEY_EMAIL, "");
				String phoneNumber = referringParams.optString(KEY_PHONE_NO, "");
				String otp = referringParams.optString(KEY_OTP, "");
				if (!"".equalsIgnoreCase(otp)) {
					verifyOtpViaEmail(this, email, phoneNumber, otp);
				} else{
					throw new Exception();
				}
			}
		} catch (Exception e) {
			readSMSClickLink();
		}
	}


	public boolean isBranchLinkNotClicked(){
		return (!holdForBranch
				|| clickCount > 1);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {


			//MyApplication.getInstance().initializeServerURL(this);

			// this method is to get hash key for windows system.
			// Utils.method1(this);

			// Utils.method1(this);

			FacebookSdk.sdkInitialize(this);

			Prefs.with(this).save(SP_OTP_SCREEN_OPEN, "");
			Utils.disableSMSReceiver(this);

			Data.locationSettingsNoPressed = false;

			Data.userData = null;


//			FlurryAgent.init(this, Config.getFlurryKey());


			Locale locale = new Locale("en");
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


			setContentView(R.layout.activity_splash_new);


			try{
				if(getIntent().getIntExtra(KEY_LOGGED_OUT, 0) == 1){
					String message = getIntent().getStringExtra(KEY_MESSAGE);
					DialogPopup.alertPopup(this, "", message);
				}
			} catch (Exception e){
				e.printStackTrace();
			}

			resumed = false;

			debugState = 0;

			resetFlags();
			enteredEmail = "";

			hold1 = false;
			hold2 = false;

			root = (RelativeLayout) findViewById(R.id.root);
			new ASSL(SplashNewActivity.this, root, 1134, 720, false);

			holdForBranch = false;
			clickCount = 0;


			linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
			textViewScroll = (TextView) findViewById(R.id.textViewScroll);

			viewInitJugnoo = (ImageView) findViewById(R.id.viewInitJugnoo);
			viewInitSplashJugnoo = (ImageView) findViewById(R.id.viewInitSplashJugnoo);
			viewInitLS = (ImageView) findViewById(R.id.viewInitLS);

			relativeLayoutJugnooLogo = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooLogo);
			imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
			imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
			imageViewDebug1 = (ImageView) findViewById(R.id.imageViewDebug1);
			imageViewDebug2 = (ImageView) findViewById(R.id.imageViewDebug2);
			imageViewDebug3 = (ImageView) findViewById(R.id.imageViewDebug3);

			relativeLayoutLS = (RelativeLayout) findViewById(R.id.relativeLayoutLS);
			linearLayoutLoginSignupButtons = (LinearLayout) findViewById(R.id.linearLayoutLoginSignupButtons);
			buttonLogin = (Button) findViewById(R.id.buttonLogin);
			buttonLogin.setTypeface(Fonts.mavenRegular(this));
			buttonRegister = (Button) findViewById(R.id.buttonRegister);
			buttonRegister.setTypeface(Fonts.mavenRegular(this));
			textViewTerms = (TextView) findViewById(R.id.textViewTerms);
			textViewTerms.setTypeface(Fonts.mavenMedium(this));
			((TextView) findViewById(R.id.textViewAlreadyHaveAccount)).setTypeface(Fonts.mavenMedium(this));

			linearLayoutNoNet = (LinearLayout) findViewById(R.id.linearLayoutNoNet);
			textViewNoNet = (TextView) findViewById(R.id.textViewNoNet);
			textViewNoNet.setTypeface(Fonts.mavenMedium(this));
			buttonNoNetCall = (Button) findViewById(R.id.buttonNoNetCall);
			buttonNoNetCall.setTypeface(Fonts.mavenRegular(this));
			buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
			buttonRefresh.setTypeface(Fonts.mavenRegular(this));

			ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
			scale.setDuration(300);
			scale.setInterpolator(new OvershootInterpolator());

			//buttonLogin.startAnimation(scale);


			String[] emails = Database.getInstance(this).getEmails();
			ArrayAdapter<String> adapter;
			if (emails == null) {
				emails = new String[]{};
			}
			adapter = new ArrayAdapter<>(this, R.layout.dropdown_textview, emails);
			adapter.setDropDownViewResource(R.layout.dropdown_textview);

			linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
			editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
			editTextEmail.setTypeface(Fonts.mavenMedium(this));
			editTextEmail.setAdapter(adapter);
			editTextPassword = (EditText) findViewById(R.id.editTextPassword);
			editTextPassword.setTypeface(Fonts.mavenMedium(this));
			textViewEmailRequired = (TextView) findViewById(R.id.textViewEmailRequired);
			textViewEmailRequired.setTypeface(Fonts.mavenMedium(this));
			textViewPasswordRequired = (TextView) findViewById(R.id.textViewPasswordRequired);
			textViewPasswordRequired.setTypeface(Fonts.mavenMedium(this));
			((TextView) findViewById(R.id.textViewLoginOr)).setTypeface(Fonts.mavenMedium(this));
			textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
			textViewForgotPassword.setTypeface(Fonts.mavenRegular(this));
			buttonEmailLogin = (Button) findViewById(R.id.buttonEmailLogin);
			buttonEmailLogin.setTypeface(Fonts.mavenRegular(this));
			buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin);
			buttonFacebookLogin.setTypeface(Fonts.mavenRegular(this));
			buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
			buttonGoogleLogin.setTypeface(Fonts.mavenRegular(this));


			relativeLayoutSignup = (RelativeLayout) findViewById(R.id.relativeLayoutSignup);
			relativeLayoutScrollStop = (RelativeLayout) findViewById(R.id.relativeLayoutScrollStop);
			editTextSName = (EditText) findViewById(R.id.editTextSName);
			editTextSName.setTypeface(Fonts.mavenMedium(this));
			editTextSEmail = (EditText) findViewById(R.id.editTextSEmail);
			editTextSEmail.setTypeface(Fonts.mavenMedium(this));
			editTextSPhone = (EditText) findViewById(R.id.editTextSPhone);
			editTextSPhone.setTypeface(Fonts.mavenMedium(this));
			editTextSPassword = (EditText) findViewById(R.id.editTextSPassword);
			editTextSPassword.setTypeface(Fonts.mavenMedium(this));
			editTextSPromo = (EditText) findViewById(R.id.editTextSPromo);
			editTextSPromo.setTypeface(Fonts.mavenMedium(this));
			textViewSNameRequired = (TextView) findViewById(R.id.textViewSNameRequired);
			textViewSNameRequired.setTypeface(Fonts.mavenMedium(this));
			textViewSEmailRequired = (TextView) findViewById(R.id.textViewSEmailRequired);
			textViewSEmailRequired.setTypeface(Fonts.mavenMedium(this));
			textViewSPhoneRequired = (TextView) findViewById(R.id.textViewSPhoneRequired);
			textViewSPhoneRequired.setTypeface(Fonts.mavenMedium(this));
			textViewSPasswordRequired = (TextView) findViewById(R.id.textViewSPasswordRequired);
			textViewSPasswordRequired.setTypeface(Fonts.mavenMedium(this));
			((TextView) findViewById(R.id.textViewSignupOr)).setTypeface(Fonts.mavenMedium(this));
			((TextView) findViewById(R.id.textViewSPhone91)).setTypeface(Fonts.mavenMedium(this));
			buttonEmailSignup = (Button) findViewById(R.id.buttonEmailSignup);
			buttonEmailSignup.setTypeface(Fonts.mavenRegular(this));
			buttonFacebookSignup = (Button) findViewById(R.id.buttonFacebookSignup);
			buttonFacebookSignup.setTypeface(Fonts.mavenRegular(this));
			buttonGoogleSignup = (Button) findViewById(R.id.buttonGoogleSignup);
			buttonGoogleSignup.setTypeface(Fonts.mavenRegular(this));
			textViewSTerms = (TextView) findViewById(R.id.textViewSTerms);
			textViewSTerms.setTypeface(Fonts.mavenMedium(this));

			linearLayoutWalletContainer = (LinearLayout) findViewById(R.id.linearLayoutWalletContainer);
			linearLayoutWalletContainerInner = (LinearLayout) findViewById(R.id.linearLayoutWalletContainerInner);
			linearLayoutPaytm = (LinearLayout) findViewById(R.id.linearLayoutPaytm);
			linearLayoutMobikwik = (LinearLayout) findViewById(R.id.linearLayoutMobikwik);
			linearLayoutFreeCharge = (LinearLayout) findViewById(R.id.linearLayoutFreeCharge);
			linearLayoutNone = (LinearLayout) findViewById(R.id.linearLayoutNone);
			((TextView) findViewById(R.id.textViewLinkWalletMessage)).setTypeface(Fonts.mavenMedium(this));
			((TextView) findViewById(R.id.textViewNone)).setTypeface(Fonts.mavenMedium(this));
			imageViewRadioPaytm = (ImageView) findViewById(R.id.imageViewRadioPaytm);
			imageViewRadioMobikwik = (ImageView) findViewById(R.id.imageViewRadioMobikwik);
			imageViewRadioFreeCharge = (ImageView) findViewById(R.id.imageViewRadioFreeCharge);
			imageViewRadioNone = (ImageView) findViewById(R.id.imageViewRadioNone);
			locationFetcher = new LocationFetcher(this, 5000, 2);

			root.setOnClickListener(onClickListenerKeybordHide);

			relativeLayoutJugnooLogo.setOnClickListener(onClickListenerKeybordHide);

			KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
					new KeyboardLayoutListener.KeyBoardStateHandler() {
						@Override
						public void keyboardOpened() {
	//						if(State.LOGIN == state){
	//							relativeLayoutJugnooLogo.setVisibility(View.GONE);
	//						}
						}

						@Override
						public void keyBoardClosed() {
	//						if(State.LOGIN == state){
	//							relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
	//						}
						}
					});
			keyboardLayoutListener.setResizeTextView(false);
			linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

			linearLayoutPaytm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal();
					setLinkedWalletTick();
				}
			});

			linearLayoutMobikwik.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					linkedWallet = LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal();
					setLinkedWalletTick();
				}
			});

			linearLayoutFreeCharge.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					linkedWallet = LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal();
					setLinkedWalletTick();
				}
			});

			linearLayoutNone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
					setLinkedWalletTick();
				}
			});


			buttonLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isBranchLinkNotClicked()) {
                        Bundle bundle = new Bundle();
						changeUIState(State.LOGIN);
					} else {
						clickCount = clickCount + 1;
					}
				}
			});

			buttonRegister.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isBranchLinkNotClicked()) {
						SplashNewActivity.registerationType = RegisterationType.EMAIL;
						changeUIState(State.SIGNUP);
					} else{
						clickCount = clickCount + 1;
					}
				}
			});

			textViewTerms.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
	//				try {
	//					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/#/terms"));
	//					startActivity(browserIntent);
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
				}
			});

			buttonNoNetCall.setVisibility(View.GONE);
			buttonNoNetCall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.openCallIntent(SplashNewActivity.this, Config.getSupportNumber(SplashNewActivity.this));
				}
			});

			buttonRefresh.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!loginDataFetched) {
						getDeviceToken();
					}
				}
			});


			imageViewBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (State.LOGIN == state) {
						performLoginBackPressed();
					} else if (State.SIGNUP == state) {
						performSignupBackPressed();
					}
					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
				}
			});


			imageViewDebug1.setOnClickListener(onClickListenerKeybordHide);
			imageViewDebug2.setOnClickListener(onClickListenerKeybordHide);
			imageViewDebug3.setOnClickListener(onClickListenerKeybordHide);
			imageViewDebug1.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					hold1 = true;
					callAfterBothHoldSuccessfully();
					return false;
				}
			});

			imageViewDebug2.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					hold2 = true;
					callAfterBothHoldSuccessfully();
					return false;
				}
			});

			imageViewDebug3.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (debugState == 1) {
						//confirmDebugPasswordPopup(SplashNewActivity.this);
						resetDebugFlags();
					}
					return false;
				}
			});

			editTextEmail.addTextChangedListener(new CustomTextWatcher(textViewEmailRequired));
			editTextPassword.addTextChangedListener(new CustomTextWatcher(textViewPasswordRequired));
			editTextEmail.setOnFocusChangeListener(onFocusChangeListener);
			editTextPassword.setOnFocusChangeListener(onFocusChangeListener);

			buttonEmailLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
					String email = editTextEmail.getText().toString().trim();
					String password = editTextPassword.getText().toString().trim();
					if ("".equalsIgnoreCase(email)) {
						editTextEmail.requestFocus();
						editTextEmail.setError(getResources().getString(R.string.nl_login_email_empty_error));
					} else {
						if ("".equalsIgnoreCase(password)) {
							editTextPassword.requestFocus();
							editTextPassword.setError(getResources().getString(R.string.nl_login_empty_password_error));
						} else {
							boolean onlyDigits = Utils.checkIfOnlyDigits(email);
							if (onlyDigits) {
								email = Utils.retrievePhoneNumberTenChars(email);
								if (!Utils.validPhoneNumber(email)) {
									editTextEmail.requestFocus();
									editTextEmail.setError(getResources().getString(R.string.invalid_phone_error));
								} else {
									email = "+91" + email;
									sendLoginValues(SplashNewActivity.this, email, password, true);
									phoneNoLogin = true;
								}
							} else {
								if (Utils.isEmailValid(email)) {
									enteredEmail = email;
									sendLoginValues(SplashNewActivity.this, email, password, false);
									phoneNoLogin = false;
								} else {
									editTextEmail.requestFocus();
									editTextEmail.setError("Please enter valid email");
								}
							}
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


			buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
						Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
						facebookLoginHelper.openFacebookSession();
					} else {
						DialogPopup.dialogNoInternet(SplashNewActivity.this,
								Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
								new Utils.AlertCallBackWithButtonsInterface() {
									@Override
									public void positiveClick(View v) {
										buttonFacebookLogin.performClick();
									}

									@Override
									public void neutralClick(View v) {
									}

									@Override
									public void negativeClick(View v) {
									}
								});
					}
				}
			});
			buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
					startActivityForResult(new Intent(SplashNewActivity.this, GoogleSigninActivity.class),
							GOOGLE_SIGNIN_REQ_CODE_LOGIN);

					} else{
						DialogPopup.dialogNoInternet(SplashNewActivity.this,
								Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
								new Utils.AlertCallBackWithButtonsInterface() {
									@Override
									public void positiveClick(View v) {
										buttonGoogleLogin.performClick();
									}

									@Override
									public void neutralClick(View v) {
									}

									@Override
									public void negativeClick(View v) {
									}
								});
					}
				}
			});
			textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
					ForgotPasswordScreen.emailAlready = editTextEmail.getText().toString();
					startActivity(new Intent(SplashNewActivity.this, ForgotPasswordScreen.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					finish();
				}
			});

			callbackManager = CallbackManager.Factory.create();

			facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
				@Override
				public void facebookLoginDone(FacebookUserData facebookUserData) {
					Data.facebookUserData = facebookUserData;
					if(State.LOGIN == state || State.SIGNUP == state) {
						sendFacebookLoginValues(SplashNewActivity.this);
					}
				}

				@Override
				public void facebookLoginError(String message) {
					Utils.showToast(getApplicationContext(), message);
				}
			});
			editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					editTextPassword.requestFocus();
					return true;
				}
			});
			editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					buttonEmailLogin.performClick();
					return true;
				}
			});

			editTextSName.addTextChangedListener(new CustomTextWatcher(textViewSNameRequired));
			editTextSEmail.addTextChangedListener(new CustomTextWatcher(textViewSEmailRequired));
			editTextSPhone.addTextChangedListener(new CustomTextWatcher(textViewSPhoneRequired));
			editTextSPassword.addTextChangedListener(new CustomTextWatcher(textViewSPasswordRequired));

			editTextSName.setOnFocusChangeListener(onFocusChangeListener);
			editTextSEmail.setOnFocusChangeListener(onFocusChangeListener);
			editTextSPhone.setOnFocusChangeListener(onFocusChangeListener);
			editTextSPassword.setOnFocusChangeListener(onFocusChangeListener);
			editTextSPromo.setOnFocusChangeListener(onFocusChangeListener);

			buttonEmailSignup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextSName);

					String name = editTextSName.getText().toString().trim();
					if (name.length() > 0) {
						name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
					}
					String referralCode = editTextSPromo.getText().toString().trim();
					String emailId = editTextSEmail.getText().toString().trim();
					boolean noFbEmail = false;

					if (RegisterationType.FACEBOOK == registerationType && emailId.equalsIgnoreCase("")) {
						emailId = "n@n.c";
						noFbEmail = true;
					}


					String phoneNo = editTextSPhone.getText().toString().trim();
					String password = editTextSPassword.getText().toString().trim();


					if ("".equalsIgnoreCase(name) || (name.startsWith("."))) {
						editTextSName.requestFocus();
						editTextSName.setError("Please enter name");
					} else if (!Utils.hasAlphabets(name)) {
						editTextSName.requestFocus();
						editTextSName.setError("Please enter at least one alphabet");
					} else {
						if ("".equalsIgnoreCase(emailId)) {
							editTextSEmail.requestFocus();
							editTextSEmail.setError("Please enter email id");
						} else {
							if ("".equalsIgnoreCase(phoneNo)) {
								editTextSPhone.requestFocus();
								editTextSPhone.setError("Please enter phone number");
							} else {
								phoneNo = Utils.retrievePhoneNumberTenChars(phoneNo);
								if (!Utils.validPhoneNumber(phoneNo)) {
									editTextSPhone.requestFocus();
									editTextSPhone.setError("Please enter valid phone number");
								} else {
									phoneNo = "+91" + phoneNo;
									if ("".equalsIgnoreCase(password)) {
										editTextSPassword.requestFocus();
										editTextSPassword.setError("Please enter password");
									} else {
										if ((linkedWallet == LinkedWalletStatus.NO_WALLET.getOrdinal() && Utils.isEmailValid(emailId))
												||
												((linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()
														|| linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()
														|| linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal())
														&& Utils.isEmailValid(emailId) && !emailId.contains("+"))
												) {
											if (password.length() >= 6) {
												Prefs.with(SplashNewActivity.this).save(SP_REFERRAL_CODE, referralCode);
												if (RegisterationType.FACEBOOK == registerationType) {
													if (noFbEmail) {
														emailId = "";
													}
													sendFacebookSignupValues(SplashNewActivity.this, referralCode, phoneNo, password, linkedWallet);
												} else if (RegisterationType.GOOGLE == registerationType) {
													sendGoogleSignupValues(SplashNewActivity.this, referralCode, phoneNo, password, linkedWallet);
												} else {
													sendSignupValues(SplashNewActivity.this, name, referralCode, emailId, phoneNo, password, linkedWallet);
												}
											} else {
												editTextSPassword.requestFocus();
												editTextSPassword.setError("Password must be of atleast six characters");
											}

										} else {
											editTextSEmail.requestFocus();
											editTextSEmail.setError("Please enter valid email id");
										}

									}
								}
							}
						}
					}

				}
			});
			buttonFacebookSignup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buttonFacebookLogin.performClick();
				}
			});
			buttonGoogleSignup.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buttonGoogleLogin.performClick();
				}
			});
			editTextSName.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					editTextSEmail.requestFocus();
					return true;
				}
			});
			editTextSEmail.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					editTextSPhone.requestFocus();
					return true;
				}
			});
			editTextSPhone.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					editTextSPassword.requestFocus();
					return true;
				}
			});
			editTextSPassword.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					editTextSPromo.requestFocus();
					return true;
				}
			});
			editTextSPromo.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					buttonEmailSignup.performClick();
					return true;
				}
			});
			textViewSTerms.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/#/terms"));
						startActivity(browserIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			relativeLayoutScrollStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});



			initiateDeviceInfoVariables();
			showLocationEnableDialog();

			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


			try {
				if (getIntent().hasExtra(KEY_SPLASH_STATE)) {
					int stateInt = getIntent().getIntExtra(KEY_SPLASH_STATE, State.SPLASH_INIT.getOrdinal());
					if (State.LOGIN.getOrdinal() == stateInt) {
						state = State.LOGIN;
					} else if (State.SIGNUP.getOrdinal() == stateInt) {
						state = State.SIGNUP;
					} else if (State.SPLASH_LS.getOrdinal() == stateInt) {
						state = State.SPLASH_LS;
					} else {
						state = State.SPLASH_INIT;
					}
				} else {
					state = State.SPLASH_INIT;
				}
			} catch (Exception e) {
				e.printStackTrace();
				state = State.SPLASH_INIT;
			}

			changeUIState(state);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logSome();


        if(Utils.isAppInstalled(this, POKEMON_GO_APP_PACKAGE)
                && Prefs.with(this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, -1) == -1){
            Prefs.with(this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 1);
        }


		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverDeviceToken,
				new IntentFilter(INTENT_ACTION_DEVICE_TOKEN_UPDATE));

	}

	private void logSome(){
		Log.i("Splash", "temp");
	}

	private void changeUIState(State state) {
		imageViewJugnooLogo.requestFocus();
		relativeLayoutScrollStop.setVisibility(View.VISIBLE);
		switch (state) {
			case SPLASH_INIT:
				viewInitJugnoo.setVisibility(View.VISIBLE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewInitLS.setVisibility(View.VISIBLE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SPLASH_LS:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewInitLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SPLASH_NO_NET:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewInitLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.GONE);
				linearLayoutNoNet.setVisibility(View.VISIBLE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case LOGIN:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewInitLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SIGNUP:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewInitLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.GONE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.GONE);
				relativeLayoutScrollStop.setVisibility(View.GONE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				//getAllowedAuthChannels(SplashNewActivity.this);
				break;

		}
		this.state = state;

		if(State.SPLASH_INIT == state) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getDeviceToken();
				}
			}, 500);
		}
		else if(State.LOGIN == state){
			// set login screen values according to intent
			setLoginScreenValuesOnCreate();
		}
		else if(State.SIGNUP == state) {
			// set signupscreen values according to intent
			setSignupScreenValuesOnCreate();
		}
	}

	private void readSMSClickLink(){
		try {
			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
			if ("".equalsIgnoreCase(pair.first) && !Data.linkFoundOnce) {
				new ReadSMSClickLinkAsync().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callAfterBothHoldSuccessfully() {
		if (hold1 && hold2) {
			debugState = 1;
			root.setBackgroundColor(getResources().getColor(R.color.theme_color_pressed));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					root.setBackgroundResource(R.drawable.bg_img);
				}
			}, 200);
			hold1 = false;
			hold2 = false;
		}
	}

	private void resetDebugFlags() {
		hold1 = false;
		hold2 = false;
		debugState = 0;
	}


	private void sendToRegisterThroughSms(String referralCode) {
		if (!"".equalsIgnoreCase(referralCode)) {
			SplashNewActivity.registerationType = RegisterationType.EMAIL;
			setIntent(new Intent().putExtra(KEY_REFERRAL_CODE, referralCode));
			changeUIState(State.SIGNUP);
		}
	}

	private View.OnClickListener onClickListenerKeybordHide = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Utils.hideSoftKeyboard(SplashNewActivity.this, editTextSName);
		}
	};

	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus && v instanceof EditText) {
				((EditText) v).setError(null);
			}
		}
	};

	public void getDeviceToken() {
//				try {
//					FirebaseInstanceId.getInstance().getToken();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				if ("".equalsIgnoreCase(Prefs.with(this).getString(Constants.SP_DEVICE_TOKEN, ""))) {
					CallProgressWheel.showLoadingDialog(SplashNewActivity.this, "Loading...");
					getHandlerGoToAccessToken().removeCallbacks(getRunnableGoToAccessToken());
					getHandlerGoToAccessToken().postDelayed(getRunnableGoToAccessToken(), 5000);
				} else {
					goToAccessTokenLogin();
				}
	}

	Handler handlerGoToAccessToken = null;
	Runnable runnableGoToAccessToken = null;

	private Handler getHandlerGoToAccessToken(){
		if(handlerGoToAccessToken == null){
			handlerGoToAccessToken = new Handler();
		}
		return handlerGoToAccessToken;
	}

	private Runnable getRunnableGoToAccessToken(){
		if(runnableGoToAccessToken == null){
			runnableGoToAccessToken = new Runnable() {
				@Override
				public void run() {
					Log.e(TAG, "getRunnableGoToAccessToken running");
					goToAccessTokenLogin();
				}
			};
		}
		return runnableGoToAccessToken;
	}

	@Override
	public void onNewIntent(Intent intent) {
		this.setIntent(intent);
	}

	private BroadcastReceiver broadcastReceiverDeviceToken = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.hasExtra(KEY_DEVICE_TOKEN)) {
					getHandlerGoToAccessToken().removeCallbacks(getRunnableGoToAccessToken());
					goToAccessTokenLogin();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void goToAccessTokenLogin() {
		try {
			CallProgressWheel.dismissLoadingDialog();
			Log.e("deviceToken received", "> " + MyApplication.getInstance().getDeviceToken());
			accessTokenLogin(SplashNewActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		setPermissions();

		//retryAccessTokenLogin();
		resumed = true;
		locationFetcher.connect();
	}

	private void setPermissions() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				new Permissive.Request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
						.withRationale(new Rationale() {
							@Override
							public void onShowRationale(Activity activity, String[] allowablePermissions, PermissiveMessenger messenger) {
								SingleButtonAlert.showAlertGps(SplashNewActivity.this, "Please make sure you have granted all permission to acess Contacts, location and send sms etc.", "Ok", new SingleButtonAlert.OnAlertOkClickListener() {
									@Override
									public void onOkButtonClicked() {
										finish();
//                                        messenger.cancelPermissionsRequest();
									}
								});
							}
						})
						.whenPermissionsGranted(new PermissionsGrantedListener() {
							@Override
							public void onPermissionsGranted(String[] permissions) throws SecurityException {

								retryAccessTokenLogin();
								/*if (accessToken.equalsIgnoreCase("") && (Prefs.with(SplashNewActivity.this).getInt("activity_open", 0) != 1)) {
									Prefs.with(SplashNewActivity.this).save("activity_open", 1);
									startActivity(new Intent(SplashNewActivity.this, SignInActivity.class));
									overridePendingTransition(0, 0);
									finish();
								} else{
									if(!Prefs.with(SplashNewActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "").equalsIgnoreCase("")){
										retryAccessTokenLogin();
									}
								}*/
							}
						})
						.whenPermissionsRefused(new PermissionsRefusedListener() {
							@Override
							public void onPermissionsRefused(String[] permissions) {
								finish();
							}
						})
						.execute(SplashNewActivity.this);
			}
		}, 3000);
	}


	public void retryAccessTokenLogin() {
		if (State.LOGIN != state && State.SIGNUP != state && resumed) {
			buttonRefresh.performClick();
		}
	}


	@Override
	protected void onPause() {

		super.onPause();
		locationFetcher.destroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == GOOGLE_SIGNIN_REQ_CODE_LOGIN) {
				if (RESULT_OK == resultCode) {
					Data.googleSignInAccount = data.getParcelableExtra(KEY_GOOGLE_PARCEL);
					sendGoogleLoginValues(this);
				}
			}else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
				Bundle bundle = data.getExtras();
				String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
				String yblRefId = bundle.getString("yblRefId");
				String virtualAddress = bundle.getString("virtualAddress");
				String status = bundle.getString("status");
				String statusdesc = bundle.getString("statusdesc");
				String registrationDate = bundle.getString("registrationDate");

				String AccountNo = bundle.getString("accountNo");
				String ifsc = bundle.getString("ifsc");
				String accName = bundle.getString("accName");

//            System.out.println("data=== "+bundle.getString("add1"));
				System.out.println("virtual address== " + virtualAddress + " date=== " + registrationDate + "  ybl== " + yblRefId + "  pgM== " + pgMeTrnRefNo + " status== " + status + "  dsc ==" + statusdesc + " AccountNo == " + AccountNo + " ifsc == " + ifsc + " accName == " + accName);

				VerifyRegisterResponse verifyRegisterResponse = new VerifyRegisterResponse();
				verifyRegisterResponse.setPgMeTrnRefNo(pgMeTrnRefNo);
				verifyRegisterResponse.setYblRefId(yblRefId);
				verifyRegisterResponse.setVirtualAddress(virtualAddress);
				verifyRegisterResponse.setStatus(status);
				verifyRegisterResponse.setStatusdesc(statusdesc);
				verifyRegisterResponse.setRegistrationDate(registrationDate);

				verifyRegisterResponse.setAccountNo(AccountNo);
				verifyRegisterResponse.setIfsc(ifsc);
				verifyRegisterResponse.setAccName(accName);

				if (virtualAddress.length() > 0) {
					callVerifyUserApi(verifyRegisterResponse);
				}
			} else{
				callbackManager.onActivityResult(requestCode, resultCode, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callVerifyUserApi(VerifyRegisterResponse verifyRegisterResponse) {
		CallProgressWheel.showLoadingDialog(SplashNewActivity.this, AppConstants.PLEASE);
		String deviceToken = Prefs.with(SplashNewActivity.this).getString(SharedPreferencesName.DEVICE_TOKEN, "");
//        String accessToken =   Prefs.with(SignUpActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN,"");
		VerifyUserRequest request = new VerifyUserRequest();
		request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
		request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(SplashNewActivity.this));
		request.setToken(accessToken);
		request.setVpa(verifyRegisterResponse.getVirtualAddress());
		request.setDeviceType("0");
		request.setPhone_no(phoneNumber);
		request.setMessage(verifyRegisterResponse.toString());

		WebApi mWebApi = RetrofitClient.createService(WebApi.class);

		mWebApi.verifyUser(request, new Callback<CommonResponse>() {
			@Override
			public void success(CommonResponse tokenGeneratedResponse, Response response) {
				CallProgressWheel.dismissLoadingDialog();
				if (tokenGeneratedResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
					int flag = tokenGeneratedResponse.getFlag();
					if (flag == 401) {
//						String authSecret = tokenGeneratedResponse.getUserData().getAuthKey() + Config.getClientSharedSecret();
//						accessToken = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this).first;
						accessTokenLogin(SplashNewActivity.this);
					}
					else if(flag == 403)
					{
						logoutFunc(SplashNewActivity.this, tokenGeneratedResponse.getMessage());
					}
					else
						CommonMethods.callingBadToken(SplashNewActivity.this, flag, tokenGeneratedResponse.getMessage());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				try {
					System.out.println("SelectServiceActivity.failure2222222");

					CallProgressWheel.dismissLoadingDialog();

					if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(SelectServiceActivity.this,"No Internet Connection", "Ok");
						showAlertNoInternet(SplashNewActivity.this);
					} else {
						String json = new String(((TypedByteArray) error.getResponse()
								.getBody()).getBytes());
						JSONObject jsonObject = new JSONObject(json);
						SingleButtonAlert.showAlert(SplashNewActivity.this, jsonObject.getString("message"), AppConstants.OK);

					}

				} catch (Exception e) {
					e.printStackTrace();
					CallProgressWheel.dismissLoadingDialog();
				}

			}
		});


	}

	private void logoutFunc(Activity activity, String message){

		FacebookLoginHelper.logoutFacebook();

		GCMIntentService.clearNotifications(activity);

		Data.clearDataOnLogout(activity);

		ActivityCompat.finishAffinity(activity);
		Intent intent = new Intent(activity, SplashNewActivity.class);
		if(message != null){
			intent.putExtra(Constants.KEY_LOGGED_OUT, 1);
			intent.putExtra(Constants.KEY_MESSAGE, message);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}


	private ApiLoginUsingAccessToken apiLoginUsingAccessToken;
	private ApiLoginUsingAccessToken getApiLoginUsingAccessToken(){
		if(apiLoginUsingAccessToken == null){
			apiLoginUsingAccessToken = new ApiLoginUsingAccessToken(this);
		}
		return apiLoginUsingAccessToken;
	}

	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
		if (!"".equalsIgnoreCase(pair.first)){
			accessToken = pair.first;
		}

		if (!"".equalsIgnoreCase(accessToken)) {
			//String accessToken = pair.first;

			getApiLoginUsingAccessToken().hit(accessToken, null,
					new ApiLoginUsingAccessToken.Callback() {
				@Override
				public void noNet() {
					changeUIState(State.SPLASH_NO_NET);
				}

				@Override
				public void success(String clientId) {
					loginDataFetched = true;
				}

				@Override
				public void failure() {
					loginDataFetched = false;
				}

						@Override
						public void onRetry(View view) {
							accessTokenLogin(activity);
						}

						@Override
						public void onNoRetry(View view) {
							ActivityCompat.finishAffinity(activity);
						}

						@Override
						public void vpaNotFound(LoginResponse loginResponse) {
							//String authSecret = loginResponse.getUserData().getAuthKey() + Config.getClientSharedSecret();
							//accessToken = SHA256Convertor.getSHA256String(authSecret);
							phoneNumber = loginResponse.getUserData().getPhoneNo();

							Bundle bundle = new Bundle();
							bundle.putString("mid", loginResponse.getMid());
							bundle.putString("merchantKey", loginResponse.getMkey());
							bundle.putString("merchantTxnID", loginResponse.getToken());
							Log.i(TAG, "TOKEN IS : " + loginResponse.getToken());
							bundle.putString("appName", "jugnooApp");
							Intent intent = new Intent(getApplicationContext(), Registration.class);
							intent.putExtras(bundle);
							startActivityForResult(intent, 1);
						}

					});

		} else {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				changeUIState(State.SPLASH_LS);
				//getAllowedAuthChannels(SplashNewActivity.this);
			} else {
				changeUIState(State.SPLASH_NO_NET);
			}
//			sendToRegisterThroughSms(Data.deepLinkReferralCode);
		}
	}



	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception {
//		"popup": {
//	        "title": "Update Version",
//	        "text": "Update app with new version!",
//	        "cur_version": 116,			// could be used for local check
//	        "is_force": 1				// 1 for forced, 0 for not forced
//	}

		try {
			JSONObject jsonObject = jObj;
			if (jObj.getJSONObject(KEY_USER_DATA).has("popup")) {
				jsonObject = jObj.getJSONObject(KEY_USER_DATA);
			}
			try {
				JSONObject jupdatePopupInfo = jsonObject.getJSONObject("popup");
				String title = jupdatePopupInfo.getString("title");
				String text = jupdatePopupInfo.getString("text");
				int currentVersion = jupdatePopupInfo.getInt("cur_version");
				int isForce = jupdatePopupInfo.getInt("is_force");

				if (Data.appVersion >= currentVersion) {
					return false;
				} else {
					SplashNewActivity.appUpdatePopup(title, text, isForce, activity);
					if (isForce == 1) {
						return true;
					} else {
						return false;
					}
				}
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * Displays appUpdatePopup dialog
	 */
	public static void appUpdatePopup(String title, String message, final int isForced, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenLight(activity));
			textHead.setVisibility(View.VISIBLE);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if (isForced == 1) {
						ActivityCompat.finishAffinity(activity);
					}
				}
			});


			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo"));
					activity.startActivity(intent);
					ActivityCompat.finishAffinity(activity);
				}

			});


			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkIfTrivialAPIErrors(Activity activity, JSONObject jObj) {
		try {
			int flag = jObj.getInt("flag");
			if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
				CallProgressWheel.dismissLoadingDialog();
				Utils.logoutUser(activity);
				return true;
			} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
				CallProgressWheel.dismissLoadingDialog();
				String errorMessage = jObj.getString("error");
				DialogPopup.alertPopup(activity, "", errorMessage);
				return true;
			} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
				CallProgressWheel.dismissLoadingDialog();
				String message = jObj.getString("message");
				DialogPopup.alertPopup(activity, "", message);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("e", "=" + e);
		}
		return false;
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (State.SPLASH_LS == state || State.SPLASH_INIT == state || State.SPLASH_NO_NET == state) {
					if (SplashNewActivity.this.hasWindowFocus() && loginDataFetched) {
						loginDataFetched = false;

						Intent intent = new Intent(SplashNewActivity.this, MainActivity.class);
						intent.setData(getIntent().getData());
						startActivity(intent);
						ActivityCompat.finishAffinity(SplashNewActivity.this);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
				}
				else if(State.LOGIN == state || State.SIGNUP == state){
					if(SplashNewActivity.this.hasWindowFocus() && loginDataFetched){
						Map<String, String> articleParams = new HashMap<String, String>();
						articleParams.put("username", Data.userData.userName);
//						FlurryAgent.logEvent("App Login", articleParams);

						loginDataFetched = false;

						Intent intent = new Intent(SplashNewActivity.this, MainActivity.class);

						// changed on 23-11-2016
						// intent.setData(Data.splashIntentUri);
						//intent.setData(Data)

						startActivity(intent);
						ActivityCompat.finishAffinity(SplashNewActivity.this);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
					else if(SplashNewActivity.this.hasWindowFocus() && emailRegister){
						emailRegister = false;
						sendIntentToRegisterScreen(RegisterationType.EMAIL);
					}
					else if(SplashNewActivity.this.hasWindowFocus() && facebookRegister){
						facebookRegister = false;
						sendIntentToRegisterScreen(RegisterationType.FACEBOOK);
					} else if (SplashNewActivity.this.hasWindowFocus() && googleRegister) {
						googleRegister = false;
						sendIntentToRegisterScreen(RegisterationType.GOOGLE);
					} else if (SplashNewActivity.this.hasWindowFocus() && sendToOtpScreen) {
						sendIntentToOtpScreen();
					}
				}
			}
		}, 500);

	}

	@Override
	public void startActivity(Intent intent) {
		newActivityStarted = true;
		super.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverDeviceToken);
		if(!newActivityStarted){
			Data.linkFoundOnce = false;
		}
		super.onDestroy();
		ASSL.closeActivity(root);
		System.gc();
	}

	@Override
	public void onBackPressed() {
		if (State.LOGIN == state) {
			performLoginBackPressed();
		} else if (State.SIGNUP == state) {
			performSignupBackPressed();
		} else {
			super.onBackPressed();
		}
	}




	private void showLocationEnableDialog() {
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (resp != ConnectionResult.SUCCESS) {
			Log.e("Google Play Service Error ", "=" + resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		} else {
			//LocationInit.showLocationAlertDialog(this);
		}
	}

	private void initiateDeviceInfoVariables() {
		try {                                                                                        // to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = pInfo.versionCode;
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
			Log.i("deviceName", Data.deviceName + "..");

				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);

			Log.e("Data.uniqueDeviceId = ", "=" + Data.uniqueDeviceId);

			Utils.generateKeyHash(this);

		} catch (Exception e) {
			Log.e("error in fetching appVersion and gcm key", ".." + e.toString());
		}
	}

	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
	}


	public enum State {
		SPLASH_INIT(0), SPLASH_LS(1), SPLASH_NO_NET(2), LOGIN(3), SIGNUP(4);

		private int ordinal;

		State(int ordinal) {
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}


	public class CustomTextWatcher implements TextWatcher {
		private TextView textViewRequired;

		public CustomTextWatcher(TextView textViewRequired) {
			this.textViewRequired = textViewRequired;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			textViewRequired.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
		}
	}


	private void setLoginScreenValuesOnCreate() {
		// set email screen values according to intent
		editTextEmail.setText("");
		editTextPassword.setText("");
		try {
			if (getIntent().hasExtra(KEY_BACK_FROM_OTP) && RegisterationType.EMAIL == SplashNewActivity.registerationType) {
				if (phoneNoLogin) {
					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
				} else {
					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (getIntent().hasExtra(KEY_PREVIOUS_LOGIN_EMAIL)) {
				String previousLoginEmail = getIntent().getStringExtra(KEY_PREVIOUS_LOGIN_EMAIL);
				editTextEmail.setText(previousLoginEmail);
				fromPreviousAccounts = true;
			} else {
				fromPreviousAccounts = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fromPreviousAccounts = false;
		}

		try {
			if (getIntent().hasExtra(KEY_FORGOT_LOGIN_EMAIL)) {
				String forgotLoginEmail = getIntent().getStringExtra(KEY_FORGOT_LOGIN_EMAIL);
				editTextEmail.setText(forgotLoginEmail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (getIntent().hasExtra(KEY_ALREADY_VERIFIED_EMAIL)) {
				String alreadyVerifiedEmail = getIntent().getStringExtra(KEY_ALREADY_VERIFIED_EMAIL);
				editTextEmail.setText(alreadyVerifiedEmail);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		try {
			if (getIntent().hasExtra(KEY_ALREADY_REGISTERED_EMAIL)) {
				String alreadyRegisterEmail = getIntent().getStringExtra(KEY_ALREADY_REGISTERED_EMAIL);
				editTextEmail.setText(alreadyRegisterEmail);
			}
		} catch(Exception e){
			e.printStackTrace();
		}


		editTextEmail.setSelection(editTextEmail.getText().length());
	}


	public void performLoginBackPressed() {
		if (fromPreviousAccounts) {
			Intent intent = new Intent(SplashNewActivity.this, MultipleAccountsActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		} else {
			FacebookLoginHelper.logoutFacebook();
			changeUIState(State.SPLASH_LS);
		}
	}


	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			CallProgressWheel.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			if (isPhoneNumber) {
				params.put("phone_no", emailId);
			} else {
				params.put("email", emailId);
			}
			params.put("password", password);
			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}


			Log.i("params", "=" + params);

			RestClient.getApiServices().loginUsingEmailOrPhoneNo(params, new Callback<LoginResponse>() {
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
								emailNeedRegister = emailId;
								emailRegister = true;
								notRegisteredMsg = error;
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", error);
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								if (isPhoneNumber) {
									enteredEmail = jObj.getString("user_email");
								} else {
									enteredEmail = emailId;
								}
								linkedWallet = jObj.optInt("reg_wallet_type");
								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
								accessToken = jObj.getString("access_token");
								Prefs.with(activity).save(SP_KNOWLARITY_MISSED_CALL_NUMBER,
										jObj.optString(KEY_KNOWLARITY_MISSED_CALL_NUMBER, ""));
								Prefs.with(activity).save(SP_OTP_VIA_CALL_ENABLED,
										jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1));
								otpErrorMsg = jObj.getString("error");
								SplashNewActivity.registerationType = RegisterationType.EMAIL;
								sendToOtpScreen = true;
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.EMAIL);
									Database.getInstance(SplashNewActivity.this).insertEmail(emailId);
									loginDataFetched = true;
								}
							}else if(ApiResponseFlags.VPA_NOT_FOUND.getOrdinal() == flag){
								// make vpa api call to yes bank server
								//String accessToken = loginResponse.getUserData().getToken();
								String authSecret = loginResponse.getUserData().getAuthKey() + Config.getClientSharedSecret();
								accessToken = SHA256Convertor.getSHA256String(authSecret);
								phoneNumber = loginResponse.getUserData().getPhoneNo();

								Bundle bundle = new Bundle();
								bundle.putString("mid", loginResponse.getMid());
								bundle.putString("merchantKey", loginResponse.getMkey());
								bundle.putString("merchantTxnID", loginResponse.getToken());
								bundle.putString("appName", "jugnooApp");
								Intent intent = new Intent(getApplicationContext(), Registration.class);
								intent.putExtras(bundle);
								startActivityForResult(intent, 1);
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							CallProgressWheel.dismissLoadingDialog();
						} else {
							CallProgressWheel.dismissLoadingDialog();
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						CallProgressWheel.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
					CallProgressWheel.dismissLoadingDialog();
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
			resetFlags();
			CallProgressWheel.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}

			Log.i("params", "" + params);

			RestClient.getApiServices().loginUsingFacebook(params, new Callback<LoginResponse>() {
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
								facebookRegister = true;
								notRegisteredMsg = error;
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", error);
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								linkedWallet = jObj.optInt("reg_wallet_type");
								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
								accessToken = jObj.getString("access_token");
								SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
								SplashNewActivity.this.accessToken = jObj.getString("access_token");
								Prefs.with(activity).save(SP_KNOWLARITY_MISSED_CALL_NUMBER,
										jObj.optString(KEY_KNOWLARITY_MISSED_CALL_NUMBER, ""));
								Prefs.with(activity).save(SP_OTP_VIA_CALL_ENABLED,
										jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1));
								otpErrorMsg = jObj.getString("error");
								SplashNewActivity.registerationType = RegisterationType.FACEBOOK;
								sendToOtpScreen = true;
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.FACEBOOK);
									loginDataFetched = true;

									Database.getInstance(SplashNewActivity.this).insertEmail(Data.facebookUserData.userEmail);
								}
							} else if(ApiResponseFlags.VPA_NOT_FOUND.getOrdinal() == flag){
								// make vpa api call to yes bank server
								//String accessToken = loginResponse.getUserData().getToken();
								String authSecret = loginResponse.getUserData().getAuthKey() + Config.getClientSharedSecret();
								accessToken = SHA256Convertor.getSHA256String(authSecret);
								phoneNumber = loginResponse.getUserData().getPhoneNo();

								Bundle bundle = new Bundle();
								bundle.putString("mid", loginResponse.getMid());
								bundle.putString("merchantKey", loginResponse.getMkey());
								bundle.putString("merchantTxnID", loginResponse.getToken());
								Log.i(TAG, "TOKEN IS : " + loginResponse.getToken());
								bundle.putString("appName", "jugnooApp");
								Intent intent = new Intent(getApplicationContext(), Registration.class);
								intent.putExtras(bundle);
								startActivityForResult(intent, 1);
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							CallProgressWheel.dismissLoadingDialog();
						} else {
							CallProgressWheel.dismissLoadingDialog();
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						CallProgressWheel.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingFacebook error=" + error.toString());
					CallProgressWheel.dismissLoadingDialog();
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
			resetFlags();
			CallProgressWheel.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put("google_access_token", Data.googleSignInAccount.getIdToken());

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}

			Log.i("params", "" + params);


			RestClient.getApiServices().loginUsingGoogle(params, new Callback<LoginResponse>() {
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
								googleRegister = true;
								notRegisteredMsg = error;
								//DialogPopup.alertPopup(activity, "", error);
							}
							else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
								String error = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", error);
							}
							else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
								linkedWallet = jObj.optInt("reg_wallet_type");
								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
								accessToken = jObj.getString("access_token");
								SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
								SplashNewActivity.this.accessToken = jObj.getString("access_token");
								Prefs.with(activity).save(SP_KNOWLARITY_MISSED_CALL_NUMBER,
										jObj.optString(KEY_KNOWLARITY_MISSED_CALL_NUMBER, ""));
								Prefs.with(activity).save(SP_OTP_VIA_CALL_ENABLED,
										jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1));
								otpErrorMsg = jObj.getString("error");
								SplashNewActivity.registerationType = RegisterationType.GOOGLE;
								sendToOtpScreen = true;
							}
							else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
								if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.GOOGLE);
									loginDataFetched = true;

									Database.getInstance(SplashNewActivity.this).insertEmail(Data.googleSignInAccount.getEmail());
								}
							} else if(ApiResponseFlags.VPA_NOT_FOUND.getOrdinal() == flag){
								// make vpa api call to yes bank server
								//String accessToken = loginResponse.getUserData().getToken();
								String authSecret = loginResponse.getUserData().getAuthKey() + Config.getClientSharedSecret();
								accessToken = SHA256Convertor.getSHA256String(authSecret);
								phoneNumber = loginResponse.getUserData().getPhoneNo();

								Bundle bundle = new Bundle();
								bundle.putString("mid", loginResponse.getMid());
								bundle.putString("merchantKey", loginResponse.getMkey());
								bundle.putString("merchantTxnID", loginResponse.getToken());
								Log.i(TAG, "TOKEN IS : " + loginResponse.getToken());
								bundle.putString("appName", "jugnooApp");
								Intent intent = new Intent(getApplicationContext(), Registration.class);
								intent.putExtras(bundle);
								startActivityForResult(intent, 1);
							}
							else{
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							CallProgressWheel.dismissLoadingDialog();
						}
						else{
							CallProgressWheel.dismissLoadingDialog();
						}

					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						CallProgressWheel.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingGoogle error="+error.toString());
					CallProgressWheel.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	/**
	 * Send intent to otp screen by making required data objects
	 * flag 0 for email, 1 for Facebook
	 */
	public void sendIntentToOtpScreen() {
		if (State.LOGIN == state) {
			DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", otpErrorMsg, new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogPopup.dismissAlertPopup();
					OTPConfirmScreen.intentFromRegister = false;
					if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
						OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
								Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
								Data.facebookUserData.accessToken,
								Data.facebookUserData.userEmail,
								Data.facebookUserData.userName,
								phoneNoOfUnverifiedAccount, "", "", accessToken);
					} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
						OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
								Data.googleSignInAccount.getDisplayName(),
								Data.googleSignInAccount.getEmail(),
								"",
								phoneNoOfUnverifiedAccount, "", "", accessToken);
					} else {
						OTPConfirmScreen.intentFromRegister = false;
						OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "", accessToken);
					}

					Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
					intent.putExtra("show_timer", 0);
					intent.putExtra(LINKED_WALLET, linkedWallet);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});
		} else if (State.SIGNUP == state) {
			OTPConfirmScreen.intentFromRegister = true;
			generateOTPRegisterData();
			Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
			intent.putExtra("show_timer", 1);
			intent.putExtra(LINKED_WALLET_MESSAGE, linkedWalletErrorMsg);
			intent.putExtra(LINKED_WALLET, linkedWallet);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}


	public void sendIntentToRegisterScreen(final RegisterationType registerationType){
		if(State.LOGIN == state){
			DialogPopup.alertPopupWithListener(this, "", notRegisteredMsg, new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SplashNewActivity.registerationType = registerationType;
					changeUIState(State.SIGNUP);
				}
			});
		} else{
			SplashNewActivity.registerationType = registerationType;
			changeUIState(State.SIGNUP);
		}
	}


	public void performSignupBackPressed() {
		FacebookLoginHelper.logoutFacebook();
		changeUIState(State.SPLASH_LS);
	}

	public enum RegisterationType {
		EMAIL(0), FACEBOOK(1), GOOGLE(2);

		private int ordinal;

		RegisterationType(int ordinal) {
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}


	private void setSignupScreenValuesOnCreate() {

		editTextSName.setText(""); editTextSName.setEnabled(true);
		editTextSEmail.setText(""); editTextSEmail.setEnabled(true);
		editTextSPromo.setText("");
		editTextSPhone.setText("");
		editTextSPassword.setText("");

		fillSocialAccountInfo(SplashNewActivity.registerationType);


		try {
			if (getIntent().hasExtra(KEY_BACK_FROM_OTP)) {
				if (RegisterationType.FACEBOOK == registerationType) {
					editTextSPromo.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.facebookRegisterData.phoneNo));
					editTextSPassword.setText(OTPConfirmScreen.facebookRegisterData.password);
				} else if (RegisterationType.GOOGLE == registerationType) {
					editTextSPromo.setText(OTPConfirmScreen.googleRegisterData.referralCode);
					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.googleRegisterData.phoneNo));
					editTextSPassword.setText(OTPConfirmScreen.googleRegisterData.password);
				} else {
					editTextSName.setText(OTPConfirmScreen.emailRegisterData.name);
					editTextSEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
					editTextSPromo.setText(OTPConfirmScreen.emailRegisterData.referralCode);
					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.emailRegisterData.phoneNo));
					editTextSPassword.setText(OTPConfirmScreen.emailRegisterData.password);
				}

				editTextSName.setSelection(editTextSName.getText().length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			if(getIntent().hasExtra(KEY_REFERRAL_CODE)){
				String referralCode = getIntent().getStringExtra(KEY_REFERRAL_CODE);
				editTextSPromo.setText(referralCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Data.previousAccountInfoList == null) {
			Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
		}

		setLinkedWalletTick();


		new ReadSMSAsync().execute();

	}


	private void fillSocialAccountInfo(RegisterationType registerationType) {
		try {
			SplashNewActivity.registerationType = registerationType;
			if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
				editTextSName.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
				editTextSEmail.setText(Data.facebookUserData.userEmail);

				if (Data.facebookUserData.firstName != null && !Data.facebookUserData.firstName.equalsIgnoreCase("")) {
					editTextSName.setEnabled(false);
				} else {
					editTextSName.setEnabled(true);
				}
				if (Data.facebookUserData.userEmail != null && !Data.facebookUserData.userEmail.equalsIgnoreCase("")) {
					editTextSEmail.setEnabled(false);
				} else {

					editTextSEmail.setEnabled(true);
				}
			} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
				editTextSName.setText(Data.googleSignInAccount.getDisplayName());
				editTextSEmail.setText(Data.googleSignInAccount.getEmail());

				if (Data.googleSignInAccount.getDisplayName() != null && !Data.googleSignInAccount.getDisplayName().equalsIgnoreCase("")) {
					editTextSName.setEnabled(false);
				} else {
					editTextSName.setEnabled(true);
				}
				if (Data.googleSignInAccount.getEmail() != null && !Data.googleSignInAccount.getEmail().equalsIgnoreCase("")) {
					editTextSEmail.setEnabled(false);
				} else {
					editTextSEmail.setEnabled(true);
				}
			}
			else if(RegisterationType.EMAIL == SplashNewActivity.registerationType){
				if(Utils.checkIfOnlyDigits(emailNeedRegister)){
					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(emailNeedRegister));
				} else{
					editTextSEmail.setText(emailNeedRegister);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private class ReadSMSAsync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String referralCode = getSmsFindReferralCode(1 * 60 * 60 * 1000);
			return referralCode;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (editTextSPromo.getText().toString().equalsIgnoreCase("")) {
				editTextSPromo.setText(s);
			}
		}
	}

	private String getSmsFindReferralCode(long diff) {
		String referralCode = "";
		try {
			Uri uri = Uri.parse("content://sms/inbox");
			long now = System.currentTimeMillis();
			long last1 = now - diff;    //in millis
			String[] selectionArgs = new String[]{Long.toString(last1)};
			String selection = "date" + ">?";
			Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);

			if (cursor != null && cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
//                    String number = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//                    Date smsDayTime = new Date(Long.valueOf(date));
//                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
//                    String typeOfSMS = null;
//                    switch (Integer.parseInt(type)) {
//                        case 1:
//                            typeOfSMS = "INBOX";
//                            break;
//
//                        case 2:
//                            typeOfSMS = "SENT";
//                            break;
//
//                        case 3:
//                            typeOfSMS = "DRAFT";
//                            break;
//                    }
                    Log.e("body", "=" + body);
                    try {
                        if (body.contains("Jugnoo")) {
                            String[] codeArr = body.split("code ");
                            String[] spaceArr = codeArr[1].split(" ");
                            String rCode = spaceArr[0];
                            if (!"".equalsIgnoreCase(rCode)) {
                                referralCode = rCode;
                                break;
                            }
                        }
                    } catch (Exception e) {
                    }
//                    stringBuffer.append("\nPhone Number:--- " + number + " \nMessage Type:--- "
//                            + typeOfSMS + " \nMessage Date:--- " + smsDayTime
//                            + " \nMessage Body:--- " + body);
//                    stringBuffer.append("\n----------------------------------");
                    cursor.moveToNext();
                }
//                DialogPopup.alertPopup(this, "", stringBuffer.toString());
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referralCode;
    }


    /**
     * ASync for register from server
     */
    public void sendSignupValues(final Activity activity, final String name, final String referralCode, final String emailId, final String phoneNo,
                                 final String password, final int linkedWallet) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            CallProgressWheel.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();


            params.put("user_name", name);
            params.put("phone_no", phoneNo);
            params.put("email", emailId);
            params.put("password", password);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);

            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);

            params.put("client_id", Config.getAutosClientId());
            params.put(KEY_REFERRAL_CODE, referralCode);

            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("reg_wallet_type", String.valueOf(linkedWallet));

            if (linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()) {
            } else {
            }

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");
            }

            Log.i("register_usi", params.toString());


            RestClient.getApiServices().registerUsingEmail(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "registerUsingEmail response = " + responseStr);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        SplashNewActivity.registerationType = RegisterationType.EMAIL;
                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", error);
                                } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    setIntent(new Intent().putExtra(KEY_ALREADY_REGISTERED_EMAIL, emailId));
                                    DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
                                } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                    SplashNewActivity.this.name = name;
                                    SplashNewActivity.this.emailId = emailId;
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);

                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATIONS.getOrdinal() == flag) {
                                    SplashNewActivity.this.name = name;
                                    SplashNewActivity.this.emailId = emailId;
                                    SplashNewActivity.this.phoneNo = phoneNo;
                                    SplashNewActivity.this.password = password;
                                    SplashNewActivity.this.referralCode = referralCode;
                                    SplashNewActivity.this.accessToken = "";
                                    parseDataSendToMultipleAccountsScreen(activity, jObj);
                                } else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
                                    SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
                                    SplashNewActivity.this.name = name;
                                    SplashNewActivity.this.emailId = emailId;
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
								CallProgressWheel.dismissLoadingDialog();
                            }
                        } else {
							CallProgressWheel.dismissLoadingDialog();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						CallProgressWheel.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "registerUsingEmail error=" + error.toString());
					CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }


    /**
     * ASync for login from server
     */
    public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password
            , final int linkedWallet) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            CallProgressWheel.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();
            params.put("user_fb_id", Data.facebookUserData.fbId);
            params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
            params.put("fb_access_token", Data.facebookUserData.accessToken);
            params.put("fb_mail", Data.facebookUserData.userEmail);
            params.put("username", Data.facebookUserData.userName);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put(KEY_REFERRAL_CODE, referralCode);

            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("client_id", Config.getAutosClientId());
            params.put("reg_wallet_type", String.valueOf(linkedWallet));
            if (linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()) {
            } else {
            }

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");
            }

            Log.e("register_using_facebook params", params.toString());


            RestClient.getApiServices().registerUsingFacebook(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "registerUsingFacebook response = " + response);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        SplashNewActivity.registerationType = RegisterationType.FACEBOOK;
                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", error);
                                } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
                                } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);

                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATIONS.getOrdinal() == flag) {
                                    SplashNewActivity.this.phoneNo = phoneNo;
                                    SplashNewActivity.this.password = password;
                                    SplashNewActivity.this.referralCode = referralCode;
                                    SplashNewActivity.this.accessToken = "";
                                    parseDataSendToMultipleAccountsScreen(activity, jObj);
                                } else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
                                    SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
								CallProgressWheel.dismissLoadingDialog();
                            }
                        } else {
							CallProgressWheel.dismissLoadingDialog();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
						CallProgressWheel.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "registerUsingFacebook error=" + error.toString());
					CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    /**
     * ASync for login from server
     */
    public void sendGoogleSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password, final int linkedWallet) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            CallProgressWheel.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            params.put("google_access_token", Data.googleSignInAccount.getIdToken());

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put(KEY_REFERRAL_CODE, referralCode);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);

            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("client_id", Config.getAutosClientId());
            params.put("reg_wallet_type", String.valueOf(linkedWallet));
            if (linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()) {
            } else {
            }

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");
            }

            Log.e("register_using_facebook params", params.toString());

            RestClient.getApiServices().registerUsingGoogle(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "registerUsingGoogle response = " + responseStr);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        SplashNewActivity.registerationType = RegisterationType.GOOGLE;
                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", error);
                                } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
                                } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);

                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATIONS.getOrdinal() == flag) {
                                    SplashNewActivity.this.phoneNo = phoneNo;
                                    SplashNewActivity.this.password = password;
                                    SplashNewActivity.this.referralCode = referralCode;
                                    SplashNewActivity.this.accessToken = "";
                                    parseDataSendToMultipleAccountsScreen(activity, jObj);
                                } else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
                                    SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
								CallProgressWheel.dismissLoadingDialog();
                            }
                        } else {
							CallProgressWheel.dismissLoadingDialog();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
						CallProgressWheel.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "registerUsingGoogle error=" + error.toString());
					CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }



    private void parseOTPSignUpData(JSONObject jObj, String password, String referralCode, int linkedWallet) throws Exception {
        SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
        SplashNewActivity.this.password = password;
        SplashNewActivity.this.referralCode = referralCode;
        SplashNewActivity.this.accessToken = jObj.getString("access_token");
        Prefs.with(this).save(SP_KNOWLARITY_MISSED_CALL_NUMBER,
                jObj.optString(KEY_KNOWLARITY_MISSED_CALL_NUMBER, ""));
        Prefs.with(this).save(SP_OTP_VIA_CALL_ENABLED,
                jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1));
        sendToOtpScreen = true;
        linkedWalletErrorMsg = jObj.optString(KEY_MESSAGE, "");
		Prefs.with(this).save(SP_WALLET_AT_SIGNUP, String.valueOf(linkedWallet));
    }

    private void generateOTPRegisterData() {
        if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
            OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
                    Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
                    Data.facebookUserData.accessToken,
                    Data.facebookUserData.userEmail,
                    Data.facebookUserData.userName,
                    phoneNo, password, referralCode, accessToken);
        } else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
            OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
                    Data.googleSignInAccount.getDisplayName(),
                    Data.googleSignInAccount.getEmail(),
                    "",
                    phoneNo, password, referralCode, accessToken);
        } else {
            OTPConfirmScreen.intentFromRegister = true;
            OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode, accessToken);
        }
    }


    public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
        generateOTPRegisterData();
        SplashNewActivity.multipleCaseJSON = jObj;
        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();
        Data.previousAccountInfoList.addAll(JSONParser.parsePreviousAccounts(jObj));
        startActivity(new Intent(activity, MultipleAccountsActivity.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private View.OnClickListener onClickListenerAlreadyRegistered = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeUIState(State.LOGIN);
        }
    };


    public void verifyOtpViaEmail(final Activity activity, final String email, final String phoneNo, String otp) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            //final ProgressDialog progressDialog = CallProgressWheel.showLoadingDialogNewInstance(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();


            params.put("email", email);
            params.put("password", "");
            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("client_id", Config.getAutosClientId());
            params.put("otp", otp);

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");
            }

            Log.i("params", "" + params.toString());

            RestClient.getApiServices().verifyOtp(params, new Callback<LoginResponse>() {
                @Override
                public void success(LoginResponse loginResponse, Response response) {

                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "verifyOtp jsonString = " + jsonString);
                        JSONObject jObj = new JSONObject(jsonString);

                        int flag = jObj.getInt("flag");
                        String message = JSONParser.getServerMessage(jObj);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                    new JSONParser().parseAccessTokenLoginData(activity, jsonString,
                                            loginResponse, LoginVia.EMAIL_OTP);
                                    Database.getInstance(activity).insertEmail(email);
                                    Database.getInstance(activity).close();
                                    loginDataFetched = true;
                                }
                            } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.AUTH_ALREADY_VERIFIED.getOrdinal() == flag) {
                                DialogPopup.alertPopupWithListener(activity, "", message,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                setIntent(new Intent().putExtra(KEY_ALREADY_VERIFIED_EMAIL, email));
                                                changeUIState(State.LOGIN);
                                            }
                                        });
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "verifyOtp error=" + error);
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    private class ReadSMSClickLinkAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String link = getSmsFindVerificationLink(4 * 60 * 60 * 1000);
            return link;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equalsIgnoreCase("")) {
                if (!SplashNewActivity.this.isFinishing()) {
                    Utils.openUrl(SplashNewActivity.this, s);
                    Data.linkFoundOnce = true;
                }
            }
        }

        private String getSmsFindVerificationLink(long diff) {
            String link = "";
            try {
                Uri uri = Uri.parse("content://sms/inbox");
                long now = System.currentTimeMillis();
                long last1 = now - diff;    //in millis
                String[] selectionArgs = new String[]{Long.toString(last1)};
                String selection = "date" + ">?";
                Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);

                if (cursor != null) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                        Log.i(TAG, "sms body=>" + body);
                        try {
                            if (body.contains(DOMAIN_SHARE_JUGNOO_IN)) {
                                String[] arr = body.split(" ");
                                for (String str : arr) {
                                    if (str.contains(DOMAIN_SHARE_JUGNOO_IN)) {
                                        if (str.charAt(str.length() - 1) == '.') {
                                            str = str.substring(0, str.length() - 1);
                                        }
                                        link = str;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        if (!link.equalsIgnoreCase("")) {
                            break;
                        }
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return link;
        }
    }


    private void firstTimeEvents() {
        if (!Prefs.with(this).contains(SP_FIRST_OPEN_TIME)) {
            Prefs.with(this).save(SP_FIRST_OPEN_TIME, System.currentTimeMillis());
        }

        if (!Prefs.with(this).contains(SP_APP_DOWNLOAD_SOURCE_SENT)) {
            HashMap<String, String> map = new HashMap<>();
            Prefs.with(this).save(SP_APP_DOWNLOAD_SOURCE_SENT, 1);
        }
    }


	private void setLinkedWalletTick(){
		if (linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()) {
			setWalletRadio(imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, imageViewRadioNone);

		}
		else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
			setWalletRadio(imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioFreeCharge, imageViewRadioNone);
		}
		else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
			setWalletRadio(imageViewRadioFreeCharge, imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioNone);
		}
		else {
			setWalletRadio(imageViewRadioNone, imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge);
		}
	}

	private void setWalletRadio(ImageView selected, ImageView unSelected1, ImageView unSelected2, ImageView unSelected3){
		selected.setImageResource(R.drawable.ic_radio_button_selected);
		unSelected1.setImageResource(R.drawable.ic_radio_button_normal);
		unSelected2.setImageResource(R.drawable.ic_radio_button_normal);
		unSelected3.setImageResource(R.drawable.ic_radio_button_normal);
	}

}
