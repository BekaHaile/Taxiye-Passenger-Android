package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.Builder;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

@SuppressLint("DefaultLocale")
public class HomeActivity extends FragmentActivity implements AppInterruptHandler, LocationUpdate {

	
	
	DrawerLayout drawerLayout;																		// views declaration
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	
	
	
	ImageView profileImg;
	TextView userName;
	
	RelativeLayout driverModeRl;
	TextView driverModeText;
	ImageView driverModeToggle;
	
	RelativeLayout jugnooONRl;
	TextView jugnooONText;
	ImageView jugnooONToggle;
	
	RelativeLayout driverTypeRl;
	TextView driverTypeText;
	
	RelativeLayout inviteFriendRl;
	TextView inviteFriendText;
	
	RelativeLayout bookingsRl;
	TextView bookingsText;
	
	RelativeLayout aboutRl;
	TextView aboutText;
	
	RelativeLayout supportRl;
	TextView supportText;
	
	RelativeLayout sosRl;
	TextView sosText;
	
	RelativeLayout languagePrefrencesRl;
	TextView languagePrefrencesText;
	
	RelativeLayout logoutRl;
	TextView logoutText;
	
	
	
	
	
	
	
	
	
	
	
	
	//Top RL
	RelativeLayout topRl;
	Button menuBtn, backBtn; //, favBtn;
	TextView title;
	ImageView jugnooLogo;
	Button checkServerBtn, toggleDebugModeBtn;
	
	
	
	//Passenger main layout
	RelativeLayout passengerMainLayout;
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	//Initial layout
	RelativeLayout initialLayout;
	TextView textViewAssigningInProgress;
	RelativeLayout searchBarLayout;
	EditText searchEt;
	Button search, myLocationBtn, requestRideBtn, initialCancelRideBtn;
	RelativeLayout nearestDriverRl;
	TextView nearestDriverText;
	ProgressBar nearestDriverProgress;
	
	
	
	//Request Final Layout
	RelativeLayout requestFinalLayout;
	ImageView driverImage, driverCarImage;
	TextView driverName, driverTime;
	Button callDriverBtn;
	TextView inRideRideInProgress;
	Button customerInRideMyLocationBtn;
	
	
	
	
	
	
	
	
	//Search layout
	RelativeLayout searchLayout;
	ListView searchList;
	SearchListAdapter searchListAdapter;
	
	
	
	
	
	
	
	//Center Location Layout
//	RelativeLayout centreLocationRl, centreInfoRl;
//	ProgressBar centreInfoProgress;
//	TextView centreLocationSnippet;
//	Button setFavoriteBtn;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Driver main layout 
	RelativeLayout driverMainLayout;
	
	
	//Driver initial layout
	RelativeLayout driverInitialLayout;
	RelativeLayout driverNewRideRequestRl;
	ListView driverRideRequestsList;
	TextView driverNewRideRequestText;
	TextView driverNewRideRequestClickText;
	Button driverInitialMyLocationBtn;
	
	DriverRequestListAdapter driverRequestListAdapter;
	
	
	
	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn;
	
	
	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;
	
	TextView driverPassengerName;
	TextView driverPassengerRatingValue;
	RelativeLayout driverPassengerCallRl;
	TextView driverPassengerCallText;
	
	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideMyLocationBtn, driverStartRideBtn;
	Button driverCancelRideBtn;
	
	//End ride layout
	RelativeLayout driverInRideMainRl;
	
	Button driverEndRideMyLocationBtn;
	TextView driverIRDistanceText, driverIRDistanceValue, driverIRDistanceKmText;
	TextView driverIRFareText, driverIRFareRsText, driverIRFareValue;
	
	TextView driverRideTimeText;
	PausableChronometer rideTimeChronometer;
	
	RelativeLayout driverWaitRl;
	TextView driverWaitText;
	PausableChronometer waitChronometer;
	
	Button driverEndRideBtn;
	public static int waitStart = 2;
	
	
	
	
	//Review layout
	RelativeLayout endRideReviewRl;
		
	ImageView reviewUserImgBlured, reviewUserImage;
	TextView reviewUserName, reviewReachedDestinationText, 
	reviewDistanceText, reviewDistanceValue, 
	reviewWaitText, reviewWaitValue,
	reviewFareText, reviewFareValue, 
	reviewRatingText;
	LinearLayout reviewRatingBarRl, endRideInfoRl;
	TextView jugnooRideOverText, takeFareText;
	RatingBar reviewRatingBar;
	Button reviewSubmitBtn;
	
	
	
	
	
																								// data variables declaration
	
	
	
	
	
	
	
	
	Location lastLocation;
	
	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>(); 
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	
	static double totalDistance = -1, totalFare = 0, previousWaitTime = 0, previousRideTime = 0;
	static double fareFixed = 30, farePerKm = 10, fareThresholdDistance = 2;
	
	static String waitTime = "";
	
	
	static Location myLocation;
	
	
	
	LocationManager locationManager;
	
	static PassengerScreenMode passengerScreenMode;
	
	static UserMode userMode;
	
	static JugnooDriverMode jugnooDriverMode;
	static ExceptionalDriver exceptionalDriver = ExceptionalDriver.NO;
	
	static DriverScreenMode driverScreenMode;
	
	
	
	
	GetDistanceTimeAddress getDistanceTimeAddress;
	
	
	
	
	Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker;
	MarkerOptions markerOptionsCustomerPickupLocation;
	
	static AppInterruptHandler appInterruptHandler;
	
	static Activity activity;
	
	boolean customerCancelPressed = false, 
			userCanceledDialogShown = false;
	boolean loggedOut = false, 
			zoomedToMyLocation = false;
	boolean dontCallRefreshDriver = false;
	
	
	AlertDialog gpsDialogAlert;
	Dialog noDriversDialog;
	
	LocationFetcher lowPowerLF, highAccuracyLF;
	
	
	
	
	
	
	public static AppMode appMode;
	
	public static final int MAP_PATH_COLOR = Color.RED;
	public static final int D_TO_C_MAP_PATH_COLOR = Color.RED;
	
	public static final long DRIVER_START_RIDE_CHECK_METERS = 600;
	
	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000;
	public static final double MAX_DISPLACEMENT_THRESHOLD = 200;
	public static final long SERVICE_RESTART_TIMER = 12 * 60 * 60 * 1000;
	
	public static final long DRIVER_FILTER_DISTANCE = 2600;
	
	public static final float LOW_POWER_ACCURACY_CHECK = 2000, HIGH_ACCURACY_ACCURACY_CHECK = 200;
	
	public static final long AUTO_RATING_DELAY = 5 * 60 * 1000;
	
	
	
	
	public static final String REQUEST_RIDE_BTN_NORMAL_TEXT = "Call an auto", REQUEST_RIDE_BTN_ASSIGNING_DRIVER_TEXT = "Assigning driver...";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		HomeActivity.appInterruptHandler = HomeActivity.this;
		
		activity = this;
		
		customerCancelPressed = false;
		userCanceledDialogShown = false;
		
		loggedOut = false;
		zoomedToMyLocation = false;
		dontCallRefreshDriver = false;
		
		appMode = AppMode.NORMAL;
		
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		
		new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);
		
		
		
		
		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		
		
		
		
		profileImg = (ImageView) findViewById(R.id.profileImg);
		userName = (TextView) findViewById(R.id.userName); userName.setTypeface(Data.regularFont(getApplicationContext()));
		
		driverModeRl = (RelativeLayout) findViewById(R.id.driverModeRl);
		driverModeText = (TextView) findViewById(R.id.driverModeText); driverModeText.setTypeface(Data.regularFont(getApplicationContext()));
		driverModeToggle = (ImageView) findViewById(R.id.driverModeToggle);
		
		jugnooONRl = (RelativeLayout) findViewById(R.id.jugnooONRl);
		jugnooONText = (TextView) findViewById(R.id.jugnooONText); jugnooONText.setTypeface(Data.regularFont(getApplicationContext()));
		jugnooONToggle = (ImageView) findViewById(R.id.jugnooONToggle);
		
		driverTypeRl = (RelativeLayout) findViewById(R.id.driverTypeRl);
		driverTypeText = (TextView) findViewById(R.id.driverTypeText); driverTypeText.setTypeface(Data.regularFont(getApplicationContext()));
		
		inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
		inviteFriendText = (TextView) findViewById(R.id.inviteFriendText); inviteFriendText.setTypeface(Data.regularFont(getApplicationContext()));
		
		bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
		bookingsText = (TextView) findViewById(R.id.bookingsText); bookingsText.setTypeface(Data.regularFont(getApplicationContext()));
		
		aboutRl = (RelativeLayout) findViewById(R.id.aboutRl);
		aboutText = (TextView) findViewById(R.id.aboutText); aboutText.setTypeface(Data.regularFont(getApplicationContext()));
		
		supportRl = (RelativeLayout) findViewById(R.id.supportRl);
		supportText = (TextView) findViewById(R.id.supportText); supportText.setTypeface(Data.regularFont(getApplicationContext()));
		
		sosRl = (RelativeLayout) findViewById(R.id.sosRl);
		sosText = (TextView) findViewById(R.id.sosText); sosText.setTypeface(Data.regularFont(getApplicationContext()));
		
		languagePrefrencesRl = (RelativeLayout) findViewById(R.id.languagePrefrencesRl);
		languagePrefrencesText = (TextView) findViewById(R.id.languagePrefrencesText); languagePrefrencesText.setTypeface(Data.regularFont(getApplicationContext()));
		
		logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
		logoutText = (TextView) findViewById(R.id.logoutText); logoutText.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		
		
		
		
		//Top RL
		topRl = (RelativeLayout) findViewById(R.id.topRl);
		menuBtn = (Button) findViewById(R.id.menuBtn);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		jugnooLogo = (ImageView) findViewById(R.id.jugnooLogo);
		checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
		toggleDebugModeBtn = (Button) findViewById(R.id.toggleDebugModeBtn);
//		favBtn = (Button) findViewById(R.id.favBtn);
		
		
		menuBtn.setVisibility(View.VISIBLE);
		jugnooLogo.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		
		
		
		
		
		
		//Map Layout
		mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		
		
		
		
		
		
		
		//Passenger main layout
		passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);
		
		
		
		//Initial layout 
		initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
		textViewAssigningInProgress = (TextView) findViewById(R.id.textViewAssigningInProgress); textViewAssigningInProgress.setTypeface(Data.regularFont(getApplicationContext()));
		searchBarLayout = (RelativeLayout) findViewById(R.id.searchBarLayout);
		searchBarLayout.setVisibility(View.GONE);
		
		searchEt = (EditText) findViewById(R.id.searchEt); searchEt.setTypeface(Data.regularFont(getApplicationContext()));
		search = (Button) findViewById(R.id.search);
		
		myLocationBtn = (Button) findViewById(R.id.myLocationBtn);
		requestRideBtn = (Button) findViewById(R.id.requestRideBtn); requestRideBtn.setTypeface(Data.regularFont(getApplicationContext()));
		initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn); initialCancelRideBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		nearestDriverRl = (RelativeLayout) findViewById(R.id.nearestDriverRl);
		nearestDriverText = (TextView) findViewById(R.id.nearestDriverText); nearestDriverText.setTypeface(Data.regularFont(getApplicationContext()));
		nearestDriverProgress = (ProgressBar) findViewById(R.id.nearestDriverProgress);
		
		
		
		
		
		
		
		
		
		//Request Final Layout
		requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);
		
		driverImage = (ImageView) findViewById(R.id.driverImage);
		driverCarImage = (ImageView) findViewById(R.id.driverCarImage);
		
		driverName = (TextView) findViewById(R.id.driverName); driverName.setTypeface(Data.regularFont(getApplicationContext()));
		driverTime = (TextView) findViewById(R.id.driverTime); driverTime.setTypeface(Data.regularFont(getApplicationContext()));
		callDriverBtn = (Button) findViewById(R.id.callDriverBtn); callDriverBtn.setTypeface(Data.regularFont(getApplicationContext()));
		inRideRideInProgress = (TextView) findViewById(R.id.inRideRideInProgress); inRideRideInProgress.setTypeface(Data.regularFont(getApplicationContext()));
		customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);
		
		
		
		
		
		
		
		

		
		
		//Center location layout
//		centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
//		centreInfoRl = (RelativeLayout) findViewById(R.id.centreInfoRl);
//		centreInfoProgress = (ProgressBar) findViewById(R.id.centreInfoProgress);
//		centreLocationSnippet = (TextView) findViewById(R.id.centreLocationSnippet); centreLocationSnippet.setTypeface(Data.regularFont(getApplicationContext()));
//		setFavoriteBtn = (Button) findViewById(R.id.setFavoriteBtn);
//		
//		centreLocationSnippet.setMinimumWidth((int)(200.0 * ASSL.Xscale()));
//		centreLocationSnippet.setMaxWidth((int)(500.0 * ASSL.Xscale()));
		
		
		
		
		
		//Search Layout
		searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
		searchList = (ListView) findViewById(R.id.searchList);
		searchListAdapter = new SearchListAdapter();
		searchList.setAdapter(searchListAdapter);
		
		
		
		
		
		
		
		
		
		
		// Driver main layout 
		driverMainLayout = (RelativeLayout) findViewById(R.id.driverMainLayout);
		
		
		//Driver initial layout
		driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
		driverNewRideRequestRl = (RelativeLayout) findViewById(R.id.driverNewRideRequestRl);
		driverRideRequestsList = (ListView) findViewById(R.id.driverRideRequestsList);
		driverNewRideRequestText = (TextView) findViewById(R.id.driverNewRideRequestText); driverNewRideRequestText.setTypeface(Data.regularFont(getApplicationContext()));
		driverNewRideRequestClickText = (TextView) findViewById(R.id.driverNewRideRequestClickText); driverNewRideRequestClickText.setTypeface(Data.regularFont(getApplicationContext()));
		driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
		
		driverNewRideRequestRl.setVisibility(View.GONE);
		driverRideRequestsList.setVisibility(View.GONE);
		
		driverRequestListAdapter = new DriverRequestListAdapter();
		driverRideRequestsList.setAdapter(driverRequestListAdapter);
		
		
		// Driver Request Accept layout
		driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
		driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
		driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn); driverAcceptRideBtn.setTypeface(Data.regularFont(getApplicationContext()));
		driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn); driverCancelRequestBtn.setTypeface(Data.regularFont(getApplicationContext()));
		driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
		
		
		
		// Driver engaged layout
		driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
		

		driverPassengerName = (TextView) findViewById(R.id.driverPassengerName); driverPassengerName.setTypeface(Data.regularFont(getApplicationContext()));
		driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue); driverPassengerRatingValue.setTypeface(Data.regularFont(getApplicationContext()));
		driverPassengerCallRl = (RelativeLayout) findViewById(R.id.driverPassengerCallRl);
		driverPassengerCallText = (TextView) findViewById(R.id.driverPassengerCallText); driverPassengerCallText.setTypeface(Data.regularFont(getApplicationContext()));
		
		driverPassengerRatingValue.setVisibility(View.GONE);
		
		//Start ride layout
		driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
		driverStartRideMyLocationBtn = (Button) findViewById(R.id.driverStartRideMyLocationBtn);
		driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn); driverStartRideBtn.setTypeface(Data.regularFont(getApplicationContext()));
		driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn); driverCancelRideBtn.setTypeface(Data.regularFont(getApplicationContext()));

//		driverStartRideSlider.setThumb(createStartRideThumbDrawable());
//		driverStartRideSlider.setThumbOffset((int)(5.0f * ASSL.Xscale()));
		
		
		//End ride layout
		driverInRideMainRl = (RelativeLayout) findViewById(R.id.driverInRideMainRl);
		
		driverEndRideMyLocationBtn = (Button) findViewById(R.id.driverEndRideMyLocationBtn);
		
		driverIRDistanceText = (TextView) findViewById(R.id.driverIRDistanceText); driverIRDistanceText.setTypeface(Data.regularFont(getApplicationContext()));
		driverIRDistanceValue = (TextView) findViewById(R.id.driverIRDistanceValue); driverIRDistanceValue.setTypeface(Data.regularFont(getApplicationContext()));
		driverIRDistanceKmText = (TextView) findViewById(R.id.driverIRDistanceKmText); driverIRDistanceKmText.setTypeface(Data.regularFont(getApplicationContext()));
		
		driverIRFareText = (TextView) findViewById(R.id.driverIRFareText); driverIRFareText.setTypeface(Data.regularFont(getApplicationContext()));
		driverIRFareRsText = (TextView) findViewById(R.id.driverIRFareRsText); driverIRFareRsText.setTypeface(Data.regularFont(getApplicationContext()));
		driverIRFareValue = (TextView) findViewById(R.id.driverIRFareValue); driverIRFareValue.setTypeface(Data.regularFont(getApplicationContext()));
		
		driverRideTimeText = (TextView) findViewById(R.id.driverRideTimeText); driverRideTimeText.setTypeface(Data.regularFont(getApplicationContext()));
		rideTimeChronometer = (PausableChronometer) findViewById(R.id.rideTimeChronometer); rideTimeChronometer.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		
		driverWaitRl = (RelativeLayout) findViewById(R.id.driverWaitRl);
		driverWaitText = (TextView) findViewById(R.id.driverWaitText); driverWaitText.setTypeface(Data.regularFont(getApplicationContext()));
		waitChronometer = (PausableChronometer) findViewById(R.id.waitChronometer); waitChronometer.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);

		driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn); driverEndRideBtn.setTypeface(Data.regularFont(getApplicationContext()));
		waitStart = 2;

//		driverEndRideSlider.setThumb(createEndRideThumbDrawable());
//		driverEndRideSlider.setThumbOffset((int)(5.0f * ASSL.Xscale()));
		
		
		
		rideTimeChronometer.setText("00:00:00");
		rideTimeChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer cArg) {
						long time = SystemClock.elapsedRealtime() - cArg.getBase();
						rideTimeChronometer.eclipsedTime = time;
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(hh + ":" + mm + ":" + ss);
					}
				});
		
		waitChronometer.setText("00:00:00");
		waitChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer cArg) {
						long time = SystemClock.elapsedRealtime() - cArg.getBase();
						waitChronometer.eclipsedTime = time;
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(hh + ":" + mm + ":" + ss);
					}
				});
		
		
		
		
		//Review Layout
		endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);
		
		reviewUserImgBlured = (ImageView) findViewById(R.id.reviewUserImgBlured);
		reviewUserImage = (ImageView) findViewById(R.id.reviewUserImage);
		
		reviewUserName = (TextView) findViewById(R.id.reviewUserName); reviewUserName.setTypeface(Data.regularFont(getApplicationContext()));
		reviewReachedDestinationText = (TextView) findViewById(R.id.reviewReachedDestinationText); reviewReachedDestinationText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText); reviewDistanceText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue); reviewDistanceValue.setTypeface(Data.regularFont(getApplicationContext()));
		reviewWaitText = (TextView) findViewById(R.id.reviewWaitText); reviewWaitText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		reviewWaitValue = (TextView) findViewById(R.id.reviewWaitValue); reviewWaitValue.setTypeface(Data.regularFont(getApplicationContext()));
		reviewFareText = (TextView) findViewById(R.id.reviewFareText); reviewFareText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		reviewFareValue = (TextView) findViewById(R.id.reviewFareValue); reviewFareValue.setTypeface(Data.regularFont(getApplicationContext()));
		reviewRatingText = (TextView) findViewById(R.id.reviewRatingText); reviewRatingText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		
		reviewRatingBarRl = (LinearLayout) findViewById(R.id.reviewRatingBarRl);
		endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
		jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText); 
		jugnooRideOverText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		takeFareText = (TextView) findViewById(R.id.takeFareText); 
		takeFareText.setTypeface(Data.regularFont(getApplicationContext()));
		reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
		reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn); reviewSubmitBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		reviewRatingBar.setRating(0);
		
		
				 
		
		
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Top bar events
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setUserData();
				drawerLayout.openDrawer(menuLayout);
			}
		});
		
		
//		favBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			}
//		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		
		checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {

				String message = "";
				
				if(Data.SERVER_URL.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
					message = "Current server is TRIAL. "+Data.TRIAL_SERVER_URL;
				}
				else if(Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
					message = "Current server is LIVE. "+Data.LIVE_SERVER_URL;
				}
				else if(Data.SERVER_URL.equalsIgnoreCase(Data.DEV_SERVER_URL)){
					message = "Current server is DEV. "+Data.DEV_SERVER_URL;
				}
				
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
				
				return false;
			}
		});
		
		toggleDebugModeBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(HomeActivity.this);
				return false;
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// menu events\
		driverModeToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("userMode","="+userMode);
				if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
					changeDriverModeAsync(HomeActivity.this, 0);
				}
				else if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
					changeDriverModeAsync(HomeActivity.this, 1);
				}
			}
		});
		
		
		jugnooONToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("hellondsmhfajk.", "userMode = "+userMode + " driverScreenMode = "+driverScreenMode);
				if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
					if(jugnooDriverMode == JugnooDriverMode.ON){
						jugnooDriverMode = JugnooDriverMode.OFF;
						changeJugnooON(jugnooDriverMode);
					}
					else{
						jugnooDriverMode = JugnooDriverMode.ON;
						changeJugnooON(jugnooDriverMode);
					}
				}
			}
		});
		
		
		driverTypeRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(HomeActivity.this, DriverTypeActivity.class));
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		inviteFriendRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if("".equalsIgnoreCase(Data.fbAccessToken)){
					facebookLogin();
				}
				else{
					inviteFbFriend();
				}
			}
		});
		
		
		aboutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		supportRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@jugnoo.in" });
				email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Support");
				email.putExtra(Intent.EXTRA_TEXT, "");
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Choose an Email client:"));
			}
		});
		
		
		
		sosRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:100"));
		        startActivity(callIntent);
			}
		});
		
		
		languagePrefrencesRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, LanguagePrefrencesActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		bookingsRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(userMode == UserMode.DRIVER){
					startActivity(new Intent(HomeActivity.this, DriverHistoryActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				else if(userMode == UserMode.PASSENGER){
					startActivity(new Intent(HomeActivity.this, BookingActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			}
		});
		
		
		
		
		
		
		logoutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(((userMode == UserMode.DRIVER) && (driverScreenMode == DriverScreenMode.D_INITIAL)) 
						|| ((userMode == UserMode.PASSENGER) && (passengerScreenMode == PassengerScreenMode.P_INITIAL))){
					logoutPopup(HomeActivity.this);
				}
				else{
					new DialogPopup().alertPopup(activity, "", "Ride in progress. You can logout only after the ride ends.");
				}
			}
		});
		
		
		menuLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Customer initial layout events
		search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String searchPlace = searchEt.getText().toString();
				
				if(searchPlace.length() > 0){
					searchGooglePlaces(searchPlace);
					hideSoftKeyboard();
				}
			}
		});
		
		
		searchEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						search.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		requestRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(requestRideBtn.getText().toString().equalsIgnoreCase(REQUEST_RIDE_BTN_NORMAL_TEXT)){
						if(checkWorkingTime()){
							if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
								if(myLocation != null){
									if(Data.driverInfos.size() > 0){
										callAnAutoPopup(HomeActivity.this);
									}
									else{
										noDriverAvailablePopup(HomeActivity.this, true);
									}
								}
								else{
									Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
								}
							}
							else{
								new DialogPopup().alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
							}
						}
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		
		initialCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				customerCancelPressed = true;
				cancelCustomerRequestAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer request final layout events
		callDriverBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+Data.assignedDriverInfo.phoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer center location info layout events
//		setFavoriteBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				saveToFavoritePopup(HomeActivity.this, fullAddress, map.getCameraPosition().target);
//			}
//		});
//		
//		centreInfoRl.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver initial layout events
		driverNewRideRequestRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverNewRideRequestRl.setVisibility(View.GONE);
				driverRideRequestsList.setVisibility(View.VISIBLE);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver accept layout events
		
		driverRequestAcceptBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverScreenMode = DriverScreenMode.D_INITIAL;
				switchDriverScreen(driverScreenMode);
			}
		});
		
		driverAcceptRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getBatteryPercentage() >= 10){
					 GCMIntentService.clearNotifications(HomeActivity.this);
					 GCMIntentService.stopRing();
					driverAcceptRideAsync(HomeActivity.this);
				}
				else{
					new DialogPopup().alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to accept the ride. Plugin to a power source to continue.");
				}
			}
		});
		
		
		driverCancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				GCMIntentService.stopRing();
				driverRejectRequestAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver start ride layout events
		driverPassengerCallRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+Data.assignedCustomerInfo.phoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		driverStartRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(getBatteryPercentage() >= 10){
					startRidePopup(HomeActivity.this);
				}
				else{
					new DialogPopup().alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to start the ride. Plugin to a power source to continue.");
				}
				
//	        	double displacement = distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), Data.dCustLatLng);
//	        	
//	        	if(displacement <= 100){
//	        		buildAlertMessageNoGps();
//		        	
//		        	GCMIntentService.clearNotifications(HomeActivity.this);
//		        	new GetAddressStartRide().execute();
//	        	}
//	        	else{
//	        		new DialogPopup().alertPopup(HomeActivity.this, "", "You must be present near the customer pickup location to start ride.");
//	        	}
	        }
		});
		
		
		
		driverCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelRidePopup(HomeActivity.this);
				
//				GCMIntentService.clearNotifications(HomeActivity.this);
//				driverRejectRideAsync(HomeActivity.this, 1);
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver in ride layout events 
		driverWaitRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(waitStart == 2){ 
					Log.e("waitChronometer.eclipsedTime on first start","="+waitChronometer.eclipsedTime);
					waitChronometer.start();
					rideTimeChronometer.stop();
					driverWaitRl.setBackgroundResource(R.drawable.red_btn_selector);
					driverWaitText.setText(getResources().getString(R.string.stop_wait));
					waitStart = 1;
					
					startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 1);
					
				}
				else{
					if(waitStart == 1){
						Log.e("waitChronometer.stop()","in driverWaitBtn on click");
						waitChronometer.stop();
						rideTimeChronometer.start();
						driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
						driverWaitText.setText(getResources().getString(R.string.start_wait));
						waitStart = 0;
						
						startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 0);
					}
					else if(waitStart == 0){
						waitChronometer.start();
						rideTimeChronometer.stop();
						driverWaitRl.setBackgroundResource(R.drawable.red_btn_selector);
						driverWaitText.setText(getResources().getString(R.string.stop_wait));
						waitStart = 1;
						
						startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 1);
					}
				}
				
			}
		});
		
		
		driverEndRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				endRidePopup(HomeActivity.this);
				
//				GCMIntentService.clearNotifications(HomeActivity.this);
//				Log.e("waitChronometer.stop()","in driverEndRideSlider on click");
//				waitChronometer.stop();
//				rideTimeChronometer.stop();
//				
//				driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
//				driverWaitText.setText(getResources().getString(R.string.start_wait));
//				waitStart = 0;
//				
//				long elapsedMillis = waitChronometer.eclipsedTime;
//				long seconds = elapsedMillis / 1000;
//				double minutes = Math.ceil(((double)seconds) / 60.0);
//				
//				driverScreenMode = DriverScreenMode.D_RIDE_END;
//				
//				new GetAddressEndRide(minutes).execute();
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// End ride review layout events
		endRideReviewRl.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
						
			}
		});
				
				
		reviewSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				int rating = (int)reviewRatingBar.getRating();
				
				if(rating > 0){
					if(userMode == UserMode.DRIVER){
						submitReviewAsync(HomeActivity.this, Data.dEngagementId, "0", Data.dCustomerId, ""+rating);
					}
					else if(userMode == UserMode.PASSENGER){
						submitReviewAsync(HomeActivity.this, Data.cEngagementId, "1", Data.cDriverId, ""+rating);
					}
				}
				else{
					if(userMode == UserMode.DRIVER){
						submitReviewAsync(HomeActivity.this, Data.dEngagementId, "0", Data.dCustomerId, "5");
					}
					else if(userMode == UserMode.PASSENGER){
						submitReviewAsync(HomeActivity.this, Data.cEngagementId, "1", Data.cDriverId, "5");
					}
				}
			}
		});
		
		
		
		
		
		
		lastLocation = null;
		
																	// map object initialized
		if(map != null){
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setTiltGesturesEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			//30.7500, 76.7800
			
			if(Data.latitude == 0 && Data.longitude == 0){
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
			}
			else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
			}
			
			
			// Find ZoomControl view
			View zoomControls = mapFragment.getView().findViewById(0x1);

			if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			    // ZoomControl is inside of RelativeLayout
			    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

			    // Align it to - parent top|left
			    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			    // Update margins, set to 10dp
			    final int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 250*ASSL.Yscale(),
			            getResources().getDisplayMetrics());
			    final int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20*ASSL.Yscale(),
			            getResources().getDisplayMetrics());
			    
			    params.setMargins(0, marginTop, marginRight, 0);
			}
			
			
			
			
			map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker arg0) {
					
					if(arg0.getTitle().equalsIgnoreCase("pickup location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Your Pickup Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("customer_current_location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
						map.setInfoWindowAdapter(customIW);
						
						return true;
					}
					
					else if(arg0.getTitle().equalsIgnoreCase("start ride location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("driver position")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Driver Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("driver shown to customer")){
						if(appMode == AppMode.DEBUG){
							String driverId = arg0.getSnippet();
							try{
								DriverInfo driverInfo = Data.driverInfos.get(Data.driverInfos.indexOf(new DriverInfo(driverId, 0, 0)));
								new DialogPopup().alertPopup(activity, "Driver Info", ""+driverInfo);
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						return true;
					}
					else{
						return true;
					}
//					else{
//						
//						int index = -1;
//						
//						for(int i=0; i<Data.driverRideRequests.size(); i++){
//							if(Data.driverRideRequests.get(i).engagementId.equalsIgnoreCase(arg0.getTitle())){
//								index = i;
//								break;
//							}
//						}
//						
//						if(index != -1){
//							Data.dCustomerId = Data.driverRideRequests.get(index).customerId;
//							Data.dEngagementId = Data.driverRideRequests.get(index).engagementId;
//							
//							Log.i("map cleared", "map.setOnMarkerClickListener");
//							map.clear();
//							
//							Data.dCustLatLng = Data.driverRideRequests.get(index).latLng;
//							
//							MarkerOptions markerOptions = new MarkerOptions();
//							markerOptions.title(Data.driverRideRequests.get(index).engagementId);
//							markerOptions.snippet("");
//							markerOptions.position(Data.driverRideRequests.get(index).latLng);
//							markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPassengerMarkerBitmap()));
//							
//							map.addMarker(markerOptions);
//							
//							driverNewRideRequestRl.setVisibility(View.GONE);
//							
//							driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
//							switchDriverScreen(driverScreenMode);
//						}
//						
//						
//						
//						
//						return true;
//					}
					
					
				}
			});
			
			
			
			
			
			
			
			
			
			new MapStateListener(map, mapFragment, this) {
				  @Override
				  public void onMapTouched() {
				    // Map touched
				  }

				  @Override
				  public void onMapReleased() {
				    // Map released
				  }

				  @Override
				  public void onMapUnsettled() {
				    // Map unsettled
					  if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  
						  if(getDistanceTimeAddress != null){
							  getDistanceTimeAddress.cancel(true);
							  getDistanceTimeAddress = null;
						  }
					  }
				  }

				  
				  
				  @Override
				  public void onMapSettled() {
				    // Map settled
					  if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  if(myLocation != null){
							  if(!dontCallRefreshDriver){
								  getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
										  myLocation.getLongitude()), false);
								  getDistanceTimeAddress.execute();
							  }
						  }
					  }
				  }
				};
			
			
			
			
			
			myLocationBtn.setOnClickListener(mapMyLocationClick);
			driverInitialMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverRequestAcceptMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverStartRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverEndRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			
		}
		
		
		
		
		
		
		
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		
		setUserData();
		
		

		
		if(userMode == null){
			userMode = UserMode.PASSENGER;
		}
		
		if(passengerScreenMode == null){
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
		}
		
		if(driverScreenMode == null){
			driverScreenMode = DriverScreenMode.D_INITIAL;
		}
		
		if(userMode == UserMode.DRIVER){
			driverModeToggle.setImageResource(R.drawable.on);
		}
		else{
			driverModeToggle.setImageResource(R.drawable.off);
		}
		
		switchUserScreen(userMode);
		
		startUIAfterGettingUserStatus();
		
		
		
		
		
		
		
//		getAllFavoriteAsync(HomeActivity.this);
		
		
		
		gpsListener = new CustomLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME_PERIOD, 0, gpsListener);
	    
		
		
		Database2 database2 = new Database2(HomeActivity.this);
		String jugnooOn = database2.getJugnooOn();
		
		
		if("on".equalsIgnoreCase(jugnooOn)){
			jugnooDriverMode = JugnooDriverMode.ON;
		}
		else{
			jugnooDriverMode = JugnooDriverMode.OFF;
		}
		if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
			changeJugnooON(jugnooDriverMode);
		}
		
		changeExceptionalDriverUI();
		
		database2.insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Data.SERVER_URL);
		database2.close();
	}
	
	
	public void startUIAfterGettingUserStatus(){
		if(userMode == UserMode.PASSENGER){
			if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
				initiateRequestRide(false);
			}
			else if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
				switchPassengerScreen(passengerScreenMode);
			}
			else{
				switchPassengerScreen(passengerScreenMode);
			}
		}
		else if(userMode == UserMode.DRIVER){
			switchDriverScreen(driverScreenMode);
		}
	}
	
	public void initiateRequestRide(boolean newRequest){
		
		if(newRequest){
			Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			Data.cSessionId = "";
			Data.cEngagementId = "";
		}
		else{
			if(myLocation == null){
				Data.pickupLatLng = new LatLng(0, 0);
			}
			else{
				Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			}
			
			Data.cEngagementId = "";
		}
		
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_C_SESSION_ID, Data.cSessionId);
		editor.putString(Data.SP_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_LAST_LATITUDE, ""+Data.pickupLatLng.latitude);
		editor.putString(Data.SP_LAST_LONGITUDE, ""+Data.pickupLatLng.longitude);
		editor.commit();

		cancelTimerUpdateDrivers();
		
		HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
		switchPassengerScreen(passengerScreenMode);
		
		customerCancelPressed = false;
		
		startTimerRequestRide();
	}
	
	
	
	
	Handler jugnooDriverOnHandler;
	Runnable jugnooDriverOnRunnable;
	
	public void changeJugnooON(JugnooDriverMode mode){
		
		try{
			if(jugnooDriverOnHandler != null && jugnooDriverOnRunnable != null){
				jugnooDriverOnHandler.removeCallbacks(jugnooDriverOnRunnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		Database2 database2 = new Database2(HomeActivity.this);
		
		if(mode == JugnooDriverMode.ON){
			jugnooDriverMode = JugnooDriverMode.ON;
			jugnooONToggle.setImageResource(R.drawable.on);
			database2.updateJugnooOn("on");
			
			stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
			startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
			
			Intent restartService = new Intent(getApplicationContext(), DriverLocationUpdateService.class);
			restartService.setPackage(getPackageName());
			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmService.cancel(restartServicePI);
			
		}
		else{
			jugnooDriverMode = JugnooDriverMode.OFF;
			jugnooONToggle.setImageResource(R.drawable.off);
			database2.updateJugnooOn("off");
			
			stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
			
			
			Intent restartService = new Intent(getApplicationContext(), DriverLocationUpdateService.class);
			restartService.setPackage(getPackageName());
			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + SERVICE_RESTART_TIMER, restartServicePI);
			
			
			jugnooDriverOnHandler = null;
			jugnooDriverOnRunnable = null;
			
			jugnooDriverOnHandler = new Handler();
			jugnooDriverOnRunnable = new Runnable() {
				
				@Override
				public void run() {
					changeJugnooONUI(JugnooDriverMode.ON);
				}
			};
			jugnooDriverOnHandler.postDelayed(jugnooDriverOnRunnable, SERVICE_RESTART_TIMER);
			
			database2.updateDriverServiceRestartOnReboot("yes");
			
			sendNullLocationToServerForDriver();
			
		}
		
		database2.close();
		
		
	}
	
	
	public void sendNullLocationToServerForDriver(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
					nameValuePairs.add(new BasicNameValuePair("latitude", "0"));
					nameValuePairs.add(new BasicNameValuePair("longitude", "0"));
					nameValuePairs.add(new BasicNameValuePair("device_token", Data.deviceToken));
					
					Log.e("nameValuePairs in sending null loc","="+nameValuePairs);
					
					HttpRequester simpleJSONParser = new HttpRequester();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL+"/update_driver_location", nameValuePairs);
					
					Log.e("result ","="+result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	public void changeExceptionalDriverUI(){
		
		if(exceptionalDriver == ExceptionalDriver.YES){
			jugnooONRl.setVisibility(View.VISIBLE);
			driverModeRl.setVisibility(View.GONE);
			logoutRl.setVisibility(View.GONE);
		}
		else{
			jugnooONRl.setVisibility(View.GONE);
			driverModeRl.setVisibility(View.VISIBLE);
			logoutRl.setVisibility(View.VISIBLE);
		}
		
	}
	
	
	
	public void changeJugnooONUI(JugnooDriverMode mode){
		if(mode == JugnooDriverMode.ON){
			jugnooDriverMode = JugnooDriverMode.ON;
			jugnooONToggle.setImageResource(R.drawable.on);
		}
		else{
			jugnooDriverMode = JugnooDriverMode.OFF;
			jugnooONToggle.setImageResource(R.drawable.off);
		}
	}
	
	
	
	OnClickListener mapMyLocationClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(myLocation != null){
				if(map.getCameraPosition().zoom < 12){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12));
				}
				else if(map.getCameraPosition().zoom < 17){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17));
				}
				else{
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	public void setUserData(){
		try{
			
			userName.setText(Data.userData.userName);
			
			Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(profileImg);}catch(Exception e){}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	
	public void switchUserScreen(UserMode mode){
		
		if(getDistanceTimeAddress != null){
			getDistanceTimeAddress.cancel(true);
			getDistanceTimeAddress = null;
		}
		
		Database2 database2 = new Database2(HomeActivity.this);
		
		switch(mode){
		
			case DRIVER:
				cancelTimerUpdateDrivers();
				
				database2.updateUserMode(Database2.UM_DRIVER);
				
				passengerMainLayout.setVisibility(View.GONE);
				driverMainLayout.setVisibility(View.VISIBLE);
				
//				favBtn.setVisibility(View.GONE);
				
				break;
			
				
				
				
			case PASSENGER:
				
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				database2.updateUserMode(Database2.UM_PASSENGER);
				
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
//				favBtn.setVisibility(View.VISIBLE);
				
				break;
			
				
				
				
			default:
				database2.updateUserMode(Database2.UM_PASSENGER);
		
		}
		
		database2.close();
		
	}
	
	
	
	public void updateDriverServiceFast(String choice){
		Database2 database = new Database2(HomeActivity.this);
		database.updateDriverServiceFast(choice);
		database.close();
	}
	
	
	public void switchDriverScreen(DriverScreenMode mode){
		if(userMode == UserMode.DRIVER){
			
			initializeFusedLocationFetchers();
			
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
			
			saveDataOnPause(false);
			
		if(mode == DriverScreenMode.D_RIDE_END){
			startAutomaticReviewHandler();
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			
			
			
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			reviewWaitValue.setText(waitTime+" min");
			reviewFareValue.setText("Rs. "+decimalFormat.format(totalFare));
			
			reviewRatingText.setText("Customer Rating");
			reviewRatingBar.setRating(0);
			
			reviewUserName.setText(Data.assignedCustomerInfo.name);
			
			Data.assignedCustomerInfo.image = Data.assignedCustomerInfo.image.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.assignedCustomerInfo.image).skipMemoryCache().transform(new BlurTransform()).into(reviewUserImgBlured);}catch(Exception e){}
			try{Picasso.with(HomeActivity.this).load(Data.assignedCustomerInfo.image).skipMemoryCache().transform(new CircleTransform()).into(reviewUserImage);}catch(Exception e){}
			
			reviewSubmitBtn.setText("OK");
			
			reviewRatingBarRl.setVisibility(View.GONE);
			endRideInfoRl.setVisibility(View.VISIBLE);
			
			jugnooRideOverText.setText("The Jugnoo ride is over.");
			takeFareText.setText("Please take the fare as shown above from the customer.");
			
			
		}
		else{
			stopAutomaticReviewhandler();
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey));
		}
		
		
		switch(mode){
		
			case D_INITIAL:
				
				updateDriverServiceFast("no");
				
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				cancelCustomerPathUpdateTimer();
				
				if(map != null){
					map.clear();
				}
				
				getAndShowAllDriverRequests(HomeActivity.this);
				
				break;
				
				
			case D_REQUEST_ACCEPT:
				
				updateDriverServiceFast("no");
				
				if(!isServiceRunning(HomeActivity.this, DriverLocationUpdateService.class.getName())){
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				}
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.VISIBLE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				
			
				break;
				
				
				
			case D_START_RIDE:
				
				updateDriverServiceFast("yes");
				
				changeJugnooON(JugnooDriverMode.ON);

				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				if(map != null){
					map.clear();
					
					markerOptionsCustomerPickupLocation = null;
					
					markerOptionsCustomerPickupLocation = new MarkerOptions();
					markerOptionsCustomerPickupLocation.title(Data.dEngagementId);
					markerOptionsCustomerPickupLocation.snippet("");
					markerOptionsCustomerPickupLocation.position(Data.dCustLatLng);
					markerOptionsCustomerPickupLocation.icon(BitmapDescriptorFactory.fromBitmap(createPassengerMarkerBitmap()));
					
					map.addMarker(markerOptionsCustomerPickupLocation);
				}
				
				
				double rateingD = 4;
				try{
					rateingD = Double.parseDouble(Data.assignedCustomerInfo.rating);
				} catch(Exception e){
					e.printStackTrace();
				}
				
				driverPassengerName.setText(Data.assignedCustomerInfo.name);
				driverPassengerRatingValue.setText(decimalFormat.format(rateingD) + " "+getResources().getString(R.string.rating));
				
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				
				driverStartRideMainRl.setVisibility(View.VISIBLE);
				driverInRideMainRl.setVisibility(View.GONE);
				
				
				startCustomerPathUpdateTimer();
				
				
				break;
				
				
				
				
			case D_IN_RIDE:
				
				updateDriverServiceFast("no");
				
				cancelCustomerPathUpdateTimer();
				
				startMapAnimateTimer();
				
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				long timeR = (long)HomeActivity.previousRideTime;
				int hR = (int) (timeR / 3600000);
				int mR = (int) (timeR - hR * 3600000) / 60000;
				int sR = (int) (timeR - hR * 3600000 - mR * 60000) / 1000;
				String hhR = hR < 10 ? "0" + hR : hR + "";
				String mmR = mR < 10 ? "0" + mR : mR + "";
				String ssR = sR < 10 ? "0" + sR : sR + "";
				rideTimeChronometer.setText(hhR + ":" + mmR + ":" + ssR);
				
				rideTimeChronometer.eclipsedTime = (long)HomeActivity.previousRideTime;
				rideTimeChronometer.start();
				
				
				long time = (long)HomeActivity.previousWaitTime;
				int h = (int) (time / 3600000);
				int m = (int) (time - h * 3600000) / 60000;
				int s = (int) (time - h * 3600000 - m * 60000) / 1000;
				String hh = h < 10 ? "0" + h : h + "";
				String mm = m < 10 ? "0" + m : m + "";
				String ss = s < 10 ? "0" + s : s + "";
				waitChronometer.setText(hh + ":" + mm + ":" + ss);
				
				waitChronometer.eclipsedTime =  (long)HomeActivity.previousWaitTime;
				
				
				
				if(map != null){
					map.clear();
				}
				
				driverPassengerName.setText(Data.assignedCustomerInfo.name);
				
			
				
				
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				
				driverStartRideMainRl.setVisibility(View.GONE);
				driverInRideMainRl.setVisibility(View.VISIBLE);
				
			
				break;
				
				
			case D_RIDE_END:
				
				updateDriverServiceFast("no");
				
				cancelMapAnimateTimer();

				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				
			
				break;
				
				
			
			default:
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
		
		}
		}
		
	}
	
	
	
	public void switchPassengerScreen(PassengerScreenMode mode){
		if(userMode == UserMode.PASSENGER){
			
			initializeFusedLocationFetchers();
			
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
		
			saveDataOnPause(false);
			
		if(mode == PassengerScreenMode.P_RIDE_END){
			startAutomaticReviewHandler();
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			double totalDistanceInKm = Data.totalDistance;
			
			
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			reviewWaitValue.setText(Data.waitTime+" min");
			reviewFareValue.setText("Rs. "+decimalFormat.format(Data.totalFare));
			
			reviewRatingText.setText("Driver Rating");
			reviewRatingBar.setRating(0);
			
			reviewUserName.setText(Data.assignedDriverInfo.name);
			
			Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new BlurTransform()).into(reviewUserImgBlured);}catch(Exception e){}
			try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new CircleTransform()).into(reviewUserImage);}catch(Exception e){}
			
			reviewSubmitBtn.setText("OK");
			
			reviewRatingBarRl.setVisibility(View.GONE);
			endRideInfoRl.setVisibility(View.VISIBLE);
			
			jugnooRideOverText.setText("The Jugnoo ride is over.");
			takeFareText.setText("Please pay the fare as shown above to the driver.");
			
		}
		else{
			stopAutomaticReviewhandler();
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey));
		}
		
		
		
		
		
		switch(mode){
		
			case P_INITIAL:

				requestRideBtn.setText(REQUEST_RIDE_BTN_NORMAL_TEXT);
				requestRideBtn.setBackgroundResource(R.drawable.blue_btn_selector);
				
				cancelDriverLocationUpdateTimer();
				cancelTimerRequestRide();
				
				try{pickupLocationMarker.remove();} catch(Exception e){}
				try{driverLocationMarker.remove();} catch(Exception e){}
				
				
		        GCMIntentService.clearNotifications(getApplicationContext());
				
				if(getDistanceTimeAddress != null){
					getDistanceTimeAddress.cancel(true);
					getDistanceTimeAddress = null;
				}
				
				
//				if(myLocation != null){
//					getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
//							myLocation.getLongitude()), false);
//					getDistanceTimeAddress.execute();
//				}
				
				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
//				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				searchBarLayout.setVisibility(View.GONE);
				
				nearestDriverRl.setVisibility(View.VISIBLE);
				initialCancelRideBtn.setVisibility(View.GONE);
				textViewAssigningInProgress.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.VISIBLE);
				
				Log.e("Data.latitude", "="+Data.latitude);
				Log.e("myLocation", "="+myLocation);
				
				if (Data.latitude != 0 && Data.longitude != 0) {
					showDriverMarkersAndPanMap(new LatLng(Data.latitude, Data.longitude));
				} else if (myLocation != null) {
					showDriverMarkersAndPanMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				}
				
				dontCallRefreshDriver = true;
				
				startTimerUpdateDrivers();
				
				break;
				
				
			case P_ASSIGNING:
				
				requestRideBtn.setText(REQUEST_RIDE_BTN_ASSIGNING_DRIVER_TEXT);
				requestRideBtn.setBackgroundResource(R.drawable.blue_btn_normal);
				
				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
//				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				searchBarLayout.setVisibility(View.GONE);
				
				if(map != null){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.pickupLatLng);
					markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));
					
					pickupLocationMarker = map.addMarker(markerOptions);
				}
				
				
				nearestDriverRl.setVisibility(View.GONE);
				initialCancelRideBtn.setVisibility(View.VISIBLE);
				textViewAssigningInProgress.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.VISIBLE);

				cancelTimerUpdateDrivers();
				
				break;
				
		
			case P_SEARCH:

				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
//				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.VISIBLE);
				
				
				menuBtn.setVisibility(View.GONE);
				jugnooLogo.setVisibility(View.GONE);
				backBtn.setVisibility(View.VISIBLE);
				title.setVisibility(View.VISIBLE);
//				favBtn.setVisibility(View.GONE);
				
				title.setText(getResources().getString(R.string.search_results));
				
				searchListAdapter.notifyDataSetChanged();

				
				break;
				
				
				
			case P_REQUEST_FINAL:
				
				if(map != null){
					
					if(Data.pickupLatLng == null){
						Log.e("Data.mapTarget","=null");
						SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
						String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
						String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
						Data.pickupLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					}
					else{
						Log.e("Data.mapTarget","=not null");
					}
					
					Log.e("Data.mapTarget","="+Data.pickupLatLng);
					Log.e("Data.assignedDriverInfo.latLng","="+Data.assignedDriverInfo.latLng);
					
					map.clear();
					
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.pickupLatLng);
					markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));
					
					pickupLocationMarker = map.addMarker(markerOptions);
					
					MarkerOptions markerOptions1 = new MarkerOptions();
					markerOptions1.title("driver position");
					markerOptions1.snippet("");
					markerOptions1.position(Data.assignedDriverInfo.latLng);
					markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(createCarMarkerBitmap()));
					
					driverLocationMarker = map.addMarker(markerOptions1);
					
					Log.i("marker added", "REQUEST_FINAL");
				}
				
				

				driverName.setText(Data.assignedDriverInfo.name);
				
				if("".equalsIgnoreCase(Data.assignedDriverInfo.durationToReach)){
					driverTime.setText("");
				}
				else{
					if(Locale.getDefault().getLanguage().equalsIgnoreCase("hi")){
						driverTime.setText(Data.assignedDriverInfo.durationToReach + " "+ getResources().getString(R.string.will_arrive_in));
	    	 		}
	    	 		else{
	    	 			driverTime.setText(getResources().getString(R.string.will_arrive_in) +" "+ Data.assignedDriverInfo.durationToReach);
	    	 		}
				}
				
				Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
				try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new RoundBorderTransform()).into(driverImage);}catch(Exception e){}
				
				
				Data.assignedDriverInfo.carImage = Data.assignedDriverInfo.carImage.replace("http://graph.facebook", "https://graph.facebook");
				try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.carImage).skipMemoryCache().transform(new RoundBorderTransform()).into(driverCarImage);}catch(Exception e){}
				
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
//				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				driverTime.setVisibility(View.VISIBLE);
				inRideRideInProgress.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.GONE);
				
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						driverTime.setVisibility(View.GONE);
					}
				}, 60000);

				startDriverLocationUpdateTimer();
				
				
				break;
				
				
				
			case P_IN_RIDE:
				
				
				cancelDriverLocationUpdateTimer();
				
				startMapAnimateTimer();
				
				if(map != null){
					map.clear();
				}
				
				driverName.setText(Data.assignedDriverInfo.name);
				
				Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
				try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new RoundBorderTransform()).into(driverImage);}catch(Exception e){}
				
				Data.assignedDriverInfo.carImage = Data.assignedDriverInfo.carImage.replace("http://graph.facebook", "https://graph.facebook");
				try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.carImage).skipMemoryCache().transform(new RoundBorderTransform()).into(driverCarImage);}catch(Exception e){}
				
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
//				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				driverTime.setVisibility(View.GONE);
				inRideRideInProgress.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.GONE);

				
				break;
				
			case P_RIDE_END:
				
				cancelMapAnimateTimer();
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
//				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.GONE);

				
				break;
				
				
			default:

				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.GONE);
//				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.VISIBLE);

				
		}
		
		
		}
		
	}
	
	
	/**
	 * Hides keyboard
	 * @param context
	 */
	public void hideSoftKeyboard() {
		try{
			InputMethodManager mgr = (InputMethodManager) HomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
		} catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	
	
	
	
	
	
	class CustomLocationListener implements LocationListener{
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
		@Override
		public void onProviderEnabled(String provider) {
		}
		
		@Override
		public void onProviderDisabled(String provider) {
		}
		
		@Override
		public void onLocationChanged(Location location) {
			drawLocationChanged(location);
		}
		
	}
	
	
	LocationListener gpsListener = new CustomLocationListener();
	
	
//	static String LOG_FILE = "LOGFILE_Loc";
//	static String APP_NAME = "Jugnoo";
//	
//	static void writeLogToFile(final String response) {
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					String fileName = Environment.getExternalStorageDirectory() + "/" + APP_NAME + "_" + LOG_FILE + ".txt";
//					File gpxfile = new File(fileName);
//					
//					if(!gpxfile.exists()){
//						gpxfile.createNewFile();
//					}
//					
//					FileWriter writer = new FileWriter(gpxfile, true);
//					writer.append("\n" + response);
//					writer.flush();
//					writer.close();
//					
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
//		}).start();
//
//	}

	
	
	
	
	void buildAlertMessageNoGps() {
		if(!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(gpsDialogAlert != null && gpsDialogAlert.isShowing()){
		    }
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("The app needs active GPS connection. Enable it from Settings.")
			           .setCancelable(false)
			           .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
			               public void onClick(final DialogInterface dialog, final int id) {
			                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			               }
			           })
			           ;
			    gpsDialogAlert = null;
			    gpsDialogAlert = builder.create();
			    gpsDialogAlert.show();
			}
		}
		else{
			if(gpsDialogAlert != null && gpsDialogAlert.isShowing()){
				gpsDialogAlert.dismiss();
		    }
		}
	}
	
	
	
	@Override
	protected void onResume() {
		
		super.onResume();
	    
		if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
			  if(myLocation != null){
				  if(!dontCallRefreshDriver){
					  getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
							  myLocation.getLongitude()), false);
					  getDistanceTimeAddress.execute();
				  }
			  }
		  }
	    
	    if(FavoriteActivity.zoomToMap){
	    	FavoriteActivity.zoomToMap = false;
	    	if(map != null){
	    		map.animateCamera(CameraUpdateFactory.newLatLng(FavoriteActivity.zoomLatLng), 1000, null);
	    	}
	    }
	    
	    try{
	    	if(userMode == UserMode.PASSENGER){
	    		if(locationManager == null){
	    			gpsListener = new CustomLocationListener();
	    			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME_PERIOD, 0, gpsListener);
	    		}
	    	}
	    	else if(userMode == UserMode.DRIVER){
	    		if(locationManager == null){
		    		gpsListener = new CustomLocationListener();
		    		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME_PERIOD, 0, gpsListener);
		    	}
	    	}
	    } catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    initializeFusedLocationFetchers();
	    
	    
	    
	}
	
	
//	public void updateTextViews(){
//		Resources resources = getResources();
//		
//		driverModeText.setText(resources.getString(R.string.driver_mode));
//		jugnooONText.setText(resources.getString(R.string.jugnoo_on));
//		driverTypeText.setText(resources.getString(R.string.driver_type));
//		inviteFriendText.setText(resources.getString(R.string.invite_friends));
//		bookingsText.setText(resources.getString(R.string.rides));
//		aboutText.setText(resources.getString(R.string.about));
//		supportText.setText(resources.getString(R.string.support));
//		sosText.setText(resources.getString(R.string.sos));
//		languagePrefrencesText.setText(resources.getString(R.string.language_preferences));
//		logoutText.setText(resources.getString(R.string.logout));
//		
//		searchEt.setHint(resources.getString(R.string.search));
//		
//		
//		
//		
//		
//		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
//			requestRideBtn.setText(resources.getString(R.string.request_ride));
//		}
//		else if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
//			requestRideBtn.setText(resources.getString(R.string.assigning_driver));
//		}
//		
//		initialCancelRideBtn.setText(resources.getString(R.string.cancel_ride));
//		
//		try{
//		if(Data.assignedDriverInfo != null){
//			if(Locale.getDefault().getLanguage().equalsIgnoreCase("hi")){
//				driverTime.setText(Data.assignedDriverInfo.durationToReach + " "+ resources.getString(R.string.will_arrive_in));
//	 		}
//	 		else{
//	 			driverTime.setText(resources.getString(R.string.will_arrive_in) +" "+ Data.assignedDriverInfo.durationToReach);
//	 		}
//		}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		callDriverBtn.setText(resources.getString(R.string.call_driver));
//		
//		inRideRideInProgress.setText(resources.getString(R.string.ride_in_progress));
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		driverAcceptRideBtn.setText(resources.getString(R.string.accept_ride));
//		driverCancelRequestBtn.setText(resources.getString(R.string.cancel_request));
//		
//
//		if(Data.assignedCustomerInfo != null){
//			double rateingD = 4;
//			try{
//				rateingD = Double.parseDouble(Data.assignedCustomerInfo.rating);
//			} catch(Exception e){
//				e.printStackTrace();
//			}
//			driverPassengerRatingValue.setText(decimalFormat.format(rateingD) + " "+resources.getString(R.string.rating));
//		}
//		
//		driverPassengerCallText.setText(resources.getString(R.string.call));
//		
//		driverStartRideBtn.setText(resources.getString(R.string.start_ride));
//		driverCancelRideBtn.setText(resources.getString(R.string.cancel_ride));
//		
//		
//		
//		
//		
//		driverIRDistanceText.setText(resources.getString(R.string.distance));
//		driverIRFareText.setText(resources.getString(R.string.fare));
//		driverRideTimeText.setText(resources.getString(R.string.ride_time));
//		
//		
//		
//		
//		
//		try {
//			if(waitStart == 2){ 
//				driverWaitText.setText(resources.getString(R.string.start_wait));
//			}
//			else{
//				if(waitStart == 1){
//					driverWaitText.setText(resources.getString(R.string.stop_wait));
//				}
//				else if(waitStart == 0){
//					driverWaitText.setText(resources.getString(R.string.start_wait));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		driverEndRideBtn.setText(resources.getString(R.string.end_ride));
//		
//		
//		
//		
//		
//		
//		reviewReachedDestinationText.setText(resources.getString(R.string.reached_destination));
//		reviewDistanceText.setText(resources.getString(R.string.distance));
//		reviewWaitText.setText(resources.getString(R.string.wait_time));
//		reviewFareText.setText(resources.getString(R.string.fare));
//		
//		reviewSubmitBtn.setText(resources.getString(R.string.submit));
//		
//		
//		
//		if(userMode == UserMode.DRIVER){
//			reviewRatingText.setText(resources.getString(R.string.customer_rating));
//		}
//		else{
//			reviewRatingText.setText(resources.getString(R.string.driver_rating));
//		}
//		
//		
//		
//		
//		
//		
//	}
	
	
	
	public void saveDataOnPause(final boolean stopWait){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
		
        try {
			if(userMode == UserMode.DRIVER){
				
				SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				Editor editor = pref.edit();
				
				if(driverScreenMode == DriverScreenMode.D_START_RIDE){
					
					editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_START_RIDE);
					
					editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
					editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
					
					editor.putString(Data.SP_D_LATITUDE, ""+Data.dCustLatLng.latitude);
					editor.putString(Data.SP_D_LONGITUDE, ""+Data.dCustLatLng.longitude);
					
					editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
					editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
					editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
					editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
					
					
				}
				else if(driverScreenMode == DriverScreenMode.D_IN_RIDE){
					
					if(stopWait){
						if(waitChronometer.isRunning){
				    		runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									try{
										Log.e("waitChronometer.stop()","in onPause on click");
						        		waitChronometer.stop();
						        		driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
						        		driverWaitText.setText(getResources().getString(R.string.start_wait));
										waitStart = 0;
					        		} catch(Exception e){
					        			e.printStackTrace();
					        		}
								}
							});
				    		try{
				    			startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 0);
				    		} catch(Exception e){
				    			e.printStackTrace();
				    		}
						}
						
						if(rideTimeChronometer.isRunning){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try{
										rideTimeChronometer.stop();
									} catch(Exception e){
					        			e.printStackTrace();
					        		}
								}
							});
						}
						
					}
					
					editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_IN_RIDE);
					
					editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
					editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
					
					editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
					editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
					editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
					editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
					
					long elapsedMillis = waitChronometer.eclipsedTime;
			    	
					editor.putString(Data.SP_TOTAL_DISTANCE, ""+totalDistance);
					editor.putString(Data.SP_WAIT_TIME, ""+elapsedMillis);
					
					Log.e("Data.SP_WAIT_TIME", "=="+elapsedMillis);
					
					long elapsedRideTime = rideTimeChronometer.eclipsedTime;
					editor.putString(Data.SP_RIDE_TIME, ""+elapsedRideTime);
					
					Log.e("Data.SP_RIDE_TIME", "=="+elapsedRideTime);
					
					if(HomeActivity.myLocation != null){
						editor.putString(Data.SP_LAST_LATITUDE, ""+HomeActivity.myLocation.getLatitude());
			    		editor.putString(Data.SP_LAST_LONGITUDE, ""+HomeActivity.myLocation.getLongitude());
					}
					else{
						editor.putString(Data.SP_LAST_LATITUDE, "0");
			    		editor.putString(Data.SP_LAST_LONGITUDE, "0");
					}
					
				}
				else{
					editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");
				}
				
				
				editor.commit();
				
			}
			else if(userMode == UserMode.PASSENGER){
			    
				
				SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				Editor editor = pref.edit();
				
				if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
					
					editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_REQUEST_FINAL);
					
					editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
					editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
					editor.putString(Data.SP_C_LATITUDE, ""+Data.assignedDriverInfo.latLng.latitude);
					editor.putString(Data.SP_C_LONGITUDE, ""+Data.assignedDriverInfo.latLng.longitude);
					editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
					editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
					editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
					editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
					editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
					editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
					editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
					
					
				}
				else if(passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
					
					editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_IN_RIDE);
					
					editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
					editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
					editor.putString(Data.SP_C_LATITUDE, ""+Data.assignedDriverInfo.latLng.latitude);
					editor.putString(Data.SP_C_LONGITUDE, ""+Data.assignedDriverInfo.latLng.longitude);
					editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
					editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
					editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
					editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
					editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
					editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
					editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
					
					
					editor.putString(Data.SP_TOTAL_DISTANCE, ""+totalDistance);
					
					if(HomeActivity.myLocation != null){
						editor.putString(Data.SP_LAST_LATITUDE, ""+HomeActivity.myLocation.getLatitude());
			    		editor.putString(Data.SP_LAST_LONGITUDE, ""+HomeActivity.myLocation.getLongitude());
					}
					else{
						editor.putString(Data.SP_LAST_LATITUDE, "0");
			    		editor.putString(Data.SP_LAST_LONGITUDE, "0");
					}
					
				
				}
				else{
					editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");
				}
				
				editor.commit();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			}
		}).start();
	
	}
	
	
	
	
	@Override
	protected void onPause() {
		
		GCMIntentService.clearNotifications(getApplicationContext());
		
		saveDataOnPause(false);
		
		
		try{
			if(userMode == UserMode.PASSENGER){
				if(locationManager != null && gpsListener != null){
					locationManager.removeUpdates(gpsListener);
					gpsListener = null;
					locationManager = null;
				}
			}
			else if(userMode == UserMode.DRIVER){
	    		if(driverScreenMode != DriverScreenMode.D_IN_RIDE){
	    			if(locationManager != null && gpsListener != null){
						locationManager.removeUpdates(gpsListener);
						gpsListener = null;
						locationManager = null;
					}
	    		}
	    	}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		destroyFusedLocationFetchers();
		
		super.onPause();
		
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		try{
			if(userMode == UserMode.DRIVER){
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE || driverScreenMode == DriverScreenMode.D_START_RIDE){
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				}
				else{
					super.onBackPressed();
				}
			}
			else if(userMode == UserMode.PASSENGER){
				if(passengerScreenMode == PassengerScreenMode.P_SEARCH){
					passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
				}
				else{
					super.onBackPressed();
				}
			}
			else{
				super.onBackPressed();
			}
		} catch(Exception e){
			e.printStackTrace();
			super.onBackPressed();
		}
	}
	
	    
	
	@Override
    public void onDestroy() {
        try{
        	
        	saveDataOnPause(true);
        	
    		GCMIntentService.clearNotifications(HomeActivity.this);
    		GCMIntentService.stopRing();
    		
    		try{
    			if(locationManager != null && gpsListener != null){
    				locationManager.removeUpdates(gpsListener);
    				gpsListener = null;
    				locationManager = null;
    			}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		
    		destroyFusedLocationFetchers();
	        
	        ASSL.closeActivity(drawerLayout);
	        cancelTimerUpdateDrivers();
	        
	        appInterruptHandler = null;
	        
	        System.gc();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        super.onDestroy();
    }
	
	
	
	
	
	
	public void drawLocationChanged(Location location){
		
		try {
			if(map != null){
				zoomToCurrentLocationAtFirstLocationFix(location);
			
			HomeActivity.myLocation = location;
			updatePickupLocation(location);
			
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE || passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
					
					saveDataOnPause(false);
					
					final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
					if(lastLocation != null){
						
						final LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
						
						double displacement = distance(lastLatLng, currentLatLng);
						Log.e("displacement", "="+displacement);
						
						if(displacement < MAX_DISPLACEMENT_THRESHOLD){
							
							totalDistance = totalDistance + displacement;
							map.addPolyline(new PolylineOptions()
					        .add(lastLatLng, currentLatLng)
					        .width(5)
					        .color(MAP_PATH_COLOR).geodesic(true));
							
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									Log.e("lastLatLng","="+lastLatLng);
									Log.e("currentLatLng","="+currentLatLng);
									Database database = new Database(HomeActivity.this);
									database.insertPolyLine(lastLatLng, currentLatLng);
									database.close();
								}
							}).start();
							
							updateDistanceFareTexts();
							
						}
						else{
							new CreatePathAsyncTask(lastLatLng, currentLatLng, displacement).execute();
						}
						
					}
					else if(lastLocation == null){
						
						if(totalDistance == -1){
							totalDistance = 0;
							
							MarkerOptions markerOptions = new MarkerOptions();
							markerOptions.snippet("");
							markerOptions.title("start ride location");
							markerOptions.position(currentLatLng);
							markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));
							map.addMarker(markerOptions);

							updateDistanceFareTexts();
							
						}
						else{
							try{
								displayOldPath();
								
								double displacement = distance(Data.startRidePreviousLatLng, currentLatLng);
								Log.e("displacement", "="+displacement);
								
								if(displacement < MAX_DISPLACEMENT_THRESHOLD){
									totalDistance = totalDistance + displacement;
									map.addPolyline(new PolylineOptions()
							        .add(Data.startRidePreviousLatLng, currentLatLng)
							        .width(5)
							        .color(MAP_PATH_COLOR).geodesic(true));
									
									new Thread(new Runnable() {
										
										@Override
										public void run() {
											Log.e("Data.startRidePreviousLatLng","="+Data.startRidePreviousLatLng);
											Log.e("currentLatLng","="+currentLatLng);
											Database database = new Database(HomeActivity.this);
											database.insertPolyLine(Data.startRidePreviousLatLng, currentLatLng);
											database.close();
										}
									}).start();

									updateDistanceFareTexts();
									
								}
								else{
									new CreatePathAsyncTask(Data.startRidePreviousLatLng, currentLatLng, displacement).execute();
								}
								
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						
						
					}
					
					
					lastLocation = location;
					
//					Log.writeLogToFile("LatLngDataFile", "@\""+lastLocation.getLatitude()+"\",@\""+lastLocation.getLongitude()+"\"");
					
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	public void updateDistanceFareTexts(){
		double totalDistanceInKm = Math.abs(totalDistance/1000.0);
		driverIRDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm));
		
		
		double fare = 0;//fareFixed + ((totalDistanceInKm <= fareThresholdDistance) ? (0) : ((totalDistanceInKm - fareThresholdDistance)* farePerKm));
		if(totalDistanceInKm <= fareThresholdDistance){
			fare = fareFixed;
		}
		else{
			fare = fareFixed + ((totalDistanceInKm - fareThresholdDistance) * farePerKm);
		}
		fare = Math.ceil(fare);
		driverIRFareValue.setText(""+decimalFormat.format(fare));
	}
	
	
	
	public void displayOldPath(){
		
		Database database = new Database(getApplicationContext());
		ArrayList<Pair<LatLng, LatLng>> path = database.getSavedPath();
		database.close();
		
		LatLng firstLatLng = null;
		
		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.width(5);
		polylineOptions.color(MAP_PATH_COLOR);
		polylineOptions.geodesic(true);
		
		for(Pair<LatLng, LatLng> pair : path){
			LatLng src = pair.first;
            LatLng dest = pair.second;
            
			if(firstLatLng == null){
				firstLatLng = src;
			}
			
			polylineOptions.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));
		}
		
		map.addPolyline(polylineOptions);
		
		if(firstLatLng != null){
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.snippet("");
			markerOptions.title("start ride location");
			markerOptions.position(firstLatLng);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));
			map.addMarker(markerOptions);
		}
		
	}
	
	
	
	public Bitmap createCarMarkerBitmap(){
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		int width = (int)(70.0f * scale);
		int height = (int)(70.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = getResources().getDrawable(R.drawable.car_android);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	public Bitmap createPassengerMarkerBitmap(){
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		int width = (int)(50.0f * scale);
		int height = (int)(69.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = getResources().getDrawable(R.drawable.passenger);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	
	public Bitmap createPinMarkerBitmap(){
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		int width = (int)(44.0f * scale);
		int height = (int)(70.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = getResources().getDrawable(R.drawable.pin_ball);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	
	
	double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			double distanceFormated = Double.parseDouble(decimalFormat.format(distance));
			return distanceFormated;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
	

	
	//http://maps.googleapis.com/maps/api/directions/json?origin=30.7342187,76.78088307&destination=30.74571777,76.78635478&sensor=false&mode=driving&alternatives=false
	public String makeURLPath(LatLng source, LatLng destination){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(source.longitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&sensor=false&mode=driving&alternatives=false");
        return urlString.toString();
	}

	
	class CreatePathAsyncTask extends AsyncTask<Void, Void, String>{
	    String url;
	    double displacementToCompare;
	    LatLng source, destination;
	    CreatePathAsyncTask(LatLng source, LatLng destination, double displacementToCompare){
	    	this.source = source;
	    	this.destination = destination;
	        this.url = makeURLPath(source, destination);
	        this.displacementToCompare = displacementToCompare;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	return new HttpRequester().getJSONFromUrl(url);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        if(result!=null){
	            drawPath(result, displacementToCompare, source, destination);

				updateDistanceFareTexts();
				
	        }
	    }
	}
	
	
	public void drawPath(String result, double displacementToCompare, LatLng source, LatLng destination) {
	    try {
	    	
	    	 final JSONObject json = new JSONObject(result);
	    	
	    	JSONObject leg0 = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
	    	double distanceOfPath = leg0.getJSONObject("distance").getDouble("value");
	    	
	    	if(distanceOfPath <= (displacementToCompare*1.8)){														// distance would be approximately correct

		           JSONArray routeArray = json.getJSONArray("routes");
		           JSONObject routes = routeArray.getJSONObject(0);
		           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		           String encodedString = overviewPolylines.getString("points");
		           List<LatLng> list = decodePoly(encodedString);
			    	
			    	totalDistance = totalDistance + distanceOfPath;
			    	
			    	 Database database = new Database(HomeActivity.this);
			    	 
		           for(int z = 0; z<list.size()-1;z++){
		                LatLng src= list.get(z);
		                LatLng dest= list.get(z+1);
		                map.addPolyline(new PolylineOptions()
		                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
		                .width(5)
				        .color(MAP_PATH_COLOR).geodesic(true));
						database.insertPolyLine(src, dest);
		            }
		           database.close();
	    	}
	    	else{																									// displacement would be correct
	    		totalDistance = totalDistance + displacementToCompare;
	    		
	    		 Database database = new Database(HomeActivity.this);
	    		 map.addPolyline(new PolylineOptions()
	                .add(new LatLng(source.latitude, source.longitude), new LatLng(destination.latitude, destination.longitude))
	                .width(5)
			        .color(MAP_PATH_COLOR).geodesic(true));
					database.insertPolyLine(source, destination);
					database.close();
	    		
	    	}
	           
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	} 
	
	
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int bInt, shift = 0, result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlat = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlng = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lng += dlng;

	        LatLng pLatLng = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(pLatLng);
	    }
	    return poly;
	}
	
	
	
	
	class ViewHolderSearch {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class SearchListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderSearch holder;

		public SearchListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return searchResults.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			
			if (convertView == null) {
				
				holder = new ViewHolderSearch();
				convertView = mInflater.inflate(R.layout.search_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); holder.name.setTypeface(Data.regularFont(getApplicationContext()));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderSearch) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+searchResults.get(position).name + "\n" + searchResults.get(position).address);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderSearch) v.getTag();
					
					passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
					
					SearchResult searchResult = searchResults.get(holder.id);
					
					
					Log.e("searchResult.latLng ==",">"+searchResult.latLng);
					
					map.animateCamera(CameraUpdateFactory.newLatLng(searchResult.latLng), 1000, null);
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	
	class ViewHolderDriverRequest {
		TextView textViewRequestAddress, textViewRequestDistance, textViewRequestTime, textViewRequestNumber;
		RelativeLayout relative;
		int id;
	}

	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRequest holder;
		
		DateOperations dateOperations;
		
		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateOperations = new DateOperations();
		}
		
		public DateOperations getDateOperations(){
			if(dateOperations == null){
				dateOperations = new DateOperations();
			}
			return dateOperations;
		}

		@Override
		public int getCount() {
			return Data.driverRideRequests.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				
				holder = new ViewHolderDriverRequest();
				convertView = mInflater.inflate(R.layout.list_item_driver_request, null);
				
				holder.textViewRequestAddress = (TextView) convertView.findViewById(R.id.textViewRequestAddress); holder.textViewRequestAddress.setTypeface(Data.regularFont(getApplicationContext()));
				holder.textViewRequestDistance = (TextView) convertView.findViewById(R.id.textViewRequestDistance); holder.textViewRequestDistance.setTypeface(Data.regularFont(getApplicationContext()));
				holder.textViewRequestTime = (TextView) convertView.findViewById(R.id.textViewRequestTime); holder.textViewRequestTime.setTypeface(Data.regularFont(getApplicationContext()));
				holder.textViewRequestNumber = (TextView) convertView.findViewById(R.id.textViewRequestNumber); holder.textViewRequestNumber.setTypeface(Data.regularFont(getApplicationContext()));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRequest) convertView.getTag();
			}
			
			
			DriverRideRequest driverRideRequest = Data.driverRideRequests.get(position);
			
			holder.id = position;
			
			holder.textViewRequestNumber.setText(""+(position+1));
			holder.textViewRequestAddress.setText(driverRideRequest.address);
			
			
			long timeDiff = getDateOperations().getTimeDifference(getDateOperations().getCurrentTime(), driverRideRequest.startTime);
			long timeDiffInSec = timeDiff / 1000;
			holder.textViewRequestTime.setText(""+timeDiffInSec + " sec left");
			
			if(myLocation != null){
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				double distance = distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), driverRideRequest.latLng);
				if(distance >= 1000){
					holder.textViewRequestDistance.setText(""+decimalFormat.format(distance/1000)+" km away");
				}
				else{
					holder.textViewRequestDistance.setText(""+distance+" m away");
				}
			}
			else{
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}
			
			
			
			
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						holder = (ViewHolderDriverRequest) v.getTag();
						
						DriverRideRequest driverRideRequest = Data.driverRideRequests.get(holder.id);
						
						Data.dEngagementId = driverRideRequest.engagementId;
						Data.dCustomerId = driverRideRequest.customerId;
						Data.dCustLatLng = driverRideRequest.latLng;
						
						
						driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
						switchDriverScreen(driverScreenMode);
						
						
						map.animateCamera(CameraUpdateFactory.newLatLng(driverRideRequest.latLng), 1000, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	
	

	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=30.75,76.78&destinations=30.78,76.79&language=EN&sensor=false
	
	public String makeURL(LatLng source, LatLng destination){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(source.longitude));
        urlString.append("&destinations=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&language=EN&sensor=false&alternatives=false");
        return urlString.toString();
	}
	
	
	
	
	class GetDistanceTimeAddress extends AsyncTask<Void, Void, String>{
	    String url;
	    
	    String distance, duration;
	    
	    LatLng destination;
	    
	    boolean driverAcceptPushRecieved;
	    
	    public GetDistanceTimeAddress(LatLng destination, boolean driverAcceptPushRecieved){
	    	this.distance = "";
	    	this.duration = "";
	    	this.destination = destination;
	    	this.driverAcceptPushRecieved = driverAcceptPushRecieved;
	    }
	    

	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(userMode == UserMode.PASSENGER){
		        runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						if(driverAcceptPushRecieved){
							passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
							switchPassengerScreen(passengerScreenMode);
				        }
						
						nearestDriverProgress.setVisibility(View.VISIBLE);
				        nearestDriverText.setVisibility(View.GONE);
				        dontCallRefreshDriver = false;
				        
				        addCurrentLocationAddressMarker(destination);
					}
				});
	        }
	    }
	    
	    
	    @Override
	    protected String doInBackground(Void... params) {
	    	if(userMode == UserMode.PASSENGER){
		    	try{
		    		
		    		if(!driverAcceptPushRecieved){
			    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
		    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+destination.latitude));
		    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+destination.longitude));
		    			HttpRequester simpleJSONParser = new HttpRequester();
		    			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/find_a_driver", nameValuePairs);
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			Log.e("result of /find_a_driver", "="+result);
		    			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
		    			}
		    			else{
		    				try{
		    					JSONObject jObj = new JSONObject(result);
		    					new JSONParser().parseDriversToShow(jObj, "data");
		    				}
		    				catch(Exception e){
		    					e.printStackTrace();
		    				}
		    			}
		    		}
		    		
		    		LatLng source = null;
					
		    		if(!driverAcceptPushRecieved){
		    			double minDistance = 999999999;
			    		for(int i=0; i<Data.driverInfos.size(); i++){
			    			if(distance(destination, Data.driverInfos.get(i).latLng) < minDistance){
			    				minDistance = distance(destination, Data.driverInfos.get(i).latLng);
			    				source = Data.driverInfos.get(i).latLng;
			    			}
			    		}
			    		
			    		if(Data.driverInfos.size() > 0){
			    		double approxDistance = (minDistance * 1.5);
			    		if(approxDistance < 1000){
			    			distance = decimalFormat.format(approxDistance)+" m";
			    		}
			    		else{
			    			approxDistance = approxDistance / 1000;
			    			distance = decimalFormat.format(approxDistance)+" km";
			    		}
			    		}
			    		else{
			    			distance = "error";
			    		}
			    		return distance;
		    		}
		    		else if(driverAcceptPushRecieved){
		    			source = Data.assignedDriverInfo.latLng;
		    			
		    			if(source == null){
			    			return "error";
			    		}
			    			
			    		this.url = makeURL(source, destination);
				    	HttpRequester jParser = new HttpRequester();
				    	String response = jParser.getJSONFromUrl(url);
				    	JSONObject jsonObject = new JSONObject(response);
				    	String status = jsonObject.getString("status");
				    	if("OK".equalsIgnoreCase(status)){
				    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
				    		distance = element0.getJSONObject("distance").getString("text") ;
				    		duration = element0.getJSONObject("duration").getString("text");
				    		if(driverAcceptPushRecieved){
				    			Data.assignedDriverInfo.distanceToReach = distance;
				    			Data.assignedDriverInfo.durationToReach = duration;
				    		}
				    		return "Distance: " + distance + "\n" + "Duration: " + duration;
				    	}
				    	
		    		}
		    		
		    		
		    		
			    	
			    	
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
	    	}
	    	
	        return "error";
	    }
	    @Override
	    protected void onPostExecute(final String result) {
	        super.onPostExecute(result);  
	        if(userMode == UserMode.PASSENGER){
		        runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(!driverAcceptPushRecieved){
							showDriverMarkersAndPanMap(destination);
			    		}
				        
				        dontCallRefreshDriver = true;
						
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								dontCallRefreshDriver = false;
							}
						}, 4000);
				        
				        
				        nearestDriverProgress.setVisibility(View.GONE);
				        
				        nearestDriverText.setVisibility(View.VISIBLE);
				        
						
				        String distanceString = "";
				        
				        if(!"error".equalsIgnoreCase(result)){
					        
					        Log.i("passengerScreenMode","="+passengerScreenMode);
					        
					        if(!driverAcceptPushRecieved){
					 	        
					 	       	if(!"".equalsIgnoreCase(distance)){
					 	       		distanceString = getResources().getString(R.string.nearest_driver_is) + " " + distance + " " + getResources().getString(R.string.away);
						        }
						        else{
						        	distanceString = getResources().getString(R.string.could_not_find_nearest_driver_distance);
						        }
					 	        
					 	       	nearestDriverText.setText(distanceString);
					 	       
					        }
					        else if(driverAcceptPushRecieved){
					        	
					        }
				        }
				        else{
				        	distanceString = getResources().getString(R.string.no_drivers_nearby);
				        	nearestDriverText.setText(distanceString);
				        }
				        
				        
				        
				        
				        
				        if(!driverAcceptPushRecieved){
				        	Data.pickupLatLng = destination;
				        }
				        else if (driverAcceptPushRecieved) {
							SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_REQUEST_FINAL);
	
							editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
							editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
							editor.putString(Data.SP_C_LATITUDE, "" + Data.assignedDriverInfo.latLng.latitude);
							editor.putString(Data.SP_C_LONGITUDE, "" + Data.assignedDriverInfo.latLng.longitude);
							editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
							editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
							editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
							editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
							editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
							editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
							editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
	
							editor.commit();
							
							passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
							switchPassengerScreen(passengerScreenMode);
						}
					}
				});
	        }
	        
	        
	    }
	    
	    
	    
	}
	
	
	
	
	public void addDriverMarkerForCustomer(DriverInfo driverInfo){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title("driver shown to customer");
		markerOptions.snippet(""+driverInfo.userId);
		markerOptions.position(driverInfo.latLng);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createCarMarkerBitmap()));
		map.addMarker(markerOptions);
//		CircleOptions circleOptions = new CircleOptions();
//		circleOptions.center(driverInfo.latLng);
//		circleOptions.fillColor(Color.argb(20, 0, 0, 255));
//		circleOptions.radius(5000);
//		circleOptions.strokeWidth(0);
//		map.addCircle(circleOptions);
	}
	
	public void addCurrentLocationAddressMarker(LatLng latLng){
		try {
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.title("customer_current_location");
			markerOptions.snippet("");
			markerOptions.position(latLng);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));
			currentLocationMarker = map.addMarker(markerOptions);
		} catch (Exception e) {
		}
	}
	
	
	public void showDriverMarkersAndPanMap(LatLng userLatLng){
		if(map != null){
			map.clear();
			addCurrentLocationAddressMarker(userLatLng);
			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
			for(int i=0; i<Data.driverInfos.size(); i++){
				addDriverMarkerForCustomer(Data.driverInfos.get(i));
				boundsBuilder.include(Data.driverInfos.get(i).latLng);
			}
			boundsBuilder.include(new LatLng(userLatLng.latitude, userLatLng.longitude));
			try {
				final LatLngBounds bounds = boundsBuilder.build();
				final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int)(200*minScaleRatio)), 1000, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 1000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/**
	 * To search addresses related to particular address available on google
	 */
	public void searchGooglePlaces(String searchPlace) {
		
		final ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "","Searching... ", true);
		
		RequestParams params = new RequestParams();

		searchResults.clear();

		String ignr2 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="
				+ ""
				+ ","
				+ ""
				+ "&radius=50000"
				+ "&query="
				+ searchPlace
				+ "&sensor=true&key="+Data.MAPS_BROWSER_KEY;
		// "https://maps.googleapis.com/maps/api/place/textsearch/json?location=%f,%f&radius=2bb0000&query=%@&sensor=true&key=%@";

		ignr2 = ignr2.replaceAll(" ", "%20");

		AsyncHttpClient client = Data.getClient();
		client.post(ignr2, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				try {
					Log.e("request fail", arg3.getMessage().toString());
				} catch (Exception e) {
					Log.e("moving from", e.toString());
				}
				dialog.dismiss();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String response = new String(arg2);
				Log.e("request result", response);
				try {
					JSONArray info = null;
					JSONObject jj = new JSONObject(response);
					info = jj.getJSONArray("results");
					Log.v("converted", info + "");
					for (int a = 0; a < info.length(); a++) {
						JSONObject first = info.getJSONObject(a);
						Log.e("first" + a, "" + first);
					}
					Log.v("info.len....", "" + info.length());
					for (int i = 0; i < info.length(); i++) {
						// printing the values to the logcat
						try {
							SearchResult searchResult = new SearchResult(info.getJSONObject(i).getString("name"),
									info.getJSONObject(i).getString("formatted_address"), 
									new LatLng(
									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")
									));
							
							searchResults.add(searchResult);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < searchResults.size(); i++) {
						Log.i("Results name : ....", "" + searchResults.get(i).name);
					}
					passengerScreenMode = PassengerScreenMode.P_SEARCH;
					switchPassengerScreen(passengerScreenMode);
					
					Toast.makeText(getApplicationContext(), ""+searchResults.size() + " results found.", Toast.LENGTH_LONG).show();
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("errorr at response", "" + e.toString());
				}
				
				dialog.dismiss();
			}
			
		});
	}
	
	
	
	public float getBatteryPercentage(){
		try {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = (level / (float)scale)*100;
			
			// Are we charging / charged?
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			                     status == BatteryManager.BATTERY_STATUS_FULL;
			if(isCharging){
				return 70;
			}
			else{
				return batteryPct;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 70;
		}
	}
	
	
	String getAddress(double curLatitude, double curLongitude) {
    	String fullAddress = "Unnamed";

        try {
        	Log.i("curLatitude ",">"+curLatitude);
        	Log.i("curLongitude ",">"+curLongitude);
        	
            JSONObject jsonObj = new JSONObject(new HttpRequester().getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/json?" +
            		"latlng=" + curLatitude + "," + curLongitude 
            		+ "&sensor=true"));
            
            String status = jsonObj.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                
                String streetNumber = "", route = "", subLocality2 = "", subLocality1 = "", locality = "", administrativeArea2 = "", 
                		administrativeArea1 = "", country = "", postalCode = "";
                
                if(zero.has("address_components")){
                	try {
                		
                		ArrayList<String> selectedAddressComponentsArr = new ArrayList<String>();
						JSONArray addressComponents = zero.getJSONArray("address_components");
						
						for(int i=0; i<addressComponents.length(); i++){
							
							JSONObject iObj = addressComponents.getJSONObject(i);
							JSONArray jArr = iObj.getJSONArray("types");
							
							ArrayList<String> addressTypes = new ArrayList<String>();
							for(int j=0; j<jArr.length(); j++){
								addressTypes.add(jArr.getString(j));
							}
							
							if("".equalsIgnoreCase(streetNumber) && addressTypes.contains("street_number")){
								streetNumber = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(streetNumber) && !selectedAddressComponentsArr.toString().contains(streetNumber)){
									selectedAddressComponentsArr.add(streetNumber);
								}
							}
							if("".equalsIgnoreCase(route) && addressTypes.contains("route")){
								route = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(route) && !selectedAddressComponentsArr.toString().contains(route)){
									selectedAddressComponentsArr.add(route);
								}
							}
							if("".equalsIgnoreCase(subLocality2) && addressTypes.contains("sublocality_level_2")){
								subLocality2 = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(subLocality2) && !selectedAddressComponentsArr.toString().contains(subLocality2)){
									selectedAddressComponentsArr.add(subLocality2);
								}
							}
							if("".equalsIgnoreCase(subLocality1) && addressTypes.contains("sublocality_level_1")){
								subLocality1 = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(subLocality1) && !selectedAddressComponentsArr.toString().contains(subLocality1)){
									selectedAddressComponentsArr.add(subLocality1);
								}
							}
							if("".equalsIgnoreCase(locality) && addressTypes.contains("locality")){
								locality = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(locality) && !selectedAddressComponentsArr.toString().contains(locality)){
									selectedAddressComponentsArr.add(locality);
								}
							}
							if("".equalsIgnoreCase(administrativeArea2) && addressTypes.contains("administrative_area_level_2")){
								administrativeArea2 = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(administrativeArea2) && !selectedAddressComponentsArr.toString().contains(administrativeArea2)){
									selectedAddressComponentsArr.add(administrativeArea2);
								}
							}
							if("".equalsIgnoreCase(administrativeArea1) && addressTypes.contains("administrative_area_level_1")){
								administrativeArea1 = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(administrativeArea1) && !selectedAddressComponentsArr.toString().contains(administrativeArea1)){
									selectedAddressComponentsArr.add(administrativeArea1);
								}
							}
							if("".equalsIgnoreCase(country) && addressTypes.contains("country")){
								country = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(country) && !selectedAddressComponentsArr.toString().contains(country)){
									selectedAddressComponentsArr.add(country);
								}
							}
							if("".equalsIgnoreCase(postalCode) && addressTypes.contains("postal_code")){
								postalCode = iObj.getString("long_name");
								if(!"".equalsIgnoreCase(postalCode) && !selectedAddressComponentsArr.toString().contains(postalCode)){
									selectedAddressComponentsArr.add(postalCode);
								}
							}
						}
						
						fullAddress = "";
						Log.e("selectedAddressComponentsArr in getAddress = ", "="+selectedAddressComponentsArr);
						if(selectedAddressComponentsArr.size() > 0){
							for(int i=0; i<selectedAddressComponentsArr.size(); i++){
								if(i<selectedAddressComponentsArr.size()-1){
									fullAddress = fullAddress + selectedAddressComponentsArr.get(i) + ", ";
								}
								else{
									fullAddress = fullAddress + selectedAddressComponentsArr.get(i);
								}
							}
						}
						else{
							fullAddress = zero.getString("formatted_address");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						fullAddress = zero.getString("formatted_address");
					}
                }
                else{
                	fullAddress = zero.getString("formatted_address");
                }
                
                Log.e("Results.length() ==","="+Results.length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fullAddress;
    }

	
	
	
	
	
	
	/**
	 * ASync for cancelCustomerRequestAsync from server
	 */
	public void cancelCustomerRequestAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("session_id", Data.cSessionId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("session_id", "="+Data.cSessionId);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/cancel_the_request", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							customerCancelPressed = false;
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							handleConnectionLost();
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									customerCancelPressed = false;
									
								}
								else{
									cancelTimerRequestRide();
									
									passengerScreenMode = PassengerScreenMode.P_INITIAL;
									switchPassengerScreen(passengerScreenMode);
									
									if(map != null && pickupLocationMarker != null){
										pickupLocationMarker.remove();
									}
										
									if(myLocation != null){
										getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
												myLocation.getLongitude()), false);
										getDistanceTimeAddress.execute();
									}
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								customerCancelPressed = false;
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			customerCancelPressed = false;
		}

	}
	
	
	
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void changeDriverModeAsync(final Activity activity, final int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("flag", "=" + flag);
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/switch_to_driver_mode", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(1 == flag){
										makeMeDriverPopup(activity, errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									if(flag == 1){
										
										try {
											int excepInt = jObj.getInt("exceptional_driver");
											if(1 == excepInt){
												HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
											}
											else{
												HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
											}
										} catch (Exception e) {
											HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
											e.printStackTrace();
										}
										
										changeExceptionalDriverUI();
										
										userMode = UserMode.DRIVER;
										driverModeToggle.setImageResource(R.drawable.on);
										
										switchUserScreen(userMode);
										
										driverScreenMode = DriverScreenMode.D_INITIAL;
										switchDriverScreen(driverScreenMode);
										
										getAndShowAllDriverRequests(activity);
									}
									else{
										userMode = UserMode.PASSENGER;
										driverModeToggle.setImageResource(R.drawable.off);
										
										switchUserScreen(userMode);
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void deleteAllDriverRequests(final Activity activity){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2 database2 = new Database2(activity);
				database2.deleteAllDriverRequests();
				database2.close();
			}
		}).start();
	}
	
	
	public void deleteParticularDriverRequest(final Activity activity, final String engagementId){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2 database2 = new Database2(activity);
				database2.deleteDriverRequest(engagementId);
				database2.close();
			}
		}).start();
	}
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverAcceptRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			userCanceledDialogShown = false;
			
			DialogPopup.showLoadingDialog(activity, "Fetching customer data...");
			
			RequestParams params = new RequestParams();
			 
			if(myLocation != null){
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}
			
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);

			Log.e("accept ride api", "=");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("user_id", "=" + Data.dCustomerId);
			Log.i("engage_id", "=" + Data.dEngagementId);
			Log.i("latitude", "="+Data.latitude);
			Log.i("longitude", "="+Data.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/accept_a_request", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							DialogPopup.dismissLoadingDialog();
							handleConnectionLost();
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) 
						{
							String response = new String(arg2);
							Log.v("accept ride api Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									Log.e("accept_a_request flag", "="+flag);
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									
//									response = {"error":"Request timed out","flag":10}
									DialogPopup.dismissLoadingDialog();
									
									reduceRideRequest(activity, Data.dEngagementId);
									
									userCanceledDialogShown = false;
									
								}
								else{
									
									if(jObj.has("flag")){
										try{
											int flag = jObj.getInt("flag");
											Log.e("accept_a_request flag", "="+flag);
											String logMessage = jObj.getString("log");
											new DialogPopup().alertPopup(activity, "", ""+logMessage);
											
										} catch(Exception e){
											e.printStackTrace();
										}
										reduceRideRequest(activity, Data.dEngagementId);
									}
									else{
//										{
//											Òuser_dataÓ: {
//										            "user_name": result_customer[0].user_name,
//										            "phone_no": result_customer[0].phone_no,
//										            "user_image": result_customer[0].user_image,
//										            "user_rating": rating};
										
										JSONObject userData = jObj.getJSONObject("user_data");
										
										String userName = userData.getString("user_name");
										String userImage = userData.getString("user_image");
										String phoneNo = userData.getString("phone_no");
										String rating = "4";
										try{
											rating = userData.getString("user_rating");
										} catch(Exception e){
											e.printStackTrace();
										}
										if(userName == null){
											userName = "";
										}
										if(userImage == null){
											userImage = "http://jugnoo-images.s3.amazonaws.com/user_profile/user.png";
										}
										if(phoneNo == null){
											phoneNo = "";
										}
										
										Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, userName,
												userImage, phoneNo, rating);
										
										Data.driverRideRequests.clear();
										
										SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
										Editor editor = pref.edit();
										editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_START_RIDE);
										
										editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
										editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
										
										editor.putString(Data.SP_D_LATITUDE, ""+Data.dCustLatLng.latitude);
										editor.putString(Data.SP_D_LONGITUDE, ""+Data.dCustLatLng.longitude);
										
										editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
										editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
										editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
										editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
										
										editor.commit();
	
								        GCMIntentService.clearNotifications(getApplicationContext());
								        
										driverScreenMode = DriverScreenMode.D_START_RIDE;
										switchDriverScreen(driverScreenMode);
										
										deleteAllDriverRequests(HomeActivity.this);
										
									}
									
									
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
							
							DialogPopup.dismissLoadingDialog();
							
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	
	public void reduceRideRequest(Activity activity, String engagementId){
		deleteParticularDriverRequest(activity, engagementId);
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
	}
	
	
	public void getAndShowAllDriverRequests(final Activity activity){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2 database2 = new Database2(activity);
				Data.driverRideRequests.clear();
				Data.driverRideRequests.addAll(database2.getAllDriverRequests());
				database2.close();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showAllRideRequestsOnMap();
					}
				});
			}
		}).start();
	}
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverRejectRequestAsync(final Activity activity) {

			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
			
				
				params.put("access_token", Data.userData.accessToken);
				params.put("customer_id", Data.dCustomerId);
				params.put("engagement_id", Data.dEngagementId);

				Log.i("access_token", "=" + Data.userData.accessToken);
				Log.i("customer_id", "=" + Data.dCustomerId);
				Log.i("engagement_id", "=" + Data.dEngagementId);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/reject_a_request", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
//										{"log":"rejected successfully"}
//										{"log":"Rejected successfully","flag":110}
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												new DialogPopup().alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										
										if(map != null){
											map.clear();
										}
										stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
										
										reduceRideRequest(activity, Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverStartRideAsync(final Activity activity, double pickupLatitude, double pickupLongitude) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("pickup_latitude", ""+pickupLatitude);
			params.put("pickup_longitude", ""+pickupLongitude);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("pickup_latitude", "=" + pickupLatitude);
			Log.i("pickup_longitude", "=" + pickupLongitude);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/start_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							handleConnectionLost();
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) 
						{
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"ride_started"}

									if(map != null){
										map.clear();
									}
									
									lastLocation = null;
									
									HomeActivity.previousWaitTime = 0;
									HomeActivity.previousRideTime = 0;
									HomeActivity.totalDistance = -1;
									
									Database database = new Database(HomeActivity.this);
									database.deleteSavedPath();
									database.close();
									
									waitStart = 2;
									
						        	driverScreenMode = DriverScreenMode.D_IN_RIDE;
									switchDriverScreen(driverScreenMode);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverCancelRideAsync(final Activity activity) {

			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
			
				
				params.put("access_token", Data.userData.accessToken);
				params.put("customer_id", Data.dCustomerId);
				params.put("engagement_id", Data.dEngagementId);

				Log.e("access_token", "=" + Data.userData.accessToken);
				Log.e("customer_id", "=" + Data.dCustomerId);
				Log.e("engagement_id", "=" + Data.dEngagementId);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/cancel_the_ride", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
//								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
								handleConnectionLost();
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
//										{"log":"rejected successfully"}
//										response = {"log":"Ride cancelled successfully","flag":109}
										
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												new DialogPopup().alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										if(map != null){
											map.clear();
										}
										stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
										
										reduceRideRequest(activity, Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, double dropLatitude, double dropLongitude, double waitMinutes) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			DecimalFormat decimalFormatWait = new DecimalFormat("#");
			
			waitTime = decimalFormatWait.format(waitMinutes);
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("latitude", ""+dropLatitude);
			params.put("longitude", ""+dropLongitude);
			params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
			params.put("wait_time", waitTime);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("latitude", "="+dropLatitude);
			Log.i("longitude", "="+dropLongitude);
			Log.i("distance_travelled", "="+decimalFormat.format(totalDistanceInKm));
			Log.i("wait_time", "="+waitTime);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/end_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							driverScreenMode = DriverScreenMode.D_IN_RIDE;
							DialogPopup.dismissLoadingDialog();
							rideTimeChronometer.start();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									driverScreenMode = DriverScreenMode.D_IN_RIDE;
									rideTimeChronometer.start();
								}
								else{
									
//									{"log":"ride_ended"}//result

									try{
										totalFare = jObj.getDouble("fare");
									} catch(Exception e){
										e.printStackTrace();
										totalFare = 0;
									}

									lastLocation = null;
									
									if(map != null){
										map.clear();
									}
									
									waitStart = 2;
									waitChronometer.stop();
									rideTimeChronometer.stop();
									Log.e("waitChronometer.stop()","in endRideAPi on click");
									
									
									clearSPData();
									
									
						        	driverScreenMode = DriverScreenMode.D_RIDE_END;
									switchDriverScreen(driverScreenMode);
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								rideTimeChronometer.start();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			driverScreenMode = DriverScreenMode.D_IN_RIDE;
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			rideTimeChronometer.start();
		}

	}
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void submitReviewAsync(final Activity activity, String engagementId, final String flag, String ratingReceiverId, String givenRating) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);
			params.put("flag", flag);
			params.put("rating_receiver_id", ratingReceiverId);
			params.put("given_rating", givenRating);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", engagementId);
			Log.i("flag", flag);
			Log.i("rating_receiver_id", ratingReceiverId);
			Log.i("given_rating", givenRating);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/rating", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Rated successfully"}
									

									if(flag.equalsIgnoreCase("0")){
										userMode = UserMode.DRIVER;
										driverModeToggle.setImageResource(R.drawable.on);
										
										switchUserScreen(userMode);
										
										driverScreenMode = DriverScreenMode.D_INITIAL;
										switchDriverScreen(driverScreenMode);
									}
									else{
										userMode = UserMode.PASSENGER;
										driverModeToggle.setImageResource(R.drawable.off);
										
										switchUserScreen(userMode);
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	void saveToFavoritePopup(final Activity activity, String locationName, final LatLng favLatLng){

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.save_to_favorite_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(getApplicationContext()));
			final EditText favoriteNameEt = (EditText) dialog.findViewById(R.id.favoriteNameEt); favoriteNameEt.setTypeface(Data.regularFont(getApplicationContext()));
			
			favoriteNameEt.setText(locationName);
			
			favoriteNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					favoriteNameEt.setError(null);
				}
			});
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(getApplicationContext()));
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(getApplicationContext()));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					String favName = favoriteNameEt.getText().toString().trim();
					
					if(!"".equalsIgnoreCase(favName)){
						dialog.dismiss();
						saveToFavoriteAsync(activity, favName, favLatLng);
					}
					else{
						favoriteNameEt.requestFocus();
						favoriteNameEt.setError("Favorite location name cannot be empty.");
					}
					
				}
				
			});
			
			
			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * ASync for saveToFavoriteAsync from server
	 */
	public void saveToFavoriteAsync(final Activity activity, String favName, LatLng favLatLng) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("fav_name", favName);
			params.put("fav_latitude", ""+favLatLng.latitude);
			params.put("fav_longitude", ""+favLatLng.longitude);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("fav_name", "="+favName);
			Log.i("fav_latitude", "="+favLatLng.latitude);
			Log.i("fav_longitude", "="+favLatLng.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Added to favourite successfully"}	//result

									
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
									
									getAllFavoriteAsync(activity);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	

	
	/**
	 * ASync for get all favorite locations from server
	 */
	public void getAllFavoriteAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);

			Log.i("access_token", "=" + Data.userData.accessToken);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/get_fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	

									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
									}
									else{
									}
								}
								else{
									
									JSONArray favouriteData = jObj.getJSONArray("favourite_data");
									
									Data.favoriteLocations.clear();
									
									if(favouriteData.length() > 0){
										
										for(int i=0; i<favouriteData.length(); i++){
											JSONObject favData = favouriteData.getJSONObject(i);
											
											Data.favoriteLocations.add(new FavoriteLocation(favData.getInt("s_no"), favData.getString("fav_name"), 
													new LatLng(favData.getDouble("fav_latitude"), favData.getDouble("fav_longitude"))));
											
										}
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
	
						}
					});
		}
		else {
		}

	}
	
	
	
	
	
	
	
	
	public void facebookLogin() {
		
		
		if (!AppStatus.getInstance(HomeActivity.this).isOnline(
				HomeActivity.this)) {
			new DialogPopup().alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
		} else {
			Log.i(" connection", " connection");
			SplashLogin.session = new Session(HomeActivity.this);
			Session.setActiveSession(SplashLogin.session);
			Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);

			Session.OpenRequest openRequest = null;
			openRequest = new Session.OpenRequest(HomeActivity.this);
			openRequest.setPermissions(Arrays.asList("email", "user_friends", "user_photos"));

			try {
				if (SplashLogin.isSystemPackage(getPackageManager().getPackageInfo("com.facebook.katana", 0))) {
					openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
				} else {
					openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			openRequest.setCallback(new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {

					if (session.isOpened()) {
						Session.openActiveSession(HomeActivity.this, true, new Session.StatusCallback() {
									@SuppressWarnings("deprecation")
									@Override
									public void call(final Session session, SessionState state, Exception exception) {
										Log.v("session.isOpened()", "" + session.isOpened());
										Log.v("app id", "" + session.getApplicationId());
										if (session.isOpened()) {
											Log.e("heyyy", "Logged in..." + session.getAccessToken());

											Data.fbAccessToken = session.getAccessToken();
											Log.e("fbAccessToken===", "="+Data.fbAccessToken);
									    	
											
											DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
											

											Request.executeMeRequestAsync(session,
													new Request.GraphUserCallback() {
														@Override
														public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook

															DialogPopup.dismissLoadingDialog();
															
															if (user != null) {
																Log.i("data", "username" + user.getName() + "fbid!" + user.getId() + " firstname "
																				+ user.getFirstName() + " lastname " + user.getLastName() + "  ");
																Log.i("res", response.toString());
																Log.i("user", "User " + user);
																
																Data.fbId = user.getId();
																Data.fbFirstName = user.getFirstName();
																Data.fbLastName = user.getLastName();
																Data.fbUserName = user.getUsername();
																
																try {
																	Data.fbUserEmail = ((String)user.asMap().get("email"));
																	if("".equalsIgnoreCase(Data.fbUserEmail)){
																		Data.fbUserEmail = user.getUsername() + "@facebook.com";
																	}
																} catch (Exception e2) {
																	Data.fbUserEmail = user.getUsername() + "@facebook.com";
																	e2.printStackTrace();
																}

																Log.e("Data.fbId","="+Data.fbId);
																Log.e("Data.fbFirstName","="+Data.fbFirstName);
																Log.e("Data.fbLastName","="+Data.fbLastName);
																Log.e("Data.fbUserName","="+Data.fbUserName);
																Log.e("Data.userEmail","="+Data.fbUserEmail);

																
																inviteFbFriend();
																
															}
															else{
																new DialogPopup().alertPopup(HomeActivity.this, "Facebook Error", "Error in fetching information from Facebook.");
															}
															

														}
													});
										}
										else if (session.isClosed()) {
											Log.e("heyy", "Logged out...");

											DialogPopup.dismissLoadingDialog();
										}
									}
								});

					} else if (session.isClosed()) {
						
					}
					
					
				}
			});
			SplashLogin.session.openForRead(openRequest);
		}
		
		
		
	}

	
	
	
	
	
	/**
	 * ASync for logout from server
	 */
	public void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("id", Data.userData.id);

			Log.i("access_token", "="+Data.userData.accessToken);
			Log.i("id", "="+Data.userData.id);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL+"/logout", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){

									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
//									
//									int flag = jObj.getInt("flag");	
//									String errorMessage = jObj.getString("error");
//									
//									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
//										new DialogPopup().alertPopup(activity, "", errorMessage);
//									}
//									else{
//										new DialogPopup().alertPopup(activity, "", errorMessage);
//									}
								}
								else{

									PicassoTools.clearCache(Picasso.with(HomeActivity.this));
									
									try {
										Session session = new Session(HomeActivity.this);
										Session.setActiveSession(session);	
										session.closeAndClearTokenInformation();	
									}
									catch(Exception e) {
										Log.v("Logout", "Error"+e);	
									}
									
									GCMIntentService.clearNotifications(HomeActivity.this);
									
									Data.clearDataOnLogout(HomeActivity.this);
									
									userMode = UserMode.PASSENGER;
									passengerScreenMode = PassengerScreenMode.P_INITIAL;
									driverScreenMode = DriverScreenMode.D_INITIAL;
									
									loggedOut = true;
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}

						
						@Override
						public void onRetry(int retryNo) {
							Log.e("retryNo","="+retryNo);
							super.onRetry(retryNo);
						}
						
						
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	/**
	 * ASync for start or end wait from server
	 */
	public void startEndWaitAsync(final Activity activity, String customerId, int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", customerId);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("customer_id", "="+customerId);
			Log.i("flag", "="+flag);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/start_end_wait", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
								}
								else{
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
	
						}
					});
		}
		else {
		}

	}
	
	
	
	
	void logoutPopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.logout_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					logoutAsync(activity);
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	void noDriverAvailablePopup(final Activity activity, boolean zeroDriversNearby){
		try {
			if(noDriversDialog != null && noDriversDialog.isShowing()){
				noDriversDialog.dismiss();
			}
			noDriversDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			noDriversDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			noDriversDialog.setContentView(R.layout.no_driver_dialog);

			FrameLayout frameLayout = (FrameLayout) noDriversDialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = noDriversDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			noDriversDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			noDriversDialog.setCancelable(false);
			noDriversDialog.setCanceledOnTouchOutside(false);

			TextView textHead = (TextView) noDriversDialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			textHead.setVisibility(View.GONE);
			TextView textMessage = (TextView) noDriversDialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.regularFont(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			if(zeroDriversNearby){
				textMessage.setText("Sorry there are no drivers available nearby within 3 km. We will look into it");
			}
			else{
				textMessage.setText("Sorry, All our drivers are currently busy. We are unable to offer you services right now. Please try again sometime later.");
			}
			

			Button btnOk = (Button) noDriversDialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.regularFont(activity));
			Button crossbtn = (Button) noDriversDialog
					.findViewById(R.id.crossbtn);
			crossbtn.setTypeface(Data.regularFont(activity));
			crossbtn.setVisibility(View.GONE);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					noDriversDialog.dismiss();
					noDriverAsync(activity);
					noDriversDialog = null;
				}
			});

			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					noDriversDialog.dismiss();
					noDriverAsync(activity);
					noDriversDialog = null;
				}

			});

			noDriversDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * ASync for no drivers from server
	 */
	public void noDriverAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);

			if(myLocation != null){
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}
			
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/request_now", params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
						}
					});
		}
	}
	
	
	
	
	
	public void inviteFbFriend(){
		
		Bundle parameters = new Bundle();
		parameters.putString("message", "Download app now to get started. Available on Google Play Store and App Store");
		parameters.putString("data", "Get from one place to another with ease.");
		parameters.putString("link", "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo");
		

		WebDialog.Builder builder = new Builder(HomeActivity.this, Session.getActiveSession(), "apprequests", parameters);

		builder.setOnCompleteListener(new OnCompleteListener() {

		    @Override
		    public void onComplete(Bundle values, FacebookException error) {
		    	Log.e("values","="+values);
		    	Log.e("error","="+error);
		        if (error != null){
		        	Toast.makeText(HomeActivity.this,"Request cancelled",Toast.LENGTH_SHORT).show();
		        }
		        else{

		            final String requestId = values.getString("request");
		            if (requestId != null) {
		            	new DialogPopup().alertPopup(HomeActivity.this, "", "Friends invited successfully.");
		            } 
		            else {
		                Toast.makeText(HomeActivity.this,"Request cancelled",Toast.LENGTH_SHORT).show();
		            }
		        }                       
		    }
		});

		WebDialog webDialog = builder.build();
		webDialog.show();
	        
	        
	}
	
	
	
	
	
	
	void makeMeDriverPopup(final Activity activity, String message){
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_two_btn_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText("Alert");
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					makeMeDriverAsync(activity);
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * ASync for make me driver server
	 */
	public void makeMeDriverAsync(final Activity activity) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/make_me_driver_request", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.v("Server response", "response = " + response);

								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										

										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
//									{"log": "Thank you for signing up. We will reach out to you soon."}
										
										new DialogPopup().alertPopup(activity, "", jObj.getString("log"));

									
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
								}

								DialogPopup.dismissLoadingDialog();
								
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	Timer timerDriverLocationUpdater;
	
	TimerTask timerTaskDriverLocationUpdater;
	
	
	public void startDriverLocationUpdateTimer(){
		
		try{
			if(timerTaskDriverLocationUpdater != null){
				timerTaskDriverLocationUpdater.cancel();
				timerTaskDriverLocationUpdater = null;
			}
			
			if(timerDriverLocationUpdater != null){
				timerDriverLocationUpdater.cancel();
				timerDriverLocationUpdater.purge();
				timerDriverLocationUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			timerDriverLocationUpdater = new Timer();
			
			timerTaskDriverLocationUpdater = new TimerTask() {
				
				@Override
				public void run() {
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						
						if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
								
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
							nameValuePairs.add(new BasicNameValuePair("driver_id", Data.assignedDriverInfo.userId));
							
							
							
							HttpRequester simpleJSONParser = new HttpRequester();
							String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/get_driver_current_location", nameValuePairs);
							
							
							if(result.contains(HttpRequester.SERVER_TIMEOUT)){
							}
							else{
								try {
									JSONObject jObj = new JSONObject(result);
									
									if(!jObj.isNull("error")){
	
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											
										}
									}
									else{
										
	//								{
	//								    "data": [
	//								        {
	//								            "user_id": 192,
	//								            "current_location_latitude": 30.718788,
	//								            "current_location_longitude": 76.810109
	//								        }
	//								    ]
	//								}
										
										JSONArray data = jObj.getJSONArray("data");
										JSONObject data0 = data.getJSONObject(0);
										final LatLng driverCurrentLatLng = new LatLng(data0.getDouble("current_location_latitude"),
												data0.getDouble("current_location_longitude"));
										
										if(Data.assignedDriverInfo != null){
											Data.assignedDriverInfo.latLng = driverCurrentLatLng;
										}
										
										runOnUiThread(new Runnable() {
											
											@Override
											public void run() {
												try{
													if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
														driverLocationMarker.setPosition(driverCurrentLatLng);
													}
												} catch(Exception e){
													e.printStackTrace();
												}
											}
										});
										
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
					
				}
			};
			
			
			timerDriverLocationUpdater.scheduleAtFixedRate(timerTaskDriverLocationUpdater, 10, 15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelDriverLocationUpdateTimer(){
		try{
			if(timerTaskDriverLocationUpdater != null){
				timerTaskDriverLocationUpdater.cancel();
				timerTaskDriverLocationUpdater = null;
			}
			
			if(timerDriverLocationUpdater != null){
				timerDriverLocationUpdater.cancel();
				timerDriverLocationUpdater.purge();
				timerDriverLocationUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	Timer timerCustomerPathUpdater;
	TimerTask timerTaskCustomerPathUpdater;
	
	
	public void startCustomerPathUpdateTimer(){
		
		try{
			if(timerTaskCustomerPathUpdater != null){
				timerTaskCustomerPathUpdater.cancel();
				timerTaskCustomerPathUpdater = null;
			}
			
			if(timerCustomerPathUpdater != null){
				timerCustomerPathUpdater.cancel();
				timerCustomerPathUpdater.purge();
				timerCustomerPathUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			timerCustomerPathUpdater = new Timer();
			
			timerTaskCustomerPathUpdater = new TimerTask() {
				
				@Override
				public void run() {
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
							//customer path updater
							if(myLocation != null){
								if(Data.dCustLatLng != null){
									
									if(driverScreenMode == DriverScreenMode.D_START_RIDE){
										LatLng source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
										String url = makeURLPath(source, Data.dCustLatLng);
										String result = new HttpRequester().getJSONFromUrl(url);
										
										if(result != null){
											final List<LatLng> list = getLatLngListFromPath(result);
											
											runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													try {
														if(driverScreenMode == DriverScreenMode.D_START_RIDE){
															map.clear();
															
															if(markerOptionsCustomerPickupLocation == null){
																markerOptionsCustomerPickupLocation = new MarkerOptions();
																markerOptionsCustomerPickupLocation.title(Data.dEngagementId);
																markerOptionsCustomerPickupLocation.snippet("");
																markerOptionsCustomerPickupLocation.position(Data.dCustLatLng);
																markerOptionsCustomerPickupLocation.icon(BitmapDescriptorFactory.fromBitmap(createPassengerMarkerBitmap()));
															}
															
															map.addMarker(markerOptionsCustomerPickupLocation);
															
															for(int z = 0; z<list.size()-1;z++){
															    LatLng src= list.get(z);
															    LatLng dest= list.get(z+1);
															    map.addPolyline(new PolylineOptions()
															    .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
															    .width(5)
															    .color(D_TO_C_MAP_PATH_COLOR).geodesic(true));
															}
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											});
								        }
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			
			
			timerCustomerPathUpdater.scheduleAtFixedRate(timerTaskCustomerPathUpdater, 10, 15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelCustomerPathUpdateTimer(){
		try{
			if(timerTaskCustomerPathUpdater != null){
				timerTaskCustomerPathUpdater.cancel();
				timerTaskCustomerPathUpdater = null;
			}
			
			if(timerCustomerPathUpdater != null){
				timerCustomerPathUpdater.cancel();
				timerCustomerPathUpdater.purge();
				timerCustomerPathUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	

	
	
    Timer timerUpdateDrivers;
	TimerTask timerTaskUpdateDrivers;
	
	public void startTimerUpdateDrivers(){
		cancelTimerUpdateDrivers();
		try {
			timerUpdateDrivers = new Timer();
			timerTaskUpdateDrivers = new TimerTask() {
				@Override
				public void run() {
					try {
						if(HomeActivity.appInterruptHandler != null){
							HomeActivity.appInterruptHandler.refreshDriverLocations();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			timerUpdateDrivers.scheduleAtFixedRate(timerTaskUpdateDrivers, 100, 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelTimerUpdateDrivers(){
		try{
			if(timerTaskUpdateDrivers != null){
				timerTaskUpdateDrivers.cancel();
				timerTaskUpdateDrivers = null;
			}
			if(timerUpdateDrivers != null){
				timerUpdateDrivers.cancel();
				timerUpdateDrivers.purge();
				timerUpdateDrivers = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	Timer timerMapAnimate;
	TimerTask timerTaskMapAnimate;
	
	
	public void startMapAnimateTimer() {

		try {
			if (timerTaskMapAnimate != null) {
				timerTaskMapAnimate.cancel();
				timerTaskMapAnimate = null;
			}

			if (timerMapAnimate != null) {
				timerMapAnimate.cancel();
				timerMapAnimate.purge();
				timerMapAnimate = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			timerMapAnimate = new Timer();

			timerTaskMapAnimate = new TimerTask() {

				@Override
				public void run() {
					try {
						if (myLocation != null && map != null) {

							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if(myLocation.getSpeed() >= 1 && myLocation.getBearing() != 0){
										CameraPosition cameraPosition = new CameraPosition(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 
													map.getCameraPosition().zoom, 
													map.getCameraPosition().tilt,
													myLocation.getBearing());
										map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
									}
								}
							});
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			timerMapAnimate.scheduleAtFixedRate(timerTaskMapAnimate, 30000, 30000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void cancelMapAnimateTimer(){
		try{
			if(timerTaskMapAnimate != null){
				timerTaskMapAnimate.cancel();
				timerTaskMapAnimate = null;
			}
			
			if(timerMapAnimate != null){
				timerMapAnimate.cancel();
				timerMapAnimate.purge();
				timerMapAnimate = null;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public List<LatLng> getLatLngListFromPath(String result){
		try {
	    	 final JSONObject json = new JSONObject(result);
		     JSONArray routeArray = json.getJSONArray("routes");
		     JSONObject routes = routeArray.getJSONObject(0);
		     JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		     String encodedString = overviewPolylines.getString("points");
		     List<LatLng> list = decodePoly(encodedString);
		     return list;
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return new ArrayList<LatLng>();
	    }
	}
	
	
	
	
	
	
	
	
	
	
	void callAnAutoPopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_two_btn_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
			textHead.setVisibility(View.VISIBLE);
			textHead.setText("Chalo Jugnoo Se");
			textMessage.setText("Do you want to call an auto?");
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
			
			btnOk.setText("Now");
			btnCancel.setText("Later");
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					initiateRequestRide(true);
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	void startRidePopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_two_btn_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
			
			textMessage.setText("Are you sure you want to start ride?");
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(myLocation != null){
						dialog.dismiss();

			        	double displacement = distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), Data.dCustLatLng);
			        	
			        	if(displacement <= DRIVER_START_RIDE_CHECK_METERS){
			        		buildAlertMessageNoGps();
				        	
				        	GCMIntentService.clearNotifications(activity);
				        	driverStartRideAsync(activity, myLocation.getLatitude(), myLocation.getLongitude());
			        	}
			        	else{
			        		new DialogPopup().alertPopup(activity, "", "You must be present near the customer pickup location to start ride.");
			        	}
					}
					else{
						Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
					}
		        	
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	void endRidePopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
				
				textMessage.setText("Are you sure you want to end ride?");
				
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(myLocation != null){
							dialog.dismiss();
	
							GCMIntentService.clearNotifications(HomeActivity.this);
							Log.e("waitChronometer.stop()","in driverEndRideSlider on click");
							waitChronometer.stop();
							rideTimeChronometer.stop();
							
							driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
							driverWaitText.setText(getResources().getString(R.string.start_wait));
							waitStart = 0;
							
							long elapsedMillis = waitChronometer.eclipsedTime;
							long seconds = elapsedMillis / 1000;
							double minutes = Math.ceil(((double)seconds) / 60.0);
							
							driverScreenMode = DriverScreenMode.D_RIDE_END;
							
				        	driverEndRideAsync(activity, myLocation.getLatitude(), myLocation.getLongitude(), minutes);
						}
						else{
							Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
						}
			        	
					}
					
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	
		void cancelRidePopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.custom_two_btn_dialog);

					FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
					
					textMessage.setText("Are you sure you want to cancel ride?");
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							
							GCMIntentService.clearNotifications(HomeActivity.this);
							driverCancelRideAsync(HomeActivity.this);
						}
						
						
					});
					
					btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
						
					});

					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
		
		
		public void confirmDebugPasswordPopup(final Activity activity){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.otp_confirm_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.regularFont(activity));
				
				textHead.setText("Confirm Debug Password");
				textMessage.setText("Please enter password to continue.");
				
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.regularFont(activity));
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(Data.DEBUG_PASSWORD.equalsIgnoreCase(code)){
								dialog.dismiss();
								changeDebugModePopup(activity);
							}
							else{
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
				
				crossbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		void changeDebugModePopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));
				
				
				if(appMode == AppMode.DEBUG){
					textMessage.setText("App is in DEBUG mode.\nChange to:");
				}
				else if(appMode == AppMode.NORMAL){
					textMessage.setText("App is in NORMAL mode.\nChange to:");
				}
				
				
				
				
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
				btnOk.setText("NORMAL");
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.regularFont(activity));
				btnCancel.setText("DEBUG");
				
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.regularFont(activity));
				crossbtn.setVisibility(View.VISIBLE);
				
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						appMode = AppMode.NORMAL;
						dialog.dismiss();
					}
					
					
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						appMode = AppMode.DEBUG;
						dialog.dismiss();
					}
					
				});
				
				crossbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});
				

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		

		
		
		
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus){
			if(loggedOut){
				loggedOut = false;
				
				cancelTimerUpdateDrivers();
		        stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				startActivity(new Intent(HomeActivity.this, SplashLogin.class));
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
			else{
				buildAlertMessageNoGps();
			}
		}
	}
	
	
	
	

	@Override
	public void startRideForCustomer(final int flag) {
		try {
			if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
			Log.e("in ","herestartRideForCustomer");
			
			if(flag == 0){
			
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("in in herestartRideForCustomer  run class","=");
						
						lastLocation = null;
						
						HomeActivity.totalDistance = -1;
						Database database = new Database(HomeActivity.this);
						database.deleteSavedPath();
						database.close();
						
						
						passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
						switchPassengerScreen(passengerScreenMode);
					}
				});
				
			}
			else{
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("in in herestartRideForCustomer  run class","=");
						passengerScreenMode = PassengerScreenMode.P_INITIAL;
						switchPassengerScreen(passengerScreenMode);
						new DialogPopup().alertPopup(HomeActivity.this, "", "Driver has canceled the ride.");
					}
				});
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}



	@Override
	public void refreshDriverLocations() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						if(myLocation != null){
							getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
									myLocation.getLongitude()), false);
							getDistanceTimeAddress.execute();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	

	// 0 = not found   1 = accept
	@Override
	public void rideRequestAcceptedInterrupt(JSONObject jObj) {

		try {
			if(userMode == UserMode.PASSENGER){
				cancelTimerUpdateDrivers();
				
				cancelTimerRequestRide();
				Log.e("customerCancelBeforePushReceive ","="+customerCancelPressed);
				if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
					fetchAcceptedDriverInfoAndChangeState(jObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void fetchAcceptedDriverInfoAndChangeState(JSONObject jObj){
		try {
			cancelTimerRequestRide();
			passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
//			{
//	            "user_name": "Shankar Bhagwati",
//	            "session_id": 798,
//	            "driver_car_image": "",
//	            "flag": 5,
//	            "engagement_id": "2704",
//	            "phone_no": "+919780298413",
//	            "driver_id": 207,
//	            "rating": 4.888888888888889,
//	            "user_image": "http:\/\/graph.facebook.com\/717496164959213\/picture?width=160&height=160"
//	        }
			
			Data.cSessionId = jObj.getString("session_id");
			Data.cEngagementId = jObj.getString("engagement_id");
			
			Data.cDriverId = jObj.getString("driver_id");
			String userName = jObj.getString("user_name");
			String driverPhone = jObj.getString("phone_no");
			String driverImage = jObj.getString("user_image");
			String driverCarImage = jObj.getString("driver_car_image");
			double latitude = jObj.getDouble("current_location_latitude");
			double longitude = jObj.getDouble("current_location_longitude");
			String driverRating = jObj.getString("rating");
			
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName, 
					driverImage, driverCarImage, driverPhone, driverRating);
			
			
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.e("assignedDriverInfo", "="+Data.assignedDriverInfo);
					Log.e("myLocation", "="+myLocation);
					if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
					}
					if(myLocation != null){
						getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
								myLocation.getLongitude()), true);
						getDistanceTimeAddress.execute();
					}
				}
			});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	


	public void showAllRideRequestsOnMap(){
		
		try {
			if(userMode == UserMode.DRIVER){
				
				map.clear();
				
			
				if(Data.driverRideRequests.size() > 0){
					
					driverRequestListAdapter.notifyDataSetChanged();
					
					if(driverNewRideRequestRl.getVisibility() == View.GONE && driverRideRequestsList.getVisibility() == View.GONE){
						driverNewRideRequestRl.setVisibility(View.VISIBLE);
						driverRideRequestsList.setVisibility(View.GONE);
					}
					
					LatLng last = map.getCameraPosition().target;
					
					for(int i=0; i<Data.driverRideRequests.size(); i++){
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.title(Data.driverRideRequests.get(i).engagementId);
						markerOptions.snippet("");
						markerOptions.position(Data.driverRideRequests.get(i).latLng);
						markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPassengerMarkerBitmap()));
						
						map.addMarker(markerOptions);
						
					}
					
					driverNewRideRequestText.setText(getResources().getString(R.string.you_have)+" "+Data.driverRideRequests.size()+" "+getResources().getString(R.string.ride_request));
					
					map.animateCamera(CameraUpdateFactory.newLatLng(last), 1000, null);
					
				}
				else{
					driverNewRideRequestRl.setVisibility(View.GONE);
					driverRideRequestsList.setVisibility(View.GONE);
				}
			
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onNewRideRequest() {
		if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getAndShowAllDriverRequests(HomeActivity.this);
				}
			});
		}
	}

	@Override
	public void onCancelRideRequest(final String engagementId, final boolean acceptedByOtherDriver) {
		try {
				if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getAndShowAllDriverRequests(HomeActivity.this);
						}
					});
				}
				else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
								DialogPopup.dismissLoadingDialog();
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								if(!userCanceledDialogShown){
									if(acceptedByOtherDriver){
										new DialogPopup().alertPopup(HomeActivity.this, "", "This request has been accepted by other driver");
									}
									else{
										new DialogPopup().alertPopup(HomeActivity.this, "", "User has canceled the request");
									}
								}
								userCanceledDialogShown = false;
							}
						}
					});
				}
				else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_START_RIDE){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								new DialogPopup().alertPopup(HomeActivity.this, "", "User has canceled the request");
							}
						}
					});
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onRideRequestTimeout(final String engagementId) {
		if(userMode == UserMode.DRIVER ){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
						if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
							driverScreenMode = DriverScreenMode.D_INITIAL;
							switchDriverScreen(driverScreenMode);
						}
					}
					else if(driverScreenMode == DriverScreenMode.D_INITIAL){
						getAndShowAllDriverRequests(HomeActivity.this);
					}
				}
			});
		}
	}
	


	
	public void clearSPData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
        	
        	SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
    		Editor editor = pref.edit();
        		
    		editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");
    		
    		editor.putString(Data.SP_D_ENGAGEMENT_ID, "");
    		editor.putString(Data.SP_D_CUSTOMER_ID, "");
    		editor.putString(Data.SP_D_LATITUDE, "0");
    		editor.putString(Data.SP_D_LONGITUDE, "0");
    		editor.putString(Data.SP_D_CUSTOMER_NAME, "");
    		editor.putString(Data.SP_D_CUSTOMER_IMAGE, "");
    		editor.putString(Data.SP_D_CUSTOMER_PHONE, "");
    		editor.putString(Data.SP_D_CUSTOMER_RATING, "");
    		
    		
    		
    		
    		editor.putString(Data.SP_TOTAL_DISTANCE, "0");
    		editor.putString(Data.SP_WAIT_TIME, "0");
    		editor.putString(Data.SP_RIDE_TIME, "0");
    		editor.putString(Data.SP_LAST_LATITUDE, "0");
    		editor.putString(Data.SP_LAST_LONGITUDE, "0");
    		
    		
    		
    		
    		editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");

    		editor.putString(Data.SP_C_SESSION_ID, "");
    		editor.putString(Data.SP_C_ENGAGEMENT_ID, "");
    		editor.putString(Data.SP_C_DRIVER_ID, "");
    		editor.putString(Data.SP_C_LATITUDE, "0");
    		editor.putString(Data.SP_C_LONGITUDE, "0");
    		editor.putString(Data.SP_C_DRIVER_NAME, "");
    		editor.putString(Data.SP_C_DRIVER_IMAGE, "");
    		editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, "");
    		editor.putString(Data.SP_C_DRIVER_PHONE, "");
			editor.putString(Data.SP_C_DRIVER_RATING, "");
    		editor.putString(Data.SP_C_DRIVER_DISTANCE, "0");
    		editor.putString(Data.SP_C_DRIVER_DURATION, "");
    		
    		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
    		editor.putString(Data.SP_C_TOTAL_FARE, "0");
    		editor.putString(Data.SP_C_WAIT_TIME, "0");
        		
        	
        	editor.commit();
    		
        	
        	Database database = new Database(HomeActivity.this);
			database.deleteSavedPath();
			database.close();
        	
		
			}
		}).start();
	}
	


	@Override
	public void customerEndRideInterrupt() {
		try{
			if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						lastLocation = null;
						clearSPData();
						map.clear();
						passengerScreenMode = PassengerScreenMode.P_RIDE_END;
						switchPassengerScreen(passengerScreenMode);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	static class FBLogoutNoIntent extends AsyncTask<Void, Void, String> {
		
		Activity act;
		
		public FBLogoutNoIntent(Activity act){
			this.act = act;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		protected String doInBackground(Void... urls) {
			try {
				//deactivating current session of FB
				try {	
					Session session = new Session(act);
					Session.setActiveSession(session);	
					session.closeAndClearTokenInformation();	
				}
				catch(Exception e) {
					Log.v("Logout", "Error"+e);	
				}
			
				SharedPreferences pref = act.getSharedPreferences("myPref", 0);
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			} catch (Exception e) {
				Log.v("e", e.toString());

			}
			
			return "";
		}

		@Override
		protected void onPostExecute(String text) {
			
		}
	}

	public static void logoutUser(final Activity cont){
		try{
			
			new FBLogoutNoIntent(cont).execute();
			
			PicassoTools.clearCache(Picasso.with(cont));
			
			cont.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
					Editor editor = pref.edit();
					editor.clear();
					editor.commit();
					Data.clearDataOnLogout(cont);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(cont);
					builder.setMessage(cont.getResources().getString(R.string.your_login_session_expired)).setTitle(cont.getResources().getString(R.string.alert));
					builder.setCancelable(false);
			        builder.setPositiveButton(cont.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			                    @Override
			                    public void onClick(DialogInterface dialog, int which) {
			                    	try {
			                			dialog.dismiss();
			                			cont.startActivity(new Intent(cont, SplashLogin.class));
			                			cont.finish();
			                			cont.overridePendingTransition(
			                					R.anim.left_in,
			                					R.anim.left_out);
			                		} catch (Exception e) {
			                			Log.i("excption logout",
			                					e.toString());
			                		}
			                    }
			                });
			        
			        AlertDialog alertDialog = builder.create();
			        alertDialog.show();
				}
			});
        
		} catch(Exception e){e.printStackTrace();}
		
	}
	
	
	//Invalid access token
	public void backgroundThread(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Do background work here
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// Update UI here
						
					}
				});
				
				
			}
		}).start();
	
	}
	
	
	public Handler automaticReviewHandler;
	public Runnable automaticReviewRunnable;
	
	public void startAutomaticReviewHandler(){
		try {
			stopAutomaticReviewhandler();
			automaticReviewHandler = new Handler();
			automaticReviewRunnable = new Runnable() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_RIDE_END){
								GCMIntentService.clearNotifications(HomeActivity.this);
								submitReviewAsync(HomeActivity.this, Data.dEngagementId, "0", Data.dCustomerId, "5");
							}
							else if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_RIDE_END){
								GCMIntentService.clearNotifications(HomeActivity.this);
								submitReviewAsync(HomeActivity.this, Data.cEngagementId, "1", Data.cDriverId, "5");
							}
						}
					});
				}
			};
			
			automaticReviewHandler.postDelayed(automaticReviewRunnable, AUTO_RATING_DELAY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopAutomaticReviewhandler(){
		try {
			if(automaticReviewHandler != null && automaticReviewRunnable != null){
				automaticReviewHandler.removeCallbacks(automaticReviewRunnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		automaticReviewHandler = null;
		automaticReviewRunnable = null;
	}
	
	
	
	
	
	
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("HomeActivity started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	
	
	
	public void initializeFusedLocationFetchers() {
		destroyFusedLocationFetchers();
		if(((userMode == UserMode.DRIVER) && (driverScreenMode != DriverScreenMode.D_IN_RIDE)) 
				|| ((userMode == UserMode.PASSENGER) && (passengerScreenMode != PassengerScreenMode.P_IN_RIDE))){
			Log.e("initializeFusedLocationFetchers", "userMode = "+userMode);
			Log.i("initializeFusedLocationFetchers", "driverScreenMode = "+driverScreenMode);
			Log.i("initializeFusedLocationFetchers", "passengerScreenMode = "+passengerScreenMode);
			if (myLocation == null) {
				if (lowPowerLF == null) {
					lowPowerLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 0);
				}
				if (highAccuracyLF == null) {
					highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
				}
			} 
			else {
				if (highAccuracyLF == null) {
					highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
				}
			}
		}
	}
	
	
	public void destroyFusedLocationFetchers(){
		destroyLowPowerFusedLocationFetcher();
		destroyHighAccuracyFusedLocationFetcher();
	}
	
	public void destroyLowPowerFusedLocationFetcher(){
		try{
			if(lowPowerLF != null){
				lowPowerLF.destroy();
				lowPowerLF = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void destroyHighAccuracyFusedLocationFetcher(){
		try{
			if(highAccuracyLF != null){
				highAccuracyLF.destroy();
				highAccuracyLF = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public void onLocationChanged(Location location, int priority) {
		Log.i("onLocationChanged interface ", "location = "+location);
		if(((userMode == UserMode.DRIVER) && (driverScreenMode != DriverScreenMode.D_IN_RIDE)) 
				|| ((userMode == UserMode.PASSENGER) && (passengerScreenMode != PassengerScreenMode.P_IN_RIDE))){
			if(priority == 0){
				if(location.getAccuracy() <= LOW_POWER_ACCURACY_CHECK){
					zoomToCurrentLocationAtFirstLocationFix(location);
					HomeActivity.myLocation = location;
					updatePickupLocation(location);
				}
			}
			else if(priority == 2){
				destroyLowPowerFusedLocationFetcher();
				if(location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK){
					zoomToCurrentLocationAtFirstLocationFix(location);
					HomeActivity.myLocation = location;
					updatePickupLocation(location);
				}
			}
		}
	}
	
	
	public void zoomToCurrentLocationAtFirstLocationFix(Location location){
		HomeActivity.myLocation = location;
		try {
			if(map != null){
				if(!zoomedToMyLocation){
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
					zoomedToMyLocation = true;
					if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(location.getLatitude(), 
								location.getLongitude()), false);
						getDistanceTimeAddress.execute();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 *  Updates PickupLocation on first location fix, when passenger state is P_ASSIGNING_DRIVERS 
	 * 	and saving it in shared preferences.
	 * @param location location changed in listeners
	 */
	public void updatePickupLocation(Location location){
		if(userMode == UserMode.PASSENGER){
			if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
				if(Data.pickupLatLng.latitude == 0 && Data.pickupLatLng.longitude == 0){
					Data.pickupLatLng = new LatLng(location.getLatitude(), location.getLongitude());
					SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
					Editor editor = pref.edit();
					editor.putString(Data.SP_C_SESSION_ID, Data.cSessionId);
					editor.putString(Data.SP_TOTAL_DISTANCE, "0");
					editor.putString(Data.SP_LAST_LATITUDE, ""+Data.pickupLatLng.latitude);
		    		editor.putString(Data.SP_LAST_LONGITUDE, ""+Data.pickupLatLng.longitude);
		    		editor.commit();
		    		
		    		if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
						getDistanceTimeAddress = null;
					}
					if(myLocation != null){
						getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
								myLocation.getLongitude()), false);
						getDistanceTimeAddress.execute();
					}
				}
			}
			else if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
				if("".equalsIgnoreCase(Data.assignedDriverInfo.durationToReach)){
					if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
					}
					if(myLocation != null){
						getDistanceTimeAddress = new GetDistanceTimeAddress(new LatLng(myLocation.getLatitude(), 
								myLocation.getLongitude()), true);
						getDistanceTimeAddress.execute();
					}
				}
			}
		}
	}
	
	
	
	
	public boolean checkWorkingTime(){
		return true;
//		try {
//			Calendar calendar = Calendar.getInstance();
//			int hour = calendar.get(Calendar.HOUR_OF_DAY);
//			
//			if(hour>=7 && hour<=21){
//				return true;
//			}
//			else{
//				new DialogPopup().alertPopup(HomeActivity.this, "", "We are currently operational between 7 am - 9 pm. " +
//						"We are increasing our operations hour to 24 hours soon");
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return true;
//		}
	}
	
//	SELECT * FROM `tb_updated_user_location` WHERE `user_id`=231 and `location_updated_timestamp` >= '2014-10-18' and `location_updated_timestamp` < '2014-10-20'
	
	
	//TODO new Request ride timerTask
    Timer timerRequestRide;
	TimerTask timerTaskRequestRide;
	long requestRideLifeTime;
	long serverRequestStartTime = 0;
	long serverRequestEndTime = 0;
	long executionTime = -1;
	long requestPeriod = 20000;
	
	public void startTimerRequestRide(){
		cancelTimerRequestRide();
		try {
			
			requestRideLifeTime = ((2 * 60 * 1000) + (1 * 60 * 1000));
			serverRequestStartTime = 0;
			serverRequestEndTime = 0;
			executionTime = -1;
			requestPeriod = 20000;
			
			timerRequestRide = new Timer();
			timerTaskRequestRide = new TimerTask() {
				@Override
				public void run() {
					
					long startTime = System.currentTimeMillis();
					
					try {
						Log.e("request_ride execution", "="+(serverRequestEndTime - executionTime));
						if(executionTime >= serverRequestEndTime){
							cancelTimerRequestRide();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
										noDriverAvailablePopup(HomeActivity.this, false);
										HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
								}
							});
						}
						else{
							if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
								ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
								nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
								nameValuePairs.add(new BasicNameValuePair("latitude", ""+Data.pickupLatLng.latitude));
								nameValuePairs.add(new BasicNameValuePair("longitude", "" + Data.pickupLatLng.longitude));
								
								if("".equalsIgnoreCase(Data.cSessionId)){
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "0"));
								}
								else{
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "1"));
								}
								
								
								String response = new HttpRequester().getJSONFromUrlParams(Data.SERVER_URL+"/request_ride", nameValuePairs);
								
								Log.i("response of request_ride", "="+response);
								
								if(response.contains(HttpRequester.SERVER_TIMEOUT)){
									Log.e("timeout","=");
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
//											new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
										}
									});
								}
								else{
									try{
										JSONObject jObj = new JSONObject(response);
										if(!jObj.isNull("error")){
											String errorMessage = jObj.getString("error");
											if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
												cancelTimerRequestRide();
												HomeActivity.logoutUser(activity);
											}
											else{
												
											}
										}
										else{
											int flag = jObj.getInt("flag");
											
											
											if(ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
												final String log = jObj.getString("log");
												Log.e("ASSIGNING_DRIVERS log", "="+log);
												final String start_time = jObj.getString("start_time");
												if(executionTime == -1){
													serverRequestStartTime = new DateOperations().getMilliseconds(start_time);
													serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
													long stopTime = System.currentTimeMillis();
												    long elapsedTime = stopTime - startTime;
													executionTime = serverRequestStartTime + elapsedTime;
												}
												
												Data.cSessionId = jObj.getString("session_id");
											}
											else if(ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag){
												if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
													cancelTimerRequestRide();
													fetchAcceptedDriverInfoAndChangeState(jObj);
												}
											}
											
//											response = {
//													"flag": constants.responseFlags.RIDE_STARTED,
//													"driver_id": driver_id, 
//													"user_name": result_driver[0].user_name, 
//													"phone_no": result_driver[0].phone_no,
//													            "user_image": result_driver[0].user_image, 
//													"driver_car_image": result_driver[0].driver_car_image, 
//													"driver_car_number": result_driver[0].driver_car_number,
//													            "rating": rating,
//													            "current_location_latitude": result_driver[0].current_location_latitude, 
//													"current_location_longitude": result_driver[0].current_location_longitude,
//													            "engagement_id": engagement_id,
//													            "session_id": session_id};};
											
											else if(ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag){
												
											}
											else if(ApiResponseFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag){
												final String log = jObj.getString("log");
												Log.e("NO_DRIVERS_AVAILABLE log", "="+log);
												cancelTimerRequestRide();
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
															noDriverAvailablePopup(HomeActivity.this, false);
															HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
															switchPassengerScreen(passengerScreenMode);
														}
													}
												});
											}
											
										}
									} catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					long stopTime = System.currentTimeMillis();
				    long elapsedTime = stopTime - startTime;
				    
				    if(executionTime != -1){
				    	if(elapsedTime >= requestPeriod){
				    		executionTime = executionTime + elapsedTime;
				    	}
				    	else{
				    		executionTime = executionTime + requestPeriod;
				    	}
				    }
				    Log.i("request_ride execution", "="+(serverRequestEndTime - executionTime));
					
				}
			};
			
			timerRequestRide.scheduleAtFixedRate(timerTaskRequestRide, 0, requestPeriod);
			startAssigningDriversAnimation();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelTimerRequestRide(){
		stopAssigningDriversAnimation();
		try{
			if(timerTaskRequestRide != null){
				timerTaskRequestRide.cancel();
				timerTaskRequestRide = null;
			}
			if(timerRequestRide != null){
				timerRequestRide.cancel();
				timerRequestRide.purge();
				timerRequestRide = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void startAssigningDriversAnimation(){
		try{
			Animation myFadeInAnimation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.tween);
			requestRideBtn.startAnimation(myFadeInAnimation);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void stopAssigningDriversAnimation(){
		try{
			requestRideBtn.clearAnimation();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	


	@Override
	public void onNoDriversAvailablePushRecieved(final String logMessage) {
		cancelTimerRequestRide();
		if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING && !customerCancelPressed){
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					noDriverAvailablePopup(HomeActivity.this, false);
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
				}
			});
		}
	}

	
	
	
	
	
	

	
	public void handleConnectionLost() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Data.userData != null){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
						}
					});
					int currentUserStatus = 0;
					if(UserMode.DRIVER == userMode){
						currentUserStatus = 1;
					}
					else if(UserMode.PASSENGER == userMode){
						currentUserStatus = 2;
					}
					if(currentUserStatus != 0){
						String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
						if(resp.contains(HttpRequester.SERVER_TIMEOUT)){
							String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(resp.contains(HttpRequester.SERVER_TIMEOUT)){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new DialogPopup().alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
									}
								});
							}
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							startUIAfterGettingUserStatus();
						}
					});
				}
				//TODO refresh application on connection lost
			}
		}).start();
	}
	
	
	
}