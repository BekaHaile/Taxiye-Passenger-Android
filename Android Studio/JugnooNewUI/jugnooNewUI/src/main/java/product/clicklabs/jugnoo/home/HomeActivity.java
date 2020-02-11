package product.clicklabs.jugnoo.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hippo.ChatByUniqueIdAttributes;
import com.hippo.HippoConfig;
import com.hippo.HippoNotificationConfig;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;
import com.sabkuchfresh.dialogs.OrderCompleteReferralDialog;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.TransactionUtils;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.Target;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.branch.referral.Branch;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import okio.BufferedSource;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.ChatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.DeleteCacheIntentService;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.GCMIntentService;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RazorpayBaseActivity;
import product.clicklabs.jugnoo.RideCancellationActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.BadgesAdapter;
import product.clicklabs.jugnoo.adapters.BadgesAdapter;
import product.clicklabs.jugnoo.adapters.BidsPlacedAdapter;
import product.clicklabs.jugnoo.adapters.CorporatesAdapter;
import product.clicklabs.jugnoo.adapters.FeedbackReasonsAdapter;
import product.clicklabs.jugnoo.adapters.RideTypesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.ApiCampaignAvailRequest;
import product.clicklabs.jugnoo.apis.ApiCampaignRequestCancel;
import product.clicklabs.jugnoo.apis.ApiCancelRequest;
import product.clicklabs.jugnoo.apis.ApiEmergencyDisable;
import product.clicklabs.jugnoo.apis.ApiFareEstimate;
import product.clicklabs.jugnoo.apis.ApiFetchUserAddress;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.apis.GoogleJungleCaching;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.BidInfo;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FeedBackInfo;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.LatLngCoordinates;
import product.clicklabs.jugnoo.datastructure.MapsApiSources;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.directions.JungleApisImpl;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.EmergencyDialog;
import product.clicklabs.jugnoo.emergency.EmergencyDisableDialog;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.home.adapters.SpecialPickupItemsAdapter;
import product.clicklabs.jugnoo.home.adapters.VehiclesTabAdapter;
import product.clicklabs.jugnoo.home.dialogs.CancellationChargesDialog;
import product.clicklabs.jugnoo.home.dialogs.DriverCallDialog;
import product.clicklabs.jugnoo.home.dialogs.DriverNotFoundDialog;
import product.clicklabs.jugnoo.home.dialogs.DriverTipInteractor;
import product.clicklabs.jugnoo.home.dialogs.EditDropConfirmation;
import product.clicklabs.jugnoo.home.dialogs.EditDropDialog;
import product.clicklabs.jugnoo.home.dialogs.EnterBidDialog;
import product.clicklabs.jugnoo.home.dialogs.InAppCampaignDialog;
import product.clicklabs.jugnoo.home.dialogs.NotesDialog;
import product.clicklabs.jugnoo.home.dialogs.PartnerWithJugnooDialog;
import product.clicklabs.jugnoo.home.dialogs.PaymentOptionDialog;
import product.clicklabs.jugnoo.home.dialogs.PaytmRechargeDialog;
import product.clicklabs.jugnoo.home.dialogs.PriorityTipDialog;
import product.clicklabs.jugnoo.home.dialogs.PushDialog;
import product.clicklabs.jugnoo.home.dialogs.RateAppDialog;
import product.clicklabs.jugnoo.home.dialogs.RideConfirmationDialog;
import product.clicklabs.jugnoo.home.dialogs.ReinviteFriendsDialog;
import product.clicklabs.jugnoo.home.dialogs.RideConfirmationDialog;
import product.clicklabs.jugnoo.home.dialogs.SaveLocationDialog;
import product.clicklabs.jugnoo.home.dialogs.SavedAddressPickupDialog;
import product.clicklabs.jugnoo.home.dialogs.ServiceUnavailableDialog;
import product.clicklabs.jugnoo.home.dialogs.TutorialInfoDialog;
import product.clicklabs.jugnoo.home.dialogs.VehicleFareEstimateDialog;
import product.clicklabs.jugnoo.home.fragments.ScheduleRideFragment;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideEndFragmentMode;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogHelper;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogModeValue;
import product.clicklabs.jugnoo.newui.dialog.RewardsDialog;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.models.Promo;
import product.clicklabs.jugnoo.rentals.InstructionDialog;
import product.clicklabs.jugnoo.rentals.RentalStationAdapter;
import product.clicklabs.jugnoo.rentals.damagereport.DamageReportActivity;
import product.clicklabs.jugnoo.rentals.models.GetLockStatusResponse;
import product.clicklabs.jugnoo.rentals.models.GpsLockStatus;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.retrofit.model.CouponType;
import product.clicklabs.jugnoo.retrofit.model.FetchCorporatesResponse;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.MediaInfo;
import product.clicklabs.jugnoo.retrofit.model.NearbyPickupRegions;
import product.clicklabs.jugnoo.retrofit.model.Package;
import product.clicklabs.jugnoo.retrofit.model.PaymentResponse;
import product.clicklabs.jugnoo.retrofit.model.RequestRideConfirm;
import product.clicklabs.jugnoo.retrofit.model.ServiceType;
import product.clicklabs.jugnoo.retrofit.model.ServiceTypeValue;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.room.DBObject;
import product.clicklabs.jugnoo.room.apis.DBCoroutine;
import product.clicklabs.jugnoo.room.database.SearchLocationDB;
import product.clicklabs.jugnoo.room.model.SearchLocation;
import product.clicklabs.jugnoo.smartlock.callbacks.SmartlockCallbacks;
import product.clicklabs.jugnoo.smartlock.controller.SmartLockController;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.SupportMailActivity;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.t20.T20Dialog;
import product.clicklabs.jugnoo.t20.T20Ops;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.tutorials.NewUserFlow;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.FrameAnimDrawable;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.NonScrollGridView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.SelectorBitmapLoader;
import product.clicklabs.jugnoo.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.widgets.MySpinner;
import product.clicklabs.jugnoo.widgets.PrefixedEditText;
import product.clicklabs.jugnoo.youtube.YoutubeVideoActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.sabkuchfresh.feed.utils.FeedUtils.dpToPx;
import static product.clicklabs.jugnoo.datastructure.PassengerScreenMode.P_ASSIGNING;
import static product.clicklabs.jugnoo.datastructure.PassengerScreenMode.P_ASSIGNING;
import static product.clicklabs.jugnoo.datastructure.PassengerScreenMode.P_DRIVER_ARRIVED;
import static product.clicklabs.jugnoo.datastructure.PassengerScreenMode.P_INITIAL;

//import com.google.ads.conversiontracking.AdWordsAutomatedUsageReporter;
//import com.google.ads.conversiontracking.AdWordsConversionReporter;


public class HomeActivity extends RazorpayBaseActivity implements AppInterruptHandler,
        SearchListAdapter.SearchListActionsHandler, Constants, OnMapReadyCallback, View.OnClickListener,
        GACategory, GAAction, BidsPlacedAdapter.Callback, ScheduleRideFragment.InteractionListener,
        RideTypesAdapter.OnSelectedCallback, SaveLocationDialog.SaveLocationListener, RentalStationAdapter.RentalStationAdapterOnClickHandler,
        RewardsDialog.ScratchCardRevealedListener, CoroutineScope,
        RideConfirmationDialog.RideRequestConfirmListener, DriverNotFoundDialog.RideRequestConfirmListener, DriverCallDialog.CallDriverListener,
		EditDropDialog.Callback, BadgesAdapter.BadgesClickListener {


    private static final int REQUEST_CODE_LOCATION_SERVICE = 1024;
    private static final int REQ_CODE_PERMISSION_CONTACT = 1000;
    private static final int REQ_CODE_VIDEO = 9112, RESULT_PAUSE = 5;
	private static final int REQUEST_CODE_PAY_VIA_UPI = 1026;


	private float ONGOING_RIDE_PATH_ZINDEX = 2;
	private float PICKUP_TO_DROP_PATH_ZINDEX = 0;

    private final String TAG = "Home Screen";
    private String macId ="";

    public DrawerLayout drawerLayout;

    private BluetoothAdapter mBluetoothAdapter;// views declaration


    MenuBar menuBar;

    TopBar topBar;

    //FABView fabView;
    FABViewTest fabViewTest;


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

    ImageView imageViewRideNow, imageViewInAppCampaign;
    RelativeLayout relativeLayoutInitialSearchBar, relativeLayoutDestSearchBar;
    TextView textViewInitialSearch, textViewDestSearch, tvPickupRentalOutstation;
    ImageView imageViewDropCross;
    ProgressWheel progressBarInitialSearch;
    Button initialMyLocationBtn, changeLocalityBtn, buttonChangeLocalityMyLocation, confirmMyLocationBtn;
    LinearLayout linearLayoutRequestMain;
    RelativeLayout relativeLayoutInAppCampaignRequest;
    TextView textViewInAppCampaignRequest, textViewTotalFare, textViewTotalFareValue, textViewIncludes;
    Button buttonCancelInAppCampaignRequest;
    RecyclerView rvRideTypes;
    RideTypesAdapter rideTypesAdapter;

    RelativeLayout relativeLayoutGoogleAttr;
    ImageView imageViewGoogleAttrCross, imageViewConfirmDropLocationEdit;
    TextView textViewGoogleAttrText;


    //Location Error layout
    RelativeLayout relativeLayoutLocationError;
    RelativeLayout relativeLayoutLocationErrorSearchBar;


    //Assigining layout
    RelativeLayout assigningLayout, rlAssigningNormal, rlAssigningBidding, rlBidTimer;
    TextView textViewFindingDriver, tvBidTimer;
    private ProgressWheel pwBidTimer;
    TextView tvInitialCancelRide, tvRaiseBidValue;
	Button bRaiseOfferFare;
	TextView tvRaiseFareMinus, tvRaiseFarePlus;
	LinearLayout llRaiseBidButton;
    private LinearLayout llFindingADriver;
    public RelativeLayout relativeLayoutAssigningDropLocationParent;
    private RelativeLayout relativeLayoutConfirmBottom, relativeLayoutConfirmRequest;
    private TextView tvPickupAssigning, tvDropAssigning, textViewFellowRider;
    boolean cancelTouchHold = false, placeAdded, zoomAfterFindADriver, fromNaviCurrentLocation;


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
    TextView textViewInRideDriverName, textViewInRideDriverCarNumber, textViewInRideState, textViewDriverRating, tvTermsAndConditions;
    RelativeLayout relativeLayoutDriverRating, relativeLayoutOfferConfirm,layoutAddedTip, rlNotes;
    Button buttonCancelRide, buttonAddMoneyToWallet, buttonCallDriver,buttonTipDriver,buttonAddTipEndRide;
    ImageView ivMoreOptions;
    RelativeLayout relativeLayoutFinalDropLocationParent, relativeLayoutGreat;
    LinearLayout relativeLayoutTotalFare;
    TextView textViewIRPaymentOptionValue, textViewRupee, tvFreeRidesForLife;
    ImageView imageViewIRPaymentOption, imageViewThumbsUpGif, imageViewOfferConfirm, imageViewNotes;
    PopupMenu popupInRide;


    //Search Layout
    RelativeLayout relativeLayoutSearchContainer, relativeLayoutSearch, relativeLayoutPoolSharing;


    //Center Location Layout
    RelativeLayout centreLocationRl, relativeLayoutPinEtaRotate;
    ImageView centreLocationPin, imageViewCenterPinMargin;
    TextView textViewCentrePinETA;


    //End Ride layout
    RelativeLayout endRideReviewRl;
    ScrollView scrollViewRideSummary;
    LinearLayout linearLayoutRideSummaryContainer, linearLayoutRideSummary;
    TextView textViewRSTotalFareValue, textViewRSCashPaidValue,tvDesc;
    LinearLayout linearLayoutRSViewInvoice, linearLayoutSendInvites, linearLayoutPaymentModeConfirm;
	DiscreteScrollView badgesScroll;
	LinearLayout badgesNormal;

	RelativeLayout plusBadge;
    RatingBarMenuFeedback ratingBarRSFeedback;
    TextView textViewRSWhatImprove, textViewRSOtherError;
    NonScrollGridView gridViewRSFeedbackReasons;
    FeedbackReasonsAdapter feedbackReasonsAdapter;
    BadgesAdapter badgesAdapter;

    EditText editTextRSFeedback;
    Button buttonRSSubmitFeedback, buttonRSSkipFeedback;
    TextView textViewRSScroll, textViewChangeLocality;
    private TextView textViewSendInvites, textViewSendInvites2, textViewThumbsDown, textViewThumbsUp, textViewCancellation,
            textViewPaymentModeValueConfirm, textViewOffersConfirm, textVieGetFareEstimateConfirm, textViewPoolInfo1, textViewCouponApplied,
            textViewRideEndWithImage;
    private RelativeLayout changeLocalityLayout, relativeLayoutPoolInfoBar, relativeLayoutRideEndWithImage;
    private LinearLayout linearlayoutChangeLocalityInner;
    private View viewPoolInfoBarAnim;
    private View findDriverJugnooAnimation;
    private AnimationDrawable jugnooAnimation;
    private ImageView imageViewThumbsDown, imageViewThumbsUp, ivEndRideType,
            imageViewPaymentModeConfirm, imageViewRideEndWithImage;
    private Button buttonConfirmRequest, buttonEndRideSkip, buttonEndRideInviteFriends;
    private LinearLayout llPayOnline;
    private TextView tvPayOnline, tvPayOnlineIn,textViewShowFareEstimate;
    private boolean isFromConfirmToOther;
    private CardView cvPayOnline;


    // data variables declaration

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    DecimalFormat decimalFormat1DigitAD = new DecimalFormat("#.#");



    static long previousWaitTime = 0, previousRideTime = 0;
    private final int SEARCH_FLIP_ANIMATION_TIME = 200;
    private final float SEARCH_FLIP_ANIMATION_MARGIN = 20f;
    public final int DESTINATION_PERSISTENCE_TIME = 30; // in minutes
    private final double CHOOSE_SAVED_PICKUP_ADDRESS = 300;

    public static Location myLocation;


    public static UserMode userMode;
    public static PassengerScreenMode passengerScreenMode;


    Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker, dropLocationMarker, dropInitialMarker,
            driverMarkerInRide;
    Polyline pathToDropLocationPolyline;
    PolylineOptions pathToDropLocationPolylineOptions;
    ArrayList<Polyline> polylinesInRideDriverPath = new ArrayList<>();
    ArrayList<PolylineOptions> polylineOptionsInRideDriverPath = new ArrayList<>();

    public static AppInterruptHandler appInterruptHandler;
    public boolean promoSelectionLastOperation = false;
    boolean loggedOut = false,
            zoomedToMyLocation = false,
            mapTouchedOnce = false;
    boolean dontCallRefreshDriver = false, firstTimeZoom = false, zoomingForDeepLink = false;
    boolean searchedALocation = false;

    Dialog noDriversDialog, dialogUploadContacts, freshIntroDialog;
    PushDialog pushDialog;

    LocationFetcher highSpeedAccuracyLF = null;

    PromoCoupon promoCouponSelectedForRide;
    private DriverTipInteractor driverTipInteractor;


    public static final long LOCATION_UPDATE_TIME_PERIOD = 1 * 10000; //in milliseconds

    public static final int RIDE_ELAPSED_PATH_COLOR = Color.RED;
    public static final int RIDE_LEFT_PATH = Color.BLUE;

    public static final double MIN_BALANCE_ALERT_VALUE = 100; //in Rupees

    public static final float LOW_POWER_ACCURACY_CHECK = 2000, HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
    public static final float WAIT_FOR_ACCURACY_UPPER_BOUND = 2000, WAIT_FOR_ACCURACY_LOWER_BOUND = 200;  //in meters

    public static final double MAP_PAN_DISTANCE_CHECK = 50; // in meters
    public static final double MIN_DISTANCE_FOR_REFRESH = 50; // in meters
    public static final double MIN_DISTANCE_FOR_PICKUP_POINT_UPDATE = 50; // in meters

    public static final float MAX_ZOOM = 16;
    private static final int MAP_ANIMATE_DURATION = 400;

    public static final double FIX_ZOOM_DIAGONAL = 100;
    private final float MAP_PADDING = 100f;

    public static final long FETCH_WALLET_BALANCE_REFRESH_TIME = 5 * 60 * 1000;


    private final String GOOGLE_ADWORD_CONVERSION_ID = "947755540";




    public boolean activityResumed = false;
    public static boolean feedbackSkipped = false;

    public ASSL assl;


    private int showAllDrivers = 0, showDriverInfo = 0, rating = 0, jugnooPoolFareId = 0;

    public boolean intentFired = false, dropLocationSearched = false, confirmedScreenOpened, specialPickupScreenOpened;


    CallbackManager callbackManager;
    public final int FARE_ESTIMATE = 4;
    private String dropLocationSearchText = "";
    public SlidingBottomPanelV4 slidingBottomPanel;

    private T20Ops t20Ops = new T20Ops();
    private PlaceSearchListFragment.PlaceSearchMode placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.PICKUP;
    private LatLngBounds.Builder latLngBoundsBuilderPool;
    private ArrayList<SearchResult> lastDestination = new ArrayList<SearchResult>();
    private long thumbsUpGifStartTime = 0;
    private int shakeAnim = 0;

    private PokestopHelper pokestopHelper;
    ImageView imageViewPokemonOnOffInitial, imageViewPokemonOnOffConfirm, imageViewPokemonOnOffEngaged;
    private Bundle bundle;
    public float scale = 0f;
    private boolean rideNowClicked = false;
    private String dropAddressName = "";
    private RelativeLayout relativeLayoutSlidingBottomParent;
    private View viewSlidingExtra;
    private View fabViewIntial;
    private View fabViewFinal;
    private SpecialPickupItemsAdapter specialPickupItemsAdapter;
    private RelativeLayout rlSelectedPickup, rlSpecialPickup;
    private TextView tvSelectedPickup, tvSpecialPicupTitle, tvSpecialPicupDesc;
    private ImageView ivSpecialPickupArrow;
    private Button bSpecialPicupConfirmRequest, specialPickupLocationBtn;
    public ArrayList<NearbyPickupRegions.HoverInfo> specialPickups = new ArrayList<>();
    private MySpinner spin;
    private boolean specialPickupSelected;
    private String selectedSpecialPickup = "";
    /*private RelativeLayout relativeLayoutFAB;
    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton fabDelivery;
    private FloatingActionButton fabMeals;
    private FloatingActionButton fabFresh;
    private FloatingActionButton fabAutos;
    private View fabExtra;*/
    private TextView bChatDriver;
    private RelativeLayout rlChatDriver;
    private TextView tvChatCount;
    private ArrayList<Marker> markersSpecialPickup = new ArrayList<>();
    private ArrayList<MarkerOptions> markerOptionsSpecialPickup = new ArrayList<>();
//    private float mapPaddingSpecialPickup = 268f, mapPaddingConfirm = 238f;
    private boolean setPickupAddressZoomedOnce = false;
    private float previousZoomLevel = -1.0f;
    private TransactionUtils transactionUtils;
    public RelativeLayout relativeLayoutContainer,scheduleRideContainer;
    private FrameLayout coordinatorLayout;
    private HippoNotificationConfig fuguNotificationConfig = new HippoNotificationConfig();
    public Gson gson = new Gson();
    private boolean addressPopulatedFromDifferentOffering;


    private RecyclerView rvBidsIncoming;
    private BidsPlacedAdapter bidsPlacedAdapter;

    private RelativeLayout rlThumbsType;
    private LinearLayout llRatingFeedbackType;
    private TextView tvTipAmountLabel,tvEditTip,tvRemoveTip;
    public static final int REQ_CODE_ADD_CARD_DRIVER_TIP = 0x167;
    public static final int REQ_BLE_ENABLE = 188;
    public boolean scheduleRideOpen;
    public int selectedIdForScheduleRide,selectedRideTypeForScheduleRide;
    public Region selectedRegionForScheduleRide = null;
    public boolean isScheduleRideEnabled,showScheduleRideTut ;
    private Runnable scheduleRideRunnable =  new Runnable() {
        @Override
        public void run() {
            topBar.tvScheduleRidePopup.setVisibility(View.GONE);
        }
    };

    private RecyclerView recyclerViewVehiclesConfirmRide;
    private VehiclesTabAdapter vehiclesTabAdapterConfirmRide;
    private Polyline polylineP2D;
    private PolylineOptions polylineOptionsP2D;

    private ImageView ivLikePickup, ivLikeDrop;
    private LinearLayout llFeedbackMain, llAddTip;
    private TextView tvTipFirst, tvTipSecond, tvTipThird, tvSkipTip,tvDriverName;
    private PrefixedEditText etTipOtherValue;
    private Button bPayTip;
    private double tipSelected;
    private TextWatcher textWatcherOtherTip;

    private ConstraintLayout constraintLayoutRideTypeConfirm;
    private ImageView ivRideTypeImage;
    private TextView tvRideTypeInfo, tvRideTypeRateInfo;
    private Button buttonConfirmRideType;
    private TextView tvHourlyPackage,tvMultipleStops,tvSafe,tvOneWayTrip,tvRoundTrips,tvAdvanceBookings;
    private String mNotes = "";
    private boolean mIsPickup = false, isPickupSet = false;

    private  ApiAddHomeWorkAddress apiAddHomeWorkAddress;


    // RENTALS

    private boolean checkLockStatus = false; // true -> lock , false -> unlock
    private String qrCode = "";
    private String qrCodeDetails = "";

    private Button buttonEndRide, buttonLockRide, buttonUnlockRide;
    Button damageReportButton;
    private RelativeLayout rentalInRideLayout;


    // Rental End Ride Layout
    private LinearLayout rentalEndRideLayout;
    private TextView textViewEndRide;


    private View endRideJugnooAnimation;
    private AnimationDrawable rentalJugnooAnimation;
    private Button customerLocation;
//    public static int rentalInRideStatus = RentalRideStatus.ONGOING.getOrdinal();
    Dialog dialogRentalStations;

    boolean isNewUI = false;

    RelativeLayout relativeLayoutSearchContainerNew, relativeLayoutDestSearchBarNew, relativeLayoutInitialSearchBarNew;
    TextView textViewDestSearchNew,textViewInitialSearchNew;
    ImageView imageViewDropCrossNew;
    LinearLayout linearLayoutConfirmOption,linearLayoutBidValue;
    EditText editTextBidValue;
    private int regionIdFareSetInETBid;
    private SearchLocationDB searchLocationDB;

    private CardView cvTutorialBanner;
    private TextView tvTutorialBanner, tvAddedTip;
    private ImageView ivCrossTutorialBanner;
    private String mLogMsg;
    private Integer mRequestType = 0, mRequestLevelndex = 0;

    private LinearLayout llPayViaUpi;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchLocationDB = DBObject.INSTANCE.getInstance();
        DBCoroutine.Companion.deleteLocationIfDatePassed(searchLocationDB);

        String languageToLoad = LocaleHelper.getLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        GAUtils.trackScreenView(RIDES + HOME);
        Data.currentActivity = HomeActivity.class.getName();

       /* if((Data.userData.getShowHomeScreen() == 1)
                && ((Data.userData.getFreshEnabled() != 0) || (Data.userData.getMealsEnabled() != 0)
                || (Data.userData.getGroceryEnabled() != 0) || (Data.userData.getMenusEnabled() != 0)
                || (Data.userData.getPayEnabled() != 0)))
        {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyApplication.getInstance().getAppSwitcher().switchApp(HomeActivity.this,
                           Prefs.with(HomeActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
                            getIntent().getData(), getCurrentPlaceLatLng(), true);
                }
            }, 500);
            Data.userData.setShowHomeScreen(0);
        }*/


        try {
            setContentView(R.layout.activity_home);
        } catch (Exception e) {
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


        isNewUI = Data.autoData != null && Data.autoData.getNewUIFlag();


        callbackManager = CallbackManager.Factory.create();

        HomeActivity.appInterruptHandler = HomeActivity.this;


        showAllDrivers = Prefs.with(this).getInt(SPLabels.SHOW_ALL_DRIVERS, 0);
        showDriverInfo = Prefs.with(this).getInt(SPLabels.SHOW_DRIVER_INFO, 0);
        isScheduleRideEnabled = Prefs.with(this).getBoolean(Constants.SCHEDULE_RIDE_ENABLED, false);
        if(Data.autoData != null && Data.autoData.getServiceTypeSelected() != null) {
            Data.autoData.getServiceTypeSelected().setScheduleAvailable(isScheduleRideEnabled ? 1 : 0);
        }
        showScheduleRideTut = Prefs.with(this).getBoolean(Constants.SHOW_TUT_SCHEDULE_RIDE, true);
        activityResumed = false;
        dropLocationSearched = false;

        loggedOut = false;
        zoomedToMyLocation = false;
        dontCallRefreshDriver = false;
        mapTouchedOnce = false;
        firstTimeZoom = false;
        searchedALocation = false;
        zoomingForDeepLink = false;
        freshIntroDialog = null;
        rideNowClicked = false;


        coordinatorLayout = (FrameLayout) findViewById(R.id.coordinatorLayout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        assl = new ASSL(HomeActivity.this, coordinatorLayout, 1134, 720, false);


        //Swipe menu
        menuBar = new MenuBar(this, drawerLayout);

        slidingBottomPanel = new SlidingBottomPanelV4(HomeActivity.this, drawerLayout);


        //Top RL
        topBar = new TopBar(this, drawerLayout);

        //FAB View
        //fabView = new FABView(this);


        //Map Layout
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapTouched = false;
        scale = getResources().getDisplayMetrics().density;


        //Passenger main layout
        passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);


        //Initial layout
        initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
        changeLocalityLayout = (RelativeLayout) findViewById(R.id.changeLocalityLayout);
        linearlayoutChangeLocalityInner = (LinearLayout) findViewById(R.id.linearlayoutChangeLocalityInner);
        textViewChangeLocality = (TextView) findViewById(R.id.textViewChangeLocality);
        textViewChangeLocality.setTypeface(Fonts.mavenLight(this));
        buttonChangeLocalityMyLocation = (Button) findViewById(R.id.buttonChangeLocalityMyLocation);

        initialMyLocationBtn = (Button) findViewById(R.id.initialMyLocationBtn);
        confirmMyLocationBtn = (Button) findViewById(R.id.confirmMyLocationBtn);
        changeLocalityBtn = (Button) findViewById(R.id.changeLocalityBtn);
        changeLocalityBtn.setTypeface(Fonts.mavenRegular(this));

        initialMyLocationBtn.setVisibility(View.VISIBLE);
        changeLocalityLayout.setVisibility(View.GONE);

        imageViewRideNow = (ImageView) findViewById(R.id.imageViewRideNow);

        imageViewInAppCampaign = (ImageView) findViewById(R.id.imageViewInAppCampaign);
        imageViewInAppCampaign.setVisibility(View.GONE);
        linearLayoutRequestMain = (LinearLayout) findViewById(R.id.linearLayoutRequestMain);
        linearLayoutRequestMain.setVisibility(View.VISIBLE);
        relativeLayoutInAppCampaignRequest = (RelativeLayout) findViewById(R.id.relativeLayoutInAppCampaignRequest);
        relativeLayoutInAppCampaignRequest.setVisibility(View.GONE);
        textViewInAppCampaignRequest = (TextView) findViewById(R.id.textViewInAppCampaignRequest);
        textViewInAppCampaignRequest.setTypeface(Fonts.mavenLight(this));
        buttonCancelInAppCampaignRequest = (Button) findViewById(R.id.buttonCancelInAppCampaignRequest);
        buttonCancelInAppCampaignRequest.setTypeface(Fonts.mavenRegular(this));

		rvRideTypes = findViewById(R.id.rvRideTypes);
		rvRideTypes.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(this,
				LinearLayoutManager.HORIZONTAL, false));

        relativeLayoutSearchContainer = (RelativeLayout) findViewById(R.id.relativeLayoutSearchContainer);
        relativeLayoutInitialSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutInitialSearchBar);
        relativeLayoutDestSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutDestSearchBar);
        textViewInitialSearch = (TextView) findViewById(R.id.textViewInitialSearch);
        tvPickupRentalOutstation = (TextView) findViewById(R.id.tvPickupRentalOutstation);
        textViewInitialSearch.setTypeface(Fonts.mavenRegular(this));
        textViewDestSearch = (TextView) findViewById(R.id.textViewDestSearch);
        tvPickupRentalOutstation = (TextView) findViewById(R.id.tvPickupRentalOutstation);
        textViewDestSearch.setTypeface(Fonts.mavenRegular(this));
        tvPickupRentalOutstation.setTypeface(Fonts.mavenRegular(this));

        ivLikePickup = findViewById(R.id.ivLikePickup);
        ivLikeDrop = findViewById(R.id.ivLikeDrop);
        ivLikePickup.setVisibility(getLikePickupDropVisibility());
        ivLikeDrop.setVisibility(View.GONE);

        progressBarInitialSearch = (ProgressWheel) findViewById(R.id.progressBarInitialSearch);
        progressBarInitialSearch.setVisibility(View.GONE);
        imageViewDropCross = (ImageView) findViewById(R.id.imageViewDropCross);
        imageViewDropCross.setVisibility(View.GONE);
        relativeLayoutGreat = (RelativeLayout) findViewById(R.id.relativeLayoutGreat);
        imageViewThumbsUpGif = (ImageView) findViewById(R.id.imageViewThumbsUpGif);
        imageViewConfirmDropLocationEdit = (ImageView) findViewById(R.id.imageViewConfirmDropLocationEdit);
        relativeLayoutRideEndWithImage = (RelativeLayout) findViewById(R.id.relativeLayoutRideEndWithImage);
        textViewRideEndWithImage = (TextView) findViewById(R.id.textViewRideEndWithImage);
        textViewRideEndWithImage.setTypeface(Fonts.mavenMedium(this));
        buttonEndRideSkip = (Button) findViewById(R.id.buttonEndRideSkip);
        buttonEndRideSkip.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        buttonEndRideInviteFriends = (Button) findViewById(R.id.buttonEndRideInviteFriends);
        buttonEndRideInviteFriends.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        imageViewRideEndWithImage = (ImageView) findViewById(R.id.imageViewRideEndWithImage);

        relativeLayoutGoogleAttr = (RelativeLayout) findViewById(R.id.relativeLayoutGoogleAttr);
        imageViewGoogleAttrCross = (ImageView) findViewById(R.id.imageViewGoogleAttrCross);
        textViewGoogleAttrText = (TextView) findViewById(R.id.textViewGoogleAttrText);
        textViewGoogleAttrText.setTypeface(Fonts.mavenMedium(this));
        relativeLayoutGoogleAttr.setVisibility(View.GONE);
        relativeLayoutPoolSharing = (RelativeLayout) findViewById(R.id.relativeLayoutPoolSharing);
        textViewFellowRider = (TextView) findViewById(R.id.textViewFellowRider);
        textViewFellowRider.setTypeface(Fonts.mavenMedium(this));
        relativeLayoutConfirmBottom = (RelativeLayout) findViewById(R.id.relativeLayoutConfirmBottom);
        relativeLayoutConfirmRequest = (RelativeLayout) findViewById(R.id.relativeLayoutConfirmRequest);
        relativeLayoutConfirmRequest.setVisibility(View.GONE);
        relativeLayoutTotalFare = findViewById(R.id.relativeLayoutTotalFare);
		textViewRupee = findViewById(R.id.textViewRupee);
		textViewRupee.setTypeface(Fonts.mavenMedium(this));
		tvFreeRidesForLife = findViewById(R.id.tvFreeRidesForLife); tvFreeRidesForLife.setTypeface(Fonts.mavenMedium(this));
        buttonConfirmRequest = (Button) findViewById(R.id.buttonConfirmRequest);
        buttonConfirmRequest.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        linearLayoutPaymentModeConfirm = (LinearLayout) findViewById(R.id.linearLayoutPaymentModeConfirm);
        imageViewPaymentModeConfirm = (ImageView) findViewById(R.id.imageViewPaymentModeConfirm);
        textViewPaymentModeValueConfirm = (TextView) findViewById(R.id.textViewPaymentModeValueConfirm);
        textViewPaymentModeValueConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewOffersConfirm = (TextView) findViewById(R.id.textViewOffersConfirm);
        textViewOffersConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textVieGetFareEstimateConfirm = (TextView) findViewById(R.id.textVieGetFareEstimateConfirm);
        textVieGetFareEstimateConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        imageViewOfferConfirm = (ImageView) findViewById(R.id.imageViewOfferConfirm);
        imageViewNotes = (ImageView) findViewById(R.id.imageViewNotes);
        imageViewOfferConfirm.setVisibility(View.GONE);
        imageViewNotes.setVisibility(View.GONE);
        relativeLayoutOfferConfirm = (RelativeLayout) findViewById(R.id.linearLayoutOfferConfirm);
        rlNotes = findViewById(R.id.rlNotes);
        relativeLayoutPoolInfoBar = (RelativeLayout) findViewById(R.id.relativeLayoutPoolInfoBar);
        viewPoolInfoBarAnim = findViewById(R.id.viewPoolInfoBarAnim);
        viewPoolInfoBarAnim.setVisibility(View.VISIBLE);
        textViewPoolInfo1 = (TextView) findViewById(R.id.textViewPoolInfo1);
        textViewCouponApplied = (TextView) findViewById(R.id.tv_coupon_applied);
        textViewPoolInfo1.setTypeface(Fonts.mavenMedium(this));
        textViewCouponApplied.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        if (Data.isMenuTagEnabled(MenuInfoTags.OFFERS)) {
            relativeLayoutOfferConfirm.setVisibility(View.VISIBLE);
            findViewById(R.id.ivOfferConfirmDiv).setVisibility(View.VISIBLE);
        } else {
            relativeLayoutOfferConfirm.setVisibility(View.GONE);
            findViewById(R.id.ivOfferConfirmDiv).setVisibility(View.GONE);
        }


        //Location error layout
        relativeLayoutLocationError = (RelativeLayout) findViewById(R.id.relativeLayoutLocationError);
        relativeLayoutLocationErrorSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutLocationErrorSearchBar);
        ((TextView) findViewById(R.id.textViewLocationErrorSearch)).setTypeface(Fonts.mavenMedium(this));
        relativeLayoutLocationError.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textViewThanks)).setTypeface(Fonts.avenirNext(this), Typeface.BOLD);


        //Assigning layout
        assigningLayout = (RelativeLayout) findViewById(R.id.assigningLayout);
		rlAssigningNormal = findViewById(R.id.rlAssigningNormal);
		rlAssigningBidding = findViewById(R.id.rlAssigningBidding);
		rlBidTimer = findViewById(R.id.rlBidTimer);
        textViewFindingDriver = (TextView) findViewById(R.id.textViewFindingDriver);
        textViewFindingDriver.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        pwBidTimer = (ProgressWheel) findViewById(R.id.pwBidTimer);
        tvBidTimer = (TextView) findViewById(R.id.tvBidTimer);
        tvBidTimer.setTypeface(Fonts.mavenMedium(this));
        tvInitialCancelRide = findViewById(R.id.tvInitialCancelRide);
        tvInitialCancelRide.setTypeface(Fonts.mavenRegular(this));
		bRaiseOfferFare = findViewById(R.id.bRaiseOfferFare);
		bRaiseOfferFare.setTypeface(Fonts.mavenMedium(this));
		bRaiseOfferFare.setVisibility(View.GONE);
		llRaiseBidButton = findViewById(R.id.llRaiseBidButton);
		tvRaiseFareMinus = findViewById(R.id.tvRaiseFareMinus); tvRaiseFareMinus.setTypeface(Fonts.mavenMedium(this));
		tvRaiseFarePlus = findViewById(R.id.tvRaiseFarePlus); tvRaiseFarePlus.setTypeface(Fonts.mavenMedium(this));
		tvRaiseBidValue = findViewById(R.id.tvRaiseBidValue); tvRaiseBidValue.setTypeface(Fonts.mavenMedium(this));
        findDriverJugnooAnimation = findViewById(R.id.findDriverJugnooAnimation);
        if (findDriverJugnooAnimation instanceof ImageView) {
            jugnooAnimation = (AnimationDrawable) findDriverJugnooAnimation.getBackground();
        }
        rvBidsIncoming = (RecyclerView) findViewById(R.id.rvBidsIncoming);
        rvBidsIncoming.setHasFixedSize(false);
        rvBidsIncoming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bidsPlacedAdapter = new BidsPlacedAdapter(this, rvBidsIncoming, this);
        rvBidsIncoming.setAdapter(bidsPlacedAdapter);
        rvBidsIncoming.setVisibility(View.GONE);


		llFindingADriver = findViewById(R.id.llFindingADriver);
        relativeLayoutAssigningDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutAssigningDropLocationParent);
		tvPickupAssigning = findViewById(R.id.tvPickupAssigning);
		tvPickupAssigning.setTypeface(Fonts.mavenMedium(this));
		tvPickupAssigning.setText("");
		tvDropAssigning = findViewById(R.id.tvDropAssigning);
		tvDropAssigning.setTypeface(Fonts.mavenMedium(this));
		tvDropAssigning.setText("");


        //Request Final Layout
        requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);

        relativeLayoutInRideInfo = (RelativeLayout) findViewById(R.id.relativeLayoutInRideInfo);
        textViewInRidePromoName = (TextView) findViewById(R.id.textViewInRidePromoName);
        textViewInRidePromoName.setTypeface(Fonts.mavenRegular(this));
        linearLayoutSurgeContainer = (LinearLayout) findViewById(R.id.linearLayoutSurgeContainer);
        textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor);
        textViewInRideFareFactor.setTypeface(Fonts.mavenMedium(this));
        relativeLayoutFinalDropLocationClick = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationClick);
        textViewFinalDropLocationClick = (TextView) findViewById(R.id.textViewFinalDropLocationClick);
        textViewFinalDropLocationClick.setTypeface(Fonts.mavenMedium(this));
        textViewFinalDropLocationClick.setText("");
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
        tvTermsAndConditions = (TextView) findViewById(R.id.tvTermsAndConditions);
        textViewDriverRating.setTypeface(Fonts.mavenMedium(this));
        tvTermsAndConditions.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        relativeLayoutDriverRating = (RelativeLayout) findViewById(R.id.relativeLayoutDriverRating);
        textViewCancellation = (TextView) findViewById(R.id.textViewCancellation);
        textViewCancellation.setTypeface(Fonts.mavenRegular(this));
        textViewTotalFare = (TextView) findViewById(R.id.textViewTotalFare);
        textViewTotalFare.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewTotalFareValue = (TextView) findViewById(R.id.textViewTotalFareValue);
        textViewTotalFareValue.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewIncludes = (TextView) findViewById(R.id.textViewIncludes);
        textViewIncludes.setTypeface(Fonts.mavenMedium(this));

        buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
        buttonCancelRide.setTypeface(Fonts.mavenRegular(this));
        ivMoreOptions = (ImageView) findViewById(R.id.ivMoreOptions);
        layoutAddedTip = findViewById(R.id.layout_added_tip);
        tvTipAmountLabel = findViewById(R.id.tvTipAmount);
        tvTipAmountLabel.setTypeface(Fonts.mavenRegular(this));
        tvEditTip = findViewById(R.id.tvEditTip);
        tvEditTip.setTypeface(Fonts.mavenRegular(this));
        tvRemoveTip = findViewById(R.id.tvRemoveTip);
        tvRemoveTip.setTypeface(Fonts.mavenRegular(this));
        View.OnClickListener tipClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvEditTip:
                        showDriverTipDialog();
                        break;
                    case R.id.tvRemoveTip:
                        getDriverTipInteractor().deleteTip();
                        break;

                }
            }
        };
        tvEditTip.setOnClickListener(tipClickListener);
        tvRemoveTip.setOnClickListener(tipClickListener);

        buttonAddMoneyToWallet = (Button) findViewById(R.id.buttonAddMoneyToWallet);
        buttonAddMoneyToWallet.setTypeface(Fonts.mavenRegular(this));
        buttonCallDriver = (Button) findViewById(R.id.buttonCallDriver);
        buttonCallDriver.setTypeface(Fonts.mavenRegular(this));
        buttonTipDriver = (Button) findViewById(R.id.buttonTipDriver);
        buttonAddTipEndRide = (Button) findViewById(R.id.buttonAddTipEndRide);
        buttonTipDriver.setTypeface(Fonts.mavenRegular(this));
        buttonAddTipEndRide.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutFinalDropLocationParent = (RelativeLayout) findViewById(R.id.relativeLayoutFinalDropLocationParent);
        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
        scheduleRideContainer = (RelativeLayout) findViewById(R.id.scheduleRideContainer);


        textViewIRPaymentOptionValue = (TextView) findViewById(R.id.textViewIRPaymentOptionValue);
        textViewIRPaymentOptionValue.setTypeface(Fonts.mavenMedium(this));
        imageViewIRPaymentOption = (ImageView) findViewById(R.id.imageViewIRPaymentOption);

        linearLayoutSendInvites = (LinearLayout) findViewById(R.id.linearLayoutSendInvites);
        textViewSendInvites = (TextView) findViewById(R.id.textViewSendInvites);
        textViewSendInvites.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        textViewSendInvites2 = (TextView) findViewById(R.id.textViewSendInvites2);
        textViewSendInvites2.setTypeface(Fonts.mavenRegular(this));


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
        textViewRSTotalFareValue = (TextView) findViewById(R.id.textViewRSTotalFareValue);
        textViewRSTotalFareValue.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewRSTotalFare)).setTypeface(Fonts.mavenMedium(this));
        textViewRSCashPaidValue = (TextView) findViewById(R.id.textViewRSCashPaidValue);
        tvDesc=findViewById(R.id.tvAddCompliment);
        textViewRSCashPaidValue.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewRSCashPaid)).setTypeface(Fonts.mavenMedium(this));
        linearLayoutRSViewInvoice = (LinearLayout) findViewById(R.id.linearLayoutRSViewInvoice);
        ((TextView) findViewById(R.id.textViewRSInvoice)).setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewRSRateYourRide)).setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        ivEndRideType = (ImageView) findViewById(R.id.ivEndRideType);
        imageViewThumbsDown = (ImageView) findViewById(R.id.imageViewThumbsDown);
        imageViewThumbsUp = (ImageView) findViewById(R.id.imageViewThumbsUp);
        textViewThumbsDown = (TextView) findViewById(R.id.textViewThumbsDown);
        textViewThumbsDown.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewThumbsUp = (TextView) findViewById(R.id.textViewThumbsUp);
        textViewThumbsUp.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        llPayOnline = (LinearLayout) findViewById(R.id.llPayOnline);
        cvPayOnline = findViewById(R.id.cvPayOnline);
        tvPayOnline = (TextView) findViewById(R.id.tvPayOnline);
        tvPayOnline.setTypeface(tvPayOnline.getTypeface(), Typeface.BOLD);
        tvPayOnlineIn = (TextView) findViewById(R.id.tvPayOnlineIn);
        llFeedbackMain = findViewById(R.id.llFeedbackMain);
        llAddTip = findViewById(R.id.llAddTip);
        ((TextView)findViewById(R.id.tvTipDriver)).setTypeface(((TextView)findViewById(R.id.tvTipDriver)).getTypeface(), Typeface.BOLD);
        tvTipFirst = findViewById(R.id.tvTipFirst);
        tvTipSecond = findViewById(R.id.tvTipSecond);
        tvTipThird = findViewById(R.id.tvTipThird);
        tvDriverName=findViewById(R.id.driverName);
        tvSkipTip = findViewById(R.id.tvSkipTip);
        etTipOtherValue = findViewById(R.id.etTipOtherValue);
        bPayTip = findViewById(R.id.bPayTip);
        etTipOtherValue.clearFocus();

		llPayViaUpi = findViewById(R.id.llPayViaUpi);

        tvTipFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tipSelected = (double) v.getTag();
                onTipSelected(true);
            }
        });
        tvTipSecond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tipSelected = (double) v.getTag();
                onTipSelected(true);
            }
        });
        tvTipThird.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tipSelected = (double) v.getTag();
                onTipSelected(true);
            }
        });
        tvSkipTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.autoData.getEndRideData().setDriverTipAmount(1);
                updateDriverTipUI(passengerScreenMode);
            }
        });
        bPayTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.autoData != null && Data.autoData.getEndRideData() != null) {
                    if(tipSelected <= 0){
                        Utils.showToast(HomeActivity.this, getString(R.string.please_select_some_amount));
                        return;
                    }
                    if (Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                        getPaymentOptionDialogForTip().show(Data.autoData.getEndRideData().getPaymentOption(), getString(R.string.pay_for_tip_using));
                    } else {
                        getDriverTipInteractor().addTip(tipSelected, -1);
                    }
                }
            }
        });
        textWatcherOtherTip = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    tipSelected = Double.parseDouble(s.toString());
                    onTipSelected(false);
                } catch(Exception ignored){}
            }
        };
        etTipOtherValue.addTextChangedListener(textWatcherOtherTip);


        rlChatDriver = (RelativeLayout) findViewById(R.id.rlChatDriver);
        bChatDriver = (TextView) findViewById(R.id.bChatDriver);
        bChatDriver.setOnClickListener(this);
        tvChatCount = (TextView) findViewById(R.id.tvChatCount);
        tvChatCount.setTypeface(Fonts.mavenMedium(this));


        ratingBarRSFeedback = (RatingBarMenuFeedback) findViewById(R.id.ratingBarRSFeedback);
        badgesScroll=findViewById(R.id.badges);
        badgesNormal=findViewById(R.id.badgesNormal);
        plusBadge=findViewById(R.id.plus_badge);
        ratingBarRSFeedback.setScore(0, false);
        textViewRSWhatImprove = (TextView) findViewById(R.id.textViewRSWhatImprove);
        textViewRSWhatImprove.setTypeface(Fonts.mavenLight(this));
        textViewRSOtherError = (TextView) findViewById(R.id.textViewRSOtherError);
        textViewRSOtherError.setTypeface(Fonts.mavenLight(this));
        gridViewRSFeedbackReasons = (NonScrollGridView) findViewById(R.id.gridViewRSFeedbackReasons);

        // Special Pickup views
        rlSpecialPickup = (RelativeLayout) findViewById(R.id.rlSpecialPickup);
        specialPickupLocationBtn = (Button) findViewById(R.id.specialPickupLocationBtn);
        spin = (MySpinner) findViewById(R.id.simpleSpinner);
        tvSpecialPicupTitle = (TextView) findViewById(R.id.tvSpecialPicupTitle);
        tvSpecialPicupTitle.setTypeface(Fonts.mavenMedium(this));
        tvSpecialPicupDesc = (TextView) findViewById(R.id.tvSpecialPicupDesc);
        tvSpecialPicupDesc.setTypeface(Fonts.mavenRegular(this));
        rlSelectedPickup = (RelativeLayout) findViewById(R.id.rlSelectedPickup);
        tvSelectedPickup = (TextView) findViewById(R.id.tvSelectedPickup);
        ivSpecialPickupArrow = (ImageView) findViewById(R.id.ivSpecialPickupArrow);
        bSpecialPicupConfirmRequest = (Button) findViewById(R.id.bSpecialPicupConfirmRequest);
        bSpecialPicupConfirmRequest.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        specialPickupItemsAdapter = new SpecialPickupItemsAdapter(HomeActivity.this, specialPickups);
        try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > Build.VERSION_CODES.JELLY_BEAN) {
                spin.setDropDownWidth((int) (ASSL.Xscale() * 550));
                spin.setDropDownVerticalOffset((int) (ASSL.Xscale() * 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            spin.setAdapter(specialPickupItemsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rlSpecialPickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        spin.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LatLng specialPicupLatLng = new LatLng(Double.parseDouble(Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(position).getLatitude()),
                        Double.parseDouble(Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(position).getLongitude()));
                Data.autoData.getPickupSearchResult().setLatitude(specialPicupLatLng.latitude);
                Data.autoData.getPickupSearchResult().setLongitude(specialPicupLatLng.longitude);
                Log.w("pickuplogging", "special pickup spin itemsel"+Data.autoData.getPickupLatLng());
                getApiFindADriver().setRefreshLatLng(specialPicupLatLng);
                specialPickupSelected = true;
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(specialPicupLatLng, MAX_ZOOM), getMapAnimateDuration(), null);
                addUserCurrentLocationAddressMarker();
                selectedSpecialPickup = Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(position).getText() + ", ";
                String address = selectedSpecialPickup + Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng());
                textViewInitialSearch.setText(address);
                textViewInitialSearchNew.setText(address);
                tvPickupRentalOutstation.setText(address);
                GAUtils.event(RIDES, HOME, SPECIAL_PICKUP_CHOOSED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bSpecialPicupConfirmRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(getApiFindADriver().findADriverNeeded(Data.autoData.getPickupLatLng())){
                    findDriversETACall(true, false);
                } else {
                    requestRideClick();
                }*/
                imageViewRideNow.performClick();
            }
        });

        try {
            feedbackReasonsAdapter = new FeedbackReasonsAdapter(this, Data.autoData.getFeedbackReasons(),
                    new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
                        @Override
                        public void onLastItemSelected(boolean selected, String name) {
                            if (!selected) {
                                if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                                    textViewRSOtherError.setText("");
                                }
                            }
                        }
                        @Override
                        public void showCommentBox(int visibility){
                            if(visibility == View.GONE) {
                                editTextRSFeedback.setText("");
                                Utils.hideKeyboard(HomeActivity.this);
                            }
                            HomeActivity.this.findViewById(R.id.cvAdditionalComments).setVisibility(visibility);
                        }
                    });

            gridViewRSFeedbackReasons.setAdapter(feedbackReasonsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editTextRSFeedback = (EditText) findViewById(R.id.editTextRSFeedback);
        editTextRSFeedback.setTypeface(Fonts.mavenLight(this));
        buttonRSSubmitFeedback = (Button) findViewById(R.id.buttonRSSubmitFeedback);
        buttonRSSubmitFeedback.setTypeface(Fonts.mavenRegular(this));
        buttonRSSkipFeedback = (Button) findViewById(R.id.buttonRSSkipFeedback);
        buttonRSSkipFeedback.setTypeface(Fonts.mavenRegular(this));
        textViewRSScroll = (TextView) findViewById(R.id.textViewRSScroll);

        textViewRSWhatImprove.setVisibility(View.GONE);
        gridViewRSFeedbackReasons.setVisibility(View.GONE);
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
//        layoutParams.height = (int) (ASSL.Yscale() * 200);
//        editTextRSFeedback.setLayoutParams(layoutParams);
        textViewRSOtherError.setText("");
        textViewShowFareEstimate = (TextView)findViewById(R.id.tvShowFareEstimate);
        textViewShowFareEstimate.setTypeface(Fonts.mavenRegular(this));
        textViewShowFareEstimate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        if(Data.autoData!=null && Data.autoData.getRegions().size()==1){
                            getSlidingBottomPanel().getViewPager().setCurrentItem(1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*rlSelectedPickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_in);
                cvSpecialPickup.setVisibility(View.VISIBLE);
                cvSpecialPickup.setAnimation(animation);
            }
        });*/


        try {
            if (Data.autoData != null) {
                textViewSendInvites.setText(Data.autoData.getInRideSendInviteTextBoldV2());
                textViewSendInvites2.setText(Data.autoData.getInRideSendInviteTextNormalV2());
                if (Data.autoData.getConfirmScreenFareEstimateEnable().equalsIgnoreCase("1")) {
                    textVieGetFareEstimateConfirm.setVisibility(View.VISIBLE);
                    findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.VISIBLE);
                } else {
                    textVieGetFareEstimateConfirm.setVisibility(View.GONE);
                    findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        recyclerViewVehiclesConfirmRide = (RecyclerView) findViewById(R.id.recyclerViewVehiclesConfirmRide);
        recyclerViewVehiclesConfirmRide.setLayoutManager(new LinearLayoutManager(HomeActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVehiclesConfirmRide.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVehiclesConfirmRide.setHasFixedSize(false);



        imageViewPokemonOnOffInitial = (ImageView) findViewById(R.id.imageViewPokemonOnOffInitial);
        imageViewPokemonOnOffInitial.setVisibility(View.GONE);
        imageViewPokemonOnOffConfirm = (ImageView) findViewById(R.id.imageViewPokemonOnOffConfirm);
        imageViewPokemonOnOffConfirm.setVisibility(View.GONE);
        imageViewPokemonOnOffEngaged = (ImageView) findViewById(R.id.imageViewPokemonOnOffEngaged);
        imageViewPokemonOnOffEngaged.setVisibility(View.GONE);

        fabViewIntial = (View) findViewById(R.id.fabViewIntial);
        fabViewFinal = (View) findViewById(R.id.fabViewFinal);
//        fabViewTest = new FABViewTest(this, fabViewIntial);
        //relativeLayoutFABTest = (RelativeLayout)findViewById(R.id.relativeLayoutFABTest);
        relativeLayoutSlidingBottomParent = (RelativeLayout) findViewById(R.id.relativeLayoutSlidingBottomParent);
        viewSlidingExtra = (View) findViewById(R.id.viewSlidingExtra);

        constraintLayoutRideTypeConfirm = findViewById(R.id.constraintLayoutRideTypeConfirm);
        ivRideTypeImage = findViewById(R.id.ivRideTypeImage);
        ivRideTypeImage.setVisibility(View.GONE);
        tvRideTypeInfo = findViewById(R.id.tvRideTypeInfo);
        tvRideTypeInfo.setTypeface(Fonts.mavenRegular(this));
        tvRideTypeRateInfo = findViewById(R.id.tvRideTypeRateInfo);
        tvRideTypeRateInfo.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);

        tvHourlyPackage = findViewById(R.id.tvHourlyPackage);
        tvHourlyPackage.setTypeface(Fonts.mavenMedium(this));
        tvMultipleStops = findViewById(R.id.tvMultipleStops);
        tvMultipleStops.setTypeface(Fonts.mavenMedium(this));
        tvSafe = findViewById(R.id.tvSafe);
        tvSafe.setTypeface(Fonts.mavenMedium(this));
        tvAdvanceBookings = findViewById(R.id.tvAdvanceBookings);
        tvAdvanceBookings.setTypeface(Fonts.mavenMedium(this));
        tvRoundTrips = findViewById(R.id.tvRoundTrips);
        tvRoundTrips.setTypeface(Fonts.mavenMedium(this));
        tvOneWayTrip = findViewById(R.id.tvOneWayTrip);
        tvOneWayTrip.setTypeface(Fonts.mavenMedium(this));

        buttonConfirmRideType = findViewById(R.id.buttonConfirmRideType);
        buttonConfirmRideType.setTypeface(Fonts.mavenMedium(this));
        buttonConfirmRideType.setOnClickListener(v -> {
//            if (getFilteredDrivers() == 0) {
//                noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
//            } else {
//                if (Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType())
//                        && Data.autoData.getDropLatLng() == null) {
//                    destinationRequiredShake();
//                    return;
//                }
                topBar.openScheduleFragment(Data.autoData.getServiceTypeSelected(), false);
//            }
        });


        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Utils.hideSoftKeyboard(HomeActivity.this, textViewInitialSearch);
                Utils.hideSoftKeyboard(HomeActivity.this, textViewInitialSearchNew);
                Utils.hideSoftKeyboard(HomeActivity.this, tvPickupRentalOutstation);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        relativeLayoutPoolInfoBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getDeepindex() == AppLinkIndex.OPEN_JEANIE.getOrdinal()
                            && fabViewTest != null) {
                        fabViewTest.menuLabelsRightTest.open(true);
                    } else if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getDeepindex() == -1
                            || slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected()
                            .getDeepindex() == AppLinkIndex.OPEN_COUPONS_DIALOG.getOrdinal()
                            || getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon().getId() > 0) {
                        if (Data.autoData.getRegions().size() == 1) {
                            slidingBottomPanel.slideOnClick(findViewById(R.id.linearLayoutOffers));
                        } else if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.NORMAL.getOrdinal()) {
                            slidingBottomPanel.getRequestRideOptionsFragment().getPromoCouponsDialog().show(
									Data.userData.getCoupons(ProductType.AUTO, HomeActivity.this, false));
                        }
                        GAUtils.event(RIDES, HOME, OFFERS + BAR + CLICKED);
                    } else if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.NORMAL.getOrdinal()) {
                        Data.deepLinkIndex = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getDeepindex();
                        deepLinkAction.openDeepLink(HomeActivity.this, getCurrentPlaceLatLng());
                        GAUtils.event(RIDES, HOME, OFFERS + BAR + CLICKED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        textVieGetFareEstimateConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFareEstimate();


            }
        });

        relativeLayoutOfferConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon().getId() <= 0) {
                        slidingBottomPanel.getRequestRideOptionsFragment().setSelectedCoupon(promoCouponSelectedForRide);
                    }
                    slidingBottomPanel.getRequestRideOptionsFragment().getPromoCouponsDialog().show(
							Data.userData.getCoupons(ProductType.AUTO, HomeActivity.this, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rlNotes.setOnClickListener(v -> {
            try {
                NotesDialog notesDialog = new NotesDialog(HomeActivity.this, mNotes, notes -> {
                    mNotes = notes;
                    if(notes != null && !notes.isEmpty()) {
                        imageViewNotes.setVisibility(View.VISIBLE);
                    } else {
                        imageViewNotes.setVisibility(View.GONE);
                    }
                });
                notesDialog.show(ProductType.AUTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        linearLayoutPaymentModeConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingBottomPanel.getRequestRideOptionsFragment().getPaymentOptionDialog().show(-1, null);
            }
        });

		linearLayoutInRideDriverInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


        // RENTALS

        buttonEndRide = findViewById(R.id.buttonEndRide);
        buttonLockRide = findViewById(R.id.buttonLockRide);
        buttonUnlockRide = findViewById(R.id.buttonUnlockRide);
        buttonEndRide.setOnClickListener(this);
        buttonUnlockRide.setOnClickListener(this);
        buttonLockRide.setOnClickListener(this);

        rentalInRideLayout = findViewById(R.id.layout_rental_in_ride);

        rentalEndRideLayout = findViewById(R.id.layout_rental_end_ride);
        textViewEndRide = findViewById(R.id.textViewEndRide);

        customerLocation = findViewById(R.id.customerLocation);
        endRideJugnooAnimation = findViewById(R.id.jugnoo_animation);
//        if (endRideJugnooAnimation instanceof ImageView) {
//            rentalJugnooAnimation = (AnimationDrawable) endRideJugnooAnimation.getBackground();
//        }


        damageReportButton = findViewById(R.id.damage_button);
        damageReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DamageReportActivity.class);
                startActivity(intent);
            }
        });

        // Customer initial layout events
        rideNowClicked = false;
        imageViewRideNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
					if(Prefs.with(HomeActivity.this).getInt(Constants.KEY_CUSTOMER_PICKUP_ADDRESS_EMPTY_CHECK_ENABLED, 0) == 1
							&& (Data.autoData.getPickupLatLng() == null || TextUtils.isEmpty(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng())))){
						Utils.showToast(HomeActivity.this,getString(R.string.please_confirm_you_have_selected_pickup_address));
						return;
					}
                    topBar.tvScheduleRidePopup.setVisibility(View.GONE);
                    if (map != null) {
                        if (!rideNowClicked) {
                            if (getApiFindADriver().findADriverNeeded(Data.autoData.getPickupLatLng())) {
                                findDriversETACall(true, false, false, null);
                            } else {
                                imageViewRideNowPoolCheck();
                            }
                            getFabViewTest().hideJeanieHelpInSession();
                            rideNowClicked = true;
                            getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rideNowClicked = false;
                                }
                            }, 300);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonConfirmRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.hideKeyboard(HomeActivity.this);
                    if(Prefs.with(HomeActivity.this).getInt(Constants.KEY_CUSTOMER_PICKUP_ADDRESS_EMPTY_CHECK_ENABLED, 0) == 1
							&& (Data.autoData.getPickupLatLng() == null || TextUtils.isEmpty(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng())))){
						Utils.showToast(HomeActivity.this,getString(R.string.please_confirm_you_have_selected_pickup_address));
                    	return;
					}

                    Region region = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
                    if((region.getDestinationMandatory() == 1
                            &&  Data.autoData.getDropLatLng() != null)
                            || region.getDestinationMandatory() == 0) {

                    	//if selected region is for reverse bid or fare mandatory and regionFare is null then we cannot proceed further
                    	if(Prefs.with(HomeActivity.this).getInt(Constants.KEY_CUSTOMER_REGION_FARE_CHECK_ENABLED, 0) == 1
                    			&& (region.getReverseBid() == 1 || region.getFareMandatory() == 1)
								&& (region.getRegionFare() == null || region.getRegionFare().getPoolFareId() <= 0)){
                    		if(Data.autoData.getDropLatLng() == null){
								Utils.showToast(HomeActivity.this,getString(R.string.destination_required));
							} else {
								fareEstimatBeforeRequestRide();
								Utils.showToast(HomeActivity.this,getString(R.string.fares_updated));
							}
                    		return;
						}

                        if(relativeLayoutInitialSearchBarNew.getVisibility() == View.GONE) {
                            relativeLayoutInitialSearchBarNew.setVisibility(View.VISIBLE);
                            isPickupSet = true;
                            setHeightDropAddress(1);
                            setPickupLocationInitialUI();
                            return;
                        }
                        if(Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_REQUEST_RIDE_POPUP, 0) == 1
								&& region.getReverseBid() != 1 && region.getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                            openRequestConfirmDialog();
                        } else {
                            onReqestRideConfirmClick();
                        }
                    } else {
                        Utils.showToast(HomeActivity.this,getString(R.string.destination_required));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        relativeLayoutRideEndWithImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        buttonEndRideSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutRideEndWithImage.setVisibility(View.GONE);
                submitFeedbackToInitial(5);
            }
        });

        buttonEndRideInviteFriends.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToShareActivity(false);
                submitFeedbackToInitial(5);
            }
        });

        tvTermsAndConditions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new VehicleFareEstimateDialog().show(HomeActivity.this,
                        slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());
            }
        });

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

        ivLikePickup.setTag("");
        ivLikePickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ivLikePickup.getTag().toString())){
                    return;
                }
                SearchResult searchResult = Data.autoData.getPickupSearchResult();
                if(TextUtils.isEmpty(searchResult.getAddress())) {
                    Utils.showToast(HomeActivity.this, getString(R.string.please_wait));
                    return;
                }
                Intent intent = new Intent(HomeActivity.this, AddPlaceActivity.class);
                intent.putExtra(Constants.KEY_REQUEST_CODE, searchResult.getPlaceRequestCode());
                intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
                intent.putExtra(Constants.KEY_DIRECT_CONFIRM, true);
                startActivityForResult(intent, searchResult.getPlaceRequestCode());
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                likeClicked = 1;
            }
        });
        ivLikeDrop.setTag("");
        ivLikeDrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ivLikeDrop.getTag().toString())){
                    return;
                }
                String address = Data.autoData.getDropAddress();
                String title = address.equalsIgnoreCase(textViewDestSearch.getText().toString()) ? "" : textViewDestSearch.getText().toString();
                SearchResult searchResult = new SearchResult(title, address, "",
                        Data.autoData.getDropLatLng().latitude, Data.autoData.getDropLatLng().longitude);
                if(TextUtils.isEmpty(searchResult.getAddress())) {
                    Utils.showToast(HomeActivity.this, getString(R.string.please_wait));
                    return;
                }
                Intent intent = new Intent(HomeActivity.this, AddPlaceActivity.class);
                intent.putExtra(Constants.KEY_REQUEST_CODE, searchResult.getPlaceRequestCode());
                intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
                intent.putExtra(Constants.KEY_DIRECT_CONFIRM, true);
                startActivityForResult(intent, searchResult.getPlaceRequestCode());
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                likeClicked = 2;
            }
        });


        relativeLayoutInitialSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ViewGroup viewGroup = ((ViewGroup) relativeLayoutInitialSearchBar.getParent());
                    int index = viewGroup.indexOfChild(relativeLayoutDestSearchBar);
                    if (index == 1 && Data.autoData.getDropLatLng() == null) {
                        translateViewTop(viewGroup, relativeLayoutInitialSearchBar, true, true);
                        translateViewBottom(viewGroup, relativeLayoutDestSearchBar, false, true);
                        if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()
								|| slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getDestinationMandatory() == 1) {
                            textViewDestSearch.setText(getResources().getString(R.string.destination_required));
                            textViewDestSearchNew.setText(getResources().getString(R.string.destination_required));
                        } else {
                            textViewDestSearch.setText(getResources().getString(R.string.enter_destination));
                            textViewDestSearchNew.setText(getResources().getString(R.string.enter_destination));
                        }
                        textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color_light));
                        textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color_light));
                    } else {
                        openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.PICKUP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        relativeLayoutDestSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
                    int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
                    if (index == 1 && Data.autoData.getDropLatLng() == null && !isNewUI) {
                        translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                        translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
                    } else {
                        if(getSlidingBottomPanel().getRequestRideOptionsFragment()
                                .getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()){

                            openRentalStationList();
                            checkFareEstimate = false;
                        }
                        else{
                            openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.DROP);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvPickupRentalOutstation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.PICKUP);
            }
        });

        imageViewDropCross.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Data.autoData.setDropLatLng(null);
                    Data.autoData.setDropAddress("");
                    Data.autoData.setDropAddressId(0);
                    dropLocationSearched = false;
                    if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()
							|| slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getDestinationMandatory() == 1) {
                        textViewDestSearch.setText(R.string.destination_required);
                        textViewDestSearchNew.setText(R.string.destination_required);
                    } else {
                        textViewDestSearch.setText(R.string.enter_destination);
                        textViewDestSearchNew.setText(R.string.enter_destination);
                    }
                    imageViewDropCross.setVisibility(View.GONE);
                    ivLikeDrop.setVisibility(View.GONE);
                    ((ViewGroup) relativeLayoutInitialSearchBar.getParent()).bringChildToFront(relativeLayoutInitialSearchBar);
                    translateViewBottomTop(relativeLayoutDestSearchBar, false);
                    translateViewTopBottom(relativeLayoutInitialSearchBar, true);
                    Prefs.with(HomeActivity.this).save(SPLabels.ENTERED_DESTINATION, "");
                /*if(dropInitialMarker != null) {
                    dropInitialMarker.remove();
                }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.PICKUP);
            }
        });


        //Location error layout
        relativeLayoutLocationError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationGotNow();
                    setServiceAvailablityUI(Data.autoData.getFarAwayCity());

                    callMapTouchedRefreshDrivers(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        relativeLayoutLocationErrorSearchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode.PICKUP);
                locationGotNow();
            }
        });

		tvFreeRidesForLife.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MenuAdapter.onClickAction(MenuInfoTags.FREE_RIDES_NEW.getTag(),0,0,HomeActivity.this,getCurrentPlaceLatLng());
			}
		});


        // Assigning layout events
        textViewCancellation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

		bRaiseOfferFare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Data.autoData.getIsReverseBid() == 1) {
					cancelAndReBid();
				}
			}
		});

		tvRaiseFareMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double innerValue = Prefs.with(HomeActivity.this).getFloat(Constants.KEY_MIN_REGION_FARE, 20.0f);
				if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getReverseBid() == 1
						&& slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionFare()!= null) {
					innerValue = Math.ceil(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionFare().getFare() * 0.8);
				}

				double incrementVal = getBidIncrementValFromServer();
				if(Data.autoData.getChangedBidValue()-incrementVal >= innerValue) {
					Data.autoData.setChangedBidValue(Data.autoData.getChangedBidValue() - incrementVal);
					tvRaiseBidValue.setText(Utils.formatCurrencyValue(Data.autoData.getCurrency(), Data.autoData.getChangedBidValue()));
					bRaiseOfferFare.setEnabled(Data.autoData.getInitialBidValue() != Data.autoData.getChangedBidValue());
				} else {
					Utils.showToast(HomeActivity.this, getString(R.string.offer_cannot_be_less_than_min_value));
				}
			}
		});
		tvRaiseFarePlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double outerValue = Prefs.with(HomeActivity.this).getFloat(Constants.KEY_MAX_REGION_FARE, 5000.0f);
				if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getReverseBid() == 1
						&& slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionFare()!= null) {
					outerValue = Math.ceil(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionFare().getFare() * 10);
				}
				double incrementVal = getBidIncrementValFromServer();
				if(Data.autoData.getChangedBidValue()+incrementVal <= outerValue) {
					Data.autoData.setChangedBidValue(Data.autoData.getChangedBidValue() + incrementVal);
					tvRaiseBidValue.setText(Utils.formatCurrencyValue(Data.autoData.getCurrency(), Data.autoData.getChangedBidValue()));
					bRaiseOfferFare.setEnabled(Data.autoData.getInitialBidValue() != Data.autoData.getChangedBidValue());
				} else {
					Utils.showToast(HomeActivity.this, getString(R.string.offer_cannot_be_more_than_max_value));
				}
			}
		});

        tvInitialCancelRide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!"".equalsIgnoreCase(Data.autoData.getcSessionId())) {
                        cancelCustomerRequestAsync(HomeActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


		tvDropAssigning.setOnClickListener(new OnClickListener() {
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

                try {
                    Data.deepLinkIndex = Data.autoData.getRideStartInviteTextDeepIndexV2();
                    deepLinkAction.openDeepLink(HomeActivity.this, getCurrentPlaceLatLng());
                    GAUtils.event(RIDES, RIDE + IN_PROGRESS, Constants.REFERRAL + CLICKED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // customer request final layout events
        buttonCancelRide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!Data.autoData.getAssignedDriverInfo().getCancelRideThrashHoldTime().equalsIgnoreCase("") &&
                            System.currentTimeMillis() > DateOperations.getMilliseconds(DateOperations.utcToLocal(Data.autoData.getAssignedDriverInfo().getCancelRideThrashHoldTime()))) {
                        new CancellationChargesDialog(HomeActivity.this, new CancellationChargesDialog.Callback() {
                            @Override
                            public void onDialogDismiss() {

                            }

                            @Override
                            public void onYes() {
                                startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            }

                            @Override
                            public void onNo() {

                            }
                        }).showCancellationChargesDialog(Data.autoData.getCancellationChargesPopupTextLine1(), Data.autoData.getCancellationChargesPopupTextLine2());
                    } else {
                        startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        popupInRide = new PopupMenu(HomeActivity.this, ivMoreOptions);
        popupInRide.getMenuInflater().inflate(R.menu.menu_in_ride, popupInRide.getMenu());
        popupInRide.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.cancel){
                    buttonCancelRide.performClick();
                }
                return true;
            }
        });

        ivMoreOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupInRide.show();
            }
        });

        buttonAddMoneyToWallet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    MyApplication.getInstance().getWalletCore().addMoneyToWalletIntent(HomeActivity.this,
                            Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonCallDriver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callDriverDuringRide(HomeActivity.this);
            }
        });
        buttonTipDriver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverTipDialog();

            }
        });
        buttonAddTipEndRide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverTipDialog();

            }
        });


        relativeLayoutFinalDropLocationClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Data.autoData.getAssignedDriverInfo().getIsPooledRide() != 1
							&& Prefs.with(HomeActivity.this).getInt(KEY_REVERSE_BID, 0) != 1) {
                        initDropLocationSearchUI(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


        ratingBarRSFeedback.setOnScoreChanged(new RatingBarMenuFeedback.IRatingBarCallbacks() {
            @Override
            public void scoreChanged(float score) {
                try {
                    int position=(int)Math.abs(score)-1;
                    buttonRSSubmitFeedback.setVisibility(View.VISIBLE);
                    editTextRSFeedback.setText("");
                    findViewById(R.id.cvAdditionalComments).setVisibility(View.GONE);
                    resetFeedBackListClicked(position);
                    feedbackReasonsAdapter.notifyDataSetChanged();
                    plusBadge.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            badgesNormal.setVisibility(View.GONE);
                            badgesScroll.setVisibility(View.VISIBLE);
                            badgesAdapter=new BadgesAdapter(HomeActivity.this,Data.autoData.getFeedBackInfoRatingData().get(position).getImageBadges());
                            badgesScroll.setAdapter(badgesAdapter);
                            badgesScroll.setItemTransformer(new ScaleTransformer.Builder()
                                    .setMaxScale(1.5f)
                                    .setMinScale(0.8f)
                                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                                    .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                                    .build());

                            badgesScroll.setOffscreenItems(3);
							badgesScroll.setItemTransitionTimeMillis(300);

                        }
                    });

                    badgesScroll.setVisibility(View.GONE);


                    if(!Data.autoData.getFeedBackInfoRatingData().isEmpty()){
                        int imageBadgeSize;
                        tvDesc.setVisibility(View.VISIBLE);
                        tvDesc.setText(Data.autoData.getFeedBackInfoRatingData().get(position).getDesc());
                        try {

                            imageBadgeSize=Data.autoData.getFeedBackInfoRatingData().get(position).getImageBadges().size();
                            if(imageBadgeSize>1){

                                Picasso.with(HomeActivity.this).load(Data.autoData.getFeedBackInfoRatingData().get(position).getImageBadges().get(0).getImageAdress()).transform(new CircleTransform()).into(((ImageView)findViewById(R.id.badge1)));
                                Picasso.with(HomeActivity.this).load(Data.autoData.getFeedBackInfoRatingData().get(position).getImageBadges().get(1).getImageAdress()).transform(new CircleTransform()).into(((ImageView)findViewById(R.id.badge2)));
                                badgesNormal.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                badgesNormal.setVisibility(View.GONE);
                            }
                            feedbackReasonsAdapter = new FeedbackReasonsAdapter(HomeActivity.this, Data.autoData.getFeedBackInfoRatingData().get(position).getTextBadges(),
                                    new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
                                        @Override
                                        public void onLastItemSelected(boolean selected, String name) {
                                            if (!selected) {
                                                if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                                                    textViewRSOtherError.setText("");
                                                }
                                            }
                                        }
                                        @Override
                                        public void showCommentBox(int visibility){
                                            if(visibility == View.GONE) {
                                                editTextRSFeedback.setText("");
                                                Utils.hideKeyboard(HomeActivity.this);
                                            }
                                            HomeActivity.this.findViewById(R.id.cvAdditionalComments).setVisibility(visibility);
                                        }
                                    });

                            gridViewRSFeedbackReasons.setAdapter(feedbackReasonsAdapter);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        tvDesc.setVisibility(View.GONE);
                        badgesNormal.setVisibility(View.GONE);
						if (Data.autoData.getFeedbackReasons().size() > 0) {
							if (score > 0 && score <= 3) {
								textViewRSWhatImprove.setVisibility(View.VISIBLE);
								gridViewRSFeedbackReasons.setVisibility(View.VISIBLE);


//                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
//                            layoutParams.height = (int) (ASSL.Yscale() * 150);
//                            editTextRSFeedback.setLayoutParams(layoutParams);
							} else {
								setZeroRatingView();
							}
						}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        changeLocalityLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutRideSummary, textViewRSScroll,
                new KeyboardLayoutListener.KeyBoardStateHandler() {
                    @Override
                    public void keyboardOpened() {
//                        editTextRSFeedback.setHint("");
                        scrollViewRideSummary.smoothScrollTo(0, buttonRSSubmitFeedback.getBottom());
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
                    rating = Math.abs(Math.round(ratingBarRSFeedback.getScore()));
                    Log.e("rating screen =", "= feedbackStr = " + feedbackStr + " , rating = " + rating);

                    String feedbackReasons = feedbackReasonsAdapter.getSelectedReasons();
					if (Data.autoData.getFeedBackInfoRatingData() != null) {
						feedbackReasons = feedbackReasonsAdapter.getSelectedReasonsId();
					}
                    boolean isLastReasonSelected = feedbackReasonsAdapter.isLastSelected();

                    if (0 == rating) {
                        DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.we_take_your_feedback_seriously));
                    } else {
//                        if (Data.autoData.getFeedbackReasons().size() > 0 && rating <= 3) {
//                            if (feedbackReasons.length() > 0) {
//                                if (isLastReasonSelected && feedbackStr.length() == 0) {
//                                    textViewRSOtherError.setText(getString(R.string.star_required));
//                                    return;
//                                }
//                            } else {
//                                DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.please_provide_reason_for_rating));
//                                return;
//                            }
//                        }

                        if (feedbackStr.length() > 150) {
                            editTextRSFeedback.requestFocus();
                            editTextRSFeedback.setError(getString(R.string.review_must_be_in_150));
                        } else {
                            submitFeedbackToDriverAsync(HomeActivity.this, Data.autoData.getcEngagementId(), Data.autoData.getcDriverId(),
                                    rating, feedbackStr, feedbackReasons);
                            goodFeedbackViewType();
                            flurryEventGAForTransaction();
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
                try {
                    skipFeedbackForCustomerAsync(HomeActivity.this, Data.autoData.getcEngagementId());
                    flurryEventGAForTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayoutRSViewInvoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Data.autoData.getEndRideData() != null) {
                        linearLayoutRideSummaryContainerSetVisiblity(View.VISIBLE, RideEndFragmentMode.INVOICE);
                        GAUtils.event(RIDES, FEEDBACK, VIEW_INVOICE + CLICKED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        imageViewThumbsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    rating = 5;
                    //imageViewThumbsDown.clearAnimation();
                    //imageViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_up));
                    //textViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.fade_in));

                    //setZeroRatingView();
                    if (MyApplication.getInstance().isOnline()) {
                        thumbsUpGifStartTime = System.currentTimeMillis();
                        rating = 5;
                        submitFeedbackToDriverAsync(HomeActivity.this, Data.autoData.getcEngagementId(), Data.autoData.getcDriverId(),
                                rating, "", "");
                        goodFeedbackViewType();
                        GAUtils.event(RIDES, RIDE + FINISHED, THUMB_UP + CLICKED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageViewThumbsDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    rating = 1;
                    //linearLayoutRideSummaryContainerSetVisiblity(View.VISIBLE, RideEndFragmentMode.BAD_FEEDBACK);
                    submitFeedbackToDriverAsync(HomeActivity.this, Data.autoData.getcEngagementId(), Data.autoData.getcDriverId(),
                            rating, "", "");
                    if (Data.isFuguChatEnabled()
							|| Data.isHippoTicketForRideEnabled(HomeActivity.this)) {
                        fuguCustomerHelpRides(false);
                    }
                    else if (Data.isMenuTagEnabled(MenuInfoTags.EMAIL_SUPPORT)) {
                        startActivity(new Intent(HomeActivity.this, SupportMailActivity.class));
                    } else {
                        Intent intent = new Intent(HomeActivity.this, SupportActivity.class);
                        intent.putExtra(INTENT_KEY_FROM_BAD, 1);
                        intent.putExtra(KEY_ENGAGEMENT_ID, Integer.parseInt(Data.autoData.getcEngagementId()));
                        intent.putExtra(KEY_PRODUCT_TYPE, ProductType.AUTO.getOrdinal());
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        GAUtils.event(RIDES, RIDE + FINISHED, THUMB_DOWN + CLICKED);
                    }
                    //imageViewThumbsUp.clearAnimation();
                    //imageViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_down));
                    //textViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.fade_in));

                /*textViewRSWhatImprove.setVisibility(View.VISIBLE);
                gridViewRSFeedbackReasons.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
                layoutParams.height = (int) (ASSL.Yscale() * 150);
                editTextRSFeedback.setLayoutParams(layoutParams);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        llPayOnline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Data.autoData.getEndRideData() != null) {
                        if (Prefs.with(HomeActivity.this).getInt(KEY_FORCE_MPESA_PAYMENT, 0) == 1
                                && Data.autoData.getEndRideData().toPay > 0
                                && Data.autoData.getEndRideData().getShowPaymentOptions() == 1) {
                            initiateRideEndPaymentAPI(Data.autoData.getEndRideData().engagementId, PaymentOption.MPESA.getOrdinal());
                        } else {
                            linearLayoutRideSummaryContainerSetVisiblity(View.VISIBLE, RideEndFragmentMode.ONLINE_PAYMENT);
                        }
                        GAUtils.event(RIDES, RIDE + END, ONLINE_PAYMENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

		llPayViaUpi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Data.autoData != null
						&& Data.autoData.getEndRideData() != null
						&& Data.autoData.getEndRideData().toPay > 0
						&& passengerScreenMode == PassengerScreenMode.P_RIDE_END) {
					try {
						Uri uri =
								new Uri.Builder()
										.scheme("upi")
										.authority("pay")
										.appendQueryParameter("pa", Data.autoData.getEndRideData().getDriverUpiId())
										.appendQueryParameter("pn", Data.autoData.getEndRideData().driverName)
										.appendQueryParameter("tr", "" + Data.autoData.getEndRideData().engagementId)
										.appendQueryParameter("tn", "Payment for Jugnoo Ride")
										.appendQueryParameter("am", ""+(Config.getServerUrl().equalsIgnoreCase(BuildConfig.LIVE_URL) ? Data.autoData.getEndRideData().toPay : 1))
										.appendQueryParameter("cu", "INR")
										.build();
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(uri);
						startActivityForResult(intent, REQUEST_CODE_PAY_VIA_UPI);
					} catch (Exception e) {
						e.printStackTrace();
						Utils.showToast(HomeActivity.this, getString(R.string.upi_supported_app_not_installed));
					}
				}
			}
		});


        View.OnClickListener onClickListenerPokeOnOff = new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Prefs.with(HomeActivity.this).getInt(Constants.KEY_SHOW_POKEMON_DATA, 0) == 1) {
                        if (Prefs.with(HomeActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                            Prefs.with(HomeActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 0);
                        } else {
                            Prefs.with(HomeActivity.this).save(Constants.SP_POKESTOP_ENABLED_BY_USER, 1);
                        }
                        showPokestopOnOffButton(passengerScreenMode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        imageViewPokemonOnOffInitial.setOnClickListener(onClickListenerPokeOnOff);
        imageViewPokemonOnOffConfirm.setOnClickListener(onClickListenerPokeOnOff);
        imageViewPokemonOnOffEngaged.setOnClickListener(onClickListenerPokeOnOff);


        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(pushBroadcastReceiver,
                    new IntentFilter(Data.LOCAL_BROADCAST));
        } catch (Exception e) {

        }

        rlThumbsType = (RelativeLayout) findViewById(R.id.rlThumbsType);
        llRatingFeedbackType = (LinearLayout) findViewById(R.id.llRatingFeedbackType);

		cvTutorialBanner = findViewById(R.id.cvTutorialBanner);
		tvTutorialBanner = findViewById(R.id.tvTutorialBanner);
        tvAddedTip = findViewById(R.id.tvAddedTip);
        tvAddedTip.setVisibility(View.GONE);
		ivCrossTutorialBanner = findViewById(R.id.ivCrossTutorialBanner);

		ivCrossTutorialBanner.setOnClickListener(v -> {
			cvTutorialBanner.setVisibility(View.GONE);
			Prefs.with(HomeActivity.this).save(Constants.KEY_TUTORIAL_SKIPPED, true);
		});
		tvTutorialBanner.setOnClickListener(v->{
			LatLng latLng = new LatLng(LocationFetcher.getSavedLatFromSP(HomeActivity.this), LocationFetcher.getSavedLngFromSP(HomeActivity.this));
			if(myLocation != null){
				latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			}
			TutorialInfoDialog.INSTANCE.showAddToll(HomeActivity.this, latLng);
		});

        if (Prefs.with(this).getInt(Constants.KEY_RIDE_FEEDBACK_RATING_BAR, 0) == 1) {
            rlThumbsType.setVisibility(View.GONE);
            llRatingFeedbackType.setVisibility(View.VISIBLE);
        } else {
            rlThumbsType.setVisibility(View.VISIBLE);
            llRatingFeedbackType.setVisibility(View.GONE);
        }

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    menuBar.setUserData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);

        viewSlidingExtra.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Data.userData.getSignupTutorial() != null) {
                        if (Data.userData.getSignupTutorial().getDs1() == 1) {
                            startActivity(new Intent(HomeActivity.this, NewUserFlow.class));
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Data.userData.getSignupTutorial() != null) {
                        if (Data.userData.getSignupTutorial().getTs1() == 1
                                || Data.userData.getSignupTutorial().getTs2() == 1) {
                            getTransactionUtils().openSignUpTutorialFragment(HomeActivity.this, relativeLayoutContainer, 2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 4000);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    fabViewTest.expandJeanieFirstTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);

        try {
            if (Data.getFuguChatBundle() != null) {
                fuguNotificationConfig.handleHippoPushNotification(HomeActivity.this, Data.getFuguChatBundle());
                Data.setFuguChatBundle(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutSearchContainerNew = findViewById(R.id.relativeLayoutSearchContainerNew);
        relativeLayoutDestSearchBarNew = findViewById(R.id.relativeLayoutDestSearchBarNew);
        relativeLayoutInitialSearchBarNew = findViewById(R.id.relativeLayoutInitialSearchBarNew);
        linearLayoutConfirmOption = findViewById(R.id.linearLayoutConfirmOption2);
        linearLayoutBidValue = findViewById(R.id.linearLayoutBidValue);
        editTextBidValue = findViewById(R.id.editTextBidValue);
        editTextBidValue.setTypeface(Fonts.mavenRegular(this));
        textViewDestSearchNew = findViewById(R.id.textViewDestSearchNew); textViewDestSearchNew.setTypeface(Fonts.mavenRegular(this));
        relativeLayoutInitialSearchBarNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutInitialSearchBar.performClick();
            }
        });
        relativeLayoutDestSearchBarNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutDestSearchBar.performClick();
            }
        });
        imageViewDropCrossNew = findViewById(R.id.imageViewDropCrossNew);
        imageViewDropCrossNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewDropCross.performClick();
            }
        });
        textViewInitialSearchNew = findViewById(R.id.textViewInitialSearchNew); textViewInitialSearchNew.setTypeface(Fonts.mavenRegular(this));
        if(isNewUI) {
            relativeLayoutSearchContainerNew.setVisibility(View.VISIBLE);
            relativeLayoutSearchContainer.setVisibility(View.GONE);
            linearLayoutConfirmOption.setBackground(getResources().getDrawable(R.color.white));
        } else {
            relativeLayoutSearchContainerNew.setVisibility(View.GONE);
            relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
            linearLayoutConfirmOption.setBackground(getResources().getDrawable(R.color.menu_item_selector_color_F7));
        }


        checkForYoutubeIntent();

    }

    private void onReqestRideConfirmClick() {
        Region region = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
        if (isNewUI && region.getReverseBid() == 1
                && !editTextBidValue.getText().toString().isEmpty()) {
            double innerValue = Prefs.with(HomeActivity.this).getFloat(Constants.KEY_MIN_REGION_FARE, 20.0f);
            double outerValue = Prefs.with(HomeActivity.this).getFloat(Constants.KEY_MAX_REGION_FARE, 5000.0f);
            if(region.getRegionFare()!= null) {
                innerValue = Math.ceil(region.getRegionFare().getFare() * 0.8);
                outerValue = Math.ceil(region.getRegionFare().getFare() * 10);
            }
            String minBidValueStr = Utils.formatCurrencyValue(Data.autoData.getCurrency(), innerValue);
            String innerStr = getString(R.string.bid_lower_value_err, minBidValueStr);
            String outerStr = getString(R.string.bid_greater_amount_err);
            if (Double.parseDouble(editTextBidValue.getText().toString()) < innerValue) {
                EnterBidDialog.INSTANCE.showRaiseFareDialog(HomeActivity.this, innerStr,
                        getString(R.string.raise_to_format, minBidValueStr), innerValue, false, value -> {
                            editTextBidValue.setText(value);
                            buttonConfirmRequest.performClick();
                        });

            } else if (Double.parseDouble(editTextBidValue.getText().toString()) > outerValue) {
                EnterBidDialog.INSTANCE.show(HomeActivity.this, null, outerStr,
                        getString(R.string.suggested_fare)+": "+minBidValueStr, Utils.getCurrencySymbol(Data.autoData.getCurrency()), getString(R.string.confirm), true, value -> {
                            editTextBidValue.setText(value);
                            buttonConfirmRequest.performClick();
                        });

            } else {
                if (getApiFindADriver().findADriverNeeded(Data.autoData.getPickupLatLng())) {
                    findDriversETACall(true, true, false, getApiFindADriver().getParams());
                } else {
                    if (getSlidingBottomPanel().getRequestRideOptionsFragment()
                            .getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                        openBikeRentalScan();
                    } else {
                        requestRideClick();
                    }
                }

            }
        } else if(isNewUI && region.getReverseBid() == 1
                && editTextBidValue.getText().toString().isEmpty()) {
            Utils.showToast(HomeActivity.this,getString(R.string.error_bid_value));
        } else {
            if (getApiFindADriver().findADriverNeeded(Data.autoData.getPickupLatLng())) {
                findDriversETACall(true, true, false, getApiFindADriver().getParams());
            } else {
                if (getSlidingBottomPanel().getRequestRideOptionsFragment()
                        .getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                    openBikeRentalScan();
                } else {
                    requestRideClick();
                }
            }
        }
    }
    public void resetFeedBackListClicked(int pos){
        for(FeedbackReason fr:Data.autoData.getFeedbackReasons()){
            fr.checked=false;
        }
        for (FeedbackReason fr:Data.autoData.getFeedBackInfoRatingData().get(pos).getTextBadges()){
            fr.checked=false;
        }
    }
    @Override
    public void onClickBadge(int badgeId,int position){
        badgesScroll.smoothScrollToPosition(position);
    }


    public void setServiceTypeAdapter(boolean setAdapter) {
        if(Data.autoData != null) {
            ArrayList<ServiceType> serviceTypesEligible = new ArrayList<>();
            boolean nomatch = true;
            for(ServiceType st: Data.autoData.getServiceTypes()){
                if(st.getSupportedVehicleTypes() == null
                        || st.getSupportedVehicleTypes().contains(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getVehicleType())
						|| st.getSupportedVehicleTypes().contains(-1)){

                if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null && st.getSupportedRideTypes() != null
                        && Data.autoData.getServiceTypeSelected().getSupportedRideTypes().size() == st.getSupportedRideTypes().size()) {
                    boolean matched = true;
                    for (int rideType : Data.autoData.getServiceTypeSelected().getSupportedRideTypes()) {
                        if (!st.getSupportedRideTypes().contains(rideType)) {
                            matched = false;
                            break;
                        }
                    }
                    if(matched && !isFromConfirmToOther){
                        st.setSelected(true);
                        nomatch = false;
                    }
                    if(isFromConfirmToOther) {
                        isFromConfirmToOther = false;
                    }
                }
                    serviceTypesEligible.add(st);
                }
            }
            if(nomatch && serviceTypesEligible.size() > 0){
                serviceTypesEligible.get(0).setSelected(true);
            }
            if(setAdapter) {
                if (rideTypesAdapter == null) {
                    rideTypesAdapter = new RideTypesAdapter(serviceTypesEligible, rvRideTypes, Fonts.mavenMedium(this), this);
                    rvRideTypes.setAdapter(rideTypesAdapter);
                } else {
                    rideTypesAdapter.setList(serviceTypesEligible);
                }
            }
            rvRideTypes.setVisibility(serviceTypesEligible.size() > 1 ? View.VISIBLE : View.GONE);
			setTopBarMenuIcon();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTopBarTransNewUI();
                }
            }, 50);
        }
    }

    public void openPickupDropSearchUI(PlaceSearchListFragment.PlaceSearchMode mode) {
        placeSearchMode = mode;
        setServiceAvailablityUI("");
        passengerScreenMode = PassengerScreenMode.P_SEARCH;
        switchPassengerScreen(passengerScreenMode);
    }

    public boolean checkFareEstimate = false;

    public void openFareEstimate() {
        if(Data.autoData == null || Data.autoData.getPickupLatLng() == null){
            Utils.showToast(this, getString(R.string.set_your_pickup_location));
            return;
        }
        Intent intent = new Intent(HomeActivity.this, FareEstimateActivity.class);
        intent.putExtra(Constants.KEY_REGION, gson.toJson(getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected(), Region.class));
        intent.putExtra(Constants.KEY_COUPON_SELECTED, gson.toJson(getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon()));
        intent.putExtra(Constants.KEY_IS_COUPON, getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon() instanceof CouponInfo);
        intent.putExtra(KEY_RIDE_TYPE, slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType());
        intent.putExtra(KEY_PICKUP_LATITUDE, Data.autoData.getPickupLatLng().latitude);
        intent.putExtra(KEY_PICKUP_LONGITUDE, Data.autoData.getPickupLatLng().longitude);
        intent.putExtra(KEY_PICKUP_LOCATION_ADDRESS, Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
        if(Data.autoData.getDropLatLng() != null) {
            intent.putExtra(KEY_DROP_LATITUDE, Data.autoData.getDropLatLng().latitude);
            intent.putExtra(KEY_DROP_LONGITUDE, Data.autoData.getDropLatLng().longitude);
            intent.putExtra(KEY_DROP_LOCATION_ADDRESS, Data.autoData.getDropAddress());
            startActivityForResult(intent, FARE_ESTIMATE);

        } else {
            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() ==
                    RideTypeValue.BIKE_RENTAL.getOrdinal()) {

                if (getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                checkFareEstimate = true;
                openRentalStationList();
            }
            else{
                startActivityForResult(intent, FARE_ESTIMATE);
            }
        }

        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private void showDriverTipDialog() {

        Double amount;
        String currency;
        try {
            if(passengerScreenMode==PassengerScreenMode.P_IN_RIDE){
                amount = Data.autoData.getAssignedDriverInfo().getTipAmount();
                currency = Data.autoData.getAssignedDriverInfo().getCurrency();
            }else if(passengerScreenMode==PassengerScreenMode.P_RIDE_END){
                amount = Data.autoData.getEndRideData().getDriverTipAmount();
                currency = Data.autoData.getEndRideData().getCurrency();

            }else{
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if(Data.autoData==null || Data.autoData.getcEngagementId()==null || !Data.autoData.getIsTipEnabled()){
            return;
        }

        getDriverTipInteractor().showPriorityTipDialog(amount,currency);
    }

    private DriverTipInteractor getDriverTipInteractor() {
        if(driverTipInteractor ==null){
            driverTipInteractor = new DriverTipInteractor(HomeActivity.this, new DriverTipInteractor.Callback() {

                @Override
                public void onConfirmed(Double amount, String engagementId) {

                    if(passengerScreenMode==PassengerScreenMode.P_RIDE_END){
                        getRideSummaryAPI(HomeActivity.this,Data.autoData.getcEngagementId(), null);
                    }else{
                        updateDriverTipUI(passengerScreenMode);

                    }

                }

                @Override
                public void onCancelled() {

                }
            },Data.autoData.getcEngagementId());
        }
        return driverTipInteractor;
    }

    private void setAddedTipUI() {
        if(Data.autoData!=null && Data.autoData.getAssignedDriverInfo()!=null &&
           Data.autoData.getIsTipEnabled() && Data.autoData.getAssignedDriverInfo().getTipAmount()!=null){
            layoutAddedTip.setVisibility(View.VISIBLE);
            tvTipAmountLabel.setText(getString(R.string.label_tip_added,
            Utils.formatCurrencyValue(Data.autoData.getAssignedDriverInfo().getCurrency(),Data.autoData.getAssignedDriverInfo().getTipAmount())));
        }else {
            layoutAddedTip.setVisibility(View.GONE);
        }
    }

    private void goodFeedbackViewType() {
        if (Data.userData != null) {
            if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_1);
            } else if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_2.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_2);
            } else if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_3.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_3);
            } else if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_4.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_4);
            } else if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                endRideWithGif();
            }
            disableEmergencyModeIfNeeded(Data.autoData.getcEngagementId());
            updateTopBar();
            topBar.imageViewHelp.setVisibility(View.GONE);
        }
    }

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public RelativeLayout getRelativeLayoutContainer() {
        return relativeLayoutContainer;
    }
    public RelativeLayout getScheduleRideContainer() {
        return scheduleRideContainer;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // map object initialized
        map = googleMap;
        if (map != null) {

			boolean success = map.setMapStyle(
					MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_json));

            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            enableMapMyLocation();
            map.getUiSettings().setTiltGesturesEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (Prefs.with(this).getInt(KEY_CUSTOMER_GOOGLE_TRAFFIC_ENABLED, 0) == 1) {
                map.setTrafficEnabled(true);
            }

            //30.7500, 76.7800
            //22.971723, 78.754263
            try {


                if (getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE)) {
                    double latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude);
                    double longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude);
                    LatLng latLngInIntent = new LatLng(latitude, longitude);
                    if (MapUtils.distance(new LatLng(Data.latitude, Data.longitude), latLngInIntent) > 200) {
                        zoomedToMyLocation = true;
                        Data.latitude = latitude;
                        Data.longitude = longitude;
                        Log.w("pickuplogging", "onMapReady"+Data.autoData.getPickupLatLng());
                        mapTouched = true;
                        addressPopulatedFromDifferentOffering = true;
                    }
                }
                LatLng latLng;
                if ((P_INITIAL == passengerScreenMode && Data.locationSettingsNoPressed)
                        || (Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0)) {
                    latLng = Data.getIndiaCentre();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                    forceFarAwayCity();
                    Data.autoData.setLastRefreshLatLng(latLng);
                } else {
                    latLng = new LatLng(Data.latitude, Data.longitude);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAX_ZOOM));
                    if (Data.autoData.getLastRefreshLatLng() == null) {
                        Data.autoData.setLastRefreshLatLng(latLng);
                    }
                }
                Log.w("getAddressAsync", "onMapReady");
                if(passengerScreenMode == P_INITIAL) {
					getAddressAsync(latLng, getInitialPickupTextView(), null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
				}
            } catch (Exception e) {
                e.printStackTrace();
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

                        if((passengerScreenMode == PassengerScreenMode.P_IN_RIDE || passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED)
								&& (Data.autoData.getAssignedDriverInfo() != null && Data.autoData.getAssignedDriverInfo().getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal())
                                && showSaveLocationDialog()
                                && !Prefs.with(HomeActivity.this).getBoolean(Constants.SKIP_SAVE_PICKUP_LOCATION, false)
                        && HomeUtil.getNearBySavedAddress(HomeActivity.this, Data.autoData.getPickupLatLng(), false) == null) {
                            openSaveLocationDialog(true);
                            pickupLocationMarker.remove();
                            map.moveCamera(CameraUpdateFactory.newLatLng(Data.autoData.getPickupLatLng()));
                        }

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
                    }
//                    else if(arg0.getSnippet().equalsIgnoreCase("poke_")){
//                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getTitle(), "");
//                        map.setInfoWindowAdapter(customIW);
//                        return true;
//                    }
                    else if (arg0.getTitle().equalsIgnoreCase("driver shown to customer")) {
                        if (1 == showDriverInfo) {
                            String driverId = arg0.getSnippet();
                            try {
                                final DriverInfo driverInfo = Data.autoData.getDriverInfos().get(Data.autoData.getDriverInfos().indexOf(new DriverInfo(driverId)));
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
                    } else if(arg0.getTitle().equalsIgnoreCase("End Location")) {
                        if((passengerScreenMode == PassengerScreenMode.P_IN_RIDE || passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED
                                || passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL)
								&& (Data.autoData.getAssignedDriverInfo() != null && Data.autoData.getAssignedDriverInfo().getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal())
                                && showSaveLocationDialog()
                                && !Prefs.with(HomeActivity.this).getBoolean(Constants.SKIP_SAVE_DROP_LOCATION, false)
                                && HomeUtil.getNearBySavedAddress(HomeActivity.this, Data.autoData.getDropLatLng(), false) == null) {
                            openSaveLocationDialog(false);
                            dropLocationMarker.remove();
                            map.moveCamera(CameraUpdateFactory.newLatLng(Data.autoData.getDropLatLng()));
                        }
                        return true;
                    } else if (!TextUtils.isEmpty(arg0.getTitle()) || "recent".equalsIgnoreCase(arg0.getTitle())) {
//                        CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getTitle(), arg0.getSnippet());
//                        map.setInfoWindowAdapter(customIW);

                        return true;
                    } else {
                        return true;
                    }
                }
            });


            mapStateListener = new MapStateListener(map, mapFragment, this) {

                boolean touchCalled, releaseCalled;
                @Override
                public void onMapTouched() {
                    // Map touched
                    touchCalled = true;
                    mapTouched = true;
                    zoomAfterFindADriver = false;
					myLocationButtonPressed = false;
                }

                @Override
                public void onMapReleased() {
                    // Map released
                    releaseCalled = true;
                    customerInRideMyLocationBtn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onMapUnsettled() {
                    // Map unsettled
                }

                @Override
                public void moveMap() {

                }

                @Override
                public void onMapSettled() {
                    // Map settled
                    FreshActivity.saveAddressRefreshBoolean(HomeActivity.this, false);
                    boolean refresh = false;
                    try {
                        checkForMyLocationButtonVisibility();
                        if((Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 1 && !isNewUI) || fromNaviCurrentLocation) {
                            Log.w("findADriverAndGeocode", "onMapSettled");
                            refresh = findADriverAndGeocode(fromNaviCurrentLocation ? getCurrentLatLng() : map.getCameraPosition().target, mapTouched, touchCalled, releaseCalled);
							fromNaviCurrentLocation = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    specialPickupSelected = false;
                    touchCalled = false;
                    releaseCalled = false;

                    if (Data.userData != null && pokestopHelper != null
                            && (P_INITIAL != passengerScreenMode || !refresh)) {
                        pokestopHelper.checkPokestopData(map.getCameraPosition().target, Data.userData.getCurrentCity());
                    }
                }

                @Override
                public void onCameraPositionChanged(CameraPosition cameraPosition) {

                    Log.v("camera position is", "--> " + cameraPosition.zoom);
                    if (previousZoomLevel != cameraPosition.zoom) {
                        if ((savedAddressState != HomeUtil.SavedAddressState.MARKER_WITH_TEXT) && cameraPosition.zoom > 17f) {
                            homeUtil.displaySavedAddressesAsFlags(HomeActivity.this, assl, map, true, passengerScreenMode);
                            savedAddressState = HomeUtil.SavedAddressState.MARKER_WITH_TEXT;
                            homeUtil.displayPointOfInterestMarkers(HomeActivity.this, assl, map, passengerScreenMode);
                        }
                        else if ((savedAddressState != HomeUtil.SavedAddressState.MARKER) && (cameraPosition.zoom < 17f) && (cameraPosition.zoom > 12f)) {
                            homeUtil.displaySavedAddressesAsFlags(HomeActivity.this, assl, map, false, passengerScreenMode);
                            savedAddressState = HomeUtil.SavedAddressState.MARKER;
                            homeUtil.displayPointOfInterestMarkers(HomeActivity.this, assl, map, passengerScreenMode);
                        }
                        else if (cameraPosition.zoom < 10f) {
                            homeUtil.removeSavedAddress(map);
                            savedAddressState = HomeUtil.SavedAddressState.BLANK;
                            homeUtil.removeMarkersPointsOfInterest(map);
                        }
                    }
                    previousZoomLevel = cameraPosition.zoom;
                }
            };


            initialMyLocationBtn.setOnClickListener(mapMyLocationClick);
            confirmMyLocationBtn.setOnClickListener(mapMyLocationClick);
            buttonChangeLocalityMyLocation.setOnClickListener(mapMyLocationClick);
            customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
            specialPickupLocationBtn.setOnClickListener(mapMyLocationClick);

            customerLocation.setOnClickListener(mapMyLocationClick);


            pokestopHelper = new PokestopHelper(this, map, assl);

        }


//        genieLayout = new GenieLayout(this);
//        genieLayout.addGenieLayout(drawerLayout, linearLayoutRequestInfo);

        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (userMode == null) {
                userMode = UserMode.PASSENGER;
            }

            if (passengerScreenMode == null) {
                passengerScreenMode = P_INITIAL;
            }


            switchUserScreen();

			setServiceTypeAdapter(true);
            startUIAfterGettingUserStatus();
			getHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (Data.autoData != null && Data.autoData.getDropLatLng() == null && !isNewUI()) {
						relativeLayoutDestSearchBar.performClick();
					}
				}
			}, 500);

            textViewCancellation.setVisibility(View.GONE);


            openPushDialog();

            getApiFetchUserAddress().hit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!"".equalsIgnoreCase(Data.deepLinkClassName)) {
                Class cls = Class.forName(getPackageName() + "." + Data.deepLinkClassName);
                startActivity(new Intent(this, cls));
                overridePendingTransition(R.anim.hold, R.anim.hold);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Branch.getInstance(this).setIdentity(Data.userData.userIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }



        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "");
        Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, "");



        try {
            if (getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE).getBoolean(Constants.KEY_INTERNAL_APP_SWITCH, false)
                    && Prefs.with(this).getInt(KEY_STATE_RESTORE_NEEDED, 0) == 1) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callAndHandleStateRestoreAPI(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 0);
        }

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    menuBar.setUserData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // to check if user has selected some promo coupon from promotions screen
                    slidingBottomPanel.getRequestRideOptionsFragment().selectAutoSelectedCouponAtRequestRide();
                    showPoolInforBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);


    }

	public TextView getInitialPickupTextView() {
		return Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())
				|| Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType()) ?
				tvPickupRentalOutstation : isNewUI() ? textViewInitialSearchNew : textViewInitialSearch;
	}

	private void openSaveLocationDialog(final boolean isPickup) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LatLng latLng = isPickup ? Data.autoData.getPickupLatLng() : Data.autoData.getDropLatLng();
        String address = isPickup ? Data.autoData.getPickupAddress(latLng) : Data.autoData.getDropAddress();
        DialogFragment dialogFragment = SaveLocationDialog.newInstance(latLng.latitude,latLng.longitude, address, isPickup, googleMapPadding - 10);
        dialogFragment.show(ft, "dialog");
    }

    private void openRequestConfirmDialog() {
        setTipAmountToZero(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(RideConfirmationDialog.class.getSimpleName());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = RideConfirmationDialog.newInstance(getRequestRideObject(checkForTipEnabled()));
        dialogFragment.show(ft, RideConfirmationDialog.class.getSimpleName());
    }

    private boolean checkForTipEnabled() {
        return mRequestLevelndex != -1 && mRequestLevelndex < Data.autoData.getRequestLevels().size()
                ? Data.autoData.getRequestLevels().get(mRequestLevelndex).getTipEnabled() == 1 && Data.autoData.getDropLatLng() != null : false;
    }

    private RequestRideConfirm getRequestRideObject(boolean showTipLevelWise) {
        Region region = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
        boolean isNotInRange = region.getRideType() == RideTypeValue.POOL.getOrdinal() || region.getFareMandatory() == 1;
        boolean showTip = region.getReverseBid() == 0 && region.getFareMandatory() == 1 && showTipLevelWise;

        RequestRideConfirm requestRideConfirm = new RequestRideConfirm(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()),
                region.getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal() ? "" : Data.autoData.getDropAddress(), region.getImages().getTabHighlighted(),
                region.getRegionName(), region.getDisclaimerText(), region.getRegionFare() != null ? region.getRegionFare().getFareText(0).toString() : "",
                isNotInRange && region.getRegionFare() != null ? region.getRegionFare().getFare() : 0.0,
                region.getRegionFare() != null ? region.getRegionFare().getMinFare() : 0.0,
                region.getRegionFare() != null ? region.getRegionFare().getMaxFare() : 0.0, Data.autoData.getCurrency(), showTip);

        return requestRideConfirm;
    }

    private void openDriverNotFoundTipDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(DriverNotFoundDialog.class.getSimpleName());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = DriverNotFoundDialog.newInstance(getRequestRideObject(checkForTipEnabled()));
        dialogFragment.show(ft, DriverNotFoundDialog.class.getSimpleName());
    }

    private void openDriverContactListDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(DriverCallDialog.class.getSimpleName());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = DriverCallDialog.newInstance(getRequestRideObject(checkForTipEnabled()));
        dialogFragment.show(ft, DriverCallDialog.class.getSimpleName());
    }

    private void enableMapMyLocation() {
        if (map != null
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(passengerScreenMode != PassengerScreenMode.P_IN_RIDE);
        }
    }

    public RelativeLayout getRelativeLayoutSlidingBottomParent() {
        return relativeLayoutSlidingBottomParent;
    }


    public View getViewSlidingExtra() {
        return viewSlidingExtra;
    }

//    public RelativeLayout getRelativeLayoutFABTest() {
//        return relativeLayoutFABTest;
//    }


    private void endRideWithGif() {
        relativeLayoutGreat.setVisibility(View.VISIBLE);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.great_place_holder);
        Glide.with(HomeActivity.this)
                .load(R.drawable.android_thumbs_up)
                .apply(options)
                //.fitCenter()
                .into(imageViewThumbsUpGif);
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewThumbsUpGif.setImageDrawable(null);
            }
        }, 3000);
    }

    private void endRideWithImages(int drawable) {
        relativeLayoutRideEndWithImage.setVisibility(View.VISIBLE);
        imageViewRideEndWithImage.setImageResource(drawable);
    }


    private void showServiceUnavailableDialog() {
        new ServiceUnavailableDialog(HomeActivity.this, new ServiceUnavailableDialog.Callback() {
            @Override
            public void onDialogDismiss() {

            }

            @Override
            public void onOk() {

            }
        }).showServiceUnavailableDialog();
    }

    public void flurryEventGAForTransaction() {

        List<Product> productList = new ArrayList<>();

        Product product = new Product()
                .setCategory("Auto")
                .setId("0")
                .setName("Paytm")
                .setPrice(Data.autoData.getEndRideData().paidUsingPaytm);

        Product product1 = new Product()
                .setCategory("Auto")
                .setId("1")
                .setName("JC")
                .setPrice(Data.autoData.getEndRideData().paidUsingWallet);

        Product product2 = new Product()
                .setCategory("Auto")
                .setId("2")
                .setName("Cash")
                .setPrice(Data.autoData.getEndRideData().toPay);
        productList.add(product);
        productList.add(product1);
        productList.add(product2);


        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                .setTransactionId(Data.autoData.getEndRideData().engagementId)
                .setTransactionAffiliation("Auto")
                .setTransactionRevenue(Data.autoData.getEndRideData().finalFare);

        GAUtils.transactionCompleteEvent(productList, productAction);
    }

    private void setEnteredDestination() {
        if (!Prefs.with(HomeActivity.this).getString(SPLabels.ENTERED_DESTINATION, "").equalsIgnoreCase("")) {
            SearchResult temp = new Gson().fromJson(Prefs.with(HomeActivity.this).getString(SPLabels.ENTERED_DESTINATION, ""), SearchResult.class);
            long diff = System.currentTimeMillis() - temp.getTime();
            int minutes = (int) ((diff / (1000 * 60)) % 60);
            Log.v("diff is ", "--> " + minutes);
            if (minutes < DESTINATION_PERSISTENCE_TIME
                    && Utils.compareDouble(temp.getLatitude(), 0) != 0
                    && Utils.compareDouble(temp.getLongitude(), 0) != 0) {
                setDropAddressAndExpandFields(temp);
            } else {
                Prefs.with(HomeActivity.this).save(SPLabels.ENTERED_DESTINATION, "");
            }

        }
    }

    private void setDropAddressAndExpandFields(SearchResult searchResult) {
        Data.autoData.setDropLatLng(searchResult.getLatLng());
        Data.autoData.setDropAddress(searchResult.getAddress());
        Data.autoData.setDropAddressId(searchResult.getId());
        if (Data.autoData.getDropLatLng() != null) {
            if (textViewDestSearch.getText().toString().isEmpty()
                    || textViewDestSearch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.enter_destination))
                    || textViewDestSearch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.destination_required))) {
                translateViewBottom(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutDestSearchBar, true, false);
                translateViewTop(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutInitialSearchBar, false, false);
            }

            textViewDestSearch.setText(searchResult.getNameForText(this));
            textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color));
            textViewDestSearchNew.setText(searchResult.getNameForText(this));
            textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color));

            relativeLayoutDestSearchBar.setBackgroundResource(R.drawable.background_white_rounded_bordered);
            imageViewDropCross.setVisibility(View.VISIBLE);
            imageViewDropCrossNew.setVisibility(View.VISIBLE);
            setLikeDropVisibilityAndBG();
        }
    }


    private void translateViewBottom(final ViewGroup viewGroup, final View mView, final boolean viewExchange,
                                     final boolean callNextAnim) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int) (ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN));
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
                params.topMargin = ((int) (ASSL.Yscale() * 98f));
                mView.setLayoutParams(params);
                if (callNextAnim) {
                    translateViewBottomTop(mView, viewExchange);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);

    }

    private void translateViewBottomTop(final View mView, final boolean viewExchange) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int) (ASSL.Yscale() * (-SEARCH_FLIP_ANIMATION_MARGIN)));
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
                    mView.setBackgroundResource(R.drawable.background_white_rounded_bordered);
                } else {
                    mView.setBackgroundResource(R.drawable.bg_menu_item_selector_color_rb);
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                params.topMargin = (int) (ASSL.Yscale() * 80f);
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
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int) (ASSL.Yscale() * (-SEARCH_FLIP_ANIMATION_MARGIN)));
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
                params.topMargin = (int) (ASSL.Yscale() * 0f);
                mView.setLayoutParams(params);
                if (callNextAnim) {
                    translateViewTopBottom(mView, viewExchange);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);
    }

    private void translateViewTopBottom(final View mView, final boolean viewExchange) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, (int) (ASSL.Yscale() * SEARCH_FLIP_ANIMATION_MARGIN));
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
                    mView.setBackgroundResource(R.drawable.background_white_rounded_bordered);
                } else {
                    mView.setBackgroundResource(R.drawable.bg_menu_item_selector_color_rb);
                }
                mView.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                params.topMargin = (int) (ASSL.Yscale() * 20f);
                mView.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(animation);
    }

    private void resetPickupDropFeilds() {
        try {
            Data.autoData.setDropLatLng(null);
            Data.autoData.setDropAddress("");
            Data.autoData.setDropAddressId(0);
            dropLocationSearched = false;


            textViewDestSearch.setText("");
            textViewDestSearchNew.setText("");
            imageViewDropCross.setVisibility(View.GONE);
            imageViewDropCrossNew.setVisibility(View.GONE);
            ivLikeDrop.setVisibility(View.GONE);

            relativeLayoutDestSearchBar.setBackgroundResource(R.drawable.bg_menu_item_selector_color_rb);
            relativeLayoutDestSearchBar.clearAnimation();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayoutDestSearchBar.getLayoutParams();
            params.topMargin = (int) (ASSL.Yscale() * 80f);
            relativeLayoutDestSearchBar.setLayoutParams(params);

            relativeLayoutInitialSearchBar.setBackgroundResource(R.drawable.background_white_rounded_bordered);
            relativeLayoutInitialSearchBar.clearAnimation();
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) relativeLayoutInitialSearchBar.getLayoutParams();
            params1.topMargin = (int) (ASSL.Yscale() * 20f);
            relativeLayoutInitialSearchBar.setLayoutParams(params1);

            ((ViewGroup) relativeLayoutInitialSearchBar.getParent()).bringChildToFront(relativeLayoutInitialSearchBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private float googleMapPadding = 0;

    public void setGoogleMapPadding(float bottomPadding, boolean isScaled) {
        try {
            float mapTopPadding = 0.0f;
            if (map != null) {
                if (P_INITIAL == passengerScreenMode) {
                    mapTopPadding = confirmedScreenOpened ? 300.0f : isNewUI ? 96.0f: 200.0f;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) centreLocationRl.getLayoutParams();
                    params.setMargins(0, (int) (ASSL.Yscale() * mapTopPadding), 0, 0);
                    params.setMarginStart(0);
                    params.setMarginEnd(0);
                    centreLocationRl.setLayoutParams(params);
                    bottomPadding = (isNewUI() || confirmedScreenOpened) ? bottomPadding - (ASSL.Yscale() * 125F) : 0F;
                } else {
                    mapTopPadding = 230.0f;
                    if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode){
						bottomPadding = bottomPadding - (ASSL.Yscale() * 125F);
					}
                }
                map.setPadding(0, (int) (ASSL.Yscale() * mapTopPadding), 0, (int) ((isScaled ? 1 : ASSL.Yscale()) * bottomPadding));
                googleMapPadding = bottomPadding;
                setCentrePinAccToGoogleMapPadding();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCentrePinAccToGoogleMapPadding() {
        try {
            if (P_INITIAL == passengerScreenMode) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewCenterPinMargin.getLayoutParams();
                params.height = (int) (ASSL.Yscale() * googleMapPadding);
                imageViewCenterPinMargin.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callMapTouchedRefreshDrivers(HashMap<String, String> params) {
        try {
            if (Data.userData != null) {
                if ((P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode) &&
                        map != null && !confirmedScreenOpened) {
                    if (!dontCallRefreshDriver && Data.autoData.getPickupLatLng() != null) {
                        findDriversETACall(false, false, false, params);
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


    @Override
    public void showCommentBox(int visibilty){
        if(visibilty == View.GONE) {
            editTextRSFeedback.setText("");
            Utils.hideKeyboard(HomeActivity.this);
        }
        HomeActivity.this.findViewById(R.id.cvAdditionalComments).setVisibility(visibilty);
    }

    public void initiateRequestRide(boolean newRequest) {
        if (newRequest) {
			Region regionSelected = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
            if (!isPoolRideAtConfirmation()) {
                double fareFactor = regionSelected.getCustomerFareFactor();
                int priorityTipCategory = regionSelected.getPriorityTipCategory();
                new PriorityTipDialog(HomeActivity.this, fareFactor, priorityTipCategory,
                        new PriorityTipDialog.Callback() {
                            @Override
                            public void onConfirmed(boolean confirmClicked) {
                                finalRequestRideTimerStart(regionSelected);
                            }

                            @Override
                            public void onCancelled() {
                                Log.v("Request of Ride", "Aborted");
                                specialPickupScreenOpened = false;
                                passengerScreenMode = P_INITIAL;
                                switchPassengerScreen(passengerScreenMode);
                            }
                        }).showDialog();
            } else {
                finalRequestRideTimerStart(regionSelected);
            }

        } else {
            Data.autoData.setcEngagementId("");
            switchRequestRideUI();
            startTimerRequestRide();
        }
    }

    private void finalRequestRideTimerStart(Region region) {
        try {
            Data.autoData.setcSessionId("");
            Data.autoData.setBidInfos(null);
			Prefs.with(this).save(Constants.KEY_REQUEST_RIDE_START_TIME, System.currentTimeMillis());
			Data.autoData.setIsReverseBid(region.getReverseBid());
			Prefs.with(this).save(KEY_REVERSE_BID, Data.autoData.getIsReverseBid());
			bidsPlacedAdapter.setList(Data.autoData.getBidInfos(), Data.autoData.getBidTimeout(),
					slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionName());
            Data.autoData.setcEngagementId("");
            dropLocationSearchText = "";

            if (myLocation == null) {
                myLocation = new Location(LocationManager.GPS_PROVIDER);
                myLocation.setAccuracy(50);
                myLocation.setLatitude(Data.latitude);
                myLocation.setLongitude(Data.longitude);
            }

            switchRequestRideUI();
            startTimerRequestRide();
            if (Data.TRANSFER_FROM_JEANIE == 1) {
                Data.TRANSFER_FROM_JEANIE = 0;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<SearchResult> fetchLastLocations(String fromList) {
        ArrayList<SearchResult> lastLocationSavedList = null;

        try {
            String json = Prefs.with(HomeActivity.this).getString(fromList, "");
            Type type = new TypeToken<ArrayList<SearchResult>>() {
            }.getType();
            lastLocationSavedList = new Gson().fromJson(json, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lastLocationSavedList == null) {
            lastLocationSavedList = new ArrayList<>();
        }
        return lastLocationSavedList;
    }

    private void snapPickupLocToNearbyAddress(SearchResult searchResult) {
        try {
            Data.autoData.setPickupSearchResult(searchResult.getAddress(), searchResult.getLatLng());
            Log.w("pickuplogging", "snap pickup to nrearby"+Data.autoData.getPickupLatLng());
            map.moveCamera(CameraUpdateFactory.newLatLng(Data.autoData.getPickupLatLng()));
            addUserCurrentLocationAddressMarker();
            if (getApiFindADriver().findADriverNeeded(Data.autoData.getPickupLatLng())) {
                findDriversETACall(true, false, true, getApiFindADriver().getParams());
            } else {
                requestRideDriverCheck();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestRideClick() {
            try {
                if (map != null) {
                    if (MyApplication.getInstance().isOnline()) {

                        boolean proceed = slidingBottomPanel.getRequestRideOptionsFragment().displayAlertAndCheckForSelectedWalletCoupon();
                        if (proceed) {

                            boolean callRequestRide = MyApplication.getInstance().getWalletCore()
                                    .requestWalletBalanceCheck(HomeActivity.this, Data.autoData.getPickupPaymentOption());
                            MyApplication.getInstance().getWalletCore().requestRideWalletSelectedFlurryEvent(Data.autoData.getPickupPaymentOption(), TAG);

                            if (callRequestRide) {
                                promoCouponSelectedForRide = slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon();

                                if (!specialPickupScreenOpened && Data.autoData.getUseRecentLocAtRequest() == 1) {
                                    SearchResult searchResult = HomeUtil.getNearBySavedAddress(HomeActivity.this, Data.autoData.getPickupLatLng(),
											true);
                                    if (searchResult != null) {
                                        if (MapUtils.distance(Data.autoData.getPickupLatLng(), searchResult.getLatLng())
                                                <= Data.autoData.getUseRecentLocAutoSnapMinDistance()) {
                                            snapPickupLocToNearbyAddress(searchResult);
                                        } else {
                                            new SavedAddressPickupDialog(HomeActivity.this, searchResult, new SavedAddressPickupDialog.Callback() {
                                                @Override
                                                public void onDialogDismiss() {
                                                    requestRideDriverCheck();
                                                    GAUtils.event(RIDES, PICKUP_LOCATION_DIFFERENT_FROM_CURRENT_LOCATION + POPUP, GAAction.CANCEL + CLICKED);
                                                }

                                                @Override
                                                public void yesClicked(SearchResult searchResult) {
                                                    snapPickupLocToNearbyAddress(searchResult);
                                                    GAUtils.event(RIDES, PICKUP_LOCATION_DIFFERENT_FROM_CURRENT_LOCATION + POPUP, GAAction.OK + CLICKED);
                                                }
                                            }).show();
                                        }
                                    } else {
                                        callAnAutoPopup(HomeActivity.this);
                                    }
                                } else {
                                    callAnAutoPopup(HomeActivity.this);
                                }
                                //callAnAutoPopup(HomeActivity.this);

                                Prefs.with(HomeActivity.this).save(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED, 0);
                                Prefs.with(HomeActivity.this).save(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED, 0);
                            }
                            Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                        }

                    } else {
                        DialogPopup.dialogNoInternet(HomeActivity.this, getString(R.string.connection_lost_title), getString(R.string.connection_lost_desc), new Utils.AlertCallBackWithButtonsInterface() {
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

    private void setTipAmountToZero(boolean isResetRequest) {
        if(Data.autoData != null) {
            Data.autoData.setNoDriverFoundTip(0.0);
        }
        if(isResetRequest) mRequestType = 0;
        mRequestLevelndex = 0;
    }

    private boolean addressMatchedWithSavedAddresses(String address) {
        if (address.equalsIgnoreCase(getResources().getString(R.string.home)) || address.equalsIgnoreCase(getResources().getString(R.string.work))) {
            return true;
        }
        for (SearchResult searchResult : Data.userData.getSearchResults()) {
            if (address.equalsIgnoreCase(searchResult.getName()) || address.equalsIgnoreCase(searchResult.getAddress())) {
                return true;
            }
        }
        return false;
    }


    public void switchRequestRideUI() {

        cancelTimerUpdateDrivers();

        HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
        switchPassengerScreen(passengerScreenMode);
    }

    public void setNotes(final String notes) {
        mNotes = notes;
    }

    OnClickListener mapMyLocationClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            requestLocationPermissionExplicit();
        }
    };

    @Override
    public void permissionGranted(int requestCode) {
        super.permissionGranted(requestCode);
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            navigateToCurrLoc();
        } else if (requestCode == REQUEST_CODE_LOCATION_SERVICE) {
            startInRideLocationService();
        } else if (requestCode == REQ_CODE_PERMISSION_CONTACT) {
            fetchContacts();
        }
    }

    private boolean myLocationButtonPressed = false;

    private void navigateToCurrLoc() {
        if (TextUtils.isEmpty(Data.autoData.getFarAwayCity()) && (isPoolRideAtConfirmation() || isNormalRideWithDropAtConfirmation())) {
            poolPathZoomAtConfirm();
            return;
        }
        if (passengerScreenMode != P_INITIAL
                && Data.autoData != null
                && Data.autoData.getAssignedDriverInfo() != null
                && Data.autoData.getAssignedDriverInfo().latLng != null) {
			myLocationButtonPressed = true;
            zoomtoPickupAndDriverLatLngBounds(Data.autoData.getAssignedDriverInfo().latLng, null, 0);
        } else {
            if (myLocation != null) {
                try {
                    fromNaviCurrentLocation = true;
                    setCentrePinAccToGoogleMapPadding();
                    mapStateListener.touchMapExplicit();
                    zoomAfterFindADriver = true;
                    LatLng current = getDeviceLocation();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, MAX_ZOOM), getMapAnimateDuration(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showToast(HomeActivity.this, getString(R.string.waiting_for_location));
                getHandler().postDelayed(runnableInitLocationFetcher, 1000);
            }
        }
    }

    Runnable runnableInitLocationFetcher = new Runnable() {
        @Override
        public void run() {
            initializeFusedLocationFetchers();
        }
    };


    public void setUserData() {
        try {
            menuBar.setUserData();

            updateInRideAddMoneyToWalletButtonText();
            setPaymentOptionInRide();
            textViewRideEndWithImage.setText(Data.autoData.getRideEndGoodFeedbackText());

            slidingBottomPanel.updatePaymentOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchUserScreen() {

        passengerMainLayout.setVisibility(View.VISIBLE);

        MyApplication.getInstance().getDatabase2().close();

    }


    public void switchPassengerScreen(PassengerScreenMode mode) {
        try {

            if (userMode == UserMode.PASSENGER) {

                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                    currentLocationMarker = null;
                }

                if(mode != PassengerScreenMode.P_INITIAL
						&& pickupLocationMarker != null){
                	pickupLocationMarker.remove();
                }
                if (mode != PassengerScreenMode.P_DRIVER_ARRIVED && mode != PassengerScreenMode.P_REQUEST_FINAL) {
                    try {
                        driverLocationMarker.remove();
                        driverLocationMarker = null;
                    } catch (Exception e) {
                    }
                }
                if (mode != PassengerScreenMode.P_IN_RIDE) {
                    try {
                        driverMarkerInRide.remove();
                        driverMarkerInRide = null;
                    } catch (Exception e) {
                    }
                    lastRidePathLatLng = null;
                }

                removeSaveLocationDialog();

                if (mode == PassengerScreenMode.P_RIDE_END) {
                    if (Data.autoData.getEndRideData() != null) {
//                        genieLayout.setVisibility(View.GONE);

						badgesAdapter=null;
            			badgesNormal.setVisibility(View.GONE);
            			badgesScroll.setVisibility(View.GONE);
						findViewById(R.id.cvAdditionalComments).setVisibility(View.GONE);
						buttonRSSubmitFeedback.setVisibility(View.GONE);

                        mapLayout.setVisibility(View.VISIBLE);
                        endRideReviewRl.setVisibility(View.VISIBLE);
                        if (Data.autoData.getEndRideData().getIsPooled() == 1
                                && TextUtils.isEmpty(Data.autoData.getEndRideData().getIconUrl())) {
                            ivEndRideType.setImageResource(R.drawable.ic_history_pool);
                        } else {
                            HomeUtil.setVehicleIcon(this, ivEndRideType, Data.autoData.getEndRideData().getIconUrl(),
                                    Data.autoData.getAssignedDriverInfo().getVehicleIconSet().getIconInvoice(), true, null);
                        }

                        scrollViewRideSummary.scrollTo(0, 0);
                        ratingBarRSFeedback.setScore(0f, false);
                        setZeroRatingView();
                        relativeLayoutRideEndWithImage.setVisibility(View.GONE);
                        relativeLayoutGreat.setVisibility(View.GONE);

                        editTextRSFeedback.setText("");
                        for (int i = 0; i < Data.autoData.getFeedbackReasons().size(); i++) {
                            Data.autoData.getFeedbackReasons().get(i).checked = false;
                        }
                        feedbackReasonsAdapter.notifyDataSetChanged();
                        textViewRSTotalFareValue.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), Data.autoData.getEndRideData().finalFare));
                        /*imageViewThumbsUp.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_up));
                        imageViewThumbsDown.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_down));
                        textViewThumbsUp.setVisibility(View.INVISIBLE);
                        textViewThumbsDown.setVisibility(View.INVISIBLE);*/

                        Data.autoData.getEndRideData().setDriverNameCarName(Data.autoData.getAssignedDriverInfo().name, Data.autoData.getAssignedDriverInfo().carNumber);
                        Prefs.with(HomeActivity.this).save(SP_DRIVER_BEARING, 0f);

						if (findViewById(R.id.ivDriverImageEndRide) != null
								&& Data.autoData != null
								&& Data.autoData.getAssignedDriverInfo() != null) {
							Picasso.with(this)
									.load(Data.autoData.getAssignedDriverInfo().image)
									.transform(new CircleTransform())
									.into(((ImageView) findViewById(R.id.ivDriverImageEndRide)));
							tvDriverName.setText(Data.autoData.getAssignedDriverInfo().name);
						}

                        // delete the RidePath Table from Phone Database :)
                        MyApplication.getInstance().getDatabase2().deleteRidePathTable();
						pathToDropLocationPolylineOptions = null;
						driverToDropPathShown = false;

                        findViewById(R.id.llRideEndTotalFareTakeCash).setVisibility(Prefs.with(this)
                                .getInt(Constants.KEY_SHOW_FARE_DETAILS_AT_RIDE_END, 1) == 1 ? View.VISIBLE : View.GONE);
                        if (Prefs.with(this).getInt(Constants.KEY_SHOW_TAKE_CASH_AT_RIDE_END, 1) == 1) {
                            findViewById(R.id.llRideEndTakeCash).setVisibility(View.VISIBLE);
                            findViewById(R.id.ivDivRideEndTakeCash).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.llRideEndTakeCash).setVisibility(View.GONE);
                            findViewById(R.id.ivDivRideEndTakeCash).setVisibility(View.GONE);
                        }

                        int onlinePaymentVisibility = updateRideEndPayment();
                        if (onlinePaymentVisibility == View.VISIBLE && Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()) {
                            llPayOnline.post(new Runnable() {
                                @Override
                                public void run() {
                                    llPayOnline.performClick();
                                }
                            });
                        }
                        if (Prefs.with(HomeActivity.this).getInt(KEY_FORCE_MPESA_PAYMENT, 0) == 1) {
                            tvPayOnlineIn.setText(getString(R.string.pay_via_format,
                                    MyApplication.getInstance().getWalletCore().getMPesaName(this)));
                        }

                    } else {
                        passengerScreenMode = P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    }
                } else {
                    mapLayout.setVisibility(View.VISIBLE);
                    endRideReviewRl.setVisibility(View.GONE);
                    if(tipSelected != 0) {
                        tipSelected = 0;
                        onTipSelected(true);
                    }
                }


                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                relativeLayoutConfirmRequest.setVisibility(View.GONE);
                rlSpecialPickup.setVisibility(View.GONE);
                rlChatDriver.setVisibility(View.GONE);
                scheduleRideContainer.setVisibility(View.GONE);
                constraintLayoutRideTypeConfirm.setVisibility(View.GONE);

                if(inRideCheck()
                        && Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null
                        && Data.autoData.getAssignedDriverInfo().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                    startPollingGetGPSLockStatus();
                } else {
                    cancelPollingGetGPSLockStatus();
                }

                if(mode != PassengerScreenMode.P_ASSIGNING){
					getHandler().removeCallbacks(runnableBidTimer);
					topBar.tvCancel.setVisibility(View.GONE);
				}
                cvTutorialBanner.setVisibility(View.GONE);
				DriverToPickupPath.INSTANCE.removePolylineDriverToPickup(mode);

                switch (mode) {

                    case P_INITIAL:

                        Log.d("HomeActivityResult" , " P_INITIAL");
                        fabViewTest = new FABViewTest(this, fabViewIntial);
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (fabViewTest != null) {
                                    fabViewTest.showTutorial();
                                }
                            }
                        }, 1000);
                        GCMIntentService.clearNotifications(HomeActivity.this);
                        Prefs.with(HomeActivity.this).save(Constants.KEY_CHAT_COUNT, 0);

                        Data.autoData.setAssignedDriverInfo(null);

                        MyApplication.getInstance().getDatabase2().deleteRidePathTable();
						pathToDropLocationPolylineOptions = null;
						driverToDropPathShown = false;


                        clearMap();

						initialLayout.post(new Runnable() {
							@Override
							public void run() {
								RelativeLayout.LayoutParams paramsInitial = (RelativeLayout.LayoutParams) initialLayout.getLayoutParams();
								if(!isNewUI()) {
									setEnteredDestination();
									paramsInitial.topMargin = (int) (ASSL.Yscale() * 96F);
								} else {
									paramsInitial.topMargin = 0;
								}
								initialLayout.setLayoutParams(paramsInitial);
							}
						});
                        initialLayout.setVisibility(View.VISIBLE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);
                        relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                        relativeLayoutInitialSearchBar.setVisibility(View.VISIBLE);

                        imageViewRideNow.setVisibility(View.VISIBLE);
                        changeLocalityLayout.setVisibility(View.GONE);


                        rentalInRideLayout.setVisibility(View.GONE);
                        rentalEndRideLayout.setVisibility(View.GONE);


                        cancelTimerRequestRide();


                        Log.e("Data.latitude", "=" + Data.latitude);
                        Log.e("myLocation", "=" + myLocation);

                        if (Data.autoData.getPickupLatLng() == null) {
                        	LatLng latLng = null;
                            if (Data.latitude != 0 && Data.longitude != 0) {
								latLng = new LatLng(Data.latitude, Data.longitude);
                                Log.w("pickuplogging", "case P_INITIAL Data.latitude"+Data.autoData.getPickupLatLng());
                            } else if (myLocation != null) {
								latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                Log.w("pickuplogging", "case P_INITIAL mylocation"+Data.autoData.getPickupLatLng());
                            }
                            getAddressAsync(latLng, getInitialPickupTextView(), null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
                        }

						if(passengerScreenMode == P_INITIAL
								&& isNewUI() && Data.autoData.getPickupLatLng() != null) {
							pickupLocationEtaMarker();
						}

                        topBar.imageViewHelp.setVisibility(View.GONE);

                        if (!specialPickupScreenOpened && !firstTimeZoom && !confirmedScreenOpened) {
                            if (Data.autoData.getPickupLatLng() != null) {
                                zoomToCurrentLocationWithOneDriver(Data.autoData.getPickupLatLng());
                            } else if (myLocation != null) {
                                zoomToCurrentLocationWithOneDriver(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                            }
                        }
                        firstTimeZoom = true;

                        if (Data.locationSettingsNoPressed) {
                            relativeLayoutLocationError.setVisibility(View.VISIBLE);
                            initialMyLocationBtn.setVisibility(View.GONE);
                            imageViewRideNow.setVisibility(View.GONE);
//							genieLayout.setVisibility(View.GONE);
                            hideCenterPickupPin();
                            changeLocalityLayout.setVisibility(View.GONE);
                            Data.locationSettingsNoPressed = false;
                        } else {
                            relativeLayoutLocationError.setVisibility(View.GONE);
                            checkForMyLocationButtonVisibility();
                            imageViewRideNow.setVisibility(View.VISIBLE);
//							genieLayout.setVisibility(View.VISIBLE);
                            showCenterPickupPin(true);
                        }

						relativeLayoutConfirmBottom.getMeasuredHeight();
						constraintLayoutRideTypeConfirm.getMeasuredHeight();
                        findADriverFinishing(false, !switchUICalledFromStateRestore);

                        linearLayoutRequestMain.setVisibility(View.VISIBLE);
                        relativeLayoutInitialSearchBar.setEnabled(true);
                        imageViewConfirmDropLocationEdit.setVisibility(View.GONE);
                        if (specialPickupScreenOpened) {
                            rlSpecialPickup.setVisibility(View.VISIBLE);
                            topBar.imageViewMenu.setVisibility(View.GONE);
                            topBar.imageViewBack.setVisibility(View.VISIBLE);
                            specialPickupItemsAdapter.setResults(Data.autoData.getNearbyPickupRegionses().getHoverInfo());
                            showSpecialPickupMarker(specialPickups);
                        } else {
                            rlSpecialPickup.setVisibility(View.GONE);
                            showPoolInforBar(false);
                        }


                        if (confirmedScreenOpened) {
                            buttonConfirmRequest.setText(R.string.confirm_request);
                            imageViewRideNow.setVisibility(View.GONE);
                            relativeLayoutSearchContainerNew.setVisibility(View.GONE);
                            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            relativeLayoutConfirmRequest.setVisibility(View.VISIBLE);
                            buttonConfirmRequest.setBackground(ContextCompat.getDrawable(this, R.drawable.capsule_theme_login_selector));
                            if (isPoolRideAtConfirmation() || isNormalRideWithDropAtConfirmation()) {
                                hideCenterPickupPin();
                                imageViewConfirmDropLocationEdit.setVisibility(View.VISIBLE);
                                setLikeDropVisibilityAndBG();
                                fareEstimatBeforeRequestRide();
                            } else {
                                ivLikeDrop.setVisibility(View.GONE);
                            }
                            linearLayoutRequestMain.setVisibility(View.GONE);
                            topBar.imageViewMenu.setVisibility(View.GONE);
                            topBar.imageViewBack.setVisibility(View.VISIBLE);

                            if(mNotes != null && !mNotes.isEmpty()) {
                                imageViewNotes.setVisibility(View.VISIBLE);
                            } else {
                                imageViewNotes.setVisibility(View.GONE);
                            }

                            imageViewDropCross.setVisibility(View.GONE);
                            imageViewDropCrossNew.setVisibility(View.GONE);
                            updateConfirmedStatePaymentUI();
                            updateConfirmedStateCoupon();
                            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams((int) (ASSL.Yscale()*500),(int) (ASSL.Xscale()*96));
                            params.setMargins(0,0,0,0);
                            params.addRule(RelativeLayout.BELOW,linearLayoutConfirmOption.getId());
                            params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                            buttonConfirmRequest.setLayoutParams(params);

                            linearLayoutBidValue.setVisibility(View.GONE);
                            relativeLayoutTotalFare.setVisibility(View.VISIBLE);
                            if(Data.autoData.showRegionSpecificFare()){
                                recyclerViewVehiclesConfirmRide.setVisibility(View.VISIBLE);
                                relativeLayoutTotalFare.setVisibility(View.GONE);
                                textVieGetFareEstimateConfirm.setVisibility(View.GONE);
                                findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.GONE);
                                try {
                                    if(recyclerViewVehiclesConfirmRide.getAdapter()==null){
                                        vehiclesTabAdapterConfirmRide = new VehiclesTabAdapter(HomeActivity.this, Data.autoData.getRegions(),true);
                                        recyclerViewVehiclesConfirmRide.setAdapter(vehiclesTabAdapterConfirmRide);
                                    }else{
                                        recyclerViewVehiclesConfirmRide.getAdapter().notifyDataSetChanged();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                recyclerViewVehiclesConfirmRide.setVisibility(View.GONE);
								updateConfirmedStateFare();
                            }
                            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getCustomerNotesEnabled() == 1 ||
                                getResources().getBoolean(R.bool.show_add_notes)) {
                                rlNotes.setVisibility(View.VISIBLE);
                                findViewById(R.id.ivNotes).setVisibility(View.VISIBLE);
                            } else {
                                rlNotes.setVisibility(View.GONE);
                                findViewById(R.id.ivNotes).setVisibility(View.GONE);
                            }
                        } else if(isNewUI) {
                            buttonConfirmRequest.setText(R.string.confirm_ride);
                            imageViewRideNow.setVisibility(View.GONE);
                            relativeLayoutSearchContainerNew.setVisibility(View.VISIBLE);
                            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            relativeLayoutConfirmRequest.setVisibility(View.VISIBLE);
                            ivLikeDrop.setVisibility(View.GONE);
                            linearLayoutRequestMain.setVisibility(View.GONE);
                            topBar.imageViewBack.setVisibility(View.GONE);
                            topBar.imageViewMenu.setVisibility(View.VISIBLE);
                            buttonConfirmRequest.setBackground(ContextCompat.getDrawable(this, R.drawable.background_theme_gradient_selector));
                            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int) (ASSL.Xscale()*80));
                            buttonConfirmRequest.setTextSize(19);
                            params.setMargins(30,0,30,20);
                            params.addRule(RelativeLayout.BELOW,linearLayoutConfirmOption.getId());
                            params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                            buttonConfirmRequest.setLayoutParams(params);
                            if (isNewUiWithDropAtConfirmation()) {
                                if(currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }
                                fareEstimatBeforeRequestRide();
                            }
                            if(mNotes != null && !mNotes.isEmpty()) {
                                imageViewNotes.setVisibility(View.VISIBLE);
                            } else {
                                imageViewNotes.setVisibility(View.GONE);
                            }

                            imageViewDropCross.setVisibility(View.GONE);
                            imageViewDropCrossNew.setVisibility(View.GONE);
                            updateConfirmedStatePaymentUI();
                            updateConfirmedStateCoupon();
                            //fabView.setRelativeLayoutFABVisibility(mode);
                            recyclerViewVehiclesConfirmRide.setVisibility(View.VISIBLE);
                            relativeLayoutTotalFare.setVisibility(View.GONE);
                            textVieGetFareEstimateConfirm.setVisibility(View.GONE);
                            findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.GONE);
                            try {
                                if(recyclerViewVehiclesConfirmRide.getAdapter()==null){
                                    vehiclesTabAdapterConfirmRide = new VehiclesTabAdapter(HomeActivity.this, Data.autoData.getRegions(),true);
                                    recyclerViewVehiclesConfirmRide.setAdapter(vehiclesTabAdapterConfirmRide);
                                }else{
                                    recyclerViewVehiclesConfirmRide.getAdapter().notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getCustomerNotesEnabled() == 1 ||
                                    getResources().getBoolean(R.bool.show_add_notes)) {
                                rlNotes.setVisibility(View.VISIBLE);
                                findViewById(R.id.ivNotes).setVisibility(View.VISIBLE);
                            } else {
                                rlNotes.setVisibility(View.GONE);
                                findViewById(R.id.ivNotes).setVisibility(View.GONE);
                            }
                            relativeLayoutSearchContainer.setVisibility(View.GONE);
                            hideCenterPickupPin();
                            setPickupLocationInitialUI();
                            if(Data.autoData != null) {
                                if (!TextUtils.isEmpty(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()))) {
                                    textViewInitialSearchNew.setText(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
                                }
                                if(!TextUtils.isEmpty(Data.autoData.getDropAddress())) {
                                    textViewDestSearchNew.setText(Data.autoData.getDropAddress());
                                }
                            }
                            checkForNoDriverFoundHelp();
                        } else {
                            if (!specialPickupScreenOpened && map != null) {
                                if (!searchedALocation) {
                                    dropAddressName = "";
                                }
                            }
							tvDropAssigning.setText("");
                            textViewFinalDropLocationClick.setText("");
                        }


                        if(scheduleRideOpen){
                           scheduleRideContainer.setVisibility(View.VISIBLE);
                            topBar.imageViewMenu.setVisibility(View.GONE);
                            topBar.imageViewBack.setVisibility(View.VISIBLE);
                        } else {
                            scheduleRideContainer.setVisibility(View.GONE);
                        }
                        searchedALocation = false;

                        setFabMarginInitial(false, false);

                        initAndClearInRidePath();
                        getTrackingLogHelper().uploadAllTrackLogs();


                        if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType()
                                == RideTypeValue.BIKE_RENTAL.getOrdinal())
                        {
                            damageReportButton.setVisibility(View.GONE);
                            Log.d("HomeActivityResult" , " Rental");
                        }


						String tutorialBannerText = Prefs.with(this).getString(KEY_CUSTOMER_TUTORIAL_BANNER_TEXT, "");
						cvTutorialBanner.setVisibility(TextUtils.isEmpty(tutorialBannerText)
								|| Prefs.with(this).getBoolean(Constants.KEY_TUTORIAL_SKIPPED, false) ? View.GONE : View.VISIBLE);
						tvTutorialBanner.setText(tutorialBannerText);

                        break;


                    case P_SEARCH:

                        Log.d("HomeActivityResult" , " P_SEARCH");


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.VISIBLE);
                        requestFinalLayout.setVisibility(View.GONE);

                        rentalInRideLayout.setVisibility(View.GONE);
                        rentalEndRideLayout.setVisibility(View.GONE);

                        damageReportButton.setVisibility(View.GONE);

                        hideCenterPickupPin();

                        topBar.imageViewHelp.setVisibility(View.GONE);
                        topBar.imageViewBack.setVisibility(View.VISIBLE);
                        topBar.imageViewMenu.setVisibility(View.GONE);

                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

                        break;


                    case P_ASSIGNING:
                        Log.d("HomeActivityResult" , " P_ASSIGNING");

                        Log.e(TAG, "P_ASSIGNING");
                        fabViewIntial.setVisibility(View.GONE);
                        fabViewFinal.setVisibility(View.VISIBLE);
                        fabViewTest = new FABViewTest(this, fabViewFinal);
                        findDriverJugnooAnimation.setVisibility(View.VISIBLE);
                        if (findDriverJugnooAnimation instanceof ImageView) {
                            jugnooAnimation.start();
                        }
                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.VISIBLE);
                        rvBidsIncoming.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);

                        rentalInRideLayout.setVisibility(View.GONE);
                        rentalEndRideLayout.setVisibility(View.GONE);
                        damageReportButton.setVisibility(View.GONE);


                        hideCenterPickupPin();

//                        if (Data.autoData.getCancellationChargesPopupTextLine1().equalsIgnoreCase("")) {
                            textViewCancellation.setVisibility(View.GONE);
//                        }

                        textViewFindingDriver.setText(R.string.finding_a_driver);
                        findViewById(R.id.vBidTimer).setVisibility(View.GONE);
                        if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() ==
                                RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                            textViewFindingDriver.setText(R.string.booking_a_bike);
                        } else {
                            textViewFindingDriver.setText(R.string.finding_a_driver);
                        }

                        pwBidTimer.setVisibility(View.GONE);
                        tvBidTimer.setVisibility(View.GONE);
                        tvInitialCancelRide.setVisibility(View.GONE);
                        try {
                            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateBidsView();

                        checkForAddedTip();

                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 600);


                        if (map != null) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("pickup location");
                            markerOptions.snippet("");
                            markerOptions.position(Data.autoData.getPickupLatLng());
                            markerOptions.zIndex(HOME_MARKER_ZINDEX);

                            if ((confirmedScreenOpened &&
                                    (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()))
                                    || Data.autoData.getDropLatLng() != null) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                        .getTextAssignBitmap(HomeActivity.this,
                                                slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getEta(),
                                                getResources().getDimensionPixelSize(R.dimen.text_size_22))));
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                        .getTextBitmap(HomeActivity.this,
                                                slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getEta(),
                                                getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
                            }


                            pickupLocationMarker = map.addMarker(markerOptions);
                            getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        if (map != null && Data.autoData.getPickupLatLng() != null) {
                                            map.animateCamera(CameraUpdateFactory.newLatLng(Data.autoData.getPickupLatLng()), getMapAnimateDuration(), null);
                                        }
                                }
                            }, 1000);
                        }

                        stopDropLocationSearchUI(false);

                        setDropLocationAssigningUI();

                        relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);


                        topBar.imageViewHelp.setVisibility(View.GONE);



                        fabViewTest.setMenuLabelsRightTestPadding(160f);
                        setJeanieVisibility();
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);


                        break;


                    case P_REQUEST_FINAL:

                        Log.d("HomeActivityResult" , " P_REQUEST_FINAL");

                        fabViewIntial.setVisibility(View.GONE);
                        fabViewFinal.setVisibility(View.GONE);
//                        if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null
//                                && Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())) {
//                            relativeLayoutFinalDropLocationClick.setVisibility(View.GONE);
//                        } else {
//                            relativeLayoutFinalDropLocationClick.setVisibility(View.VISIBLE);
//                        }
                        fabViewTest = new FABViewTest(this, fabViewFinal);
                        if (map != null) {

                            Log.e("Data.autoData.getAssignedDriverInfo().latLng", "=" + Data.autoData.getAssignedDriverInfo().latLng);

                            if (driverLocationMarker == null) {
                                clearMap();
                                driverLocationMarker = getAssignedDriverCarMarkerOptions(Data.autoData.getAssignedDriverInfo());
                                if (Utils.compareFloat(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f), 0f) != 0) {
                                    driverLocationMarker.setRotation(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f));
                                } else {
                                    driverLocationMarker.setRotation((float) Data.autoData.getAssignedDriverInfo().getBearing());
                                }
                            } else {
                                MarkerAnimation.clearAsyncList();
                                MarkerAnimation.animateMarkerToICS(Data.autoData.getcEngagementId(), driverLocationMarker,
                                        Data.autoData.getAssignedDriverInfo().latLng, new LatLngInterpolator.LinearFixed(), null,
										true, getMarkerAnimationDuration());
                            }
							myLocationButtonPressed = true;
							DriverToPickupPath.INSTANCE.showPath(this, mode, map,
									Data.autoData.getAssignedDriverInfo().latLng, Data.autoData.getPickupLatLng());
                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.autoData.getPickupLatLng(), false));
                        }

                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        damageReportButton.setVisibility(View.GONE);



                        rentalStateUIHandling(mode);

                        hideCenterPickupPin();

                        setAssignedDriverData(mode);


                        if (dropLocationSearched) {
                            initDropLocationSearchUI(true);
                        } else {
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();
                        zoomtoPickupAndDriverLatLngBounds(Data.autoData.getAssignedDriverInfo().latLng, null, 0);

                        buttonCancelRide.setVisibility(View.GONE);
                        ivMoreOptions.setVisibility(Prefs.with(this).getInt(KEY_CUSTOMER_CANCEL_RIDE_ENABLED, 1) == 1 ? View.VISIBLE : View.GONE);
                        buttonAddMoneyToWallet.setVisibility(View.GONE);
                        linearLayoutSendInvites.setVisibility(View.GONE);
                        linearLayoutSendInvites.clearAnimation();
                        setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);

                        try {
                            if (Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1) {
                                relativeLayoutPoolSharing.setVisibility(View.VISIBLE);
                                imageViewFinalDropLocationEdit.setVisibility(View.GONE);
                            } else {
                                relativeLayoutPoolSharing.setVisibility(View.GONE);
                            }
                            setPoolRideStatus(Data.autoData.getAssignedDriverInfo().getPoolRideStatusString(), Data.autoData.getAssignedDriverInfo().getFellowRiders());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1
								|| Prefs.with(HomeActivity.this).getInt(KEY_REVERSE_BID, 0) == 1){
							imageViewFinalDropLocationEdit.setVisibility(View.GONE);
						}
                        checkForGoogleLogoVisibilityInRide();
                        setFabViewAtRide(mode);
						fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

                        showChatButton();

                        break;

                    case P_DRIVER_ARRIVED:

                        fabViewIntial.setVisibility(View.GONE);
                        fabViewFinal.setVisibility(View.GONE);
                        fabViewTest = new FABViewTest(this, fabViewFinal);

                        if (map != null) {

                            Log.e("Data.autoData.getAssignedDriverInfo().latLng", "=" + Data.autoData.getAssignedDriverInfo().latLng);
                            if (driverLocationMarker == null) {
                                clearMap();
                                driverLocationMarker = getAssignedDriverCarMarkerOptions(Data.autoData.getAssignedDriverInfo());
                                if (Utils.compareFloat(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f), 0f) != 0) {
                                    driverLocationMarker.setRotation(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f));
                                }
                                MyApplication.getInstance().getDatabase2().insertTrackingLogs(Integer.parseInt(Data.autoData.getcEngagementId()),
                                        Data.autoData.getAssignedDriverInfo().latLng,
                                        driverLocationMarker.getRotation(),
                                        TrackingLogModeValue.RESET.getOrdinal(),
                                        Data.autoData.getAssignedDriverInfo().latLng, 0);

                                if (Data.autoData.getDropLatLng() != null) {
                                    setDropLocationMarker();
                                }
                            } else {
                                MarkerAnimation.clearAsyncList();
                                MarkerAnimation.animateMarkerToICS(Data.autoData.getcEngagementId(), driverLocationMarker,
                                        Data.autoData.getAssignedDriverInfo().latLng, new LatLngInterpolator.LinearFixed(), null,
										true, getMarkerAnimationDuration());
                            }
							myLocationButtonPressed = true;
							DriverToPickupPath.INSTANCE.showPath(this, mode, map,
									Data.autoData.getAssignedDriverInfo().latLng, Data.autoData.getPickupLatLng());
                            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.autoData.getPickupLatLng(), true));
                        }


                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
//                        requestFinalLayout.setVisibility(View.VISIBLE);
                        damageReportButton.setVisibility(View.GONE);


                        rentalStateUIHandling(mode);


                        hideCenterPickupPin();

                        if (dropLocationSearched) {
                            initDropLocationSearchUI(true);
                        } else {
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();

                        setAssignedDriverData(mode);
                        zoomtoPickupAndDriverLatLngBounds(Data.autoData.getAssignedDriverInfo().latLng, null, 0);


						buttonCancelRide.setVisibility(View.GONE);
						ivMoreOptions.setVisibility(Prefs.with(this).getInt(KEY_CUSTOMER_CANCEL_RIDE_ENABLED, 1) == 1 ? View.VISIBLE : View.GONE);
                        buttonAddMoneyToWallet.setVisibility(View.GONE);
                        linearLayoutSendInvites.setVisibility(View.GONE);
                        linearLayoutSendInvites.clearAnimation();
                        setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);

                        try {
                            if (Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1) {
                                relativeLayoutPoolSharing.setVisibility(View.VISIBLE);
                                imageViewFinalDropLocationEdit.setVisibility(View.GONE);
                            } else {
                                relativeLayoutPoolSharing.setVisibility(View.GONE);
                            }
                            setPoolRideStatus(Data.autoData.getAssignedDriverInfo().getPoolRideStatusString(), Data.autoData.getAssignedDriverInfo().getFellowRiders());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

						if(Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1
								|| Prefs.with(HomeActivity.this).getInt(KEY_REVERSE_BID, 0) == 1){
							imageViewFinalDropLocationEdit.setVisibility(View.GONE);
						}
                        checkForGoogleLogoVisibilityInRide();
                        setFabViewAtRide(mode);
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

                        showChatButton();

                        break;


                    case P_IN_RIDE:

                        rentalStateUIHandling(mode);

                        fabViewIntial.setVisibility(View.GONE);
                        fabViewFinal.setVisibility(View.GONE);
                        fabViewTest = new FABViewTest(this, fabViewFinal);
                        if (map != null) {
                            if (driverMarkerInRide == null) {
                                clearMap();

                                if (Data.autoData.getDropLatLng() != null) {
                                    setDropLocationMarker();
                                    setPickupToDropPath();
                                }
                            }
                            if (Data.autoData.getPickupLatLng() != null) {
                                pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.autoData.getPickupLatLng(), true));
                            }
                        }


                        initialLayout.setVisibility(View.GONE);
                        ivMoreOptions.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);

                        damageReportButton.setVisibility(View.GONE);





                        hideCenterPickupPin();


                        if (dropLocationSearched) {
                            initDropLocationSearchUI(true);
                        } else {
                            stopDropLocationSearchUI(true);
                        }
                        setDropLocationEngagedUI();

                        setAssignedDriverData(mode);
						myLocationButtonPressed = true;
                        zoomtoPickupAndDriverLatLngBounds(Data.autoData.getAssignedDriverInfo().latLng, null, 0);

                        buttonCancelRide.setVisibility(View.GONE);
                        buttonAddMoneyToWallet.setVisibility(View.GONE);

                        if (TextUtils.isEmpty(Data.autoData.getInRideSendInviteTextBoldV2())
                                && TextUtils.isEmpty(Data.autoData.getInRideSendInviteTextNormalV2())) {
                            linearLayoutSendInvites.setVisibility(View.GONE);
                        } else if (Data.isMenuTagEnabled(MenuInfoTags.FREE_RIDES)) {
                            linearLayoutSendInvites.setVisibility(View.VISIBLE);
                        }
                        updateInRideAddMoneyToWalletButtonText();
                        setPaymentOptionInRide();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);

                        try {
                            if (Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1) {
                                relativeLayoutPoolSharing.setVisibility(View.VISIBLE);
                                imageViewFinalDropLocationEdit.setVisibility(View.GONE);
                            } else {
                                relativeLayoutPoolSharing.setVisibility(View.GONE);
                            }
                            setPoolRideStatus(Data.autoData.getAssignedDriverInfo().getPoolRideStatusString(), Data.autoData.getAssignedDriverInfo().getFellowRiders());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

						if(Data.autoData.getAssignedDriverInfo().getIsPooledRide() == 1
								|| Prefs.with(HomeActivity.this).getInt(KEY_REVERSE_BID, 0) == 1){
							imageViewFinalDropLocationEdit.setVisibility(View.GONE);
						}
                        checkForGoogleLogoVisibilityInRide();

                        setFabViewAtRide(mode);
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);




                        try {
                            getTrackingLogHelper().uploadAllTrackLogs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        showChatButton();

                        if (Data.autoData != null && Data.autoData.getReferralPopupContent() != null) {
                            if (Data.autoData.getReferralPopupContent().getShown() == 0) {
                                new OrderCompleteReferralDialog(this, new OrderCompleteReferralDialog.Callback() {
                                    @Override
                                    public void onDialogDismiss() {
                                    }

                                    @Override
                                    public void onConfirmed() {
                                    }
                                }).show(false, "", "", "", Data.autoData.getReferralPopupContent(),
                                        Integer.parseInt(Data.autoData.getcEngagementId()),
                                        -1, ProductType.AUTO.getOrdinal());
                                Data.autoData.getReferralPopupContent().setShown(1);
                            }
                        }

                        break;

                    case P_RIDE_END:

                        Log.d("HomeActivityResult" , " P_RIDE_END");

                        fabViewTest = new FABViewTest(this, fabViewFinal);
                        initialLayout.setVisibility(View.GONE);
                        assigningLayout.setVisibility(View.GONE);
                        relativeLayoutSearchSetVisiblity(View.GONE);
                        requestFinalLayout.setVisibility(View.GONE);

                        rentalInRideLayout.setVisibility(View.GONE);
                        rentalEndRideLayout.setVisibility(View.GONE);
                        damageReportButton.setVisibility(View.GONE);
                        tvDesc.setText(getResources().getString(R.string.add_your_reviews));

                        hideCenterPickupPin();

                        topBar.imageViewHelp.setVisibility(View.VISIBLE);
                        setGoogleMapPadding(0, false);

                        linearLayoutRideSummaryContainerSetVisiblity(View.GONE, RideEndFragmentMode.INVOICE);
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

                        dismissPushDialog(true);

                        Prefs.with(HomeActivity.this).save(SPLabels.ENTERED_DESTINATION, "");
                        //fabView.setRelativeLayoutFABVisibility(mode);

                        getTrackingLogHelper().uploadAllTrackLogs();
                        break;

                }

                initiateTimersForStates(mode);
                dismissReferAllDialog(mode);


                updateTopBar();

                updateDriverTipUI(mode);

                openPaytmRechargeDialog();

                showReferAllDialog();
                callT20AndReferAllDialog(mode);

				enableMapMyLocation();

                int savedMode = Prefs.with(this).getInt(SP_CURRENT_STATE, P_INITIAL.getOrdinal());
                if(savedMode != mode.getOrdinal()){
                	if((mode.getOrdinal() == PassengerScreenMode.P_REQUEST_FINAL.getOrdinal()
								&& Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ACCEPT, 0) == 1)
							|| (mode.getOrdinal() == PassengerScreenMode.P_DRIVER_ARRIVED.getOrdinal()
								&& Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ARRIVED, 0) == 1)
							|| (mode.getOrdinal() == PassengerScreenMode.P_IN_RIDE.getOrdinal()
								&& Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_START, 0) == 1)
							|| (mode.getOrdinal() == PassengerScreenMode.P_RIDE_END.getOrdinal()
								&& Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_END, 0) == 1)){
						SoundMediaPlayer.INSTANCE.startSound(this, R.raw.ride_status_update, 1, false);
					}
				}
                Prefs.with(this).save(SP_CURRENT_STATE, mode.getOrdinal());

                startStopLocationUpdateService(mode);

                showPokestopOnOffButton(mode);

                setScheduleRideUI(mode);
                badgesAdapter=null;


                try {
                    getTrackingLogHelper().startSyncService(mode, Data.userData.accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForAddedTip() {
        if(Data.autoData != null && Data.autoData.getNoDriverFoundTip() > 0.0) {
            tvAddedTip.setVisibility(View.VISIBLE);
            tvAddedTip.setText(getString(R.string.label_tip_added, Utils.formatCurrencyValue(Data.autoData.getCurrency(), Data.autoData.getNoDriverFoundTip())));
        } else {
            tvAddedTip.setVisibility(View.GONE);
        }
    }

    private void setDropEditInAssigningState() {
        try {
            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()
                || slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getReverseBid() == 1
                || (Data.autoData != null && Data.autoData.getIsReverseBid() == 1)) {
				tvDropAssigning.setEnabled(false);
            } else {
				tvDropAssigning.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLikeDropVisibilityAndBG() {
        ivLikeDrop.setVisibility(getLikePickupDropVisibility());
        if(ivLikeDrop.getVisibility() == View.VISIBLE && Data.autoData != null && Data.autoData.getDropLatLng() != null){
            SearchResult searchResult = HomeUtil.getNearBySavedAddress(this, Data.autoData.getDropLatLng(), false);
            if(searchResult != null){
                ivLikeDrop.setImageResource(R.drawable.ic_heart_filled);
                ivLikeDrop.setTag("liked");
            } else {
                ivLikeDrop.setImageResource(R.drawable.ic_heart);
                ivLikeDrop.setTag("");
            }
        }
    }

    private void setScheduleRideUI(PassengerScreenMode mode) {
        if (mode == P_INITIAL && isScheduleRideEnabled && !scheduleRideOpen) {
            topBar.imageViewScheduleRide.setVisibility(View.VISIBLE);
            if(showScheduleRideTut){
                showScheduleRideTut = false;
                Prefs.with(this).save(Constants.SHOW_TUT_SCHEDULE_RIDE, false);
                topBar.tvScheduleRidePopup.setVisibility(View.VISIBLE);
                getHandler().postDelayed(scheduleRideRunnable,5 * 1000);
            }else{
                topBar.tvScheduleRidePopup.setVisibility(View.GONE);
            }

        }else{
            topBar.tvScheduleRidePopup.setVisibility(View.GONE);
            topBar.imageViewScheduleRide.setVisibility(View.GONE);
        }
    }

    private void updateDriverTipUI(PassengerScreenMode mode) {
        if(mode==PassengerScreenMode.P_IN_RIDE && Data.autoData!=null  &&
                Data.autoData.getAssignedDriverInfo()!=null &&   Data.autoData.getIsTipEnabled()
                && Data.autoData.getAssignedDriverInfo().getIsCorporateRide() == 0
				&& Data.autoData.getAssignedDriverInfo().getTipBeforeRequestRide() <= 0){
            buttonTipDriver.setVisibility(View.VISIBLE);
            setAddedTipUI();
        }else if(mode==PassengerScreenMode.P_RIDE_END && Data.autoData!=null  &&
                Data.autoData.getIsTipEnabled()  &&  Data.autoData.getEndRideData()!=null &&
                Data.autoData.getEndRideData().getIsCorporateRide() == 0 &&
                Data.autoData.getEndRideData().getDriverTipAmount()<=0 &&
                Data.autoData.getEndRideData().getShowTipOption() == 1){
           buttonAddTipEndRide.setVisibility(View.GONE);
           llFeedbackMain.setVisibility(View.GONE);
           llAddTip.setVisibility(View.VISIBLE);
           double first = 10, second = 20, third = 30;
            try {
                String tipValues = Prefs.with(this).getString(KEY_CUSTOMER_TIP_VALUES, "10,20,30");
                if(tipValues.contains(",")){
                    String arr[] = tipValues.split(",");
                    if(arr.length > 0){
                        first = Double.parseDouble(arr[0]);
                    }
                    if(arr.length > 1){
                        second = Double.parseDouble(arr[1]);
                    }
                    if(arr.length > 2){
                        third = Double.parseDouble(arr[2]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTipFirst.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), first)); tvTipFirst.setTag(first);
           tvTipSecond.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), second)); tvTipSecond.setTag(second);
           tvTipThird.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), third)); tvTipThird.setTag(third);
            etTipOtherValue.setPrefixTextColor(ContextCompat.getColor(this, R.color.theme_color));
            etTipOtherValue.setPrefix(Data.autoData.getEndRideData().getCurrency());

        }else{
            driverTipInteractor = null;
            buttonAddTipEndRide.setVisibility(View.GONE);
            layoutAddedTip.setVisibility(View.GONE);
            buttonTipDriver.setVisibility(View.GONE);
            llFeedbackMain.setVisibility(View.VISIBLE);
            llAddTip.setVisibility(View.GONE);
        }

    }

    private void onTipSelected(boolean clearText){
        tvTipFirst.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        tvTipSecond.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        tvTipThird.setTextColor(ContextCompat.getColor(this, R.color.text_color));

        tvTipFirst.setBackgroundResource(R.drawable.circle_white_grey_selector);
        tvTipSecond.setBackgroundResource(R.drawable.circle_white_grey_selector);
        tvTipThird.setBackgroundResource(R.drawable.circle_white_grey_selector);

        if(tipSelected == (double)tvTipFirst.getTag()){
            tvTipFirst.setBackgroundResource(R.drawable.circle_theme);
            tvTipFirst.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        else if(tipSelected == (double)tvTipSecond.getTag()){
            tvTipSecond.setBackgroundResource(R.drawable.circle_theme);
            tvTipSecond.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        else if(tipSelected == (double)tvTipThird.getTag()){
            tvTipThird.setBackgroundResource(R.drawable.circle_theme);
            tvTipThird.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        if(clearText){
            etTipOtherValue.clearFocus();
            etTipOtherValue.removeTextChangedListener(textWatcherOtherTip);
            etTipOtherValue.setText("");
            etTipOtherValue.addTextChangedListener(textWatcherOtherTip);
            Utils.hideSoftKeyboard(this, etTipOtherValue);
        }
    }

    public int updateRideEndPayment() {
        try {
            textViewRSCashPaidValue.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), Data.autoData.getEndRideData().toPay));
            int onlinePaymentVisibility = (Data.autoData.getEndRideData().toPay > 0 &&
                    (Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.CASH.getOrdinal()
                            || Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
                            || Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.MPESA.getOrdinal())
                    && Data.autoData.getEndRideData().getShowPaymentOptions() == 1) ? View.VISIBLE : View.GONE;
            llPayOnline.setVisibility(onlinePaymentVisibility);
            cvPayOnline.setVisibility(onlinePaymentVisibility);
            tvPayOnline.setVisibility(onlinePaymentVisibility);

            if(Prefs.with(this).getInt(KEY_PAY_VIA_UPI_ENABLED, 0) == 1
					&& Data.autoData.getEndRideData().getPaymentOption() == PaymentOption.CASH.getOrdinal()
            		&& Data.autoData.getEndRideData().toPay > 0
					&& !TextUtils.isEmpty(Data.autoData.getEndRideData().getDriverUpiId())){
				cvPayOnline.setVisibility(View.VISIBLE);
				tvPayOnline.setVisibility(View.VISIBLE);
				llPayViaUpi.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llPayViaUpi.getLayoutParams();
				params.setMarginStart(llPayOnline.getVisibility() == View.VISIBLE ? Utils.dpToPx(this, 12) : 0);
				llPayViaUpi.setLayoutParams(params);
			} else {
				llPayViaUpi.setVisibility(View.GONE);
			}

            return onlinePaymentVisibility;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return View.GONE;
    }

    private void showChatButton() {
        try {
            if (passengerScreenMode != PassengerScreenMode.P_IN_RIDE
					&& Data.autoData.getAssignedDriverInfo().getChatEnabled() == 1) {
                rlChatDriver.setVisibility(View.VISIBLE);
				buttonCallDriver.setVisibility(View.GONE);
                if (Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 0) > 0) {
                    tvChatCount.setVisibility(View.VISIBLE);
                    tvChatCount.setText(Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 0));
                } else {
                    tvChatCount.setVisibility(View.GONE);
                    Prefs.with(HomeActivity.this).save(KEY_CHAT_COUNT, 0);
                }
            } else {
                rlChatDriver.setVisibility(View.GONE);
				buttonCallDriver.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setFabViewAtRide(PassengerScreenMode mode) {
        float containerHeight = 110f;
        if (buttonCancelRide.getVisibility() == View.VISIBLE) {
            containerHeight = containerHeight + 40f;
        }
        if (relativeLayoutPoolSharing.getVisibility() == View.VISIBLE) {
            containerHeight = containerHeight + 50f;
        }
        if (relativeLayoutInRideInfo.getVisibility() == View.VISIBLE) {
            containerHeight = containerHeight + 50f;
        }
        if(layoutAddedTip.getVisibility() == View.VISIBLE) {
//            fabViewTest.setFABButtons()
        } else if (layoutAddedTip.getVisibility() == View.GONE) {
//            containerHeight = containerHeight + 50f;
        }
        fabViewTest.setMenuLabelsRightTestPadding(containerHeight);
    }


    private void callT20AndReferAllDialog(PassengerScreenMode mode) {
        t20Ops.openDialog(this, Data.autoData.getcEngagementId(), mode, new T20Dialog.T20DialogCallback() {
            @Override
            public void onDismiss() {
            }

            @Override
            public void notShown() {
            }
        });
    }

    private void showPromoFailedAtSignupDialog() {
        try {
            if (Data.userData.getPromoSuccess() == 0
                    && P_INITIAL == passengerScreenMode) {
                DialogPopup.alertPopupWithListener(HomeActivity.this, "",
                        Data.userData.getPromoMessage(),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MenuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag(), 0, 0, HomeActivity.this, getCurrentPlaceLatLng());
                            }
                        });
                Data.userData.setPromoSuccess(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public SlidingBottomPanelV4 getSlidingBottomPanel() {
        return slidingBottomPanel;
    }


    private void startStopLocationUpdateService(PassengerScreenMode mode) {
        Prefs.with(this).save(Constants.SP_CURRENT_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
        if ((PassengerScreenMode.P_REQUEST_FINAL == mode
                || PassengerScreenMode.P_DRIVER_ARRIVED == mode
                || PassengerScreenMode.P_IN_RIDE == mode)
                && Prefs.with(this).getLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_INTERVAL) > 0) {
            if (!Utils.isServiceRunning(this, LocationUpdateService.class.getName())) {
                if (PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
                    permissionGranted(REQUEST_CODE_LOCATION_SERVICE);
                }

               /* if(Prefs.with(this).getBoolean(ASK_LOCATION_PERMISSION_BEFORE_RIDE))
                getPermissionCommon().getPermission(REQUEST_CODE_LOCATION_SERVICE,  android.Manifest.permission.ACCESS_FINE_LOCATION);*/
            } else {
                Intent intent = new Intent(this, LocationUpdateService.class);
                intent.putExtra(ACTION_UPDATE_STATE, 1);
                startService(intent);
            }
        } else {
            Intent intent = new Intent(this, LocationUpdateService.class);
            stopService(intent);
        }
    }

    private void startInRideLocationService() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        intent.putExtra(KEY_ONE_SHOT, false);
        startService(intent);
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

    private void dismissReferAllDialog(PassengerScreenMode mode) {
        try {
            if (PassengerScreenMode.P_IN_RIDE != mode
                    && P_INITIAL != mode) {
                dismissReferAllDialog();
                dialogUploadContacts = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissReferAllDialog() {
        if (dialogUploadContacts != null && dialogUploadContacts.isShowing()) {
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

    public View getViewPoolInfoBarAnim() {
        return viewPoolInfoBarAnim;
    }

    private void setZeroRatingView() {
        textViewRSWhatImprove.setVisibility(View.GONE);
        gridViewRSFeedbackReasons.setVisibility(View.GONE);
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextRSFeedback.getLayoutParams();
//        layoutParams.height = (int) (ASSL.Yscale() * 200);
//        editTextRSFeedback.setLayoutParams(layoutParams);
        textViewRSOtherError.setText("");
    }

    private PlaceSearchListFragment getPlaceSearchListFragment(PassengerScreenMode mode) {
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(PlaceSearchListFragment.class.getSimpleName() + mode);
        return (PlaceSearchListFragment) frag;
    }

    private void relativeLayoutSearchSetVisiblity(int visiblity) {
        if (View.VISIBLE == visiblity) {
            relativeLayoutSearch.setVisibility(View.VISIBLE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_SEARCH);
            if (frag == null || frag.isRemoving()) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SEARCH_FIELD_TEXT, "");

                int modeClicked ;
                if (placeSearchMode.getOrdinal() == PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal()) {
                    bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.enter_destination));
                    modeClicked = PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal();
                } else {
                    bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.enter_pickup));
                    modeClicked = PlaceSearchListFragment.PlaceSearchMode.PICKUP.getOrdinal();
                }
                int mode = placeSearchMode.getOrdinal();
//                if(selectPickUpdropAtOnce && (P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)){
//                    mode = PlaceSearchListFragment.PlaceSearchMode.PICKUP_AND_DROP.getOrdinal();
//                }
                bundle.putInt(KEY_SEARCH_MODE, mode);
                bundle.putInt(KEY_SEARCH_MODE_CLICKED, modeClicked);
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutSearch.getId(), PlaceSearchListFragment.newInstance(bundle),
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_SEARCH)
                        .commitAllowingStateLoss();
            }
        } else {
            relativeLayoutSearch.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_SEARCH);
            if (frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }


    private void relativeLayoutAssigningDropLocationParentSetVisibility(int visiblity) {
        if (View.VISIBLE == visiblity) {

            fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            relativeLayoutAssigningDropLocationParent.setVisibility(View.VISIBLE);

            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_ASSIGNING);
            if (frag == null || frag.isRemoving()) {
                Bundle bundle = new Bundle();
                if (tvDropAssigning.getText().length() > 0) {
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, tvDropAssigning.getText().toString());
                } else {
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
                }
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                bundle.putInt(KEY_SEARCH_MODE, PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal());
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutAssigningDropLocationParent.getId(), PlaceSearchListFragment.newInstance(bundle),
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_ASSIGNING)
                        .commitAllowingStateLoss();
            }
        } else {
            relativeLayoutAssigningDropLocationParent.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_ASSIGNING);
            if (frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            setJeanieVisibility();
        }
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) assigningLayout.getLayoutParams();
        params.topMargin = View.VISIBLE == visiblity ? (int)(ASSL.Yscale() * 96F) : isNewUI() ? 0 : (int)(ASSL.Yscale() * 96F);
		assigningLayout.setLayoutParams(params);
		setTopBarTransNewUI();
    }

    private void relativeLayoutFinalDropLocationParentSetVisibility(int visiblity, String text) {
        if (View.VISIBLE == visiblity) {
            fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            relativeLayoutFinalDropLocationParent.setVisibility(View.VISIBLE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if (frag == null || frag.isRemoving()) {
                Bundle bundle = new Bundle();
                if (textViewFinalDropLocationClick.getText().length() > 0) {
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, textViewFinalDropLocationClick.getText().toString());
                } else {
                    bundle.putString(KEY_SEARCH_FIELD_TEXT, text);
                }
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                bundle.putInt(KEY_SEARCH_MODE, PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal());
                getSupportFragmentManager().beginTransaction()
                        .add(relativeLayoutFinalDropLocationParent.getId(), PlaceSearchListFragment.newInstance(bundle),
                                PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_REQUEST_FINAL)
                        .commitAllowingStateLoss();
            }
            customerInRideMyLocationBtn.setVisibility(View.GONE);
        } else {
            relativeLayoutFinalDropLocationParent.setVisibility(View.GONE);
            Fragment frag = getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL);
            if (frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            setJeanieVisibility();
            customerInRideMyLocationBtn.setVisibility(View.VISIBLE);
        }
    }


    private Fragment fragToAdd;

    private void linearLayoutRideSummaryContainerSetVisiblity(int visiblity, RideEndFragmentMode rideEndFragmentMode) {
        if (View.VISIBLE == visiblity) {
            linearLayoutRideSummaryContainer.setVisibility(View.VISIBLE);
            Fragment fragToCheck = null;
            String tag = "", title = "";
            if (RideEndFragmentMode.INVOICE == rideEndFragmentMode) {
                fragToCheck = getRideSummaryFragment();
                fragToAdd = RideSummaryFragment.newInstance(-1, null, false, EngagementStatus.ENDED.getOrdinal());
                tag = RideSummaryFragment.class.getName();
                title = getResources().getString(R.string.receipt);
            } else if (RideEndFragmentMode.ONLINE_PAYMENT == rideEndFragmentMode) {
                fragToCheck = getStarSubscriptionCheckoutFragment();
                fragToAdd = StarSubscriptionCheckoutFragment.newInstance(Integer.parseInt(Data.autoData.getEndRideData().engagementId),
                        Data.autoData.getEndRideData().finalFare,
                        Data.autoData.getEndRideData().toPay,
                        Data.autoData.getEndRideData().getCurrency());
                tag = StarSubscriptionCheckoutFragment.class.getName();
                title = getResources().getString(R.string.pay_online);
            }
            if ((fragToCheck == null || fragToCheck.isRemoving())
                    && fragToAdd != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(linearLayoutRideSummaryContainer.getId(),
                                fragToAdd,
                                tag)
                        .addToBackStack(tag)
                        .commitAllowingStateLoss();
                topBar.setTopBarState(false, title);
            }
        } else {
            linearLayoutRideSummaryContainer.setVisibility(View.GONE);
            if (fragToAdd != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragToAdd)
                        .commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            topBar.setTopBarState(true, "");
        }
    }

    private RideSummaryFragment getRideSummaryFragment() {
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(RideSummaryFragment.class.getName());
        return (RideSummaryFragment) frag;
    }

    private StarSubscriptionCheckoutFragment getStarSubscriptionCheckoutFragment() {
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag(StarSubscriptionCheckoutFragment.class.getName());
        return (StarSubscriptionCheckoutFragment) frag;
    }

    private void updateInRideAddMoneyToWalletButtonText() {
        try {
            MyApplication.getInstance().getWalletCore().addMoneyToWalletTextDuringRide(Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode());
        } catch (Exception e) {
        }
    }


    private void setDropLocationAssigningUI() {
        try {
        	getAddressAsync(Data.autoData.getPickupLatLng(), tvPickupAssigning, null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
			if (getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal() ||
                    Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())) {
				tvDropAssigning.setVisibility(View.GONE);
				findViewById(R.id.iv2NewUIDropDashedLineAssigning).setVisibility(View.GONE);
				findViewById(R.id.iv3NewUIDropMarkAssigning).setVisibility(View.GONE);
			} else {
				tvDropAssigning.setVisibility(View.VISIBLE);
				findViewById(R.id.iv2NewUIDropDashedLineAssigning).setVisibility(View.VISIBLE);
				findViewById(R.id.iv3NewUIDropMarkAssigning).setVisibility(View.VISIBLE);
			}
			setDropLocationMarker();
			setDropEditInAssigningState();
			if (Data.autoData.getDropLatLng() != null && passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
				getAddressAsync(Data.autoData.getDropLatLng(), tvDropAssigning, null, PlaceSearchListFragment.PlaceSearchMode.DROP);
			} else if (Data.autoData.getDropLatLng() == null) {
				tvDropAssigning.setText("");
				dropAddressName = "";
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPickupLocationInitialUI() {
        try {
			if (getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal() &&
                    !Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())
                    && (Data.autoData.getDropLatLng() == null || Data.autoData.getDropAddress() == null)
                    && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1 && !isPickupSet) {
                relativeLayoutInitialSearchBarNew.setVisibility(View.GONE);
				findViewById(R.id.iv1).setVisibility(View.GONE);
				findViewById(R.id.iv2NewUIDropDashedLine).setVisibility(View.GONE);
			} else {
                relativeLayoutInitialSearchBarNew.setVisibility(View.VISIBLE);
                findViewById(R.id.iv1).setVisibility(View.VISIBLE);
                findViewById(R.id.iv2NewUIDropDashedLine).setVisibility(View.VISIBLE);
			}
			if ((Data.autoData.getDropLatLng() == null || Data.autoData.getDropAddress() == null)
					&& Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1 && !isPickupSet) {
				setHeightDropAddress(0); // large
			} else {
				setHeightDropAddress(1); //Normal
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setDropLocationEngagedUI() {
        if (Data.autoData.getDropLatLng() == null) {
            textViewFinalDropLocationClick.setText("");
            dropAddressName = "";
            imageViewFinalDropLocationEdit.setVisibility(View.GONE);
            progressBarFinalDropLocation.setVisibility(View.GONE);
        } else {
            setDropLocationMarker();
            imageViewFinalDropLocationEdit.setVisibility(View.VISIBLE);
            Log.w("getAddressAsync", "setDropEngagedUI");
            getAddressAsync(Data.autoData.getDropLatLng(), textViewFinalDropLocationClick, progressBarFinalDropLocation, PlaceSearchListFragment.PlaceSearchMode.DROP);
        }
    }

    private void setDropLocationMarker() {
        if (Data.autoData.getDropLatLng() != null
                && (PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)) {
            if (dropLocationMarker != null) {
                dropLocationMarker.remove();
            }
            boolean savedLocationView = false;
            if((PassengerScreenMode.P_IN_RIDE == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
             || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode)
                    && showSaveLocationDialog()
                    && HomeUtil.getNearBySavedAddress(this, Data.autoData.getDropLatLng(), false) == null) {
                savedLocationView = true;
            }
            if(Data.autoData.getAssignedDriverInfo() != null
					&& Data.autoData.getAssignedDriverInfo().getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal()) {
            	dropLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.autoData.getDropLatLng(), savedLocationView && !Prefs.with(HomeActivity.this).getBoolean(Constants.SKIP_SAVE_DROP_LOCATION, false)));
            } else if(PassengerScreenMode.P_ASSIGNING == passengerScreenMode
					&& slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal()){
				dropLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.autoData.getDropLatLng(), savedLocationView && !Prefs.with(HomeActivity.this).getBoolean(Constants.SKIP_SAVE_DROP_LOCATION, false)));
			}

        }
    }

    private void setPickupToDropPath() {
        if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
            if (pathToDropLocationPolylineOptions != null) {
                if (pathToDropLocationPolyline != null) {
                    pathToDropLocationPolyline.remove();
                }
                pathToDropLocationPolyline = map.addPolyline(pathToDropLocationPolylineOptions);
            }
            plotPolylineInRideDriverPath();
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


    private void checkForGoogleLogoVisibilityInRide() {
        try {
        	if(linearLayoutInRideDriverInfo == null || map == null){
        		return;
			}
			linearLayoutInRideDriverInfo.post(new Runnable() {
				@Override
				public void run() {
					setGoogleMapPadding(linearLayoutInRideDriverInfo.getMeasuredHeight() - (int) (125 * ASSL.Yscale()), true);
				}
			});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAssignedDriverData(PassengerScreenMode mode) {
        try {
            textViewInRideDriverName.setText(Data.autoData.getAssignedDriverInfo().name);
            if (!"".equalsIgnoreCase(Data.autoData.getAssignedDriverInfo().carNumber)) {
                textViewInRideDriverCarNumber.setText(Data.autoData.getAssignedDriverInfo().carNumber);
            } else {
                textViewInRideDriverCarNumber.setText("");
            }

            if (!getString(R.string.no_promo_code_applied).equalsIgnoreCase(Data.autoData.getAssignedDriverInfo().promoName)) {
                relativeLayoutInRideInfo.setVisibility(View.VISIBLE);
                textViewInRidePromoName.setText(Data.autoData.getAssignedDriverInfo().promoName);
            } else {
                relativeLayoutInRideInfo.setVisibility(View.GONE);
                textViewInRidePromoName.setText("");
            }


            if (Data.autoData.getFareFactor() > 1 || Data.autoData.getFareFactor() < 1) {
                linearLayoutSurgeContainer.setVisibility(View.VISIBLE);
                textViewInRideFareFactor.setText(decimalFormat.format(Data.autoData.getFareFactor()) + "x");
            } else {
                linearLayoutSurgeContainer.setVisibility(View.GONE);
                textViewInRideFareFactor.setText("");
            }


            if (PassengerScreenMode.P_REQUEST_FINAL == mode || PassengerScreenMode.P_DRIVER_ARRIVED == mode) {
                updateDriverETAText(mode);
            } else {
                textViewInRideState.setText(R.string.ride_in_progress);
            }

            try {
                double rating = Double.parseDouble(Data.autoData.getAssignedDriverInfo().rating);
                if (rating > 0) {
                    relativeLayoutDriverRating.setVisibility(View.VISIBLE);
                    textViewDriverRating.setText(decimalFormat1DigitAD.format(rating));
                } else {
                    relativeLayoutDriverRating.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                relativeLayoutDriverRating.setVisibility(View.GONE);
            }

            Data.autoData.getAssignedDriverInfo().image = Data.autoData.getAssignedDriverInfo().image.replace("http://graph.facebook", "https://graph.facebook");
            try {
                if (!"".equalsIgnoreCase(Data.autoData.getAssignedDriverInfo().image)) {
                    float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                    Picasso.with(HomeActivity.this).load(Data.autoData.getAssignedDriverInfo().image)
                            .placeholder(R.drawable.ic_driver_placeholder)
                            .transform(new CircleTransform())
                            .resize((int) (130f * minRatio), (int) (130f * minRatio)).centerCrop()
                            .into(imageViewInRideDriver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPaymentOptionInRide() {
        try {
            imageViewIRPaymentOption.setImageResource(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionIconSmall(Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode()));
            textViewIRPaymentOptionValue.setTextColor(MyApplication.getInstance().getWalletCore()
                    .getWalletBalanceColor(Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode()));
            if(Data.autoData.getAssignedDriverInfo().getIsCorporateRide() == 1){
                textViewIRPaymentOptionValue.setText(getString(R.string.corporate));
                imageViewIRPaymentOption.setImageResource(R.drawable.ic_corporate);
            }
            else if (PaymentOption.CASH.getOrdinal() == Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode()) {
                textViewIRPaymentOptionValue.setText(getString(R.string.cash));
            } else {
                textViewIRPaymentOptionValue.setText(MyApplication.getInstance().getWalletCore()
                        .getPaymentOptionBalanceText(Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode(),this));
            }
            if(PaymentOption.CASH.getOrdinal() == Data.autoData.getAssignedDriverInfo().getPreferredPaymentMode()
                    && Prefs.with(this).getInt(KEY_SHOW_IN_RIDE_PAYMENT_OPTION, 1) != 1){
                imageViewIRPaymentOption.setVisibility(View.GONE);
                textViewIRPaymentOptionValue.setVisibility(View.GONE);
                findViewById(R.id.ivDivBeforePaymentOps).setVisibility(View.GONE);
            } else {
                imageViewIRPaymentOption.setVisibility(View.VISIBLE);
                textViewIRPaymentOptionValue.setVisibility(View.VISIBLE);
                findViewById(R.id.ivDivBeforePaymentOps).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }


    public void updateDriverETAText(PassengerScreenMode passengerScreenMode) {
        if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode) {
            if (!"".equalsIgnoreCase(Data.autoData.getAssignedDriverInfo().getEta())) {
                try {
                    if (Data.autoData.getDropLatLng() != null) {
                        pickupLocationMarker.setIcon(BitmapDescriptorFactory
                                .fromBitmap(CustomMapMarkerCreator
                                        .getTextAssignBitmap(HomeActivity.this, Data.autoData.getAssignedDriverInfo().getEta(),
                                                getResources().getDimensionPixelSize(R.dimen.text_size_24))));
                    } else {
                        pickupLocationMarker.setIcon(BitmapDescriptorFactory
                                .fromBitmap(CustomMapMarkerCreator
                                        .getTextBitmap(HomeActivity.this, Data.autoData.getAssignedDriverInfo().getEta(),
                                                getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewInRideState.setText(R.string.driver_enroute);
            }
        } else if (PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
            textViewInRideState.setText(R.string.arrived_at_pickup);
        }
    }


    private void initDropLocationSearchUI(boolean engaged) {
        try {
            dropLocationSearched = true;
            if (!engaged) {
                relativeLayoutAssigningDropLocationParentSetVisibility(View.VISIBLE);
            } else {
                relativeLayoutFinalDropLocationParentSetVisibility(View.VISIBLE, dropLocationSearchText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopDropLocationSearchUI(boolean engaged) {
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
        String clientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
        if (Data.userData == null) {
            boolean dataFoundNull = false;
            if (clientId.equalsIgnoreCase(Config.getAutosClientId()) && Data.autoData == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getFreshClientId()) && Data.getFreshData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getMealsClientId()) && Data.getMealsData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getGroceryClientId()) && Data.getGroceryData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getMenusClientId()) && Data.getMenusData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId()) && Data.getDeliveryCustomerData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getPayClientId()) && Data.getPayData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getFeedClientId()) && Data.getFeedData() == null) {
                dataFoundNull = true;
            } else if (clientId.equalsIgnoreCase(Config.getProsClientId()) && Data.getProsData() == null) {
                dataFoundNull = true;
            }
            if (dataFoundNull) {
                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private void fetchContacts() {
        if (getPermissionCommon().isGranted(Manifest.permission.READ_CONTACTS, this)) {
            Intent syncContactsIntent = new Intent(HomeActivity.this, ContactsUploadService.class);
            syncContactsIntent.putExtra("access_token", Data.userData.accessToken);
            syncContactsIntent.putExtra("session_id", Data.autoData.getcSessionId());
            syncContactsIntent.putExtra("engagement_id", Data.autoData.getcEngagementId());
            syncContactsIntent.putExtra(KEY_IS_LOGIN_POPUP, "0");
            startService(syncContactsIntent);
            registerDialogDismissReceiver();
            dismissReferAllDialog();
        } else {
            getPermissionCommon().getPermission(REQ_CODE_PERMISSION_CONTACT, Manifest.permission.READ_CONTACTS);
        }
    }


    private void showReferAllDialog() {
        try {
            if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
                //******** If return 0 then show popup, contact not saved in database.
                if (Data.autoData.getReferAllStatus() == 0
                        && (Prefs.with(HomeActivity.this).getInt(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0) == 0)
                        && dialogUploadContacts == null
                        && getString(R.string.no_promo_code_applied).equalsIgnoreCase(Data.autoData.getAssignedDriverInfo().promoName)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    dialogUploadContacts = DialogPopup.uploadContactsTwoButtonsWithListeners(HomeActivity.this,
                            Data.autoData.getReferAllTitle(),
                            Data.autoData.getReferAllText(),
                            getResources().getString(R.string.upload_contact_yes),
                            getResources().getString(R.string.upload_contact_no_thanks),
                            false,
                            new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (MyApplication.getInstance().isOnline()) {
                                        Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 1);
                                        fetchContacts();
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
                                    if (MyApplication.getInstance().isOnline()) {
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
            } else if (P_INITIAL == passengerScreenMode) {
                if (Data.autoData.getReferAllStatusLogin() == 0
                        && dialogUploadContacts == null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    dialogUploadContacts = DialogPopup.uploadContactsTwoButtonsWithListeners(HomeActivity.this,
                            Data.autoData.getReferAllTitleLogin(),
                            Data.autoData.getReferAllTextLogin(),
                            getResources().getString(R.string.upload_contact_yes),
                            getResources().getString(R.string.upload_contact_no_thanks),
                            false,
                            new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (MyApplication.getInstance().isOnline()) {

                                        Data.autoData.setReferAllStatus(1);
                                        Data.autoData.setReferAllStatusLogin(1);
                                        fetchContacts();
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
                                    if (MyApplication.getInstance().isOnline()) {
                                        Data.autoData.setReferAllStatus(0);
                                        Data.autoData.setReferAllStatusLogin(-1);
                                        uploadContactsApi(true);
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
//                                    showPoolIntroDialog();
                                    showPromoFailedAtSignupDialog();
                                }
                            });
                } else {
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
        if (intent.hasExtra(Constants.KEY_EVENT)
                && intent.getStringExtra(Constants.KEY_EVENT).equalsIgnoreCase(Constants.KEY_RIDE_ACCEPTED)) {
            GAUtils.event(RIDES, HOME, RIDE_ACCEPTED_PUSH + CLICKED);
        }
        try {

            if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.FUGU_CUSTOM_ACTION_PAYLOAD)) {
                Log.i(TAG, "onNewIntent: Fugu Broadcast received");
                String payload = intent.getStringExtra(Constants.FUGU_CUSTOM_ACTION_PAYLOAD);
                FuguCustomActionModel customActionModel = gson.fromJson(payload, FuguCustomActionModel.class);
                if (customActionModel.getDeepIndex() != null && customActionModel.getDeepIndex() != -1) {
                    Data.deepLinkIndex = customActionModel.getDeepIndex();
                    if (customActionModel.getDeepIndex() == AppLinkIndex.RIDE_HISTORY.getOrdinal()) {
                        Data.deepLinkOrderId = customActionModel.getOrderId();
                        Data.deepLinkProductType = ProductType.FEED.getOrdinal();
                    }
                    MenuAdapter.closeDrawerIfOpen(this);
                    DeepLinkAction.openDeepLink(this, getCurrentPlaceLatLng());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean ignoreTimeCheckFetchWalletBalance = true;

    Runnable runnableFindADriver = new Runnable() {
        @Override
        public void run() {
            if(P_INITIAL == passengerScreenMode) {
                callMapTouchedRefreshDrivers(getApiFindADriver().getParams());
            }
        }
    };

    private StreamClient streamClient = new StreamClient();

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(() -> {
            if(Prefs.with(HomeActivity.this).getBoolean(Constants.SP_PROMO_SCRATCHED, false)) {
                slidingBottomPanel.getRequestRideOptionsFragment().selectAutoSelectedCouponAtRequestRide();
                Prefs.with(HomeActivity.this).save(Constants.SP_PROMO_SCRATCHED, false);
            }
        }, 400);

        try {


            if(pushDialog != null) {
                pushDialog.onResume();
            }
            removeSaveLocationDialog();

            checkForNoDriverFoundHelp();

            if(Data.autoData != null && Data.autoData.getServiceTypeSelected() != null) {
                isScheduleRideEnabled = Data.autoData.getServiceTypeSelected().getScheduleAvailable() == 1;
            } else if(Data.autoData != null && Data.autoData.getServiceTypes() != null && Data.autoData.getServiceTypes().size() > 0
                    && Data.autoData.getServiceTypes().get(0) != null){
                isScheduleRideEnabled = Data.autoData.getServiceTypes().get(0).getScheduleAvailable() == 1;
            }
            setScheduleIcon();

            enableMapMyLocation();
            Data.setLastActivityOnForeground(HomeActivity.this);

            switchAppOfClientId(this, getCurrentPlaceLatLng());

            Utils.hideSoftKeyboard(HomeActivity.this, editTextRSFeedback);
            if (!checkIfUserDataNull(HomeActivity.this)) {
                setUserData();

                try {
                    if (activityResumed) {
                        saveDriverBearing();
                        if (!intentFired
                                && P_INITIAL == passengerScreenMode && !confirmedScreenOpened
                                && map != null && myLocation != null && !isSpecialPickupScreenOpened()
                                && PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
                                && Prefs.with(this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 1 && !isNewUI) {
                            try {

                                LatLng currLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                Data.setLatLngOfJeanieLastShown(currLatLng);
                                Data.autoData.setLastRefreshLatLng(currLatLng);
                                Log.w("getAddressAsync", "onResume");
                                getAddressAsync(currLatLng, getInitialPickupTextView(), null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
                            } catch (Exception ignored) {}
                            initialMyLocationBtn.performClick();
                            mapTouched = true;
                        }
                        if (!feedbackSkipped && !placeAdded
                                && PassengerScreenMode.P_RIDE_END != passengerScreenMode
                                && PassengerScreenMode.P_SEARCH != passengerScreenMode
                                && !isPoolRideAtConfirmation()
                                && !isNormalRideWithDropAtConfirmation()
                                && !isSpecialPickupScreenOpened()
                                && !rentalCaseResume) {
                            callAndHandleStateRestoreAPI(false);
                        } else {
                            initiateTimersForStates(passengerScreenMode);
                        }
                        getHandler().postDelayed(runnableFindADriver, 200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rentalCaseResume = false;

                try {
                    fetchWalletBalance(this, ignoreTimeCheckFetchWalletBalance);
                    ignoreTimeCheckFetchWalletBalance = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (Data.autoData.isSupportFeedbackSubmitted()) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogPopup.dialogBanner(HomeActivity.this, getString(R.string.thank_you_for_valuable_feedback));
                            }
                        }, 300);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Data.autoData.setSupportFeedbackSubmitted(false);


                performDeeplink();


                String alertMessage = Prefs.with(this).getString(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                if (!"".equalsIgnoreCase(alertMessage)) {
                    Prefs.with(this).save(SPLabels.UPLOAD_CONTACTS_ERROR, "");
                    DialogPopup.alertPopup(this, "", alertMessage);
                }

                updateTopBar();

                try {
                    pokestopHelper.checkPokestopData(map.getCameraPosition().target, Data.userData.getCurrentCity());
                } catch (Exception e) {
                }


                // to check if selected destination saved address is deleted or not
                if (passengerScreenMode == P_INITIAL) {
                	if(Data.autoData != null && Data.autoData.getDropLatLng() != null) {
						SearchResult searchResult = HomeUtil.getNearBySavedAddress(HomeActivity.this, Data.autoData.getDropLatLng(),
								false);
						if (searchResult == null && Data.autoData.getDropAddressId() > 0) {
							imageViewDropCross.performClick();
							imageViewDropCrossNew.performClick();
						}
					}
					if(intentFired && Data.autoData.getPickupLatLng() != null){
                        SearchResult searchResult = HomeUtil.getNearBySavedAddress(HomeActivity.this, Data.autoData.getPickupLatLng(),
								false);
                        if (searchResult != null) {
                            textViewInitialSearch.setText(searchResult.getName());
                            textViewInitialSearchNew.setText(searchResult.getName());
                            tvPickupRentalOutstation.setText(searchResult.getName());
                        } else {
                        	String address = Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng());
                            textViewInitialSearch.setText(address);
                            textViewInitialSearchNew.setText(address);
                            tvPickupRentalOutstation.setText(address);
                        }
                    }
                }
            }


            activityResumed = true;
            intentFired = false;
            feedbackSkipped = false;
            placeAdded = false;

            initializeFusedLocationFetchers();

            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 19) {
                DialogPopup.showLocationSettingsAlert(HomeActivity.this);
            }

            Utils.hideSoftKeyboard(this, editTextRSFeedback);


            if (AccountActivity.updateMenuBar) {
                menuBar.setProfileData();
                AccountActivity.updateMenuBar = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkForNoDriverFoundHelp() {
        String msg = Prefs.with(this).getString(KEY_PUSH_NO_DRIVER_FOUND_HELP, "");
        if(msg != null && !msg.isEmpty()) {
            try {
                onNoDriverHelpPushReceived(new JSONObject(msg));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Prefs.with(this).save(KEY_PUSH_NO_DRIVER_FOUND_HELP, "");
        }
    }

    private void removeSaveLocationDialog() {
        // **** remove save location overlay *** //
        DialogFragment prev = (DialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            prev.dismiss();
        }
        // *************************************** //
    }

    private void performDeeplink() {
        if (Data.userData != null && Data.userData.isRidesAndFatafatEnabled()
                && !Prefs.with(this).getString(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "").equalsIgnoreCase("")) { //or deeplink to other client id
            Data.deepLinkIndex = AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal();
        }
        deepLinkAction.openDeepLink(HomeActivity.this, getCurrentPlaceLatLng());
        performDeepLinkForLatLngRequest();
    }

    public static void switchAppOfClientId(Activity activity, LatLng latLng) {
        String clientIdFromHomeSwitcher = Prefs.with(activity).getString("home_switcher_client_id", "");
        if (!TextUtils.isEmpty(clientIdFromHomeSwitcher)) {
            MyApplication.getInstance().getAppSwitcher().switchApp(activity, clientIdFromHomeSwitcher, null,
                    latLng, null);
        }
        Prefs.with(activity).save("home_switcher_client_id", "");
    }

    private void saveDriverBearing() {
        try {
            if ((passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL
                    || passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED)
                    && driverLocationMarker != null) {
                Prefs.with(HomeActivity.this).save(SP_DRIVER_BEARING, driverLocationMarker.getRotation());
            } else if (passengerScreenMode == PassengerScreenMode.P_IN_RIDE
                    && driverMarkerInRide != null) {
                Prefs.with(HomeActivity.this).save(SP_DRIVER_BEARING, driverMarkerInRide.getRotation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bChatDriver:
                openChatScreen();
                break;
            case R.id.buttonEndRide: // 8
/*
                DialogPopup.alertPopupTwoButtonsWithListeners(this, getString(R.string.please_lock_the_bike_and_proceed), v1 -> {

                });
*/
                textViewEndRide.setText(R.string.ending_ride);
                updateLockStatusApi(GpsLockStatus.REQ_END_RIDE_LOCK);

                break;
            case R.id.buttonUnlockRide: // 6
                textViewEndRide.setText(R.string.unlocking_ride);
                updateLockStatusApi(GpsLockStatus.REQ_UNLOCK);
                break;
            case R.id.buttonLockRide: // 4
/*
                DialogPopup.alertPopupTwoButtonsWithListeners(this, getString(R.string.please_lock_the_bike_and_proceed), v1 -> {

                });
*/
                textViewEndRide.setText(R.string.locking_ride);
                updateLockStatusApi(GpsLockStatus.REQ_LOCK);

                break;
        }
    }

    public void openChatScreen() {
        Prefs.with(HomeActivity.this).save(KEY_CHAT_COUNT, 0);
        tvChatCount.setVisibility(View.GONE);
        startActivity(new Intent(HomeActivity.this, ChatActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        GAUtils.event(RIDES, DRIVER_ENROUTE, CHAT + GAAction.BUTTON + CLICKED);
    }

    public CallbackPaymentOptionSelector getCallbackPaymentOptionSelector() {
        return callbackPaymentOptionSelector;
    }



    private void performDeepLinkForLatLngRequest() {
        try {
            if (P_INITIAL == passengerScreenMode) {
                if (Data.deepLinkPickup == 1) {
                    zoomingForDeepLink = true;
                    getHandler().postDelayed(new Runnable() {
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
        } catch (Exception e) {
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
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        intentFired = true;
        super.startActivityForResult(intent, requestCode, options);
    }

    boolean rentalCaseResume = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if(requestCode==REQ_BLE_ENABLE){
                 Log.e(TAG,"bluetooth permission result");
                 initiateBleProcess();
                }else if(requestCode==REQ_CODE_ADD_CARD_DRIVER_TIP){
                    if(driverTipInteractor!=null && driverTipInteractor.actionButton!=null){
                        driverTipInteractor.actionButton.performClick();
                    }

                }else if (requestCode == Constants.REQUEST_CODE_ADD_HOME
                        || requestCode == Constants.REQUEST_CODE_ADD_WORK
                        || requestCode == REQUEST_CODE_ADD_NEW_LOCATION) {
                    if(likeClicked == 0) {   // likeClicked == 0 come from save Location UI
                        if(pickupLocationMarker != null) {
                            pickupLocationMarker.remove();
                        }
                        pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.autoData.getPickupLatLng(),
                                passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED || passengerScreenMode == PassengerScreenMode.P_IN_RIDE));
                        setDropLocationMarker();
                    } else {
                        String strResult = data.getStringExtra("PLACE");
                        SearchResult searchResult = new Gson().fromJson(strResult, SearchResult.class);
                        if (searchResult != null) {
                            placeAdded = true;
                            if (passengerScreenMode == P_INITIAL) {
                                if (likeClicked == 1) {
                                    textViewInitialSearch.setText(searchResult.getNameForText(this));
                                    textViewInitialSearchNew.setText(searchResult.getNameForText(this));
                                    tvPickupRentalOutstation.setText(searchResult.getNameForText(this));
                                    ivLikePickup.setImageResource(R.drawable.ic_heart_filled);
                                    ivLikePickup.setTag("liked");
                                } else if (likeClicked == 2) {
                                    textViewDestSearch.setText(searchResult.getNameForText(this));
                                    textViewDestSearchNew.setText(searchResult.getNameForText(this));
                                    tvPickupRentalOutstation.setText(searchResult.getNameForText(this));
                                    ivLikeDrop.setImageResource(R.drawable.ic_heart_filled);
                                    ivLikeDrop.setTag("liked");
                                }
                            }
                        }
                    }
                } else if (requestCode == FARE_ESTIMATE) {
                    try {


                        if(data.getBooleanExtra(KEY_SCHEDULE_RIDE,false)){
                            performBackpressed();
                            return;
                        }
                        if (data.hasExtra(KEY_SEARCH_RESULT)) {
                            SearchResult searchResult = new Gson().fromJson(data.getStringExtra(KEY_SEARCH_RESULT), SearchResult.class);
                            searchResult.setTime(System.currentTimeMillis());
                            if(Utils.compareDouble(searchResult.getLatitude(), 0) != 0
                                    && Utils.compareDouble(searchResult.getLongitude(), 0) != 0) {
                                setDropAddressAndExpandFields(searchResult);
                            } else {
                                Utils.showToast(this, getString(R.string.wrong_address_selected));
                            }
//                            saveLastDestinations(searchResult);
                        }
                        slidingBottomPanel.getImageViewExtraForSliding().performClick();
                        // check if we need to avoid the action click
                        if (data.getExtras() == null || !data.getExtras().containsKey(Constants.KEY_AVOID_RIDE_ACTION)) {
                            imageViewRideNow.performClick();
                        }
                        activityResumed = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Request Code for IntentItegrator -> 49374

                else if (requestCode == IntentIntegrator.REQUEST_CODE) {

                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                    rentalCaseResume = true;

                    if (result != null) {
                        if (result.getContents() != null) {
                            String bikeNumber = "";
                            qrCodeDetails = result.getContents();

                            bikeNumber = extractQRCode(result.getContents());
                            if (!bikeNumber.equals("error")) {
                                qrCode = bikeNumber;
                                Log.e(TAG,"bluetooth enabled"+ Data.autoData.getBluetoothEnabled());
                                if(Data.autoData.getBluetoothEnabled()==1){
                                    initiateBleProcess();
                                }else{
                                    requestRideClick();
                                }
                                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            } else {
                                Toast.makeText(this, getString(R.string.incorrect_qr_code), Toast.LENGTH_SHORT).show();
                            }
                        } else if (data != null) {
                            qrCode = data.getStringExtra("qrCode");
                            qrCodeDetails = data.getStringExtra("qr_code_details");
                            if(Data.autoData.getBluetoothEnabled()==1){
                                initiateBleProcess();
                            }else{
                                requestRideClick();
                            }
                            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }
                }
                else if(requestCode == REQUEST_CODE_PAY_VIA_UPI){
                	for(String key : data.getExtras().keySet()){
						Log.v("onActivityResult REQUEST_CODE_PAY_VIA_UPI", "key="+key+", value="+data.getExtras().get(key));
					}
					LogUpiResponse.INSTANCE.hitApi(this, Data.autoData.getcEngagementId(),
							Data.autoData.getEndRideData().getDriverUpiId(),
							data.getExtras(), logUpiCallback);

				}
                else {
                    Log.v("onActivityResult else part", "onActivityResult else part");
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                }
            } else if(resultCode == RESULT_PAUSE && requestCode == REQ_CODE_VIDEO && pushDialog != null) {
                pushDialog.onActivityResult();
            } else{
                if(requestCode==REQ_BLE_ENABLE){
                    Log.e(TAG,"bluetooth permission result failed");
                    Data.autoData.setBluetoothEnabled(0);
                    requestRideClick();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        likeClicked = 0;
    }

    private LogUpiResponse.LogUpiCallback logUpiCallback = new LogUpiResponse.LogUpiCallback(){
		@Override
		public void onApiSuccess(String engagementId) {
			getRideSummaryAPI(HomeActivity.this, engagementId, null);
		}
	};

    private void initiateBleProcess(){
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Failed to acquire bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "process initiated");
            smartLockObj.intializeBle(HomeActivity.this);
            Log.e(TAG, "device token default" + macId);
            Log.e(TAG, "qrcode default" + qrCode);
            Log.e(TAG, "device token api size : " + Data.autoData.getDriverInfos().size());
            boolean isDeviceFound = false;
            for (int i = 0; i < Data.autoData.getDriverInfos().size(); i++) {
                if (Data.autoData.getDriverInfos().get(i).getExternalId() != null && Data.autoData.getDriverInfos().get(i).getExternalId().equals(qrCode)) {
                    if (Data.autoData.getDriverInfos().get(i).getDeviceToken() != null) {
                        macId = Data.autoData.getDriverInfos().get(i).getDeviceToken();
                        isDeviceFound = true;
                        startPairing();
                        break;
                    }
                }
                Log.e(TAG, "device token" + macId);
            }
            if (isDeviceFound==false){
               // DialogPopup.alertPopup(HomeActivity.this, "", "You are not authorised to use this device");
                Log.e("ble data", "device not found in api data , switched to server flow ");
                Data.autoData.setBluetoothEnabled(0);
                requestRideClick();

            }
        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,188);
        }
    }

    private void startPairing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (HomeActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1008);
            } else {
                Log.e(TAG, "make pair called" + macId);
                smartLockObj.makePair(macId);
            }
        }
    }

    // Extracting Bike number from the scan
    public String extractQRCode(final String result) {

//        String bikeNumber;
//        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
//            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
//        } else if (result.length() == 11) {
//            // TODO apply the check that all 11 digits must be numbers
//            bikeNumber = result;
//        } else {
//            bikeNumber = "error";
//        }
//        return bikeNumber;

        String bikeNumber = "";
        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
        } else {
            bikeNumber = result;
        }
        return bikeNumber;
    }


    SmartLockController smartLockObj = new SmartLockController(new SmartlockCallbacks() {
        @Override
        public void makePair(boolean status) {
            Log.d(TAG,"bluetooth device connected"+status);
            if(status){
                requestRideClick();
            }

        }

        @Override
        public void updateStatus(int status) {
            //0 for lock device
            //1 for device unlocked
            Log.e(TAG, "bluetooth device unlocked" + status);
            if (status == 3) {
                dialogRentalLock(HomeActivity.this);
            } else {
                callUpdateServerApi(status);
            }
        }

        @Override
        public void checkForBluetoth(){
            Log.e(TAG,"bluetooth enable popup called");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQ_BLE_ENABLE);
        }

        @Override
        public void unableToPair(boolean status) {
          if(status==false){
              Log.e("ble data", "unable to pair with device going with server flow");
              Data.autoData.setBluetoothEnabled(0);
              requestRideClick();
          }
        }


    });

    private void callUpdateServerApi(int lockStatus) {
        HashMap<String, String> nameValuePairs = new HashMap<>();
        nameValuePairs.put("access_token", Data.userData.accessToken);
        nameValuePairs.put("engagement_id", "" + Data.autoData.getcEngagementId());
        nameValuePairs.put("lock_status", "" + lockStatus);
        nameValuePairs.put("latitude", "" + Data.autoData.getPickupLatLng().latitude);
        nameValuePairs.put("longitude", "" + Data.autoData.getPickupLatLng().longitude);

        new HomeUtil().putDefaultParams(nameValuePairs);
        Response responseRetro = RestClient.getApiService().updateLockStatus(nameValuePairs);
        String response = new String(((TypedByteArray) responseRetro.getBody()).getBytes());
        Log.e(TAG, "update to server result=" + response);
        try {
            JSONObject jObj = new JSONObject(response);
            int flag = jObj.getInt("gps_lock_status");
            Log.e("TAG","gps lock status "+flag);
            if(flag==GpsLockStatus.END_RIDE_LOCK.getOrdinal()){
                smartLockObj.disconnectDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//The three parameters are the request code with the same custom, the permission array, the authorization result, and the permission array
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG,"bluetooth permission result"+requestCode);
        if (requestCode == 1) {
            smartLockObj.makePair(macId);
        }
    }


    public static void checkForAccessTokenChange(Activity activity) {
        Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
        if (!"".equalsIgnoreCase(pair.first)) {
            if (Data.userData == null) {
                splashIntent(activity);
            } else {
                if (!pair.first.equalsIgnoreCase(Data.userData.accessToken)) {
                    splashIntent(activity);
                }
            }
        } else {
            if (Data.userData != null) {
                splashIntent(activity);
            }
        }
    }


    public static void splashIntent(Activity cont) {
        try {
            FacebookLoginHelper.logoutFacebook();
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
        try {
            if (userMode == UserMode.PASSENGER) {
                pauseAllTimers();
                saveDriverBearing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(pushDialog != null) {
            pushDialog.onPause();
        }
        super.onPause();
    }


    public void backFromSearchToInitial() {
        try {
            Utils.hideSoftKeyboard(this, textViewInitialSearch);
            Utils.hideSoftKeyboard(this, textViewInitialSearchNew);
            Utils.hideSoftKeyboard(this, tvPickupRentalOutstation);
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    passengerScreenMode = P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                }
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
                return;
            }
            if (fabViewTest.menuLabelsRightTest.isOpened()) {
                fabViewTest.menuLabelsRightTest.close(true, false);
                return;
            }
        } catch (Exception e) {
        }
        performBackpressed();
    }


    public void performBackpressed() {
        try {
            if (PassengerScreenMode.P_SEARCH == passengerScreenMode) {
                backFromSearchToInitial();
            } else if (dropLocationSearched && PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
                stopDropLocationSearchUI(false);
            } else if (dropLocationSearched &&
                    (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
                            PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
                            PassengerScreenMode.P_IN_RIDE == passengerScreenMode)) {
                stopDropLocationSearchUI(true);
            } else if (PassengerScreenMode.P_RIDE_END == passengerScreenMode
                    && linearLayoutRideSummaryContainer.getVisibility() == View.VISIBLE) {
                linearLayoutRideSummaryContainerSetVisiblity(View.GONE, RideEndFragmentMode.INVOICE);
            } else if (PassengerScreenMode.P_RIDE_END == passengerScreenMode
                    && relativeLayoutRideEndWithImage.getVisibility() == View.VISIBLE) {
                buttonEndRideSkip.performClick();
            } else {
                if (slidingBottomPanel.getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else if (confirmedScreenOpened) {
                    confirmedScreenOpened = false;
                    if (Data.autoData.getDropLatLng() != null) {
                        imageViewDropCross.setVisibility(View.VISIBLE);
                        imageViewDropCrossNew.setVisibility(View.VISIBLE);
                        setLikeDropVisibilityAndBG();
                    }
                    passengerScreenMode = P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                    if(Data.autoData != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLng(Data.autoData.getPickupLatLng()), MAP_ANIMATE_DURATION, null);
                    }
//                    navigateToCurrLoc(false);
                } else if (specialPickupScreenOpened) {
                    specialPickupScreenOpened = false;
                    passengerScreenMode = P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                    initialMyLocationBtn.performClick();
                }
                else if (scheduleRideOpen) {
                    scheduleRideContainer.setVisibility(View.GONE);
                    topBar.imageViewBack.setVisibility(View.GONE);
                    topBar.imageViewMenu.setVisibility(View.VISIBLE);
                    setScheduleIcon();
                    topBar.textViewTitle.setText(getResources().getString(R.string.rides));
                    getSupportFragmentManager().popBackStackImmediate();
//                    passengerScreenMode = PassengerScreenMode.P_INITIAL;
//                    switchPassengerScreen(passengerScreenMode);
//                    initialMyLocationBtn.performClick();
                }
                else {
                    MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
                    finishWithToast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ActivityCompat.finishAffinity(this);
        }
    }


    private int backPressedCount = 0;

    private void finishWithToast() {
        backPressedCount++;
        getHandler().removeCallbacks(runnableBackPressReset);
        getHandler().postDelayed(runnableBackPressReset, 2000);
        if (backPressedCount == 2) {
            ActivityCompat.finishAffinity(HomeActivity.this);
            Utils.cancelToast();
        } else {
            Utils.showToast(this, getString(R.string.press_back_again_to_quit));
        }
    }

    private Runnable runnableBackPressReset = new Runnable() {
        @Override
        public void run() {
            backPressedCount = 0;
        }
    };


    @Override
    public void onDestroy() {
        try {
            getHandler().removeCallbacks(scheduleRideRunnable);
            startService(new Intent(this, DeleteCacheIntentService.class));

            GCMIntentService.clearNotifications(HomeActivity.this);

            ASSL.closeActivity(drawerLayout);
            cancelTimerUpdateDrivers();

            appInterruptHandler = null;

            FacebookLoginHelper.logoutFacebook();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(pushBroadcastReceiver);
            System.gc();
            searchLocationDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void intentToShareActivity(boolean fromDeepLink) {
        Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
        intent.putExtra(KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, fromDeepLink);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        relativeLayoutRideEndWithImage.setVisibility(View.GONE);

        try {
            if (fromDeepLink) {
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiFindADriver getApiFindADriver() {
        if (apiFindADriver == null) {
            apiFindADriver = new ApiFindADriver(this, slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected(),
                    new ApiFindADriver.Callback() {
                        @Override
                        public void onPre() {
                            specialPickupScreenOpened = false;
                            selectedSpecialPickup = "";
                            rlSpecialPickup.setVisibility(View.GONE);
                            updateTopBar();
                            progressBarInitialSearch.setVisibility(View.VISIBLE);
                            progressBarInitialSearch.spin();
                            imageViewRideNow.setEnabled(false);
                            removeSpecialPickupMarkers();
                            try {
                                if (userMode == UserMode.PASSENGER) {
                                    dontCallRefreshDriver = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCompleteFindADriver() {
                            findADriverFinishing(true, false);
                            addUserCurrentLocationAddressMarker();
                            if(mLogMsg != null && mRequestType != null) {
                                noDriverAvailablePopup(HomeActivity.this, false, mLogMsg, mRequestType);
                                mLogMsg = null;
                            }
                            try {

                                if(getScheduleRideFragment()!=null){
                                    ServiceType serviceTypeSel = Data.autoData.getServiceTypeSelected();
                                    if(Data.autoData != null && !Data.autoData.getIsServiceAvailable()){
                                        DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.this_service_not_available, Data.autoData.getPreviousSelService()));
                                        isScheduleRideEnabled = false;
                                        performBackpressed();
                                    }else {
                                        getScheduleRideFragment().updateVehicleAdapter();
                                        if(Data.autoData != null && serviceTypeSel != null) {
                                            isScheduleRideEnabled = serviceTypeSel.getScheduleAvailable() == 1;
                                        }
                                        if (isScheduleRideEnabled && !TextUtils.isEmpty(Data.autoData.getFarAwayCity())) {
                                            DialogPopup.alertPopup(HomeActivity.this, "", Data.autoData.getFarAwayCity());
                                            performBackpressed();
                                        }
                                    }
                                }

                                if (P_INITIAL == passengerScreenMode) {
                                    pokestopHelper.checkPokestopData(map.getCameraPosition().target, Data.userData.getCurrentCity());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure() {
                            try {
                                if (Data.autoData.getDriverInfos().size() == 0) {
                                    textViewCentrePinETA.setText("-");
                                    noDriverNearbyToast(getResources().getString(R.string.couldnt_find_drivers_nearby));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void continueRequestRide(boolean confirmedScreenOpened, boolean savedAddressUsed) {
                            if (savedAddressUsed) {
                                requestRideDriverCheck();
                            } else if (confirmedScreenOpened) {
                                if (getSlidingBottomPanel().getRequestRideOptionsFragment()
                                        .getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                                    openBikeRentalScan();
                                } else {
                                    requestRideClick();
                                }
                            } else {
                                imageViewRideNowPoolCheck();
                            }
                        }

                        @Override
                        public void stopRequestRide(boolean confirmedScreenOpened) {
                            Utils.showToast(HomeActivity.this, getResources().getString(R.string.fares_updated));
                            if (!confirmedScreenOpened) {
                                Animation anim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.rotate_shake);
                                imageViewRideNow.startAnimation(anim);
                            }
                            if (confirmedScreenOpened
                                    && (isPoolRideAtConfirmation() || isNormalRideWithDropAtConfirmation())) {
                                fareEstimatBeforeRequestRide();
                            }
                        }

                        @Override
                        public void onFinish() {
                            progressBarInitialSearch.stopSpinning();
                            progressBarInitialSearch.setVisibility(View.GONE);
                            imageViewRideNow.setEnabled(true);
                            slidingBottomPanel.updatePaymentOptions();
                        }

                        @Override
                        public void updateWalletConfig() {
                            if (Data.userData != null) {
                                slidingBottomPanel.updatePaymentOptions();
                            }
                        }

                        @Override
                        public void updateSideMenu(ArrayList<MenuInfo> menuInfos) {
                            if (Data.userData != null && menuInfos != null && menuInfos.size() > 0) {
                                /*
                                 * hack for preserving jugnoo star MenuInfo if it was there in Login api
                                 * For Parminder
                                 */
                                MenuInfo menuInfoStar = null;
                                int index = Data.userData.getMenuInfoList().indexOf(new MenuInfo(MenuInfoTags.JUGNOO_STAR.getTag()));
                                if (index > -1) {
                                    menuInfoStar = Data.userData.getMenuInfoList().get(index);
                                }
                                Data.userData.setMenuInfoList(menuInfos);
                                if (Data.userData.getMenuInfoList().indexOf(new MenuInfo(MenuInfoTags.JUGNOO_STAR.getTag())) < 0
                                        && menuInfoStar != null && Data.userData.getMenuInfoList().size() >= index) {
                                    Data.userData.getMenuInfoList().add(index, menuInfoStar);
                                }

                                menuBar.setUserData();
                            }
                        }
                    });
        }
        return apiFindADriver;
    }

    private ApiFindADriver apiFindADriver = null;

    private void findDriversETACall(boolean beforeRequestRide, boolean confirmedScreenOpened, boolean savedAddressUsed, HashMap<String, String> params) {
        boolean showLoader = mLogMsg != null && mRequestType != null;
        getApiFindADriver()
				.hit(Data.userData.accessToken, Data.autoData.getPickupLatLng(), Data.autoData.getDropLatLng(), showAllDrivers, showDriverInfo,
                slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected(), beforeRequestRide,
                confirmedScreenOpened, savedAddressUsed, params, showLoader);
    }

    private void findADriverFinishing(boolean showPoolIntro, boolean useServerDefaultCoupon) {
        if (P_INITIAL == passengerScreenMode) {
			ArrayList<Region> regions = Data.autoData.getRegions();
            if (regions != null && regions.size() > 0) {
                boolean setNew = Data.autoData.getNewUIFlag();
                if (setNew) {
                    if (!isNewUI) {
                        isNewUI = true;
                        passengerScreenMode = P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    }
                } else {
                    if (isNewUI) {
                        isNewUI = false;
                        passengerScreenMode = P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    }
                }



                if(isNewUI) {

					textViewRupee.setText(Utils.getCurrencySymbol(Data.autoData.getCurrency()));

					if(Data.userData != null && Data.userData.getReferralMessages().getMultiLevelReferralEnabled()){
						tvFreeRidesForLife.setVisibility(View.VISIBLE);
					}

                    relativeLayoutTotalFare.setVisibility(View.GONE);
                    linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);
                    relativeLayoutSearchContainer.setVisibility(View.GONE);
                    tvTermsAndConditions.setVisibility(View.GONE);
                    imageViewRideNow.setVisibility(View.GONE);
                    linearLayoutConfirmOption.setBackground(ContextCompat.getDrawable(this,R.color.white));
                    if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getReverseBid() == 1) {

						//for setting region fare in etBid again
						regionIdFareSetInETBid = 0;

						showReverseBidField(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());

                        relativeLayoutOfferConfirm.setVisibility(View.GONE);
                        boolean isCashOnly = true;
                        if (MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas().size() > 0) {
                            for (PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas()) {
                                if (paymentModeConfigData.getPaymentOption() != PaymentOption.CASH.getOrdinal()
                                        && paymentModeConfigData.getEnabled() == 1
                                        && !slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRestrictedPaymentModes().contains(paymentModeConfigData.getPaymentOption())
                                        && paymentModeConfigData.getPaymentOption() != 0) {
                                    isCashOnly = false;
                                }
                            }
                        }
                        if (isCashOnly) {
                            linearLayoutPaymentModeConfirm.setVisibility(View.GONE);
                        }
                    } else {
                        linearLayoutBidValue.setVisibility(View.GONE);
                        relativeLayoutOfferConfirm.setVisibility(View.VISIBLE);
                        linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);
                        if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionFare() != null
                                && slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getFareMandatory() == 1) {
//                            tvTermsAndConditions.setVisibility(View.VISIBLE);
                        }
                    }
                } else {

					tvFreeRidesForLife.setVisibility(View.GONE);

                    if(!confirmedScreenOpened){
                        imageViewRideNow.setVisibility(View.VISIBLE);
                    }
                    relativeLayoutSearchContainer.setVisibility(View.VISIBLE);

                    linearLayoutConfirmOption.setBackground(getResources().getDrawable(R.color.menu_item_selector_color_F7));
                    linearLayoutBidValue.setVisibility(View.GONE);
                    linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);
                    if(!Data.autoData.showRegionSpecificFare()) {
                        relativeLayoutTotalFare.setVisibility(View.VISIBLE);
                    }
                    relativeLayoutOfferConfirm.setVisibility(View.VISIBLE);
                }
            }
            try {
//                if (!stopDefaultCoupon && (slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon().getId() > 0 || promoSelectionLastOperation)) {
//                    defaultCouponSelection();
//                }
                if(vehiclesTabAdapterConfirmRide!=null){
                    vehiclesTabAdapterConfirmRide.setList(regions);
                }
                slidingBottomPanel.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (zoomAfterFindADriver) {
                    zoomAfterFindADriver = false;
                    zoomToCurrentLocationWithOneDriver(getDeviceLocation());
                }
                if (relativeLayoutLocationError.getVisibility() == View.GONE) {
                    setServiceTypeAdapter(true);
                    setServiceTypeUI();
                    showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());
                    homeUtil.displayPointOfInterestMarkers(HomeActivity.this, assl, map, passengerScreenMode);
                    dontCallRefreshDriver = false;

                    updateImageViewRideNowIcon();
                    setupInAppCampaignUI();
                    Log.e(TAG, "findADriverFinishing");
                    fabViewTest = new FABViewTest(this, fabViewIntial);
                    setJeanieVisibility();
                    showPoolInforBar(false);
                    if (showPoolIntro) {
//                        showPoolIntroDialog();
                    }
                    updateSpecialPickupScreen();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private LatLng getDeviceLocation(){
//        if(Data.autoData != null && Data.autoData.getPickupLatLng() != null
//                && Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 0){
//            return Data.autoData.getPickupLatLng();
//        } else {
        if(myLocation != null) {
            return new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        } else {
            return new LatLng(Data.latitude,Data.longitude);
        }
//        }
    }

    public static boolean checkCouponDropValidity(PromoCoupon lasSelectedCustomCoupon) {

    	if(Data.autoData == null){
    		return false;
		}

        if(Data.autoData.getDropLatLng() != null
				&& lasSelectedCustomCoupon != null
                && (lasSelectedCustomCoupon.getType() == CouponType.DROP_BASED.getType()
				|| lasSelectedCustomCoupon.getType() == CouponType.PICKUP_DROP_BASED.getType())
                && lasSelectedCustomCoupon.getDropRadius() > 0){
            boolean matched = false;
            for(LatLngCoordinates llc : lasSelectedCustomCoupon.getDropLocationCoordinates()){
                if(MapUtils.distance(llc.getLatLng(), Data.autoData.getDropLatLng()) <= lasSelectedCustomCoupon.getDropRadius()){
                    matched = true;
                    break;
                }
            }
            if(!matched){
                lasSelectedCustomCoupon = null;
            }
        }
        boolean lastSelectedCouponExits = false;
        if (lasSelectedCustomCoupon != null) {
            for (PromoCoupon promoCoupon : Data.autoData.getPromoCoupons()) {
                if (promoCoupon.getId() == lasSelectedCustomCoupon.getId()) {
                    lastSelectedCouponExits = true;
                }

            }
        }

		//if false: 	default selection needed, hence coupon is invalid
		//else if true: coupon is valid

        return lastSelectedCouponExits;

    }

    private boolean updateSpecialPickupScreen() {
        specialPickups.clear();
        if (Data.autoData.getNearbyPickupRegionses() != null
                && Data.autoData.getNearbyPickupRegionses().getHoverInfo() != null
                && Data.autoData.getNearbyPickupRegionses().getHoverInfo().size() > 0) {
            specialPickups.addAll(Data.autoData.getNearbyPickupRegionses().getHoverInfo());
            return true;
        } else {
            specialPickupScreenOpened = false;
            selectedSpecialPickup = "";
            rlSpecialPickup.setVisibility(View.GONE);
            updateTopBar();
            return false;
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (specialPickups.get(i).getText().equals(myString)) {
                index = i;
                LatLng specialPicupLatLng = new LatLng(Double.parseDouble(Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(index).getLatitude()),
                        Double.parseDouble(Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(index).getLongitude()));
                Data.autoData.getPickupSearchResult().setLatitude(specialPicupLatLng.latitude);
                Data.autoData.getPickupSearchResult().setLongitude(specialPicupLatLng.longitude);
                Log.w("pickuplogging", "getIndex"+Data.autoData.getPickupLatLng());
                addUserCurrentLocationAddressMarker();
                specialPickupSelected = true;
                selectedSpecialPickup = Data.autoData.getNearbyPickupRegionses().getHoverInfo().get(index).getText() + ", ";
                textViewInitialSearchNew.setText(selectedSpecialPickup + Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
                tvPickupRentalOutstation.setText(selectedSpecialPickup + Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
                break;
            }
        }
        return index;
    }


    private void setJeanieVisibility() {
        try {
            if (Data.userData.getIntegratedJugnooEnabled() == 1) {
                if ((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0) &&
                        (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0)
                        && (Data.userData.getMenusEnabled() == 0) && (Data.userData.getPayEnabled() == 0)
                        && (Data.userData.getDeliveryCustomerEnabled() == 0)
                        && (Data.userData.getFeedEnabled() == 0)
                        && Data.userData.getProsEnabled() == 0) {
                    //imageViewFabFake.setVisibility(View.GONE);
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
                } else {
                    //imageViewFabFake.setVisibility(View.VISIBLE); // fab existing
                    if ((passengerScreenMode == P_INITIAL && !confirmedScreenOpened)
                            || ( relativeLayoutFinalDropLocationParent.getVisibility() == View.GONE)) {
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                        fabViewTest.setFABButtons(false);
                    }
                    if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL
							|| passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED
							|| passengerScreenMode == PassengerScreenMode.P_IN_RIDE) {
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
                    }
                }
            } else {
                fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }



    private void setupInAppCampaignUI() {
        try {
            if (Data.autoData.getCampaigns() != null && Data.autoData.getCampaigns().getMapLeftButton() != null) {
                imageViewInAppCampaign.clearAnimation();
                imageViewInAppCampaign.setVisibility(View.VISIBLE);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new FrameAnimDrawable(HomeActivity.this, (ArrayList<String>) Data.autoData.getCampaigns().getMapLeftButton().getImages(),
                                    imageViewInAppCampaign);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            } else {
                imageViewInAppCampaign.clearAnimation();
                imageViewInAppCampaign.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PartnerWithJugnooDialog partnerWithJugnooDialog = null;
    private PartnerWithJugnooDialog.Callback callbackPartner = null;
    private int partnerDialogCount = 0;

    //Our service is not available in this area
    public void setServiceAvailablityUI(String farAwayCity) {
        if (P_INITIAL == passengerScreenMode
                && relativeLayoutLocationError.getVisibility() == View.GONE) {
            if (!"".equalsIgnoreCase(farAwayCity)) {
                slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                changeLocalityLayout.setVisibility(View.VISIBLE);
				relativeLayoutConfirmRequest.setVisibility(View.GONE);
                textViewChangeLocality.setText(farAwayCity);
                relativeLayoutInAppCampaignRequest.setVisibility(View.GONE);
                textViewCentrePinETA.setText("-");
                imageViewRideNow.setVisibility(View.GONE);
                textViewShowFareEstimate.setVisibility(View.GONE);
                initialMyLocationBtn.setVisibility(View.VISIBLE);
                constraintLayoutRideTypeConfirm.setVisibility(View.GONE);
                topBar.imageViewScheduleRide.setVisibility(View.GONE);

                if(partnerDialogCount < Prefs.with(this).getInt(KEY_CUSTOMER_PARTNER_DIALOG_VIEW_COUNT, 5)
                        && Prefs.with(this).getInt(KEY_CUSTOMER_OPEN_PARTNER_DIALOG, 0) == 1) {
                    if (partnerWithJugnooDialog == null) {
                        partnerWithJugnooDialog = new PartnerWithJugnooDialog();
                        callbackPartner = () -> {
                            partnerDialogCount++;
                        };
                    }
                    partnerWithJugnooDialog.show(this, Prefs.with(this).getString(KEY_CUSTOMER_PARTNER_DIALOG_TITLE, getString(R.string.customer_partner_dialog_title)),
                            Prefs.with(this).getString(KEY_CUSTOMER_PARTNER_DIALOG_MESSAGE, getString(R.string.customer_partner_dialog_message)), callbackPartner);
                }
            } else {
                if(!confirmedScreenOpened && !isNewUI()) {
                    imageViewRideNow.setVisibility(View.VISIBLE);
                }
                checkForMyLocationButtonVisibility();
                changeLocalityLayout.setVisibility(View.GONE);
                if(passengerScreenMode == P_INITIAL && (confirmedScreenOpened || isNewUI())) {
					relativeLayoutConfirmRequest.setVisibility(View.VISIBLE);
				}
                if(partnerWithJugnooDialog != null) {
                    partnerWithJugnooDialog.dismiss();
                }
            }
            setJeanieVisibility();
            showPokestopOnOffButton(passengerScreenMode);
        }
    }

    private boolean showSaveLocationDialog(){
		return Prefs.with(this).getInt(KEY_CUSTOMER_SHOW_SAVE_LOCATION_DIALOG, 0) == 1;
	}

    public MarkerOptions getStartPickupLocMarkerOptions(LatLng latLng, boolean inRide) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("pickup location");
        markerOptions.snippet("");
        markerOptions.position(latLng);
        markerOptions.zIndex(HOME_MARKER_ZINDEX);
        if (inRide) {
            if((Data.autoData.getAssignedDriverInfo() != null && Data.autoData.getAssignedDriverInfo().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal())
					|| Prefs.with(HomeActivity.this).getBoolean(Constants.SKIP_SAVE_PICKUP_LOCATION, false)
                    || !showSaveLocationDialog()
                    || HomeUtil.getNearBySavedAddress(this, Data.autoData.getPickupLatLng(), false) != null) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromBitmap(CustomMapMarkerCreator
                                .createPinMarkerBitmapStart(HomeActivity.this)));
            } else {
                    markerOptions.icon(BitmapDescriptorFactory
                            .fromBitmap(CustomMapMarkerCreator
                                    .createCustomMarkerWithSavedLocation(HomeActivity.this, true)));
            }
        } else {
            if (Data.autoData.getDropLatLng() != null) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromBitmap(CustomMapMarkerCreator
                                .getTextAssignBitmap(HomeActivity.this, Data.autoData.getAssignedDriverInfo().getEta(),
                                        getResources().getDimensionPixelSize(R.dimen.text_size_24))));
            } else {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromBitmap(CustomMapMarkerCreator
                                .getTextBitmap(HomeActivity.this, Data.autoData.getAssignedDriverInfo().getEta(),
                                        getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size))));
            }
        }
        return markerOptions;
    }

    public Marker getAssignedDriverCarMarkerOptions(DriverInfo driverInfo) {
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title("driver position");
        markerOptions1.snippet("");
        markerOptions1.position(driverInfo.latLng);
        markerOptions1.anchor(0.5f, 0.5f);
        markerOptions1.zIndex(HOME_MARKER_ZINDEX);
        return driverInfo.addMarkerToMap(driverInfo.getMarkerUrl(), this, map, markerOptions1);
    }

    private final float HOME_MARKER_ZINDEX = 2.0f;

    public Marker addDriverMarkerForCustomer(DriverInfo driverInfo, String markerUrl) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("driver shown to customer");
        markerOptions.snippet("" + driverInfo.userId);
        markerOptions.position(driverInfo.latLng);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.zIndex(HOME_MARKER_ZINDEX);
        markerOptions.rotation((float) driverInfo.getBearing());
        return driverInfo.addMarkerToMap(markerUrl, this, map, markerOptions);
    }


    public void addUserCurrentLocationAddressMarker() {
        try {
            if((passengerScreenMode == P_INITIAL
                    && !confirmedScreenOpened
                    && Prefs.with(this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 0) || isNewUI) {
                LatLng latLng = Data.autoData.getPickupLatLng();
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                        .getTextBitmap(HomeActivity.this,
                                slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getEta(),
                                getResources().getDimensionPixelSize(R.dimen.marker_eta_text_size)));
                if (currentLocationMarker != null) {
                    currentLocationMarker.setPosition(latLng);
                    currentLocationMarker.setIcon(descriptor);
                } else if(!isNewUI()) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title("customer_current_location");
                    markerOptions.snippet("");
                    markerOptions.position(latLng);
                    markerOptions.icon(descriptor);
                    markerOptions.zIndex(1);
                    currentLocationMarker = map.addMarker(markerOptions);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<Marker> markersDriversFindADriver = new ArrayList<>();

    private void clearMarkersDriversFindADriver() {
        for (Marker marker : markersDriversFindADriver) {
            marker.remove();
        }
        markersDriversFindADriver.clear();
    }

    public void showDriverMarkersAndPanMap(final LatLng userLatLng, Region region) {
        try {
            if ("".equalsIgnoreCase(Data.autoData.getFarAwayCity())) {
                if (userMode == UserMode.PASSENGER &&
                        ((P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)
                                || PassengerScreenMode.P_ASSIGNING == passengerScreenMode)) {
                    if (map != null) {
                        setDropLocationMarker();
                        clearMarkersDriversFindADriver();
                        int regionDriversCount = 0;
                        for (int i = 0; i < Data.autoData.getDriverInfos().size(); i++) {
                            DriverInfo driver = Data.autoData.getDriverInfos().get(i);
                            if (driver.getOperatorId() == region.getOperatorId() && driver.getRegionIds().contains(region.getRegionId()) && (driver.getPaymentMethod() == DriverInfo.PaymentMethod.BOTH.getOrdinal() || driver.getPaymentMethod() == 0
                                    || Data.autoData.getPickupPaymentOption() == PaymentOption.CASH.getOrdinal())
                                    ) {
                                driver.setVehicleIconSet(region.getVehicleIconSet().getName());
                                markersDriversFindADriver.add(addDriverMarkerForCustomer(Data.autoData.getDriverInfos().get(i), region.getImages().getMarkerIcon()));
                                regionDriversCount++;
                            }
                        }
                        if (!mapTouchedOnce) {
                            zoomToCurrentLocationWithOneDriver(userLatLng);
                            mapTouchedOnce = true;
                        }

                        if (regionDriversCount == 0) {
                            textViewCentrePinETA.setText("-");
                        } else {
                            textViewCentrePinETA.setText(region.getEta());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomtoPickupAndDriverLatLngBounds(final LatLng driverLatLng, List<LatLng> latLngsToInclude, int duration) {
        try {
            if ((PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)
                    && Data.autoData.getPickupLatLng() != null && driverLatLng != null) {

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                double distance = 0;
				if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
						|| PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
					boundsBuilder.include(Data.autoData.getPickupLatLng());
					distance = MapUtils.distance(Data.autoData.getPickupLatLng(), driverLatLng);
				}
				else if (Data.autoData.getDropLatLng() != null && PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
					boundsBuilder.include(Data.autoData.getDropLatLng());
					distance = MapUtils.distance(Data.autoData.getDropLatLng(), driverLatLng);
				}
                if (distance <= 100000) {
                    boundsBuilder.include(driverLatLng);
                    if (latLngsToInclude != null) {
                        for (LatLng latLng : latLngsToInclude) {
                            boundsBuilder.include(latLng);
                        }
                    }
                    final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder, FIX_ZOOM_DIAGONAL);
                    if (duration == 0) {
                        duration = getMapAnimateDuration();
                    }
                    final int finalDuration = duration;
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if ((PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                                        || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                                        || PassengerScreenMode.P_IN_RIDE == passengerScreenMode)
                                        && bounds != null && map != null) {
                                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (MAP_PADDING * ASSL.minRatio())), finalDuration, null);
                                    customerInRideMyLocationBtn.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zoomToCurrentLocationWithOneDriver(final LatLng userLatLng) {

        try {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLng firstLatLng = null;
            Region region = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
            DriverInfo firstDriverInfo = null;
            for (DriverInfo driverInfo : Data.autoData.getDriverInfos()) {
                if (driverInfo.getOperatorId() == region.getOperatorId()
                        && driverInfo.getRegionIds().contains(region.getRegionId())
                        ) {
                    firstDriverInfo = driverInfo;
                    break;
                }
            }
            if ((P_INITIAL == passengerScreenMode || PassengerScreenMode.P_ASSIGNING == passengerScreenMode)
                    && firstDriverInfo != null) {
                firstLatLng = firstDriverInfo.latLng;
            }
            Runnable runnableZoom = new Runnable() {
                @Override
                public void run() {
                    try {
                        if ("".equalsIgnoreCase(Data.autoData.getFarAwayCity()) && !isSpecialPickupScreenOpened() && !isPoolRideAtConfirmation()) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM), getMapAnimateDuration(), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

			boolean fixedZoom = false;
            if (firstLatLng != null && !isSpecialPickupScreenOpened()) {
                double distance = MapUtils.distance(userLatLng, firstLatLng);
                if (distance <= 15000) {
                    boundsBuilder.include(new LatLng(userLatLng.latitude, firstLatLng.longitude));
                    boundsBuilder.include(new LatLng(firstLatLng.latitude, userLatLng.longitude));
                    boundsBuilder.include(new LatLng(userLatLng.latitude, ((2 * userLatLng.longitude) - firstLatLng.longitude)));
                    boundsBuilder.include(new LatLng(((2 * userLatLng.latitude) - firstLatLng.latitude), userLatLng.longitude));
                } else {
                    fixedZoom = true;
                }
            }
			final boolean finalFixedZoom = fixedZoom;
			boundsBuilder.include(userLatLng);
			if((isNewUI() || confirmedScreenOpened) && Data.autoData != null && Data.autoData.getDropLatLng() != null){
				boundsBuilder.include(Data.autoData.getDropLatLng());
			}

			try {
				final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(boundsBuilder, region.getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal() ? 10 : FIX_ZOOM_DIAGONAL);
				final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());

				runnableZoom = new Runnable() {
					@Override
					public void run() {
						try {
							if (finalFixedZoom) {
								map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLatLng.latitude, userLatLng.longitude), MAX_ZOOM), getMapAnimateDuration(), null);
							} else {
								map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (MAP_PADDING * minScaleRatio)), getMapAnimateDuration(), null);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			} catch (Exception e) {
				e.printStackTrace();
			}

            getHandler().postDelayed(runnableZoom, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSavedAddressLikeButton(LatLng currentLatLng, boolean isPickup){
        SearchResult searchResult = HomeUtil.getNearBySavedAddress(HomeActivity.this, currentLatLng,
				false);
        ImageView iv = isPickup? ivLikePickup : ivLikeDrop;
        if (searchResult != null) {
            iv.setImageResource(R.drawable.ic_heart_filled);
            iv.setTag("liked");
        } else {
            iv.setImageResource(R.drawable.ic_heart);
            iv.setTag("");
        }
    }

    private void getAddressAsync(final LatLng currentLatLng, final TextView textView, final ProgressWheel progressBar,
								 PlaceSearchListFragment.PlaceSearchMode placeSearchMode) {

        try {
            boolean addressNeeded = !isNewUI || !(Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1);

            SearchResult searchResult = HomeUtil.getNearBySavedAddress(HomeActivity.this, currentLatLng,
					true);
            if (passengerScreenMode == P_INITIAL) {
                if (searchResult != null) {
                    addressNeeded = false;
                    textView.setText(searchResult.getNameForText(this));
                    if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                        tvPickupRentalOutstation.setText(searchResult.getNameForText(this));
                        textViewInitialSearchNew.setText(searchResult.getNameForText(this));
						Data.autoData.setPickupSearchResult(searchResult);
                    }
                    if(textView == textViewInitialSearch){
                        ivLikePickup.setImageResource(R.drawable.ic_heart_filled);
                        ivLikePickup.setTag("liked");
                    }
                } else {
                	String address = Data.autoData.getPickupAddress(currentLatLng);
                    if(TextUtils.isEmpty(address)) {
                        if (textView == textViewInitialSearch) {
                            ivLikePickup.setImageResource(R.drawable.ic_heart);
                            ivLikePickup.setTag("");
                        }
                        Data.autoData.setPickupSearchResult(isNewUI && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1 ? "Current Location" : "", currentLatLng);
						if(Prefs.with(this).getInt(KEY_CUSTOMER_HIT_GEOCODE_FREE_ROAM, 1) == 0){
							addressNeeded = false;
							textView.setHint(R.string.enter_pickup);
                            textView.setText(Data.autoData.getPickupSearchResult().getAddress());
                            if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                                tvPickupRentalOutstation.setHint(R.string.enter_pickup);
                                tvPickupRentalOutstation.setText("");
                                textViewInitialSearchNew.setHint(R.string.enter_pickup);
                                textViewInitialSearchNew.setText("");
                            }
						} else {
                            textView.setText(Data.autoData.getPickupSearchResult().getAddress());
                            textView.setHint(R.string.enter_pickup);
                        }
                    } else {
                        addressNeeded = false;
                        textView.setText(address);
                        if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                            tvPickupRentalOutstation.setText(address);
                            textViewInitialSearchNew.setText(address);
                        }
                    }
                }
            } else if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                    || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
                if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP
						&& searchResult != null
						&& (Data.autoData.getDropLatLng() == null || TextUtils.isEmpty(Data.autoData.getDropAddress()))){
                    Data.autoData.setDropLatLng(searchResult.getLatLng());
                    Data.autoData.setDropAddressId(searchResult.getId());
                    Data.autoData.setDropAddress(searchResult.getAddress());
                } else if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP
						&& searchResult != null
						&& TextUtils.isEmpty(Data.autoData.getPickupAddress(searchResult.getLatLng()))){
                    Data.autoData.setPickupSearchResult(searchResult);
                }
                if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP
						&& Data.autoData.getDropLatLng() != null && !TextUtils.isEmpty(Data.autoData.getDropAddress())) {
                	if(searchResult != null){
                		dropAddressName = searchResult.getNameForText(this);
					}
                    if (dropAddressName.length() == 0) {
                        textView.setText(Data.autoData.getDropAddress());
                    } else {
                        textView.setText(dropAddressName);
                    }
                    addressNeeded = false;
                } else if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP
						&& !TextUtils.isEmpty(Data.autoData.getPickupAddress(currentLatLng))){
                	if(searchResult != null){
						textView.setText(searchResult.getNameForText(this));
					} else {
						textView.setText(Data.autoData.getPickupAddress(currentLatLng));
					}
					addressNeeded = false;
				}
            }

            if (addressNeeded) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                	DialogPopup.showLoadingDialog(this, getString(R.string.loading));
				}
                textView.setText("");
                textView.setHint(R.string.getting_address);

				GoogleJungleCaching.INSTANCE.hitGeocode(currentLatLng, (googleGeocodeResponse, singleAddress) -> {
					try {
						String address = null;
						if(googleGeocodeResponse != null){
							GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse);
							address = gapiAddress.getSearchableAddress();
						} else if(singleAddress != null){
							address = singleAddress;
						}
						textView.setText(address);
						if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
							textView.setHint(getResources().getString(R.string.enter_pickup));
                            tvPickupRentalOutstation.setHint(getResources().getString(R.string.enter_pickup));
                            tvPickupRentalOutstation.setText(address);
                            textViewInitialSearchNew.setHint(getResources().getString(R.string.enter_pickup));
                            textViewInitialSearchNew.setText(address);
							Data.autoData.setPickupSearchResult(address, currentLatLng);
						} else if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP){
							textView.setHint(getResources().getString(R.string.enter_destination));
							Data.autoData.setDropAddress(address);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.GONE);
					}
					DialogPopup.dismissLoadingDialog();
				});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ASync for cancelCustomerRequestAsync from server
     */
    public void cancelCustomerRequestAsync(final Activity activity) {
		ApiCancelRequest.INSTANCE.cancelCustomerRequestAsync(activity, new ApiCancelRequest.Callback() {
			@Override
			public void onSuccess() {
				customerUIBackToInitialAfterCancel();
			}

			@Override
			public void onFailure() {
				callAndHandleStateRestoreAPI(true);
			}

			@Override
			public boolean canDismissDialog() {
				return true;
			}
		});
		if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected() != null) {
			GAUtils.event(RIDES, HOME,
					slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionName() + " " + REQUEST + CANCELLED);
		}
	}
    public void cancelAndReBid() {
		ApiCancelRequest.INSTANCE.cancelCustomerRequestAsync(this, new ApiCancelRequest.Callback() {
			@Override
			public void onSuccess() {
				cancelTimerRequestRide();
				getHandler().postDelayed(runnableRaiseBid, 5000);
			}

			@Override
			public void onFailure() {
				callAndHandleStateRestoreAPI(true);
			}

			@Override
			public boolean canDismissDialog() {
				return false;
			}
		});
	}
	private Runnable runnableRaiseBid = new Runnable() {
		@Override
		public void run() {
			DialogPopup.dismissLoadingDialog();
			editTextBidValue.setText(Utils.getMoneyDecimalFormat().format(Data.autoData.getChangedBidValue()));
			editTextBidValue.setSelection(editTextBidValue.getText().length());

			finalRequestRideTimerStart(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());
		}
	};


    public void customerUIBackToInitialAfterCancel() {
        firstTimeZoom = false;


        cancelTimerRequestRide();
        slidingBottomPanel.getRequestRideOptionsFragment().setRegionSelected(null);

		if (map != null && pickupLocationMarker != null) {
			pickupLocationMarker.remove();
		}

        passengerScreenMode = P_INITIAL;
        switchPassengerScreen(passengerScreenMode);


        dontCallRefreshDriver = false;
		if(!confirmedScreenOpened && !isNewUI()) {
			callMapTouchedRefreshDrivers(null);
		}
		setTextToPickupDropTVs();
        setTipAmountToZero(true);

	}

	public void setTextToPickupDropTVs() {
		if(isNewUI()){
			if(Data.autoData.getPickupLatLng() != null && !TextUtils.isEmpty(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()))){
				SearchResult sr = HomeUtil.getNearBySavedAddress(this, Data.autoData.getPickupLatLng(), true);
				if(sr == null) {
					textViewInitialSearchNew.setText(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
				} else {
					textViewInitialSearchNew.setText(sr.getNameForText(this));
				}
			}
			if(Data.autoData.getDropLatLng() != null && !TextUtils.isEmpty(Data.autoData.getDropAddress())){
				SearchResult sr = HomeUtil.getNearBySavedAddress(this, Data.autoData.getDropLatLng(), true);
				if(sr == null) {
					textViewDestSearchNew.setText(Data.autoData.getDropAddress());
				} else {
					textViewDestSearchNew.setText(sr.getNameForText(this));
				}
			}
		}
	}


	public void initializeStartRideVariables() {

        HomeActivity.previousWaitTime = 0;
        HomeActivity.previousRideTime = 0;

        clearRideSPData();
    }


    void noDriverAvailablePopup(final Activity activity, boolean zeroDriversNearby, String message, int requestType) {
        try {
            setTipAmountToZero(false);
            ArrayList<FindADriverResponse.RequestLevels> requestLevels = Data.autoData.getRequestLevels();

            int nextLevelIndex = getNextRequestLevel(requestType, requestLevels);

            // set requestType that is required for request Ride Api hit
            mRequestType = nextLevelIndex != -1 ? requestLevels.get(nextLevelIndex).getLevel() : 0;
            mRequestLevelndex = nextLevelIndex;

            if(mRequestType == 1) {
                openDriverNotFoundTipDialog();
            } else if(mRequestType == 2) {
                openDriverContactListDialog();
            } else {
                if (noDriversDialog != null && noDriversDialog.isShowing()) {
                    noDriversDialog.dismiss();
                }
                noDriversDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                noDriversDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
                noDriversDialog.setContentView(R.layout.dialog_custom_one_button);

                RelativeLayout frameLayout = (RelativeLayout) noDriversDialog.findViewById(R.id.rv);
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

                textMessage.setMovementMethod(LinkMovementMethod.getInstance());
                textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

                if (zeroDriversNearby) {
                    textMessage.setText(R.string.sorry_no_drivers_available_nearby);
                } else {
                    if ("".equalsIgnoreCase(message)) {
                        textMessage.setText(R.string.sorry_all_drivers_busy);
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
                slidingBottomPanel.getRequestRideOptionsFragment().setRegionSelected(null);
                noDriversDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNextRequestLevel(int requestType, ArrayList<FindADriverResponse.RequestLevels> requestLevels) {
        Region regionSelected = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
        if(regionSelected.getReverseBid() == 1 || regionSelected.getFareMandatory() == 0 ||  Data.autoData.getDropLatLng() == null) {
            return -1;
        }
        int index = -1;
        // check for next level
        for(int i = 0; i < requestLevels.size(); i++) {
            if(requestLevels.get(i).getLevel() == requestType) {
                index = i + 1;
                break;
            } else {
                index = -1;
            }
        }
        // check index with size of arrayList
        if(index > requestLevels.size() - 1 || !isNewUI) {
            index = -1;
        }
        //check if next level enabled or not
        if(index != -1 && requestLevels.get(index).getEnabled() == 0) {
            int tempIndex = index;
            index = -1;
            for(int i = tempIndex + 1; i < requestLevels.size(); i++) {
                if(requestLevels.get(i).getEnabled() == 1) {
                    index = i;
                    break;
                } else {
                    continue;
                }
            }
        }
        return index;
    }


    public void getRideSummaryAPI(final Activity activity, final String engagementId, final Promo promo) {
        if (!checkIfUserDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                Log.d("HomeActivityRental","GetRideSummmary online");

                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(KEY_ENGAGEMENT_ID, engagementId);
                params.put(KEY_SHOW_RIDE_MENU, "0");

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().getRideSummary(params, new Callback<ShowPanelResponse>() {
                    @Override
                    public void success(ShowPanelResponse showPanelResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getRideSummary response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {
                                    try {
                                        int rideEndGoodFeedbackViewType = jObj.optInt(KEY_RIDE_END_GOOD_FEEDBACK_VIEW_TYPE, Data.autoData.getRideEndGoodFeedbackViewType());
                                        Data.autoData.setRideEndGoodFeedbackViewType(rideEndGoodFeedbackViewType);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    JSONParser.parseRateAppFlagContent(activity, jObj);

                                    Data.userData.updateWalletBalances(jObj, false);
                                    MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(jObj);

                                    Data.autoData.setEndRideData(JSONParser.parseEndRideData(jObj, engagementId, Data.autoData.getFareStructure().getFixedFare()));

                                    clearSPData();
									if (jObj.has(Constants.KEY_FEEDBACK_INFO)) {
										JSONArray jsonArray = jObj.getJSONArray(Constants.KEY_FEEDBACK_INFO);
										JSONParser.parseFeedBackInfo(jsonArray);
									}

                                    passengerScreenMode = PassengerScreenMode.P_RIDE_END;
                                    switchPassengerScreen(passengerScreenMode);

                                    Utils.hideSoftKeyboard(activity, textViewInitialSearch);
                                    Utils.hideSoftKeyboard(activity, textViewInitialSearchNew);
                                    Utils.hideSoftKeyboard(activity, tvPickupRentalOutstation);

                                    setUserData();

                                    showScratchCard(promo);

                                } else {
                                    Log.d("HomeActivityRental","GetRideSummmary Offline 1");

                                    endRideRetryDialog(activity, engagementId, activity.getString(R.string.connection_lost_please_try_again), promo);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            Log.d("HomeActivityRental","GetRideSummmary offline 2");

                            endRideRetryDialog(activity, engagementId, activity.getString(R.string.connection_lost_please_try_again), promo);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("HomeActivityRental","GetRideSummmary offline 3");

                        Log.e(TAG, "getRideSummary error=" + error);
                        DialogPopup.dismissLoadingDialog();
                        endRideRetryDialog(activity, engagementId, activity.getString(R.string.connection_lost_please_try_again), promo);
                    }
                });
            } else {
                endRideRetryDialog(activity, engagementId, activity.getString(R.string.connection_lost_desc), promo);
            }
        }
    }

    private void showScratchCard(Promo promo) {
        if(promo != null) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RewardsDialog dialogFragment = RewardsDialog.newInstance(promo, true, true);
                    dialogFragment.show(getSupportFragmentManager(), "scratchDialog");
                }
            }, 10);
        }
    }

    public void endRideRetryDialog(final Activity activity, final String engagementId, String errorMessage, final Promo promo) {
        DialogPopup.alertPopupWithListener(activity, "", errorMessage, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRideSummaryAPI(activity, engagementId, promo);
            }
        });
    }

    public void sendDropLocationAPI(final Activity activity, final LatLng dropLatLng, final ProgressWheel progressWheel,
									final boolean zoomAfterDropSet, final String address, int poolFareId) {
        if (MyApplication.getInstance().isOnline()) {

            progressWheel.setVisibility(View.VISIBLE);

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("session_id", Data.autoData.getcSessionId());
            params.put(KEY_OP_DROP_LATITUDE, "" + dropLatLng.latitude);
            params.put(KEY_OP_DROP_LONGITUDE, "" + dropLatLng.longitude);
            if(!address.equalsIgnoreCase(Constants.UNNAMED)) {
                params.put(KEY_DROP_LOCATION_ADDRESS, address);
            } else {
                params.put(KEY_DROP_LOCATION_ADDRESS, "");
            }
            if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
                params.put("engagement_id", Data.autoData.getcEngagementId());
            }
            if(poolFareId > 0){
				params.put(KEY_POOL_FARE_ID, String.valueOf(poolFareId));
			}

            Log.i("params", "=" + params);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().addDropLocation(params, new Callback<SettleUserDebt>() {
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
                                createDataAndInsert(1);
                                Data.autoData.setDropLatLng(dropLatLng);
                                Data.autoData.setDropAddress(address);

                                if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
                                    stopDropLocationSearchUI(false);
                                    setDropLocationAssigningUI();
                                } else if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode ||
                                        PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode ||
                                        PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {

                                    stopDropLocationSearchUI(true);
                                    setDropLocationEngagedUI();
                                    if(Data.autoData.getAssignedDriverInfo() != null) {
										getDropLocationPathAndDisplay(Data.autoData.getAssignedDriverInfo().latLng, zoomAfterDropSet, null);
									}
                                }

                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                    }
//                        DialogPopup.dismissLoadingDialog();
                    progressWheel.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "addDropLocation error=" + error.toString());
//                        DialogPopup.dismissLoadingDialog();
                    progressWheel.setVisibility(View.GONE);
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        } else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
            progressWheel.setVisibility(View.GONE);
        }
    }

    private HashMap<String, String> getParamsForDriverLocationUpdate(){
		HashMap<String, String> nameValuePairs = new HashMap<>();
		if(Data.userData != null
				&& Data.autoData != null
				&& Data.autoData.getAssignedDriverInfo() != null
				&& Data.autoData.getPickupLatLng() != null) {
			nameValuePairs.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			nameValuePairs.put(KEY_DRIVER_ID, Data.autoData.getAssignedDriverInfo().userId);
			nameValuePairs.put(KEY_PICKUP_LATITUDE, "" + Data.autoData.getPickupLatLng().latitude);
			nameValuePairs.put(KEY_PICKUP_LONGITUDE, "" + Data.autoData.getPickupLatLng().longitude);
		}

		HomeUtil.addDefaultParams(nameValuePairs);
		return nameValuePairs;
	}

	private boolean driverIsArrivingCheck(){
    	return (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)
				&& (Data.userData != null)
				&& (Data.autoData.getAssignedDriverInfo() != null)
				&& (Data.autoData.getPickupLatLng() != null);
	}


    //Customer's timer
    Timer timerDriverLocationUpdater;
    TimerTask timerTaskDriverLocationUpdater;

    public void startDriverLocationUpdateTimer() {

		cancelDriverLocationUpdateTimer();

		if(Prefs.with(this).getInt(KEY_DRIVER_TRACKING_USING_STREAM_ENABLED, 1) == 1){
			if (MyApplication.getInstance().isOnline()
					&& driverIsArrivingCheck()) {

				streamClient.startLocationStream(getParamsForDriverLocationUpdate(), locationStreamCallback);
			} else {
				streamClient.stopLocationStream();
				if(driverIsArrivingCheck() && !MyApplication.getInstance().isOnline()) {
					DialogPopup.alertPopupWithListener(this, "", getString(R.string.no_internet_connection),
							getString(R.string.tap_to_retry), new OnClickListener() {
								@Override
								public void onClick(View v) {
									startDriverLocationUpdateTimer();
								}
							}, false);
				}
			}

		} else {
			timerDriverLocationUpdater = new Timer();
			timerTaskDriverLocationUpdater = new TimerTask() {

				@Override
				public void run() {
					try {
						if (MyApplication.getInstance().isOnline()
								&& (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)
								&& (Data.userData != null)
								&& (Data.autoData.getAssignedDriverInfo() != null)
								&& (Data.autoData.getPickupLatLng() != null)) {

							long startTime = System.currentTimeMillis();
							Response response = RestClient.getApiService().getDriverCurrentLocation(getParamsForDriverLocationUpdate());
							String result = new String(((TypedByteArray) response.getBody()).getBytes());

							consumeDriverLocationUpdates(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			timerDriverLocationUpdater.scheduleAtFixedRate(timerTaskDriverLocationUpdater, 5000,
					Prefs.with(this).getInt(KEY_CUSTOMER_FETCH_DRIVER_LOCATION_INTERVAL, 30000));
		}

    }

	public void consumeDriverLocationUpdates(String result) {
		try {
			Log.d(TAG, "consumeDriverLocationUpdates result="+result);
			if(result.equalsIgnoreCase("streaming real time data")){
				return;
			}

			JSONObject jObj = new JSONObject(result);

			int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.DRIVER_LOCATION.getOrdinal());
			if (ApiResponseFlags.DRIVER_LOCATION.getOrdinal() == flag) {
				Log.d(TAG, "consumeDriverLocationUpdates DRIVER_LOCATION");
				final LatLng driverCurrentLatLng = new LatLng(jObj.getDouble(KEY_LATITUDE), jObj.getDouble(KEY_LONGITUDE));
				String eta = jObj.optString(KEY_ETA, "5");

				if (Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null
						&& MapUtils.distance(Data.autoData.getAssignedDriverInfo().latLng, driverCurrentLatLng) > 5) {
					Log.d(TAG, "consumeDriverLocationUpdates driverCurrentLatLng");
					DriverToPickupPath.INSTANCE.showPath(HomeActivity.this, passengerScreenMode, map, driverCurrentLatLng, Data.autoData.getPickupLatLng());

					Data.autoData.getAssignedDriverInfo().latLng = driverCurrentLatLng;
					Data.autoData.getAssignedDriverInfo().setEta(eta);
					MyApplication.getInstance().getDatabase2().insertDriverLocations(Integer.parseInt(Data.autoData.getcEngagementId()), driverCurrentLatLng);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								if (Data.autoData != null
										&& (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
										|| PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)
										&& driverLocationMarker != null) {
									Log.d(TAG, "consumeDriverLocationUpdates runOnUiThread");
									MarkerAnimation.clearAsyncList();
									MarkerAnimation.animateMarkerToICS(Data.autoData.getcEngagementId(), driverLocationMarker,
											driverCurrentLatLng, new LatLngInterpolator.LinearFixed(), getMarkerCallbackAnim(),
											false, getMarkerAnimationDuration());
									updateDriverETAText(passengerScreenMode);
								}
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

	private long getMarkerAnimationDuration(){
    	return Prefs.with(this).getLong(passengerScreenMode == PassengerScreenMode.P_IN_RIDE ?
				KEY_DRIVER_MARKER_ANIM_DURATION_INRIDE : KEY_DRIVER_MARKER_ANIM_DURATION_ACCEPT, 9000);
	}

	private StreamClient.LocationStreamCallback locationStreamCallback = new StreamClient.LocationStreamCallback() {
		@NotNull
		@Override
		public HashMap<String, String> getParams() {
			return getParamsForDriverLocationUpdate();
		}

		@Override
		public void onResponse(@NotNull String response) {
			consumeDriverLocationUpdates(response);
		}
	};


	private MarkerAnimation.CallbackAnim callbackAnim = null;
	private MarkerAnimation.CallbackAnim getMarkerCallbackAnim() {
		if(callbackAnim == null){
			callbackAnim = new MarkerAnimation.CallbackAnim() {
				@Override
				public void onPathFound(List<LatLng> latLngs) {
//
				}

				@Override
				public void onTranslate(LatLng latLng, double duration) {
					if (latLng != null && myLocationButtonPressed) {
						zoomtoPickupAndDriverLatLngBounds(latLng, null, (int) (0.9d * duration));
					}
				}

				@Override
				public void onAnimComplete() {

				}

				@Override
				public void onAnimNotDone() {

				}
			};
		}
		return callbackAnim;
	}


	public void cancelDriverLocationUpdateTimer() {
        try {
        	if(!driverIsArrivingCheck()) {
				streamClient.stopLocationStream();
			}

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
	Runnable runnableUpdateDriversInitial;

    public void startTimerUpdateDrivers() {
        cancelTimerUpdateDrivers();
        try {
			runnableUpdateDriversInitial = new Runnable() {
				@Override
				public void run() {
					if (HomeActivity.appInterruptHandler != null) {
						HomeActivity.appInterruptHandler.refreshDriverLocations();
					}
					if(runnableUpdateDriversInitial != null) {
						getHandler().postDelayed(runnableUpdateDriversInitial, 60000);
					}
				}
			};
			getHandler().postDelayed(runnableUpdateDriversInitial, 60000);
            Log.i("timerUpdateDrivers", "started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTimerUpdateDrivers() {
		if (runnableUpdateDriversInitial != null) {
			getHandler().removeCallbacks(runnableUpdateDriversInitial);
			runnableUpdateDriversInitial = null;
		}
    }


    //Both driver and customer
    Timer timerMapAnimateAndUpdateRideData;
    TimerTask timerTaskMapAnimateAndUpdateRideData;

	private boolean driverToDropPathShown = false;
    public void startMapAnimateAndUpdateRideDataTimer() {
        cancelMapAnimateAndUpdateRideDataTimer();
        try {
            timerMapAnimateAndUpdateRideData = new Timer();
            timerTaskMapAnimateAndUpdateRideData = new TimerTask() {

                @Override
                public void run() {
                    try {
                        HashMap<String, String> nameValuePairs = new HashMap<>();
                        nameValuePairs.put("last_sent_max_id", "" +
                                MyApplication.getInstance().getDatabase2().getLastRowIdInRideInfo());
                        nameValuePairs.put("engagement_id", Data.autoData.getcEngagementId());
                        nameValuePairs.put("access_token", Data.userData.accessToken);

                        new HomeUtil().putDefaultParams(nameValuePairs);
                        Response response = RestClient.getApiService().getOngoingRidePath(nameValuePairs);
                        String result = new String(((TypedByteArray) response.getBody()).getBytes());

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
                                            LatLng lastLatLng = null;
                                            List<LatLng> latLngsList = new ArrayList<LatLng>();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                RidePath currentRidePath = new RidePath(
                                                        jsonObject.getLong("id"),
                                                        jsonObject.getDouble("source_latitude"),
                                                        jsonObject.getDouble("source_longitude"),
                                                        jsonObject.getDouble("destination_latitude"),
                                                        jsonObject.getDouble("destination_longitude"));

                                                ridePathsList.add(currentRidePath);
												lastLatLng = new LatLng(currentRidePath.destinationLatitude, currentRidePath.destinationLongitude);

												if(latLngsList.size() == 0){
													latLngsList.add(new LatLng(currentRidePath.sourceLatitude, currentRidePath.sourceLongitude));
												}
												latLngsList.add(lastLatLng);
                                            }

                                            if (lastLatLng != null) {
                                                Data.autoData.getAssignedDriverInfo().latLng = lastLatLng;
                                                getDropLocationPathAndDisplay(Data.autoData.getAssignedDriverInfo().latLng, myLocationButtonPressed, latLngsList);
                                            }
                                            else if(!driverToDropPathShown){
												getDropLocationPathAndDisplay(Data.autoData.getAssignedDriverInfo().latLng, myLocationButtonPressed, latLngsList);
											}
                                            try {
                                                MyApplication.getInstance().getDatabase2().createRideInfoRecords(ridePathsList);

												polylineOptionsInRideDriverPath = null;
												plotPolylineInRideDriverPath();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

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

            displayOldPath();
            if (driverMarkerInRide == null) {
                if (lastRidePathLatLng != null) {
                    Data.autoData.getAssignedDriverInfo().latLng = lastRidePathLatLng;
                }
                driverMarkerInRide = getAssignedDriverCarMarkerOptions(Data.autoData.getAssignedDriverInfo());
                if(Prefs.with(this).getInt(KEY_SHOW_DRIVER_MARKER_IN_RIDE, 1) == 0) {
					driverMarkerInRide.setIcon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
							.createMarkerBitmapForResource(HomeActivity.this, R.drawable.ic_marker_transparent)));
				}
                if (driverMarkerInRide.getRotation() == 0f) {
                    if (Utils.compareFloat(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f), 0f) != 0) {
                        driverMarkerInRide.setRotation(Prefs.with(HomeActivity.this).getFloat(SP_DRIVER_BEARING, 0f));
                    } else {
                        driverMarkerInRide.setRotation((float) Data.autoData.getAssignedDriverInfo().getBearing());
                    }
                }
            }
            MarkerAnimation.clearAsyncList();

            timerMapAnimateAndUpdateRideData.scheduleAtFixedRate(timerTaskMapAnimateAndUpdateRideData, 100,
                    Prefs.with(this).getInt(KEY_CUSTOMER_FETCH_INRIDE_PATH_INTERVAL, 30000));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private LatLng lastRidePathLatLng;

    private ArrayList<PolylineOptions> getPolylineOptionsInRideDriverPath() {
        if (polylineOptionsInRideDriverPath == null) {
            polylineOptionsInRideDriverPath = new ArrayList<>();
        }
        if (polylineOptionsInRideDriverPath.size() == 0) {
            try {
            	if(Prefs.with(this).getInt(KEY_SHOW_RIDE_COVERED_PATH, 0) == 1) {
					ArrayList<RidePath> ridePathsList = MyApplication.getInstance().getDatabase2().getRidePathInfo();
					int coveredPathColor = Prefs.with(this).getInt(KEY_SHOW_RIDE_COVERED_PATH, 0) == 1 ? RIDE_ELAPSED_PATH_COLOR : Color.TRANSPARENT;
					PolylineOptions polylineOptions = new PolylineOptions();
					polylineOptions.width(ASSL.Xscale() * 5F);
					polylineOptions.color(coveredPathColor);
					polylineOptions.geodesic(false);
					polylineOptions.zIndex(ONGOING_RIDE_PATH_ZINDEX);
					for (RidePath ridePath : ridePathsList) {
						if (polylineOptions.getPoints().size() == 0) {
							polylineOptions.add(new LatLng(ridePath.sourceLatitude, ridePath.sourceLongitude));
						}
						polylineOptions.add(new LatLng(ridePath.destinationLatitude, ridePath.destinationLongitude));
						lastRidePathLatLng = ridePath.getDestinationLatLng();
					}
					polylineOptionsInRideDriverPath.add(polylineOptions);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return polylineOptionsInRideDriverPath;
    }

    private void plotPolylineInRideDriverPath() {
        if (map != null) {
            initAndClearInRidePath();
            for (PolylineOptions polylineOptions : getPolylineOptionsInRideDriverPath()) {
                polylinesInRideDriverPath.add(map.addPolyline(polylineOptions));
            }
        }
    }

    private void initAndClearInRidePath() {
        if (map != null) {
            if (polylinesInRideDriverPath == null) {
                polylinesInRideDriverPath = new ArrayList<>();
            }
            if (polylinesInRideDriverPath.size() > 0) {
                for (Polyline polyline : polylinesInRideDriverPath) {
                    polyline.remove();
                }
                polylinesInRideDriverPath.clear();
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayOldPath() {
        try {
            polylineOptionsInRideDriverPath = null;
            plotPolylineInRideDriverPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean toShowPathToDrop() {
        return (PassengerScreenMode.P_IN_RIDE == passengerScreenMode);
    }

    public void getDropLocationPathAndDisplay(final LatLng pickupLatLng, final boolean zoom, final List<LatLng> latLngsListForDriverAnimation) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<LatLng> listPath = null;
                        if (MyApplication.getInstance().isOnline() && Data.autoData.getDropLatLng() != null && pickupLatLng != null && toShowPathToDrop()) {
							JungleApisImpl.DirectionsResult result = JungleApisImpl.INSTANCE.getDirectionsPathSync(pickupLatLng, Data.autoData.getDropLatLng(), "metric", MapsApiSources.CUSTOMER_DRIVER_TO_DROP, false, false);
                            if (result != null) {
                                listPath = result.getLatLngs();
								driverToDropPathShown = true;
                                final List<LatLng> finalListPath = listPath;
                                if (listPath.size() > 0) {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                if (toShowPathToDrop()) {
                                                    pathToDropLocationPolylineOptions = new PolylineOptions();
                                                    pathToDropLocationPolylineOptions.width(ASSL.Xscale() * 5f)
															.color(ContextCompat.getColor(HomeActivity.this, R.color.google_path_polyline_color))
															.geodesic(true).zIndex(PICKUP_TO_DROP_PATH_ZINDEX);

                                                    // for joining last point of driver tracked path to path left (red to blue)
                                                    if (latLngsListForDriverAnimation != null && latLngsListForDriverAnimation.size() > 1) {
                                                        pathToDropLocationPolylineOptions.add(latLngsListForDriverAnimation.get(latLngsListForDriverAnimation.size() - 1));
                                                    }
                                                    for (int z = 0; z < finalListPath.size(); z++) {
                                                        pathToDropLocationPolylineOptions.add(finalListPath.get(z));
                                                    }

                                                    if (pathToDropLocationPolyline != null) {
                                                        pathToDropLocationPolyline.remove();
                                                    }
                                                    pathToDropLocationPolyline = map.addPolyline(pathToDropLocationPolylineOptions);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }


                        final List<LatLng> finalListPath1 = listPath;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (driverMarkerInRide != null && latLngsListForDriverAnimation != null && latLngsListForDriverAnimation.size() > 1) {
                                        int untrackedPathColor = Data.autoData.getDropLatLng() != null ? getResources().getColor(R.color.google_path_polyline_color) : Color.TRANSPARENT;
										MarkerAnimation.clearAsyncList();

										boolean showRideCoveredPath = Prefs.with(HomeActivity.this).getInt(KEY_SHOW_RIDE_COVERED_PATH, 0) == 1;

                                        MarkerAnimation.animateMarkerOnList(driverMarkerInRide, latLngsListForDriverAnimation,
                                                new LatLngInterpolator.LinearFixed(),
												showRideCoveredPath, map,
												RIDE_ELAPSED_PATH_COLOR, untrackedPathColor, ASSL.Xscale() * 5f,
												getMarkerCallbackAnim(), false, getMarkerAnimationDuration());
                                    } else {
                                        MarkerAnimation.clearPolylines();

										if (zoom) {
											List<LatLng> latLngsToInclude = new ArrayList<LatLng>();
											if (latLngsListForDriverAnimation != null) {
												latLngsToInclude.addAll(latLngsListForDriverAnimation);
											}
											if (finalListPath1 != null) {
												latLngsToInclude.addAll(finalListPath1);
											}
											zoomtoPickupAndDriverLatLngBounds(Data.autoData.getAssignedDriverInfo().latLng, latLngsToInclude, 0);
										}
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
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MarkerOptions getCustomerLocationMarkerOptions(LatLng customerLatLng, boolean savedLocationView) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("End Location");
        markerOptions.snippet("");
        markerOptions.position(customerLatLng);
        markerOptions.zIndex(HOME_MARKER_ZINDEX);
        markerOptions.icon(savedLocationView ? BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCustomMarkerWithSavedLocation(HomeActivity.this, false)) :
                BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmapEnd(HomeActivity.this)));
        return markerOptions;
    }

    void callAnAutoPopup(final Activity activity) {
        try {

            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_custom_two_buttons);

            new ASSL(activity, (RelativeLayout) dialog.findViewById(R.id.rv), 1134, 720, true);

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
            textHead.setText(R.string.confirm_ride_request);


            final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.mavenMedium(activity));

            btnOk.setText(getString(R.string.ok));
            btnCancel.setText(getString(R.string.cancel));

            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestRideDriverCheck()) {
                        dialog.dismiss();
                    }
                }

            });

            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    specialPickupScreenOpened = false;
//                    passengerScreenMode = P_INITIAL;
//                    switchPassengerScreen(passengerScreenMode);
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
            if ((Utils.compareDouble(Data.autoData.getPickupLatLng().latitude, 30.7500) == 0 && Utils.compareDouble(Data.autoData.getPickupLatLng().longitude, 76.7800) == 0)
                    ||
                    (Utils.compareDouble(Data.autoData.getPickupLatLng().latitude, 22.971723) == 0 && Utils.compareDouble(Data.autoData.getPickupLatLng().longitude, 78.754263) == 0)) {
                myLocation = null;
                Utils.showToast(activity, getResources().getString(R.string.waiting_for_location));
                return;
            } else {
                if (myLocation == null) {
                    //We could not detect your location. Are you sure you want to request an auto to pick you at this location
                    myLocation = new Location(LocationManager.GPS_PROVIDER);
                    myLocation.setLatitude(Data.autoData.getPickupLatLng().latitude);
                    myLocation.setLongitude(Data.autoData.getPickupLatLng().longitude);
                    myLocation.setAccuracy(100);
                    myLocation.setTime(System.currentTimeMillis());
                    textMessage.setText(getString(R.string.we_could_not_detect_your_location));
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
                        textMessage.setText(getString(R.string.location_accuracy_is_low));
                        dialog.show();
                    } else {
                        double distance = MapUtils.distance(Data.autoData.getPickupLatLng(), new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        if (distance > MAP_PAN_DISTANCE_CHECK && Data.autoData.getNoDriverFoundTip() == 0.0) {
                            textMessage.setText(getString(R.string.the_pickup_location_you_have_set_is_different));
                            dialog.show();
                        } else {
                            requestRideDriverCheck();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean requestRideDriverCheck() {
        if (MyApplication.getInstance().isOnline()) {
            if (getFilteredDrivers() == 0) {
                noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
                specialPickupScreenOpened = false;
                passengerScreenMode = P_INITIAL;
                switchPassengerScreen(passengerScreenMode);
                return true;
            } else {
                initiateRequestRide(true);
                return true;
            }
        } else {
            DialogPopup.dialogNoInternet(HomeActivity.this, getString(R.string.connection_lost_title),
                    getString(R.string.connection_lost_desc), new Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View v) {
                            requestRideDriverCheck();
                        }

                        @Override
                        public void neutralClick(View v) {
                        }

                        @Override
                        public void negativeClick(View v) {
                        }
                    });
        }
        return false;
    }

    private int getFilteredDrivers() {
        int driversCount = 0;

        if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() ==
                            RideTypeValue.BIKE_RENTAL.getOrdinal())
        {
            return 1;
        }

        for (DriverInfo driverInfo : Data.autoData.getDriverInfos()) {
            if (driverInfo.getOperatorId() == slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOperatorId()
                    && driverInfo.getRegionIds() != null
                    && driverInfo.getRegionIds().contains(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionId())
                    && (driverInfo.getPaymentMethod() == DriverInfo.PaymentMethod.BOTH.getOrdinal() || driverInfo.getPaymentMethod() == 0
                    || Data.autoData.getPickupPaymentOption() == PaymentOption.CASH.getOrdinal())
                    ) {
                driversCount++;
            }
        }
        return driversCount;
    }

    private void noDriverNearbyToast(String message) {
        Toast toast = Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * Displays popup to rate the app
     */
    public void rateAppPopup(final RateAppDialogContent rateAppDialogContent) {
        try {
            if (rateAppDialogContent != null) {
                getRateAppDialog().show(rateAppDialogContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RateAppDialog rateAppDialog;

    private RateAppDialog getRateAppDialog() {
        if (rateAppDialog == null) {
            rateAppDialog = new RateAppDialog(this);
        }
        return rateAppDialog;
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
    public void startRideForCustomer(final int flag, final String message, final PlaceOrderResponse.ReferralPopupContent referralPopupContent) {
        try {
            if (userMode == UserMode.PASSENGER && (passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)) {
                Log.e("in ", "herestartRideForCustomer");

                closeCancelActivity();

                if (flag == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Log.i("in in herestartRideForCustomer  run class", "=");
                                if (Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null) {
                                    Data.autoData.setReferralPopupContent(referralPopupContent);
                                    initializeStartRideVariables();
                                    passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
                                    switchPassengerScreen(passengerScreenMode);
                                } else {
                                    callAndHandleStateRestoreAPI(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Log.i("in in herestartRideForCustomer  run class", "=");
                                if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode) {
                                    firstTimeZoom = false;
                                    passengerScreenMode = P_INITIAL;
                                    switchPassengerScreen(passengerScreenMode);
                                    if(!isFinishing()) {
										DialogPopup.alertPopup(HomeActivity.this, "", message);
									}
                                    dismissSOSDialog();
                                }
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


    @Override
    public void refreshDriverLocations() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isSpecialPickupScreenOpened() && !scheduleRideOpen) {
                        callMapTouchedRefreshDrivers(getApiFindADriver().getParams());
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
            if (userMode == UserMode.PASSENGER) {
                if (jObj.getString("session_id").equalsIgnoreCase(Data.autoData.getcSessionId())) {
                    cancelTimerUpdateDrivers();
                    cancelTimerRequestRide();
                    fetchAcceptedDriverInfoAndChangeState(jObj, ApiResponseFlags.RIDE_ACCEPTED.getOrdinal());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdatePoolRideStatus(JSONObject jsonObject) {
        try {
            final String poolRideStatusString = jsonObject.optString("message", getResources().getString(R.string.sharing_your_ride_with));
            ArrayList<String> fellowRiders = new ArrayList<>();
            JSONArray userNames = jsonObject.optJSONArray("user_names");
            if (userNames != null) {
                for (int i = 0; i < userNames.length(); i++) {
                    fellowRiders.add(userNames.getJSONObject(i).optString("user_name"));
                }
            }
            setPoolRideStatus(poolRideStatusString, fellowRiders);
            Data.autoData.getAssignedDriverInfo().setPoolRideStatusString(poolRideStatusString);
            Data.autoData.getAssignedDriverInfo().setFellowRiders(fellowRiders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    StringBuilder sb;

    private void setPoolRideStatus(String message, ArrayList<String> userNames) {
        if (message.equalsIgnoreCase("")) {
            message = getResources().getString(R.string.sharing_your_ride_with);
        }
        sb = new StringBuilder();
        sb.append(message);
        if (userNames != null) {
            for (int i = 0; i < userNames.size(); i++) {
                sb.append(userNames.get(i));
                break;
            }
            if (userNames.size() > 1) {
                if (userNames.size() == 2) {
                    sb.append(" and " + (userNames.size() - 1) + " other.");
                } else {
                    sb.append(" and " + (userNames.size() - 1) + " others.");
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewFellowRider.setText(sb);
            }
        });
    }


    @Override
    public void onCancelCompleted() {
        customerUIBackToInitialAfterCancel();
    }


    public void fetchAcceptedDriverInfoAndChangeState(JSONObject jObj, int flag) {
        try {
            cancelTimerRequestRide();
            ArrayList<String> fellowRiders = new ArrayList<>();
            Data.autoData.setcSessionId(jObj.getString("session_id"));
            setTipAmountToZero(true);
            Data.autoData.setcEngagementId(jObj.getString("engagement_id"));
            Data.autoData.setcDriverId(jObj.getString("driver_id"));

            String userName = jObj.getString("user_name");
            String driverPhone = jObj.getString("phone_no");
            String driverImage = jObj.getString("user_image");
            String driverCarImage = jObj.getString("driver_car_image");
            double latitude = jObj.getDouble("current_location_latitude");
            double longitude = jObj.getDouble("current_location_longitude");
            int chatEnabled = jObj.optInt("chat_enabled", 0);
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
            String promoName = JSONParser.getPromoName(this, jObj);

            LatLng pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);
            Log.w("pickuplogging", "fetchAcceptedDriverInfoAndChangeState"+pickupLatLng);
            String pickupAddress = jObj.optString(KEY_PICKUP_LOCATION_ADDRESS, Data.autoData.getPickupAddress(pickupLatLng));
            Data.autoData.setPickupSearchResult(new SearchResult(pickupAddress, pickupAddress, "", pickupLatitude, pickupLongitude));

            JSONParser.parseDropLatLng(jObj);

            double fareFactor = 1.0, bearing = 0.0;
            if (jObj.has("fare_factor")) {
                fareFactor = jObj.getDouble("fare_factor");
            }
            Data.autoData.setFareFactor(fareFactor);
            double fareFixed = 0;
            String cancelRideThrashHoldTime = "";
            int cancellationCharges = 0;
            try {
                fareFixed = jObj.optJSONObject("fare_details").optDouble("fare_fixed", 0);
                cancelRideThrashHoldTime = jObj.optString("cancel_ride_threshold_time", "");
                cancellationCharges = jObj.optInt("cancellation_charge", 0);
                bearing = jObj.optDouble(KEY_BEARING, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
            int preferredPaymentMode = jObj.optInt(KEY_PREFERRED_PAYMENT_MODE, PaymentOption.CASH.getOrdinal());
            int isPooledRIde = jObj.optInt(KEY_IS_POOLED, 0);

            Schedule scheduleT20 = JSONParser.parseT20Schedule(jObj);

            int vehicleType = jObj.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
            int operatorId = jObj.optInt(KEY_OPERATOR_ID, 0);
            String iconSet = jObj.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
            String currency = jObj.optString(KEY_CURRENCY);
            Double tipAmount = jObj.optDouble(KEY_TIP_AMOUNT);
            String vehicleIconUrl = jObj.optString(Constants.KEY_MARKER_ICON);
            Prefs.with(this).save(Constants.KEY_EMERGENCY_NO, jObj.optString(KEY_EMERGENCY_NO, getString(R.string.police_number)));
            int isCorporateRide= jObj.optInt(Constants.KEY_IS_CORPORATE_RIDE, 0);
            String cardId= jObj.optString(Constants.KEY_CARD_ID, "0");
            int rideType = jObj.optInt(KEY_RIDE_TYPE, RideTypeValue.NORMAL.getOrdinal());

            int gpsLockStatus = jObj.optInt(KEY_GPS_LOCK_STATUS,GpsLockStatus.UNLOCK.getOrdinal());
            int fareMandatory = jObj.optInt(Constants.KEY_FARE_MANDATORY,0);
            double tipBeforeRequestRide = jObj.optDouble(Constants.KEY_TIP_PROVIDED_BEFORE_RIDE_REQUEST, 0.0);

            String userIdentifier = jObj.optString(Constants.KEY_DRIVER_IDENTIFIER, "");

            Data.autoData.setAssignedDriverInfo(new DriverInfo(this, Data.autoData.getcDriverId(), latitude, longitude, userName,
                    driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta,
                    fareFixed, preferredPaymentMode, scheduleT20, vehicleType, iconSet, cancelRideThrashHoldTime,
                    cancellationCharges, isPooledRIde, "", fellowRiders, bearing, chatEnabled, operatorId, currency, vehicleIconUrl,tipAmount,
                    isCorporateRide, cardId, rideType, gpsLockStatus, fareMandatory, tipBeforeRequestRide, userIdentifier));

            JSONParser.FuguChannelData fuguChannelData = new JSONParser.FuguChannelData();
            JSONParser.parseFuguChannelDetails(jObj, fuguChannelData);
            Data.autoData.setFuguChannelId(fuguChannelData.getFuguChannelId());
            Data.autoData.setFuguChannelName(fuguChannelData.getFuguChannelName());
            Data.autoData.setFuguTags(fuguChannelData.getFuguTags());

            MyApplication.getInstance().getDatabase2().insertDriverLocations(Integer.parseInt(Data.autoData.getcEngagementId()), new LatLng(latitude, longitude));

            if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
                passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
            } else if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {
                initializeStartRideVariables();
                passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
            } else if (ApiResponseFlags.RIDE_ARRIVED.getOrdinal() == flag) {
                passengerScreenMode = PassengerScreenMode.P_DRIVER_ARRIVED;
            }
            if(rideType == RideTypeValue.BIKE_RENTAL.getOrdinal()){
                HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchPassengerScreen(passengerScreenMode);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clearRideSPData() {

        MyApplication.getInstance().getDatabase().deleteSavedPath();

    }

    public void clearSPData() {

        MyApplication.getInstance().getDatabase().deleteSavedPath();

    }


    @Override
    public void customerEndRideInterrupt(final String engagementId, final Promo promo) {
        try {
            if (userMode == UserMode.PASSENGER && engagementId.equalsIgnoreCase(Data.autoData.getcEngagementId())) {
                closeCancelActivity();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //dismiss rental dialogs if any
                        DialogPopup.dismissAlertPopup();
                        if(rentalLockDialog != null){
                            rentalLockDialog.dismiss();
                        }
                        getRideSummaryAPI(HomeActivity.this, engagementId, promo);
                        if(driverTipInteractor != null) {
							driverTipInteractor.dismissDialog();
						}
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


    private void checkForMyLocationButtonVisibility() {
        if(P_INITIAL != passengerScreenMode){
            return;
        }
        if (relativeLayoutFinalDropLocationParent.getVisibility() == View.GONE) {
            try {
            	LatLng latLngToMatch = getDeviceLocation();
                if ("".equalsIgnoreCase(Data.autoData.getFarAwayCity()) || changeLocalityLayout.getVisibility() == View.GONE) {
                    if (MapUtils.distance(latLngToMatch, map.getCameraPosition().target) > MAP_PAN_DISTANCE_CHECK) {
                        initialMyLocationBtn.setVisibility(View.VISIBLE);
                    } else {
                        initialMyLocationBtn.setVisibility(View.GONE);
                    }
                } else {
                    initialMyLocationBtn.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                if ("".equalsIgnoreCase(Data.autoData.getFarAwayCity()) || changeLocalityLayout.getVisibility() == View.GONE) {
                    initialMyLocationBtn.setVisibility(View.VISIBLE);
                }
            }
        } else {
            initialMyLocationBtn.setVisibility(View.GONE);
        }
    }


    public void initializeFusedLocationFetchers() {
        getLocationFetcher().connect(this, 10000);
    }





    public void locationChanged(Location location) {
        super.locationChanged(location);
        try {
            Data.latitude = location.getLatitude();
            Data.longitude = location.getLongitude();
			if (getResources().getBoolean(R.bool.autos_map_traffic_default_enabled)) {
				map.setTrafficEnabled(true);
			}
            if (location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK) {
                HomeActivity.myLocation = location;
            }
            checkForMyLocationButtonVisibility();

            if (P_INITIAL == passengerScreenMode && !zoomedToMyLocation && !zoomingForDeepLink) {
                Data.autoData.setFarAwayCity("");
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
            if (!cached && P_INITIAL == passengerScreenMode
                    && relativeLayoutLocationError.getVisibility() == View.VISIBLE) {
                locationGotNow();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void locationGotNow() {
        relativeLayoutLocationError.setVisibility(View.GONE);
        checkForMyLocationButtonVisibility();
        if(!confirmedScreenOpened && !isNewUI()) {
            imageViewRideNow.setVisibility(View.VISIBLE);
        }
        showCenterPickupPin(true);
    }

    private void showCenterPickupPin(boolean showMarker) {
        if(Prefs.with(this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 1 && !isNewUI){
            centreLocationRl.setVisibility(View.VISIBLE);
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
                currentLocationMarker = null;
            }
        } else {
            if(showMarker){
                addUserCurrentLocationAddressMarker();
            }
            centreLocationRl.setVisibility(View.GONE);
        }
    }

    private void hideCenterPickupPin(){
        centreLocationRl.setVisibility(View.GONE);
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            currentLocationMarker = null;
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
    boolean requestRideApiInProgress = false;

    public void startTimerRequestRide() {
        cancelTimerRequestRide();
        Prefs.with(HomeActivity.this).save(Constants.SKIP_SAVE_PICKUP_LOCATION, false);
        Prefs.with(HomeActivity.this).save(Constants.SKIP_SAVE_DROP_LOCATION, false);
        try {

            //******************* TYPE : 0 for Pickup Location, TYPE :- 1 for Drop Location ********************//
            callInsertData();
            requestRideLifeTime = Data.autoData.getIsReverseBid() == 1 ? Data.autoData.getBidRequestRideTimeout() : 3 * 60 * 1000;
            serverRequestStartTime = System.currentTimeMillis();
            serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
            executionTime = -10;
            if(Data.autoData.getIsReverseBid() == 1){
                requestPeriod = 5000;
            } else {
                requestPeriod = 20000;
            }
			requestRideApiInProgress = false;

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
                                            noDriverAvailablePopup(HomeActivity.this, false, "", -1);
                                            HomeActivity.passengerScreenMode = P_INITIAL;
                                            switchPassengerScreen(passengerScreenMode);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
							Log.e("request_ride execution", "requestRideApiInProgress =" + requestRideApiInProgress+", SessionId="+Data.autoData.getcSessionId());
                            if ((!requestRideApiInProgress || !"".equalsIgnoreCase(Data.autoData.getcSessionId()))
									&& HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                updateCancelButtonUI();

                                long apiStartTime = System.currentTimeMillis();
                                HashMap<String, String> nameValuePairs = new HashMap<>();
                                nameValuePairs.put("access_token", Data.userData.accessToken);
                                nameValuePairs.put("latitude", "" + Data.autoData.getPickupLatLng().latitude);
                                nameValuePairs.put("longitude", "" + Data.autoData.getPickupLatLng().longitude);
								putSelectedPickupAddress(nameValuePairs);

								//30.7500, 76.7800
//								nameValuePairs.add(new BasicNameValuePair("latitude", "30.7500"));
//								nameValuePairs.add(new BasicNameValuePair("longitude", "76.7800"));

								Region regionSelected = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();

                                if (myLocation != null) {
                                    nameValuePairs.put("current_latitude", "" + myLocation.getLatitude());
                                    nameValuePairs.put("current_longitude", "" + myLocation.getLongitude());
                                } else {
                                    nameValuePairs.put("current_latitude", "" + Data.autoData.getPickupLatLng().latitude);
                                    nameValuePairs.put("current_longitude", "" + Data.autoData.getPickupLatLng().longitude);
                                }
                                if (regionSelected.getRideType() != RideTypeValue.BIKE_RENTAL.getOrdinal()
                                        && !(Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null && Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType()))
										&& Data.autoData.getDropLatLng() != null
                                        && Utils.compareDouble(Data.autoData.getDropLatLng().latitude, 0) != 0
                                        && Utils.compareDouble(Data.autoData.getDropLatLng().longitude, 0) != 0) {
                                    nameValuePairs.put(KEY_OP_DROP_LATITUDE, String.valueOf(Data.autoData.getDropLatLng().latitude));
                                    nameValuePairs.put(KEY_OP_DROP_LONGITUDE, String.valueOf(Data.autoData.getDropLatLng().longitude));
									putSelectedDropAddress(nameValuePairs);
								}

                                if (promoCouponSelectedForRide != null && regionSelected.getReverseBid() == 0) {
                                    if (promoCouponSelectedForRide instanceof CouponInfo) {
                                        nameValuePairs.put(KEY_COUPON_TO_APPLY, String.valueOf(promoCouponSelectedForRide.getId()));
                                        if (promoCouponSelectedForRide.getId() == 0) {
                                            nameValuePairs.put(KEY_PROMO_TO_APPLY, String.valueOf(promoCouponSelectedForRide.getId()));
                                        }
                                    } else if (promoCouponSelectedForRide instanceof PromotionInfo) {
                                        nameValuePairs.put(KEY_PROMO_TO_APPLY, String.valueOf(promoCouponSelectedForRide.getId()));
                                    }
                                    nameValuePairs.put(KEY_MASTER_COUPON, String.valueOf(promoCouponSelectedForRide.getMasterCoupon()));
                                }
                                if ("".equalsIgnoreCase(Data.autoData.getcSessionId())) {
                                    double fareFactor = regionSelected.getCustomerFareFactor();
                                    double driverFareFactor = regionSelected.getDriverFareFactor();

                                    nameValuePairs.put("duplicate_flag", "0");
                                    nameValuePairs.put(KEY_CUSTOMER_FARE_FACTOR, String.valueOf(fareFactor));
                                    nameValuePairs.put(KEY_DRIVER_FARE_FACTOR, String.valueOf(driverFareFactor));

                                    if (myLocation != null && myLocation.hasAccuracy()) {
                                        nameValuePairs.put("location_accuracy", "" + myLocation.getAccuracy());
                                    }

                                } else {
                                    nameValuePairs.put("duplicate_flag", "1");
                                }


                                String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
                                if (links != null) {
                                    if (!"[]".equalsIgnoreCase(links)) {
                                        nameValuePairs.put(KEY_BRANCH_REFERRING_LINKS, links);
                                    }
                                }

                                nameValuePairs.put(Constants.KEY_PREFERRED_PAYMENT_MODE, "" + Data.autoData.getPickupPaymentOption());
								if(Data.autoData.getPickupPaymentOption() == PaymentOption.CORPORATE.getOrdinal()){
									nameValuePairs.put(Constants.KEY_MANUAL_RIDE_REQUEST, "" +	CorporatesAdapter.Companion
											.getSelectedCorporateBusinessId());
								}
                                nameValuePairs.put(KEY_VEHICLE_TYPE, String.valueOf(regionSelected.getVehicleType()));
                                nameValuePairs.put(KEY_REVERSE_BID, String.valueOf(regionSelected.getReverseBid()));
                                nameValuePairs.put(KEY_REGION_ID, String.valueOf(regionSelected.getRegionId()));
                                if(mNotes != null && !mNotes.isEmpty()) {
                                    nameValuePairs.put(Constants.KEY_CUSTOMER_NOTE, mNotes + "");
                                }
                                if(Data.autoData.getSelectedPackage() != null) {
                                    nameValuePairs.put(Constants.KEY_PACKAGE_ID, String.valueOf(Data.autoData.getSelectedPackage().getPackageId()));
                                }
                                if (isNewUI && regionSelected.getReverseBid() == 1) {
                                    if (!editTextBidValue.getText().toString().isEmpty()) {
                                        nameValuePairs.put(Constants.KEY_INITIAL_BID_VALUE, editTextBidValue.getText().toString());
                                    }
                                }
                                if(Data.autoData.showRegionSpecificFare()){
                                    if ((regionSelected.getRideType() == RideTypeValue.POOL.getOrdinal() || regionSelected.getFareMandatory() == 1)
                                            && regionSelected.getRegionFare() != null && regionSelected.getRegionFare().getPoolFareId() > 0) {
                                        nameValuePairs.put(Constants.KEY_POOL_FARE_ID, "" + regionSelected.getRegionFare().getPoolFareId());
                                    }
                                } else {
                                    if (regionSelected.getRideType() == RideTypeValue.POOL.getOrdinal()) {
                                        nameValuePairs.put(Constants.KEY_POOL_FARE_ID, "" + jugnooPoolFareId);
                                    } else if(regionSelected.getFareMandatory() == 1 && jugnooPoolFareId > 0){
                                        nameValuePairs.put(Constants.KEY_POOL_FARE_ID, "" + jugnooPoolFareId);
                                    }
                                }

                                if(Data.autoData.getPickupPaymentOption()==PaymentOption.STRIPE_CARDS.getOrdinal()) {
                                    String cardId = Prefs.with(HomeActivity.this).getString(Constants.STRIPE_SELECTED_POS, "0");
                                    if(!cardId.equalsIgnoreCase("0")) {
                                        nameValuePairs.put(Constants.KEY_CARD_ID, cardId);
                                    }
                                }
//                                if(regionSelected.getRegionFare() != null && regionSelected.getFareMandatory() == 1){
//                                    nameValuePairs.put(Constants.KEY_FARE, "" + regionSelected.getRegionFare().getFare());
//
//                                    if(getApiFindADriver() != null && getApiFindADriver().getParams() != null){
//                                        nameValuePairs.put(KEY_RIDE_DISTANCE, getApiFindADriver().getParams().get(KEY_RIDE_DISTANCE));
//                                        nameValuePairs.put(KEY_RIDE_TIME, getApiFindADriver().getParams().get(KEY_RIDE_TIME));
//                                    }
//                                }



                                // TODO  if() delete the taxi one And unComment the bikeRental one

                                if (regionSelected.getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                                    nameValuePairs.put("qr_code", qrCode);
                                    nameValuePairs.put("ride_type", String.valueOf(RideTypeValue.BIKE_RENTAL.getOrdinal()));
                                    nameValuePairs.put("qr_code_details",qrCodeDetails);
                                }
                                nameValuePairs.put("is_bluetooth_tracker",""+Data.autoData.getBluetoothEnabled());
//                                nameValuePairs.put("is_scratch_coupon_applicable", "" + true); // key added for differentiating request ride call from new scratch App and old App

                                Log.i("nameValuePairs of request_ride", "=" + nameValuePairs);


                                //add tip feature keys in /request_ride
                                if(Data.autoData.getNoDriverFoundTip() > 0.0) {
                                    nameValuePairs.put("tip_amount","" + Data.autoData.getNoDriverFoundTip());
                                }
                                if(regionSelected.getReverseBid() == 0 && regionSelected.getFareMandatory() == 1) {
                                    nameValuePairs.put("request_level", mRequestType + "");
                                }
                                if(mRequestType + 1 >= 2) {
                                    mRequestType = 0;
                                }



                                try {
                                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

								requestRideApiInProgress = true;
                                new HomeUtil().putDefaultParams(nameValuePairs);
                                Response responseRetro = RestClient.getApiService().requestRide(nameValuePairs);
                                String response = new String(((TypedByteArray) responseRetro.getBody()).getBytes());

                                try {
                                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }



                                if (responseRetro == null || response == null
                                        || response.contains(Constants.SERVER_TIMEOUT)) {
                                    Log.e("timeout", "=");
                                } else {
                                    try {
                                        JSONObject jObj = new JSONObject(response);
                                        if (!jObj.isNull("error")) {
                                            final String errorMessage = jObj.getString("error");
                                            final int flag = jObj.getInt("flag");
                                            if (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                                                cancelTimerRequestRide();
                                                HomeActivity.logoutUser(HomeActivity.this);
                                            } else {
                                                if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                                    cancelTimerRequestRide();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                                    DialogPopup.alertPopup(HomeActivity.this, "", errorMessage);
                                                                    HomeActivity.passengerScreenMode = P_INITIAL;
                                                                    switchPassengerScreen(passengerScreenMode);
                                                                    DialogPopup.dismissLoadingDialog();
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            int flag = jObj.getInt("flag");
                                            if (ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag) {
                                                final String log = jObj.getString("log");
                                                final int lockStatus = jObj.optInt("gps_lock_status", 0);
                                                if(lockStatus==3){
                                                    smartLockObj.downDevice();
                                                }
                                                Log.e("ASSIGNING_DRIVERS log", "=" + log);
                                                final String start_time = jObj.getString("start_time");
                                                if (executionTime < 0) {
                                                    serverRequestStartTime = new DateOperations().getMilliseconds(start_time);
                                                    serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
                                                    long stopTime = System.currentTimeMillis();
                                                    long elapsedTime = stopTime - startTime;
                                                    executionTime = serverRequestStartTime + elapsedTime;
                                                }
                                                if ("".equalsIgnoreCase(Data.autoData.getcSessionId())) {
                                                    fbLogEvent(nameValuePairs);

                                                    confirmedScreenOpened = false;
                                                    specialPickupScreenOpened = false;
                                                    if (Data.autoData.getPickupPaymentOption() != PaymentOption.CASH.getOrdinal()) {
                                                        Prefs.with(HomeActivity.this).save(SP_LAST_USED_WALLET, Data.autoData.getPickupPaymentOption());
                                                    }

                                                    if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected() != null) {
                                                        GAUtils.event(RIDES, HOME, slidingBottomPanel
                                                                .getRequestRideOptionsFragment().getRegionSelected().getRegionName() + " " + REQUESTED);
                                                    }
                                                    if(nameValuePairs.containsKey(Constants.KEY_INITIAL_BID_VALUE)){
                                                    	Data.autoData.setInitialBidValue(Double.parseDouble(nameValuePairs.get(Constants.KEY_INITIAL_BID_VALUE)));
                                                    } else if (!editTextBidValue.getText().toString().isEmpty()) {
                                                    	Data.autoData.setInitialBidValue(Double.parseDouble(editTextBidValue.getText().toString()));
                                                    } else {
                                                    	Data.autoData.setInitialBidValue(0);
                                                    }
												}
                                                checkForAddedTip();
                                                Data.autoData.setcSessionId(jObj.getString("session_id"));
                                                Data.autoData.setNoDriverFoundTip(jObj.optDouble("tip_amount", 0.0));
                                                Data.autoData.setBidInfos(JSONParser.parseBids(HomeActivity.this, Constants.KEY_BIDS, jObj));
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            updateBidsView();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag
                                                    || ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag
                                                    || ApiResponseFlags.RIDE_ARRIVED.getOrdinal() == flag) {
                                                if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                    cancelTimerRequestRide();
                                                    fetchAcceptedDriverInfoAndChangeState(jObj, flag);
                                                }
                                            } else if (ApiResponseFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag) {
                                                final String log = jObj.getString("log");
                                                final int requestType = jObj.optInt("request_level", -1);
                                                Log.e("NO_DRIVERS_AVAILABLE log", "=" + log);
                                                cancelTimerRequestRide();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                                if((requestType == 0 || requestType == 1) && isNewUI) {
                                                                    mLogMsg = log;
                                                                    mRequestType = requestType;
                                                                } else {
                                                                    noDriverAvailablePopup(HomeActivity.this, false, log, requestType);
                                                                }
                                                                firstTimeZoom = false;
                                                                HomeActivity.passengerScreenMode = P_INITIAL;
                                                                switchPassengerScreen(passengerScreenMode);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else if (ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag) {
                                                final String message = jObj.optString(KEY_MESSAGE, "");
                                                final double userDebt = jObj.optDouble(KEY_USER_DEBT, 0);
                                                Log.e("USER_IN_DEBT message", "=" + message);
                                                cancelTimerRequestRide();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                                                HomeActivity.passengerScreenMode = P_INITIAL;
                                                                switchPassengerScreen(passengerScreenMode);
                                                                new UserDebtDialog(HomeActivity.this, Data.userData,
                                                                        new UserDebtDialog.Callback() {
                                                                            @Override
                                                                            public void successFullyDeducted(double userDebt) {
                                                                                MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
                                                                                setUserData();
                                                                            }
                                                                        }).showUserDebtDialog(userDebt, message);
                                                            }
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
                                mNotes = "";
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

	public void putSelectedDropAddress(HashMap<String, String> nameValuePairs) {
		if (Data.autoData != null && !Data.autoData.getDropAddress().equalsIgnoreCase(Constants.UNNAMED)) {
			nameValuePairs.put(KEY_DROP_LOCATION_ADDRESS, Data.autoData.getDropAddress());
		} else {
			nameValuePairs.put(KEY_DROP_LOCATION_ADDRESS, "");
		}
	}

	public void putSelectedPickupAddress(HashMap<String, String> nameValuePairs) {
    	if(Data.autoData != null) {
			String address = selectedSpecialPickup
					+ (Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()).equalsIgnoreCase("Current Location")
					? "" : Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()));
			if (!TextUtils.isEmpty(address) && !address.equalsIgnoreCase(Constants.UNNAMED)) {
				nameValuePairs.put(KEY_PICKUP_LOCATION_ADDRESS, address);
			}
		}
	}

	private void createDataAndInsert(final int type) {
        SearchResult searchResult = HomeUtil.getNearBySavedAddress(this, type == 0 ? Data.autoData.getPickupLatLng() : Data.autoData.getDropLatLng(), true);
        if(searchResult != null) {
            return;
        }
        SearchLocation searchLocation;
        if(type == 0) {
        	if(TextUtils.isEmpty(Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()))){
        		return;
			}
            searchLocation = new SearchLocation(Data.autoData.getPickupLatLng().latitude, Data.autoData.getPickupLatLng().longitude, "",
                    Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()), 0 + "", System.currentTimeMillis(), type);
        } else {
            if(Data.autoData.getDropLatLng() == null){
                return;
            }
            searchLocation = new SearchLocation(Data.autoData.getDropLatLng().latitude, Data.autoData.getDropLatLng().longitude, "",
                    Data.autoData.getDropAddress(), Data.autoData.getDropAddressId() + "", System.currentTimeMillis(), type);
        }

        DBCoroutine.Companion.insertLocation(searchLocationDB, searchLocation);
//        DBCoroutine.Companion.getPickupLocation(searchLocationDB, new DBCoroutine.SearchLocationCallback() {
//            @Override
//            public void onSearchLocationReceived(@NotNull List<SearchLocation> searchLocation) {
//
//                Log.w("search Location:- ", searchLocation.toString());
//            }
//        });

    }

    public boolean isNewUI() {
        return isNewUI;
    }

    public void cancelTimerRequestRide() {
        try {
			requestRideApiInProgress = false;
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
                try {
                    if ("".equalsIgnoreCase(Data.autoData.getcSessionId())) {
                        relativeLayoutAssigningDropLocationParentSetVisibility(View.GONE);
                        tvInitialCancelRide.setVisibility(View.GONE);
                        topBar.tvCancel.setVisibility(View.GONE);
                        findDriverJugnooAnimation.setVisibility(View.VISIBLE);
                        if (findDriverJugnooAnimation instanceof ImageView) {
                            jugnooAnimation.start();
                        }
                    } else {
                        setDropLocationAssigningUI();

                        if (bidsPlacedAdapter.getItemCount() == 0) {
							topBar.tvCancel.setVisibility(View.GONE);
                            tvInitialCancelRide.setVisibility(View.VISIBLE);
                        } else {
							topBar.tvCancel.setVisibility(View.VISIBLE);
                            tvInitialCancelRide.setVisibility(View.GONE);
                        }

                        if (findDriverJugnooAnimation instanceof ImageView) {
                            jugnooAnimation.stop();
                        }
                        findDriverJugnooAnimation.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fbLogEvent(HashMap<String, String> params) {
        try {
            Bundle bundle = new Bundle();
            for (String key : params.keySet()) {
                bundle.putString(key, params.get(key));
            }

            MyApplication.getInstance().getAppEventsLogger().logEvent("request_ride",
                    bundle
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNoDriversAvailablePushRecieved(final String logMessage, final int requestType) {
        cancelTimerRequestRide();
        if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
            firstTimeZoom = false;
            HomeActivity.passengerScreenMode = P_INITIAL;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if((requestType == 0 || requestType == 1) && isNewUI) {
                            mLogMsg = logMessage;
                            mRequestType = requestType;
                        } else {
                            noDriverAvailablePopup(HomeActivity.this, false, logMessage, requestType);
                        }
                        HomeActivity.passengerScreenMode = P_INITIAL;
                        switchPassengerScreen(passengerScreenMode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public void onAfterRideFeedbackSubmitted(final int givenRating) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterRideFeedbackSubmitted(givenRating);
            }
        });

    }


    public void afterRideFeedbackSubmitted(final int givenRating) {
        try {
            try {
                disableEmergencyModeIfNeeded(Data.autoData.getcEngagementId());
            } catch (Exception e) {
                e.printStackTrace();
            }


            ReferralActions.incrementTransactionCount(HomeActivity.this);
            userMode = UserMode.PASSENGER;

            switchUserScreen();

            if (givenRating >= 4 && Data.userData.getCustomerRateAppFlag() == 1) {
                rateAppPopup(Data.userData.getRateAppDialogContent());
            }
            firstTimeZoom = false;
            dropLocationSearchText = "";
			isPickupSet = false;

            slidingBottomPanel.getRequestRideOptionsFragment().setSelectedCoupon(null);


            Data.autoData.setLastRefreshLatLng(null);
            Data.autoData.setPickupSearchResult(null);
            Log.w("pickuplogging", "afterRideFeedbackSubmitted"+Data.autoData.getPickupLatLng());
            Data.autoData.setDropLatLng(null);
            Data.autoData.setDropAddress("");
            Data.autoData.setDropAddressId(0);
            Data.setRecentAddressesFetched(false);
            Data.autoData.clearRegionFares();
            if(getApiFindADriver().getParams() != null) {
				getApiFindADriver().getParams().clear();
			}

            if(!editTextBidValue.getText().toString().isEmpty()) {
                editTextBidValue.setText("");
            }
            confirmedScreenOpened = false;
            specialPickupScreenOpened = false;

            MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
            setUserData();

            resetPickupDropFeilds();
			removeP2DPolyline();

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dontCallRefreshDriver = false;
                    if(myLocation != null && mapStateListener != null && map != null) {
                        mapStateListener.touchMapExplicit();
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), MAX_ZOOM), getMapAnimateDuration(), null);
                        if(Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 0  || isNewUI) {
                            LatLng currentLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                            Log.w("pickuplogging", "afterRideFeedbackSubmitted delayed runnable"+currentLatLng);
                            Log.w("findADriverAndGeocode", "afterRideFeedback");
                            findADriverAndGeocode(currentLatLng, true, true, true);
                        }
						passengerScreenMode = P_INITIAL;
						switchPassengerScreen(passengerScreenMode);
                    }
                }
            }, 500);

            Utils.hideSoftKeyboard(HomeActivity.this, editTextRSFeedback);

            try {
                AppEventsLogger.newLogger(this).logPurchase(BigDecimal.valueOf(Data.autoData.getEndRideData().toPay), Currency.getInstance(Data.autoData.getEndRideData().getCurrency()));
            } catch (Exception e) {
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
                            getString(R.string.jugnoo_cash_added, getString(R.string.app_name)),
                            message,
                            getString(R.string.check_balance), getString(R.string.call_support),
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
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
                try {
                    if (Data.autoData.getAssignedDriverInfo() != null) {
                        passengerScreenMode = PassengerScreenMode.P_DRIVER_ARRIVED;
                        switchPassengerScreen(passengerScreenMode);
                    } else {
                        callAndHandleStateRestoreAPI(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private Dialog sosDialog;

    public void sosDialog(final Activity activity) {
        if (Data.userData.getEmergencyContactsList() != null) {
            sosDialog = new EmergencyDialog(activity, Data.autoData.getcEngagementId(), new EmergencyDialog.CallBack() {
                @Override
                public void onEnableEmergencyModeClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, EmergencyActivity.class);
                    intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                            EmergencyActivity.EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal());
                    intent.putExtra(Constants.KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
                    intent.putExtra(Constants.KEY_DRIVER_ID, Data.autoData.getAssignedDriverInfo().userId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    GAUtils.event(RIDES, GAAction.HELP + POPUP, ENABLE_EMERGENCY_MODE + CLICKED);
                }

                @Override
                public void onEmergencyModeDisabled(String engagementId) {
                    disableEmergencyMode(engagementId);
                }

                @Override
                public void onSendRideStatusClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, EmergencyActivity.class);
                    intent.putExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                            EmergencyActivity.EmergencyActivityMode.SEND_RIDE_STATUS.getOrdinal());
                    intent.putExtra(Constants.KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    GAUtils.event(RIDES, GAAction.HELP + POPUP, SEND_RIDE_STATUS + CLICKED);
                }

                @Override
                public void onInAppCustomerSupportClick(View view) {
                    if (Data.isFuguChatEnabled()) {
                        fuguCustomerHelpRides(true);
                    } else if (Data.isMenuTagEnabled(MenuInfoTags.EMAIL_SUPPORT)) {
                        activity.startActivity(new Intent(activity, SupportMailActivity.class));
                    } else {
                        Intent intent = new Intent(HomeActivity.this, SupportActivity.class);
                        intent.putExtra(KEY_ENGAGEMENT_ID, Integer.parseInt(Data.autoData.getcEngagementId()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                    GAUtils.event(RIDES, GAAction.HELP + POPUP, IN_APP_SUPPORT + CLICKED);
                }

                @Override
                public void onDialogClosed(View view) {
                }

                @Override
                public void onDialogDismissed() {

                }
            }).show(Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0));
        }
    }

    private final String FUGU_TAG_SOS = "SOS";

    public void fuguCustomerHelpRides(boolean fromSos) {
        try {
        	if(!fromSos && Data.isHippoTicketForRideEnabled(this)){
				HomeUtil.openHippoTicketForRide(this,
						Integer.parseInt(Data.autoData.getcEngagementId()),
						Integer.parseInt(Data.autoData.getcDriverId()));
			}
        	else if (!TextUtils.isEmpty(Data.autoData.getFuguChannelId())) {
                Data.autoData.getFuguTags().remove(FUGU_TAG_SOS);
                if (fromSos) {
                    Data.autoData.getFuguTags().add(FUGU_TAG_SOS);
                }
                ChatByUniqueIdAttributes chatAttr = new ChatByUniqueIdAttributes.Builder()
                        .setTransactionId(Data.autoData.getFuguChannelId())
                        .setUserUniqueKey(String.valueOf(Data.getFuguUserData().getUserId()))
                        .setChannelName(Data.autoData.getFuguChannelName())
                        .setTags(Data.autoData.getFuguTags())
                        .build();
                HippoConfig.getInstance().openChatByUniqueId(chatAttr);
            } else {
                HippoConfig.getInstance().openChat(HomeActivity.this, Data.CHANNEL_ID_FUGU_ISSUE_RIDE());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(HomeActivity.this, getString(R.string.something_went_wrong));
        }
    }

    private void dismissSOSDialog() {
        if (sosDialog != null && sosDialog.isShowing()) {
            sosDialog.dismiss();
        }
    }

    public static int localModeEnabled = -1;

    private void updateTopBar() {
        try {
            setTopBarTransNewUI();
            if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                    || PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                    || (PassengerScreenMode.P_RIDE_END == passengerScreenMode
                    && relativeLayoutRideEndWithImage.getVisibility() == View.GONE && relativeLayoutGreat.getVisibility() == View.GONE)) {
                int modeEnabled = Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
                if (modeEnabled == 1) {
                    topBar.textViewTitle.setText(getResources().getString(R.string.emergency_mode_enabled));

//                    topBar.textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)getResources().getDimensionPixelSize(R.dimen.text_size_30)*minRatio);
                    topBar.textViewTitle.setTextColor(getResources().getColor(R.color.red));
                    topBar.imageViewMenu.setImageResource(R.drawable.menu_icon_selector_emergency);
                    localModeEnabled = modeEnabled;
                    return;
                } else {
                    if (localModeEnabled == 1) {
                        EmergencyDisableDialog emergencyDisableDialog = new EmergencyDisableDialog(HomeActivity.this);
                        emergencyDisableDialog.show();
                    }
                    if (getStarSubscriptionCheckoutFragment() != null) {
                        topBar.textViewTitle.setText(getResources().getString(R.string.pay_online));
                    } else {
                        topBar.textViewTitle.setText(PassengerScreenMode.P_RIDE_END == passengerScreenMode ? R.string.rating_summary : R.string.rides);
                    }
                }
                localModeEnabled = modeEnabled;
            } else if(PassengerScreenMode.P_SEARCH == passengerScreenMode && isNewUI && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1){
                if(placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP) {
                    topBar.textViewTitle.setText(getResources().getString(R.string.drop_location));
                } else {
                    topBar.textViewTitle.setText(getResources().getString(R.string.pickup_location));
                }
            }else {
                Prefs.with(this).save(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
                if (confirmedScreenOpened) {
                    topBar.textViewTitle.setText(getResources().getString(R.string.confirmation));
                } else if (specialPickupScreenOpened) {
                    topBar.textViewTitle.setText(getResources().getString(R.string.choose_pickup));
                } else if (getStarSubscriptionCheckoutFragment() != null) {
                    topBar.textViewTitle.setText(getResources().getString(R.string.pay_online));
                } else {
                    if(scheduleRideOpen) {
                        if (Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null
                                && Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())) {
                            topBar.textViewTitle.setText(R.string.rentals);
                        } else if (Data.autoData.getServiceTypeSelected() != null && Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null && Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType())) {
                            topBar.textViewTitle.setText(R.string.out_station);
                        } else {
                            topBar.textViewTitle.setText(R.string.schedule_a_ride);
                        }
                    } else {
                        topBar.textViewTitle.setText(R.string.rides);
                    }

                }

                localModeEnabled = 0;
            }

//            topBar.textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) getResources().getDimensionPixelSize(R.dimen.text_size_40) * minRatio);
			setTopBarMenuIcon();
            topBar.textViewTitle.setTextColor(getResources().getColor(R.color.text_color));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTopBarMenuIcon(){
		topBar.imageViewMenu.setImageResource(rvRideTypes.getVisibility() == View.VISIBLE
				|| topBar.textViewTitle.getVisibility() == View.VISIBLE ?
				R.drawable.ic_menu_selector : R.drawable.ic_menu_home_new_selector);
	}

    public void setTopBarTransNewUI() {
		RelativeLayout.LayoutParams paramsInitial = (RelativeLayout.LayoutParams) initialLayout.getLayoutParams();
        if(((passengerScreenMode == P_INITIAL && rvRideTypes.getVisibility() == View.GONE)
				|| (passengerScreenMode == P_ASSIGNING && relativeLayoutAssigningDropLocationParent.getVisibility() == View.GONE))
				&& (Data.autoData.getBidInfos() == null || Data.autoData.getBidInfos().size() == 0)
				&& isNewUI && !confirmedScreenOpened && !scheduleRideOpen && !specialPickupScreenOpened) {
            topBar.topRl.setBackground(ContextCompat.getDrawable(this, R.color.transparent));
            topBar.imageViewShadow.setBackground(ContextCompat.getDrawable(this, R.color.transparent));
            topBar.textViewTitle.setVisibility(View.GONE);
			paramsInitial.topMargin = 0;
        } else {
            topBar.topRl.setBackground(ContextCompat.getDrawable(this, R.color.white));
            topBar.imageViewShadow.setBackground(ContextCompat.getDrawable(this, R.drawable.shadow_down));
            topBar.textViewTitle.setVisibility(View.VISIBLE);
			paramsInitial.topMargin = (int) (ASSL.Yscale() * 96F);
        }
		initialLayout.setLayoutParams(paramsInitial);
    }


    private void disableEmergencyModeIfNeeded(final String engagementId) {
        int modeEnabled = Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0);
        if (modeEnabled == 1) {
            disableEmergencyMode(engagementId);
        }
    }

    public void disableEmergencyMode(final String engagementId) {
        new ApiEmergencyDisable(this, new ApiEmergencyDisable.Callback() {
            @Override
            public void onSuccess() {
                updateTopBar();
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onRetry(View view) {
                disableEmergencyMode(engagementId);
            }

            @Override
            public void onNoRetry(View view) {

            }
        }).emergencyDisable(engagementId);
    }


    private boolean switchUICalledFromStateRestore;

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
                                        DialogPopup.showLoadingDialog(HomeActivity.this, getString(R.string.loading));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        int currentUserStatus = 2;
                        final PassengerScreenMode passengerScreenModeOld = passengerScreenMode;
                        final String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken,
                                currentUserStatus, getApiFindADriver(), getCurrentPlaceLatLng());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                	DialogPopup.dismissLoadingDialog();
                                    if (!placeAdded) {
                                        if (PassengerScreenMode.P_IN_RIDE != passengerScreenModeOld
                                                && PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                                                && Data.autoData.getDropLatLng() != null) {
                                            // havan karo yahan pe
                                        }
                                        if (resp.equalsIgnoreCase(Constants.REJECT_API)) {
                                            return;
                                        }
                                        switchUICalledFromStateRestore = true;
                                        startUIAfterGettingUserStatus();
                                        switchUICalledFromStateRestore = false;
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

    @Override
    public void onSkipClicked(boolean isPickup) {
        if(isPickup) {
            mIsPickup = true;
            Prefs.with(HomeActivity.this).save(Constants.SKIP_SAVE_PICKUP_LOCATION, true);
            pickupLocationMarker = map.addMarker(getStartPickupLocMarkerOptions(Data.autoData.getPickupLatLng(),
                    passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED || passengerScreenMode == PassengerScreenMode.P_IN_RIDE));
        } else {
            mIsPickup = false;
            Prefs.with(HomeActivity.this).save(Constants.SKIP_SAVE_DROP_LOCATION, true);
            setDropLocationMarker();
        }
    }

    @Override
    public void onSaveLocationClick(boolean isPickup) {
        if(isPickup) {
            mIsPickup = true;
            SearchResult searchResult = new SearchResult("", Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()),
                    "", Data.autoData.getPickupLatLng().latitude, Data.autoData.getPickupLatLng().longitude);
            openAddAddressActivity(searchResult);
        } else {
            mIsPickup = false;
            SearchResult searchResult = new SearchResult("", Data.autoData.getDropAddress(),
                    "", Data.autoData.getDropLatLng().latitude, Data.autoData.getDropLatLng().longitude);
            openAddAddressActivity(searchResult);
        }
    }

    /**
     *
     */
    private void openAddAddressActivity(final SearchResult searchResult) {
        Intent intent = new Intent(HomeActivity.this, AddPlaceActivity.class);
        intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
        intent.putExtra(Constants.KEY_DIRECT_CONFIRM, true);
        intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
        startActivityForResult(intent, searchResult.getPlaceRequestCode());
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
	@NotNull
	@Override
	public CoroutineContext getCoroutineContext() {
		return null;
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

    private DeepLinkAction deepLinkAction = new DeepLinkAction();

    private void openPushDialog() {
		try {
			dismissPushDialog(false);

			if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
				drawerLayout.closeDrawer(GravityCompat.START);
			}

			String pushDialogContent = Prefs.with(this).getString(Constants.SP_PUSH_DIALOG_CONTENT,
					Constants.EMPTY_JSON_OBJECT);
			JSONObject jObj = new JSONObject(pushDialogContent);
			if(jObj.optInt(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.REINVITE_USERS.getOrdinal()){
				pushDialog = null;

				String image = jObj.optString(Constants.KEY_IMAGE, jObj.optString(Constants.KEY_PICTURE, ""));

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ReinviteFriendsDialog reinviteFriendsDialog = ReinviteFriendsDialog.newInstance(image,
						jObj.optString(Constants.KEY_MESSAGE, ""));
				reinviteFriendsDialog.show(ft, ReinviteFriendsDialog.class.getSimpleName());
				Prefs.with(this).save(SP_PUSH_DIALOG_CONTENT, EMPTY_JSON_OBJECT);
			} else {

				PushDialog dialog = new PushDialog(HomeActivity.this, new PushDialog.Callback() {
					@Override
					public void onButtonClicked(int deepIndex, String url, int restaurantId) {
						if ("".equalsIgnoreCase(url)) {
							Data.deepLinkIndex = deepIndex;
							Prefs.with(HomeActivity.this).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, "" + restaurantId);
							deepLinkAction.openDeepLink(HomeActivity.this, getCurrentPlaceLatLng());
						} else {
							Utils.openUrl(HomeActivity.this, url);
						}
					}
				}).show(pushDialogContent);
				if (dialog != null) {
					pushDialog = dialog;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void dismissPushDialog(boolean clearDialogContent) {
        if (pushDialog != null) {
            pushDialog.dismiss(clearDialogContent);
            pushDialog = null;
        }
    }

    private boolean stopDefaultCoupon;
    public void callFindADriverAfterCouponSelect(){
        if(confirmedScreenOpened || isNewUI()) {
            if (Data.autoData.showRegionSpecificFare()) {
                HashMap<String, String> params = new HashMap<>();
                if (getApiFindADriver().getParams() != null) {
                    params.put(KEY_RIDE_DISTANCE, getApiFindADriver().getParams().get(KEY_RIDE_DISTANCE));
                    params.put(KEY_RIDE_TIME, getApiFindADriver().getParams().get(KEY_RIDE_TIME));
                }
                PromoCoupon pc = slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon();
                if (pc != null && pc.getId() > 0) {
                    params.put(pc instanceof CouponInfo ? Constants.KEY_COUPON_TO_APPLY : Constants.KEY_PROMO_TO_APPLY, String.valueOf(pc.getId()));
                }
				stopDefaultCoupon = true;
                DialogPopup.showLoadingDialog(this, getString(R.string.loading));
                findDriversETACall(false, false, false, params);
            } else {
                fareEstimatBeforeRequestRide();
            }
        }
    }

    private ApiFareEstimate apiFareEstimate = null;
    private void fareEstimatBeforeRequestRide() {
        jugnooPoolFareId = 0;
        if (Data.autoData.getDropLatLng() != null) {
        	Region region = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
            int isPooled = region.getRideType() == RideTypeValue.POOL.getOrdinal() ? 1 : 0;
            boolean callFareEstimate = region.getRideType() == RideTypeValue.POOL.getOrdinal()
					|| region.getShowFareEstimate() == 1
					|| region.getFareMandatory() == 1;
            PromoCoupon promoCouponSelected = null;
            try {
                promoCouponSelected = slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon();
            } catch (Exception ignored) {}
            if(apiFareEstimate == null) {
				apiFareEstimate = new ApiFareEstimate(HomeActivity.this, new ApiFareEstimate.Callback() {
					@Override
					public void onSuccess(List<LatLng> list, String distanceText,
										  String timeText, double distanceValue, double timeValue, PromoCoupon promoCoupon) {
						mapTouched = false;
						latLngBoundsBuilderPool = new LatLngBounds.Builder();
						callInsertData();

						polylineOptionsP2D = new PolylineOptions();
						polylineOptionsP2D.width(ASSL.Xscale() * 7).color(getResources().getColor(R.color.google_path_polyline_color)).geodesic(true);
						for (int z = 0; z < list.size(); z++) {
							polylineOptionsP2D.add(list.get(z));
							latLngBoundsBuilderPool.include(list.get(z));
						}

						if (polylineP2D != null) {
							polylineP2D.remove();
						}
						polylineP2D = map.addPolyline(polylineOptionsP2D);

						pickupLocationEtaMarker();

						if(dropLocationMarker != null){
							dropLocationMarker.remove();
						}
						MarkerOptions poolMarkerOptionEnd = new MarkerOptions();
						poolMarkerOptionEnd.title("End");
						poolMarkerOptionEnd.position(Data.autoData.getDropLatLng());
						poolMarkerOptionEnd.zIndex(HOME_MARKER_ZINDEX);
						poolMarkerOptionEnd.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmapEnd(HomeActivity.this
						)));
						//map.addMarker(poolMarkerEnd);
						dropLocationMarker = map.addMarker(poolMarkerOptionEnd);

						poolPathZoomAtConfirm();
						closeFabView();

						if (Data.autoData.showRegionSpecificFare() || !callFareEstimate) {
							HashMap<String, String> params = new HashMap<>();
							params.put(Constants.KEY_RIDE_DISTANCE, "" + (distanceValue / 1000D));
							params.put(Constants.KEY_RIDE_TIME, "" + (timeValue / 60D));
							if (promoCoupon != null && promoCoupon.getId() != -1) {
								params.put(promoCoupon instanceof CouponInfo ? Constants.KEY_COUPON_TO_APPLY : Constants.KEY_PROMO_TO_APPLY, String.valueOf(promoCoupon.getId()));
							}
							putSelectedPickupAddress(params);
							putSelectedDropAddress(params);
							findDriversETACall(false, false, false, params);
						}
					}

					@Override
					public void onRetry() {
						closeFabView();
					}

					@Override
					public void onNoRetry() {
						closeFabView();
					}

					@Override
					public void onFareEstimateSuccess(String currency, String minFare, String maxFare, double convenienceCharge,
													  double tollCharge) {
						closeFabView();
						textViewTotalFareValue.setText(Utils.formatCurrencyValue(currency, minFare) + " - " +
								Utils.formatCurrencyValue(currency, maxFare));
						if (Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE, 0) == 1) {
							textViewTotalFareValue.append(" ");
							textViewTotalFareValue.append(getString(R.string.bracket_in_format, currency));
						}
						if (convenienceCharge > 0) {
							textViewIncludes.setVisibility(View.VISIBLE);
							textViewIncludes.setText(getString(R.string.convenience_charge_format, Utils.formatCurrencyValue(currency, convenienceCharge)));
						} else {
							textViewIncludes.setVisibility(View.GONE);
						}
						setTextTollCharges(currency, tollCharge);
						callInsertData();
					}

					private void setTextTollCharges(String currency, double tollCharge) {
						if (tollCharge > 0) {
							textViewIncludes.setVisibility(View.VISIBLE);
							if (textViewIncludes.getText().length() > 0)
								textViewIncludes.append("\n");
							textViewIncludes.append(getString(R.string.expected_toll_charge) + " " + Utils.formatCurrencyValue(currency, tollCharge));
						}
					}
					private void closeFabView(){
						if(fabViewTest != null) {
							fabViewTest.closeMenu();
						}
					}

					@Override
					public void onPoolSuccess(String currency, double fare, double rideDistance, String rideDistanceUnit,
											  double rideTime, String rideTimeUnit, final int poolFareId,
											  double convenienceCharge, String text, double tollCharge) {
						Log.v("Pool Fare value is ", "--> " + fare);
						jugnooPoolFareId = poolFareId;

						textViewTotalFareValue.setText(Utils.formatCurrencyValue(currency, fare));
						if (Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE, 0) == 1) {
							textViewTotalFareValue.append(" ");
							textViewTotalFareValue.append(getString(R.string.bracket_in_format, currency));
						}

						textViewIncludes.setVisibility(View.VISIBLE);
						textViewIncludes.setText(text);
						setTextTollCharges(currency, tollCharge);
						closeFabView();
                        callInsertData();
					}

					@Override
					public void onDirectionsFailure() {
						jugnooPoolFareId = 0;
						textViewTotalFareValue.setText("-");

						textViewIncludes.setVisibility(View.GONE);
						textViewIncludes.setText("");
						closeFabView();
						if(Data.autoData != null) {
							Data.autoData.clearRegionFares();
							if(vehiclesTabAdapterConfirmRide != null) {
								vehiclesTabAdapterConfirmRide.setList(Data.autoData.getRegions());
							}
							if(getApiFindADriver().getParams() != null) {
								getApiFindADriver().getParams().clear();
							}
						}
					}

					@Override
					public void onFareEstimateFailure() {
						jugnooPoolFareId = 0;
						textViewTotalFareValue.setText("-");

						textViewIncludes.setVisibility(View.GONE);
						textViewIncludes.setText("");
						closeFabView();
					}
				});
			}

			apiFareEstimate.getDirectionsAndComputeFare(Data.autoData.getPickupLatLng(), Data.autoData.getDropLatLng(), isPooled, Data.autoData.showRegionSpecificFare() ?false: callFareEstimate,
					region, promoCouponSelected, null, MapsApiSources.CUSTOMER_FARE_ESTIMATE_HOME);
        } else {
            textViewDestSearch.setText(getResources().getString(R.string.destination_required));
            textViewDestSearch.setTextColor(getResources().getColor(R.color.red));
            textViewDestSearchNew.setText(getResources().getString(R.string.destination_required));
            textViewDestSearchNew.setTextColor(getResources().getColor(R.color.red));

            ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
            int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
            if (index == 1 && Data.autoData.getDropLatLng() == null) {
                translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
            }
            if (Data.autoData.getDropLatLng() == null) {
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                textViewDestSearch.startAnimation(shake);
                textViewDestSearchNew.startAnimation(shake);
            }
            try {
                fabViewTest.closeMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callInsertData() {
        if(!Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()).equalsIgnoreCase("Current Location")) {
            createDataAndInsert(0);
        }
        createDataAndInsert(1);
    }

    public void pickupLocationEtaMarker() {
        try {
            pickupLocationMarker.remove();
        } catch (Exception e) {
        }
        MarkerOptions poolMarkerOptionStart = new MarkerOptions();
        poolMarkerOptionStart.title("Start");
        poolMarkerOptionStart.position(Data.autoData.getPickupLatLng());
        poolMarkerOptionStart.zIndex(HOME_MARKER_ZINDEX);
//                poolMarkerOptionStart.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(HomeActivity.this,
//                        assl, R.drawable.pin_ball_start)));
        poolMarkerOptionStart.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                .getTextAssignBitmap(HomeActivity.this,
                        slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getEta(),
                        getResources().getDimensionPixelSize(R.dimen.text_size_24))));
        if(map!= null) {
            pickupLocationMarker = map.addMarker(poolMarkerOptionStart);
        }
    }

    private void poolPathZoomAtConfirm() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (latLngBoundsBuilderPool != null) {
                        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapLatLngBoundsCreator.createBoundsWithMinDiagonal(latLngBoundsBuilderPool, FIX_ZOOM_DIAGONAL),
                                (int) (minRatio * MAP_PADDING)),
                                getMapAnimateDuration(), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }


    private ApiFetchWalletBalance apiFetchWalletBalance = null;

    private void fetchWalletBalance(final Activity activity, boolean ignoreTimeCheck) {
        try {
            if (apiFetchWalletBalance == null) {
                apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
                        setUserData();
                    }

                    @Override
                    public void onFailure() {
                        try {
                            setUserData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onRetry(View view) {
                    }

                    @Override
                    public void onNoRetry(View view) {
                    }
                });
            }
            long lastFetchWalletBalanceCall = Prefs.with(activity).getLong(SPLabels.CHECK_BALANCE_LAST_TIME, (System.currentTimeMillis() - (2 * FETCH_WALLET_BALANCE_REFRESH_TIME)));
            long lastCallDiff = System.currentTimeMillis() - lastFetchWalletBalanceCall;
            if (ignoreTimeCheck || lastCallDiff >= FETCH_WALLET_BALANCE_REFRESH_TIME) {
                apiFetchWalletBalance.getBalance(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadContactsApi(final boolean fromLogin) {
        HashMap<String, String> params = new HashMap<>();
        if (MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(this, getString(R.string.loading));
            params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
            if (fromLogin) {
                params.put(KEY_ENGAGEMENT_ID, "");
                params.put(KEY_USER_RESPONSE, "-2");
                params.put(KEY_IS_LOGIN_POPUP, "1");
            } else {
                params.put(KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
                params.put(KEY_USER_RESPONSE, "-1");
            }
            Log.i("access_token and session_id", Data.userData.accessToken + ", " + Data.autoData.getcSessionId() + ", " + Data.autoData.getcEngagementId());

            Log.i("params request_dup_registration", "=" + params);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().referAllContacts(params, new Callback<SettleUserDebt>() {
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
                                Data.autoData.setReferAllStatus(-1);
                            }
                        } else {
                            //Prefs.with(HomeActivity.this).save(SPLabels.UPLOAD_CONTACT_NO_THANKS, 0);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        //DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
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


    public void skipFeedbackForCustomerAsync(final Activity activity, final String engagementId) {
        try {
            final HashMap<String, String> params = new HashMap<>();
            params.put("access_token", Data.userData.accessToken);
            params.put("engagement_id", engagementId);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().skipRatingByCustomer(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    Log.i(TAG, "skipRatingByCustomer response = " + response);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "skipRatingByCustomer error=" + error.toString());
                }
            });

            MyApplication.getInstance().getDatabase2().insertPendingAPICall(activity,
                    PendingCall.SKIP_RATING_BY_CUSTOMER.getPath(), params);

            try {
                Data.autoData.getDriverInfos().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            HomeActivity.feedbackSkipped = true;
            afterRideFeedbackSubmitted(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void submitFeedbackToDriverAsync(final Activity activity, final String engagementId, final String ratingReceiverId,
                                            final int givenRating, final String feedbackText, final String feedbackReasons) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                //DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

                params.put("access_token", Data.userData.accessToken);
                params.put(KEY_GIVEN_RATING, String.valueOf(givenRating));
                params.put("engagement_id", engagementId);
                params.put("driver_id", ratingReceiverId);
                params.put("feedback", feedbackText);

				if (badgesAdapter != null && Data.autoData.getFeedBackInfoRatingData() != null) {
					String imgBadgeIds = badgesAdapter.getClickedBadgesIds();
					params.put("image_badge_ids", imgBadgeIds);
					params.put("text_badge_ids", feedbackReasons);
				}
                params.put("feedback_reasons", feedbackReasons);
                Log.i("params", "=" + params);


                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().rateTheDriver(params, new Callback<SettleUserDebt>() {
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

                                    if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                        getHandler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                submitFeedbackToInitial(givenRating);
                                            }
                                        }, 3000);
                                    } else if (Data.autoData.getRideEndGoodFeedbackViewType() == RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal()
                                            || givenRating == 1) {
                                        submitFeedbackToInitial(givenRating);
                                    }


                                    try {
                                        Data.autoData.getDriverInfos().clear();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        relativeLayoutGreat.setVisibility(View.GONE);
                                    }
                                } else {
                                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                                    relativeLayoutGreat.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            relativeLayoutGreat.setVisibility(View.GONE);
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "rateTheDriver error=" + error.toString());
                        relativeLayoutGreat.setVisibility(View.GONE);
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                    }
                });
            } else {
                DialogPopup.dialogNoInternet(HomeActivity.this,
                        activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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

//        if(givenRating == 5) {
//            getRateAppDialog().thumbsUpClickedAPI();
//        }
    }

    private void submitFeedbackToInitial(int givenRating) {
        relativeLayoutGreat.setVisibility(View.GONE);
        try {
            Data.autoData.getDriverInfos().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        afterRideFeedbackSubmitted(givenRating);
    }

    @Override
    public void onTextChange(String text) {
        if ((PassengerScreenMode.P_ASSIGNING == passengerScreenMode
                || PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode
                || (P_INITIAL == passengerScreenMode
                && placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP))
                && !"".equalsIgnoreCase(text)) {
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
    public void onPlaceClick(SearchResult autoCompleteSearchResult) {
        Log.d("RentalStationClick" , " onPlaceClick");

        if (P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
            if(scheduleRideOpen){
                return;
            }
            zoomAfterFindADriver = false;
            if (!isPoolRideAtConfirmation()) {
                if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                    textViewInitialSearch.setText(autoCompleteSearchResult.getNameForText(this));
                    textViewInitialSearchNew.setText(autoCompleteSearchResult.getNameForText(this));
                    tvPickupRentalOutstation.setText(autoCompleteSearchResult.getNameForText(this));
                } else if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP) {
                    textViewDestSearch.setText(autoCompleteSearchResult.getNameForText(this));
                    textViewDestSearchNew.setText(autoCompleteSearchResult.getNameForText(this));
                    dropAddressName = autoCompleteSearchResult.getNameForText(this);
                    setPickupLocationInitialUI();
                }
                searchedALocation = true;
            }
            if(isNewUiWithDropAtConfirmation()) {
                if(currentLocationMarker != null) {
                    currentLocationMarker.remove();
                }
                fareEstimatBeforeRequestRide();
            }
        } else if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
			tvDropAssigning.setText(autoCompleteSearchResult.getNameForText(this));
            dropAddressName = autoCompleteSearchResult.getNameForText(this);
        } else if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
        }
    }

    @Override
    public void onPlaceSearchPre() {
        if (P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
            if(scheduleRideOpen){
                return;
            }
            if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                progressBarInitialSearch.setVisibility(View.VISIBLE);
                progressBarInitialSearch.spin();
            }
        }

        Log.e("onPlaceSearchPre", "=");
    }

    private void setSearchResultToPickupCase(SearchResult searchResult) {
        try {
            if (searchResult != null && !TextUtils.isEmpty(searchResult.getAddress())) {
                textViewInitialSearch.setText(searchResult.getNameForText(this));
                textViewInitialSearchNew.setText(searchResult.getNameForText(this));
                tvPickupRentalOutstation.setText(searchResult.getNameForText(this));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.getLatLng(), MAX_ZOOM), MAP_ANIMATE_DURATION, null);
                setPickupAddressZoomedOnce = true;
                mapTouched = true;
                updateSavedAddressLikeButton(searchResult.getLatLng(), true);
                Data.autoData.setPickupSearchResult(searchResult);
                Log.w("pickuplogging", "setSearchResultToPickupCase"+Data.autoData.getPickupLatLng());

                try {
                    Log.e("searchResult.getThirdPartyAttributions()", "=" + searchResult.getThirdPartyAttributions());
                    if (TextUtils.isEmpty(searchResult.getThirdPartyAttributions())) {
                        relativeLayoutGoogleAttr.setVisibility(View.GONE);
                    } else {
                        relativeLayoutGoogleAttr.setVisibility(View.VISIBLE);
                        textViewGoogleAttrText.setText(Html.fromHtml(searchResult.getThirdPartyAttributions().toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addUserCurrentLocationAddressMarker();
                if(Prefs.with(HomeActivity.this).getInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, 1) == 0 || isNewUI) {
                    Log.w("findADriverAndGeocode", "setSearchResultToPickupCase");
                    findADriverAndGeocode(Data.autoData.getPickupLatLng(), true, true, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean findADriverAndGeocode(LatLng latLng, boolean mapTouched, boolean touchCalled, boolean releaseCalled) {
        boolean refresh = false;
        if (Data.autoData.getLastRefreshLatLng() == null
                || (MapUtils.distance(Data.autoData.getLastRefreshLatLng(), latLng) > MIN_DISTANCE_FOR_REFRESH)) {
            Data.autoData.setLastRefreshLatLng(latLng);
            refresh = true;
        }
        if (P_INITIAL == passengerScreenMode && !confirmedScreenOpened && !specialPickupSelected) {
            if (refresh && touchCalled && releaseCalled) {
                Log.w("getAddressAsync", "findADriverAndGeocode");
                getAddressAsync(latLng, getInitialPickupTextView(), null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
            }
            if ((refresh && mapTouched) || addressPopulatedFromDifferentOffering) {
                addressPopulatedFromDifferentOffering = false;
                if(Data.autoData.getDropLatLng() != null && passengerScreenMode == P_INITIAL && (confirmedScreenOpened || isNewUI())){
					fareEstimatBeforeRequestRide();
				} else {
					callMapTouchedRefreshDrivers(null);
				}
            }
        }
        return refresh;
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult, PlaceSearchListFragment.PlaceSearchMode placeSearchMode) {

        try {

            PlaceSearchListFragment.PlaceSearchMode searchMode = placeSearchMode==null?this.placeSearchMode:placeSearchMode;
            if (P_INITIAL == passengerScreenMode
                    || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
                isPickupSet = true;


                if(scheduleRideOpen){
                    if(getScheduleRideFragment() != null){
                        getScheduleRideFragment().searchResultReceived(searchResult, searchMode);
                    }
                }
				removeP2DPolyline();
				if (searchMode == PlaceSearchListFragment.PlaceSearchMode.PICKUP) {
                    passengerScreenMode = P_INITIAL;
                    progressBarInitialSearch.stopSpinning();
                    progressBarInitialSearch.setVisibility(View.GONE);
                    passengerScreenMode = P_INITIAL;

					if (map != null && searchResult != null) {
						setSearchResultToPickupCase(searchResult);
						GAUtils.event(RIDES, HOME, PICKUP + LOCATION + ENTERED);
					}
                    switchPassengerScreen(passengerScreenMode);
                } else if (searchMode == PlaceSearchListFragment.PlaceSearchMode.DROP) {

                    if (Data.autoData.getDropLatLng() == null) {
                        translateViewBottom(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutDestSearchBar, true, false);
                        translateViewTop(((ViewGroup) relativeLayoutDestSearchBar.getParent()), relativeLayoutInitialSearchBar, false, false);
                    }
                    Data.autoData.setDropLatLng(searchResult.getLatLng());
                    Data.autoData.setDropAddress(searchResult.getAddress());
                    Data.autoData.setDropAddressId(searchResult.getId());
                    relativeLayoutInitialSearchBar.setBackgroundResource(R.drawable.background_white_rounded_bordered);
                    imageViewDropCross.setVisibility(View.VISIBLE);
                    imageViewDropCrossNew.setVisibility(View.VISIBLE);
                    setLikeDropVisibilityAndBG();

                    // Save Last 3 Destination...
                    saveLastDestinations(searchResult);

                    passengerScreenMode = P_INITIAL;
                    textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color));
                    textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color));
                    switchPassengerScreen(passengerScreenMode);

                    if (!scheduleRideOpen) {
                        if ((slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal() &&
                                shakeAnim > 0 && !updateSpecialPickupScreen())
                                || (!specialPickupScreenOpened && !confirmedScreenOpened && !isNewUI() &&
								(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getShowFareEstimate() == 1
										|| slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getFareMandatory() == 1))) {
                            textViewTotalFareValue.setText("");
                            imageViewRideNow.performClick();
                        } else if(!specialPickupScreenOpened && !confirmedScreenOpened && !isNewUI()) {
                            findDriversETACall(false, false, false, null);
                        }
                    }
                    GAUtils.event(RIDES, HOME, DESTINATION + LOCATION + ENTERED);
                }
            } else if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
                saveLastDestinations(searchResult);
                sendDropLocationAPI(HomeActivity.this, searchResult.getLatLng(),
                        getPlaceSearchListFragment(passengerScreenMode).getProgressBarSearch(), false, searchResult.getAddress(), 0);
            } else if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                    || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                    || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {

            	if(Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null
						&& Data.autoData.getAssignedDriverInfo().getFareMandatory() == 1) {
					EditDropConfirmation.INSTANCE.fareEstimateAndConfirm(this, Integer.parseInt(Data.autoData.getcEngagementId()),
							Data.autoData.getPickupLatLng(), Data.autoData.getPickupAddress(Data.autoData.getPickupLatLng()),
							searchResult.getLatLng(), searchResult.getAddress(), searchResult.getName(),
							Data.autoData.getAssignedDriverInfo().getCurrency());
				} else {
					updateDropToUIAndServerApi(searchResult, 0);
				}
            }

            Log.e("onPlaceSearchPost", "=" + searchResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDropToUIAndServerApi(SearchResult searchResult, int poolFareId){
		saveLastDestinations(searchResult);

		textViewFinalDropLocationClick.setText(searchResult.getNameForText(this));
		dropAddressName = searchResult.getNameForText(this);

		sendDropLocationAPI(HomeActivity.this, searchResult.getLatLng(),
				getPlaceSearchListFragment(PassengerScreenMode.P_REQUEST_FINAL).getProgressBarSearch(), true,
				searchResult.getAddress(), poolFareId);
		if (PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
			GAUtils.event(RIDES, RIDE + IN_PROGRESS, DESTINATION + UPDATED);
		}
	}

	public void removeP2DPolyline() {
		polylineOptionsP2D = null;
		if(polylineP2D != null){
			polylineP2D.remove();
		}
		polylineP2D = null;
	}

	@Override
    public void onPlaceSearchError() {
        if (P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) {
            if(scheduleRideOpen){

                return;
            }
            if (placeSearchMode == PlaceSearchListFragment.PlaceSearchMode.DROP) {
                textViewDestSearch.setText("");
                textViewDestSearchNew.setText("");
                dropAddressName = "";
            } else {
                textViewInitialSearch.setText("");
                textViewInitialSearchNew.setText("");
                tvPickupRentalOutstation.setText("");
            }
        } else if (PassengerScreenMode.P_ASSIGNING == passengerScreenMode) {
			tvDropAssigning.setText("");
            dropAddressName = "";
        } else if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
            textViewFinalDropLocationClick.setText("");
            dropAddressName = "";
        }

        Log.e("onPlaceSearchError", "=");
    }

    @Override
    public void onPlaceSaved() {
        if((P_INITIAL == passengerScreenMode
                || PassengerScreenMode.P_SEARCH == passengerScreenMode) && scheduleRideOpen){

            return;
        }
        placeAdded = true;
    }

    @Override
    public void onNotifyDataSetChanged(int count) {

    }

	@Override
	public void onSetLocationOnMapClicked() {

	}

	private void saveLastDestinations(SearchResult searchResult) {
        try {
            lastDestination.clear();
            lastDestination.addAll(fetchLastLocations(SPLabels.LAST_DESTINATION));
            if (lastDestination.size() == 0) {
                if (!addressMatchedWithSavedAddresses(searchResult.getName())) {
                    lastDestination.add(0, new SearchResult(searchResult.getName(), searchResult.getAddress(), searchResult.getPlaceId()
                            , searchResult.getLatLng().latitude, searchResult.getLatLng().longitude));
                }
            } else {
                boolean isSame = false;
                for (int i = 0; i < lastDestination.size(); i++) {
                    if (searchResult.getName().equalsIgnoreCase(lastDestination.get(i).getName())
                            && searchResult.getAddress().equalsIgnoreCase(lastDestination.get(i).getAddress())) {
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    if (!addressMatchedWithSavedAddresses(searchResult.getName())) {
                        lastDestination.add(0, new SearchResult(searchResult.getName(), searchResult.getAddress(), searchResult.getPlaceId()
                                , searchResult.getLatLng().latitude, searchResult.getLatLng().longitude));
                    }
                }
                if (lastDestination.size() > 3) {
                    lastDestination.remove(3);
                }
                Log.v("size of last Destination", "---> " + lastDestination.size());
            }
            String tempDest = new Gson().toJson(lastDestination);
            Prefs.with(HomeActivity.this).save(SPLabels.LAST_DESTINATION, tempDest);

            // Save Entered Destination for 30 min or till Ride End...
            String strResult = new Gson().toJson(searchResult, SearchResult.class);
            Prefs.with(HomeActivity.this).save(SPLabels.ENTERED_DESTINATION, strResult);
            setEnteredDestination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PaytmRechargeDialog paytmRechargeDialog;

    private void openPaytmRechargeDialog() {
        try {
            if (Data.userData.getPaytmRechargeInfo() != null) {
                if (paytmRechargeDialog != null
                        && paytmRechargeDialog.getDialog() != null
                        && paytmRechargeDialog.getDialog().isShowing()) {
                    paytmRechargeDialog.updateDialogDataAndContent(Data.userData.getPaytmRechargeInfo().getTransferId(),
                            Data.userData.getPaytmRechargeInfo().getTransferSenderName(),
                            Data.userData.getPaytmRechargeInfo().getTransferPhone(),
                            Data.userData.getPaytmRechargeInfo().getTransferAmount());
                } else {
                    paytmRechargeDialog = new PaytmRechargeDialog(HomeActivity.this,
                            Data.userData.getPaytmRechargeInfo().getTransferId(),
                            Data.userData.getPaytmRechargeInfo().getTransferSenderName(),
                            Data.userData.getPaytmRechargeInfo().getTransferPhone(),
                            Data.userData.getPaytmRechargeInfo().getTransferAmount(),
                            new PaytmRechargeDialog.Callback() {
                                @Override
                                public void onOk() {
                                    if (Data.userData != null) {
                                        Data.userData.setPaytmRechargeInfo(null);
                                        Prefs.with(HomeActivity.this).save(SPLabels.CHECK_BALANCE_LAST_TIME,
                                                (System.currentTimeMillis() - (2 * FETCH_WALLET_BALANCE_REFRESH_TIME)));
                                        fetchWalletBalance(HomeActivity.this, true);
                                    }
                                }

                                @Override
                                public void onCancel() {
                                    if (Data.userData != null) {
                                        Data.userData.setPaytmRechargeInfo(null);
                                    }
                                }
                            });
                    paytmRechargeDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Animation bounceScale;

    private Animation getBounceScale() {
        if (bounceScale == null) {
            bounceScale = AnimationUtils.loadAnimation(this, R.anim.bounce_scale);
        }
        return bounceScale;
    }

    public boolean setVehicleTypeSelected(int position, boolean userClicked, boolean firstTime) {
        boolean changed = false;

        int oldVehicleType = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getVehicleType();
        int oldOperatorId = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOperatorId();
        int oldRegionId = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionId();
        int oldRideType = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType();
        slidingBottomPanel.getRequestRideOptionsFragment().setRegionSelected(position);
        Region regionSelected = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected();
        int newVehicleType = regionSelected.getVehicleType();
        slidingBottomPanel.getRequestRideOptionsFragment().updatePaymentOption();
        if(isNewUI) {
            relativeLayoutTotalFare.setVisibility(View.GONE);
            linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);
            tvTermsAndConditions.setVisibility(View.GONE);
            if(regionSelected.getReverseBid() == 1) {
				showReverseBidField(regionSelected);
				relativeLayoutOfferConfirm.setVisibility(View.GONE);
                boolean isCashOnly = true;
                if (MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas().size() > 0) {
                    for (PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas()) {
                        if (paymentModeConfigData.getPaymentOption() != PaymentOption.CASH.getOrdinal()
                                && paymentModeConfigData.getEnabled() == 1
                                && !regionSelected.getRestrictedPaymentModes().contains(paymentModeConfigData.getPaymentOption())
                                && paymentModeConfigData.getPaymentOption() != 0) {
                            isCashOnly = false;
                        }
                    }
                }
                if (isCashOnly) {
                    linearLayoutPaymentModeConfirm.setVisibility(View.GONE);
                }
            } else {
                linearLayoutBidValue.setVisibility(View.GONE);
                relativeLayoutOfferConfirm.setVisibility(View.VISIBLE);
                linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);

                if (regionSelected.getRegionFare() != null
                        && regionSelected.getFareMandatory() == 1) {
//                    tvTermsAndConditions.setVisibility(View.VISIBLE);
                }
            }
            setPickupLocationInitialUI();
        } else {
            linearLayoutBidValue.setVisibility(View.GONE);
            linearLayoutPaymentModeConfirm.setVisibility(View.VISIBLE);
            if(!Data.autoData.showRegionSpecificFare()) {
                relativeLayoutTotalFare.setVisibility(View.VISIBLE);
            }
            relativeLayoutOfferConfirm.setVisibility(View.VISIBLE);
        }

        if(regionSelected.getRideType()== RideTypeValue.BIKE_RENTAL.getOrdinal()) {
        	relativeLayoutDestSearchBar.setVisibility(View.GONE);
			relativeLayoutDestSearchBarNew.setVisibility(View.GONE);

			View viewDash = findViewById(R.id.iv2NewUIDropDashedLine);
			if(viewDash != null){
				viewDash.setVisibility(View.GONE);
			}
			View viewDrop = findViewById(R.id.iv3NewUIDropMark);
			if(viewDrop != null){
				viewDrop.setVisibility(View.GONE);
			}
        } else {
            if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())
                    || Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType())) {
                relativeLayoutSearchContainer.setVisibility(View.GONE);
            }
            if(isNewUI) {
                relativeLayoutDestSearchBarNew.setVisibility(View.VISIBLE);
            } else {
                relativeLayoutDestSearchBar.setVisibility(View.VISIBLE);
            }

			View viewDash = findViewById(R.id.iv2NewUIDropDashedLine);
			if(viewDash != null){
                if((Data.autoData.getDropLatLng() == null || Data.autoData.getDropAddress() == null)
                        && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1 && !isPickupSet) {
                    setHeightDropAddress(0); // large
                } else {
                    setHeightDropAddress(1); //Normal
                }
			}
			View viewDrop = findViewById(R.id.iv3NewUIDropMark);
			if(viewDrop != null){
				viewDrop.setVisibility(View.VISIBLE);
			}
        }
        if(passengerScreenMode == P_INITIAL && (confirmedScreenOpened || isNewUI) && Data.autoData.getPickupLatLng() != null) {
            pickupLocationEtaMarker();
        }
        if(oldRegionId != regionSelected.getRegionId()) {
            mNotes = "";
        }
		ArrayList<Region> regions = Data.autoData.getRegions();
        if((confirmedScreenOpened || isNewUI) && vehiclesTabAdapterConfirmRide!=null){
            vehiclesTabAdapterConfirmRide.setList(regions);
        }
        if (regions.size() == 1) {
            imageViewRideNow.setImageDrawable(regionSelected.getVehicleIconSet().getRequestSelector(this));
        } else {
            if (regionSelected.getOperatorId() != oldOperatorId
                    || !regionSelected.getVehicleType().equals(oldVehicleType)
                    || !regionSelected.getRegionId().equals(oldRegionId)
                    ) {
                if (oldRideType == RideTypeValue.POOL.getOrdinal()
                        && (textViewDestSearch.getText().toString()
                        .equalsIgnoreCase(getResources().getString(R.string.enter_destination)) || textViewDestSearchNew.getText().toString()
                        .equalsIgnoreCase(getResources().getString(R.string.enter_destination)))) {
                    textViewDestSearch.setText("");
                    textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color));
                    textViewDestSearchNew.setText("");
                    textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color));
                }
                setRegionUI(firstTime);
                changed = true;
            } else {
                if((!confirmedScreenOpened && !firstTime) && !isNewUI){
                    if (getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    } else {
                        getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
            }
        }

        //update service types patti as it depends on region selected
        setServiceTypeAdapter(true);

        addUserCurrentLocationAddressMarker();
        updateFareEstimateHoverButton();
        if(changed){
			zoomToCurrentLocationWithOneDriver(Data.autoData.getPickupLatLng() != null ? Data.autoData.getPickupLatLng() : getDeviceLocation());
		}

        // This code checks if the current Coupon selected is valid for the new Vehicle Type and if not it then checks for a default coupon
        // if it exists for that vehicle type, if it cannot find anything it then sets the coupon to null

        PromoCoupon selectedCoupon = slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon();


        if (!stopDefaultCoupon && !(userClicked && oldVehicleType == newVehicleType)) {//do not change promo if user clicked on same vehicle Type
            if (selectedCoupon == null || selectedCoupon.getId() <= 0
					|| (!selectedCoupon.isVehicleTypeExists(getVehicleTypeSelected(), getOperatorIdSelected()) || !checkCouponDropValidity(selectedCoupon))) {
                PromoCoupon defaultCoupon = Data.userData.getDefaultCoupon(getVehicleTypeSelected(), getOperatorIdSelected(), HomeActivity.this);

                if (defaultCoupon != null) {
                    slidingBottomPanel.getRequestRideOptionsFragment().setSelectedCoupon(defaultCoupon);

                    //to call find a driver again if previous find a driver was called without coupon selected or with different coupon
                    String previousPromoId = "";
                    if(getApiFindADriver().getParams() != null){
                    	if(!getApiFindADriver().getParams().getOrDefault(KEY_COUPON_TO_APPLY, "").equalsIgnoreCase("")){
							previousPromoId = getApiFindADriver().getParams().get(KEY_COUPON_TO_APPLY);
						}
                    	else if(!getApiFindADriver().getParams().getOrDefault(KEY_PROMO_TO_APPLY, "").equalsIgnoreCase("")){
							previousPromoId = getApiFindADriver().getParams().get(KEY_PROMO_TO_APPLY);
						}
					}
                    if(!String.valueOf(defaultCoupon.getId()).equalsIgnoreCase(previousPromoId)){
						callFindADriverAfterCouponSelect();
					}

                } else {
                    slidingBottomPanel.getRequestRideOptionsFragment().setSelectedCoupon(-1, false);
                }
                updateConfirmedStateCoupon();
            }
        }
		stopDefaultCoupon = false;

        showPoolInforBar(false);
        slidingBottomPanel.getRequestRideOptionsFragment().updateOffersCount();

		if (regionSelected.getRideType() ==
				RideTypeValue.BIKE_RENTAL.getOrdinal()) {
			damageReportButton.setVisibility(View.GONE);
		} else {
			damageReportButton.setVisibility(View.GONE);
		}

        return changed;
    }

	public void showReverseBidField(Region region) {
		linearLayoutBidValue.setVisibility(View.VISIBLE);
		if(region.getRegionFare() != null && regionIdFareSetInETBid != region.getRegionId()) {
			editTextBidValue.setText(Utils.getMoneyDecimalFormat().format(region.getRegionFare().getFare() * 0.8));
			editTextBidValue.setSelection(editTextBidValue.getText().length());
			regionIdFareSetInETBid = region.getRegionId();
		}
	}

	//    0 :- Height Large
//    1 :- Normal Height
    private void setHeightDropAddress(int caseVal) {

        View viewDash = findViewById(R.id.iv2NewUIDropDashedLine);
        ImageView iv3NewUIDropMark = findViewById(R.id.iv3NewUIDropMark);
        LinearLayout rlMark = findViewById(R.id.rlMark);

        int dp70 = Utils.dpToPx(this, 55);
        int dp40 = (int)(ASSL.minRatio()*16F);
        int dp30 = Utils.dpToPx(this, 15);
        int dp50 = Utils.dpToPx(this, 22);
        int dp68 = Utils.dpToPx(this, 30);

		ViewGroup.LayoutParams layoutParamsInitial = relativeLayoutInitialSearchBarNew.getLayoutParams();
		layoutParamsInitial.height = dp70;

        ViewGroup.LayoutParams layoutParams = relativeLayoutDestSearchBarNew.getLayoutParams();
        ViewGroup.LayoutParams params = iv3NewUIDropMark.getLayoutParams();
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(viewDash != null){
            switch (caseVal) {
                case 0 :
                    viewDash.setVisibility(View.GONE);
                    layoutParams.height = dp70;
                    params.height = dp40;
                    params.width = dp40;
                    params2.setMargins(dp30, dp50, dp30, 0);
                    break;

                case 1:
                default:
                    viewDash.setVisibility(View.VISIBLE);
                    layoutParams.height = dp70;
                    params.height = dp40;
                    params.width = dp40;
                    params2.setMargins(dp30, dp68, dp30, 0);
            }
            rlMark.setLayoutParams(params2);
            relativeLayoutDestSearchBarNew.setLayoutParams(layoutParams);
            iv3NewUIDropMark.setLayoutParams(params);
			relativeLayoutInitialSearchBarNew.setLayoutParams(layoutParamsInitial);
        }
    }

    public void updateFareEstimateHoverButton() {
        if(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() != RideTypeValue.POOL.getOrdinal()){
            textViewShowFareEstimate.setVisibility(Prefs.with(this).getInt(KEY_SHOW_FARE_ESTIMATE_HOVER_BUTTON, 0) == 1 ? View.VISIBLE : View.GONE);
        } else {
            textViewShowFareEstimate.setVisibility(View.GONE);
        }
    }

    public int getVehicleTypeSelected() {
        return slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getVehicleType();
    }

    public int getOperatorIdSelected() {
        return slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOperatorId();
    }

    private SelectorBitmapLoader selectorBitmapLoader;
    private int regionIdForSelectorLoader;

    private SelectorBitmapLoader getSelectorBitmapLoader(int regionIdForSelectorLoader) {
        if (selectorBitmapLoader == null) {
            selectorBitmapLoader = new SelectorBitmapLoader(this);
        }
        this.regionIdForSelectorLoader = regionIdForSelectorLoader;
        return selectorBitmapLoader;
    }

    private void updateImageViewRideNowIcon() {
        try {
            imageViewRideNow.setImageDrawable(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRequestSelector(this));
            getSelectorBitmapLoader(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionId())
                    .loadSelector(imageViewRideNow,
                            slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getImages().getRideNowNormal(),
                            slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getImages().getRideNowHighlighted(),
                            new SelectorBitmapLoader.Callback() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    if (regionIdForSelectorLoader == slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionId()) {
                                        imageViewRideNow.setImageDrawable(drawable);
                                    }
                                }
                            }, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRegionUI(boolean firstTime) {
        try {
            updateImageViewRideNowIcon();
            imageViewRideNow.startAnimation(getBounceScale());
            showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());
//        if(!firstTime && map != null){
//            zoomToCurrentLocationWithOneDriver(map.getCameraPosition().target);
//        }

            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()) {
                ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
                int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
                if (!firstTime && index == 1 && Data.autoData.getDropLatLng() == null) {
                    translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
                    translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
                }
                if (Data.autoData.getDropLatLng() == null) {
                    textViewDestSearch.setText(getResources().getString(R.string.destination_required));
                    textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color_light));
                    textViewDestSearchNew.setText(getResources().getString(R.string.destination_required));
                    textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color_light));
                }
                showPoolInforBar(false);
                try {
                    if (Prefs.with(HomeActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                        imageViewPokemonOnOffInitial.setAlpha(1.0f);
                    } else {
                        imageViewPokemonOnOffInitial.setAlpha(0.3f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ViewGroup viewGroup = ((ViewGroup) relativeLayoutInitialSearchBar.getParent());
                int index = viewGroup.indexOfChild(relativeLayoutDestSearchBar);
                if (!firstTime && index == 1 && Data.autoData.getDropLatLng() == null) {
                    translateViewTop(viewGroup, relativeLayoutInitialSearchBar, true, true);
                    translateViewBottom(viewGroup, relativeLayoutDestSearchBar, false, true);
                }
                if (Data.autoData.getDropLatLng() == null) {
                    textViewDestSearch.setText(getResources().getString(R.string.enter_destination));
                    textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color_light));
                    textViewDestSearchNew.setText((isNewUI && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1)
                            ? getResources().getString(R.string.text_where_to_go) : getResources().getString(R.string.enter_destination));
                    textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color_light));
                }
                viewPoolInfoBarAnim.setVisibility(View.VISIBLE);
                showPoolInforBar(false);
                setFabMarginInitial(false, false);
            }
            slidingBottomPanel.getRequestRideOptionsFragment()
                    .updateBottomMultipleView(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType());


            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Prefs.with(HomeActivity.this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                            imageViewPokemonOnOffInitial.setAlpha(1.0f);
                        } else {
                            imageViewPokemonOnOffInitial.setAlpha(0.3f);
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

    public void setFabMarginInitial(boolean isSliding, boolean isSlidingPanelCollapsing) {
        try {
            if (P_INITIAL == passengerScreenMode) {
                fabViewFinal.setVisibility(View.GONE);
                fabViewIntial.setVisibility(View.VISIBLE);
                final ValueAnimator animator;
                int handlerTime = 0;
                if (viewPoolInfoBarAnim.getVisibility() == View.VISIBLE) {
                    if (changeLocalityLayout.getVisibility() == View.VISIBLE) {
                        animator = ValueAnimator.ofInt(fabViewTest.menuLabelsRightTest.getPaddingBottom(), (int) (62f * scale + 0.5f));
                        handlerTime = 0;
                    } else {
                        animator = ValueAnimator.ofInt(fabViewTest.menuLabelsRightTest.getPaddingBottom(), (int) (22f * scale + 0.5f));
                        handlerTime = 0;
                    }
                } else {
                    animator = ValueAnimator.ofInt(fabViewTest.menuLabelsRightTest.getPaddingBottom(), (int) (62f * scale + 0.5f));
                    if (isSliding) {
                        handlerTime = 0;
                    } else {
                        handlerTime = 250;
                    }
                }
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (P_INITIAL == passengerScreenMode) {
                            fabViewTest.setMenuLabelsRightTestPadding((Integer) valueAnimator.getAnimatedValue());
                        }
                    }
                });
                if (isSlidingPanelCollapsing) {
                    handlerTime = 300;
                }
                animator.setDuration(300);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animator.start();
                    }
                }, handlerTime);
            }
        } catch (Exception e) {
        }


        setJeanieVisibility();
    }

    public void setDestinationBarPlaceholderText(int rideTypeValue) {
        if (rideTypeValue == RideTypeValue.POOL.getOrdinal()) {
            if (Data.autoData.getDropLatLng() == null) {
                textViewDestSearch.setText(getResources().getString(R.string.destination_required));
                textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color_light));
                textViewDestSearchNew.setText(getResources().getString(R.string.destination_required));
                textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color_light));
            }
        } else {
            if (Data.autoData.getDropLatLng() == null) {
                textViewDestSearch.setText(getResources().getString(R.string.enter_destination));
                textViewDestSearch.setTextColor(getResources().getColor(R.color.text_color_light));
                textViewDestSearchNew.setText((isNewUI && Prefs.with(this).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1)
                        ? getResources().getString(R.string.text_where_to_go) : getResources().getString(R.string.enter_destination));
                textViewDestSearchNew.setTextColor(getResources().getColor(R.color.text_color_light));
            }
        }
    }

    public void showPoolInforBar(boolean isSlidingPanelCollapsing) {
        try {
            float mapBottomPadding = 0f;
            String textToShow = "";


            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts() != null
                    && !slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts().getText1().equalsIgnoreCase("")) {
                textToShow = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts().getText1();
            } else if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts() != null
                    && !TextUtils.isEmpty(slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts().getText2())) {
                textToShow = slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts().getText2();
            }
            boolean isCustomCouponText = false;
            PromoCoupon promoCoupon = getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon();

            if (promoCoupon != null && promoCoupon.getId() > 0) {
                textToShow = promoCoupon.getTitle();
                isCustomCouponText = true;
                textViewCouponApplied.setVisibility(View.VISIBLE);
            } else {
                textViewCouponApplied.setVisibility(View.GONE);

            }
            textToShow = textToShow.trim();
            if ((slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()) &&
                    (getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) &&
                    (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts() != null
                            && !slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getOfferTexts().getText1().equalsIgnoreCase("")) && !isCustomCouponText) {
                viewPoolInfoBarAnim.setVisibility(View.GONE);
                setFabMarginInitial(false, isSlidingPanelCollapsing);
                textViewPoolInfo1.setText(textToShow);
                relativeLayoutPoolInfoBar.setBackgroundResource(R.drawable.background_pool_info);
                textViewPoolInfo1.setTextColor(getResources().getColor(R.color.text_color));
                mapBottomPadding = 72f;
            } else if ((getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) &&
                    (!TextUtils.isEmpty(textToShow))) {


                viewPoolInfoBarAnim.setVisibility(View.GONE);
                setFabMarginInitial((Data.autoData.getRegions().size() == 1), isSlidingPanelCollapsing);

                textViewPoolInfo1.setText(textToShow);
                relativeLayoutPoolInfoBar.setBackgroundColor(getResources().getColor(R.color.text_color));
                textViewPoolInfo1.setTextColor(getResources().getColor(R.color.white));

                if (!isCustomCouponText) {
                    final StyleSpan bss = new StyleSpan(Typeface.BOLD);
                    final SpannableStringBuilder sb = new SpannableStringBuilder(" " + getString(R.string.view_all_offers));
                    sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textViewPoolInfo1.append(sb);
                }
                mapBottomPadding = 72f;
            } else {
                viewPoolInfoBarAnim.setVisibility(View.VISIBLE);
                setFabMarginInitial(false, isSlidingPanelCollapsing);
            }
            if (P_INITIAL == passengerScreenMode
                    && !specialPickupScreenOpened && !confirmedScreenOpened && !isNewUI()) {
                setGoogleMapPadding(mapBottomPadding, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forceFarAwayCity() {
        try {
            Data.autoData.setFarAwayCity(getResources().getString(R.string.service_not_available));
            setServiceAvailablityUI(Data.autoData.getFarAwayCity());
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public LatLng getCurrentPlaceLatLng() {
        if (Data.getLatLngOfJeanieLastShown() != null) {
            return Data.getLatLngOfJeanieLastShown();
        } else if (map != null) {
            return map.getCameraPosition().target;
        } else {
            return new LatLng(Data.latitude, Data.longitude);
        }
    }

    public LatLng getCurrentLatLng() {
    	LatLng latLng;
        if (myLocation != null) {
			latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        } else {
			latLng = new LatLng(Data.latitude, Data.longitude);
        }
        if(MapUtils.distance(latLng, new LatLng(0,0)) > 10){
        	return latLng;
		} else {
        	return new LatLng(LocationFetcher.getSavedLatFromSP(this), LocationFetcher.getSavedLngFromSP(this));
		}
    }


    private ApiCampaignAvailRequest apiCampaignAvailRequest;

    private ApiCampaignAvailRequest getApiCampaignAvailRequest() {
        if (apiCampaignAvailRequest == null) {
            apiCampaignAvailRequest = new ApiCampaignAvailRequest(this, new ApiCampaignAvailRequest.Callback() {
                @Override
                public void onPre() {
                    try {
                        linearLayoutRequestMain.setVisibility(View.GONE);
                        relativeLayoutInAppCampaignRequest.setVisibility(View.VISIBLE);
                        if (Data.autoData.getCampaigns().getMapLeftButton() != null) {
                            textViewInAppCampaignRequest.setText(Data.autoData.getCampaigns().getMapLeftButton().getText());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int flag, String message, String image, int width, int height) {
                    try {
                        if (campaignApiCancelled || "".equalsIgnoreCase(image)) {
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

    private Target getTargetAvailCampaign(int flagAvailCampaign, String messageAvailCampaign) {
        this.messageAvailCampaign = messageAvailCampaign;
        this.flagAvailCampaign = flagAvailCampaign;
        if (targetAvailCampaign == null) {
            targetAvailCampaign = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    try {
                        backFromCampaignAvailLoading();
                        if (!campaignApiCancelled) {
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == HomeActivity.this.flagAvailCampaign) {
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

    private void callCampaignAvailRequest() {
        try {
            if (Data.autoData.getCampaigns() != null && Data.autoData.getCampaigns().getMapLeftButton() != null) {
                campaignApiCancelled = false;
                getApiCampaignAvailRequest().availCampaign(map.getCameraPosition().target,
                        Data.autoData.getCampaigns().getMapLeftButton().getCampaignId());
            } else {
                Utils.showToast(this, getString(R.string.no_campaign_currently));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCampaignAvailed() {
        try {
            if (Data.autoData.getCampaigns().getMapLeftButton().getShowCampaignAfterAvail() == 0) {
                Data.autoData.setCampaigns(null);
                setupInAppCampaignUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean campaignApiCancelled = false;
    private ApiCampaignRequestCancel apiCampaignRequestCancel;

    private ApiCampaignRequestCancel getApiCampaignRequestCancel() {
        if (apiCampaignRequestCancel == null) {
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

    private void callApiCampaignRequestCancel() {
        try {
            if (Data.autoData.getCampaigns() != null && Data.autoData.getCampaigns().getMapLeftButton() != null) {
                getApiCampaignRequestCancel().cancelCampaignRequest(Data.autoData.getCampaigns().getMapLeftButton().getCampaignId());
            } else {
                Utils.showToast(this, getString(R.string.no_campaign_currently));
                backFromCampaignAvailLoading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backFromCampaignAvailLoading() {
        linearLayoutRequestMain.setVisibility(View.VISIBLE);
        relativeLayoutInAppCampaignRequest.setVisibility(View.GONE);
    }


    public void updateConfirmedStatePaymentUI() {
        try {
            imageViewPaymentModeConfirm.setImageResource(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()));
            textViewPaymentModeValueConfirm.setText(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption(),this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateConfirmedStateCoupon() {
        try {
            promoCouponSelectedForRide = slidingBottomPanel.getRequestRideOptionsFragment().getSelectedCoupon();
            if (promoCouponSelectedForRide.getId() > 0) {
                imageViewOfferConfirm.setVisibility(View.VISIBLE);
            } else {
                imageViewOfferConfirm.setVisibility(View.GONE);
            }
            showPoolInforBar(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateConfirmedStateFare() {
        try {
            if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()
                    || slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getShowFareEstimate() == 1
					|| slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getFareMandatory() == 1) {
                relativeLayoutTotalFare.setVisibility(View.VISIBLE);
                textVieGetFareEstimateConfirm.setVisibility(View.GONE);
                findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.VISIBLE);
            } else {
                relativeLayoutTotalFare.setVisibility(View.GONE);
                if (Data.autoData.getConfirmScreenFareEstimateEnable().equalsIgnoreCase("1")) {
                    textVieGetFareEstimateConfirm.setVisibility(View.VISIBLE);
                    findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.VISIBLE);
                } else {
                    textVieGetFareEstimateConfirm.setVisibility(View.GONE);
                    findViewById(R.id.vDivFareEstimateConfirm).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setShakeAnim(int shakeAnim) {
        this.shakeAnim = shakeAnim;
    }

    public void imageViewRideNowPoolCheck() {
        rlSpecialPickup.setVisibility(View.GONE);
        if (getSlidingBottomPanel().getRequestRideOptionsFragment()
                .getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()) {
            if (Data.autoData.getDropLatLng() != null) {
                //requestRideClick();
                shakeAnim = 0;
                if (updateSpecialPickupScreen() && !isSpecialPickupScreenOpened()) {
                    // show special pickup screen
                    specialPickupScreenOpened = true;
                    passengerScreenMode = P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                    showSpecialPickupMarker(specialPickups);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.autoData.getPickupLatLng(), MAX_ZOOM));
                    spin.setSelection(getIndex(spin, Data.autoData.getNearbyPickupRegionses().getDefaultLocation().getText()));
                } else {
                    specialPickupScreenOpened = false;
                    removeSpecialPickupMarkers();
                    rlSpecialPickup.setVisibility(View.GONE);
                    updateTopBar();
                    if(!isNewUI) {
                        openConfirmRequestView();
                    } else {
                        requestRideClick();
                    }
                }
            } else {
                destinationRequiredShake();
            }
        }

        else if (getSlidingBottomPanel().getRequestRideOptionsFragment()
                .getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
            openBikeRentalScan();
        }

        else {

            if (Data.autoData.getDropLatLng() == null && getSlidingBottomPanel().getRequestRideOptionsFragment()
                    .getRegionSelected().getDestinationMandatory() == 1) {
                destinationRequiredShake();
            } else if(!isNewUI){
                if (updateSpecialPickupScreen() && !isSpecialPickupScreenOpened()) {
                    // show special pickup screen

                    specialPickupScreenOpened = true;
                    passengerScreenMode = P_INITIAL;
                    switchPassengerScreen(passengerScreenMode);
                    showSpecialPickupMarker(specialPickups);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(Data.autoData.getPickupLatLng(), MAX_ZOOM));
                    spin.setSelection(getIndex(spin, Data.autoData.getNearbyPickupRegionses().getDefaultLocation().getText()));

                } else if (Data.autoData.getDropLatLng() != null
                        && (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getShowFareEstimate() == 1
						|| slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getFareMandatory() == 1)) {
                    specialPickupScreenOpened = false;
                    removeSpecialPickupMarkers();
                    rlSpecialPickup.setVisibility(View.GONE);
                    updateTopBar();
                    openConfirmRequestView();
                } else {
                    removeSpecialPickupMarkers();
                    requestRideClick();
                    slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        }
    }

    public void openBikeRentalScan() {
        if (Data.autoData.getDropLatLng() == null && getSlidingBottomPanel().getRequestRideOptionsFragment()
                .getRegionSelected().getDestinationMandatory() == 1) {
            destinationRequiredShake();
        }
        else {
            //old functions as it is
            specialPickupScreenOpened = false;
            removeSpecialPickupMarkers();
            rlSpecialPickup.setVisibility(View.GONE);
            updateTopBar();

            InstructionDialog.showHelpDialog(HomeActivity.this);
        }
    }

    private void destinationRequiredShake() {
        Log.e("action","assiging driver");
        if (slidingBottomPanel.getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingBottomPanel.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        textViewDestSearch.setText(getResources().getString(R.string.destination_required));
        textViewDestSearch.setTextColor(getResources().getColor(R.color.red));
        textViewDestSearchNew.setText(getResources().getString(R.string.destination_required));
        textViewDestSearchNew.setTextColor(getResources().getColor(R.color.red));

        ViewGroup viewGroup = ((ViewGroup) relativeLayoutDestSearchBar.getParent());
        int index = viewGroup.indexOfChild(relativeLayoutInitialSearchBar);
        if (index == 1 && Data.autoData.getDropLatLng() == null) {
            translateViewBottom(viewGroup, relativeLayoutDestSearchBar, true, true);
            translateViewTop(viewGroup, relativeLayoutInitialSearchBar, false, true);
        }
        if (Data.autoData.getDropLatLng() == null) {
            Animation shake = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.shake);
            textViewDestSearch.startAnimation(shake);
            textViewDestSearchNew.startAnimation(shake);
            shakeAnim++;
            if (shakeAnim > 3) {
                        /*new PoolDestinationDialog(HomeActivity.this, new PoolDestinationDialog.Callback() {
                            @Override
                            public void onEnterDestination() {
                                placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.DROP;
                                setServiceAvailablityUI("");
                                passengerScreenMode = PassengerScreenMode.P_SEARCH;
                                switchPassengerScreen(passengerScreenMode);
                            }
                        }).show();*/
            }
        }
    }

    public void openConfirmRequestView() {
        confirmedScreenOpened = true;
        passengerScreenMode = P_INITIAL;
        switchPassengerScreen(passengerScreenMode);
    }

    private boolean isPoolRideAtConfirmation() {
        return P_INITIAL == passengerScreenMode
                && confirmedScreenOpened
                && slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal();
    }

    private boolean isNormalRideWithDropAtConfirmation() {
        return P_INITIAL == passengerScreenMode
                && (confirmedScreenOpened || isNewUI)
                && slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() != RideTypeValue.POOL.getOrdinal()
                && Data.autoData.getDropLatLng() != null
                && Data.autoData.getPickupLatLng() != null;
    }

    private boolean isSpecialPickupScreenOpened() {
        return P_INITIAL == passengerScreenMode
                && specialPickupScreenOpened;
    }

    private boolean isNewUiWithDropAtConfirmation() {
        return P_INITIAL == passengerScreenMode
                && isNewUI
                && slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() != RideTypeValue.POOL.getOrdinal()
                && Data.autoData.getDropLatLng() != null;
    }


    private void clearMap() {
        try {
            map.clear();
            pokestopHelper.mapCleared();
            pokestopHelper.checkPokestopData(map.getCameraPosition().target, Data.userData.getCurrentCity());
            homeUtil.displaySavedAddressesAsFlags(this, assl, map, true, passengerScreenMode);
            homeUtil.displayPointOfInterestMarkers(this, assl, map, passengerScreenMode);
            if (driverMarkerInRide != null) {
                driverMarkerInRide.remove();
            }
            driverMarkerInRide = null;
            polylineP2D = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPokestopOnOffButton(PassengerScreenMode mode) {
        try {
            if (Utils.isAppInstalled(this, POKEMON_GO_APP_PACKAGE)
                    && changeLocalityLayout.getVisibility() == View.GONE
                    && Prefs.with(this).getInt(Constants.KEY_SHOW_POKEMON_DATA, 0) == 1) {
                //imageViewPokemonOnOffInitial.setVisibility(View.VISIBLE);
                //imageViewPokemonOnOffConfirm.setVisibility(View.VISIBLE);
                //imageViewPokemonOnOffEngaged.setVisibility(View.VISIBLE);

                ImageView imageView = null;
                if (mode == PassengerScreenMode.P_REQUEST_FINAL || mode == PassengerScreenMode.P_DRIVER_ARRIVED
                        || mode == PassengerScreenMode.P_IN_RIDE) {
                    imageView = imageViewPokemonOnOffEngaged;
                } else if (mode == P_INITIAL) {
                    if (confirmedScreenOpened) {
                        imageView = imageViewPokemonOnOffConfirm;
                    } else {
                        imageView = imageViewPokemonOnOffInitial;
                    }
                }
                if (imageView != null) {
                    if (Prefs.with(this).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                        imageView.setAlpha(1.0f);
                    } else {
                        imageView.setAlpha(0.3f);
                    }
                    pokestopHelper.checkPokestopData(map.getCameraPosition().target, Data.userData.getCurrentCity());
                }
            } else {
                imageViewPokemonOnOffInitial.setVisibility(View.GONE);
                imageViewPokemonOnOffConfirm.setVisibility(View.GONE);
                imageViewPokemonOnOffEngaged.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openNotification() {
        menuBar.getMenuAdapter().onClickAction(MenuInfoTags.INBOX.getTag(), 0, 0, HomeActivity.this, getCurrentPlaceLatLng());
    }

    private BroadcastReceiver pushBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            // Get extra data included in the Intent
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                        if (flag == -1) {
                            String message = intent.getStringExtra("message");
                            int type = intent.getIntExtra("open_type", 0);
                            if (type == 0) {
                                Log.d("receiver", "Got message: " + message);
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                if (Prefs.with(HomeActivity.this).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()).equals(Config.getAutosClientId())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(HomeActivity.this, Config.getFreshClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            } else if (type == 2) {
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                if (Prefs.with(HomeActivity.this).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()).equals(Config.getAutosClientId())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(HomeActivity.this, Config.getGroceryClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            } else if (type == 3) {
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                if (Prefs.with(HomeActivity.this).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()).equals(Config.getAutosClientId())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(HomeActivity.this, Config.getMenusClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            } else if (type == 4) {
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                if (Prefs.with(HomeActivity.this).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()).equals(Config.getAutosClientId())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(HomeActivity.this, Config.getDeliveryCustomerClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            }
                        } else {
                            if (PushFlags.INITIATE_PAYTM_RECHARGE.getOrdinal() == flag) {
                                String message = intent.getStringExtra(KEY_MESSAGE);
                                Data.userData.setPaytmRechargeInfo(JSONParser.parsePaytmRechargeInfo(new JSONObject(message)));
                                openPaytmRechargeDialog();
                            }else if (PushFlags.UNLOCK_BLE_DEVICE.getOrdinal() == flag){
                                smartLockObj.downDevice();
                            } else if (PushFlags.CHAT_MESSAGE.getOrdinal() == flag) {
                                tvChatCount.setVisibility(View.VISIBLE);
                                tvChatCount.setText(String.valueOf(Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 1)));
                            } else if (Constants.OPEN_DEEP_INDEX == flag) {
                                deepLinkAction.openDeepLink(HomeActivity.this, getCurrentPlaceLatLng());
                            } else if (PushFlags.BID_RECEIVED.getOrdinal() == flag) {
                                if (passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
                                    String message = intent.getStringExtra(Constants.KEY_MESSAGE);
                                    JSONObject jObj = new JSONObject(message);
                                    Data.autoData.setBidInfos(JSONParser.parseBids(HomeActivity.this, Constants.KEY_BIDS, jObj));
                                    updateBidsView();
                                }
                            } else if (PushFlags.MPESA_PAYMENT_SUCCESS.getOrdinal() == flag) {
                                //  textViewRSCashPaidValue.setText(Utils.formatCurrencyValue(Data.autoData.getEndRideData().getCurrency(), Data.autoData.getEndRideData().toPay));
                                //DialogPopup.alertPopup(HomeActivity.this, "", intent.getStringExtra("message"));
                                rideEndPaymentSuccess(intent.getStringExtra("message"), Double.parseDouble(intent.getStringExtra(Constants.TO_PAY)));
                            } else if (PushFlags.MPESA_PAYMENT_FAILURE.getOrdinal() == flag) {
                                //DialogPopup.alertPopup(HomeActivity.this, "", intent.getStringExtra("message"));
                                //updateRideEndPayment();
                                rideEndPaymentSuccess(intent.getStringExtra("message"), null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void updateBidsView() {
    	bidsPlacedAdapter.setList(Data.autoData.getBidInfos(), Data.autoData.getBidTimeout(),
				slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRegionName());

        if (slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected().getRideType() ==
                RideTypeValue.BIKE_RENTAL.getOrdinal()) {
            textViewFindingDriver.setText(bidsPlacedAdapter.getItemCount() == 0 ? R.string.booking_a_bike : R.string.tap_a_bid);
        } else {
            textViewFindingDriver.setText(bidsPlacedAdapter.getItemCount() == 0 ? R.string.finding_a_driver : R.string.tap_a_bid);
        }

        //bids from various drivers will show and block the cancel request button in this case
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) assigningLayout.getLayoutParams();
        if (bidsPlacedAdapter.getItemCount() > 0) {
			rlAssigningBidding.setVisibility(View.VISIBLE);
			rlBidTimer.setVisibility(View.GONE);
			rlAssigningNormal.setVisibility(View.GONE);
			topBar.tvCancel.setVisibility(View.VISIBLE);

			params.topMargin = (int) (ASSL.Yscale() * 96F);

		} else {
			rlAssigningBidding.setVisibility(View.GONE);
			rlBidTimer.setVisibility(View.GONE);
			rlAssigningNormal.setVisibility(View.VISIBLE);
			topBar.tvCancel.setVisibility(View.GONE);

			params.topMargin = isNewUI() ? 0 : (int) (ASSL.Yscale() * 96F);
		}
        assigningLayout.setLayoutParams(params);

		updateCancelButtonUI();

		//bid and cancel buttons visibility and ui
		double incrementVal = getBidIncrementValFromServer();
		int bidFareVisibility = (tvInitialCancelRide.getVisibility() == View.VISIBLE
				&& Data.autoData.getIsReverseBid() == 1 && incrementVal > 0D
				&& Data.autoData.getInitialBidValue() > 0) ? View.VISIBLE : View.GONE;
		bRaiseOfferFare.setVisibility(bidFareVisibility);
		bRaiseOfferFare.setEnabled(Data.autoData.getInitialBidValue() != Data.autoData.getChangedBidValue());
		llRaiseBidButton.setVisibility(bidFareVisibility);
		tvRaiseBidValue.setText(Utils.formatCurrencyValue(Data.autoData.getCurrency(), Data.autoData.getChangedBidValue()));
		if(bidFareVisibility == View.VISIBLE){
			tvInitialCancelRide.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color_selector));
			tvInitialCancelRide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		} else {
			tvInitialCancelRide.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color_red_dark_aplha_selector));
			tvInitialCancelRide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}

		//setting text on reverse bid increment and decrement values
		SpannableStringBuilder ssbMinus = new SpannableStringBuilder("-"+Utils.formatCurrencyValue(Data.autoData.getCurrency(), incrementVal));
		ssbMinus.setSpan(new RelativeSizeSpan(1.2F), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssbMinus.setSpan(new StyleSpan(Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssbMinus.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvRaiseFareMinus.setText(ssbMinus);
		SpannableStringBuilder ssbPlus = new SpannableStringBuilder("+"+Utils.formatCurrencyValue(Data.autoData.getCurrency(), incrementVal));
		ssbPlus.setSpan(new RelativeSizeSpan(1.2F), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssbPlus.setSpan(new StyleSpan(Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssbPlus.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvRaiseFarePlus.setText(ssbPlus);

		//set top bar transaparent or opaque based on new ui and drop layout visibility
		setTopBarTransNewUI();


		long startTime = Prefs.with(this).getLong(Constants.KEY_REQUEST_RIDE_START_TIME, System.currentTimeMillis());
		bidTime = Data.autoData.getBidRequestRideTimeout() - (System.currentTimeMillis() - startTime);

        if (bidTime <= 0) {
            getHandler().removeCallbacks(runnableBidTimer);
            findViewById(R.id.vBidTimer).setVisibility(View.GONE);
            pwBidTimer.setVisibility(View.GONE);
            tvBidTimer.setVisibility(View.GONE);
        } else {
            findViewById(R.id.vBidTimer).setVisibility(View.VISIBLE);
            pwBidTimer.setVisibility(View.VISIBLE);
            tvBidTimer.setVisibility(View.VISIBLE);

            getHandler().removeCallbacks(runnableBidTimer);
            getHandler().post(runnableBidTimer);
        }

        //set google map padding according to bottom layout
		llFindingADriver.post(() -> setGoogleMapPadding(llFindingADriver.getMeasuredHeight(), true));
    }

	private double getBidIncrementValFromServer() {
		double incrementVal = 0D;
		try{incrementVal = Double.parseDouble(Prefs.with(this).getString(KEY_CUSTOMER_BID_INCREMENT, String.valueOf(0D)));}catch(Exception e){}
		return incrementVal;
	}


	private double bidTime = -1;
    private Runnable runnableBidTimer = new Runnable() {
        @Override
        public void run() {
			try {
				bidTime = bidTime - 75.0;
				tvBidTimer.setText(String.valueOf((int)(bidTime/1000D)));
				pwBidTimer.setInstantProgress((float) (bidTime / (double) Data.autoData.getBidRequestRideTimeout()));
				bidsPlacedAdapter.updateProgress();
				if (bidTime > 0) {
					getHandler().postDelayed(this, 75);
				} else {
					getHandler().removeCallbacks(this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    };



	public void cancelClick() {
		if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING) {
			tvInitialCancelRide.performClick();
		}
	}

    private TrackingLogHelper trackingLogHelper;

    private TrackingLogHelper getTrackingLogHelper() {
        if (trackingLogHelper == null) {
            trackingLogHelper = new TrackingLogHelper(this);
        }
        return trackingLogHelper;
    }

    public FABViewTest getFabViewTest() {
        return fabViewTest;
    }

    private void showSpecialPickupMarker(ArrayList<NearbyPickupRegions.HoverInfo> specialPickups) {
        try {
            if (map != null) {
                float zIndex = 0.0f;
                for (NearbyPickupRegions.HoverInfo specialPickup : specialPickups) {
                    LatLng specialPicupLatLng = new LatLng(Double.parseDouble(specialPickup.getLatitude()),
                            Double.parseDouble(specialPickup.getLongitude()));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(specialPickup.getText());
                    markerOptions.snippet("special_pickup");
                    markerOptions.position(specialPicupLatLng);
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.zIndex(zIndex);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                            .createMarkerBitmapForResource(HomeActivity.this, R.drawable.ic_special_location_pin, 37f, 37f)));
                    markersSpecialPickup.add(map.addMarker(markerOptions));
                    markerOptionsSpecialPickup.add(markerOptions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSpecialPickupMarkers() {
        try {
            markerOptionsSpecialPickup.clear();
            for (Marker marker : markersSpecialPickup) {
                marker.remove();
            }
            markersSpecialPickup.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiFetchUserAddress apiFetchUserAddress;

    private ApiFetchUserAddress getApiFetchUserAddress() {
        if (apiFetchUserAddress == null) {
            apiFetchUserAddress = new ApiFetchUserAddress(this, new ApiFetchUserAddress.Callback() {
                @Override
                public void onSuccess() {
                    try {
                        homeUtil.displaySavedAddressesAsFlags(HomeActivity.this, assl, map, true, passengerScreenMode);
                        if(passengerScreenMode == P_INITIAL) {
                            getAddressAsync(new LatLng(Data.latitude, Data.longitude), getInitialPickupTextView(), null, PlaceSearchListFragment.PlaceSearchMode.PICKUP);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {

                }

                @Override
                public void onNoRetry(View view) {

                }

                @Override
                public void onFinish() {

                }
            });
        }
        return apiFetchUserAddress;
    }

    private HomeUtil homeUtil = new HomeUtil();



    private HomeUtil.SavedAddressState savedAddressState = HomeUtil.SavedAddressState.BLANK;

    private int getMapAnimateDuration() {
        if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode
                || PassengerScreenMode.P_IN_RIDE == passengerScreenMode) {
            return 1000;
        }
        return MAP_ANIMATE_DURATION;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void initiateRideEndPaymentAPI(String engagementId, final int paymentOption) {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
        params.put(Constants.KEY_PREFERRED_PAYMENT_MODE, String.valueOf(paymentOption));

        new ApiCommon<PaymentResponse>(this).showLoader(true).execute(params, ApiName.INITIATE_RIDE_END_PAYMENT,
                new APICommonCallback<PaymentResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        return false;

                    }

                    @Override
                    public void onSuccess(final PaymentResponse response, String message, int flag) {
                        try {
//                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
//                                if (paymentOption == PaymentOption.MPESA.getOrdinal()) {
//                                    rideEndPaymentSuccess(message);
//                                }
//                            } else {
                            DialogPopup.alertPopup(HomeActivity.this, "", message);
                            //}

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.connection_lost_please_try_again));
                        }
                    }

                    @Override
                    public boolean onError(PaymentResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                    @Override
                    public boolean onFailure(Exception error) {
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
    }

    private void rideEndPaymentSuccess(String message, Double remaining) {
        if (Data.autoData != null && Data.autoData.getEndRideData() != null) {
            Data.autoData.getEndRideData().setShowPaymentOptions(0);
            if (remaining != null) {
                Data.autoData.getEndRideData().toPay = remaining;
            }
        }
        DialogPopup.alertPopupWithListener(this, "", message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRideEndPayment();
                setUserData();
            }
        });
    }


	@Override
	public void onBidAccepted(@NotNull BidInfo bidInfo) {
		selectBidAPI(String.valueOf(bidInfo.getEngagementId()));
	}

	@Override
	public void onBidCancelled(@NotNull BidInfo bidInfo, boolean showDialog) {
    	bidsInCancelState.add(bidInfo.getEngagementId());
		cancelBidApi(bidInfo, showDialog);
	}

	@Override
	public boolean isBidCancelling(int engagementId) {
		return bidsInCancelState.contains(engagementId);
	}

	public void selectBidAPI(String engagementId) {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);

        new ApiCommon<FeedCommonResponse>(this).showLoader(true).execute(params, ApiName.SELECT_BID,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {
                        try {
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                            } else {
                                DialogPopup.alertPopup(HomeActivity.this, "", message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.connection_lost_please_try_again));
                        }
                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });
    }

    private ArrayList<Integer> bidsInCancelState = new ArrayList<>();
    public void cancelBidApi(BidInfo bidInfo, boolean showDialog) {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(bidInfo.getEngagementId()));

        new ApiCommon<FeedCommonResponse>(this).showLoader(showDialog).execute(params, ApiName.CANCEL_BID,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {
                        try {
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
								bidsInCancelState.remove(Integer.valueOf(bidInfo.getEngagementId()));
								Data.autoData.getBidInfos().remove(bidInfo);
								bidsPlacedAdapter.notifyDataSetChanged();
								updateBidsView();
                            } else {
                                DialogPopup.alertPopup(HomeActivity.this, "", message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(HomeActivity.this, "", getString(R.string.connection_lost_please_try_again));
                        }
                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
    }

    private CallbackPaymentOptionSelector callbackPaymentOptionSelector = new CallbackPaymentOptionSelector() {
        @Override
        public void onPaymentOptionSelected(PaymentOption paymentOption) {

            Data.autoData.setPickupPaymentOption(paymentOption.getOrdinal());
            getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
            getSlidingBottomPanel().getRequestRideOptionsFragment().dismissPaymentDialog();
            if(getScheduleRideFragment()!=null){
                getScheduleRideFragment().updatePaymentOption();
                getScheduleRideFragment().dismissPaymentDialog();
            }

        }

        @Override
        public void onWalletAdd(PaymentOption paymentOption) {
            getSlidingBottomPanel().getRequestRideOptionsFragment().dismissPaymentDialog();
            if(getScheduleRideFragment()!=null){
                getScheduleRideFragment().dismissPaymentDialog();
            }
            if(passengerScreenMode == PassengerScreenMode.P_RIDE_END){
                getPaymentOptionDialogForTip().dismiss();
            }
        }

        @Override
        public String getAmountToPrefill() {
            return "";
        }

        @Override
        public void onWalletOptionClick() {
            showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected());
        }

        @Override
        public int getSelectedPaymentOption() {
            return Data.autoData.getPickupPaymentOption();
        }

        @Override
        public void setSelectedPaymentOption(int paymentOption) {
            Data.autoData.setPickupPaymentOption(paymentOption);
        }

        @Override
        public boolean isRazorpayEnabled() {
            return Data.autoData != null && Data.autoData.isRazorpayEnabled();
        }
    };


    private PaymentOptionDialog paymentOptionDialogTip;
    public PaymentOptionDialog getPaymentOptionDialogForTip(){
        if(paymentOptionDialogTip == null){
            paymentOptionDialogTip = new PaymentOptionDialog(this, getCallbackPaymentOptionSelector(), new PaymentOptionDialog.Callback() {
                @Override
                public void onDialogDismiss() {

                }

                @Override
                public void onPaymentModeUpdated() {
                    DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this,
                            getString(R.string.confirm_payment_via_card_ending_format,
                                    MyApplication.getInstance().getWalletCore().getConfigDisplayNameCards(HomeActivity.this, Data.autoData.getPickupPaymentOption())),
                            v -> getDriverTipInteractor().addTip(tipSelected, Data.autoData.getEndRideData().getPaymentOption()));
                }
                @Override
                public void getSelectedPaymentOption() {

                }
            });
        }
        return paymentOptionDialogTip;
    }

    private Integer likePickupDropVisibility = null;
    private Integer getLikePickupDropVisibility(){
        if(likePickupDropVisibility == null){
            likePickupDropVisibility = Prefs.with(this).getInt(KEY_PICKUP_DROP_LIKE_ENABLED, 0) == 1 ? View.VISIBLE : View.GONE;
        }
        return likePickupDropVisibility;
    }

    private int likeClicked = 0;


    public ScheduleRideFragment getScheduleRideFragment(){
        return (ScheduleRideFragment) getSupportFragmentManager().findFragmentByTag(ScheduleRideFragment.class.getName());
    }

    @Override
    public void onAttachScheduleRide() {
        scheduleRideOpen = true;
        setTopBarTransNewUI();
        topBar.imageViewScheduleRide.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);
    }

    @Override
    public void onDestroyScheduleRide() {
        scheduleRideOpen = false;
        setScheduleIcon();
        setTopBarTransNewUI();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START);
    }


    public boolean showSurgeIcon(){
        return Prefs.with(this).getInt(KEY_CUSTOMER_SHOW_SURGE_ICON, 1) == 1;
    }
    private void fetchCorporates(final PaymentOption paymentOption){
        if(Data.autoData.getCorporatesFetched().size() > 0){
            Data.autoData.setPickupPaymentOption(paymentOption.getOrdinal());
            getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
            if(getScheduleRideFragment()!=null){
                getScheduleRideFragment().updatePaymentOption();
            }
            return;
        }
        final HashMap<String, String> params = new HashMap<>();
        new ApiCommon<FetchCorporatesResponse>(this).showLoader(true).execute(params, ApiName.FETCH_CORPORATES,
                new APICommonCallback<FetchCorporatesResponse>() {

                    @Override
                    public void onSuccess(final FetchCorporatesResponse response, String message, int flag) {
                        Data.autoData.setCorporatesFetched((ArrayList<Corporate>) response.getCorporates());
                        if(Data.autoData.getCorporatesFetched().size() > 0){
                            Data.autoData.getCorporatesFetched().get(0).setSelected(true);
                        }
                        Data.autoData.setPickupPaymentOption(paymentOption.getOrdinal());
                        getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                        if(getScheduleRideFragment()!=null){
                            getScheduleRideFragment().updatePaymentOption();
                        }
                    }

                    @Override
                    public boolean onError(FetchCorporatesResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });
    }

    @Override
    public void onServiceTypeSelected(@NotNull ServiceType serviceType) {
        Data.autoData.setSelectedPackage(null);
        isScheduleRideEnabled = serviceType.getScheduleAvailable() == 1;
        setScheduleIcon();
        Log.e("onServiceTypeSelected",""+getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRideType());
        if (getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
            relativeLayoutDestSearchBar.setVisibility(View.GONE);
        }
        if (confirmedScreenOpened) {
            confirmedScreenOpened = false;
            isFromConfirmToOther = true;
            if (Data.autoData.getDropLatLng() != null) {
                imageViewDropCross.setVisibility(View.VISIBLE);
                imageViewDropCrossNew.setVisibility(View.VISIBLE);
                setLikeDropVisibilityAndBG();
            }
            passengerScreenMode = P_INITIAL;
            switchPassengerScreen(passengerScreenMode);
            if (Data.autoData != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(Data.autoData.getPickupLatLng()), MAP_ANIMATE_DURATION, null);
            }
        }
        Data.autoData.setServiceTypeSelected(serviceType);
        if(Data.autoData != null && Data.autoData.getServiceTypeSelected() != null
         && (Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())
          || Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType()))) {
//            slidingBottomPanel.getRequestRideOptionsFragment().setSelectedCoupon(null);
            promoCouponSelectedForRide = null;
            showPoolInforBar(false);
        }
        setServiceTypeUI();
        mNotes = "";
        slidingBottomPanel.getRequestRideOptionsFragment().updateRegionsUI();

        setServiceTypeTextIconsChanges(serviceType.getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType()));
        showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected());
    }

    private void setScheduleIcon() {
        if(passengerScreenMode == P_INITIAL && isScheduleRideEnabled && !scheduleRideOpen) {
            topBar.imageViewScheduleRide.setVisibility(View.VISIBLE);
        } else {
            topBar.imageViewScheduleRide.setVisibility(View.GONE);
        }
    }

    private void setServiceTypeUI(){
        if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes() != null
                && (Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())
                || Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType()))){
            constraintLayoutRideTypeConfirm.setVisibility(View.VISIBLE);
            linearLayoutRequestMain.setVisibility(View.GONE);
            relativeLayoutSearchContainer.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(Data.autoData.getServiceTypeSelected().getImages())) {
                Picasso.with(this).load(Data.autoData.getServiceTypeSelected().getImages()).into(ivRideTypeImage);
            }
            tvRideTypeInfo.setText(Utils.trimHTML(Utils.fromHtml(Data.autoData.getServiceTypeSelected().getDescription())));
            slidingBottomPanel.getSlidingUpPanelLayout().setEnabled(false);

            tvRideTypeRateInfo.setText(null);
			ArrayList<Region> regions = Data.autoData.getRegions();
            if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType())){
                if(Data.autoData != null
                        && regions.size() > 0
                        && regions.get(0).getPackages() != null
                        && regions.get(0).getPackages().size() > 0) {
                    Region region = regions.get(0);
                    tvRideTypeRateInfo.setText(R.string.package_starting_at);
                    tvRideTypeRateInfo.append(" ");
                    tvRideTypeRateInfo.append(getThemeColorSpannableString(Utils.formatCurrencyValue(region.getFareStructure().getCurrency(),
                            (region.getPackages() != null && !region.getPackages().isEmpty() && region.getPackages().get(0).getFareFixed() != null) ? region.getPackages().get(0).getFareFixed() : 0)));
                }
            } else if(Data.autoData.getServiceTypeSelected().getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType())){
                if(Data.autoData != null
                        && regions.size() > 0
                        && regions.get(0).getFareStructure() != null) {
                    Region region = regions.get(0);
                    tvRideTypeRateInfo.setText(R.string.package_starting_at);
                    tvRideTypeRateInfo.append(" ");
                    tvRideTypeRateInfo.append(getThemeColorSpannableString(Utils.formatCurrencyValue(region.getFareStructure().getCurrency(),
                            (region.getPackages() != null && !region.getPackages().isEmpty() && region.getPackages().get(0).getFareFixed() != null) ? region.getPackages().get(0).getFareFixed() : 0)));
                }
            }
			relativeLayoutConfirmBottom.setVisibility(View.GONE);

        } else {
            if(isNewUI) {
                relativeLayoutSearchContainerNew.setVisibility(View.VISIBLE);
                relativeLayoutSearchContainer.setVisibility(View.GONE);
                linearLayoutConfirmOption.setBackground(getResources().getDrawable(R.color.white));
            } else {
                relativeLayoutSearchContainerNew.setVisibility(View.GONE);
                relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                linearLayoutConfirmOption.setBackground(getResources().getDrawable(R.color.menu_item_selector_color_F7));
            }

			relativeLayoutConfirmBottom.setVisibility(View.VISIBLE);
            constraintLayoutRideTypeConfirm.setVisibility(View.GONE);
            linearLayoutRequestMain.setVisibility(View.VISIBLE);
            slidingBottomPanel.getSlidingUpPanelLayout().setEnabled(true);
            constraintLayoutRideTypeConfirm.postDelayed(() -> {
                setServiceTypeAdapter(false);
            }, 200);

        }
		relativeLayoutConfirmBottom.post(new Runnable() {
			@Override
			public void run() {
				int bottomPadding = relativeLayoutConfirmBottom.getMeasuredHeight();
				if(relativeLayoutConfirmBottom.getVisibility() == View.GONE){
					bottomPadding = constraintLayoutRideTypeConfirm.getMeasuredHeight();
				}
				setGoogleMapPadding(bottomPadding, true);
			}
		});
    }

    private SpannableStringBuilder getThemeColorSpannableString(String message){
        SpannableStringBuilder ssb = new SpannableStringBuilder(message);
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.theme_color)), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }


    private void updateLockStatusApi(final GpsLockStatus gpsLockStatus) {

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_LOCK_STATUS, String.valueOf(gpsLockStatus.getOrdinal()));
        params.put(Constants.KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());

        rentalEndRideLayout.setVisibility(View.VISIBLE);
        Log.i(TAG, String.valueOf(params));

            new ApiCommon<>(this).showLoader(false).putAccessToken(true)
                    .execute(params, ApiName.RENTALS_UPDATE_LOCK_STATUS, new APICommonCallback<FeedCommonResponse>() {
                        @Override
                        public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {
                            rentalEndRideLayout.setVisibility(View.GONE);
                            Log.d("HomeActivityRental"," Flag Update Lock Status Success " + String.valueOf(flag));
                            if(Data.autoData.getBluetoothEnabled()==1){
                                if(gpsLockStatus==GpsLockStatus.REQ_UNLOCK){
                                    smartLockObj.downDevice();
                                }else if(gpsLockStatus==GpsLockStatus.END_RIDE_LOCK) {
                                    smartLockObj.upDevice();
                                }
                            }
                            switchRentalInRideUI(gpsLockStatus);
                            rentalInRideLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(Data.autoData.getAssignedDriverInfo()!=null)
                                    apiGetGpsLockStatusAP(Data.autoData.getAssignedDriverInfo().getGpsLockStatus());
                                }
                            }, 2000);
                        }

                        @Override
                        public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                            Log.d("HomeActivityRental"," Flag Update Lock Status Error " + String.valueOf(flag));
                            rentalEndRideLayout.setVisibility(View.GONE);
                            return false;
                        }
                    });

    }


    private void apiGetGpsLockStatusRH(final int gpsLockStatusOld){
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
        new ApiCommon<GetLockStatusResponse>(HomeActivity.this).putAccessToken(true).showLoader(false)
                .execute(params, ApiName.RENTALS_GET_LOCK_STATUS, new APICommonCallback<GetLockStatusResponse>() {
                    @Override
                    public void onSuccess(GetLockStatusResponse feedCommonResponse, String message, int flag) {
                        if(feedCommonResponse.getGpsLockStatus() != -1) {
                            Data.autoData.getAssignedDriverInfo().setGpsLockStatus(feedCommonResponse.getGpsLockStatus());
                        } else if(feedCommonResponse.getGpsLockStatus()==GpsLockStatus.END_RIDE_LOCK.getOrdinal()){
                            if(Data.autoData.getBluetoothEnabled()==1){
                                smartLockObj.disconnectDevice();
                            }
                        }else{
                            Data.autoData.getAssignedDriverInfo().setGpsLockStatus(gpsLockStatusOld);
                        }
                        rentalStateUIHandling(passengerScreenMode);
                    }

                    @Override
                    public boolean onError(GetLockStatusResponse feedCommonResponse, String message, int flag) {
                        return true;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if(pollingGetGPSLockStatus) {
                            getHandler().postDelayed(runnableGetGPSLockStatus, Prefs.with(HomeActivity.this).getLong(Constants.KEY_CUSTOMER_GPS_LOCK_STATUS_POLLING_INTERVAL, 30000));
                        }
                    }
                });
    }

    private void apiGetGpsLockStatusAP(final int gpsLockStatusOld){
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, Data.autoData.getcEngagementId());
        new ApiCommon<GetLockStatusResponse>(HomeActivity.this).putAccessToken(true).showLoader(false)
                .execute(params, ApiName.RENTALS_GET_LOCK_STATUS, new APICommonCallback<GetLockStatusResponse>() {
                    @Override
                    public void onSuccess(GetLockStatusResponse feedCommonResponse, String message, int flag) {
                        rentalEndRideLayout.setVisibility(View.GONE);
                        if(feedCommonResponse.getGpsLockStatus() != -1) {
                            Data.autoData.getAssignedDriverInfo().setGpsLockStatus(feedCommonResponse.getGpsLockStatus());
                        } else {
                            Data.autoData.getAssignedDriverInfo().setGpsLockStatus(gpsLockStatusOld);
                        }
                        rentalStateUIHandling(passengerScreenMode);
                    }

                    @Override
                    public boolean onError(GetLockStatusResponse feedCommonResponse, String message, int flag) {
                            if(flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()
                                    && gpsLockStatusOld != GpsLockStatus.LOCK.getOrdinal()) {
                                Data.autoData.getAssignedDriverInfo().setGpsLockStatus(GpsLockStatus.UNLOCK.getOrdinal());
                                rentalStateUIHandling(passengerScreenMode);
                                rentalInRideLayout.postDelayed(() -> {
                                    dialogRentalLock(HomeActivity.this);

                                }, 200);
                                return true;
                            } else {
                                return false;
                            }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }


    private boolean pollingGetGPSLockStatus = false;
    private Runnable runnableGetGPSLockStatus = new Runnable() {
        @Override
        public void run() {
            if(inRideCheck() && Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null) {
                apiGetGpsLockStatusRH(Data.autoData.getAssignedDriverInfo().getGpsLockStatus());
            }
        }
    };

    private void startPollingGetGPSLockStatus(){
        getHandler().removeCallbacks(runnableGetGPSLockStatus);
        pollingGetGPSLockStatus = true;
        getHandler().postDelayed(runnableGetGPSLockStatus, 200);
    }

    private void cancelPollingGetGPSLockStatus(){
        pollingGetGPSLockStatus = false;
        getHandler().removeCallbacks(runnableGetGPSLockStatus);
    }

    private void rentalStateUIHandling(PassengerScreenMode mode){
        if(mode == PassengerScreenMode.P_REQUEST_FINAL || mode == PassengerScreenMode.P_DRIVER_ARRIVED) {
            if (Data.autoData.getAssignedDriverInfo().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                rentalInRideLayout.setVisibility(View.VISIBLE);
                requestFinalLayout.setVisibility(View.GONE);
            } else {
                rentalInRideLayout.setVisibility(View.GONE);
                requestFinalLayout.setVisibility(View.VISIBLE);
            }
            rentalEndRideLayout.setVisibility(View.GONE);
        } else if(mode == PassengerScreenMode.P_IN_RIDE) {
            if (Data.autoData.getAssignedDriverInfo().getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                int gpsLockStatus = Data.autoData.getAssignedDriverInfo().getGpsLockStatus();
                requestFinalLayout.setVisibility(View.GONE);
                if (gpsLockStatus == GpsLockStatus.REQ_LOCK.getOrdinal()
                        || gpsLockStatus == GpsLockStatus.REQ_UNLOCK.getOrdinal()
                        || gpsLockStatus == GpsLockStatus.REQ_END_RIDE_LOCK.getOrdinal()) {
                    rentalEndRideLayout.setVisibility(View.VISIBLE);
                    rentalInRideLayout.setVisibility(View.GONE);
                    endRideJugnooAnimation.setVisibility(View.VISIBLE);
//                    if (endRideJugnooAnimation instanceof ImageView) {
//                        rentalJugnooAnimation.stop();
//                        rentalJugnooAnimation.start();
//                    }
                    if (gpsLockStatus == GpsLockStatus.REQ_LOCK.getOrdinal()) {
                        textViewEndRide.setText(R.string.locking_ride);
                    } else if (gpsLockStatus == GpsLockStatus.REQ_UNLOCK.getOrdinal()) {
                        textViewEndRide.setText(R.string.unlocking_ride);
                    } else if (gpsLockStatus == GpsLockStatus.REQ_END_RIDE_LOCK.getOrdinal()) {
                        textViewEndRide.setText(R.string.ending_ride);
                    }
                } else {
//                    rentalJugnooAnimation.stop();
                    rentalEndRideLayout.setVisibility(View.GONE);
                    rentalInRideLayout.setVisibility(View.VISIBLE);
                    updateGpsLockStatus(gpsLockStatus);
                }
            } else {
//                rentalJugnooAnimation.stop();
                rentalEndRideLayout.setVisibility(View.GONE);
                rentalInRideLayout.setVisibility(View.GONE);
                requestFinalLayout.setVisibility(View.VISIBLE);
            }
        }
    }







    @Override
    public void callRentalOutstationRequestRide(@Nullable ServiceType serviceType, @NotNull Region region,
                                                @Nullable Package selectedPackage, @NotNull SearchResult searchResultPickup,
                                                @Nullable SearchResult searchResultDestination, @NotNull String dateTime) {
        if(serviceType != null) {
            if (getFilteredDrivers() == 0) {
                performBackpressed();
                noDriverNearbyToast(getResources().getString(R.string.no_driver_nearby_try_again));
            } else {
            Data.autoData.setServiceTypeSelected(serviceType);
            if (serviceType.getSupportedRideTypes() != null
                    && (serviceType.getSupportedRideTypes().contains(ServiceTypeValue.RENTAL.getType()) || serviceType.getSupportedRideTypes().contains(ServiceTypeValue.OUTSTATION.getType()))) {
                Data.autoData.setPickupSearchResult(searchResultPickup);
                Log.w("pickuplogging", "callRentalOutstationRequestRide"+Data.autoData.getPickupLatLng());
                Data.autoData.setSelectedPackage(selectedPackage);
                slidingBottomPanel.getRequestRideOptionsFragment().setRegionSelected(region);
                finalRequestRideTimerStart(region);
                performBackpressed();
            }
        }
        }
    }
    public void setServiceTypeTextIconsChanges(boolean isRental){
    if(isRental){
        tvHourlyPackage.setVisibility(View.VISIBLE);
        tvMultipleStops.setVisibility(View.VISIBLE);
        tvSafe.setVisibility(View.VISIBLE);
        tvOneWayTrip.setVisibility(View.GONE);
        tvRoundTrips.setVisibility(View.GONE);
        tvAdvanceBookings.setVisibility(View.GONE);
    }
    else{
        tvHourlyPackage.setVisibility(View.GONE);
        tvMultipleStops.setVisibility(View.GONE);
        tvSafe.setVisibility(View.GONE);
        tvOneWayTrip.setVisibility(View.VISIBLE);
        tvRoundTrips.setVisibility(View.VISIBLE);
        tvAdvanceBookings.setVisibility(View.VISIBLE);
    }
    }


    public void switchRentalInRideUI(GpsLockStatus gpsLockStatus) {
        if(inRideCheck() && Data.autoData != null && Data.autoData.getAssignedDriverInfo() != null) {
            Data.autoData.getAssignedDriverInfo().setGpsLockStatus(gpsLockStatus.getOrdinal());
            rentalStateUIHandling(passengerScreenMode);
        }
    }

    private void openRentalStationList() {
        dialogRentalStations = new Dialog(HomeActivity.this);
        dialogRentalStations.setTitle(R.string.drop_stations);
        dialogRentalStations.setContentView(R.layout.layout_rental_destination);
        RecyclerView recyclerView = dialogRentalStations.findViewById(R.id.recycler_view_rental_station);

        LinearLayoutManager linearLayoutManagerRentalDestination = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManagerRentalDestination);

        List<Region> region = Data.autoData.getRegions();
        List<Region.Stations> stations = new ArrayList<>();

        for (int i = 0; i < region.size(); i++) {
            if (region.get(i).getRideType() == RideTypeValue.BIKE_RENTAL.getOrdinal()) {
                stations = region.get(i).getStations();
            }
        }

        if (stations.size() <= 0) {
            Utils.showToast(HomeActivity.this, getString(R.string.no_nearby_drop_off_stations));
        } else {
            RentalStationAdapter adapter = new RentalStationAdapter(HomeActivity.this, stations);
            recyclerView.setAdapter(adapter);

            Objects.requireNonNull(dialogRentalStations.getWindow()).setGravity(Gravity.CENTER);
            Objects.requireNonNull(dialogRentalStations.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialogRentalStations.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogRentalStations.show();
        }
    }

    @Override
    public void onStationClick(SearchResult autoCompleteSearchResult){
       Log.d("RentalStationClick" , " OnStationClick");
        dialogRentalStations.dismiss();
        placeSearchMode = PlaceSearchListFragment.PlaceSearchMode.DROP;

        onPlaceClick(autoCompleteSearchResult);
        onPlaceSearchPre();
        onPlaceSearchPost(autoCompleteSearchResult, placeSearchMode);

        if(checkFareEstimate) {
            openFareEstimate();
        }

    }

    private boolean inRideCheck(){
        return (passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL
                || passengerScreenMode == PassengerScreenMode.P_DRIVER_ARRIVED
                || passengerScreenMode == PassengerScreenMode.P_IN_RIDE);
    }

    @Override
    public void updateGpsLockStatus(int gpsLockStatus) {
        buttonEndRide.setVisibility(View.VISIBLE);

        if(inRideCheck()
                && (gpsLockStatus == GpsLockStatus.LOCK.getOrdinal()
                || gpsLockStatus == GpsLockStatus.REQ_UNLOCK.getOrdinal()
                || gpsLockStatus == GpsLockStatus.UNLOCK_FAILED.getOrdinal())) {
            buttonUnlockRide.setVisibility(View.VISIBLE);
            buttonLockRide.setVisibility(View.GONE);
        } else {
            buttonLockRide.setVisibility(View.VISIBLE);
            buttonUnlockRide.setVisibility(View.GONE);
        }

    }

    @Override
    public void onNoDriverHelpPushReceived(JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(isNewUI && P_INITIAL == passengerScreenMode) {
                    dialogNoDriverHelp(jsonObject);
                }
            }
        });
    }

    Dialog noDriverFoundHelpDialog;
    private void dialogNoDriverHelp(final JSONObject jsonObject) {
        try {
            if(noDriverFoundHelpDialog != null && noDriverFoundHelpDialog.isShowing()) {
                noDriverFoundHelpDialog.dismiss();
            }
            String msg = jsonObject.optString(KEY_MESSAGE, "");
            String title = jsonObject.optString(KEY_TITLE, "");
            String fuguChannelId = jsonObject.optString("fugu_channel_id", "");
            String fuguChannelName = jsonObject.optString("fugu_channel_name", "");
            String pickupAddress = jsonObject.optString("pickup_address", "");
            String fuguTags = jsonObject.getString("fugu_tags");
            JSONArray arrTags = new JSONArray(fuguTags);
            ArrayList<String> tags = new ArrayList<>();
            for(int i = 0; i < arrTags.length(); i++) {
                tags.add(arrTags.get(i).toString());
            }
            String altMessage = getString(R.string.text_help_me_to_book_a_ride, pickupAddress);
            noDriverFoundHelpDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            noDriverFoundHelpDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            noDriverFoundHelpDialog.setContentView(R.layout.dialog_rentals_lock);

            RelativeLayout relative = (RelativeLayout) noDriverFoundHelpDialog.findViewById(R.id.relative);

            WindowManager.LayoutParams layoutParams = noDriverFoundHelpDialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            noDriverFoundHelpDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            noDriverFoundHelpDialog.setCancelable(false);
            noDriverFoundHelpDialog.setCanceledOnTouchOutside(false);


            Button buttonOk = noDriverFoundHelpDialog.findViewById(R.id.bOk);
            TextView tvTitle = noDriverFoundHelpDialog.findViewById(R.id.tvTitle);
            TextView tvMessage = noDriverFoundHelpDialog.findViewById(R.id.tvMessage);
            TextView tvTitleHelp = noDriverFoundHelpDialog.findViewById(R.id.tvTitleHelp);
            tvTitleHelp.setVisibility(View.VISIBLE);
            ImageView imageViewLock = noDriverFoundHelpDialog.findViewById(R.id.image_view_lock);
            tvMessage.setText(msg);
            tvTitle.setText(title);
            imageViewLock.setBackgroundResource(R.drawable.ic_support);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (ASSL.Xscale() * 260), (int) (ASSL.Xscale() * 260));
            params.setMargins((int) (ASSL.Xscale() * 40), (int) (ASSL.Xscale() * 40), 0, 0);
            imageViewLock.setLayoutParams(params);
            buttonOk.setText(getString(R.string.chat_with_us));
            ImageView imageViewClose = noDriverFoundHelpDialog.findViewById(R.id.ivClose);

            imageViewClose.setOnClickListener(v -> noDriverFoundHelpDialog.dismiss());
            buttonOk.setOnClickListener(v -> {
				ChatByUniqueIdAttributes chatAttr = new ChatByUniqueIdAttributes.Builder()
						.setTransactionId(fuguChannelId)
						.setUserUniqueKey(String.valueOf(Data.getFuguUserData().getUserId()))
						.setChannelName(fuguChannelName)
						.setTags(tags)
						.setMessage(new String[]{altMessage})
						.build();
				HippoConfig.getInstance().openChatByUniqueId(chatAttr);
                noDriverFoundHelpDialog.dismiss();
            });
            relative.setOnClickListener(v -> noDriverFoundHelpDialog.dismiss());

            noDriverFoundHelpDialog.show();
            Prefs.with(this).save(KEY_PUSH_NO_DRIVER_FOUND_HELP, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Dialog rentalLockDialog;
    private void dialogRentalLock(Activity activity) {
        try {
            rentalLockDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            rentalLockDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            rentalLockDialog.setContentView(R.layout.dialog_rentals_lock);

            RelativeLayout relative = (RelativeLayout) rentalLockDialog.findViewById(R.id.relative);

            WindowManager.LayoutParams layoutParams = rentalLockDialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            rentalLockDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            rentalLockDialog.setCancelable(true);
            rentalLockDialog.setCanceledOnTouchOutside(true);


            Button buttonOk = rentalLockDialog.findViewById(R.id.bOk);
            ImageView imageViewClose = rentalLockDialog.findViewById(R.id.ivClose);

            imageViewClose.setOnClickListener(v -> rentalLockDialog.dismiss());
            buttonOk.setOnClickListener(v -> rentalLockDialog.dismiss());
            relative.setOnClickListener(v -> rentalLockDialog.dismiss());

            rentalLockDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScratchCardRevealed() {

    }

    @Override
    public void onCancelClick(boolean isPickup) {
        setTipAmountToZero(true);
    }

    @Override
    public void onOkClick(boolean isPickup) {
        onReqestRideConfirmClick();
    }

    private void checkForYoutubeIntent(){
		if(!Prefs.with(this).getBoolean(Constants.SP_YOUTUBE_TUTORIAL_SKIPPED, false)
				&& Data.userData != null
				&& Data.userData.getReferralMessages().getMultiLevelReferralEnabled()
				&& Data.userData.getReferralMessages().getReferralImages() != null){
			String youtubeId = "";
			for(MediaInfo mi : Data.userData.getReferralMessages().getReferralImages()){
				if(mi.checkIsYoutubeVideo()){
					youtubeId = mi.getYoutubeId();
				}
			}
			if(!TextUtils.isEmpty(youtubeId)) {
				startActivity(YoutubeVideoActivity.createIntent(this, youtubeId));
			}
		}
	}

    @Override
    public void onCancelClick() {
        setTipAmountToZero(true);
    }

    @Override
    public void onOkClick() {
        mRequestType = 1;
        onReqestRideConfirmClick();
    }

    @Override
    public void onCancelClicked() {
        setTipAmountToZero(true);
    }

    @Override
    public void onCallDriverOkClicked() {
        mRequestType = 2;
        onReqestRideConfirmClick();
    }

	@Override
	public void onEditDropConfirm(@Nullable LatLng dropLatLng, @Nullable String dropAddress, @Nullable String dropName, Integer poolFareId) {
    	SearchResult searchResult = new SearchResult(dropName, dropAddress, "", dropLatLng.latitude, dropLatLng.longitude);
		updateDropToUIAndServerApi(searchResult, poolFareId);
	}
}
