package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.branch.referral.Branch;
import product.clicklabs.jugnoo.adapters.FeedbackReasonsAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.ShowPromotionsResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.NonScrollGridView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.EventsHolder;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class HomeActivity extends BaseFragmentActivity implements AppInterruptHandler, LocationUpdate, FlurryEventNames,

		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DisplayPushHandler,
        SearchListAdapter.SearchListActionsHandler, Constants{


    private final String TAG = HomeActivity.class.getSimpleName();

    DrawerLayout drawerLayout;                                                                        // views declaration


    //menu bar 
    LinearLayout menuLayout;


    LinearLayout linearLayoutProfile;
    ImageView imageViewProfile;
    TextView textViewUserName, textViewViewAccount;

    RelativeLayout relativeLayoutGetRide;
    TextView textViewGetRide;

	RelativeLayout relativeLayoutJugnooLine;

    RelativeLayout relativeLayoutInvite;
    TextView textViewInvite;

    RelativeLayout relativeLayoutWallet;
    TextView textViewWallet, textViewWalletValue;
	ProgressWheel progressBarMenuPaytmWalletLoading;

    RelativeLayout relativeLayoutPromotions;
    TextView textViewPromotions, textViewPromotionsValue;

    RelativeLayout relativeLayoutTransactions;
    TextView textViewTransactions;

	RelativeLayout relativeLayoutNotificationMenu;
	TextView textViewNotificationValueMenu;

	RelativeLayout relativeLayoutFareEstimate;
    RelativeLayout relativeLayoutFareDetails;
    TextView textViewFareDetails;

    RelativeLayout relativeLayoutSupport;
    TextView textViewSupport;

    RelativeLayout relativeLayoutAbout;
    TextView textViewAbout;



    //Top RL
    RelativeLayout topBarMain;
    RelativeLayout topRl;
    ImageView imageViewMenu, imageViewSearchCancel;
    TextView title;
    Button checkServerBtn;
    ImageView imageViewHelp;
	RelativeLayout relativeLayoutNotification;
	TextView textViewNotificationValue;


    //Passenger main layout
    RelativeLayout passengerMainLayout;


    //Map layout
    RelativeLayout mapLayout;
    GoogleMap map;
    TouchableMapFragment mapFragment;


    //Initial layout
    RelativeLayout initialLayout;

    TextView textViewInitialInstructions;
    RelativeLayout relativeLayoutInitialFareFactor;
    TextView textViewCurrentFareFactor;
	ImageView imageViewRideNow;
	RelativeLayout relativeLayoutInitialSearchBar;
	TextView textViewInitialSearch;
	ProgressWheel progressBarInitialSearch;
    Button initialMyLocationBtn, initialMyLocationBtnChangeLoc, changeLocalityBtn;

	RelativeLayout relativeLayoutGoogleAttr;
	ImageView imageViewGoogleAttrCross;
	TextView textViewGoogleAttrText;


	//Location Error layout
	RelativeLayout relativeLayoutLocationError;
	RelativeLayout relativeLayoutLocationErrorSearchBar;




    //Assigining layout
    RelativeLayout assigningLayout;
    TextView textViewFindingDriver;
	SmoothProgressBar progressBarFindingDriver;
    Button assigningMyLocationBtn, initialCancelRideBtn;
    RelativeLayout relativeLayoutAssigningDropLocationParent;
    LinearLayout linearLayoutAssigningDropLocationClick;
	boolean cancelTouchHold = false, placeAdded;


    //Request Final Layout
    RelativeLayout requestFinalLayout;
    RelativeLayout relativeLayoutInRideInfo;
    TextView textViewInRidePromoName, textViewInRideFareFactor;
    LinearLayout linearLayoutFinalDropLocationClick;
    Button customerInRideMyLocationBtn;
	LinearLayout linearLayoutInRideDriverInfo;
    ImageView imageViewInRideDriver;
    TextView textViewInRideDriverName, textViewInRideDriverCarNumber, textViewInRideState;
    Button buttonCancelRide, buttonAddPaytmCash, buttonCallDriver;
    RelativeLayout relativeLayoutFinalDropLocationParent;
	LinearLayout linearLayoutInRideBottom;
	RelativeLayout relativeLayoutIRPaymentOption;
	TextView textViewIRPaymentOption, textViewIRPaymentOptionValue, textViewInRideMinimumFare;
	ImageView imageViewIRPaymentOptionPaytm, imageViewIRPaymentOptionCash;



    //Search Layout
    RelativeLayout relativeLayoutSearch;



    //Center Location Layout
    RelativeLayout centreLocationRl;
    ImageView centreLocationPin, imageViewCenterPinMargin;
	TextView textViewCentrePinETA;



    //End Ride layout
    RelativeLayout endRideReviewRl;
    ScrollView scrollViewRideSummary;
    LinearLayout linearLayoutRideSummary;
    TextView textViewRSTotalFareValue, textViewRSCashPaidValue;
    LinearLayout linearLayoutRSViewInvoice;

    RatingBar ratingBarRSFeedback;
    TextView textViewRSWhatImprove, textViewRSOtherError;
    NonScrollGridView gridViewRSFeedbackReasons;
    FeedbackReasonsAdapter feedbackReasonsAdapter;
    EditText editTextRSFeedback;
    Button buttonRSSubmitFeedback, buttonRSSkipFeedback;
    TextView textViewRSScroll, textViewChangeLocality;

    private RelativeLayout changeLocalityLayout;
    private AnimationDrawable jugnooAnimation;
    private ImageView findDriverJugnooAnimation;

    /*ScrollView scrollViewEndRide;

    TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
	RelativeLayout relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge,
        relativeLayoutEndRideDiscount, relativeLayoutPaidUsingJugnooCash, relativeLayoutPaidUsingPaytm;
	LinearLayout linearLayoutEndRideTime, linearLayoutEndRideWaitTime;
	NonScrollListView listViewEndRideDiscounts;
    TextView textViewEndRideFareValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
			textViewEndRideDiscount, textViewEndRideDiscountRupee, textViewEndRideDiscountValue,
			textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRidePaytmValue, textViewEndRideToBePaidValue, textViewEndRideBaseFareValue,
			textViewEndRideDistanceValue, textViewEndRideTime, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue;
	TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
    Button buttonEndRideOk;
	EndRideDiscountsAdapter endRideDiscountsAdapter;*/


    // data variables declaration

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");

    LatLng lastSearchLatLng;

    static double totalDistance = -1;


    static long previousWaitTime = 0, previousRideTime = 0;


    static Location myLocation;


    public static UserMode userMode;
    public static PassengerScreenMode passengerScreenMode;


	String etaMinutes = "5", farAwayCity = "";


    Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker, dropLocationMarker;
    Polyline pathToDropLocationPolyline;
    PolylineOptions pathToDropLocationPolylineOptions;

    static AppInterruptHandler appInterruptHandler;

    static Activity activity;

    boolean loggedOut = false,
        zoomedToMyLocation = false,
        mapTouchedOnce = false;
    boolean dontCallRefreshDriver = false, zoomedForSearch = false, pickupDropZoomed = false, firstTimeZoom = false, zoomingForDeepLink = false;


    Dialog noDriversDialog, dialogUploadContacts;

    LocationFetcher lowPowerLF, highAccuracyLF;

    PromoCoupon promoCouponSelectedForRide;


    //TODO check final variables
    public static final long LOCATION_UPDATE_TIME_PERIOD = 1 * 10000; //in milliseconds

    public static final int RIDE_ELAPSED_PATH_COLOR = Color.RED;
    public static final int RIDE_LEFT_PATH = Color.BLUE;

    public static final double MIN_BALANCE_ALERT_VALUE = 100; //in Rupees

    public static final float LOW_POWER_ACCURACY_CHECK = 2000, HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
    public static final float WAIT_FOR_ACCURACY_UPPER_BOUND = 2000, WAIT_FOR_ACCURACY_LOWER_BOUND = 200;  //in meters

    public static final double MAP_PAN_DISTANCE_CHECK = 50; // in meters
    public static final double MIN_DISTANCE_FOR_REFRESH = -1; // in meters

    public static final float MAX_ZOOM = 15;

	public static final double FIX_ZOOM_DIAGONAL = 408;

	public static final long PAYTM_CHECK_BALANCE_REFRESH_TIME = 5 * 60 * 1000;

	public static final int PAYTM_TUTORIAL_DIALOG_DISPLAY_COUNT = 1;



    public CheckForGPSAccuracyTimer checkForGPSAccuracyTimer;


    public boolean activityResumed = false;
    public static boolean rechargedOnce = false, feedbackSkipped = false;

    public ASSL assl;


    private int showAllDrivers = 0, showDriverInfo = 0, priorityTipCategory = PriorityTipCategory.NO_PRIORITY_DIALOG.getOrdinal();

    private boolean intentFired = false, dropLocationSearched = false, promoOpened = false;

//    GenieLayout genieLayout;

	private GAPIAddress gapiAddressForPin;

	private GoogleApiClient mGoogleApiClient;


    CallbackManager callbackManager;
    public final int ADD_HOME = 2, ADD_WORK = 3;
    private String dropLocationSearchText = "";
    private SlidingBottomPanel slidingBottomPanel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_home);
        } catch(Exception e){
            e.printStackTrace();
            Crashlytics.logException(e);
            DialogPopup.alertPopupWithListener(this, "", getResources().getString(R.string.insert_sd_card),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
            return;
        }

//		Data.getDeepLinkIndexFromIntent(getIntent());

		EventsHolder.displayPushHandler = this;

		Data.latitude = Data.loginLatitude;
		Data.longitude = Data.loginLongitude;


		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();


        FacebookSdk.sdkInitialize(this);

        callbackManager = CallbackManager.Factory.create();

        HomeActivity.appInterruptHandler = HomeActivity.this;


        showAllDrivers = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfo = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);

        activity = this;

        activityResumed = false;
        rechargedOnce = false;
        dropLocationSearched = false;
        promoOpened = false;

        loggedOut = false;
        zoomedToMyLocation = false;
        dontCallRefreshDriver = false;
        mapTouchedOnce = false;
        pickupDropZoomed = false;
		zoomedForSearch = false;
		firstTimeZoom = false;
		zoomingForDeepLink = false;




        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);


        //Swipe menu
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);


        linearLayoutProfile = (LinearLayout) findViewById(R.id.linearLayoutProfile);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setTypeface(Fonts.mavenRegular(this));
        textViewViewAccount = (TextView) findViewById(R.id.textViewViewAccount);
        textViewViewAccount.setTypeface(Fonts.latoRegular(this));

        relativeLayoutGetRide = (RelativeLayout) findViewById(R.id.relativeLayoutGetRide);
        textViewGetRide = (TextView) findViewById(R.id.textViewGetRide);
        textViewGetRide.setTypeface(Fonts.mavenLight(this));

		relativeLayoutJugnooLine = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooLine);
		((TextView) findViewById(R.id.textViewJugnooLine)).setTypeface(Fonts.mavenLight(this));

        relativeLayoutInvite = (RelativeLayout) findViewById(R.id.relativeLayoutInvite);
        textViewInvite = (TextView) findViewById(R.id.textViewInvite);
        textViewInvite.setTypeface(Fonts.mavenLight(this));

        relativeLayoutWallet = (RelativeLayout) findViewById(R.id.relativeLayoutWallet);
        textViewWallet = (TextView) findViewById(R.id.textViewWallet);
        textViewWallet.setTypeface(Fonts.mavenLight(this));
        textViewWalletValue = (TextView) findViewById(R.id.textViewWalletValue);
        textViewWalletValue.setTypeface(Fonts.latoRegular(this));
		progressBarMenuPaytmWalletLoading = (ProgressWheel) findViewById(R.id.progressBarMenuPaytmWalletLoading);
		progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);

        relativeLayoutPromotions = (RelativeLayout) findViewById(R.id.relativeLayoutPromotions);
        textViewPromotions = (TextView) findViewById(R.id.textViewPromotions);
        textViewPromotions.setTypeface(Fonts.mavenLight(this));
        textViewPromotionsValue = (TextView) findViewById(R.id.textViewPromotionsValue);
        textViewPromotionsValue.setTypeface(Fonts.latoRegular(this));
		textViewPromotionsValue.setVisibility(View.GONE);

        relativeLayoutTransactions = (RelativeLayout) findViewById(R.id.relativeLayoutTransactions);
        textViewTransactions = (TextView) findViewById(R.id.textViewTransactions);
        textViewTransactions.setTypeface(Fonts.mavenLight(this));

		relativeLayoutNotificationMenu = (RelativeLayout) findViewById(R.id.relativeLayoutNotificationMenu);
		textViewNotificationValueMenu = (TextView) findViewById(R.id.textViewNotificationValueMenu);
		textViewNotificationValueMenu.setTypeface(Fonts.mavenLight(this));
		((TextView)findViewById(R.id.textViewNotificationMenu)).setTypeface(Fonts.mavenLight(this));

		relativeLayoutFareEstimate = (RelativeLayout) findViewById(R.id.relativeLayoutFareEstimate);
		((TextView) findViewById(R.id.textViewFareEstimate)).setTypeface(Fonts.mavenLight(this));

		relativeLayoutFareDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareDetails); relativeLayoutFareDetails.setVisibility(View.GONE);
        textViewFareDetails = (TextView) findViewById(R.id.textViewFareDetails);
        textViewFareDetails.setTypeface(Fonts.mavenLight(this));

        relativeLayoutSupport = (RelativeLayout) findViewById(R.id.relativeLayoutSupport);
        textViewSupport = (TextView) findViewById(R.id.textViewSupport);
        textViewSupport.setTypeface(Fonts.mavenLight(this));

        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutAbout);
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);
        textViewAbout.setTypeface(Fonts.mavenLight(this));

        slidingBottomPanel = new SlidingBottomPanel(HomeActivity.this, drawerLayout);



        //Top RL
        topBarMain = (RelativeLayout) findViewById(R.id.topBarMain);
        setupTopBarWithState(PassengerScreenMode.P_INITIAL);


        //Map Layout
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));


        //Passenger main layout
        passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);


        //Initial layout
        initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
        textViewInitialInstructions = (TextView) findViewById(R.id.textViewInitialInstructions);
        textViewInitialInstructions.setTypeface(Fonts.mavenLight(this));
        textViewInitialInstructions.setVisibility(View.GONE);
        changeLocalityLayout = (RelativeLayout)findViewById(R.id.changeLocalityLayout);
        textViewChangeLocality = (TextView)findViewById(R.id.textViewChangeLocality);textViewChangeLocality.setTypeface(Fonts.mavenLight(this));


        relativeLayoutInitialFareFactor = (RelativeLayout) findViewById(R.id.relativeLayoutInitialFareFactor);
        textViewCurrentFareFactor = (TextView) findViewById(R.id.textViewCurrentFareFactor);
        textViewCurrentFareFactor.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewCurrentRatesInfo)).setTypeface(Fonts.latoRegular(this));

        initialMyLocationBtn = (Button) findViewById(R.id.initialMyLocationBtn);
        initialMyLocationBtnChangeLoc = (Button) findViewById(R.id.initialMyLocationBtnChangeLoc);
        changeLocalityBtn = (Button) findViewById(R.id.changeLocalityBtn);
        changeLocalityBtn.setTypeface(Fonts.mavenRegular(this));

        initialMyLocationBtn.setVisibility(View.VISIBLE);
        changeLocalityLayout.setVisibility(View.GONE);
        changeLocalityBtn.setVisibility(View.GONE);
        initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

        imageViewRideNow = (ImageView) findViewById(R.id.imageViewRideNow);



        relativeLayoutInitialSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutInitialSearchBar);
        textViewInitialSearch = (TextView) findViewById(R.id.textViewInitialSearch); textViewInitialSearch.setTypeface(Fonts.latoRegular(this));
        progressBarInitialSearch = (ProgressWheel) findViewById(R.id.progressBarInitialSearch);
        progressBarInitialSearch.setVisibility(View.GONE);

		relativeLayoutGoogleAttr = (RelativeLayout) findViewById(R.id.relativeLayoutGoogleAttr);
		imageViewGoogleAttrCross = (ImageView) findViewById(R.id.imageViewGoogleAttrCross);
		textViewGoogleAttrText = (TextView) findViewById(R.id.textViewGoogleAttrText);
		textViewGoogleAttrText.setTypeface(Fonts.latoRegular(this));
		relativeLayoutGoogleAttr.setVisibility(View.GONE);





		//Location error layout
		relativeLayoutLocationError = (RelativeLayout) findViewById(R.id.relativeLayoutLocationError);
		relativeLayoutLocationErrorSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutLocationErrorSearchBar);
		((TextView)findViewById(R.id.textViewLocationErrorSearch)).setTypeface(Fonts.latoRegular(this));
		relativeLayoutLocationError.setVisibility(View.GONE);






        //Assigning layout
        assigningLayout = (RelativeLayout) findViewById(R.id.assigningLayout);
        textViewFindingDriver = (TextView) findViewById(R.id.textViewFindingDriver);
        textViewFindingDriver.setTypeface(Fonts.mavenLight(this));
		progressBarFindingDriver = (SmoothProgressBar) findViewById(R.id.progressBarFindingDriver);
        assigningMyLocationBtn = (Button) findViewById(R.id.assigningMyLocationBtn);
        initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn);
        initialCancelRideBtn.setTypeface(Fonts.mavenRegular(this));
        findDriverJugnooAnimation = (ImageView)findViewById(R.id.findDriverJugnooAnimation);
        jugnooAnimation = (AnimationDrawable) findDriverJugnooAnimation.getBackground();


        relativeLayoutAssigningDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationParent);
        linearLayoutAssigningDropLocationClick = (LinearLayout) findViewById(R.id.linearLayoutAssigningDropLocationClick);
        ((TextView)findViewById(R.id.textViewAssigningDropLocationClick)).setTypeface(Fonts.latoRegular(this));




        //Request Final Layout
        requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);

        relativeLayoutInRideInfo = (RelativeLayout) findViewById(R.id.relativeLayoutInRideInfo);
        textViewInRidePromoName = (TextView) findViewById(R.id.textViewInRidePromoName);
        textViewInRidePromoName.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
        textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor);
        textViewInRideFareFactor.setTypeface(Fonts.latoRegular(this));
        linearLayoutFinalDropLocationClick = (LinearLayout) findViewById(R.id.linearLayoutFinalDropLocationClick);
        ((TextView)findViewById(R.id.textViewFinalDropLocationClick)).setTypeface(Fonts.latoRegular(this));
        customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);
		linearLayoutInRideDriverInfo = (LinearLayout) findViewById(R.id.linearLayoutInRideDriverInfo);
        imageViewInRideDriver = (ImageView) findViewById(R.id.imageViewInRideDriver);
        textViewInRideDriverName = (TextView) findViewById(R.id.textViewInRideDriverName);
        textViewInRideDriverName.setTypeface(Fonts.mavenLight(this));
        textViewInRideDriverCarNumber = (TextView) findViewById(R.id.textViewInRideDriverCarNumber);
        textViewInRideDriverCarNumber.setTypeface(Fonts.mavenLight(this));
        textViewInRideState = (TextView) findViewById(R.id.textViewInRideState);
        textViewInRideState.setTypeface(Fonts.mavenLight(this));

        buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
        buttonCancelRide.setTypeface(Fonts.mavenLight(this));
		buttonAddPaytmCash = (Button) findViewById(R.id.buttonAddPaytmCash);
		buttonAddPaytmCash.setTypeface(Fonts.mavenLight(this));
        buttonCallDriver = (Button) findViewById(R.id.buttonCallDriver);
        buttonCallDriver.setTypeface(Fonts.mavenLight(this));

        relativeLayoutFinalDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationParent);


		linearLayoutInRideBottom = (LinearLayout) findViewById(R.id.linearLayoutInRideBottom);
		relativeLayoutIRPaymentOption = (RelativeLayout) findViewById(R.id.relativeLayoutIRPaymentOption);
		textViewIRPaymentOption = (TextView) findViewById(R.id.textViewIRPaymentOption); textViewIRPaymentOption.setTypeface(Fonts.mavenRegular(this));
		textViewIRPaymentOptionValue = (TextView) findViewById(R.id.textViewIRPaymentOptionValue); textViewIRPaymentOptionValue.setTypeface(Fonts.mavenRegular(this));
		textViewInRideMinimumFare = (TextView) findViewById(R.id.textViewInRideMinimumFare); textViewInRideMinimumFare.setTypeface(Fonts.latoRegular(this));
		imageViewIRPaymentOptionPaytm = (ImageView) findViewById(R.id.imageViewIRPaymentOptionPaytm);
		imageViewIRPaymentOptionCash = (ImageView) findViewById(R.id.imageViewIRPaymentOptionCash);




        //Search Layout
		relativeLayoutSearch = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);




        //Center location layout
        centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
        centreLocationPin = (ImageView) findViewById(R.id.centreLocationPin);
		imageViewCenterPinMargin = (ImageView) findViewById(R.id.imageViewCenterPinMargin);
		textViewCentrePinETA = (TextView) findViewById(R.id.textViewCentrePinETA);
		textViewCentrePinETA.setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewCentrePinETAMin)).setTypeface(Fonts.latoRegular(this));

        //Review Layout
        endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);

        scrollViewRideSummary = (ScrollView) findViewById(R.id.scrollViewRideSummary);
        linearLayoutRideSummary = (LinearLayout) findViewById(R.id.linearLayoutRideSummary);
        textViewRSTotalFareValue = (TextView) findViewById(R.id.textViewRSTotalFareValue); textViewRSTotalFareValue.setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSTotalFare)).setTypeface(Fonts.mavenRegular(this));
        textViewRSCashPaidValue = (TextView) findViewById(R.id.textViewRSCashPaidValue); textViewRSCashPaidValue.setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSCashPaid)).setTypeface(Fonts.mavenRegular(this));
        linearLayoutRSViewInvoice = (LinearLayout) findViewById(R.id.linearLayoutRSViewInvoice);
        ((TextView)findViewById(R.id.textViewRSInvoice)).setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSRateYourRide)).setTypeface(Fonts.mavenRegular(this));

        ratingBarRSFeedback = (RatingBar) findViewById(R.id.ratingBarRSFeedback); ratingBarRSFeedback.setRating(0);
        textViewRSWhatImprove = (TextView) findViewById(R.id.textViewRSWhatImprove); textViewRSWhatImprove.setTypeface(Fonts.mavenLight(this));
        textViewRSOtherError = (TextView) findViewById(R.id.textViewRSOtherError); textViewRSOtherError.setTypeface(Fonts.mavenLight(this));
        gridViewRSFeedbackReasons = (NonScrollGridView) findViewById(R.id.gridViewRSFeedbackReasons);
        feedbackReasonsAdapter = new FeedbackReasonsAdapter(this, Data.feedbackReasons,
            new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
            @Override
            public void onLastItemSelected(boolean selected) {
                if(!selected){
                    if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                        textViewRSOtherError.setText("");
                    }
                }
            }
        });
        gridViewRSFeedbackReasons.setAdapter(feedbackReasonsAdapter);
        editTextRSFeedback = (EditText) findViewById(R.id.editTextRSFeedback); editTextRSFeedback.setTypeface(Fonts.mavenLight(this));
        buttonRSSubmitFeedback = (Button) findViewById(R.id.buttonRSSubmitFeedback); buttonRSSubmitFeedback.setTypeface(Fonts.mavenRegular(this));
        buttonRSSkipFeedback = (Button) findViewById(R.id.buttonRSSkipFeedback); buttonRSSkipFeedback.setTypeface(Fonts.mavenRegular(this));
        textViewRSScroll = (TextView) findViewById(R.id.textViewRSScroll);

        textViewRSWhatImprove.setVisibility(View.GONE);
        gridViewRSFeedbackReasons.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
        layoutParams.height = (int)(ASSL.Yscale() * 200);
        editTextRSFeedback.setLayoutParams(layoutParams);
        textViewRSOtherError.setText("");



        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Utils.hideSoftKeyboard(HomeActivity.this, textViewInitialSearch);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        // menu events
        linearLayoutProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AccountActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

        relativeLayoutGetRide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(menuLayout);
            }
        });

		relativeLayoutJugnooLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(Data.userData.showJugnooSharing == 1) {
					startActivity(new Intent(HomeActivity.this, JugnooLineActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(JUGNOO_LINE_CLICK);
				}
			}
		});

        relativeLayoutInvite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ShareActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(INVITE_EARN_MENU);
            }
        });

        relativeLayoutWallet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
                intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
				startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(WALLET_MENU);
            }
        });

        relativeLayoutPromotions.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (map != null) {
                    Data.latitude = map.getCameraPosition().target.latitude;
                    Data.longitude = map.getCameraPosition().target.longitude;
					startActivity(new Intent(HomeActivity.this, PromotionsActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(PROMOTIONS_CHECKED);
                } else {
                    Toast.makeText(getApplicationContext(), "Waiting for location...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relativeLayoutTransactions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RideTransactionsActivity.class);
				startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(RIDE_HISTORY);
            }
        });

		relativeLayoutNotificationMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, NotificationCenterActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
				FlurryEventLogger.event(NOTIFICATION_CENTER_MENU);
			}
		});

		relativeLayoutFareEstimate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activityResumed = false;
                if(map != null) {
                    Data.pickupLatLng = map.getCameraPosition().target;
                }
				startActivity(new Intent(HomeActivity.this, FareEstimateActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(FARE_ESTIMATE);
			}
		});

        relativeLayoutFareDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				sendToFareDetails();
            }
        });

        relativeLayoutSupport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, SupportActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(SUPPORT_OPTIONS);
            }
        });

        relativeLayoutAbout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
            }
        });



        menuLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        changeLocalityLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        // Customer initial layout events
        imageViewRideNow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				try {
					if(map != null) {
                        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                            Data.pickupLatLng = map.getCameraPosition().target;
                            FlurryEventLogger.event(AUTO_RIDE_ICON);

                            boolean proceed = slidingBottomPanel.displayAlertAndCheckForSelectedPaytmCoupon();
                            if(proceed) {
                                boolean callRequestRide = true;
                                if (Data.pickupPaymentOption == PaymentOption.PAYTM.getOrdinal()) {
                                    if (Data.userData.getPaytmBalance() > 0) {
                                        callRequestRide = true;
                                        if (Data.fareStructure != null && Data.userData.getPaytmBalance() < Data.fareStructure.fixedFare) {
                                            DialogPopup.dialogBanner(HomeActivity.this, getResources().getString(R.string.paytm_low_cash));
                                        }
                                    } else {
                                        callRequestRide = false;
                                        if(Data.userData.getPaytmError() == 1){
                                            DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.paytm_error_cash_select_cash));
                                        } else{
                                            DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.paytm_no_cash));
                                        }
                                    }
                                    FlurryEventLogger.event(PAYTM_SELECTED_WHEN_REQUESTING);
                                } else {
                                    FlurryEventLogger.event(CASH_SELECTED_WHEN_REQUESTING);
                                    callRequestRide = true;
                                }
                                if (callRequestRide) {
                                    promoCouponSelectedForRide = slidingBottomPanel.getSelectedCoupon();
                                    callAnAutoPopup(HomeActivity.this, Data.pickupLatLng);
                                    FlurryEventLogger.event(FINAL_RIDE_CALL_MADE);
                                    if (promoCouponSelectedForRide.id > 0) {
                                        FlurryEventLogger.event(COUPONS_SELECTED);
                                    } else {
                                        FlurryEventLogger.event(COUPON_NOT_SELECTED);
                                    }
                                }

                                Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                            }

                        } else {
                            DialogPopup.dialogNoInternet(HomeActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick() {
                                    imageViewRideNow.performClick();
                                }

                                @Override
                                public void neutralClick() {

                                }

                                @Override
                                public void negativeClick() {

                                }
                            });
                        }
                    }
                } catch (Exception e) {
					e.printStackTrace();
				}
			}
        });






        relativeLayoutInitialSearchBar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                passengerScreenMode = PassengerScreenMode.P_SEARCH;
                switchPassengerScreen(passengerScreenMode);
            }
        });

		imageViewGoogleAttrCross.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				relativeLayoutGoogleAttr.setVisibility(View.GONE);
			}
		});


        changeLocalityBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewInitialSearch.setText("");
                relativeLayoutInitialSearchBar.performClick();
            }
        });


		//Location error layout
		relativeLayoutLocationError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewInitialSearch.setText("");
				relativeLayoutLocationError.setVisibility(View.GONE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
				imageViewRideNow.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.VISIBLE);
				setServiceAvailablityUI(farAwayCity);
				callMapTouchedRefreshDrivers();
//				genieLayout.setVisibility(View.VISIBLE);
			}
		});

		relativeLayoutLocationErrorSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewInitialSearch.setText("");
                relativeLayoutInitialSearchBar.performClick();
                relativeLayoutLocationError.setVisibility(View.GONE);
                initialMyLocationBtn.setVisibility(View.VISIBLE);
                imageViewRideNow.setVisibility(View.VISIBLE);
                centreLocationRl.setVisibility(View.VISIBLE);
//				genieLayout.setVisibility(View.VISIBLE);
            }
        });


        // Assigning layout events
		assigningLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initialCancelRideBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    if ("".equalsIgnoreCase(Data.cSessionId)) {
                        if (checkForGPSAccuracyTimer != null) {
                            if (checkForGPSAccuracyTimer.isRunning) {
                                checkForGPSAccuracyTimer.stopTimer();
                                customerUIBackToInitialAfterCancel();
                            }
                        }
                    } else {
                        FlurryEventLogger.event(REQUEST_CANCELLED_FINDING_DRIVER);
                        cancelCustomerRequestAsync(HomeActivity.this);
                    }
            }
        });

		/*initialCancelRideBtn.setOnTouchListener(new View.OnTouchListener() {

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (cancelTouchHold) {
                        if ("".equalsIgnoreCase(Data.cSessionId)) {
                            if (checkForGPSAccuracyTimer != null) {
                                if (checkForGPSAccuracyTimer.isRunning) {
                                    checkForGPSAccuracyTimer.stopTimer();
                                    customerUIBackToInitialAfterCancel();
                                }
                            }
                        } else {
                            textViewFindingDriver.setText("Cancelling");
                            progressBarFindingDriver.setSmoothProgressDrawableSpeed(2.0f);
                            progressBarFindingDriver.setSmoothProgressDrawableProgressiveStartSpeed(1.5f);
                            progressBarFindingDriver.setSmoothProgressDrawableMirrorMode(true);
                            progressBarFindingDriver.setSmoothProgressDrawableReversed(true);
                            cancelCustomerRequestAsync(HomeActivity.this);
                            FlurryEventLogger.event(REQUEST_CANCELLED_FINDING_DRIVER);
                        }
                        cancelTouchHold = false;
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        textViewFindingDriver.setText("HOLD TO CANCEL");
                        progressBarFindingDriver.setSmoothProgressDrawableSpeed(0.5f);
                        progressBarFindingDriver.setSmoothProgressDrawableMirrorMode(false);
                        progressBarFindingDriver.setSmoothProgressDrawableReversed(false);
                        progressBarFindingDriver.progressiveStart();
                        progressBarFindingDriver.setSmoothProgressDrawableProgressiveStartSpeed(0.9f);

                        handler.post(runnable);
                        cancelTouchHold = true;

                        break;

                    case MotionEvent.ACTION_UP:
                        if (cancelTouchHold) {
                            cancelTouchHold = false;
                            textViewFindingDriver.setText("Finding a Jugnoo driver...");
                            progressBarFindingDriver.setSmoothProgressDrawableSpeed(2.0f);
                            progressBarFindingDriver.setSmoothProgressDrawableProgressiveStartSpeed(1.5f);
                            progressBarFindingDriver.setSmoothProgressDrawableMirrorMode(true);
                            progressBarFindingDriver.setSmoothProgressDrawableReversed(true);

                            handler.removeCallbacks(runnable);
                        }
                        break;
                }
                return true;
            }
        });*/

        linearLayoutAssigningDropLocationClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(false);
            }
        });


        //Search Layout Events
		relativeLayoutSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});




        // customer request final layout events
        buttonCancelRide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(RIDE_CANCELLED_NOT_COMPLETE);
            }
        });

        buttonAddPaytmCash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
                intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
                startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
					FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_DRIVER_ARRIVED);
				} else if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
					FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_RIDE_IN_PROGRESS);
				}
			}
		});

        buttonCallDriver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				try {
					Utils.openCallIntent(HomeActivity.this, Data.assignedDriverInfo.phoneNumber);
					if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode) {
						FlurryEventLogger.event(CALL_TO_DRIVER_MADE_WHEN_NOT_ARRIVED);
					}
					else if(PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode){
						FlurryEventLogger.event(CALL_TO_DRIVER_MADE_WHEN_ARRIVED);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
   	     });


        linearLayoutFinalDropLocationClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(true);
            }
        });




        // End ride review layout events
        endRideReviewRl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        editTextRSFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                        textViewRSOtherError.setText("");
                    }
                }
            }
        });

        ratingBarRSFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (Data.feedbackReasons.size() > 0) {
                    if (rating > 0 && rating <= 3) {
                        textViewRSWhatImprove.setVisibility(View.VISIBLE);
                        gridViewRSFeedbackReasons.setVisibility(View.VISIBLE);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
                        layoutParams.height = (int) (ASSL.Yscale() * 150);
                        editTextRSFeedback.setLayoutParams(layoutParams);
                    } else {
                        setZeroRatingView();
                    }
                }
            }
        });

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutRideSummary, textViewRSScroll,
            new KeyboardLayoutListener.KeyBoardStateHandler() {
                @Override
                public void keyboardOpened() {
                    editTextRSFeedback.setHint("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewRideSummary.smoothScrollTo(0, editTextRSFeedback.getTop() - ((int) (ASSL.Yscale() * 15)));
                        }
                    }, 200);
                }

                @Override
                public void keyBoardClosed() {
                    editTextRSFeedback.setHint(getString(R.string.leave_a_comment));
                }
            });
        linearLayoutRideSummary.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardLayoutListener.setResizeTextView(false);

        buttonRSSubmitFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String feedbackStr = editTextRSFeedback.getText().toString().trim();
                    int rating = (int) ratingBarRSFeedback.getRating();
                    rating = Math.abs(rating);
                    Log.e("rating screen =", "= feedbackStr = " + feedbackStr + " , rating = " + rating);

                    String feedbackReasons = feedbackReasonsAdapter.getSelectedReasons();
                    boolean isLastReasonSelected = feedbackReasonsAdapter.isLastSelected();

                    if (0 == rating) {
						DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.we_take_your_feedback_seriously));
						FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
					} else {
						if(Data.feedbackReasons.size() > 0 && rating <= 3){
							if(feedbackReasons.length() > 0){
								if(isLastReasonSelected && feedbackStr.length() == 0){
									textViewRSOtherError.setText(getString(R.string.star_required));
									return;
								}
							}
							else{
								DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.please_provide_reason_for_rating));
								return;
							}
						}

						if (feedbackStr.length() > 300) {
							editTextRSFeedback.requestFocus();
							editTextRSFeedback.setError(getString(R.string.review_must_be_in));
						} else {
							submitFeedbackToDriverAsync(HomeActivity.this, Data.cEngagementId, Data.cDriverId,
								rating, feedbackStr, feedbackReasons);
							FlurryEventLogger.event(FEEDBACK_AFTER_RIDE_YES);
							if (feedbackStr.length() > 0) {
								FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
							}
						}
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonRSSkipFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                skipFeedbackForCustomerAsync(HomeActivity.this, Data.cEngagementId);
                FlurryEventLogger.event(FEEDBACK_AFTER_RIDE_NO);
            }
        });

        linearLayoutRSViewInvoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.endRideData != null) {
                    Intent intent = new Intent(HomeActivity.this, RideSummaryActivity.class);
                    intent.putExtra(KEY_END_RIDE_DATA, 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        /*buttonEndRideOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GCMIntentService.clearNotifications(HomeActivity.this);
                *//*if (userMode == UserMode.PASSENGER) {
                    Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                    intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.AFTER_RIDE.getOrdinal());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }*//*

                DialogPopup.alertPopupFromPoint(HomeActivity.this, "title", "message");
            }
        });*/



        // map object initialized
        if (map != null) {

//			map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//				@Override
//				public void onMapLoaded() {
//				}
//			});
//			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//			mapFragment.getMapAsync(new OnMapReadyCallback() {
//				@Override
//				public void onMapReady(GoogleMap googleMap) {
//					map = googleMap;
//				}
//			});

            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //30.7500, 76.7800
			//22.971723, 78.754263


			if ((PassengerScreenMode.P_INITIAL == passengerScreenMode && Data.locationSettingsNoPressed)
					|| (Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0)) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.971723, 78.754263), 5));
				farAwayCity = "Our service is not available in this area";
				setServiceAvailablityUI(farAwayCity);
                Data.lastRefreshLatLng = new LatLng(22.971723, 78.754263);
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), MAX_ZOOM));
                Data.lastRefreshLatLng = new LatLng(Data.latitude, Data.longitude);
            }

            /*map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.v("map", "click");
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });*/

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {

                    if (arg0.getTitle().equalsIgnoreCase("pickup location")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Your Pickup Location", "");
                        map.setInfoWindowAdapter(customIW);

                        return false;
                    } else if (arg0.getTitle().equalsIgnoreCase("customer_current_location")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
                        map.setInfoWindowAdapter(customIW);

                        return true;
                    } else if (arg0.getTitle().equalsIgnoreCase("start ride location")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
                        map.setInfoWindowAdapter(customIW);

                        return false;
                    } else if (arg0.getTitle().equalsIgnoreCase("driver position")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Driver Location", "");
                        map.setInfoWindowAdapter(customIW);

                        return false;
                    } else if (arg0.getTitle().equalsIgnoreCase("driver shown to customer")) {
                        if (1 == showDriverInfo) {
                            String driverId = arg0.getSnippet();
                            try {
                                final DriverInfo driverInfo = Data.driverInfos.get(Data.driverInfos.indexOf(new DriverInfo(driverId)));
                                DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "Driver Info", "" + driverInfo.toString(),
                                    "Call", "Cancel", new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                            callIntent.setData(Uri.parse("tel:" + driverInfo.phoneNumber));
                                            startActivity(callIntent);
                                        }
                                    },
                                    new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }, true, false);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        return true;
                    } else {
                        return true;
                    }
                }
            });


            new MapStateListener(map, mapFragment, this) {
                @Override
                public void onMapTouched() {
                    // Map touched
                    /*if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
                        slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else{
                        slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    }*/


                }

                @Override
                public void onMapReleased() {
                    // Map released
                    try {
                        if(PassengerScreenMode.P_INITIAL == passengerScreenMode && zoomedForSearch){
                            if(lastSearchLatLng != null){
                                double distance = MapUtils.distance(lastSearchLatLng, map.getCameraPosition().target);
                                if(distance > MAP_PAN_DISTANCE_CHECK){
                                    textViewInitialSearch.setText("");
                                    zoomedForSearch = false;
                                    lastSearchLatLng = null;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMapUnsettled() {
                    // Map unsettled
                    if (userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL) {

                    }
                }

                @Override
                public void onMapSettled() {
                    // Map settled

                    boolean refresh = false;
                    if(Data.lastRefreshLatLng == null){
                        Data.lastRefreshLatLng = map.getCameraPosition().target;
                        refresh = true;
                    }
                    else{
                        Log.v("Min Difference is = ","---> "+MapUtils.distance(Data.lastRefreshLatLng, map.getCameraPosition().target));
//                        textViewNearestDriverETA
                        if(MapUtils.distance(Data.lastRefreshLatLng, map.getCameraPosition().target) > MIN_DISTANCE_FOR_REFRESH){
							Data.lastRefreshLatLng = map.getCameraPosition().target;
                            refresh = true;
                        }
                    }
                    if(refresh) {
                        callMapTouchedRefreshDrivers();
                    }
                    if (!zoomedForSearch && map != null) {
                        getPickupAddress(map.getCameraPosition().target);
                    }
                }
            };


            initialMyLocationBtn.setOnClickListener(mapMyLocationClick);
            initialMyLocationBtnChangeLoc.setOnClickListener(mapMyLocationClick);
            assigningMyLocationBtn.setOnClickListener(mapMyLocationClick);
            customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);

        }


//        genieLayout = new GenieLayout(this);
//        genieLayout.addGenieLayout(drawerLayout, linearLayoutRequestInfo);

        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (userMode == null) {
                userMode = UserMode.PASSENGER;
            }

            if (passengerScreenMode == null) {
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
            }

			if(Data.userData.paytmEnabled != 0 || Prefs.with(activity).getInt(SPLabels.PAYTM_TUTORIAL_SHOWN_COUNT, 0) >= PAYTM_TUTORIAL_DIALOG_DISPLAY_COUNT){
				if(!Data.locationSettingsNoPressed) {
//					ReferralActions.incrementAppOpen(this);
//					ReferralActions.showReferralDialog(HomeActivity.this, callbackManager);
				}
			}

			if(Data.userData.getPromoSuccess() != 0) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
//						showPaytmTutorialPopup(HomeActivity.this);
					}
				}, 1000);
			}


            // ****** Jugnoo Jeanie Tutorial Screen ****** //
            /*if((Prefs.with(activity).getInt(SPLabels.JUGNOO_JEANIE_TUTORIAL_SHOWN, 0) == 0)
                    &&((Prefs.with(this).getInt(SPLabels.SHOW_JUGNOO_JEANIE, 0) == 1))){
                Prefs.with(activity).save(SPLabels.JUGNOO_JEANIE_TUTORIAL_SHOWN, 1);
                // for tutorial screens
                startActivity(new Intent(HomeActivity.this, JugnooJeanieTutorialActivity.class));
            }*/

            // ****** New Look Tutorial Screen ***** //
            if((Prefs.with(activity).getInt(SPLabels.NEW_LOOK_TUTORIAL_SHOWN, 0) == 0)){
                Prefs.with(activity).save(SPLabels.NEW_LOOK_TUTORIAL_SHOWN, 1);
                // for tutorial screens
                new NewLookTutorialDialog(HomeActivity.this);
            }

			switchUserScreen();

            startUIAfterGettingUserStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }


		try{
			if(Data.userData.showJugnooSharing == 1) {
				relativeLayoutJugnooLine.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutJugnooLine.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
			relativeLayoutJugnooLine.setVisibility(View.GONE);
		}



//        Class.forName(getPackageName() + "." + Data.deepLinkClassName)
        try{
            if(!"".equalsIgnoreCase(Data.deepLinkClassName)) {
                Class cls = Class.forName(getPackageName() + "." + Data.deepLinkClassName);
                startActivity(new Intent(this, cls));
                overridePendingTransition(R.anim.hold, R.anim.hold);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

		try{
			Branch.getInstance(this).setIdentity(Data.userData.userIdentifier);
		} catch(Exception e){
			e.printStackTrace();
		}

		try{
			if(Data.userData.getPromoSuccess() == 0){
                DialogPopup.alertPopupWithListener(HomeActivity.this, "",
                    getResources().getString(R.string.promocode_invalid_message_on_signup),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relativeLayoutPromotions.performClick();
                        }
                    });
				Data.userData.setPromoSuccess(1);
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		Prefs.with(activity).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * PAYTM_CHECK_BALANCE_REFRESH_TIME)));

        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "");
        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, "");

    }

    public void slideOnClick(View v){
        slidingBottomPanel.slideOnClick(v);
    }

    private void setupTopBarWithState(PassengerScreenMode passengerScreenMode){
        RelativeLayout root = null;
        if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
            topBarMain.setVisibility(View.VISIBLE);
            root = topBarMain;
        }
        else{
            topBarMain.setVisibility(View.VISIBLE);
            root = topBarMain;
        }
        topRl = (RelativeLayout) root.findViewById(R.id.topRl);
        imageViewMenu = (ImageView) root.findViewById(R.id.imageViewMenu);
        imageViewSearchCancel = (ImageView) root.findViewById(R.id.imageViewSearchCancel);
        title = (TextView) root.findViewById(R.id.title);title.setTypeface(Fonts.mavenRegular(this));
        checkServerBtn = (Button) root.findViewById(R.id.checkServerBtn);
        imageViewHelp = (ImageView) root.findViewById(R.id.imageViewHelp);
        relativeLayoutNotification = (RelativeLayout) root.findViewById(R.id.relativeLayoutNotification);
        textViewNotificationValue = (TextView) root.findViewById(R.id.textViewNotificationValue);
        textViewNotificationValue.setTypeface(Fonts.latoRegular(this));
        textViewNotificationValue.setVisibility(View.GONE);

        //Top bar events
        topRl.setOnClickListener(topBarOnClickListener);
        imageViewMenu.setOnClickListener(topBarOnClickListener);
        checkServerBtn.setOnClickListener(topBarOnClickListener);

        checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "" + Config.getServerUrlName(), Toast.LENGTH_SHORT).show();
                FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
                return false;
            }
        });

        imageViewSearchCancel.setOnClickListener(topBarOnClickListener);
        imageViewHelp.setOnClickListener(topBarOnClickListener);
        relativeLayoutNotification.setOnClickListener(topBarOnClickListener);

    }

    private OnClickListener topBarOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.topRl:
                    Log.e(TAG, "topBarMain.getVisibility()=>"+topBarMain.getVisibility());
                    break;

                case R.id.imageViewMenu:
                    drawerLayout.openDrawer(menuLayout);
                    FlurryEventLogger.event(MENU_LOOKUP);
                    break;

                case R.id.imageViewBack:
                    HomeActivity.this.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                    switchPassengerScreen(HomeActivity.this.passengerScreenMode);
                    break;

                case R.id.checkServerBtn:
                    break;


                case R.id.imageViewSearchCancel:
                    textViewInitialSearch.setText("");
                    Utils.hideSoftKeyboard(HomeActivity.this, textViewInitialSearch);
                    HomeActivity.this.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                    switchPassengerScreen(HomeActivity.this.passengerScreenMode);
                    FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
                    break;

                case R.id.imageViewHelp:
                    sosDialog(HomeActivity.this);
                    FlurryEventLogger.event(SOS_ALERT_USED);
                    break;

                case R.id.relativeLayoutNotification:
                    startActivity(new Intent(HomeActivity.this, NotificationCenterActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(NOTIFICATION_ICON);
                    break;

            }
        }
    };



	private float googleMapPadding = 0;
	private void setGoogleMapPadding(float bottomPadding){
		try {
			if(map != null){
				map.setPadding(0, 0, 0, (int)(ASSL.Yscale() * bottomPadding));
				googleMapPadding = bottomPadding;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setCentrePinAccToGoogleMapPadding(){
		try {
			if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewCenterPinMargin.getLayoutParams();
				params.height = (int) (ASSL.Yscale() * googleMapPadding);
				imageViewCenterPinMargin.setLayoutParams(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






    public void callMapTouchedRefreshDrivers() {
        try {
            if (Data.userData != null) {
                if (UserMode.PASSENGER == userMode &&
                    (PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode) &&
                    map != null &&
                    HomeActivity.this.hasWindowFocus()) {
                    if (Data.userData.canChangeLocation == 1) {
                        Data.pickupLatLng = map.getCameraPosition().target;
                    } else {
                        if (myLocation != null) {
                            Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        }
                    }
                    if (!dontCallRefreshDriver && Data.pickupLatLng != null) {
                        callFindADriverAndShowPromotionsAPIS(Data.pickupLatLng);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startUIAfterGettingUserStatus() {
        if (userMode == UserMode.PASSENGER) {
            if (passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                initiateRequestRide(false);
            } else if (passengerScreenMode == PassengerScreenMode.P_RIDE_END) {
                clearSPData();
                switchPassengerScreen(passengerScreenMode);
            } else {
                switchPassengerScreen(passengerScreenMode);
            }

        }
    }


    public void sendToFareDetails() {
        HelpParticularActivity.helpSection = HelpSection.FARE_DETAILS;
        if(map != null) {
            Data.lastRefreshLatLng = map.getCameraPosition().target;
        }
		startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
		FlurryEventLogger.event(FARE_DETAILS);
    }


    public void initiateRequestRide(boolean newRequest) {
        if (newRequest) {
            Dialog dialogPriorityTip = new PriorityTipDialog(HomeActivity.this, Data.userData.fareFactor, priorityTipCategory,
                    new PriorityTipDialog.Callback() {
                        @Override
                        public void onConfirmed() {
                            Data.cSessionId = "";
                            Data.cEngagementId = "";
                            dropLocationSearchText = "";

                            if (Data.userData.canChangeLocation == 1) {
                                if (Data.pickupLatLng == null) {
                                    Data.pickupLatLng = map.getCameraPosition().target;
                                }
                                double distance = MapUtils.distance(Data.pickupLatLng, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                                if (distance > MAP_PAN_DISTANCE_CHECK) {
                                    switchRequestRideUI();
                                    startTimerRequestRide();
                                } else {
                                    checkForGPSAccuracyTimer = new CheckForGPSAccuracyTimer(HomeActivity.this, 0, 5000, System.currentTimeMillis(), 60000);
                                }
                            } else {
                                checkForGPSAccuracyTimer = new CheckForGPSAccuracyTimer(HomeActivity.this, 0, 5000, System.currentTimeMillis(), 60000);
                            }
                            if (Data.TRANSFER_FROM_JEANIE == 1) {
                                FlurryEventLogger.event(JUGNOO_STICKY_RIDE_CONFIRMATION);
                                Data.TRANSFER_FROM_JEANIE = 0;
                            }
                        }

                        @Override
                        public void onCancelled() {
                            Log.v("Request of Ride", "Aborted");
                        }
                    }).showDialog();
        } else {
            Data.cEngagementId = "";
            switchRequestRideUI();
            startTimerRequestRide();
        }

    }


    public void switchRequestRideUI() {
        SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
        Editor editor = pref.edit();
        editor.putString(Data.SP_TOTAL_DISTANCE, "0");
        editor.putString(Data.SP_LAST_LATITUDE, "" + Data.pickupLatLng.latitude);
        editor.putString(Data.SP_LAST_LONGITUDE, "" + Data.pickupLatLng.longitude);
        editor.commit();

        cancelTimerUpdateDrivers();

        HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
        switchPassengerScreen(passengerScreenMode);
    }


    OnClickListener mapMyLocationClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            textViewInitialSearch.setText("");
            if (myLocation != null) {
                try {
                    zoomedForSearch = false;
                    lastSearchLatLng = null;
					setCentrePinAccToGoogleMapPadding();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), MAX_ZOOM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
                reconnectLocationFetchers();
            }
            FlurryEventLogger.event(NAVIGATION_TO_CURRENT_LOC);
            /*if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }*/
        }
    };



    Handler reconnectionHandler = null;

    public void reconnectLocationFetchers() {
        if (reconnectionHandler == null) {
            destroyFusedLocationFetchers();
            reconnectionHandler = new Handler();
            reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					initializeFusedLocationFetchers();
					reconnectionHandler.removeCallbacks(this);
					reconnectionHandler = null;
				}
			}, 2000);
        }
    }


    public void setUserData() {
        try {
            textViewUserName.setText(Data.userData.userName);

			if(Data.userData.numCouponsAvaliable > 0) {
				textViewPromotionsValue.setVisibility(View.VISIBLE);
				textViewPromotionsValue.setText("" + Data.userData.numCouponsAvaliable);
			}
			else{
				textViewPromotionsValue.setVisibility(View.GONE);
			}

			int unreadNotificationsCount = Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			if(unreadNotificationsCount > 0){
				textViewNotificationValue.setVisibility(View.VISIBLE);
				textViewNotificationValue.setText("" + unreadNotificationsCount);
				textViewNotificationValueMenu.setVisibility(View.VISIBLE);
				textViewNotificationValueMenu.setText(""+unreadNotificationsCount);
			}
			else{
				textViewNotificationValue.setVisibility(View.GONE);
				textViewNotificationValueMenu.setVisibility(View.GONE);
			}

            textViewWalletValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getMoneyDecimalFormat().format(Data.userData.getTotalWalletBalance()));

            Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
            try {
				if(activityResumed){
					Picasso.with(HomeActivity.this).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfile);
				}
				else{
					Picasso.with(HomeActivity.this).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewProfile);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }

			if(!promoOpened) {
				updateInRideAddPaytmButtonText();
			}
			setPaymentOptionInRide();

            slidingBottomPanel.updatePreferredPaymentOptionUI();
            slidingBottomPanel.updatePaymentOption();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchUserScreen() {

        passengerMainLayout.setVisibility(View.VISIBLE);

        relativeLayoutPromotions.setVisibility(View.VISIBLE);

        Database2.getInstance(HomeActivity.this).close();

    }


    public void switchPassengerScreen(PassengerScreenMode mode) {
        try {
			promoOpened = false;

            imageViewMenu.setVisibility(View.VISIBLE);

            if (userMode == UserMode.PASSENGER) {

                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }

                if (mode == PassengerScreenMode.P_RIDE_END) {
                    if (Data.endRideData != null) {
//                        genieLayout.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.VISIBLE);
                        endRideReviewRl.setVisibility(View.VISIBLE);

                        scrollViewRideSummary.scrollTo(0, 0);
                        ratingBarRSFeedback.setRating(0f);
                        setZeroRatingView();

                        editTextRSFeedback.setText("");
                        for(int i=0; i<Data.feedbackReasons.size(); i++){
                            Data.feedbackReasons.get(i).checked = false;
                        }
                        feedbackReasonsAdapter.notifyDataSetChanged();
                        textViewRSTotalFareValue.setText(String.format(getString(R.string.ruppes_value_format_without_space),
                            "" + Utils.getMoneyDecimalFormat().format(Data.endRideData.finalFare)));
                        textViewRSCashPaidValue.setText(String.format(getString(R.string.ruppes_value_format_without_space),
                            ""+Utils.getMoneyDecimalFormat().format(Data.endRideData.toPay)));

                        Data.endRideData.setDriverNameCarName(Data.assignedDriverInfo.name, Data.assignedDriverInfo.carNumber);

                        // delete the RidePath Table from Phone Database :)
                        Database2.getInstance(HomeActivity.this).deleteRidePathTable();
                        Log.d("RidePath DB", "Deleted");
                    } else {
                        passengerScreenMode = PassengerScreenMode.P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    }
                } else {
                    mapLayout.setVisibility(View.VISIBLE);
                    endRideReviewRl.setVisibility(View.GONE);
                }


                //setSlidingUpPanelLayoutState(mode);

                switch (mode) {

                    case P_INITIAL:

                        GCMIntentService.clearNotifications(HomeActivity.this);

						Data.dropLatLng = null;

                        Database2.getInstance(HomeActivity.this).deleteRidePathTable();


						try{ map.clear(); } catch(Exception e){ e.printStackTrace(); }

                        try {
                            pickupLocationMarker.remove();
                        } catch (Exception e) {
                        }
                        try {
                            driverLocationMarker.remove();
                        } catch (Exception e) {
                        }

                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
						requestFinalLayout.setVisibility(View.GONE);
                        if (Data.userData != null && Data.userData.canChangeLocation == 1) {
                            centreLocationRl.setVisibility(View.VISIBLE);
                            relativeLayoutInitialSearchBar.setVisibility(View.VISIBLE);
                        } else {
                            centreLocationRl.setVisibility(View.GONE);
                            relativeLayoutInitialSearchBar.setVisibility(View.GONE);
                        }

                        textViewInitialInstructions.setVisibility(View.GONE);

                        imageViewRideNow.setVisibility(View.VISIBLE);

                        initialMyLocationBtn.setVisibility(View.VISIBLE);
                        changeLocalityBtn.setVisibility(View.GONE);
                        changeLocalityLayout.setVisibility(View.GONE);
                        initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

                        setFareFactorToInitialState();

                        cancelTimerRequestRide();


                        Log.e("Data.latitude", "=" + Data.latitude);
                        Log.e("myLocation", "=" + myLocation);

                        if (Data.latitude != 0 && Data.longitude != 0) {
                            showDriverMarkersAndPanMap(new LatLng(Data.latitude, Data.longitude));
                        } else if (myLocation != null) {
                            showDriverMarkersAndPanMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        }


						relativeLayoutNotification.setVisibility(View.VISIBLE);
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);


						if(!firstTimeZoom){
							if(Data.pickupLatLng != null){
								zoomToCurrentLocationWithOneDriver(Data.pickupLatLng);
							}
							else if(myLocation != null){
								zoomToCurrentLocationWithOneDriver(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
							}
						}
						firstTimeZoom = true;

						if(!zoomedForSearch && map != null) {
							getPickupAddress(map.getCameraPosition().target);
						}

						if(Data.locationSettingsNoPressed){
							relativeLayoutLocationError.setVisibility(View.VISIBLE);
							initialMyLocationBtn.setVisibility(View.GONE);
							imageViewRideNow.setVisibility(View.GONE);
//							genieLayout.setVisibility(View.GONE);
							centreLocationRl.setVisibility(View.GONE);

							Data.locationSettingsNoPressed = false;
						}
						else{
							relativeLayoutLocationError.setVisibility(View.GONE);
							initialMyLocationBtn.setVisibility(View.VISIBLE);
							imageViewRideNow.setVisibility(View.VISIBLE);
//							genieLayout.setVisibility(View.VISIBLE);
							centreLocationRl.setVisibility(View.VISIBLE);
						}

                        break;


                    case P_SEARCH:


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.VISIBLE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);


                        break;


                    case P_ASSIGNING:

                        findDriverJugnooAnimation.setVisibility(View.VISIBLE);
                        jugnooAnimation.start();
                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.VISIBLE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

						textViewFindingDriver.setText("Finding a Jugnoo driver...");
                        initialCancelRideBtn.setVisibility(View.GONE);

                        if (map != null) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("pickup location");
                            markerOptions.snippet("");
                            markerOptions.position(Data.pickupLatLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));

                            pickupLocationMarker = map.addMarker(markerOptions);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									if(map != null && Data.pickupLatLng != null){
										map.animateCamera(CameraUpdateFactory.newLatLng(Data.pickupLatLng));
									}
								}
							}, 1000);
                        }

                        stopDropLocationSearchUI(false);

                        if(Data.dropLatLng == null){
                            if ("".equalsIgnoreCase(Data.cSessionId)) {
                                linearLayoutAssigningDropLocationClick.setVisibility(View.GONE);
                            }
                            else{
                                linearLayoutAssigningDropLocationClick.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            linearLayoutAssigningDropLocationClick.setVisibility(View.GONE);
                        }
                        relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);
						setGoogleMapPadding(0);


						relativeLayoutNotification.setVisibility(View.VISIBLE);
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

                        break;


                    case P_REQUEST_FINAL:

                        if (map != null) {

                            if (Data.pickupLatLng == null) {
                                Log.e("Data.mapTarget", "=null");
                                SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
                                String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
                                String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
                                Data.pickupLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            } else {
                                Log.e("Data.mapTarget", "=not null");
                            }

                            Log.e("Data.mapTarget", "=" + Data.pickupLatLng);
                            Log.e("Data.assignedDriverInfo.latLng", "=" + Data.assignedDriverInfo.latLng);

                            map.clear();

                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng, false));

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo.latLng));

                            Log.i("marker added", "REQUEST_FINAL");
                        }

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

                        setAssignedDriverData(mode);

                        if(Data.dropLatLng == null){
                            linearLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
                        }
                        else{
                            linearLayoutFinalDropLocationClick.setVisibility(View.GONE);
                        }
                        if(dropLocationSearched){
                            initDropLocationSearchUI(true);
                        }
                        else{
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();

                        buttonCancelRide.setVisibility(View.VISIBLE);
                        buttonAddPaytmCash.setVisibility(View.GONE);
						updateUIInRideFareInfo();
						setPaymentOptionInRide();

						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

                        break;

                    case P_DRIVER_ARRIVED:


                        if (map != null) {

                            if (Data.pickupLatLng == null) {
                                Log.e("Data.mapTarget", "=null");
                                SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
                                String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
                                String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
                                Data.pickupLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            } else {
                                Log.e("Data.mapTarget", "=not null");
                            }

                            Log.e("Data.mapTarget", "=" + Data.pickupLatLng);
                            Log.e("Data.assignedDriverInfo.latLng", "=" + Data.assignedDriverInfo.latLng);

                            map.clear();

                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng, true));

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo.latLng));

                            Log.i("marker added", "REQUEST_FINAL");

                            if(Data.dropLatLng != null) {
                                setDropLocationMarker();
                                setPickupToDropPath();
                            }
                        }


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

                        if(Data.dropLatLng == null){
                            linearLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
                        }
                        else{
                            linearLayoutFinalDropLocationClick.setVisibility(View.GONE);
                        }
						if(dropLocationSearched){
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
                        setDropLocationEngagedUI();

						setAssignedDriverData(mode);


                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddPaytmCash.setVisibility(View.VISIBLE);
                        updateInRideAddPaytmButtonText();
						updateUIInRideFareInfo();
						setPaymentOptionInRide();

						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

                        break;


                    case P_IN_RIDE:

                        if (map != null) {
                            map.clear();

                            if (Data.pickupLatLng != null) {
                                map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng, true));
                            }

                            if(Data.dropLatLng != null) {
                                setDropLocationMarker();
                                setPickupToDropPath();
                            }
                        }


                        //******** If return 0 then show popup, contact not saved in database.
                        if(Data.userData.contactSaved == 0
                                && (Prefs.with(HomeActivity.this).getInt(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0) == 0)
                                && dialogUploadContacts == null
								&& Data.NO_PROMO_APPLIED.equalsIgnoreCase(Data.assignedDriverInfo.promoName)) {
							drawerLayout.closeDrawer(menuLayout);
                            dialogUploadContacts = DialogPopup.uploadContactsTwoButtonsWithListeners(HomeActivity.this,
									Data.userData.referAllTitle,
                                    Data.userData.referAllText,
                                    getResources().getString(R.string.upload_contact_yes),
                                    getResources().getString(R.string.upload_contact_no_thanks),
                                    false ,
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                                            Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 1);
                                            Intent syncContactsIntent = new Intent(HomeActivity.this, ContactsUploadService.class);
                                            syncContactsIntent.putExtra("access_token", Data.userData.accessToken);
                                            syncContactsIntent.putExtra("session_id", Data.cSessionId);
                                            syncContactsIntent.putExtra("engagement_id", Data.cEngagementId);
                                            startService(syncContactsIntent);
                                            registerDialogDismissReceiver();
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, -1);
                                            uploadContactsApi();
                                        }
                                    });
                        }




                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

                        if(Data.dropLatLng == null){
                            linearLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
                        }
                        else{
                            linearLayoutFinalDropLocationClick.setVisibility(View.GONE);
                        }
						if(dropLocationSearched){
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
						setDropLocationEngagedUI();

						setAssignedDriverData(mode);

                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddPaytmCash.setVisibility(View.VISIBLE);
                        updateInRideAddPaytmButtonText();
						updateUIInRideFareInfo();
						setPaymentOptionInRide();

						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

                        break;

                    case P_RIDE_END:

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        imageViewSearchCancel.setVisibility(View.GONE);
						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.VISIBLE);
						setGoogleMapPadding(0);

//                        genieLayout.setVisibility(View.GONE);

						Data.pickupLatLng = null;

                        break;


                    default:
                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        endRideReviewRl.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

						relativeLayoutNotification.setVisibility(View.GONE);
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.VISIBLE);


                }

                initiateTimersForStates(mode);
                dismissReferAllDialog(mode);



//                endRideReviewRl.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSlidingUpPanelLayoutState(PassengerScreenMode passengerScreenMode){
        if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else{
            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    public SlidingBottomPanel getSlidingBottomPanel(){
        return slidingBottomPanel;
    }


    private BroadcastReceiver receiver;
    private void registerDialogDismissReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(ACTION_LOADING_COMPLETE)) {
						DialogPopup.dismissLoadingDialog();
						unregisterReceiver(receiver);
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_LOADING_COMPLETE);
        registerReceiver(receiver, intentFilter);
    }

    private void dismissReferAllDialog(PassengerScreenMode mode){
        try {
            if(PassengerScreenMode.P_IN_RIDE != mode){
                if(dialogUploadContacts != null){
                    if(dialogUploadContacts.isShowing()) {
                        dialogUploadContacts.dismiss();
                    }
					dialogUploadContacts = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiateTimersForStates(PassengerScreenMode passengerScreenMode) {
        try {
            switch (passengerScreenMode) {
                case P_INITIAL:
                    startTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_SEARCH:
                    break;

                case P_ASSIGNING:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_REQUEST_FINAL:
                    cancelTimerUpdateDrivers();
                    startDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_DRIVER_ARRIVED:
                    cancelTimerUpdateDrivers();
                    startDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_IN_RIDE:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    startMapAnimateAndUpdateRideDataTimer();
					try {
						getDropLocationPathAndDisplay(Data.pickupLatLng);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

                case P_RIDE_END:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                default:

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setZeroRatingView(){
        textViewRSWhatImprove.setVisibility(View.GONE);
        gridViewRSFeedbackReasons.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
        layoutParams.height = (int) (ASSL.Yscale() * 200);
        editTextRSFeedback.setLayoutParams(layoutParams);
        textViewRSOtherError.setText("");
    }

    private PlaceSearchListFragment getPlaceSearchListFragment(PassengerScreenMode mode){
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(PlaceSearchListFragment.class.getSimpleName() + mode);
        return (PlaceSearchListFragment) frag;
    }

    private void relativeLayoutSearchSetVisiblity(int visiblity){
        if(View.VISIBLE == visiblity){
            relativeLayoutSearch.setVisibility(View.VISIBLE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_SEARCH);
            if(frag == null || frag.isRemoving()) {
                PlaceSearchListFragment placeSearchListFragment = new PlaceSearchListFragment(this, mGoogleApiClient);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SEARCH_FIELD_TEXT, textViewInitialSearch.getText().toString());
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.search_state_edit_text_hint));
                placeSearchListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutSearch.getId(), placeSearchListFragment,
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_SEARCH)
                        .commitAllowingStateLoss();
            }
        } else{
            relativeLayoutSearch.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_SEARCH);
            if(frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }



    private void relativeLayoutAssigningDropLocationParentSetVisibility(int visiblity){
        if(View.VISIBLE == visiblity){
            relativeLayoutAssigningDropLocationParent.setVisibility(View.VISIBLE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_ASSIGNING);
            if(frag == null || frag.isRemoving()) {
                PlaceSearchListFragment placeSearchListFragment = new PlaceSearchListFragment(this, mGoogleApiClient);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                placeSearchListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutAssigningDropLocationParent.getId(), placeSearchListFragment,
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_ASSIGNING)
                        .commitAllowingStateLoss();
            }
        } else{
            relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_ASSIGNING);
            if(frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    private void relativeLayoutFinalDropLocationParentSetVisibility(int visiblity, String text){
        if(View.VISIBLE == visiblity){
            relativeLayoutFinalDropLocationParent.setVisibility(View.VISIBLE);
            relativeLayoutInRideInfo.setVisibility(View.GONE);
            linearLayoutFinalDropLocationClick.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if(frag == null || frag.isRemoving()) {
                PlaceSearchListFragment placeSearchListFragment = new PlaceSearchListFragment(this, mGoogleApiClient);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SEARCH_FIELD_TEXT, text);
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                placeSearchListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutFinalDropLocationParent.getId(), placeSearchListFragment,
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_REQUEST_FINAL)
                        .commitAllowingStateLoss();
            }
        } else{
            relativeLayoutFinalDropLocationParent.setVisibility(View.GONE);
            relativeLayoutInRideInfo.setVisibility(View.VISIBLE);
            linearLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if(frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }


	private void updateInRideAddPaytmButtonText(){
		try{
            if (Data.userData.paytmEnabled == 1 && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
				buttonAddPaytmCash.setText("Add Paytm Cash");
			}
			else{
				buttonAddPaytmCash.setText("Add Paytm Wallet");
			}
		} catch(Exception e){
			buttonAddPaytmCash.setText("Add Paytm Wallet");
		}
	}


	private void updateUIInRideFareInfo(){
		try{
			if(Data.assignedDriverInfo.getFareFixed() > 0) {
				textViewInRideMinimumFare.setVisibility(View.VISIBLE);
				textViewInRideMinimumFare.setText("Minimum Fare "+
						getResources().getString(R.string.rupee)+" "+Data.assignedDriverInfo.getFareFixedStr());
			}
			else{
				textViewInRideMinimumFare.setVisibility(View.GONE);
			}
		} catch(Exception e){}
		checkForGoogleLogoVisibilityInRide();
	}



    private void setDropLocationEngagedUI(){
        if(Data.dropLatLng == null){
            linearLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
        }
        else{
            setDropLocationMarker();
            linearLayoutFinalDropLocationClick.setVisibility(View.GONE);
        }
    }

    private void setDropLocationMarker(){
		if(Data.dropLatLng != null) {
			if (dropLocationMarker != null) {
				dropLocationMarker.remove();
			}
			dropLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.dropLatLng));
		}
    }

    private void setPickupToDropPath(){
        if(pathToDropLocationPolylineOptions != null) {
            if (pathToDropLocationPolyline != null) {
                pathToDropLocationPolyline.remove();
            }
            pathToDropLocationPolyline = map.addPolyline(pathToDropLocationPolylineOptions);
        }
    }


    private void pauseAllTimers() {
        try {
            cancelTimerUpdateDrivers();
            cancelDriverLocationUpdateTimer();
            cancelMapAnimateAndUpdateRideDataTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	private void checkForGoogleLogoVisibilityInRide(){
		try{
			float padding = 0;
			if (textViewInRideMinimumFare.getVisibility() == View.VISIBLE) {
				padding = padding + 42;
			}
			padding = padding + 350f;
			setGoogleMapPadding(padding);
		} catch(Exception e){
			e.printStackTrace();
		}
	}



    public void setFareFactorToInitialState() {
        try {
            if ((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)) {
                if (Data.userData.fareFactor > 1 || Data.userData.fareFactor < 1) {
                    relativeLayoutInitialFareFactor.setVisibility(View.VISIBLE);
                    textViewCurrentFareFactor.setText(decimalFormat.format(Data.userData.fareFactor) + "x");
                } else {
                    relativeLayoutInitialFareFactor.setVisibility(View.GONE);
                }
                setBottomMarginOfView(initialMyLocationBtn, 80f);
                //setBottomMarginOfView(imageViewRideNow, 40f);
                //setBottomMarginOfView(changeLocalityBtn, 100f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		if(!promoOpened) {
			checkForGoogleLogoVisibilityBeforeRide();
		}
    }

	private void setBottomMarginOfView(View view, float bottomMargin){
		try {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
			params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, (int)(ASSL.Yscale() * bottomMargin));
			view.setLayoutParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setAssignedDriverData(PassengerScreenMode mode) {
        textViewInRideDriverName.setText(Data.assignedDriverInfo.name);
        if (!"".equalsIgnoreCase(Data.assignedDriverInfo.carNumber)) {
            textViewInRideDriverCarNumber.setText(Data.assignedDriverInfo.carNumber);
        } else {
            textViewInRideDriverCarNumber.setText("");
        }

        if (!Data.NO_PROMO_APPLIED.equalsIgnoreCase(Data.assignedDriverInfo.promoName)) {
            textViewInRidePromoName.setText(Data.assignedDriverInfo.promoName);
        } else {
            textViewInRidePromoName.setText("");
        }


        textViewInRideFareFactor.setVisibility(View.VISIBLE);
        if (Data.userData.fareFactor > 1) {
            textViewInRideFareFactor.setText("Price: " + decimalFormat.format(Data.userData.fareFactor) + "x");
        } else if (Data.userData.fareFactor < 1) {
            textViewInRideFareFactor.setText("Price: " + decimalFormat.format(Data.userData.fareFactor) + "x");
        } else {
            textViewInRideFareFactor.setText("");
        }

        if (textViewInRidePromoName.getText().length() == 0 && textViewInRideFareFactor.getText().length() == 0) {
            relativeLayoutInRideInfo.setVisibility(View.GONE);
        } else {
            relativeLayoutInRideInfo.setVisibility(View.VISIBLE);
        }


        if (PassengerScreenMode.P_REQUEST_FINAL == mode || PassengerScreenMode.P_DRIVER_ARRIVED == mode) {
            updateDriverETAText(mode);
        } else {
            textViewInRideState.setText("Ride in\nprogress");
        }


        Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
        try {
			if(!"".equalsIgnoreCase(Data.assignedDriverInfo.image)) {
				Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).transform(new CircleTransform()).into(imageViewInRideDriver);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	private void setPaymentOptionInRide(){
		try{
			if(Data.assignedDriverInfo.getPreferredPaymentMode() == PaymentOption.PAYTM.getOrdinal()){
				relativeLayoutIRPaymentOption.setVisibility(View.VISIBLE);
				imageViewIRPaymentOptionPaytm.setVisibility(View.VISIBLE);
				imageViewIRPaymentOptionCash.setVisibility(View.GONE);
				textViewIRPaymentOption.setText(getResources().getString(R.string.paytm));
				textViewIRPaymentOptionValue.setVisibility(View.VISIBLE);
				textViewIRPaymentOptionValue.setText(getResources().getString(R.string.rupee)+" "+Data.userData.getPaytmBalanceStr());
			}
			else{
				relativeLayoutIRPaymentOption.setVisibility(View.VISIBLE);
				imageViewIRPaymentOptionPaytm.setVisibility(View.GONE);
				imageViewIRPaymentOptionCash.setVisibility(View.VISIBLE);
				textViewIRPaymentOption.setText(getResources().getString(R.string.cash));
				textViewIRPaymentOptionValue.setVisibility(View.GONE);
			}
		} catch(Exception e){
		}
	}



    public void updateDriverETAText(PassengerScreenMode passengerScreenMode) {
        if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode) {
            if (!"".equalsIgnoreCase(Data.assignedDriverInfo.getEta())) {
                try {
//                    double etaMin = Double.parseDouble(Data.assignedDriverInfo.getEta());
//                    if (etaMin > 1) {
//                        textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.getEta() + " minutes");
//                    } else {
//                        textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.getEta() + " minute");
//                    }
                    pickupLocationMarker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(CustomMapMarkerCreator
                                    .getTextBitmap(HomeActivity.this, assl, Data.assignedDriverInfo.getEta(), 12)));
                } catch (Exception e) {
                    e.printStackTrace();
//                    textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.getEta() + " minutes");
                }
                textViewInRideState.setText("Driver\nEnroute");
            }
        } else if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
            textViewInRideState.setText("Driver arrived\nat Pickup");
        }
    }



    private void initDropLocationSearchUI(boolean engaged){
		try {
			dropLocationSearched = true;
			if (!engaged) {
                relativeLayoutAssigningDropLocationParentSetVisibility(View.VISIBLE);
			}
			else{
                relativeLayoutFinalDropLocationParentSetVisibility(View.VISIBLE, dropLocationSearchText);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void stopDropLocationSearchUI(boolean engaged){
		try {
			dropLocationSearched = false;
			if (!engaged) {
                relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);
			} else {
                relativeLayoutFinalDropLocationParentSetVisibility(View.GONE, dropLocationSearchText);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public static boolean checkIfUserDataNull(Activity activity) {
        Log.e("checkIfUserDataNull", "Data.userData = " + Data.userData);
        if (Data.userData == null) {
            activity.startActivity(new Intent(activity, SplashNewActivity.class));
            activity.finish();
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

            if (!checkIfUserDataNull(HomeActivity.this)) {
                setUserData();

                try {
                    if (activityResumed) {
                        if (!feedbackSkipped && !promoOpened && !placeAdded
                            && PassengerScreenMode.P_RIDE_END != passengerScreenMode) {
                            callAndHandleStateRestoreAPI(false);
                        }
                        initiateTimersForStates(passengerScreenMode);

                        if (!intentFired) {
                            if (userMode == UserMode.PASSENGER &&
                                    (PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)) {
                                if (map != null && myLocation != null) {
                                    map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), 500, null);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (Data.supportFeedbackSubmitted) {
                        drawerLayout.closeDrawer(menuLayout);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogPopup.dialogBanner(HomeActivity.this, "Thank you for your valuable feedback");
                            }
                        }, 300);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Data.supportFeedbackSubmitted = false;


                openDeepLink();
                performDeepLinkRequest();

                EventsHolder.displayPushHandler = this;

                startNotifsUpdater();

                getPaytmBalance(this);


                String alertMessage = Prefs.with(this).getString(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                if (!"".equalsIgnoreCase(alertMessage)) {
                    Prefs.with(this).save(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                    DialogPopup.alertPopup(this, "", alertMessage);
                }

            }

            HomeActivity.checkForAccessTokenChange(this);

            activityResumed = true;
            intentFired = false;
            feedbackSkipped = false;
            placeAdded = false;

            initializeFusedLocationFetchers();

            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 19) {
                DialogPopup.showLocationSettingsAlert(activity);
            }


            if ((isAccessibilitySettingsOn(getApplicationContext())
                    && (Prefs.with(HomeActivity.this).contains(SPLabels.JUGNOO_JEANIE_STATE) == false))) {
                Prefs.with(HomeActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, true);
            } else {
                if ((isAccessibilitySettingsOn(getApplicationContext())
                        && (Prefs.with(HomeActivity.this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == true))) {
                    Prefs.with(HomeActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, true);
                } else {
                    //Prefs.with(HomeActivity.this).save(SPLabels.JUGNOO_JEANIE_STATE, false);
                }
            }

        Utils.hideSoftKeyboard(this, editTextRSFeedback);

//        genieLayout.setGenieParams();
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "product.clicklabs.jugnoo/product.clicklabs.jugnoo.sticky.WindowChangeDetectingService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("Jugnoo Jeanie", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Jugnoo Jeanie", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("Jugnoo Jeanie", "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v("Jugnoo Jeanie", "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v("Jugnoo Jeanie", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("Jugnoo Jeanie", "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

	private void startNotifsUpdater(){
		try {
			updateNotifsHandler.post(updateNotifsRunnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void stopNotifsUpdater(){
		try {
			updateNotifsHandler.removeCallbacks(updateNotifsRunnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Handler updateNotifsHandler = new Handler();
	private Runnable updateNotifsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				new UpdateNotificationsAsync(updateNotifsHandler, this).execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


    private class UpdateNotificationsAsync extends AsyncTask<String, String, String>{

		private Handler updateNotifsHandler;
		private Runnable updateNotifsRunnable;

		public UpdateNotificationsAsync(Handler updateNotifsHandler, Runnable updateNotifsRunnable){
			this.updateNotifsHandler = updateNotifsHandler;
			this.updateNotifsRunnable = updateNotifsRunnable;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				ArrayList<NotificationData> notificationDatas = Database2.getInstance(HomeActivity.this).getAllNotification();
				if(notificationDatas.size() == 0) {
					Prefs.with(HomeActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
				}
				else if(notificationDatas.size() > 0
						&& Prefs.with(HomeActivity.this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) > notificationDatas.size()){
					Prefs.with(HomeActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, notificationDatas.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			setUserData();
			try {
				updateNotifsHandler.postDelayed(updateNotifsRunnable, 30000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}





	private void openDeepLink(){
		try{

			if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutInvite.performClick();
			}
			else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutWallet.performClick();
			}
			else if(AppLinkIndex.PROMOTIONS.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutPromotions.performClick();
			}
			else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutTransactions.performClick();
			}
			else if(AppLinkIndex.FARE_DETAILS.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutFareDetails.performClick();
			}
			else if(AppLinkIndex.SUPPORT.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutSupport.performClick();
			}
			else if(AppLinkIndex.ABOUT.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutAbout.performClick();
			}
			else if(AppLinkIndex.ACCOUNT.getOrdinal() == Data.deepLinkIndex){
				linearLayoutProfile.performClick();
			}
			else if(AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == Data.deepLinkIndex){
				relativeLayoutNotification.performClick();
			}

		} catch(Exception e){
			e.printStackTrace();
		}
		Data.deepLinkIndex = -1;
	}

	private void performDeepLinkRequest(){
		try{
			if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
				if(Data.deepLinkPickup == 1) {
					zoomingForDeepLink = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (map != null) {
								zoomToCurrentLocationWithOneDriver(new LatLng(Data.deepLinkPickupLatitude, Data.deepLinkPickupLongitude));
							}
							zoomingForDeepLink = false;
						}
					}, 500);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		Data.deepLinkPickup = -1;
	}


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public void startActivity(Intent intent) {
        intentFired = true;
        super.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode==RESULT_OK) {
                if (requestCode == ADD_HOME) {
                    String strResult = data.getStringExtra("PLACE");
                    Gson gson = new Gson();
                    AutoCompleteSearchResult searchResult = gson.fromJson(strResult, AutoCompleteSearchResult.class);
                    if(searchResult != null){
                        placeAdded = true;
                        Prefs.with(HomeActivity.this).save(SPLabels.ADD_HOME, strResult);
                    }else {
                    }

                } else if (requestCode == ADD_WORK) {
                    String strResult = data.getStringExtra("PLACE");
                    Gson gson = new Gson();
                    AutoCompleteSearchResult searchResult = gson.fromJson(strResult, AutoCompleteSearchResult.class);
                    if(searchResult != null) {
                        placeAdded = true;
                        Prefs.with(HomeActivity.this).save(SPLabels.ADD_WORK, strResult);
                    }else{
                    }
                } else {
                    Log.v("onActivityResult else part", "onActivityResult else part");
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void checkForAccessTokenChange(Activity activity) {
        Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
        if (!"".equalsIgnoreCase(pair.first)) {
            if (Data.userData == null) {
                logoutIntent(activity);
            } else {
                if (!pair.first.equalsIgnoreCase(Data.userData.accessToken)) {
                    logoutIntent(activity);
                }
            }
        } else {
            if (Data.userData == null) {

            } else {
                logoutIntent(activity);
            }
        }
    }


    public static void logoutIntent(Activity cont) {
        try {
            FacebookLoginHelper.logoutFacebook();
            Data.userData = null;
            Intent intent = new Intent(cont, SplashNewActivity.class);
            cont.startActivity(intent);
            cont.finish();
            cont.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			Branch.getInstance(cont).logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
		stopNotifsUpdater();
        destroyFusedLocationFetchers();

        try {
            if (userMode == UserMode.PASSENGER) {
                pauseAllTimers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onPause();

    }


	private void backFromSearchToInitial(){
		try {
			textViewInitialSearch.setText("");
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
			switchPassengerScreen(passengerScreenMode);
			FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onBackPressed() {
        try {
//            if (genieLayout.areJugnooIconsVisible()) {
//                genieLayout.hideAnims();
//            }

            if (PassengerScreenMode.P_SEARCH == passengerScreenMode) {
				backFromSearchToInitial();
            }
            else if (promoOpened && PassengerScreenMode.P_INITIAL == passengerScreenMode){
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
            }
            else if(dropLocationSearched && PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
                stopDropLocationSearchUI(false);
                FlurryEventLogger.event(DROP_LOCATION_OPENED_NOT_USED_FINDING_DRIVER);
            }
            else if(dropLocationSearched &&
					(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
							PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
							PassengerScreenMode.P_IN_RIDE == passengerScreenMode)){
                stopDropLocationSearchUI(true);
                FlurryEventLogger.event(DROP_LOCATION_OPENED_BUT_NOT_USED_RIDE_ACCEPTED);
            }
            else{
                ActivityCompat.finishAffinity(this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        startService(new Intent(BaseActivity.GENIE_SERVICE));
                    }
                }, 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ActivityCompat.finishAffinity(this);
        }
    }

    @Override
    public void onDestroy() {
        try {

            GCMIntentService.clearNotifications(HomeActivity.this);

            destroyFusedLocationFetchers();

            ASSL.closeActivity(drawerLayout);
            cancelTimerUpdateDrivers();

            appInterruptHandler = null;

            FacebookLoginHelper.logoutFacebook();

            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void callFindADriverAndShowPromotionsAPIS(LatLng requestLatLng){
        promoCouponSelectedForRide = null;

        findDriversETACall(Data.pickupLatLng);
        fetchPromotionsAPI(this, Data.pickupLatLng);
    }

    private void findDriversETACall(final LatLng destination){
        try {
            if (userMode == UserMode.PASSENGER) {
                addCurrentLocationAddressMarker(destination);
                textViewInitialInstructions.setVisibility(View.GONE);
                dontCallRefreshDriver = false;
                etaMinutes = "5";
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("latitude", "" + destination.latitude);
            params.put("longitude", "" + destination.longitude);

            if (1 == showAllDrivers) {
                params.put("show_all", "1");
            }
            if(1 == showDriverInfo){
                params.put("show_phone_no", "1");
            }

            Log.i("params in find_a_driver", "=" + params);

            RestClient.getApiServices().findADriverCall(params, new Callback<FindADriverResponse>() {
                @Override
                public void success(FindADriverResponse findADriverResponse, Response response) {
                    try {
                        Log.e("find_a_driver resp", "resp- " + new String(((TypedByteArray) response.getBody()).getBytes()));
                        Data.driverInfos.clear();
                        for (FindADriverResponse.Driver driver : findADriverResponse.getDrivers()) {
                            double bearing = 0;
                            if(driver.getBearing() != null){
                                bearing = driver.getBearing();
                            }
                            Data.driverInfos.add(new DriverInfo(String.valueOf(driver.getUserId()), driver.getLatitude(), driver.getLongitude(), driver.getUserName(), "",
                                    "", driver.getPhoneNo(), String.valueOf(driver.getRating()), "", 0, bearing));
                        }
                        etaMinutes = String.valueOf(findADriverResponse.getEta());
                        if(findADriverResponse.getPriorityTipCategory() != null){
                            priorityTipCategory = findADriverResponse.getPriorityTipCategory();
                        }

                        Data.userData.fareFactor = findADriverResponse.getFareFactor();
                        if (findADriverResponse.getFarAwayCity() == null) {
                            farAwayCity = "";
                        } else {
                            farAwayCity = findADriverResponse.getFarAwayCity();
                        }

                        if (relativeLayoutLocationError.getVisibility() == View.GONE) {
                            showDriverMarkersAndPanMap(destination);
                            dontCallRefreshDriver = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dontCallRefreshDriver = false;
                                }
                            }, 300);

                            if (Data.driverInfos.size() == 0) {
                                //textViewInitialInstructions.setVisibility(View.VISIBLE);
                                //textViewInitialInstructions.setText("No drivers nearby");
                                textViewCentrePinETA.setText("-");
                            } else {
                                textViewInitialInstructions.setVisibility(View.GONE);
                                textViewCentrePinETA.setText(etaMinutes);
                            }

                            setServiceAvailablityUI(farAwayCity);
                        }
                        setFareFactorToInitialState();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    //textViewInitialInstructions.setVisibility(View.VISIBLE);
                    textViewInitialInstructions.setText("Couldn't find drivers nearby.");
                    textViewCentrePinETA.setText("-");
                    noDriverNearbyToast("Couldn't find drivers nearby.");
                    setServiceAvailablityUI(farAwayCity);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchPromotionsAPI(final Activity activity, LatLng promoLatLng) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("latitude", "" + promoLatLng.latitude);
            params.put("longitude", "" + promoLatLng.longitude);
            Log.i("params", "=" + params);

            RestClient.getApiServices().showAvailablePromotionsCall(params, new Callback<ShowPromotionsResponse>() {
                @Override
                public void success(ShowPromotionsResponse showPromotionsResponse, Response response) {

                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj = new JSONObject(jsonString);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.AVAILABLE_PROMOTIONS.getOrdinal() == showPromotionsResponse.getFlag()) {
                                ArrayList<PromoCoupon> promoCoupons = new ArrayList<PromoCoupon>();
                                for (ShowPromotionsResponse.Coupon coupon : showPromotionsResponse.getCoupons()) {
                                    promoCoupons.add(new CouponInfo(coupon.getAccountId(),
                                            coupon.getCouponType(),
                                            coupon.getStatus(),
                                            coupon.getTitle(),
                                            coupon.getSubtitle(),
                                            coupon.getDescription(),
                                            coupon.getImage(),
                                            coupon.getRedeemedOn(),
                                            coupon.getExpiryDate(), "", ""));
                                }
                                for (ShowPromotionsResponse.Promotion promotion : showPromotionsResponse.getPromotions()) {
                                    promoCoupons.add(new PromotionInfo(promotion.getPromoId(),
                                            promotion.getTitle(),
                                            promotion.getTermsNConds()));
                                }

                                double fareFactor = Double.parseDouble(showPromotionsResponse.getDynamicFactor());
                                for(ShowPromotionsResponse.FareStructure fareStructure : showPromotionsResponse.getFareStructure()){
                                    String startTime = fareStructure.getStartTime();
                                    String endTime = fareStructure.getEndTime();
                                    String localStartTime = DateOperations.getUTCTimeInLocalTimeStamp(startTime);
                                    String localEndTime = DateOperations.getUTCTimeInLocalTimeStamp(endTime);
                                    long diffStart = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localStartTime);
                                    long diffEnd = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localEndTime);
                                    double convenienceCharges = 0;
                                    if(fareStructure.getConvenienceCharge() != null){
                                        convenienceCharges = fareStructure.getConvenienceCharge();
                                    }
                                    if(diffStart >= 0 && diffEnd <= 0){
                                        Data.fareStructure = new FareStructure(fareStructure.getFareFixed(),
                                                fareStructure.getFareThresholdDistance(),
                                                fareStructure.getFarePerKm(),
                                                fareStructure.getFarePerMin(),
                                                fareStructure.getFareThresholdTime(),
                                                fareStructure.getFarePerWaitingMin(),
                                                fareStructure.getFareThresholdWaitingTime(), convenienceCharges);
                                        Data.fareStructure.fareFactor = fareFactor;
                                        break;
                                    }
                                }

                                Data.fareStructure.fareFactor = Data.userData.fareFactor;

                                slidingBottomPanel.update(promoCoupons);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("show promotion api", "errorrrr"+error.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	private void checkForGoogleLogoVisibilityBeforeRide(){
		try{
			float padding = 0;
			if(textViewInitialInstructions.getVisibility() == View.VISIBLE){
				padding = padding + 58;
			}
			if(relativeLayoutInitialFareFactor.getVisibility() == View.VISIBLE){
				padding = padding + 62;
			}
			setGoogleMapPadding(20);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	//Our service is not available in this area
	private void setServiceAvailablityUI(String farAwayCity){
		if (!"".equalsIgnoreCase(farAwayCity)) {
            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            //textViewInitialInstructions.setVisibility(View.VISIBLE);
            textViewInitialInstructions.setText(farAwayCity);
            changeLocalityLayout.setVisibility(View.VISIBLE);
            textViewChangeLocality.setText(farAwayCity);
            //noDriverNearbyToast(farAwayCity);

			imageViewRideNow.setVisibility(View.GONE);

			initialMyLocationBtn.setVisibility(View.GONE);
			changeLocalityBtn.setVisibility(View.VISIBLE);
			initialMyLocationBtnChangeLoc.setVisibility(View.VISIBLE);
//									genieLayout.setVisibility(View.GONE);
		} else {
			if(!promoOpened) {
				imageViewRideNow.setVisibility(View.VISIBLE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
			}
            //slidingBottomPanel.getSlidingUpPanelLayout().setPanelHeight((int)(ASSL.Yscale()*110));
            //slidingBottomPanel.getSlidingUpPanelLayout().setEnabled(true);
            //slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            changeLocalityLayout.setVisibility(View.GONE);
			changeLocalityBtn.setVisibility(View.GONE);
			initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

			if (PassengerScreenMode.P_INITIAL == passengerScreenMode && !promoOpened) {
//										genieLayout.setVisibility(View.VISIBLE);
			}
		}
	}



    public MarkerOptions getStartPickupLocMarkerOptions(LatLng latLng, boolean inRide){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("pickup location");
        markerOptions.snippet("");
        markerOptions.position(latLng);
        if(inRide){
            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(CustomMapMarkerCreator
                            .createPinMarkerBitmapStart(HomeActivity.this, assl)));
        } else{
            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(CustomMapMarkerCreator
                            .getTextBitmap(HomeActivity.this, assl, Data.assignedDriverInfo.getEta(), 12)));
        }
//
        return markerOptions;
    }

    public MarkerOptions getAssignedDriverCarMarkerOptions(LatLng latlng){
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title("driver position");
        markerOptions1.snippet("");
        markerOptions1.position(latlng);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
        markerOptions1.anchor(0.5f, 0.5f);
        return markerOptions1;
    }

    public void addDriverMarkerForCustomer(DriverInfo driverInfo) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("driver shown to customer");
        markerOptions.snippet("" + driverInfo.userId);
        markerOptions.position(driverInfo.latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.rotation((float) driverInfo.getBearing());
        map.addMarker(markerOptions);
    }

    public void addCurrentLocationAddressMarker(LatLng latLng) {
        try {
            if (Data.userData.canChangeLocation == 0) {
                addUserCurrentLocationAddressMarker(latLng);
            }
        } catch (Exception e) {
        }
    }

    public void addUserCurrentLocationAddressMarker(LatLng latLng) {
        try {
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("customer_current_location");
            markerOptions.snippet("");
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
            currentLocationMarker = map.addMarker(markerOptions);
        } catch (Exception e) {
        }
    }

    public void showDriverMarkersAndPanMap(final LatLng userLatLng) {
        try {
			if("".equalsIgnoreCase(farAwayCity)) {
				if (userMode == UserMode.PASSENGER &&
						((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)
								|| PassengerScreenMode.P_ASSIGNING == passengerScreenMode)) {
					if (map != null) {
						map.clear();
						addCurrentLocationAddressMarker(userLatLng);
						setDropLocationMarker();
						for (int i = 0; i < Data.driverInfos.size(); i++) {
							addDriverMarkerForCustomer(Data.driverInfos.get(i));
						}
						if (!mapTouchedOnce) {
							zoomToCurrentLocationWithOneDriver(userLatLng);
							mapTouchedOnce = true;
						}
					}
				}
			}
			if (userMode == UserMode.PASSENGER && (passengerScreenMode == PassengerScreenMode.P_ASSIGNING)) {
				addUserCurrentLocationAddressMarker(userLatLng);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zoomToCurrentLocationWithOneDriver(final LatLng userLatLng) {

        try {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLng firstLatLng = null;
            if((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_ASSIGNING == passengerScreenMode)
                && Data.driverInfos.size() > 0) {
                firstLatLng = Data.driverInfos.get(0).latLng;
            }
            if (firstLatLng != null) {
                boolean fixedZoom = false;
                double distance = MapUtils.distance(userLatLng, firstLatLng);
                if (distance <= 15000) {
                    boundsBuilder.include(new LatLng(userLatLng.latitude, firstLatLng.longitude));
                    boundsBuilder.include(new LatLng(firstLatLng.latitude, userLatLng.longitude));
                    boundsBuilder.include(new LatLng(userLatLng.latitude, ((2 * userLatLng.longitude) - firstLatLng.longitude)));
                    boundsBuilder.include(new LatLng(((2 * userLatLng.latitude) - firstLatLng.latitude), userLatLng.longitude));
                } else {
                    fixedZoom = true;
                }

                final boolean finalFixedZoom = fixedZoom;
                boundsBuilder.include(userLatLng);

                try {
                    final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder, FIX_ZOOM_DIAGONAL);
                    final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(finalFixedZoom){
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM));
                                }
                                else {
                                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 1000, null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
								if("".equalsIgnoreCase(farAwayCity)) {
									map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM));
								}
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	Thread pickupAddressFetcherThread;
	private void getPickupAddress(final LatLng currentLatLng){
		stopPickupAddressFetcherThread();
		try{
			if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
				progressBarInitialSearch.setVisibility(View.VISIBLE);
				textViewInitialSearch.setText("");
				textViewInitialSearch.setHint("Getting address...");
				pickupAddressFetcherThread = new Thread(new Runnable() {
					@Override
					public void run() {
						gapiAddressForPin = MapUtils.getGAPIAddressObject(currentLatLng);
						String address = gapiAddressForPin.getSearchableAddress();

//						address = MapUtils.getGAPIAddress(currentLatLng);
						setPickupAddress(address);
						stopPickupAddressFetcherThread();
					}
				});
				pickupAddressFetcherThread.start();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void stopPickupAddressFetcherThread(){
		try{
			if(pickupAddressFetcherThread != null){
				pickupAddressFetcherThread.interrupt();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		pickupAddressFetcherThread = null;
	}

	private void setPickupAddress(final String address){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
					textViewInitialSearch.setHint("Set pick up location");
					textViewInitialSearch.setText(address);
				}
				progressBarInitialSearch.setVisibility(View.GONE);
			}
		});
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
            Log.i("session_id", "=" + Data.cSessionId);


            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/cancel_the_request", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        callAndHandleStateRestoreAPI(true);
                    }


                    @Override
                    public void onSuccess(String response) {
                        Log.e("Server response", "response = " + response);
                        DialogPopup.dismissLoadingDialog();

                        try {
                            jObj = new JSONObject(response);

                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                    HomeActivity.logoutUser(activity);
                                } else {
                                    DialogPopup.alertPopupWithListener(activity, "", errorMessage,
                                            new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    callAndHandleStateRestoreAPI(true);
                                                }
                                            });
                                }
                            } else {
                                customerUIBackToInitialAfterCancel();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }


                    }
                });
        } else {
            //DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            DialogPopup.dialogNoInternet(HomeActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                @Override
                public void positiveClick() {
                    cancelCustomerRequestAsync(HomeActivity.this);
                }

                @Override
                public void neutralClick() {

                }

                @Override
                public void negativeClick() {

                }
            });
        }
    }


    public void customerUIBackToInitialAfterCancel() {
		firstTimeZoom = false;
		pickupDropZoomed = false;

        cancelTimerRequestRide();

        passengerScreenMode = PassengerScreenMode.P_INITIAL;
        switchPassengerScreen(passengerScreenMode);

        if (map != null && pickupLocationMarker != null) {
            pickupLocationMarker.remove();
        }

        callMapTouchedRefreshDrivers();

    }


    public void initializeStartRideVariables() {

        HomeActivity.previousWaitTime = 0;
        HomeActivity.previousRideTime = 0;
        HomeActivity.totalDistance = -1;

        clearRideSPData();
    }


    void noDriverAvailablePopup(final Activity activity, boolean zeroDriversNearby, String message) {
        try {
            if (noDriversDialog != null && noDriversDialog.isShowing()) {
                noDriversDialog.dismiss();
            }
            noDriversDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            noDriversDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            noDriversDialog.setContentView(R.layout.dialog_custom_one_button);

            FrameLayout frameLayout = (FrameLayout) noDriversDialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = noDriversDialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            noDriversDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            noDriversDialog.setCancelable(true);
            noDriversDialog.setCanceledOnTouchOutside(true);

            TextView textHead = (TextView) noDriversDialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            textHead.setVisibility(View.GONE);

            TextView textMessage = (TextView) noDriversDialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.latoRegular(activity));

            textMessage.setMovementMethod(new ScrollingMovementMethod());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            if (zeroDriversNearby) {
                textMessage.setText("Sorry there are no drivers available nearby within 3 km. We will look into it");
            } else {
                if ("".equalsIgnoreCase(message)) {
                    textMessage.setText("Sorry, All our drivers are currently busy. We are unable to offer you services right now. Please try again sometime later.");
                } else {
                    textMessage.setText(message);
                }
            }


            Button btnOk = (Button) noDriversDialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (noDriversDialog != null) {
                        noDriversDialog.dismiss();
                    }
                    noDriversDialog = null;
                }
            });

            frameLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (noDriversDialog != null) {
                        noDriversDialog.dismiss();
                    }
                    noDriversDialog = null;
                }
            });

            noDriversDialog.findViewById(R.id.rl1).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });

            noDriversDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void acceptAppRatingRequestAPI(final Activity activity) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                RequestParams params = new RequestParams();
                params.put("access_token", Data.userData.accessToken);
                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/accept_app_rating_request", params,
                    new CustomAsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(Throwable arg3) {
                            Log.e("request fail", arg3.toString());
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i("Server response accept_app_rating_request", "response = " + response);
                            try {
                                jObj = new JSONObject(response);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {

                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getRideSummaryAPI(final Activity activity, final String engagementId) {
        if (!checkIfUserDataNull(activity)) {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, "Loading...");
                RequestParams params = new RequestParams();
                params.put("access_token", Data.userData.accessToken);
                params.put("engagement_id", engagementId);
                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/get_ride_summary", params,
                    new CustomAsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(Throwable arg3) {
                            DialogPopup.dismissLoadingDialog();
                            endRideRetryDialog(activity, engagementId, Data.SERVER_NOT_RESOPNDING_MSG);
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i("Server response get_ride_summary", "response = " + response);
                            DialogPopup.dismissLoadingDialog();
                            try {
                                jObj = new JSONObject(response);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {

//											{
//											    "pickup_address": "1050-1091, Madhya Marg, 28B, Sector 26 East, Chandigarh 160102, India",
//											    "drop_address": "1050-1091, Madhya Marg, 28B, Sector 26 East, Chandigarh 160102, India",
//											    "pickup_time": "05:51 PM",
//											    "drop_time": "05:51 PM",
//											    "fare": 26,
//											    "discount": 0,
//											    "paid_using_wallet": 0,
//											    "to_pay": 26,
//											    "distance": 0,
//											    "ride_time": 1,
//											    "flag": 115
//											}

                                        try {
                                            if (jObj.has("rate_app")) {
                                                Data.customerRateAppFlag = jObj.getInt("rate_app");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            if (jObj.has("jugnoo_balance")) {
                                                Data.userData.setJugnooBalance(jObj.getDouble("jugnoo_balance"));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
										try {
											if (jObj.has("paytm_balance")) {
												Data.userData.setPaytmBalance(jObj.getDouble("paytm_balance"));
											}
										} catch (Exception e) {
											e.printStackTrace();
										}

										Data.endRideData = JSONParser.parseEndRideData(jObj, engagementId, Data.fareStructure.fixedFare);

                                        clearSPData();
                                        map.clear();
                                        passengerScreenMode = PassengerScreenMode.P_RIDE_END;
                                        switchPassengerScreen(passengerScreenMode);

                                        Utils.hideSoftKeyboard(activity, textViewInitialSearch);

                                        setUserData();

                                    } else {
                                        endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
                            }
                        }
                    });
            } else {
                endRideRetryDialog(activity, engagementId, Data.CHECK_INTERNET_MSG);
            }
        }
    }

    public void endRideRetryDialog(final Activity activity, final String engagementId, String errorMessage) {
        DialogPopup.alertPopupWithListener(activity, "", errorMessage + "\nTap OK to retry", new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRideSummaryAPI(activity, engagementId);
            }
        });
    }






    public void sendDropLocationAPI(final Activity activity, final LatLng dropLatLng, final ProgressWheel progressWheel) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			progressWheel.setVisibility(View.VISIBLE);

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("session_id", Data.cSessionId);
            params.put("op_drop_latitude", ""+dropLatLng.latitude);
            params.put("op_drop_longitude", "" + dropLatLng.longitude);
			if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
				params.put("engagement_id", Data.cEngagementId);
			}

            Log.i("params", "=" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/add_drop_location", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
//                        DialogPopup.dismissLoadingDialog();
						progressWheel.setVisibility(View.GONE);
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }


                    @Override
                    public void onSuccess(String response) {
                        Log.e("Server response", "response = " + response);

                        try {
                            jObj = new JSONObject(response);
                            String message = JSONParser.getServerMessage(jObj);
                            if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
                                int flag = jObj.getInt("flag");
                                if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                                else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){

									Data.dropLatLng = dropLatLng;

                                    if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
                                        linearLayoutAssigningDropLocationClick.setVisibility(View.GONE);
                                        stopDropLocationSearchUI(false);
                                    }
                                    else if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
											PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
											PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
                                        stopDropLocationSearchUI(true);
                                        linearLayoutFinalDropLocationClick.setVisibility(View.GONE);

										if(Data.dropLatLng != null){
											setDropLocationMarker();
										}

										getDropLocationPathAndDisplay(Data.pickupLatLng);
                                    }

                                }
                                else{
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }
//                        DialogPopup.dismissLoadingDialog();
						progressWheel.setVisibility(View.GONE);
                    }
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            progressWheel.setVisibility(View.GONE);
        }
    }

    //Customer's timer
    Timer timerDriverLocationUpdater;
    TimerTask timerTaskDriverLocationUpdater;

    public void startDriverLocationUpdateTimer() {

        cancelDriverLocationUpdateTimer();

        try {
            timerDriverLocationUpdater = new Timer();

            timerTaskDriverLocationUpdater = new TimerTask() {

                @Override
                public void run() {
                    try {
                        if (AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)
                            && (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)
                            && (Data.userData != null)
                            && (Data.assignedDriverInfo != null)
								&& (Data.pickupLatLng != null)) {

                            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
                            nameValuePairs.add(new BasicNameValuePair("driver_id", Data.assignedDriverInfo.userId));
							nameValuePairs.add(new BasicNameValuePair("pickup_latitude", ""+Data.pickupLatLng.latitude));
							nameValuePairs.add(new BasicNameValuePair("pickup_longitude", ""+Data.pickupLatLng.longitude));


                            HttpRequester simpleJSONParser = new HttpRequester();
                            String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/get_driver_current_location", nameValuePairs);

                            Log.e("result of get_driver_current_location", "=" + result);
                            try {
                                JSONObject jObj = new JSONObject(result);

                                if (!jObj.isNull("error")) {
                                    String errorMessage = jObj.getString("error");
                                    if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                        HomeActivity.logoutUser(HomeActivity.this);
                                    }
                                } else {
//										{"flag":117,"latitude":30.718956,"longitude":76.810267,"eta":0}
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.DRIVER_LOCATION.getOrdinal() == flag) {
                                        final LatLng driverCurrentLatLng = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                                        String eta = "5";
                                        if (jObj.has("eta")) {
                                            eta = jObj.getString("eta");
                                        }
                                        if (Data.assignedDriverInfo != null) {
                                            Data.assignedDriverInfo.latLng = driverCurrentLatLng;
                                            Data.assignedDriverInfo.setEta(eta);
                                        }

                                        HomeActivity.this.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {
                                                    if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
                                                        if (map != null) {
                                                            if (HomeActivity.this.hasWindowFocus()) {
//                                                                driverLocationMarker.setPosition(driverCurrentLatLng);
                                                                MarkerAnimation.animateMarkerToICS(driverLocationMarker,
                                                                        driverCurrentLatLng, new LatLngInterpolator.Spherical());
                                                                updateDriverETAText(passengerScreenMode);
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            getDropLocationPathAndDisplay(Data.pickupLatLng);

            timerDriverLocationUpdater.scheduleAtFixedRate(timerTaskDriverLocationUpdater, 10, 15000);
            Log.i("timerDriverLocationUpdater", "started");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cancelDriverLocationUpdateTimer() {
        try {
            if (timerTaskDriverLocationUpdater != null) {
                timerTaskDriverLocationUpdater.cancel();
                timerTaskDriverLocationUpdater = null;
            }

            if (timerDriverLocationUpdater != null) {
                timerDriverLocationUpdater.cancel();
                timerDriverLocationUpdater.purge();
                timerDriverLocationUpdater = null;
            }
            Log.i("timerDriverLocationUpdater", "canceled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Customer's timer
    Timer timerUpdateDrivers;
    TimerTask timerTaskUpdateDrivers;

    public void startTimerUpdateDrivers() {
        cancelTimerUpdateDrivers();
        try {
            timerUpdateDrivers = new Timer();
            timerTaskUpdateDrivers = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (HomeActivity.appInterruptHandler != null) {
                            HomeActivity.appInterruptHandler.refreshDriverLocations();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timerUpdateDrivers.scheduleAtFixedRate(timerTaskUpdateDrivers, 100, 60000);
            Log.i("timerUpdateDrivers", "started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTimerUpdateDrivers() {
        try {
            if (timerTaskUpdateDrivers != null) {
                timerTaskUpdateDrivers.cancel();
                timerTaskUpdateDrivers = null;
            }
            if (timerUpdateDrivers != null) {
                timerUpdateDrivers.cancel();
                timerUpdateDrivers.purge();
                timerUpdateDrivers = null;
            }
            Log.i("timerUpdateDrivers", "canceled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Both driver and customer
    Timer timerMapAnimateAndUpdateRideData;
    TimerTask timerTaskMapAnimateAndUpdateRideData;


    public void startMapAnimateAndUpdateRideDataTimer() {
        cancelMapAnimateAndUpdateRideDataTimer();
        try {
            timerMapAnimateAndUpdateRideData = new Timer();
            timerTaskMapAnimateAndUpdateRideData = new TimerTask() {

                @Override
                public void run() {
                    try {
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("last_sent_max_id", "" +
                            Database2.getInstance(HomeActivity.this).getLastRowIdInRideInfo()));
                        nameValuePairs.add(new BasicNameValuePair("engagement_id", Data.cEngagementId));
                        nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));

                        HttpRequester simpleJSONParser = new HttpRequester();
                        String result = simpleJSONParser.getJSONFromUrlParamsViaGetRequest
                            (Config.getServerUrl() + "/get_ongoing_ride_path", nameValuePairs);

                        Log.e("result of get_ongoing_ride_path", "=" + result);

                        try {
                            final JSONObject jObj = new JSONObject(result);
                            final int flag = jObj.getInt("flag");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (ApiResponseFlags.RIDE_PATH_INFO_SUCCESS.getOrdinal() == flag) {

                                            ArrayList<RidePath> ridePathsList = new ArrayList<>();

                                            JSONArray jsonArray = jObj.getJSONArray("locations");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                RidePath currentRidePath = new RidePath(
                                                    jsonObject.getInt("id"),
                                                    jsonObject.getDouble("source_latitude"),
                                                    jsonObject.getDouble("source_longitude"),
                                                    jsonObject.getDouble("destination_latitude"),
                                                    jsonObject.getDouble("destination_longitude"));

                                                ridePathsList.add(currentRidePath);

                                                LatLng start = new LatLng(currentRidePath.sourceLatitude, currentRidePath.sourceLongitude);
                                                LatLng end = new LatLng(currentRidePath.destinationLatitude, currentRidePath.destinationLongitude);

                                                final PolylineOptions polylineOptions = new PolylineOptions();
                                                polylineOptions.add(start, end);
                                                polylineOptions.width(ASSL.Xscale() * 5);
                                                polylineOptions.color(RIDE_ELAPSED_PATH_COLOR);
                                                polylineOptions.geodesic(false);

                                                // Drawing poly-line in the Google Map
                                                if (map != null && polylineOptions != null) {
                                                    map.addPolyline(polylineOptions);
                                                }
                                            }

                                            try { Database2.getInstance(HomeActivity.this).createRideInfoRecords(ridePathsList); } catch (Exception e) { e.printStackTrace(); }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            getDropLocationPathAndDisplay(Data.pickupLatLng);
            displayOldPath();

            timerMapAnimateAndUpdateRideData.scheduleAtFixedRate(timerTaskMapAnimateAndUpdateRideData, 100, 15000);
            Log.i("timerMapAnimateAndUpdateRideData", "started");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cancelMapAnimateAndUpdateRideDataTimer() {
        try {
            if (timerTaskMapAnimateAndUpdateRideData != null) {
                timerTaskMapAnimateAndUpdateRideData.cancel();
                timerTaskMapAnimateAndUpdateRideData = null;
            }

            if (timerMapAnimateAndUpdateRideData != null) {
                timerMapAnimateAndUpdateRideData.cancel();
                timerMapAnimateAndUpdateRideData.purge();
                timerMapAnimateAndUpdateRideData = null;
            }
            Log.i("timerMapAnimateAndUpdateRideData", "canceled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayOldPath() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<RidePath> ridePathsList = new ArrayList<>();
                    ridePathsList.addAll(Database2.getInstance(HomeActivity.this).getRidePathInfo());

                    for (int i=0; i<ridePathsList.size(); i++) {
                        RidePath ridePath = ridePathsList.get(i);
                        final PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.add(new LatLng(ridePath.sourceLatitude, ridePath.sourceLongitude),
                            new LatLng(ridePath.destinationLatitude, ridePath.destinationLongitude));
                        polylineOptions.width(ASSL.Xscale() * 5);
                        polylineOptions.color(RIDE_ELAPSED_PATH_COLOR);
                        polylineOptions.geodesic(false);
                        if (map != null && polylineOptions != null) {
                            map.addPolyline(polylineOptions);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean toShowPathToDrop(){
        return (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
            PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
            PassengerScreenMode.P_IN_RIDE == passengerScreenMode);
    }

    public void getDropLocationPathAndDisplay(final LatLng lastLatLng) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(lastLatLng != null && Data.dropLatLng != null && !pickupDropZoomed){
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(lastLatLng).include(Data.dropLatLng);
                                            LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, FIX_ZOOM_DIAGONAL);
                                            float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 1000, new GoogleMap.CancelableCallback() {
												@Override
												public void onFinish() {
													pickupDropZoomed = true;
												}

												@Override
												public void onCancel() {
													pickupDropZoomed = false;
												}

											});
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    }
                            }, 500);
                        }
                    });
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) && Data.dropLatLng != null && lastLatLng != null && toShowPathToDrop()) {
							String url = MapUtils.makeDirectionsURL(lastLatLng, Data.dropLatLng);
							Log.i("url", "=" + url);
							String result = new HttpRequester().getJSONFromUrl(url);
							Log.i("result", "=" + result);
							if (result != null) {
								final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
								if (list.size() > 0) {
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											try {
												if (toShowPathToDrop()) {
													LatLngBounds.Builder builder = new LatLngBounds.Builder();

													if(lastLatLng != null && Data.dropLatLng != null){
														builder.include(lastLatLng).include(Data.dropLatLng);
													}

													pathToDropLocationPolylineOptions = new PolylineOptions();
													pathToDropLocationPolylineOptions.width(ASSL.Xscale() * 5).color(RIDE_LEFT_PATH).geodesic(true);
													for (int z = 0; z < list.size(); z++) {
														pathToDropLocationPolylineOptions.add(list.get(z));
														builder.include(list.get(z));
													}

													if (pathToDropLocationPolyline != null) {
														pathToDropLocationPolyline.remove();
													}
													pathToDropLocationPolyline = map.addPolyline(pathToDropLocationPolylineOptions);

													try {
														if(!pickupDropZoomed) {
															LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, FIX_ZOOM_DIAGONAL);
															float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
															map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 500, null);
															pickupDropZoomed = true;
														}
													} catch (Exception e) {
														e.printStackTrace();
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MarkerOptions getCustomerLocationMarkerOptions(LatLng customerLatLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("End Location");
        markerOptions.snippet("");
        markerOptions.position(customerLatLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmapEnd(HomeActivity.this, assl)));
        return markerOptions;
    }

    void callAnAutoPopup(final Activity activity, LatLng pickupLatLng) {
        try {

            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_custom_two_buttons);

            new ASSL(activity, (FrameLayout) dialog.findViewById(R.id.rv), 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.latoRegular(activity));

            textHead.setVisibility(View.VISIBLE);
            textHead.setText("Chalo Jugnoo Se");


            final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.latoRegular(activity));

            btnOk.setText("OK");
            btnCancel.setText("Cancel");

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppStatus.getInstance(activity).isOnline(activity)) {
                        dialog.dismiss();
                        if (Data.driverInfos.size() == 0) {
                            noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
                            //Toast.makeText(HomeActivity.this, getResources().getString(R.string.no_driver_nearby_try_again), Toast.LENGTH_LONG).show();
                        } else {
                            initiateRequestRide(true);
                            FlurryEventLogger.event(FINAL_CALL_RIDE);
                        }
                    } else{
                        DialogPopup.dialogNoInternet(HomeActivity.this, Data.CHECK_INTERNET_TITLE,
                                Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick() {
                                btnOk.performClick();
                            }

                            @Override
                            public void neutralClick() {

                            }

                            @Override
                            public void negativeClick() {

                            }
                        });
                    }
                }

            });

            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            dialog.findViewById(R.id.rl1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


            dialog.findViewById(R.id.rv).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Data.pickupLatLng = pickupLatLng;
            Data.dropLatLng = null;

            //30.7500, 76.7800
			//22.971723, 78.754263
            if((Utils.compareDouble(Data.pickupLatLng.latitude, 30.7500) == 0 && Utils.compareDouble(Data.pickupLatLng.longitude, 76.7800) == 0)
					||
					(Utils.compareDouble(Data.pickupLatLng.latitude, 22.971723) == 0 && Utils.compareDouble(Data.pickupLatLng.longitude, 78.754263) == 0)){
                myLocation = null;
                Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if (myLocation == null) {
                    //We could not detect your location. Are you sure you want to request an auto to pick you at this location
                    myLocation = new Location(LocationManager.GPS_PROVIDER);
                    myLocation.setLatitude(Data.pickupLatLng.latitude);
                    myLocation.setLongitude(Data.pickupLatLng.longitude);
                    myLocation.setAccuracy(100);
                    myLocation.setTime(System.currentTimeMillis());
                    textMessage.setText("We could not detect your location. Are you sure you want to request an auto to pick you at this location?");
                    dialog.show();
                } else {
                    boolean cached = false;
                    try {
                        if (myLocation != null) {
                            Bundle bundle = myLocation.getExtras();
                            cached = bundle.getBoolean("cached");
                        }
                    } catch (Exception e) {
                    }
                    if (cached) {
                        //Location accuracy is low. Are you sure you want to request an auto to pick you at this location
                        textMessage.setText("Location accuracy is low. Are you sure you want to request an auto to pick you at this location?");
                        dialog.show();
                    } else {
                        double distance = MapUtils.distance(Data.pickupLatLng, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        if (distance > MAP_PAN_DISTANCE_CHECK) {
                            textMessage.setText("The pickup location you have set is different from your current location. Are you sure you want an auto at this pickup location?");
                            dialog.show();
                        } else {
                            if (Data.driverInfos.size() == 0) {
                                noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
                                //Toast.makeText(HomeActivity.this, getResources().getString(R.string.no_driver_nearby_try_again), Toast.LENGTH_LONG).show();
                            } else{
                                initiateRequestRide(true);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noDriverNearbyToast(String message){
        Toast toast = Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * Displays popup to rate the app
     */
    public void rateAppPopup(final Activity activity) {
        try {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "Rate Us", "Liked our services!!! Please rate us on Play Store", "RATE NOW", "LATER",
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptAppRatingRequestAPI(activity);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo"));
                        activity.startActivity(intent);
                        FlurryEventLogger.event(RATE_US_NOW_POP_RATED);

                    }
                },
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FlurryEventLogger.event(RATE_US_NOW_POP_NOT_RATED);
                    }
                }, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            if (loggedOut) {
                loggedOut = false;

                cancelTimerUpdateDrivers();

                Intent intent = new Intent(HomeActivity.this, SplashNewActivity.class);
                intent.putExtra("no_anim", "yes");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        }
    }


    @Override
    public void startRideForCustomer(final int flag) {
        try {
            if (userMode == UserMode.PASSENGER && (passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)) {
                Log.e("in ", "herestartRideForCustomer");

                closeCancelActivity();

                if (flag == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("in in herestartRideForCustomer  run class", "=");
                            initializeStartRideVariables();
                            passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
                            switchPassengerScreen(passengerScreenMode);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("in in herestartRideForCustomer  run class", "=");
                            if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
								firstTimeZoom = false;
								pickupDropZoomed = false;
								passengerScreenMode = PassengerScreenMode.P_INITIAL;
                                switchPassengerScreen(passengerScreenMode);
                                DialogPopup.alertPopup(HomeActivity.this, "", "Your ride has been cancelled due to an unexpected issue");
                            }
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
                    callMapTouchedRefreshDrivers();
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
            if (userMode == UserMode.PASSENGER) {
                if (jObj.getString("session_id").equalsIgnoreCase(Data.cSessionId)) {
                    cancelTimerUpdateDrivers();
                    cancelTimerRequestRide();
                    fetchAcceptedDriverInfoAndChangeState(jObj, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCancelCompleted() {
        customerUIBackToInitialAfterCancel();
    }


    public void fetchAcceptedDriverInfoAndChangeState(JSONObject jObj, boolean inRide) {
        try {
            cancelTimerRequestRide();

            Data.cSessionId = jObj.getString("session_id");
            Data.cEngagementId = jObj.getString("engagement_id");
            Data.cDriverId = jObj.getString("driver_id");

            String userName = jObj.getString("user_name");
            String driverPhone = jObj.getString("phone_no");
            String driverImage = jObj.getString("user_image");
            String driverCarImage = jObj.getString("driver_car_image");
            double latitude = jObj.getDouble("current_location_latitude");
            double longitude = jObj.getDouble("current_location_longitude");
            double pickupLatitude, pickupLongitude;
            if (jObj.has("pickup_latitude")) {
                pickupLatitude = jObj.getDouble("pickup_latitude");
                pickupLongitude = jObj.getDouble("pickup_longitude");
            } else {
                if (myLocation != null) {
                    pickupLatitude = myLocation.getLatitude();
                    pickupLongitude = myLocation.getLongitude();
                } else {
                    pickupLatitude = map.getCameraPosition().target.latitude;
                    pickupLongitude = map.getCameraPosition().target.longitude;
                }
            }
            String carNumber = "";
            if (jObj.has("driver_car_no")) {
                carNumber = jObj.getString("driver_car_no");
            }
            int freeRide = 0;
            if (jObj.has("free_ride")) {
                freeRide = jObj.getInt("free_ride");
            }
			String eta = "5";
			if (jObj.has("eta")) {
				eta = jObj.getString("eta");
			}
            String driverRating = jObj.getString("rating");
			String promoName = JSONParser.getPromoName(jObj);

            Data.pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);
			Data.startRidePreviousLatLng = Data.pickupLatLng;

			double fareFactor = 1.0;
			if (jObj.has("fare_factor")) {
				fareFactor = jObj.getDouble("fare_factor");
			}
			Data.userData.fareFactor = fareFactor;
			double fareFixed = 0;
            try{
                fareFixed = jObj.optJSONObject("fare_details").optDouble("fare_fixed", 0);
            } catch(Exception e){
                e.printStackTrace();
            }
			int preferredPaymentMode = jObj.optInt("preferred_payment_mode", PaymentOption.CASH.getOrdinal());


            Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName,
                driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta, fareFixed, preferredPaymentMode);



			if(inRide){
				initializeStartRideVariables();
				passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						switchPassengerScreen(passengerScreenMode);
					}
				});
			}
			else{
				passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.e("assignedDriverInfo", "=" + Data.assignedDriverInfo);
						Log.e("myLocation", "=" + myLocation);
						if (myLocation != null) {
							passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
							switchPassengerScreen(passengerScreenMode);
						}
					}
				});
			}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void clearRideSPData() {

        SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
        Editor editor = pref.edit();

        editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
        editor.putString(Data.SP_WAIT_TIME, "0");
        editor.putString(Data.SP_RIDE_TIME, "0");
        editor.putString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis());
        editor.putString(Data.SP_LAST_LATITUDE, "0");
        editor.putString(Data.SP_LAST_LONGITUDE, "0");

        editor.commit();

        Database.getInstance(this).deleteSavedPath();
        Database.getInstance(this).close();

    }

    public void clearSPData() {

        SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
        Editor editor = pref.edit();

        editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
        editor.putString(Data.SP_WAIT_TIME, "0");
        editor.putString(Data.SP_RIDE_TIME, "0");
        editor.putString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis());
        editor.putString(Data.SP_LAST_LATITUDE, "0");
        editor.putString(Data.SP_LAST_LONGITUDE, "0");

        editor.commit();

        Database.getInstance(this).deleteSavedPath();
        Database.getInstance(this).close();

    }


    @Override
    public void customerEndRideInterrupt(final String engagementId) {
        try {
            if (userMode == UserMode.PASSENGER && engagementId.equalsIgnoreCase(Data.cEngagementId)) {
                closeCancelActivity();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getRideSummaryAPI(HomeActivity.this, engagementId);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeStatePushReceived() {
        try {
            closeCancelActivity();
            callAndHandleStateRestoreAPI(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeCancelActivity() {
        try {
            runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						if (RideCancellationActivity.activityCloser != null) {
							RideCancellationActivity.activityCloser.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logoutUser(final Activity cont) {
        try {

            SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
            Editor editor = pref.edit();
            editor.clear();
            editor.commit();
			Data.clearDataOnLogout(cont);

			PicassoTools.clearCache(Picasso.with(cont));

			cont.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					FacebookLoginHelper.logoutFacebook();

					AlertDialog.Builder builder = new AlertDialog.Builder(cont);
					builder.setMessage(cont.getResources().getString(R.string.your_login_session_expired)).setTitle(cont.getResources().getString(R.string.alert));
					builder.setCancelable(false);
					builder.setPositiveButton(cont.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								dialog.dismiss();
								Intent intent = new Intent(cont, SplashNewActivity.class);
								intent.putExtra("no_anim", "yes");
								cont.startActivity(intent);
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

			Branch.getInstance(cont).logout();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // *****************************Used for flurry work***************//
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
        FlurryAgent.onEvent("HomeActivity started");
		mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
		mGoogleApiClient.disconnect();
    }


    public void initializeFusedLocationFetchers() {
        destroyFusedLocationFetchers();
        if (myLocation == null) {
            if (lowPowerLF == null) {
                lowPowerLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 0);
            }
            if (highAccuracyLF == null) {
                highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
            }
        } else {
            if (highAccuracyLF == null) {
                highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
            }
        }
    }


    public void destroyFusedLocationFetchers() {
        destroyLowPowerFusedLocationFetcher();
        destroyHighAccuracyFusedLocationFetcher();
    }

    public void destroyLowPowerFusedLocationFetcher() {
        try {
            if (lowPowerLF != null) {
                lowPowerLF.destroy();
                lowPowerLF = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyHighAccuracyFusedLocationFetcher() {
        try {
            if (highAccuracyLF != null) {
                highAccuracyLF.destroy();
                highAccuracyLF = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void onLocationChanged(Location location, int priority) {
		try {
			Data.latitude = location.getLatitude();
			Data.longitude = location.getLongitude();
			if (priority == 0) {
				if (location.getAccuracy() <= LOW_POWER_ACCURACY_CHECK) {
					HomeActivity.myLocation = location;
					updatePickupLocation(location);
				}
			} else if (priority == 2) {
				destroyLowPowerFusedLocationFetcher();
				if (location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK) {
					HomeActivity.myLocation = location;
					updatePickupLocation(location);
				}
			}

			if(PassengerScreenMode.P_INITIAL == passengerScreenMode && !zoomedToMyLocation && !zoomingForDeepLink){
				farAwayCity = "";
				zoomToCurrentLocationWithOneDriver(new LatLng(location.getLatitude(), location.getLongitude()));
			}
			zoomedToMyLocation = true;


			boolean cached = false;
			try {
				if (myLocation != null) {
					Bundle bundle = myLocation.getExtras();
					cached = bundle.getBoolean("cached");
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
			if(!cached && PassengerScreenMode.P_INITIAL == passengerScreenMode
					&& relativeLayoutLocationError.getVisibility() == View.VISIBLE) {
				relativeLayoutLocationError.setVisibility(View.GONE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
				imageViewRideNow.setVisibility(View.VISIBLE);
//				genieLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    /**
     * Updates PickupLocation on first location fix, when passenger state is P_ASSIGNING_DRIVERS
     * and saving it in shared preferences.
     *
     * @param location location changed in listeners
     */
    public void updatePickupLocation(Location location) {
        if (userMode == UserMode.PASSENGER) {
            if (passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {

                if (Data.pickupLatLng.latitude == 0 && Data.pickupLatLng.longitude == 0) {
                    Data.pickupLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
                    Editor editor = pref.edit();
                    editor.putString(Data.SP_TOTAL_DISTANCE, "0");
                    editor.putString(Data.SP_LAST_LATITUDE, "" + Data.pickupLatLng.latitude);
                    editor.putString(Data.SP_LAST_LONGITUDE, "" + Data.pickupLatLng.longitude);
                    editor.commit();

					if (myLocation != null) {
                        callFindADriverAndShowPromotionsAPIS(Data.pickupLatLng);
					}
                }
            } else if (passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL) {

            }
        }
    }


    //Customer's timer
    Timer timerRequestRide;
    TimerTask timerTaskRequestRide;
    long requestRideLifeTime;
    long serverRequestStartTime = 0;
    long serverRequestEndTime = 0;
    long executionTime = -10;
    long requestPeriod = 20000;

    public void startTimerRequestRide() {
        cancelTimerRequestRide();
        try {

            requestRideLifeTime = ((2 * 60 * 1000) + (1 * 60 * 1000));
            serverRequestStartTime = System.currentTimeMillis();
            serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
            executionTime = -10;
            requestPeriod = 20000;

            timerRequestRide = new Timer();
            timerTaskRequestRide = new TimerTask() {
                @Override
                public void run() {

                    long startTime = System.currentTimeMillis();

                    try {
                        Log.e("request_ride execution", "=" + (serverRequestEndTime - executionTime));
                        if (executionTime >= serverRequestEndTime) {
                            cancelTimerRequestRide();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                            noDriverAvailablePopup(HomeActivity.this, false, "");
                                            HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                                            switchPassengerScreen(passengerScreenMode);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {

                                updateCancelButtonUI();

                                HashMap<String, String> nameValuePairs = new HashMap<>();
                                nameValuePairs.put("access_token", Data.userData.accessToken);
                                nameValuePairs.put("latitude", "" + Data.pickupLatLng.latitude);
                                nameValuePairs.put("longitude", "" + Data.pickupLatLng.longitude);

								//30.7500, 76.7800
//								nameValuePairs.add(new BasicNameValuePair("latitude", "30.7500"));
//								nameValuePairs.add(new BasicNameValuePair("longitude", "76.7800"));

                                if (myLocation != null) {
                                    nameValuePairs.put("current_latitude", "" + myLocation.getLatitude());
                                    nameValuePairs.put("current_longitude", "" + myLocation.getLongitude());
                                } else {
                                    nameValuePairs.put("current_latitude", "" + Data.pickupLatLng.latitude);
                                    nameValuePairs.put("current_longitude", "" + Data.pickupLatLng.longitude);
                                }

                                if (promoCouponSelectedForRide != null) {
                                    if (promoCouponSelectedForRide instanceof CouponInfo) {
                                        nameValuePairs.put("coupon_to_apply", "" + promoCouponSelectedForRide.id);
                                        if (promoCouponSelectedForRide.id == 0) {
                                            nameValuePairs.put("promo_to_apply", "" + promoCouponSelectedForRide.id);
                                        }
                                    } else if (promoCouponSelectedForRide instanceof PromotionInfo) {
                                        nameValuePairs.put("promo_to_apply", "" + promoCouponSelectedForRide.id);
                                    }
                                }

                                if ("".equalsIgnoreCase(Data.cSessionId)) {
                                    nameValuePairs.put("duplicate_flag", "0");
                                    nameValuePairs.put("fare_factor", "" + Data.userData.fareFactor);
                                    if (myLocation != null && myLocation.hasAccuracy()) {
                                        nameValuePairs.put("location_accuracy", "" + myLocation.getAccuracy());
                                    }

                                } else {
                                    nameValuePairs.put("duplicate_flag", "1");
                                }


                                String links = Database2.getInstance(HomeActivity.this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
								if(links != null){
                                    if(!"[]".equalsIgnoreCase(links)) {
                                        nameValuePairs.put("branch_referring_links", links);
                                    }
								}

								nameValuePairs.put("preferred_payment_mode", "" + Data.pickupPaymentOption);

                                Log.i("nameValuePairs of request_ride", "=" + nameValuePairs);
//                                String response = new HttpRequester().getJSONFromUrlParams(Config.getServerUrl() + "/request_ride", nameValuePairs);

                                Response responseRetro = RestClient.getApiServices().requestRide(nameValuePairs);
                                String response = new String(((TypedByteArray) responseRetro.getBody()).getBytes());

                                Log.e("response of request_ride", "=" + response);

//                                {
//                                    "flag": 105,
//                                    "log": "Assigning driver",
//                                    "session_id": 41082,
//                                    "latitude": 30.718031,
//                                    "longitude": 76.811286,
//                                    "start_time": "2015-12-22 05:55:59"
//                                }

//                                {
//                                    "engagement_id": 52949,
//                                    "session_id": 41085,
//                                    "driver_id": 1515,
//                                    "pickup_latitude": 30.718031,
//                                    "pickup_longitude": 76.811286,
//                                    "status": 1,
//                                    "free_ride": 0,
//                                    "fare_factor": 1,
//                                    "fare_details": {
//                                    "id": 5,
//                                        "fare_fixed": 15,
//                                        "fare_per_km": 5,
//                                        "fare_threshold_distance": 0,
//                                        "fare_per_min": 1,
//                                        "fare_threshold_time": 0,
//                                        "night_fare_applicable": 0,
//                                        "fare_per_waiting_min": 2,
//                                        "fare_threshold_waiting_time": 0,
//                                        "type": 0,
//                                        "per_ride_driver_subsidy": 0,
//                                        "accept_subsidy_per_km": 0,
//                                        "accept_subsidy_threshold_distance": 0,
//                                        "accept_subsidy_before_threshold": 0,
//                                        "accept_subsidy_after_threshold": 0
//                                },
//                                    "coupon": "",
//                                    "promotion": "",
//                                    "user_name": "Driver 007",
//                                    "phone_no": "+916000000040",
//                                    "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png",
//                                    "driver_car_image": "",
//                                    "driver_car_no": "DL 4C 1234",
//                                    "current_location_latitude": 30.719034,
//                                    "current_location_longitude": 76.810056,
//                                    "total_rating_got_driver": 8,
//                                    "total_rating_driver": 35,
//                                    "rating": 4.375,
//                                    "eta": 1,
//                                    "flag": 107,
//                                    "preferred_payment_mode": 1
//                                }

//                                {
//                                    "flag": 106,
//                                    "log": "Sorry, All our drivers are currently busy. We are unable to offer you services right now. Please try again sometime later."
//                                }
                                if (responseRetro == null || response == null
                                        || response.contains(HttpRequester.SERVER_TIMEOUT)) {
                                    Log.e("timeout", "=");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//											DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject jObj = new JSONObject(response);
                                        if (!jObj.isNull("error")) {
                                            final String errorMessage = jObj.getString("error");
                                            final int flag = jObj.getInt("flag");
                                            if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                                cancelTimerRequestRide();
                                                HomeActivity.logoutUser(activity);
                                            } else {
                                                if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                                    cancelTimerRequestRide();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                                DialogPopup.alertPopup(HomeActivity.this, "", errorMessage);
                                                                HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                                                                switchPassengerScreen(passengerScreenMode);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            int flag = jObj.getInt("flag");
                                            if (ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag) {
                                                final String log = jObj.getString("log");
                                                Log.e("ASSIGNING_DRIVERS log", "=" + log);
                                                final String start_time = jObj.getString("start_time");
                                                if (executionTime < 0) {
                                                    serverRequestStartTime = new DateOperations().getMilliseconds(start_time);
                                                    serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
                                                    long stopTime = System.currentTimeMillis();
                                                    long elapsedTime = stopTime - startTime;
                                                    executionTime = serverRequestStartTime + elapsedTime;
                                                }
												if ("".equalsIgnoreCase(Data.cSessionId)) {
													BranchMetricsUtils.logEvent(HomeActivity.this, BRANCH_EVENT_REQUEST_RIDE, true);
                                                    FbEvents.logEvent(HomeActivity.this, FB_EVENT_REQUEST_RIDE, true);
												}
                                                Data.cSessionId = jObj.getString("session_id");
                                            } else if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                    cancelTimerRequestRide();
                                                    fetchAcceptedDriverInfoAndChangeState(jObj, false);
                                                }
                                            } else if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {
                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                    cancelTimerRequestRide();
													fetchAcceptedDriverInfoAndChangeState(jObj, true);
                                                }
                                            } else if (ApiResponseFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag) {
                                                final String log = jObj.getString("log");
                                                Log.e("NO_DRIVERS_AVAILABLE log", "=" + log);
                                                cancelTimerRequestRide();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                            noDriverAvailablePopup(HomeActivity.this, false, log);
															firstTimeZoom = false;
															pickupDropZoomed = false;
															HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                                                            switchPassengerScreen(passengerScreenMode);
                                                        }
                                                    }
                                                });
                                            } else if(ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag){
                                                final String message = jObj.optString(KEY_MESSAGE, "");
                                                final double userDebt = jObj.optDouble(KEY_USER_DEBT, 0);
                                                Log.e("USER_IN_DEBT message", "=" + message);
                                                cancelTimerRequestRide();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                            HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                                                            switchPassengerScreen(passengerScreenMode);
                                                            new UserDebtDialog(HomeActivity.this, Data.userData).showUserDebtDialog(userDebt, message);
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateCancelButtonUI();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long stopTime = System.currentTimeMillis();
                    long elapsedTime = stopTime - startTime;

                    if (executionTime < 0) {
                        executionTime = serverRequestStartTime + elapsedTime;
                    }
                    if (executionTime > 0) {
                        if (elapsedTime >= requestPeriod) {
                            executionTime = executionTime + elapsedTime;
                        } else {
                            executionTime = executionTime + requestPeriod;
                        }
                    }
                    Log.i("request_ride execution", "=" + (serverRequestEndTime - executionTime));

                }
            };

            timerRequestRide.scheduleAtFixedRate(timerTaskRequestRide, 0, requestPeriod);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTimerRequestRide() {
        try {
            if (timerTaskRequestRide != null) {
                timerTaskRequestRide.cancel();
                timerTaskRequestRide = null;
            }
            if (timerRequestRide != null) {
                timerRequestRide.cancel();
                timerRequestRide.purge();
                timerRequestRide = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateCancelButtonUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ("".equalsIgnoreCase(Data.cSessionId)) {
                    relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);
                    initialCancelRideBtn.setVisibility(View.GONE);
                    findDriverJugnooAnimation.setVisibility(View.VISIBLE);
                    jugnooAnimation.start();
                } else {
                    if(Data.dropLatLng == null){
                        linearLayoutAssigningDropLocationClick.setVisibility(View.VISIBLE);
                    }
                    else{
                        linearLayoutAssigningDropLocationClick.setVisibility(View.GONE);
                    }
                    initialCancelRideBtn.setVisibility(View.VISIBLE);
                    jugnooAnimation.stop();
                    findDriverJugnooAnimation.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onNoDriversAvailablePushRecieved(final String logMessage) {
        cancelTimerRequestRide();
        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
			firstTimeZoom = false;
			pickupDropZoomed = false;
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noDriverAvailablePopup(HomeActivity.this, false, logMessage);
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                }
            });
        }
    }


    @Override
    public void onAfterRideFeedbackSubmitted(final int givenRating, final boolean skipped) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ReferralActions.incrementTransactionCount(HomeActivity.this);
                userMode = UserMode.PASSENGER;

                switchUserScreen();

                if (givenRating >= 4 && Data.customerRateAppFlag == 1) {
                    rateAppPopup(activity);
                } else {
                }
				firstTimeZoom = false;
				pickupDropZoomed = false;

                slidingBottomPanel.setSelectedCoupon(null);
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
                Utils.hideSoftKeyboard(HomeActivity.this, editTextRSFeedback);

				BranchMetricsUtils.logEvent(HomeActivity.this, BRANCH_EVENT_RIDE_COMPLETED, true);
                FbEvents.logEvent(HomeActivity.this, FB_EVENT_RIDE_COMPLETED, true);

            }
        });

    }




    @Override
    public void onJugnooCashAddedByDriver(final double jugnooBalance, final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (Data.userData != null) {
                        Data.userData.setJugnooBalance(jugnooBalance);
                        setUserData();
                    }
                    DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this,
                        "Jugnoo Cash added",
                        message,
                        "Check Balance", "Call Support",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
//                                HomeActivity.this.startActivity(new Intent(HomeActivity.this, WalletActivity.class));
                                HomeActivity.this.startActivity(new Intent(HomeActivity.this, PaymentActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            }
                        },
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Utils.openCallIntent(HomeActivity.this, Config.getSupportNumber(HomeActivity.this));
                            }
                        }, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDriverArrived(final String logMessage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                passengerScreenMode = PassengerScreenMode.P_DRIVER_ARRIVED;
                switchPassengerScreen(passengerScreenMode);
            }
        });
    }

    @Override
    public void refreshOnPendingCallsDone() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });
    }

    @Override
    public void showDialog(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                DialogPopup.alertPopup(HomeActivity.this, "", message);
            }
        });
    }

    @Override
    public void onEmergencyContactVerified(int emergencyContactId) {
        try {
            EmergencyContact emergencyContact = new EmergencyContact(emergencyContactId);
            if (Data.emergencyContactsList != null && Data.emergencyContactsList.contains(emergencyContact)) {
                Data.emergencyContactsList.get(Data.emergencyContactsList.indexOf(emergencyContact)).verificationStatus = 1;
                if (EmergencyContactsActivity.refreshEmergencyContacts != null) {
                    EmergencyContactsActivity.refreshEmergencyContacts.refreshEmergencyContacts();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String CALL = "CALL", SMS = "SMS", CALL_100 = "CALL_100";

    private void sosDialog(final Activity activity) {
        if (Data.emergencyContactsList != null) {
            boolean sosContactVerified = false;
            String primaryPhone = "", phoneString = "";

            String separator = "; ";
            if(android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
                separator = ", ";
            }

            if(Data.emergencyContactsList.size() > 1){
                if(1 == Data.emergencyContactsList.get(0).verificationStatus && 1 == Data.emergencyContactsList.get(1).verificationStatus){
                    sosContactVerified = true;
                    primaryPhone = Data.emergencyContactsList.get(0).phoneNo;
                    phoneString = Data.emergencyContactsList.get(0).phoneNo + separator + Data.emergencyContactsList.get(1).phoneNo;
                }
                else if(1 == Data.emergencyContactsList.get(0).verificationStatus){
                    sosContactVerified = true;
                    primaryPhone = Data.emergencyContactsList.get(0).phoneNo;
                    phoneString = Data.emergencyContactsList.get(0).phoneNo;
                }
                else if(1 == Data.emergencyContactsList.get(1).verificationStatus){
                    sosContactVerified = true;
                    primaryPhone = Data.emergencyContactsList.get(1).phoneNo;
                    phoneString = Data.emergencyContactsList.get(1).phoneNo;
                }
                else{
                    sosContactVerified = false;
                }
            }
            else if(Data.emergencyContactsList.size() > 0){
                if(1 == Data.emergencyContactsList.get(0).verificationStatus){
                    sosContactVerified = true;
                    primaryPhone = Data.emergencyContactsList.get(0).phoneNo;
                    phoneString = Data.emergencyContactsList.get(0).phoneNo;
                }
                else{
                    sosContactVerified = false;
                }
            }
            else{
                sosContactVerified = false;
            }


            if(sosContactVerified){
                sosAlertDialog(activity, primaryPhone, phoneString);
            }
            else{
                call100Dialog(activity);
            }

        } else {
            call100Dialog(activity);
        }
    }


    private void sosAlertDialog(final Activity activity, final String primaryPhone, final String phoneString){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Send ALERT?", "CALL", "SMS",
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openCallIntent(activity, primaryPhone);
                    raiseSOSAlertAPI(activity, CALL);
                    FlurryEventLogger.event(SOS_CALL_TO_EMERGENCY_CONTACT);
                }
            },
            new OnClickListener() {
                @Override
                public void onClick(View v) {
//                            Emergency Alert! 'So and so' needs your help.
//                                Their alert location _____________________.
//                                Driver Details : Name
//                            Phone Number
//                            Auto Details:  XXXXXXXXX"

                    //https://www.google.co.in/maps/preview?q=30.723848,76.852293

                    String locationLink = "https://maps.google.co.in/maps/preview?q=";
                    if (myLocation != null) {
                        locationLink = locationLink + myLocation.getLatitude() + "," + myLocation.getLongitude();
                    } else {
                        locationLink = locationLink + LocationFetcher.getSavedLatFromSP(activity) + "," + LocationFetcher.getSavedLngFromSP(activity);
                    }

                    String message = "Emergency Alert! "+Data.userData.userName+" needs your help.\n"+
                        "Their alert location "+locationLink+".\n" +
                        "Driver Details : "+Data.assignedDriverInfo.name+"\n" +
                        Data.assignedDriverInfo.phoneNumber+"\n" +
                        "Auto Details: "+Data.assignedDriverInfo.carNumber;

                    Utils.openSMSIntent(activity, phoneString, message);
                    raiseSOSAlertAPI(activity, SMS);
                    FlurryEventLogger.event(SOS_SMS_TO_EMERGENCY_CONTACT);
                }
            }, true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FlurryEventLogger.event(SOS_ALERT_CANCELLED);
                }
            });
    }

    private void call100Dialog(final Activity activity) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Send ALERT?", "CALL 100", "Add Contact",
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openCallIntent(activity, "100");
                    raiseSOSAlertAPI(activity, CALL_100);
                }
            },
            new OnClickListener() {
                @Override
                public void onClick(View v) {
					activity.startActivity(new Intent(activity, EmergencyContactsActivity.class));
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(SOS_ALERT_CANCELLED);
                }
            }, true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FlurryEventLogger.event(SOS_ALERT_CANCELLED);
                }
            });
    }


    private void raiseSOSAlertAPI(final Activity activity, String alertType) {
        try {
            final RequestParams params = new RequestParams();
            params.put("access_token", Data.userData.accessToken);
            params.put("driver_id", Data.assignedDriverInfo.userId);
            params.put("engagement_id", Data.cEngagementId);
            params.put("alert_type", alertType);

            if (myLocation != null) {
                params.put("latitude", "" + myLocation.getLatitude());
                params.put("longitude", "" + myLocation.getLongitude());
            } else {
                params.put("latitude", "" + LocationFetcher.getSavedLatFromSP(activity));
                params.put("longitude", "" + LocationFetcher.getSavedLngFromSP(activity));
            }

            final String url = Config.getServerUrl() + "/emergency/alert";

            AsyncHttpClient client = Data.getClient();
            client.post(url, params,
                new CustomAsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response /emergency/alert", "response = " + response);
                        try {
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callAndHandleStateRestoreAPI(final boolean showDialogs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Data.userData != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (showDialogs) {
                                        DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        int currentUserStatus = 2;
                        String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
                        if (resp.contains(HttpRequester.SERVER_TIMEOUT)) {
                            String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
                            if (resp1.contains(HttpRequester.SERVER_TIMEOUT)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (showDialogs) {
                                                DialogPopup.alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (showDialogs) {
                                        DialogPopup.dismissLoadingDialog();
                                    }
									if(!promoOpened && !placeAdded) {
										startUIAfterGettingUserStatus();
									}
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    class CheckForGPSAccuracyTimer {

        public Timer timer;
        public TimerTask timerTask;

        public long startTime, lifeTime, endTime, period, executionTime;
        public boolean isRunning = false;

        public CheckForGPSAccuracyTimer(Context context, long delay, long period, long startTime, long lifeTime) {
            Log.i("CheckForGPSAccuracyTimer before start myLocation = ", "=" + myLocation);
            isRunning = false;
            if (myLocation != null) {
                if (myLocation.hasAccuracy()) {
                    float accuracy = myLocation.getAccuracy();
                    if (accuracy > HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND) {
                        displayLessAccurateToast(context);
                    } else if (accuracy <= HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND
                        && accuracy > HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND) {
                        startTimer(context, delay, period, startTime, lifeTime);
                        HomeActivity.this.switchRequestRideUI();
                    } else if (accuracy <= HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND) {
                        HomeActivity.this.switchRequestRideUI();
                        HomeActivity.this.startTimerRequestRide();
                    } else {
                        displayLessAccurateToast(context);
                    }
                } else {
                    displayLessAccurateToast(context);
                }
            } else {
                displayLessAccurateToast(context);
            }
        }


        public void displayLessAccurateToast(Context context) {
            Toast.makeText(context, "Please wait for sometime. We need to get your more accurate location.", Toast.LENGTH_LONG).show();
        }

        public void initRequestRideUi() {
            stopTimer();
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.this.startTimerRequestRide();
                }
            });
        }

        public void stopRequest(Context context) {
            displayLessAccurateToast(context);
            stopTimer();
            HomeActivity.this.customerUIBackToInitialAfterCancel();
        }

        public void startTimer(final Context context, long delay, long period, long startTime, long lifeTime) {
            stopTimer();
            isRunning = true;

            this.startTime = startTime;
            this.lifeTime = lifeTime;
            this.endTime = startTime + lifeTime;
            this.period = period;
            this.executionTime = -1;

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                long start = System.currentTimeMillis();
                                if (executionTime == -1) {
                                    executionTime = CheckForGPSAccuracyTimer.this.startTime;
                                }

                                if (executionTime >= CheckForGPSAccuracyTimer.this.endTime) {
                                    //Timer finished
                                    if (myLocation != null) {
                                        if (myLocation.hasAccuracy()) {
                                            float accuracy = myLocation.getAccuracy();
                                            if (accuracy > HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND) {
                                                stopRequest(context);
                                            } else {
                                                initRequestRideUi();
                                            }
                                        } else {
                                            stopRequest(context);
                                        }
                                    } else {
                                        stopRequest(context);
                                    }
                                } else {
                                    //Check for location accuracy
                                    Log.i("CheckForGPSAccuracyTimer myLocation = ", "=" + myLocation);
                                    if (myLocation != null) {
                                        if (myLocation.hasAccuracy()) {
                                            float accuracy = myLocation.getAccuracy();
                                            if (accuracy <= HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND) {
                                                initRequestRideUi();
                                            }
                                        }
                                    }
                                }
                                long stop = System.currentTimeMillis();
                                long elapsedTime = stop - start;
                                if (executionTime != -1) {
                                    if (elapsedTime >= CheckForGPSAccuracyTimer.this.period) {
                                        executionTime = executionTime + elapsedTime;
                                    } else {
                                        executionTime = executionTime + CheckForGPSAccuracyTimer.this.period;
                                    }
                                }
                                Log.i("WaitForGPSAccuracyTimerTask execution", "=" + (CheckForGPSAccuracyTimer.this.endTime - executionTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, delay, period);
            isRunning = true;
        }

        public void stopTimer() {
            try {
                isRunning = false;
                Log.e("WaitForGPSAccuracyTimerTask", "stopTimer");
                startTime = 0;
                lifeTime = 0;
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

	@Override
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUserData();
            }
        });
	}








	private void getPaytmBalance(final Activity activity) {
		try {
			long lastPaytmBalanceCall = Prefs.with(activity).getLong(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * PAYTM_CHECK_BALANCE_REFRESH_TIME)));
			long lastCallDiff = System.currentTimeMillis() - lastPaytmBalanceCall;
			Log.e("lastCallDiff", "=" + lastCallDiff);
			if(1 == Data.userData.paytmEnabled && lastCallDiff >= PAYTM_CHECK_BALANCE_REFRESH_TIME) {
				if (AppStatus.getInstance(this).isOnline(this)) {
					slidingBottomPanel.setPaytmLoadingVisiblity(View.VISIBLE);
					progressBarMenuPaytmWalletLoading.setVisibility(View.VISIBLE);
					textViewWalletValue.setVisibility(View.GONE);
					RequestParams params = new RequestParams();
					params.put("access_token", Data.userData.accessToken);
					params.put("client_id", Config.getClientId());
					params.put("is_access_token_new", "1");

					AsyncHttpClient client = Data.getClient();
					client.post(Config.getTXN_URL() + "/paytm/check_balance", params, new CustomAsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							Log.i("request succesfull", "response = " + response);
							try {
								JSONObject jObj = new JSONObject(response.toString());
								JSONParser.parsePaytmBalanceStatus(HomeActivity.this, jObj);
								setUserData();
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);
                            slidingBottomPanel.setPaytmLoadingVisiblity(View.GONE);
							textViewWalletValue.setVisibility(View.VISIBLE);
						}

						@Override
						public void onFailure(Throwable arg0) {
							try {
								Log.e("request fail", arg0.toString());
								JSONParser.setPaytmErrorCase();
								setUserData();
								progressBarMenuPaytmWalletLoading.setVisibility(View.GONE);
                                slidingBottomPanel.setPaytmLoadingVisiblity(View.GONE);
								textViewWalletValue.setVisibility(View.VISIBLE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}








	private void showPaytmTutorialPopup(final Activity activity) {
		try {
			if(Data.userData.paytmEnabled == 0 && Prefs.with(activity).getInt(SPLabels.PAYTM_TUTORIAL_SHOWN_COUNT, 0) < PAYTM_TUTORIAL_DIALOG_DISPLAY_COUNT) {
				imageViewMenu.performClick();
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_paytm_tutorial);

				LinearLayout linearLayoutPaytmTutorial = (LinearLayout) dialog.findViewById(R.id.linearLayoutPaytmTutorial);
				new ASSL(activity, (LinearLayout) dialog.findViewById(R.id.linearLayoutPaytmTutorial), 1134, 720, true);

				RelativeLayout relativeLayoutAdjustable = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutAdjustable);

				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);

				RelativeLayout relativeLayoutWalletTut = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutWalletTut);
				((TextView) dialog.findViewById(R.id.textViewWalletTut)).setTypeface(Fonts.latoRegular(activity));
				TextView textViewWalletValueTut = (TextView) dialog.findViewById(R.id.textViewWalletValueTut);
				textViewWalletValueTut.setTypeface(Fonts.latoRegular(activity));
				textViewWalletValueTut.setText(activity.getResources().getString(R.string.rupee) + " " + Utils.getMoneyDecimalFormat().format(Data.userData.getJugnooBalance()));

				relativeLayoutWalletTut.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
                        intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
                        startActivity(intent);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
						FlurryEventLogger.event(WALLET_VIA_TUTORIAL);
					}
				});

				linearLayoutPaytmTutorial.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();

				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) relativeLayoutAdjustable.getLayoutParams();
				params.height = relativeLayoutWallet.getTop();
				relativeLayoutAdjustable.setLayoutParams(params);

				Prefs.with(activity).save(SPLabels.PAYTM_TUTORIAL_SHOWN_COUNT, (Prefs.with(activity).getInt(SPLabels.PAYTM_TUTORIAL_SHOWN_COUNT, 0)+1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void uploadContactsApi(){
        RequestParams params = new RequestParams();
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(this, "Loading...");
            params.put("access_token", Data.userData.accessToken);
            //params.put("session_id", Data.cSessionId);
            params.put("engagement_id", Data.cEngagementId);
            params.put("user_response", -1);
            Log.i("access_token and session_id", Data.userData.accessToken + ", " + Data.cSessionId + ", " + Data.cEngagementId);

            Log.i("params request_dup_registration", "=" + params);


            //SyncHttpClient client1 = Data.getSyncClient();
            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/refer_all_contacts", params,
                    new CustomAsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(Throwable arg3) {
                            DialogPopup.dismissLoadingDialog();
                            Log.e("request fail", arg3.toString());
                            //Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i("Response of referral", "response = " + response);
                            DialogPopup.dismissLoadingDialog();
                            try {
                                JSONObject jObj = new JSONObject(response);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                Log.e("message=", "="+message);
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                    Data.userData.contactSaved = -1;
                                }
                                else{
                                    //Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                                }
                            }  catch (Exception exception) {
                                exception.printStackTrace();
                                //DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }


                        }
                    });
        }
        else {
            //Database2.getInstance(ContactsUploadService.this).insertPendingAPICall(ContactsUploadService.this, Config.getServerUrl()+"/refer_all_contacts", params);
            //DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }



    private void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {
        try {
            final RequestParams params = new RequestParams();
            params.put("access_token", Data.userData.accessToken);
            params.put("engagement_id", engagementId);

            final String url = Config.getServerUrl() + "/skip_rating_by_customer";

            try { Data.driverInfos.clear(); } catch (Exception e) { e.printStackTrace(); }

            AsyncHttpClient client = Data.getClient();
            client.post(url, params,
                new CustomAsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response", "response = " + response);

                    }
                });

            Database2.getInstance(activity).insertPendingAPICall(activity, url, params);

            HomeActivity.feedbackSkipped = true;
            HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, String ratingReceiverId,
                                            final int givenRating, String feedbackText, String feedbackReasons) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                params.put("access_token", Data.userData.accessToken);
                params.put("given_rating", "" + givenRating);
                params.put("engagement_id", engagementId);
                params.put("driver_id", ratingReceiverId);
                params.put("feedback", feedbackText);
                params.put("feedback_reasons", feedbackReasons);

                Log.i("params", "=" + params);

                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/rate_the_driver", params,
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
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                        Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
                                        if (HomeActivity.appInterruptHandler != null) {
                                            HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(givenRating, false);
                                        }
                                        try { Data.driverInfos.clear(); } catch (Exception e) { e.printStackTrace(); }
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                            DialogPopup.dismissLoadingDialog();
                        }
                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTextChange(String text) {
        if((PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)
                && !"".equalsIgnoreCase(text)){
            dropLocationSearchText = text;
        }
    }

    @Override
    public void onSearchPre() {

    }

    @Override
    public void onSearchPost() {

    }

    @Override
    public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode){
            FlurryEventLogger.event(PICKUP_LOCATION_SET);
            textViewInitialSearch.setText(autoCompleteSearchResult.name);
            zoomedForSearch = true;
            lastSearchLatLng = null;
            passengerScreenMode = PassengerScreenMode.P_INITIAL;
            switchPassengerScreen(passengerScreenMode);
        }

        Log.e("onPlaceClick", "="+autoCompleteSearchResult);
    }

    @Override
    public void onPlaceSearchPre() {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode){
            progressBarInitialSearch.setVisibility(View.VISIBLE);
        }

        Log.e("onPlaceSearchPre", "=");
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult) {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
            progressBarInitialSearch.setVisibility(View.GONE);
            if (map != null && searchResult != null) {
                textViewInitialSearch.setText(searchResult.name);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, MAX_ZOOM), 500, null);
                lastSearchLatLng = searchResult.latLng;

                try {
                    Log.e("searchResult.getThirdPartyAttributions()", "=" + searchResult.getThirdPartyAttributions());
                    if (searchResult.getThirdPartyAttributions() == null) {
                        relativeLayoutGoogleAttr.setVisibility(View.GONE);
                    } else {
                        relativeLayoutGoogleAttr.setVisibility(View.VISIBLE);
                        textViewGoogleAttrText.setText(Html.fromHtml(searchResult.getThirdPartyAttributions().toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
            sendDropLocationAPI(HomeActivity.this, searchResult.latLng,
                    getPlaceSearchListFragment(passengerScreenMode).getProgressBarSearch());
            FlurryEventLogger.event(DROP_LOCATION_USED_FINIDING_DRIVER);
        }
        else if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
            sendDropLocationAPI(HomeActivity.this, searchResult.latLng,
                    getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL).getProgressBarSearch());
            FlurryEventLogger.event(DROP_LOCATION_USED_RIDE_ACCEPTED);
        }

        Log.e("onPlaceSearchPost", "=" + searchResult);
    }

    @Override
    public void onPlaceSearchError() {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode){
            progressBarInitialSearch.setVisibility(View.GONE);
        }

        Log.e("onPlaceSearchError", "=");
    }

    @Override
    public void onPlaceSaved() {
        placeAdded = true;
    }

}