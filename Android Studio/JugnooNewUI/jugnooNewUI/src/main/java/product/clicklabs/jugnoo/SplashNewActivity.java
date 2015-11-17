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
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
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
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
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
import rmn.androidscreenlibrary.ASSL;

public class SplashNewActivity extends BaseActivity implements LocationUpdate, FlurryEventNames {

	//adding drop location

	LinearLayout relative;

	ImageView imageViewJugnooLogo;
	ImageView imageViewDebug1, imageViewDebug2;

	RelativeLayout relativeLayoutLoginSignupButtons;
	Button buttonLogin, buttonRegister;

	LinearLayout linearLayoutNoNet;
	TextView textViewNoNet;
	Button buttonNoNetCall;

	boolean loginDataFetched = false, resumed = false;

	boolean touchedDown1 = false, touchedDown2 = false;
	int debugState = 0;



	// *****************************Used for flurry work***************//

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

							if(referringParams.has("pickup_lat") && referringParams.has("pickup_lng")){
								Data.deepLinkPickup = 1;
								Data.deepLinkPickupLatitude = Double.parseDouble(referringParams.optString("pickup_lat"));
								Data.deepLinkPickupLongitude = Double.parseDouble(referringParams.optString("pickup_lng"));
							}
							else{
								Data.deepLinkIndex = referringParams.optInt("deepindex", -1);
							}

							Log.e("Deeplink =", "=" + Data.deepLinkIndex);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
//		Data.getDeepLinkIndexFromIntent(intent);
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

//		Data.getDeepLinkIndexFromIntent(getIntent());


		try {
			NewRelic.withApplicationToken(
					Config.getNewRelicKey()
			).start(this.getApplication());
		} catch (Exception e) {
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

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);


		imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);

		imageViewDebug1 = (ImageView) findViewById(R.id.imageViewDebug1);
		imageViewDebug2 = (ImageView) findViewById(R.id.imageViewDebug2);


		relativeLayoutLoginSignupButtons = (RelativeLayout) findViewById(R.id.relativeLayoutLoginSignupButtons);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setTypeface(Fonts.latoRegular(getApplicationContext()), Typeface.BOLD);
		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setTypeface(Fonts.latoRegular(getApplicationContext()), Typeface.BOLD);

		linearLayoutNoNet = (LinearLayout) findViewById(R.id.linearLayoutNoNet);
		textViewNoNet = (TextView) findViewById(R.id.textViewNoNet);
		textViewNoNet.setTypeface(Fonts.latoRegular(this));
		buttonNoNetCall = (Button) findViewById(R.id.buttonNoNetCall);
		buttonNoNetCall.setTypeface(Fonts.latoRegular(this));

		buttonNoNetCall.setText("Call on " + Config.getSupportNumber(SplashNewActivity.this) + " to book your ride");


		relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
		linearLayoutNoNet.setVisibility(View.GONE);


		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(LOGIN_OPTION_MAIN);
				Intent intent = new Intent(SplashNewActivity.this, SplashLogin.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(SIGNUP);
				RegisterScreen.facebookLogin = false;
				Intent intent = new Intent(SplashNewActivity.this, RegisterScreen.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		buttonNoNetCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.openCallIntent(SplashNewActivity.this, Config.getSupportNumber(SplashNewActivity.this));
			}
		});

		relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!loginDataFetched) {
					getDeviceToken();
				}
			}
		});

//		imageViewJugnooLogo.setOnLongClickListener(new View.OnLongClickListener() {
//
//			@Override
//			public boolean onLongClick(View v) {
//				confirmDebugPasswordPopup(SplashNewActivity.this);
//				FlurryEventLogger.debugPressed("no_token");
//				return false;
//			}
//		});
//
//        imageViewJugnooLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


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
						relative.setBackgroundColor(getResources().getColor(R.color.yellow_alpha));
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								relative.setBackgroundColor(getResources().getColor(R.color.yellow));
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


		if (getIntent().hasExtra("no_anim")) {
			FacebookLoginHelper.logoutFacebook();
			imageViewJugnooLogo.clearAnimation();
			getDeviceToken();
		} else {
			Animation animation = new AlphaAnimation(0, 1);
			animation.setFillAfter(true);
			animation.setDuration(1000);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.setAnimationListener(new ShowAnimListener());
			imageViewJugnooLogo.startAnimation(animation);
		}


		startService(new Intent(this, PushPendingCallsService.class));

		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (resp != ConnectionResult.SUCCESS) {
			Log.e("Google Play Service Error ", "=" + resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		} else {
			LocationInit.showLocationAlertDialog(this);
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
			relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
			linearLayoutNoNet.setVisibility(View.GONE);
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
			relative.performClick();
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


	class ShowAnimListener implements AnimationListener {

		public ShowAnimListener() {
		}

		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
			imageViewJugnooLogo.clearAnimation();
			getDeviceToken();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}


	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {

		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);

		relativeLayoutLoginSignupButtons.setVisibility(View.GONE);
		linearLayoutNoNet.setVisibility(View.GONE);

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
				params.put("device_token", Data.deviceToken);


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
				linearLayoutNoNet.setVisibility(View.VISIBLE);
			}
		} else {
			relativeLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
			if (!AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				linearLayoutNoNet.setVisibility(View.VISIBLE);
			}
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
		ASSL.closeActivity(relative);
		System.gc();
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




}
