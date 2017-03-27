package com.sabkuchfresh.feed.ui.api;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.FeedCommonResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Parminder Singh on 3/27/17.
 */

public class ApiCommon {


    public static <T extends FeedCommonResponse> void apiHit(final FreshActivity activity, final boolean showLoader) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                if (showLoader) {
                    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                }

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                new HomeUtil().putDefaultParams(params);


                RestClient.getFeedApiService().testAPI(params, new Callback<T>() {
                    @Override
                    public void success(T feedCommonResponse, Response response) {
                        feedCommonResponse.getError();

                        ApiCommon.<T>abi(activity, showLoader);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static <T extends FeedCommonResponse> void abi(FreshActivity activity, boolean showLoader) {
        ApiCommon.<T>apiHit(activity, showLoader);
    }
}
