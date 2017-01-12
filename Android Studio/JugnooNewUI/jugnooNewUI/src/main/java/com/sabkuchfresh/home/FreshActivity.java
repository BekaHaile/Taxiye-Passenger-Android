package com.sabkuchfresh.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jugnoo.pay.activities.PaySDKUtils;
import com.jugnoo.pay.models.MessageRequest;
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
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.GroceryFragment;
import com.sabkuchfresh.fragments.MealAddonItemsFragment;
import com.sabkuchfresh.fragments.MealFragment;
import com.sabkuchfresh.fragments.MenusCheckoutMergedFragment;
import com.sabkuchfresh.fragments.MenusFilterCuisinesFragment;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.fragments.MenusFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SubItemCompare;
import com.sabkuchfresh.retrofit.model.SubItemComparePrice;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceRev;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
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
import product.clicklabs.jugnoo.support.SupportActivity;
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

    private RelativeLayout relativeLayoutCart, relativeLayoutCheckoutBar, relativeLayoutSort, relativeLayoutCartNew, relativeLayoutLeft;
    private LinearLayout linearLayoutCheckout, linearLayoutCheckoutContainer;
    private TextView textViewTotalPrice, textViewCheckout, textViewMinOrder, textViewCartItemsCountNew, textViewCartItemsCount;
    private ImageView imageViewCartNew;

    private MenuBar menuBar;
    private TopBar topBar;
    private FABViewTest fabViewTest;
    private TransactionUtils transactionUtils;

    private ProductsResponse productsResponse;
    private UserCheckoutResponse userCheckoutResponse;

    private String selectedAddress = "";
    private LatLng selectedLatLng;

    private int selectedAddressId = 0;
    private String selectedAddressType = "";
    private String splInstr = "";
    private Slot slotSelected, slotToSelect;
    private PaymentOption paymentOption;

    private List<DeliveryAddress> deliveryAddresses;
    public ArrayList<SubItem> subItemsInCart;

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

    private AppBarLayout appBarLayout;
    private RelativeLayout searchLayout;
    private Toolbar toolbar;

    public void openNotification() {
        menuBar.getMenuAdapter().onClickAction(MenuInfoTags.INBOX.getTag());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_fresh);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");

            appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);


//            if(Data.userData.getShowHomeScreen() == 1)
//            {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this,
//                                Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
//                                getIntent().getData(), getSelectedLatLng(), true);
//                    }
//                }, 500);
//                Data.userData.setShowHomeScreen(0);
//            }

            Data.currentActivity = FreshActivity.class.getName();
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            new ASSL(this, drawerLayout, 1134, 720, false);
            scale = getResources().getDisplayMetrics().density;

            relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
            searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
            ((TextView) findViewById(R.id.textViewSearch)).setTypeface(Fonts.mavenMedium(this));
            searchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchItem();
                }
            });

            try {
                mBus = ((MyApplication) getApplication()).getBus();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0){
                    Data.latitude = Data.loginLatitude;
                    Data.longitude = Data.loginLongitude;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            resetAddressFields();

            if(getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE)){
                setSelectedLatLng(new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
                        getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude)));
            }

            relativeLayoutCart = (RelativeLayout) findViewById(R.id.relativeLayoutCart);
            linearLayoutCheckoutContainer = (LinearLayout) findViewById(R.id.linearLayoutCheckoutContainer);
            relativeLayoutCheckoutBar = (RelativeLayout) findViewById(R.id.relativeLayoutCheckoutBar);
            relativeLayoutCartNew = (RelativeLayout) findViewById(R.id.relativeLayoutCartNew);
            linearLayoutCheckout = (LinearLayout) findViewById(R.id.linearLayoutCheckout);
            relativeLayoutSort = (RelativeLayout) findViewById(R.id.relativeLayoutSort);
            relativeLayoutLeft = (RelativeLayout) findViewById(R.id.relativeLayoutLeft);

            imageViewCartNew = (ImageView) findViewById(R.id.imageViewCartNew);

            textViewCartItemsCountNew = (TextView) findViewById(R.id.textViewCartItemsCountNew);
            textViewCartItemsCountNew.setTypeface(Fonts.mavenRegular(this));

            textViewCartItemsCount = (TextView) findViewById(R.id.textViewCartItemsCount);
            textViewCartItemsCount.setTypeface(Fonts.mavenRegular(this));
            textViewCartItemsCount.setMinWidth((int)(45f * ASSL.Xscale()));

            textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
            textViewTotalPrice.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

            textViewCheckout = (TextView) findViewById(R.id.textViewCheckout);
            textViewCheckout.setTypeface(Fonts.mavenRegular(this));
            textViewMinOrder = (TextView) findViewById(R.id.textViewMinOrder);
            textViewMinOrder.setTypeface(Fonts.mavenRegular(this));

            topView = (View) findViewById(R.id.topBarMain);

            menuBar = new MenuBar(this, drawerLayout);
            topBar = new TopBar(this, drawerLayout);
            fabViewTest = new FABViewTest(this, findViewById(R.id.relativeLayoutFABTest));

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(verticalOffset == 0){
                        if(fabViewTest.getMenuLabelsRightTest().getVisibility() == View.GONE) {
                            fabViewTest.getMenuLabelsRightTest().setVisibility(View.VISIBLE);
                            fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_in_slow_fab));
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fabViewTest.getMenuLabelsRightTest().setVisibility(View.VISIBLE);
                                    fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_in_slow_fab));
                                }
                            }, 1000);*/
                        }
                    } else{
                        if(verticalOffset < -100) {
                            if(fabViewTest.getMenuLabelsRightTest().getVisibility() == View.VISIBLE) {
                                fabViewTest.getMenuLabelsRightTest().setVisibility(View.GONE);
                                fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_out_slow_fab));
                                /*new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fabViewTest.getMenuLabelsRightTest().setVisibility(View.GONE);
                                        fabViewTest.getMenuLabelsRightTest().startAnimation(AnimationUtils.loadAnimation(FreshActivity.this, R.anim.fade_out_slow_fab));
                                    }
                                }, 1000);*/
                            }
                        }
                    }
                }
            });


            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeyboard(FreshActivity.this, textViewCartItemsCountNew);
                    FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.CHECKOUT_SCREEN);
                    FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.REVIEW_CART, getProduct());

                    int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                    if (updateCartValuesGetTotalPrice().second > 0) {
                        if (isMealAddonItemsAvailable()) {
                            addMealAddonItemsFragment();
                        } else {
                            openCart(appType);

                            if (appType == AppConstant.ApplicationType.MEALS) {
                                MyApplication.getInstance().logEvent(FirebaseEvents.M_CART, null);
                            } else {
                                if ((getFreshSearchFragment() != null) && (!getFreshSearchFragment().isHidden())) {
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


            relativeLayoutCheckoutBar.setOnClickListener(onClickListener);
            linearLayoutCheckout.setOnClickListener(onClickListener);
            relativeLayoutCartNew.setOnClickListener(onClickListener);

            relativeLayoutCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideSoftKeyboard(FreshActivity.this, textViewCartItemsCount);
                        FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CART, FlurryEventNames.BOTTOM_ICON);
                        if(!canOrder && Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType) == AppConstant.ApplicationType.MEALS)
                            return;

                        if(updateCartValuesGetTotalPrice().second > 0) {
                            int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                            openCart(appType);
                        } else {
                            Utils.showToast(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty));
                        }

                        if((getFreshSearchFragment() != null) && (!getFreshSearchFragment().isHidden())){
                            int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                            if(appType == AppConstant.ApplicationType.GROCERY){
                                MyApplication.getInstance().logEvent(FirebaseEvents.G_SEARCH_CART, null);
                            } else {
                                MyApplication.getInstance().logEvent(FirebaseEvents.F_SEARCH_CART, null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            relativeLayoutSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FreshFragment fragment = getFreshFragment();
                    MealFragment mealFragment = getMealFragment();
                    GroceryFragment groceryFragment = getGroceryFragment();
                    VendorMenuFragment vendorMenuFragment = getVendorMenuFragment();

                    if (fragment != null && !fragment.isHidden()) {
                        fragment.getFreshSortingDialog().showSorting();
                    } else if(mealFragment != null && !mealFragment.isHidden()) {
                        mealFragment.getFreshSortingDialog().showSorting();
                    } else if(groceryFragment != null && !groceryFragment.isHidden()) {
                        groceryFragment.getFreshSortingDialog().showSorting();
                    } else if(vendorMenuFragment != null && !vendorMenuFragment.isHidden()) {
                        vendorMenuFragment.getFreshSortingDialog().showSorting();
                    }
                }
            });




            try {
                float marginBottom = 77f;
                String lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);
                if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
                    addMealFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);
                } else if(lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                    openCart();
                    addGroceryFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.GROCERY);
                    lastClientId = Config.getGroceryClientId();
                } else if(lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                    openCart();
                    addMenusFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);
                    lastClientId = Config.getMenusClientId();
                    marginBottom = 33f;
                } else {
                    openCart();
                    addFreshFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
                    lastClientId = Config.getFreshClientId();
                }
                int dpAsPixels = (int) (marginBottom*scale + 0.5f);
                fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, lastClientId);
            } catch (Exception e) {
                e.printStackTrace();
                addFreshFragment();
            }


            // Register to receive messages.
            // We are registering an observer (mMessageReceiver) to receive Intents
            // with actions named "custom-event-name".
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



    public void intentToShareActivity(){
        Intent intent = new Intent(FreshActivity.this, ShareActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private boolean isAvailable() {
        boolean flag = false;
//        if(getProductsResponse() != null
//                && getProductsResponse().getCategories() != null) {
//            for (Category category : getProductsResponse().getCategories()) {
//                for (SubItem subItem : category.getSubItems()) {
//                    if (subItem.getGroupId() != category.getCurrentGroupId() && subItem.getSubItemQuantitySelected()>0) {
//                        return true;
//                    }
//                }
//            }
//        }
        return flag;
    }

    public void openCart() {
                try{
                    if(getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE).getBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, false)){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(productsResponse != null && productsResponse.getCategories() != null) {
                                        updateCartFromSP();
                                        relativeLayoutCartNew.performClick();
                                    } else {
                                        updateCart = true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 400);

                    }
                } catch (Exception e){
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
                        if(flag == -1) {
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
									relativeLayoutCartNew.performClick();
								} else {
									Bundle bundle = new Bundle();
									bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
									MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getFreshClientId(), null,
											getCurrentPlaceLatLng(), bundle);
								}
							} else if (type == 1) {
								intentToShareActivity();
							} else if(type == 2){
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                String lastClientId = Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getGroceryClientId());
                                if (lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                                    updateCartFromSP();
                                    relativeLayoutCartNew.performClick();
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getGroceryClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            } else if(type == 3){
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                String lastClientId = Prefs.with(FreshActivity.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMenusClientId());
                                if (lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                                    updateCartFromSP();
                                    relativeLayoutCartNew.performClick();
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
                                    MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getMenusClientId(), null,
                                            getCurrentPlaceLatLng(), bundle);
                                }
                            } else if(type == 10){
                                setRefreshCart(true);
                            }
						} else {
							if(flag == PushFlags.DISPLAY_MESSAGE.getOrdinal()){
								Data.getDeepLinkIndexFromIntent(FreshActivity.this, intent);
								openPushDialog();
							} else if(PushFlags.INITIATE_PAYTM_RECHARGE.getOrdinal() == flag) {
                                String message = intent.getStringExtra(Constants.KEY_MESSAGE);
                                Data.userData.setPaytmRechargeInfo(JSONParser.parsePaytmRechargeInfo(new JSONObject(message)));
                                openPaytmRechargeDialog();
                            }
                            else if(PushFlags.STATUS_CHANGED.getOrdinal() == flag)
                            {
                                String clientId = intent.getStringExtra(Constants.KEY_CLIENT_ID);
                                Fragment fragment = getTopFragment();
                                    if(fragment instanceof MealFragment && FreshActivity.this.hasWindowFocus())
                                    {
                                        ((MealFragment)fragment).getAllProducts(true, getSelectedLatLng());
                                    }
                                    else
                                    {
                                        Intent intent1 = new Intent(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE);
                                        intent1.putExtra(Constants.KEY_FLAG, flag);
                                        LocalBroadcastManager.getInstance(FreshActivity.this).sendBroadcast(intent1);
                                    }
                            }
                            else if(PushFlags.MENUS_STATUS.getOrdinal() == flag)
                            {
                                Fragment fragment = getTopFragment();

                                if(fragment instanceof MenusFragment && FreshActivity.this.hasWindowFocus())
                                {
                                    ((MenusFragment)fragment).getAllMenus(true, getSelectedLatLng());
                                }
                                else
                                {
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

            if(Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getAutosClientId())){
                HomeActivity.homeSwitcher = true;
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getAutosClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if(Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getMealsClientId())){
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getMealsClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if(Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getGroceryClientId())){
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getGroceryClientId(), null,
                        getCurrentPlaceLatLng(), null);
            } else if(Prefs.with(this).getString("home_switcher_client_id", "").equalsIgnoreCase(Config.getFreshClientId())){
                MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getFreshClientId(), null,
                        getCurrentPlaceLatLng(), null);
            }
            Prefs.with(this).save("home_switcher_client_id", "");


        if (!HomeActivity.checkIfUserDataNull(this)) {
            menuBar.setUserData();
            topBar.setUserData();


            fetchWalletBalance(this);

            if (locationFetcher == null) {
                locationFetcher = new LocationFetcher(this, 60000l);
            } else {
                locationFetcher.connect();
            }

            if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                    Data.userData.getIntegratedJugnooEnabled() == 1) {
                if((getFreshFragment() != null && !getFreshFragment().isHidden()) ||
                        (getMealFragment() != null && !getMealFragment().isHidden()) ||
                        (getGroceryFragment() != null && !getGroceryFragment().isHidden())
                        || (getMenusFragment() != null && !getMenusFragment().isHidden())) {
                    //fabViewTest.relativeLayoutFABTest.setVisibility(View.INVISIBLE);
                    //fabViewTest.setFABMenuDrawable();
                    //imageViewFabFake.setVisibility(View.VISIBLE);
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                    fabViewTest.setFABButtons();
                }
            } else{
                fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
                //imageViewFabFake.setVisibility(View.GONE);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resumeMethod() {
        menuBar.setUserData();
        topBar.setUserData();

        fetchWalletBalance(this);
    }

    public void toggle() {
        FreshFragment frag = getFreshFragment();
        MealFragment mealFragment = getMealFragment();
        if(frag != null && !frag.isHidden()) {
            addMealFragment(frag, true);
        } else if(mealFragment != null && !mealFragment.isHidden()) {
            addFreshFragment1(mealFragment, true);
        }
    }

    public FreshFragment getFreshFragment() {
        return (FreshFragment) getSupportFragmentManager().findFragmentByTag(FreshFragment.class.getName());
    }

    public MealFragment getMealFragment() {
        return (MealFragment) getSupportFragmentManager().findFragmentByTag(MealFragment.class.getName());
    }

    public GroceryFragment getGroceryFragment () {
        return (GroceryFragment) getSupportFragmentManager().findFragmentByTag(GroceryFragment.class.getName());
    }

    public MenusFragment getMenusFragment () {
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

    public TopBar getTopBar() {
        return topBar;
    }

    public void showBottomBar(boolean flag) {
        if(flag && (getFeedbackFragment() == null || !getFeedbackFragment().isVisible())) {
            relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
        }else {
            relativeLayoutCheckoutBar.setVisibility(View.GONE);
            textViewMinOrder.setVisibility(View.GONE);
        }
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
                            String categoryName = "",itemName = "";
                            double price = 0.0;
                            int qty = 0, itemId = 0;

                            qty = subItem.getSubItemQuantitySelected();
                            categoryName = category.getCategoryName();
                            itemId = subItem.getSubItemId();
                            itemName = subItem.getSubItemName();
                            price = subItem.getPrice();

                            Product product = new Product()
                                    .setCategory(categoryName)
                                    .setId(""+itemId)
                                    .setName(itemName)
                                    .setPrice(price)
                                    .setQuantity(qty);
//                                                                .setPosition(4);
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
        Pair<Double, Integer> pair;
        totalPrice = 0;
        totalQuantity = 0;
        try {
            if (getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        if (subItem.getSubItemQuantitySelected() > 0) {
                            totalQuantity++;
                            totalPrice = totalPrice + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
                        }
                    }
                }
                textViewTotalPrice.setText(String.format(getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(totalPrice)));
                if (totalQuantity > 0) {
                    textViewCartItemsCount.setVisibility(View.VISIBLE);
                    textViewCartItemsCountNew.setVisibility(View.VISIBLE);
                    if(drawerLayout.getDrawerLockMode(GravityCompat.START)== DrawerLayout.LOCK_MODE_UNLOCKED)
                    imageViewCartNew.setImageResource(R.drawable.ic_cart_fill);
                    String total = String.valueOf(totalQuantity);
                    textViewCartItemsCount.setText(total);
                    textViewCartItemsCountNew.setText(total);
                } else {
                    textViewCartItemsCount.setVisibility(View.GONE);
                    textViewCartItemsCountNew.setVisibility(View.GONE);
                    imageViewCartNew.setImageResource(R.drawable.ic_cart_empty);
                }
                if(getVendorMenuFragment() != null && getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                    if (getMenusCheckoutMergedFragment() == null && totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                    }
                    else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                }
                else if(getFreshFragment() != null) {
                    setMinOrderAmountText(getFreshFragment());
                }
                else if(getGroceryFragment() != null) {
                    setMinOrderAmountText(getGroceryFragment());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveCartToSP();
        if (totalQuantity > 0) {
        }
        pair = new Pair<>(totalPrice, totalQuantity);
        return pair;
    }

    public double getTotalPrice() {
        double totalAmount = 0;
        try {
            if (getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        if (subItem.getSubItemQuantitySelected() > 0) {
                            totalAmount = totalAmount + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalAmount;
    }

    private void setFabButtons(){
        if (Data.userData.getFreshEnabled() == 1) {
            fabViewTest.fabFreshTest.setVisibility(View.VISIBLE);
        } else {
            fabViewTest.fabFreshTest.setVisibility(View.GONE);
        }

        if (Data.userData.getMealsEnabled() == 1) {
            fabViewTest.fabMealsTest.setVisibility(View.VISIBLE);
        } else {
            fabViewTest.fabMealsTest.setVisibility(View.GONE);
        }

//        if (Data.userData.getDeliveryEnabled() == 1) {
//            fabViewTest.fabDeliveryTest.setVisibility(View.VISIBLE);
//        } else {
//            fabViewTest.fabDeliveryTest.setVisibility(View.GONE);
//        }
    }

    public RelativeLayout getSearchLayout() {
        return searchLayout;
    }

    public void fragmentUISetup(Fragment fragment) {
        try {
            int appType = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
            textViewMinOrder.setVisibility(View.GONE);

            topBar.title.setTypeface(Fonts.avenirNext(this));
            topBar.below_shadow.setVisibility(View.VISIBLE);
            relativeLayoutCartNew.setVisibility(View.GONE);
            linearLayoutCheckout.setVisibility(View.VISIBLE);
            topBar.textViewSkip.setVisibility(View.GONE);
            topBar.relativeLayoutLocality.setVisibility(View.GONE);
            topBar.textViewReset.setVisibility(View.GONE);

            RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) topBar.title.getLayoutParams();
            titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);

//        Utils.hideViewByScale(relativeLayoutCartRound);
//        relativeLayoutCartRound.hide();
            topView.setVisibility(View.VISIBLE);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            lp.height = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(lp);

            fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
            //imageViewFabFake.setVisibility(View.GONE);
            topBar.editTextDeliveryAddress.setVisibility(View.GONE);

            relativeLayoutLeft.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            resetToolbar();

            if (fragment instanceof FreshFragment) {
                resetToolbarWithScroll();
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.below_shadow.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				if(relativeLayoutCheckoutBar.getVisibility() != View.VISIBLE)
                    relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    //imageViewFabFake.setVisibility(View.VISIBLE);
                    float marginBottom = 85f;
                    int dpAsPixels = (int) (marginBottom*scale + 0.5f);
                    fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
					//fabViewTest.setFABMenuDrawable();
				}

				relativeLayoutCartNew.setVisibility(View.VISIBLE);
                relativeLayoutCart.setVisibility(View.GONE);
				linearLayoutCheckout.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.VISIBLE);
				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.fresh));
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                topBar.relativeLayoutLocality.setVisibility(View.VISIBLE);
                setRelativeLayoutLocalityClick();
                setMinOrderAmountText(fragment);
                setNoItemsView(fragment);
                searchLayout.setVisibility(View.VISIBLE);

            } else if(fragment instanceof MealFragment){
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				relativeLayoutCartNew.setVisibility(View.VISIBLE);
                relativeLayoutCart.setVisibility(View.GONE);
				linearLayoutCheckout.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.VISIBLE);

				if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    //imageViewFabFake.setVisibility(View.VISIBLE);
					fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
					//fabViewTest.setFABMenuDrawable();
				}

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.meals));
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                topBar.relativeLayoutLocality.setVisibility(View.VISIBLE);
                setRelativeLayoutLocalityClick();
                setNoItemsView(fragment);
			}
            else if (fragment instanceof GroceryFragment) {
                resetToolbarWithScroll();
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.below_shadow.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.GONE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                if(relativeLayoutCheckoutBar.getVisibility() != View.VISIBLE)
                    relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

                if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    //imageViewFabFake.setVisibility(View.VISIBLE);
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                    float marginBottom = 85f;
                    int dpAsPixels = (int) (marginBottom*scale + 0.5f);
                    fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
                    //fabViewTest.setFABMenuDrawable();
                }
                relativeLayoutCartNew.setVisibility(View.VISIBLE);
                relativeLayoutCart.setVisibility(View.GONE);
                linearLayoutCheckout.setVisibility(View.GONE);

                relativeLayoutSort.setVisibility(View.VISIBLE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.grocery));
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                topBar.relativeLayoutLocality.setVisibility(View.VISIBLE);
                setRelativeLayoutLocalityClick();
                setMinOrderAmountText(fragment);
                setNoItemsView(fragment);
                searchLayout.setVisibility(View.VISIBLE);

            } else if (fragment instanceof MenusFragment) {
                topBar.imageViewMenu.setVisibility(View.VISIBLE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.GONE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);
                if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                }


                relativeLayoutCartNew.setVisibility(View.VISIBLE);
                relativeLayoutCart.setVisibility(View.GONE);
                linearLayoutCheckout.setVisibility(View.GONE);

                relativeLayoutSort.setVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.menus));
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewMenu.getId());
                topBar.relativeLayoutLocality.setVisibility(View.VISIBLE);
                setRelativeLayoutLocalityClick();

            }
            else if (fragment instanceof VendorMenuFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.below_shadow.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                if(relativeLayoutCheckoutBar.getVisibility() != View.VISIBLE)
                    relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);


                relativeLayoutCartNew.setVisibility(View.VISIBLE);
                relativeLayoutCart.setVisibility(View.GONE);
                linearLayoutCheckout.setVisibility(View.GONE);
                relativeLayoutSort.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(vendorOpened.getName());
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
                if(getVendorOpened() != null && getVendorOpened().getMinimumOrderAmount() != null) {
                    if (totalPrice < getVendorOpened().getMinimumOrderAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                    textViewMinOrder.setText(getString(R.string.minimum_order) + " "
                            + getString(R.string.rupees_value_format_without_space, Utils.getMoneyDecimalFormatWithoutFloat().format(getVendorOpened().getMinimumOrderAmount())));
                }
                resetToolbarWithScroll();
                searchLayout.setVisibility(View.VISIBLE);
            }
            else if (fragment instanceof MenusFilterFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);


                relativeLayoutCartNew.setVisibility(View.VISIBLE);
                linearLayoutCheckout.setVisibility(View.GONE);
                relativeLayoutSort.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.filters);
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
                topBar.textViewReset.setVisibility(View.VISIBLE);
            }
            else if (fragment instanceof MenusFilterCuisinesFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);


                relativeLayoutCartNew.setVisibility(View.VISIBLE);
                linearLayoutCheckout.setVisibility(View.GONE);
                relativeLayoutSort.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(R.string.select_cuisines);
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, topBar.imageViewBack.getId());
                topBar.textViewReset.setVisibility(View.GONE);
            }
            else if (fragment instanceof FreshCheckoutMergedFragment || fragment instanceof MenusCheckoutMergedFragment) {
                resetToolbar();
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
                relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.checkout));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof AddAddressMapFragment || fragment instanceof AddToAddressBookFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
                relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
                if(fragment instanceof AddToAddressBookFragment){
                    topBar.title.setText(getResources().getString(R.string.confirm_address));
                } else if(fragment instanceof AddAddressMapFragment){
                    topBar.title.setText(getResources().getString(R.string.choose_your_address));
                } else {
                    topBar.title.setText(getResources().getString(R.string.address));
                }
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if(fragment instanceof DeliveryAddressesFragment){
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                topBar.below_shadow.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);

                relativeLayoutSort.setVisibility(View.GONE);
                relativeLayoutCart.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.GONE);
                topBar.title.setText("Type Delivery Address");
                topBar.editTextDeliveryAddress.setVisibility(View.VISIBLE);
                topBar.editTextDeliveryAddress.requestFocus();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }
            else if (fragment instanceof FreshSearchFragment) {
				topView.setVisibility(View.GONE);
                CoordinatorLayout.LayoutParams lp1 = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
                lp.height = 0;
                appBarLayout.setLayoutParams(lp1);
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				relativeLayoutSort.setVisibility(View.GONE);
                relativeLayoutCart.setVisibility(View.VISIBLE);

                try {
                    if(appType == AppConstant.ApplicationType.MENUS && getVendorMenuFragment() != null
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

                topBar.title.setVisibility(View.VISIBLE);
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                setMinOrderAmountText(fragment);

			} else if(fragment instanceof FeedbackFragment) {
                topBar.below_shadow.setVisibility(View.GONE);
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);
				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                resetToolbar();
			}
            else if(fragment instanceof OrderStatusActivity) {
                topBar.below_shadow.setVisibility(View.VISIBLE);
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                textViewCheckout.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);
                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                resetToolbar();
            }
            else if (fragment instanceof MealAddonItemsFragment) {
                topBar.imageViewMenu.setVisibility(View.GONE);
                topBar.relativeLayoutNotification.setVisibility(View.GONE);
                topBar.imageViewBack.setVisibility(View.VISIBLE);
                topBar.imageViewDelete.setVisibility(View.GONE);
                relativeLayoutCheckoutBar.setVisibility(View.GONE);

                relativeLayoutSort.setVisibility(View.GONE);
                relativeLayoutCart.setVisibility(View.VISIBLE);

                topBar.title.setVisibility(View.VISIBLE);
                topBar.title.setText(getResources().getString(R.string.pick_addons));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                topBar.textViewSkip.setVisibility(View.VISIBLE);

            }

            topBar.title.setLayoutParams(titleLayoutParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMinOrderAmountText(Fragment fragment){
        try {
            if(getFreshFragment() != null || getGroceryFragment() != null || (getFreshSearchFragment() != null && getVendorMenuFragment() == null)) {
                if (getProductsResponse() != null && totalQuantity > 0
                        && (fragment instanceof FreshFragment || fragment instanceof GroceryFragment || fragment instanceof FreshSearchFragment)
                        && (relativeLayoutCheckoutBar.getVisibility() == View.VISIBLE)) {
                    if(Data.userData.isSubscriptionActive() && !TextUtils.isEmpty(getProductsResponse().getSubscriptionMessage())){
                        textViewMinOrder.setVisibility(View.VISIBLE);
                        textViewMinOrder.setText(getProductsResponse().getSubscriptionMessage());
                    }
                    else if(totalPrice < getProductsResponse().getDeliveryInfo().getMinAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                        double leftAmount = getProductsResponse().getDeliveryInfo().getMinAmount() - totalPrice;
                        textViewMinOrder.setText(getString(R.string.fresh_min_order_value_format,
                                Utils.getMoneyDecimalFormatWithoutFloat().format(leftAmount)));
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                } else {
                    textViewMinOrder.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setNoItemsView(Fragment fragment){
        try {
            if(fragment instanceof FreshFragment || fragment instanceof GroceryFragment){
                    if(getProductsResponse() != null && getProductsResponse().getCategories().size() == 0){
                        getTopBar().below_shadow.setVisibility(View.VISIBLE);
                        showBottomBar(false);
                    }
            }
            else if(fragment instanceof MealFragment){
                if(((MealFragment)fragment).getMealAdapter().getItemCount()>0) {
                    showBottomBar(true);
                } else {
                    showBottomBar(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void searchItem() {
        getTransactionUtils().openSearchFragment(FreshActivity.this, relativeLayoutContainer);
    }

    private FreshSearchFragment getFreshSearchFragment() {
        return (FreshSearchFragment) getSupportFragmentManager().findFragmentByTag(FreshSearchFragment.class.getName());
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
                            ((FreshCheckoutMergedFragment)frag).deleteCart();
                        } else if(getMenusCheckoutMergedFragment() != null){
                            getMenusCheckoutMergedFragment().deleteCart();
                        } else {
                            frag = getMealAddonItemsFragment();
                            if(frag != null){
                                ((MealAddonItemsFragment)frag).deleteCart();
                            }
                        }

                        if(type == AppConstant.ApplicationType.FRESH) {
                            clearCart();
                        } else if(type == AppConstant.ApplicationType.GROCERY){
                            clearGroceryCart();
                        } else if(type == AppConstant.ApplicationType.MENUS){
                            clearMenusCart();
                        } else{
                            clearMealCart();
                        }
                        if(type == AppConstant.ApplicationType.MEALS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        } else if(type == AppConstant.ApplicationType.GROCERY){
                            MyApplication.getInstance().logEvent(FirebaseEvents.G_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        } else if(type == AppConstant.ApplicationType.MENUS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        }else{
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(type == AppConstant.ApplicationType.MEALS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        } else if(type == AppConstant.ApplicationType.GROCERY){
                            MyApplication.getInstance().logEvent(FirebaseEvents.G_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        } else if(type == AppConstant.ApplicationType.MENUS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        } else{
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        }
                    }
                }, true, false);
    }


    public void updateMenu() {
        menuBar.setUserData();
    }

    private void resetAddressFields(){
        selectedAddress = "";
        selectedAddressId = 0;
        selectedAddressType = "";
        selectedLatLng = new LatLng(Data.latitude, Data.longitude);
    }

    public void orderComplete() {
        clearCart();
        for (Category category : productsResponse.getCategories()) {
            for (SubItem subItem : category.getSubItems()) {
                subItem.setSubItemQuantitySelected(0);
            }
        }
//        resetAddressFields();
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
//                FreshFragment freshFrag = getFreshFragment();
//                MealFragment mealsFrag = getMealFragment();
//                GroceryFragment groceryFrag = getGroceryFragment();
//                MenusFragment menusFrag = getMenusFragment();
//                if (freshFrag != null) {
//                    freshFrag.getAllProducts(true, getSelectedLatLng());
//                } else if(mealsFrag != null) {
//                    mealsFrag.getAllProducts(true, getSelectedLatLng());
//                } else if(groceryFrag != null) {
//                    groceryFrag.getAllProducts(true, getSelectedLatLng());
//                } else if(menusFrag != null) {
//                    menusFrag.getAllMenus(true, getSelectedLatLng());
//                }
                setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));
            }
        }, 1000);

    }

    private void addFreshFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FreshFragment(),
                        FreshFragment.class.getName())
                .addToBackStack(FreshFragment.class.getName())
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

    public void openSupport() {
        startActivity(new Intent(this, SupportActivity.class));
    }

    public void openMapAddress(Bundle bundle) {
        FlurryEventLogger.event(Address_Screen, SCREEN_TRANSITION, ADD_NEW_ADDRESS);
        getTransactionUtils().openMapFragment(FreshActivity.this, relativeLayoutContainer, bundle);
    }

    public void openAddToAddressBook(Bundle bundle) {
        getTransactionUtils().openAddToAddressFragment(FreshActivity.this, relativeLayoutContainer, bundle);
    }



    public void performBackPressed() {

        if(getFeedbackFragment() != null && getSupportFragmentManager().getBackStackEntryCount() == 2 && !getFeedbackFragment().isUpbuttonClicked) {
            finish();
        }
        Utils.hideSoftKeyboard(this, textViewCartItemsCountNew);
        final AddToAddressBookFragment fragment = getAddToAddressBookFragment();
        if(fragment != null && fragment.locationEdited){
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
        }
        else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else if(locationSearchShown) {
            locationSearchShown = false;
            try {
                mBus.post(new AddressSearch(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if((getSupportFragmentManager().getBackStackEntryCount() == 2 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() != null)){
                FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.HOME_SCREEN);
            }

            if((getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 4 && getFreshSearchFragment() != null)){
                FlurryEventLogger.event(FlurryEventNames.CHECKOUT, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.REVIEW_CART_SCREEN);
            }
            if(getFreshSearchFragment() != null){
                getFreshSearchFragment().clearArrays();
            }

            super.onBackPressed();
        }
    }




    public static final long FETCH_WALLET_BALANCE_REFRESH_TIME = 5 * 60 * 1000;
    private ApiFetchWalletBalance apiFetchWalletBalance = null;
    private void fetchWalletBalance(final Activity activity) {
        try {
            if(apiFetchWalletBalance == null){
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
            if(lastCallDiff >= FETCH_WALLET_BALANCE_REFRESH_TIME) {
                apiFetchWalletBalance.getBalance(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUserData() {
        try {
            menuBar.setUserData();
            topBar.setUserData();

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

    public void setProductsResponse(ProductsResponse productsResponse) {
        this.productsResponse = productsResponse;
        mContactNo = productsResponse.getSupportContact();
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
        if(paymentOption == null){
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
        saveCartToSP();
        Log.e(TAG, "cart saved=" + Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));

        if (locationFetcher != null) {
            locationFetcher.destroy();
        }

    }

    public void saveCartToSP() {
        try {
            JSONObject jCart = new JSONObject();
            if (getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        if (subItem.getSubItemQuantitySelected() > 0) {
                            try {
                                jCart.put(String.valueOf(subItem.getSubItemId()), (int) subItem.getSubItemQuantitySelected());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            int type = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
            if(type == AppConstant.ApplicationType.FRESH) {
                Prefs.with(this).save(Constants.SP_FRESH_CART, jCart.toString());
            } else if(type == AppConstant.ApplicationType.GROCERY){
                Prefs.with(this).save(Constants.SP_GROCERY_CART, jCart.toString());
            } else if(type == AppConstant.ApplicationType.MENUS){
                if(getVendorOpened() != null){
                    JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
                    if(getVendorOpened().getRestaurantId().equals(jsonSavedCart.optInt(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId()))){
                        if(jCart.length() > 0) {
                            jCart.put(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId());
                            jCart.put(Constants.KEY_RESTAURANT_NAME, getVendorOpened().getName());
                        }
                        Prefs.with(this).save(Constants.SP_MENUS_CART, jCart.toString());
                    }
                }
            } else{
                Prefs.with(this).save(Constants.SP_MEAL_CART, jCart.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCartFromSP() {
        try {
            JSONObject jCart;
            int type = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
            if(type == AppConstant.ApplicationType.FRESH) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
            } else if(type == AppConstant.ApplicationType.GROCERY){
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT));
            } else if(type == AppConstant.ApplicationType.MENUS){
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
            } else{
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT));
            }
            if (getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                boolean cartUpdated = false;
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        subItem.setSubItemQuantitySelected(0);
                        try {
                            int savedQuant = jCart.optInt(String.valueOf(subItem.getSubItemId()),
                                    (int) subItem.getSubItemQuantitySelected());
                            if(subItem.getStock() < savedQuant){
                                savedQuant = subItem.getStock();
                                cartUpdated = true;
                            }
                            subItem.setSubItemQuantitySelected(savedQuant);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(cartUpdated) {
                    saveCartToSP();
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
            switch(event.postion){
				case 0:
					for (Category category : productsResponse.getCategories()) {
						Collections.sort(category.getSubItems(), new SubItemCompare());
					}
                    gaEvents("", FlurryEventNames.SORT, FlurryEventNames.A_Z);
					try {
						mBus.post(new UpdateMainList(true));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 1:
					for (Category category : productsResponse.getCategories()) {
						Collections.sort(category.getSubItems(), new Comparator<SubItem>() {
							@Override
							public int compare(SubItem p1, SubItem p2) {
								return p1.getPriorityId()- p2.getPriorityId();
							}

						});
	//                    Collections.sort(category.getSubItems(), new SubItemComparePriority());
					}
                    gaEvents("", FlurryEventNames.SORT, FlurryEventNames.POPULARITY);
					try {
						mBus.post(new UpdateMainList(true));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					for (Category category : productsResponse.getCategories()) {
						Collections.sort(category.getSubItems(), new SubItemComparePrice());
					}
                    gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
					try {
						mBus.post(new UpdateMainList(true));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 3:
					for (Category category : productsResponse.getCategories()) {
						Collections.sort(category.getSubItems(), new SubItemComparePriceRev());
					}
                    gaEvents("", FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
					try {
						mBus.post(new UpdateMainList(true));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					// should not happened
					break;
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


    public void addFreshFragment1(Fragment fragment, boolean swipeFlag) {

        final FragmentManager fragmentManager =  getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.executePendingTransactions();


        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(swipeFlag) {
            transaction.setCustomAnimations(R.anim.grow_from_middle, R.anim.hold, R.anim.hold, R.anim.shrink_to_middle);
        } else {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out);
        }
        transaction.replace(R.id.relativeLayoutContainer, new FreshFragment(), FreshFragment.class.getName());
        transaction.addToBackStack(FreshFragment.class.getName());
        transaction.commitAllowingStateLoss();

    }

    public void addMealFragment(Fragment fragment, boolean swipeFlag) {

        final FragmentManager fragmentManager =  getSupportFragmentManager();

        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.executePendingTransactions();


        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(swipeFlag) {
            transaction.setCustomAnimations(R.anim.grow_from_middle, R.anim.hold, R.anim.hold, R.anim.shrink_to_middle);
        } else {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out);
        }
        transaction.replace(R.id.relativeLayoutContainer, new MealFragment(), MealFragment.class.getName());
        transaction.addToBackStack(MealFragment.class.getName());
        transaction.commitAllowingStateLoss();


    }

    public MenuBar getMenuBar(){
        return menuBar;
    }



    private DeepLinkAction deepLinkAction = new DeepLinkAction();
    private PushDialog pushDialog;
    private void openPushDialog(){
        dismissPushDialog(false);
        PushDialog dialog = new PushDialog(FreshActivity.this, new PushDialog.Callback() {
            @Override
            public void onButtonClicked(int deepIndex, String url) {
                if("".equalsIgnoreCase(url)) {
                    Data.deepLinkIndex = deepIndex;
                    deepLinkAction.openDeepLink(menuBar);
                } else{
                    Utils.openUrl(FreshActivity.this, url);
                }
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


    private PaytmRechargeDialog paytmRechargeDialog;
    private void openPaytmRechargeDialog(){
        try {
            if(Data.userData.getPaytmRechargeInfo() != null) {
                if (paytmRechargeDialog != null
                        && paytmRechargeDialog.getDialog() != null
                        && paytmRechargeDialog.getDialog().isShowing()) {
                    paytmRechargeDialog.updateDialogDataAndContent(Data.userData.getPaytmRechargeInfo().getTransferId(),
                            Data.userData.getPaytmRechargeInfo().getTransferSenderName(),
                            Data.userData.getPaytmRechargeInfo().getTransferPhone(),
                            Data.userData.getPaytmRechargeInfo().getTransferAmount());
                } else{
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
                                         final boolean editThisAddress, final int placeRequestCode){
        if(apiAddHomeWorkAddress == null){
            apiAddHomeWorkAddress = new ApiAddHomeWorkAddress(this, new ApiAddHomeWorkAddress.Callback() {
                @Override
                public void onSuccess(SearchResult searchResult, String strResult, boolean addressDeleted) {
                    try {
                        Fragment deliveryAddressesFragment = getDeliveryAddressesFragment();
                        if(deliveryAddressesFragment != null) {
							getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
						} else {
							getSupportFragmentManager().popBackStack(AddAddressMapFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                        if(!addressDeleted) {
                            setSelectedAddressId(searchResult.getId());
                            saveOfferingLastAddress(appType);
                        } else{
                            setSelectedAddress("");
                            setSelectedLatLng(null);
                            setSelectedAddressId(0);
                            setSelectedAddressType("");

                            setRefreshCart(true);
                            saveOfferingLastAddress(appType);
                            setLocalityAddressFirstTime(Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType));

                            try {
                                if(getUserCheckoutResponse() != null){
									if(getUserCheckoutResponse().getCheckoutData().getLastAddressId().equals(searchResult.getId())){
										getUserCheckoutResponse().getCheckoutData().setLastAddressId(0);
										getUserCheckoutResponse().getCheckoutData().setLastAddressType("");
									}
								}
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if(getFreshCheckoutMergedFragment() != null) {
                                getFreshCheckoutMergedFragment().updateAddressView();
                            } else if(getMenusCheckoutMergedFragment() != null){
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
    public int getPlaceRequestCode(){
        return placeRequestCode;
    }
    public void setPlaceRequestCode(int placeRequestCode){
        this.placeRequestCode = placeRequestCode;
    }

    private SearchResult searchResult;
    public SearchResult getSearchResult(){
        return searchResult;
    }
    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    private boolean editThisAddress;
    public boolean isEditThisAddress() {
        return editThisAddress;
    }
    public void setEditThisAddress(boolean editThisAddress){
        this.editThisAddress = editThisAddress;
    }



    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public LatLng getSelectedLatLng() {
        if(selectedLatLng != null){
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
    public void setDeliveryAddressToEdit(DeliveryAddress deliveryAddressToEdit){
        this.deliveryAddressToEdit = deliveryAddressToEdit;
    }


    public void addMealAddonItemsFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new MealAddonItemsFragment(),
                        MealAddonItemsFragment.class.getName())
                .addToBackStack(MealAddonItemsFragment.class.getName())
                .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                .commitAllowingStateLoss();
    }

    public MealAddonItemsFragment getMealAddonItemsFragment() {
        return (MealAddonItemsFragment) getSupportFragmentManager().findFragmentByTag(MealAddonItemsFragment.class.getName());
    }

    public boolean isMealAddonItemsAvailable(){
        return Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType) == AppConstant.ApplicationType.MEALS
                && getProductsResponse().getCategories().size() > 1
                && getProductsResponse().getCategories().get(1).getSubItems() != null
                && getProductsResponse().getCategories().get(1).getSubItems().size() > 0;
    }

    public boolean checkForMinus(int position, SubItem subItem) {
        if(isMealAddonItemsAvailable()){
            boolean addOnAdded = false;
            boolean itemIsAddon = false;
            for(SubItem si : getProductsResponse().getCategories().get(1).getSubItems()){
                if(si.getSubItemQuantitySelected() > 0){
                    addOnAdded = true;
                }
                if(si.getSubItemId().equals(subItem.getSubItemId())){
                    itemIsAddon = true;
                }
            }
            int mealsQuantity = 0;
            for(SubItem si : getProductsResponse().getCategories().get(0).getSubItems()){
                mealsQuantity = mealsQuantity + si.getSubItemQuantitySelected();
            }
            return !(addOnAdded && !itemIsAddon && mealsQuantity == 1);
        } else {
            return true;
        }
    }

    public void clearMealsCartIfNoMainMeal(){
        try {
            Fragment frag = getFreshCheckoutMergedFragment();
            if (frag != null) {
                ((FreshCheckoutMergedFragment)frag).deleteCart();
            } else if(getMenusCheckoutMergedFragment() != null){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AddToAddressBookFragment getAddToAddressBookFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        if(fragmentTag.equalsIgnoreCase(AddToAddressBookFragment.class.getName())){
            return (AddToAddressBookFragment) fragmentManager.findFragmentByTag(fragmentTag);
        } else{
            return null;
        }
    }



    public Fragment getTopFragment(){
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openCart(int appType){
        if(appType == AppConstant.ApplicationType.MENUS && getVendorOpened() != null) {
            getTransactionUtils().openMenusCheckoutMergedFragment(FreshActivity.this, relativeLayoutContainer);
        } else {
            getTransactionUtils().openCheckoutMergedFragment(FreshActivity.this, relativeLayoutContainer);
        }
    }


    public void saveCheckoutData(boolean clearData){
        Gson gson = new Gson();
        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
        CheckoutSaveData checkoutSaveData;
        if(clearData){
            checkoutSaveData = new CheckoutSaveData();
        } else {
            checkoutSaveData = new CheckoutSaveData(getPaymentOption().getOrdinal(), getSpecialInst(), getSelectedAddress(),
                    getSelectedLatLng(), getSelectedAddressId(), getSelectedAddressType());
        }
        if(appType == AppConstant.ApplicationType.FRESH){
            Prefs.with(this).save(Constants.SP_FRESH_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        }
        else if(appType == AppConstant.ApplicationType.MEALS){
            Prefs.with(this).save(Constants.SP_MEALS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        }
        else if(appType == AppConstant.ApplicationType.GROCERY){
            Prefs.with(this).save(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        }
        else if(appType == AppConstant.ApplicationType.MENUS){
            Prefs.with(this).save(Constants.SP_MENUS_CHECKOUT_SAVE_DATA, gson.toJson(checkoutSaveData, CheckoutSaveData.class));
        }
    }

    public CheckoutSaveData getCheckoutSaveData(){
        Gson gson = new Gson();
        int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
        if(appType == AppConstant.ApplicationType.MEALS){
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_MEALS_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        }
        else if(appType == AppConstant.ApplicationType.GROCERY){
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        }
        else if(appType == AppConstant.ApplicationType.MENUS){
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_MENUS_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        }
        else {
            return gson.fromJson(Prefs.with(this).getString(Constants.SP_FRESH_CHECKOUT_SAVE_DATA,
                    gson.toJson(new CheckoutSaveData(), CheckoutSaveData.class)), CheckoutSaveData.class);
        }
    }

    private boolean refreshCart = false;
    public boolean isRefreshCart(){
        return refreshCart;
    }
    public void setRefreshCart(boolean refreshCart){
        this.refreshCart = refreshCart;
    }



    private MenusResponse.Vendor vendorOpened;
    public MenusResponse.Vendor getVendorOpened() {
        return vendorOpened;
    }

    public void setVendorOpened(MenusResponse.Vendor vendorOpened) {
        this.vendorOpened = vendorOpened;
    }

    public RelativeLayout getRelativeLayoutCartNew(){
        return relativeLayoutCartNew;
    }


    private void setRelativeLayoutLocalityClick(){
        topBar.relativeLayoutLocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTransactionUtils().openDeliveryAddressFragment(FreshActivity.this, getRelativeLayoutContainer());
            }
        });
    }

    private MenusResponse menusResponse;

    public MenusResponse getMenusResponse() {
        return menusResponse;
    }

    public void setMenusResponse(MenusResponse menusResponse) {
        this.menusResponse = menusResponse;
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

    public boolean checkForAdd(int position, final SubItem subItem) {
        int appType = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
        if(appType == AppConstant.ApplicationType.MENUS){
            try {
                JSONObject jsonSavedCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
                if(getVendorOpened() != null
						&& !getVendorOpened().getRestaurantId().equals(jsonSavedCart
						.optInt(Constants.KEY_RESTAURANT_ID, getVendorOpened().getRestaurantId()))){
                    String oldRestaurantName = jsonSavedCart.optString(Constants.KEY_RESTAURANT_NAME, "");
                    DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                            getString(R.string.previous_vendor_cart_message_format, oldRestaurantName),
                            getString(R.string.ok), getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clearMenusCart();
                                    subItem.setSubItemQuantitySelected(subItem.getSubItemQuantitySelected() + 1);
                                    getVendorMenuFragment().getFreshCategoryFragmentsAdapter().notifyDataSetChanged();
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
        } else {
            return true;
        }
    }




    public void getAddressAndFetchOfferingData(final LatLng currentLatLng, final int appType){
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

    private void retryDialogLocationFetch(DialogErrorType dialogErrorType, final LatLng currentLatLng, final int appType){
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


    public void setAddressAndFetchOfferingData(int appType){
        try {
            String address = "";
            if(TextUtils.isEmpty(getSelectedAddressType())){
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
            getTopBar().textViewLocationValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26f * ASSL.Xscale());

            getTopBar().textViewLocationValue.setText(address);
            if(getFreshCheckoutMergedFragment() == null && getMenusCheckoutMergedFragment() == null && getFeedbackFragment() == null) {
                if (appType == AppConstant.ApplicationType.FRESH && getFreshFragment() != null) {
                    getFreshFragment().getAllProducts(true, getSelectedLatLng());
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

    public void setLocalityAddressFirstTime(int appType){
        if(selectedLatLng == null || TextUtils.isEmpty(getSelectedAddress())){
            setOfferingLastAddressToActivityVariables(appType);
        } else {
            setAddressAndFetchOfferingData(appType);
        }
    }



    public void saveOfferingLastAddress(int appType){
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

    public void setOfferingLastAddressToActivityVariables(int appType){
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = gson.fromJson(Prefs.with(this)
                    .getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
            if(searchResultLocality != null && !TextUtils.isEmpty(searchResultLocality.getAddress())){
                setSearchResultToActVarsAndFetchData(searchResultLocality, appType);
            } else {
                SearchResult searchResult = homeUtil.getNearBySavedAddress(FreshActivity.this, getSelectedLatLng(),
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                if(searchResult != null && !TextUtils.isEmpty(searchResult.getAddress())){
                    setSearchResultToActVarsAndFetchData(searchResult, appType);
                } else {
                    getAddressAndFetchOfferingData(getSelectedLatLng(), appType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearchResultToActVarsAndFetchData(SearchResult searchResultLocality, int appType){
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HomeUtil homeUtil = new HomeUtil();

    private boolean isAddressConfirmed = false;
    public boolean isAddressConfirmed(){
        return isAddressConfirmed;
    }
    public void setIsAddressConfirmed(boolean isAddressConfirmed){
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
    public PaySDKUtils getPaySDKUtils(){
        if(paySDKUtils == null){
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
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = (int)(96f * ASSL.Yscale());
        toolbar.setLayoutParams(layoutParams);
    }

    public void resetToolbarWithScroll(){
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);

        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = (int)(164f * ASSL.Yscale());
        toolbar.setLayoutParams(layoutParams);
    }
}
