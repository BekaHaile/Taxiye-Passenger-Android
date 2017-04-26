package com.sabkuchfresh.home;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
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
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
import com.sabkuchfresh.dialogs.FreshSortDialog;
import com.sabkuchfresh.feed.ui.fragments.FeedAddPostFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedChangeCityFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedClaimHandleFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedHomeFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedNotificationsFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedPostDetailFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedReserveSpotFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedSpotReservedSharingFragment;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
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
import com.sabkuchfresh.fragments.MenusCheckoutMergedFragment;
import com.sabkuchfresh.fragments.MenusFilterCuisinesFragment;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.fragments.MenusFragment;
import com.sabkuchfresh.fragments.MenusItemCustomizeFragment;
import com.sabkuchfresh.fragments.MenusSearchFragment;
import com.sabkuchfresh.fragments.NewFeedbackFragment;
import com.sabkuchfresh.fragments.RestaurantAddReviewFragment;
import com.sabkuchfresh.fragments.RestaurantImageFragment;
import com.sabkuchfresh.fragments.RestaurantReviewsListFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;
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
import com.tsengvn.typekit.TypekitContextWrapper;

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
import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.OrderStatusFragment;
import product.clicklabs.jugnoo.PaperDBKeys;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.DeepLinkAction;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.dialogs.PaytmRechargeDialog;
import product.clicklabs.jugnoo.home.dialogs.PushDialog;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.tutorials.NewUserFlow;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
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

    private TextView textViewMinOrder;

    private MenuBar menuBar;
    private TopBar topBar;
    private FABViewTest fabViewTest;
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
    private TextView tvAddPost;
    private ImageView ivProfilePic;

    public View getFeedHomeAddPostView() {
        return feedHomeAddPostView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_fresh);
			ButterKnife.bind(this);
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

            if (getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE)) {
                Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);
                setSelectedLatLng(new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
                        getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude)));
            }

            textViewMinOrder = (TextView) findViewById(R.id.textViewMinOrder);

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



           /* appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset == 0) {
                        if (fabViewTest.getMenuLabelsRightTest().getVisibility() == View.GONE) {
                            fabViewTest.getMenuLabelsRightTest().setVisibility(View.VISIBLE);
                            fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_in_slow_fab));
                        }
                    } else {
                        if (verticalOffset < -100) {
                            if (fabViewTest.getMenuLabelsRightTest().getVisibility() == View.VISIBLE) {
                                fabViewTest.getMenuLabelsRightTest().setVisibility(View.GONE);
                                fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_out_slow_fab));
                            }
                        }
                    }
                }
            });*/


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
                            openCart(appType);
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


            try {
                float marginBottom = 60f;
                String lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);

                createAppCart(lastClientId);

                if (lastClientId.equalsIgnoreCase(Config.getMealsClientId())) {
                    addMealFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);
                } else if (lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                    openCart();
                    addGroceryFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.GROCERY);
                    lastClientId = Config.getGroceryClientId();
                } else if (lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                    fetchFiltersFromSP();
                    openCart();
                    addMenusFragment();

                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);
                    lastClientId = Config.getMenusClientId();

                } else if (lastClientId.equalsIgnoreCase(Config.getFeedClientId())) {
                    if(Data.getFeedData().getFeedActive()) {
                        if(Data.getFeedData().getHasHandle()){



                            addFeedFragment();

                        }else{

                            addClaimHandleFragment();
                        }

                    } else {
                        addFeedReserveSpotFragment();
                    }
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FEED);
                    lastClientId = Config.getFeedClientId();

                } else {
                    openCart();
                    addFreshHomeFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
                    lastClientId = Config.getFreshClientId();
                }


                int dpAsPixels = (int) (marginBottom * scale + 0.5f);
                fabViewTest.setMenuLabelsRightTestPadding(marginBottom);
                fabViewTest.setRlGenieHelpBottomMargin(200f);
                lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);
                Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
            } catch (Exception e) {
                e.printStackTrace();
                addFreshHomeFragment();
            }


            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, getIntentFiler());

            openPushDialog();
            deepLinkAction.openDeepLink(menuBar);
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
        tvAddPost = (TextView) findViewById(R.id.tvAddPost);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
    }

    public TextView getTvAddPost(){
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
        } else if (fragment instanceof VendorMenuFragment) {
            slots.add(new SortResponseModel(0, "A-Z", false));
            slots.add(new SortResponseModel(1, "Price: Low to High", false));
            slots.add(new SortResponseModel(2, "Price: High to Low", false));

            slots.get(1).check = true;
            menusSort = slots.get(1).id;
            onSortEvent(new SortSelection(menusSort));
        }
    }

    public void updateSortSelectedFromAPI(Fragment fragment, JSONObject jObj) {
        if (fragment instanceof FreshFragment) {
            if (freshSort == -1) {
                int sortedBy = jObj.optInt(Constants.SORTED_BY);
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
                            } else if (getAppType() == AppConstant.ApplicationType.MENUS && getMenuProductsResponse() != null && getMenuProductsResponse().getCategories() != null) {
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

                                    if (type == 0) {
                                        Log.d("receiver", "Got message: " + message);
                                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                            drawerLayout.closeDrawer(GravityCompat.START);
                                        }
                                        String lastClientId = Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
                                        if (lastClientId.equalsIgnoreCase(Config.getFreshClientId())) {
                                            updateItemListFromSPDB();
                                            llCheckoutBar.performClick();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                            MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getFreshClientId(), null,
                                                    getCurrentPlaceLatLng(), bundle);
                                        }
                                    } else if (type == 1) {
                                        intentToShareActivity();
                                    } else if (type == 2) {
                                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                            drawerLayout.closeDrawer(GravityCompat.START);
                                        }
                                        String lastClientId = Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getGroceryClientId());
                                        if (lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                                            updateItemListFromSPDB();
                                            llCheckoutBar.performClick();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                            MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getGroceryClientId(), null,
                                                    getCurrentPlaceLatLng(), bundle);
                                        }
                                    } else if (type == 3) {
                                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                            drawerLayout.closeDrawer(GravityCompat.START);
                                        }
                                        String lastClientId = Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMenusClientId());
                                        if (lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                                            updateItemListFromSPDB();
                                            llCheckoutBar.performClick();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                            MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getMenusClientId(), null,
                                                    getCurrentPlaceLatLng(), bundle);
                                        }
                                    } else if (type == 10) {
                                        setRefreshCart(true);
                                    }
                                } else {
                                    if (flag == PushFlags.DISPLAY_MESSAGE.getOrdinal()) {
                                        // for refreshing generate feed api on feed like comment related pushes
                                        if(intent.getIntExtra(Constants.KEY_DEEPINDEX, -1) == AppLinkIndex.FEED_PAGE.getOrdinal()
                                                && intent.getIntExtra(Constants.KEY_POST_ID, -1) != -1) {
                                            if(getTopFragment() instanceof FeedHomeFragment) {
//                                            getFeedHomeFragment().fetchFeedsApi(false, false);
                                            }
                                        } else {
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
                                        if (fragment instanceof MealFragment && FreshActivity.this.hasWindowFocus()) {
                                            ((MealFragment) fragment).getAllProducts(true, getSelectedLatLng());
                                        } else {
                                            Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                            intent1.putExtra(Constants.KEY_FLAG, flag);
                                            intent1.putExtra(Constants.KEY_ORDER_ID, intent.getIntExtra(Constants.KEY_ORDER_ID, -1));
                                            intent1.putExtra(Constants.KEY_CLOSE_TRACKING, intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0));
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);
                                        }
                                    } else if (PushFlags.MENUS_STATUS.getOrdinal() == flag || PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
                                        Fragment fragment = getMenusFragment();
                                        if (fragment != null && FreshActivity.this.hasWindowFocus()) {
                                            ((MenusFragment) fragment).getAllMenus(true, getSelectedLatLng());
                                        } else {
                                            Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                            intent1.putExtra(Constants.KEY_FLAG, flag);
                                            intent1.putExtra(Constants.KEY_ORDER_ID, intent.getIntExtra(Constants.KEY_ORDER_ID, -1));
                                            intent1.putExtra(Constants.KEY_CLOSE_TRACKING, intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0));
                                            LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);
                                        }
                                    } else if (Constants.OPEN_DEEP_INDEX == flag) {
                                        deepLinkAction.openDeepLink(menuBar);
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

    public Bus getBus() {
        return mBus;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            isTimeAutomatic();

            if (Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getAutosClientId())) {
                HomeActivity.homeSwitcher = true;
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getAutosClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if (Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getMealsClientId())) {
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getMealsClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if (Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getGroceryClientId())) {
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getGroceryClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if (Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getFreshClientId())) {
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getFreshClientId(), null,
                        getCurrentPlaceLatLng(), null);
            }
            Prefs.with(this).save("home_switcher_client_id", "");


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
                            || (getFeedReserveSpotFragment() != null && !getFeedReserveSpotFragment().isHidden())
                            || (getFeedSpotReservedSharingFragment() != null && !getFeedSpotReservedSharingFragment().isHidden())
							|| (getFeedClaimHandleFragment() != null && !getFeedClaimHandleFragment().isHidden())) {
                        fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                        fabViewTest.setFABButtons();
                    }
                } else {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
                }


                // to check from Last selected address that if its id is -10(marked for special case of
                // deleting selected delivery address from address book), if it is the case clear
                // address related local variables and save empty jsonObject in SP for setting
                // delivery location to current location.
                SearchResult searchResultLastFMM = gson.fromJson(Prefs.with(this)
                        .getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
                if(searchResultLastFMM.getId() == null || searchResultLastFMM.getId() == -10){
                    setSelectedLatLng(new LatLng(Data.latitude, Data.longitude));
                    setSelectedAddress("");
                    setSelectedAddressId(0);
                    setSelectedAddressType("");

                    Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);
                    setIsAddressConfirmed(false);
                }
                // else if selected address is updated by user, updating address related local variables
                // from SP search result
                else if(searchResultLastFMM.getId() > 0 && searchResultLastFMM.getId() == getSelectedAddressId()){
                    setSelectedLatLng(searchResultLastFMM.getLatLng());
                    setSelectedAddress(searchResultLastFMM.getAddress());
                    setSelectedAddressType(searchResultLastFMM.getName());
                }
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

    private FreshCheckoutMergedFragment getFreshCheckoutMergedFragment() {
        return (FreshCheckoutMergedFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutMergedFragment.class.getName());
    }

    private MenusCheckoutMergedFragment getMenusCheckoutMergedFragment() {
        return (MenusCheckoutMergedFragment) getSupportFragmentManager().findFragmentByTag(MenusCheckoutMergedFragment.class.getName());
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
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
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
                                        totalQuantity = totalQuantity + itemSelected.getQuantity();
                                        totalPrice = totalPrice + itemSelected.getTotalPriceWithQuantity();
                                    }
                                }
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            for (ItemSelected itemSelected : item.getItemSelectedList()) {
                                if (itemSelected.getQuantity() > 0) {
                                    totalQuantity = totalQuantity + itemSelected.getQuantity();
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
            int llSearchCartContainerVis = View.VISIBLE;
            int llSearchCartVis = View.VISIBLE;
            int llCartContainerVis = View.GONE;
            int ivSearchVis = View.GONE;
            int llSearchContainerVis = View.GONE;
            topBar.imageViewDelete.setVisibility(View.GONE);
            topBar.textViewReset.setVisibility(View.GONE);
            if (topView.getVisibility() != View.VISIBLE)
                topView.setVisibility(View.VISIBLE);
            fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            topBar.editTextDeliveryAddress.setVisibility(View.GONE);

            int rlFilterVis = View.GONE;
            topBar.buttonCheckServer.setVisibility(View.GONE);
            topBar.ivAddReview.setVisibility(View.GONE);
            topBar.tvNameCap.setVisibility(View.GONE);
            topBar.imageViewBack.setImageResource(R.drawable.ic_back_selector);
            topBar.tvDeliveryAddress.setVisibility(View.GONE);
            int padding = (int) (20f * ASSL.minRatio());
            int visMinOrder = -1;
            topBar.progressWheelDeliveryAddressPin.setVisibility(View.GONE);
            int freshSortVis = View.GONE;

            if (fragment instanceof FreshHomeFragment) {
                topBar.buttonCheckServer.setVisibility(View.VISIBLE);
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }


                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.fatafat));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
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
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) {
                    llSearchCartVis = View.GONE;
                }

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.meals));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            } else if (fragment instanceof GroceryFragment) {
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.grocery));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof MenusFragment) {
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }
                rlFilterVis = View.VISIBLE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.menus));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                topBar.getLlSearchCart().setLayoutTransition(null);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                }
                visMinOrder = setMinOrderAmountText(fragment);

            } else if (fragment instanceof VendorMenuFragment || fragment instanceof RestaurantImageFragment) {
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(vendorOpened.getName());
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                visMinOrder = setMinOrderAmountText(fragment);
                if(fragment instanceof VendorMenuFragment) {
                    freshSortVis = View.VISIBLE;
                }

            } else if (fragment instanceof MenusItemCustomizeFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getString(R.string.customize_item));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof MenusFilterFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.filters);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                topBar.textViewReset.setVisibility(View.VISIBLE);
            } else if (fragment instanceof MenusFilterCuisinesFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.select_cuisines);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof FreshCheckoutMergedFragment || fragment instanceof MenusCheckoutMergedFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartVis = View.GONE;

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
                topBar.ivAddReview.setVisibility(View.VISIBLE);
            } else if (fragment instanceof RestaurantAddReviewFragment) {
            	topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setImageResource(R.drawable.ic_cross_grey_selector);
                padding = (int) (25f * ASSL.minRatio());

                topBar.title.setVisibility(View.GONE);
                topBar.tvNameCap.setVisibility(View.VISIBLE);
                try {
                    topBar.tvNameCap.setText(Data.userData.userName.substring(0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            } else if (fragment instanceof FeedHomeFragment || fragment instanceof FeedReserveSpotFragment || fragment instanceof FeedSpotReservedSharingFragment ||
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
            }
            topBar.imageViewBack.setPadding(padding, padding, padding, padding);

            if(visMinOrder != 1) {
                textViewMinOrderSetVisibility(View.GONE);
            }

            topBar.getLlSearchCartContainer().setVisibility(llSearchCartContainerVis);
            topBar.getLlSearchCart().setVisibility(llSearchCartVis);
            if(totalPrice <= 0 || totalQuantity <= 0){
                llCartContainerVis = View.GONE;
            }
            llCheckoutBarSetVisibility(llCartContainerVis);
            topBar.getIvSearch().setVisibility(ivSearchVis);
            topBar.getLlSearchContainer().setVisibility(llSearchContainerVis);
            topBar.rlFilter.setVisibility(rlFilterVis);

            RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) topBar.title.getLayoutParams();
            if (topBar.getLlSearchCart().getVisibility() == View.VISIBLE) {
                topBar.title.setGravity(Gravity.LEFT);
                titleLayoutParams.setMargins((int) (ASSL.Xscale() * 20f), 0, 0, 0);
                titleLayoutParams.addRule(RelativeLayout.LEFT_OF, topBar.getLlSearchCart().getId());
            }
            else {
                topBar.title.setGravity(Gravity.CENTER);
                titleLayoutParams.setMargins((int) (ASSL.Xscale() * -32f), 0, 0, 0);
            }
            if(topBar.ivAddReview.getVisibility() == View.VISIBLE){
                titleLayoutParams.addRule(RelativeLayout.LEFT_OF, topBar.ivAddReview.getId());
            } else if(fragment instanceof FeedReserveSpotFragment
                    || fragment instanceof FeedSpotReservedSharingFragment
                    || fragment instanceof FeedNotificationsFragment || fragment instanceof FeedChangeCityFragment){
                topBar.title.setGravity(Gravity.CENTER);
                titleLayoutParams.setMargins((int) (ASSL.Xscale() * -32f), 0, 0, 0);
            }
            if(fragment instanceof FeedbackFragment){
                topBar.title.setGravity(Gravity.CENTER);
                titleLayoutParams.addRule(RelativeLayout.LEFT_OF, 0);
                titleLayoutParams.setMargins((int) (ASSL.Xscale() * -32f), 0, 0, 0);
            }


            if(fragment instanceof FeedbackFragment){
                topBar.title.setGravity(Gravity.CENTER);
                titleLayoutParams.addRule(RelativeLayout.LEFT_OF, 0);
                titleLayoutParams.setMargins((int) (ASSL.Xscale() * -32f), 0, 0, 0);
            }
            topBar.ivFreshSort.setVisibility(freshSortVis);

            feedHomeAddPostView.setVisibility(View.GONE);
            topBar.title.setLayoutParams(titleLayoutParams);
            setCollapsingToolbar(fragment instanceof VendorMenuFragment, fragment);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCollapsingToolbar(boolean isEnable, Fragment fragment) {


        AppBarLayout.LayoutParams collapsingToolBarParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        CoordinatorLayout.LayoutParams relativeparams = (CoordinatorLayout.LayoutParams) relativeLayoutContainer.getLayoutParams();
        if (fragment instanceof RestaurantImageFragment)
            relativeparams.setBehavior(null);
        else
            relativeparams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        relativeLayoutContainer.requestLayout();


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



       /* CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout.LayoutParams appbar = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        collapsingToolbarLayout.setLayoutParams(appbar);


        CollapsingToolbarLayout.LayoutParams collapseParams = (CollapsingToolbarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        collapseParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        toolbar.setLayoutParams(collapseParams);
*/
    }


    AppBarLayout.OnOffsetChangedListener feedHomeAppBarListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


            if(getTopFragment() instanceof FeedHomeFragment) {
                feedHomeAddPostView.animate().translationY(feedHomeAddPostView.getHeight() - ((appBarLayout.getTotalScrollRange() + verticalOffset) * 1.0f / appBarLayout.getTotalScrollRange()) * feedHomeAddPostView.getHeight()).start();
                if(verticalOffset== -appBarLayout.getTotalScrollRange()) {
                    if (fabViewTest.relativeLayoutFABTest.getVisibility() == View.VISIBLE) {
                        fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
                    }
                }
                else{
                    if(fabViewTest.relativeLayoutFABTest.getVisibility() == View.GONE){
                        fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                    }
                }
            }
           /* if(verticalOffset== -appBarLayout.getTotalScrollRange())
            {



                if(findViewById(R.id.add_post).getVisibility()==View.VISIBLE)
                {
                    findViewById(R.id.add_post).setVisibility(View.GONE);
                }
            }
            else if(findViewById(R.id.add_post).getVisibility()==View.GONE){
                findViewById(R.id.add_post).setVisibility(View.VISIBLE);
            }*/
        }
    };

    public void openRestaurantFragment() {
        if (canExitVendorMenu())
            transactionUtils.openRestaurantImageFragment(FreshActivity.this, relativeLayoutContainer);
    }

    /**
     * To set text and visibility of textViewMinOrderAmount
     * @param fragment fragment to call this
     * @return returns 1 if visibility changed by this function else 0
     */
    public int setMinOrderAmountText(Fragment fragment) {
        try {
            if (getFreshCheckoutMergedFragment() == null) {
                if (getFreshFragment() != null || (getFreshSearchFragment() != null && getVendorMenuFragment() == null)) {
                    int textViewMinOrderVis;
                    if (fragment instanceof FreshFragment || fragment instanceof FreshSearchFragment) {
                        if (Data.userData.isSubscriptionActive() && getProductsResponse() != null && !TextUtils.isEmpty(getProductsResponse().getSubscriptionMessage())) {
                            textViewMinOrderVis = View.VISIBLE;
                            textViewMinOrder.setText(getProductsResponse().getSubscriptionMessage());
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
                } else if (fragment instanceof VendorMenuFragment || fragment instanceof MenusSearchFragment) {
                    int textViewMinOrderVis = View.GONE;
                    if (getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                        if (totalPrice < getVendorOpened().getMinimumOrderAmount()) {
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
                }
            } else {
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
            if (getAppType() == AppConstant.ApplicationType.MENUS) {
                if (getTopFragment() instanceof MenusFragment) {
                    getMenusFragment().openSearch(false);
                    topBar.title.setVisibility(View.GONE);
                    topBar.title.invalidate();
                    topBar.animateSearchBar(true);
                      GAUtils.event(GACategory.MENUS, GAAction.HOME ,GAAction.SEARCH_BUTTON + GAAction.CLICKED);
                }
                else if (getTopFragment() instanceof VendorMenuFragment || getTopFragment() instanceof RestaurantImageFragment) {
                        if(getTopFragment() instanceof VendorMenuFragment){
                            GAUtils.event(GACategory.MENUS, GAAction.RESTAURANT_HOME ,GAAction.SEARCH_BUTTON + GAAction.CLICKED);
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
//        FlurryEventLogger.eventGA(Events.MENUS, Events.FILTERS, Events.MENU_FILTERS);
        GAUtils.event(GAAction.MENUS, GAAction.HOME ,GAAction.FILTER_BUTTON + GAAction.CLICKED);
        getTransactionUtils().openMenusFilterFragment(this, getRelativeLayoutContainer());
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
                        } else if (getMenusCheckoutMergedFragment() != null) {
                            getMenusCheckoutMergedFragment().deleteCart();
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
                        } else if (type == AppConstant.ApplicationType.MENUS) {
                            clearMenusCart();
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

    public void clearAllCartAtOrderComplete() {
        int type = getAppType();
        if (type == AppConstant.ApplicationType.FRESH) {
            clearCart();
        } else if (type == AppConstant.ApplicationType.GROCERY) {
            clearGroceryCart();
        } else if (type == AppConstant.ApplicationType.MENUS) {
            clearMenusCart();
        } else {
            clearMealCart();
        }
        if (type == AppConstant.ApplicationType.MENUS) {
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
        if (getFeedHomeFragment() != null && getAppType() == AppConstant.ApplicationType.MENUS
                && ((backPressed && getTopFragment() instanceof VendorMenuFragment)
                || (!backPressed && getTopFragment() instanceof FreshCheckoutMergedFragment))) {
            Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FEED);
            Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFeedClientId());
        }
    }

    public void orderComplete() {
        clearAllCartAtOrderComplete();
        splInstr = "";
        slotSelected = null;
        slotToSelect = null;
        paymentOption = null;
        setPlaceOrderResponse(null);

        setSelectedAddress("");
        setSelectedLatLng(null);
        setSelectedAddressId(0);
        Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT);
        setSelectedAddressType("");


        checkForBackToFeed(false);

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - 1; i++) {
            fm.popBackStack();
        }

        updateCartValuesGetTotalPrice();

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));
            }
        }, 1000);
        clearEtFocus();
    }

    private void addFreshHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FreshHomeFragment(),
                        FreshHomeFragment.class.getName())
                .addToBackStack(FreshHomeFragment.class.getName())
                .commitAllowingStateLoss();
    }


    private void addMealFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new MealFragment(),
                        MealFragment.class.getName())
                .addToBackStack(MealFragment.class.getName())
                .commitAllowingStateLoss();
    }

    private void addGroceryFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new GroceryFragment(),
                        GroceryFragment.class.getName())
                .addToBackStack(GroceryFragment.class.getName())
                .commitAllowingStateLoss();
    }

    private void addMenusFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new MenusFragment(),
                        MenusFragment.class.getName())
                .addToBackStack(MenusFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void addFeedFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FeedHomeFragment(),
                        FeedHomeFragment.class.getName())
                .addToBackStack(FeedHomeFragment.class.getName())
                .commitAllowingStateLoss();
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
                GAUtils.event(GACategory.MENUS, GAAction.CUSTOMIZE_ITEM, GAAction.BACK_BUTTON + GAAction.CLICKED);
            } else if (getTopFragment() instanceof MenusSearchFragment && getAppType() == AppConstant.ApplicationType.MENUS) {
                GAUtils.event(GACategory.MENUS, GAAction.RESTAURANT_SEARCH, GAAction.BACK_BUTTON + GAAction.CLICKED);
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
            getMenusFragment().openSearch(false);


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


            super.onBackPressed();

        }
    }

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
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }
        if(fabViewTest.menuLabelsRightTest.isOpened()){
            fabViewTest.menuLabelsRightTest.close(true);
            return;
        }
        performBackPressed(true);
    }

    @Override
    protected void onDestroy() {
        try {
            mBus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        System.gc();
		ButterKnife.unbind(this);
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

    public LatLng getCurrentPlaceLatLng() {
        return getSelectedLatLng();
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

    private boolean swipeFlag;

    public boolean isSwipeAvailable() {
        return swipeFlag;
    }

    public void setSwipeAvailable(boolean swipeFlag) {
        this.swipeFlag = swipeFlag;
    }


    @Override
    protected void onPause() {
        super.onPause();

        try {
            Utils.hideKeyboard(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cartChangedAtCheckout && getFreshCheckoutMergedFragment() != null) {
            updateItemListFromDBFMG(null);
        }
        saveItemListToSPDB();
        saveAppCart(getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));

        if (getAppType() == AppConstant.ApplicationType.MENUS) {
            saveFilters();
        }


        MyApplication.getInstance().getLocationFetcher().destroy();

    }

    public void saveItemListToSPDB() {
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
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
                            }
                        }
                    }
                }
            }
            if (getVendorOpened() != null) {
                JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
                if (getVendorOpened().getRestaurantId().equals(jsonSavedCart.optInt(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId()))) {
                    if (jCart.length() > 0) {
                        jCart.put(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId());
                        jCart.put(Constants.KEY_RESTAURANT_NAME, getVendorOpened().getName());
                    }
                    Prefs.with(this).save(Constants.SP_MENUS_CART, jCart.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateItemListFromSPDB() {
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
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
            Gson gson = new Gson();
            JSONObject jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
            if (getMenuProductsResponse() != null
                    && getMenuProductsResponse().getCategories() != null) {
                for (com.sabkuchfresh.retrofit.model.menus.Category category : getMenuProductsResponse().getCategories()) {
                    if (category.getSubcategories() != null) {
                        for (Subcategory subcategory : category.getSubcategories()) {
                            for (Item item : subcategory.getItems()) {
                                item.getItemSelectedList().clear();
                                JSONArray jsonArrayItem = jCart.optJSONArray(String.valueOf(item.getRestaurantItemId()));
                                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                    for (int i = 0; i < jsonArrayItem.length(); i++) {
                                        try {
                                            ItemSelected itemSelected = gson.fromJson(jsonArrayItem.getString(i), ItemSelected.class);
                                            if (itemSelected.getQuantity() > 0) {
                                                item.getItemSelectedList().add(itemSelected);
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        }
                    } else if (category.getItems() != null) {
                        for (Item item : category.getItems()) {
                            item.getItemSelectedList().clear();
                            JSONArray jsonArrayItem = jCart.optJSONArray(String.valueOf(item.getRestaurantItemId()));
                            if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                for (int i = 0; i < jsonArrayItem.length(); i++) {
                                    try {
                                        ItemSelected itemSelected = gson.fromJson(jsonArrayItem.getString(i), ItemSelected.class);
                                        if (itemSelected.getQuantity() > 0) {
                                            item.getItemSelectedList().add(itemSelected);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
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
    }

    private void clearMealCart() {
        Paper.book().delete(DB_MEALS_CART);
        Paper.book().delete(DB_PREVIOUS_VENDOR);
        createAppCart(Config.getMealsClientId());
    }

    private void clearGroceryCart() {
    }



    public void clearMenusCart() {
        Prefs.with(this).save(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT);
    }

    @Subscribe
    public void onSortEvent(SortSelection event) {
        try {
            Comparator comparator = null;
            if (getAppType() == AppConstant.ApplicationType.MENUS) {
                switch (event.postion) {
                    case 0:
                        comparator = new ItemCompareAtoZ();
                        GAUtils.event(GACategory.MENUS, GAAction.SORT_POPUP ,GAAction.A_Z);

                        break;
                    case 1:
                        comparator = new ItemComparePriceLowToHigh();
                        GAUtils.event(GACategory.MENUS, GAAction.SORT_POPUP ,GAAction.PRICE_LOW_TO_HIGH);
                        break;
                    case 2:
                        comparator = new ItemComparePriceHighToLow();
                        GAUtils.event(GACategory.MENUS, GAAction.SORT_POPUP ,GAAction.PRICE_HIGH_TO_LOW);

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
                    deepLinkAction.openDeepLink(menuBar);
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
                            setSelectedAddressId(searchResult.getId());
                            saveOfferingLastAddress(appType);
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
                            } else if (getMenusCheckoutMergedFragment() != null) {
                                getMenusCheckoutMergedFragment().updateAddressView();
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
            } else if (getMenusCheckoutMergedFragment() != null) {
                getMenusCheckoutMergedFragment().deleteCart();
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

    public void openCart(int appType) {
        if (appType == AppConstant.ApplicationType.MENUS && getVendorOpened() != null) {
            if (canExitVendorMenu()){
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
        }
        if (appType == AppConstant.ApplicationType.FRESH) {
            Prefs.with(this).save(Constants.SP_FRESH_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.MEALS) {
            Prefs.with(this).save(Constants.SP_MEALS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.GROCERY) {
            Prefs.with(this).save(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        } else if (appType == AppConstant.ApplicationType.MENUS) {
            Prefs.with(this).save(Constants.SP_MENUS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
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
        } else if (appType == AppConstant.ApplicationType.MENUS) {
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_MENUS_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
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
    }


    private MenusResponse menusResponse;

    public MenusResponse getMenusResponse() {
        return menusResponse;
    }

    public void setMenusResponse(MenusResponse menusResponse) {
        this.menusResponse = menusResponse;
    }

    public void saveFilters() {
        Prefs.with(this).save(Constants.SP_MENUS_FILTER_SORT_BY, sortBySelected.getOrdinal());
        Prefs.with(this).save(Constants.SP_MENUS_FILTER_MIN_ORDER, moSelected.getOrdinal());
        Prefs.with(this).save(Constants.SP_MENUS_FILTER_DELIVERY_TIME, dtSelected.getOrdinal());
        StringBuilder sbCuisines = new StringBuilder();
        if (cuisinesSelected.size() > 0) {
            for (String cuisine : cuisinesSelected) {
                sbCuisines.append(cuisine).append(",");
            }
        }
        Prefs.with(this).save(Constants.SP_MENUS_FILTER_CUISINES, sbCuisines.toString());

        StringBuilder sbQF = new StringBuilder();
        if (quickFilterSelected.size() > 0) {
            for (String qf : quickFilterSelected) {
                sbQF.append(qf).append(",");
            }
        }
        Prefs.with(this).save(Constants.SP_MENUS_FILTER_QUICK, sbQF.toString());
    }


    public void fetchFiltersFromSP() {
        int sortBy = Prefs.with(this).getInt(Constants.SP_MENUS_FILTER_SORT_BY, sortBySelected.getOrdinal());
        if (sortBy == MenusFilterFragment.SortType.POPULARITY.getOrdinal()) {
            sortBySelected = MenusFilterFragment.SortType.POPULARITY;
        } else if (sortBy == MenusFilterFragment.SortType.DISTANCE.getOrdinal()) {
            sortBySelected = MenusFilterFragment.SortType.DISTANCE;
        } else if (sortBy == MenusFilterFragment.SortType.PRICE.getOrdinal()) {
            sortBySelected = MenusFilterFragment.SortType.PRICE;
        } else if (sortBy == MenusFilterFragment.SortType.ONLINEPAYMENTACCEPTED.getOrdinal()) {
            sortBySelected = MenusFilterFragment.SortType.ONLINEPAYMENTACCEPTED;
        }

        int mo = Prefs.with(this).getInt(Constants.SP_MENUS_FILTER_MIN_ORDER, moSelected.getOrdinal());
        if (mo == MenusFilterFragment.MinOrder.MO150.getOrdinal()) {
            moSelected = MenusFilterFragment.MinOrder.MO150;
        } else if (mo == MenusFilterFragment.MinOrder.MO250.getOrdinal()) {
            moSelected = MenusFilterFragment.MinOrder.MO250;
        } else if (mo == MenusFilterFragment.MinOrder.MO500.getOrdinal()) {
            moSelected = MenusFilterFragment.MinOrder.MO500;
        }

        int dt = Prefs.with(this).getInt(Constants.SP_MENUS_FILTER_DELIVERY_TIME, dtSelected.getOrdinal());
        if (dt == MenusFilterFragment.DeliveryTime.DT30.getOrdinal()) {
            dtSelected = MenusFilterFragment.DeliveryTime.DT30;
        } else if (dt == MenusFilterFragment.DeliveryTime.DT45.getOrdinal()) {
            dtSelected = MenusFilterFragment.DeliveryTime.DT45;
        } else if (dt == MenusFilterFragment.DeliveryTime.DT60.getOrdinal()) {
            dtSelected = MenusFilterFragment.DeliveryTime.DT60;
        }

        String cuisines = Prefs.with(this).getString(Constants.SP_MENUS_FILTER_CUISINES, "");
        if (!TextUtils.isEmpty(cuisines)) {
            String arr[] = cuisines.split(",");
            cuisinesSelected.clear();
            for (String cuisine : arr) {
                cuisinesSelected.add(cuisine);
            }
        }

        String qfs = Prefs.with(this).getString(Constants.SP_MENUS_FILTER_QUICK, "");
        if (!TextUtils.isEmpty(qfs)) {
            String arr[] = qfs.split(",");
            quickFilterSelected.clear();
            for (String qf : arr) {
                quickFilterSelected.add(qf);
            }
        }

    }


    private MenusFilterFragment.SortType sortBySelected = MenusFilterFragment.SortType.NONE;
    private MenusFilterFragment.MinOrder moSelected = MenusFilterFragment.MinOrder.NONE;
    private MenusFilterFragment.DeliveryTime dtSelected = MenusFilterFragment.DeliveryTime.NONE;
    private ArrayList<String> cuisinesSelected = new ArrayList<>();
    private ArrayList<String> quickFilterSelected = new ArrayList<>();

    public MenusFilterFragment.SortType getSortBySelected() {
        return sortBySelected;
    }

    public void setSortBySelected(MenusFilterFragment.SortType sortBySelected) {
        this.sortBySelected = sortBySelected;
    }

    public MenusFilterFragment.MinOrder getMoSelected() {
        return moSelected;
    }

    public void setMoSelected(MenusFilterFragment.MinOrder moSelected) {
        this.moSelected = moSelected;
    }

    public MenusFilterFragment.DeliveryTime getDtSelected() {
        return dtSelected;
    }

    public void setDtSelected(MenusFilterFragment.DeliveryTime dtSelected) {
        this.dtSelected = dtSelected;
    }

    public ArrayList<String> getCuisinesSelected() {
        return cuisinesSelected;
    }

    public void setCuisinesSelected(ArrayList<String> cuisinesSelected) {
        this.cuisinesSelected = cuisinesSelected;
    }

    public ArrayList<String> getQuickFilterSelected() {
        return quickFilterSelected;
    }

    public void setQuickFilterSelected(ArrayList<String> quickFilterSelected) {
        this.quickFilterSelected = quickFilterSelected;
    }

    private ArrayList<FilterCuisine> filterCuisinesLocal = new ArrayList<>();

    public ArrayList<FilterCuisine> getFilterCuisinesLocal() {
        return filterCuisinesLocal;
    }

    public void setFilterCuisinesLocal(ArrayList<FilterCuisine> filterCuisinesLocal) {
        this.filterCuisinesLocal = filterCuisinesLocal;
    }

    public boolean checkForAdd() {
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
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
//                                    TODO clear cart for previous selected activity.getCart().getDeliveryStoreCart(activity.getOpenedVendorId()).getSubItemHashMap().clear();
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
            JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
            if (getVendorOpened() != null
                    && !getVendorOpened().getRestaurantId().equals(jsonSavedCart
                    .optInt(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId()))) {
                String oldRestaurantName = jsonSavedCart.optString(Constants.KEY_RESTAURANT_NAME, "");
                DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                        getString(R.string.previous_vendor_cart_message_format, oldRestaurantName),
                        getString(R.string.ok), getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearMenusCart();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAddressAndFetchOfferingData(int appType) {
        try {
            setAddressTextToLocationPlaceHolder();
            if (getFreshCheckoutMergedFragment() == null && getMenusCheckoutMergedFragment() == null && getFeedbackFragment() == null) {
                if (appType == AppConstant.ApplicationType.FRESH && getFreshHomeFragment() != null) {
                    getFreshHomeFragment().getSuperCategoriesAPI(true);
                } else if (appType == AppConstant.ApplicationType.MEALS && getMealFragment() != null) {
                    getMealFragment().getAllProducts(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.GROCERY && getGroceryFragment() != null) {
                    getGroceryFragment().getAllProducts(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.MENUS && getMenusFragment() != null) {
                    getMenusFragment().getAllMenus(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.FEED && getFeedHomeFragment() != null) {
                    getFeedHomeFragment().fetchFeedsApi(true, false);
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
            Prefs.with(this).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, gson.toJson(searchResultLocality, SearchResult.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOfferingLastAddressToActivityVariables(int appType) {
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = gson.fromJson(Prefs.with(this)
                    .getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
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
                if (getFreshCheckoutMergedFragment() != null || getMenusCheckoutMergedFragment() != null) {
                    setRefreshCart(true);
                    GAUtils.event(getGaCategory(), DELIVERY_ADDRESS, MODIFIED);
                }
                int appType = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
                setAddressAndFetchOfferingData(appType);
                saveOfferingLastAddress(appType);
                if (getFreshCheckoutMergedFragment() != null && (getDeliveryAddressesFragment() != null || getAddToAddressBookFragmentDirect() != null)) {
                    getFreshCheckoutMergedFragment().setDeliveryAddressUpdated(true);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HomeUtil homeUtil = new HomeUtil();

    private boolean isAddressConfirmed = false;

    public boolean isAddressConfirmed() {
        return isAddressConfirmed;
    }

    public void setIsAddressConfirmed(boolean isAddressConfirmed) {
        this.isAddressConfirmed = isAddressConfirmed;
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
                    getMenusFragment().getMenusRestaurantAdapter().searchRestaurant(s.toString().trim());
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
    }

    private void setLlLocalityClick() {
        if (deliveryAddressView != null) {
            deliveryAddressView.llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTransactionUtils().openDeliveryAddressFragment(FreshActivity.this, getRelativeLayoutContainer());
                    GAUtils.event(getGaCategory(), HOME, DELIVERY_ADDRESS+CLICKED);
                }
            });
        }
    }

    public void setDeliveryAddressViewVisibility(int visibility) {
        if (deliveryAddressView != null) {
            deliveryAddressView.llLocation.setVisibility(visibility);
        }
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
                            setDeliveryAddressModelToSelectedAddress();
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


    public interface CityChangeCallback {
        void onYesClick();

        void onNoClick();
    }

    private DeliveryAddressModel deliveryAddressModel;
    private Gson gson = new Gson();
    public Gson getGson(){
        return gson;
    }

    public void saveDeliveryAddressModel() {
        deliveryAddressModel = new DeliveryAddressModel(getSelectedAddress(), getSelectedLatLng(),
                getSelectedAddressId(), getSelectedAddressType());
        try {
            Prefs.with(this).save(Constants.SP_FRESH_CART_ADDRESS, gson.toJson(deliveryAddressModel,
                    DeliveryAddressModel.class));
        } catch (Exception e) {
        }
    }

    private void setDeliveryAddressModelToSelectedAddress() {
        if (deliveryAddressModel == null) {
            try {
                deliveryAddressModel = gson.fromJson(Prefs.with(this).getString(Constants.SP_FRESH_CART_ADDRESS,
                        Constants.EMPTY_JSON_OBJECT), DeliveryAddressModel.class);
            } catch (Exception e) {
            }
        }
        if (deliveryAddressModel != null) {
            setSelectedAddress(deliveryAddressModel.getAddress());
            setSelectedLatLng(deliveryAddressModel.getLatLng());
            setSelectedAddressId(deliveryAddressModel.getId());
            setSelectedAddressType(deliveryAddressModel.getType());
            onAddressUpdated(new AddressAdded(true));
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
                    topBar.getIvSearch().setAlpha((int) searchAndCapsuleAlpha2);
                    topBar.ivFreshSort.setAlpha((int) searchAndCapsuleAlpha2);
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

                    topBar.getIvSearch().setAlpha((int) searchAndCapsuleAlpha1);
                    topBar.ivFreshSort.setAlpha((int) searchAndCapsuleAlpha1);
                    if (topBar.ivFreshSort.isSelected()) {
                        topBar.ivFreshSort.setSelected(false);
                    }

                   /*  if (topBar.llCartContainer.isSelected())
                        topBar.llCartContainer.setSelected(false);

                    topBar.llCartContainer.getBackground().setAlpha((int) searchAndCapsuleAlpha1);*/
                    topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.argb((int) searchAndCapsuleAlpha1, 89, 89, 104), PorterDuff.Mode.SRC_ATOP);

                }


                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255 - calculatedAlpha));

                tvCollapRestaurantRating.setAlpha((float) (255 - calculatedAlpha) / 255f);
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
        tvCollapRestaurantDeliveryTime = (TextView) findViewById(R.id.tvCollapRestaurantDeliveryTime);
        rlCollapseDetails = (RelativeLayout) findViewById(R.id.layout_rest_details);
        llCollapseRating = (LinearLayout) findViewById(R.id.llCollapseRating);


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


       /* llCollapseRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestaurantReviewsListFragment();
            }
        });*/

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
                topBar.getIvSearch().setAlpha(255);
                topBar.ivFreshSort.setSelected(true);
                topBar.ivFreshSort.setAlpha(255);


                //back Button
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                topBar.imageViewBack.getDrawable().setAlpha(255);


                //Restaurant Details
                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255));
//                tvCollapRestaurantRating.setTextColor(tvCollapRestaurantRating.getTextColors().withAlpha(255));
                tvCollapRestaurantRating.setAlpha(1f);
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
                topBar.getIvSearch().setAlpha(255);
                topBar.ivFreshSort.setSelected(false);
                topBar.ivFreshSort.setAlpha(255);

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
        }
    };


    public void setVendorDeliveryTimeToTextView(MenusResponse.Vendor vendor, TextView textView) {
        final String prefix;
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final SpannableStringBuilder sb;
        if (vendor.getIsClosed() == 0) {
            String deliveryTime = String.valueOf(vendor.getDeliveryTime());
            if (vendor.getMinDeliveryTime() != null) {
                deliveryTime = String.valueOf(vendor.getMinDeliveryTime()) + "-" + deliveryTime;
            }
            prefix = getString(R.string.delivers_in);
            sb = new SpannableStringBuilder(deliveryTime + " mins");

        } else {
            prefix = getString(R.string.opens_at);
            sb = new SpannableStringBuilder(String.valueOf(DateOperations.convertDayTimeAPViaFormat(vendor.getOpensAt())));
        }
        sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(prefix);
        textView.append(" ");
        textView.append(sb);

        if(!TextUtils.isEmpty(vendor.getDeliveryTimeText())){
            textView.setText(Utils.trimHTML(Utils.fromHtml(vendor.getDeliveryTimeText())));
        }
    }

    public FABViewTest getFabViewTest() {
        return fabViewTest;
    }

    public int setRatingAndGetColor(TextView tv, Double rating, String colorCode, boolean setBackgroundColor) {
        Spannable spannable = new SpannableString(getString(R.string.star_icon) + " " + rating);
        Typeface star = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        spannable.setSpan(new CustomTypeFaceSpan("", star), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    public void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void openRestaurantReviewsListFragment() {
        if (getVendorOpened() != null) {
            appBarLayout.setExpanded(false, false);
            llCheckoutBarSetVisibility(View.GONE);
            topBar.ivSearch.setVisibility(View.GONE);
            GAUtils.event(GACategory.MENUS, GAAction.RESTAURANT_HOME , GAAction.FEED + GAAction.CLICKED);
            getTransactionUtils().openRestaurantReviewsListFragment(this, relativeLayoutContainer, getVendorOpened().getRestaurantId());
        }
    }

    public void openRestaurantAddReviewFragment(boolean isPlusButtonClicked) {
        if (getVendorOpened() != null) {
            if(isPlusButtonClicked){
                this.currentReview=null;
            }
            getTransactionUtils().openRestaurantAddReviewFragment(this, relativeLayoutContainer, getVendorOpened().getRestaurantId());
        }
    }

    public boolean canExitVendorMenu() {
        if (getTopFragment() != null && getTopFragment() instanceof VendorMenuFragment && mCurrentState == State.IDLE && currentVerticalOffSet != -1)
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
    public void fetchRestaurantMenuAPI(final int restaurantId){
        if(apiFetchRestaurantMenu == null){
            apiFetchRestaurantMenu = new ApiFetchRestaurantMenu(this, new ApiFetchRestaurantMenu.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {
                    fetchRestaurantMenuAPI(restaurantId);
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        apiFetchRestaurantMenu.hit(restaurantId, getSelectedLatLng().latitude,
                getSelectedLatLng().longitude);
    }


    private AppCart appCart;
    public AppCart getCart(){
        return appCart;
    }
    private void createAppCart(String clientId){
        if(clientId.equalsIgnoreCase(Config.getFreshClientId())){
            appCart = Paper.book().read(DB_FRESH_CART, new AppCart());
        }
        else if(clientId.equalsIgnoreCase(Config.getMealsClientId())){
            appCart = Paper.book().read(DB_MEALS_CART, new AppCart());
        }
    }
    private void saveAppCart(String clientId){
        if(clientId.equalsIgnoreCase(Config.getFreshClientId())){
            if(appCart != null) {
                Paper.book().write(DB_FRESH_CART, appCart);
            } else {
                Paper.book().delete(DB_FRESH_CART);
            }
        }
        else if(clientId.equalsIgnoreCase(Config.getMealsClientId())){
            if(appCart != null) {
                Paper.book().write(DB_MEALS_CART, appCart);
            } else {
                Paper.book().delete(DB_MEALS_CART);
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
        checkout.setImage(R.drawable.jugnoo_icon);
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
            intent.putExtra(Constants.KEY_APP_TYPE, getAppType());
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


    public void openFeedDetailsFragmentWithPostId(int postId){
        try {
            FeedDetail feedDetail = new FeedDetail();
            feedDetail.setPostId(postId);
            getTransactionUtils().openFeedCommentsFragment(this, getRelativeLayoutContainer(), feedDetail, -1, false);
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
            GAUtils.event(GACategory.MENUS, GAAction.RESTAURANT_HOME, GAAction.SORT_BUTTON + GAAction.CLICKED);
        } else if (getAppType() == AppConstant.ApplicationType.FRESH && getFreshFragment() != null) {
            if (getFreshFragment().getSuperCategory() != null)
                GAUtils.event(FRESH, getFreshFragment().getSuperCategory().getSuperCategoryName(), SORT_BUTTON + CLICKED);
        }
    }

    public void llCheckoutBarSetVisibilityDirect(int visibility){
        if(llCheckoutBar != null) {
            if (visibility == View.VISIBLE && totalPrice > 0 && totalQuantity > 0) {
                llCheckoutBar.setVisibility(View.VISIBLE);
            } else {
                llCheckoutBar.setVisibility(View.GONE);
            }
        }
    }

    private long checkoutBarAnimDuration = 150L;
    private void llCheckoutBarSetVisibility(int visibility){
        if(visibility == View.VISIBLE && llCheckoutBar.getVisibility() != View.VISIBLE){
            llCheckoutBar.setVisibility(View.VISIBLE);

            llCheckoutBar.clearAnimation();
            Animation animation = new TranslateAnimation(0, 0, llCheckoutBar.getMeasuredHeight(), 0);
            animation.setDuration(checkoutBarAnimDuration);
            llCheckoutBar.startAnimation(animation);

            if(getFreshHomeFragment() != null && getFreshHomeFragment().getRvFreshSuper() != null){
                getFreshHomeFragment().getRvFreshSuper().setPadding(0, 0, 0, llCheckoutBar.getMeasuredHeight());
            }

            if(textViewMinOrder.getVisibility() == View.VISIBLE) {
                textViewMinOrder.clearAnimation();
                Animation translateUp = new TranslateAnimation(0, 0, llCheckoutBar.getMeasuredHeight(), 0);
                translateUp.setDuration(checkoutBarAnimDuration);
                textViewMinOrder.startAnimation(translateUp);
            }

        } else if(visibility == View.GONE && llCheckoutBar.getVisibility() != View.GONE){
            llCheckoutBar.clearAnimation();

            Animation animation = new TranslateAnimation(0, 0, 0, llCheckoutBar.getMeasuredHeight());
            animation.setDuration(checkoutBarAnimDuration);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    textViewMinOrder.clearAnimation();
                    llCheckoutBar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            llCheckoutBar.startAnimation(animation);

            if(getFreshHomeFragment() != null && getFreshHomeFragment().getRvFreshSuper() != null){
                getFreshHomeFragment().getRvFreshSuper().setPadding(0, 0, 0, 0);
            }

            if(textViewMinOrder.getVisibility() == View.VISIBLE) {
                textViewMinOrder.clearAnimation();
                Animation translateDown = new TranslateAnimation(0, 0, 0, llCheckoutBar.getMeasuredHeight());
                translateDown.setDuration(checkoutBarAnimDuration);
                textViewMinOrder.startAnimation(translateDown);
            }
        }
    }

    private void textViewMinOrderSetVisibility(int visibility){
        if(textViewMinOrder.getVisibility() != visibility) {
            textViewMinOrder.clearAnimation();
            textViewMinOrder.setVisibility(visibility);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(llCheckoutBar != null) {
            llCheckoutBar.post(new Runnable() {
                @Override
                public void run() {
                    llCheckoutBar.getMeasuredHeight();
                    llCheckoutBar.setVisibility(View.GONE);
                }
            });
        }

    }

}
