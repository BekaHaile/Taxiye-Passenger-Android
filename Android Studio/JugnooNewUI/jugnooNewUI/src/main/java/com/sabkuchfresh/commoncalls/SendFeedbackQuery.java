package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import android.view.View;

import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Parminder Singh on 1/31/17.
 */

public class SendFeedbackQuery {

    public interface FeedbackResultListener {

        void onSendFeedbackResult(boolean isSuccess, int rating);


    }

    public static void sendQuery(final String orderId, final ProductType productType, final int rating, final String feedbackTitle, final String negativeReasons, final Activity activity, final FeedbackResultListener feedbackResultListener) {
        try {
            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                Data.getMenusData().setPendingFeedback(0);
            } else {
                activity.finish();
            }
            if (MyApplication.getInstance().isOnline()) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.ORDER_ID, "" + orderId);
                params.put(Constants.RATING, "" + rating);

                if (productType == ProductType.MENUS) {
                    params.put(Constants.FEEDBACK_TITLE, "" + feedbackTitle);
                }

                params.put(Constants.RATING_TYPE, "0");
                params.put(Constants.INTERATED, "1");
                params.put(Constants.COMMENT, negativeReasons);
                params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {


                                feedbackResultListener.onSendFeedbackResult(true, rating);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        RetryDialog(DialogErrorType.CONNECTION_LOST, productType, activity, orderId, rating, feedbackTitle, negativeReasons, feedbackResultListener);

                    }
                };
                new HomeUtil().putDefaultParams(params);
                if (productType == ProductType.MENUS) {
                    RestClient.getMenusApiService().orderFeedback(params, callback);
                } else {
                    RestClient.getFreshApiService().orderFeedback(params, callback);
                }

            } else {
                RetryDialog(DialogErrorType.NO_NET, productType, activity, orderId, rating, feedbackTitle, negativeReasons, feedbackResultListener);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private static void RetryDialog(DialogErrorType errorType, final ProductType productType, final Activity activity, final String orderId, final int rating, final String feedbackTitle, final String negativeReasons, final FeedbackResultListener feedbackResultListener) {
        DialogPopup.dialogNoInternet(activity, errorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        sendQuery(orderId, productType, rating, feedbackTitle, negativeReasons, activity, feedbackResultListener);
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
