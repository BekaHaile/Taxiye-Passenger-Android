package com.sabkuchfresh.fragments;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCurrentStatusIciciUpi;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.fatafatchatpay.FatafatChatPayActivity;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.utils.Utils;
import com.sabkuchfresh.utils.WrapContentLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class MenusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction{
    private final String TAG = MenusFragment.class.getSimpleName();

    private RelativeLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private DeliveryHomeAdapter deliveryHomeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewRestaurant;
    private TextView textViewNothingFound;
//    private DeliveryDisplayCategoriesView deliveryDisplayCategoriesView;
    private RelativeLayout rlMainContainer;
    private View vDividerLocation;

    public View rootView;
    private FreshActivity activity;


    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> statusMeals = new ArrayList<>();
    private ArrayList<String> statusFatafat = new ArrayList<>();

    PushDialog pushDialog;
    public boolean resumed = false, searchOpened = false;
    private WrapContentLinearLayoutManager linearLayoutManager;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private boolean isPagingApiInProgress;
    private boolean hasMorePages;
    private int noOfCategories;
    private boolean isKeyboardOpen;
    public boolean chatAvailable;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;
    private ValueAnimator openAnim;
    private ValueAnimator closeAnim;
    private int widthFatafatChatIconText;
    private TextView tvSuggestStore;
    private LinearLayout llNoMenusAddStore;

    public MenusResponse.StripInfo getCurrentStripInfo() {
        return currentStripInfo;
    }

    private MenusResponse.StripInfo currentStripInfo;

    public MenusFragment() {
    }

    public static final int TYPE_API_MENUS_ADDRESS_CHANGE  = 0;
    public static final int TYPE_API_MENUS_REFRESH  = 1;
    public static final int TYPE_API_MENUS_SEARCH  = 2;
    public static final int TYPE_API_MENUS_FILTER = 3;
    public static final int TYPE_API_MENUS_CATEGORY_CHANGE = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus, container, false);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);setUpUIforCategoriesOpened(activity.getCategoryOpened());
//        activity.setDeliveryAddressView(rootView);
        activity.setCategoryIdOpened(null);
        activity.rlfabViewFatafat.setVisibility(View.GONE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.GONE); //only for first time

        widthFatafatChatIconText =  activity.getResources().getDimensionPixelSize(R.dimen.dp_120);


      /*  deliveryDisplayCategoriesView = new DeliveryDisplayCategoriesView(activity,
                rootView.findViewById(R.id.rLCategoryDropDown), this);*/

        Data.AppType = Config.getLastOpenedClientId(activity).equals(Config.getDeliveryCustomerClientId()) 
                ? AppConstant.ApplicationType.DELIVERY_CUSTOMER : AppConstant.ApplicationType.MENUS;
        Prefs.with(activity).save(Constants.APP_TYPE, Data.AppType);
        GAUtils.trackScreenView(activity.getGaCategory() + HOME);
        GAUtils.trackScreenView(activity.getGaCategory() + HOME + V2);

        llRoot = (RelativeLayout) rootView.findViewById(R.id.llRoot);

        try {
            if (!TextUtils.isEmpty(Data.userData.getUserId())) {
                MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rlMainContainer = (RelativeLayout) rootView.findViewById(R.id.rlMainContainer);
        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        tvSuggestStore = (TextView) relativeLayoutNoMenus.findViewById(R.id.tv_add_store);
        llNoMenusAddStore = (LinearLayout)relativeLayoutNoMenus.findViewById(R.id.layout_add_store);
        tvSuggestStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.addSuggestStoreFragment();
            }
        });
        ((TextView) rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

        textViewNothingFound = (TextView) rootView.findViewById(R.id.textViewNothingFound);
        textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        linearLayoutManager = new WrapContentLinearLayoutManager(activity);
        recyclerViewRestaurant.setLayoutManager(linearLayoutManager);
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);
        recyclerViewRestaurant.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        recyclerViewRestaurant.requestFocus();


        vDividerLocation = rootView.findViewById(R.id.vDividerLocation); vDividerLocation.setVisibility(View.VISIBLE);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);

        deliveryHomeAdapter = new DeliveryHomeAdapter(activity, new DeliveryHomeAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int vendorId) {
                activity.fetchRestaurantMenuAPI(vendorId, false, null, null, -1, null);
                Utils.hideSoftKeyboard(activity, relativeLayoutNoMenus);
            }

            @Override
            public void onBannerInfoDeepIndexClick(int deepIndex) {
                Data.deepLinkIndex = deepIndex;
                if (activity != null) {
                    activity.openDeepIndex();
                }
            }

            @Override
            public void openCategory(MenusResponse.Category category) {
                switchCategory(category);
            }

            @Override
            public void apiRecommendRestaurant(int categoryId, String restaurantName, String locality, String telephone) {

                hitApiRecommendRestaurant(categoryId, restaurantName,locality, telephone);
                GAUtils.event(activity.getGaCategory(), GAAction.HOME , GAAction.NEW_RESTAURANT + GAAction.SUBMITTED);


            }
        }, recyclerViewRestaurant, status,statusMeals,statusFatafat);

        recyclerViewRestaurant.setAdapter(deliveryHomeAdapter);
        activity.setLocalityAddressFirstTime(activity.getAppType());

        if(!activity.checkForReorderMenus(true)) {
            // to open pending feedback page
            try {
                final String pendingFeedbackClientId = pendingFeedbackClientId();
                if (pendingFeedbackClientId!=null) {

                    activity.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            activity.openFeedback(pendingFeedbackClientId);
                        }
                    }, 300);
                }else{
                    // to open restaurant page if from deep link
                    try {
                        if (!"-1".equalsIgnoreCase(Prefs.with(activity).getString(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, "-1"))) {
                            int restId = Integer.parseInt(Prefs.with(activity).getString(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, "-1"));
                            activity.fetchRestaurantMenuAPI(restId, false, null, null, -1, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Prefs.with(activity).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, "-1");
                    }

                    // for other client ids deeplink on delivery customer only case
                    String clientIdForDeepLink = Prefs.with(activity).getString(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "");
                    if(!clientIdForDeepLink.equalsIgnoreCase("") && !clientIdForDeepLink.equals(Config.getLastOpenedClientId(activity))){
                        activity.switchOfferingViaClientId(clientIdForDeepLink);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



            Prefs.with(activity).save(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "");
        }

        // dialogs at login
        try {
            if (Data.userData.getPromoSuccess() == 0) {
                showPromoFailedAtSignupDialog();
            } else if (getMenusOrDeliveryData().getIsFatafatEnabled() == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
                getMenusOrDeliveryData().setIsFatafatEnabled(AppConstant.IsFatafatEnabled.ENABLED);
                showPopup();
            } else if (getMenusOrDeliveryData().getPopupData() != null) {
                pushDialog = new PushDialog(activity, new PushDialog.Callback() {
                    @Override
                    public void onButtonClicked(int deepIndex) {

                    }
                });
                pushDialog.show(getMenusOrDeliveryData().getPopupData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        activity.getTopBar().ivSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTopBar().etSearch.setText("");
            }
        });


        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                    isKeyboardOpen = true;
                    activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                    activity.hideMenusCartSelectedLayout();
                    activity.rlfabViewFatafat.setVisibility(View.GONE);

            }

            @Override
            public void keyBoardClosed() {

                    isKeyboardOpen = false;
                    activity.getHandler().postDelayed(onKeyBoardCloseRunnable,200);


            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);

        llRoot.post(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    activity.getMenusCartSelectedLayout().checkForVisibility();
                }
            }
        });

        recyclerViewRestaurant.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isPagingApiInProgress && hasMorePages) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            fetchNextPage();
                        }
                    }




                    if(openAnim!=null && openAnim.isRunning())
                       openAnim.cancel();

                    if(closeAnim==null || !closeAnim.isRunning()){
                        closeAnim = ValueAnimator.ofInt(activity.tvFatfatChatIconText.getMeasuredWidth(), 0);
                        closeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = activity.tvFatfatChatIconText.getLayoutParams();
                                layoutParams.width = val;
                                activity.tvFatfatChatIconText.setLayoutParams(layoutParams);
                            }
                        });

                        closeAnim.setDuration(180);
                        closeAnim.start();
                    }




                }else{
                    if(closeAnim!=null && closeAnim.isRunning())
                        closeAnim.cancel();
                    if(openAnim==null || !openAnim.isRunning()){
                        openAnim = ValueAnimator.ofInt(activity.tvFatfatChatIconText.getMeasuredWidth(), widthFatafatChatIconText);
                        openAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = activity.tvFatfatChatIconText.getLayoutParams();
                                layoutParams.width = val;
                                activity.tvFatfatChatIconText.setLayoutParams(layoutParams);
                            }
                        });
                        openAnim.setDuration(180);
                        openAnim.start();
                    }

                }

                try {
                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    int percentage = (int) (100.0 * offset / (float) (range - extent));

                    if (percentage > 0 && percentage % 10 == 0) {
                        GAUtils.event(activity.getGaCategory(), HOME + LIST_SCROLLED, percentage + "%");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });
        return rootView;
    }

    @Nullable
    private String pendingFeedbackClientId() {

        if(activity.isDeliveryOpenInBackground()){

            if(Data.getDeliveryCustomerData()!=null && Data.getDeliveryCustomerData().getPendingFeedback()==1){
                return Config.getDeliveryCustomerClientId();
            }

            if(Data.getFeedData()!=null && Data.getFeedData().getPendingFeedback()==1){
                return Config.getFeedClientId();
            }

            if(Data.getMealsData()!=null && Data.getMealsData().getPendingFeedback()==1){
                return Config.getMealsClientId();
            }

            if(Data.getFreshData()!=null && Data.getFreshData().getPendingFeedback()==1){
                return Config.getFreshClientId();
            }
        }else{
            if(Data.getMenusData()!=null && Data.getMenusData().getPendingFeedback()==1){
                return Config.getMenusClientId();
            }
        }
        return null;
    }

    private Runnable onKeyBoardCloseRunnable = new Runnable() {
        @Override
        public void run() {
            if (!iSChildCategoryOpen() && Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
            }
            activity.getMenusCartSelectedLayout().checkForVisibility();
            toggleFatafatChatIconVisibility();
        }
    };

    private LoginResponse.Menus getMenusOrDeliveryData() {
        if (activity.getAppType() == AppConstant.ApplicationType.DELIVERY_CUSTOMER || activity.isDeliveryOpenInBackground()) {
            return Data.getDeliveryCustomerData();
        } else {
            return Data.getMenusData();
        }
    }

    public void switchCategory(MenusResponse.Category category) {


        try {
            GAUtils.event(GACategory.FATAFAT3, GAAction.FATAFAT_CATEGORY, category.getCategoryName() + " " + GAAction.CLICKED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(category!=null && !TextUtils.isEmpty(category.getClientId()))
        {
            switch (category.getClientId()){
                case Config.MEALS_CLIENT_ID:
                case Config.FRESH_CLIENT_ID:
                    activity.setCategoryIdOpened(null);
                    activity.switchOffering(category.getClientId());
                    try {
                        sendUserClickEvent(category.getClientId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        }else{

            getAllMenus(true, activity.getSelectedLatLng(), true,category, MenusFragment.TYPE_API_MENUS_CATEGORY_CHANGE);


        }

    }

    public void sendUserClickEvent(String clientId) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_MERCHANT_CATEGORY_ID, "0");
        params.put(Constants.KEY_CLIENT_ID, clientId);

        new ApiCommon<>(activity).showLoader(false).execute(params, ApiName.USER_CLICK_EVENTS_CATEGORY,
                new APICommonCallback<FeedCommonResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        return true;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        return true;
                    }

                    @Override
                    public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {

                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return true;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        return true;
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
    }

    private void setUpUIforCategoriesOpened(MenusResponse.Category category) {
        if (activity.getTopFragment() instanceof MenusFragment) {
            if (activity.getAppType()== AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                if(activity.getCategoryIdOpened()<0 || noOfCategories==1){
                    if(searchOpened){
                        activity.getTopBar().imageViewMenu.setVisibility(View.GONE);
                        activity.getTopBar().imageViewBack.setVisibility(View.VISIBLE);
                    }else{
                        activity.getTopBar().imageViewMenu.setVisibility(View.VISIBLE);
                        activity.getTopBar().imageViewBack.setVisibility(View.GONE);
                    }

                    activity.getTopBar().ivFilterApplied.setVisibility(View.GONE);
                    activity.getTopBar().title.setText(activity.getString(R.string.delivery_new_name));
                    /*String titleToSet = noOfCategories==1 && category!=null?category.getCategoryName():activity.getString(R.string.title_fatafat_home_page);
                    String locationText = activity.getTopBar().getTvTopBarDeliveryAddressLocation().getText().toString().trim();
                    if(locationText.length()>0){
                        titleToSet+=" NEAR " + activity.getTopBar().getTvTopBarDeliveryAddressLocation();

                    }*/
                    activity.getTopBar().getTvAddressLayoutTitle().setText(noOfCategories==1 && category!=null?category.getCategoryName():activity.getString(R.string.title_fatafat_home_page));
                    activity.setMenusFilterVisibility(noOfCategories==1&&category!=null?View.VISIBLE:View.GONE);
                    activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                    if (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }

                }else{
                    activity.getTopBar().imageViewMenu.setVisibility(View.GONE);
                    activity.getTopBar().imageViewBack.setVisibility(View.VISIBLE);
                    activity.getTopBar().title.setText(category.getCategoryName());
                    activity.getTopBar().getTvAddressLayoutTitle().setText(category.getCategoryName());
                    activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                    activity.setMenusFilterVisibility(View.VISIBLE);
                    activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                }

                if(!toggleFatafatChatIconVisibility()){
                    activity.rlfabViewFatafat.setVisibility(View.GONE);

                }
            }
        }


    }

    private long lastTimeRefreshed = System.currentTimeMillis();

    private static final long MAX_REFRESH_INTERVAL = 60*1000;
    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && resumed) {
            if(activity.isRefreshCart()
                    || System.currentTimeMillis()-lastTimeRefreshed >= MAX_REFRESH_INTERVAL){
                activity.setLocalityAddressFirstTime(activity.getAppType());
            }
            activity.setRefreshCart(false);
        }
        resumed = true;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                activity.registerForKeyBoardEvent(mKeyBoardStateHandler);
                if(activity.isDeliveryOpenInBackground() && (activity.getAppType()!= AppConstant.ApplicationType.DELIVERY_CUSTOMER || !Config.getLastOpenedClientId(activity).equals(Config.getDeliveryCustomerClientId()))){
                    Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getDeliveryCustomerClientId());
                    Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.DELIVERY_CUSTOMER);
                    Data.AppType = activity.getAppType();
                }


                if (activity.openVendorMenuFragmentOnBack) {
                    activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                    activity.openVendorMenuFragmentOnBack = false;
                    return;
                }
                activity.fragmentUISetup(this);
                if(searchOpened){
                    activity.getTopBar().rlSearch.setVisibility(View.GONE);
                    activity.getTopBar().llSearchContainer.setVisibility(View.VISIBLE);
                    activity.getTopBar().getLlTopBarDeliveryAddress().setVisibility(View.GONE);

                    try {
                        if(!activity.getTopBar().etSearch.getText().toString().equals(searchText)){
                            activity.getTopBar().etSearch.setText(searchText);
                            activity.getTopBar().etSearch.setSelection(searchText.length());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                setUpUIforCategoriesOpened(activity.getCategoryOpened());
                activity.setCurrentDeliveryStripToMinOrder();
                if (!activity.isOrderJustCompleted()) {
                    activity.setAddressTextToLocationPlaceHolder();
                } else {
                    recyclerViewRestaurant.postDelayed(runnableScrollToTop, 100);
                }
               activity.getMenuBar().setUserData();
//                activity.resumeMethod();
              /*  if (searchOpened) {
                    toggleSearch(false, true);
                }*/


                final boolean refreshCartFinal = activity.isRefreshCart() ||
                        // if  activity.getDeliveryAddressesFragment()!=null then the bus has already posted for address changed and the api has been called
                        //so we do not need to call the api again in that case
                        ( activity.getDeliveryAddressesFragment()==null && MapUtils.distance(currentMenusLatLngData, activity.getSelectedLatLng()) > 10) ;

                    activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshCartFinal) {
                            activity.setLocalityAddressFirstTime(activity.getAppType());
                        }
                        activity.setRefreshCart(false);
                    }
                }, 300);
                if(!serviceUnavailable ||
                        (activity.getMenusResponse() != null && activity.getMenusResponse().getRecentOrders().size() > 0)){
                    activity.getMenusCartSelectedLayout().checkForVisibility();
                }


            } else {
                activity.unRegisterKeyBoardListener();
                activity.hideMenusCartSelectedLayout();
                if(onKeyBoardCloseRunnable!=null){
                    activity.getHandler().removeCallbacks(onKeyBoardCloseRunnable);

                }

            }
            try {
                product.clicklabs.jugnoo.utils.Utils.hideSoftKeyboard(activity,activity.getTopBar().etSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable runnableScrollToTop = new Runnable() {
        @Override
        public void run() {
            recyclerViewRestaurant.scrollToPosition(0);
        }
    };

    @Override
    public void onDestroyView() {
        try {
            if(onKeyBoardCloseRunnable!=null) {
                activity.getHandler().removeCallbacks(onKeyBoardCloseRunnable);
            }deliveryHomeAdapter.removeHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }


    @Override
    public void onRefresh() {
        getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_REFRESH);
    }

    private int currentPageCount = 1;
    private LatLng currentMenusLatLngData;

    public void getAllMenus(final boolean loader, final LatLng latLng, final boolean scrollToTop, final MenusResponse.Category categoryObject, final int typeApi) {

        Log.d(TAG, "getAllMenus: init"+System.currentTimeMillis());
        if (isMenusApiInProgress)
            return;

        if (typeApi == TYPE_API_MENUS_ADDRESS_CHANGE || typeApi == TYPE_API_MENUS_CATEGORY_CHANGE) {
            if (activity.getMenusFilterFragment() != null) {
                activity.getMenusFilterFragment().resetAllFilters();
            }
            if (searchOpened) {
                toggleSearch(true, false);

            }
        }

        int categoryId = categoryObject==null?-1:categoryObject.getId();


        final String searchTextCurr = searchText;
        try {


            if (!MyApplication.getInstance().isOnline()) {
                retryDialog(DialogErrorType.NO_NET, latLng, loader, false, scrollToTop, categoryObject, typeApi);
                swipeRefreshLayout.setRefreshing(false);
                return;
            }


            if (loader)
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));


            HashMap<String, String> params = getMenusApiHashMap(latLng, categoryId);
            Callback<MenusResponse> callback = new Callback<MenusResponse>() {
                @Override
                public void success(final MenusResponse menusResponse, Response response) {
                    Log.d(TAG, "getAllMenus: success"+System.currentTimeMillis());
                    boolean shouldRecallSearchAPI = false;
                    lastTimeRefreshed = System.currentTimeMillis();
                    if (typeApi == TYPE_API_MENUS_ADDRESS_CHANGE || typeApi == TYPE_API_MENUS_CATEGORY_CHANGE) {
                        activity.setOfferingsVisibility(menusResponse.getOfferingsVisibilityData());
                        serviceUnavailable = (menusResponse.getServiceUnavailable() == 1);
                        chatAvailable = menusResponse.getChatAvailable() ==1;
                        activity.setCategoryIdOpened(categoryObject);


                        if (activity.getCategoryIdOpened() < 0) {
                            //IF single offering don't show categories bar and display this category on home page instead
                            if (menusResponse.getCategories() != null && menusResponse.getCategories().size() == 1) {
                                activity.setCategoryIdOpened(menusResponse.getCategories().get(0));
                            }
                            noOfCategories = menusResponse.getCategories() == null ? 0 : menusResponse.getCategories().size();
                            activity.setMenusResponse(menusResponse);
                        }

                        status.clear();
                        status.addAll(menusResponse.getRecentOrdersPossibleStatus());
                        statusMeals.clear();
                        statusMeals.addAll(menusResponse.getRecentOrdersPossibleMealsStatus());
                        statusFatafat.clear();
                        statusFatafat.addAll(menusResponse.getRecentOrdersPossibleFatafatStatus());
                        currentStripInfo = null;
                        currentStripInfo = menusResponse.getStripInfo();
                        activity.setCurrentDeliveryStripToMinOrder();
                        setUpUIforCategoriesOpened(activity.getCategoryOpened());
                        setSearcHintText();


                    }

                    // set the openMerchantInfo irrespective of api
                    activity.setMenusIsOpenMerchantInfo(menusResponse.isOpenMerchantInfo());



                    swipeRefreshLayout.setRefreshing(false);
                    activity.getTopBar().setPBSearchVisibility(View.GONE);

                    relativeLayoutNoMenus.setVisibility(View.GONE);
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        String message = menusResponse.getMessage();
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {


                                currentPageCount = 1;
                                hasMorePages = menusResponse.isPageLengthComplete();

                                if (activity.getMenusResponse() == null) {
                                    activity.setMenusResponse(menusResponse);
                                }

                                if (activity.getMenusFilterFragment() != null && typeApi != TYPE_API_MENUS_REFRESH && typeApi != TYPE_API_MENUS_SEARCH) {
                                    activity.getMenusFilterFragment().updateDataLists(menusResponse);
                                }
                                deliveryHomeAdapter.setList(menusResponse, false, hasMorePages);


                                activity.setMenuRefreshLatLng(latLng);
                                currentMenusLatLngData = latLng;
                                setUpServiceUnavailability(menusResponse);
                                checkIciciPaymentStatusApi(activity);


                                if (scrollToTop && linearLayoutManager != null) {
                                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                                }
                                shouldRecallSearchAPI = true;

                            } else {
                                if (!searchOpened) {
                                    DialogPopup.alertPopup(activity, "", message);

                                }
                            }



                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        retryDialog(DialogErrorType.SERVER_ERROR, latLng, loader, false, scrollToTop, categoryObject, typeApi);
                    }
                    isMenusApiInProgress = false;
                    if(noOfCategories==1){
                        //only one category exists no need to hit again
                        pendingCategoryApi=null;
                    }
                    if(pendingCategoryApi!=null){
                         MenusResponse.Category category = new MenusResponse.Category(pendingCategoryApi.getId(),pendingCategoryApi.getCategoryName());
                         pendingCategoryApi=null;
                         switchCategory(category);


                    }else if(shouldRecallSearchAPI){
                         recallSearch(searchTextCurr);

                     }

                    Log.d(TAG, "getAllMenus: success"+System.currentTimeMillis());
                    DialogPopup.dismissLoadingDialog();


                }

                @Override
                public void failure(RetrofitError error) {

                    relativeLayoutNoMenus.setVisibility(View.GONE);
                    Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                    swipeRefreshLayout.setRefreshing(false);
                    activity.getTopBar().setPBSearchVisibility(View.GONE);
                    isMenusApiInProgress = false;
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader, false, scrollToTop, categoryObject, typeApi);


                }
            };

            if (searchOpened && !swipeRefreshLayout.isRefreshing()) {
                activity.getTopBar().setPBSearchVisibility(View.VISIBLE);
            }


            isMenusApiInProgress = true;
            Log.d(TAG, "getAllMenus: hit"+System.currentTimeMillis());
            if (isSearchingCase(searchText)) {
                params.put(Constants.KEY_SEARCH_TEXT, searchText);
                RestClient.getMenusApiService().fetchRestaurantViaSearchV2(params, callback);
            } else {
                RestClient.getMenusApiService().nearbyRestaurants(params, callback);
            }


        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
            isMenusApiInProgress = false;
        }
    }

    private void setSearcHintText() {
        if(activity.getCategoryOpened()!=null && activity.getCategoryIdOpened()>0){
            activity.getTopBar().etSearch.setHint("Search in " + activity.getCategoryOpened().getCategoryName());

        }else{
            activity.getTopBar().etSearch.setHint("Search..");

        }
    }

    private boolean serviceUnavailable;
    private void setUpServiceUnavailability(MenusResponse menusResponse) {
        relativeLayoutNoMenus.setVisibility((menusResponse.getRecentOrders().size() == 0 && menusResponse.getServiceUnavailable() == 1) ? View.VISIBLE : View.GONE);

        if (relativeLayoutNoMenus.getVisibility() == View.VISIBLE) {
            llNoMenusAddStore.setVisibility(activity.isDeliveryOpenInBackground() && Data.getDeliveryCustomerData()!=null && Data.getDeliveryCustomerData().getShowAddStore()?View.VISIBLE:View.GONE);
            activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
            activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
            activity.llCheckoutBarSetVisibilityDirect(View.GONE);
            if (searchOpened) {
                toggleSearch(true, true);
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideSoftKeyboard(activity, activity.getTopBar().etSearch);
                    }
                }, 100);
            }
            activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            recyclerViewRestaurant.setVisibility(View.GONE);
//            showCategoriesDropDown(false, 0);
            activity.hideMenusCartSelectedLayout();
        } else {
            activity.getTopBar().getLlSearchCart().setVisibility(serviceUnavailable ? View.GONE : View.VISIBLE);
            recyclerViewRestaurant.setVisibility(View.VISIBLE);
            if(!isKeyboardOpen){
                activity.getMenusCartSelectedLayout().checkForVisibility();

            }
        }
        activity.setTitleAlignment(false);
    }

    @NonNull
    private HashMap<String, String> getMenusApiHashMap(LatLng latLng, int categoryId) {
        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
        params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
        params.put(Constants.INTERATED, "1");
        params.put(Constants.PAGE_NO, String.valueOf(0));

        if(categoryId > 0){
            params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(categoryId));
        }
        //Sort Keys
        if (activity.getSortBySelected() != null) {
            JSONArray sortArray = new JSONArray();
            sortArray.put(activity.getSortBySelected().getKey());
            params.put(Constants.KEY_SORTING, sortArray.toString());

        }
        //Quick Filter Keys
        if (activity.getFilterSelected() != null && activity.getFilterSelected().size() > 0) {
            JSONArray filtersSelected = new JSONArray();
            for (MenusResponse.KeyValuePair filter : activity.getFilterSelected()) {
                filtersSelected.put(filter.getKey());
            }
            params.put(Constants.KEY_FILTERS, filtersSelected.toString());
        }

        //Cuisines List
        if (activity.getCuisinesSelected() != null && activity.getCuisinesSelected().size() > 0) {
            ArrayList<Integer> cusiinesSelectedId = new ArrayList<>();
            for (FilterCuisine cuisine : activity.getCuisinesSelected()) {
                cusiinesSelectedId.add(cuisine.getId());
            }
            params.put(Constants.KEY_CUISINE_SELECTED, cusiinesSelectedId.toString());
        }

        new HomeUtil().putDefaultParams(params);
        return params;
    }


    public void fetchNextPage() {

//        if(activity.getCategoryIdOpened() < 0){
//            return;
//        }

        if (!MyApplication.getInstance().isOnline()) {
            retryDialog(DialogErrorType.NO_NET, activity.getMenuRefreshLatLng(), false, true, false,activity.getCategoryOpened(), TYPE_API_MENUS_REFRESH);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        HashMap<String, String> params = getMenusApiHashMap(activity.getMenuRefreshLatLng(), activity.getCategoryIdOpened());
        params.put(Constants.PAGE_NO, String.valueOf(currentPageCount));
        deliveryHomeAdapter.showPaginationProgressBar(true, true);
        isPagingApiInProgress = true;
        Callback<MenusResponse> callback = new Callback<MenusResponse>() {
            @Override
            public void success(MenusResponse menusResponse, Response response) {
                isPagingApiInProgress = false;
                activity.getTopBar().setPBSearchVisibility(View.GONE);
                relativeLayoutNoMenus.setVisibility(View.GONE);


                boolean isProgressBarRemoved = false;
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getAllProducts response = " + responseStr);
                try {
                    JSONObject jObj = new JSONObject(responseStr);
                    String message = menusResponse.getMessage();
                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {

                            //set Variables for pagination
                            currentPageCount++;
                            hasMorePages =  menusResponse.isPageLengthComplete();
                            deliveryHomeAdapter.setList(menusResponse, true, hasMorePages);
                            isProgressBarRemoved = true;//Set list removes progress bar to accumulate animation of both insert and delete in one go


                        } else {
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if(!isProgressBarRemoved){
                    deliveryHomeAdapter.showPaginationProgressBar(false, true);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                isPagingApiInProgress = false;
                relativeLayoutNoMenus.setVisibility(View.GONE);
                isMenusApiInProgress = false;
                activity.getTopBar().setPBSearchVisibility(View.GONE);
                deliveryHomeAdapter.showPaginationProgressBar(false, true);
                retryDialog(DialogErrorType.CONNECTION_LOST, activity.getMenuRefreshLatLng(), false, true, false,activity.getCategoryOpened(), TYPE_API_MENUS_REFRESH);

            }
        };

        if (isSearchingCase(searchText)) {
            params.put(Constants.KEY_SEARCH_TEXT, searchText);
            RestClient.getMenusApiService().fetchRestaurantViaSearchV2(params, callback);
        } else {
            RestClient.getMenusApiService().nearbyRestaurants(params, callback);
        }


    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader, final boolean isPagination, final boolean scrollToTop, final MenusResponse.Category category, final int typeApi) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (isPagination) {
                            fetchNextPage();
                        } else {
                            getAllMenus(loader, latLng, scrollToTop,category , typeApi);

                        }
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                }, Data.getCurrentIciciUpiTransaction(activity.getAppType()) == null);
    }


    private void showPromoFailedAtSignupDialog() {
        try {
            if (Data.userData.getPromoSuccess() == 0) {
                DialogPopup.alertPopupWithListener(activity, "",
                        Data.userData.getPromoMessage(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MenuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag(),activity,activity.getSelectedLatLng());
                            }
                        });
                Data.userData.setPromoSuccess(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopup() {
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
            }
        }).showNoDeliveryDialog();

    }

    private List<Object> previousVendors;
    public void toggleSearch(boolean clearEt, boolean doChangesWithAdapter) {
        if (searchOpened) {
            searchOpened = false;
            activity.getTopBar().etSearch.setText("");
            if (activity.getSearchedRestaurantIds() != null)
                activity.getSearchedRestaurantIds().clear();

            if (doChangesWithAdapter) {
                if(activity.getCategoryIdOpened() < 0 || noOfCategories ==1) {
                    if (previousVendors != null) {
                        deliveryHomeAdapter.setList(previousVendors);
                    }
                    deliveryHomeAdapter.hideCateogiresBar(false);

                } else {

                        getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_CATEGORY_CHANGE);


                }

            }


            if (isKeyboardOpen) {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                activity.rlfabViewFatafat.setVisibility(View.GONE);
            }
            if (relativeLayoutNoMenus.getVisibility() == View.VISIBLE) {
                activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
            }
            activity.getTopBar().animateSearchBar(false);
            try {
                Utils.hideSoftKeyboard(activity, activity.getTopBar().etSearch);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            searchOpened = true;

            if (clearEt) {
                activity.getTopBar().etSearch.setText("");
            } else {
                activity.getTopBar().etSearch.setText(searchText);
                activity.getTopBar().etSearch.setSelection(activity.getTopBar().etSearch.getText().length());
            }



            activity.getTopBar().imageViewMenu.setVisibility(View.GONE);
            activity.getTopBar().imageViewBack.setVisibility(View.VISIBLE);
            activity.getTopBar().title.setVisibility(View.GONE);
            activity.getTopBar().llSearchContainer.setVisibility(View.VISIBLE);
            activity.getTopBar().setSearchVisibility(View.VISIBLE);
            activity.getTopBar().rlSearch.setVisibility(View.GONE);
            activity.getTopBar().getLlTopBarDeliveryAddress().setVisibility(View.GONE);
            activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            activity.getTopBar().etSearch.requestFocus();
            Utils.showSoftKeyboard(activity, activity.getTopBar().etSearch);
            if(doChangesWithAdapter){
                previousVendors = deliveryHomeAdapter.getDataToDisplay();
                deliveryHomeAdapter.hideCateogiresBar(true);

            }
        }
    }

    public boolean getSearchOpened() {
        return searchOpened;
    }

    public void applyFilter(boolean scrollToTop) {
        if (scrollToTop) {
            getAllMenus(true, activity.getMenuRefreshLatLng(), true,activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_FILTER);
        }
    }


    private static void checkIciciPaymentStatusApi(final FreshActivity activity) {
        int apptype = activity.isDeliveryOpenInBackground()? AppConstant.ApplicationType.DELIVERY_CUSTOMER: AppConstant.ApplicationType.MENUS;

        // if we have any pay via fatafat chat order pending
        boolean shouldCheckForFeedOrder = activity.isDeliveryOpenInBackground() && Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED)!=null;
        if (Data.getCurrentIciciUpiTransaction(apptype) != null ) {
            activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(apptype));
            ApiCurrentStatusIciciUpi.checkIciciPaymentStatusApi(activity, true, new ApiCurrentStatusIciciUpi.ApiCurrentStatusListener() {
                @Override
                public void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus) {
                    activity.getMenusCartSelectedLayout().getRlMenusCartSelectedInner().performClick();
                }
            },apptype);
        } else if(shouldCheckForFeedOrder){

            // open fugu chat and then launch fatafatchatPay, so hit order history to get fugu channel data
            fetchOrderHistory(activity,Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED).getOrderId());

        }

    }

    /**
     * Fetch order history
     * @param context calling context
     * @param orderId the order id
     */
    private static void fetchOrderHistory(final Activity context, int orderId){

        try {
            DialogPopup.showLoadingDialog(context, context.getResources().getString(R.string.loading));

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));
            params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.FEED.getOrdinal()));
            params.put(Constants.KEY_CLIENT_ID, Config.getFeedClientId());
            params.put(Constants.INTERATED, "1");

            Callback<HistoryResponse> callback = new Callback<HistoryResponse>() {
                @Override
                public void success(HistoryResponse historyResponse, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(context, jObj)) {
                            int flag = jObj.getInt(Constants.KEY_FLAG);
                            String message = JSONParser.getServerMessage(jObj);
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                HistoryResponse.Datum datum = historyResponse.getData().get(0);
                                //launch fugu and then fatafatChatPay
                                try {
                                    if(!TextUtils.isEmpty(datum.getFuguChannelId())){
                                        FuguConfig.getInstance().openChatByTransactionId(datum.getFuguChannelId()
                                                ,String.valueOf(Data.getFuguUserData().getUserId()),
                                                datum.getFuguChannelName(), datum.getFuguTags());
                                    }else{
                                        FuguConfig.getInstance().openChat(context, Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    product.clicklabs.jugnoo.utils.Utils.showToast(context, context.getString(R.string.something_went_wrong));
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        //open fatafat chat pay
                                        context.startActivity(new Intent(context,FatafatChatPayActivity.class)
                                                .putExtra(Constants.KEY_IS_UPI_PENDING,true));
                                    }
                                },200);


                            } else {
                                DialogPopup.alertPopup(context, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        retryDialog(context,DialogErrorType.SERVER_ERROR);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {

                    DialogPopup.dismissLoadingDialog();
                    retryDialog(context, DialogErrorType.CONNECTION_LOST);
                }
            };
            new HomeUtil().putDefaultParams(params);
            RestClient.getFatafatApiService().getCustomOrderHistory(params, callback);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void retryDialog(final Activity activity, final DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        // try again
                        fetchOrderHistory(activity,Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED).getOrderId());

                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                },true);

    }

    private String searchText = "";
    private boolean isMenusApiInProgress = false;

    public void searchRestaurant(String s) {
        if (searchOpened) {
            int oldLength = searchText.length();
            searchText = s;
            if (searchText.length() > 2) {
                getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_SEARCH);
            } else {
                if (oldLength > s.length() && oldLength >= 1 && s.length() == 0) {
                    getAllMenus(false, activity.getSelectedLatLng(), true,activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_SEARCH);
                }
            }
        }
    }

    private void recallSearch(String previousSearchText) {
        if (searchOpened && !searchText.trim().equalsIgnoreCase(previousSearchText)) {
            getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_SEARCH);
        }
    }

/*
    @Override
    public void onCategoryClick(MenusResponse.Category category) {
        if(activity.getCategoryIdOpened() != category.getId()) {
            switchCategory(category, false);
        }
    }

    @Override
    public void onDropDownToggle(boolean shown) {
        try {
            activity.getMenusCartSelectedLayout().disableView(!shown);
            if(!isHidden()) {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(shown ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    public String getSearchText() {
        return searchText;
    }

   /* public boolean isCategoryDropDownVisible() {
        return deliveryDisplayCategoriesView!=null && deliveryDisplayCategoriesView.isDropDownVisible();
    }*/

/*    private void showCategoriesDropDown(boolean show, int categoriesCount){
        RelativeLayout.LayoutParams paramsMain = (RelativeLayout.LayoutParams) rlMainContainer.getLayoutParams();
        if(activity.getAppType() == AppConstant.ApplicationType.DELIVERY_CUSTOMER && show && categoriesCount > 0){
            deliveryDisplayCategoriesView.setRootVisibility(View.VISIBLE);
            paramsMain.setMargins(0, activity.getResources().getDimensionPixelSize(R.dimen.height_category_bar), 0, 0);
            deliveryDisplayCategoriesView.setViewAccCategoriesCount(categoriesCount);
        } else {
            deliveryDisplayCategoriesView.setRootVisibility(View.GONE);
            paramsMain.setMargins(0, 0, 0, 0);
        }
        rlMainContainer.setLayoutParams(paramsMain);
    }*/

    private void hitApiRecommendRestaurant(int categoryId, String restaurantName, String locality, String telephone) {
        try {
            if(TextUtils.isEmpty(restaurantName)){
                product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getString(R.string.restaurant_name_is_neccessary));
                return;
            }
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, "");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_RESTAURANT_NAME, restaurantName);
                params.put(Constants.KEY_RESTAURANT_ADDRESS, locality);
                params.put(Constants.KEY_RESTAURANT_PHONE, telephone);
                if(categoryId > 0) {
                    params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(categoryId));
                }

                new HomeUtil().putDefaultParams(params);
                RestClient.getMenusApiService().suggestRestaurant(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt productsResponse, Response response) {
                        try {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                    DialogPopup.alertPopupWithListener(activity,
                                            activity.getString(R.string.thanks_for_recommendation),
                                            productsResponse.getMessage(),
                                            activity.getString(R.string.ok),
                                            null, false, true, true);
                                    if(deliveryHomeAdapter!=null){
                                        deliveryHomeAdapter.resetForm();

                                    }
                                } else {
                                    DialogPopup.alertPopup(activity, "", productsResponse.getMessage());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "fetchRestaurantViaSearch error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSearchingCase(String searchTextCurr){
        return (searchOpened && searchTextCurr.length() > 2);
    }

	public boolean isServiceUnavailable() {
		return serviceUnavailable;
	}

	public boolean iSChildCategoryOpen(){
        return activity.isDeliveryOpenInBackground() && noOfCategories>1&& activity.getCategoryIdOpened()>0;
    }

    public boolean toggleFatafatChatIconVisibility(){
        if(Data.userData!=null && Data.userData.getDeliveryCustomerEnabled()==1 &&
                activity.getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER  && chatAvailable){
            activity.rlfabViewFatafat.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    public boolean isMenusApiInProgress() {
        return isMenusApiInProgress;
    }

    private MenusResponse.Category pendingCategoryApi;
    public void setPendingCategoryAPI(MenusResponse.Category category) {
        this.pendingCategoryApi = category;
    }
}
