package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCurrentStatusIciciUpi;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.utils.Utils;
import com.sabkuchfresh.utils.WrapContentLinearLayoutManager;
import com.sabkuchfresh.widgets.DeliveryDisplayCategoriesView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction,
        DeliveryDisplayCategoriesView.Callback {
    private final String TAG = MenusFragment.class.getSimpleName();

    private RelativeLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private DeliveryHomeAdapter deliveryHomeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewRestaurant;
    private TextView textViewNothingFound;
    private DeliveryDisplayCategoriesView deliveryDisplayCategoriesView;
    private RelativeLayout rlMainContainer;
    private View vDividerLocation;

    private View rootView;
    private FreshActivity activity;


    private ArrayList<String> status = new ArrayList<>();

    PushDialog pushDialog;
    private boolean resumed = false, searchOpened = false;
    private KeyboardLayoutListener keyboardLayoutListener;
    private WrapContentLinearLayoutManager linearLayoutManager;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private boolean isPagingApiInProgress;
    private boolean hasMorePages;

    public MenusFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus, container, false);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        activity.setDeliveryAddressView(rootView);
        activity.setCategoryIdOpened(-1);

        deliveryDisplayCategoriesView = new DeliveryDisplayCategoriesView(activity,
                rootView.findViewById(R.id.rLCategoryDropDown), this);

        Data.AppType = AppConstant.ApplicationType.MENUS;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);
        GAUtils.trackScreenView(MENUS + HOME);

        llRoot = (RelativeLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(Data.userData.getUserId())) {
                MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rlMainContainer = (RelativeLayout) rootView.findViewById(R.id.rlMainContainer);
        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView) rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

        textViewNothingFound = (TextView) rootView.findViewById(R.id.textViewNothingFound);
        textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        linearLayoutManager = new WrapContentLinearLayoutManager(activity);
        recyclerViewRestaurant.setLayoutManager(linearLayoutManager);
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);
        /*textViewNoMenus = (TextView) rootView.findViewById(R.id.textViewNoMenus); textViewNoMenus.setTypeface(Fonts.mavenMedium(activity));*/


        vDividerLocation = rootView.findViewById(R.id.vDividerLocation);
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
            public void openCategory(int categoryId) {
                switchCategory(categoryId, false);
            }

            @Override
            public void apiRecommendRestaurant(String restaurantName, String locality, String telephone) {

                hitApiRecommendRestaurant(restaurantName,locality,telephone);
                GAUtils.event(GAAction.MENUS, GAAction.HOME , GAAction.NEW_RESTAURANT + GAAction.SUBMITTED);


            }
        }, recyclerViewRestaurant, status);

        recyclerViewRestaurant.setAdapter(deliveryHomeAdapter);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);

        try {
            if (Data.getMenusData() != null && Data.getMenusData().getPendingFeedback() == 1) {

                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        activity.openFeedback();
                    }
                }, 300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (Data.userData.getPromoSuccess() == 0) {
                showPromoFailedAtSignupDialog();
            } else if (Data.getMenusData().getIsFatafatEnabled() == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
                Data.getMenusData().setIsFatafatEnabled(AppConstant.IsFatafatEnabled.ENABLED);
                showPopup();
            } else if (Data.getMenusData().getPopupData() != null) {
                pushDialog = new PushDialog(activity, new PushDialog.Callback() {
                    @Override
                    public void onButtonClicked(int deepIndex) {

                    }
                });
                pushDialog.show(Data.getMenusData().getPopupData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        activity.getTopBar().ivSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTopBar().etSearch.setText("");
            }
        });

        keyboardLayoutListener = new KeyboardLayoutListener(llRoot,
                (TextView) rootView.findViewById(R.id.tvScroll), new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                if (activity.getTopFragment() instanceof MenusFragment) {
                    activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                }
            }

            @Override
            public void keyBoardClosed() {
                if (activity.getTopFragment() instanceof MenusFragment) {
                    if (Prefs.with(activity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }
                }
            }
        });
        keyboardLayoutListener.setResizeTextView(false);

        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
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
                }

                try {
                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    int percentage = (int) (100.0 * offset / (float) (range - extent));

                    if (percentage > 0 && percentage % 10 == 0) {
                        GAUtils.event(MENUS, HOME + LIST_SCROLLED, percentage + "%");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });
        return rootView;
    }

    public void switchCategory(int categoryId, boolean isBackPressed) {

        if(deliveryDisplayCategoriesView==null)
            return;

        if(isBackPressed && deliveryDisplayCategoriesView.isDropDownVisible()){
            deliveryDisplayCategoriesView.toggleDropDown();
            return;
        }
        activity.setCategoryIdOpened(categoryId);
        getAllMenus(true, activity.getSelectedLatLng(), true);
        deliveryDisplayCategoriesView.setCategoryLabelIcon(categoryId);
        activity.setMenusFilterVisibility(activity.getCategoryIdOpened() > 0 ? View.VISIBLE : View.GONE);
    }

    private long lastTimeRefreshed = System.currentTimeMillis();
    private static final long MAX_REFRESH_INTERVAL = 60*1000;
    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && resumed) {
            if(activity.isRefreshCart()
                    || System.currentTimeMillis()-lastTimeRefreshed >= MAX_REFRESH_INTERVAL){
                activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);
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
                if (activity.openVendorMenuFragmentOnBack) {
                    activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                    activity.openVendorMenuFragmentOnBack = false;
                    return;
                }
                activity.fragmentUISetup(this);
                if (!activity.isOrderJustCompleted()) {
                    activity.setAddressTextToLocationPlaceHolder();
                } else {
                    recyclerViewRestaurant.postDelayed(runnableScrollToTop, 100);
                }
                activity.resumeMethod();
                if (searchOpened) {
                    searchOpened = false;
                    toggleSearch(false);
                }

                final boolean refreshCartFinal = activity.isRefreshCart();
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshCartFinal) {
                            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);
                        }
                        activity.setRefreshCart(false);
                    }
                }, 300);
                activity.getMenusCartSelectedLayout().checkForVisibility();
            } else {
                activity.getMenusCartSelectedLayout().setVisibility(View.GONE);
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
            deliveryHomeAdapter.removeHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
        ASSL.closeActivity(llRoot);
        System.gc();
    }


    @Override
    public void onRefresh() {
        getAllMenus(false, activity.getSelectedLatLng(), true);
    }

    private int currentPageCount = 1;

    public void getAllMenus(final boolean loader, final LatLng latLng, final boolean scrollToTop) {
        final String searchTextCurr = searchText;
        try {
            if (searchOpened && isMenusApiInProgress)
                return;

            if (!MyApplication.getInstance().isOnline()) {
                retryDialog(DialogErrorType.NO_NET, latLng, loader, false, scrollToTop);
                swipeRefreshLayout.setRefreshing(false);
                return;
            }


            if (loader)
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

            HashMap<String, String> params = getMenusApiHashMap(latLng);
            Callback<MenusResponse> callback = new Callback<MenusResponse>() {
                @Override
                public void success(final MenusResponse menusResponse, Response response) {
                    lastTimeRefreshed = System.currentTimeMillis();
                    DialogPopup.dismissLoadingDialog();
                    swipeRefreshLayout.setRefreshing(false);
                    activity.getTopBar().setPBSearchVisibility(View.GONE);
                    isMenusApiInProgress = false;
                    recallSearch(searchTextCurr);
                    relativeLayoutNoMenus.setVisibility(View.GONE);
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "getAllProducts response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        String message = menusResponse.getMessage();
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {

                                currentPageCount = 1;
                                hasMorePages =  menusResponse.getCategories()!=null && menusResponse.isPageLengthComplete();
                                status.clear();
                                status.addAll(menusResponse.getRecentOrdersPossibleStatus());

                                // check if only one category is coming view will be set like single category expanded
                                if (activity.getCategoryIdOpened() < 0) {
                                    if(!isSearchingCase()) {
                                        if (menusResponse.getCategories().size() == 1) {
                                            activity.setCategoryIdOpened(menusResponse.getCategories().get(0).getId());
                                        }
                                        activity.setMenusResponse(menusResponse);
                                        deliveryDisplayCategoriesView.setCategories(menusResponse.getCategories());
                                    }
//                                    activity.getCuisinesSelected().clear();
//                                    activity.getFilterSelected().clear();
//                                    activity.setSortBySelected(null);
                                }
                                if (activity.getMenusFilterFragment() != null) {
                                    activity.getMenusFilterFragment().updateDataLists(menusResponse);
                                }
                                showCategoriesDropDown(activity.getMenusResponse().getCategories().size() > 1);
                                activity.setMenusFilterVisibility(activity.getCategoryIdOpened() > 0 ? View.VISIBLE : View.GONE);
                                deliveryHomeAdapter.setList(menusResponse, false, hasMorePages && activity.getCategoryIdOpened()>0);



                                activity.setMenuRefreshLatLng(latLng);
                                setUpServiceUnavailability(menusResponse);

                                checkIciciPaymentStatusApi(activity);


                                if (scrollToTop && linearLayoutManager != null) {
                                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                                }

                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        retryDialog(DialogErrorType.SERVER_ERROR, latLng, loader, false, scrollToTop);
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    relativeLayoutNoMenus.setVisibility(View.GONE);
                    Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    swipeRefreshLayout.setRefreshing(false);
                    activity.getTopBar().setPBSearchVisibility(View.GONE);
                    isMenusApiInProgress = false;
                    retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader, false, scrollToTop);

                }
            };

            if (searchOpened && !swipeRefreshLayout.isRefreshing()) {
                activity.getTopBar().setPBSearchVisibility(View.VISIBLE);
            }


            isMenusApiInProgress = true;
            if (isSearchingCase()) {
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

    private void setUpServiceUnavailability(MenusResponse menusResponse) {
        relativeLayoutNoMenus.setVisibility((menusResponse.getRecentOrders().size() == 0 && menusResponse.getServiceUnavailable() == 1) ? View.VISIBLE : View.GONE);

        if (relativeLayoutNoMenus.getVisibility() == View.VISIBLE) {
            activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
            activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
            activity.llCheckoutBarSetVisibilityDirect(View.GONE);
            if (searchOpened) {
                toggleSearch(true);
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideSoftKeyboard(activity, activity.getTopBar().etSearch);
                    }
                }, 100);
            }
            activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            recyclerViewRestaurant.setVisibility(View.GONE);
            showCategoriesDropDown(false);
        } else {
            activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
            activity.setMenusFilterVisibility(activity.getCategoryIdOpened() > 0 ? View.VISIBLE : View.GONE);
            recyclerViewRestaurant.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    private HashMap<String, String> getMenusApiHashMap(LatLng latLng) {
        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
        params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
        params.put(Constants.INTERATED, "1");
        params.put(Constants.PAGE_NO, String.valueOf(0));

        if(activity.getCategoryIdOpened() > 0){
            params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(activity.getCategoryIdOpened()));
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
        }

        new HomeUtil().putDefaultParams(params);
        return params;
    }


    public void fetchNextPage() {

        if(activity.getCategoryIdOpened() < 0){
            return;
        }

        if (!MyApplication.getInstance().isOnline()) {
            retryDialog(DialogErrorType.NO_NET, activity.getMenuRefreshLatLng(), false, true, false);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        HashMap<String, String> params = getMenusApiHashMap(activity.getMenuRefreshLatLng());
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
                            deliveryHomeAdapter.setList(menusResponse, true,hasMorePages && activity.getCategoryIdOpened()>0);
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
                retryDialog(DialogErrorType.CONNECTION_LOST, activity.getMenuRefreshLatLng(), false, true, false);

            }
        };

        if (isSearchingCase()) {
            params.put(Constants.KEY_SEARCH_TEXT, searchText);
            RestClient.getMenusApiService().fetchRestaurantViaSearchV2(params, callback);
        } else {
            RestClient.getMenusApiService().nearbyRestaurants(params, callback);
        }


    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader, final boolean isPagination, final boolean scrollToTop) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (isPagination) {
                            fetchNextPage();
                        } else {
                            getAllMenus(loader, latLng, scrollToTop);

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
                                activity.getMenuBar().menuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag());
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
    public void toggleSearch(boolean clearEt) {
        if (searchOpened) {
            searchOpened = false;
            activity.getTopBar().etSearch.setText("");
            if (activity.getSearchedRestaurantIds() != null)
                activity.getSearchedRestaurantIds().clear();
            if (previousVendors != null) {
                deliveryHomeAdapter.setList(previousVendors);
            }


            if (keyboardLayoutListener.getKeyBoardState() == 1) {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
            }
            if (relativeLayoutNoMenus.getVisibility() == View.VISIBLE) {
                activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
            }
            activity.getTopBar().animateSearchBar(false);

        } else {
            searchOpened = true;
            previousVendors = deliveryHomeAdapter.getDataToDisplay();

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
            activity.getTopBar().ivSearch.setVisibility(View.GONE);
            activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            activity.getTopBar().etSearch.requestFocus();
            Utils.showSoftKeyboard(activity, activity.getTopBar().etSearch);
        }
    }

    public boolean getSearchOpened() {
        return searchOpened;
    }

    public void applyFilter(boolean scrollToTop) {
        if (scrollToTop) {
            getAllMenus(true, activity.getMenuRefreshLatLng(), true);
        }
    }


    private static void checkIciciPaymentStatusApi(final FreshActivity activity) {
        if (Data.getCurrentIciciUpiTransaction(activity.getAppType()) != null) {
            activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(activity.getAppType()));
            ApiCurrentStatusIciciUpi.checkIciciPaymentStatusApi(activity, true, new ApiCurrentStatusIciciUpi.ApiCurrentStatusListener() {
                @Override
                public void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus) {
                    activity.getMenusCartSelectedLayout().getRlMenusCartSelectedInner().performClick();
                }
            });
        }

    }

    private String searchText = "";
    private boolean isMenusApiInProgress = false;

    public void searchRestaurant(String s) {
        if (searchOpened) {
            int oldLength = searchText.length();
            searchText = s;
            if (searchText.length() > 2) {
                getAllMenus(false, activity.getSelectedLatLng(), true);
            } else {
                if (oldLength > s.length() && oldLength >= 1 && s.length() == 0) {
                    getAllMenus(false, activity.getSelectedLatLng(), true);
                }
            }
        }
    }

    private void recallSearch(String previousSearchText) {
        if (searchOpened && !searchText.trim().equalsIgnoreCase(previousSearchText)) {
            getAllMenus(false, activity.getSelectedLatLng(), true);
        }
    }

    @Override
    public void onCategoryClick(MenusResponse.Category category) {
        if(activity.getCategoryIdOpened() != category.getId()) {
            switchCategory(category.getId(), false);
        }
    }

    @Override
    public void onDropDownToggle(boolean shown) {
        if(!isHidden()) {
            activity.getFabViewTest().setRelativeLayoutFABTestVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean isCategoryDropDownVisible() {
        return deliveryDisplayCategoriesView!=null && deliveryDisplayCategoriesView.isDropDownVisible();
    }

    private void showCategoriesDropDown(boolean show){
        RelativeLayout.LayoutParams paramsMain = (RelativeLayout.LayoutParams) rlMainContainer.getLayoutParams();
        if(show){
            deliveryDisplayCategoriesView.setRootVisibility(View.VISIBLE);
            paramsMain.setMargins(0, activity.getResources().getDimensionPixelSize(R.dimen.height_category_bar), 0, 0);
            vDividerLocation.setVisibility(View.VISIBLE);
        } else {
            deliveryDisplayCategoriesView.setRootVisibility(View.GONE);
            paramsMain.setMargins(0, 0, 0, 0);
            vDividerLocation.setVisibility(View.VISIBLE);
        }
        rlMainContainer.setLayoutParams(paramsMain);
    }

    private void hitApiRecommendRestaurant(String restaurantName, String locality, String telephone) {
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
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_RESTAURANT_NAME, restaurantName);
                params.put(Constants.KEY_RESTAURANT_ADDRESS, locality);
                params.put(Constants.KEY_RESTAURANT_PHONE, telephone);

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

    private boolean isSearchingCase(){
        return (searchOpened && searchText.length() > 2);
    }

}
