package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshSuperCategoriesAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View rootView;
    private LinearLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private FreshActivity activity;
    private RecyclerView rvFreshSuper;
    private FreshSuperCategoriesAdapter adapter;
    private TextView textViewNothingFound;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_home, container, false);
        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            activity = (FreshActivity) getActivity();
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.fragmentUISetup(this);
        activity.setDeliveryAddressView(rootView);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.white);
        swipeContainer.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        swipeContainer.setSize(SwipeRefreshLayout.DEFAULT);
        swipeContainer.setEnabled(true);
        //swipeContainer.setVisibility(View.GONE);

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);
        rvFreshSuper = (RecyclerView) rootView.findViewById(R.id.rvFreshSuper);
        rvFreshSuper.setItemAnimator(new DefaultItemAnimator());
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)){
                    case FreshSuperCategoriesAdapter.SINGLE_ITEM:
                        return 2;
                    case FreshSuperCategoriesAdapter.MAIN_ITEM:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        rvFreshSuper.setLayoutManager(gridLayoutManager);
        adapter = new FreshSuperCategoriesAdapter(activity, new FreshSuperCategoriesAdapter.Callback() {
            @Override
            public void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory) {
                activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer(), superCategory);
            }
        });

        rvFreshSuper.setAdapter(adapter);

        relativeLayoutNoMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            adapter.notifyDataSetChanged();
            activity.fragmentUISetup(this);
            if(activity.getCartChangedAtCheckout()){
                activity.updateCartFromSP();
                activity.updateCartValuesGetTotalPrice();
            }
            activity.setCartChangedAtCheckout(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(activity.isRefreshCart()){
                        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);
                    }
                    activity.setRefreshCart(false);
                }
            }, 300);
        }

        if(relativeLayoutNoMenus.getVisibility() == View.VISIBLE){
            activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
            activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
        }
    }


    public void getSuperCategoriesAPI() {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
                params.put(Constants.INTERATED, "1");

                new HomeUtil().putDefaultParams(params);
                RestClient.getFreshApiService().getSuperCategories(params, new Callback<SuperCategoriesData>() {
                    @Override
                    public void success(final SuperCategoriesData superCategoriesData, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if(superCategoriesData.getFlag() == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                oSnapNotAvailableCase(superCategoriesData.getMessage());
                            } else if(superCategoriesData.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                if(activity.getCartCityId() == -1){
                                    activity.setCartCityId(superCategoriesData.getDeliveryInfo().getCityId());
                                }

                                if(!activity.checkForCityChange(superCategoriesData.getDeliveryInfo().getCityId(),
                                        new FreshActivity.CityChangeCallback() {
                                            @Override
                                            public void onYesClick() {
                                                setSuperCategoriesDataToUI(superCategoriesData);
                                            }

                                            @Override
                                            public void onNoClick() {

                                            }
                                        })){
                                    setSuperCategoriesDataToUI(superCategoriesData);
                                }

                            } else {
                                DialogPopup.alertPopup(activity, "", superCategoriesData.getMessage());
                                stopOhSnap();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogSuperCategoriesAPI(DialogErrorType.SERVER_ERROR);
                            stopOhSnap();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialogSuperCategoriesAPI(DialogErrorType.CONNECTION_LOST);
                        stopOhSnap();
                    }
                });
            }
            else {
                retryDialogSuperCategoriesAPI(DialogErrorType.NO_NET);
            }
            swipeContainer.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSuperCategoriesDataToUI(SuperCategoriesData superCategoriesData){
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.setSuperCategoriesData(superCategoriesData);
        adapter.setList(superCategoriesData.getSuperCategories());
        activity.updateCartValuesGetTotalPrice();
        stopOhSnap();
    }

    private void retryDialogSuperCategoriesAPI(DialogErrorType dialogErrorType){
        swipeContainer.setVisibility(View.VISIBLE);
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getSuperCategoriesAPI();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    public void oSnapNotAvailableCase(String message){
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
        relativeLayoutNoMenus.setVisibility(View.VISIBLE);
        textViewNothingFound.setText(!TextUtils.isEmpty(message) ? message : getString(R.string.nothing_found_near_you));
    }

    public void stopOhSnap(){
        relativeLayoutNoMenus.setVisibility(View.GONE);
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        getSuperCategoriesAPI();
    }
}
