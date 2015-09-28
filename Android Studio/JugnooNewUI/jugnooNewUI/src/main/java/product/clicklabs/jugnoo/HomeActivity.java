package product.clicklabs.jugnoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.branch.referral.Branch;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.adapters.PromotionListEventHandler;
import product.clicklabs.jugnoo.adapters.PromotionsListAdapter;
import product.clicklabs.jugnoo.adapters.SearchListActionsHandler;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import rmn.androidscreenlibrary.ASSL;

@SuppressLint("DefaultLocale")
public class HomeActivity extends BaseFragmentActivity implements AppInterruptHandler, LocationUpdate, FlurryEventNames {


    DrawerLayout drawerLayout;                                                                        // views declaration


    //menu bar 
    LinearLayout menuLayout;


    LinearLayout linearLayoutProfile;
    ImageView imageViewProfile;
    TextView textViewUserName, textViewViewAccount;

    RelativeLayout relativeLayoutGetRide;
    TextView textViewGetRide;

    RelativeLayout relativeLayoutInvite;
    TextView textViewInvite;

    RelativeLayout relativeLayoutJugnooCash;
    TextView textViewJugnooCash, textViewJugnooCashValue;

    RelativeLayout relativeLayoutPromotions;
    TextView textViewPromotions, textViewPromotionsValue;

    RelativeLayout relativeLayoutTransactions;
    TextView textViewTransactions;

    RelativeLayout relativeLayoutFareDetails;
    TextView textViewFareDetails;

    RelativeLayout relativeLayoutSupport;
    TextView textViewSupport;

    RelativeLayout relativeLayoutAbout;
    TextView textViewAbout;


    //Top RL
    RelativeLayout topRl;
    ImageView imageViewMenu, imageViewSearchCancel, imageViewBack;
    TextView title;
    Button checkServerBtn;
    ImageView jugnooShopImageView;
    ImageView imageViewGift, imageViewHelp;


    //Passenger main layout
    RelativeLayout passengerMainLayout;


    //Map layout
    RelativeLayout mapLayout;
    GoogleMap map;
    TouchableMapFragment mapFragment;


    //Initial layout
    RelativeLayout initialLayout;
    TextView textViewNearestDriverETA;
    RelativeLayout relativeLayoutInitialFareFactor;
    TextView textViewCurrentFareFactor;
    Button initialMyLocationBtn, initialMyLocationBtnChangeLoc, changeLocalityBtn, buttonChalo;
    LinearLayout linearLayoutPromo, linearLayoutCouponList, linearLayoutPromoShadow;
    ListView listViewPromotions;
    PromotionsListAdapter promotionsListAdapter;
    LinearLayout linearLayoutFareEstimate, linearLayoutRateCard;

    ImageView imageViewRideNow;

    RelativeLayout relativeLayoutInitialSearchBar;
    TextView textViewInitialSearch;
    ProgressBar progressBarInitialSearch;
    RelativeLayout relativeLayoutRequestInfo;


	//Location Error layout
	RelativeLayout relativeLayoutLocationError;
	RelativeLayout relativeLayoutLocationErrorSearchBar;




    //Assigining layout
    RelativeLayout assigningLayout;
    TextView textViewFindingDriver;
	SmoothProgressBar progressBarFindingDriver;
    Button assigningMyLocationBtn, initialCancelRideBtn;
    RelativeLayout relativeLayoutAssigningDropLocationParent, relativeLayoutAssigningDropLocationBar;
    EditText editTextAssigningDropLocation;
    ProgressBar progressBarAssigningDropLocation;
    NonScrollListView listViewAssigningDropLocationSearch;
    ScrollView scrollViewAssigning;
    LinearLayout linearLayoutScrollAssigning;
    TextView textViewScrollAssigning;
	boolean cancelTouchHold = false;


    //Request Final Layout
    RelativeLayout requestFinalLayout;
    RelativeLayout relativeLayoutInRideInfo;
    TextView textViewInRidePromoName, textViewInRideFareFactor;
    Button customerInRideMyLocationBtn;
    ImageView imageViewInRideDriver, imageViewInRideDriverCar;
    TextView textViewInRideDriverName, textViewInRideDriverCarNumber, textViewInRideState, textViewInRideLowJugnooCash;
    Button buttonCancelRide, buttonAddJugnooCash, buttonCallDriver;
    RelativeLayout relativeLayoutFinalDropLocationParent, relativeLayoutFinalDropLocationBar;
    EditText editTextFinalDropLocation;
    ProgressBar progressBarFinalDropLocation;
    NonScrollListView listViewFinalDropLocationSearch;
    ScrollView scrollViewFinal;
    LinearLayout linearLayoutScrollFinal;
    TextView textViewScrollFinal;



    //Search Layout
    LinearLayout linearLayoutSearch;
    EditText editTextSearch;
    ProgressBar progressBarSearch;
    NonScrollListView listViewSearch;
    LinearLayout linearLayoutScrollSearch;
    TextView textViewScrollSearch;


    //Center Location Layout
    RelativeLayout centreLocationRl;
    ImageView centreLocationPin;


    //End Ride layout
    RelativeLayout endRideReviewRl;
    ScrollView scrollViewEndRide;

    TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
	RelativeLayout relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge;
	LinearLayout linearLayoutEndRideTime, linearLayoutEndRideWaitTime;
	NonScrollListView listViewEndRideDiscounts;
    TextView textViewEndRideFareValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
			textViewEndRideDiscount, textViewEndRideDiscountRupee, textViewEndRideDiscountValue,
			textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRideToBePaidValue, textViewEndRideBaseFareValue,
			textViewEndRideDistanceValue, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue;
	TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
    Button buttonEndRideOk;
	EndRideDiscountsAdapter endRideDiscountsAdapter;









    // data variables declaration

    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");

    LatLng lastSearchLatLng;

    static double totalDistance = -1;


    static long previousWaitTime = 0, previousRideTime = 0;


    static Location myLocation;


    static UserMode userMode;
    static PassengerScreenMode passengerScreenMode;


    FindDriversETAAsync findDriversETAAsync;


    Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker, dropLocationMarker;
    Polyline pathToDropLocationPolyline;
    PolylineOptions pathToDropLocationPolylineOptions;

    static AppInterruptHandler appInterruptHandler;

    static Activity activity;

    boolean loggedOut = false,
        zoomedToMyLocation = false,
        mapTouchedOnce = false;
    boolean dontCallRefreshDriver = false, zoomedForSearch = false, pickupDropZoomed = false, firstTimeZoom = false;


    Dialog noDriversDialog;

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

    public static final float MAX_ZOOM = 15;



    public CheckForGPSAccuracyTimer checkForGPSAccuracyTimer;


    public boolean activityResumed = false;
    public static boolean rechargedOnce = false, feedbackSkipped = false;

    public ASSL assl;


    private int showAllDrivers = 0, showDriverInfo = 0;

    private boolean intentFired = false, dropLocationSearched = false, promoOpened = false;

    GenieLayout genieLayout;

	private GAPIAddress gapiAddressForPin;


    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        FacebookSdk.sdkInitialize(this);

        callbackManager = CallbackManager.Factory.create();

        ReferralActions.incrementAppOpen(this);

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




        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);


        //Swipe menu
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);


        linearLayoutProfile = (LinearLayout) findViewById(R.id.linearLayoutProfile);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        textViewViewAccount = (TextView) findViewById(R.id.textViewViewAccount);
        textViewViewAccount.setTypeface(Fonts.latoLight(this), Typeface.BOLD);

        relativeLayoutGetRide = (RelativeLayout) findViewById(R.id.relativeLayoutGetRide);
        textViewGetRide = (TextView) findViewById(R.id.textViewGetRide);
        textViewGetRide.setTypeface(Fonts.latoRegular(this));

        relativeLayoutInvite = (RelativeLayout) findViewById(R.id.relativeLayoutInvite);
        textViewInvite = (TextView) findViewById(R.id.textViewInvite);
        textViewInvite.setTypeface(Fonts.latoRegular(this));

        relativeLayoutJugnooCash = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCash);
        textViewJugnooCash = (TextView) findViewById(R.id.textViewJugnooCash);
        textViewJugnooCash.setTypeface(Fonts.latoRegular(this));
        textViewJugnooCashValue = (TextView) findViewById(R.id.textViewJugnooCashValue);
        textViewJugnooCashValue.setTypeface(Fonts.latoRegular(this));

        relativeLayoutPromotions = (RelativeLayout) findViewById(R.id.relativeLayoutPromotions);
        textViewPromotions = (TextView) findViewById(R.id.textViewPromotions);
        textViewPromotions.setTypeface(Fonts.latoRegular(this));
        textViewPromotionsValue = (TextView) findViewById(R.id.textViewPromotionsValue);
        textViewPromotionsValue.setTypeface(Fonts.latoRegular(this));

        relativeLayoutTransactions = (RelativeLayout) findViewById(R.id.relativeLayoutTransactions);
        textViewTransactions = (TextView) findViewById(R.id.textViewTransactions);
        textViewTransactions.setTypeface(Fonts.latoRegular(this));

        relativeLayoutFareDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareDetails);
        textViewFareDetails = (TextView) findViewById(R.id.textViewFareDetails);
        textViewFareDetails.setTypeface(Fonts.latoRegular(this));

        relativeLayoutSupport = (RelativeLayout) findViewById(R.id.relativeLayoutSupport);
        textViewSupport = (TextView) findViewById(R.id.textViewSupport);
        textViewSupport.setTypeface(Fonts.latoRegular(this));

        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutAbout);
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);
        textViewAbout.setTypeface(Fonts.latoRegular(this));


        //Top RL
        topRl = (RelativeLayout) findViewById(R.id.topRl);
        imageViewMenu = (ImageView) findViewById(R.id.imageViewMenu);
        imageViewSearchCancel = (ImageView) findViewById(R.id.imageViewSearchCancel);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
        imageViewGift = (ImageView) findViewById(R.id.imageViewGift);
        imageViewHelp = (ImageView) findViewById(R.id.imageViewHelp);
        jugnooShopImageView = (ImageView) findViewById(R.id.jugnooShopImageView);


        //Map Layout
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));


        //Passenger main layout
        passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);


        //Initial layout 
        initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
        textViewNearestDriverETA = (TextView) findViewById(R.id.textViewNearestDriverETA);
        textViewNearestDriverETA.setTypeface(Fonts.latoRegular(this));

        relativeLayoutInitialFareFactor = (RelativeLayout) findViewById(R.id.relativeLayoutInitialFareFactor);
        textViewCurrentFareFactor = (TextView) findViewById(R.id.textViewCurrentFareFactor);
        textViewCurrentFareFactor.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewCurrentRatesInfo)).setTypeface(Fonts.latoRegular(this));

        initialMyLocationBtn = (Button) findViewById(R.id.initialMyLocationBtn);
        initialMyLocationBtnChangeLoc = (Button) findViewById(R.id.initialMyLocationBtnChangeLoc);
        changeLocalityBtn = (Button) findViewById(R.id.changeLocalityBtn);
        changeLocalityBtn.setTypeface(Fonts.latoRegular(this));

        initialMyLocationBtn.setVisibility(View.VISIBLE);
        changeLocalityBtn.setVisibility(View.GONE);
        initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

        imageViewRideNow = (ImageView) findViewById(R.id.imageViewRideNow);

        relativeLayoutRequestInfo = (RelativeLayout) findViewById(R.id.relativeLayoutRequestInfo);


        relativeLayoutInitialSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutInitialSearchBar);
        textViewInitialSearch = (TextView) findViewById(R.id.textViewInitialSearch);
        textViewInitialSearch.setTypeface(Fonts.latoRegular(this));
        progressBarInitialSearch = (ProgressBar) findViewById(R.id.progressBarInitialSearch);
        progressBarInitialSearch.setVisibility(View.GONE);

        buttonChalo = (Button) findViewById(R.id.buttonChalo); buttonChalo.setTypeface(Fonts.latoRegular(this));
        linearLayoutPromo = (LinearLayout) findViewById(R.id.linearLayoutPromo); linearLayoutPromo.setVisibility(View.GONE);
        linearLayoutCouponList = (LinearLayout) findViewById(R.id.linearLayoutCouponList);
        linearLayoutPromoShadow = (LinearLayout) findViewById(R.id.linearLayoutPromoShadow);
        ((TextView) findViewById(R.id.textViewPromoChooseCoupon)).setTypeface(Fonts.latoRegular(this));
        listViewPromotions = (ListView) findViewById(R.id.listViewPromotions);

        promotionsListAdapter = new PromotionsListAdapter(this, new PromotionListEventHandler() {
            @Override
            public void onDismiss() {
                if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
                    passengerScreenMode = PassengerScreenMode.P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                    Utils.hideSoftKeyboard(HomeActivity.this, editTextFinalDropLocation);
                }
            }

            @Override
            public void onPromoListFetched(int totalPromoCoupons) {
                promoOpened = true;
                imageViewMenu.setVisibility(View.GONE);
                imageViewBack.setVisibility(View.VISIBLE);
                genieLayout.setVisibility(View.GONE);
                centreLocationRl.setVisibility(View.VISIBLE);
                linearLayoutPromo.setVisibility(View.VISIBLE);

                if(totalPromoCoupons > 0){
                    linearLayoutCouponList.setVisibility(View.VISIBLE);
					if(totalPromoCoupons <= 2){
						Utils.expandListForVariableHeight(listViewPromotions);

						LinearLayout.LayoutParams layoutParamsList = (LinearLayout.LayoutParams) listViewPromotions.getLayoutParams();
						LinearLayout.LayoutParams layoutParamsLinear = (LinearLayout.LayoutParams) linearLayoutCouponList.getLayoutParams();
						layoutParamsLinear.height = (int) ((ASSL.Yscale() * 370.0f) - ((ASSL.Yscale() * 270.0f) - layoutParamsList.height));
						linearLayoutCouponList.setLayoutParams(layoutParamsLinear);
					}
					else{
						LinearLayout.LayoutParams layoutParamsList = (LinearLayout.LayoutParams) listViewPromotions.getLayoutParams();
						layoutParamsList.height = (int) (ASSL.Yscale() * 270.0f);
						listViewPromotions.setLayoutParams(layoutParamsList);

						LinearLayout.LayoutParams layoutParamsLinear = (LinearLayout.LayoutParams) linearLayoutCouponList.getLayoutParams();
						layoutParamsLinear.height = (int) (ASSL.Yscale() * 370.0f);
						linearLayoutCouponList.setLayoutParams(layoutParamsLinear);
					}
                }
                else{
                    linearLayoutCouponList.setVisibility(View.GONE);
                }
            }
		});
        listViewPromotions.setAdapter(promotionsListAdapter);

        linearLayoutFareEstimate = (LinearLayout) findViewById(R.id.linearLayoutFareEstimate);
        linearLayoutRateCard = (LinearLayout) findViewById(R.id.linearLayoutRateCard);
        ((TextView) findViewById(R.id.textViewFareEstimate)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewRateCard)).setTypeface(Fonts.latoRegular(this));





		//Location error layout
		relativeLayoutLocationError = (RelativeLayout) findViewById(R.id.relativeLayoutLocationError);
		relativeLayoutLocationErrorSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutLocationErrorSearchBar);
		((TextView)findViewById(R.id.textViewLocationErrorSearch)).setTypeface(Fonts.latoRegular(this));
		relativeLayoutLocationError.setVisibility(View.GONE);






        //Assigning layout
        assigningLayout = (RelativeLayout) findViewById(R.id.assigningLayout);
        textViewFindingDriver = (TextView) findViewById(R.id.textViewFindingDriver);
        textViewFindingDriver.setTypeface(Fonts.latoRegular(this));
		progressBarFindingDriver = (SmoothProgressBar) findViewById(R.id.progressBarFindingDriver);
        assigningMyLocationBtn = (Button) findViewById(R.id.assigningMyLocationBtn);
        initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn);
        initialCancelRideBtn.setTypeface(Fonts.latoRegular(this));

        relativeLayoutAssigningDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationParent);
        relativeLayoutAssigningDropLocationParent.setBackgroundColor(getResources().getColor(R.color.transparent));
        relativeLayoutAssigningDropLocationBar = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationBar);
        editTextAssigningDropLocation = (EditText) findViewById(R.id.editTextAssigningDropLocation);
        editTextAssigningDropLocation.setTypeface(Fonts.latoRegular(this));
        progressBarAssigningDropLocation = (ProgressBar) findViewById(R.id.progressBarAssigningDropLocation); progressBarAssigningDropLocation.setVisibility(View.GONE);
        listViewAssigningDropLocationSearch = (NonScrollListView) findViewById(R.id.listViewAssigningDropLocationSearch);
        scrollViewAssigning = (ScrollView) findViewById(R.id.scrollViewAssigning);
        scrollViewAssigning.setVisibility(View.GONE);
        linearLayoutScrollAssigning = (LinearLayout) findViewById(R.id.linearLayoutScrollAssigning);
        textViewScrollAssigning = (TextView) findViewById(R.id.textViewScrollAssigning);
        linearLayoutScrollAssigning.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutScrollAssigning, textViewScrollAssigning, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {

            }

            @Override
            public void keyBoardClosed() {

            }
        }));

        SearchListAdapter dropLocationAssigningSearchListAdapter = new SearchListAdapter(this, editTextAssigningDropLocation, new LatLng(30.75, 76.78),
            new SearchListActionsHandler() {
                @Override
                public void onSearchPre() {
                    progressBarAssigningDropLocation.setVisibility(View.VISIBLE);
                    if(scrollViewAssigning.getVisibility() == View.GONE) {
                        initDropLocationSearchUI(false);
                    }
                }

                @Override
                public void onSearchPost() {
                    progressBarAssigningDropLocation.setVisibility(View.GONE);
                }

                @Override
                public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
                }

                @Override
                public void onPlaceSearchPre() {
                    DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                }

                @Override
                public void onPlaceSearchPost(SearchResult searchResult) {
                    DialogPopup.dismissLoadingDialog();
                    sendDropLocationAPI(HomeActivity.this, searchResult.latLng);
                    FlurryEventLogger.event(DROP_LOCATION_USED_FINIDING_DRIVER);
                }

                @Override
                public void onPlaceSearchError() {
                    DialogPopup.dismissLoadingDialog();
                }
            });

        listViewAssigningDropLocationSearch.setAdapter(dropLocationAssigningSearchListAdapter);



        //Request Final Layout
        requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);

        relativeLayoutInRideInfo = (RelativeLayout) findViewById(R.id.relativeLayoutInRideInfo);
        textViewInRidePromoName = (TextView) findViewById(R.id.textViewInRidePromoName);
        textViewInRidePromoName.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
        textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor);
        textViewInRideFareFactor.setTypeface(Fonts.latoRegular(this));

        customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);

        imageViewInRideDriver = (ImageView) findViewById(R.id.imageViewInRideDriver);
        imageViewInRideDriverCar = (ImageView) findViewById(R.id.imageViewInRideDriverCar);

        textViewInRideDriverName = (TextView) findViewById(R.id.textViewInRideDriverName);
        textViewInRideDriverName.setTypeface(Fonts.latoRegular(this));
        textViewInRideDriverCarNumber = (TextView) findViewById(R.id.textViewInRideDriverCarNumber);
        textViewInRideDriverCarNumber.setTypeface(Fonts.latoRegular(this));
        textViewInRideState = (TextView) findViewById(R.id.textViewInRideState);
        textViewInRideState.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
        textViewInRideLowJugnooCash = (TextView) findViewById(R.id.textViewInRideLowJugnooCash);
        textViewInRideLowJugnooCash.setTypeface(Fonts.latoRegular(this));
        textViewInRideLowJugnooCash.setVisibility(View.GONE);

        buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
        buttonCancelRide.setTypeface(Fonts.latoRegular(this));
        buttonAddJugnooCash = (Button) findViewById(R.id.buttonAddJugnooCash);
        buttonAddJugnooCash.setTypeface(Fonts.latoRegular(this));
        buttonCallDriver = (Button) findViewById(R.id.buttonCallDriver);
        buttonCallDriver.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

        relativeLayoutFinalDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationParent);
        relativeLayoutFinalDropLocationParent.setBackgroundColor(getResources().getColor(R.color.transparent));
        relativeLayoutFinalDropLocationBar = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationBar);
        editTextFinalDropLocation = (EditText) findViewById(R.id.editTextFinalDropLocation);
        editTextFinalDropLocation.setTypeface(Fonts.latoRegular(this));
        progressBarFinalDropLocation = (ProgressBar) findViewById(R.id.progressBarFinalDropLocation); progressBarFinalDropLocation.setVisibility(View.GONE);
        listViewFinalDropLocationSearch = (NonScrollListView) findViewById(R.id.listViewFinalDropLocationSearch);
        scrollViewFinal = (ScrollView) findViewById(R.id.scrollViewFinal);
        scrollViewFinal.setVisibility(View.GONE);
        linearLayoutScrollFinal = (LinearLayout) findViewById(R.id.linearLayoutScrollFinal);
        textViewScrollFinal = (TextView) findViewById(R.id.textViewScrollFinal);
        linearLayoutScrollFinal.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutScrollFinal, textViewScrollFinal, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {

            }

            @Override
            public void keyBoardClosed() {

            }
        }));


        SearchListAdapter dropLocationFinalSearchListAdapter = new SearchListAdapter(this, editTextFinalDropLocation, new LatLng(30.75, 76.78),
            new SearchListActionsHandler() {
                @Override
                public void onSearchPre() {
                    progressBarFinalDropLocation.setVisibility(View.VISIBLE);
                    if(scrollViewFinal.getVisibility() == View.GONE) {
                        initDropLocationSearchUI(true);
                    }
                }

                @Override
                public void onSearchPost() {
                    progressBarFinalDropLocation.setVisibility(View.GONE);
                }

                @Override
                public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
                }

                @Override
                public void onPlaceSearchPre() {
                    DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
                }

                @Override
                public void onPlaceSearchPost(SearchResult searchResult) {
                    DialogPopup.dismissLoadingDialog();
                    sendDropLocationAPI(HomeActivity.this, searchResult.latLng);
                    FlurryEventLogger.event(DROP_LOCATION_USED_RIDE_ACCEPTED);
                }

                @Override
                public void onPlaceSearchError() {
                    DialogPopup.dismissLoadingDialog();
                }
            });
        listViewFinalDropLocationSearch.setAdapter(dropLocationFinalSearchListAdapter);



        //Search Layout
        linearLayoutSearch = (LinearLayout) findViewById(R.id.linearLayoutSearch);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.setTypeface(Fonts.latoRegular(this));
        progressBarSearch = (ProgressBar) findViewById(R.id.progressBarSearch);
        progressBarSearch.setVisibility(View.GONE);
        listViewSearch = (NonScrollListView) findViewById(R.id.listViewSearch);
        linearLayoutScrollSearch = (LinearLayout) findViewById(R.id.linearLayoutScrollSearch);
        textViewScrollSearch = (TextView) findViewById(R.id.textViewScrollSearch);
        linearLayoutScrollSearch.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutScrollSearch, textViewScrollSearch, new KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {

			}

			@Override
			public void keyBoardClosed() {

			}
		}));

        SearchListAdapter searchListAdapter = new SearchListAdapter(this, editTextSearch, new LatLng(30.75, 76.78),
            new SearchListActionsHandler() {
                @Override
                public void onSearchPre() {
                    progressBarSearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSearchPost() {
                    progressBarSearch.setVisibility(View.GONE);
                }

                @Override
                public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
                    FlurryEventLogger.event(PICKUP_LOCATION_SET);
                    textViewInitialSearch.setText(autoCompleteSearchResult.name);
                    zoomedForSearch = true;
                    lastSearchLatLng = null;
                    passengerScreenMode = PassengerScreenMode.P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                }

                @Override
                public void onPlaceSearchPre() {
                    progressBarInitialSearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPlaceSearchPost(SearchResult searchResult) {
                    progressBarInitialSearch.setVisibility(View.GONE);
                    if (map != null && searchResult != null) {
                        textViewInitialSearch.setText(searchResult.name);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, MAX_ZOOM), 1000, null);
                        lastSearchLatLng = searchResult.latLng;
                    }
                }

                @Override
                public void onPlaceSearchError() {
                    progressBarInitialSearch.setVisibility(View.GONE);
                }
            });

        listViewSearch.setAdapter(searchListAdapter);






        //Center location layout
        centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
        centreLocationPin = (ImageView) findViewById(R.id.centreLocationPin);







        //Review Layout
        endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);
        scrollViewEndRide = (ScrollView) findViewById(R.id.scrollViewEndRide);

        textViewEndRideDriverName = (TextView) findViewById(R.id.textViewEndRideDriverName); textViewEndRideDriverName.setTypeface(Fonts.latoRegular(this));
        textViewEndRideDriverCarNumber = (TextView) findViewById(R.id.textViewEndRideDriverCarNumber); textViewEndRideDriverCarNumber.setTypeface(Fonts.latoRegular(this));

        textViewEndRideStartLocationValue = (TextView) findViewById(R.id.textViewEndRideStartLocationValue); textViewEndRideStartLocationValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideEndLocationValue = (TextView) findViewById(R.id.textViewEndRideEndLocationValue); textViewEndRideEndLocationValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideStartTimeValue = (TextView) findViewById(R.id.textViewEndRideStartTimeValue); textViewEndRideStartTimeValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideEndTimeValue = (TextView) findViewById(R.id.textViewEndRideEndTimeValue); textViewEndRideEndTimeValue.setTypeface(Fonts.latoRegular(this));

        textViewEndRideFareValue = (TextView) findViewById(R.id.textViewEndRideFareValue); textViewEndRideFareValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideDiscountValue = (TextView) findViewById(R.id.textViewEndRideDiscountValue); textViewEndRideDiscountValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideFinalFareValue = (TextView) findViewById(R.id.textViewEndRideFinalFareValue); textViewEndRideFinalFareValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideJugnooCashValue = (TextView) findViewById(R.id.textViewEndRideJugnooCashValue); textViewEndRideJugnooCashValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideToBePaidValue = (TextView) findViewById(R.id.textViewEndRideToBePaidValue); textViewEndRideToBePaidValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideBaseFareValue = (TextView) findViewById(R.id.textViewEndRideBaseFareValue); textViewEndRideBaseFareValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideDistanceValue = (TextView) findViewById(R.id.textViewEndRideDistanceValue); textViewEndRideDistanceValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideTimeValue = (TextView) findViewById(R.id.textViewEndRideTimeValue); textViewEndRideTimeValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideWaitTimeValue = (TextView) findViewById(R.id.textViewEndRideWaitTimeValue); textViewEndRideWaitTimeValue.setTypeface(Fonts.latoRegular(this));
        textViewEndRideFareFactorValue = (TextView) findViewById(R.id.textViewEndRideFareFactorValue); textViewEndRideFareFactorValue.setTypeface(Fonts.latoRegular(this));

		relativeLayoutLuggageCharge = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharge);
		relativeLayoutConvenienceCharge = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharge);
		linearLayoutEndRideTime = (LinearLayout) findViewById(R.id.linearLayoutEndRideTime);
		linearLayoutEndRideWaitTime = (LinearLayout) findViewById(R.id.linearLayoutEndRideWaitTime);

		textViewEndRideLuggageChargeValue = (TextView) findViewById(R.id.textViewEndRideLuggageChargeValue); textViewEndRideLuggageChargeValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideConvenienceChargeValue = (TextView) findViewById(R.id.textViewEndRideConvenienceChargeValue); textViewEndRideConvenienceChargeValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideDiscount = (TextView) findViewById(R.id.textViewEndRideDiscount); textViewEndRideDiscount.setTypeface(Fonts.latoRegular(this));
		textViewEndRideDiscountRupee = (TextView) findViewById(R.id.textViewEndRideDiscountRupee); textViewEndRideDiscountRupee.setTypeface(Fonts.latoRegular(this));

		listViewEndRideDiscounts = (NonScrollListView) findViewById(R.id.listViewEndRideDiscounts);
		endRideDiscountsAdapter = new EndRideDiscountsAdapter(this);
		listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

        buttonEndRideOk = (Button) findViewById(R.id.buttonEndRideOk); buttonEndRideOk.setTypeface(Fonts.latoRegular(this));



        ((TextView) findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.latoRegular(this));

        ((TextView) findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideFareRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewEndRideLuggageChargeRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.latoRegular(this));
		((TextView) findViewById(R.id.textViewEndRideConvenienceChargeRupee)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideFinalFareRupee)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideJugnooCashRupee)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideToBePaidRupee)).setTypeface(Fonts.latoRegular(this));
        ((TextView) findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewEndRideWaitTime)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewEndRideFareFactor)).setTypeface(Fonts.latoRegular(this), Typeface.BOLD);


        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				hideAnims();
			}

			@Override
			public void onDrawerClosed(View drawerView) {

			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});


        //Top bar events
        imageViewMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(menuLayout);
                hideAnims();
                FlurryEventLogger.event(MENU_LOOKUP);
            }
        });

        imageViewBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});

        checkServerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideAnims();
			}
		});

        checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(getApplicationContext(), "" + Config.getServerUrlName(), Toast.LENGTH_SHORT).show();
				FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
				return false;
			}
		});


        jugnooShopImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Data.userData != null) {
                    if (Data.userData.nukkadEnable == 1) {
                        startActivity(new Intent(HomeActivity.this, ItemInfosListActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        FlurryEventLogger.christmasNewScreenOpened(Data.userData.accessToken);
                    }
                }
                hideAnims();
            }
        });

        imageViewSearchCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewInitialSearch.setText("");
                editTextSearch.setText("");
                Utils.hideSoftKeyboard(HomeActivity.this, editTextSearch);
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
                FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
            }
        });

        imageViewGift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(REFERRAL_GIFT_ICON);
                startActivity(new Intent(HomeActivity.this, ShareActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        imageViewHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sosDialog(HomeActivity.this);
                FlurryEventLogger.event(SOS_ALERT_USED);
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

        relativeLayoutInvite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imageViewGift.performClick();
                FlurryEventLogger.event(INVITE_EARN_MENU);
            }
        });

        relativeLayoutJugnooCash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PaymentActivity.addPaymentPath = AddPaymentPath.FROM_WALLET;
                startActivity(new Intent(HomeActivity.this, PaymentActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(JUGNOO_CASH_MENU);
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

        relativeLayoutFareDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendToFareDetails();
                FlurryEventLogger.event(FARE_DETAILS);
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
















        // Customer initial layout events
        imageViewRideNow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    hideAnims();
                    if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						if (map != null) {
							promoCouponSelectedForRide = null;
							final LatLng requestLatLng = map.getCameraPosition().target;
							Data.pickupLatLng = requestLatLng;

							editTextAssigningDropLocation.setText("");
							editTextFinalDropLocation.setText("");

							promotionsListAdapter.fetchPromotionsAPI(HomeActivity.this, requestLatLng);
							FlurryEventLogger.event(AUTO_RIDE_ICON);
						}
					} else {
                        DialogPopup.alertPopup(HomeActivity.this, "",
                            Data.CHECK_INTERNET_MSG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonChalo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                promoCouponSelectedForRide = promotionsListAdapter.getSelectedCoupon();
                callAnAutoPopup(HomeActivity.this, Data.pickupLatLng);
                FlurryEventLogger.event(FINAL_RIDE_CALL_MADE);
            }
        });

        linearLayoutPromoShadow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewBack.performClick();
            }
        });




        relativeLayoutInitialSearchBar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextSearch.requestFocus();
                editTextSearch.setText(textViewInitialSearch.getText().toString());
                editTextSearch.setSelection(editTextSearch.getText().length());
                Utils.showSoftKeyboard(HomeActivity.this, editTextSearch);
                passengerScreenMode = PassengerScreenMode.P_SEARCH;
                switchPassengerScreen(passengerScreenMode);
            }
        });


        changeLocalityBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewInitialSearch.setText("");
                relativeLayoutInitialSearchBar.performClick();
                hideAnims();
            }
        });


        linearLayoutPromo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        linearLayoutFareEstimate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activityResumed = false;
                startActivity(new Intent(HomeActivity.this, FareEstimateActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FARE_ESTIMATE);
            }
        });

        linearLayoutRateCard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activityResumed = false;
                sendToFareDetails();
                FlurryEventLogger.event(RATE_CARD);
            }
        });









		//Location error layout
		relativeLayoutLocationError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewInitialSearch.setText("");
				relativeLayoutLocationError.setVisibility(View.GONE);
				relativeLayoutRequestInfo.setVisibility(View.VISIBLE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
				imageViewRideNow.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.VISIBLE);
				genieLayout.setVisibility(View.VISIBLE);
			}
		});

		relativeLayoutLocationErrorSearchBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewInitialSearch.setText("");
				relativeLayoutInitialSearchBar.performClick();
				relativeLayoutLocationError.setVisibility(View.GONE);
				relativeLayoutRequestInfo.setVisibility(View.VISIBLE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
				imageViewRideNow.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.VISIBLE);
				genieLayout.setVisibility(View.VISIBLE);
			}
		});











        // Assigning layout events
//        initialCancelRideBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if ("".equalsIgnoreCase(Data.cSessionId)) {
//                    if (checkForGPSAccuracyTimer != null) {
//                        if (checkForGPSAccuracyTimer.isRunning) {
//                            checkForGPSAccuracyTimer.stopTimer();
//                            customerUIBackToInitialAfterCancel();
//                        }
//                    }
//                } else {
//                    cancelCustomerRequestAsync(HomeActivity.this);
//                    FlurryEventLogger.event(REQUEST_CANCELLED_FINDING_DRIVER);
//                }
//            }
//        });

		assigningLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		initialCancelRideBtn.setOnTouchListener(new View.OnTouchListener() {

			Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if(cancelTouchHold) {
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

						handler.postDelayed(runnable, 2000);
						cancelTouchHold = true;

						break;

					case MotionEvent.ACTION_UP:
						if(cancelTouchHold) {
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
		});



        relativeLayoutAssigningDropLocationBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(false);
            }
        });

        editTextAssigningDropLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(false);
            }
        });




























        //Search Layout Events
        linearLayoutSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        editTextSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextSearch.requestFocus();
                Utils.showSoftKeyboard(HomeActivity.this, editTextSearch);
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

        buttonAddJugnooCash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PaymentActivity.addPaymentPath = AddPaymentPath.FROM_IN_RIDE;
                startActivity(new Intent(HomeActivity.this, PaymentActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                if(PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
                    FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_DRIVER_ARRIVED);
                }
                else if(PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
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

        textViewInRideLowJugnooCash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonAddJugnooCash.performClick();
                FlurryEventLogger.event(JUGNOO_CASH_ADDED_WHEN_ALERT_GENERATED);
            }
        });

        relativeLayoutFinalDropLocationBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDropLocationSearchUI(true);
            }
        });

        editTextFinalDropLocation.setOnClickListener(new OnClickListener() {
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


        buttonEndRideOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GCMIntentService.clearNotifications(HomeActivity.this);
                if (userMode == UserMode.PASSENGER) {
                    Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                    intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.AFTER_RIDE.getOrdinal());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });



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

            if (0 == Data.latitude && 0 == Data.longitude) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
            }


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
                    hideAnims();
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
                        if (findDriversETAAsync != null) {
                            findDriversETAAsync.cancel(true);
                            findDriversETAAsync = null;
                        }
                    }
                }

                @Override
                public void onMapSettled() {
                    // Map settled
                    callMapTouchedRefreshDrivers();
					if(!zoomedForSearch && map != null){
						getPickupAddress(map.getCameraPosition().target);
					}
                }
            };


            initialMyLocationBtn.setOnClickListener(mapMyLocationClick);
            initialMyLocationBtnChangeLoc.setOnClickListener(mapMyLocationClick);
            assigningMyLocationBtn.setOnClickListener(mapMyLocationClick);
            customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);

        }


        genieLayout = new GenieLayout(this);
        genieLayout.addGenieLayout(drawerLayout, relativeLayoutRequestInfo);

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


            Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Config.getServerUrl());

            ReferralActions.showReferralDialog(HomeActivity.this, callbackManager);

        } catch (Exception e) {
            e.printStackTrace();
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

    }


    Handler shakeHandler;
    Runnable shakeRunnable;
    int shakeCount = 0;
    private void startGiftShake(){
        imageViewGift.setVisibility(View.VISIBLE);
        shakeCount = 0;
        shakeHandler = new Handler();
        shakeRunnable = new Runnable() {
            @Override
            public void run() {
                if (PassengerScreenMode.P_SEARCH == passengerScreenMode) {
                    imageViewGift.clearAnimation();
                    imageViewGift.setVisibility(View.GONE);
                } else {
                    if (View.VISIBLE != imageViewGift.getVisibility()) {
                        imageViewGift.setVisibility(View.VISIBLE);
                    }
                    shakeView(imageViewGift);
                }
                if(shakeCount <= 2){
                    shakeCount++;
                    shakeHandler.postDelayed(shakeRunnable, 10000);
                }
                else{
                    shakeHandler.removeCallbacks(shakeRunnable);
                }
            }
        };

        shakeHandler.postDelayed(shakeRunnable, 10000);
    }

    private void stopGiftShake(){
        try{
            imageViewGift.clearAnimation();
            imageViewGift.setVisibility(View.GONE);
            shakeCount = 0;
            shakeHandler.removeCallbacks(shakeRunnable);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void shakeView(View v) {
        // Create shake effect from xml resource
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        // Perform animation
        v.startAnimation(shake);
    }


    public void enableJugnooShopUI() {
        if ((UserMode.PASSENGER == userMode) && (Data.userData != null) && (Data.userData.nukkadEnable == 1)) {
            jugnooShopImageView.setVisibility(View.VISIBLE);
            try {
                if (!"".equalsIgnoreCase(Data.userData.nukkadIcon)) {
                    Picasso.with(HomeActivity.this).load(Data.userData.nukkadIcon).into(jugnooShopImageView);
                } else {
                    jugnooShopImageView.setImageResource(R.drawable.gift_button_selector);
                }
            } catch (Exception e) {
            }
        } else {
            jugnooShopImageView.setVisibility(View.GONE);
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
                        findDriversETAAsync = new FindDriversETAAsync(Data.pickupLatLng);
                        findDriversETAAsync.execute();
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
        startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    public void initiateRequestRide(boolean newRequest) {

        if (newRequest) {
            Data.cSessionId = "";
            Data.cEngagementId = "";

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
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), MAX_ZOOM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
                reconnectLocationFetchers();
            }
            hideAnims();
            FlurryEventLogger.event(NAVIGATION_TO_CURRENT_LOC);
        }
    };

    public void hideAnims() {
        if (genieLayout != null) {
            genieLayout.hideAnims();
        }
    }

    public void clearAnims() {
        if (genieLayout != null) {
            genieLayout.clearAllAnims();
        }
    }


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

            textViewPromotionsValue.setText("" + Data.userData.numCouponsAvaliable);

            textViewJugnooCashValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getMoneyDecimalFormat().format(Data.userData.jugnooBalance));

            Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
            try {
                Picasso.with(HomeActivity.this).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewProfile);
            } catch (Exception e) {
            }

            updateLowJugnooCashBanner(passengerScreenMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchUserScreen() {

        if (findDriversETAAsync != null) {
            findDriversETAAsync.cancel(true);
            findDriversETAAsync = null;
        }


        Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_PASSENGER);

        passengerMainLayout.setVisibility(View.VISIBLE);

        relativeLayoutPromotions.setVisibility(View.VISIBLE);

        Database2.getInstance(HomeActivity.this).close();

    }


    public void switchPassengerScreen(PassengerScreenMode mode) {
        try {
            imageViewMenu.setVisibility(View.VISIBLE);
            imageViewBack.setVisibility(View.GONE);

            if (userMode == UserMode.PASSENGER) {

                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }

                if (mode == PassengerScreenMode.P_RIDE_END) {
                    if (Data.endRideData != null) {
                        genieLayout.setVisibility(View.GONE);

                        scrollViewEndRide.scrollTo(0, 0);

                        mapLayout.setVisibility(View.VISIBLE);
                        endRideReviewRl.setVisibility(View.VISIBLE);

                        textViewEndRideDriverName.setText(Data.assignedDriverInfo.name);
                        textViewEndRideDriverCarNumber.setText(Data.assignedDriverInfo.carNumber);

                        textViewEndRideStartLocationValue.setText(Data.endRideData.pickupAddress);
                        textViewEndRideEndLocationValue.setText(Data.endRideData.dropAddress);

                        textViewEndRideStartTimeValue.setText(Data.endRideData.pickupTime);
                        textViewEndRideEndTimeValue.setText(Data.endRideData.dropTime);

                        textViewEndRideFareValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.fare));

						if(Utils.compareDouble(Data.endRideData.luggageCharge, 0) > 0){
							relativeLayoutLuggageCharge.setVisibility(View.VISIBLE);
							textViewEndRideLuggageChargeValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.luggageCharge));
						} else{
							relativeLayoutLuggageCharge.setVisibility(View.GONE);
						}

						if(Utils.compareDouble(Data.endRideData.convenienceCharge, 0) > 0){
							relativeLayoutConvenienceCharge.setVisibility(View.VISIBLE);
							textViewEndRideConvenienceChargeValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.convenienceCharge));
						} else{
							relativeLayoutConvenienceCharge.setVisibility(View.GONE);
						}

						if(Data.endRideData.discountTypes.size() > 1){
							listViewEndRideDiscounts.setVisibility(View.VISIBLE);
							endRideDiscountsAdapter.setList(Data.endRideData.discountTypes);
							textViewEndRideDiscount.setText("Discounts");
							textViewEndRideDiscountRupee.setVisibility(View.GONE);
							textViewEndRideDiscountValue.setVisibility(View.GONE);
						}
						else if(Data.endRideData.discountTypes.size() > 0){
							listViewEndRideDiscounts.setVisibility(View.GONE);
							textViewEndRideDiscount.setText(Data.endRideData.discountTypes.get(0).name);
							textViewEndRideDiscountRupee.setVisibility(View.VISIBLE);
							textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
							textViewEndRideDiscountValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.discount));
						}
						else{
							listViewEndRideDiscounts.setVisibility(View.GONE);
							textViewEndRideDiscount.setText("Discounts");
							textViewEndRideDiscountRupee.setVisibility(View.VISIBLE);
							textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
							textViewEndRideDiscountValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.discount));
						}

                        textViewEndRideFinalFareValue.setText(Utils.getMoneyDecimalFormat().format(Math.abs(Data.endRideData.fare - Data.endRideData.discount)));
                        textViewEndRideJugnooCashValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.paidUsingWallet));
                        textViewEndRideToBePaidValue.setText(Utils.getMoneyDecimalFormat().format(Data.endRideData.toPay));


                        textViewEndRideFareFactorValue.setText(decimalFormat.format(Data.endRideData.fareFactor) + "x");
                        textViewEndRideBaseFareValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getMoneyDecimalFormat().format(Data.endRideData.baseFare));
                        double totalDistanceInKm = Data.endRideData.distance;
                        String kmsStr = "";
                        if (totalDistanceInKm > 1) {
                            kmsStr = "kms";
                        } else {
                            kmsStr = "km";
                        }
                        textViewEndRideDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
						if(Data.endRideData.rideTime > -1){
							linearLayoutEndRideTime.setVisibility(View.VISIBLE);
							textViewEndRideTimeValue.setText(decimalFormatNoDecimal.format(Data.endRideData.rideTime) + " min");
						} else{
							linearLayoutEndRideTime.setVisibility(View.GONE);
						}
						if(Data.endRideData.waitTime > 0){
							linearLayoutEndRideWaitTime.setVisibility(View.VISIBLE);
							textViewEndRideWaitTimeValue.setText(decimalFormatNoDecimal.format(Data.endRideData.waitTime) + " min");
						}
						else{
							linearLayoutEndRideWaitTime.setVisibility(View.GONE);
						}

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


                enableJugnooShopUI();

                switch (mode) {

                    case P_INITIAL:

						Data.dropLatLng = null;

                        Database2.getInstance(HomeActivity.this).deleteRidePathTable();

                        clearAnims();

                        try {
                            pickupLocationMarker.remove();
                        } catch (Exception e) {
                        }
                        try {
                            driverLocationMarker.remove();
                        } catch (Exception e) {
                        }



                        if (findDriversETAAsync != null) {
                            findDriversETAAsync.cancel(true);
                            findDriversETAAsync = null;
                        }


                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        if (Data.userData != null && Data.userData.canChangeLocation == 1) {
                            centreLocationRl.setVisibility(View.VISIBLE);
                            relativeLayoutInitialSearchBar.setVisibility(View.VISIBLE);
                        } else {
                            centreLocationRl.setVisibility(View.GONE);
                            relativeLayoutInitialSearchBar.setVisibility(View.GONE);
                        }

                        textViewNearestDriverETA.setText("Finding nearby drivers...");

                        imageViewRideNow.setVisibility(View.VISIBLE);

                        initialMyLocationBtn.setVisibility(View.VISIBLE);
                        changeLocalityBtn.setVisibility(View.GONE);
                        initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

                        linearLayoutPromo.setVisibility(View.GONE);
                        promoOpened = false;

                        setFareFactorToInitialState();

                        cancelTimerRequestRide();


                        Log.e("Data.latitude", "=" + Data.latitude);
                        Log.e("myLocation", "=" + myLocation);

                        if (Data.latitude != 0 && Data.longitude != 0) {
                            showDriverMarkersAndPanMap(new LatLng(Data.latitude, Data.longitude));
                        } else if (myLocation != null) {
                            showDriverMarkersAndPanMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        }


                        startGiftShake();
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
							relativeLayoutRequestInfo.setVisibility(View.GONE);
							initialMyLocationBtn.setVisibility(View.GONE);
							imageViewRideNow.setVisibility(View.GONE);
							genieLayout.setVisibility(View.GONE);
							centreLocationRl.setVisibility(View.GONE);

							Data.locationSettingsNoPressed = false;
						}
						else{
							relativeLayoutLocationError.setVisibility(View.GONE);
							relativeLayoutRequestInfo.setVisibility(View.VISIBLE);
							initialMyLocationBtn.setVisibility(View.VISIBLE);
							imageViewRideNow.setVisibility(View.VISIBLE);
							genieLayout.setVisibility(View.VISIBLE);
							centreLocationRl.setVisibility(View.VISIBLE);
						}

                        break;


                    case P_SEARCH:

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        jugnooShopImageView.setVisibility(View.GONE);
                        stopGiftShake();
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.VISIBLE);

                        genieLayout.setVisibility(View.GONE);


                        break;


                    case P_ASSIGNING:

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.GONE);
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
                        }

                        stopDropLocationSearchUI(false);

                        if(Data.dropLatLng == null){
                            if ("".equalsIgnoreCase(Data.cSessionId)) {
                                relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
                            }
                            else{
                                relativeLayoutAssigningDropLocationParent.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
//                            setDropLocationMarker();
                            relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
                        }



                        startGiftShake();
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);

                        genieLayout.setVisibility(View.GONE);

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

                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng));

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo.latLng));

                            Log.i("marker added", "REQUEST_FINAL");
                        }


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

                        setAssignedDriverData(mode);

                        if(dropLocationSearched){
                            editTextFinalDropLocation.setText(editTextAssigningDropLocation.getText().toString());
                            editTextFinalDropLocation.setSelection(editTextFinalDropLocation.getText().toString().length());
                            initDropLocationSearchUI(true);
                        }
                        else{
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();

                        buttonCancelRide.setVisibility(View.VISIBLE);
                        buttonAddJugnooCash.setVisibility(View.GONE);

                        textViewInRideLowJugnooCash.setVisibility(View.GONE);


                        stopGiftShake();
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

                        genieLayout.setVisibility(View.GONE);

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

                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng));

                            driverLocationMarker = map.addMarker(getAssignedDriverCarMarkerOptions(Data.assignedDriverInfo.latLng));

                            Log.i("marker added", "REQUEST_FINAL");

                            if(Data.dropLatLng != null) {
                                setDropLocationMarker();
                                setPickupToDropPath();
                            }
                        }


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

						if(dropLocationSearched){
							String searchText = "";
							if(editTextAssigningDropLocation.getText().toString().trim().length() > 0){
								searchText = editTextAssigningDropLocation.getText().toString().trim();
							}
							else if(editTextFinalDropLocation.getText().toString().trim().length() > 0){
								searchText = editTextFinalDropLocation.getText().toString().trim();
							}
							editTextFinalDropLocation.setText(searchText);
							editTextFinalDropLocation.setSelection(searchText.length());
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
                        setDropLocationEngagedUI();

                        setAssignedDriverData(mode);

                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddJugnooCash.setVisibility(View.VISIBLE);

                        textViewInRideLowJugnooCash.setVisibility(View.GONE);


                        stopGiftShake();
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

                        genieLayout.setVisibility(View.GONE);

                        break;


                    case P_IN_RIDE:

                        if (map != null) {
                            map.clear();

                            if (Data.pickupLatLng != null) {
                                map.addMarker(getStartPickupLocMarkerOptions(Data.pickupLatLng));
                            }

                            if(Data.dropLatLng != null) {
                                setDropLocationMarker();
                                setPickupToDropPath();
                            }
                        }


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.VISIBLE);
                        centreLocationRl.setVisibility(View.GONE);

						if(dropLocationSearched){
							String searchText = "";
							if(editTextAssigningDropLocation.getText().toString().trim().length() > 0){
								searchText = editTextAssigningDropLocation.getText().toString().trim();
							}
							else if(editTextFinalDropLocation.getText().toString().trim().length() > 0){
								searchText = editTextFinalDropLocation.getText().toString().trim();
							}
							editTextFinalDropLocation.setText(searchText);
							editTextFinalDropLocation.setSelection(searchText.length());
							initDropLocationSearchUI(true);
						}
						else{
							stopDropLocationSearchUI(true);
						}
						setDropLocationEngagedUI();


                        setAssignedDriverData(mode);

                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddJugnooCash.setVisibility(View.VISIBLE);

                        updateLowJugnooCashBanner(mode);


                        stopGiftShake();
                        imageViewHelp.setVisibility(View.VISIBLE);
                        imageViewSearchCancel.setVisibility(View.GONE);

                        genieLayout.setVisibility(View.GONE);

                        break;

                    case P_RIDE_END:

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        imageViewSearchCancel.setVisibility(View.GONE);
                        stopGiftShake();
                        imageViewHelp.setVisibility(View.VISIBLE);

                        genieLayout.setVisibility(View.GONE);

						Data.pickupLatLng = null;

                        break;


                    default:

                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        endRideReviewRl.setVisibility(View.GONE);
                        centreLocationRl.setVisibility(View.GONE);

                        stopGiftShake();
                        imageViewHelp.setVisibility(View.GONE);
                        imageViewSearchCancel.setVisibility(View.GONE);

                        genieLayout.setVisibility(View.VISIBLE);


                }

                initiateTimersForStates(mode);


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



    private void setDropLocationEngagedUI(){
        if(Data.dropLatLng == null){
            relativeLayoutFinalDropLocationParent.setVisibility(View.VISIBLE);
        }
        else{
            setDropLocationMarker();
            relativeLayoutFinalDropLocationParent.setVisibility(View.GONE);
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

    public void updateLowJugnooCashBanner(PassengerScreenMode mode) {
        if (PassengerScreenMode.P_IN_RIDE == mode) {
            if (Data.userData != null) {
                if (HomeActivity.rechargedOnce) {
                    textViewInRideLowJugnooCash.setVisibility(View.GONE);
                } else if (Data.userData.jugnooBalance < MIN_BALANCE_ALERT_VALUE) {
                    textViewInRideLowJugnooCash.setVisibility(View.VISIBLE);
                } else {
                    textViewInRideLowJugnooCash.setVisibility(View.GONE);
                }
            } else {
                textViewInRideLowJugnooCash.setVisibility(View.GONE);
            }
        } else {
            textViewInRideLowJugnooCash.setVisibility(View.GONE);
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
            }
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

        if (!"No Promo Code applied".equalsIgnoreCase(Data.assignedDriverInfo.promoName)) {
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
            textViewInRideState.setText("Ride in progress");
        }


        Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
        try {
            Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).transform(new CircleTransform()).into(imageViewInRideDriver);
        } catch (Exception e) {
        }


        Data.assignedDriverInfo.carImage = Data.assignedDriverInfo.carImage.replace("http://graph.facebook", "https://graph.facebook");
        try {
            Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.carImage).transform(new CircleTransform()).into(imageViewInRideDriverCar);
        } catch (Exception e) {
        }

    }


    public void updateDriverETAText(PassengerScreenMode passengerScreenMode) {
        if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode) {
            if (!"".equalsIgnoreCase(Data.assignedDriverInfo.eta)) {
                try {
                    double etaMin = Double.parseDouble(Data.assignedDriverInfo.eta);
                    if (etaMin > 1) {
                        textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.eta + " minutes");
                    } else {
                        textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.eta + " minute");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    textViewInRideState.setText("Will arrive in " + Data.assignedDriverInfo.eta + " minutes");
                }
            }
        } else if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
            textViewInRideState.setText("Driver arrived at pick up point");
        }
    }



    private void initDropLocationSearchUI(boolean engaged){
		try {
			dropLocationSearched = true;
			if(!engaged) {
				editTextAssigningDropLocation.requestFocus();
				relativeLayoutAssigningDropLocationParent.setBackgroundColor(getResources().getColor(R.color.white_translucent));
				relativeLayoutAssigningDropLocationBar.setBackgroundResource(R.drawable.background_white_rounded_bordered);
				scrollViewAssigning.setVisibility(View.VISIBLE);
				Utils.showSoftKeyboard(HomeActivity.this, editTextAssigningDropLocation);
			}
			else{
				editTextFinalDropLocation.requestFocus();
				relativeLayoutFinalDropLocationParent.setBackgroundColor(getResources().getColor(R.color.white_translucent));
				relativeLayoutFinalDropLocationBar.setBackgroundResource(R.drawable.background_white_rounded_bordered);
				scrollViewFinal.setVisibility(View.VISIBLE);
				Utils.showSoftKeyboard(HomeActivity.this, editTextFinalDropLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void stopDropLocationSearchUI(boolean engaged){
		try {
			dropLocationSearched = false;
			if(!engaged) {
				relativeLayoutAssigningDropLocationParent.setBackgroundColor(getResources().getColor(R.color.transparent));
				relativeLayoutAssigningDropLocationBar.setBackgroundResource(R.drawable.background_white_rounded);
				scrollViewAssigning.setVisibility(View.GONE);
				progressBarAssigningDropLocation.setVisibility(View.GONE);
				editTextAssigningDropLocation.setText("");
				Utils.hideSoftKeyboard(HomeActivity.this, editTextAssigningDropLocation);
			} else {
				relativeLayoutFinalDropLocationParent.setBackgroundColor(getResources().getColor(R.color.transparent));
				relativeLayoutFinalDropLocationBar.setBackgroundResource(R.drawable.background_white_rounded);
				scrollViewFinal.setVisibility(View.GONE);
				progressBarFinalDropLocation.setVisibility(View.GONE);
				editTextFinalDropLocation.setText("");
				Utils.hideSoftKeyboard(HomeActivity.this, editTextFinalDropLocation);
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
    protected void onResume() {
        super.onResume();

        if (!checkIfUserDataNull(HomeActivity.this)) {
            setUserData();

            try {
                if (activityResumed) {
                    if (!feedbackSkipped) {
						callAndHandleStateRestoreAPI(false);
                        initiateTimersForStates(passengerScreenMode);
                    }

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

			try{
				if(Data.supportFeedbackSubmitted) {
					drawerLayout.closeDrawer(menuLayout);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							DialogPopup.dialogBanner(HomeActivity.this, "Thank you for your valuable feedback");
						}
					}, 300);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			Data.supportFeedbackSubmitted = false;

        }

        HomeActivity.checkForAccessTokenChange(this);

        activityResumed = true;
        intentFired = false;
        feedbackSkipped = false;

		initializeFusedLocationFetchers();

//        genieLayout.setGenieParams();
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
        try {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
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

        destroyFusedLocationFetchers();

        GCMIntentService.clearNotifications(getApplicationContext());

        try {
            if (userMode == UserMode.PASSENGER) {
                pauseAllTimers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        genieLayout.saveGenieParams();
        clearAnims();

        super.onPause();

    }


    @Override
    public void onBackPressed() {
        try {
            if (genieLayout.areJugnooIconsVisible()) {
                genieLayout.hideAnims();
            }

            if (PassengerScreenMode.P_SEARCH == passengerScreenMode) {
                textViewInitialSearch.setText("");
                editTextSearch.setText("");
                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
                FlurryEventLogger.event(PICKUP_LOCATION_NOT_SET);
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



    class FindDriversETAAsync extends AsyncTask<Void, Void, String> {

        LatLng destination;

        String etaMinutes = "5", farAwayCity = "";

        public FindDriversETAAsync(LatLng destination) {
            this.destination = destination;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (userMode == UserMode.PASSENGER) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        addCurrentLocationAddressMarker(destination);
                        textViewNearestDriverETA.setText("Finding nearby drivers...");
                        dontCallRefreshDriver = false;
                    }
                });
                etaMinutes = "5";
            }
        }


        @Override
        protected String doInBackground(Void... params) {
            if (userMode == UserMode.PASSENGER) {
                try {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
                    nameValuePairs.add(new BasicNameValuePair("latitude", "" + destination.latitude));
                    nameValuePairs.add(new BasicNameValuePair("longitude", "" + destination.longitude));

                    if (1 == showAllDrivers) {
                        nameValuePairs.add(new BasicNameValuePair("show_all", "1"));
                    }
					if(1 == showDriverInfo){
						nameValuePairs.add(new BasicNameValuePair("show_phone_no", "1"));
					}

                    Log.i("nameValuePairs in find_a_driver", "=" + nameValuePairs);

                    HttpRequester simpleJSONParser = new HttpRequester();
                    String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/find_a_driver", nameValuePairs);
                    Log.i("result in find_a_driver", "=" + result);
                    simpleJSONParser = null;
                    nameValuePairs = null;

//		    			{
//		    			    "flag": 175,
//		    			    "drivers": [
//		    			        {
//		    			            "user_id": 209,
//		    			            "user_name": "Driver 2",
//		    			            "phone_no": "+919780298413",
//		    			            "latitude": 30.718963,
//		    			            "longitude": 76.810523,
//		    			            "rating": 5
//		    			        }
//		    			    ],
//		    			    "eta": 1
//		    			}

                    if (result.contains(HttpRequester.SERVER_TIMEOUT)) {
                    } else {
                        try {
                            JSONObject jObj = new JSONObject(result);
                            new JSONParser().parseDriversToShow(jObj, "drivers");
                            etaMinutes = jObj.getString("eta");
                            Data.userData.fareFactor = jObj.getDouble("fare_factor");

                            if (jObj.has("far_away_city")) {
                                farAwayCity = jObj.getString("far_away_city");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return etaMinutes;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "error";
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (userMode == UserMode.PASSENGER) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showDriverMarkersAndPanMap(destination);
                            dontCallRefreshDriver = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dontCallRefreshDriver = false;
                                }
                            }, 300);

                            if (!"error".equalsIgnoreCase(result)) {
                                if (Data.driverInfos.size() == 0) {
                                    textViewNearestDriverETA.setText("No drivers nearby");
                                } else {
                                    if ("1".equalsIgnoreCase(etaMinutes)) {
                                        textViewNearestDriverETA.setText("Nearest driver is " + etaMinutes + " minute away");
                                    } else {
                                        textViewNearestDriverETA.setText("Nearest driver is " + etaMinutes + " minutes away");
                                    }
                                }
                            } else {
                                textViewNearestDriverETA.setText("Couldn't find drivers nearby.");
                            }

							if(relativeLayoutLocationError.getVisibility() == View.GONE) {
								if (!"".equalsIgnoreCase(farAwayCity)) {
									textViewNearestDriverETA.setText(farAwayCity);

									imageViewRideNow.setVisibility(View.GONE);

									initialMyLocationBtn.setVisibility(View.GONE);
									changeLocalityBtn.setVisibility(View.VISIBLE);
									initialMyLocationBtnChangeLoc.setVisibility(View.VISIBLE);

									genieLayout.setVisibility(View.GONE);

								} else {
									imageViewRideNow.setVisibility(View.VISIBLE);

									initialMyLocationBtn.setVisibility(View.VISIBLE);
									changeLocalityBtn.setVisibility(View.GONE);
									initialMyLocationBtnChangeLoc.setVisibility(View.GONE);

									if (PassengerScreenMode.P_INITIAL == passengerScreenMode && !promoOpened) {
										genieLayout.setVisibility(View.VISIBLE);
									}
								}
							}

                            setFareFactorToInitialState();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }


    }



    public MarkerOptions getStartPickupLocMarkerOptions(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("pickup location");
        markerOptions.snippet("");
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmapStart(HomeActivity.this, assl)));
        return markerOptions;
    }


    public MarkerOptions getAssignedDriverCarMarkerOptions(LatLng latlng){
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title("driver position");
        markerOptions1.snippet("");
        markerOptions1.position(latlng);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
        markerOptions1.anchor(0.5f, 0.7f);
        return markerOptions1;
    }




    public void addDriverMarkerForCustomer(DriverInfo driverInfo) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("driver shown to customer");
        markerOptions.snippet("" + driverInfo.userId);
        markerOptions.position(driverInfo.latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
        markerOptions.anchor(0.5f, 0.5f);
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
            if (userMode == UserMode.PASSENGER && (passengerScreenMode == PassengerScreenMode.P_ASSIGNING)) {
                addUserCurrentLocationAddressMarker(userLatLng);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO
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
                    final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder);
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
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM));
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
			}
		});
	}






    /**
     * ASync for cancelCustomerRequestAsync from server
     */
    public void cancelCustomerRequestAsync(final Activity activity) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

//            DialogPopup.showLoadingDialog(activity, "Loading...");

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

                        try {
                            jObj = new JSONObject(response);

                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                    HomeActivity.logoutUser(activity);
                                } else {
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                }
                            } else {
                                customerUIBackToInitialAfterCancel();
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

//        if (userMode == UserMode.PASSENGER &&
//            (PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)) {
//            if (map != null && myLocation != null) {
//                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), 500, null);
//            }
//        }

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
                                                Data.userData.jugnooBalance = jObj.getDouble("jugnoo_balance");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

										Data.endRideData = JSONParser.parseEndRideData(jObj, engagementId, Data.fareStructure.fixedFare);

                                        clearSPData();
                                        map.clear();
                                        passengerScreenMode = PassengerScreenMode.P_RIDE_END;
                                        switchPassengerScreen(passengerScreenMode);

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






    public void sendDropLocationAPI(final Activity activity, final LatLng dropLatLng) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

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
                        DialogPopup.dismissLoadingDialog();
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
                                        stopDropLocationSearchUI(false);
                                        relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
                                    }
                                    else if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
											PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
											PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
                                        stopDropLocationSearchUI(true);
                                        relativeLayoutFinalDropLocationParent.setVisibility(View.GONE);

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
                        DialogPopup.dismissLoadingDialog();
                    }
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
                                            Data.assignedDriverInfo.eta = eta;
                                        }

                                        HomeActivity.this.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {
                                                    if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
                                                        if (map != null) {
                                                            if (HomeActivity.this.hasWindowFocus()) {
                                                                driverLocationMarker.setPosition(driverCurrentLatLng);
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

//                                            if (map != null && ridePath != null) {
//                                                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(ridePath.destinationLatitude, ridePath.destinationLongitude)));
//                                            }
//                                            if (map != null && myLocation != null) {
//                                                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
//                                            }

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
                                            LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder);
                                            float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 2000, new GoogleMap.CancelableCallback() {
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
                                                        LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder);
                                                        float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 1000, null);
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


            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.latoRegular(activity));

            btnOk.setText("OK");
            btnCancel.setText("Cancel");

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    initiateRequestRide(true);
                    FlurryEventLogger.event(FINAL_CALL_RIDE);
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
            if(Utils.compareDouble(Data.pickupLatLng.latitude, 30.7500) == 0 && Utils.compareDouble(Data.pickupLatLng.longitude, 76.7800) == 0){
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
                        e.printStackTrace();
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
                            initiateRequestRide(true);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_OFFLINE);
                        Database2.getInstance(HomeActivity.this).close();
                    }
                }).start();

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
                    fetchAcceptedDriverInfoAndChangeState(jObj);
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


    public void fetchAcceptedDriverInfoAndChangeState(JSONObject jObj) {
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

            String driverRating = jObj.getString("rating");

            Data.pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);

            String promoName = JSONParser.getPromoName(jObj);
            String eta = "5";
            if (jObj.has("eta")) {
                eta = jObj.getString("eta");
            }

            Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName,
                driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta);


            double fareFactor = 1.0;
            if (jObj.has("fare_factor")) {
                fareFactor = jObj.getDouble("fare_factor");
            }

            Data.userData.fareFactor = fareFactor;


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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void fetchAcceptedDriverStartRideInfoAndChangeState(JSONObject jObj) {
        try {
            cancelTimerRequestRide();
            passengerScreenMode = PassengerScreenMode.P_IN_RIDE;

            // response = {
            // "flag": 114,
            // "driver_id": 1,
            // "user_name": "Name",
            // "phone_no": "0172",
            // "user_image": "abcd.png",
            // "driver_car_image": "wxyz.png",
            // "driver_car_number": "1234",
            // "rating": 5,
            // "current_location_latitude": 11,
            // "current_location_longitude": 11,
            // "engagement_id": 1,
            // "session_id": 1};};

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

            String carNumber = "";
            if (jObj.has("driver_car_no")) {
                carNumber = jObj.getString("driver_car_no");
            }

            int freeRide = 0;
            if (jObj.has("free_ride")) {
                freeRide = jObj.getInt("free_ride");
            }

            String promoName = JSONParser.getPromoName(jObj);

            String eta = "5";
            if (jObj.has("eta")) {
                eta = jObj.getString("eta");
            }

            Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName,
                driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta);


            double fareFactor = 1.0;
            if (jObj.has("fare_factor")) {
                fareFactor = jObj.getDouble("fare_factor");
            }

            Data.userData.fareFactor = fareFactor;

            Data.startRidePreviousLatLng = Data.pickupLatLng;
            initializeStartRideVariables();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchPassengerScreen(passengerScreenMode);
                }
            });

        } catch (JSONException e) {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
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
			if (priority == 0) {
				if (location.getAccuracy() <= LOW_POWER_ACCURACY_CHECK) {
					HomeActivity.myLocation = location;
					callMapTouchedRefreshDrivers();
					updatePickupLocation(location);
				}
			} else if (priority == 2) {
				destroyLowPowerFusedLocationFetcher();
				if (location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK) {
					HomeActivity.myLocation = location;
					callMapTouchedRefreshDrivers();
					updatePickupLocation(location);
				}
			}

			if(!zoomedToMyLocation){
				zoomToCurrentLocationWithOneDriver(new LatLng(location.getLatitude(), location.getLongitude()));
			}
			zoomedToMyLocation = true;

			Data.latitude = location.getLatitude();
			Data.longitude = location.getLongitude();

			boolean cached = false;
			try {
				if (myLocation != null) {
					Bundle bundle = myLocation.getExtras();
					cached = bundle.getBoolean("cached");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!cached && PassengerScreenMode.P_INITIAL == passengerScreenMode
					&& relativeLayoutLocationError.getVisibility() == View.VISIBLE) {
				relativeLayoutLocationError.setVisibility(View.GONE);
				relativeLayoutRequestInfo.setVisibility(View.VISIBLE);
				initialMyLocationBtn.setVisibility(View.VISIBLE);
				imageViewRideNow.setVisibility(View.VISIBLE);
				genieLayout.setVisibility(View.VISIBLE);
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

                if (findDriversETAAsync != null) {
                    findDriversETAAsync.cancel(true);
                    findDriversETAAsync = null;
                }

                if (Data.pickupLatLng.latitude == 0 && Data.pickupLatLng.longitude == 0) {
                    Data.pickupLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
                    Editor editor = pref.edit();
                    editor.putString(Data.SP_TOTAL_DISTANCE, "0");
                    editor.putString(Data.SP_LAST_LATITUDE, "" + Data.pickupLatLng.latitude);
                    editor.putString(Data.SP_LAST_LONGITUDE, "" + Data.pickupLatLng.longitude);
                    editor.commit();

					if (myLocation != null) {
						findDriversETAAsync = new FindDriversETAAsync(Data.pickupLatLng);
						findDriversETAAsync.execute();
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
            serverRequestStartTime = 0;
            serverRequestEndTime = 0;
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

                                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
                                nameValuePairs.add(new BasicNameValuePair("latitude", "" + Data.pickupLatLng.latitude));
                                nameValuePairs.add(new BasicNameValuePair("longitude", "" + Data.pickupLatLng.longitude));
                                if (myLocation != null) {
                                    nameValuePairs.add(new BasicNameValuePair("current_latitude", "" + myLocation.getLatitude()));
                                    nameValuePairs.add(new BasicNameValuePair("current_longitude", "" + myLocation.getLongitude()));
                                } else {
                                    nameValuePairs.add(new BasicNameValuePair("current_latitude", "" + Data.pickupLatLng.latitude));
                                    nameValuePairs.add(new BasicNameValuePair("current_longitude", "" + Data.pickupLatLng.longitude));
                                }

                                if (promoCouponSelectedForRide != null) {
                                    if (promoCouponSelectedForRide instanceof CouponInfo) {
                                        nameValuePairs.add(new BasicNameValuePair("coupon_to_apply", "" + promoCouponSelectedForRide.id));
                                        if (promoCouponSelectedForRide.id == 0) {
                                            nameValuePairs.add(new BasicNameValuePair("promo_to_apply", "" + promoCouponSelectedForRide.id));
                                        }
                                    } else if (promoCouponSelectedForRide instanceof PromotionInfo) {
                                        nameValuePairs.add(new BasicNameValuePair("promo_to_apply", "" + promoCouponSelectedForRide.id));
                                    }
                                }

                                if ("".equalsIgnoreCase(Data.cSessionId)) {
                                    nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "0"));
                                    nameValuePairs.add(new BasicNameValuePair("fare_factor", "" + Data.userData.fareFactor));
                                    if (myLocation != null && myLocation.hasAccuracy()) {
                                        nameValuePairs.add(new BasicNameValuePair("location_accuracy", "" + myLocation.getAccuracy()));
                                    }
                                } else {
                                    nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "1"));
                                }

                                Log.i("nameValuePairs of request_ride", "=" + nameValuePairs);
                                String response = new HttpRequester().getJSONFromUrlParams(Config.getServerUrl() + "/request_ride", nameValuePairs);

                                Log.e("response of request_ride", "=" + response);

                                if (response.contains(HttpRequester.SERVER_TIMEOUT)) {
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

                                                Data.cSessionId = jObj.getString("session_id");
                                            } else if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                    cancelTimerRequestRide();
                                                    fetchAcceptedDriverInfoAndChangeState(jObj);
                                                }
                                            } else if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {
                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                    cancelTimerRequestRide();
                                                    fetchAcceptedDriverStartRideInfoAndChangeState(jObj);
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
                    relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
                    initialCancelRideBtn.setVisibility(View.GONE);
                } else {
                    if(Data.dropLatLng == null){
                        relativeLayoutAssigningDropLocationParent.setVisibility(View.VISIBLE);
                    }
                    else{
                        relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
                    }
                    initialCancelRideBtn.setVisibility(View.VISIBLE);
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
                    if (skipped && Data.customerRateAppFlag == 1) {
                        rateAppPopup(activity);
                    } else {
                    }
                }
				firstTimeZoom = false;
				pickupDropZoomed = false;

                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
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
                        Data.userData.jugnooBalance = jugnooBalance;
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
                                Utils.openCallIntent(HomeActivity.this, Config.getSupportNumber());
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
            }, false, false, new DialogInterface.OnCancelListener() {
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
            }, false, false, new DialogInterface.OnCancelListener() {
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
                                    startUIAfterGettingUserStatus();
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


}