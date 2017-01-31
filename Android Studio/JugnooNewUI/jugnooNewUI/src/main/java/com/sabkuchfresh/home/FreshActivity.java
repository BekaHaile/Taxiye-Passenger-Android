package com.sabkuchfresh.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jugnoo.pay.activities.PaySDKUtils;
import com.jugnoo.pay.models.MessageRequest;
import com.sabkuchfresh.adapters.FreshSortingAdapter;
import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.bus.AddressSearch;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.datastructure.CheckoutSaveData;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.fragments.AddAddressMapFragment;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
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
import com.sabkuchfresh.fragments.VendorMenuFragment;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
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
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemCompareAtoZ;
import com.sabkuchfresh.retrofit.model.menus.ItemComparePriceHighToLow;
import com.sabkuchfresh.retrofit.model.menus.ItemComparePriceLowToHigh;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.OrderStatusActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
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
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
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
public class FreshActivity extends AppCompatActivity implements LocationUpdate, FlurryEventNames {

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

    /**
     * this holds the reference for the Otto Bus which we declared in LavocalApplication
     */
    protected Bus mBus;
    private double totalPrice = 0;
    private int totalQuantity = 0;

    public boolean updateCart = false;

    private LocationFetcher locationFetcher;
    public float scale = 0f;

    public boolean locationSearchShown = false;
    public boolean canOrder = false;

    public int freshSort = -1;
    public int mealSort = -1;
    public int menusSort = -1;

    private VendorMenuResponse vendorMenuResponse;

    private AppBarLayout appBarLayout;
    private RelativeLayout rlSort, rlSortBg, rlSortContainer;
    private View viewSortFake, viewSortFake1;
    private ImageView ivSort;
    private Toolbar toolbar;
    private RecyclerView rvDeliverySlots;
    private FreshSortingAdapter sortingAdapter;
    private static final float COLLAPSE_TOOLBAR_HEIGHT = 270f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_fresh);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");

            appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);


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
                setSelectedLatLng(new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
                        getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude)));
            }

            textViewMinOrder = (TextView) findViewById(R.id.textViewMinOrder);
            textViewMinOrder.setTypeface(Fonts.mavenRegular(this));
            rlSort = (RelativeLayout) findViewById(R.id.rlSort);
            rlSortBg = (RelativeLayout) findViewById(R.id.rlSortBg);
            ivSort = (ImageView) findViewById(R.id.ivSort);
            viewSortFake = (View) findViewById(R.id.viewSortFake);
            viewSortFake1 = (View) findViewById(R.id.viewSortFake1);
            rlSortContainer = (RelativeLayout) findViewById(R.id.rlSortContainer);
            rvDeliverySlots = (RecyclerView) findViewById(R.id.rvDeliverySlots);
            rvDeliverySlots.setLayoutManager(new LinearLayoutManager(this));
            rvDeliverySlots.setItemAnimator(new DefaultItemAnimator());
            rvDeliverySlots.setHasFixedSize(false);

            topView = (View) findViewById(R.id.topBarMain);

            menuBar = new MenuBar(this, drawerLayout);
            topBar = new TopBar(this, drawerLayout);
            fabViewTest = new FABViewTest(this, findViewById(R.id.relativeLayoutFABTest));

            topBar.etSearch.addTextChangedListener(textWatcher);
            resetToolbar();

            /**
             Edited by Parminder Singh on 1/30/17 at 12:54 PM
             **/


            initCollapseToolBarViews();


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

            sortingAdapter = new FreshSortingAdapter(FreshActivity.this, slots, new FreshSortingAdapter.Callback() {
                @Override
                public void onSlotSelected(int position, SortResponseModel slot) {
                    int type = getAppType();
                    if (type == AppConstant.ApplicationType.MEALS) {
                        mealSort = position;
                        MyApplication.getInstance().logEvent(FirebaseEvents.M_SORT + "_" + slot.name, null);
                    } else if (type == AppConstant.ApplicationType.GROCERY) {
                        freshSort = position;
                        MyApplication.getInstance().logEvent(FirebaseEvents.G_SORT + "_" + slot.name, null);
                    } else if (type == AppConstant.ApplicationType.MENUS) {
                        menusSort = position;
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_SORT + "_" + slot.name, null);
                    } else {
                        freshSort = position;
                        MyApplication.getInstance().logEvent(FirebaseEvents.F_SORT + "_" + slot.name, null);
                    }
                    getBus().post(new SortSelection(position));
                    ivSort.performClick();
                }
            });
            rvDeliverySlots.setAdapter(sortingAdapter);


            View.OnClickListener checkoutOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeyboard(FreshActivity.this, topBar.etSearch);
                    FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.CHECKOUT_SCREEN);
                    FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.REVIEW_CART, getProduct());

                    int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                    updateCartFromSP();
                    if (updateCartValuesGetTotalPrice().second > 0) {
                        if (getMealAddonItemsFragment() == null && isMealAddonItemsAvailable()) {
                            getTransactionUtils().addMealAddonItemsFragment(FreshActivity.this, relativeLayoutContainer);
                        } else {
                            openCart(appType);

                            if (appType == AppConstant.ApplicationType.MEALS) {
                                MyApplication.getInstance().logEvent(FirebaseEvents.M_CART, null);
                            } else {
                                if ((getFreshSearchFragment() != null && !getFreshSearchFragment().isHidden())
                                        || (getMenusSearchFragment() != null && !getMenusSearchFragment().isHidden())) {
                                    if (appType == AppConstant.ApplicationType.GROCERY) {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.G_SEARCH_GO, null);
                                    } else if (appType == AppConstant.ApplicationType.MENUS) {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_SEARCH_GO, null);
                                    } else {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.F_SEARCH_GO, null);
                                    }
                                } else {
                                    if (appType == AppConstant.ApplicationType.GROCERY) {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.G_CART, null);
                                    } else if (appType == AppConstant.ApplicationType.MENUS) {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_CART, null);
                                    } else {
                                        MyApplication.getInstance().logEvent(FirebaseEvents.F_CART, null);
                                    }
                                }
                            }
                        }
                    } else {
                        Utils.showToast(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty));
                    }
                }
            };


            topBar.getLlCartContainer().setOnClickListener(checkoutOnClickListener);

            ivSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewSortFake.getVisibility() == View.GONE) {
                        viewSortFake.setVisibility(View.VISIBLE);
                        viewSortFake1.setVisibility(View.VISIBLE);
                        rlSortContainer.setVisibility(View.GONE);
                    } else {
                        viewSortFake.setVisibility(View.GONE);
                        viewSortFake1.setVisibility(View.GONE);
                        rlSortContainer.setVisibility(View.VISIBLE);
                    }
                }
            });

            rlSortContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivSort.performClick();
                }
            });


            try {
                float marginBottom = 77f;
                String lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);
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
                    marginBottom = 33f;
                } else {
                    openCart();
                    addFreshHomeFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
                    lastClientId = Config.getFreshClientId();
                }
                int dpAsPixels = (int) (marginBottom * scale + 0.5f);
                fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
            } catch (Exception e) {
                e.printStackTrace();
                addFreshHomeFragment();
            }


            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter(Data.LOCAL_BROADCAST));

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
        sortingAdapter.notifyDataSetChanged();
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (getAppType() != AppConstant.ApplicationType.MENUS && productsResponse != null && productsResponse.getCategories() != null) {
                                updateCartFromSP();
                                topBar.getLlCartContainer().performClick();
                            } else if (getAppType() == AppConstant.ApplicationType.MENUS && getMenuProductsResponse() != null && getMenuProductsResponse().getCategories() != null) {
                                updateCartFromSP();
                                topBar.getLlCartContainer().performClick();
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
                                    updateCartFromSP();
                                    topBar.getLlCartContainer().performClick();
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
                                    updateCartFromSP();
                                    topBar.getLlCartContainer().performClick();
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
                                    updateCartFromSP();
                                    topBar.getLlCartContainer().performClick();
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
                                Data.getDeepLinkIndexFromIntent(FreshActivity.this, intent);
                                openPushDialog();
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
                                    LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);
                                }
                            } else if (PushFlags.MENUS_STATUS.getOrdinal() == flag) {
                                Fragment fragment = getTopFragment();

                                if (fragment instanceof MenusFragment && FreshActivity.this.hasWindowFocus()) {
                                    ((MenusFragment) fragment).getAllMenus(true, getSelectedLatLng());
                                } else {
                                    Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                    intent1.putExtra(Constants.KEY_FLAG, flag);
                                    LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);
                                }
                            }
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


                fetchWalletBalance(this);

                if (locationFetcher == null) {
                    locationFetcher = new LocationFetcher(this, 60000l);
                } else {
                    locationFetcher.connect();
                }

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                        Data.userData.getIntegratedJugnooEnabled() == 1) {
                    if ((getFreshHomeFragment() != null && !getFreshHomeFragment().isHidden()) ||
                            (getMealFragment() != null && !getMealFragment().isHidden()) ||
                            (getGroceryFragment() != null && !getGroceryFragment().isHidden())
                            || (getMenusFragment() != null && !getMenusFragment().isHidden())) {
                        fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                        fabViewTest.setFABButtons();
                    }
                } else {
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
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

    public FreshHomeFragment getFreshHomeFragment() {
        return (FreshHomeFragment) getSupportFragmentManager().findFragmentByTag(FreshHomeFragment.class.getName());
    }

    public FreshFragment getFreshFragment() {
        return (FreshFragment) getSupportFragmentManager().findFragmentByTag(FreshFragment.class.getName());
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

    public VendorMenuFragment getVendorMenuFragment() {
        return (VendorMenuFragment) getSupportFragmentManager().findFragmentByTag(VendorMenuFragment.class.getName());
    }

    private FreshCheckoutMergedFragment getFreshCheckoutMergedFragment() {
        return (FreshCheckoutMergedFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutMergedFragment.class.getName());
    }

    private MenusCheckoutMergedFragment getMenusCheckoutMergedFragment() {
        return (MenusCheckoutMergedFragment) getSupportFragmentManager().findFragmentByTag(MenusCheckoutMergedFragment.class.getName());
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
        saveCartToSPFMG(subItemToUpdate);
        Pair<Double, Integer> pair = getSubItemInCartTotalPrice();
        try {
            if (totalPrice > 0) {
                topBar.getLlCartAmount().setVisibility(View.VISIBLE);
                topBar.getTvCartAmount().setText(String.format(getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(totalPrice)));
            } else {
                topBar.getLlCartAmount().setVisibility(View.GONE);
            }
            if (getFreshFragment() != null) {
                setMinOrderAmountText(getFreshFragment());
            } else if (getGroceryFragment() != null) {
                setMinOrderAmountText(getGroceryFragment());
            }
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
                if (totalPrice > 0) {
                    topBar.getLlCartAmount().setVisibility(View.VISIBLE);
                    topBar.getTvCartAmount().setText(String.format(getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormat().format(totalPrice)));
                } else {
                    topBar.getLlCartAmount().setVisibility(View.GONE);
                }
                if (getVendorMenuFragment() != null && getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                    if (getMenusCheckoutMergedFragment() == null && totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveCartToSPMenus();
        if (totalQuantity > 0) {
        }
        pair = new Pair<>(totalPrice, totalQuantity);
        return pair;
    }


    public void fragmentUISetup(Fragment fragment) {
        try {
            int llSearchCartContainerVis = View.VISIBLE;
            int llSearchCartVis = View.VISIBLE;
            int llCartContainerVis = View.GONE;
            int ivSearchVis = View.GONE;
            int llSearchContainerVis = View.GONE;
            int appType = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
            textViewMinOrder.setVisibility(View.GONE);

            topBar.imageViewDelete.setVisibility(View.GONE);
            topBar.textViewReset.setVisibility(View.GONE);

            RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) topBar.title.getLayoutParams();
            titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);

            topView.setVisibility(View.VISIBLE);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            lp.height = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(lp);

            fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
            topBar.editTextDeliveryAddress.setVisibility(View.GONE);

            rlSort.setVisibility(View.GONE);

            topBar.llSearchContainer.setVisibility(View.GONE);
            int rlFilterVis = View.GONE;
            topBar.buttonCheckServer.setVisibility(View.GONE);

            if (fragment instanceof FreshHomeFragment) {
                topBar.buttonCheckServer.setVisibility(View.VISIBLE);
                llSearchCartContainerVis = View.VISIBLE;
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    float marginBottom = 40f;
                    int dpAsPixels = (int) (marginBottom * scale + 0.5f);
                    fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                }


                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.fresh));
                //topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                setMinOrderAmountText(fragment);

            } else if (fragment instanceof FreshFragment) {
                llSearchCartContainerVis = View.VISIBLE;
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                rlSort.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.fresh));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
                setMinOrderAmountText(fragment);


            } else if (fragment instanceof MealFragment) {
                llSearchCartContainerVis = View.VISIBLE;
                llCartContainerVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    float marginBottom = 40f;
                    int dpAsPixels = (int) (marginBottom * scale + 0.5f);
                    fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                }

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.meals));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
            } else if (fragment instanceof GroceryFragment) {
                llSearchCartContainerVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);

                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                    float marginBottom = 85f;
                    int dpAsPixels = (int) (marginBottom * scale + 0.5f);
                    fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                }
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.grocery));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                setMinOrderAmountText(fragment);

            } else if (fragment instanceof MenusFragment) {
                llSearchCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                if (Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                }
                rlFilterVis = View.VISIBLE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.menus));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());

            } else if (fragment instanceof VendorMenuFragment) {
                llCartContainerVis = View.VISIBLE;
                ivSearchVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                rlSort.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(vendorOpened.getName());
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());


                if (getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                    if (totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                    textViewMinOrder.setText(getString(R.string.minimum_order) + " "
                            + getString(R.string.rupees_value_format_without_space, Utils.getMoneyDecimalFormatWithoutFloat().format(getVendorOpened().getMinimumOrderAmount())));
                }
            } else if (fragment instanceof MenusItemCustomizeFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);


                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getString(R.string.customize_item));
                //topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
            } else if (fragment instanceof MenusFilterFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.filters);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
                topBar.textViewReset.setVisibility(View.VISIBLE);
            } else if (fragment instanceof MenusFilterCuisinesFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.select_cuisines);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
            } else if (fragment instanceof FreshCheckoutMergedFragment || fragment instanceof MenusCheckoutMergedFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                llSearchCartContainerVis = View.VISIBLE;
                llSearchCartVis = View.GONE;

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.checkout));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            } else if (fragment instanceof AddAddressMapFragment || fragment instanceof AddToAddressBookFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                if (fragment instanceof AddToAddressBookFragment) {
                    topBar.title.setText(getResources().getString(R.string.confirm_address));
                } else if (fragment instanceof AddAddressMapFragment) {
                    topBar.title.setText(getResources().getString(R.string.choose_your_address));
                } else {
                    topBar.title.setText(getResources().getString(R.string.address));
                }
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
                llSearchCartContainerVis = View.VISIBLE;

                try {
                    if (appType == AppConstant.ApplicationType.MENUS && getVendorMenuFragment() != null
                            && getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                        if (totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                            textViewMinOrder.setVisibility(View.VISIBLE);
                        } else {
                            textViewMinOrder.setVisibility(View.GONE);
                        }
                        textViewMinOrder.setText(getString(R.string.minimum_order) + " "
                                + getString(R.string.rupees_value_format_without_space, Utils.getMoneyDecimalFormatWithoutFloat().format(getVendorOpened().getMinimumOrderAmount())));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                setMinOrderAmountText(fragment);

            } else if (fragment instanceof FeedbackFragment) {
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.imageViewBack.setVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            } else if (fragment instanceof OrderStatusActivity) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                //topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            } else if (fragment instanceof MealAddonItemsFragment) {
                llCartContainerVis = View.VISIBLE;
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.pick_addons));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }

            if (topBar.imageViewMenu.getVisibility() == View.VISIBLE) {
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
            } else if (topBar.imageViewBack.getVisibility() == View.VISIBLE) {
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
            }


            topBar.title.setLayoutParams(titleLayoutParams);

            topBar.getLlSearchCartContainer().setVisibility(llSearchCartContainerVis);
            topBar.getLlSearchCart().setVisibility(llSearchCartVis);
            topBar.getLlCartContainer().setVisibility(llCartContainerVis);
            topBar.getIvSearch().setVisibility(ivSearchVis);
            topBar.getLlSearchContainer().setVisibility(llSearchContainerVis);
            topBar.rlFilter.setVisibility(rlFilterVis);


            /**
             Edited by Parminder Singh on 1/30/17 at 9:59 PM
             **/

            setCollapsingToolbar(fragment instanceof VendorMenuFragment);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Edited by Parminder Singh on 1/30/17 at 12:28 PM
     **/


    private void setCollapsingToolbar(boolean isEnable) {
        if (isEnable) {
            findViewById(R.id.layout_rest_details).setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            layoutParams.height = (int) (ASSL.Yscale() * COLLAPSE_TOOLBAR_HEIGHT);
            appBarLayout.setLayoutParams(layoutParams);

            CollapsingToolbarLayout.LayoutParams toolBarParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                toolBarParams.height = actionBarHeight;
                toolbar.setLayoutParams(toolBarParams);

            }


            onStateChanged(appBarLayout, State.EXPANDED);


            appBarLayout.addOnOffsetChangedListener(collapseBarController);

        } else {

            appBarLayout.removeOnOffsetChangedListener(collapseBarController);

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            layoutParams.height = AppBarLayout.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(layoutParams);

            CollapsingToolbarLayout.LayoutParams toolBarParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            toolBarParams.height = CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT;
            toolbar.setLayoutParams(toolBarParams);


            onStateChanged(appBarLayout, State.COLLAPSED);

        }






       /* CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout.LayoutParams appbar = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        collapsingToolbarLayout.setLayoutParams(appbar);


        CollapsingToolbarLayout.LayoutParams collapseParams = (CollapsingToolbarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        collapseParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN);
        toolbar.setLayoutParams(collapseParams);
*/
    }

    public void setMinOrderAmountText(Fragment fragment) {
        try {
            if (getFreshFragment() != null || getGroceryFragment() != null || (getFreshSearchFragment() != null && getVendorMenuFragment() == null)) {
                if (getProductsResponse() != null && totalQuantity > 0
                        && (fragment instanceof FreshFragment || fragment instanceof GroceryFragment || fragment instanceof FreshSearchFragment)) {
                    if (Data.userData.isSubscriptionActive() && !TextUtils.isEmpty(getProductsResponse().getSubscriptionMessage())) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                        textViewMinOrder.setText(getProductsResponse().getSubscriptionMessage());
                    } else if (totalPrice < getSuperCategoriesData().getDeliveryInfo().getMinAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                        double leftAmount = getSuperCategoriesData().getDeliveryInfo().getMinAmount() - totalPrice;
                        textViewMinOrder.setText(getString(R.string.fresh_min_order_value_format,
                                Utils.getMoneyDecimalFormatWithoutFloat().format(leftAmount)));
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                } else {
                    textViewMinOrder.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void searchItem() {
        try {
            if (getAppType() == AppConstant.ApplicationType.MENUS) {
                if (getTopFragment() instanceof MenusFragment) {
                    getMenusFragment().openSearch();
                } else if (getTopFragment() instanceof VendorMenuFragment) {
                    getTransactionUtils().openMenusSearchFragment(FreshActivity.this, relativeLayoutContainer);
                }
            } else {
                if (getFreshFragment() != null) {
                    getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer, getFreshFragment().getSuperCategory().getSuperCategoryId(),
                            getSuperCategoriesData().getDeliveryInfo().getCityId());
                } else {
                    getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer, -1, getSuperCategoriesData().getDeliveryInfo().getCityId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMenusFilter() {
        getTransactionUtils().openMenusFilterFragment(this, getRelativeLayoutContainer());
    }

    private FreshSearchFragment getFreshSearchFragment() {
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
                        FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.DELETE, FlurryEventNames.ALL);

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
                        if (type == AppConstant.ApplicationType.MEALS) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.YES, null);
                        } else if (type == AppConstant.ApplicationType.GROCERY) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.G_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.YES, null);
                        } else if (type == AppConstant.ApplicationType.MENUS) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.YES, null);
                        } else {
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.YES, null);
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == AppConstant.ApplicationType.MEALS) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.NO, null);
                        } else if (type == AppConstant.ApplicationType.GROCERY) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.G_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.NO, null);
                        } else if (type == AppConstant.ApplicationType.MENUS) {
                            MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.NO, null);
                        } else {
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART + "_" + FirebaseEvents.TRASH + "_" + FirebaseEvents.NO, null);
                        }
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

    public void orderComplete() {
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
        splInstr = "";
        slotSelected = null;
        slotToSelect = null;
        paymentOption = null;

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount() - 1; i++) {
            fm.popBackStack();
        }

        updateCartValuesGetTotalPrice();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));
            }
        }, 1000);

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


    /**
     * Method used to open feedback screen
     */
    public void openFeedback() {
        getTransactionUtils().openFeedback(FreshActivity.this, relativeLayoutContainer);
    }

    public void openMapAddress(Bundle bundle) {
        FlurryEventLogger.event(Address_Screen, SCREEN_TRANSITION, ADD_NEW_ADDRESS);
        getTransactionUtils().openMapFragment(FreshActivity.this, relativeLayoutContainer, bundle);
    }

    public void openAddToAddressBook(Bundle bundle) {
        getTransactionUtils().openAddToAddressFragment(FreshActivity.this, relativeLayoutContainer, bundle);
    }


    public void performBackPressed() {

        if (getFeedbackFragment() != null && getSupportFragmentManager().getBackStackEntryCount() == 2 && !getFeedbackFragment().isUpbuttonClicked) {
            finish();
        }
        try {
            Utils.hideSoftKeyboard(this, textViewMinOrder);
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
                            performBackPressed();
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
            getMenusFragment().openSearch();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            if ((getSupportFragmentManager().getBackStackEntryCount() == 2 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() != null)) {
                FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.HOME_SCREEN);
            }

            if ((getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 4 && getFreshSearchFragment() != null)) {
                FlurryEventLogger.event(FlurryEventNames.CHECKOUT, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.REVIEW_CART_SCREEN);
            }
            if (getTopFragment() instanceof FreshSearchFragment) {
                getFreshSearchFragment().clearArrays();
            } else if (getTopFragment() instanceof MenusSearchFragment) {
                getMenusSearchFragment().clearArrays();
            }

            super.onBackPressed();
        }
    }


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
        performBackPressed();
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
        if (cartChangedAtCheckout && getFreshCheckoutMergedFragment() != null) {
            updateCartFromSPFMG(null);
        }
        saveCartToSP();
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
            saveFilters();
        }

        if (locationFetcher != null) {
            locationFetcher.destroy();
        }

    }

    public void saveCartToSP() {
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
            saveCartToSPMenus();
        } else {
            saveCartToSPFMG(null);
        }
    }

    private void saveCartToSPFMG(SubItem subItemToUpdate) {
        try {
            JSONObject jCart = new JSONObject();
            if (getAppType() == AppConstant.ApplicationType.FRESH) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
            } else if (getAppType() == AppConstant.ApplicationType.GROCERY) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT));
            } else {
                if (subItemToUpdate != null) {
                    jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT));
                }
            }
            Gson gson = new Gson();
            if (subItemToUpdate == null && getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        if (subItem.getSubItemQuantitySelected() > 0) {
                            try {
                                jCart.put(String.valueOf(subItem.getSubItemId()), gson.toJson(subItem, SubItem.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                jCart.remove(String.valueOf(subItem.getSubItemId()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else if (subItemToUpdate != null) {
                if (subItemToUpdate.getSubItemQuantitySelected() > 0) {
                    try {
                        jCart.put(String.valueOf(subItemToUpdate.getSubItemId()), gson.toJson(subItemToUpdate, SubItem.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jCart.remove(String.valueOf(subItemToUpdate.getSubItemId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            int type = getAppType();
            if (type == AppConstant.ApplicationType.FRESH) {
                Prefs.with(this).save(Constants.SP_FRESH_CART, jCart.toString());
            } else if (type == AppConstant.ApplicationType.GROCERY) {
                Prefs.with(this).save(Constants.SP_GROCERY_CART, jCart.toString());
            } else {
                Prefs.with(this).save(Constants.SP_MEAL_CART, jCart.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCartToSPMenus() {
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


    public void updateCartFromSP() {
        if (getAppType() == AppConstant.ApplicationType.MENUS) {
            updateCartFromSPMenus();
        } else {
            updateCartFromSPFMG(null);
        }
    }

    public void updateCartFromSPFMG(ArrayList<SubItem> subItems) {
        try {
            JSONObject jCart;
            int type = getAppType();
            if (type == AppConstant.ApplicationType.FRESH) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
            } else if (type == AppConstant.ApplicationType.GROCERY) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT));
            } else {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT));
            }
            Gson gson = new Gson();
            if (subItems == null && getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                boolean cartUpdated = false;
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        subItem.setSubItemQuantitySelected(0);
                        try {
                            String jItem = jCart.optString(String.valueOf(subItem.getSubItemId()), "");
                            if (!TextUtils.isEmpty(jItem)) {
                                SubItem subItemSaved = gson.fromJson(jItem, SubItem.class);
                                if (subItem.getStock() < subItemSaved.getSubItemQuantitySelected()) {
                                    subItemSaved.setSubItemQuantitySelected(subItem.getStock());
                                    cartUpdated = true;
                                }
                                subItem.setSubItemQuantitySelected(subItemSaved.getSubItemQuantitySelected());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (cartUpdated) {
                    saveCartToSPFMG(null);
                }
            } else if (subItems != null) {
                boolean cartUpdated = false;
                for (SubItem subItem : subItems) {
                    subItem.setSubItemQuantitySelected(0);
                    try {
                        String jItem = jCart.optString(String.valueOf(subItem.getSubItemId()), "");
                        if (!TextUtils.isEmpty(jItem)) {
                            SubItem subItemSaved = gson.fromJson(jItem, SubItem.class);
                            if (subItem.getStock() < subItemSaved.getSubItemQuantitySelected()) {
                                subItemSaved.setSubItemQuantitySelected(subItem.getStock());
                                cartUpdated = true;
                            }
                            subItem.setSubItemQuantitySelected(subItemSaved.getSubItemQuantitySelected());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cartUpdated) {
                    saveCartToSPFMG(null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCartFromSPMenus() {
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


    private void clearCart() {
        Prefs.with(this).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);
    }

    private void clearMealCart() {
        Prefs.with(this).save(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT);
    }

    private void clearGroceryCart() {
        Prefs.with(this).save(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT);
    }


    private void gaEvents(String category, String action, String label) {
        if (category.equalsIgnoreCase("")) {
            if (getTopFragment() instanceof FreshFragment) {
                FlurryEventLogger.event(FlurryEventNames.FRESH_FRAGMENT, action, label);
            } else if (getTopFragment() instanceof MealFragment) {
                FlurryEventLogger.event(FlurryEventNames.MEALS_FRAGMENT, action, label);
            } else if (getTopFragment() instanceof GroceryFragment) {
                FlurryEventLogger.event(FlurryEventNames.GROCERY_FRAGMENT, action, label);
            } else if (getTopFragment() instanceof MenusFragment) {
                FlurryEventLogger.event(FlurryEventNames.MENUS_FRAGMENT, action, label);
            }
        } else {
            FlurryEventLogger.event(category, action, label);
        }
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
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.A_Z);
                        break;
                    case 1:
                        comparator = new ItemComparePriceLowToHigh();
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                        break;
                    case 2:
                        comparator = new ItemComparePriceHighToLow();
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_HIGH_TO_LOW);
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
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.A_Z);
                        break;
                    case 1:
                        comparator = new SubItemComparePriority();
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.POPULARITY);
                        break;
                    case 2:
                        comparator = new SubItemComparePriceLowToHigh();
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                        break;
                    case 3:
                        comparator = new SubItemComparePriceHighToLow();
                        gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_HIGH_TO_LOW);
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

    @Override
    public void onLocationChanged(Location location) {
        Data.latitude = location.getLatitude();
        Data.longitude = location.getLongitude();
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
                        } else {
                            getSupportFragmentManager().popBackStack(AddAddressMapFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                                }
                            }
                        }
                    }
                    updateCartValuesGetTotalPrice();
                }
            }
            clearMealCart();
            updateCartFromSP();
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

    private void openCart(int appType) {
        if (appType == AppConstant.ApplicationType.MENUS && getVendorOpened() != null) {
            getTransactionUtils().openMenusCheckoutMergedFragment(FreshActivity.this, relativeLayoutContainer);
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
            return true;
        }
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
                                String resp = new String(((TypedByteArray) response.getBody()).getBytes());
                                GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(resp);
                                String address = gapiAddress.formattedAddress;
                                setSelectedAddress(address);
                                setSelectedLatLng(currentLatLng);
                                setSelectedAddressId(0);
                                setSelectedAddressType("");
                                setAddressAndFetchOfferingData(appType);
                                DialogPopup.dismissLoadingDialog();
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


    public void setAddressAndFetchOfferingData(int appType) {
        try {
            String address = "";
            if (TextUtils.isEmpty(getSelectedAddressType())) {
                String[] arr = null;
                if (getSelectedAddress().contains(",")) {
                    arr = getSelectedAddress().split(", ");
                } else {
                    arr = getSelectedAddress().split(" ");
                }
                if (arr.length > 1) {
                    address = arr[0] + ", " + arr[1];
                } else if (arr.length > 0) {
                    address = arr[0];
                }
            } else {
                address = getSelectedAddressType();
            }
            setLocationAddress(address);
            if (getFreshCheckoutMergedFragment() == null && getMenusCheckoutMergedFragment() == null && getFeedbackFragment() == null) {
                if (appType == AppConstant.ApplicationType.FRESH && getFreshHomeFragment() != null) {
                    getFreshHomeFragment().getSuperCategoriesAPI();
                } else if (appType == AppConstant.ApplicationType.MEALS && getMealFragment() != null) {
                    getMealFragment().getAllProducts(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.GROCERY && getGroceryFragment() != null) {
                    getGroceryFragment().getAllProducts(true, getSelectedLatLng());
                } else if (appType == AppConstant.ApplicationType.MENUS && getMenusFragment() != null) {
                    getMenusFragment().getAllMenus(true, getSelectedLatLng());
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
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                if (searchResult != null && !TextUtils.isEmpty(searchResult.getAddress())) {
                    setSearchResultToActVarsAndFetchData(searchResult, appType);
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
                setRefreshCart(true);
                int appType = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
                setAddressAndFetchOfferingData(appType);
                saveOfferingLastAddress(appType);
                if (getFreshCheckoutMergedFragment() != null
                        && (getDeliveryAddressesFragment() != null || getAddToAddressBookFragmentDirect() != null)) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetToolbar() {
  /*      AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
//                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
//                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        params.setScrollFlags(0);

        float height = 96f;

        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = (int)(height * ASSL.Yscale());
        toolbar.setLayoutParams(layoutParams);*/
    }


    public void openMenusItemCustomizeFragment(int categoryPos, int subCategoryPos, int itemPos) {
        getTransactionUtils().openMenusItemCustomizeFragment(this, getRelativeLayoutContainer(), categoryPos, subCategoryPos, itemPos);
    }

    public int getAppType() {
        return Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
    }


    public ArrayList<SubItem> fetchCartList() {
        ArrayList<SubItem> subItemsInCart = new ArrayList<>();
        try {
            JSONObject jCart = new JSONObject();
            if (getAppType() == AppConstant.ApplicationType.FRESH) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
            } else if (getAppType() == AppConstant.ApplicationType.GROCERY) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT));
            } else {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT));
            }

            Gson gson = new Gson();
            Iterator<String> itemIds = jCart.keys();
            while (itemIds.hasNext()) {
                String itemId = itemIds.next();
                String jItem = jCart.optString(itemId, "");
                if (!TextUtils.isEmpty(jItem)) {
                    SubItem subItemSaved = gson.fromJson(jItem, SubItem.class);
                    if (subItemSaved.getSubItemQuantitySelected() > 0) {
                        subItemsInCart.add(subItemSaved);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subItemsInCart;
    }


    public void saveCartList(ArrayList<SubItem> subItems) {
        try {
            JSONObject jCart = new JSONObject();
            Gson gson = new Gson();
            for (SubItem subItem : subItems) {
                if (subItem.getSubItemQuantitySelected() > 0) {
                    try {
                        jCart.put(String.valueOf(subItem.getSubItemId()), gson.toJson(subItem, SubItem.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            int type = getAppType();
            if (type == AppConstant.ApplicationType.FRESH) {
                Prefs.with(this).save(Constants.SP_FRESH_CART, jCart.toString());
            } else if (type == AppConstant.ApplicationType.GROCERY) {
                Prefs.with(this).save(Constants.SP_GROCERY_CART, jCart.toString());
            } else {
                Prefs.with(this).save(Constants.SP_MEAL_CART, jCart.toString());
            }
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
                if (getFreshSearchFragment() != null) {
                    getFreshSearchFragment().searchFreshItems(s.toString());
                } else if (getTopFragment() instanceof MenusFragment) {
                    getMenusFragment().getMenusRestaurantAdapter().searchVendorsFromTopBar(s.toString());
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
                }
            });
        }
    }

    public void setDeliveryAddressViewVisibility(int visibility) {
        if (deliveryAddressView != null) {
            deliveryAddressView.llLocation.setVisibility(visibility);
        }
    }

    /**
     * Edited by Parminder Singh on 1/30/17 at 12:25 PM
     **/


    private State mCurrentState;
    public TextView tvCollapRestaurantName;
    public TextView tvCollapRestaurantReviews;
    private RelativeLayout rlCollapseDetails;
    private LinearLayout llCartContainer;
    private LinearLayout llToolbarLayout;
    public ImageView ivCollapseRestImage;
    private AppBarLayout.OnOffsetChangedListener collapseBarController = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
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


                topBar.getIvSearch().getBackground().setAlpha(calculatedAlpha);

                llCartContainer.getBackground().setAlpha(calculatedAlpha);

                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255 - calculatedAlpha));


                tvCollapRestaurantReviews.setTextColor(tvCollapRestaurantReviews.getTextColors().withAlpha(255 - calculatedAlpha));
                ivCollapseRestImage.getBackground().setAlpha(255 - calculatedAlpha);

                topBar.imageViewBack.getDrawable().setAlpha(calculatedAlpha);


                topBar.title.setTextColor(topBar.title.getTextColors().withAlpha(calculatedAlpha));


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
        tvCollapRestaurantReviews = (TextView) findViewById(R.id.tv_rest_reviews);
        rlCollapseDetails = (RelativeLayout) findViewById(R.id.layout_rest_details);
        llCartContainer = (LinearLayout) findViewById(R.id.llCartContainer);
        //llToolbarLayout = (R) findViewById(R.id.topRl);
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
                llCartContainer.getBackground().setAlpha(0);
                topBar.getIvSearch().getBackground().setAlpha(0);

                //back Button
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                topBar.imageViewBack.getDrawable().setAlpha(255);


                //Restaurant Details
                tvCollapRestaurantName.setTextColor(tvCollapRestaurantName.getTextColors().withAlpha(255));
                tvCollapRestaurantReviews.setTextColor(tvCollapRestaurantReviews.getTextColors().withAlpha(255));
                break;
            case COLLAPSED:
                //Toolbar
                //  toolbar.getBackground().setAlpha(255);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


                //Restaurant Details
                rlCollapseDetails.setVisibility(View.GONE);


                //Cart and Search Button
                llCartContainer.getBackground().setAlpha(255);
                topBar.getIvSearch().getBackground().setAlpha(255);

                //back Button
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.lightBlackTxtColor), PorterDuff.Mode.SRC_ATOP);
                topBar.imageViewBack.getDrawable().setAlpha(255);

                //Title
                topBar.title.setTextColor(topBar.title.getTextColors().withAlpha(255));
                break;
            case IDLE:
                topBar.imageViewBack.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.lightBlackTxtColor), PorterDuff.Mode.SRC_ATOP);
                rlCollapseDetails.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                break;
        }
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

}
