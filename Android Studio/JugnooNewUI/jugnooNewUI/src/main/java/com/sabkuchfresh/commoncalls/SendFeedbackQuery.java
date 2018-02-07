package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import android.text.TextUtils;
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

    public void sendQuery(final int orderId, final int restaurantId, final ProductType productType, final int rating, final String ratingType,
                          final String comments, final String reviewDesc,
                          final Activity activity, final FeedbackResultListener feedbackResultListener, final boolean isFeedOrder) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                if (orderId > 0) {
                    if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                        Data.getFreshData().setPendingFeedback(0);
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                        Data.getMealsData().setPendingFeedback(0);
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                        Data.getGroceryData().setPendingFeedback(0);
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                        Data.getMenusData().setPendingFeedback(0);
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getDeliveryCustomerClientId())) {
                        Data.getDeliveryCustomerData().setPendingFeedback(0);
                    }
                }

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.RATING_TYPE, "0");
                params.put(Constants.INTERATED, "1");
                if (orderId > 0) {
                    params.put(Constants.ORDER_ID, "" + orderId);
                }
                if (ratingType != null && (ratingType.equals(Constants.RATING_TYPE_STAR) || ratingType.equals(Constants.RATING_TYPE_THUMBS))) {
                    params.put(Constants.RATING_TYPE, ratingType);
                }

                if (rating > 0) {
                    params.put(Constants.RATING, "" + rating);
                }
                if (!TextUtils.isEmpty(comments)) {
                    params.put(Constants.COMMENT, comments);
                }
                if (!TextUtils.isEmpty(reviewDesc)) {
                    params.put(Constants.KEY_REVIEW_DESC, reviewDesc);
                }
                if (restaurantId > 0) {
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(restaurantId));
                }

                params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                feedbackResultListener.onSendFeedbackResult(true, rating);
                            } else {
                                DialogPopup.alertPopup(activity, "", notificationInboxResponse.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST, productType, activity, orderId, restaurantId, rating, ratingType, comments, reviewDesc, feedbackResultListener,isFeedOrder);
                    }
                };
                new HomeUtil().putDefaultParams(params);

                if(isFeedOrder){

                    RestClient.getFatafatApiService().orderFeedback(params,callback);
                }
                else if (productType == ProductType.MENUS || productType == ProductType.DELIVERY_CUSTOMER) {
                    RestClient.getMenusApiService().orderFeedback(params, callback);
                } else {
                    RestClient.getFreshApiService().orderFeedback(params, callback);
                }

            } else {
                retryDialog(DialogErrorType.NO_NET, productType, activity, orderId, restaurantId, rating, ratingType, comments, reviewDesc, feedbackResultListener,isFeedOrder);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private void retryDialog(DialogErrorType errorType, final ProductType productType, final Activity activity,
                             final int orderId, final int restaurantId, final int rating, final String ratingType,
                             final String negativeReasons, final String reviewDesc, final FeedbackResultListener feedbackResultListener, final boolean isFeedOrder) {
        DialogPopup.dialogNoInternet(activity, errorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        sendQuery(orderId, restaurantId, productType, rating, ratingType, negativeReasons, reviewDesc, activity, feedbackResultListener, isFeedOrder);
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
