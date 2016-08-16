//package com.sabkuchfresh;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.res.Configuration;
//import android.database.Cursor;
//import android.graphics.Typeface;
//import android.location.Location;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.text.Editable;
//import android.text.InputFilter;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.text.method.ScrollingMovementMethod;
//import android.util.Pair;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.animation.OvershootInterpolator;
//import android.view.animation.ScaleAnimation;
//import android.view.inputmethod.EditorInfo;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//import android.widget.Toast;
//
//import com.crashlytics.android.Crashlytics;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsConstants;
//import com.facebook.appevents.AppEventsLogger;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.sabkuchfresh.TokenGenerator.AccessTokenGenerator;
//import com.sabkuchfresh.TokenGenerator.HomeUtil;
//import com.sabkuchfresh.analytics.FbEvents;
//import com.sabkuchfresh.analytics.FlurryEventLogger;
//import com.sabkuchfresh.analytics.FlurryEventNames;
//import com.sabkuchfresh.analytics.NudgeClient;
//import com.sabkuchfresh.config.Config;
//import com.sabkuchfresh.config.ConfigMode;
//import com.sabkuchfresh.data.Database;
//import com.sabkuchfresh.datastructure.ApiResponseFlags;
//import com.sabkuchfresh.datastructure.EmailRegisterData;
//import com.sabkuchfresh.datastructure.FacebookRegisterData;
//import com.sabkuchfresh.datastructure.GoogleRegisterData;
//import com.sabkuchfresh.datastructure.LinkedWalletStatus;
//import com.sabkuchfresh.datastructure.LoginVia;
//import com.sabkuchfresh.datastructure.PreviousAccountInfo;
//import com.sabkuchfresh.datastructure.SPLabels;
//import com.sabkuchfresh.home.BaseActivity;
//import com.sabkuchfresh.home.DebugOptionsActivity;
//import com.sabkuchfresh.home.ForgotPasswordScreen;
//import com.sabkuchfresh.home.FreshActivity;
//import com.sabkuchfresh.home.MultipleAccountsActivity;
//import com.sabkuchfresh.home.OTPConfirmScreen;
//import com.sabkuchfresh.retrofit.RestClient;
//import com.sabkuchfresh.retrofit.model.LoginResponse;
//import com.sabkuchfresh.retrofit.model.SettleUserDebt;
//import com.sabkuchfresh.utils.ASSL;
//import com.sabkuchfresh.utils.AppStatus;
//import com.sabkuchfresh.utils.Constants;
//import com.sabkuchfresh.utils.Data;
//import com.sabkuchfresh.utils.DeviceTokenGenerator;
//import com.sabkuchfresh.utils.DialogPopup;
//import com.sabkuchfresh.utils.FacebookLoginCallback;
//import com.sabkuchfresh.utils.FacebookLoginHelper;
//import com.sabkuchfresh.utils.FacebookUserData;
//import com.sabkuchfresh.utils.Fonts;
//import com.sabkuchfresh.utils.GoogleSigninActivity;
//import com.sabkuchfresh.utils.IDeviceTokenReceiver;
//import com.sabkuchfresh.utils.JSONParser;
//import com.sabkuchfresh.utils.JsonUtils;
//import com.sabkuchfresh.utils.KeyboardLayoutListener;
//import com.sabkuchfresh.utils.LocationFetcher;
//import com.sabkuchfresh.utils.LocationInit;
//import com.sabkuchfresh.utils.Log;
//import com.sabkuchfresh.utils.Prefs;
//import com.sabkuchfresh.utils.UniqueIMEIID;
//import com.sabkuchfresh.utils.Utils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//import io.branch.referral.Branch;
//import io.branch.referral.BranchError;
//import io.fabric.sdk.android.Fabric;
//import product.clicklabs.jugnoo.R;
//import retrofit.Callback;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//import retrofit.mime.TypedByteArray;
//
//
//public class SplashNewActivity extends BaseActivity implements LocationFetcher.LocationUpdate, FlurryEventNames, Constants {
//
//	//adding drop location
//
//	RelativeLayout root;
//	LinearLayout linearLayoutMain;
//	TextView textViewScroll;
//
//	private final String TAG = SplashNewActivity.class.getSimpleName();
//
//	ImageView viewInitJugnoo, viewInitLS, viewInitSplashJugnoo;
//	RelativeLayout relativeLayoutJugnooLogo;
//	ImageView imageViewBack, imageViewJugnooLogo, imageViewAddPaytm;
//	ImageView imageViewDebug1, imageViewDebug2, imageViewDebug3;
//
//	RelativeLayout relativeLayoutLS;
//	LinearLayout linearLayoutLoginSignupButtons, linearLayoutAddPatym;
//	Button buttonLogin, buttonRegister;
//	TextView textViewTerms, textViewAddPaytm;
//	LinearLayout linearLayoutNoNet;
//	TextView textViewNoNet;
//	Button buttonNoNetCall, buttonRefresh;
//
//	LinearLayout linearLayoutLogin;
//	AutoCompleteTextView editTextEmail;
//	EditText editTextPassword;
//	TextView textViewEmailRequired, textViewPasswordRequired, textViewForgotPassword;
//	Button buttonEmailLogin, buttonFacebookLogin, buttonGoogleLogin;
//
//	LinearLayout linearLayoutSignup;
//	EditText editTextSName, editTextSEmail, editTextSPhone, editTextSPassword, editTextSPromo;
//	TextView textViewSNameRequired, textViewSEmailRequired, textViewSPhoneRequired, textViewSPasswordRequired;
//	Button buttonEmailSignup, buttonFacebookSignup, buttonGoogleSignup;
//	TextView textViewSTerms;
//
//	boolean loginDataFetched = false, resumed = false;
//
//	int debugState = 0;
//	boolean hold1 = false, hold2 = false;
//	boolean holdForBranch = false;
//	int clickCount = 0, linkedWallet = 0;
//
//	private State state = State.SPLASH_LS;
//
//
//	CallbackManager callbackManager;
//	FacebookLoginHelper facebookLoginHelper;
//
//	boolean emailRegister = false, facebookRegister = false, googleRegister = false, sendToOtpScreen = false, fromPreviousAccounts = false;
//	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "", accessToken = "",
//			emailNeedRegister = "", linkedWalletErrorMsg = "";
//	private String enteredEmail = "";
//	public static boolean phoneNoLogin = false;
//	private static final int GOOGLE_SIGNIN_REQ_CODE_LOGIN = 1124;
//
//	public void resetFlags() {
//		loginDataFetched = false;
//		emailRegister = false;
//		facebookRegister = false;
//		googleRegister = false;
//		sendToOtpScreen = false;
//		phoneNoOfUnverifiedAccount = "";
//		otpErrorMsg = "";
//		notRegisteredMsg = "";
//		emailNeedRegister = "";
//	}
//
//
//	String name = "", referralCode = "", emailId = "", phoneNo = "", password = "";
//	public static RegisterationType registerationType = RegisterationType.EMAIL;
//	public static JSONObject multipleCaseJSON;
//
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//
////	@Override
////	public void onStart() {
////		super.onStart();
////
////
////		firstTimeEvents();
////	}
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        firstTimeEvents();
//
//        Branch branch = Branch.getInstance();
//
//        branch.initSession(new Branch.BranchReferralInitListener(){
//            @Override
//            public void onInitFinished(JSONObject referringParams, BranchError error) {
//                if (error == null) {
//
//                    try {
//                        String code = JsonUtils.readString(referringParams, KEY_REFERRAL_CODE, false, false);
//                        if(!TextUtils.isEmpty(code))
//                            editTextSPromo.setText(code);
//
//                        //Toast.makeText(SplashNewActivity.this, ""+code, Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
//                    // params will be empty if no data found
//                    // ... insert custom logic here ...
//                } else {
//                    Log.i("MyApp", error.getMessage());
//                }
//            }
//        }, this.getIntent().getData(), this);
//    }
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        this.setIntent(intent);
//    }
//
//
//	private void fetchPhoneNoOtpFromBranchParams(JSONObject referringParams){
//		try {
//			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
//			if ("".equalsIgnoreCase(pair.first)) {
//				String email = referringParams.optString(KEY_EMAIL, "");
//				String phoneNumber = referringParams.optString(KEY_PHONE_NO, "");
//				String otp = referringParams.optString(KEY_OTP, "");
//				if (!"".equalsIgnoreCase(otp)) {
//					verifyOtpViaEmail(this, email, phoneNumber, otp);
//				} else{
//					throw new Exception();
//				}
//			}
//		} catch (Exception e) {
//			readSMSClickLink();
//		}
//	}
//
//
//
//	public boolean isBranchLinkNotClicked(){
//		return (!holdForBranch
//				|| clickCount > 1);
//	}
//
//	public static void initializeServerURL(Context context) {
//		String link = Prefs.with(context).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());
//
//		ConfigMode configModeToSet;
//		if (link.equalsIgnoreCase(Config.getLiveServerUrl())
//				|| link.equalsIgnoreCase(Config.getLegacyServerUrl())) {
//			configModeToSet = ConfigMode.LIVE;
//		} else if (link.equalsIgnoreCase(Config.getDevServerUrl())) {
//			configModeToSet = ConfigMode.DEV;
//		} else if (link.equalsIgnoreCase(Config.getDev1ServerUrl())) {
//			configModeToSet = ConfigMode.DEV_1;
//		} else if (link.equalsIgnoreCase(Config.getDev2ServerUrl())) {
//			configModeToSet = ConfigMode.DEV_2;
//		} else if (link.equalsIgnoreCase(Config.getDev3ServerUrl())) {
//			configModeToSet = ConfigMode.DEV_3;
//		} else {
//			Config.CUSTOM_SERVER_URL = link;
//			configModeToSet = ConfigMode.CUSTOM;
//		}
//
//		if(configModeToSet != Config.getConfigMode()){
//			RestClient.clearRestClient();
//		}
//		Config.setConfigMode(configModeToSet);
//
//		Prefs.with(context).save(SPLabels.SERVER_SELECTED, Config.getServerUrl());
//
//		RestClient.setupRestClient();
//		RestClient.setupFreshApiRestClient();
//	}
//
//    @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		try {
//
//			Fabric.with(this, new Crashlytics());
//
//			try {
//				if (getIntent().hasExtra("deep_link_class")) {
//					Data.deepLinkClassName = getIntent().getStringExtra("deep_link_class");
//				} else {
//					Data.deepLinkClassName = "";
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Data.deepLinkClassName = "";
//			}
//
//			Data.splashIntentUri = getIntent().getData();
//
//			Data.getDeepLinkIndexFromIntent(this, getIntent());
//
//
//			try {
//				Data.TRANSFER_FROM_JEANIE = 0;
//				if (getIntent().hasExtra("transfer_from_jeanie")) {
//					Data.TRANSFER_FROM_JEANIE = getIntent().getIntExtra("transfer_from_jeanie", 0);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//
//			FacebookSdk.sdkInitialize(this);
//            AppEventsLogger.activateApp(this);
//
//			Utils.disableSMSReceiver(this);
//
//			Data.locationSettingsNoPressed = false;
//
//			Data.userData = null;
//
//			initializeServerURL(this);
//
//			Locale locale = new Locale("en");
//			Locale.setDefault(locale);
//			Configuration config = new Configuration();
//			config.locale = locale;
//			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//
//
//			setContentView(R.layout.activity_splash_new);
//
//
//			resumed = false;
//
//			debugState = 0;
//
//			resetFlags();
//			enteredEmail = "";
//
//			hold1 = false;
//			hold2 = false;
//
//			root = (RelativeLayout) findViewById(R.id.root);
//			new ASSL(SplashNewActivity.this, root, 1134, 720, false);
//
//			holdForBranch = false;
//			clickCount = 0;
//
//			if (Data.locationFetcher == null) {
//				Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
//			} else{
//				Data.locationFetcher.connect();
//			}
//
//			linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
//			textViewScroll = (TextView) findViewById(R.id.textViewScroll);
//
//			viewInitJugnoo = (ImageView) findViewById(R.id.viewInitJugnoo);
//			viewInitSplashJugnoo = (ImageView) findViewById(R.id.viewInitSplashJugnoo);
//			viewInitLS = (ImageView) findViewById(R.id.viewInitLS);
//
//			relativeLayoutJugnooLogo = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooLogo);
//			imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
//			imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
//			imageViewDebug1 = (ImageView) findViewById(R.id.imageViewDebug1);
//			imageViewDebug2 = (ImageView) findViewById(R.id.imageViewDebug2);
//			imageViewDebug3 = (ImageView) findViewById(R.id.imageViewDebug3);
//			textViewAddPaytm = (TextView) findViewById(R.id.textViewAddPaytm);
//			textViewAddPaytm.setTypeface(Fonts.mavenLight(this));
//			imageViewAddPaytm = (ImageView) findViewById(R.id.imageViewAddPaytm);
//			linearLayoutAddPatym = (LinearLayout) findViewById(R.id.linearLayoutAddPatym);
//
//			relativeLayoutLS = (RelativeLayout) findViewById(R.id.relativeLayoutLS);
//			linearLayoutLoginSignupButtons = (LinearLayout) findViewById(R.id.linearLayoutLoginSignupButtons);
//			buttonLogin = (Button) findViewById(R.id.buttonLogin);
//			buttonLogin.setTypeface(Fonts.mavenRegular(this));
//			buttonRegister = (Button) findViewById(R.id.buttonRegister);
//			buttonRegister.setTypeface(Fonts.mavenRegular(this));
//			textViewTerms = (TextView) findViewById(R.id.textViewTerms);
//			textViewTerms.setTypeface(Fonts.latoRegular(this));
//			((TextView) findViewById(R.id.textViewAlreadyHaveAccount)).setTypeface(Fonts.latoRegular(this));
//
//			linearLayoutNoNet = (LinearLayout) findViewById(R.id.linearLayoutNoNet);
//			textViewNoNet = (TextView) findViewById(R.id.textViewNoNet);
//			textViewNoNet.setTypeface(Fonts.latoRegular(this));
//			buttonNoNetCall = (Button) findViewById(R.id.buttonNoNetCall);
//			buttonNoNetCall.setTypeface(Fonts.mavenRegular(this));
//			buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
//			buttonRefresh.setTypeface(Fonts.mavenRegular(this));
//
//			ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
//			scale.setDuration(300);
//			scale.setInterpolator(new OvershootInterpolator());
//
//			//buttonLogin.startAnimation(scale);
//
//
//			String[] emails = Database.getInstance(this).getEmails();
//			ArrayAdapter<String> adapter;
//			if (emails == null) {
//				emails = new String[]{};
//			}
//			adapter = new ArrayAdapter<>(this, R.layout.dropdown_textview, emails);
//			adapter.setDropDownViewResource(R.layout.dropdown_textview);
//
//			linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
//			editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
//			editTextEmail.setTypeface(Fonts.latoRegular(this));
//			editTextEmail.setAdapter(adapter);
//			editTextPassword = (EditText) findViewById(R.id.editTextPassword);
//			editTextPassword.setTypeface(Fonts.latoRegular(this), Typeface.ITALIC);
//			textViewEmailRequired = (TextView) findViewById(R.id.textViewEmailRequired);
//			textViewEmailRequired.setTypeface(Fonts.latoRegular(this));
//			textViewPasswordRequired = (TextView) findViewById(R.id.textViewPasswordRequired);
//			textViewPasswordRequired.setTypeface(Fonts.latoRegular(this));
//			((TextView) findViewById(R.id.textViewLoginOr)).setTypeface(Fonts.latoRegular(this));
//			textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
//			textViewForgotPassword.setTypeface(Fonts.mavenRegular(this));
//			buttonEmailLogin = (Button) findViewById(R.id.buttonEmailLogin);
//			buttonEmailLogin.setTypeface(Fonts.mavenRegular(this));
//			buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin);
//			buttonFacebookLogin.setTypeface(Fonts.mavenRegular(this));
//			buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
//			buttonGoogleLogin.setTypeface(Fonts.mavenRegular(this));
//
//
//			linearLayoutSignup = (LinearLayout) findViewById(R.id.linearLayoutSignup);
//			editTextSName = (EditText) findViewById(R.id.editTextSName);
//			editTextSName.setTypeface(Fonts.latoRegular(this));
//			editTextSEmail = (EditText) findViewById(R.id.editTextSEmail);
//			editTextSEmail.setTypeface(Fonts.latoRegular(this));
//			editTextSPhone = (EditText) findViewById(R.id.editTextSPhone);
//			editTextSPhone.setTypeface(Fonts.latoRegular(this));
//			editTextSPassword = (EditText) findViewById(R.id.editTextSPassword);
//			editTextSPassword.setTypeface(Fonts.latoRegular(this));
//			editTextSPromo = (EditText) findViewById(R.id.editTextSPromo);
//			editTextSPromo.setTypeface(Fonts.latoRegular(this));
//			textViewSNameRequired = (TextView) findViewById(R.id.textViewSNameRequired);
//			textViewSNameRequired.setTypeface(Fonts.latoRegular(this));
//			textViewSEmailRequired = (TextView) findViewById(R.id.textViewSEmailRequired);
//			textViewSEmailRequired.setTypeface(Fonts.latoRegular(this));
//			textViewSPhoneRequired = (TextView) findViewById(R.id.textViewSPhoneRequired);
//			textViewSPhoneRequired.setTypeface(Fonts.latoRegular(this));
//			textViewSPasswordRequired = (TextView) findViewById(R.id.textViewSPasswordRequired);
//			textViewSPasswordRequired.setTypeface(Fonts.latoRegular(this));
//			((TextView) findViewById(R.id.textViewSignupOr)).setTypeface(Fonts.latoRegular(this));
//			((TextView) findViewById(R.id.textViewSPhone91)).setTypeface(Fonts.latoRegular(this));
//			buttonEmailSignup = (Button) findViewById(R.id.buttonEmailSignup);
//			buttonEmailSignup.setTypeface(Fonts.mavenRegular(this));
//			buttonFacebookSignup = (Button) findViewById(R.id.buttonFacebookSignup);
//			buttonFacebookSignup.setTypeface(Fonts.mavenRegular(this));
//			buttonGoogleSignup = (Button) findViewById(R.id.buttonGoogleSignup);
//			buttonGoogleSignup.setTypeface(Fonts.mavenRegular(this));
//			textViewSTerms = (TextView) findViewById(R.id.textViewSTerms);
//			textViewSTerms.setTypeface(Fonts.latoRegular(this));
//
//			root.setOnClickListener(onClickListenerKeybordHide);
//
//			relativeLayoutJugnooLogo.setOnClickListener(onClickListenerKeybordHide);
//
//			KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
//					new KeyboardLayoutListener.KeyBoardStateHandler() {
//						@Override
//						public void keyboardOpened() {
//	//						if(State.LOGIN == state){
//	//							relativeLayoutJugnooLogo.setVisibility(View.GONE);
//	//						}
//						}
//
//						@Override
//						public void keyBoardClosed() {
//	//						if(State.LOGIN == state){
//	//							relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//	//						}
//						}
//					});
//			keyboardLayoutListener.setResizeTextView(false);
//			linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
//
//			linearLayoutAddPatym.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
//						linkedWallet = LinkedWalletStatus.NO_WALLET.getOrdinal();
//						imageViewAddPaytm.setImageResource(R.drawable.checkbox_signup_unchecked);
//                        FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.PAYTM_SIGNUP, FlurryEventNames.NO);
//					} else {
//						linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal();
//						imageViewAddPaytm.setImageResource(R.drawable.checkbox_signup_checked);
//                        FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.PAYTM_SIGNUP, FlurryEventNames.YES);
//					}
//				}
//			});
//
//			buttonLogin.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (isBranchLinkNotClicked()) {
//						linkedWallet = 0;
//						changeUIState(State.LOGIN);
//					} else {
//						clickCount = clickCount + 1;
//					}
//				}
//			});
//
//			buttonRegister.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(isBranchLinkNotClicked()) {
//						linkedWallet = 1;
//						SplashNewActivity.registerationType = RegisterationType.EMAIL;
//						changeUIState(State.SIGNUP);
//					} else{
//						clickCount = clickCount + 1;
//					}
//				}
//			});
//
//			textViewTerms.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					//				try {
//					//					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/#/terms"));
//					//					startActivity(browserIntent);
//					//				} catch (Exception e) {
//					//					e.printStackTrace();
//					//				}
//				}
//			});
//
//			buttonNoNetCall.setVisibility(View.GONE);
//			buttonNoNetCall.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Utils.openCallIntent(SplashNewActivity.this, Config.getSupportNumber(SplashNewActivity.this));
//				}
//			});
//
//			buttonRefresh.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (!loginDataFetched) {
//						getDeviceToken();
//					}
//				}
//			});
//
//
//			imageViewBack.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (State.LOGIN == state) {
//						performLoginBackPressed();
//					} else if (State.SIGNUP == state) {
//						performSignupBackPressed();
//					}
//					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
//				}
//			});
//
//
//			imageViewDebug1.setOnClickListener(onClickListenerKeybordHide);
//			imageViewDebug2.setOnClickListener(onClickListenerKeybordHide);
//			imageViewDebug3.setOnClickListener(onClickListenerKeybordHide);
//			imageViewDebug1.setOnLongClickListener(new View.OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					hold1 = true;
//					callAfterBothHoldSuccessfully();
//					return false;
//				}
//			});
//
//			imageViewDebug2.setOnLongClickListener(new View.OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					hold2 = true;
//					callAfterBothHoldSuccessfully();
//					return false;
//				}
//			});
//
//			imageViewDebug3.setOnLongClickListener(new View.OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					if (debugState == 1) {
//						confirmDebugPasswordPopup(SplashNewActivity.this);
//						resetDebugFlags();
//					}
//					return false;
//				}
//			});
//
//			editTextEmail.addTextChangedListener(new CustomTextWatcher(textViewEmailRequired));
//			editTextPassword.addTextChangedListener(new CustomTextWatcher(textViewPasswordRequired));
//			editTextEmail.setOnFocusChangeListener(onFocusChangeListener);
//			editTextPassword.setOnFocusChangeListener(onFocusChangeListener);
//
//			buttonEmailLogin.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//                    FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.LOGIN, FlurryEventNames.EMAIL);
//					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
//					String email = editTextEmail.getText().toString().trim();
//					String password = editTextPassword.getText().toString().trim();
//					if ("".equalsIgnoreCase(email)) {
//						editTextEmail.requestFocus();
//						editTextEmail.setError(getResources().getString(R.string.nl_login_email_empty_error));
//					} else {
//						if ("".equalsIgnoreCase(password)) {
//							editTextPassword.requestFocus();
//							editTextPassword.setError(getResources().getString(R.string.nl_login_empty_password_error));
//						} else {
//							boolean onlyDigits = Utils.checkIfOnlyDigits(email);
//							if (onlyDigits) {
//								email = Utils.retrievePhoneNumberTenChars(email);
//								if (!Utils.validPhoneNumber(email)) {
//									editTextEmail.requestFocus();
//									editTextEmail.setError(getResources().getString(R.string.nl_login_invalid_email_error));
//								} else {
//									email = "+91" + email;
//									sendLoginValues(SplashNewActivity.this, email, password, true);
//									phoneNoLogin = true;
//								}
//							} else {
//								if (Utils.isEmailValid(email)) {
//									enteredEmail = email;
//									sendLoginValues(SplashNewActivity.this, email, password, false);
//									phoneNoLogin = false;
//								} else {
//									editTextEmail.requestFocus();
//									editTextEmail.setError("Please enter valid email");
//								}
//							}
//
//
//						}
//					}
//				}
//			});
//			editTextEmail.addTextChangedListener(new TextWatcher() {
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//				}
//
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//				}
//
//				@Override
//				public void afterTextChanged(Editable s) {
//					if (Utils.checkIfOnlyDigits(s.toString())) {
//						InputFilter[] fArray = new InputFilter[1];
//						fArray[0] = new InputFilter.LengthFilter(10);
//						editTextEmail.setFilters(fArray);
//					} else {
//						InputFilter[] fArray = new InputFilter[1];
//						fArray[0] = new InputFilter.LengthFilter(1000);
//						editTextEmail.setFilters(fArray);
//					}
//				}
//			});
//
//
//			buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					if (AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
//                        FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.LOGIN, FlurryEventNames.FB);
//						Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
//						facebookLoginHelper.openFacebookSession();
//					} else {
//						DialogPopup.dialogNoInternet(SplashNewActivity.this,
//								Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
//								new Utils.AlertCallBackWithButtonsInterface() {
//									@Override
//									public void positiveClick(View v) {
//										buttonFacebookLogin.performClick();
//									}
//
//									@Override
//									public void neutralClick(View v) {
//									}
//
//									@Override
//									public void negativeClick(View v) {
//									}
//								});
//					}
//				}
//			});
//			buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
//                        FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.LOGIN, FlurryEventNames.GOOGLE);
//					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
//					startActivityForResult(new Intent(SplashNewActivity.this, GoogleSigninActivity.class),
//							GOOGLE_SIGNIN_REQ_CODE_LOGIN);
//					} else{
//						DialogPopup.dialogNoInternet(SplashNewActivity.this,
//								Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
//								new Utils.AlertCallBackWithButtonsInterface() {
//									@Override
//									public void positiveClick(View v) {
//										buttonGoogleLogin.performClick();
//									}
//
//									@Override
//									public void neutralClick(View v) {
//									}
//
//									@Override
//									public void negativeClick(View v) {
//									}
//								});
//					}
//				}
//			});
//			textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//                    FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.FORGOT_PASSWORD, FlurryEventNames.FORGOT_PASSWORD);
//					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
//					ForgotPasswordScreen.emailAlready = editTextEmail.getText().toString();
//					startActivity(new Intent(SplashNewActivity.this, ForgotPasswordScreen.class));
//					overridePendingTransition(R.anim.right_in, R.anim.right_out);
//					finish();
//				}
//			});
//
//			callbackManager = CallbackManager.Factory.create();
//
//			facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
//				@Override
//				public void facebookLoginDone(FacebookUserData facebookUserData) {
//					Data.facebookUserData = facebookUserData;
//					if(State.LOGIN == state || State.SIGNUP == state) {
//						sendFacebookLoginValues(SplashNewActivity.this);
//						FlurryEventLogger.facebookLoginClicked(Data.facebookUserData.fbId);
//					}
//				}
//
//				@Override
//				public void facebookLoginError(String message) {
//					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//				}
//			});
//			editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					editTextPassword.requestFocus();
//					return true;
//				}
//			});
//			editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					buttonEmailLogin.performClick();
//					return true;
//				}
//			});
//
//			editTextSName.addTextChangedListener(new CustomTextWatcher(textViewSNameRequired));
//			editTextSEmail.addTextChangedListener(new CustomTextWatcher(textViewSEmailRequired));
//			editTextSPhone.addTextChangedListener(new CustomTextWatcher(textViewSPhoneRequired));
//			editTextSPassword.addTextChangedListener(new CustomTextWatcher(textViewSPasswordRequired));
//
//			editTextSName.setOnFocusChangeListener(onFocusChangeListener);
//			editTextSEmail.setOnFocusChangeListener(onFocusChangeListener);
//			editTextSPhone.setOnFocusChangeListener(onFocusChangeListener);
//			editTextSPassword.setOnFocusChangeListener(onFocusChangeListener);
//			editTextSPromo.setOnFocusChangeListener(onFocusChangeListener);
//
//			buttonEmailSignup.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Utils.hideSoftKeyboard(SplashNewActivity.this, editTextSName);
//                    //FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.SIGNUP, FlurryEventNames.EMAIL);
//					String name = editTextSName.getText().toString().trim();
//					if (name.length() > 0) {
//						name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
//					}
//					String referralCode = editTextSPromo.getText().toString().trim();
//					String emailId = editTextSEmail.getText().toString().trim();
//					boolean noFbEmail = false;
//
//					if (RegisterationType.FACEBOOK == registerationType && emailId.equalsIgnoreCase("")) {
//						emailId = "n@n.c";
//						noFbEmail = true;
//					}
//
//
//					String phoneNo = editTextSPhone.getText().toString().trim();
//					String password = editTextSPassword.getText().toString().trim();
//
//
//					if ("".equalsIgnoreCase(name) || (name.startsWith("."))) {
//						editTextSName.requestFocus();
//						editTextSName.setError("Please enter name");
//					} else if (!Utils.hasAlphabets(name)) {
//						editTextSName.requestFocus();
//						editTextSName.setError("Please enter at least one alphabet");
//					} else {
//						if ("".equalsIgnoreCase(emailId)) {
//							editTextSEmail.requestFocus();
//							editTextSEmail.setError("Please enter email id");
//						} else {
//							if ("".equalsIgnoreCase(phoneNo)) {
//								editTextSPhone.requestFocus();
//								editTextSPhone.setError("Please enter phone number");
//							} else {
//								phoneNo = Utils.retrievePhoneNumberTenChars(phoneNo);
//								if (!Utils.validPhoneNumber(phoneNo)) {
//									editTextSPhone.requestFocus();
//									editTextSPhone.setError("Please enter valid phone number");
//								} else {
//									phoneNo = "+91" + phoneNo;
//									if ("".equalsIgnoreCase(password)) {
//										editTextSPassword.requestFocus();
//										editTextSPassword.setError("Please enter password");
//									} else {
//										if (Utils.isEmailValid(emailId)) {
//											if (password.length() >= 6) {
//												Prefs.with(SplashNewActivity.this).save(SP_REFERRAL_CODE, referralCode);
//												if (RegisterationType.FACEBOOK == registerationType) {
//                                                    if (noFbEmail) {
//                                                        emailId = "";
//                                                    }
//                                                    if (!Data.facebookUserData.userEmail.equalsIgnoreCase(editTextSEmail.getText().toString().trim())) {
//                                                        Data.facebookUserData.setUserEmail(editTextSEmail.getText().toString().trim());
//                                                    }
//                                                    FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.SIGNUP, FlurryEventNames.FB);
//
//													sendFacebookSignupValues(SplashNewActivity.this, referralCode, phoneNo, password, linkedWallet);
//												} else if (RegisterationType.GOOGLE == registerationType) {
//                                                    FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.SIGNUP, FlurryEventNames.GOOGLE);
//													sendGoogleSignupValues(SplashNewActivity.this, referralCode, phoneNo, password, linkedWallet);
//												} else {
//                                                    FlurryEventLogger.event(FlurryEventNames.USER_ACCOUNT, FlurryEventNames.SIGNUP, FlurryEventNames.EMAIL);
//													sendSignupValues(SplashNewActivity.this, name, referralCode, emailId, phoneNo, password, linkedWallet);
//												}
//											} else {
//												editTextSPassword.requestFocus();
//												editTextSPassword.setError("Password must be of atleast six characters");
//											}
//
//										} else {
//											editTextSEmail.requestFocus();
//											editTextSEmail.setError("Please enter valid email id");
//										}
//
//									}
//								}
//							}
//						}
//					}
//
//				}
//			});
//			buttonFacebookSignup.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					buttonFacebookLogin.performClick();
//				}
//			});
//			buttonGoogleSignup.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					buttonGoogleLogin.performClick();
//				}
//			});
//			editTextSName.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					editTextSEmail.requestFocus();
//					return true;
//				}
//			});
//			editTextSEmail.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					editTextSPhone.requestFocus();
//					return true;
//				}
//			});
//			editTextSPhone.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					editTextSPassword.requestFocus();
//					return true;
//				}
//			});
//			editTextSPassword.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					editTextSPromo.requestFocus();
//					return true;
//				}
//			});
//			editTextSPromo.setOnEditorActionListener(new OnEditorActionListener() {
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					buttonEmailSignup.performClick();
//					return true;
//				}
//			});
//			textViewSTerms.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					try {
//						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/#/termsFatafat"));
//						startActivity(browserIntent);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//
//
//			initiateDeviceInfoVariables();
//			//showLocationEnableDialog();
//
//			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//
//			try {
//				if (getIntent().hasExtra(KEY_SPLASH_STATE)) {
//					int stateInt = getIntent().getIntExtra(KEY_SPLASH_STATE, State.SPLASH_INIT.getOrdinal());
//					if (State.LOGIN.getOrdinal() == stateInt) {
//						state = State.LOGIN;
//					} else if (State.SIGNUP.getOrdinal() == stateInt) {
//						state = State.SIGNUP;
//					} else if (State.SPLASH_LS.getOrdinal() == stateInt) {
//						state = State.SPLASH_LS;
//					} else {
//						state = State.SPLASH_INIT;
//					}
//				} else {
//					state = State.SPLASH_INIT;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				state = State.SPLASH_INIT;
//			}
//
//			changeUIState(state);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		logSome();
//
//	}
//
//
//	private void logSome(){
//		Log.i("Splash", "temp");
//	}
//
//	private void changeUIState(State state) {
//		imageViewJugnooLogo.requestFocus();
//		switch (state) {
//			case SPLASH_INIT:
//				viewInitJugnoo.setVisibility(View.VISIBLE);
//				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
//				viewInitLS.setVisibility(View.VISIBLE);
//
//				imageViewBack.setVisibility(View.GONE);
//				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//
//				relativeLayoutLS.setVisibility(View.VISIBLE);
//				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
//
//				linearLayoutLogin.setVisibility(View.VISIBLE);
//				linearLayoutSignup.setVisibility(View.VISIBLE);
//				break;
//
//			case SPLASH_LS:
//				viewInitJugnoo.setVisibility(View.GONE);
//				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
//				viewInitLS.setVisibility(View.GONE);
//
//				imageViewBack.setVisibility(View.GONE);
//				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//
//				relativeLayoutLS.setVisibility(View.VISIBLE);
//				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
//
//				linearLayoutLogin.setVisibility(View.VISIBLE);
//				linearLayoutSignup.setVisibility(View.VISIBLE);
//				break;
//
//			case SPLASH_NO_NET:
//				viewInitJugnoo.setVisibility(View.GONE);
//				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
//				viewInitLS.setVisibility(View.GONE);
//
//				imageViewBack.setVisibility(View.GONE);
//				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//
//				relativeLayoutLS.setVisibility(View.VISIBLE);
//				linearLayoutLoginSignupButtons.setVisibility(View.GONE);
//				linearLayoutNoNet.setVisibility(View.VISIBLE);
//
//				linearLayoutLogin.setVisibility(View.GONE);
//				linearLayoutSignup.setVisibility(View.GONE);
//				break;
//
//			case LOGIN:
//				viewInitJugnoo.setVisibility(View.GONE);
//				viewInitSplashJugnoo.setVisibility(View.GONE);
//				viewInitLS.setVisibility(View.GONE);
//
//				imageViewBack.setVisibility(View.VISIBLE);
//				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//
//				relativeLayoutLS.setVisibility(View.GONE);
//				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
//
//				linearLayoutLogin.setVisibility(View.VISIBLE);
//				linearLayoutSignup.setVisibility(View.GONE);
//				break;
//
//			case SIGNUP:
//				viewInitJugnoo.setVisibility(View.GONE);
//				viewInitSplashJugnoo.setVisibility(View.GONE);
//				viewInitLS.setVisibility(View.GONE);
//
//				imageViewBack.setVisibility(View.VISIBLE);
//				relativeLayoutJugnooLogo.setVisibility(View.GONE);
//
//				relativeLayoutLS.setVisibility(View.GONE);
//				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
//
//				linearLayoutLogin.setVisibility(View.GONE);
//				linearLayoutSignup.setVisibility(View.VISIBLE);
//				break;
//
//		}
//		this.state = state;
//
//		if(State.SPLASH_INIT == state) {
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					getDeviceToken();
//				}
//			}, 500);
//		}
//		else if(State.LOGIN == state){
//			// set login screen values according to intent
//			setLoginScreenValuesOnCreate();
//		}
//		else if(State.SIGNUP == state) {
//			// set signupscreen values according to intent
//			setSignupScreenValuesOnCreate();
//		}
//	}
//
//	private void readSMSClickLink(){
//		try {
//			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
//			if ("".equalsIgnoreCase(pair.first) && !Data.linkFoundOnce) {
//				new ReadSMSClickLinkAsync().execute();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void callAfterBothHoldSuccessfully() {
//		if (hold1 && hold2) {
//			debugState = 1;
//			root.setBackgroundColor(getResources().getColor(R.color.theme_color_pressed));
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					root.setBackgroundResource(R.drawable.splash_bg);
//				}
//			}, 200);
//			hold1 = false;
//			hold2 = false;
//		}
//	}
//
//	private void resetDebugFlags() {
//		hold1 = false;
//		hold2 = false;
//		debugState = 0;
//	}
//
//
//	private void sendToRegisterThroughSms(String referralCode) {
//		if (!"".equalsIgnoreCase(referralCode)) {
//			Data.deepLinkIndex = -1;
//			SplashNewActivity.registerationType = RegisterationType.EMAIL;
//			setIntent(new Intent().putExtra(KEY_REFERRAL_CODE, referralCode));
//			changeUIState(State.SIGNUP);
//		}
//	}
//
//	private View.OnClickListener onClickListenerKeybordHide = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			Utils.hideSoftKeyboard(SplashNewActivity.this, editTextSName);
//		}
//	};
//
//	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
//		@Override
//		public void onFocusChange(View v, boolean hasFocus) {
//			if (!hasFocus && v instanceof EditText) {
//				((EditText) v).setError(null);
//			}
//		}
//	};
//
//	public void getDeviceToken() {
//		boolean mockLocationEnabled = false;
//		if(Data.locationFetcher != null){
//			mockLocationEnabled = Utils.mockLocationEnabled(Data.locationFetcher.getLocationUnchecked());
//		}
//		if(mockLocationEnabled) {
//			DialogPopup.alertPopupWithListener(SplashNewActivity.this, "",
//					getResources().getString(R.string.disable_mock_location),
//					new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
//							finish();
//							if(Data.locationFetcher != null) {
//								Data.locationFetcher.destroy();
//							}
//							Data.locationFetcher = null;
//						}
//					});
//		} else {
//			DialogPopup.showLoadingDialogDownwards(SplashNewActivity.this, "Loading...");
//			new DeviceTokenGenerator().generateDeviceToken(SplashNewActivity.this, new IDeviceTokenReceiver() {
//
//				@Override
//				public void deviceTokenReceived(final String regId) {
//					runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							DialogPopup.dismissLoadingDialog();
//							Data.deviceToken = regId;
//							Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
//							accessTokenLogin(SplashNewActivity.this);
////							FlurryEventLogger.appStarted(regId);
//						}
//					});
//
//				}
//			});
//		}
//	}
//
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (Data.locationFetcher == null) {
//			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
//		} else{
//			Data.locationFetcher.connect();
//		}
//
//		retryAccessTokenLogin();
//		resumed = true;
//
//		AppEventsLogger.activateApp(this);
//
//		NudgeClient.getGcmClient(this);
//
//	}
//
//
//	public void retryAccessTokenLogin() {
//		if (State.LOGIN != state && State.SIGNUP != state && resumed) {
//			buttonRefresh.performClick();
//		}
//	}
//
//
//	@Override
//	protected void onPause() {
//		try {
//			Data.locationFetcher.destroy();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		super.onPause();
//
//		AppEventsLogger.deactivateApp(this);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		try {
//			super.onActivityResult(requestCode, resultCode, data);
//			if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
//				if (0 == resultCode) {
//					Data.locationSettingsNoPressed = true;
//				}
//			} else if (requestCode == GOOGLE_SIGNIN_REQ_CODE_LOGIN) {
//				if (RESULT_OK == resultCode) {
//					Data.googleSignInAccount = data.getParcelableExtra(KEY_GOOGLE_PARCEL);
//					sendGoogleLoginValues(this);
//				}
//			}
//			else{
//				callbackManager.onActivityResult(requestCode, resultCode, data);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	/**
//	 * ASync for access token login from server
//	 */
//	public void accessTokenLogin(final Activity activity) {
//		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
//		if (!"".equalsIgnoreCase(pair.first)) {
//			String accessToken = pair.first;
//
//			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//
//				DialogPopup.showLoadingDialogDownwards(activity, "Loading...");
//
//				if (Data.locationFetcher != null) {
//					Data.loginLatitude = Data.locationFetcher.getLatitude();
//					Data.loginLongitude = Data.locationFetcher.getLongitude();
//				}
//
//				HashMap<String, String> params = new HashMap<>();
//				params.put("access_token", accessToken);
//				params.put("device_token", Data.getDeviceToken());
//
//
//				params.put("latitude", "" + Data.loginLatitude);
//				params.put("longitude", "" + Data.loginLongitude);
//
//
//				params.put("app_version", "" + Data.appVersion);
//				params.put("device_type", Data.DEVICE_TYPE);
//				params.put("unique_device_id", Data.uniqueDeviceId);
//				params.put("client_id", Config.getClientId());
//				params.put("is_access_token_new", "" + pair.second);
//
//				if (Utils.isDeviceRooted()) {
//					params.put("device_rooted", "1");
//				} else {
//					params.put("device_rooted", "0");
//				}
//
//				Log.e("params login_using_access_token", "=" + params);
//
//				final long startTime = System.currentTimeMillis();
//				RestClient.getFreshApiService().loginUsingAccessToken(params, new Callback<LoginResponse>() {
//					@Override
//					public void success(LoginResponse loginResponse, Response response) {
//						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
//						Log.i(TAG, "loginUsingAccessToken response = " + responseStr);
//						performLoginSuccess(activity, responseStr, loginResponse);
//					}
//
//					@Override
//					public void failure(RetrofitError error) {
//						Log.e(TAG, "loginUsingAccessToken error="+error.toString());
//						performLoginFailure(activity);
//					}
//				});
//
//			} else {
//				changeUIState(State.SPLASH_NO_NET);
//			}
//		} else {
//			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//				changeUIState(State.SPLASH_LS);
//			} else {
//				changeUIState(State.SPLASH_NO_NET);
//			}
//			sendToRegisterThroughSms(Data.deepLinkReferralCode);
//		}
//	}
//
//
//	public void performLoginSuccess(Activity activity, String response, LoginResponse loginResponse) {
//		try {
//			JSONObject jObj = new JSONObject(response);
//
//			int flag = jObj.getInt("flag");
//
//			if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//				if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
//					String error = jObj.getString("error");
//					DialogPopup.alertPopup(activity, "", error);
//					DialogPopup.dismissLoadingDialog();
//				} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
//					String error = jObj.getString("message");
//					DialogPopup.alertPopup(activity, "", error);
//					DialogPopup.dismissLoadingDialog();
//				} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
//					if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
//						accessTokenDataParseAsync(activity, response, loginResponse);
//					} else {
//						DialogPopup.dismissLoadingDialog();
//					}
//				} else {
//					DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//					DialogPopup.dismissLoadingDialog();
//				}
//			} else {
//				DialogPopup.dismissLoadingDialog();
//			}
//
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//			DialogPopup.dismissLoadingDialog();
//		}
//	}
//
//	public void performLoginFailure(Activity activity) {
//		DialogPopup.dismissLoadingDialog();
//		DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//		DialogPopup.dismissLoadingDialog();
//		changeUIState(State.SPLASH_NO_NET);
//	}
//
//	public void accessTokenDataParseAsync(Activity activity, String response, LoginResponse loginResponse){
//		String resp;
//		try {
//			resp = new JSONParser().parseAccessTokenLoginData(activity, response, loginResponse, LoginVia.ACCESS);
//            FlurryEventLogger.userLogin(Data.userData.getUserId(), "User Sign In", "Sign In through accessToken");
//			Log.e("AccessTokenDataParseAsync resp", "=" + resp);
//		} catch (Exception e) {
//			e.printStackTrace();
//			resp = SERVER_TIMEOUT;
//		}
//		if (resp.contains(SERVER_TIMEOUT)) {
//			loginDataFetched = false;
//			DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//		} else {
//			loginDataFetched = true;
//		}
//		DialogPopup.dismissLoadingDialog();
//
//	}
//
//
//	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception {
////		"popup": {
////	        "title": "Update Version",
////	        "text": "Update app with new version!",
////	        "cur_version": 116,			// could be used for local check
////	        "is_force": 1				// 1 for forced, 0 for not forced
////	}
//		if (!jObj.isNull("popup")) {
//			try {
//				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup");
//				String title = jupdatePopupInfo.getString("title");
//				String text = jupdatePopupInfo.getString("text");
//				int currentVersion = jupdatePopupInfo.getInt("cur_version");
//				int isForce = jupdatePopupInfo.getInt("is_force");
//
//				if (Data.appVersion >= currentVersion) {
//					return false;
//				} else {
//					SplashNewActivity.appUpdatePopup(title, text, isForce, activity);
//					if (isForce == 1) {
//						return true;
//					} else {
//						return false;
//					}
//				}
//			} catch (Exception e) {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
//
//
//	/**
//	 * Displays appUpdatePopup dialog
//	 */
//	public static void appUpdatePopup(String title, String message, final int isForced, final Activity activity) {
//		try {
//
//			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
//			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
//			dialog.setContentView(R.layout.dialog_custom_two_buttons);
//
//			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
//			new ASSL(activity, frameLayout, 1134, 720, false);
//
//			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//			layoutParams.dimAmount = 0.6f;
//			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//			dialog.setCancelable(false);
//			dialog.setCanceledOnTouchOutside(false);
//
//
//			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
//			textHead.setTypeface(Fonts.mavenRegular(activity));
//			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
//			textMessage.setTypeface(Fonts.mavenLight(activity));
//			textHead.setVisibility(View.VISIBLE);
//
//			textMessage.setMovementMethod(new ScrollingMovementMethod());
//			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
//
//			textHead.setText(title);
//			textMessage.setText(message);
//
//			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
//			btnOk.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
//
//			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
//			btnCancel.setTypeface(Fonts.mavenRegular(activity));
//			btnCancel.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					dialog.dismiss();
//					if (isForced == 1) {
//						ActivityCompat.finishAffinity(activity);
//					}
//				}
//			});
//
//
//			btnOk.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					dialog.dismiss();
//					Intent intent = new Intent(Intent.ACTION_VIEW);
//					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sabkuchfresh"));
//					activity.startActivity(intent);
//					ActivityCompat.finishAffinity(activity);
//				}
//
//			});
//
//
//			dialog.show();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static boolean checkIfTrivialAPIErrors(Activity activity, JSONObject jObj) {
//		try {
//			int flag = jObj.getInt("flag");
//			if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
//				DialogPopup.dismissLoadingDialog();
//				HomeUtil.logoutUser(activity);
//				return true;
//			} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
//				DialogPopup.dismissLoadingDialog();
//				String errorMessage = jObj.getString("error");
//				DialogPopup.alertPopup(activity, "", errorMessage);
//				return true;
//			} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
//				DialogPopup.dismissLoadingDialog();
//				String message = jObj.getString("message");
//				DialogPopup.alertPopup(activity, "", message);
//				return true;
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			Log.e("e", "=" + e);
//		}
//		return false;
//	}
//
//
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if (State.SPLASH_LS == state || State.SPLASH_INIT == state || State.SPLASH_NO_NET == state) {
//					if (SplashNewActivity.this.hasWindowFocus() && loginDataFetched) {
//						loginDataFetched = false;
//						Intent intent = new Intent(SplashNewActivity.this, FreshActivity.class);
//						intent.setData(getIntent().getData());
//						startActivity(intent);
//						ActivityCompat.finishAffinity(SplashNewActivity.this);
//						overridePendingTransition(R.anim.right_in, R.anim.right_out);
//					}
//				}
//				else if(State.LOGIN == state || State.SIGNUP == state){
//					if(SplashNewActivity.this.hasWindowFocus() && loginDataFetched){
//						Map<String, String> articleParams = new HashMap<String, String>();
//						articleParams.put("username", Data.userData.userName);
//
//						loginDataFetched = false;
//						Intent intent = new Intent(SplashNewActivity.this, FreshActivity.class);
//						intent.setData(Data.splashIntentUri);
//						startActivity(intent);
//						ActivityCompat.finishAffinity(SplashNewActivity.this);
//						overridePendingTransition(R.anim.right_in, R.anim.right_out);
//					}
//					else if(SplashNewActivity.this.hasWindowFocus() && emailRegister){
//						emailRegister = false;
//						sendIntentToRegisterScreen(RegisterationType.EMAIL);
//					}
//					else if(SplashNewActivity.this.hasWindowFocus() && facebookRegister){
//						facebookRegister = false;
//						sendIntentToRegisterScreen(RegisterationType.FACEBOOK);
//					} else if (SplashNewActivity.this.hasWindowFocus() && googleRegister) {
//						googleRegister = false;
//						sendIntentToRegisterScreen(RegisterationType.GOOGLE);
//					} else if (SplashNewActivity.this.hasWindowFocus() && sendToOtpScreen) {
//						sendIntentToOtpScreen();
//					}
//				}
//			}
//		}, 500);
//
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		if(!newActivityStarted){
//			Data.linkFoundOnce = false;
//		}
//		super.onDestroy();
//		ASSL.closeActivity(root);
//		System.gc();
//	}
//
//	@Override
//	public void onBackPressed() {
//		if (State.LOGIN == state) {
//			performLoginBackPressed();
//		} else if (State.SIGNUP == state) {
//			performSignupBackPressed();
//		} else {
//			super.onBackPressed();
//		}
//	}
//
//	public void confirmDebugPasswordPopup(final Activity activity) {
//
//		try {
//			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
//			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
//			dialog.setContentView(R.layout.dialog_edittext_confirm);
//
//			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
//			new ASSL(activity, frameLayout, 1134, 720, true);
//
//			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//			layoutParams.dimAmount = 0.6f;
//			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//			dialog.setCancelable(true);
//			dialog.setCanceledOnTouchOutside(true);
//
//
//			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
//			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
//			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
//			textMessage.setTypeface(Fonts.mavenLight(activity));
//			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode);
//			etCode.setTypeface(Fonts.latoRegular(activity));
//
//			textHead.setText("Confirm Debug Password");
//			textMessage.setText("Please enter password to continue.");
//
//			textHead.setVisibility(View.GONE);
//			textMessage.setVisibility(View.GONE);
//
//
//			final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
//			btnConfirm.setTypeface(Fonts.mavenRegular(activity));
//
//			btnConfirm.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					String code = etCode.getText().toString().trim();
//					if ("".equalsIgnoreCase(code)) {
//						etCode.requestFocus();
//						etCode.setError("Code can't be empty.");
//					} else {
//						if (Config.getDebugPassword().equalsIgnoreCase(code)) {
//							dialog.dismiss();
//							activity.startActivity(new Intent(activity, DebugOptionsActivity.class));
//							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
//							ActivityCompat.finishAffinity(activity);
//						} else {
//							etCode.requestFocus();
//							etCode.setError("Code not matched.");
//						}
//					}
//				}
//
//			});
//
//
//			etCode.setOnEditorActionListener(new OnEditorActionListener() {
//
//				@Override
//				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//					int result = actionId & EditorInfo.IME_MASK_ACTION;
//					switch (result) {
//						case EditorInfo.IME_ACTION_DONE:
//							btnConfirm.performClick();
//							break;
//
//						case EditorInfo.IME_ACTION_NEXT:
//							break;
//
//						default:
//					}
//					return true;
//				}
//			});
//
//			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//				}
//			});
//
//			frameLayout.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			});
//
//
//			dialog.show();
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					Utils.showSoftKeyboard(SplashNewActivity.this, etCode);
//				}
//			}, 100);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	@Override
//	public void onLocationChanged(Location location, int priority) {
//		Data.loginLatitude = location.getLatitude();
//		Data.loginLongitude = location.getLongitude();
//	}
//
//
//	private void showLocationEnableDialog() {
//		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//		if (resp != ConnectionResult.SUCCESS) {
//			Log.e("Google Play Service Error ", "=" + resp);
//			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
//		} else {
//			LocationInit.showLocationAlertDialog(this);
//		}
//	}
//
//	private void initiateDeviceInfoVariables() {
//		try {                                                                                        // to get AppVersion, OS version, country code and device name
//			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//			Data.appVersion = pInfo.versionCode;
//			Log.i("appVersion", Data.appVersion + "..");
//			Data.osVersion = android.os.Build.VERSION.RELEASE;
//			Log.i("osVersion", Data.osVersion + "..");
//			Data.country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
//			Log.i("countryCode", Data.country + "..");
//			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
//			Log.i("deviceName", Data.deviceName + "..");
//
//			if (Config.getConfigMode() == ConfigMode.LIVE) {
//				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
//			} else {
//				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
//			}
//
//			Log.e("Data.uniqueDeviceId = ", "=" + Data.uniqueDeviceId);
//
//			Utils.generateKeyHash(this);
//
//		} catch (Exception e) {
//			Log.e("error in fetching appVersion and gcm key", ".." + e.toString());
//		}
//	}
//
//
//	public enum State {
//		SPLASH_INIT(0), SPLASH_LS(1), SPLASH_NO_NET(2), LOGIN(3), SIGNUP(4);
//
//		private int ordinal;
//
//		State(int ordinal) {
//			this.ordinal = ordinal;
//		}
//
//		public int getOrdinal() {
//			return ordinal;
//		}
//	}
//
//
//	public class CustomTextWatcher implements TextWatcher {
//		private TextView textViewRequired;
//
//		public CustomTextWatcher(TextView textViewRequired) {
//			this.textViewRequired = textViewRequired;
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//		}
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			textViewRequired.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
//		}
//	}
//
//
//	private void setLoginScreenValuesOnCreate() {
//		// set email screen values according to intent
//		editTextEmail.setText("");
//		editTextPassword.setText("");
//		try {
//			if (getIntent().hasExtra(KEY_BACK_FROM_OTP) && RegisterationType.EMAIL == SplashNewActivity.registerationType) {
//				if (phoneNoLogin) {
//					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
//				} else {
//					editTextEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			if (getIntent().hasExtra(KEY_PREVIOUS_LOGIN_EMAIL)) {
//				String previousLoginEmail = getIntent().getStringExtra(KEY_PREVIOUS_LOGIN_EMAIL);
//				editTextEmail.setText(previousLoginEmail);
//				fromPreviousAccounts = true;
//			} else {
//				fromPreviousAccounts = false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			fromPreviousAccounts = false;
//		}
//
//		try {
//			if (getIntent().hasExtra(KEY_FORGOT_LOGIN_EMAIL)) {
//				String forgotLoginEmail = getIntent().getStringExtra(KEY_FORGOT_LOGIN_EMAIL);
//				editTextEmail.setText(forgotLoginEmail);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			if (getIntent().hasExtra(KEY_ALREADY_VERIFIED_EMAIL)) {
//				String alreadyVerifiedEmail = getIntent().getStringExtra(KEY_ALREADY_VERIFIED_EMAIL);
//				editTextEmail.setText(alreadyVerifiedEmail);
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		try {
//			if (getIntent().hasExtra(KEY_ALREADY_REGISTERED_EMAIL)) {
//				String alreadyRegisterEmail = getIntent().getStringExtra(KEY_ALREADY_REGISTERED_EMAIL);
//				editTextEmail.setText(alreadyRegisterEmail);
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//
//
//		editTextEmail.setSelection(editTextEmail.getText().length());
//	}
//
//
//	public void performLoginBackPressed() {
//		if (fromPreviousAccounts) {
//			Intent intent = new Intent(SplashNewActivity.this, MultipleAccountsActivity.class);
//			startActivity(intent);
//			finish();
//			overridePendingTransition(R.anim.left_in, R.anim.left_out);
//		} else {
//			FacebookLoginHelper.logoutFacebook();
//			changeUIState(State.SPLASH_LS);
//		}
//	}
//
//
//	/**
//	 * ASync for login from server
//	 */
//	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			if (isPhoneNumber) {
//				params.put("phone_no", emailId);
//			} else {
//				params.put("email", emailId);
//			}
//			params.put("password", password);
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("client_id", Config.getClientId());
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.i("params", "=" + params);
//
//			RestClient.getFreshApiService().loginUsingEmailOrPhoneNo(params, new Callback<LoginResponse>() {
//				@Override
//				public void success(LoginResponse loginResponse, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "loginUsingEmailOrPhoneNo response = " + responseStr);
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//
//						int flag = jObj.getInt("flag");
//
//						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
//								String error = jObj.getString("error");
//								emailNeedRegister = emailId;
//								emailRegister = true;
//								notRegisteredMsg = error;
//							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
//								String message = JSONParser.getServerMessage(jObj);
//								//String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", message);
//							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
//								if (isPhoneNumber) {
//									enteredEmail = jObj.getString("user_email");
//								} else {
//									enteredEmail = emailId;
//								}
//								linkedWallet = jObj.optInt("reg_wallet_type");
//								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
//								accessToken = jObj.getString("access_token");
//								Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
//								Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
//								otpErrorMsg = jObj.getString("error");
//								SplashNewActivity.registerationType = RegisterationType.EMAIL;
//								sendToOtpScreen = true;
//							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
//								if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
//									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
//                                            loginResponse, LoginVia.EMAIL);
//									Database.getInstance(SplashNewActivity.this).insertEmail(emailId);
//									loginDataFetched = true;
//								}
//							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//							}
//							DialogPopup.dismissLoadingDialog();
//						} else {
//							DialogPopup.dismissLoadingDialog();
//						}
//
//					} catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//						DialogPopup.dismissLoadingDialog();
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		}
//		else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//
//	}
//
//	public void sendFacebookLoginValues(final Activity activity) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//
//			params.put("user_fb_id", Data.facebookUserData.fbId);
//			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
//			params.put("fb_access_token", Data.facebookUserData.accessToken);
//			params.put("fb_mail", Data.facebookUserData.userEmail);
//			params.put("username", Data.facebookUserData.userName);
//
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("client_id", Config.getClientId());
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.i("params", "" + params);
//
//			RestClient.getFreshApiService().loginUsingFacebook(params, new Callback<LoginResponse>() {
//				@Override
//				public void success(LoginResponse loginResponse, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "loginUsingFacebook response = " + responseStr);
//
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//
//						int flag = jObj.getInt("flag");
//
//						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
//								String error = jObj.getString("error");
//								facebookRegister = true;
//								notRegisteredMsg = error;
//							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
//								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
//							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
//								linkedWallet = jObj.optInt("reg_wallet_type");
//								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
//								accessToken = jObj.getString("access_token");
//								SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
//								SplashNewActivity.this.accessToken = jObj.getString("access_token");
//								Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
//								Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
//								otpErrorMsg = jObj.getString("error");
//								SplashNewActivity.registerationType = RegisterationType.FACEBOOK;
//								sendToOtpScreen = true;
//							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
//								if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
//									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
//                                            loginResponse, LoginVia.FACEBOOK);
//									loginDataFetched = true;
//									Database.getInstance(SplashNewActivity.this).insertEmail(Data.facebookUserData.userEmail);
//								}
//							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//							}
//							DialogPopup.dismissLoadingDialog();
//						} else {
//							DialogPopup.dismissLoadingDialog();
//						}
//
//					} catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//						DialogPopup.dismissLoadingDialog();
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "loginUsingFacebook error=" + error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		}
//		else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//
//	}
//
//	public void sendGoogleLoginValues(final Activity activity) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			params.put("google_access_token", Data.googleSignInAccount.getIdToken());
//
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("client_id", Config.getClientId());
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.i("params", "" + params);
//
//
//			RestClient.getFreshApiService().loginUsingGoogle(params, new Callback<LoginResponse>() {
//				@Override
//				public void success(LoginResponse loginResponse, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "loginUsingGoogle response = " + responseStr);
//
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//
//						int flag = jObj.getInt("flag");
//
//						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
//							if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
//								String error = jObj.getString("error");
//								googleRegister = true;
//								notRegisteredMsg = error;
//							}
//							else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
//								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
//							}
//							else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
//								linkedWallet = jObj.optInt("reg_wallet_type");
//								phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
//								accessToken = jObj.getString("access_token");
//								SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
//								SplashNewActivity.this.accessToken = jObj.getString("access_token");
//								Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
//								Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
//								otpErrorMsg = jObj.getString("error");
//								SplashNewActivity.registerationType = RegisterationType.GOOGLE;
//								sendToOtpScreen = true;
//							}
//							else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
//								if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
//									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
//											loginResponse, LoginVia.GOOGLE);
//									loginDataFetched = true;
//                                    FlurryEventLogger.userLogin(Data.userData.getUserId(), "User Sign In", "Sign In through email");
//									Database.getInstance(SplashNewActivity.this).insertEmail(Data.googleSignInAccount.getEmail());
//								}
//							}
//							else{
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//							}
//							DialogPopup.dismissLoadingDialog();
//						}
//						else{
//							DialogPopup.dismissLoadingDialog();
//						}
//
//					}  catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//						DialogPopup.dismissLoadingDialog();
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "loginUsingGoogle error="+error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		}
//		else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//
//	}
//
//
//	/**
//	 * Send intent to otp screen by making required data objects
//	 * flag 0 for email, 1 for Facebook
//	 */
//	public void sendIntentToOtpScreen() {
//		if (State.LOGIN == state) {
//			DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", otpErrorMsg, new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					DialogPopup.dismissAlertPopup();
//					OTPConfirmScreen.intentFromRegister = false;
//					if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
//						OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
//								Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
//								Data.facebookUserData.accessToken,
//								Data.facebookUserData.userEmail,
//								Data.facebookUserData.userName,
//								phoneNoOfUnverifiedAccount, "", "", accessToken);
//					} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
//						OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
//								Data.googleSignInAccount.getDisplayName(),
//								Data.googleSignInAccount.getEmail(),
//								"",
//								phoneNoOfUnverifiedAccount, "", "", accessToken);
//					} else {
//						OTPConfirmScreen.intentFromRegister = false;
//						OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "", accessToken);
//					}
//
//					Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
//					intent.putExtra("show_timer", 0);
//					intent.putExtra(LINKED_WALLET, linkedWallet);
//					startActivity(intent);
//					finish();
//					overridePendingTransition(R.anim.right_in, R.anim.right_out);
//				}
//			});
//		} else if (State.SIGNUP == state) {
//			OTPConfirmScreen.intentFromRegister = true;
//			generateOTPRegisterData();
//			Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
//			intent.putExtra("show_timer", 1);
//			intent.putExtra(LINKED_WALLET_MESSAGE, linkedWalletErrorMsg);
//			intent.putExtra(LINKED_WALLET, linkedWallet);
//			startActivity(intent);
//			finish();
//			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//		}
//	}
//
//
//	public void sendIntentToRegisterScreen(final RegisterationType registerationType){
//		if(State.LOGIN == state){
//			DialogPopup.alertPopupWithListener(this, "", notRegisteredMsg, new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					linkedWallet = 1;
//					SplashNewActivity.registerationType = registerationType;
//					changeUIState(State.SIGNUP);
//				}
//			});
//		} else{
//			SplashNewActivity.registerationType = registerationType;
//			changeUIState(State.SIGNUP);
//		}
//	}
//
//
//	public void performSignupBackPressed() {
//		FacebookLoginHelper.logoutFacebook();
//		changeUIState(State.SPLASH_LS);
//	}
//
//	public enum RegisterationType {
//		EMAIL(0), FACEBOOK(1), GOOGLE(2);
//
//		private int ordinal;
//
//		RegisterationType(int ordinal) {
//			this.ordinal = ordinal;
//		}
//
//		public int getOrdinal() {
//			return ordinal;
//		}
//	}
//
//
//	private void setSignupScreenValuesOnCreate() {
//
//		editTextSName.setText(""); editTextSName.setEnabled(true);
//		editTextSEmail.setText(""); editTextSEmail.setEnabled(true);
//		editTextSPromo.setText("");
//		editTextSPhone.setText("");
//		editTextSPassword.setText("");
//
//		fillSocialAccountInfo(SplashNewActivity.registerationType);
//
//
//		try {
//			if (getIntent().hasExtra(KEY_BACK_FROM_OTP)) {
//				if (RegisterationType.FACEBOOK == registerationType) {
//					editTextSPromo.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
//					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.facebookRegisterData.phoneNo));
//					editTextSPassword.setText(OTPConfirmScreen.facebookRegisterData.password);
//				} else if (RegisterationType.GOOGLE == registerationType) {
//					editTextSPromo.setText(OTPConfirmScreen.googleRegisterData.referralCode);
//					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.googleRegisterData.phoneNo));
//					editTextSPassword.setText(OTPConfirmScreen.googleRegisterData.password);
//				} else {
//					editTextSName.setText(OTPConfirmScreen.emailRegisterData.name);
//					editTextSEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
//					editTextSPromo.setText(OTPConfirmScreen.emailRegisterData.referralCode);
//					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.emailRegisterData.phoneNo));
//					editTextSPassword.setText(OTPConfirmScreen.emailRegisterData.password);
//				}
//
//				editTextSName.setSelection(editTextSName.getText().length());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try{
//			if(getIntent().hasExtra(KEY_REFERRAL_CODE)){
//				String referralCode = getIntent().getStringExtra(KEY_REFERRAL_CODE);
//				editTextSPromo.setText(referralCode);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (Data.previousAccountInfoList == null) {
//			Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
//		}
//
//		if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
//			imageViewAddPaytm.setImageResource(R.drawable.checkbox_signup_checked);
//		} else {
//			imageViewAddPaytm.setImageResource(R.drawable.checkbox_signup_unchecked);
//		}
//
//
//		new ReadSMSAsync().execute();
//
//	}
//
//
//	private void fillSocialAccountInfo(RegisterationType registerationType) {
//		try {
//			SplashNewActivity.registerationType = registerationType;
//			if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
//				editTextSName.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
//				editTextSEmail.setText(Data.facebookUserData.userEmail);
//
//				if (Data.facebookUserData.firstName != null && !Data.facebookUserData.firstName.equalsIgnoreCase("")) {
//					editTextSName.setEnabled(false);
//				} else {
//					editTextSName.setEnabled(true);
//				}
//				if (Data.facebookUserData.userEmail != null && !Data.facebookUserData.userEmail.equalsIgnoreCase("")) {
//					editTextSEmail.setEnabled(false);
//				} else {
//
//					editTextSEmail.setEnabled(true);
//				}
//			} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
//				editTextSName.setText(Data.googleSignInAccount.getDisplayName());
//				editTextSEmail.setText(Data.googleSignInAccount.getEmail());
//
//				if (Data.googleSignInAccount.getDisplayName() != null && !Data.googleSignInAccount.getDisplayName().equalsIgnoreCase("")) {
//					editTextSName.setEnabled(false);
//				} else {
//					editTextSName.setEnabled(true);
//				}
//				if (Data.googleSignInAccount.getEmail() != null && !Data.googleSignInAccount.getEmail().equalsIgnoreCase("")) {
//					editTextSEmail.setEnabled(false);
//				} else {
//					editTextSEmail.setEnabled(true);
//				}
//			}
//			else if(RegisterationType.EMAIL == SplashNewActivity.registerationType){
//				if(Utils.checkIfOnlyDigits(emailNeedRegister)){
//					editTextSPhone.setText(Utils.retrievePhoneNumberTenChars(emailNeedRegister));
//				} else{
//					editTextSEmail.setText(emailNeedRegister);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	private class ReadSMSAsync extends AsyncTask<String, Integer, String> {
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			String referralCode = getSmsFindReferralCode(1 * 60 * 60 * 1000);
//			return referralCode;
//		}
//
//		@Override
//		protected void onPostExecute(String s) {
//			super.onPostExecute(s);
//			if (editTextSPromo.getText().toString().equalsIgnoreCase("")) {
//				editTextSPromo.setText(s);
//			}
//		}
//	}
//
//	private String getSmsFindReferralCode(long diff) {
//		String referralCode = "";
//		try {
//			Uri uri = Uri.parse("content://sms/inbox");
//			long now = System.currentTimeMillis();
//			long last1 = now - diff;    //in millis
//			String[] selectionArgs = new String[]{Long.toString(last1)};
//			String selection = "date" + ">?";
//			Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
//
//			if (cursor != null && cursor.moveToFirst()) {
//				for (int i = 0; i < cursor.getCount(); i++) {
//					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
////                    String number = cursor.getString(cursor.getColumnIndexOrThrow("address"));
////                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
////                    Date smsDayTime = new Date(Long.valueOf(date));
////                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
////                    String typeOfSMS = null;
////                    switch (Integer.parseInt(type)) {
////                        case 1:
////                            typeOfSMS = "INBOX";
////                            break;
////
////                        case 2:
////                            typeOfSMS = "SENT";
////                            break;
////
////                        case 3:
////                            typeOfSMS = "DRAFT";
////                            break;
////                    }
//					Log.e("body", "=" + body);
//					try {
//						if (body.contains("Jugnoo")) {
//							String[] codeArr = body.split("code ");
//							String[] spaceArr = codeArr[1].split(" ");
//							String rCode = spaceArr[0];
//							if (!"".equalsIgnoreCase(rCode)) {
//								referralCode = rCode;
//								break;
//							}
//						}
//					} catch (Exception e) {
//					}
////                    stringBuffer.append("\nPhone Number:--- " + number + " \nMessage Type:--- "
////                            + typeOfSMS + " \nMessage Date:--- " + smsDayTime
////                            + " \nMessage Body:--- " + body);
////                    stringBuffer.append("\n----------------------------------");
//					cursor.moveToNext();
//				}
////                DialogPopup.alertPopup(this, "", stringBuffer.toString());
//			}
//			if (cursor != null) {
//				cursor.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return referralCode;
//	}
//
//
//	/**
//	 * ASync for register from server
//	 */
//	public void sendSignupValues(final Activity activity, final String name, final String referralCode, final String emailId, final String phoneNo,
//								 final String password, final int linkedWallet) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			params.put("user_name", name);
//			params.put("phone_no", phoneNo);
//			params.put("email", emailId);
//			params.put("password", password);
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//
//			params.put("client_id", Config.getClientId());
//			params.put(KEY_REFERRAL_CODE, referralCode);
//
//			params.put("device_token", Data.getDeviceToken());
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("reg_wallet_type", String.valueOf(linkedWallet));
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.i("register_using_email params", params.toString());
//
//
//
//			RestClient.getFreshApiService().registerUsingEmail(params, new Callback<SettleUserDebt>() {
//				@Override
//				public void success(SettleUserDebt settleUserDebt, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "registerUsingEmail response = " + responseStr);
//
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//						SplashNewActivity.registerationType = RegisterationType.EMAIL;
//						if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
//							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//								int flag = jObj.getInt("flag");
//								if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									DialogPopup.alertPopup(activity, "", error);
//								} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									setIntent(new Intent().putExtra(KEY_ALREADY_REGISTERED_EMAIL, emailId));
//									DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
//								} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
//									SplashNewActivity.this.name = name;
//									SplashNewActivity.this.emailId = emailId;
//									parseOTPSignUpData(jObj, password, referralCode);
//									nudgeSignupEvent(phoneNo, emailId, name);
//
//								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
//									SplashNewActivity.this.name = name;
//									SplashNewActivity.this.emailId = emailId;
//									SplashNewActivity.this.phoneNo = phoneNo;
//									SplashNewActivity.this.password = password;
//									SplashNewActivity.this.referralCode = referralCode;
//									SplashNewActivity.this.accessToken = "";
//									parseDataSendToMultipleAccountsScreen(activity, jObj);
//								} else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag){
//									SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
//									SplashNewActivity.this.name = name;
//									SplashNewActivity.this.emailId = emailId;
//									parseOTPSignUpData(jObj, password, referralCode);
//								} else{
//									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//								}
//								DialogPopup.dismissLoadingDialog();
//							}
//						} else {
//							DialogPopup.dismissLoadingDialog();
//						}
//					} catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//						DialogPopup.dismissLoadingDialog();
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "registerUsingEmail error="+error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		} else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//
//	}
//
//
//	/**
//	 * ASync for login from server
//	 */
//	public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password
//			, final int linkedWallet) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			params.put("user_fb_id", Data.facebookUserData.fbId);
//			params.put("user_fb_name", Data.facebookUserData.firstName+ " " + Data.facebookUserData.lastName);
//			params.put("fb_access_token", Data.facebookUserData.accessToken);
//			params.put("fb_mail", Data.facebookUserData.userEmail);
//			params.put("username", Data.facebookUserData.userName);
//
//			params.put("phone_no", phoneNo);
//			params.put("password", password);
//			params.put(KEY_REFERRAL_CODE, referralCode);
//
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("client_id", Config.getClientId());
//			params.put("reg_wallet_type", String.valueOf(linkedWallet));
//
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.e("register_using_facebook params", params.toString());
//
//
//			RestClient.getFreshApiService().registerUsingFacebook(params, new Callback<SettleUserDebt>() {
//				@Override
//				public void success(SettleUserDebt settleUserDebt, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "registerUsingFacebook response = " + response);
//
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//						SplashNewActivity.registerationType = RegisterationType.FACEBOOK;
//						if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
//							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//								int flag = jObj.getInt("flag");
//								if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									DialogPopup.alertPopup(activity, "", error);
//								} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
//								} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
//									parseOTPSignUpData(jObj, password, referralCode);
//
//									nudgeSignupEvent(phoneNo, Data.facebookUserData.userEmail,
//											Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
//
//								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
//									SplashNewActivity.this.phoneNo = phoneNo;
//									SplashNewActivity.this.password = password;
//									SplashNewActivity.this.referralCode = referralCode;
//									SplashNewActivity.this.accessToken = "";
//									parseDataSendToMultipleAccountsScreen(activity, jObj);
//								} else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
//									SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
//									parseOTPSignUpData(jObj, password, referralCode);
//								} else {
//									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//								}
//								DialogPopup.dismissLoadingDialog();
//							}
//						} else {
//							DialogPopup.dismissLoadingDialog();
//						}
//					} catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.dismissLoadingDialog();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "registerUsingFacebook error=" + error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		} else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//	}
//
//
//	/**
//	 * ASync for login from server
//	 */
//	public void sendGoogleSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password, final int linkedWallet) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//			resetFlags();
//			DialogPopup.showLoadingDialog(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			params.put("google_access_token", Data.googleSignInAccount.getIdToken());
//
//			params.put("phone_no", phoneNo);
//			params.put("password", password);
//			params.put(KEY_REFERRAL_CODE, referralCode);
//
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("client_id", Config.getClientId());
//			params.put("reg_wallet_type", String.valueOf(linkedWallet));
//
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//			params.put(KEY_SOURCE, getAppSource());
//
//			Log.e("register_using_facebook params", params.toString());
//
//			RestClient.getFreshApiService().registerUsingGoogle(params, new Callback<SettleUserDebt>() {
//				@Override
//				public void success(SettleUserDebt settleUserDebt, Response response) {
//					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//					Log.i(TAG, "registerUsingGoogle response = " + responseStr);
//
//					try {
//						JSONObject jObj = new JSONObject(responseStr);
//						SplashNewActivity.registerationType = RegisterationType.GOOGLE;
//						if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
//							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//								int flag = jObj.getInt("flag");
//								if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									DialogPopup.alertPopup(activity, "", error);
//								} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
//									String error = jObj.getString("error");
//									DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
//								} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
//									parseOTPSignUpData(jObj, password, referralCode);
//									nudgeSignupEvent(phoneNo, Data.googleSignInAccount.getEmail(),
//											Data.googleSignInAccount.getDisplayName());
//
//								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
//									SplashNewActivity.this.phoneNo = phoneNo;
//									SplashNewActivity.this.password = password;
//									SplashNewActivity.this.referralCode = referralCode;
//									SplashNewActivity.this.accessToken = "";
//									parseDataSendToMultipleAccountsScreen(activity, jObj);
//								} else if (ApiResponseFlags.PAYTM_WALLET_NOT_ADDED.getOrdinal() == flag) {
//									SplashNewActivity.this.linkedWallet = LinkedWalletStatus.PAYTM_WALLET_ERROR.getOrdinal();
//									parseOTPSignUpData(jObj, password, referralCode);
//								} else {
//									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//								}
//								DialogPopup.dismissLoadingDialog();
//							}
//						} else {
//							DialogPopup.dismissLoadingDialog();
//						}
//					} catch (Exception exception) {
//						exception.printStackTrace();
//						DialogPopup.dismissLoadingDialog();
//						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//					}
//				}
//
//				@Override
//				public void failure(RetrofitError error) {
//					Log.e(TAG, "registerUsingGoogle error=" + error.toString());
//					DialogPopup.dismissLoadingDialog();
//					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				}
//			});
//
//		} else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//	}
//
//	private void nudgeSignupEvent(String phoneNo, String email, String userName){
//		try {
//			NudgeClient.initialize(SplashNewActivity.this, phoneNo, userName, email, phoneNo);
//			JSONObject map = new JSONObject();
//			map.put(KEY_PHONE_NO, phoneNo);
//			map.put(KEY_EMAIL, email);
//			map.put(KEY_USER_NAME, userName);
//			map.put(KEY_LATITUDE, Data.loginLatitude);
//			map.put(KEY_LONGITUDE, Data.loginLongitude);
//			NudgeClient.trackEventUserId(SplashNewActivity.this, NUDGE_SIGNUP, map);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	private void parseOTPSignUpData(JSONObject jObj, String password, String referralCode) throws Exception {
//		SplashNewActivity.this.phoneNo = jObj.getString("phone_no");
//		SplashNewActivity.this.password = password;
//		SplashNewActivity.this.referralCode = referralCode;
//		SplashNewActivity.this.accessToken = jObj.getString("access_token");
//		Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
//		Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
//		sendToOtpScreen = true;
//		linkedWalletErrorMsg = jObj.optString(KEY_MESSAGE, "");
//	}
//
//	private void generateOTPRegisterData() {
//		if (RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
//			OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
//					Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
//					Data.facebookUserData.accessToken,
//					Data.facebookUserData.userEmail,
//					Data.facebookUserData.userName,
//					phoneNo, password, referralCode, accessToken);
//		} else if (RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
//			OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
//					Data.googleSignInAccount.getDisplayName(),
//					Data.googleSignInAccount.getEmail(),
//					"",
//					phoneNo, password, referralCode, accessToken);
//		} else {
//			OTPConfirmScreen.intentFromRegister = true;
//			OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode, accessToken);
//		}
//	}
//
//
//	public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
//		generateOTPRegisterData();
//		SplashNewActivity.multipleCaseJSON = jObj;
//		if (Data.previousAccountInfoList == null) {
//			Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
//		}
//		Data.previousAccountInfoList.clear();
//		Data.previousAccountInfoList.addAll(JSONParser.parsePreviousAccounts(jObj));
//		startActivity(new Intent(activity, MultipleAccountsActivity.class));
//		finish();
//		overridePendingTransition(R.anim.right_in, R.anim.right_out);
//	}
//
//	private View.OnClickListener onClickListenerAlreadyRegistered = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			changeUIState(State.LOGIN);
//		}
//	};
//
//
//
//	public void verifyOtpViaEmail(final Activity activity, final String email, final String phoneNo, String otp) {
//		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//
//			final ProgressDialog progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, "Loading...");
//
//			HashMap<String, String> params = new HashMap<>();
//
//			if (Data.locationFetcher != null) {
//				Data.loginLatitude = Data.locationFetcher.getLatitude();
//				Data.loginLongitude = Data.locationFetcher.getLongitude();
//			}
//
//			params.put("email", email);
//			params.put("password", "");
//			params.put("device_token", Data.getDeviceToken());
//			params.put("device_type", Data.DEVICE_TYPE);
//			params.put("device_name", Data.deviceName);
//			params.put("app_version", "" + Data.appVersion);
//			params.put("os_version", Data.osVersion);
//			params.put("country", Data.country);
//			params.put("unique_device_id", Data.uniqueDeviceId);
//			params.put("latitude", "" + Data.loginLatitude);
//			params.put("longitude", "" + Data.loginLongitude);
//			params.put("client_id", Config.getClientId());
//			params.put("otp", otp);
//
//			if (Utils.isDeviceRooted()) {
//				params.put("device_rooted", "1");
//			} else {
//				params.put("device_rooted", "0");
//			}
//
//			Log.i("params", "" + params.toString());
//
//			RestClient.getFreshApiService().verifyOtp(params, new Callback<LoginResponse>() {
//                @Override
//                public void success(LoginResponse loginResponse, Response response) {
//
//                    try {
//                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
//                        Log.i(TAG, "verifyOtp jsonString = " + jsonString);
//                        JSONObject jObj = new JSONObject(jsonString);
//
//                        int flag = jObj.getInt("flag");
//                        String message = JSONParser.getServerMessage(jObj);
//                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//                            if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
//                                String error = jObj.getString("error");
//                                DialogPopup.alertPopup(activity, "", error);
//                            } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
//                                String error = jObj.getString("error");
//                                DialogPopup.alertPopup(activity, "", error);
//                            } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
//                                if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
//                                    new JSONParser().parseAccessTokenLoginData(activity, jsonString,
//                                            loginResponse, LoginVia.EMAIL_OTP);
//                                    Database.getInstance(activity).insertEmail(email);
//                                    Database.getInstance(activity).close();
//                                    loginDataFetched = true;
//                                    //BranchMetricsUtils.logEvent(activity, BRANCH_EVENT_REGISTRATION, false);
//                                    FbEvents.logEvent(activity, AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, false);
//                                }
//                            } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
//                                String error = jObj.getString("error");
//                                DialogPopup.alertPopup(activity, "", error);
//                            } else if (ApiResponseFlags.AUTH_ALREADY_VERIFIED.getOrdinal() == flag) {
//                                DialogPopup.alertPopupWithListener(activity, "", message,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                setIntent(new Intent().putExtra(KEY_ALREADY_VERIFIED_EMAIL, email));
//                                                changeUIState(State.LOGIN);
//                                            }
//                                        });
//                            } else {
//                                DialogPopup.alertPopup(activity, "", message);
//                            }
//                        }
//                    } catch (Exception exception) {
//                        exception.printStackTrace();
//                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//                    }
//                    if (progressDialog != null) progressDialog.dismiss();
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    Log.e(TAG, "verifyOtp error=" + error);
//                    if (progressDialog != null) progressDialog.dismiss();
//                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//                }
//            });
//		} else {
//			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
//		}
//	}
//
//
//
//
//
//	private class ReadSMSClickLinkAsync extends AsyncTask<String, Integer, String> {
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			String link = getSmsFindVerificationLink(4 * 60 * 60 * 1000);
//			return link;
//		}
//
//		@Override
//		protected void onPostExecute(String s) {
//			super.onPostExecute(s);
//			if(!s.equalsIgnoreCase("")){
//				if(!SplashNewActivity.this.isFinishing()){
//					Utils.openUrl(SplashNewActivity.this, s);
//					Data.linkFoundOnce = true;
//				}
//			}
//		}
//
//		private String getSmsFindVerificationLink(long diff) {
//			String link = "";
//			try {
//				Uri uri = Uri.parse("content://sms/inbox");
//				long now = System.currentTimeMillis();
//				long last1 = now - diff;    //in millis
//				String[] selectionArgs = new String[]{Long.toString(last1)};
//				String selection = "date" + ">?";
//				Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
//
//				if (cursor != null) {
//					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//						String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
//						Log.i(TAG, "sms body=>"+body);
//						try {
//							if(body.contains(DOMAIN_SHARE_JUGNOO_IN)){
//								String[] arr = body.split(" ");
//								for(String str : arr){
//									if(str.contains(DOMAIN_SHARE_JUGNOO_IN)){
//										if(str.charAt(str.length()-1) == '.'){
//											str = str.substring(0, str.length()-1);
//										}
//										link = str;
//										break;
//									}
//								}
//							}
//						} catch (Exception e) {}
//						if(!link.equalsIgnoreCase("")){
//							break;
//						}
//					}
//				}
//				if (cursor != null){
//					cursor.close();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return link;
//		}
//	}
//
//
//
//
//	private void firstTimeEvents(){
//		if(!Prefs.with(this).contains(SP_FIRST_OPEN_TIME)){
//			Prefs.with(this).save(SP_FIRST_OPEN_TIME, System.currentTimeMillis());
//		}
//
//		if(!Prefs.with(this).contains(SP_APP_DOWNLOAD_SOURCE_SENT)){
//			HashMap<String, String> map = new HashMap<>();
//			map.put(KEY_SOURCE, Config.getDownloadSource());
////			FlurryEventLogger.event(FlurryEventNames.APP_DOWNLOAD_SOURCE, map);
//			Prefs.with(this).save(SP_APP_DOWNLOAD_SOURCE_SENT, 1);
//		}
//	}
//
//
//	private String getAppSource(){
//		StringBuilder sb = new StringBuilder();
//		sb.append(Prefs.with(this).getString(SP_INSTALL_REFERRER_CONTENT, ""));
//		if(sb.length() > 0){
//			sb.append("&");
//		}
//		sb.append(KEY_DOWNLOAD_SOURCE).append("=").append(Config.getDownloadSource());
//		return sb.toString();
//	}
//
//
//}
