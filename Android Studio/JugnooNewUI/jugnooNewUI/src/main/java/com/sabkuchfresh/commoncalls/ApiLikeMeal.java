package com.sabkuchfresh.commoncalls;

/**
 * Created by Parminder Saini on 17/07/17.
 */

import android.app.Activity;

import com.sabkuchfresh.retrofit.model.SubItem;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Parminder Singh on 3/14/17.
 */

public class ApiLikeMeal {

    private ApiLikeMeal() {

    }

    public LikeMealCallback likeMealCallback;

    public ApiLikeMeal(LikeMealCallback callbackResponse) {
        likeMealCallback = callbackResponse;

    }


    public void likeFeed(final Activity activity, final boolean isLikeAPI, final int position, final SubItem subItem) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.STORE_ID, "" + 2);
                params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                params.put(Constants.INTERATED, "1");
                params.put(Constants.IS_LIKED, isLikeAPI ? "1" : "0");
                params.put(Constants.KEY_ITEM_ID, String.valueOf(subItem.getSubItemId()));
                new HomeUtil().putDefaultParams(params);


                Callback<SettleUserDebt> callBack = new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    if(feedbackResponse.getShowToastMessage())
                                        Utils.showToast(activity,feedbackResponse.getToastMessage());

                                    likeMealCallback.onSuccess(isLikeAPI, position, subItem);
                                } else {
                                    Utils.showToast(activity, message);
                                    likeMealCallback.onFailure(isLikeAPI, position, subItem);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            likeMealCallback.onFailure(isLikeAPI, position, subItem);
                            Utils.showToast(activity, activity.getString(R.string.unable_to_connect));

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        likeMealCallback.onFailure(isLikeAPI, position, subItem);
                        Utils.showToast(activity, activity.getString(R.string.network_error));


                    }
                };

                RestClient.getFreshApiService().markMealAsFavourite(params,callBack);

            } else {
                Utils.showToast(activity, activity.getString(R.string.connection_error));
                likeMealCallback.onFailure(isLikeAPI, position, subItem);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface LikeMealCallback {
        void onSuccess(boolean isLiked, int posInOriginalList, SubItem subItem);

        void onFailure(boolean isLiked, int posInOriginalList, SubItem subItem);


    }
}
