package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.MenusRestaurantAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
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

    private RelativeLayout linearLayoutRoot;
    private MenusRestaurantAdapter menusRestaurantAdapter;
    private ImageView imageViewNoMenus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewRestaurant;
    private EditText editTextSearch;

    private View rootView;
    private FreshActivity activity;

    private MenusResponse menusResponse;
    private ArrayList<MenusResponse.Vendor> vendors = new ArrayList<>();

    public MenusFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

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

        editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch); editTextSearch.setTypeface(Fonts.mavenMedium(activity));

        recyclerViewRestaurant = (RecyclerView) rootView.findViewById(R.id.recyclerViewRestaurant);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewRestaurant.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRestaurant.setHasFixedSize(false);

        imageViewNoMenus = (ImageView) rootView.findViewById(R.id.imageViewNoMenus);
        imageViewNoMenus.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);

        menusRestaurantAdapter = new MenusRestaurantAdapter(activity, vendors, new MenusRestaurantAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int position, SubItem slot) {

            }
        });

        recyclerViewRestaurant.setAdapter(menusRestaurantAdapter);

        getAllMenus(true);

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

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden()) {
            getAllMenus(activity.isRefreshCart());
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
                getAllMenus(true);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRefresh() {
        getAllMenus(false);
    }

    public void getAllMenus(final boolean loader) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                ProgressDialog progressDialog = null;
                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");

                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getMenusApiService().nearbyRestaurants(params, new Callback<MenusResponse>() {
                    @Override
                    public void success(MenusResponse menusResponse, Response response) {
                        imageViewNoMenus.setVisibility(View.GONE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()){
                                    MenusFragment.this.menusResponse = menusResponse;
                                    menusRestaurantAdapter.setList((ArrayList<MenusResponse.Vendor>) menusResponse.getVendors());

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
                        imageViewNoMenus.setVisibility(View.GONE);
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        retryDialog(DialogErrorType.CONNECTION_LOST);

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        imageViewNoMenus.setVisibility(View.VISIBLE);

        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllMenus(true);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


}
