package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
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
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.utils.Utils;

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
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusFragment extends Fragment implements FlurryEventNames, SwipeRefreshLayout.OnRefreshListener{
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

        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(!TextUtils.isEmpty(Data.userData.getUserId())) {
                MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);
        /*textViewNoMenus = (TextView) rootView.findViewById(R.id.textViewNoMenus); textViewNoMenus.setTypeface(Fonts.mavenMedium(activity));*/


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);

        menusRestaurantAdapter = new MenusRestaurantAdapter(activity, vendors, recentOrder,status, new MenusRestaurantAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int position, MenusResponse.Vendor vendor) {
                getVendorMenu(vendor);
                Utils.hideSoftKeyboard(activity, relativeLayoutNoMenus);
            }

            @Override
            public void onNotify(int count) {
      /*          textViewNoMenus.setVisibility(count > 0 || vendors.size() == 0 ? View.GONE : View.VISIBLE);*/
            }
        });

        recyclerViewRestaurant.setAdapter(menusRestaurantAdapter);

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);

        try {
            if(Data.getMenusData() != null && Data.getMenusData().getPendingFeedback() == 1) {
                new Handler().postDelayed(new Runnable() {
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
            if(Data.userData.getPromoSuccess() == 0) {
                showPromoFailedAtSignupDialog();
            } else if(Data.getMenusData().getIsFatafatEnabled() == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
                Data.getMenusData().setIsFatafatEnabled(AppConstant.IsFatafatEnabled.ENABLED);
                showPopup();
            } else if(Data.getMenusData().getPopupData() != null) {
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


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        activity.getTopBar().ivSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTopBar().etSearch.setText("");
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden() && resumed) {
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);
            activity.setRefreshCart(false);
        }
        resumed = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            activity.resumeMethod();
            menusRestaurantAdapter.applyFilter();
            activity.getTopBar().ivFilterApplied.setVisibility(menusRestaurantAdapter.filterApplied() ? View.VISIBLE : View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(activity.isRefreshCart()){
                        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MENUS);
                    }
                    activity.setRefreshCart(false);
                }
            }, 300);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(llRoot);
        System.gc();
    }

    @Override
    public void onRefresh() {
        getAllMenus(false, activity.getSelectedLatLng());
    }

    public void getAllMenus(final boolean loader, final LatLng latLng) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                ProgressDialog progressDialog = null;
                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");

                new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getMenusApiService().nearbyRestaurants(params, new Callback<MenusResponse>() {
                    @Override
                    public void success(MenusResponse menusResponse, Response response) {
                        relativeLayoutNoMenus.setVisibility(View.GONE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = menusResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()){
                                    activity.setMenusResponse(menusResponse);
                                    vendors = (ArrayList<MenusResponse.Vendor>) menusResponse.getVendors();

                                    recentOrder.clear();
                                    recentOrder.addAll(menusResponse.getRecentOrders());
                                    status.clear();
                                    status.addAll(menusResponse.getRecentOrdersPossibleStatus());

                                    menusRestaurantAdapter.setList(vendors);
                                    menusRestaurantAdapter.applyFilter();
                                    activity.getTopBar().ivFilterApplied.setVisibility(menusRestaurantAdapter.filterApplied() ? View.VISIBLE : View.GONE);
                                    relativeLayoutNoMenus.setVisibility((menusResponse.getRecentOrders().size() == 0
                                            && menusResponse.getVendors().size() == 0) ? View.VISIBLE : View.GONE);
                                    activity.setMenuRefreshLatLng(new LatLng(latLng.latitude, latLng.longitude));
                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        relativeLayoutNoMenus.setVisibility(View.GONE);
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        retryDialog(DialogErrorType.CONNECTION_LOST, latLng);

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET, latLng);
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllMenus(true, latLng);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    public void getVendorMenu(final MenusResponse.Vendor vendor) {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(vendor.getRestaurantId()));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                Log.i(TAG, "getVendorMenu params=" + params.toString());

                new HomeUtil().putDefaultParams(params);
                RestClient.getMenusApiService().restaurantMenu(params, new Callback<VendorMenuResponse>() {
                    @Override
                    public void success(VendorMenuResponse productsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getVendorMenu response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = productsResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                    activity.setVendorOpened(vendor);
                                    activity.setMenuProductsResponse(productsResponse);
                                    if(activity.menusSort == -1) {
                                        activity.menusSort = jObj.getInt(Constants.SORTED_BY);
                                    }
                                    if(vendor.getIsClosed()==1) {
                                        activity.clearMenusCart();
                                    }
                                    activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogVendorData(DialogErrorType.CONNECTION_LOST, vendor);
                    }
                });
            }
            else {
                retryDialogVendorData(DialogErrorType.NO_NET, vendor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void retryDialogVendorData(DialogErrorType dialogErrorType, final MenusResponse.Vendor vendor){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getVendorMenu(vendor);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void showPromoFailedAtSignupDialog(){
        try{
            if(Data.userData.getPromoSuccess() == 0) {
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
        } catch(Exception e){
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
        }).show();

    }

    public void openSearch(){
       if(searchOpened){
           searchOpened = false;
           activity.getTopBar().etSearch.setText("");
           activity.fragmentUISetup(this);
           activity.setDeliveryAddressViewVisibility(View.VISIBLE);
       } else {
           searchOpened = true;
           activity.getTopBar().etSearch.setText("");
           activity.getTopBar().imageViewMenu.setVisibility(View.GONE);
           activity.getTopBar().imageViewBack.setVisibility(View.VISIBLE);
           activity.getTopBar().title.setVisibility(View.GONE);
           activity.getTopBar().llSearchContainer.setVisibility(View.VISIBLE);

           activity.getTopBar().setSearchVisibility(View.VISIBLE);
           activity.getTopBar().ivSearch.setVisibility(View.GONE);
           activity.getTopBar().rlFilter.setVisibility(View.GONE);
           activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
           activity.setDeliveryAddressViewVisibility(View.GONE);

           activity.getTopBar().etSearch.requestFocus();
           Utils.showSoftKeyboard(activity, activity.getTopBar().etSearch);
       }
    }

    public boolean getSearchOpened(){
        return searchOpened;
    }

    public MenusRestaurantAdapter getMenusRestaurantAdapter(){
        return menusRestaurantAdapter;
    }

}
