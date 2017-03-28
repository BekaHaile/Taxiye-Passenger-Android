package com.sabkuchfresh.feed.ui.api;

import android.app.Activity;
import android.view.View;

import com.sabkuchfresh.retrofit.model.feed.FeedCommonResponse;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Parminder Singh on 3/27/17.
 */

public class ApiCommon {


    public static <T extends FeedCommonResponse> Callback<T> hitAPI(final Activity activity, final boolean showLoader, final APICommonCallback apiCommonCallback) {


        if (!MyApplication.getInstance().isOnline()) {
            if (apiCommonCallback.onNotConnected()) {
                ApiCommon.<T>retryDialog(DialogErrorType.NO_NET, activity, showLoader, apiCommonCallback);
            }
            return null;
        }


        return new Callback<T>() {
            @Override
            public void success(T feedCommonResponse, Response response) {

                if (feedCommonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                    apiCommonCallback.onSuccess(feedCommonResponse, feedCommonResponse.getMessage(), feedCommonResponse.getFlag());

                } else {
                    if (apiCommonCallback.onError(feedCommonResponse, feedCommonResponse.getMessage(), feedCommonResponse.getFlag())) {
                        DialogPopup.alertPopup(activity, "", feedCommonResponse.getMessage());
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                if (apiCommonCallback.onException(error)) {
                    ApiCommon.<T>retryDialog(DialogErrorType.CONNECTION_LOST, activity, showLoader, apiCommonCallback);
                }


            }
        };


    }

    private static <T extends FeedCommonResponse> void retryDialog(DialogErrorType dialogErrorType, final Activity activity, final boolean showLoader, final APICommonCallback apiCommonCallback) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        ApiCommon.<T>hitAPI(activity, showLoader, apiCommonCallback);
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
