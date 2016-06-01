package product.clicklabs.jugnoo.home;

import android.animation.LayoutTransition;
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.google.ads.conversiontracking.AdWordsAutomatedUsageReporter;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.branch.referral.Branch;
import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.GCMIntentService;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideCancellationActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.FeedbackReasonsAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiCampaignAvailRequest;
import product.clicklabs.jugnoo.apis.ApiCampaignRequestCancel;
import product.clicklabs.jugnoo.apis.ApiFareEstimate;
import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.apis.ApiPaytmCheckBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.EmergencyDialog;
import product.clicklabs.jugnoo.emergency.EmergencyDisableDialog;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.fresh.FreshIntroDialog;
import product.clicklabs.jugnoo.home.dialogs.InAppCampaignDialog;
import product.clicklabs.jugnoo.home.dialogs.JugnooPoolTutorial;
import product.clicklabs.jugnoo.home.dialogs.PaytmRechargeDialog;
import product.clicklabs.jugnoo.home.dialogs.PoolFareDialog;
import product.clicklabs.jugnoo.home.dialogs.PriorityTipDialog;
import product.clicklabs.jugnoo.home.dialogs.PushDialog;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.t20.T20Dialog;
import product.clicklabs.jugnoo.t20.T20Ops;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.FrameAnimDrawable;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.NonScrollGridView;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class HomeActivity extends BaseFragmentActivity implements AppInterruptHandler, LocationUpdate, FlurryEventNames,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SearchListAdapter.SearchListActionsHandler, Constants {


    private final String TAG = "Home Screen";

    public DrawerLayout drawerLayout;                                                                        // views declaration


    MenuBar menuBar;

    TopBar topBar;


    //Passenger main layout
    RelativeLayout passengerMainLayout;


    //Map layout
    RelativeLayout mapLayout;
    public GoogleMap map;
    TouchableMapFragment mapFragment;
    public MapStateListener mapStateListener;
    boolean mapTouched = false;


    //Initial layout
    RelativeLayout initialLayout;

	ImageView imageViewRideNow, imageViewInAppCampaign, imageViewSupplyType;
	RelativeLayout relativeLayoutInitialSearchBar, relativeLayoutDestSearchBar;
	TextView textViewInitialSearch, textViewDestSearch;
    ImageView imageViewDropCross;
	ProgressWheel progressBarInitialSearch;
    Button initialMyLocationBtn, changeLocalityBtn, buttonChangeLocalityMyLocation;
    RelativeLayout relativeLayoutRequest;
    RelativeLayout relativeLayoutInAppCampaignRequest;
    TextView textViewInAppCampaignRequest;
    Button buttonCancelInAppCampaignRequest;

	RelativeLayout relativeLayoutGoogleAttr;
	ImageView imageViewGoogleAttrCross;
	TextView textViewGoogleAttrText;


	//Location Error layout
	RelativeLayout relativeLayoutLocationError;
	RelativeLayout relativeLayoutLocationErrorSearchBar;




    //Assigining layout
    RelativeLayout assigningLayout;
    TextView textViewFindingDriver;
    Button initialCancelRideBtn;
    RelativeLayout relativeLayoutAssigningDropLocationParent;
    RelativeLayout relativeLayoutAssigningDropLocationClick;
    TextView textViewAssigningDropLocationClick;
    ProgressWheel progressBarAssigningDropLocation;
    ImageView imageViewAssigningDropLocationEdit;
	boolean cancelTouchHold = false, placeAdded;


    //Request Final Layout
    LinearLayout linearLayoutSurgeContainer;
    RelativeLayout requestFinalLayout, relativeLayoutInRideInfo;
    TextView textViewInRidePromoName, textViewInRideFareFactor;
    RelativeLayout relativeLayoutFinalDropLocationClick;
    TextView textViewFinalDropLocationClick;
    ImageView imageViewFinalDropLocationEdit;
    ProgressWheel progressBarFinalDropLocation;
    Button customerInRideMyLocationBtn;
	LinearLayout linearLayoutInRideDriverInfo;
    ImageView imageViewInRideDriver;
    TextView textViewInRideDriverName, textViewInRideDriverCarNumber, textViewInRideState, textViewDriverRating;
    RelativeLayout relativeLayoutDriverRating;
    Button buttonCancelRide, buttonAddPaytmCash, buttonCallDriver;
    RelativeLayout relativeLayoutFinalDropLocationParent;
	LinearLayout relativeLayoutIRPaymentOption;
	TextView textViewIRPaymentOption, textViewIRPaymentOptionValue;
	ImageView imageViewIRPaymentOptionPaytm, imageViewIRPaymentOptionCash;



    //Search Layout
    RelativeLayout relativeLayoutSearchContainer, relativeLayoutSearch;



    //Center Location Layout
    RelativeLayout centreLocationRl, relativeLayoutPinEtaRotate;
    ImageView centreLocationPin, imageViewCenterPinMargin;
	TextView textViewCentrePinETA;



    //End Ride layout
    RelativeLayout endRideReviewRl;
    ScrollView scrollViewRideSummary;
    LinearLayout linearLayoutRideSummaryContainer, linearLayoutRideSummary;
    TextView textViewRSTotalFareValue, textViewRSCashPaidValue;
    LinearLayout linearLayoutRSViewInvoice, linearLayoutSendInvites;

    RatingBar ratingBarRSFeedback;
    TextView textViewRSWhatImprove, textViewRSOtherError;
    NonScrollGridView gridViewRSFeedbackReasons;
    FeedbackReasonsAdapter feedbackReasonsAdapter;
    EditText editTextRSFeedback;
    Button buttonRSSubmitFeedback, buttonRSSkipFeedback;
    TextView textViewRSScroll, textViewChangeLocality;
    private TextView textViewSendInvites, textViewSendInvites2, textViewThumbsDown, textViewThumbsUp;

    private RelativeLayout changeLocalityLayout;
    private AnimationDrawable jugnooAnimation;
    private ImageView findDriverJugnooAnimation, imageViewThumbsDown, imageViewThumbsUp, imageViewJugnooPool;


    // data variables declaration

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    DecimalFormat decimalFormat1DigitAD = new DecimalFormat("#.#");

    LatLng lastSearchLatLng;



    static long previousWaitTime = 0, previousRideTime = 0;
    private final int SEARCH_FLIP_ANIMATION_TIME = 200;
    private final float SEARCH_FLIP_ANIMATION_MARGIN = 20f;

    public static Location myLocation;


    public static UserMode userMode;
    public static PassengerScreenMode passengerScreenMode;


    Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker, dropLocationMarker;
    Polyline pathToDropLocationPolyline;
    PolylineOptions pathToDropLocationPolylineOptions;

    public static AppInterruptHandler appInterruptHandler;

    boolean loggedOut = false,
        zoomedToMyLocation = false,
        mapTouchedOnce = false;
    boolean dontCallRefreshDriver = false, zoomedForSearch = false, pickupDropZoomed = false, firstTimeZoom = false, zoomingForDeepLink = false;
    boolean dropLocationSet = false;

    Dialog noDriversDialog, dialogUploadContacts, dialogPaytmRecharge, freshIntroDialog;
    PushDialog pushDialog;

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
    public static final double MIN_DISTANCE_FOR_REFRESH = 50; // in meters

    public static final float MAX_ZOOM = 15;
    private static final int MAP_ANIMATE_DURATION = 300;

	public static final double FIX_ZOOM_DIAGONAL = 408;

	public static final long PAYTM_CHECK_BALANCE_REFRESH_TIME = 5 * 60 * 1000;


    private final String GOOGLE_ADWORD_CONVERSION_ID = "947755540";



    public CheckForGPSAccuracyTimer checkForGPSAccuracyTimer;


    public boolean activityResumed = false;
    public static boolean rechargedOnce = false, feedbackSkipped = false;

    public ASSL assl;


    private int showAllDrivers = 0, showDriverInfo = 0, rating = 0, isPoolRequest = 0, jugnooPoolFareId = 0;

    private boolean intentFired = false, dropLocationSearched = false;

	private GoogleApiClient mGoogleApiClient;


    CallbackManager callbackManager;
    public final int ADD_HOME = 2, ADD_WORK = 3;
    private String dropLocationSearchText = "";
    private SlidingBottomPanel slidingBottomPanel;

    private T20Ops t20Ops = new T20Ops();
    private PlaceSearchListFragment.PlaceSearchMode placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.PICKUP;
    private Polyline poolPolyline;
    private Marker poolMarkerStart, poolMarkerEnd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().trackScreenView(TAG);

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

        activityResumed = false;
        rechargedOnce = false;
        dropLocationSearched = false;

        loggedOut = false;
        zoomedToMyLocation = false;
        dontCallRefreshDriver = false;
        mapTouchedOnce = false;
        pickupDropZoomed = false;
		zoomedForSearch = false;
		firstTimeZoom = false;
		zoomingForDeepLink = false;
        freshIntroDialog = null;
        dropLocationSet = false;




        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);


        //Swipe menu
        menuBar = new MenuBar(this, drawerLayout);

        slidingBottomPanel = new SlidingBottomPanel(HomeActivity.this, drawerLayout);



        //Top RL
        topBar = new TopBar(this, drawerLayout);


        //Map Layout
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapTouched = false;


        //Passenger main layout
        passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);


        //Initial layout
        initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
        changeLocalityLayout = (RelativeLayout)findViewById(R.id.changeLocalityLayout);
        textViewChangeLocality = (TextView)findViewById(R.id.textViewChangeLocality);textViewChangeLocality.setTypeface(Fonts.mavenLight(this));
        buttonChangeLocalityMyLocation = (Button) findViewById(R.id.buttonChangeLocalityMyLocation);

        initialMyLocationBtn = (Button) findViewById(R.id.initialMyLocationBtn);
        changeLocalityBtn = (Button) findViewById(R.id.changeLocalityBtn);
        changeLocalityBtn.setTypeface(Fonts.mavenRegular(this));

        initialMyLocationBtn.setVisibility(View.VISIBLE);
        changeLocalityLayout.setVisibility(View.GONE);

        imageViewRideNow = (ImageView) findViewById(R.id.imageViewRideNow);
        imageViewSupplyType = (ImageView) findViewById(R.id.imageViewSupplyType);

        imageViewInAppCampaign = (ImageView) findViewById(R.id.imageViewInAppCampaign);
        imageViewInAppCampaign.setVisibility(View.GONE);
        imageViewJugnooPool = (ImageView) findViewById(R.id.imageViewJugnooPool);
        imageViewJugnooPool.setVisibility(View.GONE);
        relativeLayoutRequest = (RelativeLayout) findViewById(R.id.relativeLayoutRequest);
        relativeLayoutRequest.setVisibility(View.VISIBLE);
        relativeLayoutInAppCampaignRequest = (RelativeLayout) findViewById(R.id.relativeLayoutInAppCampaignRequest);
        relativeLayoutInAppCampaignRequest.setVisibility(View.GONE);
        textViewInAppCampaignRequest = (TextView) findViewById(R.id.textViewInAppCampaignRequest);
        textViewInAppCampaignRequest.setTypeface(Fonts.mavenLight(this));
        buttonCancelInAppCampaignRequest = (Button) findViewById(R.id.buttonCancelInAppCampaignRequest);
        buttonCancelInAppCampaignRequest.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutSearchContainer = (RelativeLayout) findViewById(R.id.relativeLayoutSearchContainer);
        relativeLayoutInitialSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutInitialSearchBar);
        relativeLayoutDestSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutDestSearchBar);
        textViewInitialSearch = (TextView) findViewById(R.id.textViewInitialSearch); textViewInitialSearch.setTypeface(Fonts.mavenRegular(this));
        textViewDestSearch = (TextView) findViewById(R.id.textViewDestSearch); textViewDestSearch.setTypeface(Fonts.mavenRegular(this));
        progressBarInitialSearch = (ProgressWheel) findViewById(R.id.progressBarInitialSearch);
        progressBarInitialSearch.setVisibility(View.GONE);
        imageViewDropCross = (ImageView) findViewById(R.id.imageViewDropCross);
        imageViewDropCross.setVisibility(View.GONE);

		relativeLayoutGoogleAttr = (RelativeLayout) findViewById(R.id.relativeLayoutGoogleAttr);
		imageViewGoogleAttrCross = (ImageView) findViewById(R.id.imageViewGoogleAttrCross);
		textViewGoogleAttrText = (TextView) findViewById(R.id.textViewGoogleAttrText);
        textViewGoogleAttrText.setTypeface(Fonts.mavenMedium(this));
		relativeLayoutGoogleAttr.setVisibility(View.GONE);





		//Location error layout
		relativeLayoutLocationError = (RelativeLayout) findViewById(R.id.relativeLayoutLocationError);
		relativeLayoutLocationErrorSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutLocationErrorSearchBar);
		((TextView)findViewById(R.id.textViewLocationErrorSearch)).setTypeface(Fonts.mavenMedium(this));
		relativeLayoutLocationError.setVisibility(View.GONE);






        //Assigning layout
        assigningLayout = (RelativeLayout) findViewById(R.id.assigningLayout);
        textViewFindingDriver = (TextView) findViewById(R.id.textViewFindingDriver);
        textViewFindingDriver.setTypeface(Fonts.mavenLight(this));
        initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn);
        initialCancelRideBtn.setTypeface(Fonts.mavenRegular(this));
        findDriverJugnooAnimation = (ImageView) findViewById(R.id.findDriverJugnooAnimation);
        jugnooAnimation = (AnimationDrawable) findDriverJugnooAnimation.getBackground();


        relativeLayoutAssigningDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationParent);
        relativeLayoutAssigningDropLocationClick = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationClick);
        textViewAssigningDropLocationClick = (TextView)findViewById(R.id.textViewAssigningDropLocationClick);
        textViewAssigningDropLocationClick.setTypeface(Fonts.mavenMedium(this));
        progressBarAssigningDropLocation = (ProgressWheel)findViewById(R.id.progressBarAssigningDropLocation);
        imageViewAssigningDropLocationEdit = (ImageView)findViewById(R.id.imageViewAssigningDropLocationEdit);
        imageViewAssigningDropLocationEdit.setVisibility(View.GONE);
        progressBarAssigningDropLocation.setVisibility(View.GONE);



        //Request Final Layout
        requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);

        relativeLayoutInRideInfo = (RelativeLayout) findViewById(R.id.relativeLayoutInRideInfo);
        textViewInRidePromoName = (TextView) findViewById(R.id.textViewInRidePromoName);
        textViewInRidePromoName.setTypeface(Fonts.mavenRegular(this));
        linearLayoutSurgeContainer = (LinearLayout) findViewById(R.id.linearLayoutSurgeContainer);
        textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor);
        textViewInRideFareFactor.setTypeface(Fonts.mavenMedium(this));
        relativeLayoutFinalDropLocationClick = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationClick);
        textViewFinalDropLocationClick = (TextView)findViewById(R.id.textViewFinalDropLocationClick);
        textViewFinalDropLocationClick.setTypeface(Fonts.mavenMedium(this));
        imageViewFinalDropLocationEdit = (ImageView) findViewById(R.id.imageViewFinalDropLocationEdit);
        imageViewFinalDropLocationEdit.setVisibility(View.GONE);
        progressBarFinalDropLocation = (ProgressWheel) findViewById(R.id.progressBarFinalDropLocation);
        progressBarFinalDropLocation.setVisibility(View.GONE);
        customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);
		linearLayoutInRideDriverInfo = (LinearLayout) findViewById(R.id.linearLayoutInRideDriverInfo);
        imageViewInRideDriver = (ImageView) findViewById(R.id.imageViewInRideDriver);
        textViewInRideDriverName = (TextView) findViewById(R.id.textViewInRideDriverName);
        textViewInRideDriverName.setTypeface(Fonts.mavenRegular(this));
        textViewInRideDriverCarNumber = (TextView) findViewById(R.id.textViewInRideDriverCarNumber);
        textViewInRideDriverCarNumber.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        textViewInRideState = (TextView) findViewById(R.id.textViewInRideState);
        textViewInRideState.setTypeface(Fonts.mavenMedium(this));
        textViewDriverRating = (TextView) findViewById(R.id.textViewDriverRating);
        textViewDriverRating.setTypeface(Fonts.mavenLight(this));
        relativeLayoutDriverRating = (RelativeLayout) findViewById(R.id.relativeLayoutDriverRating);

        buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
        buttonCancelRide.setTypeface(Fonts.mavenRegular(this));
		buttonAddPaytmCash = (Button) findViewById(R.id.buttonAddPaytmCash);
		buttonAddPaytmCash.setTypeface(Fonts.mavenRegular(this));
        buttonCallDriver = (Button) findViewById(R.id.buttonCallDriver);
        buttonCallDriver.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutFinalDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationParent);


		relativeLayoutIRPaymentOption = (LinearLayout) findViewById(R.id.relativeLayoutIRPaymentOption);
		textViewIRPaymentOption = (TextView) findViewById(R.id.textViewIRPaymentOption); textViewIRPaymentOption.setTypeface(Fonts.mavenRegular(this));
		textViewIRPaymentOptionValue = (TextView) findViewById(R.id.textViewIRPaymentOptionValue); textViewIRPaymentOptionValue.setTypeface(Fonts.mavenMedium(this));
		imageViewIRPaymentOptionPaytm = (ImageView) findViewById(R.id.imageViewIRPaymentOptionPaytm);
		imageViewIRPaymentOptionCash = (ImageView) findViewById(R.id.imageViewIRPaymentOptionCash);

        linearLayoutSendInvites = (LinearLayout) findViewById(R.id.linearLayoutSendInvites);
        textViewSendInvites = (TextView) findViewById(R.id.textViewSendInvites); textViewSendInvites.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        textViewSendInvites2 = (TextView) findViewById(R.id.textViewSendInvites2); textViewSendInvites2.setTypeface(Fonts.mavenRegular(this));


        //Search Layout
		relativeLayoutSearch = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);




        //Center location layout
        centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
        relativeLayoutPinEtaRotate = (RelativeLayout) findViewById(R.id.relativeLayoutPinEtaRotate);
        centreLocationPin = (ImageView) findViewById(R.id.centreLocationPin);
		imageViewCenterPinMargin = (ImageView) findViewById(R.id.imageViewCenterPinMargin);
		textViewCentrePinETA = (TextView) findViewById(R.id.textViewCentrePinETA);
        textViewCentrePinETA.setTypeface(Fonts.mavenMedium(this));
		((TextView) findViewById(R.id.textViewCentrePinETAMin)).setTypeface(Fonts.mavenMedium(this));

        //Review Layout
        endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);

        scrollViewRideSummary = (ScrollView) findViewById(R.id.scrollViewRideSummary);
        linearLayoutRideSummaryContainer = (LinearLayout) findViewById(R.id.linearLayoutRideSummaryContainer);
        linearLayoutRideSummary = (LinearLayout) findViewById(R.id.linearLayoutRideSummary);
        textViewRSTotalFareValue = (TextView) findViewById(R.id.textViewRSTotalFareValue); textViewRSTotalFareValue.setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSTotalFare)).setTypeface(Fonts.mavenRegular(this));
        textViewRSCashPaidValue = (TextView) findViewById(R.id.textViewRSCashPaidValue); textViewRSCashPaidValue.setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSCashPaid)).setTypeface(Fonts.mavenRegular(this));
        linearLayoutRSViewInvoice = (LinearLayout) findViewById(R.id.linearLayoutRSViewInvoice);
        ((TextView)findViewById(R.id.textViewRSInvoice)).setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewRSRateYourRide)).setTypeface(Fonts.mavenRegular(this));
        imageViewThumbsDown = (ImageView) findViewById(R.id.imageViewThumbsDown);
        imageViewThumbsUp = (ImageView) findViewById(R.id.imageViewThumbsUp);
        textViewThumbsDown = (TextView) findViewById(R.id.textViewThumbsDown); textViewThumbsDown.setTypeface(Fonts.mavenRegular(this));
        textViewThumbsUp = (TextView) findViewById(R.id.textViewThumbsUp); textViewThumbsUp.setTypeface(Fonts.mavenRegular(this));


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


        changeLocalityLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // Customer initial layout events
        imageViewRideNow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isPoolRequest = 0;
                requestRideClick();
                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			}
        });

        imageViewSupplyType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSupplyTypeImage();
            }
        });
        imageViewSupplyType.setVisibility(View.GONE);





        imageViewInAppCampaign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callCampaignAvailRequest();
                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        buttonCancelInAppCampaignRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiCampaignRequestCancel();
            }
        });

        relativeLayoutInAppCampaignRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        relativeLayoutInitialSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = ((ViewGroup) relativeLayoutInitialSearchBar.getParent());
                int index = viewGroup.indexOfChild(relativeLayoutDestSearchBar);
                if(index == 1 && Data.dropLatLng == null) {
                    translateViewTop(viewGroup, relativeLayoutInitialSearchBar, true, true);
                    translateViewBottom(viewGroup, relativeLayoutDestSearchBar, false, true);
                }else{
                    placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.PICKUP;
                    setServiceAvailablityUI("");
                    passengerScreenMode = PassengerScreenMode.P_SEARCH;
                    switchPassengerScreen(passengerScreenMode);
                }

            }
        });

        relativeLayoutDestSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
                int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
                if(index == 1 && Data.dropLatLng == null) {
                    translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                    translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
                }else{
                    placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.DROP;
                    setServiceAvailablityUI("");
                    passengerScreenMode = PassengerScreenMode.P_SEARCH;
                    switchPassengerScreen(passengerScreenMode);
                }
            }
        });

        imageViewDropCross.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.dropLatLng = null;
                dropLocationSet = false;
                dropLocationSearched = false;
                textViewDestSearch.setText("");
                imageViewDropCross.setVisibility(View.GONE);
                translateViewBottomTop(relativeLayoutDestSearchBar, false);
                translateViewTopBottom(relativeLayoutInitialSearchBar, true);
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
                locationGotNow();
                setServiceAvailablityUI(Data.farAwayCity);
                callMapTouchedRefreshDrivers();
//				genieLayout.setVisibility(View.VISIBLE);
			}
		});

		relativeLayoutLocationErrorSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutInitialSearchBar.performClick();
                locationGotNow();
//				genieLayout.setVisibility(View.VISIBLE);
            }
        });

        imageViewJugnooPool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if(Data.dropLatLng != null){
                isPoolRequest = 1;
                requestRideClick();
            } else{
                textViewDestSearch.setText(getResources().getString(R.string.enter_destination));
                textViewDestSearch.setTextColor(getResources().getColor(R.color.red));

                ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
                int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
                if(index == 1 && Data.dropLatLng == null) {
                    translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                    translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
                }
                if(Data.dropLatLng == null){
                    Animation shake = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.shake);
                    textViewDestSearch.startAnimation(shake);
                }
            }
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
                        FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "request ride", "cancel ride");
                        cancelCustomerRequestAsync(HomeActivity.this);
                    }
            }
        });


        relativeLayoutAssigningDropLocationClick.setOnClickListener(new OnClickListener() {
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

        linearLayoutSendInvites.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToShareActivity(false);
                FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "ride start", "send invites");
            }
        });


        // customer request final layout events
        buttonCancelRide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(RIDE_CANCELLED_NOT_COMPLETE);
                FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "accept ride", "cancel ride");
            }
        });

        buttonAddPaytmCash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
                if (Data.userData.paytmEnabled == 1 && !Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)) {
                    intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
                }
                else{
                    intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
                }
                startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
					FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_DRIVER_ARRIVED);
				} else if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
					FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_RIDE_IN_PROGRESS);
                    FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "Ride Start", "add paytm wallet");
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
                        FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "Ride Start", "Call Driver");
					}
					else if(PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode){
						FlurryEventLogger.event(CALL_TO_DRIVER_MADE_WHEN_ARRIVED);
					}
                    try {
                        JSONObject map = new JSONObject();
                        map.put(KEY_ENGAGEMENT_ID, Data.cEngagementId);
                        NudgeClient.trackEventUserId(HomeActivity.this, FlurryEventNames.NUDGE_CALL_DRIVER, map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
					e.printStackTrace();
				}
			}
   	     });


        relativeLayoutFinalDropLocationClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(true);
            }
        });

        linearLayoutInRideDriverInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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

        editTextRSFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollViewRideSummary.smoothScrollTo(0, buttonRSSubmitFeedback.getBottom());
            }
        });
        editTextRSFeedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    scrollViewRideSummary.smoothScrollTo(0, buttonRSSubmitFeedback.getBottom());
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
                    //int rating = (int) ratingBarRSFeedback.getRating();
                    //rating = Math.abs(rating);
                    Log.e("rating screen =", "= feedbackStr = " + feedbackStr + " , rating = " + rating);

                    String feedbackReasons = feedbackReasonsAdapter.getSelectedReasons();
                    boolean isLastReasonSelected = feedbackReasonsAdapter.isLastSelected();

                    if (0 == rating) {
                        DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.we_take_your_feedback_seriously));
                        FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
                    } else {
                        if (Data.feedbackReasons.size() > 0 && rating <= 3) {
                            if (feedbackReasons.length() > 0) {
                                if (isLastReasonSelected && feedbackStr.length() == 0) {
                                    textViewRSOtherError.setText(getString(R.string.star_required));
                                    return;
                                }
                            } else {
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
                            FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "ride completed", "submit feedback");
                            FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "ride completed", "rating " + rating);
                            if (feedbackStr.length() > 0) {
                                FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
                                FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "ride completed", "leave comments " + feedbackStr);
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
                FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "ride completed", "skip");
            }
        });

        linearLayoutRSViewInvoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.endRideData != null) {
                    linearLayoutRideSummaryContainerSetVisiblity(View.VISIBLE);
                    FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "ride completed", "view invoice");
                }
            }
        });


        imageViewThumbsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 5;
                imageViewThumbsDown.clearAnimation();
                //imageViewThumbsUp.clearAnimation();
                textViewThumbsUp.setVisibility(View.VISIBLE);
                textViewThumbsDown.setVisibility(View.INVISIBLE);
                imageViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_up));
                textViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.fade_in));

                setZeroRatingView();
            }
        });

        imageViewThumbsDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                imageViewThumbsUp.clearAnimation();
                //imageViewThumbsDown.clearAnimation();
                textViewThumbsDown.setVisibility(View.VISIBLE);
                textViewThumbsUp.setVisibility(View.INVISIBLE);
                imageViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_down));
                textViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.fade_in));

                textViewRSWhatImprove.setVisibility(View.VISIBLE);
                gridViewRSFeedbackReasons.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
                layoutParams.height = (int) (ASSL.Yscale() * 150);
                editTextRSFeedback.setLayoutParams(layoutParams);
            }
        });

        // map object initialized
        if (map != null) {

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
                forceFarAwayCity();
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

                        return true;
                    } else if (arg0.getTitle().equalsIgnoreCase("customer_current_location")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
                        map.setInfoWindowAdapter(customIW);

                        return true;
                    } else if (arg0.getTitle().equalsIgnoreCase("start ride location")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
                        map.setInfoWindowAdapter(customIW);

                        return true;
                    } else if (arg0.getTitle().equalsIgnoreCase("driver position")) {

                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Driver Location", "");
                        map.setInfoWindowAdapter(customIW);

                        return true;
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


            mapStateListener = new MapStateListener(map, mapFragment, this) {
                @Override
                public void onMapTouched() {
                    // Map touched
                    mapTouched = true;
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
                    checkForMyLocationButtonVisibility();
                    boolean refresh = false;
                    if(Data.lastRefreshLatLng == null){
                        Data.lastRefreshLatLng = map.getCameraPosition().target;
                        refresh = true;
                    }
                    else{
                        Log.v("Min Difference is = ","---> "+MapUtils.distance(Data.lastRefreshLatLng, map.getCameraPosition().target));
                        if(MapUtils.distance(Data.lastRefreshLatLng, map.getCameraPosition().target) > MIN_DISTANCE_FOR_REFRESH){
							Data.lastRefreshLatLng = map.getCameraPosition().target;
                            refresh = true;
                        }
                    }
                    if(refresh && mapTouched) {
                        callMapTouchedRefreshDrivers();
                    }
                    if (!zoomedForSearch && map != null) {
                        getAddress(map.getCameraPosition().target, textViewInitialSearch, progressBarInitialSearch);
                    }
                }
            };


            initialMyLocationBtn.setOnClickListener(mapMyLocationClick);
            buttonChangeLocalityMyLocation.setOnClickListener(mapMyLocationClick);
            customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);

        }


        new JugnooPoolTutorial(HomeActivity.this, new JugnooPoolTutorial.Callback() {
            @Override
            public void onContinueClicked() {

            }

            @Override
            public void onMayBeLaterClicked() {

            }

            @Override
            public void onDialogDismiss() {

            }

            @Override
            public void notShown() {

            }
        }).show();


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


            switchUserScreen();

            startUIAfterGettingUserStatus();

            if(Data.userData.getGetGogu() == 1) {
                new FetchAndSendMessages(this, Data.userData.accessToken, false, "", "").execute();
            }

            openPushDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
            FlurryAgent.setUserId(Data.userData.getUserId());
		} catch(Exception e){
			e.printStackTrace();
		}

        try {
            JSONObject map = new JSONObject();
            map.put(KEY_LATITUDE, Data.latitude);
            map.put(KEY_LONGITUDE, Data.longitude);
            NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_APP_OPEN, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

		Prefs.with(HomeActivity.this).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * PAYTM_CHECK_BALANCE_REFRESH_TIME)));

        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "");
        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, "");

    }

    private void checkForMyLocationButtonVisibility(){
        try{
            if(MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                    map.getCameraPosition().target) > MAP_PAN_DISTANCE_CHECK
                    || map.getCameraPosition().zoom != MAX_ZOOM){
                initialMyLocationBtn.setVisibility(View.VISIBLE);
                customerInRideMyLocationBtn.setVisibility(View.VISIBLE);
            } else{
                initialMyLocationBtn.setVisibility(View.GONE);
                customerInRideMyLocationBtn.setVisibility(View.GONE);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void translateViewBottom(final ViewGroup viewGroup, final View mView, final boolean viewExchange,
                                     final boolean callNextAnim) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int)(ASSL.Yscale()*SEARCH_FLIP_ANIMATION_MARGIN));
        animation.setDuration(SEARCH_FLIP_ANIMATION_TIME);
        animation.setFillAfter(false);
        mView.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(viewExchange) {
                    viewGroup.bringChildToFront(viewGroup.getChildAt(0));
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mView.getLayoutParams();
                params.topMargin = params.topMargin + (int)(ASSL.Yscale()*SEARCH_FLIP_ANIMATION_MARGIN);
                mView.setLayoutParams(params);
                if(callNextAnim) {
                    translateViewBottomTop(mView, viewExchange);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);

    }

    private void translateViewBottomTop(final View mView, final boolean viewExchange){
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int)(ASSL.Yscale() * (-SEARCH_FLIP_ANIMATION_MARGIN)));
        animation.setDuration(SEARCH_FLIP_ANIMATION_TIME);
        animation.setFillAfter(false);
        mView.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (viewExchange) {
                    mView.setBackgroundResource(R.drawable.dropshadow_in_white);
                } else {
                    mView.setBackgroundResource(R.drawable.dropshadow_in_menu_item_selector_color);
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                params.topMargin = params.topMargin - (int) (ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN);
                mView.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);
    }


    private void translateViewTop(final ViewGroup viewGroup, final View mView, final boolean viewExchange,
                                  final boolean callNextAnim) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int)(ASSL.Yscale() * (-SEARCH_FLIP_ANIMATION_MARGIN)));
        animation.setDuration(SEARCH_FLIP_ANIMATION_TIME);
        animation.setFillAfter(false);
        mView.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (viewExchange) {
                    viewGroup.bringChildToFront(viewGroup.getChildAt(0));
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                params.topMargin = params.topMargin - (int) (ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN);
                mView.setLayoutParams(params);
                if(callNextAnim) {
                    translateViewTopBottom(mView, viewExchange);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);
    }

    private void translateViewTopBottom(final View mView, final boolean viewExchange){
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int)(ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN));
        animation.setDuration(SEARCH_FLIP_ANIMATION_TIME);
        animation.setFillAfter(false);
        mView.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (viewExchange) {
                    mView.setBackgroundResource(R.drawable.dropshadow_in_white);
                } else {
                    mView.setBackgroundResource(R.drawable.dropshadow_in_menu_item_selector_color);
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                params.topMargin = params.topMargin + (int) (ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN);
                mView.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);
    }

    private void resetPickupDropFeilds(){
        try{
            Data.dropLatLng = null;
            dropLocationSet = false;
            dropLocationSearched = false;

            textViewDestSearch.setText("");
            imageViewDropCross.setVisibility(View.GONE);

            relativeLayoutDestSearchBar.setBackgroundResource(R.drawable.dropshadow_in_menu_item_selector_color);
            relativeLayoutDestSearchBar.clearAnimation();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayoutDestSearchBar.getLayoutParams();
            params.topMargin = (int) (ASSL.Yscale() * 80f);
            relativeLayoutDestSearchBar.setLayoutParams(params);

            relativeLayoutInitialSearchBar.setBackgroundResource(R.drawable.dropshadow_in_white);
            relativeLayoutInitialSearchBar.clearAnimation();
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) relativeLayoutInitialSearchBar.getLayoutParams();
            params1.topMargin = (int) (ASSL.Yscale() * 20f);
            relativeLayoutInitialSearchBar.setLayoutParams(params1);

            ((ViewGroup) relativeLayoutInitialSearchBar.getParent()).bringChildToFront(relativeLayoutInitialSearchBar);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onClickSearchCancel(){
        textViewInitialSearch.setText("");
        Utils.hideSoftKeyboard(HomeActivity.this, textViewInitialSearch);
        HomeActivity.this.passengerScreenMode = PassengerScreenMode.P_INITIAL;
        switchPassengerScreen(HomeActivity.this.passengerScreenMode);
        FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
    }


	private float googleMapPadding = 0;
	public void setGoogleMapPadding(float bottomPadding){
		try {
			if(map != null){
				map.setPadding(0, 0, 0, (int)(ASSL.Yscale() * bottomPadding));
				googleMapPadding = bottomPadding;
                setCentrePinAccToGoogleMapPadding();
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
                    Data.pickupLatLng = map.getCameraPosition().target;
                    if (!dontCallRefreshDriver && Data.pickupLatLng != null) {
                        findDriversETACall();
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



    public void initiateRequestRide(boolean newRequest) {
        if (newRequest) {
            new PriorityTipDialog(HomeActivity.this, Data.userData.fareFactor, Data.priorityTipCategory,
                    new PriorityTipDialog.Callback() {
                        @Override
                        public void onConfirmed(boolean confirmClicked) {
                            if(confirmClicked) {
                                FlurryEventLogger.eventGA(CAMPAIGNS, "priority tip pop up", "ok");
                            }
                            if(isPoolRequest == 1){
                                // Pool request
                                fareEstimateForPool();
                            } else{
                                finalRequestRideTimerStart();
                            }
                        }

                        @Override
                        public void onCancelled() {
                            Log.v("Request of Ride", "Aborted");
                            FlurryEventLogger.eventGA(CAMPAIGNS, "priority tip pop up", "cancel");
                            FlurryEventLogger.event(HomeActivity.this, SURGE_NOT_ACCEPTED);
                        }
                    }).showDialog();
        } else {
            Data.cEngagementId = "";
            switchRequestRideUI();
            startTimerRequestRide();
        }
    }

    private void finalRequestRideTimerStart(){
        try {
        Data.cSessionId = "";
        Data.cEngagementId = "";
        dropLocationSearchText = "";

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
        if (Data.TRANSFER_FROM_JEANIE == 1) {
            FlurryEventLogger.event(JUGNOO_STICKY_RIDE_CONFIRMATION);
            Data.TRANSFER_FROM_JEANIE = 0;
        }


            JSONObject map = new JSONObject();
            map.put(KEY_LATITUDE, Data.pickupLatLng.latitude);
            map.put(KEY_LONGITUDE, Data.pickupLatLng.longitude);
            NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_REQUEST_RIDE, map);
            FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, TAG, "request ride");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestRideClick(){
        try{
            onRequestRideTap();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void onRequestRideTap(){
        try {
            if(map != null) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    Data.pickupLatLng = map.getCameraPosition().target;
                    FlurryEventLogger.event(HomeActivity.this, AUTO_RIDE_ICON);
                    FlurryEventLogger.event(HomeActivity.this, CLICKS_ON_GET_A_RIDE);

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
                                    DialogPopup.alertPopupWithListener(HomeActivity.this, "",
                                            getResources().getString(R.string.paytm_no_cash),
                                            new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
                                                    if(Data.userData.paytmEnabled == 1) {
                                                        intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
                                                    } else {
                                                        intent.putExtra(KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
                                                    }
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                                }
                                            });
                                }
                            }
                            FlurryEventLogger.event(PAYTM_SELECTED_WHEN_REQUESTING);
                            FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, TAG, "paytm");
                        } else {
                            FlurryEventLogger.event(CASH_SELECTED_WHEN_REQUESTING);
                            FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, TAG, "cash");
                            callRequestRide = true;
                        }
                        if (callRequestRide) {
                            promoCouponSelectedForRide = slidingBottomPanel.getSelectedCoupon();
                            callAnAutoPopup(HomeActivity.this);

                            Prefs.with(HomeActivity.this).save(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED, 0);
                            Prefs.with(HomeActivity.this).save(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED, 0);

                            FlurryEventLogger.event(FINAL_RIDE_CALL_MADE);
                            if (promoCouponSelectedForRide.id > 0) {
                                FlurryEventLogger.event(COUPONS_SELECTED);
                                FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, TAG, "offer selected");
                            } else {
                                FlurryEventLogger.event(COUPON_NOT_SELECTED);
                            }
                        }

                        Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                    }

                } else {
                    DialogPopup.dialogNoInternet(HomeActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View v) {
                            imageViewRideNow.performClick();
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
        } catch (Exception e) {
            e.printStackTrace();
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
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), MAX_ZOOM), MAP_ANIMATE_DURATION, null);
                    mapTouched = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
                reconnectLocationFetchers();
            }
            FlurryEventLogger.event(NAVIGATION_TO_CURRENT_LOC);
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
            menuBar.setUserData();
            topBar.setUserData();

            updateInRideAddPaytmButtonText();
			setPaymentOptionInRide();

            slidingBottomPanel.updatePreferredPaymentOptionUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForFareAvailablity(){
        try{
            if(Data.fareStructure != null && !Data.fareStructure.getIsFromServer()){
                forceFarAwayCity();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void switchUserScreen() {

        passengerMainLayout.setVisibility(View.VISIBLE);

        Database2.getInstance(HomeActivity.this).close();

    }


    public void switchPassengerScreen(PassengerScreenMode mode) {
        try {
            if (userMode == UserMode.PASSENGER) {

                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }

                try {pickupLocationMarker.remove();} catch (Exception e) {}
                try {driverLocationMarker.remove();} catch (Exception e) {}

                if (mode == PassengerScreenMode.P_RIDE_END) {
                    if (Data.endRideData != null) {
//                        genieLayout.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.VISIBLE);
                        endRideReviewRl.setVisibility(View.VISIBLE);

                        linearLayoutRideSummary.setLayoutTransition(null);
                        scrollViewRideSummary.scrollTo(0, 0);
                        ratingBarRSFeedback.setRating(0f);
                        setZeroRatingView();
                        linearLayoutRideSummary.setLayoutTransition(new LayoutTransition());

                        editTextRSFeedback.setText("");
                        for(int i=0; i<Data.feedbackReasons.size(); i++){
                            Data.feedbackReasons.get(i).checked = false;
                        }
                        feedbackReasonsAdapter.notifyDataSetChanged();
                        textViewRSTotalFareValue.setText(String.format(getString(R.string.rupees_value_format_without_space),
                                "" + Utils.getMoneyDecimalFormat().format(Data.endRideData.finalFare)));
                        textViewRSCashPaidValue.setText(String.format(getString(R.string.rupees_value_format_without_space),
                                "" + Utils.getMoneyDecimalFormat().format(Data.endRideData.toPay)));

                        imageViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_up));
                        imageViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_down));
                        textViewThumbsUp.setVisibility(View.INVISIBLE);
                        textViewThumbsDown.setVisibility(View.INVISIBLE);

                        Data.endRideData.setDriverNameCarName(Data.assignedDriverInfo.name, Data.assignedDriverInfo.carNumber);

                        // delete the RidePath Table from Phone Database :)
                        Database2.getInstance(HomeActivity.this).deleteRidePathTable();
                        Log.d("RidePath DB", "Deleted");
                        List<Product> productList = new ArrayList<>();
                        Product product = new Product()
                                .setCategory("Auto")
                                .setId("0")
                                .setName("Paytm")
                                .setPrice(Data.endRideData.paidUsingPaytm);
//                                                                .setPosition(4);
                        productList.add(product);

                        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                                .setTransactionId(Data.endRideData.engagementId)
                                .setTransactionAffiliation("Auto")
                                .setTransactionRevenue(Data.endRideData.fare);

                        FlurryEventLogger.orderedProduct(productList, productAction);
                    } else {
                        passengerScreenMode = PassengerScreenMode.P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    }
                } else {
                    mapLayout.setVisibility(View.VISIBLE);
                    endRideReviewRl.setVisibility(View.GONE);
                }


                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                switch (mode) {

                    case P_INITIAL:

                        GCMIntentService.clearNotifications(HomeActivity.this);

                        if(!dropLocationSet) {
                            Data.dropLatLng = null;
                        }

                        Database2.getInstance(HomeActivity.this).deleteRidePathTable();


						try{ map.clear(); } catch(Exception e){ e.printStackTrace(); }



                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
						requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.VISIBLE);
                        relativeLayoutInitialSearchBar.setVisibility(View.VISIBLE);

                        imageViewRideNow.setVisibility(View.VISIBLE);
                        checkForMyLocationButtonVisibility();
                        changeLocalityLayout.setVisibility(View.GONE);

                        cancelTimerRequestRide();


                        Log.e("Data.latitude", "=" + Data.latitude);
                        Log.e("myLocation", "=" + myLocation);

                        if (Data.latitude != 0 && Data.longitude != 0) {
                            Data.pickupLatLng = new LatLng(Data.latitude, Data.longitude);
                        } else if (myLocation != null) {
                            Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        }


                        topBar.imageViewHelp.setVisibility(View.GONE);


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
							getAddress(map.getCameraPosition().target, textViewInitialSearch, progressBarInitialSearch);
						}

						if(Data.locationSettingsNoPressed){
							relativeLayoutLocationError.setVisibility(View.VISIBLE);
							initialMyLocationBtn.setVisibility(View.GONE);
							imageViewRideNow.setVisibility(View.GONE);
//							genieLayout.setVisibility(View.GONE);
							centreLocationRl.setVisibility(View.GONE);
                            changeLocalityLayout.setVisibility(View.GONE);

							Data.locationSettingsNoPressed = false;
						}
						else{
							relativeLayoutLocationError.setVisibility(View.GONE);
							checkForMyLocationButtonVisibility();
							imageViewRideNow.setVisibility(View.VISIBLE);
//							genieLayout.setVisibility(View.VISIBLE);
							centreLocationRl.setVisibility(View.VISIBLE);
						}

                        checkForFareAvailablity();

                        findADriverFinishing();
                        setGoogleMapPadding(0);

                        break;


                    case P_SEARCH:


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.VISIBLE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        topBar.imageViewHelp.setVisibility(View.GONE);
                        topBar.imageViewBack.setVisibility(View.VISIBLE);
                        topBar.imageViewMenu.setVisibility(View.GONE);

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
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                    .getTextBitmap(HomeActivity.this, assl,
                                            slidingBottomPanel.getRegionSelected().getEta(),
                                            getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));

                            pickupLocationMarker = map.addMarker(markerOptions);
							new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (map != null && Data.pickupLatLng != null) {
                                        map.animateCamera(CameraUpdateFactory.newLatLng(Data.pickupLatLng), MAP_ANIMATE_DURATION, null);
                                    }
                                }
                            }, 1000);
                        }

                        stopDropLocationSearchUI(false);

                        setDropLocationAssigningUI();

                        relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);
						setGoogleMapPadding(getResources().getDimension(R.dimen.map_padding_assigning));


                        topBar.imageViewHelp.setVisibility(View.GONE);

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

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo));

                            Log.i("marker added", "REQUEST_FINAL");
                        }

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

                        setAssignedDriverData(mode);

                        if(dropLocationSearched){
                            initDropLocationSearchUI(true);
                        }
                        else{
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();
                        zoomtoPickupAndDriverLatLngBounds(Data.assignedDriverInfo.latLng);

                        buttonCancelRide.setVisibility(View.VISIBLE);
                        buttonAddPaytmCash.setVisibility(View.GONE);
                        linearLayoutSendInvites.setVisibility(View.GONE);
                        linearLayoutSendInvites.clearAnimation();
                        checkForGoogleLogoVisibilityInRide();
						setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);
                        topBar.imageViewAppToggle.setVisibility(View.GONE);

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

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo));

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

						if(dropLocationSearched){
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
                        setDropLocationEngagedUI();

                        setAssignedDriverData(mode);
                        zoomtoPickupAndDriverLatLngBounds(Data.assignedDriverInfo.latLng);


                        buttonCancelRide.setVisibility(View.VISIBLE);
                        buttonAddPaytmCash.setVisibility(View.GONE);
                        linearLayoutSendInvites.setVisibility(View.GONE);
                        linearLayoutSendInvites.clearAnimation();
                        checkForGoogleLogoVisibilityInRide();
						setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);
                        topBar.imageViewAppToggle.setVisibility(View.GONE);

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



                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

						if(dropLocationSearched){
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
						setDropLocationEngagedUI();

                        setAssignedDriverData(mode);
                        zoomtoPickupAndDriverLatLngBounds(Data.assignedDriverInfo.latLng);

                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddPaytmCash.setVisibility(View.GONE);
                        linearLayoutSendInvites.setVisibility(View.VISIBLE);
                        updateInRideAddPaytmButtonText();
                        checkForGoogleLogoVisibilityInRide();
						setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);
                        topBar.imageViewAppToggle.setVisibility(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

                        break;

                    case P_RIDE_END:

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);
                        topBar.imageViewAppToggle.setVisibility(View.GONE);
						setGoogleMapPadding(0);

                        linearLayoutRideSummaryContainerSetVisiblity(View.GONE);

//                        genieLayout.setVisibility(View.GONE);

						Data.pickupLatLng = null;

                        dismissPushDialog(true);

                        dropLocationSet = false;

                        break;

                }

                initiateTimersForStates(mode);
                dismissReferAllDialog(mode);

                updateTopBar();

                openPaytmRechargeDialog();

                if(PassengerScreenMode.P_INITIAL != mode) {
                    callT20AndReferAllDialog(mode);
                }

                Prefs.with(this).save(SP_CURRENT_STATE, mode.getOrdinal());

                startStopLocationUpdateService(mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void callT20AndReferAllDialog(PassengerScreenMode mode){
        t20Ops.openDialog(this, Data.cEngagementId, mode, new T20Dialog.T20DialogCallback() {
            @Override
            public void onDismiss() {
                showReferAllDialog();
            }

            @Override
            public void notShown() {
                showReferAllDialog();
            }
        });
    }

    private void showPromoFailedAtSignupDialog(){
        try{
            if(Data.userData.getPromoSuccess() == 0
                    && PassengerScreenMode.P_INITIAL == passengerScreenMode){
                DialogPopup.alertPopupWithListener(HomeActivity.this, "",
                        Data.userData.getPromoMessage(),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                menuBar.menuAdapter.onClickAction(MenuInfoTags.PROMOTIONS.getTag());
                            }
                        });
                Data.userData.setPromoSuccess(1);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    public SlidingBottomPanel getSlidingBottomPanel(){
        return slidingBottomPanel;
    }

    private void startStopLocationUpdateService(PassengerScreenMode mode){
        Prefs.with(this).save(Constants.SP_CURRENT_ENGAGEMENT_ID, Data.cEngagementId);
        if(PassengerScreenMode.P_IN_RIDE == mode
                && Prefs.with(this).getLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_INTERVAL) > 0) {
            if(!Utils.isServiceRunning(this, LocationUpdateService.class.getName())) {
                Intent intent = new Intent(this, LocationUpdateService.class);
                intent.putExtra(KEY_ONE_SHOT, false);
                startService(intent);
            }
        } else{
            Intent intent = new Intent(this, LocationUpdateService.class);
            stopService(intent);
        }
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
            if(PassengerScreenMode.P_IN_RIDE != mode
                    && PassengerScreenMode.P_INITIAL != mode){
                dismissReferAllDialog();
                dialogUploadContacts = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissReferAllDialog(){
        if(dialogUploadContacts != null && dialogUploadContacts.isShowing()) {
            dialogUploadContacts.dismiss();
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
                bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
                if(placeSearchMode.getOrdinal() == PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal()){
                    bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.enter_destination));
                } else{
                    bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.set_pickup_location));
                }
                bundle.putInt(KEY_SEARCH_MODE, placeSearchMode.getOrdinal());
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
                if(textViewAssigningDropLocationClick.getText().length() > 0){
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, textViewAssigningDropLocationClick.getText().toString());
                } else{
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
                }
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                bundle.putInt(KEY_SEARCH_MODE, PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal());
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
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if(frag == null || frag.isRemoving()) {
                PlaceSearchListFragment placeSearchListFragment = new PlaceSearchListFragment(this, mGoogleApiClient);
                Bundle bundle = new Bundle();
                if(textViewFinalDropLocationClick.getText().length() > 0){
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, textViewFinalDropLocationClick.getText().toString());
                } else{
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, text);
                }
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                bundle.putInt(KEY_SEARCH_MODE, PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal());
                placeSearchListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutFinalDropLocationParent.getId(), placeSearchListFragment,
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_REQUEST_FINAL)
                        .commitAllowingStateLoss();
            }
        } else{
            relativeLayoutFinalDropLocationParent.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if(frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }


    private void linearLayoutRideSummaryContainerSetVisiblity(int visiblity){
        if(View.VISIBLE == visiblity){
            linearLayoutRideSummaryContainer.setVisibility(View.VISIBLE);
            Fragment frag = getRideSummaryFragment();
            if(frag == null || frag.isRemoving()) {
                getSupportFragmentManager().beginTransaction()
                        .add(linearLayoutRideSummaryContainer.getId(),
                                new RideSummaryFragment(-1),
                                RideSummaryFragment.class.getName())
                        .addToBackStack(RideSummaryFragment.class.getName())
                        .commitAllowingStateLoss();
            }
        } else{
            linearLayoutRideSummaryContainer.setVisibility(View.GONE);
            Fragment frag = getRideSummaryFragment();
            if(frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    private RideSummaryFragment getRideSummaryFragment(){
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(RideSummaryFragment.class.getName());
        return (RideSummaryFragment) frag;
    }

	private void updateInRideAddPaytmButtonText(){
		try{
            if (Data.userData.paytmEnabled == 1 && !Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)) {
				buttonAddPaytmCash.setText("Add Paytm Cash");
			}
			else{
				buttonAddPaytmCash.setText("Add Paytm Wallet");
			}
		} catch(Exception e){
			buttonAddPaytmCash.setText("Add Paytm Wallet");
		}
	}


    private void setDropLocationAssigningUI(){
        try {
            if(Data.dropLatLng == null){
				if ("".equalsIgnoreCase(Data.cSessionId)) {
					relativeLayoutAssigningDropLocationClick.setVisibility(View.GONE);
				}
				else{
					if(relativeLayoutAssigningDropLocationClick.getVisibility() == View.GONE){
						relativeLayoutAssigningDropLocationClick.setVisibility(View.VISIBLE);
						try {
							Animation topInAnimation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.top_in);
							relativeLayoutAssigningDropLocationClick.startAnimation(topInAnimation);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					textViewAssigningDropLocationClick.setText("");
					imageViewAssigningDropLocationEdit.setVisibility(View.GONE);
					progressBarAssigningDropLocation.setVisibility(View.GONE);
				}
			}
			else{
				if(relativeLayoutAssigningDropLocationClick.getVisibility() == View.GONE){
					relativeLayoutAssigningDropLocationClick.setVisibility(View.VISIBLE);
					try {
						Animation topInAnimation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.top_in);
						relativeLayoutAssigningDropLocationClick.startAnimation(topInAnimation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				setDropLocationMarker();
				imageViewAssigningDropLocationEdit.setVisibility(View.VISIBLE);
				if(textViewAssigningDropLocationClick.getText().length() == 0){
					getAddress(Data.dropLatLng, textViewAssigningDropLocationClick, progressBarAssigningDropLocation);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setDropLocationEngagedUI(){
        if(Data.dropLatLng == null){
            textViewFinalDropLocationClick.setText("");
            imageViewFinalDropLocationEdit.setVisibility(View.GONE);
            progressBarFinalDropLocation.setVisibility(View.GONE);
        }
        else{
            setDropLocationMarker();
            imageViewFinalDropLocationEdit.setVisibility(View.VISIBLE);
            if(textViewFinalDropLocationClick.getText().length() == 0){
                getAddress(Data.dropLatLng, textViewFinalDropLocationClick, progressBarFinalDropLocation);
            }
        }
    }

    private void setDropLocationMarker(){
		if(Data.dropLatLng != null
                && (PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)) {
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
            float padding = getResources().getDimension(R.dimen.map_padding_request_final);
            if(relativeLayoutInRideInfo.getVisibility() == View.VISIBLE){
                padding = padding + getResources().getDimension(R.dimen.map_padding_request_final_extra);
            }
			setGoogleMapPadding(padding);
		} catch(Exception e){
			e.printStackTrace();
		}
	}




	public void setAssignedDriverData(PassengerScreenMode mode) {
        try {
            textViewInRideDriverName.setText(Data.assignedDriverInfo.name);
            if (!"".equalsIgnoreCase(Data.assignedDriverInfo.carNumber)) {
				textViewInRideDriverCarNumber.setText(Data.assignedDriverInfo.carNumber);
			} else {
				textViewInRideDriverCarNumber.setText("");
			}

            if (!Data.NO_PROMO_APPLIED.equalsIgnoreCase(Data.assignedDriverInfo.promoName)) {
                relativeLayoutInRideInfo.setVisibility(View.VISIBLE);
				textViewInRidePromoName.setText(Data.assignedDriverInfo.promoName);
			} else {
                relativeLayoutInRideInfo.setVisibility(View.GONE);
				textViewInRidePromoName.setText("");
			}


            if (Data.userData.fareFactor > 1 || Data.userData.fareFactor < 1) {
                linearLayoutSurgeContainer.setVisibility(View.VISIBLE);
				textViewInRideFareFactor.setText(decimalFormat.format(Data.userData.fareFactor) + "x");
			} else {
                linearLayoutSurgeContainer.setVisibility(View.GONE);
				textViewInRideFareFactor.setText("");
			}


            if (PassengerScreenMode.P_REQUEST_FINAL == mode || PassengerScreenMode.P_DRIVER_ARRIVED == mode) {
				updateDriverETAText(mode);
			} else {
				textViewInRideState.setText("Ride in progress");
			}

            try {
                double rating = Double.parseDouble(Data.assignedDriverInfo.rating);
                if(rating > 0) {
                    relativeLayoutDriverRating.setVisibility(View.VISIBLE);
                    textViewDriverRating.setText(decimalFormat1DigitAD.format(rating));
                } else{
                    relativeLayoutDriverRating.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                relativeLayoutDriverRating.setVisibility(View.GONE);
            }

            Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
            try {
				if(!"".equalsIgnoreCase(Data.assignedDriverInfo.image)) {
					Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).transform(new CircleTransform()).into(imageViewInRideDriver);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
                textViewIRPaymentOption.setVisibility(View.GONE);
				textViewIRPaymentOptionValue.setVisibility(View.VISIBLE);
				textViewIRPaymentOptionValue.setText(String.format(getResources()
                        .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
                textViewIRPaymentOptionValue.setTextColor(Data.userData.getPaytmBalanceColor(this));
			}
			else{
				relativeLayoutIRPaymentOption.setVisibility(View.VISIBLE);
				imageViewIRPaymentOptionPaytm.setVisibility(View.GONE);
				imageViewIRPaymentOptionCash.setVisibility(View.VISIBLE);
                textViewIRPaymentOption.setVisibility(View.VISIBLE);
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
                    pickupLocationMarker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(CustomMapMarkerCreator
                                    .getTextBitmap(HomeActivity.this, assl, Data.assignedDriverInfo.getEta(),
                                            getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewInRideState.setText("Driver Enroute");
            }
        } else if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
            textViewInRideState.setText("Arrived at Pickup");
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

    private void showReferAllDialog(){
        try {
            if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
                //******** If return 0 then show popup, contact not saved in database.
                if (Data.userData.contactSaved == 0
                        && (Prefs.with(HomeActivity.this).getInt(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0) == 0)
                        && dialogUploadContacts == null
                        && Data.NO_PROMO_APPLIED.equalsIgnoreCase(Data.assignedDriverInfo.promoName)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    dialogUploadContacts = DialogPopup.uploadContactsTwoButtonsWithListeners(HomeActivity.this,
                            Data.userData.referAllTitle,
                            Data.userData.referAllText,
                            getResources().getString(R.string.upload_contact_yes),
                            getResources().getString(R.string.upload_contact_no_thanks),
                            false,
                            new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)) {
                                        FlurryEventLogger.eventGA(CAMPAIGNS, "CBCR pop up", "yes");
                                        DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                                        Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 1);
                                        Intent syncContactsIntent = new Intent(HomeActivity.this, ContactsUploadService.class);
                                        syncContactsIntent.putExtra("access_token", Data.userData.accessToken);
                                        syncContactsIntent.putExtra("session_id", Data.cSessionId);
                                        syncContactsIntent.putExtra("engagement_id", Data.cEngagementId);
                                        syncContactsIntent.putExtra(KEY_IS_LOGIN_POPUP, "0");
                                        startService(syncContactsIntent);
                                        registerDialogDismissReceiver();
                                        dismissReferAllDialog();
                                    } else {
                                        DialogPopup.dialogNoInternet(HomeActivity.this, DialogErrorType.NO_NET,
                                                new Utils.AlertCallBackWithButtonsInterface() {
                                                    @Override
                                                    public void positiveClick(View view) {

                                                    }

                                                    @Override
                                                    public void neutralClick(View view) {

                                                    }

                                                    @Override
                                                    public void negativeClick(View view) {

                                                    }
                                                });
                                    }
                                }
                            }, new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)) {
                                        FlurryEventLogger.eventGA(CAMPAIGNS, "CBCR pop up", "no thanks");
                                        Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, -1);
                                        uploadContactsApi(false);
                                        dismissReferAllDialog();
                                    } else {
                                        DialogPopup.dialogNoInternet(HomeActivity.this, DialogErrorType.NO_NET,
                                                new Utils.AlertCallBackWithButtonsInterface() {
                                                    @Override
                                                    public void positiveClick(View view) {

                                                    }

                                                    @Override
                                                    public void neutralClick(View view) {

                                                    }

                                                    @Override
                                                    public void negativeClick(View view) {

                                                    }
                                                });
                                    }
                                }
                            }, new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }
                            });
                }
            } else if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
                if (Data.userData.getReferAllStatusLogin() == 0
                        && dialogUploadContacts == null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    dialogUploadContacts = DialogPopup.uploadContactsTwoButtonsWithListeners(HomeActivity.this,
                            Data.userData.getReferAllTitleLogin(),
                            Data.userData.getReferAllTextLogin(),
                            getResources().getString(R.string.upload_contact_yes),
                            getResources().getString(R.string.upload_contact_no_thanks),
                            false,
                            new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)) {
                                        FlurryEventLogger.eventGA(CAMPAIGNS, "CBCD pop up", "yes");
                                        DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                                        Data.userData.contactSaved = 1;
                                        Data.userData.setReferAllStatusLogin(1);
                                        Intent syncContactsIntent = new Intent(HomeActivity.this, ContactsUploadService.class);
                                        syncContactsIntent.putExtra("access_token", Data.userData.accessToken);
                                        syncContactsIntent.putExtra("session_id", "");
                                        syncContactsIntent.putExtra("engagement_id", "");
                                        syncContactsIntent.putExtra(KEY_IS_LOGIN_POPUP, "1");
                                        startService(syncContactsIntent);
                                        registerDialogDismissReceiver();
                                        dismissReferAllDialog();
                                    } else{
                                        DialogPopup.dialogNoInternet(HomeActivity.this, DialogErrorType.NO_NET,
                                                new Utils.AlertCallBackWithButtonsInterface() {
                                                    @Override
                                                    public void positiveClick(View view) {

                                                    }

                                                    @Override
                                                    public void neutralClick(View view) {

                                                    }

                                                    @Override
                                                    public void negativeClick(View view) {

                                                    }
                                                });
                                    }
                                }
                            }, new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)) {
                                        FlurryEventLogger.eventGA(CAMPAIGNS, "CBCD pop up", "no thanks");
                                        Data.userData.contactSaved = 0;
                                        Data.userData.setReferAllStatusLogin(-1);
                                        uploadContactsApi(true);
                                        dismissReferAllDialog();
                                    } else{
                                        DialogPopup.dialogNoInternet(HomeActivity.this, DialogErrorType.NO_NET,
                                                new Utils.AlertCallBackWithButtonsInterface() {
                                                    @Override
                                                    public void positiveClick(View view) {

                                                    }

                                                    @Override
                                                    public void neutralClick(View view) {

                                                    }

                                                    @Override
                                                    public void negativeClick(View view) {

                                                    }
                                                });
                                    }
                                }
                            }, new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    showPromoFailedAtSignupDialog();
                                }
                            });
                } else{
                    showPromoFailedAtSignupDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (!checkIfUserDataNull(HomeActivity.this)) {
                setUserData();

                try {
                    if (activityResumed) {
                        if (!feedbackSkipped && !placeAdded
                                && PassengerScreenMode.P_RIDE_END != passengerScreenMode
                                && PassengerScreenMode.P_SEARCH != passengerScreenMode) {
                            mapTouched = false;
                            callAndHandleStateRestoreAPI(false);
                        }
                        initiateTimersForStates(passengerScreenMode);

                        if (!intentFired) {
                            if (userMode == UserMode.PASSENGER &&
                                    (PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)) {
                                if (map != null && myLocation != null) {
                                    initialMyLocationBtn.performClick();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (Data.supportFeedbackSubmitted) {
                        drawerLayout.closeDrawer(GravityCompat.START);
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

                getPaytmBalance(this);


                String alertMessage = Prefs.with(this).getString(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                if (!"".equalsIgnoreCase(alertMessage)) {
                    Prefs.with(this).save(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                    DialogPopup.alertPopup(this, "", alertMessage);
                }

                updateTopBar();

            }

            HomeActivity.checkForAccessTokenChange(this);

            activityResumed = true;
            intentFired = false;
            feedbackSkipped = false;
            placeAdded = false;

            initializeFusedLocationFetchers();

            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 19) {
                DialogPopup.showLocationSettingsAlert(HomeActivity.this);
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

            try {
				AdWordsConversionReporter.registerReferrer(this.getApplicationContext(), this.getIntent().getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
            try {
				AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(this, GOOGLE_ADWORD_CONVERSION_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                    Settings.Secure.ACCESSIBILITY_ENABLED);
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
                intentToShareActivity(true);
			}
			else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.WALLET.getTag());
			}
			else if(AppLinkIndex.PROMOTIONS.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.PROMOTIONS.getTag());
			}
			else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.HISTORY.getTag());
			}
			else if(AppLinkIndex.SUPPORT.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.SUPPORT.getTag());
			}
			else if(AppLinkIndex.ABOUT.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.ABOUT.getTag());
			}
			else if(AppLinkIndex.ACCOUNT.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.accountClick();
			}
			else if(AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.INBOX.getTag());
			}
            else if(AppLinkIndex.GAME_PAGE.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.GAME.getTag());
            }
            else if(AppLinkIndex.FRESH_PAGE.getOrdinal() == Data.deepLinkIndex){
                menuBar.menuAdapter.onClickAction(MenuInfoTags.JUGNOO_FRESH.getTag());
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
                    AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(strResult);
                    if(searchResult != null){
                        placeAdded = true;
                        Prefs.with(HomeActivity.this).save(SPLabels.ADD_HOME, strResult);
                    }else {
                    }

                } else if (requestCode == ADD_WORK) {
                    String strResult = data.getStringExtra("PLACE");
                    AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(strResult);
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


	public void backFromSearchToInitial(){
		try {
			textViewInitialSearch.setText("");
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
			switchPassengerScreen(passengerScreenMode);
			FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
            Utils.hideSoftKeyboard(this, textViewInitialSearch);
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
            else if(PassengerScreenMode.P_RIDE_END == passengerScreenMode
                    && linearLayoutRideSummaryContainer.getVisibility() == View.VISIBLE){
                linearLayoutRideSummaryContainerSetVisiblity(View.GONE);
            }
            else{
                if(slidingBottomPanel.getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                else{
                    Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                    ActivityCompat.finishAffinity(HomeActivity.this);
                }

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        startService(new Intent(BaseActivity.GENIE_SERVICE));
//                    }
//                }, 2000);
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

        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void intentToShareActivity(boolean fromDeepLink){
        Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
        intent.putExtra(KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, fromDeepLink);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

        try {
            if(fromDeepLink){
				FlurryEventLogger.event(this, INVITE_SCREEN_THROUGH_PUSH);
			} else{
				FlurryEventLogger.event(INVITE_EARN_MENU);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiFindADriver createApiFindADriver(){
        if(apiFindADriver == null) {
            apiFindADriver = new ApiFindADriver(this, slidingBottomPanel.getRegionSelected(),
                    new ApiFindADriver.Callback() {
                @Override
                public void onPre() {
                    try {
                        promoCouponSelectedForRide = null;
                        if (userMode == UserMode.PASSENGER) {
                            dontCallRefreshDriver = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onComplete() {
                    findADriverFinishing();
                }

                @Override
                public void onFailure() {
                    try {
                        if (Data.driverInfos.size() == 0) {
                            textViewCentrePinETA.setText("-");
                            noDriverNearbyToast(getResources().getString(R.string.couldnt_find_drivers_nearby));
                        }
                        setServiceAvailablityUI(Data.farAwayCity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return apiFindADriver;
    }

    private ApiFindADriver apiFindADriver = null;
    private void findDriversETACall(){
        createApiFindADriver().hit(Data.userData.accessToken, Data.pickupLatLng, showAllDrivers, showDriverInfo,
                slidingBottomPanel.getRegionSelected());
    }

    private void findADriverFinishing(){
        if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
            try {
                slidingBottomPanel.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (relativeLayoutLocationError.getVisibility() == View.GONE) {
                    showDriverMarkersAndPanMap(Data.pickupLatLng, slidingBottomPanel.getRegionSelected());
                    dontCallRefreshDriver = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dontCallRefreshDriver = false;
                        }
                    }, 300);
                    setServiceAvailablityUI(Data.farAwayCity);
                    setupFreshUI();
                    setupInAppCampaignUI();
                    setJugnooPool();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void setupFreshUI(){
        try{
            if(1 == Data.freshAvailable){
                /*Dialog locD = new FreshIntroDialog(this, freshIntroCallback).show();
                if(locD != null){
                    freshIntroDialog = locD;
                }*/
            } else {
                freshIntroCallback.notShown();
            }
            menuBar.setupFreshUI();
            topBar.setupFreshUI();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupInAppCampaignUI(){
        try{
            if(Data.campaigns != null && Data.campaigns.getMapLeftButton() != null){
                imageViewInAppCampaign.clearAnimation();
                imageViewInAppCampaign.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new FrameAnimDrawable(HomeActivity.this, (ArrayList<String>) Data.campaigns.getMapLeftButton().getImages(),
									imageViewInAppCampaign);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            } else{
                imageViewInAppCampaign.clearAnimation();
                imageViewInAppCampaign.setVisibility(View.GONE);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setJugnooPool(){
        try {
            if(Data.userData.getIsPoolEnabled() == 1){
                //Glide.with(HomeActivity.this).load(R.drawable.pool_gif_1).placeholder(R.drawable.pool_1).into(imageViewJugnooPool);
                imageViewJugnooPool.setBackgroundResource(R.drawable.pool_icon_frame_anim);
                imageViewJugnooPool.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation =
                                (AnimationDrawable) imageViewJugnooPool.getBackground();
                        frameAnimation.start();
                    }
                });
                imageViewJugnooPool.setVisibility(View.VISIBLE);
            } else{
                imageViewJugnooPool.setVisibility(View.GONE);
            }
            slidingBottomPanel.updateSlidingBottomHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private FreshIntroDialog.Callback freshIntroCallback = new FreshIntroDialog.Callback() {

        @Override
        public void onContinueClicked() {
            menuBar.menuAdapter.onClickAction(MenuInfoTags.JUGNOO_FRESH.getTag());
        }

        @Override
        public void onMayBeLaterClicked() {
            callT20AndReferAllDialog(passengerScreenMode);
        }

        @Override
        public void notShown() {
            if(1 == Data.freshAvailable){
                if(freshIntroDialog == null){
                    callT20AndReferAllDialog(passengerScreenMode);
                }
            } else {
                callT20AndReferAllDialog(passengerScreenMode);
            }
        }

        @Override
        public void onDialogDismiss() {
            freshIntroDialog = null;
        }
    };


	//Our service is not available in this area
	private void setServiceAvailablityUI(String farAwayCity){
        if (PassengerScreenMode.P_INITIAL == passengerScreenMode
                && relativeLayoutLocationError.getVisibility() == View.GONE) {
            if (!"".equalsIgnoreCase(farAwayCity)) {
                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                changeLocalityLayout.setVisibility(View.VISIBLE);
                textViewChangeLocality.setText(farAwayCity);

                textViewCentrePinETA.setText("-");
                imageViewRideNow.setVisibility(View.GONE);
                initialMyLocationBtn.setVisibility(View.GONE);
            } else {
                imageViewRideNow.setVisibility(View.VISIBLE);
                checkForMyLocationButtonVisibility();
                changeLocalityLayout.setVisibility(View.GONE);
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
                            .getTextBitmap(HomeActivity.this, assl, Data.assignedDriverInfo.getEta(),
                                    getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
        }
        return markerOptions;
    }

    public MarkerOptions getAssignedDriverCarMarkerOptions(DriverInfo driverInfo){
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title("driver position");
        markerOptions1.snippet("");
        markerOptions1.position(driverInfo.latLng);
        markerOptions1.anchor(0.5f, 0.5f);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                .createMarkerBitmapForResource(HomeActivity.this, assl, driverInfo.getVehicleIconSet().getIconMarker())));
        return markerOptions1;
    }

    public void addDriverMarkerForCustomer(DriverInfo driverInfo, int resourceId) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("driver shown to customer");
        markerOptions.snippet("" + driverInfo.userId);
        markerOptions.position(driverInfo.latLng);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.rotation((float) driverInfo.getBearing());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                .createMarkerBitmapForResource(HomeActivity.this, assl, resourceId)));
        map.addMarker(markerOptions);
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
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                    .getTextBitmap(HomeActivity.this, assl,
                            slidingBottomPanel.getRegionSelected().getEta(),
                            getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
            currentLocationMarker = map.addMarker(markerOptions);
        } catch (Exception e) {
        }
    }

    public void showDriverMarkersAndPanMap(final LatLng userLatLng, Region region) {
        try {
			if("".equalsIgnoreCase(Data.farAwayCity)) {
				if (userMode == UserMode.PASSENGER &&
						((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)
								|| PassengerScreenMode.P_ASSIGNING == passengerScreenMode)) {
					if (map != null) {
						map.clear();
						setDropLocationMarker();
						for (int i = 0; i < Data.driverInfos.size(); i++) {
                            if(region.getVehicleType().equals(Data.driverInfos.get(i).getVehicleType())
                                    && Data.driverInfos.get(i).getRegionIds().contains(region.getRegionId())) {
                                addDriverMarkerForCustomer(Data.driverInfos.get(i),
                                        region.getVehicleIconSet().getIconMarker());
                            }
						}
						if (!mapTouchedOnce) {
							zoomToCurrentLocationWithOneDriver(userLatLng);
							mapTouchedOnce = true;
						}

                        if (Data.driverInfos.size() == 0) {
                            textViewCentrePinETA.setText("-");
                        } else {
                            textViewCentrePinETA.setText(region.getEta());
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

    private void zoomtoPickupAndDriverLatLngBounds(final LatLng driverLatLng){
        try{
            if(!pickupDropZoomed && (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)
                    && Data.pickupLatLng != null && driverLatLng != null) {

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                double distance = MapUtils.distance(Data.pickupLatLng, driverLatLng);
                if (distance <= 15000) {
                    boundsBuilder.include(Data.pickupLatLng).include(driverLatLng);
                    if(Data.dropLatLng != null && PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
                        boundsBuilder.include(Data.dropLatLng);
                    }
                    final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder, FIX_ZOOM_DIAGONAL);
                    final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if ((PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                                        || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                                        || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)
                                        && bounds != null) {
                                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), MAP_ANIMATE_DURATION, null);
                                    pickupDropZoomed = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
            }
        } catch(Exception e){
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
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM), MAP_ANIMATE_DURATION, null);
                                }
                                else {
                                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), MAP_ANIMATE_DURATION, null);
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
								if("".equalsIgnoreCase(Data.farAwayCity)) {
									map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM), MAP_ANIMATE_DURATION, null);
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
	private void getAddress(final LatLng currentLatLng, final TextView textView, final ProgressWheel progressBar){
		stopPickupAddressFetcherThread();
		try {
            progressBar.setVisibility(View.VISIBLE);
            if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
                relativeLayoutPinEtaRotate.setVisibility(View.GONE);
            }
            textView.setText("");
            textView.setHint("Getting address...");
            pickupAddressFetcherThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    GAPIAddress gapiAddressForPin = MapUtils.getGAPIAddressObject(currentLatLng);
                    String address = gapiAddressForPin.getSearchableAddress();
                    setAddress(address, textView, progressBar);
                    stopPickupAddressFetcherThread();
                }
            });
            pickupAddressFetcherThread.start();
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

	private void setAddress(final String address, final TextView textView, final ProgressWheel progressBar){
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                if(PassengerScreenMode.P_INITIAL == passengerScreenMode){
                    relativeLayoutPinEtaRotate.setVisibility(View.VISIBLE);
                }
                if (PassengerScreenMode.P_INITIAL == passengerScreenMode) {
                    textView.setHint(getResources().getString(R.string.set_pickup_location));
                    textView.setText(address);
                } else if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                        || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                        || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                        || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
                    textView.setHint(getResources().getString(R.string.enter_your_destination));
                    textView.setText(address);
                }
            }
        });
	}

    /**
     * ASync for cancelCustomerRequestAsync from server
     */
    public void cancelCustomerRequestAsync(final Activity activity) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("session_id", Data.cSessionId);

            Log.i("access_token", "=" + Data.userData.accessToken);
            Log.i("session_id", "=" + Data.cSessionId);

            RestClient.getApiServices().cancelTheRequest(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "cancelTheRequest response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();

                    try {
                        JSONObject jObj = new JSONObject(responseStr);

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
                            NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_CANCEL_REQUEST, null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "cancelTheRequest error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    callAndHandleStateRestoreAPI(true);
                }
            });
        } else {
            //DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            DialogPopup.dialogNoInternet(HomeActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                @Override
                public void positiveClick(View v) {
                    cancelCustomerRequestAsync(HomeActivity.this);
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


    public void customerUIBackToInitialAfterCancel() {
		firstTimeZoom = false;
		pickupDropZoomed = false;

        cancelTimerRequestRide();

        passengerScreenMode = PassengerScreenMode.P_INITIAL;
        switchPassengerScreen(passengerScreenMode);

        if (map != null && pickupLocationMarker != null) {
            pickupLocationMarker.remove();
        }

        dontCallRefreshDriver = false;
        callMapTouchedRefreshDrivers();

    }


    public void initializeStartRideVariables() {

        HomeActivity.previousWaitTime = 0;
        HomeActivity.previousRideTime = 0;

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
            textHead.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            textHead.setVisibility(View.GONE);

            TextView textMessage = (TextView) noDriversDialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenMedium(activity));

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
            btnOk.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

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
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                RestClient.getApiServices().acceptAppRatingRequest(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "acceptAppRatingRequest response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {

                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "acceptAppRatingRequest error="+error.toString());
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
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(KEY_ENGAGEMENT_ID, engagementId);
                params.put(KEY_SHOW_RIDE_MENU, "0");
                RestClient.getApiServices().getRideSummary(params, new Callback<GetRideSummaryResponse>() {
                    @Override
                    public void success(GetRideSummaryResponse getRideSummaryResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getRideSummary response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {
                                    try {
                                        if (jObj.has("rate_app")) {
                                            Data.customerRateAppFlag = jObj.getInt("rate_app");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Data.userData.setJugnooBalance(jObj.optDouble(KEY_JUGNOO_BALANCE,
                                            Data.userData.getJugnooBalance()));
                                    Data.userData.setPaytmBalance(jObj.optDouble(KEY_PAYTM_BALANCE,
                                            Data.userData.getPaytmBalance()));

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

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "getRideSummary error="+error);
                        DialogPopup.dismissLoadingDialog();
                        endRideRetryDialog(activity, engagementId, Data.SERVER_NOT_RESOPNDING_MSG);
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

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("session_id", Data.cSessionId);
            params.put(KEY_OP_DROP_LATITUDE, ""+dropLatLng.latitude);
            params.put(KEY_OP_DROP_LONGITUDE, "" + dropLatLng.longitude);
			if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
				params.put("engagement_id", Data.cEngagementId);
			}

            Log.i("params", "=" + params);

            RestClient.getApiServices().addDropLocation(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "addDropLocation response = " + responseStr);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        String message = JSONParser.getServerMessage(jObj);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                Data.dropLatLng = dropLatLng;

                                if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
                                    stopDropLocationSearchUI(false);
                                    setDropLocationAssigningUI();
                                } else if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
                                        PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
                                        PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {

                                    pickupDropZoomed = false;
                                    stopDropLocationSearchUI(true);
                                    setDropLocationEngagedUI();

                                    getDropLocationPathAndDisplay(Data.pickupLatLng);
                                    FlurryEventLogger.eventGA(REVENUE+SLASH+ ACTIVATION + SLASH + RETENTION, "ride start", "enter destination");
                                }

                            } else {
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

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "addDropLocation error="+error.toString());
//                        DialogPopup.dismissLoadingDialog();
                    progressWheel.setVisibility(View.GONE);
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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

                            long startTime = System.currentTimeMillis();
                            HashMap<String, String> nameValuePairs = new HashMap<>();
                            nameValuePairs.put("access_token", Data.userData.accessToken);
                            nameValuePairs.put("driver_id", Data.assignedDriverInfo.userId);
							nameValuePairs.put("pickup_latitude", "" + Data.pickupLatLng.latitude);
							nameValuePairs.put("pickup_longitude", ""+Data.pickupLatLng.longitude);

                            Response response = RestClient.getApiServices().getDriverCurrentLocation(nameValuePairs);
                            String result = new String(((TypedByteArray)response.getBody()).getBytes());
                            FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_GET_DRIVER_CURRENT_LOCATION, startTime);

//                            HttpRequester simpleJSONParser = new HttpRequester();
//                            String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/get_driver_current_location", nameValuePairs);

                            Log.e(TAG, "getDriverCurrentLocation result=" + result);
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
            timerUpdateDrivers.scheduleAtFixedRate(timerTaskUpdateDrivers, 60000, 60000);
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
                        long startTime = System.currentTimeMillis();
                        HashMap<String, String> nameValuePairs = new HashMap<>();
                        nameValuePairs.put("last_sent_max_id", "" +
                            Database2.getInstance(HomeActivity.this).getLastRowIdInRideInfo());
                        nameValuePairs.put("engagement_id", Data.cEngagementId);
                        nameValuePairs.put("access_token", Data.userData.accessToken);

                        Response response = RestClient.getApiServices().getOngoingRidePath(nameValuePairs);
                        String result = new String(((TypedByteArray)response.getBody()).getBytes());
                        FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_GET_ONGOING_RIDE_PATH, startTime);

                        Log.e(TAG, "getOngoingRidePath result=" + result);

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
        return (PassengerScreenMode.P_IN_RIDE == passengerScreenMode);
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
                                        zoomtoPickupAndDriverLatLngBounds(Data.assignedDriverInfo.latLng);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);
                        }
                    });
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) && Data.dropLatLng != null && lastLatLng != null && toShowPathToDrop()) {
                            Response response = RestClient.getGoogleApiServices().getDirections(lastLatLng.latitude + "," + lastLatLng.longitude,
                                    Data.dropLatLng.latitude + "," + Data.dropLatLng.longitude, false, "driving", false);
                            String result = new String(((TypedByteArray)response.getBody()).getBytes());
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

                                                    zoomtoPickupAndDriverLatLngBounds(Data.assignedDriverInfo.latLng);
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
								} else{
                                    try {
                                        FlurryEventLogger.event(FlurryEventNames.GOOGLE_API_DIRECTIONS_FAILURE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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

    void callAnAutoPopup(final Activity activity) {
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
            textHead.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.mavenMedium(activity));

            textHead.setVisibility(View.VISIBLE);
            textHead.setText("Chalo Jugnoo Se");


            final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.mavenMedium(activity));

            btnOk.setText("OK");
            btnCancel.setText("Cancel");

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppStatus.getInstance(activity).isOnline(activity)) {
                        dialog.dismiss();
                        if (getFilteredDrivers() == 0) {
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
                                    public void positiveClick(View v) {
                                        btnOk.performClick();
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
                            FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, TAG, "different pickup location popup");
                            textMessage.setText("The pickup location you have set is different from your current location. Are you sure you want an auto at this pickup location?");
                            dialog.show();
                        } else {
                            if (getFilteredDrivers() == 0) {
                                noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
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

    private int getFilteredDrivers(){
        int driversCount = 0;
        for(DriverInfo driverInfo : Data.driverInfos){
            if(slidingBottomPanel.getRegionSelected().getVehicleType().equals(driverInfo.getVehicleType())
                    && driverInfo.getRegionIds() != null
                    && driverInfo.getRegionIds().contains(slidingBottomPanel.getRegionSelected().getRegionId())){
                driversCount++;
            }
        }
        return driversCount;
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
    public void startRideForCustomer(final int flag, final String message) {
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
                            if(Data.dropLatLng != null) {
                                pickupDropZoomed = false;
                            }
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
                                DialogPopup.alertPopup(HomeActivity.this, "", message);
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
            JSONParser.parseDropLatLng(jObj);

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

            Schedule scheduleT20 = JSONParser.parseT20Schedule(jObj);

            int vehicleType = jObj.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
            String iconSet = jObj.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
            Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName,
                driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta,
                    fareFixed, preferredPaymentMode, scheduleT20, vehicleType, iconSet);

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
        try {
            super.onStart();
            FlurryAgent.init(this, Config.getFlurryKey());
            FlurryAgent.onStartSession(this, Config.getFlurryKey());
            FlurryAgent.onEvent("HomeActivity started");
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            FlurryAgent.onEndSession(this);
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
				Data.farAwayCity = "";
                mapTouched = true;
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
                locationGotNow();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void locationGotNow(){
        textViewInitialSearch.setText("");
        relativeLayoutLocationError.setVisibility(View.GONE);
        checkForMyLocationButtonVisibility();
        imageViewRideNow.setVisibility(View.VISIBLE);
        centreLocationRl.setVisibility(View.VISIBLE);
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
                        findDriversETACall();
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

                                long apiStartTime = System.currentTimeMillis();
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
                                if(Data.dropLatLng != null){
                                    nameValuePairs.put(KEY_OP_DROP_LATITUDE, String.valueOf(Data.dropLatLng.latitude));
                                    nameValuePairs.put(KEY_OP_DROP_LONGITUDE, String.valueOf(Data.dropLatLng.longitude));
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
                                    nameValuePairs.put(KEY_CUSTOMER_FARE_FACTOR, String.valueOf(Data.userData.fareFactor));
                                    if (myLocation != null && myLocation.hasAccuracy()) {
                                        nameValuePairs.put("location_accuracy", "" + myLocation.getAccuracy());
                                    }

                                } else {
                                    nameValuePairs.put("duplicate_flag", "1");
                                }


                                String links = Database2.getInstance(HomeActivity.this).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
								if(links != null){
                                    if(!"[]".equalsIgnoreCase(links)) {
                                        nameValuePairs.put(KEY_BRANCH_REFERRING_LINKS, links);
                                    }
								}

								nameValuePairs.put("preferred_payment_mode", "" + Data.pickupPaymentOption);
                                nameValuePairs.put(KEY_VEHICLE_TYPE, String.valueOf(slidingBottomPanel
                                        .getRegionSelected().getVehicleType()));
                                nameValuePairs.put(KEY_REGION_ID, String.valueOf(slidingBottomPanel
                                        .getRegionSelected().getRegionId()));

                                nameValuePairs.put("is_pooled", String.valueOf(isPoolRequest));
                                if(isPoolRequest == 1){
                                    nameValuePairs.put("pool_fare_id", ""+jugnooPoolFareId);
                                }

                                Log.i("nameValuePairs of request_ride", "=" + nameValuePairs);

                                Response responseRetro = RestClient.getApiServices().requestRide(nameValuePairs);
                                String response = new String(((TypedByteArray) responseRetro.getBody()).getBytes());
                                FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_REQUEST_RIDE, apiStartTime);

                                try{
                                    if(promoCouponSelectedForRide.id > 0) {
                                        JSONObject map = new JSONObject();
                                        map.put(KEY_COUPON_SELECTED, promoCouponSelectedForRide.getTitle());
                                        NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_OFFER_SELECTED, map);
                                    }
                                } catch(Exception e){
                                    e.printStackTrace();
                                }

                                Log.e(TAG, "requestRide result=" + response);

                                if (responseRetro == null || response == null
                                        || response.contains(Constants.SERVER_TIMEOUT)) {
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
                                                HomeActivity.logoutUser(HomeActivity.this);
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
                                                    // Ride Requested
                                                    // Google Android in-app conversion tracking snippet
                                                    // Add this code to the event you'd like to track in your app.
                                                    // See code examples and learn how to add advanced features like app deep links at:
                                                    //     https://developers.google.com/app-conversion-tracking/android/#track_in-app_events_driven_by_advertising
                                                    AdWordsConversionReporter.reportWithConversionId(HomeActivity.this.getApplicationContext(),
                                                            GOOGLE_ADWORD_CONVERSION_ID, "rxWHCIjbw2MQlLT2wwM", "0.00", true);
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
                                                            try {
                                                                JSONObject map = new JSONObject();
                                                                map.put(KEY_LATITUDE, Data.pickupLatLng.latitude);
                                                                map.put(KEY_LONGITUDE, Data.pickupLatLng.longitude);
                                                                NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_DRIVER_NOT_ASSIGNED, map);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
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
                                                            new UserDebtDialog(HomeActivity.this, Data.userData,
                                                                    new UserDebtDialog.Callback() {
                                                                        @Override
                                                                        public void successFullyDeducted(double userDebt) {
                                                                            setUserData();
                                                                        }
                                                                    }).showUserDebtDialog(userDebt, message, false);
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
                    setDropLocationAssigningUI();
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
                    try {
                        JSONObject map = new JSONObject();
                        map.put(KEY_LATITUDE, Data.pickupLatLng.latitude);
                        map.put(KEY_LONGITUDE, Data.pickupLatLng.longitude);
                        NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_DRIVER_NOT_ASSIGNED, map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public void onAfterRideFeedbackSubmitted(final int givenRating, final boolean skipped) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                afterRideFeedbackSubmitted(givenRating, skipped);
            }
        });

    }

    public void afterRideFeedbackSubmitted(final int givenRating, final boolean skipped){
        try {
            ReferralActions.incrementTransactionCount(HomeActivity.this);
            userMode = UserMode.PASSENGER;

            switchUserScreen();

            if (givenRating >= 4 && Data.customerRateAppFlag == 1) {
				rateAppPopup(HomeActivity.this);
			}
            firstTimeZoom = false;
            pickupDropZoomed = false;
            dropLocationSearchText = "";

            slidingBottomPanel.setSelectedCoupon(null);


            Data.pickupLatLng = null;
            Data.dropLatLng = null;
            dropLocationSet = false;

            Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
            setUserData();

            resetPickupDropFeilds();

            passengerScreenMode = PassengerScreenMode.P_INITIAL;
            switchPassengerScreen(passengerScreenMode);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dontCallRefreshDriver = false;
                    callMapTouchedRefreshDrivers();
                }
            }, 150);

            Utils.hideSoftKeyboard(HomeActivity.this, editTextRSFeedback);

            BranchMetricsUtils.logEvent(HomeActivity.this, BRANCH_EVENT_RIDE_COMPLETED, true);
            FbEvents.logEvent(HomeActivity.this, FB_EVENT_RIDE_COMPLETED, true);

            // Ride Completion
            // Google Android in-app conversion tracking snippet
            // Add this code to the event you'd like to track in your app.
            // See code examples and learn how to add advanced features like app deep links at:
            //     https://developers.google.com/app-conversion-tracking/android/#track_in-app_events_driven_by_advertising
            AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),
                    GOOGLE_ADWORD_CONVERSION_ID, "IVSDCMb_umMQlLT2wwM", "0.00", true);


            try {
                JSONObject map = new JSONObject();
                map.put(KEY_FARE_VALUE, ""+Data.endRideData.fare);
                map.put(KEY_FARE_TO_PAY, ""+Data.endRideData.toPay);
                map.put(KEY_PAID_RIDE, ""+(Data.endRideData.toPay >= (0.5d * Data.endRideData.fare) ? 1 : 0));
                NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_RIDE_COMPLETED, map);
            } catch(Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String CALL = "CALL", SMS = "SMS", CALL_100 = "CALL_100";

    public void sosDialog(final Activity activity) {
        if (Data.emergencyContactsList != null) {
            new EmergencyDialog(activity, Data.cEngagementId, new EmergencyDialog.CallBack() {
                @Override
                public void onEnableEmergencyModeClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, EmergencyActivity.class);
                    intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                            EmergencyActivity.EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal());
                    intent.putExtra(Constants.KEY_ENGAGEMENT_ID, Data.cEngagementId);
                    intent.putExtra(Constants.KEY_DRIVER_ID, Data.assignedDriverInfo.userId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(EMERGENCY_MODE_ENABLED);
                    FlurryEventLogger.eventGA(HELP, "help pop up", "enable emergency mode");
                }

                @Override
                public void onEmergencyModeDisabled() {
                    updateTopBar();
                }

                @Override
                public void onSendRideStatusClick(View view) {
                    FlurryEventLogger.eventGA(HELP, "help pop up", "send ride status");
                    Intent intent = new Intent(HomeActivity.this, EmergencyActivity.class);
                    intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                            EmergencyActivity.EmergencyActivityMode.SEND_RIDE_STATUS.getOrdinal());
                    intent.putExtra(Constants.KEY_ENGAGEMENT_ID, Data.cEngagementId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }

                @Override
                public void onInAppCustomerSupportClick(View view) {
                    startActivity(new Intent(HomeActivity.this, SupportActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    FlurryEventLogger.event(SUPPORT_OPTIONS_THROUGH_EMERGENCY);
                    FlurryEventLogger.eventGA(HELP, "help pop up", "in app customer supoort");
                }

                @Override
                public void onDialogClosed(View view) {
                    FlurryEventLogger.event(SOS_ALERT_CANCELLED);
                    FlurryEventLogger.eventGA(HELP, "help pop up", "close");
                }

                @Override
                public void onDialogDismissed() {

                }
            }).show(Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0));
        }
        FlurryEventLogger.event(SOS_ALERT_USED);
    }

    public static int localModeEnabled = -1;
    private void updateTopBar(){
        try{
            float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
            if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                    || PassengerScreenMode.P_RIDE_END == passengerScreenMode){
                int modeEnabled = Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
                if(modeEnabled == 1){
                    topBar.textViewTitle.setText(getResources().getString(R.string.emergency_mode_enabled));
                    topBar.textViewTitle.getPaint().setShader(null);
                    topBar.textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)getResources().getDimensionPixelSize(R.dimen.text_size_30)*minRatio);
                    topBar.textViewTitle.setTextColor(getResources().getColor(R.color.red));
                    topBar.imageViewAppToggle.setVisibility(View.GONE);
                    topBar.imageViewMenu.setImageResource(R.drawable.menu_icon_selector_emergency);
                    localModeEnabled = modeEnabled;
                    return;
                } else{
                    if(localModeEnabled == 1){
                        EmergencyDisableDialog emergencyDisableDialog = new EmergencyDisableDialog(HomeActivity.this);
                        emergencyDisableDialog.show();
                    }
                    topBar.textViewTitle.setText(getResources().getString(R.string.app_name));
                }
                localModeEnabled = modeEnabled;
            } else{
                Prefs.with(this).save(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
                topBar.textViewTitle.setText(getResources().getString(R.string.app_name));

                localModeEnabled = 0;
            }

            topBar.textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) getResources().getDimensionPixelSize(R.dimen.text_size_40) * minRatio);
            topBar.imageViewMenu.setImageResource(R.drawable.ic_menu_selector);
            topBar.textViewTitle.getPaint().setShader(Utils.textColorGradient(this, topBar.textViewTitle));

        } catch(Exception e){
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
                        final PassengerScreenMode passengerScreenModeOld = passengerScreenMode;
                        String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken,
                                currentUserStatus, createApiFindADriver(), Data.pickupLatLng);
                        if (resp.contains(Constants.SERVER_TIMEOUT)) {
                            String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken,
                                    currentUserStatus, createApiFindADriver(), Data.pickupLatLng);
                            if (resp1.contains(Constants.SERVER_TIMEOUT)) {
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
									if(!placeAdded) {
                                        if(PassengerScreenMode.P_IN_RIDE != passengerScreenModeOld
                                            && PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                                            && Data.dropLatLng != null) {
                                            pickupDropZoomed = false;
                                        }
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

    @Override
    public void onShowDialogPush() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openPushDialog();
            }
        });
    }

    private void openPushDialog(){
        dismissPushDialog(false);
        PushDialog dialog = new PushDialog(HomeActivity.this, new PushDialog.Callback() {
            @Override
            public void onButtonClicked(int deepIndex) {
                Data.deepLinkIndex = deepIndex;
                openDeepLink();
            }
        }).show();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(dialog != null){
            pushDialog = dialog;
        }
    }

    private void dismissPushDialog(boolean clearDialogContent){
        if(pushDialog != null){
            pushDialog.dismiss(clearDialogContent);
            pushDialog = null;
        }
    }


    private void fareEstimateForPool(){
        if(Data.dropLatLng != null){
        new ApiFareEstimate(HomeActivity.this, new ApiFareEstimate.Callback() {
            @Override
            public void onSuccess(List<LatLng> list, String startAddress, String endAddress, String distanceText, String timeText, double distanceValue, double timeValue) {
                mapTouched = false;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                PolylineOptions poolPolylineOption = new PolylineOptions();
                poolPolylineOption.width(ASSL.Xscale() * 5).color(Color.BLUE).geodesic(true);
                for (int z = 0; z < list.size(); z++) {
                    poolPolylineOption.add(list.get(z));
                    builder.include(list.get(z));
                }

                final LatLngBounds latLngBounds = builder.build();

                //map.clear();
                poolPolyline = map.addPolyline(poolPolylineOption);

                MarkerOptions poolMarkerOptionStart = new MarkerOptions();
                poolMarkerOptionStart.title("Start");
                poolMarkerOptionStart.position(Data.pickupLatLng);
                poolMarkerOptionStart.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(HomeActivity.this,
                        assl, R.drawable.pin_ball_start)));
                poolMarkerStart = map.addMarker(poolMarkerOptionStart);

                MarkerOptions poolMarkerOptionEnd = new MarkerOptions();
                poolMarkerOptionEnd.title("Start");
                poolMarkerOptionEnd.position(Data.dropLatLng);
                poolMarkerOptionEnd.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(HomeActivity.this,
                        assl, R.drawable.pin_ball_end)));
                //map.addMarker(poolMarkerEnd);
                poolMarkerEnd = map.addMarker(poolMarkerOptionEnd);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(minRatio*40)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onRetry() {

            }

            @Override
            public void onNoRetry() {

            }

            @Override
            public void onFareEstimateSuccess(String minFare, String maxFare, double convenienceCharge) {

            }

            @Override
            public void onPoolSuccess(int fare, int rideDistance, String rideDistanceUnit, int rideTime, String rideTimeUnit, final int poolFareId) {
                Log.v("Pool Fare value is ","--> "+fare);
                // show popup here...
                /*DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "Pool Price", "Your Fare will be "+fare, "Confirm", "Cancel",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jugnooPoolFareId = poolFareId;
                                finalRequestRideTimerStart();
                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                poolPolyline.remove();
                                poolMarkerStart.remove();
                                poolMarkerEnd.remove();
                                initialMyLocationBtn.performClick();
                            }
                        }, true, true);*/

                new PoolFareDialog(HomeActivity.this, new PoolFareDialog.Callback() {
                    @Override
                    public void onDialogDismiss() {
                        poolPolyline.remove();
                        poolMarkerStart.remove();
                        poolMarkerEnd.remove();
                        initialMyLocationBtn.performClick();
                    }

                    @Override
                    public void onConfirmed() {
                        jugnooPoolFareId = poolFareId;
                        finalRequestRideTimerStart();
                    }
                }).showPoolFareDialog(fare);

            }
        }).getDirectionsAndComputeFare(Data.pickupLatLng, Data.dropLatLng, 1);
        } else{
            textViewDestSearch.setText(getResources().getString(R.string.enter_destination));
            textViewDestSearch.setTextColor(getResources().getColor(R.color.red));

            ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
            int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
            if(index == 1 && Data.dropLatLng == null) {
                translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
            }
            if(Data.dropLatLng == null){
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                textViewDestSearch.startAnimation(shake);
            }
        }
    }





    private ApiPaytmCheckBalance apiPaytmCheckBalance = null;
	private void getPaytmBalance(final Activity activity) {
        try {
            if(apiPaytmCheckBalance == null){
				apiPaytmCheckBalance = new ApiPaytmCheckBalance(this, new ApiPaytmCheckBalance.Callback() {
					@Override
					public void onSuccess() {
                        Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                        setUserData();
					}

					@Override
					public void onFailure() {
                        try {
                            JSONParser.setPaytmErrorCase();
                            setUserData();
                            slidingBottomPanel.setPaytmLoadingVisiblity(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        slidingBottomPanel.setPaytmLoadingVisiblity(View.GONE);
                    }

                    @Override
					public void onRetry(View view) {
					}

					@Override
					public void onNoRetry(View view) {
					}
				});
			}
            long lastPaytmBalanceCall = Prefs.with(activity).getLong(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * PAYTM_CHECK_BALANCE_REFRESH_TIME)));
            long lastCallDiff = System.currentTimeMillis() - lastPaytmBalanceCall;
            if(lastCallDiff >= PAYTM_CHECK_BALANCE_REFRESH_TIME) {
                apiPaytmCheckBalance.getBalance(Data.userData.paytmEnabled, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadContactsApi(final boolean fromLogin){
        HashMap<String, String> params = new HashMap<>();
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(this, "Loading...");
            params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
            if(fromLogin){
                params.put(KEY_ENGAGEMENT_ID, "");
                params.put(KEY_USER_RESPONSE, "-2");
                params.put(KEY_IS_LOGIN_POPUP, "1");
            } else{
                params.put(KEY_ENGAGEMENT_ID, Data.cEngagementId);
                params.put(KEY_USER_RESPONSE, "-1");
            }
            Log.i("access_token and session_id", Data.userData.accessToken + ", " + Data.cSessionId + ", " + Data.cEngagementId);

            Log.i("params request_dup_registration", "=" + params);

            RestClient.getApiServices().referAllContacts(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "referAllContacts response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.getInt("flag");
                        String message = JSONParser.getServerMessage(jObj);
                        Log.e("message=", "=" + message);
                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                            if (!fromLogin) {
                                Data.userData.contactSaved = -1;
                            }
                        } else {
                            //Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        //DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    DialogPopup.dismissLoadingDialog();
                    Log.e(TAG, "referAllContacts error=" + error.toString());
                    //Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                }
            });
        }
    }



    private void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {
        try {
            final HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("engagement_id", engagementId);

            RestClient.getApiServices().skipRatingByCustomer(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    Log.i(TAG, "skipRatingByCustomer response = " + response);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "skipRatingByCustomer error="+error.toString());
                }
            });

            Database2.getInstance(activity).insertPendingAPICall(activity,
                    PendingCall.SKIP_RATING_BY_CUSTOMER.getPath(), params);

            try { Data.driverInfos.clear(); } catch (Exception e) { e.printStackTrace(); }

            HomeActivity.feedbackSkipped = true;
            afterRideFeedbackSubmitted(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void submitFeedbackToDriverAsync(final Activity activity, final String engagementId, final String ratingReceiverId,
                                            final int givenRating, final String feedbackText, final String feedbackReasons) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

                params.put("access_token", Data.userData.accessToken);
                params.put(KEY_GIVEN_RATING, "" + givenRating);
                params.put("engagement_id", engagementId);
                params.put("driver_id", ratingReceiverId);
                params.put("feedback", feedbackText);
                params.put("feedback_reasons", feedbackReasons);

                Log.i("params", "=" + params);

                RestClient.getApiServices().rateTheDriver(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "rateTheDriver response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
                                    try { Data.driverInfos.clear(); } catch (Exception e) { e.printStackTrace(); }
                                    afterRideFeedbackSubmitted(givenRating, false);
                                    try {
                                        JSONObject map = new JSONObject();
                                        map.put(KEY_ENGAGEMENT_ID, engagementId);
                                        map.put(KEY_GIVEN_RATING, givenRating);
                                        NudgeClient.trackEventUserId(HomeActivity.this, NUDGE_FEEDBACK, map);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        Data.driverInfos.clear();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "rateTheDriver error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            } else {
                DialogPopup.dialogNoInternet(HomeActivity.this,
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                        new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                submitFeedbackToDriverAsync(activity, engagementId, ratingReceiverId,
                                        givenRating, feedbackText, feedbackReasons);
                            }

                            @Override
                            public void neutralClick(View v) {

                            }

                            @Override
                            public void negativeClick(View v) {

                            }
                        });
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
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                || (PassengerScreenMode.P_INITIAL == passengerScreenMode
                && placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP))
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
            if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                FlurryEventLogger.event(PICKUP_LOCATION_SET);
                textViewInitialSearch.setText(autoCompleteSearchResult.name);
                zoomedForSearch = true;
                lastSearchLatLng = null;
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
            }
            else if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP){
                textViewDestSearch.setText(autoCompleteSearchResult.name);
            }
        }
        else if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
            textViewAssigningDropLocationClick.setText(autoCompleteSearchResult.name);
        }
        else if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
            textViewFinalDropLocationClick.setText(autoCompleteSearchResult.name);
        }
    }

    @Override
    public void onPlaceSearchPre() {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode){
            if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                progressBarInitialSearch.setVisibility(View.VISIBLE);
            }
        }

        Log.e("onPlaceSearchPre", "=");
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult) {

        if(PassengerScreenMode.P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
            if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                progressBarInitialSearch.setVisibility(View.GONE);
                if (map != null && searchResult != null) {
                    textViewInitialSearch.setText(searchResult.name);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, MAX_ZOOM), MAP_ANIMATE_DURATION, null);
                    lastSearchLatLng = searchResult.latLng;
                    mapTouched = true;

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
            } else if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP){
                textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color));
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);

                if(Data.dropLatLng == null){
                    translateViewBottom(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutDestSearchBar, true, false);
                    translateViewTop(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutInitialSearchBar, false, false);
                }
                Data.dropLatLng = searchResult.latLng;
                dropLocationSet = true;
                relativeLayoutInitialSearchBar.setBackgroundResource(R.drawable.dropshadow_in_white);
                imageViewDropCross.setVisibility(View.VISIBLE);


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
            if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP){
                textViewDestSearch.setText("");
            }
        }
        else if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
            textViewAssigningDropLocationClick.setText("");
        }
        else if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
            textViewFinalDropLocationClick.setText("");
        }

        Log.e("onPlaceSearchError", "=");
    }

    @Override
    public void onPlaceSaved() {
        placeAdded = true;
    }

    @Override
    public void onPaytmRechargePush(final JSONObject jObj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Data.userData.setPaytmRechargeInfo(JSONParser.parsePaytmRechargeInfo(jObj));
                    openPaytmRechargeDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openPaytmRechargeDialog(){
        try {
            if(dialogPaytmRecharge != null && dialogPaytmRecharge.isShowing()){
                dialogPaytmRecharge.dismiss();
            }
            if(Data.userData.getPaytmRechargeInfo() != null) {
                dialogPaytmRecharge = new PaytmRechargeDialog(HomeActivity.this,
                        Data.userData.getPaytmRechargeInfo().getTransferId(),
                        Data.userData.getPaytmRechargeInfo().getTransferSenderName(),
                        Data.userData.getPaytmRechargeInfo().getTransferPhone(),
                        Data.userData.getPaytmRechargeInfo().getTransferAmount(),
                        new PaytmRechargeDialog.Callback() {
                            @Override
                            public void onOk() {
                                if(Data.userData != null) {
                                    Data.userData.setPaytmRechargeInfo(null);
                                    Prefs.with(HomeActivity.this).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME,
                                            (System.currentTimeMillis() - (2 * PAYTM_CHECK_BALANCE_REFRESH_TIME)));
                                    getPaytmBalance(HomeActivity.this);
                                }
                            }

                            @Override
                            public void onCancel() {
                                if(Data.userData != null) {
                                    Data.userData.setPaytmRechargeInfo(null);
                                }
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Animation bounceScale;
    private Animation getBounceScale(){
        if(bounceScale == null){
            bounceScale = AnimationUtils.loadAnimation(this, R.anim.bounce_scale);
        }
        return bounceScale;
    }

    public void setVehicleTypeSelected(int position) {
        int oldVehicleType = slidingBottomPanel.getRegionSelected().getVehicleType();
        int oldRegionId = slidingBottomPanel.getRegionSelected().getRegionId();
        slidingBottomPanel.setRegionSelected(position);
        if(!slidingBottomPanel.getRegionSelected().getVehicleType().equals(oldVehicleType)
                || !slidingBottomPanel.getRegionSelected().getRegionId().equals(oldRegionId)) {
            imageViewRideNow.setImageDrawable(slidingBottomPanel.getRegionSelected()
                    .getVehicleIconSet().getRequestSelector(this));
            imageViewRideNow.startAnimation(getBounceScale());
            showDriverMarkersAndPanMap(Data.pickupLatLng, slidingBottomPanel.getRegionSelected());
        } else if(Data.regions.size() == 1) {
            imageViewRideNow.setImageDrawable(slidingBottomPanel.getRegionSelected()
                    .getVehicleIconSet().getRequestSelector(this));
        }
    }

    public void forceFarAwayCity(){
        try {
            Data.farAwayCity = getResources().getString(R.string.service_not_available);
            setServiceAvailablityUI(Data.farAwayCity);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public LatLng getCurrentPlaceLatLng(){
        if(map != null){
            return map.getCameraPosition().target;
        }
        return null;
    }



    private ApiCampaignAvailRequest apiCampaignAvailRequest;
    private ApiCampaignAvailRequest getApiCampaignAvailRequest(){
        if(apiCampaignAvailRequest == null){
            apiCampaignAvailRequest = new ApiCampaignAvailRequest(this, new ApiCampaignAvailRequest.Callback() {
                @Override
                public void onPre() {
                    try {
                        relativeLayoutRequest.setVisibility(View.GONE);
                        relativeLayoutInAppCampaignRequest.setVisibility(View.VISIBLE);
                        if(Data.campaigns.getMapLeftButton() != null){
							textViewInAppCampaignRequest.setText(Data.campaigns.getMapLeftButton().getText());
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int flag, String message, String image, int width, int height) {
                    try {
                        if(campaignApiCancelled || "".equalsIgnoreCase(image)){
                            backFromCampaignAvailLoading();
						} else {
							float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
							Picasso.with(HomeActivity.this).load(image)
									.resize((int) (minRatio * 0.9f * (float) width), (int) (minRatio * 0.9f * (float) height))
									.centerCrop()
                                    .into(getTargetAvailCampaign(flag, message));
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                        backFromCampaignAvailLoading();
                    }
                }

                @Override
                public void onFailure() {
                    backFromCampaignAvailLoading();
                }

                @Override
                public void onRetry(View view) {
                    callCampaignAvailRequest();
                }

                @Override
                public void onNoRetry(View view) {
                    backFromCampaignAvailLoading();
                }
            });
        }
        return apiCampaignAvailRequest;
    }

    private Target targetAvailCampaign;
    private String messageAvailCampaign;
    private int flagAvailCampaign;
    private Target getTargetAvailCampaign(int flagAvailCampaign, String messageAvailCampaign){
        this.messageAvailCampaign = messageAvailCampaign;
        this.flagAvailCampaign = flagAvailCampaign;
        if(targetAvailCampaign == null){
            targetAvailCampaign = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    try {
                        backFromCampaignAvailLoading();
                        if(!campaignApiCancelled){
							if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == HomeActivity.this.flagAvailCampaign) {
								setCampaignAvailed();
							}
							new InAppCampaignDialog(HomeActivity.this, new InAppCampaignDialog.Callback() {
								@Override
								public void onDialogDismiss() {

								}
							}).show(HomeActivity.this.messageAvailCampaign, bitmap);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable drawable) {
                    backFromCampaignAvailLoading();
                }

                @Override
                public void onPrepareLoad(Drawable drawable) {

                }
            };
        }
        return targetAvailCampaign;
    }

    private void callCampaignAvailRequest(){
        try {
            if(Data.campaigns != null && Data.campaigns.getMapLeftButton() != null) {
				campaignApiCancelled = false;
				getApiCampaignAvailRequest().availCampaign(map.getCameraPosition().target,
						Data.campaigns.getMapLeftButton().getCampaignId());
			} else{
				Toast.makeText(this, getString(R.string.no_campaign_currently), Toast.LENGTH_SHORT).show();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCampaignAvailed(){
        try{
            if(Data.campaigns.getMapLeftButton().getShowCampaignAfterAvail() == 0){
                Data.campaigns = null;
                setupInAppCampaignUI();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private boolean campaignApiCancelled = false;
    private ApiCampaignRequestCancel apiCampaignRequestCancel;
    private ApiCampaignRequestCancel getApiCampaignRequestCancel(){
        if(apiCampaignRequestCancel == null){
            apiCampaignRequestCancel = new ApiCampaignRequestCancel(this, new ApiCampaignRequestCancel.Callback() {
                @Override
                public void onSuccess() {
                    campaignApiCancelled = true;
                    backFromCampaignAvailLoading();
                }

                @Override
                public void onFailure() {
                    backFromCampaignAvailLoading();
                }

                @Override
                public void onRetry(View view) {
                    callApiCampaignRequestCancel();
                }

                @Override
                public void onNoRetry(View view) {
                    backFromCampaignAvailLoading();
                }
            });
        }
        return apiCampaignRequestCancel;
    }

    private void callApiCampaignRequestCancel(){
        try {
            if(Data.campaigns != null && Data.campaigns.getMapLeftButton() != null) {
				getApiCampaignRequestCancel().cancelCampaignRequest(Data.campaigns.getMapLeftButton().getCampaignId());
			} else{
				Toast.makeText(this, getString(R.string.no_campaign_currently), Toast.LENGTH_SHORT).show();
				backFromCampaignAvailLoading();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backFromCampaignAvailLoading(){
        relativeLayoutRequest.setVisibility(View.VISIBLE);
        relativeLayoutInAppCampaignRequest.setVisibility(View.GONE);
    }


    private long supplyTypeAnimTime = 1000;
    private void animateSupplyTypeImage(){
        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
        Animation translate = new TranslateAnimation(0,
                (((float)getResources().getDisplayMetrics().widthPixels)/2f - ((187f * minRatio)/2f) - (45f * minRatio)) * 0.66f, 0, 0);
        translate.setDuration(supplyTypeAnimTime);
        translate.setFillAfter(false);

        Animation alpha = new AlphaAnimation(0.0f, 0.8f);
        alpha.setDuration(supplyTypeAnimTime);
        alpha.setFillAfter(false);

        Animation scale = new ScaleAnimation(1.0f, 1.8f, 1.0f, 1.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(supplyTypeAnimTime);
        scale.setFillAfter(false);



        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translate);
        animationSet.addAnimation(scale);
        animationSet.addAnimation(alpha);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewSupplyType.clearAnimation();
        imageViewSupplyType.startAnimation(animationSet);

    }


}