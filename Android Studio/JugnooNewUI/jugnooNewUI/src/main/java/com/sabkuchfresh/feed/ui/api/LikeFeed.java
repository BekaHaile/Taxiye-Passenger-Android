package com.sabkuchfresh.feed.ui.api;

import android.app.Activity;
import android.view.View;

import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
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

public class LikeFeed {

    private LikeFeed(){

    }

    public LikeUnLikeCallbackResponse likeUnLikeCallbackResponse;
    public LikeFeed(LikeUnLikeCallbackResponse callbackResponse){
        likeUnLikeCallbackResponse = callbackResponse;

    }


    public  void likeFeed(final long postId, final Activity activity, final boolean isLikeAPI, final int position, final FeedDetail feedDetail) {
        try {
            if(MyApplication.getInstance().isOnline()) {

//                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_POST_ID, String.valueOf(postId));
                new HomeUtil().putDefaultParams(params);


                Callback<SettleUserDebt>  callBack = new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
//                                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                                    likeUnLikeCallbackResponse.onSuccess(isLikeAPI,position,feedDetail);
                                } else {


                                    Utils.showToast(activity, message);
                                    likeUnLikeCallbackResponse.onFailure(isLikeAPI,position,feedDetail);
//                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            likeUnLikeCallbackResponse.onFailure(isLikeAPI,position,feedDetail);
                            Utils.showToast(activity, activity.getString(R.string.unable_to_connect));

//                            retryDialogLikeFeed(DialogErrorType.SERVER_ERROR,activity,postId,isLikeAPI,position,feedDetail);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        likeUnLikeCallbackResponse.onFailure(isLikeAPI,position,feedDetail);
                        Utils.showToast(activity, activity.getString(R.string.network_error));


//                        retryDialogLikeFeed(DialogErrorType.CONNECTION_LOST,activity,postId,isLikeAPI,position,feedDetail);

                    }
                };

                new HomeUtil().putDefaultParams(params);
                 if(isLikeAPI) {
                     RestClient.getFeedApiService().likeFeed(params, callBack);
                 }
                else {
                     RestClient.getFeedApiService().unlikeFeed(params, callBack);
                 }
            }
            else {
                Utils.showToast(activity, activity.getString(R.string.connection_error));
                likeUnLikeCallbackResponse.onFailure(isLikeAPI,position,feedDetail);

//                retryDialogLikeFeed(DialogErrorType.NO_NET,activity,postId,isLikeAPI,position,feedDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 }



    private  void retryDialogLikeFeed(DialogErrorType dialogErrorType, final Activity activity, final long postId, final boolean isLikeAPI, final int position, final FeedDetail feedDetail){
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        likeFeed(postId,activity, isLikeAPI, position,feedDetail);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    public  interface LikeUnLikeCallbackResponse {
        void onSuccess(boolean isLiked,int posInOriginalList,FeedDetail feedDetail);
        void onFailure(boolean isLiked,int posInOriginalList,FeedDetail feedDetail);


    }
}
