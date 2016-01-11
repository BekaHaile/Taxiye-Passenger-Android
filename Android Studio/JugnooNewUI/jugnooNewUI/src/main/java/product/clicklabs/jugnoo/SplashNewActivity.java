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
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONObject;

import java.util.Locale;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.LocationInit;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.UniqueIMEIID;
import product.clicklabs.jugnoo.utils.Utils;


public class SplashNewActivity extends BaseActivity implements LocationUpdate, FlurryEventNames {

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
	TextView textViewEmailRequired, textViewPasswordRequired, textViewLoginOr, textViewForgotPassword;
	Button buttonEmailLogin, buttonFacebookLogin, buttonGoogleLogin;

	LinearLayout linearLayoutSignup;


	boolean loginDataFetched = false, resumed = false;

	boolean touchedDown1 = false, touchedDown2 = false;
	int debugState = 0;

	private State state = State.SPLASH_LS;



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


		loginDataFetched = false;
		resumed = false;

		touchedDown1 = false;
		touchedDown2 = false;
		debugState = 0;

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

		linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
		editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
		editTextEmail.setTypeface(Fonts.latoRegular(this));
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setTypeface(Fonts.latoRegular(this), Typeface.ITALIC);
		textViewEmailRequired = (TextView) findViewById(R.id.textViewEmailRequired);
		textViewEmailRequired.setTypeface(Fonts.latoRegular(this));
		textViewPasswordRequired = (TextView) findViewById(R.id.textViewPasswordRequired);
		textViewPasswordRequired.setTypeface(Fonts.latoRegular(this));
		textViewLoginOr = (TextView) findViewById(R.id.textViewLoginOr);
		textViewLoginOr.setTypeface(Fonts.latoRegular(this));
		textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
		textViewForgotPassword.setTypeface(Fonts.mavenRegular(this));
		buttonEmailLogin = (Button) findViewById(R.id.buttonEmailLogin);
		buttonEmailLogin.setTypeface(Fonts.mavenLight(this));
		buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin);
		buttonFacebookLogin.setTypeface(Fonts.mavenLight(this));
		buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
		buttonGoogleLogin.setTypeface(Fonts.mavenLight(this));


		linearLayoutSignup = (LinearLayout) findViewById(R.id.linearLayoutSignup);


		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(LOGIN_OPTION_MAIN);
//				Intent intent = new Intent(SplashNewActivity.this, LoginActivity.class);
//				startActivity(intent);
//				finish();
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);

				changeUIState(State.LOGIN);
			}
		});

		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(SIGNUP);
//				RegisterScreen.registerationType = RegisterScreen.RegisterationType.EMAIL;
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
				if(State.LOGIN == state || State.SIGNUP == state){
					changeUIState(State.SPLASH_LS);
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









		editTextEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				textViewEmailRequired.setVisibility(editTextEmail.getText().length() > 0 ? View.GONE : View.VISIBLE);
			}
		});
		editTextPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				textViewPasswordRequired.setVisibility(editTextPassword.getText().length() > 0 ? View.GONE : View.VISIBLE);
			}
		});

		buttonEmailLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});




		changeUIState(State.SPLASH_INIT);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getDeviceToken();
			}
		}, 500);


//		if (getIntent().hasExtra("no_anim")) {
//			FacebookLoginHelper.logoutFacebook();
//			viewInitJugnoo.setVisibility(View.GONE);
//			viewSplashLS.setVisibility(View.GONE);
//			getDeviceToken();
//		} else {
//			viewInitJugnoo.setVisibility(View.VISIBLE);
//			viewSplashLS.setVisibility(View.VISIBLE);
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					viewInitJugnoo.setVisibility(View.GONE);
//					getDeviceToken();
//				}
//			}, 500);
//
//		}

		initiateDeviceInfoVariables();
		startService(new Intent(this, PushPendingCallsService.class));
		showLocationEnableDialog();
	}

	private void changeUIState(State state){
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
		if (Data.locationFetcher == null) {
			Data.locationFetcher = new LocationFetcher(SplashNewActivity.this, 1000, 1);
		}


		super.onResume();
		DialogPopup.dismissAlertPopup();
		retryAccessTokenLogin();
		resumed = true;

		AppEventsLogger.activateApp(this);
	}


	public void retryAccessTokenLogin() {
		if (resumed) {
			buttonRefresh.performClick();
		}
	}


	@Override
	protected void onPause() {
		try {
			Data.locationFetcher.destroy();
			Data.locationFetcher = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();

		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
			if (0 == resultCode) {
				Data.locationSettingsNoPressed = true;
			}
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
				if (SplashNewActivity.this.hasWindowFocus() && loginDataFetched) {
					loginDataFetched = false;
					Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
					intent.setData(getIntent().getData());
					startActivity(intent);
					ActivityCompat.finishAffinity(SplashNewActivity.this);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
		if(State.LOGIN == state || State.SIGNUP == state){
			changeUIState(State.SPLASH_LS);
		} else{
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
		SPLASH_INIT, SPLASH_LS, SPLASH_NO_NET, LOGIN, SIGNUP

	}

}
