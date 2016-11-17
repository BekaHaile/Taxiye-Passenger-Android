package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.adapters.MenusRestaurantAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
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

    private RelativeLayout linearLayoutRoot, relativeLayoutNoMenus;
    private MenusRestaurantAdapter menusRestaurantAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewRestaurant;
    private EditText editTextSearch;
    private CardView cardViewSearch;

    private View rootView;
    private FreshActivity activity;

    private MenusResponse menusResponse;
    private ArrayList<MenusResponse.Vendor> vendors = new ArrayList<>();

    PushDialog pushDialog;
    private Bus mBus;

    public MenusFragment() {
    }



    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        mBus = (activity).getBus();

        Data.AppType = AppConstant.ApplicationType.MENUS;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);

        linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
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
        cardViewSearch = (CardView) rootView.findViewById(R.id.cardViewSearch);
        editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch); editTextSearch.setTypeface(Fonts.mavenMedium(activity));
        cardViewSearch.setVisibility(View.GONE);

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);

        menusRestaurantAdapter = new MenusRestaurantAdapter(activity, vendors, new MenusRestaurantAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int position, MenusResponse.Vendor vendor) {
                getVendorMenu(vendor);
            }
        });

        recyclerViewRestaurant.setAdapter(menusRestaurantAdapter);

        setLocalityAddressFirstTime();

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
            } else if(Data.getGroceryData().getIsFatafatEnabled() == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
                Data.getGroceryData().setIsFatafatEnabled(AppConstant.IsFatafatEnabled.ENABLED);
                showPopup();
            } else if(Data.getGroceryData().getPopupData() != null) {
                pushDialog = new PushDialog(activity, new PushDialog.Callback() {
                    @Override
                    public void onButtonClicked(int deepIndex) {

                    }
                });
                pushDialog.show(Data.getGroceryData().getPopupData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden()) {
            getAllMenus(activity.isRefreshCart(), getSelectedLatLng());
            activity.setRefreshCart(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            menusRestaurantAdapter.notifyDataSetChanged();
            activity.resumeMethod();
            if(activity.isRefreshCart()){
                getAllMenus(true, getSelectedLatLng());
            }
            activity.setRefreshCart(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }

    @Override
    public void onRefresh() {
        getAllMenus(false, getSelectedLatLng());
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
                                    MenusFragment.this.menusResponse = menusResponse;
                                    menusRestaurantAdapter.setList((ArrayList<MenusResponse.Vendor>) menusResponse.getVendors());
                                    relativeLayoutNoMenus.setVisibility(menusRestaurantAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
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
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                params.put(Constants.KEY_VENDOR_ID, String.valueOf(vendor.getVid()));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                Log.i(TAG, "getVendorMenu params=" + params.toString());

                RestClient.getMenusApiService().vendorMenu(params, new Callback<ProductsResponse>() {
                    @Override
                    public void success(ProductsResponse productsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getVendorMenu response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = productsResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
                                    activity.setVendorOpened(vendor);
                                    activity.setProductsResponse(productsResponse);
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

    @Subscribe
    public void onAddressUpdated(AddressAdded event) {
        try {
            if (event.flag) {
                setAddressAndFetchMenus();
                saveMenusLastAddress();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddressAndFetchMenus(){
        try {
            String[] arr = null;
            if(activity.getSelectedAddress().contains(",")){
                arr = activity.getSelectedAddress().split(", ");
            } else {
                arr = activity.getSelectedAddress().split(" ");
            }
            String address = "";
            if(arr.length > 1){
				address = arr[0]+", "+arr[1];
			} else if(arr.length > 0){
				address = arr[0];
			}
            activity.getTopBar().textViewLocationValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30f * ASSL.Xscale());
            activity.getTopBar().textViewLocationValue.setText(address);
            getAllMenus(true, activity.getSelectedLatLng());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveMenusLastAddress(){
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = new SearchResult(activity.getSelectedAddressType(), activity.getSelectedAddress(), "",
					activity.getSelectedLatLng().latitude, activity.getSelectedLatLng().longitude);
            searchResultLocality.setId(activity.getSelectedAddressId());
            searchResultLocality.setIsConfirmed(1);
            Prefs.with(activity).save(Constants.SP_MENUS_LAST_ADDRESS_OBJ, gson.toJson(searchResultLocality, SearchResult.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMenusLastAddressToActivityVariables(){
        try {
            Gson gson = new Gson();
            SearchResult searchResultLocality = gson.fromJson(Prefs.with(activity)
                    .getString(Constants.SP_MENUS_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
            if(!TextUtils.isEmpty(searchResultLocality.getAddress())){
                activity.setSelectedAddress(searchResultLocality.getAddress());
                activity.setSelectedLatLng(searchResultLocality.getLatLng());
                activity.setSelectedAddressId(searchResultLocality.getId());
                activity.setSelectedAddressType(searchResultLocality.getName());
                setAddressAndFetchMenus();
            } else {
                getAddressAndFetchMenus(getSelectedLatLng());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LatLng getSelectedLatLng(){
        if(activity.getSelectedLatLng() != null){
            return activity.getSelectedLatLng();
        } else {
            return new LatLng(Data.latitude, Data.longitude);
        }
    }

    private void setLocalityAddressFirstTime(){
        if(activity.getSelectedLatLng() == null || TextUtils.isEmpty(activity.getSelectedAddress())){
            setMenusLastAddressToActivityVariables();
        } else {
            setAddressAndFetchMenus();
        }
    }


    private void getAddressAndFetchMenus(final LatLng currentLatLng){
        try {
            DialogPopup.showLoadingDialog(getActivity(), "Loading...");
            RestClient.getGoogleApiServices().geocode(currentLatLng.latitude + "," + currentLatLng.longitude,
                    "en", false, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            try {
                                String resp = new String(((TypedByteArray) response.getBody()).getBytes());
                                GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(resp);
                                String address = gapiAddress.formattedAddress;
                                activity.setSelectedAddress(address);
                                activity.setSelectedLatLng(currentLatLng);
                                activity.setSelectedAddressId(0);
                                activity.setSelectedAddressType("");
                                setAddressAndFetchMenus();
                                DialogPopup.dismissLoadingDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
        }

    }

}
