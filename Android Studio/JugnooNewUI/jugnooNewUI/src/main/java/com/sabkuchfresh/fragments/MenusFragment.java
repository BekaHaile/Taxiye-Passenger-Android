package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.MenusRestaurantAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCurrentStatusIciciUpi;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
public class MenusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction {
    private final String TAG = MenusFragment.class.getSimpleName();

    private LinearLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private MenusRestaurantAdapter menusRestaurantAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewRestaurant;
    private TextView textViewNothingFound;

    private View rootView;
    private FreshActivity activity;

    private ArrayList<MenusResponse.Vendor> vendors = new ArrayList<>();
    private ArrayList<RecentOrder> recentOrder = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();

    PushDialog pushDialog;
    private boolean resumed = false, searchOpened = false;
    private KeyboardLayoutListener keyboardLayoutListener;
    private LinearLayoutManager linearLayoutManager;
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

        Data.AppType = AppConstant.ApplicationType.MENUS;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);

        GAUtils.trackScreenView(MENUS+HOME);

        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
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

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView) rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

        textViewNothingFound = (TextView) rootView.findViewById(R.id.textViewNothingFound);
        textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerViewRestaurant.setLayoutManager(linearLayoutManager);
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);
        /*textViewNoMenus = (TextView) rootView.findViewById(R.id.textViewNoMenus); textViewNoMenus.setTypeface(Fonts.mavenMedium(activity));*/


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);

        menusRestaurantAdapter = new MenusRestaurantAdapter(activity, vendors, recentOrder, status, new MenusRestaurantAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int vendorId) {
                activity.fetchRestaurantMenuAPI(vendorId, false, null, null, -1, null);
                Utils.hideSoftKeyboard(activity, relativeLayoutNoMenus);
            }

            @Override
            public void onNotify(int count) {
            }

            @Override
            public void onBannerInfoDeepIndexClick(int deepIndex) {
                Data.deepLinkIndex = deepIndex;
                if(activity != null) {
                    activity.openDeepIndex();
                }
            }
        }, recyclerViewRestaurant);

        recyclerViewRestaurant.setAdapter(menusRestaurantAdapter);

        recyclerViewRestaurant.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    int percentage = (int)(100.0 * offset / (float)(range - extent));

                    if(percentage > 0 && percentage % 10 == 0) {
                        GAUtils.event(MENUS, HOME + LIST_SCROLLED, percentage + "%");
                        Log.i("GA Logged", "scroll percentage: "+ percentage + "%");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
				if(getView() != null){
					activity.getMenusCartSelectedLayout().checkForVisibility();
				}
			}
		});

        recyclerViewRestaurant.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //To implement Pagination
                if (dy > 0) {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isPagingApiInProgress && hasMorePages) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            getAllMenus(true,lastMenusLatlng, true, false);
                        }
                    }
                }
            }




        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && resumed) {
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);
            activity.setRefreshCart(false);
        }
        resumed = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                if(activity.openVendorMenuFragmentOnBack){
                    activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                    activity.openVendorMenuFragmentOnBack = false;
                    return;
                }
				activity.fragmentUISetup(this);
				if(!activity.isOrderJustCompleted()) {
					activity.setAddressTextToLocationPlaceHolder();
				}
				activity.resumeMethod();
//                applyFilter(false);
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
        }
    }

    @Override
    public void onDestroyView() {
        try {
            menusRestaurantAdapter.removeHandler();
        } catch (Exception e) {
        }
        super.onDestroyView();
        ASSL.closeActivity(llRoot);
        System.gc();
    }


    @Override
    public void onRefresh() {
        getAllMenus(false, activity.getSelectedLatLng(), false, true);
    }

    private LatLng lastMenusLatlng;
    private int currentPageCount=1 ;
    public void getAllMenus(final boolean loader, final LatLng latLng, final boolean isPagination, final boolean scrollToTop) {

        try {
            if (MyApplication.getInstance().isOnline()) {
                this.lastMenusLatlng = latLng;
                ProgressDialog progressDialog = null;
                if(isPagination){
                    isPagingApiInProgress=true;
                    if(menusRestaurantAdapter!=null){
                        menusRestaurantAdapter.showProgressBar(true);
                    }
                }else{
                    if (loader)
                        progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));
                }

                if (activity.getTopFragment() instanceof MenusFragment) {
                    activity.getTopBar().ivFilterApplied.setVisibility(filterApplied() ? View.VISIBLE : View.GONE);
                }


                HashMap<String, String> params = new HashMap<>();

                //Sort Keys
                if (activity.getSortBySelected() != MenusFilterFragment.SortType.NONE) {
                    JSONArray sortArray = new JSONArray();
                    sortArray.put(("" + activity.getSortBySelected()).toLowerCase());
                    params.put(Constants.KEY_SORTING, sortArray.toString());

                }
                //Quick Filter Keys
                if (activity.getQuickFilterSelected() != null && activity.getQuickFilterSelected().size() > 0) {
                    params.put(Constants.KEY_FILTERS, (new JSONArray(activity.getQuickFilterSelected())).toString());
                }

                //Min Order amount filter
                if(activity.getMoSelected()!=MenusFilterFragment.MinOrder.NONE){
                    params.put(Constants.KEY_MIN_ORDER_AMT,"" +  activity.getMoSelected().getOrdinal());
                }

                //Cuisines List
                if(activity.getCuisinesSelected()!=null && activity.getCuisinesSelected().size()>0){

                    ArrayList<Integer> cusiinesSelectedId = new ArrayList<>();
                    for(FilterCuisine cuisine:activity.getCuisinesSelected()){
                        cusiinesSelectedId.add(cuisine.getId());

                    }
                    params.put(Constants.KEY_CUISINE_SELECTED, cusiinesSelectedId.toString());
                }


                //Search query
                if(activity.getSearchedRestaurantIds() != null && activity.getSearchedRestaurantIds().size() > 0){
                  /*  ArrayList<String> ids = new ArrayList<>();
                    for(Integer id:activity.getSearchedRestaurantIds()){
                        ids.add( "'"+id+"'");
                    }*/
                    params.put(Constants.RESTAURANT_IDS, activity.getSearchedRestaurantIds().toString());
                }



                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                params.put(Constants.PAGE_NO, isPagination?String.valueOf(currentPageCount):"0");


                new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;

                RestClient.getMenusApiService().nearbyRestaurants(params, new Callback<MenusResponse>() {
                    @Override
                    public void success(MenusResponse menusResponse, Response response) {
                        if(isPagination){
                            isPagingApiInProgress=false;
                        }
                        relativeLayoutNoMenus.setVisibility(View.GONE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = menusResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {

                                    //set Variables for pagination
                                    hasMorePages =  menusResponse.getVendors()!=null && menusResponse.getVendors().size()>0 && menusResponse.isPageLengthComplete();

                                    // sort vendors list according to searched ids
//                                    if(menusResponse.getVendors() != null && menusResponse.getVendors().size()>0
//                                            && activity.getSearchedRestaurantIds() != null && activity.getSearchedRestaurantIds().size() > 0){
//                                        Collections.sort(menusResponse.getVendors(), new Comparator<MenusResponse.Vendor>() {
//                                            @Override
//                                            public int compare(MenusResponse.Vendor o1, MenusResponse.Vendor o2) {
//                                                int i1 = activity.getSearchedRestaurantIds().indexOf(o1.getRestaurantId());
//                                                int i2 = activity.getSearchedRestaurantIds().indexOf(o2.getRestaurantId());
//                                                if(i1 != -1 && i2 != -1){
//                                                    return i1 - i2;
//                                                } else if(i1 != -1){
//                                                    return -1;
//                                                } else {
//                                                    return 1;
//                                                }
//                                            }
//                                        });
//                                    }


                                    if(isPagination){
                                        vendors.addAll((ArrayList<MenusResponse.Vendor>) menusResponse.getVendors());
                                        menusRestaurantAdapter.setList(vendors, menusResponse.getBannerInfos(),
                                                menusResponse.getStripInfo(), menusResponse.getShowBanner(), !hasMorePages);
                                        menusResponse.setVendors(vendors);
                                        activity.setMenusResponse(menusResponse);
                                        currentPageCount++;
                                        try {
                                            if (finalProgressDialog != null)
                                                finalProgressDialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return;

                                    }
                                    currentPageCount=1;
                                    activity.setMenusResponse(menusResponse);

                                    vendors = (ArrayList<MenusResponse.Vendor>) menusResponse.getVendors();

                                    recentOrder.clear();
                                    recentOrder.addAll(menusResponse.getRecentOrders());

                                    status.clear();
                                    status.addAll(menusResponse.getRecentOrdersPossibleStatus());

                                    menusRestaurantAdapter.setList(vendors, menusResponse.getBannerInfos(),
                                            menusResponse.getStripInfo(), menusResponse.getShowBanner(), !hasMorePages);
//                                    applyFilter(false);
                                    // TODO: 22/08/17 service unavailability check
                                    relativeLayoutNoMenus.setVisibility((menusResponse.getRecentOrders().size() == 0
                                            && menusResponse.getServiceUnavailable() == 1) ? View.VISIBLE : View.GONE);
                                    activity.setMenuRefreshLatLng(new LatLng(latLng.latitude, latLng.longitude));

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
                                    } else {
                                        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
                                    }

                                    activity.setFilterCuisinesLocal(null);
                                    checkIciciPaymentStatusApi(activity);
                                    if(scrollToTop && recyclerViewRestaurant!=null){
                                            recyclerViewRestaurant.smoothScrollToPosition(0);

                                    }




                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            try {
                                if (finalProgressDialog != null)
                                    finalProgressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            exception.printStackTrace();
                        }
                        try {
                            if (finalProgressDialog != null)
                                finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(isPagination){
                            isPagingApiInProgress=false;
                        }
                        relativeLayoutNoMenus.setVisibility(View.GONE);
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        try {
                            if (finalProgressDialog != null)
                                finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader, isPagination, scrollToTop);
                        if(isPagination && menusRestaurantAdapter!=null){
                            menusRestaurantAdapter.showProgressBar(false);
                        }

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET, latLng, loader, isPagination, scrollToTop);
                swipeRefreshLayout.setRefreshing(false);
                if(isPagination && menusRestaurantAdapter!=null){
                    menusRestaurantAdapter.showProgressBar(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader, final boolean isPagination, final boolean scrollToTop) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllMenus(loader, latLng, isPagination, scrollToTop);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                },Data.getCurrentIciciUpiTransaction(activity.getAppType())==null);
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
        //123
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
                //activity.orderComplete();
            }
        }).showNoDeliveryDialog();

    }

    public void toggleSearch(boolean clearEt) {
        if (searchOpened) {
            searchOpened = false;
            activity.getTopBar().etSearch.setText("");
            if(activity.getSearchedRestaurantIds()!=null)
            activity.getSearchedRestaurantIds().clear();
            getAllMenus(true, activity.getSelectedLatLng(), false, true);
          //  activity.fragmentUISetup(this);
            if (keyboardLayoutListener.getKeyBoardState() == 1) {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
            }
            if (relativeLayoutNoMenus.getVisibility() == View.VISIBLE) {
                activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                //activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
            }
            activity.getTopBar().animateSearchBar(false);
        } else {
            searchOpened = true;
            menusRestaurantAdapter.setSearchApiHitOnce(false);
            if (clearEt) {
                activity.getTopBar().etSearch.setText("");
            } else {
                activity.getTopBar().etSearch.setText(menusRestaurantAdapter.getSearchText());
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

    public MenusRestaurantAdapter getMenusRestaurantAdapter() {
        return menusRestaurantAdapter;
    }

    public void applyFilter(boolean scrollToTop){
//        menusRestaurantAdapter.applyFilter();

        if(scrollToTop) {
            getAllMenus(true, lastMenusLatlng, false, true);
        }

    }

    public boolean filterApplied() {
        return (activity.getCuisinesSelected().size() > 0
                || activity.getMoSelected() != MenusFilterFragment.MinOrder.NONE
                || activity.getDtSelected() != MenusFilterFragment.DeliveryTime.NONE
                || activity.getSortBySelected() != MenusFilterFragment.SortType.NONE
                || activity.getQuickFilterSelected().size() > 0);
    }

    private static void checkIciciPaymentStatusApi(final FreshActivity activity) {
        if(Data.getCurrentIciciUpiTransaction(activity.getAppType())!=null){
            activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(activity.getAppType()));
            ApiCurrentStatusIciciUpi.checkIciciPaymentStatusApi(activity, true, new ApiCurrentStatusIciciUpi.ApiCurrentStatusListener() {
                @Override
                public void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus) {
                    activity.getMenusCartSelectedLayout().getRlMenusCartSelectedInner().performClick();
                }
            });
        }

    }
}
