package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HomeActivity extends FragmentActivity implements DetectRideStart, RefreshDriverLocations, RequestRideInterrupt, 
DriverChangeRideRequest, DriverStartRideInterrupt, CustomerEndRideInterrupt {

	
	
	DrawerLayout drawerLayout;
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	ProgressBar profileImgProgress;
	ImageView profileImg;
	TextView userName;
	
	RelativeLayout driverModeRl;
	TextView driverModeText;
	ImageView driverModeToggle;
	
	RelativeLayout inviteFriendRl;
	TextView inviteFriendText;
	
	RelativeLayout helpRl;
	TextView helpText;
	
	RelativeLayout aboutRl;
	TextView aboutText;
	
	RelativeLayout logoutRl;
	TextView logoutText;
	
	
	
	//Favorite bar
	LinearLayout favoriteRl;
	ListView favoriteList;
	ProgressBar favoriteProgress;
	TextView noFavoriteLocationsText, favTitleText;
	FavoriteListAdapter favoriteListAdapter;
	
	
	
	//Top RL
	RelativeLayout topRl;
	Button menuBtn, backBtn, favBtn;
	TextView title;
	ImageView jugnooLogo;
	
	
	
	//Passenger main layout
	RelativeLayout passengerMainLayout;
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	//Initial layout
	RelativeLayout initialLayout;
	EditText searchEt;
	Button search, myLocationBtn, requestRideBtn, initialCancelRideBtn;
	RelativeLayout nearestDriverRl;
	TextView nearestDriverText;
	ProgressBar nearestDriverProgress;
	
	
	//Before request final layout
	RelativeLayout beforeRequestFinalLayout;
	Button cancelRequestBtn;
	ProgressBar assignedDriverProgress;
	TextView assignedDriverText;
	
	
	//Request Final Layout
	RelativeLayout requestFinalLayout;
	ProgressBar driverImageProgress, driverCarProgress;
	ImageView driverImage, driverCarImage;
	TextView driverName, driverTime;
	Button callDriverBtn;
	TextView inRideRideInProgress;
	
	
	
	
	
	
	
	
	//Search layout
	RelativeLayout searchLayout;
	ListView searchList;
	SearchListAdapter searchListAdapter;
	
	
	
	
	
	
	
	//Center Location Layout
	RelativeLayout centreLocationRl, centreInfoRl;
	ProgressBar centreInfoProgress;
	TextView centreLocationSnippet;
	Button setFavoriteBtn;
	
	
	
	
	
	//On Map
	TextView distance;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Driver main layout 
	RelativeLayout driverMainLayout;
	
	
	//Driver initial layout
	RelativeLayout driverInitialLayout;
	RelativeLayout driverNewRideRequestRl;
	TextView driverNewRideRequestText;
	TextView driverNewRideRequestClickText;
	Button driverInitialMyLocationBtn;
	
	
	
	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn;
	
	
	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;
	
	TextView driverPassengerName;
	TextView driverPassengerRatingValue;
	Button driverPassengerCallBtn;
	ProgressBar driverPassengerImageProgress;
	ImageView driverPassengerImage;
	
	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideMyLocationBtn;
	TextView driverStartRideText;
	SlideButton driverStartRideSlider;
	Button driverCancelRideBtn;
	
	//End ride layout
	RelativeLayout driverInRideMainRl;
	PausableChronometer waitChronometer;
	Button driverWaitBtn;
	TextView driverEndRideText;
	SlideButtonInvert driverEndRideSlider;
	int waitStart = 2;
	
	
	
	
	//Review layout
	RelativeLayout endRideReviewRl;
		
	ImageView reviewUserImgBlured, reviewUserImage;
	ProgressBar reviewUserImgProgress;
	TextView reviewUserName, reviewUserRating, reviewReachedDestinationText, reviewDistanceText, reviewDistanceValue, reviewFareText, reviewFareValue, reviewRatingText;
	RatingBar reviewRatingBar;
	Button reviewSubmitBtn;
	
	
	
	
	PowerManager.WakeLock mWakeLock;
	
	
	
	
	ArrayList<Polyline> polyLinesAL = new ArrayList<Polyline>();
	
	ArrayList<Location> locations = new ArrayList<Location>();
	
	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>(); 
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	double totalDistance = 0, totalFare = 0;
	String fullAddress = "";
	
	
	
	static boolean startTracking = true;
	static Location myLocation;
	
	
	
	LocationManager locationManager;
	
	static PassengerScreenMode passengerScreenMode;
	
	static UserMode userMode;
	
	static DriverScreenMode driverScreenMode;
	
	
	GetDistanceTimeAddress getDistanceTimeAddress;
	
	BeforeCancelRequestAsync beforeCancelRequestAsync;
	
	
	Marker pickupLocationMarker;
	
	static DriverChangeRideRequest driverGetRequestPush;
	
	static DriverStartRideInterrupt driverStartRideInterrupt;
	
	static CustomerEndRideInterrupt customerEndRideInterrupt;
	
	static Activity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		CStartRideService.detectRideStart = HomeActivity.this;
		CUpdateDriverLocationsService.refreshDriverLocations = HomeActivity.this;
		CRequestRideService.requestRideInterrupt = HomeActivity.this;
		
		HomeActivity.driverGetRequestPush = HomeActivity.this;
		HomeActivity.driverStartRideInterrupt = HomeActivity.this;
		HomeActivity.customerEndRideInterrupt = HomeActivity.this;
		
		activity = this;
		
		
		startTracking = false;
		
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		
		
		
		
		new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);
		
		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		profileImgProgress = (ProgressBar) findViewById(R.id.profileImgProgress);
		profileImg = (ImageView) findViewById(R.id.profileImg);
		userName = (TextView) findViewById(R.id.userName);
		
		driverModeRl = (RelativeLayout) findViewById(R.id.driverModeRl);
		driverModeText = (TextView) findViewById(R.id.driverModeText);
		driverModeToggle = (ImageView) findViewById(R.id.driverModeToggle);
		
		inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
		inviteFriendText = (TextView) findViewById(R.id.inviteFriendText);
		
		helpRl = (RelativeLayout) findViewById(R.id.helpRl);
		helpText = (TextView) findViewById(R.id.helpText);
		
		aboutRl = (RelativeLayout) findViewById(R.id.aboutRl);
		aboutText = (TextView) findViewById(R.id.aboutText);
		
		logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
		logoutText = (TextView) findViewById(R.id.logoutText);
		
		
		//Favorite bar
		favoriteRl = (LinearLayout) findViewById(R.id.favoriteRl);
		favoriteList = (ListView) findViewById(R.id.favoriteList);
		favoriteListAdapter = new FavoriteListAdapter();
		favoriteList.setAdapter(favoriteListAdapter);
		favoriteProgress = (ProgressBar) findViewById(R.id.favoriteProgress);
		favoriteProgress.setVisibility(View.GONE);
		noFavoriteLocationsText = (TextView) findViewById(R.id.noFavoriteLocationsText);
		noFavoriteLocationsText.setVisibility(View.GONE);
		favTitleText = (TextView) findViewById(R.id.favTitleText);
		
		
		
		
		//Top RL
		topRl = (RelativeLayout) findViewById(R.id.topRl);
		menuBtn = (Button) findViewById(R.id.menuBtn);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		jugnooLogo = (ImageView) findViewById(R.id.jugnooLogo);
		favBtn = (Button) findViewById(R.id.favBtn);
		
		
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
		
		searchEt = (EditText) findViewById(R.id.searchEt);
		search = (Button) findViewById(R.id.search);
		
		myLocationBtn = (Button) findViewById(R.id.myLocationBtn);
		requestRideBtn = (Button) findViewById(R.id.requestRideBtn);
		initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn);
		
		nearestDriverRl = (RelativeLayout) findViewById(R.id.nearestDriverRl);
		nearestDriverText = (TextView) findViewById(R.id.nearestDriverText);
		nearestDriverProgress = (ProgressBar) findViewById(R.id.nearestDriverProgress);
		
		
		
		
		
		//Before request final layout
		beforeRequestFinalLayout = (RelativeLayout) findViewById(R.id.beforeRequestFinalLayout);

		cancelRequestBtn = (Button) findViewById(R.id.cancelRequestBtn);

		assignedDriverText = (TextView) findViewById(R.id.assignedDriverText);
		assignedDriverProgress = (ProgressBar) findViewById(R.id.assignedDriverProgress);
		
		
		
		
		//Request Final Layout
		requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);
		
		driverImageProgress = (ProgressBar) findViewById(R.id.driverImageProgress);
		driverCarProgress = (ProgressBar) findViewById(R.id.driverCarProgress);
		driverImage = (ImageView) findViewById(R.id.driverImage);
		driverCarImage = (ImageView) findViewById(R.id.driverCarImage);
		
		driverName = (TextView) findViewById(R.id.driverName);
		driverTime = (TextView) findViewById(R.id.driverTime);
		callDriverBtn = (Button) findViewById(R.id.callDriverBtn);
		inRideRideInProgress = (TextView) findViewById(R.id.inRideRideInProgress);
		
		
		
		
		
		
		

		
		
		//Center location layout
		centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
		centreInfoRl = (RelativeLayout) findViewById(R.id.centreInfoRl);
		centreInfoProgress = (ProgressBar) findViewById(R.id.centreInfoProgress);
		centreLocationSnippet = (TextView) findViewById(R.id.centreLocationSnippet);
		setFavoriteBtn = (Button) findViewById(R.id.setFavoriteBtn);
		
		
		
		
		
		
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
		driverNewRideRequestText = (TextView) findViewById(R.id.driverNewRideRequestText);
		driverNewRideRequestClickText = (TextView) findViewById(R.id.driverNewRideRequestClickText);
		driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
		
		
		
		// Driver Request Accept layout
		driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
		driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn);
		driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn);
		driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
		
		
		
		// Driver engaged layout
		driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
		

		driverPassengerName = (TextView) findViewById(R.id.driverPassengerName);
		driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue);
		driverPassengerCallBtn = (Button) findViewById(R.id.driverPassengerCallBtn);
		driverPassengerImageProgress = (ProgressBar) findViewById(R.id.driverPassengerImageProgress);
		driverPassengerImage = (ImageView) findViewById(R.id.driverPassengerImage);
		
		//Start ride layout
		driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
		driverStartRideMyLocationBtn = (Button) findViewById(R.id.driverStartRideMyLocationBtn);
		driverStartRideText = (TextView) findViewById(R.id.driverStartRideText);
		driverStartRideSlider = (SlideButton) findViewById(R.id.driverStartRideSlider);
		driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn);
		
		
		//End ride layout
		driverInRideMainRl = (RelativeLayout) findViewById(R.id.driverInRideMainRl);
		waitChronometer = (PausableChronometer) findViewById(R.id.waitChronometer);
		driverWaitBtn = (Button) findViewById(R.id.driverWaitBtn);
		driverEndRideText = (TextView) findViewById(R.id.driverEndRideText);
		driverEndRideSlider = (SlideButtonInvert) findViewById(R.id.driverEndRideSlider);
		waitStart = 2;
		
		
		waitChronometer.setText("00:00:00");
		waitChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer cArg) {
						long time = SystemClock.elapsedRealtime() - cArg.getBase();
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
		reviewUserImgProgress = (ProgressBar) findViewById(R.id.reviewUserImgProgress);
		
		reviewUserName = (TextView) findViewById(R.id.reviewUserName);
		reviewUserRating = (TextView) findViewById(R.id.reviewUserRating);
		reviewReachedDestinationText = (TextView) findViewById(R.id.reviewReachedDestinationText);
		reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText);
		reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue);
		reviewFareText = (TextView) findViewById(R.id.reviewFareText);
		reviewFareValue = (TextView) findViewById(R.id.reviewFareValue);
		reviewRatingText = (TextView) findViewById(R.id.reviewRatingText);
		
		reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
		reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn);
		
		reviewRatingBar.setRating(0);
		
		
		
		
				 
		
		
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Top bar events
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
			}
		});
		
		
		favBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(favoriteRl);
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// menu events
		driverModeRl.setOnClickListener(new View.OnClickListener() {
			
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
		
		
		
		inviteFriendRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(menuLayout);
				startActivity(new Intent(HomeActivity.this, InviteFacebookFriendsActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		aboutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		helpRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		logoutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO logout api
				
				

				try {	
					Session session = new Session(HomeActivity.this);
					Session.setActiveSession(session);	
					session.closeAndClearTokenInformation();	
				}
				catch(Exception e) {
					Log.v("Logout", "Error"+e);	
				}
				
				Data.clearDataOnLogout(HomeActivity.this);
				
				startActivity(new Intent(HomeActivity.this, SplashLogin.class));
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
				
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
				
				if(requestRideBtn.getText().toString().equalsIgnoreCase("Request Ride")){
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						if(Data.driverInfos.size() > 0){
							requestRideBtn.setText("Assigning driver...");
							
							passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
							Data.cEngagementId = "";
							Data.mapTarget = map.getCameraPosition().target;
							
							switchPassengerScreen(passengerScreenMode);
							
							startService(new Intent(HomeActivity.this, CRequestRideService.class));
						}
						else{
							new DialogPopup().alertPopup(HomeActivity.this, "", "No driver available currently");
						}
					}
					else{
						new DialogPopup().alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
					}
				}
				
			}
		});
		
		
		initialCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelCustomerRequestAsync(HomeActivity.this, 0, 0);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer before final request layout events
		cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelCustomerRequestAsync(HomeActivity.this, 1, 0);
			}
		});
		
		beforeRequestFinalLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
		
		requestFinalLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer center location info layout events
		setFavoriteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveToFavoritePopup(HomeActivity.this, fullAddress, map.getCameraPosition().target);
			}
		});
		
		centreInfoRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver initial layout events
		driverNewRideRequestRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(Data.driverRideRequests.size() == 1){
					driverNewRideRequestRl.setVisibility(View.GONE);
					
					driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
					switchDriverScreen(driverScreenMode);
				}
					
				
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver accept layout events
		driverAcceptRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverAcceptRideAsync(HomeActivity.this);
			}
		});
		
		
		driverCancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverRejectRideAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver start ride layout events
		driverPassengerCallBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+Data.assignedCustomerInfo.phoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		driverStartRideSlider.setSlideButtonListener(new SlideButtonListener() {  
	        @Override
	        public void handleSlide() {
	        	driverStartRideText.setVisibility(View.GONE);
	        	driverStartRideAsync(HomeActivity.this);
	        }
	    });
		
		driverCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverRejectRideAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver in ride layout events 
		driverWaitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(waitStart == 2){
					waitChronometer.setBase(SystemClock.elapsedRealtime());
					waitChronometer.start();
					driverWaitBtn.setBackgroundResource(R.drawable.red_btn_selector);
					driverWaitBtn.setText("Stop wait");
					waitStart = 1;
				}
				else{
					if(waitStart == 1){
						waitChronometer.stop();
						driverWaitBtn.setBackgroundResource(R.drawable.blue_btn_selector);
						driverWaitBtn.setText("Start wait");
						waitStart = 0;
					}
					else if(waitStart == 0){
						waitChronometer.start();
						driverWaitBtn.setBackgroundResource(R.drawable.red_btn_selector);
						driverWaitBtn.setText("Stop wait");
						waitStart = 1;
					}
				}
				
			}
		});
		
		
		
		driverEndRideSlider.setSlideButtonListener(new SlideButtonInvertListener() {
			
			@Override
			public void handleSlide() {
				
				driverEndRideText.setVisibility(View.GONE);
				waitChronometer.stop();
				
				long elapsedMillis = SystemClock.elapsedRealtime() - waitChronometer.getBase();
				long seconds = elapsedMillis / 1000;
				double minutes = Math.ceil(((double)seconds) / 60.0);
				String min = (minutes > 9)? ""+minutes : "0"+minutes;
				
				Toast.makeText(getApplicationContext(), "wait = "+min, Toast.LENGTH_SHORT).show();
	        	
				driverEndRideAsync(HomeActivity.this, min);
				
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
						new DialogPopup().alertPopup(activity, "", "Please rate the customer.");
					}
					else if(userMode == UserMode.PASSENGER){
						new DialogPopup().alertPopup(activity, "", "Please rate the driver.");
					}
					
				}
			}
		});
		
		
		
		
		
		
		polyLinesAL = new ArrayList<Polyline>();
		
		locations = new ArrayList<Location>();
		
		
																	// map object initialized
		if(map != null){
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.getUiSettings().setAllGesturesEnabled(true);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.getChandigarhLatLng(), 12));
			
			
			map.setOnMyLocationChangeListener(myLocationChangeListener);
			
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
					else if(arg0.getTitle().equalsIgnoreCase("start ride location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else{
						
						int index = -1;
						
						for(int i=0; i<Data.driverRideRequests.size(); i++){
							if(Data.driverRideRequests.get(i).engagementId.equalsIgnoreCase(arg0.getTitle())){
								index = i;
								break;
							}
						}
						
						if(index != -1){
							Data.dCustomerId = Data.driverRideRequests.get(index).customerId;
							Data.dEngagementId = Data.driverRideRequests.get(index).engagementId;
							
							map.clear();
							
							MarkerOptions markerOptions = new MarkerOptions();
							markerOptions.title(Data.driverRideRequests.get(index).engagementId);
							markerOptions.snippet("");
							markerOptions.position(Data.driverRideRequests.get(index).latLng);
							markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.passenger));
							
							map.addMarker(markerOptions);
							
							driverNewRideRequestRl.setVisibility(View.GONE);
							
							driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
							switchDriverScreen(driverScreenMode);
						}
						
						
						
						
						
						
						return true;
					}
					
					
				}
			});
			
			
			
			
			
			
			
			
			
			new MapStateListener(map, mapFragment, this) {
				  @Override
				  public void onMapTouched() {
				    // Map touched
					  Log.e("onMapTouched","=onMapTouched");
				  }

				  @Override
				  public void onMapReleased() {
				    // Map released
					  Log.e("onMapReleased","=onMapReleased");
				  }

				  @Override
				  public void onMapUnsettled() {
				    // Map unsettled
					  Log.e("onMapUnsettled","=onMapUnsettled");
					  if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  centreInfoRl.setVisibility(View.INVISIBLE);
						  centreInfoProgress.setVisibility(View.GONE);
					  }
				  }

				  @Override
				  public void onMapSettled() {
				    // Map settled
					  Log.e("onMapSettled","=onMapSettled");
					  if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  getDistanceTimeAddress = new GetDistanceTimeAddress(map.getCameraPosition().target);
						  getDistanceTimeAddress.execute();
					  }
				  }
				};
			
			
			
			
			
			myLocationBtn.setOnClickListener(mapMyLocationClick);
			driverInitialMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverRequestAcceptMyLocationBtn.setOnClickListener(mapMyLocationClick);
			
		}
		
		
		
		
		
		
		
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		
		
		
		

		userMode = UserMode.PASSENGER;
		passengerScreenMode = PassengerScreenMode.P_INITIAL;
		driverScreenMode = DriverScreenMode.D_INITIAL;
		
		
		if(userMode == UserMode.DRIVER){
			driverModeToggle.setImageResource(R.drawable.on);
		}
		else{
			driverModeToggle.setImageResource(R.drawable.off);
		}
		
		switchUserScreen(userMode);
		if(userMode == UserMode.DRIVER){
			switchDriverScreen(driverScreenMode);
		}
		else{
			switchPassengerScreen(passengerScreenMode);
		}
		
		
		
	  
		
		setUserData();
		
		getAllFavoriteAsync(HomeActivity.this);
		
	}
	
	
	OnClickListener mapMyLocationClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(myLocation != null){
				map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
			}
			else{
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	public void setUserData(){
		
		userName.setText(Data.userData.userName);
		
		AQuery aq = new AQuery(profileImg);
		aq.id(profileImg).progress(profileImgProgress).image(Data.userData.userImage, Data.imageOptionsFullRound());
		
		
	}
	
	
	
	
	public void switchUserScreen(UserMode mode){
		
		if(getDistanceTimeAddress != null){
			getDistanceTimeAddress.cancel(true);
			getDistanceTimeAddress = null;
		}
		
		stopService(new Intent(HomeActivity.this, CRequestRideService.class));
        stopService(new Intent(HomeActivity.this, CStartRideService.class));
        stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
        stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
        
		
		switch(mode){
		
			case DRIVER:
				passengerMainLayout.setVisibility(View.GONE);
				driverMainLayout.setVisibility(View.VISIBLE);
				
				favBtn.setVisibility(View.GONE);
				
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, favoriteRl);
				
				break;
			
				
				
				
			case PASSENGER:
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
				favBtn.setVisibility(View.VISIBLE);
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				
				break;
			
				
				
				
			default:
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
				favBtn.setVisibility(View.VISIBLE);
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		
		
		}
		
	}
	
	
	class LoadBlurImage extends AsyncTask<Bitmap, Integer, String>{

		Bitmap bitmap;
		String imageLink;
		
		public LoadBlurImage(String imageLink){
			this.imageLink = imageLink;
		}
		
		@Override
		protected String doInBackground(Bitmap... params) {
			try{
				bitmap = params[0];
				bitmap = new BlurImage().fastblur(bitmap, 3);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if(bitmap != null){
				reviewUserImgBlured.setImageBitmap(bitmap);
			}
			AQuery aq = new AQuery(reviewUserImage);
			aq.id(reviewUserImage).progress(reviewUserImgProgress).image(imageLink, Data.imageOptionsFullRound());
			
		}
	}
	
	
	
	
	
	public void switchDriverScreen(DriverScreenMode mode){
		if(mode == DriverScreenMode.D_RIDE_END){
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			double totalDistanceInKm = totalDistance/1000.0;
			
			
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			reviewFareValue.setText("Rs. "+decimalFormat.format(totalFare));
			
			reviewRatingText.setText("Customer Rating");
			
			reviewUserRating.setText("3.0");
			reviewUserName.setText(Data.assignedCustomerInfo.name);
			
			AQuery aquery = new AQuery(reviewUserImgBlured);
			aquery.id(reviewUserImgBlured).image(Data.assignedCustomerInfo.image, true, true, 0, 0, new BitmapAjaxCallback(){

			        @Override
			        public void callback(String url, ImageView ivView, Bitmap bmBitmap, com.androidquery.callback.AjaxStatus status){
			                new LoadBlurImage(Data.assignedCustomerInfo.image).execute(bmBitmap);
			        }
			        
			});
			
			
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey_opaque));
		}
		
		switch(mode){
		
			case D_INITIAL:
				
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
			
				break;
				
				
			case D_REQUEST_ACCEPT:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.VISIBLE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				
			
				break;
				
				
				
			case D_START_RIDE:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverStartRideSlider.setProgress(0);
				driverStartRideText.setVisibility(View.VISIBLE);
				
				driverStartRideMainRl.setVisibility(View.VISIBLE);
				driverInRideMainRl.setVisibility(View.GONE);
				
				driverPassengerName.setText(Data.assignedCustomerInfo.name);
				AQuery aq = new AQuery(driverPassengerImage);
				aq.id(driverPassengerImage).progress(driverPassengerImageProgress).image(Data.assignedCustomerInfo.image, Data.imageOptionsRound());
				
				
			
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				break;
				
				
			case D_IN_RIDE:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverEndRideSlider.setProgress(100);
				driverEndRideText.setVisibility(View.VISIBLE);
				
				driverStartRideMainRl.setVisibility(View.GONE);
				driverInRideMainRl.setVisibility(View.VISIBLE);
				
			
				break;
				
				
			case D_RIDE_END:
				
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
	
	
	
	public void switchPassengerScreen(PassengerScreenMode mode){
		
		
		if(mode == PassengerScreenMode.P_RIDE_END){
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			double totalDistanceInKm = Data.totalDistance;
			
			
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			reviewFareValue.setText("Rs. "+decimalFormat.format(Data.totalFare));
			
			reviewRatingText.setText("Driver Rating");
			
			reviewUserRating.setText("3.0");
			reviewUserName.setText(Data.assignedDriverInfo.name);
			
			AQuery aquery = new AQuery(reviewUserImgBlured);
			aquery.id(reviewUserImgBlured).image(Data.assignedDriverInfo.image, true, true, 0, 0, new BitmapAjaxCallback(){

			        @Override
			        public void callback(String url, ImageView ivView, Bitmap bmBitmap, com.androidquery.callback.AjaxStatus status){
			                new LoadBlurImage(Data.assignedDriverInfo.image).execute(bmBitmap);
			        }
			        
			});
			
			
			
			
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey_opaque));
		}
		
		
		switch(mode){
		
			case P_INITIAL:

				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				nearestDriverRl.setVisibility(View.VISIBLE);
				initialCancelRideBtn.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				startService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
				
				break;
				
				
			case P_ASSIGNING:
				
				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				if(map != null){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.mapTarget);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ball1));
					
					pickupLocationMarker = map.addMarker(markerOptions);
				}
				
				
				nearestDriverRl.setVisibility(View.GONE);
				initialCancelRideBtn.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
				
				break;
				
		
			case P_SEARCH:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.VISIBLE);
				
				
				menuBtn.setVisibility(View.GONE);
				jugnooLogo.setVisibility(View.GONE);
				backBtn.setVisibility(View.VISIBLE);
				title.setVisibility(View.VISIBLE);
				favBtn.setVisibility(View.GONE);
				
				title.setText("Search results");
				
				searchListAdapter.notifyDataSetChanged();

				
				break;
				
				
				
			case P_BEFORE_REQUEST_FINAL:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				
				break;
				
				
				
			case P_REQUEST_FINAL:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				requestFinalLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
				driverTime.setVisibility(View.VISIBLE);
				inRideRideInProgress.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
				
				
			case P_IN_RIDE:
				
				
				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				requestFinalLayout.setBackgroundColor(getResources().getColor(R.color.white_dark1));
				driverTime.setVisibility(View.GONE);
				inRideRideInProgress.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
			case P_RIDE_END:
				
				startTracking = false;
				
				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
				
			default:

				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				
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
	
	
	GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			
			Log.e("event","=="+event);
			
//			switch(event){
//				case GpsStatus.GPS_EVENT_FIRST_FIX:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_FIRST_FIX "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_SATELLITE_STATUS "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_STARTED:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_STARTED "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_STOPPED:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_STOPPED "+event, Toast.LENGTH_SHORT).show();
//					break;
//			
//				default:
//					Toast.makeText(getApplicationContext(), " "+event, Toast.LENGTH_SHORT).show();
//			
//			}
			
			
			
			
		}
	};
	
	
	
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e("locationListener onStatusChanged","= provider = "+provider + ", status = "+status+ ", extras = "+extras);
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Log.e("locationListener onProviderEnabled","="+provider);
			if(map != null){
				map.setMyLocationEnabled(true);
			}
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Log.e("locationListener onProviderDisabled","="+provider);
			if(map != null){
				map.setMyLocationEnabled(false);
			}
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.e("locationListener location changed","="+location);
		}
	};
	
	
	
	void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("The app needs active GPS connection. Do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	
	@Override
	protected void onResume() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
		super.onResume();
		
		

		if(locationManager == null){
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

	    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        buildAlertMessageNoGps();
	    }
	    else{
	    	locationManager.addGpsStatusListener(gpsStatusListener);
	    }

		
	    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
	    }
	    else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
	    }
	    
	    
	    
	    
	}
	
	
	@Override
	protected void onPause() {
		this.mWakeLock.release();
		
		locationManager.removeGpsStatusListener(gpsStatusListener);
		
		locationManager.removeUpdates(locationListener);
		
		super.onPause();
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		if(passengerScreenMode == PassengerScreenMode.P_SEARCH){
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
			switchPassengerScreen(passengerScreenMode);
		}
	}
	
	    
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        
        Data.locationFetcher.destroy();
        
        ASSL.closeActivity(drawerLayout);
        stopService(new Intent(HomeActivity.this, CRequestRideService.class));
        stopService(new Intent(HomeActivity.this, CStartRideService.class));
        stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
        
        System.gc();
        
    }
	
	
	OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {
		
		@Override
		public void onMyLocationChange(Location location) {
//			Log.e("location","=="+location);
			
			if(HomeActivity.myLocation == null){
				map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			}
			HomeActivity.myLocation = location;
			
			
			if(driverScreenMode == DriverScreenMode.D_IN_RIDE){
			
			LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			Log.e("locations.size()", "="+locations.size());
			if(locations.size() > 0){
				
				LatLng lastLatLng = new LatLng(locations.get(locations.size()-1).getLatitude(), locations.get(locations.size()-1).getLongitude());
				
				double displacement = distance(lastLatLng, currentLatLng);
				Log.e("displacement", "="+displacement);
				
				totalDistance = totalDistance + displacement;
				
				Polyline line = map.addPolyline(new PolylineOptions()
		        .add(lastLatLng, currentLatLng)
		        .width(5)
		        .color(Color.RED).geodesic(true));

		        polyLinesAL.add(line);
				
				
			}
			else if(locations.size() == 0){
				totalDistance = 0;
				
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.snippet("");
				markerOptions.title("start ride location");
				markerOptions.position(currentLatLng);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ball1));
				map.addMarker(markerOptions);
			}
			
			map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
			
			
			distance.setText("Distance = "+decimalFormat.format(totalDistance));
			

			locations.add(location);
			
			}
		}
	};
	
	
	double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

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
		public View getView(final int position, View convertView, ViewGroup parent) {

			
			if (convertView == null) {
				
				holder = new ViewHolderSearch();
				convertView = mInflater.inflate(R.layout.search_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); 
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
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, 12), 2000, null);
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	class ViewHolderFavorite {
		TextView name;
		Button favDeleteBtn;
		LinearLayout relative;
		int id;
	}

	class FavoriteListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderFavorite holder;

		public FavoriteListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return Data.favoriteLocations.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {

			
			if (convertView == null) {
				
				holder = new ViewHolderFavorite();
				convertView = mInflater.inflate(R.layout.favorite_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); 
				holder.favDeleteBtn = (Button) convertView.findViewById(R.id.favDeleteBtn);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				holder.favDeleteBtn.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(495, 127));
				ASSL.DoMagic(holder.relative);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderFavorite) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+Data.favoriteLocations.get(position).name);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = Data.favoriteLocations.get(holder.id);
					
					Log.e("searchResult.latLng ==",">"+favoriteLocation.latLng);
					
					drawerLayout.closeDrawer(favoriteRl);
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(favoriteLocation.latLng, 12), 2000, null);
					
				}
			});
			
			
			
			holder.favDeleteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = Data.favoriteLocations.get(holder.id);
					
					deleteFavoriteAsync(HomeActivity.this, favoriteLocation.sNo, holder.id);
					
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
	    
	    public GetDistanceTimeAddress(LatLng destination){
	    	stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
	    	this.distance = "";
	    	this.duration = "";
	    	this.destination = destination;
	    }
	    

	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	centreInfoRl.setVisibility(View.INVISIBLE);
				centreInfoProgress.setVisibility(View.VISIBLE);
	        	nearestDriverProgress.setVisibility(View.VISIBLE);
	 	        nearestDriverText.setVisibility(View.GONE);
	        }
	        else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	        	centreInfoRl.setVisibility(View.VISIBLE);
				centreInfoProgress.setVisibility(View.INVISIBLE);
	        	assignedDriverProgress.setVisibility(View.VISIBLE);
	        	assignedDriverText.setVisibility(View.GONE);
	        }
	        
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	try{
	    		
	    		Log.e("","");
	    		
	    		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
		    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    			nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
	    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+destination.latitude));
	    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+destination.longitude));
	    			Log.e("nameValuePairs ","="+nameValuePairs);
	    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
	    			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/find_a_driver", nameValuePairs);
	    			Log.e("result","="+result);
	    			simpleJSONParser = null;
	    			nameValuePairs = null;
	    			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
	    				Log.e("timeout","=");
	    			}
	    			else{
	    				Log.e("sucess","=");
	    				try{
	    					JSONObject jObj = new JSONObject(result);
	    						
	    					JSONArray data = jObj.getJSONArray("data");
	    						
	    					Data.driverInfos.clear();
	    						
	    					for(int i=0; i<data.length(); i++){
	    							
	    						JSONObject dataI = data.getJSONObject(i);
	    							
	    						String userId = dataI.getString("user_id");
	    						double latitude = dataI.getDouble("latitude");
	    						double longitude = dataI.getDouble("longitude");
	    							
	    						Data.driverInfos.add(new DriverInfo(userId, latitude, longitude));
	    					}
	    				}
	    				catch(Exception e){
	    					e.printStackTrace();
	    				}
	    			}
	    		
	    		}
	    		
	    		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	    			fullAddress = getAddress(destination.latitude, destination.longitude);
	    			Log.e("fullAddress",">"+fullAddress);
	    		}
	    		
	    		LatLng source = null;
				
	    		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	    			double minDistance = 999999999;
		    		for(int i=0; i<Data.driverInfos.size(); i++){
		    			if(distance(destination, Data.driverInfos.get(i).latLng) < minDistance){
		    				minDistance = distance(destination, Data.driverInfos.get(i).latLng);
		    				source = Data.driverInfos.get(i).latLng;
		    			}
		    		}
	    		}
	    		else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	    			source = Data.assignedDriverInfo.latLng;
	    		}
	    		
	    		
	    		if(source == null){
	    			return "error";
	    		}
	    		
	    		Log.e("source","="+source);
	    			
	    		this.url = makeURL(source, destination);
	    		Log.e("url","="+url);
	    		
		    	SimpleJSONParser jParser = new SimpleJSONParser();
		    	
		    	String response = jParser.getJSONFromUrl(url);
		    	
		    	JSONObject jsonObject = new JSONObject(response);
		    	
		    	
		    	String status = jsonObject.getString("status");
		    	
		    	if("OK".equalsIgnoreCase(status)){
		    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
		    		
		    		distance = element0.getJSONObject("distance").getString("text") ;
		    		
		    		duration = element0.getJSONObject("duration").getString("text");
		    		
		    		
		    		if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
		    			Data.assignedDriverInfo.distanceToReach = distance;
		    			Data.assignedDriverInfo.durationToReach = duration;
		    		}

		    		return "Distance: " + distance + "\n" + "Duration: " + duration;
		    		
		    	}
	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	
	        return "error";
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	 if(fullAddress != null && !"".equalsIgnoreCase(fullAddress)){
	 				centreLocationSnippet.setText(fullAddress);
	 				centreInfoRl.setVisibility(View.VISIBLE);
	 			}
	 			else{
	 				centreInfoRl.setVisibility(View.INVISIBLE);
	 			}
	 			centreInfoProgress.setVisibility(View.GONE);
	 			
	 			
	 			if(map != null){
					map.clear();
					for(int i=0; i<Data.driverInfos.size(); i++){
						DriverInfo driverInfo = Data.driverInfos.get(i);
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.title(""+driverInfo.userId);
						markerOptions.snippet(""+driverInfo.userId);
						markerOptions.position(driverInfo.latLng);
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_android));
						
						map.addMarker(markerOptions);
					}
					
				}
	 			
	 			
    		}
	        
	        
	        nearestDriverProgress.setVisibility(View.GONE);
	        assignedDriverProgress.setVisibility(View.GONE);
	        
	        nearestDriverText.setVisibility(View.VISIBLE);
	        assignedDriverText.setVisibility(View.VISIBLE);
	        
			
	        String distanceString = "";
	        
	        if(!"error".equalsIgnoreCase(result)){
		        
		        Log.i("passengerScreenMode","="+passengerScreenMode);
		        
		        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
		 	        
		 	        
		 	       if(!"".equalsIgnoreCase(duration) && !"".equalsIgnoreCase(distance)){
	       	 		distanceString = "Nearest driver is " + distance + " " + duration + " away.";
			        }
			        else if(!"".equalsIgnoreCase(duration)){
			        	distanceString = "Nearest driver is " + duration + " away.";
			        }
			        else if(!"".equalsIgnoreCase(distance)){
			        	distanceString = "Nearest driver is " + distance + " away.";
			        }
			        else{
			        	distanceString = "Could not find nearest driver's distance.";
			        }
		 	        
		 	       	nearestDriverText.setText(distanceString);
		 	       
		        }
		        else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
		        	
		        	
		        	if(!"".equalsIgnoreCase(duration) && !"".equalsIgnoreCase(distance)){
	        	 		distanceString = "Your ride is " + distance + " and will arrive \n in approximately " + duration + ".";
			        }
			        else{
			        	distanceString = "Could not find nearest driver's distance.";
			        }
		        	assignedDriverText.setText(distanceString);
		        	
		        }
		        
	        }
	        else{
	        	distanceString = "No drivers nearby.";
	        	nearestDriverText.setText("No drivers nearby.");
		        assignedDriverText.setText("No drivers nearby.");
	        }
	        
	        Log.i("distanceString","="+distanceString);
	        
	        
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	Data.mapTarget = destination;
	        	startService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
	        }
	        
	        if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	        	beforeCancelRequestAsync = new BeforeCancelRequestAsync();
	        	beforeCancelRequestAsync.execute();
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

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(ignr2, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.e("request result", response);
				new SimpleJSONParser().writeJSONToFile(response, "googlePlaceJson");
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

			@Override
			public void onFailure(Throwable arg0) {
				try {
					Log.e("request fail", arg0.getMessage().toString());
				} catch (Exception e) {
					Log.e("moving from", e.toString());
				}
				dialog.dismiss();
			}
		});
	}
	
	
	
	
	String getAddress(double curLatitude, double curLongitude) {
    	String fullAddress = "";

        try {

        	
        	Log.i("curLatitude ",">"+curLatitude);
        	Log.i("curLongitude ",">"+curLongitude);
        	
            JSONObject jsonObj = new JSONObject(new SimpleJSONParser().getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/json?" +
            		"latlng=" + curLatitude + "," + curLongitude 
            		+ "&sensor=true"));
            
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                fullAddress = zero.getString("formatted_address");

                Log.e("Results.length() ==","="+Results.length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fullAddress;
    }

	
	
	
	
	/**
	 * ASync for login from server
	 */
	public void getAssignedDriverInfoAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("driver_id", Data.cDriverId);

			Log.i("assigned driver info", "=");
			
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("driver_id", "=" + Data.cDriverId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/driver_details", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									
//									{
//									    "driver_data": {
//									        "user_name": "Ash Mah",
//									        "phone_no": "+919780211669",
//									        "driver_car_image": "",
//									        "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png",
//									        "latitude": 30.75,
//									        "longitude": 76.78,
//									        "rating": "NaN"
//									    }
//									}
									
									JSONObject driverData = jObj.getJSONObject("driver_data");
									
									Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, driverData.getDouble("latitude"), driverData.getDouble("longitude"), 
											driverData.getString("user_name"), driverData.getString("user_image"), driverData.getString("driver_car_image"), 
											driverData.getString("phone_no"));
									
									
									map.clear();
									
									passengerScreenMode = PassengerScreenMode.P_BEFORE_REQUEST_FINAL;
									switchPassengerScreen(passengerScreenMode);
									
									getDistanceTimeAddress = new GetDistanceTimeAddress(Data.mapTarget);
									getDistanceTimeAddress.execute();
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	class BeforeCancelRequestAsync extends AsyncTask<String, Integer, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.e("BeforeCancelRequestAsync","==onpre");
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			try{
				Thread.sleep(10000);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("BeforeCancelRequestAsync","==onpost");
			
			cancelCustomerRequestAsync(HomeActivity.this, 2, 1);
			
		}
		
	}
	
	
	
	
	/**
	 * ASync for cancelCustomerRequestAsync from server
	 */
	public void cancelCustomerRequestAsync(final Activity activity, final int switchCase, int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("driver_id", Data.cDriverId);
			params.put("engage_id", Data.cEngagementId);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("driver_id", "=" + Data.cDriverId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/cancel_the_req", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"cancelled sucessfully"}//result

									if(switchCase == 0){
										stopService(new Intent(HomeActivity.this, CRequestRideService.class));
										requestRideBtn.setText("Request Ride");
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
										
										
										if(map != null && pickupLocationMarker != null){
											pickupLocationMarker.remove();
										}
										
										getDistanceTimeAddress = new GetDistanceTimeAddress(map.getCameraPosition().target);
										getDistanceTimeAddress.execute();
									}
									else if(switchCase == 1){
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
										
										if(beforeCancelRequestAsync != null){
											beforeCancelRequestAsync.cancel(true);
											beforeCancelRequestAsync = null;
										}
										
										if(map != null && pickupLocationMarker != null){
											pickupLocationMarker.remove();
										}
										
										getDistanceTimeAddress = new GetDistanceTimeAddress(map.getCameraPosition().target);
										getDistanceTimeAddress.execute();
									
									}
									else{

										passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
										switchPassengerScreen(passengerScreenMode);
										
										
										driverName.setText(Data.assignedDriverInfo.name);
										driverTime.setText("Will arrive in "+Data.assignedDriverInfo.durationToReach);
										
										AQuery aq = new AQuery(driverImage);
										aq.id(driverImage).progress(driverImageProgress).image(Data.assignedDriverInfo.image, Data.imageOptionsRound());
										
										AQuery aq1 = new AQuery(driverCarImage);
										aq1.id(driverCarImage).progress(driverCarProgress).image(Data.assignedDriverInfo.carImage, Data.imageOptionsRound());
										
									}
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
	public void changeDriverModeAsync(final Activity activity, final int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("flag", "=" + flag);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/switch_to_driver_mode", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log": "Welcome to driver mode."}//success

									
									map.clear();
									
									
									if(flag == 1){
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
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
	public void driverAcceptRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Fetching user data...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("user_id", Data.dCustomerId);
			params.put("engage_id", Data.dEngagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("user_id", "=" + Data.dCustomerId);
			Log.i("engage_id", "=" + Data.dEngagementId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/accept_a_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{
//								    "user_data": {
//							        "user_name": "Tirthankar",
//							        "phone_no": "+919803879562",
//							        "user_image": "http://graph.facebook.com/100001398069768/picture?width=160&height=160"
//							    }
//							}

									
									JSONObject userData = jObj.getJSONObject("user_data");
									
									Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, userData.getString("user_name"),
											userData.getString("user_image"), userData.getString("phone_no"));
									
									
									
									
									Data.driverRideRequests.clear();
									
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
	public void driverRejectRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("user_id", Data.dCustomerId);
			params.put("engage_id", Data.dEngagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("user_id", "=" + Data.dCustomerId);
			Log.i("engage_id", "=" + Data.dEngagementId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/reject_a_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"rejected successfully"}

									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
									
									driverScreenMode = DriverScreenMode.D_INITIAL;
									switchDriverScreen(driverScreenMode);
									
									
									int index = -1;
									for(int i=0; i<Data.driverRideRequests.size(); i++){
										if(Data.driverRideRequests.get(i).engagementId.equalsIgnoreCase(Data.dEngagementId)){
											index = i;
											break;
										}
									}
									
									if(index != -1){
										Data.driverRideRequests.remove(index);
									}
									
									showAllRideRequests();
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
	public void driverStartRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/start_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									driverStartRideSlider.setProgress(0);
									driverStartRideText.setVisibility(View.VISIBLE);
								}
								else{
									
//									{"log":"ride_started"}

									new DialogPopup().alertPopup(activity, "", "Ride started");

									locations.clear();
									
						        	driverScreenMode = DriverScreenMode.D_IN_RIDE;
									switchDriverScreen(driverScreenMode);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								driverStartRideSlider.setProgress(0);
								driverStartRideText.setVisibility(View.VISIBLE); 
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							driverStartRideSlider.setProgress(0);
							driverStartRideText.setVisibility(View.VISIBLE);
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			driverStartRideSlider.setProgress(0);
			driverStartRideText.setVisibility(View.VISIBLE);
		}

	}
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, String waitMinutes) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}
			
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			double totalDistanceInKm = totalDistance/1000.0;
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
			params.put("wait_time", waitMinutes);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			Log.i("latitude", "="+Data.latitude);
			Log.i("longitude", "="+Data.longitude);
			Log.i("distance_travelled", "="+(totalDistance / 1000.0));
			Log.i("wait_time", "="+waitMinutes);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/end_ride", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									driverEndRideSlider.setProgress(100);
									driverEndRideText.setVisibility(View.VISIBLE);
								}
								else{
									
//									{"log":"ride_ended"}//result

									try{
										totalFare = jObj.getDouble("fare");
									} catch(Exception e){
										e.printStackTrace();
										totalFare = 0;
									}
									
									new DialogPopup().alertPopup(activity, "", "Ride ended.");

									locations.clear();
									
									map.clear();
									
									for(Polyline polyline : polyLinesAL){
										polyline.remove();
									}
									
									polyLinesAL.clear();
									
						        	driverScreenMode = DriverScreenMode.D_RIDE_END;
									switchDriverScreen(driverScreenMode);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								driverEndRideSlider.setProgress(100);
								driverEndRideText.setVisibility(View.VISIBLE);
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							driverEndRideSlider.setProgress(100);
							driverEndRideText.setVisibility(View.VISIBLE);
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			driverEndRideSlider.setProgress(100);
			driverEndRideText.setVisibility(View.VISIBLE);
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
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/rating", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Rated successfully"}

									
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));

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
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setText("Save to favorite");
			final EditText favoriteNameEt = (EditText) dialog.findViewById(R.id.favoriteNameEt);
			
			favoriteNameEt.setText(locationName);
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn);
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					saveToFavoriteAsync(activity, favoriteNameEt.getText().toString(), favLatLng);
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
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
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
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	void updateFavoriteList(){
		
		favoriteListAdapter.notifyDataSetChanged();
		
		if(Data.favoriteLocations.size() == 0){
			noFavoriteLocationsText.setVisibility(View.VISIBLE);
		}
		else{
			noFavoriteLocationsText.setVisibility(View.GONE);
		}
		
	}
	
	/**
	 * ASync for get all favorite locations from server
	 */
	public void getAllFavoriteAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			favoriteProgress.setVisibility(View.VISIBLE);
			
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);

			Log.i("access_token", "=" + Data.userData.accessToken);
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/get_fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
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
									
									updateFavoriteList();
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
	
							favoriteProgress.setVisibility(View.GONE);
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							favoriteProgress.setVisibility(View.GONE);
						}
					});
		}
		else {
		}

	}
	
	
	
	
	/**
	 * ASync for deleteFavoriteAsync from server
	 */
	public void deleteFavoriteAsync(final Activity activity, int sNo, final int index) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("s_no", ""+sNo);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("s_no", "="+sNo);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/delete_fav_locations", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Removed from favourite successfully"}	//result
									
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
									
									Data.favoriteLocations.remove(index);
									updateFavoriteList();
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	

	@Override
	public void startRideForCustomer() {
		Log.e("in ","herestartRideForCustomer");
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.i("in run herestartRideForCustomer class","=");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("in in herestartRideForCustomer  run class","=");
						passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
						switchPassengerScreen(passengerScreenMode);
					}
				});
			}
		}).start();
		
		
		
	}



	@Override
	public void refreshDriverLocations(int count) {
		
		if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
			getDistanceTimeAddress = new GetDistanceTimeAddress(map.getCameraPosition().target);
			getDistanceTimeAddress.execute();
		}
		
		
	}


	
	@Override
	public void apiStart(final int driverPos) {
		
		if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i("in run class","=");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Log.i("apiStart in in run class","=");
							DialogPopup.showLoadingDialog(HomeActivity.this, "Contacting Driver "+ driverPos +" ...");
						}
					});
				}
			}).start();
		}
		
		
	}




	@Override
	public void apiEnd() {
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.i("in run class","=");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("apiStart in in run class","=");
						DialogPopup.dismissLoadingDialog();
					}
				});
			}
		}).start();
	}

	

	// 0 = not found   1 = accept
	@Override
	public void requestRideInterrupt(int switchCase) {
		
		stopService(new Intent(HomeActivity.this, CRequestRideService.class));
		stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
		
		Log.e("switchCase","="+switchCase);
		
		if(switchCase == 0){
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(HomeActivity.this, "", "No Driver available right now.");
							requestRideBtn.setText("Request Ride");
							passengerScreenMode = PassengerScreenMode.P_INITIAL;
							switchPassengerScreen(passengerScreenMode);
						}
					});
				}
			}).start();
			
		}
		else if(switchCase == 1){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i("in run class","=");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Log.i("in in run class","=");
							requestRideBtn.setText("Request Ride");
							DialogPopup.dismissLoadingDialog();
							getAssignedDriverInfoAsync(HomeActivity.this);
						}
					});
				}
			}).start();
			
		}
		
	}


	public void showAllRideRequests(){
		
		if(userMode == UserMode.DRIVER){
		
			driverScreenMode = DriverScreenMode.D_INITIAL;
			switchDriverScreen(driverScreenMode);
			
			map.clear();
		
			if(Data.driverRideRequests.size() > 0){
				driverNewRideRequestRl.setVisibility(View.VISIBLE);
				
				LatLng last = map.getCameraPosition().target;
				
				for(int i=0; i<Data.driverRideRequests.size(); i++){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title(Data.driverRideRequests.get(i).engagementId);
					markerOptions.snippet("");
					markerOptions.position(Data.driverRideRequests.get(i).latLng);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.passenger));
					
					map.addMarker(markerOptions);
					
					if(i == Data.driverRideRequests.size()-1){
						last = Data.driverRideRequests.get(i).latLng;
						Data.dEngagementId = Data.driverRideRequests.get(i).engagementId;
						Data.dCustomerId = Data.driverRideRequests.get(i).customerId;
					}
				}
				
				
				
				driverNewRideRequestText.setText("You have "+Data.driverRideRequests.size()+" ride request");
				
				map.animateCamera(CameraUpdateFactory.newLatLng(last), 1000, null);
				
			}
			else{
				driverNewRideRequestRl.setVisibility(View.GONE);
			}
		
		}
		
		
	}
	

	@Override
	public void changeRideRequest(String dEngagementId, String dCustomerId, final LatLng rideLatLng, boolean add) {
		
		Log.i("in home class","Data.dEngagementId = "+Data.dEngagementId + " " + add);
		
		if(add){
			Data.driverRideRequests.add(new DriverRideRequest(dEngagementId, dCustomerId, rideLatLng));
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showAllRideRequests();
						}
					});
				}
			}).start();
			
		}
		else{
			
			if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
				int index = -1;
				for(int i=0; i<Data.driverRideRequests.size(); i++){
					if(Data.driverRideRequests.get(i).engagementId.equalsIgnoreCase(dEngagementId)){
						index = i;
						break;
					}
				}
				
				if(index != -1){
					Data.driverRideRequests.remove(index);
				}
				
				
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								showAllRideRequests();
							}
						});
					}
				}).start();
			}
			else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
				
				int index = -1;
				for(int i=0; i<Data.driverRideRequests.size(); i++){
					if(Data.driverRideRequests.get(i).engagementId.equalsIgnoreCase(dEngagementId)){
						index = i;
						break;
					}
				}
				
				if(index != -1){
					Data.driverRideRequests.remove(index);
				}
				
				
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								showAllRideRequests();
								
								new DialogPopup().alertPopup(HomeActivity.this, "", "The user has canceled the request");
								
							}
						});
					}
				}).start();
				
			}
			else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_START_RIDE){
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								new DialogPopup().alertPopup(HomeActivity.this, "", "The user has canceled the request");
								
							}
						});
					}
				}).start();
			}
			
			
		}
		
	}




	@Override
	public void driverStartRideInterrupt() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						driverScreenMode = DriverScreenMode.D_START_RIDE;
						switchDriverScreen(driverScreenMode);
						DialogPopup.dismissLoadingDialog();
					}
				});
			}
		}).start();
		
		
	}




	@Override
	public void customerEndRideInterrupt() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						passengerScreenMode = PassengerScreenMode.P_RIDE_END;
						switchPassengerScreen(passengerScreenMode);
					}
				});
			}
		}).start();
		
		
	}






	
	
	
	
	
}