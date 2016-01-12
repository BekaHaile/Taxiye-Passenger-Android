package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
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
import product.clicklabs.jugnoo.utils.GoogleSigninActivity;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.LocationInit;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.UniqueIMEIID;
import product.clicklabs.jugnoo.utils.Utils;


public class SplashNewActivity extends BaseActivity implements LocationUpdate, FlurryEventNames, Constants {

	//adding drop location

	RelativeLayout root;

	ImageView viewInitJugnoo, viewSplashLS, viewInitSplashJugnoo;
	RelativeLayout relativeLayoutJugnooLogo;
	ImageView imageViewBack, imageViewJugnooLogo, imageViewDebug1, imageViewDebug2;

	RelativeLayout relativeLayoutLS;
	LinearLayout linearLayoutLoginSignupButtons;
	Button buttonLogin, buttonRegister;
	TextView textViewTerms, textViewPrivacy;
	LinearLayout linearLayoutNoNet;
	TextView textViewNoNet;
	Button buttonNoNetCall, buttonRefresh;

	LinearLayout linearLayoutLogin;
	AutoCompleteTextView editTextEmail;
	EditText editTextPassword;
	TextView textViewEmailRequired, textViewPasswordRequired, textViewForgotPassword;
	Button buttonEmailLogin, buttonFacebookLogin, buttonGoogleLogin;

	LinearLayout linearLayoutSignup;
	EditText editTextSName, editTextSEmail, editTextSPhone, editTextSPassword, editTextSPromo;
	TextView textViewSNameRequired, textViewSEmailRequired, textViewSPhoneRequired, textViewSPasswordRequired,
			textViewSPromoRequired;
	Button buttonEmailSignup, buttonFacebookSignup, buttonGoogleSignup;
	TextView textViewSTerms, textViewSPrivacy;

	boolean loginDataFetched = false, resumed = false;

	boolean touchedDown1 = false, touchedDown2 = false;
	int debugState = 0;

	private State state = State.SPLASH_LS;



	CallbackManager callbackManager;
	FacebookLoginHelper facebookLoginHelper;
	boolean facebookRegister = false, googleRegister = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "", accessToken = "";
	private String enteredEmail = "";
	public static boolean phoneNoLogin = false;
	private static final int GOOGLE_SIGNIN_REQ_CODE_LOGIN = 1124;
	public void resetFlags(){
		loginDataFetched = false;
		facebookRegister = false;
		googleRegister = false;
		sendToOtpScreen = false;
		RegisterScreen.registerationType = RegisterScreen.RegisterationType.EMAIL;
		phoneNoOfUnverifiedAccount = "";
		otpErrorMsg = "";
		notRegisteredMsg = "";
	}


	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}


	@Override
	public void onStart() {
		super.onStart();

		try {
			Branch branch = Branch.getInstance(this);
			branch.initSession(new Branch.BranchReferralInitListener() {
				@Override
				public void onInitFinished(JSONObject referringParams, BranchError error) {
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
									Data.deepLinkIndex = referringParams.optInt("deepindex", -1);
									Data.deepLinkReferralCode = referringParams.optString("referral_code", "");
									Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(SplashNewActivity.this);
									if ("".equalsIgnoreCase(pair.first)
											&& !"".equalsIgnoreCase(Data.deviceToken)) {
										sendToRegisterThroughSms(Data.deepLinkReferralCode);
									}
								}
							}

							Log.e("Deeplink =", "=" + Data.deepLinkIndex);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Data.branchReferringParams = referringParams;
						Data.branchReferringLink = referringParams.optString("link", "");
						// deep link data: {"deepindex":"0","$identity_id":"176950378011563091","$one_time_use":false,"referring_user_identifier":"f2","source":"android",
						// "~channel":"Facebook","~creation_source":"SDK","~feature":"share","~id":"178470536899245547","+match_guaranteed":true,"+click_timestamp":1443850505,
						// "+is_first_session":false,"+clicked_branch_link":true}
					}
				}
			}, this.getIntent().getData(), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
		FlurryAgent.onEvent("Splash started");
	}

	@Override
	public void onNewIntent(Intent intent) {
		this.setIntent(intent);
	}



	public static void initializeServerURL(Context context) {
		String link = Prefs.with(context).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());

		if (link.equalsIgnoreCase(Config.getLiveServerUrl())) {
			Config.setConfigMode(ConfigMode.LIVE);
		} else if (link.equalsIgnoreCase(Config.getDevServerUrl())) {
			Config.setConfigMode(ConfigMode.DEV);
		} else if (link.equalsIgnoreCase(Config.getDev1ServerUrl())) {
			Config.setConfigMode(ConfigMode.DEV_1);
		} else if (link.equalsIgnoreCase(Config.getDev2ServerUrl())) {
			Config.setConfigMode(ConfigMode.DEV_2);
		} else if (link.equalsIgnoreCase(Config.getDev3ServerUrl())) {
			Config.setConfigMode(ConfigMode.DEV_3);
		} else {
			Config.CUSTOM_SERVER_URL = link;
			Config.setConfigMode(ConfigMode.CUSTOM);
		}
		RestClient.setupRestClient();
		Log.e("link", "=" + link);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		Data.getDeepLinkIndexFromIntent(getIntent());


		try {
			NewRelic.withApplicationToken(
					Config.getNewRelicKey()
			).start(this.getApplication());
		} catch (Exception e) {
			e.printStackTrace();
		}


		try{
			Data.TRANSFER_FROM_JEANIE = 0;
			if(getIntent().hasExtra("transfer_from_jeanie")){
				Data.TRANSFER_FROM_JEANIE = getIntent().getIntExtra("transfer_from_jeanie", 0);
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		FacebookSdk.sdkInitialize(this);

		Utils.disableSMSReceiver(this);

		Data.locationSettingsNoPressed = false;

		Data.userData = null;

		initializeServerURL(this);

		FlurryAgent.init(this, Config.getFlurryKey());


		Locale locale = new Locale("en");
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


		setContentView(R.layout.activity_splash_new);

		if (Data.locationFetcher == null) {
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}


		resumed = false;

		touchedDown1 = false;
		touchedDown2 = false;
		debugState = 0;

		resetFlags();
		enteredEmail = "";

		root = (RelativeLayout) findViewById(R.id.root);
		new ASSL(SplashNewActivity.this, root, 1134, 720, false);


		viewInitJugnoo = (ImageView) findViewById(R.id.viewInitJugnoo);
		viewInitSplashJugnoo = (ImageView) findViewById(R.id.viewInitSplashJugnoo);
		viewSplashLS = (ImageView) findViewById(R.id.viewSplashLS);

		relativeLayoutJugnooLogo = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooLogo);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
		imageViewDebug1 = (ImageView) findViewById(R.id.imageViewDebug1);
		imageViewDebug2 = (ImageView) findViewById(R.id.imageViewDebug2);

		relativeLayoutLS = (RelativeLayout) findViewById(R.id.relativeLayoutLS);
		linearLayoutLoginSignupButtons = (LinearLayout) findViewById(R.id.linearLayoutLoginSignupButtons);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setTypeface(Fonts.mavenLight(this));
		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setTypeface(Fonts.mavenLight(this));
		textViewTerms = (TextView) findViewById(R.id.textViewTerms);
		textViewTerms.setTypeface(Fonts.latoRegular(this));
		textViewPrivacy = (TextView) findViewById(R.id.textViewPrivacy);
		textViewPrivacy.setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewAlreadyHaveAccount)).setTypeface(Fonts.latoRegular(this));

		linearLayoutNoNet = (LinearLayout) findViewById(R.id.linearLayoutNoNet);
		textViewNoNet = (TextView) findViewById(R.id.textViewNoNet);
		textViewNoNet.setTypeface(Fonts.latoRegular(this));
		buttonNoNetCall = (Button) findViewById(R.id.buttonNoNetCall);
		buttonNoNetCall.setTypeface(Fonts.mavenLight(this));
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		buttonRefresh.setTypeface(Fonts.mavenLight(this));


		String[] emails = Database.getInstance(this).getEmails();
		ArrayAdapter<String> adapter;
		if (emails == null) { emails = new String[]{}; }
		adapter = new ArrayAdapter<>(this, R.layout.dropdown_textview, emails);
		adapter.setDropDownViewResource(R.layout.dropdown_textview);

		linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
		editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
		editTextEmail.setTypeface(Fonts.latoRegular(this)); editTextEmail.setAdapter(adapter);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setTypeface(Fonts.latoRegular(this), Typeface.ITALIC);
		textViewEmailRequired = (TextView) findViewById(R.id.textViewEmailRequired);
		textViewEmailRequired.setTypeface(Fonts.latoRegular(this));
		textViewPasswordRequired = (TextView) findViewById(R.id.textViewPasswordRequired);
		textViewPasswordRequired.setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewLoginOr)).setTypeface(Fonts.latoRegular(this));
		textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
		textViewForgotPassword.setTypeface(Fonts.mavenRegular(this));
		buttonEmailLogin = (Button) findViewById(R.id.buttonEmailLogin);
		buttonEmailLogin.setTypeface(Fonts.mavenLight(this));
		buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin);
		buttonFacebookLogin.setTypeface(Fonts.mavenLight(this));
		buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
		buttonGoogleLogin.setTypeface(Fonts.mavenLight(this));


		linearLayoutSignup = (LinearLayout) findViewById(R.id.linearLayoutSignup);
		editTextSName = (EditText) findViewById(R.id.editTextSName); editTextSName.setTypeface(Fonts.latoRegular(this));
		editTextSEmail = (EditText) findViewById(R.id.editTextSEmail); editTextSEmail.setTypeface(Fonts.latoRegular(this));
		editTextSPhone = (EditText) findViewById(R.id.editTextSPhone); editTextSPhone.setTypeface(Fonts.latoRegular(this));
		editTextSPassword = (EditText) findViewById(R.id.editTextSPassword); editTextSPassword.setTypeface(Fonts.latoRegular(this));
		editTextSPromo = (EditText) findViewById(R.id.editTextSPromo); editTextSPromo.setTypeface(Fonts.latoRegular(this));
		textViewSNameRequired = (TextView) findViewById(R.id.textViewSNameRequired); textViewSNameRequired.setTypeface(Fonts.latoRegular(this));
		textViewSEmailRequired = (TextView) findViewById(R.id.textViewSEmailRequired); textViewSEmailRequired.setTypeface(Fonts.latoRegular(this));
		textViewSPhoneRequired = (TextView) findViewById(R.id.textViewSPhoneRequired); textViewSPhoneRequired.setTypeface(Fonts.latoRegular(this));
		textViewSPasswordRequired = (TextView) findViewById(R.id.textViewSPasswordRequired); textViewSPasswordRequired.setTypeface(Fonts.latoRegular(this));
		textViewSPromoRequired = (TextView) findViewById(R.id.textViewSPromoRequired); textViewSPromoRequired.setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewSignupOr)).setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewSPhone91)).setTypeface(Fonts.latoRegular(this));
		buttonEmailSignup = (Button) findViewById(R.id.buttonEmailSignup); buttonEmailSignup.setTypeface(Fonts.mavenLight(this));
		buttonFacebookSignup = (Button) findViewById(R.id.buttonFacebookSignup); buttonFacebookSignup.setTypeface(Fonts.mavenLight(this));
		buttonGoogleSignup = (Button) findViewById(R.id.buttonGoogleSignup); buttonGoogleSignup.setTypeface(Fonts.mavenLight(this));
		textViewSTerms = (TextView) findViewById(R.id.textViewSTerms); textViewSTerms.setTypeface(Fonts.latoRegular(this));
		textViewSPrivacy = (TextView) findViewById(R.id.textViewSPrivacy); textViewSPrivacy.setTypeface(Fonts.latoRegular(this));


		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(LOGIN_OPTION_MAIN);
				changeUIState(State.LOGIN);
			}
		});

		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(SIGNUP);
				RegisterScreen.registerationType = RegisterScreen.RegisterationType.EMAIL;
//				Intent intent = new Intent(SplashNewActivity.this, RegisterScreen.class);
//				startActivity(intent);
//				finish();
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);

				changeUIState(State.SIGNUP);
			}
		});
		textViewTerms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		textViewPrivacy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

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
				if(!loginDataFetched){
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

				}
			}
		});

		imageViewDebug1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				touchedDown1 = false;
			}
		});

		imageViewDebug1.setOnTouchListener(new View.OnTouchListener() {
			Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (touchedDown1) {
						debugState = 1;
						root.setBackgroundColor(getResources().getColor(R.color.white_more_translucent));
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								root.setBackgroundResource(R.drawable.bg_img);
							}
						}, 200);
					}
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						debugState = 0;
						touchedDown1 = true;
						handler.removeCallbacks(runnable);
						handler.postDelayed(runnable, 4000);
						break;

					case MotionEvent.ACTION_UP:
						touchedDown1 = false;
						break;
				}

				return false;
			}
		});


		imageViewDebug2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				touchedDown2 = false;
			}
		});

		imageViewDebug2.setOnTouchListener(new View.OnTouchListener() {
			Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (touchedDown2 && debugState == 1) {
						debugState = 0;
						confirmDebugPasswordPopup(SplashNewActivity.this);
					}
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (debugState == 1) {
					switch (action) {
						case MotionEvent.ACTION_DOWN:
							touchedDown2 = true;
							handler.removeCallbacks(runnable);
							handler.postDelayed(runnable, 4000);
							break;

						case MotionEvent.ACTION_UP:
							touchedDown2 = false;
							debugState = 0;
							break;
					}
				}
				return false;
			}
		});









		editTextEmail.addTextChangedListener(new CustomTextWatcher(textViewEmailRequired));
		editTextPassword.addTextChangedListener(new CustomTextWatcher(textViewPasswordRequired));

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
								editTextEmail.setError(getResources().getString(R.string.nl_login_invalid_email_error));
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
				FlurryEventLogger.event(LOGIN_VIA_FACEBOOK);
				Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
				facebookLoginHelper.openFacebookSession();
			}
		});
		buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(LOGIN_VIA_GOOGLE);
				Utils.hideSoftKeyboard(SplashNewActivity.this, editTextEmail);
				startActivityForResult(new Intent(SplashNewActivity.this, GoogleSigninActivity.class),
						GOOGLE_SIGNIN_REQ_CODE_LOGIN);
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
			}
		});

		callbackManager = CallbackManager.Factory.create();

		facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
			@Override
			public void facebookLoginDone(FacebookUserData facebookUserData) {
				Data.facebookUserData = facebookUserData;
				sendFacebookLoginValues(SplashNewActivity.this);
				FlurryEventLogger.facebookLoginClicked(Data.facebookUserData.fbId);
			}

			@Override
			public void facebookLoginError(String message) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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




		try{
			if(getIntent().hasExtra(KEY_SPLASH_STATE)){
				int stateInt = getIntent().getIntExtra(KEY_SPLASH_STATE, State.SPLASH_INIT.getOrdinal());
				if(State.LOGIN.getOrdinal() == stateInt){
					state = State.LOGIN;
				}
				else if(State.SIGNUP.getOrdinal() == stateInt){
					state = State.SIGNUP;
				}
				else if(State.SPLASH_LS.getOrdinal() == stateInt){
					state = State.SPLASH_LS;
				}
				else{
					state = State.SPLASH_INIT;
				}
			}
			else{
				state = State.SPLASH_INIT;
			}
		} catch(Exception e){
			e.printStackTrace();
			state = State.SPLASH_INIT;
		}

		changeUIState(state);

		if(State.SPLASH_INIT == state) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getDeviceToken();
				}
			}, 500);
		}



		initiateDeviceInfoVariables();
		startService(new Intent(this, PushPendingCallsService.class));
		showLocationEnableDialog();


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		// set email screen values according to intent
		setLoginScreenValuesOnCreate();

	}

	private void changeUIState(State state){
		imageViewJugnooLogo.requestFocus();
		switch(state){
			case SPLASH_INIT:
				viewInitJugnoo.setVisibility(View.VISIBLE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewSplashLS.setVisibility(View.VISIBLE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				linearLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SPLASH_LS:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewSplashLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				linearLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SPLASH_NO_NET:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewSplashLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.GONE);
				linearLayoutNoNet.setVisibility(View.VISIBLE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				linearLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case LOGIN:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewSplashLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				linearLayoutSignup.setVisibility(View.VISIBLE);
				break;

			case SIGNUP:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewSplashLS.setVisibility(View.GONE);

				imageViewBack.setVisibility(View.VISIBLE);
				relativeLayoutJugnooLogo.setVisibility(View.GONE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.GONE);
				linearLayoutSignup.setVisibility(View.VISIBLE);
				break;

		}
		this.state = state;
	}


	private void sendToRegisterThroughSms(String referralCode){
		if(!"".equalsIgnoreCase(referralCode)) {
			Data.deepLinkIndex = -1;
			FlurryEventLogger.event(SIGNUP_THROUGH_REFERRAL);
			RegisterScreen.registerationType = RegisterScreen.RegisterationType.EMAIL;
			Intent intent = new Intent(SplashNewActivity.this, RegisterScreen.class);
			intent.putExtra("referral_code", referralCode);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}

	public void getDeviceToken() {
		if(ConfigMode.LIVE == Config.getConfigMode() && Utils.isAppInstalled(SplashNewActivity.this, Data.DRIVER_APP_PACKAGE)){
			DialogPopup.alertPopupTwoButtonsWithListeners(SplashNewActivity.this, "", "You need to uninstall Jugnoo Drivers App first to use this app", "Uninstall", "Cancel",
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								Intent i = new Intent();
								i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
								i.addCategory(Intent.CATEGORY_DEFAULT);
								i.setData(Uri.parse("package:" + Data.DRIVER_APP_PACKAGE));
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								try {
									startActivity(i);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					}, false, false);

		}
		else{
			DialogPopup.showLoadingDialogDownwards(SplashNewActivity.this, "Loading...");
			new DeviceTokenGenerator().generateDeviceToken(SplashNewActivity.this, new IDeviceTokenReceiver() {

				@Override
				public void deviceTokenReceived(final String regId) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
							Data.deviceToken = regId;
							Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
							accessTokenLogin(SplashNewActivity.this);
							FlurryEventLogger.appStarted(regId);
						}
					});

				}
			});
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		DialogPopup.dismissAlertPopup();
		retryAccessTokenLogin();
		resumed = true;

		AppEventsLogger.activateApp(this);

		if(Data.locationFetcher != null){
			Data.locationFetcher.connect();
		}
		else{
			Data.locationFetcher = new LocationFetcher(this, 1000, 1);
		}
	}


	public void retryAccessTokenLogin() {
		if (State.LOGIN != state && State.SIGNUP != state && resumed) {
			buttonRefresh.performClick();
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

		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
				if (0 == resultCode) {
					Data.locationSettingsNoPressed = true;
				}
			}
			else if(requestCode == GOOGLE_SIGNIN_REQ_CODE_LOGIN){
				if(RESULT_OK == resultCode){
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


	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
		if (!"".equalsIgnoreCase(pair.first)) {
			String accessToken = pair.first;

			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialogDownwards(activity, "Loading...");

				if (Data.locationFetcher != null) {
					Data.loginLatitude = Data.locationFetcher.getLatitude();
					Data.loginLongitude = Data.locationFetcher.getLongitude();
				}

				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);
				params.put("device_token", Data.getDeviceToken());


				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);


				params.put("app_version", "" + Data.appVersion);
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "" + pair.second);

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

				Log.e("params login_using_access_token", "=" + params);

				Log.e("Config.getServerUrl() + \"/login_using_access_token\"", "=" + Config.getServerUrl() + "/login_using_access_token");

				AsyncHttpClient client = Data.getClient();
				client.post(Config.getServerUrl() + "/login_using_access_token", params,
						new CustomAsyncHttpResponseHandler() {

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								performLoginFailure(activity);
							}

							@Override
							public void onSuccess(String response) {
								Log.e("Server response of access_token", "response = " + response);
								performLoginSuccess(activity, response);
							}
						});
			} else {
				changeUIState(State.SPLASH_NO_NET);
			}
		} else {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				changeUIState(State.SPLASH_LS);
			} else{
				changeUIState(State.SPLASH_NO_NET);
			}
			sendToRegisterThroughSms(Data.deepLinkReferralCode);
		}
	}


	public void performLoginSuccess(Activity activity, String response) {
		try {
			JSONObject jObj = new JSONObject(response);

			int flag = jObj.getInt("flag");

			if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
				if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
					String error = jObj.getString("error");
					DialogPopup.alertPopup(activity, "", error);
					DialogPopup.dismissLoadingDialog();
				} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
					String error = jObj.getString("error");
					DialogPopup.alertPopup(activity, "", error);
					DialogPopup.dismissLoadingDialog();
				} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
					if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
						new AccessTokenDataParseAsync(activity, response).execute();

						SharedPreferences pref1 = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
						Editor editor = pref1.edit();
						editor.putString(Data.SP_ACCESS_TOKEN_KEY, "");
						editor.commit();
					} else {
						DialogPopup.dismissLoadingDialog();
					}
				} else {
					DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
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

	public void performLoginFailure(Activity activity) {
		DialogPopup.dismissLoadingDialog();
		DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
		DialogPopup.dismissLoadingDialog();
		linearLayoutNoNet.setVisibility(View.VISIBLE);
		linearLayoutLoginSignupButtons.setVisibility(View.GONE);
		viewSplashLS.setVisibility(View.GONE);
	}


	class AccessTokenDataParseAsync extends AsyncTask<String, Integer, String> {

		Activity activity;
		String response;

		public AccessTokenDataParseAsync(Activity activity, String response) {
			this.activity = activity;
			this.response = response;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String resp = new JSONParser().parseAccessTokenLoginData(activity, response);
				Log.e("AccessTokenDataParseAsync resp", "=" + resp);
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				return HttpRequester.SERVER_TIMEOUT;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("AccessTokenDataParseAsync result", "=" + result);
			if (result.contains(HttpRequester.SERVER_TIMEOUT)) {
				loginDataFetched = false;
				DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			} else {
				loginDataFetched = true;
			}
			DialogPopup.dismissLoadingDialog();
		}
	}


	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception {
//		"popup": {
//	        "title": "Update Version",
//	        "text": "Update app with new version!",
//	        "cur_version": 116,			// could be used for local check
//	        "is_force": 1				// 1 for forced, 0 for not forced
//	}
		if (!jObj.isNull("popup")) {
			try {
				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup");
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
		} else {
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
			textHead.setTypeface(Fonts.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.latoRegular(activity));
			textHead.setVisibility(View.VISIBLE);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textHead.setText(title);
			textMessage.setText(message);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.latoRegular(activity));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if (isForced == 1) {
						activity.finish();
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
					activity.finish();
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
				if(State.SPLASH_LS == state || State.SPLASH_INIT == state || State.SPLASH_NO_NET == state) {
					if (SplashNewActivity.this.hasWindowFocus() && loginDataFetched) {
						loginDataFetched = false;
						Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
						intent.setData(getIntent().getData());
						startActivity(intent);
						ActivityCompat.finishAffinity(SplashNewActivity.this);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
				}
				else if(State.LOGIN == state){
					if(SplashNewActivity.this.hasWindowFocus() && loginDataFetched){
						Database2.getInstance(SplashNewActivity.this).updateDriverLastLocationTime();
						Database2.getInstance(SplashNewActivity.this).close();

						Map<String, String> articleParams = new HashMap<String, String>();
						articleParams.put("username", Data.userData.userName);
						FlurryAgent.logEvent("App Login", articleParams);

						loginDataFetched = false;
						Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
						intent.setData(Data.splashIntentUri);
						startActivity(intent);
						ActivityCompat.finishAffinity(SplashNewActivity.this);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
					else if(SplashNewActivity.this.hasWindowFocus() && facebookRegister){
						facebookRegister = false;
						sendIntentToRegisterScreen(RegisterScreen.RegisterationType.FACEBOOK);
					}
					else if(SplashNewActivity.this.hasWindowFocus() && googleRegister){
						googleRegister = false;
						sendIntentToRegisterScreen(RegisterScreen.RegisterationType.GOOGLE);
					}
					else if(SplashNewActivity.this.hasWindowFocus() && sendToOtpScreen){
						sendIntentToOtpScreen();
					}
				}
			}
		}, 500);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
		System.gc();
	}

	@Override
	public void onBackPressed() {
		if(State.LOGIN == state){
			performLoginBackPressed();
		}
		else if(State.SIGNUP == state){

		}
		else{
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
			textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.latoRegular(activity));
			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode);
			etCode.setTypeface(Fonts.latoRegular(activity));

			textHead.setText("Confirm Debug Password");
			textMessage.setText("Please enter password to continue.");

			textHead.setVisibility(View.GONE);
			textMessage.setVisibility(View.GONE);


			final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
			btnConfirm.setTypeface(Fonts.latoRegular(activity));

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
							activity.finish();
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
	}


	private void showLocationEnableDialog(){
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (resp != ConnectionResult.SUCCESS) {
			Log.e("Google Play Service Error ", "=" + resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		} else {
			LocationInit.showLocationAlertDialog(this);
		}
	}

	private void initiateDeviceInfoVariables(){
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

			if(Config.getConfigMode() == ConfigMode.LIVE){
				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
			}
			else{
				Data.uniqueDeviceId = UniqueIMEIID.getUniqueIMEIId(this);
			}

			Log.e("Data.uniqueDeviceId = ", "=" + Data.uniqueDeviceId);

			Utils.generateKeyHash(this);

		} catch (Exception e) {
			Log.e("error in fetching appVersion and gcm key", ".." + e.toString());
		}
	}


	public enum State{
		SPLASH_INIT(0), SPLASH_LS(1), SPLASH_NO_NET(2), LOGIN(3), SIGNUP(4);

		private int ordinal;
		State(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}


	public class CustomTextWatcher implements TextWatcher {
		private TextView textViewRequired;
		public CustomTextWatcher(TextView textViewRequired){
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


	private void setLoginScreenValuesOnCreate(){
		// set email screen values according to intent
		try {
			if(getIntent().hasExtra(KEY_BACK_FROM_OTP) && RegisterScreen.RegisterationType.EMAIL == RegisterScreen.registerationType){
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
		try {
			if (getIntent().hasExtra(KEY_PREVIOUS_LOGIN_EMAIL)) {
				String previousLoginEmail = getIntent().getStringExtra(KEY_PREVIOUS_LOGIN_EMAIL);
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
			if (getIntent().hasExtra(KEY_FORGOT_LOGIN_EMAIL)) {
				String forgotLoginEmail = getIntent().getStringExtra(KEY_FORGOT_LOGIN_EMAIL);
				editTextEmail.setText(forgotLoginEmail);
				editTextEmail.setSelection(editTextEmail.getText().length());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}



	public void performLoginBackPressed(){
		if(fromPreviousAccounts){
			Intent intent = new Intent(SplashNewActivity.this, MultipleAccountsActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		else {
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

			RequestParams params = new RequestParams();

			if(Data.locationFetcher != null){
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

			if(isPhoneNumber){
				params.put("phone_no", emailId);
			}
			else{
				params.put("email", emailId);
			}
			params.put("password", password);
			params.put("device_token", Data.getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.loginLatitude);
			params.put("longitude", ""+Data.loginLongitude);
			params.put("client_id", Config.getClientId());

			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			}
			else{
				params.put("device_rooted", "0");
			}

			Log.i("params", "="+params);

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
											enteredEmail = jObj.getString("user_email");
										}
										else{
											enteredEmail = emailId;
										}
										phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
										accessToken = jObj.getString("access_token");
										Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
										Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
										otpErrorMsg = jObj.getString("error");
										RegisterScreen.registerationType = RegisterScreen.RegisterationType.EMAIL;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											Database.getInstance(SplashNewActivity.this).insertEmail(emailId);
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

	public void sendFacebookLoginValues(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

			RequestParams params = new RequestParams();

			if(Data.locationFetcher != null){
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}


			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);

			params.put("device_token", Data.getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.loginLatitude);
			params.put("longitude", ""+Data.loginLongitude);
			params.put("client_id", Config.getClientId());

			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			}
			else{
				params.put("device_rooted", "0");
			}


			Log.i("params", ""+params);

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
										Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
										otpErrorMsg = jObj.getString("error");
										RegisterScreen.registerationType = RegisterScreen.RegisterationType.FACEBOOK;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											loginDataFetched = true;

											Database.getInstance(SplashNewActivity.this).insertEmail(Data.facebookUserData.userEmail);
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

	public void sendGoogleLoginValues(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

			RequestParams params = new RequestParams();

			if(Data.locationFetcher != null){
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

			params.put("google_access_token", Data.googleSignInAccount.getIdToken());

			params.put("device_token", Data.getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.loginLatitude);
			params.put("longitude", ""+Data.loginLongitude);
			params.put("client_id", Config.getClientId());

			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			} else{
				params.put("device_rooted", "0");
			}


			Log.i("params", ""+params);

			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/login_using_google", params,
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
										googleRegister = true;
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
										Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
										otpErrorMsg = jObj.getString("error");
										RegisterScreen.registerationType = RegisterScreen.RegisterationType.GOOGLE;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
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
		DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", otpErrorMsg, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogPopup.dismissAlertPopup();
				OTPConfirmScreen.intentFromRegister = false;
				if(RegisterScreen.RegisterationType.FACEBOOK == RegisterScreen.registerationType){
					OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
							Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
							Data.facebookUserData.accessToken,
							Data.facebookUserData.userEmail,
							Data.facebookUserData.userName,
							phoneNoOfUnverifiedAccount, "", "", accessToken);
				}
				else if(RegisterScreen.RegisterationType.GOOGLE == RegisterScreen.registerationType){
					OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
							Data.googleSignInAccount.getDisplayName(),
							Data.googleSignInAccount.getEmail(),
							"",
							phoneNoOfUnverifiedAccount, "", "", accessToken);
				}
				else{
					OTPConfirmScreen.intentFromRegister = false;
					OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "", accessToken);
				}

				Intent intent = new Intent(SplashNewActivity.this, OTPConfirmScreen.class);
				intent.putExtra("show_timer", 0);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}


	public void sendIntentToRegisterScreen(final RegisterScreen.RegisterationType registerationType){
		DialogPopup.alertPopupWithListener(this, "", notRegisteredMsg, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterScreen.registerationType = registerationType;
				startActivity(new Intent(SplashNewActivity.this, RegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}

}
