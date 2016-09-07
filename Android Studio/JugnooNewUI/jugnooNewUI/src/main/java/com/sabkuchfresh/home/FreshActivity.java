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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.bus.AddressSearch;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.fragments.AddAddressMapFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshAddressFragment;
import com.sabkuchfresh.fragments.FreshCartItemsFragment;
import com.sabkuchfresh.fragments.FreshCheckoutFragment;
import com.sabkuchfresh.fragments.FreshFragment;
import com.sabkuchfresh.fragments.FreshOrderHistoryFragment;
import com.sabkuchfresh.fragments.FreshOrderSummaryFragment;
import com.sabkuchfresh.fragments.FreshPaymentFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.FreshSupportFragment;
import com.sabkuchfresh.fragments.HomeFragment;
import com.sabkuchfresh.fragments.MealFragment;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
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

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.DeepLinkAction;
import product.clicklabs.jugnoo.home.FABView;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.home.dialogs.PaytmRechargeDialog;
import product.clicklabs.jugnoo.home.dialogs.PushDialog;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 4/6/16.
 */
public class FreshActivity extends BaseFragmentActivity implements LocationUpdate, FlurryEventNames {

    private final String TAG = FreshActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;

    private RelativeLayout relativeLayoutContainer;

    public RelativeLayout relativeLayoutCart;
    private RelativeLayout relativeLayoutCheckoutBar, relativeLayoutSort, relativeLayoutCartNew;
    private LinearLayout linearLayoutCheckout, linearLayoutCheckoutContainer;
    private TextView textViewTotalPrice, textViewCheckout, textViewMinOrder, textViewCartItemsCount, textViewCartItemsCountNew;
    private ImageView imageViewCartNew;

    private MenuBar menuBar;
    private TopBar topBar;
    private FABView fabView;
    private TransactionUtils transactionUtils;

    private ProductsResponse productsResponse;
    private UserCheckoutResponse userCheckoutResponse;

    private String selectedAddress = "";
    private String splInstr = "";
    private Slot slotSelected, slotToSelect;
    private PaymentOption paymentOption;

    private List<DeliveryAddress> deliveryAddresses;

    public String mContactNo = "";

    private View topView;

    /**
     * this holds the reference for the Otto Bus which we declared in LavocalApplication
     */
    protected Bus mBus;
    private double totalPrice = 0;

    public boolean updateCart = false;

    private LocationFetcher locationFetcher;

    // for adding address
    public String current_action = "";
    public double current_latitude = 0.0;
    public double current_longitude = 0.0;
    public String current_street = "";
    public String current_route = "";
    public String current_area = "";
    public String current_city = "";
    public String current_pincode = "";
    public boolean locationSearchShown = false;
    public boolean canOrder = false;

    public void openNotification() {
        menuBar.getMenuAdapter().onClickAction(MenuInfoTags.INBOX.getTag());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_fresh);
            Log.e("", "");
            Data.currentActivity = FreshActivity.class.getName();
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            new ASSL(this, drawerLayout, 1134, 720, false);

            relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

            try {
                mBus = ((MyApplication) getApplication()).getBus();
            } catch (Exception e) {
                e.printStackTrace();
            }

            linearLayoutCheckoutContainer = (LinearLayout) findViewById(R.id.linearLayoutCheckoutContainer);
            relativeLayoutCheckoutBar = (RelativeLayout) findViewById(R.id.relativeLayoutCheckoutBar);
            relativeLayoutCart = (RelativeLayout) findViewById(R.id.relativeLayoutCart);
            relativeLayoutCartNew = (RelativeLayout) findViewById(R.id.relativeLayoutCartNew);
            linearLayoutCheckout = (LinearLayout) findViewById(R.id.linearLayoutCheckout);
            relativeLayoutSort = (RelativeLayout) findViewById(R.id.relativeLayoutSort);

            imageViewCartNew = (ImageView) findViewById(R.id.imageViewCartNew);

            textViewCartItemsCountNew = (TextView) findViewById(R.id.textViewCartItemsCountNew);
            textViewCartItemsCountNew.setTypeface(Fonts.mavenRegular(this));

            textViewCartItemsCount = (TextView) findViewById(R.id.textViewCartItemsCount);
            textViewCartItemsCount.setTypeface(Fonts.mavenRegular(this));
            textViewCartItemsCount.setMinWidth((int) (45f * ASSL.Xscale()));

            textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
            textViewTotalPrice.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

            textViewCheckout = (TextView) findViewById(R.id.textViewCheckout);
            textViewCheckout.setTypeface(Fonts.mavenRegular(this));
            textViewMinOrder = (TextView) findViewById(R.id.textViewMinOrder);
            textViewMinOrder.setTypeface(Fonts.mavenRegular(this));

            topView = (View) findViewById(R.id.topBarMain);

            menuBar = new MenuBar(this, drawerLayout);
            topBar = new TopBar(this, drawerLayout);
            fabView = new FABView(this);

//        if(BuildConfig.DEBUG_MODE)
//            Utils.showPaystorePopup(FreshActivity.this, "", "please rate us");

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeyboard(FreshActivity.this, textViewCartItemsCount);
                    FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.CHECKOUT_SCREEN);
                    FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.REVIEW_CART, getProduct());

                    int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                    if (updateCartValuesGetTotalPrice().second > 0) {
                        if (getTransactionUtils().checkIfFragmentAdded(FreshActivity.this, FreshCartItemsFragment.class.getName())) {
                            //int appType = Prefs.with(FreshActivity.this).getInt(Constants.APP_TYPE, Data.AppType);
                            if(appType == AppConstant.ApplicationType.MEALS) {
                                if(isAvailable()) {
                                    Toast.makeText(FreshActivity.this, getResources().getString(R.string.your_cart_is_has_available), Toast.LENGTH_SHORT).show();
                                } else {
                                    getTransactionUtils().openCheckoutFragment(FreshActivity.this, relativeLayoutContainer);
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_CART+"_"+FirebaseEvents.CHECKOUT, null);
                                }
                            } else {
                                getTransactionUtils().openCheckoutFragment(FreshActivity.this, relativeLayoutContainer);
                                MyApplication.getInstance().logEvent(FirebaseEvents.F_CART+"_"+FirebaseEvents.CHECKOUT, null);
                            }
                        } else {
                            getTransactionUtils().openCartFragment(FreshActivity.this, relativeLayoutContainer);

                            if(appType == AppConstant.ApplicationType.MEALS){
                                MyApplication.getInstance().logEvent(FirebaseEvents.M_CART, null);
                            }else{
                                if((getFreshSearchFragment() != null) && (!getFreshSearchFragment().isHidden())){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.F_SEARCH_GO, null);
                                } else{
                                    MyApplication.getInstance().logEvent(FirebaseEvents.F_CART, null);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty), Toast.LENGTH_SHORT).show();
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
                            getTransactionUtils().openCartFragment(FreshActivity.this, relativeLayoutContainer);
                        } else {
                            Toast.makeText(FreshActivity.this, getResources().getString(R.string.your_cart_is_empty), Toast.LENGTH_SHORT).show();
                        }

                        if((getFreshSearchFragment() != null) && (!getFreshSearchFragment().isHidden())){
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_SEARCH_CART, null);
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

                    if (fragment != null && !fragment.isHidden()) {
                        fragment.getFreshDeliverySlotsDialog().showSorting();
                    } else if(mealFragment != null && !mealFragment.isHidden()) {
                        mealFragment.getFreshDeliverySlotsDialog().showSorting();
                    }
                }
            });


            Data.latitude = Data.loginLatitude;
            Data.longitude = Data.loginLongitude;


            try {
    //            if (Data.getFreshData().stores.size() > 1) {
    //                int fragMentType = Prefs.with(this).getInt(Constants.APP_TYPE, 0);
    //                if (fragMentType == AppConstant.ApplicationType.FRESH) {
    //                    addFreshFragment();
    //                } else if (fragMentType == AppConstant.ApplicationType.MEALS) {
    //                    addMealFragment();
    //                } else {
    //                    addNewFreshFragment();
    //                }
    //            } else if (Data.getFreshData().stores.size() == 1) {
    //                int appType = Data.getFreshData().stores.get(0).getStoreId();
    //                if (appType == AppConstant.ApplicationType.FRESH) {
    //                    addFreshFragment();
    //                } else {
    //                    addMealFragment();
    //                }
    //            } else {
    //                addFreshFragment();
    //            }

                String lastClientId = getIntent().getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);
                if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
                    addMealFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);
                } else {
                    openCart();
                    addFreshFragment();
                    Prefs.with(this).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
                    lastClientId = Config.getFreshClientId();
                }
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
                                        relativeLayoutCart.performClick();
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
									relativeLayoutCart.performClick();
								} else {
									Bundle bundle = new Bundle();
									bundle.putBoolean(Constants.KEY_APP_CART_SWITCH_BUNDLE, true);
									MyApplication.getInstance().getAppSwitcher().switchApp(FreshActivity.this, Config.getFreshClientId(), null,
											getCurrentPlaceLatLng(), bundle, false);
	//                    openCart();
	//                    addFreshFragment();
	//                    Prefs.with(context).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
								}
							} else if (type == 1) {
								intentToShareActivity();
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
        if (!HomeUtil.checkIfUserDataNull(this)) {
            try {
                mBus.register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                        (getMealFragment() != null && !getMealFragment().isHidden())) {
                    fabView.relativeLayoutFAB.setVisibility(View.VISIBLE);
                    fabView.setFABMenuDrawable();
                }
            } else{
                fabView.relativeLayoutFAB.setVisibility(View.INVISIBLE);
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

    private HomeFragment getHomeFragment() {
        return (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getName());
    }

    public FreshFragment getFreshFragment() {
        return (FreshFragment) getSupportFragmentManager().findFragmentByTag(FreshFragment.class.getName());
    }

    public MealFragment getMealFragment() {
        return (MealFragment) getSupportFragmentManager().findFragmentByTag(MealFragment.class.getName());
    }

    private FreshCartItemsFragment getFreshCartItemsFragment() {
        return (FreshCartItemsFragment) getSupportFragmentManager().findFragmentByTag(FreshCartItemsFragment.class.getName());
    }

    private FreshCheckoutFragment getFreshCheckoutFragment() {
        return (FreshCheckoutFragment) getSupportFragmentManager().findFragmentByTag(FreshCheckoutFragment.class.getName());
    }

    public FreshAddressFragment getFreshAddressFragment() {
        return (FreshAddressFragment) getSupportFragmentManager().findFragmentByTag(FreshAddressFragment.class.getName());
    }

    public TopBar getTopBar() {
        return topBar;
    }

    public void hideBottomBar(boolean flag) {
        if(flag)
            relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
        else
            relativeLayoutCheckoutBar.setVisibility(View.GONE);
    }

    public String getCurrentFragment(){
        String fragmentName = "";
        FreshFragment fragment = getFreshFragment();
        MealFragment mealFragment = getMealFragment();

        if (fragment != null && !fragment.isHidden()) {
            fragmentName = Constants.FRESH_FRAGMENT;
        } else if(mealFragment != null && !mealFragment.isHidden()) {
            fragmentName = Constants.MEALS_FRAGMENT;
        }
        return fragmentName;
    }
    //	public FreshOrderHistoryFragment getFreshOrderHistoryFragment(){
//		return (FreshOrderHistoryFragment) getSupportFragmentManager().findFragmentByTag(FreshOrderHistoryFragment.class.getName());
//	}
//
//	private FreshOrderSummaryFragment getFreshOrderSummaryFragment(){
//		return (FreshOrderSummaryFragment) getSupportFragmentManager().findFragmentByTag(FreshOrderSummaryFragment.class.getName());
//	}

    private FeedbackFragment getFeedbackFragment() {
        return (FeedbackFragment) getSupportFragmentManager().findFragmentByTag(FeedbackFragment.class.getName());
    }

    private FreshPaymentFragment getFreshPaymentFragment() {
        return (FreshPaymentFragment) getSupportFragmentManager().findFragmentByTag(FreshPaymentFragment.class.getName());
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
        int totalQuantity = 0;
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
                    imageViewCartNew.setBackgroundResource(R.drawable.ic_cart_fill);
                    String total = String.valueOf(totalQuantity);
                    textViewCartItemsCount.setText(total);
                    textViewCartItemsCountNew.setText(total);
                } else {
                    textViewCartItemsCount.setVisibility(View.GONE);
                    textViewCartItemsCountNew.setVisibility(View.GONE);
                    imageViewCartNew.setBackgroundResource(R.drawable.ic_cart_empty);
                }
                if (getFreshCartItemsFragment() != null) {
                    if (this.getFreshCartItemsFragment().isVisible() && totalPrice < getProductsResponse().getDeliveryInfo().getMinAmount()) {
                        textViewMinOrder.setVisibility(View.VISIBLE);
                    } else {
                        textViewMinOrder.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveCartToSP();
        if (totalQuantity > 0) {
            NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_FRESH_ITEMS_IN_CART, null);
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
            fabView.fabFresh.setVisibility(View.VISIBLE);
        } else {
            fabView.fabFresh.setVisibility(View.GONE);
        }

        if (Data.userData.getMealsEnabled() == 1) {
            fabView.fabMeals.setVisibility(View.VISIBLE);
        } else {
            fabView.fabMeals.setVisibility(View.GONE);
        }

        if (Data.userData.getDeliveryEnabled() == 1) {
            fabView.fabDelivery.setVisibility(View.VISIBLE);
        } else {
            fabView.fabDelivery.setVisibility(View.GONE);
        }
    }

    public void fragmentUISetup(Fragment fragment) {
        try {
            textViewMinOrder.setVisibility(View.GONE);

            topBar.title.setTypeface(Fonts.avenirNext(this));
            topBar.below_shadow.setVisibility(View.VISIBLE);
            relativeLayoutCartNew.setVisibility(View.GONE);
            linearLayoutCheckout.setVisibility(View.VISIBLE);

//        Utils.hideViewByScale(relativeLayoutCartRound);
//        relativeLayoutCartRound.hide();
            topView.setVisibility(View.VISIBLE);
            fabView.relativeLayoutFAB.setVisibility(View.INVISIBLE);
            //imageViewFabFake.setVisibility(View.GONE);

            if (fragment instanceof FreshFragment) {
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.below_shadow.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				if(relativeLayoutCheckoutBar.getVisibility() != View.VISIBLE)
                    relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);
				//imageViewFabFake.setVisibility(View.VISIBLE);
				if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
					fabView.relativeLayoutFAB.setVisibility(View.VISIBLE);
					fabView.setFABMenuDrawable();
				}


				relativeLayoutCartNew.setVisibility(View.VISIBLE);
				linearLayoutCheckout.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.VISIBLE);
				relativeLayoutCart.setVisibility(View.GONE);
				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.fresh));
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);

			} else if(fragment instanceof MealFragment){
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				relativeLayoutCartNew.setVisibility(View.VISIBLE);
				linearLayoutCheckout.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.VISIBLE);
				relativeLayoutCart.setVisibility(View.GONE);

				if(Prefs.with(FreshActivity.this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
					fabView.relativeLayoutFAB.setVisibility(View.VISIBLE);
					fabView.setFABMenuDrawable();
				}

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.meals));
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
			} else if (fragment instanceof FreshCartItemsFragment) {
				textViewMinOrder.setText(String.format(getResources().getString(R.string.fresh_min_order_value), getProductsResponse().getDeliveryInfo().getMinAmount().intValue()));
				try {
	//                String[] splited = textViewTotalPrice.getText().toString().split("\\s+");
	//                String split_one = splited[1];
					if (getTotalPrice() < getProductsResponse().getDeliveryInfo().getMinAmount()) {
						textViewMinOrder.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.VISIBLE);
				textViewCheckout.setVisibility(View.VISIBLE);
				relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.my_cart));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshCheckoutFragment) {
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

			} else if (fragment instanceof FreshAddressFragment || fragment instanceof AddAddressMapFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.address));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshPaymentFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.payment));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshOrderHistoryFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.order_history));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshOrderSummaryFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.order_summary));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshSupportFragment) {
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.support));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof FreshSearchFragment) {
				topView.setVisibility(View.GONE);
				topBar.imageViewMenu.setVisibility(View.GONE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.VISIBLE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.VISIBLE);

				relativeLayoutSort.setVisibility(View.GONE);
				relativeLayoutCart.setVisibility(View.VISIBLE);

				topBar.title.setVisibility(View.VISIBLE);
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

			} else if (fragment instanceof HomeFragment) {
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);

				relativeLayoutSort.setVisibility(View.VISIBLE);
				relativeLayoutCart.setVisibility(View.GONE);


				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.setText(getResources().getString(R.string.app_name));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);

			} else if(fragment instanceof FeedbackFragment) {
				topBar.imageViewMenu.setVisibility(View.VISIBLE);
				topBar.relativeLayoutNotification.setVisibility(View.GONE);
				topBar.imageViewBack.setVisibility(View.GONE);
				topBar.imageViewDelete.setVisibility(View.GONE);
				textViewCheckout.setVisibility(View.GONE);
				relativeLayoutCheckoutBar.setVisibility(View.GONE);
				topBar.title.setVisibility(View.VISIBLE);
				topBar.title.getPaint().setShader(Utils.textColorGradient(this, topBar.title));
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
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
        NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_FRESH_CART_DELETE_CLICKED, null);
        DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                getResources().getString(R.string.delete_fresh_cart_message),
                getResources().getString(R.string.delete),
                getResources().getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.DELETE, FlurryEventNames.ALL);

                        FreshCartItemsFragment frag = getFreshCartItemsFragment();
                        if (frag != null) {
                            frag.deleteCart();
                        }

                        if(type == AppConstant.ApplicationType.FRESH) {
                            clearCart();
                        } else {
                            clearMealCart();
                        }
                        if(type == AppConstant.ApplicationType.MEALS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        }else{
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.YES, null);
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NudgeClient.trackEventUserId(FreshActivity.this,
                                FlurryEventNames.NUDGE_FRESH_CART_DELETE_CANCEL_CLICKED, null);
                        if(type == AppConstant.ApplicationType.MEALS){
                            MyApplication.getInstance().logEvent(FirebaseEvents.M_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        }else{
                            MyApplication.getInstance().logEvent(FirebaseEvents.F_CART+"_"+FirebaseEvents.TRASH+"_"+FirebaseEvents.NO, null);
                        }
                    }
                }, true, false);
    }


    public void updateMenu() {
        menuBar.setUserData();
    }


    public void orderComplete() {
        clearCart();
        for (Category category : productsResponse.getCategories()) {
            for (SubItem subItem : category.getSubItems()) {
                subItem.setSubItemQuantitySelected(0);
            }
        }
        selectedAddress = "";
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
                FreshFragment frag = getFreshFragment();
                if (frag != null) {
                    frag.getAllProducts(true);
                } else {
                    MealFragment mealFragment = getMealFragment();
                    if(mealFragment != null) {
                        mealFragment.getAllProducts(true);
                    }
                }
            }
        }, 1000);

        NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_JUGNOO_FRESH_ORDER_PLACED, null);

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

    private void addNewFreshFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new HomeFragment(),
                        HomeFragment.class.getName())
                .addToBackStack(HomeFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openOrderHistory() {
        getTransactionUtils().openOrderHistoryFragment(FreshActivity.this, relativeLayoutContainer);
    }

    /**
     * Method used to open feedback screen
     */
    public void openFeedback() {
        getTransactionUtils().openFeedback(FreshActivity.this, relativeLayoutContainer);

//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                .add(relativeLayoutContainer.getId(), new FeedbackFragment(),
//                        FeedbackFragment.class.getName())
//                .addToBackStack(FeedbackFragment.class.getName())
//                .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
//                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//                .commitAllowingStateLoss();
    }

    public void openSupport() {
        startActivity(new Intent(this, SupportActivity.class));
//		getTransactionUtils().openSupportFragment(FreshActivity.this, relativeLayoutContainer);
    }

    public void openMapAddress() {
        FlurryEventLogger.event(Address_Screen, SCREEN_TRANSITION, ADD_NEW_ADDRESS);
        getTransactionUtils().openMapFragment(FreshActivity.this, relativeLayoutContainer);
    }

    public void openAddToAddressBook() {
        getTransactionUtils().openAddToAddressFragment(FreshActivity.this, relativeLayoutContainer);
    }



    public void performBackPressed() {

        if(getFeedbackFragment() != null && getSupportFragmentManager().getBackStackEntryCount() == 2 && !getFeedbackFragment().isUpbuttonClicked) {
            finish();
        }
        Utils.hideSoftKeyboard(this, textViewCartItemsCount);
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else if(locationSearchShown) {
            locationSearchShown = false;
            try {
                mBus.post(new AddressSearch(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (getFreshPaymentFragment() != null) {
                NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_FRESH_BACK_ON_PAYMENT_CLICKED, null);
            }
            if((getSupportFragmentManager().getBackStackEntryCount() == 2 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() != null)){
                FlurryEventLogger.event(FlurryEventNames.REVIEW_CART, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.HOME_SCREEN);
            }

            if((getSupportFragmentManager().getBackStackEntryCount() == 3 && getFreshSearchFragment() == null) ||
                    (getSupportFragmentManager().getBackStackEntryCount() == 4 && getFreshSearchFragment() != null)){
                FlurryEventLogger.event(FlurryEventNames.CHECKOUT, FlurryEventNames.SCREEN_TRANSITION, FlurryEventNames.REVIEW_CART_SCREEN);
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


//    public void performBackPressed() {
//        String pupUpto = "";
//        for (int entry = 0; entry < getFragmentManager().getBackStackEntryCount(); entry++) {
//            if (entry == 1) {
//                pupUpto = getFragmentManager().getBackStackEntryAt(entry).getName();
//                break;
//            }
//        }
//
//        try {
//            getActivity().getSupportFragmentManager().popBackStack(pupUpto, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
//        } catch (Exception e) {
//            try {
//                getActivity().getSupportFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
//
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragLayout, new StoreListCategoryFragment(), "StoreListCategoryFragment").addToBackStack("StoreListCategoryFragment")
//                        .commitAllowingStateLoss();
//            } catch (Exception e1) {
//
//            }
//        }
//
//    }


    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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
        return new LatLng(Data.latitude, Data.longitude);
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

    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
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

    public Slot getSlotToSelect() {
        return slotToSelect;
    }

    public void setSlotToSelect(Slot slotToSelect) {
        this.slotToSelect = slotToSelect;
    }

    public void setDeliveryAddresses(List<DeliveryAddress> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public List<DeliveryAddress> getDeliveryAddress() {
        return deliveryAddresses;
    }

    public PaymentOption getPaymentOption() {
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
            mBus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } else {
                Prefs.with(this).save(Constants.SP_MEAL_CART, jCart.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCartFromSP() {
        try {

            for (Category category : productsResponse.getCategories()) {
                for (SubItem subItem : category.getSubItems()) {
                    subItem.setSubItemQuantitySelected(0);
                }
            }
            JSONObject jCart;
            int type = Prefs.with(this).getInt(Constants.APP_TYPE, Data.AppType);
            if(type == AppConstant.ApplicationType.FRESH) {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT));
            } else {
                jCart = new JSONObject(Prefs.with(this).getString(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT));
            }
            if (getProductsResponse() != null
                    && getProductsResponse().getCategories() != null) {
                for (Category category : getProductsResponse().getCategories()) {
                    for (SubItem subItem : category.getSubItems()) {
                        try {
                            subItem.setSubItemQuantitySelected(jCart.optInt(String.valueOf(subItem.getSubItemId()),
                                    (int) subItem.getSubItemQuantitySelected()));
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

    private void clearCart() {
        Prefs.with(this).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);
    }

    private void clearMealCart() {
        Prefs.with(this).save(Constants.SP_MEAL_CART, Constants.EMPTY_JSON_OBJECT);
    }

    @Subscribe
    public void onSortEvent(SortSelection event) {
        switch(event.postion){
            case 0:
                for (Category category : productsResponse.getCategories()) {
                    Collections.sort(category.getSubItems(), new SubItemCompare());
                }
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.A_Z);
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
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.POPULARITY);
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
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
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
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
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
    }

    @Override
    public void onLocationChanged(Location location) {
        Data.latitude = location.getLatitude();
        Data.longitude = location.getLongitude();
    }
//
//    @Override
//    public void onitemClicked(int position) {
//        switch (position) {
//            case 0: {
////                FlurryEventLogger.event(, CART_BUTTON_CLICKED);
//                startActivity(new Intent(FreshActivity.this, AccountActivity.class));
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 1: {
//                Intent intent = new Intent(FreshActivity.this, PaymentActivity.class);
//                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_WALLET_CLICKED, null);
//                break;
//            }
//            case 2: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.HISTORY);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 3: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.SUPPORT);
//                intent.putExtra(Constants.ORDER_CONTACT, mContactNo);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 4: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.ABOUT);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 5: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.NOTIFICATION);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 6: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.SHARE);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 7: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.FEED_BACK);
//                intent.putExtra(Constants.QUESTION, Data.userData.question);
//                intent.putExtra(Constants.QUESTION_TYPE, ""+Data.userData.questionType);
//                intent.putExtra(Constants.ORDER_ID, Data.userData.orderId);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            }
//            case 8: {
//                Intent intent = new Intent(FreshActivity.this, SupportActivity.class);
//                intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.PROMO);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            }
//            case 11:
//                if(Prefs.with(this).getInt(Constants.APP_TYPE, 0) == AppConstant.ApplicationType.MEALS) {
//                    FreshFragment frag = getFreshFragment();
//                    MealFragment mealFragment = getMealFragment();
//                    if (mealFragment != null && !mealFragment.isHidden() && frag == null)
//                        addFreshFragment1(mealFragment, true);
//
//                } else {
//                    HomeFragment homeFragment = getHomeFragment();
//                    if (homeFragment != null && !homeFragment.isHidden()) {
//                        addFreshFragment1(homeFragment, false);
//                    }
//                }
//                drawerLayout.closeDrawer(GravityCompat.START);
//                break;
//            case 12:
//                if(Prefs.with(this).getInt(Constants.APP_TYPE, 0) == AppConstant.ApplicationType.FRESH) {
//                    FreshFragment frag = getFreshFragment();
//                    MealFragment mealFragment = getMealFragment();
//                    if (frag != null && !frag.isHidden() && mealFragment == null) {
//                        addMealFragment(frag, true);
//                    }
//                } else {
//                    HomeFragment homeFragment = getHomeFragment();
//                    if (homeFragment != null && !homeFragment.isHidden()) {
//                        addMealFragment(homeFragment, false);
//                    }
//                }
//                drawerLayout.closeDrawer(GravityCompat.START);
//                break;
//
//            default:
//
//                break;
//        }
//    }


    public void addFreshFragment1(Fragment fragment, boolean swipeFlag) {

        final FragmentManager fragmentManager =  getSupportFragmentManager();
        fragmentManager.popBackStack();
//        fragmentManager.popBackStackImmediate(HomeFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                .add(R.id.relativeLayoutContainer, new FreshFragment(),
//                        FreshFragment.class.getName())
//                .addToBackStack(FreshFragment.class.getName())
////                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
////                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//                .commitAllowingStateLoss();

    }

    public void addMealFragment(Fragment fragment, boolean swipeFlag) {

        final FragmentManager fragmentManager =  getSupportFragmentManager();

        fragmentManager.popBackStack();
//        fragmentManager.popBackStackImmediate(HomeFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.executePendingTransactions();
        //.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
        //.setCustomAnimations(R.anim.grow_from_middle, R.anim.hold, R.anim.hold, R.anim.shrink_to_middle)


        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(swipeFlag) {
            transaction.setCustomAnimations(R.anim.grow_from_middle, R.anim.hold, R.anim.hold, R.anim.shrink_to_middle);
        } else {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out);
        }
        transaction.replace(R.id.relativeLayoutContainer, new MealFragment(), MealFragment.class.getName());
        transaction.addToBackStack(MealFragment.class.getName());
        transaction.commitAllowingStateLoss();


//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                .replace(R.id.relativeLayoutContainer, new MealFragment(),
//                        MealFragment.class.getName())
//                .addToBackStack(MealFragment.class.getName())
////                .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
////                        .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//                .commitAllowingStateLoss();

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

}