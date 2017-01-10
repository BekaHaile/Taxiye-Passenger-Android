package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;
import product.clicklabs.jugnoo.apis.ApiLoginUsingAccessToken;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.LinkedWalletStatus;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.GoogleSigninActivity;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.LocationInit;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.OwnerInfo;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.UniqueIMEIID;
import product.clicklabs.jugnoo.utils.UserEmailFetcher;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SplashNewActivity extends BaseActivity implements LocationUpdate, FlurryEventNames, Constants, FirebaseEvents {

	//adding drop location

	RelativeLayout root, rlSplashLogo;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	private final String TAG = "Splash Screen";

	RelativeLayout relativeLayoutJugnooLogo;
	ImageView imageViewBack, imageViewJugnooLogo;
	ImageView imageViewDebug1, imageViewDebug2, imageViewDebug3, ivBottom, ivBottom1;

	RelativeLayout relativeLayoutLS;
	LinearLayout linearLayoutLoginSignupButtons;
	Button buttonLogin, buttonRegister;
	LinearLayout linearLayoutNoNet;
	TextView textViewNoNet;
	Button buttonNoNetCall, buttonRefresh;

	RelativeLayout linearLayoutLogin;
	AutoCompleteTextView editTextEmail;
	EditText editTextPassword;
	TextView textViewEmailRequired, textViewPasswordRequired, textViewForgotPassword;
	Button buttonEmailLogin;

	RelativeLayout relativeLayoutSignup, buttonFacebookLogin, buttonGoogleLogin;
	EditText editTextSName, editTextSEmail, editTextSPhone, editTextSPassword, editTextSPromo;
	TextView textViewSNameRequired, textViewSEmailRequired, textViewSPhoneRequired, textViewSPasswordRequired;
	Button buttonEmailSignup;
	TextView textViewSTerms;
	RelativeLayout buttonFacebookSignup, buttonGoogleSignup;

	LinearLayout linearLayoutWalletContainer, linearLayoutWalletContainerInner,
			linearLayoutPaytm, linearLayoutMobikwik, linearLayoutFreeCharge, linearLayoutNone, llContainer;
	ImageView imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, imageViewRadioNone;
	LinearLayout linearLayoutWalletOption;
	ImageView imageViewWalletOptionCheck;
	TextView textViewWalletOptionMessage, tvScroll;
	View extra;


	boolean loginDataFetched = false, resumed = false, newActivityStarted = false;

	int debugState = 0, userVerfied = 0;
	boolean hold1 = false, hold2 = false;
	boolean holdForBranch = false;
	int clickCount = 0, linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();

	private State state = State.SPLASH_LS;


	CallbackManager callbackManager;
	FacebookLoginHelper facebookLoginHelper;

	boolean emailRegister = false, facebookRegister = false, googleRegister = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "", accessToken = "",
			emailNeedRegister = "", linkedWalletErrorMsg = "";
	private String enteredEmail = "", fbVerifiedNumber = "";
	public static boolean phoneNoLogin = false;
	private static final int GOOGLE_SIGNIN_REQ_CODE_LOGIN = 1124;
	private LinearLayout llSignupMain;

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


	String name = "", referralCode = "", emailId = "", phoneNo = "", password = "", signUpBy = "";
	public static RegisterationType registerationType = RegisterationType.EMAIL;
	public static JSONObject multipleCaseJSON;
	private boolean openHomeSwitcher = true;

	private String phoneFetchedName = "", phoneFetchedEmail = "";

	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
	}


	@Override
	public void onStart() {
		super.onStart();

		try {
			Branch branch = Branch.getInstance(this);
			holdForBranch = true;
			branch.initSession(new Branch.BranchReferralInitListener() {

				@Override
				public void onInitFinished(JSONObject referringParams, BranchError error) {
					holdForBranch = false;
					if (error == null) {
						// params are the deep linked params associated with the link that the user clicked before showing up

						Log.e("BranchConfigTest", "deep link data: " + referringParams.toString());
						try {
							if (referringParams.has("pickup_lat") && referringParams.has("pickup_lng")) {
								Data.deepLinkPickup = 1;
								Data.deepLinkPickupLatitude = Double.parseDouble(referringParams.optString("pickup_lat"));
								Data.deepLinkPickupLongitude = Double.parseDouble(referringParams.optString("pickup_lng"));
							} else {
								if (Data.deepLinkIndex == -1) {
									Data.deepLinkIndex = referringParams.optInt(KEY_DEEPINDEX, -1);
									Data.deepLinkReferralCode = referringParams.optString(KEY_REFERRAL_CODE, "");
									Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
									if ("".equalsIgnoreCase(pair.first)
											&& !"".equalsIgnoreCase(MyApplication.getInstance().getDeviceToken())) {
										sendToRegisterThroughSms(Data.deepLinkReferralCode);
									}
								}
							}
							Log.e("Deeplink =", "=" + Data.deepLinkIndex);
						} catch (Exception e) {
							e.printStackTrace();
						}
						String link = referringParams.optString("link", "");
						if (!"".equalsIgnoreCase(link)) {
							Database2.getInstance(SplashNewActivity.this).insertLink(link);
						}

						// deep link data: {"deepindex":"0","$identity_id":"176950378011563091","$one_time_use":false,"referring_user_identifier":"f2","source":"android",
						// "~channel":"Facebook","~creation_source":"SDK","~feature":"share","~id":"178470536899245547","+match_guaranteed":true,"+click_timestamp":1443850505,
						// "+is_first_session":false,"+clicked_branch_link":true}
					}
					fetchPhoneNoOtpFromBranchParams(referringParams);
				}
			}, this.getIntent().getData(), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

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

			Fabric.with(this, new Crashlytics());

			try {
				if (getIntent().hasExtra("deep_link_class")) {
					Data.deepLinkClassName = getIntent().getStringExtra("deep_link_class");
				} else {
					Data.deepLinkClassName = "";
				}
			} catch (Exception e) {
				e.printStackTrace();
				Data.deepLinkClassName = "";
			}

			Data.splashIntentUri = getIntent().getData();


			Data.getDeepLinkIndexFromIntent(this, getIntent());

			MyApplication.getInstance().initializeServerURL(this);


			try {
				Data.TRANSFER_FROM_JEANIE = 0;
				if (getIntent().hasExtra("transfer_from_jeanie")) {
					Data.TRANSFER_FROM_JEANIE = getIntent().getIntExtra("transfer_from_jeanie", 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			FacebookSdk.sdkInitialize(this);

			Prefs.with(this).save(SP_OTP_SCREEN_OPEN, "");
			Utils.disableSMSReceiver(this);

			Data.locationSettingsNoPressed = false;

			Data.userData = null;
			Data.autoData = null;
			Data.setFreshData(null);


//			FlurryAgent.init(this, Config.getFlurryKey());


			Locale locale = new Locale("en");
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


			setContentView(R.layout.activity_splash_new);


			try{
				Utils.generateKeyHash(SplashNewActivity.this);
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

			if (Data.locationFetcher == null) {
				Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000);
			} else{
				Data.locationFetcher.connect();
			}

			linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
			textViewScroll = (TextView) findViewById(R.id.textViewScroll);
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
			((TextView) findViewById(R.id.textViewAlreadyHaveAccount)).setTypeface(Fonts.mavenMedium(this));

			rlSplashLogo = (RelativeLayout) findViewById(R.id.rlSplashLogo); rlSplashLogo.setVisibility(View.VISIBLE);
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

			llContainer = (LinearLayout) findViewById(R.id.llContainer);
			linearLayoutLogin = (RelativeLayout) findViewById(R.id.linearLayoutLogin);
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
			buttonFacebookLogin = (RelativeLayout) findViewById(R.id.buttonFacebookLogin);
			buttonGoogleLogin = (RelativeLayout) findViewById(R.id.buttonGoogleLogin);


			relativeLayoutSignup = (RelativeLayout) findViewById(R.id.relativeLayoutSignup);
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
			buttonFacebookSignup = (RelativeLayout) findViewById(R.id.buttonFacebookSignup);
			buttonGoogleSignup = (RelativeLayout) findViewById(R.id.buttonGoogleSignup);
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
			ivBottom = (ImageView) findViewById(R.id.ivBottom);
			ivBottom1 = (ImageView) findViewById(R.id.ivBottom1);

			linearLayoutWalletOption = (LinearLayout) findViewById(R.id.linearLayoutWalletOption);
			imageViewWalletOptionCheck = (ImageView) findViewById(R.id.imageViewWalletOptionCheck);
			textViewWalletOptionMessage = (TextView) findViewById(R.id.textViewWalletOptionMessage);
			textViewWalletOptionMessage.setTypeface(Fonts.mavenMedium(this));
			extra = (View) findViewById(R.id.extra);

			tvScroll = (TextView) findViewById(R.id.tvScroll);
			llSignupMain = (LinearLayout) findViewById(R.id.llSignupMain);

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

			linearLayoutWalletOption.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int walletFromServer = (int) linearLayoutWalletOption.getTag();
						if(linkedWallet == walletFromServer){
							linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
							imageViewWalletOptionCheck.setImageResource(R.drawable.checkbox_signup_unchecked);
						} else {
							linkedWallet = walletFromServer;
							imageViewWalletOptionCheck.setImageResource(R.drawable.checkbox_signup_checked);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


			buttonLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isBranchLinkNotClicked()) {
						FlurryEventLogger.event(LOGIN_OPTION_MAIN);
						FlurryEventLogger.eventGA(ACQUISITION, TAG, "Sign up");
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+SPLASH_SCREEN+"_"+LOGIN, bundle);
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
						FlurryEventLogger.event(SIGNUP);
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+SPLASH_SCREEN+"_"+SIGN_UP, bundle);
						FlurryEventLogger.eventGA(ACQUISITION, TAG, "Log in");
						SplashNewActivity.registerationType = RegisterationType.EMAIL;
						changeUIState(State.SIGNUP);
					} else{
						clickCount = clickCount + 1;
					}
				}
			});

			buttonNoNetCall.setVisibility(View.GONE);
			buttonNoNetCall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.openCallIntent(SplashNewActivity.this, Config.getSupportNumber(SplashNewActivity.this));
					FlurryEventLogger.event(CALL_WHEN_NO_INTERNET);
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
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(TRANSACTION+"_"+LOGIN_PAGE+"_"+BACK, bundle);
					} else if (State.SIGNUP == state) {
						performSignupBackPressed();
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(TRANSACTION+"_"+SIGN_UP_PAGE+"_"+BACK, bundle);
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
						confirmDebugPasswordPopup(SplashNewActivity.this);
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
                            Bundle bundle = new Bundle();
                            MyApplication.getInstance().logEvent(TRANSACTION+"_"+LOGIN_PAGE+"_"+LOGIN, bundle);
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


			buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
						signUpBy = "facebook";
						FlurryEventLogger.event(LOGIN_VIA_FACEBOOK);
						Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
						facebookLoginHelper.openFacebookSession();
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(TRANSACTION+"_"+LOGIN_PAGE+"_"+LOGIN_WITH_FACEBOOK, bundle);
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
					FlurryEventLogger.event(LOGIN_VIA_GOOGLE);
						signUpBy = "google";
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(TRANSACTION+"_"+LOGIN_PAGE+"_"+LOGIN_WITH_GOOGLE, bundle);
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
					FlurryEventLogger.event(FORGOT_PASSWORD);
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(TRANSACTION+"_"+LOGIN_PAGE+"_"+FORGET_PASSWORD, bundle);
					FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Forget password");
				}
			});

			callbackManager = CallbackManager.Factory.create();

			facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
				@Override
				public void facebookLoginDone(FacebookUserData facebookUserData) {
					Data.facebookUserData = facebookUserData;
					if(State.LOGIN == state || State.SIGNUP == state) {
						sendFacebookLoginValues(SplashNewActivity.this);
						FlurryEventLogger.facebookLoginClicked(Data.facebookUserData.fbId);
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

					if(!TextUtils.isEmpty(phoneFetchedEmail) && !phoneFetchedEmail.equalsIgnoreCase(emailId)
							&& !TextUtils.isEmpty(phoneFetchedName) && !phoneFetchedName.equalsIgnoreCase(name)){
						FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.NAME_EMAIL_AUTOFILLED_EDITED);
					} else if(!TextUtils.isEmpty(phoneFetchedEmail) && phoneFetchedEmail.equalsIgnoreCase(emailId)
							&& !TextUtils.isEmpty(phoneFetchedName) && phoneFetchedName.equalsIgnoreCase(name)){
						FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.NAME_EMAIL_AUTOFILLED_UNEDITED);
					}

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
                                                Bundle bundle = new Bundle();
                                                MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+SIGN_UP_PAGE+"_"+SIGN_UP, bundle);
												FlurryEventLogger.event(SIGNUP_FINAL);
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
						FlurryEventLogger.eventGA(ACQUISITION, "Sign up Page", "Terms of use");
                        Bundle bundle = new Bundle();
                        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+SIGN_UP_PAGE+"_"+TERMS_OF_USE, bundle);
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/#/terms"));
						startActivity(browserIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			initiateDeviceInfoVariables();
			startService(new Intent(this, PushPendingCallsService.class));
			showLocationEnableDialog();

			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


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

		try{
			if(!MyApplication.getInstance().getDeviceToken().equalsIgnoreCase("not_found")) {
				MyApplication.getInstance().getCleverTap().data.pushFcmRegistrationId(MyApplication.getInstance().getDeviceToken(), true);
				Log.d("Token", "token = "+MyApplication.getInstance().getDeviceToken());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverDeviceToken,
				new IntentFilter(INTENT_ACTION_DEVICE_TOKEN_UPDATE));

		llSignupMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(llSignupMain, tvScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {

			}

			@Override
			public void keyBoardClosed() {

			}
		}));

	}

	private void moveViewToScreenCenter(final View view){
		AnimationSet animSet = new AnimationSet(true);
		animSet.setFillAfter(true);
		animSet.setDuration(500);
		animSet.setInterpolator(new AccelerateDecelerateInterpolator());
		TranslateAnimation translate = new TranslateAnimation( 0, 0 , 0, ASSL.Yscale()*(-350f));
		translate.setFillAfter(true);
		animSet.addAnimation(translate);
		ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setFillAfter(true);
		animSet.addAnimation(scale);


		animSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
				lp.width = (int)ASSL.Yscale()*(648);
				lp.height = (int)ASSL.Yscale()*(253);
				//lp.topMargin = (int)ASSL.Yscale()*(400); // use topmargin for the y-property, left margin for the x-property of your view
				view.setLayoutParams(lp);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		view.startAnimation(animSet);
	}


	private void logSome(){
		Log.i("Splash", "temp");
	}

	private void changeUIState(State state) {
		imageViewJugnooLogo.requestFocus();
		llContainer.setVisibility(View.GONE);
		int duration = 500;
		switch (state) {
			case SPLASH_INIT:
				imageViewBack.setVisibility(View.GONE);
				extra.setVisibility(View.GONE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				ivBottom.setVisibility(View.VISIBLE);

				llContainer.setVisibility(View.VISIBLE);
				rlSplashLogo.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.GONE);
				relativeLayoutSignup.setVisibility(View.GONE);
				break;

			case SPLASH_LS:
				imageViewBack.setVisibility(View.GONE);

				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);
				//extra.setVisibility(View.VISIBLE);

				if(this.state == State.SPLASH_INIT) {
					/*ScaleAnimation animation = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					animation.setDuration(500);
					animation.setFillAfter(true);

					//moveViewToScreenCenter(relativeLayoutJugnooLogo);
					animation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							//relativeLayoutJugnooLogo.setScaleX(0.8f);
							//relativeLayoutJugnooLogo.setScaleY(0.8f);
							relativeLayoutLS.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
					relativeLayoutJugnooLogo.setAnimation(animation);*/

					relativeLayoutLS.setVisibility(View.VISIBLE);
					Animation animation8 = AnimationUtils.loadAnimation(this, R.anim.right_in);
					animation8.setFillAfter(true);
					animation8.setDuration(duration);
					relativeLayoutLS.startAnimation(animation8);

					Animation animation9 = AnimationUtils.loadAnimation(this, R.anim.right_out);
					animation9.setFillAfter(false);
					animation9.setDuration(duration);
					rlSplashLogo.startAnimation(animation9);
					rlSplashLogo.setVisibility(View.GONE);

				}

				if(this.state == State.LOGIN) {
					relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
					relativeLayoutLS.setVisibility(View.VISIBLE);
					Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.left_out);
					animation4.setFillAfter(false);
					animation4.setDuration(duration);
					linearLayoutLogin.startAnimation(animation4);

					Animation animation5 = AnimationUtils.loadAnimation(this, R.anim.left_in);
					animation5.setFillAfter(true);
					animation5.setDuration(duration);
					relativeLayoutLS.startAnimation(animation5);

				} else if(this.state == State.SIGNUP) {
					relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
					Animation animation6 = AnimationUtils.loadAnimation(this, R.anim.left_out);
					animation6.setFillAfter(false);
					animation6.setDuration(duration);
					relativeLayoutSignup.startAnimation(animation6);

					Animation animation7 = AnimationUtils.loadAnimation(this, R.anim.left_in);
					animation7.setFillAfter(true);
					animation7.setDuration(duration);
					relativeLayoutLS.startAnimation(animation7);

					animation6.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
				} else{
					relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
				}

				llContainer.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.GONE);
				relativeLayoutSignup.setVisibility(View.GONE);


				break;

			case SPLASH_NO_NET:
				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
				//extra.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.GONE);
				linearLayoutNoNet.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case LOGIN:
				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				//extra.setVisibility(View.VISIBLE);

				if(this.state == State.SPLASH_LS) {
					Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_in);
					animation.setFillAfter(true);
					animation.setDuration(duration);
					linearLayoutLogin.startAnimation(animation);

					Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.right_out);
					animation1.setFillAfter(false);
					animation1.setDuration(duration);
					relativeLayoutLS.startAnimation(animation1);
				}

				llContainer.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				rlSplashLogo.setVisibility(View.GONE);
				relativeLayoutLS.setVisibility(View.GONE);
				break;

			case SIGNUP:
				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.GONE);


				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.right_in);
				animation2.setFillAfter(true);
				animation2.setDuration(duration);
				relativeLayoutSignup.startAnimation(animation2);

				Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.right_out);
				animation3.setFillAfter(false);
				animation3.setDuration(duration);
				relativeLayoutLS.startAnimation(animation3);

				llContainer.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.GONE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				relativeLayoutLS.setVisibility(View.GONE);
				rlSplashLogo.setVisibility(View.GONE);
				getAllowedAuthChannels(SplashNewActivity.this);
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
					root.setBackgroundColor(getResources().getColor(R.color.white));
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
			Data.deepLinkIndex = -1;
			FlurryEventLogger.event(SIGNUP_THROUGH_REFERRAL);
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
		boolean mockLocationEnabled = false;
		if (Data.locationFetcher != null) {
			mockLocationEnabled = Utils.mockLocationEnabled(Data.locationFetcher.getLocationUnchecked());
		}
		if (mockLocationEnabled) {
			DialogPopup.alertPopupWithListener(SplashNewActivity.this, "",
					getResources().getString(R.string.disable_mock_location),
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
							finish();
							if (Data.locationFetcher != null) {
								Data.locationFetcher.destroy();
							}
							Data.locationFetcher = null;
						}
					});
		} else {
			if (Config.getDefaultServerUrl().equalsIgnoreCase(Config.getLiveServerUrl())
					&& Utils.isAppInstalled(SplashNewActivity.this, Data.DRIVER_APP_PACKAGE)) {
				DialogPopup.alertPopupWithListener(SplashNewActivity.this, "",
						getResources().getString(R.string.uninstall_driver_app),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								ActivityCompat.finishAffinity(SplashNewActivity.this);
							}
						});

			} else {
				try {
					FirebaseInstanceId.getInstance().getToken();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ("".equalsIgnoreCase(Prefs.with(this).getString(Constants.SP_DEVICE_TOKEN, ""))) {
					DialogPopup.showLoadingDialogDownwards(SplashNewActivity.this, "Loading...");
					getHandlerGoToAccessToken().removeCallbacks(getRunnableGoToAccessToken());
					getHandlerGoToAccessToken().postDelayed(getRunnableGoToAccessToken(), 5000);
				} else {
					goToAccessTokenLogin();
				}
			}
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
			DialogPopup.dismissLoadingDialog();
			Log.e("deviceToken received", "> " + MyApplication.getInstance().getDeviceToken());
			accessTokenLogin(SplashNewActivity.this);
			FlurryEventLogger.appStarted(MyApplication.getInstance().getDeviceToken());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	protected void onResume() {
		super.onResume();

		if (Data.locationFetcher == null) {
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000);
		} else{
			Data.locationFetcher.connect();
		}

		retryAccessTokenLogin();
		resumed = true;
		userVerfied = 0;
		AppEventsLogger.activateApp(this);


	}


	public void retryAccessTokenLogin() {
		try {
			if (State.LOGIN != state && State.SIGNUP != state && resumed) {
				buttonRefresh.performClick();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onPause() {
		try {
			Data.locationFetcher.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
				if (0 == resultCode) {
					Data.locationSettingsNoPressed = true;
					Data.locationAddressSettingsNoPressed = true;
				}
			} else if (requestCode == GOOGLE_SIGNIN_REQ_CODE_LOGIN) {
				if (RESULT_OK == resultCode) {
					Data.googleSignInAccount = data.getParcelableExtra(KEY_GOOGLE_PARCEL);
					sendGoogleLoginValues(this);
				}
			}
			else{
				callbackManager.onActivityResult(requestCode, resultCode, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		if (!"".equalsIgnoreCase(pair.first)) {
			String accessToken = pair.first;
			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}
			getApiLoginUsingAccessToken().hit(accessToken, Data.loginLatitude, Data.loginLongitude, null,
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

					});

		} else {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				changeUIState(State.SPLASH_LS);
				getAllowedAuthChannels(SplashNewActivity.this);
			} else {
				changeUIState(State.SPLASH_NO_NET);
			}
			sendToRegisterThroughSms(Data.deepLinkReferralCode);
		}
	}

	public void getAllowedAuthChannels(Activity activity){
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			//DialogPopup.showLoadingDialogDownwards(activity, "Loading...");
			HashMap<String, String> params = new HashMap<>();

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().getAllowedAuthChannels(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					//DialogPopup.dismissLoadingDialog();
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "Auth channel response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int showPaytm = jObj.optJSONObject("signup").optInt("PAYTM");
						int showMobikwik = jObj.optJSONObject("signup").optInt("MOBIKWIK");
						int showFreecharge = jObj.optJSONObject("signup").optInt("FREECHARGE");
						linkedWallet = jObj.optJSONObject("signup").optInt("DEFAULT");
						JSONArray jWalletOrder = jObj.optJSONArray(KEY_WALLET_ORDER);
						if(jWalletOrder != null){
							linearLayoutWalletContainerInner.removeAllViews();
							for(int i=0; i<jWalletOrder.length(); i++){
								if(Constants.KEY_PAYTM.equalsIgnoreCase(jWalletOrder.getString(i))){
									linearLayoutWalletContainerInner.addView(linearLayoutPaytm);
								}
								else if(Constants.KEY_MOBIKWIK.equalsIgnoreCase(jWalletOrder.getString(i))){
									linearLayoutWalletContainerInner.addView(linearLayoutMobikwik);
								}
								else if(Constants.KEY_FREECHARGE.equalsIgnoreCase(jWalletOrder.getString(i))){
									linearLayoutWalletContainerInner.addView(linearLayoutFreeCharge);
								}
							}
							linearLayoutWalletContainerInner.addView(linearLayoutNone);
						}

						if(showPaytm == 1){
							linearLayoutPaytm.setVisibility(View.VISIBLE);
						} else{
							linearLayoutPaytm.setVisibility(View.GONE);
							if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
								linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
							}
						}

						if(showMobikwik == 1){
							linearLayoutMobikwik.setVisibility(View.VISIBLE);
						} else{
							linearLayoutMobikwik.setVisibility(View.GONE);
							if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
								linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
							}
						}

						if(showFreecharge == 1){
							linearLayoutFreeCharge.setVisibility(View.VISIBLE);
						} else{
							linearLayoutFreeCharge.setVisibility(View.GONE);
							if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
								linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
							}
						}

						if(showPaytm == 1 || showMobikwik == 1 || showFreecharge == 1){
							linearLayoutWalletContainer.setVisibility(View.GONE);
						} else{
							linearLayoutWalletContainer.setVisibility(View.GONE);
						}

						setLinkedWalletTick();

						int showPaytm1 = showPaytm;
						int showMobikwik1 = showMobikwik;
						int showFreecharge1 = showFreecharge;
						int defaultTick = 1;

						try {
							showPaytm1 = jObj.optJSONObject("signup_new").optInt("PAYTM");
							showMobikwik1 = jObj.optJSONObject("signup_new").optInt("MOBIKWIK");
							showFreecharge1 = jObj.optJSONObject("signup_new").optInt("FREECHARGE");
							defaultTick = jObj.optJSONObject("signup_new").optInt("DEFAULT");
						} catch (Exception e) {
							e.printStackTrace();
						}

						linearLayoutWalletOption.setVisibility(View.VISIBLE);
						if(showPaytm1 == 1){
							textViewWalletOptionMessage.setText(getString(R.string.use_mobile_wallet_powered_by_format, getString(R.string.paytm)));
							linkedWallet = (defaultTick == 1) ? LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal() : LinkedWalletStatus.NO_WALLET.getOrdinal();
							linearLayoutWalletOption.setTag(LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal());
						}
						else if(showMobikwik1 == 1){
							textViewWalletOptionMessage.setText(getString(R.string.use_mobile_wallet_powered_by_format, getString(R.string.mobikwik)));
							linkedWallet = (defaultTick == 1) ? LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal() : LinkedWalletStatus.NO_WALLET.getOrdinal();
							linearLayoutWalletOption.setTag(LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal());
						}
						else if(showFreecharge1 == 1){
							textViewWalletOptionMessage.setText(getString(R.string.use_mobile_wallet_powered_by_format, getString(R.string.freecharge)));
							linkedWallet = (defaultTick == 1) ? LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal() : LinkedWalletStatus.NO_WALLET.getOrdinal();
							linearLayoutWalletOption.setTag(LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal());
						}
						else{
							linearLayoutWalletOption.setVisibility(View.GONE);
							linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
						}
						if(defaultTick == 1){
							imageViewWalletOptionCheck.setImageResource(R.drawable.checkbox_signup_checked);
						} else{
							imageViewWalletOptionCheck.setImageResource(R.drawable.checkbox_signup_unchecked);
						}

					}catch (Exception e){
						e.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					//DialogPopup.dismissLoadingDialog();
				}
			});

		} else {
			changeUIState(State.SPLASH_NO_NET);
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

				if (MyApplication.getInstance().appVersion() >= currentVersion) {
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
				DialogPopup.dismissLoadingDialog();
				HomeActivity.logoutUser(activity);
				return true;
			} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
				DialogPopup.dismissLoadingDialog();
				String errorMessage = jObj.getString("error");
				DialogPopup.alertPopup(activity, "", errorMessage);
				return true;
			} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
				DialogPopup.dismissLoadingDialog();
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

						MyApplication.getInstance().getAppSwitcher().switchApp(SplashNewActivity.this,
								Prefs.with(SplashNewActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
								getIntent().getData(), new LatLng(Data.loginLatitude, Data.loginLongitude), false);
//						Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
//						intent.setData(getIntent().getData());
//						startActivity(intent);
//						ActivityCompat.finishAffinity(SplashNewActivity.this);
//						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
				}
				else if(State.LOGIN == state || State.SIGNUP == state){
					if(SplashNewActivity.this.hasWindowFocus() && loginDataFetched){
						Map<String, String> articleParams = new HashMap<String, String>();
						articleParams.put("username", Data.userData.userName);
//						FlurryAgent.logEvent("App Login", articleParams);

						loginDataFetched = false;

						MyApplication.getInstance().getAppSwitcher().switchApp(SplashNewActivity.this,
								Prefs.with(SplashNewActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
								Data.splashIntentUri, new LatLng(Data.loginLatitude, Data.loginLongitude), false);
//						Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
//						intent.setData(Data.splashIntentUri);
//						startActivity(intent);
//						ActivityCompat.finishAffinity(SplashNewActivity.this);
//						overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
						sendIntentToOtpScreen(userVerfied);
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
			FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Back");
			performLoginBackPressed();
		} else if (State.SIGNUP == state) {
			FlurryEventLogger.eventGA(ACQUISITION, "Sign up Page", "Back");
			performSignupBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	public void confirmDebugPasswordPopup(final Activity activity) {

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_edittext_confirm);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenLight(activity));
			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode);
			etCode.setTypeface(Fonts.mavenMedium(activity));

			textHead.setText("Confirm Debug Password");
			textMessage.setText("Please enter password to continue.");

			textHead.setVisibility(View.GONE);
			textMessage.setVisibility(View.GONE);


			final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
			btnConfirm.setTypeface(Fonts.mavenRegular(activity));

			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String code = etCode.getText().toString().trim();
					if ("".equalsIgnoreCase(code)) {
						etCode.requestFocus();
						etCode.setError("Code can't be empty.");
					} else {
						if (Config.getDebugPassword().equalsIgnoreCase(code)) {
							dialog.dismiss();
							activity.startActivity(new Intent(activity, DebugOptionsActivity.class));
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
							ActivityCompat.finishAffinity(activity);
						} else {
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

			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});

			frameLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});


			dialog.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Utils.showSoftKeyboard(SplashNewActivity.this, etCode);
				}
			}, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onLocationChanged(Location location) {
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
	}


	private void showLocationEnableDialog() {
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (resp != ConnectionResult.SUCCESS) {
			Log.e("Google Play Service Error ", "=" + resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		} else {
			LocationInit.showLocationAlertDialog(this);
		}
	}

	private void initiateDeviceInfoVariables() {
		try {                                                                                        // to get AppVersion, OS version, country code and device name
			if (Config.getConfigMode() == ConfigMode.LIVE) {
				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
			} else {
				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
			}
		} catch (Exception e) {
		}
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
			DialogPopup.showLoadingDialog(activity, "Loading...");

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
								loginDataFetched = true;
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.EMAIL, new LatLng(Data.loginLatitude, Data.loginLongitude));
									Database.getInstance(SplashNewActivity.this).insertEmail(emailId);

								}
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
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

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
								facebookRegister = true;
								notRegisteredMsg = error;
								fbVerifiedNumber = jObj.optString("fb_verified_number");
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
								loginDataFetched = true;
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login with facebook");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.FACEBOOK, new LatLng(Data.loginLatitude, Data.loginLongitude));


									Database.getInstance(SplashNewActivity.this).insertEmail(Data.facebookUserData.userEmail);
								}
								DialogPopup.showLoadingDialog(activity, "Loading...");
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
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

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
								googleRegister = true;
								notRegisteredMsg = error;
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
											loginResponse, LoginVia.GOOGLE, new LatLng(Data.loginLatitude, Data.loginLongitude));
									FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Login with Google");
									loginDataFetched = true;

									Database.getInstance(SplashNewActivity.this).insertEmail(Data.googleSignInAccount.getEmail());
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


	/**
	 * Send intent to otp screen by making required data objects
	 * flag 0 for email, 1 for Facebook
	 */
	public void sendIntentToOtpScreen(int userVerified) {
		if (State.LOGIN == state) {
			DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", otpErrorMsg, new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogPopup.dismissAlertPopup();
					OTPConfirmScreen.intentFromRegister = false;
					Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
					if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
						OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
								Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
								Data.facebookUserData.accessToken,
								Data.facebookUserData.userEmail,
								Data.facebookUserData.userName,
								phoneNoOfUnverifiedAccount, "", "", accessToken);
						signUpBy = "facebook";
					} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
						OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
								Data.googleSignInAccount.getDisplayName(),
								Data.googleSignInAccount.getEmail(),
								"",
								phoneNoOfUnverifiedAccount, "", "", accessToken);
						signUpBy = "google";
					} else {
						OTPConfirmScreen.intentFromRegister = false;
						OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "", accessToken);
						signUpBy = "email";
					}

					intent.putExtra("signup_by", signUpBy);
					intent.putExtra("email", editTextEmail.getText().toString().trim());
					intent.putExtra("password", editTextPassword.getText().toString().trim());
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
			intent.putExtra("signup_by", signUpBy);
			intent.putExtra("email", editTextSEmail.getText().toString().trim());
			intent.putExtra("password", editTextSPassword.getText().toString().trim());
			intent.putExtra(USER_VERIFIED, userVerified);
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
		editTextSEmail.setText("");
		editTextSEmail.setEnabled(true);
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
			phoneFetchedName = "";
			phoneFetchedEmail = "";
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
				editTextSPhone.setText(fbVerifiedNumber);
				if(!TextUtils.isEmpty(fbVerifiedNumber)){
					FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.PHONE_AUTOFILLED_FB);
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
					if(emailNeedRegister.equalsIgnoreCase("")){
						String ownerEmail = UserEmailFetcher.getEmail(SplashNewActivity.this);
						if(ownerEmail != null && (!ownerEmail.equalsIgnoreCase(""))){
							editTextSEmail.setText(ownerEmail);
							editTextSName.setText(Utils.firstCharCapital(new OwnerInfo().OwnerInfo(SplashNewActivity.this, ownerEmail)));
							FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.NAME_EMAIL_AUTOFILLED);
							phoneFetchedName = editTextSName.getText().toString();
							phoneFetchedEmail = editTextSEmail.getText().toString();
						} else{
							editTextSEmail.setText("");
						}
					} else {
						editTextSEmail.setText(emailNeedRegister);
					}
				}
				TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String mPhoneNumber = tMgr.getLine1Number();
				editTextSPhone.setText(mPhoneNumber);
				if(!TextUtils.isEmpty(mPhoneNumber)){
					FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, Constants.PHONE_AUTOFILLED);
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
            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            if (Data.locationFetcher != null) {
                Data.loginLatitude = Data.locationFetcher.getLatitude();
                Data.loginLongitude = Data.locationFetcher.getLongitude();
            }

            params.put("user_name", name);
            params.put("phone_no", phoneNo);
            params.put("email", emailId);
            params.put("password", password);
            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);

            params.put("device_name", MyApplication.getInstance().deviceName());
            params.put("os_version", MyApplication.getInstance().osVersion());
            params.put("country", MyApplication.getInstance().country());

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
            params.put(KEY_SOURCE, JSONParser.getAppSource(this));
            String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
            if (links != null) {
                if (!"[]".equalsIgnoreCase(links)) {
                    params.put(KEY_BRANCH_REFERRING_LINKS, links);
                }
            }

            Log.i("register_using_email params", params.toString());


			new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().registerUsingEmail(params, new Callback<SettleUserDebt>() {
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
                                    FlurryEventLogger.eventGA(ACQUISITION, "Sign up Page", "Sign up");
									signUpBy = "email";
                                    SplashNewActivity.this.name = name;
                                    SplashNewActivity.this.emailId = emailId;
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);

                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
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
                                DialogPopup.dismissLoadingDialog();
                            }
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
                    Log.e(TAG, "registerUsingEmail error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
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
            DialogPopup.showLoadingDialog(activity, "Loading...");

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

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put(KEY_REFERRAL_CODE, referralCode);

            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);
            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_name", MyApplication.getInstance().deviceName());
            params.put("os_version", MyApplication.getInstance().osVersion());
            params.put("country", MyApplication.getInstance().country());
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
            params.put(KEY_SOURCE, JSONParser.getAppSource(this));
            String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
            if (links != null) {
                if (!"[]".equalsIgnoreCase(links)) {
                    params.put(KEY_BRANCH_REFERRING_LINKS, links);
                }
            }

            Log.e("register_using_facebook params", params.toString());


			new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().registerUsingFacebook(params, new Callback<SettleUserDebt>() {
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
                                    FlurryEventLogger.eventGA(ACQUISITION, "Sign up Page", "Sign up with Facebook");
									signUpBy = "facebook";
									userVerfied = jObj.optInt("user_verified", 0);
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);
                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                    SplashNewActivity.this.phoneNo = phoneNo;
                                    SplashNewActivity.this.password = password;
                                    SplashNewActivity.this.referralCode = referralCode;
                                    SplashNewActivity.this.accessToken = "";
                                    parseDataSendToMultipleAccountsScreen(activity, jObj);
                                } else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
                                    SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);
                                } else if(ApiResponseFlags.AUTH_VERIFICATION_SUCCESSFUL.getOrdinal() == flag){
									sendFacebookLoginValues(SplashNewActivity.this);
								} else{
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
                                DialogPopup.dismissLoadingDialog();
                            }
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
                    Log.e(TAG, "registerUsingFacebook error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
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
            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            if (Data.locationFetcher != null) {
                Data.loginLatitude = Data.locationFetcher.getLatitude();
                Data.loginLongitude = Data.locationFetcher.getLongitude();
            }

            params.put("google_access_token", Data.googleSignInAccount.getIdToken());

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put(KEY_REFERRAL_CODE, referralCode);

            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);
            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_name", MyApplication.getInstance().deviceName());
            params.put("os_version", MyApplication.getInstance().osVersion());
            params.put("country", MyApplication.getInstance().country());
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
            params.put(KEY_SOURCE, JSONParser.getAppSource(this));
            String links = Database2.getInstance(this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
            if (links != null) {
                if (!"[]".equalsIgnoreCase(links)) {
                    params.put(KEY_BRANCH_REFERRING_LINKS, links);
                }
            }

            Log.e("register_using_facebook params", params.toString());

			new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().registerUsingGoogle(params, new Callback<SettleUserDebt>() {
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
                                    FlurryEventLogger.eventGA(ACQUISITION, "Sign up Page", "Sign up with Google");
									signUpBy = "google";
                                    parseOTPSignUpData(jObj, password, referralCode, linkedWallet);

                                } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
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
                                DialogPopup.dismissLoadingDialog();
                            }
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
                    Log.e(TAG, "registerUsingGoogle error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
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

            final ProgressDialog progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            if (Data.locationFetcher != null) {
                Data.loginLatitude = Data.locationFetcher.getLatitude();
                Data.loginLongitude = Data.locationFetcher.getLongitude();
            }

            params.put("email", email);
            params.put("password", "");
            params.put("device_token", MyApplication.getInstance().getDeviceToken());
            params.put("device_name", MyApplication.getInstance().deviceName());
            params.put("os_version", MyApplication.getInstance().osVersion());
            params.put("country", MyApplication.getInstance().country());
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);
            params.put("client_id", Config.getAutosClientId());
            params.put("otp", otp);

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");
            }

            Log.i("params", "" + params.toString());

			new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
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
                                            loginResponse, LoginVia.EMAIL_OTP, new LatLng(Data.loginLatitude, Data.loginLongitude));
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
                                                FlurryEventLogger.event(LOGIN_OPTION_MAIN);
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
                    if (progressDialog != null) progressDialog.dismiss();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "verifyOtp error=" + error);
                    if (progressDialog != null) progressDialog.dismiss();
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
            map.put(KEY_SOURCE, Config.getDownloadSource());
            FlurryEventLogger.event(FlurryEventNames.APP_DOWNLOAD_SOURCE, map);
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
