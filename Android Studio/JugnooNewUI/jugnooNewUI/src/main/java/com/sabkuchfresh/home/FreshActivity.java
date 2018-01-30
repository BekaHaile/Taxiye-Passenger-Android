package com.sabkuchfresh.home;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.fugu.FuguNotificationConfig;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.jugnoo.pay.activities.PaySDKUtils;
import com.jugnoo.pay.models.MessageRequest;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sabkuchfresh.adapters.FreshSortingAdapter;
import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.bus.AddressSearch;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.commoncalls.ApiFetchRestaurantMenu;
import com.sabkuchfresh.datastructure.CheckoutSaveData;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;
import com.sabkuchfresh.dialogs.FreshSortDialog;
import com.sabkuchfresh.feed.ui.fragments.FeedAddPostFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedChangeCityFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedClaimHandleFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedHomeFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedNotificationsFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedPostDetailFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedReserveSpotFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedSpotReservedSharingFragment;
import com.sabkuchfresh.feed.ui.views.TypeWriterTextView.Typewriter;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.AnywhereHomeFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.fragments.DeliveryStoresFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.fragments.FreshFragment;
import com.sabkuchfresh.fragments.FreshHomeFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.GroceryFragment;
import com.sabkuchfresh.fragments.MealAddonItemsFragment;
import com.sabkuchfresh.fragments.MealFragment;
import com.sabkuchfresh.fragments.MealsBulkOrderFragment;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.fragments.MenusFragment;
import com.sabkuchfresh.fragments.MenusItemCustomizeFragment;
import com.sabkuchfresh.fragments.MenusSearchFragment;
import com.sabkuchfresh.fragments.MerchantInfoFragment;
import com.sabkuchfresh.fragments.NewFeedbackFragment;
import com.sabkuchfresh.fragments.RestaurantAddReviewFragment;
import com.sabkuchfresh.fragments.RestaurantImageFragment;
import com.sabkuchfresh.fragments.RestaurantReviewsListFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsCheckoutFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsHomeFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsOrderStatusFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsProductsFragment;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
import com.sabkuchfresh.retrofit.model.DeliveryStore;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SubItemCompareAtoZ;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceHighToLow;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceLowToHigh;
import com.sabkuchfresh.retrofit.model.SubItemComparePriority;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemCompareAtoZ;
import com.sabkuchfresh.retrofit.model.menus.ItemComparePriceHighToLow;
import com.sabkuchfresh.retrofit.model.menus.ItemComparePriceLowToHigh;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.CustomTypeFaceSpan;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.DeleteCacheIntentService;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.OrderStatusFragment;
import product.clicklabs.jugnoo.PaperDBKeys;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.apis.ApiLoginUsingAccessToken;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.DeepLinkAction;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.home.dialogs.PaytmRechargeDialog;
import product.clicklabs.jugnoo.home.dialogs.PushDialog;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.retrofit.OfferingsVisibilityResponse;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemFragment;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemsListFragment;
import product.clicklabs.jugnoo.support.fragments.SupportRideIssuesFragment;
import product.clicklabs.jugnoo.tutorials.NewUserFlow;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by shankar on 4/6/16.
 */
public class FreshActivity extends BaseAppCompatActivity implements PaymentResultWithDataListener, GAAction, GACategory, PaperDBKeys {

    private final String TAG = FreshActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;

    private RelativeLayout relativeLayoutContainer;

    public TextView textViewMinOrder;

    private MenuBar menuBar;
    private TopBar topBar;
    private FABViewTest fabViewTest;
    private ImageView fabViewFatafat;
    private TransactionUtils transactionUtils;

    private ProductsResponse productsResponse;
    private SuperCategoriesData superCategoriesData;
    private UserCheckoutResponse userCheckoutResponse;

    private String selectedAddress = "";
    private LatLng selectedLatLng;

    private int selectedAddressId = 0;
    private String selectedAddressType = "";
    private String splInstr = "";
    private Slot slotSelected, slotToSelect;
    private PaymentOption paymentOption;

    private List<DeliveryAddress> deliveryAddresses;
    private ArrayList<SortResponseModel> slots = new ArrayList<>();

    public String mContactNo = "";

    private View topView;
    private FetchFeedbackResponse.Review currentReview;
    private FuguNotificationConfig fuguNotificationConfig  = new FuguNotificationConfig();
    /**
     * this holds the reference for the Otto Bus which we declared in LavocalApplication
     */
    protected Bus mBus;
    private double totalPrice = 0;
    private int totalQuantity = 0;

    public boolean updateCart = false;

    public float scale = 0f;

    public boolean locationSearchShown = false;
    public boolean canOrder = false;

    public int freshSort = -1;
    public int mealSort = -1;
    public int menusSort = -1;

    private VendorMenuResponse vendorMenuResponse;

    public AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private static final float COLLAPSE_TOOLBAR_HEIGHT = 270f;

    public CallbackManager callbackManager;
    public boolean isTimeAutomatic;
    public CollapsingToolbarLayout collapsingToolbar;
    private View feedHomeAddPostView;
    private Typewriter tvAddPost;
    private ImageView ivProfilePic;
    public int currentOffsetFeedHomeAppBar;
    public boolean filtersChanged = false;
    private boolean showingEarlyBirdDiscount;
    private boolean appTypeDeliveryInBackground;
    public RelativeLayout rlfabViewFatafat;
    public TextView tvFatfatChatIconText;
    public int lastAppTypeOpen;
    private KeyboardLayoutListener mParentKeyboardLayoutListener;
    private KeyboardLayoutListener.KeyBoardStateHandler mChildKeyboardListener;



    public View getFeedHomeAddPostView() {
        return feedHomeAddPostView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_fresh);
			ButterKnife.bind(this);
            initFatafatChatIcon();

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
            setUpAddPostForFeedFragment();
            appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
            collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
            callbackManager = CallbackManager.Factory.create();

            Data.currentActivity = FreshActivity.class.getName();
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            new ASSL(this, drawerLayout, 1134, 720, false);
            scale = getResources().getDisplayMetrics().density;

            relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

            if(savedInstanceState!=null && savedInstanceState.containsKey("showingEarlyBirdDiscount")){
                showingEarlyBirdDiscount = savedInstanceState.getBoolean("showingEarlyBirdDiscount");
            }

            try {
                mBus = ((MyApplication) getApplication()).getBus();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0) {
                    Data.latitude = Data.loginLatitude;
                    Data.longitude = Data.loginLongitude;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            resetAddressFields();

            String lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);

           /* if (getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE)) {
                if (lastClientId != null && lastClientId.equalsIgnoreCase(Config.getFeedClientId())){
                    SearchResult searchResult = getGson().fromJson(Prefs.with(this).getString(Constants.SP_ASKLOCAL_LAST_ADDRESS_OBJ,
                            Constants.EMPTY_JSON_OBJECT), SearchResult.class);
                    if(searchResult.getLatitude() != null && searchResult.getLongitude() != null){
                        setSelectedLatLng(searchResult.getLatLng());
                    } else {
                        setSelectedLatLng(new LatLng(Data.latitude, Data.longitude));
                    }
                } else {
                    LatLng latLng;
                    if(getAppType() == AppConstant.ApplicationType.FEED){
                        SearchResult searchResult = getGson().fromJson(Prefs.with(this).getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ,
                                Constants.EMPTY_JSON_OBJECT), SearchResult.class);
                        if(searchResult.getLatitude() != null && searchResult.getLongitude() != null){
                            latLng = searchResult.getLatLng();
                        } else {
                            latLng = new LatLng(Data.latitude, Data.longitude);
                        }
                    } else {
                        latLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
                                getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude));
                    }
                    Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);
                    setSelectedLatLng(latLng);
                }
            }*/
            LatLng  latLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
            getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude));
            Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);


            setSelectedLatLng(latLng);
            textViewMinOrder = (TextView) findViewById(R.id.textViewMinOrder);
            textViewMinOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAppType() == AppConstant.ApplicationType.MEALS
                            && Data.getMealsData() != null
                            && Data.getMealsData().getOfferStripMeals() != null
                            && !TextUtils.isEmpty(Data.getMealsData().getOfferStripMeals().getTextToDisplay())){

                        try {
                            Data.deepLinkIndex = Integer.parseInt(Data.getMealsData().getOfferStripMeals().getDeepIndex());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } if((getAppType() == AppConstant.ApplicationType.DELIVERY_CUSTOMER) && getMenusFragment()!=null
                            && getMenusFragment().getCurrentStripInfo() != null
                            && !TextUtils.isEmpty(getMenusFragment().getCurrentStripInfo().getText())){

                        try {
                            Data.deepLinkIndex = getMenusFragment().getCurrentStripInfo().getDeepIndex();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (getAppType() == AppConstant.ApplicationType.FEED
                            && Data.getFeedData() != null
                            && Data.getFeedData().getBottomStrip() != null
                            && !TextUtils.isEmpty(Data.getFeedData().getBottomStrip().getTextToDisplay())){
                        try {
                            Data.deepLinkIndex = Integer.parseInt(Data.getFeedData().getBottomStrip().getDeepIndex());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }else
                    openDeepIndex();
                }
            });

            topView = findViewById(R.id.topBarMain);

            menuBar = new MenuBar(this, drawerLayout);
            topBar = new TopBar(this, drawerLayout);
            fabViewTest = new FABViewTest(this, findViewById(R.id.relativeLayoutFABTest));

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(fabViewTest != null) {
                        fabViewTest.showTutorial();
                    }
                }
            }, 1000);

            topBar.etSearch.addTextChangedListener(textWatcher);
            topBar.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    try {
                        getMenusFragment().searchRestaurant(topBar.etSearch.getText().toString().trim());
                        Utils.hideSoftKeyboard(FreshActivity.this,topBar.etSearch);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            menusCartSelectedLayout = new MenusCartSelectedLayout(this, findViewById(R.id.vMenusCartSaved));
            menusCartSelectedLayout.setVisibility(View.GONE);
            llCheckoutBar.setAlpha(0);

            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    if (drawerView.equals(llRightDrawer)) {
                        filtersChanged = false;
                    }
                    Utils.hideKeyboard(FreshActivity.this);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if(menuBar != null){
                        menuBar.getRecyclerViewMenu().scrollToPosition(0);
                    }
                    Utils.hideKeyboard(FreshActivity.this);
                    if(drawerView.equals(llRightDrawer)){
                        applyRealTimeFilters();
                        if(getMenusFilterFragment() != null){
                            getMenusFilterFragment().performBackPress(false);
                        }
                        topBar.ivFilterApplied.setVisibility(isFilterApplied() ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });



            View.OnClickListener checkoutOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeyboard(FreshActivity.this, topBar.etSearch);

                    int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                    updateItemListFromSPDB();
                    if (updateCartValuesGetTotalPrice().second > 0) {
                        if (getMealAddonItemsFragment() == null && isMealAddonItemsAvailable()) {
                            getTransactionUtils().addMealAddonItemsFragment(FreshActivity.this, relativeLayoutContainer);
                        } else {
                            openCart(appType, false);
                        }


                        if(getTopFragment()!=null && getTopFragment() instanceof VendorMenuFragment) {
                            GAUtils.event(getGaCategory(), RESTAURANT_HOME, CART+CLICKED);
                        } else if(getTopFragment() instanceof FreshFragment){
                            if(getFreshFragment().getSuperCategory() != null) {
                                GAUtils.event(FRESH, getFreshFragment().getSuperCategory().getSuperCategoryName(), CART + CLICKED);
                            }
                        } else {
                            GAUtils.event(getGaCategory(), HOME, CART+CLICKED);
                        }
                    } else {
                        Utils.showToast(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty));
                    }
                }
            };


            llCheckoutBar.setOnClickListener(checkoutOnClickListener);


            setOfferingData(lastClientId,true);


            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, getIntentFiler());

            openPushDialog();
            deepLinkAction.openDeepLink(FreshActivity.this, getSelectedLatLng());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mBus.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initCollapseToolBarViews();

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(Data.userData.getSignupTutorial() != null) {
                        if (Data.userData.getSignupTutorial().getDs1() == 1) {
                            startActivity(new Intent(FreshActivity.this, NewUserFlow.class));
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
                    fabViewTest.expandJeanieFirstTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);

        backPressedCount = 0;

        if(Data.getFuguChatBundle()!=null) {
            fuguNotificationConfig.handleFuguPushNotification(FreshActivity.this, Data.getFuguChatBundle());
            Data.setFuguChatBundle(null);
        }

        // add parent keyboard listener
        mParentKeyboardLayoutListener = new KeyboardLayoutListener(relativeLayoutContainer,
                null, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                if (mChildKeyboardListener != null) {
                    mChildKeyboardListener.keyboardOpened();
                }
            }

            @Override
            public void keyBoardClosed() {
                if (mChildKeyboardListener != null) {
                    mChildKeyboardListener.keyBoardClosed();
                }
            }
        });
        mParentKeyboardLayoutListener.setResizeTextView(false);
        relativeLayoutContainer.getViewTreeObserver().addOnGlobalLayoutListener(mParentKeyboardLayoutListener);


    }

    private void initFatafatChatIcon() {
        rlfabViewFatafat = (RelativeLayout)findViewById(R.id.rlMenuLabelfatat);
        fabViewFatafat = (ImageView) findViewById(R.id.menuLabelFatafat);
        tvFatfatChatIconText = (TextView) findViewById(R.id.tv_fatafat_icon_desc);
        /*
        fabViewFatafat.setMenuIcon(ContextCompat.getDrawable(this,R.drawable.ic_fatafat_chat_new));
        fabViewFatafat.setMenuButtonColorNormal(ContextCompat.getColor(this,R.color.fatafat_fab));
        fabViewFatafat.setMenuButtonColorPressed(ContextCompat.getColor(this,R.color.fatafat_fab_pressed));
        fabViewFatafat.setMenuButtonColorRipple(ContextCompat.getColor(this,R.color.fatafat_fab_pressed));
        fabViewFatafat.setOnMenuToggleListener(null);
        fabViewFatafat.setOnMenuButtonLongClickListener(null);*/
        findViewById(R.id.fab_fatafat_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDeliveryOpenInBackground()){
                    GAUtils.event(GACategory.FATAFAT3, GAAction.FATAFAT_FAB_CLICKED, GAAction.LABEL_ORDER_VIA_FATAFAT);
                    switchOffering(Config.getFeedClientId());
                }

            }
        });


        rlfabViewFatafat.setVisibility(View.GONE);
    }

    /**
     * Enables child fragments to listen for keyboard change event
     *
     * @param keyboardListener the keystate handler listener
     */
    public void registerForKeyBoardEvent(final KeyboardLayoutListener.KeyBoardStateHandler keyboardListener) {
        mChildKeyboardListener = keyboardListener;
    }

    /**
     * Unregisters the current associated keyboard listener
     */
    public void unRegisterKeyBoardListener() {
        mChildKeyboardListener =null;
    }

    private void setOfferingData(String lastClientId,boolean fromOncreate) {
        try {
            float marginBottom = 60f;

            if(fromOncreate
                    && Data.userData != null
                    && Data.userData.isRidesAndFatafatEnabled()
                    && (checkForReorderMenus(false) //either reorder case
                        ||!Prefs.with(this).getString(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "").equalsIgnoreCase(""))){ //or deeplink to other client id
                Config.setLastOpenedClientId(this, Config.getDeliveryCustomerClientId());
                lastClientId = Config.getDeliveryCustomerClientId();
            }


             if (lastClientId.equalsIgnoreCase(Config.getMealsClientId())) {
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);

            } else  if (lastClientId.equalsIgnoreCase(Config.getFreshClientId())) {
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);

            }
            if(!fromOncreate){
                updateItemListFromSPDB();
                updateCartValuesGetTotalPrice();

            }


            createAppCart(lastClientId);

            if (lastClientId.equalsIgnoreCase(Config.getMealsClientId())) {
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);

                addMealFragment(fromOncreate);
//                    addProsHomeFragment();



            } else if (lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                openCart();
                addGroceryFragment();
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.GROCERY);
            } else if (lastClientId.equalsIgnoreCase(Config.getMenusClientId()) || lastClientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())) {
                getTopBar().etSearch.setHint(getString(R.string.search_items_menus));
                int appType = lastClientId.equalsIgnoreCase(Config.getMenusClientId())?AppConstant.ApplicationType.MENUS:
                        AppConstant.ApplicationType.DELIVERY_CUSTOMER;
                fetchFiltersFromSP(appType);
                openCart();
                if(appType== AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                    appTypeDeliveryInBackground = true;

                    addCustomerDeliveryFragment();
                }
                else{
                    if(fromOncreate){
                        addCustomerDeliveryFragment();
                    }else{
                        addMenusFragment();

                    }
                }

                Prefs.with(this).save(Constants.APP_TYPE, appType);


                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction()
                                .replace(llRightDrawer.getId(), new MenusFilterFragment(), MenusFilterFragment.class.getName())
                                .commitAllowingStateLoss();
                    }
                });

            } else if (lastClientId.equalsIgnoreCase(Config.getFeedClientId())) {

          /*      if(Data.getFeedData().getFeedActive()) {
                    if(Data.getFeedData().getHasHandle()){



                        addFeedFragment();

                    }else{

                        addClaimHandleFragment();
                    }

                } else {
                    addFeedReserveSpotFragment();
                }*/
                addAnywhereHomeFragment(fromOncreate);
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FEED);

            } else if (lastClientId.equalsIgnoreCase(Config.getProsClientId())) {
                addProsHomeFragment();
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.PROS);
            } else {
                Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);

                openCart();
                addFreshHomeFragment(fromOncreate);
            }

            if(!lastClientId.equalsIgnoreCase(Config.getMenusClientId()) && !lastClientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())){
                Prefs.with(this).save(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "");
            }

            int dpAsPixels = (int) (marginBottom * scale + 0.5f);

            // Set Jeanie Padding Bottom if Offering strip shown so that they do not overlap.
            if ((getAppType() == AppConstant.ApplicationType.MEALS
                    && Data.getMealsData() != null
                    && Data.getMealsData().getOfferStripMeals() != null
                    && !TextUtils.isEmpty(Data.getMealsData().getOfferStripMeals().getTextToDisplay()))
                    ||
                    (!isDeliveryOpenInBackground() && getAppType() == AppConstant.ApplicationType.FEED
                    && Data.getFeedData() != null
                    && Data.getFeedData().getBottomStrip() != null
                    && !TextUtils.isEmpty(Data.getFeedData().getBottomStrip().getTextToDisplay()))
                    ) {
                marginBottom += 35f;
            }

            fabViewTest.setMenuLabelsRightTestPadding(marginBottom);
            fabViewTest.setRlGenieHelpBottomMargin(200f);
            float scale = getResources().getDisplayMetrics().density;
            int paddingBottom = (int) (marginBottom * scale + 0.5f);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) rlfabViewFatafat.getLayoutParams();
            layoutParams.leftMargin = 0 ;
            layoutParams.topMargin= 0;
            layoutParams.rightMargin = (int) (40f * ASSL.Yscale());
            layoutParams.bottomMargin = paddingBottom;
            rlfabViewFatafat.setLayoutParams(layoutParams);
            rlfabViewFatafat.invalidate();;
            Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
            Data.AppType = getAppType();


        } catch (Exception e) {
            e.printStackTrace();
            addFreshHomeFragment(fromOncreate);
        }
    }



    public boolean checkForReorderMenus(boolean hitRestMenu) {

        boolean reorderCase = false;
        try {
            Integer restaurantId = Prefs.with(this).getInt(Constants.ORDER_STATUS_PENDING_ID,-1);
            Integer orderId = Prefs.with(this).getInt(Constants.ORDER_STATUS_ORDER_ID,-1);
            String jsonArray = Prefs.with(this).getString(Constants.ORDER_STATUS_JSON_ARRAY,null);
            LatLng reorderLatLng = Prefs.with(this).getObject(Constants.ORDER_STATUS_LAT_LNG,LatLng.class);
            String reoderAddress = Prefs.with(this).getString(Constants.ORDER_STATUS_ADDRESS,null);
            if(restaurantId!=-1 && jsonArray!=null){

                reorderCase = true;
                if(hitRestMenu) {
                    fetchRestaurantMenuAPI(restaurantId, true, new JSONArray(jsonArray), reorderLatLng, orderId, reoderAddress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(hitRestMenu) {
            Prefs.with(this).remove(Constants.ORDER_STATUS_PENDING_ID);
            Prefs.with(this).remove(Constants.ORDER_STATUS_JSON_ARRAY);
            Prefs.with(this).remove(Constants.ORDER_STATUS_ORDER_ID);
            Prefs.with(this).remove(Constants.ORDER_STATUS_LAT_LNG);
        }
        return reorderCase;
    }

    public void setReorderLatlngToAdrress(LatLng reorderLatLng,String  reoderAddress) {
        if(reorderLatLng!=null){
            SearchResult searchResult = homeUtil.getNearBySavedAddress(this, reorderLatLng,
                    Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, true);
            if (searchResult!=null) {
                setSelectedAddress(searchResult.getAddress());
                setSelectedLatLng(searchResult.getLatLng());
                setSelectedAddressId(searchResult.getId());
                setSelectedAddressType(searchResult.getName());
                saveDeliveryAddressModel();

            }else{
                setSelectedAddress(reoderAddress);
                setSelectedLatLng(reorderLatLng);
                setSelectedAddressId(0);
                setSelectedAddressType("");
            }
            setRefreshCart(true);
            saveAddressRefreshBoolean(this,false);
            if(getFreshCheckoutMergedFragment()!=null){
                getFreshCheckoutMergedFragment().setDeliveryAddressUpdated(true);
            }
        }
    }

    private void setUpAddPostForFeedFragment() {
        feedHomeAddPostView = findViewById(R.id.add_post);
        feedHomeAddPostView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getTopFragment()  instanceof FeedHomeFragment){
                    openFeedAddPostFragment(null);
                    GAUtils.event(FEED, HOME, ADD+POST_BAR+CLICKED);
                }
            }
        });
        tvAddPost = (Typewriter) findViewById(R.id.tvAddPost);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
    }

    public Typewriter getTvAddPost(){
        return tvAddPost;
    }


    // intentFilter to add multiple actions
    public IntentFilter getIntentFiler(){
        IntentFilter intent = new IntentFilter();
        intent.addAction(Data.LOCAL_BROADCAST);
        intent.addAction(Constants.INTENT_ACTION_RAZOR_PAY_CALLBACK);
        return intent;
    }

    public void setSortingList(Fragment fragment) {
        slots.clear();
        if (fragment instanceof FreshFragment) {
            slots.add(new SortResponseModel(0, "A-Z", false));
            slots.add(new SortResponseModel(1, "Popularity", false));
            slots.add(new SortResponseModel(2, "Price: Low to High", false));
            slots.add(new SortResponseModel(3, "Price: High to Low", false));
//            if(freshSort < 0 || freshSort >= slots.size()){
//                freshSort = 0;
//            }
//            slots.get(freshSort).check = true;
        } else if (fragment instanceof VendorMenuFragment) {
            slots.add(new SortResponseModel(0, "A-Z", false));
            slots.add(new SortResponseModel(1, "Price: Low to High", false));
            slots.add(new SortResponseModel(2, "Price: High to Low", false));
//            if(menusSort < 0 || menusSort >= slots.size()){
//                menusSort = 0;
//            }
//            slots.get(menusSort).check = true;
        }
    }

    public void updateSortSelectedFromAPI(Fragment fragment, JSONObject jObj) {
        if (fragment instanceof FreshFragment) {
            if (freshSort == -1) {
                int sortedBy = jObj.optInt(Constants.SORTED_BY, 0);
                freshSort = sortedBy;
                slots.get(sortedBy).setCheck(true);
            } else {
                slots.get(freshSort).setCheck(true);
            }
            onSortEvent(new SortSelection(freshSort));
        }
    }


    public void intentToShareActivity() {
        Intent intent = new Intent(FreshActivity.this, ShareActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    public void openCart() {
        try {
            if (getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE).getBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, false)) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (getAppType() != AppConstant.ApplicationType.MENUS && productsResponse != null && productsResponse.getCategories() != null) {
                                updateItemListFromSPDB();
                                llCheckoutBar.performClick();
                            } else if (isMenusOrDeliveryOpen() && getMenuProductsResponse() != null && getMenuProductsResponse().getCategories() != null) {
                                updateItemListFromSPDB();
                                llCheckoutBar.performClick();
                            } else {
                                updateCart = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 400);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            // Get extra data included in the Intent
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (intent.getAction()) {
                            case Data.LOCAL_BROADCAST:
                                int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                                if (flag == -1) {
                                    String message = intent.getStringExtra("message");
                                    int type = intent.getIntExtra("open_type", 0);

                                    if (type == 1) {
                                        intentToShareActivity();
                                    } else if (type == 10) {
                                        setRefreshCart(true);
                                    }
                                } else {
                                    if (flag == PushFlags.DISPLAY_MESSAGE.getOrdinal()
                                            || flag == PushFlags.PROS_STATUS_SILENT.getOrdinal()) {
                                        // for refreshing generate feed api on feed like comment related pushes
                                        if(intent.getIntExtra(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.FEED_PAGE.getOrdinal()
                                                && intent.getIntExtra(Constants.KEY_POST_ID, -1) != -1) {
                                            if(getTopFragment() instanceof FeedHomeFragment) {
//                                            getFeedHomeFragment().fetchFeedsApi(false, false);
                                            }
                                        }
                                        else if((intent.getIntExtra(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.MENUS_PAGE.getOrdinal() ||
                                                intent.getIntExtra(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal())
                                                && intent.getIntExtra(Constants.KEY_RESTAURANT_ID, -1) > 0
                                                && intent.getIntExtra(Constants.KEY_FEEDBACK_ID, -1) > 0){
                                            if(getRestaurantReviewsListFragment() != null){
                                                getRestaurantReviewsListFragment().fetchFeedback(false);
                                            }
                                        }
                                        else if(intent.getIntExtra(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.PROS_PAGE.getOrdinal()
                                                && intent.getIntExtra(Constants.KEY_JOB_ID, -1) > 0){
                                            if(getProsHomeFragment() != null){
                                                if(intent.getIntExtra(Constants.KEY_IS_FEEDBACK_PENDING, 0) == 1){
                                                    if(!(getTopFragment() instanceof ProsHomeFragment)){
                                                        getSupportFragmentManager().popBackStack(ProsProductsFragment.class.getName(),
                                                                FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    }
                                                    getProsHomeFragment().onResume();
                                                } else {
                                                    getProsHomeFragment().getSuperCategoriesAPI(true);
                                                }
                                            }
                                        }
                                        else {
                                            // else normal case of displaying push dialog
                                            Data.getDeepLinkIndexFromIntent(FreshActivity.this, intent);
                                            openPushDialog();
                                        }
                                    } else if (PushFlags.INITIATE_PAYTM_RECHARGE.getOrdinal() == flag) {
                                        String message = intent.getStringExtra(Constants.KEY_MESSAGE);
                                        Data.userData.setPaytmRechargeInfo(JSONParser.parsePaytmRechargeInfo(new JSONObject(message)));
                                        openPaytmRechargeDialog();
                                    } else if (PushFlags.STATUS_CHANGED.getOrdinal() == flag) {
                                        String clientId = intent.getStringExtra(Constants.KEY_CLIENT_ID);
                                        Fragment fragment = getTopFragment();
                                        if(fragment instanceof FreshCheckoutMergedFragment && intent.hasExtra(Constants.ICICI_ORDER_STATUS)){
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(
                                                    new Intent(Constants.INTENT_ICICI_PAYMENT_STATUS_UPDATE)
                                                            .putExtra(Constants.ICICI_ORDER_STATUS,intent.getIntExtra(Constants.ICICI_ORDER_STATUS,Constants.NO_VALID_STATUS))
                                                            .putExtra(Constants.IS_MENUS_OR_DELIVERY,false)
                                                            .putExtra(Constants.KEY_MESSAGE,intent.getStringExtra(Constants.KEY_MESSAGE))
                                                            .putExtra(Constants.KEY_ORDER_ID,intent.getIntExtra(Constants.KEY_ORDER_ID,0)));
                                        }

                                        if(Data.userData.isRidesAndFatafatEnabled() && isDeliveryOpenInBackground()){
                                            if(getMenusFragment()!=null) {
                                                ((MenusFragment) fragment).getAllMenus(true, getSelectedLatLng(), false, null, MenusFragment.TYPE_API_MENUS_ADDRESS_CHANGE);
                                            }
                                        }if (!Data.userData.isRidesAndFatafatEnabled() && fragment instanceof MealFragment && FreshActivity.this.hasWindowFocus()) {
                                            ((MealFragment) fragment).getAllProducts(true, getSelectedLatLng());
                                        }
                                            Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                            intent1.putExtra(Constants.KEY_FLAG, flag);
                                            intent1.putExtra(Constants.KEY_ORDER_ID, intent.getIntExtra(Constants.KEY_ORDER_ID, -1));
                                            intent1.putExtra(Constants.KEY_CLOSE_TRACKING, intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0));
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);

                                    } else if (PushFlags.MENUS_STATUS.getOrdinal() == flag || PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
                                        Fragment fragment = getMenusFragment();
                                        if(getTopFragment()!=null && getTopFragment() instanceof FreshCheckoutMergedFragment && intent.hasExtra(Constants.ICICI_ORDER_STATUS)){
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(
                                                    new Intent(Constants.INTENT_ICICI_PAYMENT_STATUS_UPDATE)
                                                            .putExtra(Constants.ICICI_ORDER_STATUS,intent.getIntExtra(Constants.ICICI_ORDER_STATUS,Constants.NO_VALID_STATUS))
                                                            .putExtra(Constants.IS_MENUS_OR_DELIVERY,true)
                                                            .putExtra(Constants.KEY_MESSAGE,intent.getStringExtra(Constants.KEY_MESSAGE))
                                                            .putExtra(Constants.KEY_ORDER_ID,intent.getIntExtra(Constants.KEY_ORDER_ID,0)));

                                        }
                                        if (fragment != null) {
                                            if(getMenusFragment()!=null) {
                                                ((MenusFragment) fragment).getAllMenus(true, getSelectedLatLng(), false, null, MenusFragment.TYPE_API_MENUS_ADDRESS_CHANGE);
                                            }
                                        }
                                            Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                            intent1.putExtra(Constants.KEY_FLAG, flag);
                                            intent1.putExtra(Constants.KEY_ORDER_ID, intent.getIntExtra(Constants.KEY_ORDER_ID, -1));
                                            intent1.putExtra(Constants.KEY_CLOSE_TRACKING, intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0));
                                            intent1.putExtra(Constants.KEY_CLIENT_ID, intent.getStringExtra(Constants.KEY_CLIENT_ID));
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);

                                    } else if (Constants.OPEN_DEEP_INDEX == flag) {
                                        deepLinkAction.openDeepLink(FreshActivity.this, getSelectedLatLng());
                                    } else if (Constants.OPEN_APP_CLIENT_ID == flag && intent.hasExtra(Constants.KEY_CLIENT_ID)) {
                                        final String clientId = intent.getStringExtra(Constants.KEY_CLIENT_ID);
                                        if(!Config.getLastOpenedClientId(FreshActivity.this).equalsIgnoreCase(clientId)){
                                            switchOfferingViaClientId(clientId);
                                        }
                                    }
                                }
                                break;


                            case Constants.INTENT_ACTION_RAZOR_PAY_CALLBACK:
                                DialogPopup.dismissLoadingDialog();
                                String response = intent.getStringExtra(Constants.KEY_RESPONSE);
                                if (!TextUtils.isEmpty(response)) {
                                    getFreshCheckoutMergedFragment().razorpayServiceCallback(new JSONObject(response));
                                } else {
                                    getFreshCheckoutMergedFragment().razorpayServiceCallback(null);
                                }
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void switchOfferingViaClientId(final String clientId) {
        clearFragmentStackTillLast();

        handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(clientId.equalsIgnoreCase(Config.getMenusClientId())
						&& getMenusFragment() != null){

				    if(getMenusFragment().isMenusApiInProgress() || getMenusResponse()==null){
                        getMenusFragment().setPendingCategoryAPI(new MenusResponse.Category(Constants.CATEGORY_ID_RESTAURANTS, Constants.CATEGORY_RESTAURANTS_NAME));
                    }else{
                        getMenusFragment().switchCategory(new MenusResponse.Category(Constants.CATEGORY_ID_RESTAURANTS, Constants.CATEGORY_RESTAURANTS_NAME));

                    }

                } else {
					switchOffering(clientId);
				}
			}
		},10);
    }

    public Bus getBus() {
        return mBus;
    }

    private boolean isLocationChangeCheckedAfterResume;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            Data.setLastActivityOnForeground(FreshActivity.this);
            isLocationChangeCheckedAfterResume = false;
            isTimeAutomatic();
            HomeActivity.switchAppOfClientId(this, getSelectedLatLng());


            if (!HomeActivity.checkIfUserDataNull(this)) {

                menuBar.setUserData();
                try {
                    if(getAppType() == AppConstant.ApplicationType.FEED) {
                        int width = getResources().getDimensionPixelSize(R.dimen.dp_40);
                        Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform())
                                .resize(width, width).centerCrop()
                                .placeholder(R.drawable.placeholder_img)
                                .into(ivProfilePic);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }


                if(getAppType()!= AppConstant.ApplicationType.FEED) {
                    fetchWalletBalance(this);
                }

                MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 60000l);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                        Data.userData.getIntegratedJugnooEnabled() == 1) {
                    if ((getFreshHomeFragment() != null && !getFreshHomeFragment().isHidden()) ||
                            (getMealFragment() != null && !getMealFragment().isHidden()) ||
                            (getGroceryFragment() != null && !getGroceryFragment().isHidden())
                            || (getMenusFragment() != null && !getMenusFragment().isHidden())
                            || (getFeedHomeFragment() != null && !getFeedHomeFragment().isHidden())
                            || (getAnywhereHomeFragment() != null && !getAnywhereHomeFragment().isHidden())
                            || (getFeedReserveSpotFragment() != null && !getFeedReserveSpotFragment().isHidden())
                            || (getFeedSpotReservedSharingFragment() != null && !getFeedSpotReservedSharingFragment().isHidden())
							|| (getFeedClaimHandleFragment() != null && !getFeedClaimHandleFragment().isHidden())
                            || (getProsHomeFragment() != null && !getProsHomeFragment().isHidden())
                            ) {
                        if(!fabViewTest.isFabtoggleModeOn()){
                            fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                            fabViewTest.setFABButtons(false);
                        }

                    }
                } else {
                    if(!fabViewTest.isFabtoggleModeOn()){
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

                    }
                }


                // to check from Last selected address that if its id is -10(marked for special case of
                // deleting selected delivery address from address book), if it is the case, clear
                // address related local variables and save empty jsonObject in SP for setting
                // delivery location to current location.
                SearchResult searchResultLastFMM = gson.fromJson(Prefs.with(this)
                        .getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
                if(searchResultLastFMM.getId() == null || searchResultLastFMM.getId() == -10){
                    setSelectedLatLng(new LatLng(Data.latitude, Data.longitude));
                    setSelectedAddress("");
                    int previousAddressId = getSelectedAddressId();
                    setSelectedAddressId(0);
                    setSelectedAddressType("");

                    Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);

                    DeliveryAddressModel deliveryAddressModel = getDeliveryAddressModel();
                    if(deliveryAddressModel != null && deliveryAddressModel.getId() == previousAddressId){
                        saveDeliveryAddressModel(deliveryAddressModel.getAddress(), deliveryAddressModel.getLatLng(), 0, "");
                    }

                    getAddressAndFetchOfferingData(getSelectedLatLng(),getAppType());
                }
                // else if selected address is updated by user, updating address related local variables
                // from SP search result
                else if(searchResultLastFMM.getId() > 0 && searchResultLastFMM.getId() == getSelectedAddressId()){
                    setSelectedLatLng(searchResultLastFMM.getLatLng());
                    setSelectedAddress(searchResultLastFMM.getAddress());
                    setSelectedAddressType(searchResultLastFMM.getName());

                    int previousAddressId = getSelectedAddressId();
                    DeliveryAddressModel deliveryAddressModel = getDeliveryAddressModel();
                    if(deliveryAddressModel != null && deliveryAddressModel.getId() == previousAddressId){
                        saveDeliveryAddressModel(deliveryAddressModel.getAddress(), deliveryAddressModel.getLatLng(), 0, "");
                    }
                }
                // else find any tagged address near current set location, if that is not tagged
                else if(getSelectedAddressId() == 0){
                    SearchResult searchResult = homeUtil.getNearBySavedAddress(FreshActivity.this, getSelectedLatLng(),
                            Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                    if(searchResult != null){
                        setSelectedAddress(searchResult.getAddress());
                        setSelectedLatLng(searchResult.getLatLng());
                        setSelectedAddressId(searchResult.getId());
                        setSelectedAddressType(searchResult.getName());
                        setAddressTextToLocationPlaceHolder();
                        saveOfferingLastAddress(getAppType());
                    }
                }

            }

            if(AccountActivity.updateMenuBar){
                menuBar.setProfileData();
                AccountActivity.updateMenuBar=false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resumeMethod() {
        menuBar.setUserData();

        fetchWalletBalance(this);
    }


    public void isTimeAutomatic(){
        try {
            int a= android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME);
            android.util.Log.i("TAG", "isTimeAutomatic: "+a);
            isTimeAutomatic=a==1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            android.util.Log.i("TAG", "isTimeAutomatic: "+false);
        }

    }

    public FreshHomeFragment getFreshHomeFragment() {
        return (FreshHomeFragment) getSupportFragmentManager().findFragmentByTag(FreshHomeFragment.class.getName());
    }

    public FreshFragment getFreshFragment() {
        return (FreshFragment) getSupportFragmentManager().findFragmentByTag(FreshFragment.class.getName());
    }

    public DeliveryStoresFragment getDeliveryStoresFragment() {
        return (DeliveryStoresFragment) getSupportFragmentManager().findFragmentByTag(DeliveryStoresFragment.class.getName());
    }

    public MealFragment getMealFragment() {
        return (MealFragment) getSupportFragmentManager().findFragmentByTag(MealFragment.class.getName());
    }

    public GroceryFragment getGroceryFragment() {
        return (GroceryFragment) getSupportFragmentManager().findFragmentByTag(GroceryFragment.class.getName());
    }

    public MenusFragment getMenusFragment() {
        return (MenusFragment) getSupportFragmentManager().findFragmentByTag(MenusFragment.class.getName());
    }

    public FeedHomeFragment getFeedHomeFragment(){
        return (FeedHomeFragment) getSupportFragmentManager().findFragmentByTag(FeedHomeFragment.class.getName());
    }

    public FeedNotificationsFragment getFeedNotificationsFragment(){
        return (FeedNotificationsFragment) getSupportFragmentManager().findFragmentByTag(FeedNotificationsFragment.class.getName());
    }

    public FeedReserveSpotFragment getFeedReserveSpotFragment(){
        return (FeedReserveSpotFragment) getSupportFragmentManager().findFragmentByTag(FeedReserveSpotFragment.class.getName());
    }

    public FeedSpotReservedSharingFragment getFeedSpotReservedSharingFragment(){
        return (FeedSpotReservedSharingFragment) getSupportFragmentManager().findFragmentByTag(FeedSpotReservedSharingFragment.class.getName());
    }

	public FeedClaimHandleFragment getFeedClaimHandleFragment(){
		return (FeedClaimHandleFragment) getSupportFragmentManager().findFragmentByTag(FeedClaimHandleFragment.class.getName());
	}

    public FeedPostDetailFragment getOfferingsCommentFragment(){
        return (FeedPostDetailFragment) getSupportFragmentManager().findFragmentByTag(FeedPostDetailFragment.class.getName());
    }
    public FeedAddPostFragment getFeedAddPostFragment(){
        return (FeedAddPostFragment) getSupportFragmentManager().findFragmentByTag(FeedAddPostFragment.class.getName());
    }

    public VendorMenuFragment getVendorMenuFragment() {
        return (VendorMenuFragment) getSupportFragmentManager().findFragmentByTag(VendorMenuFragment.class.getName());
    }

    public MerchantInfoFragment getMerchantInfoFragment(){
        return (MerchantInfoFragment) getSupportFragmentManager().findFragmentByTag(MerchantInfoFragment.class.getName());
    }

    public FreshCheckoutMergedFragment getFreshCheckoutMergedFragment() {
        return (FreshCheckoutMergedFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutMergedFragment.class.getName());
    }

    public AnywhereHomeFragment getAnywhereHomeFragment() {
        return (AnywhereHomeFragment) getSupportFragmentManager().findFragmentByTag(AnywhereHomeFragment.class.getName());
    }


    public RestaurantImageFragment getRestaurantImageFragment() {
        return (RestaurantImageFragment) getSupportFragmentManager().findFragmentByTag(RestaurantImageFragment.class.getName());
    }

    public RestaurantReviewsListFragment getRestaurantReviewsListFragment() {
        return (RestaurantReviewsListFragment) getSupportFragmentManager().findFragmentByTag(RestaurantReviewsListFragment.class.getName());
    }

    public RestaurantAddReviewFragment getRestaurantAddReviewFragment() {
        return (RestaurantAddReviewFragment) getSupportFragmentManager().findFragmentByTag(RestaurantAddReviewFragment.class.getName());
    }

    public DeliveryAddressesFragment getDeliveryAddressesFragment() {
        return (DeliveryAddressesFragment) getSupportFragmentManager().findFragmentByTag(DeliveryAddressesFragment.class.getName());
    }

    public AddToAddressBookFragment getAddToAddressBookFragmentDirect() {
        return (AddToAddressBookFragment) getSupportFragmentManager().findFragmentByTag(AddToAddressBookFragment.class.getName());
    }

    public TopBar getTopBar() {
        return topBar;
    }


    private FeedbackFragment getFeedbackFragment() {
        return (FeedbackFragment) getSupportFragmentManager().findFragmentByTag(FeedbackFragment.class.getName());
    }

    public ProsHomeFragment getProsHomeFragment() {
        return (ProsHomeFragment) getSupportFragmentManager().findFragmentByTag(ProsHomeFragment.class.getName());
    }

    public ProsCheckoutFragment getProsCheckoutFragment() {
        return (ProsCheckoutFragment) getSupportFragmentManager().findFragmentByTag(ProsCheckoutFragment.class.getName());
    }


    public Product product;
    public List<Product> productList = new ArrayList<>();

    public List<Product> getProduct() {
        productList.clear();
        if (getProductsResponse() != null
                && getProductsResponse().getCategories() != null) {
            for (Category category : getProductsResponse().getCategories()) {
                for (SubItem subItem : category.getSubItems()) {
                    if (subItem.getSubItemQuantitySelected() > 0) {
                        try {
                            String categoryName = "", itemName = "";
                            double price = 0.0;
                            int qty = 0, itemId = 0;

                            qty = subItem.getSubItemQuantitySelected();
                            categoryName = category.getCategoryName();
                            itemId = subItem.getSubItemId();
                            itemName = subItem.getSubItemName();
                            price = subItem.getPrice();

                            Product product = new Product()
                                    .setCategory(categoryName)
                                    .setId("" + itemId)
                                    .setName(itemName)
                                    .setPrice(price)
                                    .setQuantity(qty);
                            productList.add(product);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return productList;
    }

    public Pair<Double, Integer> updateCartValuesGetTotalPrice() {
        if (isMenusOrDeliveryOpen()) {
            return updateCartValuesGetTotalPriceMenus();
        } else {
            return updateCartValuesGetTotalPriceFMG();
        }
    }

    public Pair<Double, Integer> getSubItemInCartTotalPrice() {
        Pair<Double, Integer> pair;
        totalPrice = 0;
        totalQuantity = 0;
        try {
            ArrayList<SubItem> subItemsInCart = fetchCartList();
            for (SubItem subItem : subItemsInCart) {
                if (subItem.getSubItemQuantitySelected() > 0) {
                    totalQuantity++;
                    totalPrice = totalPrice + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pair = new Pair<>(totalPrice, totalQuantity);
        return pair;
    }

    private Pair<Double, Integer> updateCartValuesGetTotalPriceFMG() {
        return updateCartValuesGetTotalPriceFMG(null);
    }

    public Pair<Double, Integer> updateCartValuesGetTotalPriceFMG(SubItem subItemToUpdate) {
        saveItemListToDBFMG(subItemToUpdate);
        Pair<Double, Integer> pair = getSubItemInCartTotalPrice();
        try {
            if (getFreshFragment() != null) {
                setMinOrderAmountText(getFreshFragment());
            } else if (getFreshSearchFragment() != null) {
                setMinOrderAmountText(getFreshSearchFragment());
            }
            updateTotalAmountPrice(totalPrice, totalQuantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pair;
    }

    private Pair<Double, Integer> updateCartValuesGetTotalPriceMenus() {
        Pair<Double, Integer> pair;
        totalPrice = 0;
        totalQuantity = 0;
        try {
            if (getMenuProductsResponse() != null
                    && getMenuProductsResponse().getCategories() != null) {
                for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                    if (category.getSubcategories() != null) {
                        for (Subcategory subcategory : category.getSubcategories()) {
                            for (Item item : subcategory.getItems()) {
                                for (ItemSelected itemSelected : item.getItemSelectedList()) {
                                    if (itemSelected.getQuantity() > 0) {
                                        totalQuantity++;
                                        totalPrice = totalPrice + itemSelected.getTotalPriceWithQuantity();
                                    }
                                }
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            for (ItemSelected itemSelected : item.getItemSelectedList()) {
                                if (itemSelected.getQuantity() > 0) {
                                    totalQuantity++;
                                    totalPrice = totalPrice + itemSelected.getTotalPriceWithQuantity();
                                }
                            }
                        }
                    }
                }
                setMinOrderAmountText(getVendorMenuFragment());
                updateTotalAmountPrice(totalPrice, totalQuantity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveItemListToSPMenus();
        pair = new Pair<>(totalPrice, totalQuantity);
        return pair;
    }

    public void updateTotalAmountPrice(double totalPrice, int quantity) {
        try {
            if (totalPrice > 0) {
                if(!(getTopFragment() instanceof FreshCheckoutMergedFragment)
                        && !(getTopFragment() instanceof MealAddonItemsFragment)
                        && !(getTopFragment() instanceof FeedbackFragment)) {
                    llCheckoutBarSetVisibility(View.VISIBLE);
                } else {
                    llCheckoutBarSetVisibility(View.GONE);
                }
                tvCartAmount.setText(String.format(getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(totalPrice)));

                tvCheckoutItemsCount.setText(Utils.fromHtml(getString(quantity == 1 ?
                        R.string.checkout_bracket_item : R.string.checkout_bracket_items,
                        String.valueOf(quantity))));
            } else {
                llCheckoutBarSetVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fragmentUISetup(Fragment fragment) {
        try {
            // setting fatafat tutorial visibility
            if(fragment instanceof AnywhereHomeFragment &&
                    Data.getFeedData().getFatafatTutorialData()!=null &&
                    Data.getFeedData().getFatafatTutorialData().size()!=0){
                topBar.imgVwFatafatTutorial.setVisibility(View.VISIBLE);
            }
            else {
                topBar.imgVwFatafatTutorial.setVisibility(View.GONE);
            }

            int llSearchCartContainerVis = View.VISIBLE;
            int llSearchCartVis = View.VISIBLE;
            int llCartContainerVis = View.GONE;
            int ivSearchVis = View.GONE;
            int llSearchContainerVis = View.GONE;
            topBar.imageViewDelete.setVisibility(View.GONE);
            if (topView.getVisibility() != View.VISIBLE)
                topView.setVisibility(View.VISIBLE);
            fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            topBar.editTextDeliveryAddress.setVisibility(View.GONE);

            int rlFilterVis = View.GONE;
            topBar.imageViewBack.setImageResource(R.drawable.ic_back_selector);
            topBar.tvDeliveryAddress.setVisibility(View.GONE);
            int padding = (int) (20f * ASSL.minRatio());
            int visMinOrder = -1;
            topBar.progressWheelDeliveryAddressPin.setVisibility(View.GONE);
            int freshSortVis = View.GONE;
			int llAddToCartVis = View.GONE;
            int llPayViewContainerVis = View.GONE;
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            bRequestBooking.setVisibility(View.GONE);
            int fabFatafatVisibility = View.GONE;
            int deliveryAddressInTopBarVisibility = View.GONE;

            if (fragment instanceof FreshHomeFragment) {

                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                topBar.imageViewBack.setVisibility(isDeliveryOpenInBackground()?View.VISIBLE:View.GONE);
                drawerLayout.setDrawerLockMode(isDeliveryOpenInBackground()?DrawerLayout.LOCK_MODE_LOCKED_CLOSED:DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                }
                topBar.title.setVisibility(View.GONE);
                deliveryAddressInTopBarVisibility = View.VISIBLE;
                topBar.title.setText(getResources().getString(R.string.fatafat));
                topBar.getTvAddressLayoutTitle().setText(getResources().getString(R.string.fatafat));
                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof FreshFragment) {
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.fatafat));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);
                freshSortVis = View.VISIBLE;

            } else if(fragment instanceof DeliveryStoresFragment){
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.delivery_stores));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            } else if (fragment instanceof MealFragment) {
                llCartContainerVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                topBar.imageViewBack.setVisibility(isDeliveryOpenInBackground()?View.VISIBLE:View.GONE);
                drawerLayout.setDrawerLockMode(isDeliveryOpenInBackground()?DrawerLayout.LOCK_MODE_LOCKED_CLOSED:DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                }
                if ((Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) ) {
                    llSearchCartVis = View.GONE;
                }
                topBar.title.setVisibility(View.GONE);
                deliveryAddressInTopBarVisibility = View.VISIBLE;
                topBar.title.setText(getResources().getString(R.string.meals));
                topBar.getTvAddressLayoutTitle().setText(getResources().getString(R.string.meals));
            } else if (fragment instanceof GroceryFragment) {
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.grocery));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof MenusFragment) {
                ivSearchVis = View.VISIBLE;
				if(llSearchCartVis == View.VISIBLE
						&& getMenusFragment() != null && getMenusFragment().isServiceUnavailable()){
					llSearchCartVis = View.GONE;
				}
                rlFilterVis = View.VISIBLE;


                if(getAppType() == AppConstant.ApplicationType.DELIVERY_CUSTOMER ){
                    topBar.title.setText(R.string.delivery_new_name);
                    topBar.getTvAddressLayoutTitle().setText(R.string.title_fatafat_home_page);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                    topBar.imageViewMenu.setVisibility(View.VISIBLE);
                    topBar.imageViewBack.setVisibility(View.GONE);
                    if(getCategoryIdOpened()<0){
                        if(Data.userData.getFeedEnabled()==0){
                            fabFatafatVisibility =  View.VISIBLE;

                        }
                        if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                            fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                        }

                    }
                }else{
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                    topBar.title.setText(R.string.menus);
                    topBar.getTvAddressLayoutTitle().setText(R.string.menus);

                    topBar.imageViewMenu.setVisibility(View.VISIBLE);
                    topBar.imageViewBack.setVisibility(View.GONE);
                    if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }
                }

                topBar.getLlSearchCart().setLayoutTransition(null);
                visMinOrder = setMinOrderAmountText(fragment);
                deliveryAddressInTopBarVisibility = View.VISIBLE;
                topBar.title.setVisibility(View.GONE);

            } else if (fragment instanceof VendorMenuFragment
                    || fragment instanceof RestaurantImageFragment
                    || fragment instanceof MerchantInfoFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(vendorOpened.getName());
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                if(fragment instanceof VendorMenuFragment) {
                    freshSortVis = View.VISIBLE;
                    ivSearchVis = View.VISIBLE;
                    llCartContainerVis = View.VISIBLE;
                    visMinOrder = setMinOrderAmountText(fragment);
                }

            } else if (fragment instanceof MenusItemCustomizeFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;
				llAddToCartVis = View.VISIBLE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getString(R.string.customize_item));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof FreshCheckoutMergedFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;
                llPayViewContainerVis = View.VISIBLE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.checkout));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof AddToAddressBookFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.confirm_address));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            } else if (fragment instanceof DeliveryAddressesFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartContainerVis = View.GONE;

                topBar.title.setVisibility(View.GONE);
                topBar.editTextDeliveryAddress.setVisibility(View.VISIBLE);
                topBar.editTextDeliveryAddress.requestFocus();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            } else if (fragment instanceof FreshSearchFragment || fragment instanceof MenusSearchFragment) {
                llCartContainerVis = View.VISIBLE;
                llSearchContainerVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.GONE);
                topBar.llSearchContainer.setVisibility(View.VISIBLE);
                topBar.animateSearchBar(true);


                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof FeedbackFragment || fragment instanceof NewFeedbackFragment) {
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            } else if (fragment instanceof OrderStatusFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                llSearchCartVis = View.GONE;
            } else if (fragment instanceof MealAddonItemsFragment) {
                llCartContainerVis = View.GONE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.pick_addons));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof RestaurantReviewsListFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                if (getVendorOpened() != null) {
                    topBar.title.setText(getVendorOpened().getName());
                }
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof RestaurantAddReviewFragment) {
            	topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setImageResource(R.drawable.ic_cross_grey_selector);
                padding = (int) (25f * ASSL.minRatio());

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.write_a_review);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof FeedAddPostFragment){
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.add_post);
                topBar.imageViewBack.setImageResource(R.drawable.ic_cross_grey_selector);
                padding = (int) (25f * ASSL.minRatio());
                llSearchCartVis = View.GONE;

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }else if(fragment instanceof AnywhereHomeFragment){
                topBar.getLlSearchCart().setLayoutTransition(null);
                topBar.imageViewMenu.setVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                topBar.imageViewBack.setVisibility(isDeliveryOpenInBackground()?View.VISIBLE:View.GONE);
                drawerLayout.setDrawerLockMode(isDeliveryOpenInBackground()?DrawerLayout.LOCK_MODE_LOCKED_CLOSED:DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(Data.getFeedName(this));

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(isDeliveryOpenInBackground()?View.GONE:View.VISIBLE);
                }
                llSearchCartVis = View.GONE;
                llPayViewContainerVis = View.VISIBLE;
                visMinOrder = setMinOrderAmountText(fragment);
            }
            else if (fragment instanceof FeedHomeFragment || fragment instanceof FeedReserveSpotFragment || fragment instanceof FeedSpotReservedSharingFragment ||
                    fragment instanceof FeedClaimHandleFragment) {
                topBar.getLlSearchCart().setLayoutTransition(null);
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(Data.getFeedName(this));

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);

            }
            else if(fragment instanceof FeedPostDetailFragment || fragment instanceof FeedNotificationsFragment  || fragment instanceof FeedChangeCityFragment){
                topBar.getLlSearchCart().setLayoutTransition(null);
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                if(fragment instanceof FeedPostDetailFragment){
                    topBar.title.setText(Data.getFeedName(this));
                } else if(fragment instanceof FeedNotificationsFragment){
                    topBar.title.setText(R.string.notifications);
                }else if(fragment instanceof FeedChangeCityFragment){
                    topBar.title.setText(R.string.select_city);
                }


                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof SupportFAQItemFragment
                    || fragment instanceof SupportRideIssuesFragment || fragment instanceof SupportFAQItemsListFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getString(R.string.order_is_late));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof MealsBulkOrderFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getString(R.string.bulk_order));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof ProsHomeFragment) {
                llCartContainerVis = View.GONE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.pros));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            }
            else if (fragment instanceof ProsProductsFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }
            else if (fragment instanceof ProsCheckoutFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.checkout));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                bRequestBooking.setVisibility(View.VISIBLE);
            }
            else if(fragment instanceof ProsOrderStatusFragment){
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }



            rlfabViewFatafat.setVisibility(View.GONE);
            topBar.imageViewBack.setPadding(padding, padding, padding, padding);
            topBar.getLlTopBarDeliveryAddress().setVisibility(deliveryAddressInTopBarVisibility);

            if(visMinOrder != 1) {
                textViewMinOrderSetVisibility(View.GONE);
            }

            topBar.getLlSearchCartContainer().setVisibility(llSearchCartContainerVis);
            topBar.getLlSearchCart().setVisibility(llSearchCartVis);
            if(totalPrice <= 0 || totalQuantity <= 0){
                llCartContainerVis = View.GONE;
            }
            llCheckoutBarSetVisibilityDirect(llCartContainerVis);
            topBar.getRlSearch().setVisibility(ivSearchVis);
            topBar.getLlSearchContainer().setVisibility(llSearchContainerVis);
            setMenusFilterVisibility(rlFilterVis);
            llAddToCart.setVisibility(llAddToCartVis);
            llPayViewContainer.setVisibility(llPayViewContainerVis);

            if (llSearchCartVis == View.VISIBLE) {
                setTitleAlignment(false);
            } else {
                setTitleAlignment(true);
            }
            if(fragment instanceof FeedReserveSpotFragment
                    || fragment instanceof FeedSpotReservedSharingFragment
                    || fragment instanceof FeedNotificationsFragment
					|| fragment instanceof FeedChangeCityFragment
					|| fragment instanceof RestaurantReviewsListFragment
                    || fragment instanceof RestaurantAddReviewFragment
                    || fragment instanceof FeedbackFragment){
                setTitleAlignment(true);
            } else if (fragment instanceof MenusFragment
                    || fragment instanceof FreshHomeFragment
                    || fragment instanceof MealFragment
                    || fragment instanceof AnywhereHomeFragment){
                setTitleAlignment(false);
            }



            topBar.rlFreshSort.setVisibility(freshSortVis);

            feedHomeAddPostView.setVisibility(View.GONE);
            setCollapsingToolbar(collapsingToolBarEnabled(fragment), fragment);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleAlignment(boolean center){
        RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) topBar.title.getLayoutParams();
        if(center){
            topBar.title.setGravity(Gravity.CENTER);
            titleLayoutParams.setMargins(0, 0, (int) (ASSL.Xscale() * 50f), 0);
        } else {
            topBar.title.setGravity(Gravity.LEFT);
            titleLayoutParams.setMargins((int) (ASSL.Xscale() * 20f), 0, 0, 0);
            titleLayoutParams.addRule(RelativeLayout.LEFT_OF, topBar.getLlSearchCart().getId());
            topBar.title.setPadding(0, 0, 0, 0);
        }
        topBar.title.setLayoutParams(titleLayoutParams);
    }

    public void setMenusFilterVisibility(int rlFilterVis) {
        topBar.rlFilter.setVisibility(rlFilterVis);
        drawerLayout.setDrawerLockMode((rlFilterVis == View.VISIBLE ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED), GravityCompat.END);
        topBar.ivFilterApplied.setVisibility(isFilterApplied() ? View.VISIBLE : View.GONE);
    }


    public void setCollapsingToolbar(boolean isEnable, Fragment fragment) {


        AppBarLayout.LayoutParams collapsingToolBarParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        CoordinatorLayout.LayoutParams relativeparams = (CoordinatorLayout.LayoutParams) relativeLayoutContainer.getLayoutParams();
        if (fragment instanceof RestaurantImageFragment)
            relativeparams.setBehavior(null);
        else
            relativeparams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        relativeLayoutContainer.requestLayout();


        findViewById(R.id.llCollapParent).setMinimumHeight(getResources().getDimensionPixelSize(fragment instanceof RestaurantImageFragment ? R.dimen.dp_90 : R.dimen.dp_1));
        if (isEnable) {
            findViewById(R.id.layout_rest_details).setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            layoutParams.height = (int) (ASSL.Yscale() * COLLAPSE_TOOLBAR_HEIGHT);
            appBarLayout.setLayoutParams(layoutParams);
            appBarLayout.requestLayout();

            collapsingToolBarParams.setScrollFlags(0);
            collapsingToolBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED|AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            collapsingToolbar.setLayoutParams(collapsingToolBarParams);


            CollapsingToolbarLayout.LayoutParams toolBarParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                toolBarParams.height = actionBarHeight;
                toolbar.setLayoutParams(toolBarParams);

            }
            topBar.getIvSearch().setSelected(false);
            onStateChanged(appBarLayout, State.EXPANDED);
            appBarLayout.addOnOffsetChangedListener(collapseBarController);


        } else {
            findViewById(R.id.layout_rest_details).setVisibility(View.GONE);
            appBarLayout.removeOnOffsetChangedListener(collapseBarController);

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            layoutParams.height = AppBarLayout.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(layoutParams);

            CollapsingToolbarLayout.LayoutParams toolBarParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            toolBarParams.height = CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT;
            toolbar.setLayoutParams(toolBarParams);


            if (fragment instanceof RestaurantImageFragment) {
                topBar.getIvSearch().setSelected(true);
                onStateChanged(appBarLayout, State.EXPANDED);
            } else {
                topBar.getIvSearch().setSelected(false);
                onStateChanged(appBarLayout, State.COLLAPSED);
            }

            if(fragment instanceof FeedHomeFragment) {
                collapsingToolBarParams.setScrollFlags(0);
                collapsingToolBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS| AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                collapsingToolbar.setLayoutParams(collapsingToolBarParams);

                appBarLayout.addOnOffsetChangedListener(feedHomeAppBarListener);
            }
            else{
                collapsingToolBarParams.setScrollFlags(0);
                collapsingToolbar.setLayoutParams(collapsingToolBarParams);

                appBarLayout.removeOnOffsetChangedListener(feedHomeAppBarListener);
                appBarLayout.removeOnOffsetChangedListener(collapseBarController);

            }




        }


    }



    AppBarLayout.OnOffsetChangedListener feedHomeAppBarListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


            currentOffsetFeedHomeAppBar = verticalOffset;
            if(getTopFragment() instanceof FeedHomeFragment) {
                if((-verticalOffset!=appBarLayout.getTotalScrollRange() &&  feedHomeAddPostView.getTranslationY()!=0 && feedHomeAddPostView.getTranslationY()!=feedHomeAddPostView.getHeight()) ||   getFeedHomeFragment().shouldTranslateFeedHomeAddPost()) {
                    feedHomeAddPostView.animate().translationY(feedHomeAddPostView.getHeight() - ((appBarLayout.getTotalScrollRange() + verticalOffset) * 1.0f / appBarLayout.getTotalScrollRange()) * feedHomeAddPostView.getHeight()).start();
                }



            }
            fabViewTest.getMenuLabelsRightTest().setAlpha((((appBarLayout.getTotalScrollRange() ) - (-verticalOffset)) * 1.0f) / (appBarLayout.getTotalScrollRange()));
            if (verticalOffset == -appBarLayout.getTotalScrollRange()) {
                fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);

            } else {
                fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);



            }

        }
    };

    public void openRestaurantImageFragment() {
        if (canExitVendorMenu())
            transactionUtils.openRestaurantImageFragment(FreshActivity.this, relativeLayoutContainer);
    }

    /**
     * To set text and visibility of textViewMinOrderAmount
     * @param fragment fragment to call this
     * @return returns 1 if visibility changed by this function else 0
     */
    public int  setMinOrderAmountText(Fragment fragment) {
        try {
            if (!(getTopFragment() instanceof FreshCheckoutMergedFragment)) {
                if (getFreshFragment() != null || (getFreshSearchFragment() != null && getVendorMenuFragment() == null)) {
                    int textViewMinOrderVis;
                    if (fragment instanceof FreshFragment || fragment instanceof FreshSearchFragment) {
                        if (Data.userData.isSubscriptionActive()) {
                            if(getProductsResponse() != null && !TextUtils.isEmpty(getProductsResponse().getSubscriptionMessage())){
                                textViewMinOrderVis = View.VISIBLE;
                                textViewMinOrder.setText(getProductsResponse().getSubscriptionMessage());
                            } else if(getSuperCategoriesData() != null && !TextUtils.isEmpty(getSuperCategoriesData().getSubscriptionMessage())){
                                textViewMinOrderVis = View.VISIBLE;
                                textViewMinOrder.setText(getSuperCategoriesData().getSubscriptionMessage());
                            } else {
                                textViewMinOrderVis = View.GONE;
                            }
                        } else if (totalQuantity > 0 && totalPrice < getOpenedDeliveryStore().getMinAmount()) {
                            textViewMinOrderVis = View.VISIBLE;
                            double leftAmount = getOpenedDeliveryStore().getMinAmount() - totalPrice;
                            textViewMinOrder.setText(getString(R.string.away_from_free_delivery_value_format,
                                    Utils.getMoneyDecimalFormatWithoutFloat().format(leftAmount)));
                        } else {
                            textViewMinOrderVis = View.GONE;
                        }
                    } else {
                        textViewMinOrderVis = View.GONE;
                    }
                    textViewMinOrderSetVisibility(textViewMinOrderVis);
                    return 1;
                } else if (getMealFragment() != null && getFeedbackFragment() == null) {
                    int textViewMinOrderVis;
                    if(getMealFragment().shouldShowStrip() && Data.getMealsData().getOfferStripMeals()!=null && !TextUtils.isEmpty(Data.getMealsData().getOfferStripMeals().getTextToDisplay())){
                        textViewMinOrderVis = View.VISIBLE;
                        textViewMinOrder.setText(Utils.trimHTML(Utils.fromHtml(Data.getMealsData().getOfferStripMeals().getTextToDisplay())));
                    } else{
                        textViewMinOrderVis = View.GONE;
                    }
                    textViewMinOrderSetVisibility(textViewMinOrderVis);
                    return 1;
                } else if (fragment instanceof VendorMenuFragment || fragment instanceof MenusSearchFragment) {
                    int textViewMinOrderVis = View.GONE;
                    if (getVendorOpened() != null && !(getTopFragment() instanceof MenusFragment) && !getVendorOpened().isOutOfRadiusStrip()) {
                        if(!TextUtils.isEmpty(getVendorOpened().getNext_slot_time())){
                            textViewMinOrder.setText(getVendorOpened().getNext_slot_time());
                            textViewMinOrderVis = View.VISIBLE;
                        } else if (totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                            textViewMinOrderVis = View.VISIBLE;
                            textViewMinOrder.setText(getString(R.string.minimum_order) + " "
                                    + getString(R.string.rupees_value_format_without_space, Utils.getMoneyDecimalFormatWithoutFloat().format(getVendorOpened().getMinimumOrderAmount())));
                        } else if (totalQuantity > 0 && getVendorOpened().getShowFreeDeliveryText() == 1
                                && totalPrice < getVendorOpened().getDeliveryAmountThreshold()) {
                            textViewMinOrderVis = View.VISIBLE;
                            double leftAmount = getVendorOpened().getDeliveryAmountThreshold() - totalPrice;
                            textViewMinOrder.setText(getString(R.string.away_from_free_delivery_value_format,
                                    Utils.getMoneyDecimalFormatWithoutFloat().format(leftAmount)));
                        } else {
                            textViewMinOrderVis = View.GONE;
                        }
                    }
                    textViewMinOrderSetVisibility(textViewMinOrderVis);
                    return 1;
                }else if(fragment instanceof AnywhereHomeFragment){

                    int textViewMinOrderVis;
                    if( Data.getFeedData().getBottomStrip()!=null && !TextUtils.isEmpty(Data.getFeedData().getBottomStrip().getTextToDisplay())){
                        textViewMinOrderVis = View.VISIBLE;
                        textViewMinOrder.setText(Utils.trimHTML(Utils.fromHtml(Data.getFeedData().getBottomStrip().getTextToDisplay())));
                    } else{
                        textViewMinOrderVis = View.GONE;
                    }
                    textViewMinOrderSetVisibility(textViewMinOrderVis);
                    return 1;
                }
            }else {
                textViewMinOrderSetVisibility(View.GONE);
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void searchItem() {
        try {
            if (isMenusOrDeliveryOpen()) {
                if (getTopFragment() instanceof MenusFragment) {
                    getMenusFragment().toggleSearch(true, true);
                    topBar.title.setVisibility(View.GONE);
                    topBar.title.invalidate();
                    topBar.animateSearchBar(true);
                      GAUtils.event(getGaCategory(), GAAction.HOME ,GAAction.SEARCH_BUTTON + GAAction.CLICKED);
                }
                else if (getTopFragment() instanceof VendorMenuFragment || getTopFragment() instanceof RestaurantImageFragment) {
                        if(getTopFragment() instanceof VendorMenuFragment){
                            GAUtils.event(getGaCategory(), GAAction.RESTAURANT_HOME ,GAAction.SEARCH_BUTTON + GAAction.CLICKED);
                        }


                       if (canExitVendorMenu())
                        getTransactionUtils().openMenusSearchFragment(FreshActivity.this, relativeLayoutContainer);

                }


            } else {
                if (getFreshFragment() != null) {
                    getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer, getFreshFragment().getSuperCategory().getSuperCategoryId(),
                            getSuperCategoriesData().getDeliveryInfo().getCityId());
                    if(getFreshFragment().getSuperCategory() != null) {
                        GAUtils.event(FRESH, getFreshFragment().getSuperCategory().getSuperCategoryName(), SEARCH_BUTTON + CLICKED);
                    }
                } else {
                    getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer, -1, getSuperCategoriesData().getDeliveryInfo().getCityId());
                    GAUtils.event(FRESH, HOME, SEARCH_BUTTON+CLICKED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMenusFilter() {
        GAUtils.event(getGaCategory(), GAAction.HOME ,GAAction.FILTER_BUTTON + GAAction.CLICKED);
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public FreshSearchFragment getFreshSearchFragment() {
        return (FreshSearchFragment) getSupportFragmentManager().findFragmentByTag(FreshSearchFragment.class.getName());
    }

    private MenusSearchFragment getMenusSearchFragment() {
        return (MenusSearchFragment) getSupportFragmentManager().findFragmentByTag(MenusSearchFragment.class.getName());
    }

    public void deleteCart() {
        final int type = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
        DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                getResources().getString(R.string.delete_fresh_cart_message),
                getResources().getString(R.string.delete),
                getResources().getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment frag = getFreshCheckoutMergedFragment();
                        if (frag != null) {
                            ((FreshCheckoutMergedFragment) frag).deleteCart();
                        } else {
                            frag = getMealAddonItemsFragment();
                            if (frag != null) {
                                ((MealAddonItemsFragment) frag).deleteCart();
                            }
                        }

                        if (type == AppConstant.ApplicationType.FRESH) {
                            clearCart();
                        } else if (type == AppConstant.ApplicationType.GROCERY) {
                            clearGroceryCart();
                        } else if (type == AppConstant.ApplicationType.MENUS || type==AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                            clearMenusCart(type);
                        } else {
                            clearMealCart();
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, true, false);
    }


    public void updateMenu() {
        menuBar.setUserData();
    }

    private void resetAddressFields() {
        selectedAddress = "";
        selectedAddressId = 0;
        selectedAddressType = "";
        selectedLatLng = new LatLng(Data.latitude, Data.longitude);
    }

    public void clearAllCartAtOrderComplete(int type) {
        if (type == AppConstant.ApplicationType.FRESH) {
            clearCart();
        } else if (type == AppConstant.ApplicationType.GROCERY) {
            clearGroceryCart();
        } else if (type == AppConstant.ApplicationType.MENUS || type== AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
            clearMenusCart(type);
        } else {
            clearMealCart();
        }
        if (type == AppConstant.ApplicationType.MENUS || type==AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
            if (getMenuProductsResponse() != null) {
                for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                    if (category.getSubcategories() != null) {
                        for (Subcategory subcategory : category.getSubcategories()) {
                            for (Item item : subcategory.getItems()) {
                                item.getItemSelectedList().clear();
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            item.getItemSelectedList().clear();
                        }
                    }
                }
            }
        } else {
            if (productsResponse != null) {
                for (Category category : productsResponse.getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        subItem.setSubItemQuantitySelected(0);
                    }
                }
            }
        }
    }


    private void checkForBackToFeed(boolean backPressed){
        if (getFeedHomeFragment() != null && isMenusOrDeliveryOpen()
                && ((backPressed && getTopFragment() instanceof VendorMenuFragment)
                || (!backPressed && getTopFragment() instanceof FreshCheckoutMergedFragment))) {
            Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FEED);
            Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFeedClientId());
        }
    }

    public void orderComplete() {
        orderJustCompleted = true;
        clearAllCartAtOrderComplete(getAppType());
        splInstr = "";
        slotSelected = null;
        slotToSelect = null;
        paymentOption = null;
        setPlaceOrderResponse(null);
        Data.setRecentAddressesFetched(false);

        setSelectedAddress("");
        setSelectedLatLng(null);
        setSelectedAddressId(0);
        Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);
        setSelectedAddressType("");

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {resetSlider();} catch (Exception e) {}
            }
        }, 100);


        checkForBackToFeed(false);

        clearFragmentStackTillLast();

        updateCartValuesGetTotalPrice();

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));
                orderJustCompleted = false;
            }
        }, 300);
        clearEtFocus();
    }

    public void clearFragmentStackTillLast() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - 1; i++) {
            fm.popBackStack();
        }
    }

    private boolean orderJustCompleted;
    public boolean isOrderJustCompleted(){
        return orderJustCompleted;
    }

    public void resetSlider(){
        rlSliderContainer.setBackgroundResource(R.color.theme_color);
        relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_color_bg);
        sliderText.setVisibility(View.VISIBLE);
        viewAlpha.setAlpha(0.0f);

        RelativeLayout.LayoutParams paramsF = (RelativeLayout.LayoutParams) tvSlide.getLayoutParams();
        paramsF.leftMargin = 0;
        relativeLayoutSlider.updateViewLayout(tvSlide, paramsF);
    }

    private void addFreshHomeFragment(boolean addNew) {

        if(addNew){
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new FreshHomeFragment(),
                            FreshHomeFragment.class.getName())
                    .addToBackStack(FreshHomeFragment.class.getName())
                    .commitAllowingStateLoss();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(relativeLayoutContainer.getId(),  new FreshHomeFragment(),
                            FreshHomeFragment.class.getName())
                    .addToBackStack(FreshHomeFragment.class.getName())
                    .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                            .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }

    }


    private void addMealFragment(boolean addNew) {
        if(addNew){
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new MealFragment(),
                            MealFragment.class.getName())
                    .addToBackStack(MealFragment.class.getName())
                    .commitAllowingStateLoss();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(relativeLayoutContainer.getId(),  new MealFragment(),
                            MealFragment.class.getName())
                    .addToBackStack(MealFragment.class.getName())
                    .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                            .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }


    }

    private void addGroceryFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new GroceryFragment(),
                        GroceryFragment.class.getName())
                .addToBackStack(GroceryFragment.class.getName())
                .commitAllowingStateLoss();
    }

    private void addCustomerDeliveryFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new MenusFragment(),
                        MenusFragment.class.getName())
                .addToBackStack(MenusFragment.class.getName())
                .commitAllowingStateLoss();
    }
    private void addMenusFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(relativeLayoutContainer.getId(),   new MenusFragment(),
                        MenusFragment.class.getName())
                .addToBackStack(MenusFragment.class.getName())
                .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                .commitAllowingStateLoss();

    }

    public void addFeedFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FeedHomeFragment(),
                        FeedHomeFragment.class.getName())
                .addToBackStack(FeedHomeFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void addAnywhereHomeFragment(boolean fromOnCreate) {
        if(fromOnCreate){
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new AnywhereHomeFragment(),
                            AnywhereHomeFragment.class.getName())
                    .addToBackStack(AnywhereHomeFragment.class.getName())
                    .commitAllowingStateLoss();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(relativeLayoutContainer.getId(),  new AnywhereHomeFragment(),
                            AnywhereHomeFragment.class.getName())
                    .addToBackStack(AnywhereHomeFragment.class.getName())
                    .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                            .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }

    }

    private void addClaimHandleFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FeedClaimHandleFragment(),
                        FeedClaimHandleFragment.class.getName())
                .addToBackStack(FeedClaimHandleFragment.class.getName())
                .commitAllowingStateLoss();
    }

    private void addFeedReserveSpotFragment() {
        if(Data.getFeedData().getFeedRank() == null){
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new FeedReserveSpotFragment(),
                            FeedReserveSpotFragment.class.getName())
                    .addToBackStack(FeedReserveSpotFragment.class.getName())
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new FeedSpotReservedSharingFragment(),
                            FeedSpotReservedSharingFragment.class.getName())
                    .addToBackStack(FeedSpotReservedSharingFragment.class.getName())
                    .commitAllowingStateLoss();
        }
    }


    private void addProsHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new ProsHomeFragment(),
                        ProsHomeFragment.class.getName())
                .addToBackStack(ProsHomeFragment.class.getName())
                .commitAllowingStateLoss();
    }


    /**
     * Method used to open feedback screen
     */
    public void openFeedback() {
        getTransactionUtils().openFeedback(FreshActivity.this, relativeLayoutContainer);
    }

    public void openAddToAddressBook(Bundle bundle) {
        getTransactionUtils().openAddToAddressFragment(FreshActivity.this, relativeLayoutContainer, bundle);
    }


    public void performBackPressed(boolean isBackPressed) {


        if(isDeliveryOpenInBackground()  && (getTopFragment() instanceof MealFragment|| getTopFragment() instanceof FreshHomeFragment || getTopFragment() instanceof AnywhereHomeFragment||getTopFragment() instanceof MerchantInfoFragment)){
            saveAppCart();
            Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getDeliveryCustomerClientId());
            Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.DELIVERY_CUSTOMER);
            Data.AppType = getAppType();
           /* if(getMenusFragment() != null && getMenusFragment().getView() != null) {
                setDeliveryAddressView(getMenusFragment().rootView);
            }*/
            super.onBackPressed();
            return;
        }
        if (isBackPressed) {
            if (getTopFragment() instanceof MealAddonItemsFragment) {
                GAUtils.event(getGaCategory(), ADD_ONS, BACK + BUTTON + CLICKED);
            } else if (getTopFragment() instanceof FreshCheckoutMergedFragment) {
                GAUtils.event(getGaCategory(), CHECKOUT, BACK + BUTTON + CLICKED);
            } else if (getTopFragment() instanceof DeliveryAddressesFragment) {
                GAUtils.event(getGaCategory(), DELIVERY_ADDRESS, BACK + BUTTON + CLICKED);
            } else if (getTopFragment() instanceof RestaurantAddReviewFragment) {
                GAUtils.event(getGaCategory(), GAAction.ADD_FEED, GAAction.FEED + GAAction.CLOSED);
            } else if (getTopFragment() instanceof MenusItemCustomizeFragment) {
                GAUtils.event(getGaCategory(), GAAction.CUSTOMIZE_ITEM, GAAction.BACK_BUTTON + GAAction.CLICKED);
            } else if (getTopFragment() instanceof MenusSearchFragment && getAppType() == AppConstant.ApplicationType.MENUS) {
                GAUtils.event(getGaCategory(), GAAction.RESTAURANT_SEARCH, GAAction.BACK_BUTTON + GAAction.CLICKED);
            } else if (getTopFragment() instanceof MenusFilterFragment) {
                GAUtils.event(getGaCategory(), GAAction.FILTERS + GAAction.MINIMIZE, "");
            } else if (getTopFragment() instanceof FreshSearchFragment) {
                if (getFreshFragment() != null) {
                    if(getFreshFragment().getSuperCategory() != null)
                        GAUtils.event(FRESH, getFreshFragment().getSuperCategory().getSuperCategoryName() + " " + SEARCH, BACK + BUTTON + CLICKED);
                } else {
                    GAUtils.event(FRESH, HOME + SEARCH, BACK + BUTTON + CLICKED);
                }
            } else if(getTopFragment() instanceof FeedPostDetailFragment){
                GAUtils.event(FEED, COMMENT, BACK+BUTTON+CLICKED);
            }
        }

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            if(getMenusFilterFragment() != null) {
                getMenusFilterFragment().performBackPress(true);
                return;
            }
        }

        if(getTopFragment() instanceof DeliveryAddressesFragment && getDeliveryAddressesFragment().backWasConsumed()){
            return;
        }

        checkForBackToFeed(true);

        if(getFeedSpotReservedSharingFragment() != null && getFeedReserveSpotFragment() != null){
            finishWithToast();
            return;
        }

        if (getFeedbackFragment() != null && getSupportFragmentManager().getBackStackEntryCount() == 2 && !getFeedbackFragment().isUpbuttonClicked) {
            finishWithToast();
            return;
        }
        try {
            Utils.hideSoftKeyboard(this, topBar.etSearch);
        } catch (Exception e) {
        }
        final AddToAddressBookFragment fragment = getAddToAddressBookFragment();
        if (fragment != null && fragment.locationEdited) {
            DialogPopup.alertPopupTwoButtonsWithListeners(FreshActivity.this, "",
                    getString(R.string.changes_not_updated_exit),
                    getString(R.string.ok), getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fragment.locationEdited = false;
                            performBackPressed(false);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false, false);
        } else if (locationSearchShown) {
            locationSearchShown = false;
            try {
                mBus.post(new AddressSearch(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (getTopFragment() instanceof MenusFragment && getMenusFragment().getSearchOpened()) {
            getMenusFragment().toggleSearch(true, true);
        } else if (getTopFragment() instanceof MenusFragment
                && (getCategoryIdOpened() > 0 )
                && getMenusResponse().getCategories() != null  // if only more than one category coming from server for the place
                && getMenusResponse().getCategories().size() > 1) {

            getMenusFragment().switchCategory(null);
            return;
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finishWithToast();
            return;
        } else {
            if (getTopFragment() instanceof FreshSearchFragment) {
                getFreshSearchFragment().clearArrays();
            } else if (getTopFragment() instanceof MenusSearchFragment) {
                getMenusSearchFragment().clearArrays();
            }
            if (getTopFragment() instanceof RestaurantReviewsListFragment && getRestaurantImageFragment() != null) {
                super.onBackPressed();
            }
            if (getTopFragment() != null && getTopFragment() instanceof FreshSearchFragment)
                topBar.animateSearchBar(false);

            if(getTopFragment() instanceof RestaurantAddReviewFragment && getRestaurantAddReviewFragment().isKeyboardOpen()){
                try {
                    Utils.hideSoftKeyboard(FreshActivity.this,getRestaurantAddReviewFragment().getFocusEditText());
                 } catch (Exception e) {
                    e.printStackTrace();
                 }
                handler.postDelayed(onBackPressRunnable,250);


            }else{
                super.onBackPressed();

            }
        }
    }
    private Runnable onBackPressRunnable = new Runnable() {
        @Override
        public void run() {
            FreshActivity.super.onBackPressed();
        }
    };

    private int backPressedCount = 0;
    private void finishWithToast(){
        backPressedCount++;
        getHandler().removeCallbacks(runnableBackPressReset);
        getHandler().postDelayed(runnableBackPressReset, 2000);
        if(backPressedCount >= 2){
            ActivityCompat.finishAffinity(this);
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


    public static final long FETCH_WALLET_BALANCE_REFRESH_TIME = 5 * 60 * 1000;
    private ApiFetchWalletBalance apiFetchWalletBalance = null;

    private void fetchWalletBalance(final Activity activity) {
        try {
            if (apiFetchWalletBalance == null) {
                apiFetchWalletBalance = new ApiFetchWalletBalance(this, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                            setUserData();
                            Intent intent = new Intent(Constants.INTENT_ACTION_WALLET_UPDATE);
                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            if (lastCallDiff >= FETCH_WALLET_BALANCE_REFRESH_TIME) {
                apiFetchWalletBalance.getBalance(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUserData() {
        try {
            menuBar.setUserData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        try {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
				drawerLayout.closeDrawer(GravityCompat.START);
				return;
			}
            if(fabViewTest.menuLabelsRightTest.isOpened()){
				fabViewTest.menuLabelsRightTest.close(true, false);
				return;
			}
        } catch (Exception e) {
        }

         performBackPressed(true);


    }

    @Override
    protected void onDestroy() {
        startService(new Intent(this, DeleteCacheIntentService.class));
        try {
            if(handler!=null){
                handler.removeCallbacks(null);

            }
            mBus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        System.gc();
        super.onDestroy();
    }

    public ProductsResponse getProductsResponse() {
        return productsResponse;
    }

    public SuperCategoriesData getSuperCategoriesData() {
        return superCategoriesData;
    }

    public void setSuperCategoriesData(SuperCategoriesData superCategoriesData) {
        this.superCategoriesData = superCategoriesData;
    }

    public void setProductsResponse(ProductsResponse productsResponse) {
        this.productsResponse = productsResponse;
        mContactNo = productsResponse.getSupportContact();
    }

    public VendorMenuResponse getMenuProductsResponse() {
        return vendorMenuResponse;
    }

    public void setMenuProductsResponse(VendorMenuResponse vendorMenuResponse) {
        this.vendorMenuResponse = vendorMenuResponse;
        mContactNo = vendorMenuResponse.getSupportContact();
    }


    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public UserCheckoutResponse getUserCheckoutResponse() {
        return userCheckoutResponse;
    }

    public void setUserCheckoutResponse(UserCheckoutResponse userCheckoutResponse) {
        this.userCheckoutResponse = userCheckoutResponse;
    }

    public RelativeLayout getRelativeLayoutContainer() {
        return relativeLayoutContainer;
    }


    public String getSpecialInst() {
        return splInstr.trim();
    }

    public void setSplInstr(String splInstr) {
        this.splInstr = splInstr;
    }

    public Slot getSlotSelected() {
        return slotSelected;
    }

    public void setSlotSelected(Slot slotSelected) {
        this.slotSelected = slotSelected;
    }

    public void setDeliveryAddresses(List<DeliveryAddress> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public List<DeliveryAddress> getDeliveryAddress() {
        return deliveryAddresses;
    }

    public PaymentOption getPaymentOption() {
        if (paymentOption == null) {
            return PaymentOption.CASH;
        }
        return paymentOption;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }


    @Override
    protected void onPause() {
        super.onPause();
        isLocationChangeCheckedAfterResume = false;
        try {
            Utils.hideKeyboard(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            if (cartChangedAtCheckout && getFreshCheckoutMergedFragment() != null) {
                updateItemListFromDBFMG(null);
            }
            if(isMenusOrDeliveryOpen()){
				getMenusCart().clearEmptyRestaurantCarts();
			}
            saveItemListToSPDB();
            saveAppCart();

            if (isMenusOrDeliveryOpen()) {
                saveFilters();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        MyApplication.getInstance().getLocationFetcher().destroy();
        Log.e("FreshActivity", "onPause");

    }

    public void saveItemListToSPDB() {
        if (isMenusOrDeliveryOpen()) {
            saveItemListToSPMenus();
        } else {
            saveItemListToDBFMG(null);
        }
    }

    /**
     * saves productResponse items list to db hashMap
     * if subItemToUpdate is null then it should be called only when item list is recreated from server's response
     *
     * @param subItemToUpdate item to save in db hashMap, if null whole categories list will be saved
     */
    private void saveItemListToDBFMG(SubItem subItemToUpdate) {
        try {
            // hash Current delivery store cart object for updating subItems list quantity
            HashMap<Integer, SubItem> subItemHashMap = getCart().getSubItemHashMap(getOpenedVendorId());
            // if app opened is meals and not a particular item is needed to be updated, clear the cart
            if(getAppType() == AppConstant.ApplicationType.MEALS && subItemToUpdate == null){
                subItemHashMap.clear();
            }

            if (subItemToUpdate == null && getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                int i = 0;
                for (Category category : getProductsResponse().getCategories()) {
                    int mainItemsCount = 0;
                    for (SubItem subItem : category.getSubItems()) {
                        if (subItem.getSubItemQuantitySelected() > 0) {
                            // updating deliveryStoreCart subItem hashMap
                            subItemHashMap.put(subItem.getSubItemId(), subItem);
                            // if app is meals increase mainItemCount
                            if(getAppType() == AppConstant.ApplicationType.MEALS && i == 0) mainItemsCount++;
                        } else {
                            // removing from deliveryStoreCart subItem hashMap
                            subItemHashMap.remove(subItem.getSubItemId());
                        }
                    }
                    // if no main meal item selected clear cart and break from loop
                    if(getAppType() == AppConstant.ApplicationType.MEALS && i == 0 && mainItemsCount == 0){
                        subItemHashMap.clear();
                        break;
                    }
                    i++;
                }
            } else if (subItemToUpdate != null) {
                if (subItemToUpdate.getSubItemQuantitySelected() > 0) {
                    // updating deliveryStoreCart subItem hashMap
                    subItemHashMap.put(subItemToUpdate.getSubItemId(), subItemToUpdate);
                } else {
                    // updating deliveryStoreCart subItem hashMap
                    subItemHashMap.remove(subItemToUpdate.getSubItemId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveItemListToSPMenus() {
        try {
            Gson gson = new Gson();
            JSONObject jCart = new JSONObject();
            if (getMenuProductsResponse() != null
                    && getMenuProductsResponse().getCategories() != null) {
                for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                    if (category.getSubcategories() != null) {
                        for (Subcategory subcategory : category.getSubcategories()) {
                            for (Item item : subcategory.getItems()) {
                                JSONArray jsonArrayItem = new JSONArray();
                                for (ItemSelected itemSelected : item.getItemSelectedList()) {
                                    if (itemSelected.getQuantity() > 0) {
                                        jsonArrayItem.put(gson.toJson(itemSelected, ItemSelected.class));
                                    }
                                }
                                if (jsonArrayItem.length() > 0) {
                                    jCart.put(String.valueOf(item.getRestaurantItemId()), jsonArrayItem);
                                    getMenusCart().putItemForRestaurant(getVendorOpened(), item);
                                }
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            JSONArray jsonArrayItem = new JSONArray();
                            for (ItemSelected itemSelected : item.getItemSelectedList()) {
                                if (itemSelected.getQuantity() > 0) {
                                    jsonArrayItem.put(gson.toJson(itemSelected, ItemSelected.class));
                                }
                            }
                            if (jsonArrayItem.length() > 0) {
                                jCart.put(String.valueOf(item.getRestaurantItemId()), jsonArrayItem);
                                getMenusCart().putItemForRestaurant(getVendorOpened(), item);
                            }
                        }
                    }
                }
            }
//            if (getVendorOpened() != null) {
//                JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
//                if (getVendorOpened().getRestaurantId().equals(jsonSavedCart.optInt(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId()))) {
//                    if (jCart.length() > 0) {
//                        jCart.put(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId());
//                        jCart.put(Constants.KEY_RESTAURANT_NAME, getVendorOpened().getName());
//                    }
//                    Prefs.with(this).save(Constants.SP_MENUS_CART, jCart.toString());
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateItemListFromSPDB() {
        if (isMenusOrDeliveryOpen()) {
            updateItemListFromSPMenus();
        } else {
            updateItemListFromDBFMG(null);
        }
    }


    /**
     * Updates productResponse item list quantity from db and db items hashMap
     * @param subItems list to update, if null whole categories will be updated
     */
    public void updateItemListFromDBFMG(ArrayList<SubItem> subItems) {
        try {
            // hash Current delivery store cart object for updating subItems list quantity
            HashMap<Integer, SubItem> subItemHashMap = getCart().getSubItemHashMap(getOpenedVendorId());

            if (subItems == null && getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        updateSubItemInListAndSaveInDB(subItemHashMap, subItem);
                    }
                }
            } else if (subItems != null) {
                for (SubItem subItem : subItems) {
                    updateSubItemInListAndSaveInDB(subItemHashMap, subItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSubItemInListAndSaveInDB(HashMap<Integer, SubItem> subItemHashMap, SubItem subItem){
        // updating deliveryStoreCart subItem hashMap
        if(subItemHashMap.containsKey(subItem.getSubItemId())){
            subItem.setSubItemQuantitySelected(subItemHashMap.get(subItem.getSubItemId()).getSubItemQuantitySelected());
            if (subItem.getStock() < subItem.getSubItemQuantitySelected()) {
                subItem.setSubItemQuantitySelected(subItem.getStock());
            }
            if (subItem.getSubItemQuantitySelected() > 0) {
                subItemHashMap.put(subItem.getSubItemId(), subItem);
            } else {
                subItemHashMap.remove(subItem.getSubItemId());
            }
        } else {
            subItem.setSubItemQuantitySelected(0);
        }
    }

    private void updateItemListFromSPMenus() {
        try {
            if (getMenuProductsResponse() != null
                    && getMenuProductsResponse().getCategories() != null) {
                for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                    if (category.getSubcategories() != null) {
                        for (Subcategory subcategory : category.getSubcategories()) {
                            for (Item item : subcategory.getItems()) {
                                getMenusCart().updateItemForRestaurant(getVendorOpened(), item);
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            getMenusCart().updateItemForRestaurant(getVendorOpened(), item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clearCart() {
        Paper.book().delete(DB_FRESH_CART);
        Paper.book().delete(DB_PREVIOUS_VENDOR);
        createAppCart(Config.getFreshClientId());
        updateItemListFromSPDB();
    }

    private void clearMealCart() {
        Paper.book().delete(DB_MEALS_CART);
        Paper.book().delete(DB_PREVIOUS_VENDOR);
        createAppCart(Config.getMealsClientId());
        updateItemListFromSPDB();
    }

    private void clearGroceryCart() {
    }



    public void clearMenusCart(int appType) {

        Prefs.with(this).remove(appType== AppConstant.ApplicationType.MENUS?Constants.CART_STATUS_REORDER_ID:Constants.CART_STATUS_REORDER_ID_CUSTOMER_DELIVERY);
        Paper.book().delete(appType==AppConstant.ApplicationType.MENUS?DB_MENUS_CART:DB_DELIVERY_CUSTOMER_CART);
        createAppCart(appType== AppConstant.ApplicationType.MENUS?Config.getMenusClientId():Config.getDeliveryCustomerClientId());
        updateItemListFromSPDB();
    }

    @Subscribe
    public void onSortEvent(SortSelection event) {
        try {
            Comparator comparator = null;
            if (isMenusOrDeliveryOpen()) {
                switch (event.postion) {
                    case 0:
                        comparator = new ItemCompareAtoZ();
                        GAUtils.event(getGaCategory(), GAAction.SORT_POPUP ,GAAction.A_Z);

                        break;
                    case 1:
                        comparator = new ItemComparePriceLowToHigh();
                        GAUtils.event(getGaCategory(), GAAction.SORT_POPUP ,GAAction.PRICE_LOW_TO_HIGH);
                        break;
                    case 2:
                        comparator = new ItemComparePriceHighToLow();
                        GAUtils.event(getGaCategory(), GAAction.SORT_POPUP ,GAAction.PRICE_HIGH_TO_LOW);

                        break;
                    default:
                        break;
                }
                try {
                    if (comparator != null) {
                        for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                            if (category.getSubcategories() != null) {
                                for (Subcategory subcategory : category.getSubcategories()) {
                                    Collections.sort(subcategory.getItems(), comparator);
                                }
                            } else if (category.getItems() != null) {
                                Collections.sort(category.getItems(), comparator);
                            }
                        }
                        mBus.post(new UpdateMainList(true));
                    }
                } catch (Exception e) {
                }
            } else {
                switch (event.postion) {
                    case 0:
                        comparator = new SubItemCompareAtoZ();
                        GAUtils.event(getGaCategory(), SORT_POPUP, A_Z);
                        break;
                    case 1:
                        comparator = new SubItemComparePriority();
                        GAUtils.event(getGaCategory(), SORT_POPUP, POPULARITY);
                        break;
                    case 2:
                        comparator = new SubItemComparePriceLowToHigh();
                        GAUtils.event(getGaCategory(), SORT_POPUP, PRICE_LOW_TO_HIGH);
                        break;
                    case 3:
                        comparator = new SubItemComparePriceHighToLow();
                        GAUtils.event(getGaCategory(), SORT_POPUP, PRICE_HIGH_TO_LOW);
                        break;
                    default:
                        break;
                }
                try {
                    if (comparator != null) {
                        for (Category category : productsResponse.getCategories()) {
                            Collections.sort(category.getSubItems(), comparator);
                        }
                        mBus.post(new UpdateMainList(true));
                    }
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public MenuBar getMenuBar() {
        return menuBar;
    }


    private DeepLinkAction deepLinkAction = new DeepLinkAction();
    private PushDialog pushDialog;

    private void openPushDialog() {
        dismissPushDialog(false);
        PushDialog dialog = new PushDialog(FreshActivity.this, new PushDialog.Callback() {
            @Override
            public void onButtonClicked(int deepIndex, String url) {
                if ("".equalsIgnoreCase(url)) {
                    Data.deepLinkIndex = deepIndex;
                    deepLinkAction.openDeepLink(FreshActivity.this, getSelectedLatLng());
                } else {
                    Utils.openUrl(FreshActivity.this, url);
                }
            }
        }).show();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (dialog != null) {
            pushDialog = dialog;
        }
    }

    private void dismissPushDialog(boolean clearDialogContent) {
        if (pushDialog != null) {
            pushDialog.dismiss(clearDialogContent);
            pushDialog = null;
        }
    }

    public void openDeepIndex(){
        if(deepLinkAction != null && menuBar != null) {
            deepLinkAction.openDeepLink(FreshActivity.this, getSelectedLatLng());
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
                    paytmRechargeDialog = new PaytmRechargeDialog(FreshActivity.this,
                            Data.userData.getPaytmRechargeInfo().getTransferId(),
                            Data.userData.getPaytmRechargeInfo().getTransferSenderName(),
                            Data.userData.getPaytmRechargeInfo().getTransferPhone(),
                            Data.userData.getPaytmRechargeInfo().getTransferAmount(),
                            new PaytmRechargeDialog.Callback() {
                                @Override
                                public void onOk() {
                                    if (Data.userData != null) {
                                        Data.userData.setPaytmRechargeInfo(null);
                                        Prefs.with(FreshActivity.this).save(SPLabels.CHECK_BALANCE_LAST_TIME,
                                                (System.currentTimeMillis() - (2 * FETCH_WALLET_BALANCE_REFRESH_TIME)));
                                        fetchWalletBalance(FreshActivity.this);
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


    private PromoCoupon selectedPromoCoupon;

    public PromoCoupon getSelectedPromoCoupon() {
        return selectedPromoCoupon;
    }

    public void setSelectedPromoCoupon(PromoCoupon selectedPromoCoupon) {
        this.selectedPromoCoupon = selectedPromoCoupon;
    }


    private ApiAddHomeWorkAddress apiAddHomeWorkAddress;

    public void hitApiAddHomeWorkAddress(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId,
                                         final boolean editThisAddress, final int placeRequestCode) {
        if (apiAddHomeWorkAddress == null) {
            apiAddHomeWorkAddress = new ApiAddHomeWorkAddress(this, new ApiAddHomeWorkAddress.Callback() {
                @Override
                public void onSuccess(SearchResult searchResult, String strResult, boolean addressDeleted) {
                    try {
                        Fragment deliveryAddressesFragment = getDeliveryAddressesFragment();
                        if (deliveryAddressesFragment != null) {
                            getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                        if (!addressDeleted) {
                            if(getAnywhereHomeFragment() == null){
                                setSelectedAddressId(searchResult.getId());
                                saveOfferingLastAddress(appType);
                            }
                        } else {
                            setSelectedAddress("");
                            setSelectedLatLng(null);
                            setSelectedAddressId(0);
                            setSelectedAddressType("");

                            setRefreshCart(true);
                            saveOfferingLastAddress(appType);
                            setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));

                            try {
                                if (getUserCheckoutResponse() != null) {
                                    if (getUserCheckoutResponse().getCheckoutData().getLastAddressId().equals(searchResult.getId())) {
                                        getUserCheckoutResponse().getCheckoutData().setLastAddressId(0);
                                        getUserCheckoutResponse().getCheckoutData().setLastAddressType("");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if (getFreshCheckoutMergedFragment() != null) {
                                getFreshCheckoutMergedFragment().updateAddressView();
                                GAUtils.event(getGaCategory(), CHECKOUT, DELIVERY_ADDRESS+MODIFIED);
                            } else if (getProsCheckoutFragment() != null) {
                                getProsCheckoutFragment().updateAddressView();
                                GAUtils.event(getGaCategory(), CHECKOUT, DELIVERY_ADDRESS+MODIFIED);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            });
        }
        apiAddHomeWorkAddress.addHomeAndWorkAddress(searchResult, deleteAddress, matchedWithOtherId, editThisAddress, placeRequestCode);
    }

    private int placeRequestCode = Constants.REQUEST_CODE_ADD_NEW_LOCATION;

    public int getPlaceRequestCode() {
        return placeRequestCode;
    }

    public void setPlaceRequestCode(int placeRequestCode) {
        this.placeRequestCode = placeRequestCode;
    }

    private SearchResult searchResult;

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    private boolean editThisAddress;

    public boolean isEditThisAddress() {
        return editThisAddress;
    }

    public void setEditThisAddress(boolean editThisAddress) {
        this.editThisAddress = editThisAddress;
    }


    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public LatLng getSelectedLatLng() {
        if (selectedLatLng != null) {
            return selectedLatLng;
        } else {
            return new LatLng(Data.latitude, Data.longitude);
        }
    }

    public void setSelectedLatLng(LatLng selectedLatLng) {
        this.selectedLatLng = selectedLatLng;
    }

    public int getSelectedAddressId() {
        return selectedAddressId;
    }

    public void setSelectedAddressId(int selectedAddressId) {
        this.selectedAddressId = selectedAddressId;
    }

    public String getSelectedAddressType() {
        return selectedAddressType;
    }

    public void setSelectedAddressType(String selectedAddressType) {
        this.selectedAddressType = selectedAddressType;
    }


    private DeliveryAddress deliveryAddressToEdit;

    public DeliveryAddress getDeliveryAddressToEdit() {
        return deliveryAddressToEdit;
    }

    public void setDeliveryAddressToEdit(DeliveryAddress deliveryAddressToEdit) {
        this.deliveryAddressToEdit = deliveryAddressToEdit;
    }


    public MealAddonItemsFragment getMealAddonItemsFragment() {
        return (MealAddonItemsFragment) getSupportFragmentManager().findFragmentByTag(MealAddonItemsFragment.class.getName());
    }

    public boolean isMealAddonItemsAvailable() {
        return Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType) == AppConstant.ApplicationType.MEALS
                && getProductsResponse().getCategories().size() > 1
                && getProductsResponse().getCategories().get(1).getSubItems() != null
                && getProductsResponse().getCategories().get(1).getSubItems().size() > 0;
    }

    public boolean checkForMinus(int position, SubItem subItem) {
        if (isMealAddonItemsAvailable()) {
            updateItemListFromDBFMG(null);
            boolean addOnAdded = false;
            boolean itemIsAddon = false;
            for (SubItem si : getProductsResponse().getCategories().get(1).getSubItems()) {
                if (si.getSubItemQuantitySelected() > 0) {
                    addOnAdded = true;
                }
                if (si.getSubItemId().equals(subItem.getSubItemId())) {
                    itemIsAddon = true;
                }
            }
            int mealsQuantity = 0;
            for (SubItem si : getProductsResponse().getCategories().get(0).getSubItems()) {
                mealsQuantity = mealsQuantity + si.getSubItemQuantitySelected();
            }
            return !(addOnAdded && !itemIsAddon && mealsQuantity == 1);
        } else {
            return true;
        }
    }

    public void clearMealsCartIfNoMainMeal() {
        try {
            Fragment frag = getFreshCheckoutMergedFragment();
            if (frag != null) {
                ((FreshCheckoutMergedFragment) frag).deleteCart();
            } else {
                frag = getMealAddonItemsFragment();
                if (frag != null) {
                    ((MealAddonItemsFragment) frag).deleteCart();
                } else {
                    if (getProductsResponse() != null && getProductsResponse().getCategories() != null) {
                        for (Category category : getProductsResponse().getCategories()) {
                            for (SubItem subItem : category.getSubItems()) {
                                if (subItem.getSubItemQuantitySelected() > 0) {
                                    subItem.setSubItemQuantitySelected(0);
                                    saveSubItemToDeliveryStoreCart(subItem);
                                }
                            }
                        }
                    }
                    updateCartValuesGetTotalPrice();
                }
            }
            clearMealCart();
            updateItemListFromSPDB();
            updateCartValuesGetTotalPrice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AddToAddressBookFragment getAddToAddressBookFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        if (fragmentTag.equalsIgnoreCase(AddToAddressBookFragment.class.getName())) {
            return (AddToAddressBookFragment) fragmentManager.findFragmentByTag(fragmentTag);
        } else {
            return null;
        }
    }


    public Fragment getTopFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openCart(int appType, boolean forceCheckout) {
        if (isMenusOrDeliveryOpen() && getVendorOpened() != null) {

            if(getVendorOpened().getOutOfRadius()==1 && isDeliveryOpenInBackground()){
                FreshCheckoutMergedFragment.orderViaFatafat(FreshCheckoutMergedFragment.prepareItemsInCartForMenus(this,null),null,
                        this,updateCartValuesGetTotalPrice().first);
                return;
            }

            if (forceCheckout || canExitVendorMenu()){
                getTransactionUtils().openCheckoutMergedFragment(FreshActivity.this, relativeLayoutContainer);
            }

        } else {
            getTransactionUtils().openCheckoutMergedFragment(FreshActivity.this, relativeLayoutContainer);
        }
    }


    public void saveCheckoutData(boolean clearData) {
        Gson gson = new Gson();
        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
        CheckoutSaveData checkoutSaveData;
        if (clearData) {
            checkoutSaveData = new CheckoutSaveData();
        } else {
            checkoutSaveData = new CheckoutSaveData(getPaymentOption().getOrdinal(), getSpecialInst(), getSelectedAddress(),
                    getSelectedLatLng(), getSelectedAddressId(), getSelectedAddressType());
			if(isMenusOrDeliveryOpen() && getVendorOpened() != null){
				checkoutSaveData.setRestaurantId(getVendorOpened().getRestaurantId());
			}
        }
        if (appType == AppConstant.ApplicationType.FRESH) {
            Prefs.with(this).save(Constants.SP_FRESH_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.MEALS) {
            Prefs.with(this).save(Constants.SP_MEALS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.GROCERY) {
            Prefs.with(this).save(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.MENUS) {
            Prefs.with(this).save(Constants.SP_MENUS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
            Prefs.with(this).save(Constants.SP_DELIVERY_CUSTOMER_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        }
    }

    public CheckoutSaveData getCheckoutSaveData() {
        Gson gson = new Gson();
        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
        if (appType == AppConstant.ApplicationType.MEALS) {
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_MEALS_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        } else if (appType == AppConstant.ApplicationType.GROCERY) {
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        } else if (isMenusOrDeliveryOpen()) {
			CheckoutSaveData checkoutSaveData = gson.fromJson(Prefs.with(this).getString(appType==AppConstant.ApplicationType.MENUS?Constants.SP_MENUS_CHECKOUT_SAVE_DATA:Constants.SP_DELIVERY_CUSTOMER_CHECKOUT_SAVE_DATA
					,gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
			if(getVendorOpened() != null
					&& getVendorOpened().getRestaurantId() != null
					&& checkoutSaveData.getRestaurantId() != getVendorOpened().getRestaurantId()){
				checkoutSaveData.setSpecialInstructions("");
			}

            return checkoutSaveData;
        } else {
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_FRESH_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        }
    }

    public boolean refreshCart2 = false;
    private boolean refreshCart = false;

    public boolean isRefreshCart() {
        return refreshCart;
    }

    public void setRefreshCart(boolean refreshCart) {
        this.refreshCart = refreshCart;
    }


    private MenusResponse.Vendor vendorOpened;

    public MenusResponse.Vendor getVendorOpened() {
        return vendorOpened;
    }

    public void setVendorOpened(MenusResponse.Vendor vendorOpened) {
        this.vendorOpened = vendorOpened;
        getMenusCart().updateRestaurant(vendorOpened);
    }


    private MenusResponse menusResponse;

    public MenusResponse getMenusResponse() {
        return menusResponse;
    }

    public void setMenusResponse(MenusResponse menusResponse) {
        this.menusResponse = menusResponse;
    }

    public void saveFilters() {
        boolean isMenus = getAppType()==AppConstant.ApplicationType.MENUS;
        if(sortBySelected == null){
            sortBySelected = new MenusResponse.KeyValuePair("");
        }
        Prefs.with(this).save(isMenus?Constants.SP_MENUS_FILTER_SORT_BY_OBJ:Constants.SP_DELIVERY_CUSTOMER_FILTER_SORT_BY_OBJ, sortBySelected, MenusResponse.KeyValuePair.class);
        if(sortBySelected != null && TextUtils.isEmpty(sortBySelected.getKey())){
            sortBySelected = null;
        }
        JsonElement element = gson.toJsonTree(cuisinesSelected, new TypeToken<List<FilterCuisine>>() {}.getType());
        if(element != null && element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            Prefs.with(this).save(isMenus?Constants.SP_MENUS_FILTER_CUISINES_GSON:Constants.SP_DELIVERY_CUSTOMER_FILTER_CUISINES_GSON, jsonArray.toString());
        }

        JsonElement elementF = gson.toJsonTree(filterSelected, new TypeToken<List<MenusResponse.KeyValuePair>>() {}.getType());
        if(elementF != null && elementF.isJsonArray()) {
            JsonArray jsonArray = elementF.getAsJsonArray();
            Prefs.with(this).save(isMenus?Constants.SP_MENUS_FILTER_QUICK_OBJ:Constants.SP_DELIVERY_CUSTOMER_FILTER_QUICK_OBJ, jsonArray.toString());
        }
    }


    public void fetchFiltersFromSP(int appType) {
        boolean isMenus = appType==AppConstant.ApplicationType.MENUS;

        sortBySelected = Prefs.with(this).getObject(isMenus?Constants.SP_MENUS_FILTER_SORT_BY_OBJ:Constants.SP_DELIVERY_CUSTOMER_FILTER_SORT_BY_OBJ, MenusResponse.KeyValuePair.class);
        if(sortBySelected != null && TextUtils.isEmpty(sortBySelected.getKey())){
            sortBySelected = null;
        }

        String cuisines = Prefs.with(this).getString(isMenus?Constants.SP_MENUS_FILTER_CUISINES_GSON:Constants.SP_DELIVERY_CUSTOMER_FILTER_CUISINES_GSON, "");
        if (!TextUtils.isEmpty(cuisines)) {
            cuisinesSelected = gson.fromJson(cuisines, new TypeToken<List<FilterCuisine>>() {}.getType());
        }

        String filters = Prefs.with(this).getString(isMenus?Constants.SP_MENUS_FILTER_QUICK_OBJ:Constants.SP_DELIVERY_CUSTOMER_FILTER_QUICK_OBJ, "");
        if (!TextUtils.isEmpty(filters)) {
            filterSelected = gson.fromJson(filters, new TypeToken<List<MenusResponse.KeyValuePair>>() {}.getType());
        }
    }


    private MenusResponse.KeyValuePair sortBySelected;
    private ArrayList<FilterCuisine> cuisinesSelected = new ArrayList<>();
    private ArrayList<MenusResponse.KeyValuePair> filterSelected = new ArrayList<>();
    private ArrayList<FilterCuisine> cuisinesAll = new ArrayList<>();
    private ArrayList<MenusResponse.KeyValuePair> filtersAll = new ArrayList<>();
    private ArrayList<MenusResponse.KeyValuePair> sortAll = new ArrayList<>();

    public MenusResponse.KeyValuePair getSortBySelected() {
        return sortBySelected;
    }

    public void setSortBySelected(MenusResponse.KeyValuePair sortBySelected) {
        this.sortBySelected = sortBySelected;
    }

    public ArrayList<FilterCuisine> getCuisinesSelected() {
        return cuisinesSelected;
    }

    public ArrayList<MenusResponse.KeyValuePair> getFilterSelected() {
        return filterSelected;
    }

    public ArrayList<FilterCuisine> getCuisinesAll() {
        return cuisinesAll;
    }

    public void setCuisinesAll(ArrayList<FilterCuisine> cuisinesAll) {
        this.cuisinesAll = cuisinesAll;
    }

    public ArrayList<MenusResponse.KeyValuePair> getSortAll() {
        return sortAll;
    }

    public void setSortAll(ArrayList<MenusResponse.KeyValuePair> sortAll) {
        this.sortAll = sortAll;
    }

    public ArrayList<MenusResponse.KeyValuePair> getFiltersAll() {
        return filtersAll;
    }

    public void setFiltersAll(ArrayList<MenusResponse.KeyValuePair> filtersAll) {
        this.filtersAll = filtersAll;
    }

    public boolean isFilterApplied(){
        return sortBySelected != null
                || cuisinesSelected.size() > 0
                || filterSelected.size() > 0;
    }

    public boolean checkForAdd() {
        if (isMenusOrDeliveryOpen()) {
            return checkForAdd(-1, null, null);
        } else {
            return checkForAddFM();
        }
    }

    private boolean checkForAddFM(){
        Set<Integer> keySet = getCart().getVendorCartHashMap().keySet();
        for(Integer vendorId : keySet){
            if(!vendorId.equals(getOpenedVendorId()) && getCart().getCartItems(vendorId).size() > 0){
                final DeliveryStore deliveryStoreLast = getLastDeliveryStore();
                if(deliveryStoreLast != null) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                            getString(R.string.you_have_selected_cart_from_this_vendor_clear_cart, deliveryStoreLast.getVendorName()),
                            getString(R.string.clear_cart),
                            getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (deliveryStoreLast != null) {
                                        getCart().getSubItemHashMap(deliveryStoreLast.getVendorId()).clear();
                                    }
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, true, false);
                }
                return false;
            }
        }
        return true;
    }

    public boolean checkForAdd(final int position, final Item item, final MenusCategoryItemsAdapter.CallbackCheckForAdd callbackCheckForAdd) {
        try {
            RestaurantCart restaurantCart = getMenusCart().getRestaurantCartFilled();

//            JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
            if (getVendorOpened() != null && restaurantCart != null
                    && !getVendorOpened().getRestaurantId().equals(restaurantCart.getRestaurant().getRestaurantId())) {
                String oldRestaurantName = restaurantCart.getRestaurant().getName();
                DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                        getString(R.string.previous_vendor_cart_message_format, oldRestaurantName),
                        getString(R.string.ok), getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearMenusCart(getAppType());
                                if (callbackCheckForAdd != null) {
                                    callbackCheckForAdd.addConfirmed(position, item);
                                }
                                getVendorMenuFragment().onUpdateListEvent(new UpdateMainList(true));
                                updateCartValuesGetTotalPrice();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, false, false);


                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }


    public void getAddressAndFetchOfferingData(final LatLng currentLatLng, final int appType) {
        try {
            DialogPopup.showLoadingDialog(this, "Loading...");
            RestClient.getGoogleApiService().geocode(currentLatLng.latitude + "," + currentLatLng.longitude,
                    "en", false, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            try {
                                DialogPopup.dismissLoadingDialog();
                                String resp = new String(((TypedByteArray) response.getBody()).getBytes());
                                GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(resp);
                                String address = gapiAddress.formattedAddress;
                                setSelectedAddress(address);
                                setSelectedLatLng(currentLatLng);
                                setSelectedAddressId(0);
                                setSelectedAddressType("");
                                setAddressAndFetchOfferingData(appType);

                            } catch (Exception e) {
                                e.printStackTrace();
                                retryDialogLocationFetch(DialogErrorType.SERVER_ERROR, currentLatLng, appType);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                            retryDialogLocationFetch(DialogErrorType.CONNECTION_LOST, currentLatLng, appType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
            retryDialogLocationFetch(DialogErrorType.CONNECTION_LOST, currentLatLng, appType);
        }
    }

    private void retryDialogLocationFetch(DialogErrorType dialogErrorType, final LatLng currentLatLng, final int appType) {
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAddressAndFetchOfferingData(currentLatLng, appType);
                    }

                    @Override
                    public void neutralClick(View view) {
                        ActivityCompat.finishAffinity(FreshActivity.this);
                    }

                    @Override
                    public void negativeClick(View view) {
                        ActivityCompat.finishAffinity(FreshActivity.this);
                    }
                });
    }


    public void setAddressTextToLocationPlaceHolder(){
        try {
            String address = "";
            if (TextUtils.isEmpty(getSelectedAddressType())) {
				address = getSelectedAddress();
			} else {
				address = getSelectedAddressType();
			}
            setLocationAddress(address);
            if(deliveryAddressView != null) {
                deliveryAddressView.showConfirmAddressBar(TextUtils.isEmpty(getSelectedAddressType()) || TextUtils.isEmpty(address));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OfferingsVisibilityController offeringsVisibilityController ;
    public void setAddressAndFetchOfferingData(int appType) {
        try {
            setAddressTextToLocationPlaceHolder();
            boolean checkForOfferingsVisibility = true;
            if (getFreshCheckoutMergedFragment() == null && getFeedbackFragment() == null && getProsCheckoutFragment() == null) {
                if (appType == AppConstant.ApplicationType.FRESH && getFreshHomeFragment() != null) {
                    getFreshHomeFragment().getSuperCategoriesAPI(true);
                } else if (appType == AppConstant.ApplicationType.MEALS && getMealFragment() != null) {
                    getMealFragment().getAllProducts(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.GROCERY && getGroceryFragment() != null) {
                    getGroceryFragment().getAllProducts(true, getSelectedLatLng());
                } else if ((appType == AppConstant.ApplicationType.MENUS || appType == AppConstant.ApplicationType.DELIVERY_CUSTOMER) && getMenusFragment() != null) {
                    checkForOfferingsVisibility = false; //in this case the offering visibility object is returned in menus api
                    getMenusFragment().getAllMenus(true, getSelectedLatLng(), false, null, MenusFragment.TYPE_API_MENUS_ADDRESS_CHANGE);
                } else if (appType == AppConstant.ApplicationType.FEED && getFeedHomeFragment() != null) {
                    getFeedHomeFragment().fetchFeedsApi(true, true, true);
                } else if (appType == AppConstant.ApplicationType.PROS && getProsHomeFragment() != null) {
                    getProsHomeFragment().getSuperCategoriesAPI(true);
                }
                if (checkForOfferingsVisibility && !isDeliveryOpenInBackground()) {
                    // no need to refresh if delivery customer is open because jeanie is only shown at home page
                    if(offeringsVisibilityController==null){
                        offeringsVisibilityController=  new OfferingsVisibilityController(this,getSelectedLatLng(),fabViewTest,menuBar);
                    }
                    offeringsVisibilityController.fetchOfferingsCorrespondingToCurrentAddress(getSelectedLatLng());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLocalityAddressFirstTime(int appType) {
        if (selectedLatLng == null || TextUtils.isEmpty(getSelectedAddress())) {
            setOfferingLastAddressToActivityVariables(appType);
        } else {
            setAddressAndFetchOfferingData(appType);
        }
    }


    public void saveOfferingLastAddress(int appType) {
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = new SearchResult(getSelectedAddressType(), getSelectedAddress(), "",
                    getSelectedLatLng().latitude, getSelectedLatLng().longitude);
            searchResultLocality.setId(getSelectedAddressId());
            searchResultLocality.setIsConfirmed(1);
            if(appType == AppConstant.ApplicationType.FEED){
                Prefs.with(this).save(Constants.SP_ASKLOCAL_LAST_ADDRESS_OBJ, gson.toJson(searchResultLocality, SearchResult.class));
            } else {
                Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, gson.toJson(searchResultLocality, SearchResult.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOfferingLastAddressToActivityVariables(int appType) {
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = gson.fromJson(Prefs.with(this)
                    .getString((appType == AppConstant.ApplicationType.FEED ?
                            Constants.SP_ASKLOCAL_LAST_ADDRESS_OBJ :
                            Constants.SP_FRESH_LAST_ADDRESS_OBJ), Constants.EMPTY_JSON_OBJECT), SearchResult.class);
            if (searchResultLocality != null && !TextUtils.isEmpty(searchResultLocality.getAddress())) {
                setSearchResultToActVarsAndFetchData(searchResultLocality, appType);
            } else {
                SearchResult searchResult = homeUtil.getNearBySavedAddress(FreshActivity.this, getSelectedLatLng(),
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, true);
                if (searchResult != null && !TextUtils.isEmpty(searchResult.getAddress())) {
                    setSearchResultToActVarsAndFetchData(searchResult, appType);
					saveOfferingLastAddress(appType);
                } else {
                    getAddressAndFetchOfferingData(getSelectedLatLng(), appType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearchResultToActVarsAndFetchData(SearchResult searchResultLocality, int appType) {
        setSelectedAddress(searchResultLocality.getAddress());
        setSelectedLatLng(searchResultLocality.getLatLng());
        setSelectedAddressId(searchResultLocality.getId());
        setSelectedAddressType(searchResultLocality.getName());
        setAddressAndFetchOfferingData(appType);
    }

    @Subscribe
    public void onAddressUpdated(AddressAdded event) {
        try {

            if (event.flag) {
                if (getFreshCheckoutMergedFragment() != null) {
                    setRefreshCart(true);
                    GAUtils.event(getGaCategory(), DELIVERY_ADDRESS, MODIFIED);
                }
                int appType = getAppType();
                if(!event.dontRefresh) {
                    setCategoryIdOpened(null);
                    setAddressAndFetchOfferingData(appType);
                }
                saveOfferingLastAddress(appType);
                if (getFreshCheckoutMergedFragment() != null && (getDeliveryAddressesFragment() != null || getAddToAddressBookFragmentDirect() != null)) {
                    getFreshCheckoutMergedFragment().setDeliveryAddressUpdated(true);
                }


            }
            if(event.hasUserChangedAddress){
                FreshActivity.saveAddressRefreshBoolean(this,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HomeUtil homeUtil = new HomeUtil();
    public HomeUtil getHomeUtil(){
        return homeUtil;
    }

    private LatLng menuRefreshLatLng;

    public LatLng getMenuRefreshLatLng() {
        return menuRefreshLatLng;
    }

    public void setMenuRefreshLatLng(LatLng menuRefreshLatLng) {
        this.menuRefreshLatLng = menuRefreshLatLng;
    }


    private PaySDKUtils paySDKUtils;

    public PaySDKUtils getPaySDKUtils() {
        if (paySDKUtils == null) {
            paySDKUtils = new PaySDKUtils();
        }
        return paySDKUtils;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode==Constants.FUGU_CUSTOM_RESULT_CODE){


                return;
            }

            if (requestCode == PaySDKUtils.REQUEST_CODE_SEND_MONEY && resultCode == Activity.RESULT_OK && data != null) {
                MessageRequest messageRequest = getPaySDKUtils().parseSendMoneyData(data);
                getFreshCheckoutMergedFragment().apiPlaceOrderPayCallback(messageRequest);
            } else {
                if (data == null) {
                    getFreshCheckoutMergedFragment().apiPlaceOrderPayCallback(null);
                }
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetCollapseToolbar() {


        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                if (behavior != null) {
                    int[] consumed = new int[2];
                    behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, 1, consumed);
                }
            }
        }, 50);


    }


    public void openMenusItemCustomizeFragment(final int categoryPos, final int subCategoryPos, final int itemPos) {

        if (canExitVendorMenu()) {
            appBarLayout.setExpanded(false, false);
            getTransactionUtils().openMenusItemCustomizeFragment(FreshActivity.this, getRelativeLayoutContainer(), categoryPos, subCategoryPos, itemPos);
        }
    }

    public int getAppType() {
        return Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
    }


    public ArrayList<SubItem> fetchCartList() {
        ArrayList<SubItem> subItemsInCart = new ArrayList<>();
        try {
            // items fetched from opened store
            subItemsInCart.clear();
            subItemsInCart.addAll(getCart().getCartItems(getOpenedVendorId()));
            Collections.reverse(subItemsInCart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subItemsInCart;
    }


    /**
     * only called to overwrite db saved cart in the Checkout page api
     * @param subItems to save in cart
     */
    public void saveCartList(ArrayList<SubItem> subItems) {
        try {
            // hash Current delivery store cart object for updating subItems list quantity
            HashMap<Integer, SubItem> subItemHashMap = getCart().getSubItemHashMap(getOpenedVendorId());
            subItemHashMap.clear();
            for (SubItem subItem : subItems) {
                // updating deliveryStoreCart subItem hashMap
                subItemHashMap.put(subItem.getSubItemId(), subItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCartCityId() {
        try {
            return getCart().getCityId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setCartCityId(int cityId) {
        try {
            getCart().setCityId(cityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean cartChangedAtCheckout = false;

    public boolean getCartChangedAtCheckout() {
        return cartChangedAtCheckout;
    }

    public void setCartChangedAtCheckout(boolean cartChangedAtCheckout) {
        this.cartChangedAtCheckout = cartChangedAtCheckout;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (getTopFragment() instanceof FreshSearchFragment) {
                    getFreshSearchFragment().searchFreshItems(s.toString());
                } else if (getTopFragment() instanceof MenusFragment) {
                    getMenusFragment().searchRestaurant(s.toString().trim());
                } else if (getTopFragment() instanceof MenusSearchFragment) {
                    getMenusSearchFragment().searchItems(s.toString().trim());
                }
                if (s.length() > 0) {
                    topBar.ivSearchCross.setVisibility(View.VISIBLE);
                } else {
                    topBar.ivSearchCross.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }


    private DeliveryAddressView deliveryAddressView;

    public void setDeliveryAddressView(View view) {
        deliveryAddressView = new DeliveryAddressView(this, view);
        setLlLocalityClick();
    }

    public DeliveryAddressView getDeliveryAddressView(){
        return deliveryAddressView;
    }

    private void setLocationAddress(String address) {
        if (deliveryAddressView != null) {
            deliveryAddressView.tvLocation.setText(address);
        }
       getTopBar().getTvTopBarDeliveryAddressLocation().setText(address);
    }

    private void setLlLocalityClick() {
        if (deliveryAddressView != null) {
            deliveryAddressView.llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeLocalityClick();
                }
            });
        }
    }

    public void onChangeLocalityClick() {
        textViewMinOrder.setVisibility(View.GONE);

        getTransactionUtils().openDeliveryAddressFragment(FreshActivity.this, getRelativeLayoutContainer(), canProceedWithoutSavedAddress(getTopFragment()));
        GAUtils.event(getGaCategory(), HOME, DELIVERY_ADDRESS+CLICKED);
    }

    public void setDeliveryAddressViewVisibility(int visibility) {
        if (deliveryAddressView != null) {
            deliveryAddressView.llLocation.setVisibility(visibility);
        }
    }

    /**
     *
     * @param fragment pass the fragment that is open
     * @return If we want the DeliveryAddressFragment to allow Selection of unsaved addresses in addressBook pass this as true;
     */
    public boolean canProceedWithoutSavedAddress(Fragment fragment){
        return  fragment instanceof MealFragment || fragment instanceof MenusFragment || fragment instanceof FreshHomeFragment;
    }

    public boolean checkForCityChange(final int cityId, final CityChangeCallback callback) {
        if (fetchCartList().size() > 0
                && getCartCityId() != cityId) {
            DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                    getString(R.string.delivery_address_changed_alert),
                    getString(R.string.yes), getString(R.string.no),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clearCart();
                            setCartCityId(cityId);
                            updateItemListFromSPDB();
                            updateCartValuesGetTotalPrice();
                            callback.onYesClick();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setDeliveryAddressModelToSelectedAddress(false,true);
                            callback.onNoClick();
                        }
                    }, false, false);
            return true;
        } else if (getCartCityId() != cityId) {
            setCartCityId(cityId);
            return false;
        } else {
            return false;
        }
    }

    /**
     * The Add Post fragment opens in two modes when we are adding or when are editing
     * @param feedDetail If feed detail object is null then it means a new post is being added
     */
    public void openFeedAddPostFragment(FeedDetail feedDetail) {
       getTransactionUtils().openFeedAddPostFragment(this, getRelativeLayoutContainer(),feedDetail);

    }

    public void performSuperBackPress() {
        super.onBackPressed();
    }

    public boolean isShowingEarlyBirdDiscount() {
        return showingEarlyBirdDiscount;
    }

    public void setShowingEarlyBirdDiscount(boolean showingEarlyBirdDiscount) {
        this.showingEarlyBirdDiscount = showingEarlyBirdDiscount;
    }

    public void setCurrentDeliveryStripToMinOrder() {
        try {
            if(getMenusFragment()!=null && getTopFragment() instanceof MenusFragment &&  getMenusFragment().getCurrentStripInfo()!=null && !TextUtils.isEmpty(getMenusFragment().getCurrentStripInfo().getText())){
                textViewMinOrder.setText(getMenusFragment().getCurrentStripInfo().getText().trim());
                textViewMinOrderSetVisibility(View.VISIBLE);

            }else{
                textViewMinOrderSetVisibility(View.GONE);

            }
            adjustFabMarginsForDeliveryOffering();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void adjustFabMarginsForDeliveryOffering() {
        try {
            boolean isMenusCartVisible =  menusCartSelectedLayout!=null && menusCartSelectedLayout.getRlMenusCartSelected().getVisibility()==View.VISIBLE;
            boolean isMenusStripVisible = textViewMinOrder.getVisibility()==View.VISIBLE;

            float padding = 20f;
            if(isMenusCartVisible){
                padding+=50f;
            }
            if(isMenusStripVisible){
                padding+=25f;
            }
            fabViewTest.setMenuLabelsRightTestPadding(padding);
            float scale = getResources().getDisplayMetrics().density;
            int paddingBottom = (int) (padding * scale + 0.5f);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) rlfabViewFatafat.getLayoutParams();
            layoutParams.leftMargin = 0 ;
            layoutParams.topMargin= 0;
            layoutParams.rightMargin = (int) (40f * ASSL.Yscale());
            layoutParams.bottomMargin = paddingBottom;
            rlfabViewFatafat.setLayoutParams(layoutParams);
            rlfabViewFatafat.invalidate();;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show Fatafat tutorial
     */
    public void showFatafatTutorial() {
        if(getTopFragment() instanceof AnywhereHomeFragment){
            ((AnywhereHomeFragment)getTopFragment()).showFatafatTutorial();
        }
    }

    public LatLng getCurrentPlaceLatLng() {
        return getSelectedLatLng();
    }


    public static class OrderViaChatData{
        private LatLng destinationlatLng;
        private String destinationAddress;
        private String restaurantName;
        private String cartText;
        private int restaurantId;

        public String getCartText() {
            return cartText;
        }

        public OrderViaChatData(LatLng destinationlatLng, String destinationAddress, String restaurantName, String cartText,int restaurantId) {
            this.destinationlatLng = destinationlatLng;
            this.destinationAddress = destinationAddress;
            this.restaurantName = restaurantName;
            this.cartText = cartText;
            this.restaurantId = restaurantId;
        }

        public OrderViaChatData(LatLng destinationlatLng, String destinationAddress, String restaurantName,int restaurantId) {
            this.destinationlatLng = destinationlatLng;
            this.destinationAddress = destinationAddress;
            this.restaurantName = restaurantName;
            this.restaurantId = restaurantId;
        }



        public LatLng getDestinationlatLng() {
            return destinationlatLng;
        }

        public String getDestinationAddress() {
            return destinationAddress;
        }

        public void setDestinationlatLng(LatLng destinationlatLng) {
            this.destinationlatLng = destinationlatLng;
        }

        public void setDestinationAddress(String destinationAddress) {
            this.destinationAddress = destinationAddress;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public int getRestaurantId() {
            return restaurantId;
        }
    }
    private OrderViaChatData orderViaChat ;
    public OrderViaChatData getOrderViaChat() {
        return orderViaChat;
    }


    public void setOrderViaChatData(OrderViaChatData orderViaChat) {
        this.orderViaChat = orderViaChat;

    }


    public interface CityChangeCallback {
        void onYesClick();

        void onNoClick();
    }

    private DeliveryAddressModel deliveryAddressModel;
    private Gson gson = new Gson();
    public Gson getGson(){
        return gson;
    }

    public static void saveAddressRefreshBoolean(Activity activity,boolean isEnable){
        Prefs.with(activity).save(Constants.SHOULD_REFRESH_ADDRESS,isEnable);
    }
    public static boolean shouldRefreshAddress(Activity activity){
        return Prefs.with(activity).getBoolean(Constants.SHOULD_REFRESH_ADDRESS,false);
    }

    public void saveDeliveryAddressModel() {
        saveDeliveryAddressModel(getSelectedAddress(), getSelectedLatLng(),
                getSelectedAddressId(), getSelectedAddressType());
    }

    public void saveDeliveryAddressModel(String address, LatLng latLng, int id, String type) {
        deliveryAddressModel = new DeliveryAddressModel(address, latLng, id, type);
        try {
            if (getAppType()==AppConstant.ApplicationType.MENUS) {
                Prefs.with(this).save(
                        Constants.SP_MENUS_CART_ADDRESS,
                        gson.toJson(deliveryAddressModel, DeliveryAddressModel.class));
            }else if(getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                Prefs.with(this).save(
                        Constants.SP_DELIVERY_CUSTOMER_CART_ADDRESS,
                        gson.toJson(deliveryAddressModel, DeliveryAddressModel.class));
            }else if(getAppType()==AppConstant.ApplicationType.MEALS){
                Prefs.with(this).save(
                        Constants.SP_MEALS_CUSTOMER_CART_ADDRESS,
                        gson.toJson(deliveryAddressModel, DeliveryAddressModel.class));
            }else{
                Prefs.with(this).save(Constants.SP_FRESH_CART_ADDRESS,
                        gson.toJson(deliveryAddressModel, DeliveryAddressModel.class));
            }
        } catch (Exception e) {}
    }

    public DeliveryAddressModel getDeliveryAddressModel() {
        try {
            String constantStringSp ;
            if(getAppType()==AppConstant.ApplicationType.MENUS){
                constantStringSp=Constants.SP_MENUS_CART_ADDRESS;
            }else if(getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                constantStringSp=Constants.SP_DELIVERY_CUSTOMER_CART_ADDRESS;
            }else if(getAppType()==AppConstant.ApplicationType.MEALS){
                constantStringSp=Constants.SP_MEALS_CUSTOMER_CART_ADDRESS;
            }else{
                constantStringSp=Constants.SP_FRESH_CART_ADDRESS;
            }
            deliveryAddressModel = gson.fromJson(Prefs.with(this).getString(constantStringSp, Constants.EMPTY_JSON_OBJECT), DeliveryAddressModel.class);
        } catch (Exception e) {
        }
        return deliveryAddressModel;
    }

    public void setDeliveryAddressModelToSelectedAddress(boolean dontRefresh,boolean pickUpCartAddress) {
        if (deliveryAddressModel == null || pickUpCartAddress) {
            try {
                String constantStringSp ;
                if(getAppType()==AppConstant.ApplicationType.MENUS){
                    constantStringSp=Constants.SP_MENUS_CART_ADDRESS;
                }else if(getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                    constantStringSp=Constants.SP_DELIVERY_CUSTOMER_CART_ADDRESS;
                }else if(getAppType()==AppConstant.ApplicationType.MEALS){
                    constantStringSp=Constants.SP_MEALS_CUSTOMER_CART_ADDRESS;
                }else{
                    constantStringSp=Constants.SP_FRESH_CART_ADDRESS;
                }
                deliveryAddressModel = gson.fromJson(Prefs.with(this).getString(constantStringSp,
                        Constants.EMPTY_JSON_OBJECT), DeliveryAddressModel.class);
            } catch (Exception e) {
            }
        }
        try {
            if (deliveryAddressModel != null) {
                setSelectedAddress(deliveryAddressModel.getAddress());
                setSelectedLatLng(deliveryAddressModel.getLatLng());
                setSelectedAddressId(deliveryAddressModel.getId());
                setSelectedAddressType(deliveryAddressModel.getType());
                onAddressUpdated(new AddressAdded(true, dontRefresh));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DeliveryAddressModel {
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("longitude")
        @Expose
        private Double longitude;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("type")
        @Expose
        private String type;

        public DeliveryAddressModel(String address, LatLng latLng, int id, String type) {
            this.address = address;
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
            this.id = id;
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public LatLng getLatLng() {
            return new LatLng(latitude, longitude);
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }
    }

    private State mCurrentState;
    public TextView tvCollapRestaurantName;
    public TextView tvCollapRestaurantRating, tvCollapRestaurantDeliveryTime;
    public LinearLayout llCollapRatingStars;
    private RelativeLayout rlCollapseDetails;
    public LinearLayout llCollapseRating;
    private LinearLayout llToolbarLayout;
    public ImageView ivCollapseRestImage;
    private int currentVerticalOffSet;
    private AppBarLayout.OnOffsetChangedListener collapseBarController = new AppBarLayout.OnOffsetChangedListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


            currentVerticalOffSet = verticalOffset;
            if (verticalOffset == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {


                int calculatedAlpha = -verticalOffset * 250 / appBarLayout.getTotalScrollRange();


                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(calculatedAlpha, 255, 255, 255)));



                /*this code animates the half range from 255-->0 changes icon and then again animates to 0-255 as the vertical offset increases and vice versa
                   this implies for back button,search button and capsule cart layout */
                if (-verticalOffset <= appBarLayout.getTotalScrollRange() / 2) {


                    float searchAndCapsuleAlpha2 = (((appBarLayout.getTotalScrollRange() / 2) - (-verticalOffset)) * 1.0f) / (appBarLayout.getTotalScrollRange() / 2 - 0);
                    searchAndCapsuleAlpha2 = searchAndCapsuleAlpha2 * 255;

                    if (!topBar.getIvSearch().isSelected()) {
                        topBar.getIvSearch().setSelected(true);
                    }
                    topBar.getRlSearch().setAlpha((int) searchAndCapsuleAlpha2);
                    topBar.rlFreshSort.setAlpha((int) searchAndCapsuleAlpha2);
                    if (!topBar.ivFreshSort.isSelected()) {
                        topBar.ivFreshSort.setSelected(true);
                    }
/*
                    if (!topBar.llCartContainer.isSelected())
                        topBar.llCartContainer.setSelected(true);

                    topBar.llCartContainer.getBackground().setAlpha((int) searchAndCapsuleAlpha2);*/
                    topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.argb((int) searchAndCapsuleAlpha2, 255, 255, 255), PorterDuff.Mode.SRC_ATOP);
                } else {

                    float searchAndCapsuleAlpha1 = (appBarLayout.getTotalScrollRange() - (-verticalOffset)) * 1.0f / (appBarLayout.getTotalScrollRange() - (appBarLayout.getTotalScrollRange() / 2));
                    searchAndCapsuleAlpha1 = 255 - (searchAndCapsuleAlpha1 * 255);


                    if (topBar.getIvSearch().isSelected()) {
                        topBar.getIvSearch().setSelected(false);
                    }

                    topBar.getRlSearch().setAlpha((int) searchAndCapsuleAlpha1);
                    topBar.rlFreshSort.setAlpha((int) searchAndCapsuleAlpha1);
                    if (topBar.ivFreshSort.isSelected()) {
                        topBar.ivFreshSort.setSelected(false);
                    }

                   /*  if (topBar.llCartContainer.isSelected())
                        topBar.llCartContainer.setSelected(false);

                    topBar.llCartContainer.getBackground().setAlpha((int) searchAndCapsuleAlpha1);*/
                    topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.argb((int) searchAndCapsuleAlpha1, 89, 89, 104), PorterDuff.Mode.SRC_ATOP);

                }


                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255 - calculatedAlpha));

                llCollapRatingStars.setAlpha((float) (255 - calculatedAlpha) / 255f);
                if (ivCollapseRestImage.getBackground() != null)
                    ivCollapseRestImage.getBackground().setAlpha(255 - calculatedAlpha);

                topBar.title.setTextColor(topBar.title.getTextColors().withAlpha(calculatedAlpha));
                tvCollapRestaurantDeliveryTime.setAlpha((float) (255 - calculatedAlpha) / 255f);


                if (mCurrentState != State.IDLE) {

                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;


            }
        }
    };

    private void initCollapseToolBarViews() {
        ivCollapseRestImage = (ImageView) findViewById(R.id.iv_rest_collapse_image);
        tvCollapRestaurantName = (TextView) findViewById(R.id.tv_rest_title);
        tvCollapRestaurantRating = (TextView) findViewById(R.id.tvCollapRestaurantRating);
        llCollapRatingStars = (LinearLayout) findViewById(R.id.llCollapRatingStars);
        tvCollapRestaurantDeliveryTime = (TextView) findViewById(R.id.tvCollapRestaurantDeliveryTime);
        rlCollapseDetails = (RelativeLayout) findViewById(R.id.layout_rest_details);
        llCollapseRating = (LinearLayout) findViewById(R.id.llCollapseRating);

        ivCollapseRestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getVendorOpened() != null && !TextUtils.isEmpty(getVendorOpened().getImage())){
                    openRestaurantImageFragment();
                }
            }
        });


        //to enable animate layout changes since it acts weirdly with collapsing toolbar if declared in xml because it animates whole heirarchy and hence toolbar behaves weirdly
        //This method is being used to animate the specific group and its nested children
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewGroup layout = (ViewGroup) findViewById(R.id.llSearchCart);
            LayoutTransition layoutTransition = layout.getLayoutTransition();
            if (layoutTransition != null) {
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            }
        }
       /* LayoutTransition layoutTransition = new LayoutTransition();
        topBar.getLlSearchCart().setLayoutTransition(layoutTransition);*/

        View.OnClickListener reviewCLickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestaurantReviewsListFragment();
            }
        };
        llCollapseRating.setOnClickListener(reviewCLickListener);
        tvCollapRestaurantName.setOnClickListener(reviewCLickListener);

    }
    public void switchOffering(final String lastClientId){

        if(!isLoginDataAvailable(lastClientId)){
            ApiLoginUsingAccessToken.Callback callback = new ApiLoginUsingAccessToken.Callback() {
                @Override
                public void noNet() {
                    DialogPopup.alertPopup(FreshActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
                }

                @Override
                public void success(String clientId) {

                    if (isLoginDataAvailable(lastClientId)) {
                        rlfabViewFatafat.setVisibility(View.GONE);
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
                        saveAppCart();
                        Prefs.with(FreshActivity.this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
                        setOfferingData(lastClientId,false);
                    }
                }

                @Override
                public void failure() {

                }

                @Override
                public void onRetry(View view) {
                    switchOffering(lastClientId);
                }

                @Override
                public void onNoRetry(View view) {

                }
            };
            new ApiLoginUsingAccessToken(FreshActivity.this).hit(Data.userData.accessToken, getSelectedLatLng().latitude, getSelectedLatLng().longitude, lastClientId,
                    true, callback);


        }else{
            rlfabViewFatafat.setVisibility(View.GONE);
            fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            saveAppCart();
            Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
            setOfferingData(lastClientId,false);
        }


    }

    public boolean isLoginDataAvailable(String lastClientId){

        switch (lastClientId){
            case Config.MEALS_CLIENT_ID:
               return  Data.getMealsData()!=null;
            case Config.FRESH_CLIENT_ID:
                return  Data.getFreshData()!=null;
            case Config.FEED_CLIENT_ID:
                return  Data.getFeedData()!=null;
            default:
                return  false;
        }
    }

    private void onStateChanged(AppBarLayout appBarLayout, State expanded) {
        switch (expanded) {
            case EXPANDED:
                //Toolbar
                // toolbar.getBackground().setAlpha(0);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //Title
                topBar.title.setVisibility(View.INVISIBLE);

                //Cart and Search Button
              /*  llCartContainer.getBackground().setAlpha(255);
                llCartContainer.setSelected(true);*/
                topBar.getIvSearch().setSelected(true);
                topBar.getRlSearch().setAlpha(255);
                topBar.ivFreshSort.setSelected(true);
                topBar.rlFreshSort.setAlpha(255);


                //back Button
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                topBar.imageViewBack.getDrawable().setAlpha(255);


                //Restaurant Details
                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255));
//                tvCollapRestaurantRating.setTextColor(tvCollapRestaurantRating.getTextColors().withAlpha(255));
                llCollapRatingStars.setAlpha(1f);
                tvCollapRestaurantDeliveryTime.setAlpha(1f);
                break;
            case COLLAPSED:
                //Toolbar
                //  toolbar.getBackground().setAlpha(255);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


                //Restaurant Details
                rlCollapseDetails.setVisibility(View.GONE);


                //Cart and Search Button
              /*  llCartContainer.getBackground().setAlpha(255);
                llCartContainer.setSelected(false);*/
                topBar.getIvSearch().setSelected(false);
                topBar.getRlSearch().setAlpha(255);
                topBar.ivFreshSort.setSelected(false);
                topBar.rlFreshSort.setAlpha(255);

                //back Button
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.lightBlackTxtColor), PorterDuff.Mode.SRC_ATOP);
                topBar.imageViewBack.getDrawable().setAlpha(255);

                //Title
                topBar.title.setTextColor(topBar.title.getTextColors().withAlpha(255));
                break;
            case IDLE:
                rlCollapseDetails.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void openMenuFeedback() {
        getTransactionUtils().openMenuFeedback(FreshActivity.this, relativeLayoutContainer);
    }

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    private LocationUpdate locationUpdate = new LocationUpdate() {
        @Override
        public void onLocationChanged(Location location) {
            Data.latitude = location.getLatitude();
            Data.longitude = location.getLongitude();

            if (!isLocationChangeCheckedAfterResume && shouldRefreshAddress(FreshActivity.this)) {
                LatLng currentLatlng = new LatLng(Data.latitude,Data.longitude);
                if(MapUtils.distance(currentLatlng,getSelectedLatLng())>500) {
                    setSelectedLatLng(currentLatlng);
                    setSelectedAddress("");
                    setSelectedAddressType("");
                    setSelectedAddressId(0);
                    saveOfferingLastAddress(getAppType());
                    setLocalityAddressFirstTime(getAppType());
                }
            }
            isLocationChangeCheckedAfterResume=true;
        }
    };


    /**
     * Sets restaurant's delivery time to textView provided and colorResId to its drawable
     * @param vendor restaurant object
     * @param textView textView to set delivery time to
     * @param colorResId resource id of color file for color to set to textView's drawable
     * @param isColor
     * @return returns the visibility for textView
     */
    public int setVendorDeliveryTimeAndDrawableColorToTextView(MenusResponse.Vendor vendor, TextView textView, int colorResId, boolean isColor) {
        if (!TextUtils.isEmpty(vendor.getDeliveryTimeText())) {
            textView.setText(Utils.trimHTML(Utils.fromHtml(vendor.getDeliveryTimeText() + " ")));
            setTextViewDrawableColor(textView, ContextCompat.getColor(this, colorResId));
            return View.VISIBLE;
        } else if (vendor.getDeliveryTimeText() == null) {
            showDeliveryStringWithTime(vendor, textView, colorResId, true);
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public int showDeliveryStringWithTime(MenusResponse.Vendor vendor, TextView textView, int colorResId, boolean showPrefixes) {
        try {
            String prefix =null;
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            final SpannableStringBuilder sb;
            if (vendor.getIsClosed() == 0) {
                String deliveryTime = String.valueOf(vendor.getDeliveryTime());
                if (vendor.getMinDeliveryTime() != null) {
                    deliveryTime = String.valueOf(vendor.getMinDeliveryTime()) + "-" + deliveryTime;
                }




                prefix = getString(R.string.delivers_in);


                sb = new SpannableStringBuilder(deliveryTime + " mins ");

            } else {

                prefix = getString(R.string.opens_at);


                sb = new SpannableStringBuilder(String.valueOf(DateOperations.convertDayTimeAPViaFormat(vendor.getOpensAt() + " ", false)));
            }
            sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(prefix);
            textView.append(" ");
            textView.append(sb);
            textView.append(".");
            setTextViewDrawableColor(textView, ContextCompat.getColor(this, colorResId));
        } catch (Exception e) {
            e.printStackTrace();
            return View.GONE;
        }
        return View.VISIBLE;
    }



    public FABViewTest getFabViewTest() {
        return fabViewTest;
    }

    public int setRatingAndGetColor(TextView tv, Double rating, String colorCode, boolean setBackgroundColor) {
        Spannable spannable = new SpannableString(getString(R.string.star_icon) + " " + rating);
        spannable.setSpan(new CustomTypeFaceSpan("", Fonts.iconsFont(this)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
        int ratingColor;
        if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7) ratingColor = Color.parseColor(colorCode);
        else
            ratingColor = Color.GREEN; //default Green Color

        if (setBackgroundColor) {
            setTextViewBackgroundDrawableColor(tv, ratingColor);
        }

        return ratingColor;
    }


    public int setRatingAndGetColorNewMenus(TextView tv, Double rating, String colorCode, boolean setBackgroundColor) {
        Spannable spannable = new SpannableString(getString(R.string.star_icon) + " " + rating);
        spannable.setSpan(new CustomTypeFaceSpan("", Fonts.iconsFont(this)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
        int ratingColor;
        if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7) ratingColor = Color.parseColor(colorCode);
        else
            ratingColor = Color.GREEN; //default Green Color

        if (setBackgroundColor) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#fac917")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ratingColor;
    }



    public int getParsedColor(String colorCode, Integer defaultColor){
        int ratingColor;
        if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7){
            ratingColor = Color.parseColor(colorCode);
        }
        else {
            if(defaultColor != null) {
                ratingColor = ContextCompat.getColor(this, defaultColor);
            } else{
                ratingColor = ContextCompat.getColor(this, R.color.text_color_light);
            }
        }
        return ratingColor;
    }

    public void setTextViewBackgroundDrawableColor(TextView textView, int color) {
        if (textView.getBackground() != null) {
            textView.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

    public void setTextViewDrawableColor(TextView textView, int resolvedColor) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(resolvedColor, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void openRestaurantReviewsListFragment() {
        if (getVendorOpened() != null) {
            appBarLayout.setExpanded(false, false);
            llCheckoutBarSetVisibility(View.GONE);
            topBar.rlSearch.setVisibility(View.GONE);
            GAUtils.event(GACategory.MENUS, GAAction.RESTAURANT_HOME , GAAction.FEED + GAAction.CLICKED);
            getTransactionUtils().openRestaurantReviewsListFragment(this, relativeLayoutContainer, getVendorOpened());
        }
    }

    public void openRestaurantAddReviewFragment(boolean isEditingReview, float rating) {
        if (getVendorOpened() != null) {
            if(!isEditingReview){
                this.currentReview=null;
            }
            getTransactionUtils().openRestaurantAddReviewFragment(this, relativeLayoutContainer, getVendorOpened().getRestaurantId(), rating);
        }
    }

    public boolean canExitVendorMenu() {
        if (getTopFragment() != null && collapsingToolBarEnabled(getTopFragment()) && mCurrentState == State.IDLE && currentVerticalOffSet != -1)
            return false;
        else
            return true;
    }

    public FetchFeedbackResponse.Review getCurrentReview() {
        return currentReview;
    }

    public void setCurrentReview(FetchFeedbackResponse.Review currentReview) {
        this.currentReview = currentReview;
    }

    private Handler handler;
    public Handler getHandler(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    private int reviewImageCount = 5;
    public void setReviewImageCount(int count){
        this.reviewImageCount = count;
    }

    public int getReviewImageCount(){
        return reviewImageCount;
    }

    private String gaCategory;
    public String getGaCategory(){
        if(gaCategory == null){
            int type = getAppType();
            if(type == AppConstant.ApplicationType.MEALS){
                gaCategory = GACategory.MEALS;
            } else if(type == AppConstant.ApplicationType.MENUS){
                gaCategory = GACategory.MENUS;
            } else if(type == AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                gaCategory = GACategory.DELIVERY_CUSTOMER;
            } else if(type == AppConstant.ApplicationType.PROS){
                gaCategory = GACategory.PROS;
            } else if(type == AppConstant.ApplicationType.FEED){
                gaCategory = Data.getFeedName(this)+" ";
            } else {
                gaCategory = GACategory.FRESH;
            }
        }
        return gaCategory;
    }

    public void clearEtFocus() {
        try {
            if (getTopBar() != null && getTopBar().etSearch != null) {
                getTopBar().etSearch.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ApiFetchRestaurantMenu apiFetchRestaurantMenu;
    public void fetchRestaurantMenuAPI(int restaurantId, boolean directCheckout, final JSONArray jsonArray, final LatLng latLng, final int reorderStatusId, final String reoderDelAddress){


        if(apiFetchRestaurantMenu == null){
            apiFetchRestaurantMenu = new ApiFetchRestaurantMenu(this, new ApiFetchRestaurantMenu.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view, int restaurantId, boolean directCheckout) {
                    fetchRestaurantMenuAPI(restaurantId, directCheckout, jsonArray, latLng, reorderStatusId, reoderDelAddress);
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        apiFetchRestaurantMenu.hit(restaurantId, getSelectedLatLng().latitude,
                getSelectedLatLng().longitude, directCheckout, jsonArray, latLng, reorderStatusId, reoderDelAddress);
    }


    private AppCart appCart;
    private AppCart appCartMeals;
    public AppCart getCart(){
        return getAppType()== AppConstant.ApplicationType.MEALS?appCartMeals:appCart;
    }
    private void createAppCart(String clientId){
        if(clientId.equalsIgnoreCase(Config.getFreshClientId())){
            appCart = Paper.book().read(DB_FRESH_CART, new AppCart());
        }
        else if(clientId.equalsIgnoreCase(Config.getMealsClientId())){
            appCartMeals = Paper.book().read(DB_MEALS_CART, new AppCart());
        }
        else if(clientId.equalsIgnoreCase(Config.getMenusClientId())){
            menusCart = Paper.book().read(DB_MENUS_CART, new MenusCart());
        }else if(clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())){
            deliveryCustomerCart = Paper.book().read(DB_DELIVERY_CUSTOMER_CART, new MenusCart());
        }
    }
    private void saveAppCart(){
        if(getAppType()== AppConstant.ApplicationType.FRESH){
            if(appCart != null) {
                Paper.book().write(DB_FRESH_CART, appCart);
            } else {
                Paper.book().delete(DB_FRESH_CART);
            }
        }
        else if(getAppType()== AppConstant.ApplicationType.MEALS){
            if(appCartMeals != null) {
                Paper.book().write(DB_MEALS_CART, appCartMeals);
            } else {
                Paper.book().delete(DB_MEALS_CART);
            }
        }
        else if(getAppType()== AppConstant.ApplicationType.MENUS){
            if(menusCart != null) {
                Paper.book().write(DB_MENUS_CART, menusCart);
            } else {
                Paper.book().delete(DB_MENUS_CART);
            }
        } else if(getAppType()== AppConstant.ApplicationType.DELIVERY_CUSTOMER){
            if(deliveryCustomerCart != null) {
                Paper.book().write(DB_DELIVERY_CUSTOMER_CART, deliveryCustomerCart);
            } else {
                Paper.book().delete(DB_DELIVERY_CUSTOMER_CART);
            }
        }
    }


    private Integer openedVendorId;
    private DeliveryStore openedDeliveryStore;
    public Integer getOpenedVendorId(){
        if(openedVendorId == null){
            openedVendorId = Prefs.with(this).getInt(Constants.SP_VENDOR_ID, 0);
        }
        return openedVendorId;
    }
    public void setOpenedVendorIdName(Integer vendorId, DeliveryStore deliveryStore){
        openedVendorId = vendorId;
        if(openedDeliveryStore != null){
            saveLastDeliveryStore(openedDeliveryStore);
        } else {
            saveLastDeliveryStore(deliveryStore);
        }
        openedDeliveryStore = deliveryStore;
        Prefs.with(this).save(Constants.SP_VENDOR_ID, vendorId.intValue());
    }
    public DeliveryStore getOpenedDeliveryStore(){
        if(openedDeliveryStore == null){
            openedDeliveryStore = getLastDeliveryStore();
        }
        return openedDeliveryStore;
    }

    public DeliveryStore getLastDeliveryStore(){
        return Paper.book().read(DB_PREVIOUS_VENDOR, null);
    }
    public void saveLastDeliveryStore(DeliveryStore deliveryStore){
        Paper.book().write(DB_PREVIOUS_VENDOR, deliveryStore);
    }

    public Integer getLastCartVendorId(){
        Set<Integer> keySet = getCart().getVendorCartHashMap().keySet();
        for(Integer vendorId : keySet) {
            if (getCart().getCartItems(vendorId).size() > 0) {
                return vendorId;
            }
        }
        int selectedVendorId = Prefs.with(this).getInt(Constants.SP_SELECTED_VENDOR_ID, -1);
        if(selectedVendorId != -1){
            Prefs.with(this).save(Constants.SP_SELECTED_VENDOR_ID, -1);
            return selectedVendorId;
        }
        return 0;
    }


    public void saveSubItemToDeliveryStoreCart(SubItem subItem){
        getCart().saveSubItemToStore(getOpenedVendorId(), subItem);
    }




    // razor pay callbacks
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        String paymentId = paymentData.getPaymentId();
        String signature = paymentData.getSignature();
        razorpayCallbackIntentService(paymentId, signature);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        razorpayCallbackIntentService("-1", "-1");
    }

    public void startRazorPayPayment(JSONObject options, boolean isUPA) {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.ic_launcher);
        try {
            options.remove(Constants.KEY_AUTH_ORDER_ID);

            options.put(Constants.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(Constants.KEY_USER_EMAIL).toString());
            options.put(Constants.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(Constants.KEY_PHONE_NO).toString());
            options.put(Constants.KEY_RAZORPAY_THEME_COLOR, "#FD7945");
            if(isUPA){
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "upi"); // "upi", ""
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : ""); // "upi", ""
            } else{
                options.put(Constants.KEY_RAZORPAY_PREFILL_METHOD, "");
                options.put(Constants.KEY_RAZORPAY_PREFILL_VPA, Data.userData != null ? Data.userData.getUpiHandle() : "");
            }

            Log.i(TAG, "startRazorPayPayment options="+options);
            checkout.setFullScreenDisable(true);

            checkout.open(this, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }

    // razor pay callback intent service
    public void razorpayCallbackIntentService(String paymentId, String signature){
        try {
            Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
            Intent intent = new Intent(this, RazorpayCallbackService.class);
            intent.putExtra(Constants.KEY_ACCESS_TOKEN, pair.first);
            intent.putExtra(Constants.KEY_RAZORPAY_PAYMENT_ID, paymentId);
            intent.putExtra(Constants.KEY_RAZORPAY_SIGNATURE, signature);
            intent.putExtra(Constants.KEY_ORDER_ID, getPlaceOrderResponse().getOrderId().intValue());
            intent.putExtra(Constants.KEY_AUTH_ORDER_ID, getPlaceOrderResponse().getRazorPaymentObject().getAuthOrderId().intValue());
            startService(intent);
            DialogPopup.showLoadingDialog(this, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //placeOrderResponse cached for PAY and RAZORPAY payment callbacks
    private PlaceOrderResponse placeOrderResponse;
    public void setPlaceOrderResponse(PlaceOrderResponse placeOrderResponse){
        this.placeOrderResponse = placeOrderResponse;
        if(placeOrderResponse != null) {
            Paper.book().write(PaperDBKeys.DB_PLACE_ORDER_RESP, placeOrderResponse);
        } else {
            Paper.book().delete(PaperDBKeys.DB_PLACE_ORDER_RESP);
        }
    }

    public PlaceOrderResponse getPlaceOrderResponse(){
        if(placeOrderResponse == null){
            placeOrderResponse = Paper.book().read(PaperDBKeys.DB_PLACE_ORDER_RESP);
        }
        return placeOrderResponse;
    }


    public void openFeedDetailsFragmentWithPostId(int postId, int postNotificationId){
        try {
            FeedDetail feedDetail = new FeedDetail();
            feedDetail.setPostId(postId);
            getTransactionUtils().openFeedCommentsFragment(this, getRelativeLayoutContainer(), feedDetail, -1, false, postNotificationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bind(R.id.llCheckoutBar)
	public LinearLayout llCheckoutBar;
	@Bind(R.id.tvCheckoutItemsCount)
	TextView tvCheckoutItemsCount;
	@Bind(R.id.tvCartAmount)
	TextView tvCartAmount;
    @Bind(R.id.vCheckoutShadow)
    View vCheckoutShadow;

    private FreshSortDialog freshSortDialog;
    public void openFreshSortDialog() {
        freshSortDialog = new FreshSortDialog(new FreshSortingAdapter.Callback() {
            @Override
            public void onSlotSelected(int position, SortResponseModel sort) {
                int type = getAppType();
                if (type == AppConstant.ApplicationType.MEALS) {
                    mealSort = position;
                } else if (type == AppConstant.ApplicationType.GROCERY) {
                    freshSort = position;
                } else if (type == AppConstant.ApplicationType.MENUS) {
                    menusSort = position;
                } else {
                    freshSort = position;
                }
                getBus().post(new SortSelection(position));
                if(freshSortDialog != null){
                    freshSortDialog.dismiss();
                }
            }
        }, R.style.Feed_Popup_Theme, this, slots);
        freshSortDialog.show(topBar.ivFreshSort);
        if (getAppType() == AppConstant.ApplicationType.MENUS && getTopFragment() instanceof VendorMenuFragment) {
            GAUtils.event(getGaCategory(), GAAction.RESTAURANT_HOME, GAAction.SORT_BUTTON + GAAction.CLICKED);
        } else if (getAppType() == AppConstant.ApplicationType.FRESH && getFreshFragment() != null) {
            if (getFreshFragment().getSuperCategory() != null)
                GAUtils.event(FRESH, getFreshFragment().getSuperCategory().getSuperCategoryName(), SORT_BUTTON + CLICKED);
        }
    }

    public ArrayList<SortResponseModel> getSlots(){
        return slots;
    }

    public void llCheckoutBarSetVisibilityDirect(int visibility){
        if(llCheckoutBar != null) {
			llCheckoutBar.clearAnimation();
            vCheckoutShadow.clearAnimation();
            getHandler().removeCallbacks(runnableLlCheckoutBarGone);
            if (visibility == View.VISIBLE && totalPrice > 0 && totalQuantity > 0) {
                if(getFeedbackFragment()!=null){
                    llCheckoutBar.setVisibility(View.GONE);
                    vCheckoutShadow.setVisibility(View.GONE);

                }else{
                    llCheckoutBar.setVisibility(View.VISIBLE);
                    vCheckoutShadow.setVisibility(View.VISIBLE);
                }
            } else {
                llCheckoutBar.setVisibility(View.GONE);
                if(textViewMinOrder.getVisibility() != View.VISIBLE) {
                    vCheckoutShadow.setVisibility(View.GONE);
                }
            }
        }
    }

    private long checkoutBarAnimDuration = 150L;
    private void llCheckoutBarSetVisibility(int visibility){
        if(visibility == View.VISIBLE && llCheckoutBar.getVisibility() != View.VISIBLE){
			llCheckoutBar.clearAnimation();
            vCheckoutShadow.clearAnimation();
            getHandler().removeCallbacks(runnableLlCheckoutBarGone);
			llCheckoutBar.setVisibility(View.VISIBLE);
            vCheckoutShadow.setVisibility(View.VISIBLE);

            Animation animation = new TranslateAnimation(0, 0, llCheckoutBar.getMeasuredHeight(), 0);
            animation.setDuration(checkoutBarAnimDuration);
            llCheckoutBar.startAnimation(animation);
            vCheckoutShadow.startAnimation(animation);

            if(getFreshHomeFragment() != null && getFreshHomeFragment().getRvFreshSuper() != null){
                getFreshHomeFragment().getRvFreshSuper().setPadding(0, 0, 0, llCheckoutBar.getMeasuredHeight()+((int)(ASSL.Yscale()*240.0f) - llCheckoutBar.getMeasuredHeight()));
            }

            if(textViewMinOrder.getVisibility() == View.VISIBLE) {
                textViewMinOrder.clearAnimation();
                textViewMinOrder.startAnimation(animation);
            }

        } else if(visibility == View.GONE && llCheckoutBar.getVisibility() != View.GONE){
            llCheckoutBar.clearAnimation();
            vCheckoutShadow.clearAnimation();

            Animation animation = new TranslateAnimation(0, 0, 0, llCheckoutBar.getMeasuredHeight());
            animation.setDuration(checkoutBarAnimDuration);
            getHandler().removeCallbacks(runnableLlCheckoutBarGone);
            getHandler().postDelayed(runnableLlCheckoutBarGone, checkoutBarAnimDuration);
            llCheckoutBar.startAnimation(animation);
            vCheckoutShadow.startAnimation(animation);

            if(getFreshHomeFragment() != null && getFreshHomeFragment().getRvFreshSuper() != null){
                getFreshHomeFragment().getRvFreshSuper().setPadding(0, 0, 0, (int)(ASSL.Yscale()*240.0f));
            }

            if(textViewMinOrder.getVisibility() == View.VISIBLE) {
                textViewMinOrder.clearAnimation();
                textViewMinOrder.startAnimation(animation);
            }
        }
    }
    private Runnable runnableLlCheckoutBarGone = new Runnable() {
        @Override
        public void run() {
            textViewMinOrder.clearAnimation();
            llCheckoutBar.setVisibility(View.GONE);
            if(textViewMinOrder.getVisibility() != View.VISIBLE) {
                vCheckoutShadow.setVisibility(View.GONE);
            }
        }
    };

    private void textViewMinOrderSetVisibility(int visibility){
        if(textViewMinOrder.getVisibility() != visibility) {
            textViewMinOrder.clearAnimation();
            vCheckoutShadow.clearAnimation();
            textViewMinOrder.setVisibility(visibility);
            if(visibility == View.VISIBLE){
                vCheckoutShadow.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(llCheckoutBar != null) {
            llCheckoutBar.setAlpha(1);
            llCheckoutBar.setVisibility(View.VISIBLE);
            llCheckoutBar.post(new Runnable() {
                @Override
                public void run() {
                    if(llCheckoutBar != null) {
                        llCheckoutBar.getMeasuredHeight();
                        llCheckoutBar.setVisibility(View.GONE);
                    }
                    if(vCheckoutShadow != null) {
                        vCheckoutShadow.setVisibility(View.GONE);
                    }
                }
            });
        }

        if(llPayViewContainer != null){
            llPayViewContainer.post(new Runnable() {
                @Override
                public void run() {
                    if(llPayViewContainer != null) {
                        ViewGroup.LayoutParams layoutParams = llPayViewContainer.getLayoutParams();
                        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_54);
                        llPayViewContainer.setLayoutParams(layoutParams);
                    }
                }
            });
        }

        if(tvFeedHyperLink != null){
            tvFeedHyperLink.post(new Runnable() {
                @Override
                public void run() {
                    if(tvFeedHyperLink != null) {
                        setFeedArrowToTextView(tvFeedHyperLink);
                    }
                }
            });
        }
    }



    @Bind(R.id.llAddToCart)
    public LinearLayout llAddToCart;
    @Bind(R.id.rlAddToCart)
	public RelativeLayout rlAddToCart;
	@Bind(R.id.tvItemTotalValue)
	public TextView tvItemTotalValue;

    @Bind(R.id.llPayViewContainer)
    public LinearLayout llPayViewContainer;
    @Bind(R.id.rlSliderContainer)
    public RelativeLayout rlSliderContainer;
    @Bind(R.id.viewAlpha)
    public View viewAlpha;
    @Bind(R.id.relativeLayoutSlider)
    public RelativeLayout relativeLayoutSlider;
    @Bind(R.id.tvSlide)
    public TextView tvSlide;
    @Bind(R.id.sliderText)
    public TextView sliderText;
    @Bind(R.id.buttonPlaceOrder)
    public Button buttonPlaceOrder;
    @Bind(R.id.tvFeedHyperLink)
    public TextView tvFeedHyperLink;
    @Bind(R.id.bRequestBooking) public Button bRequestBooking;

    public void setFeedArrowToTextView(TextView tvFeedHyperLink){
        Spannable spannable = new SpannableString(getString(R.string.back_arrow));
        spannable.setSpan(new CustomTypeFaceSpan("", Fonts.iconsFont(FreshActivity.this)), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.7f), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFeedHyperLink.setText(R.string.reviews);
        tvFeedHyperLink.append(" ");
        tvFeedHyperLink.append(spannable);
    }



    public void setRestaurantRatingStarsToLL(LinearLayout llCollapRatingStars, TextView tvCollapRestaurantRating,
                                             Double rating, int halfStarRes, int blankStarRes,
                                             TextView tvCollapRestaurantRatingCount, int ratingCount){
        if(rating == null){
            rating = 0d;
        }
        llCollapRatingStars.removeAllViews();
        llCollapRatingStars.addView(tvCollapRestaurantRating);
        tvCollapRestaurantRating.setText(String.valueOf(rating));
        addStarsToLayout(llCollapRatingStars, rating, halfStarRes, blankStarRes);
        if(tvCollapRestaurantRatingCount != null){
            llCollapRatingStars.addView(tvCollapRestaurantRatingCount);
            tvCollapRestaurantRatingCount.setText(ratingCount > 0 ? "("+ratingCount+")" : "");
        }
    }

    public void clearRestaurantRatingStars(LinearLayout llCollapRatingStars, TextView tvCollapRestaurantRating,
                                           TextView tvCollapRestaurantRatingCount){
        llCollapRatingStars.removeAllViews();
        tvCollapRestaurantRating.setText("");
        if(tvCollapRestaurantRatingCount != null){
            tvCollapRestaurantRatingCount.setText("");
        }
    }

    public void addStarsToLayout(LinearLayout llCollapRatingStars, Double rating, int halfStarRes, int blankStarRes){
        double ratingInt = rating.intValue();
        for(int i=0; i<5; i++){
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp_11), getResources().getDimensionPixelSize(R.dimen.dp_11));
            if(i==0){
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_5), 0, 0, 0);
            } else {
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_1), 0, 0, 0);
            }
            if(i < ratingInt){
                star.setImageResource(blankStarRes);
                star.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.green_delivery_stores), PorterDuff.Mode.SRC_IN));
            } else if(i == ratingInt){
                double decimal = Math.round((rating - Math.floor(rating))*10.0)/10.0;
                if(decimal < 0.3){
                    star.setImageResource(blankStarRes);
                } else if(decimal < 0.8){
                    star.setImageResource(halfStarRes);
                } else {
                    star.setImageResource(blankStarRes);
                    star.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.green_delivery_stores), PorterDuff.Mode.SRC_IN));
                }
            } else {
                star.setImageResource(blankStarRes);
            }
            llCollapRatingStars.addView(star, params);
        }
    }

    @Bind(R.id.llRightDrawer)
    public LinearLayout llRightDrawer;

    public MenusFilterFragment getMenusFilterFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MenusFilterFragment.class.getName());
        if(fragment != null){
            return (MenusFilterFragment) fragment;
        }
        return null;
    }

    public void applyRealTimeFilters(){
        if(getMenusFragment() != null) {
            getMenusFragment().applyFilter(filtersChanged);
        }
    }

    private MenusCartSelectedLayout menusCartSelectedLayout;
    public MenusCartSelectedLayout getMenusCartSelectedLayout(){
        return menusCartSelectedLayout;
    }

    public void hideMenusCartSelectedLayout(){
        menusCartSelectedLayout.setVisibility(View.GONE);
        adjustFabMarginsForDeliveryOffering();
    }


    private MenusCart menusCart;
    private MenusCart deliveryCustomerCart;
    public MenusCart getMenusCart(){
        MenusCart menusCart1 = getAppType()== AppConstant.ApplicationType.MENUS?menusCart:deliveryCustomerCart;
        if(menusCart1 == null){
            createAppCart(Config.getLastOpenedClientId(this));
        }
        return getAppType()== AppConstant.ApplicationType.MENUS?menusCart:deliveryCustomerCart;
    }

    public boolean openVendorMenuFragmentOnBack;
    public void refreshMealsAdapter(){
        if(getMealFragment()!=null &&  getMealFragment().getMealAdapter()!=null){
         getMealFragment().getMealAdapter().notifyDataSetChanged();
        }
    }

    private boolean prosTaskCreated;

    public boolean isProsTaskCreated() {
        return prosTaskCreated;
    }

    public void setProsTaskCreated(boolean prosTaskCreated) {
        this.prosTaskCreated = prosTaskCreated;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showingEarlyBirdDiscount",showingEarlyBirdDiscount);
    }

    private int scrollToCategoryId = -1;

    public int getScrollToCategoryId() {
        return scrollToCategoryId;
    }

    public void setScrollToCategoryId(int scrollToCategoryId) {
        this.scrollToCategoryId = scrollToCategoryId;
    }

    private List<Integer> searchedRestaurantIds;
    public List<Integer> getSearchedRestaurantIds(){
        return searchedRestaurantIds;
    }
    public void setSearchedRestaurantIds(List<Integer> searchedRestaurantIds){
        this.searchedRestaurantIds = searchedRestaurantIds;
    }


    private MenusResponse.Category category;
    public int getCategoryIdOpened() {
        return category==null?-1:category.getId();
    }
    public MenusResponse.Category getCategoryOpened(){
        return category;
    }

    public void setCategoryIdOpened(MenusResponse.Category category) {
        this.category = category;
    }

    public boolean isMenusOrDeliveryOpen(){
         return getAppType()== AppConstant.ApplicationType.MENUS || getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER;
    }

    public boolean collapsingToolBarEnabled(Fragment fragment){
        boolean isEnable = shouldOpenMerchantInfoFragment() ?
                fragment instanceof MerchantInfoFragment : fragment instanceof VendorMenuFragment;
        return isEnable;
    }

    public boolean shouldOpenMerchantInfoFragment(){
        return Config.getLastOpenedClientId(this).equals(Config.getDeliveryCustomerClientId())
                || (getMenusResponse() != null && getMenusResponse().isOpenMerchantInfo());
    }
   public boolean isDeliveryOpenInBackground(){
       return appTypeDeliveryInBackground;
   }

    public void showPaySliderEnabled(boolean isEnable){


        if(isEnable){
            if(viewAlpha.getTag()!=null && viewAlpha.getTag().equals("Disabled")){
                viewAlpha.setTag("Enabled");
                viewAlpha.setBackgroundColor(ContextCompat.getColor(FreshActivity.this,R.color.slider_green));
                viewAlpha.setAlpha(0);

            }
        }else{
          viewAlpha.setTag("Disabled");viewAlpha.setBackgroundColor(ContextCompat.getColor(FreshActivity.this,R.color.grey_969696));
           viewAlpha.setAlpha(1);
        }



    }
    public boolean isPaySliderEnabled(){
       return  !(viewAlpha.getTag()!=null && viewAlpha.getTag().equals("Disabled"));
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().width = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().width = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {

            if(intent.getExtras()!=null && intent.getExtras().containsKey(Constants.FUGU_CUSTOM_ACTION_PAYLOAD)){
                Log.i(TAG, "onNewIntent: Fugu Broadcast received" );
                String payload = intent.getStringExtra(Constants.FUGU_CUSTOM_ACTION_PAYLOAD);
                FuguCustomActionModel customActionModel = gson.fromJson(payload, FuguCustomActionModel.class);
                if(customActionModel.getDeepIndex()!=null && customActionModel.getDeepIndex()!=-1){
                    Data.deepLinkIndex = customActionModel.getDeepIndex();
                    if(customActionModel.getDeepIndex()==AppLinkIndex.RIDE_HISTORY.getOrdinal()){
                        Data.deepLinkOrderId = customActionModel.getOrderId();
                        Data.deepLinkProductType = ProductType.FEED.getOrdinal();
                    }

                    MenuAdapter.closeDrawerIfOpen(this);
                    DeepLinkAction.openDeepLink(this,getSelectedLatLng());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOfferingsVisibility(OfferingsVisibilityResponse.OfferingsVisibilityData offeringsVisibility){

        if(offeringsVisibility!=null && fabViewTest!=null){
            boolean isStateChanged = fabViewTest.triggerStateChangeFunction(offeringsVisibility);
            if(isStateChanged && menuBar!=null && menuBar.getMenuAdapter()!=null){
                menuBar.getMenuAdapter().notifyDataSetChanged();
            }
        }
    }

    public String currentOpenClientIdForFab(){
        if(isDeliveryOpenInBackground())return Config.DELIVERY_CUSTOMER_CLIENT_ID;
        return  Config.getLastOpenedClientId(this);

    }
}
