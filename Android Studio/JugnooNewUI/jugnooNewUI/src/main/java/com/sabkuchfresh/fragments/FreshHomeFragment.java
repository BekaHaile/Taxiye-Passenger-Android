package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment {

    private View rootView;
    private RelativeLayout relative;
    private FreshActivity activity;
    private RecyclerView rvFreshSuper;
    private FreshSuperCategoriesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_home, container, false);
        relative = (RelativeLayout) rootView.findViewById(R.id.relative);
        try {
            activity = (FreshActivity) getActivity();
            if (relative != null) {
                new ASSL(activity, relative, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.fragmentUISetup(this);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);

        rvFreshSuper = (RecyclerView) rootView.findViewById(R.id.rvFreshSuper);
        rvFreshSuper.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.HORIZONTAL, false);
        rvFreshSuper.setLayoutManager(gridLayoutManager);
        adapter = new FreshSuperCategoriesAdapter(activity, new FreshSuperCategoriesAdapter.Callback() {
            @Override
            public void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory) {
                activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer(), superCategory);
            }
        });

        rvFreshSuper.setAdapter(adapter);

        getSuperCategoriesAPI();

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
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
                params.put(Constants.KEY_CLIENT_ID, Config.getGroceryClientId());
                params.put(Constants.INTERATED, "1");

                new HomeUtil().putDefaultParams(params);
                RestClient.getFreshApiService().getSuperCategories(params, new Callback<SuperCategoriesData>() {
                    @Override
                    public void success(SuperCategoriesData superCategoriesData, Response response) {
                        try {
                            if(superCategoriesData.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                adapter.setList(superCategoriesData.getSuperCategories());
                            } else {
                                DialogPopup.alertPopup(activity, "", superCategoriesData.getMessage());
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogSuperCategoriesAPI(DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialogSuperCategoriesAPI(DialogErrorType.CONNECTION_LOST);
                    }
                });
            }
            else {
                retryDialogSuperCategoriesAPI(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogSuperCategoriesAPI(DialogErrorType dialogErrorType){
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
}
