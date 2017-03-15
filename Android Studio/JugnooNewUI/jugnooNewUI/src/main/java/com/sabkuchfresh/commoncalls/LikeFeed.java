package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import android.view.View;

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


    public  void likeFeed(final long postId, final Activity activity, final boolean isLikeAPI, final int position) {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

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
                                    likeUnLikeCallbackResponse.onSuccess(isLikeAPI,position);


                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogLikeFeed(DialogErrorType.SERVER_ERROR,activity,postId,isLikeAPI,position);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialogLikeFeed(DialogErrorType.CONNECTION_LOST,activity,postId,isLikeAPI,position);

                    }
                };


                 if(isLikeAPI) {
                     RestClient.getFeedApiService().likeFeed(params, callBack);
                 }
                else {
                     RestClient.getFeedApiService().unlikeFeed(params, callBack);
                 }
            }
            else {

                retryDialogLikeFeed(DialogErrorType.NO_NET,activity,postId,isLikeAPI,position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 }



    private  void retryDialogLikeFeed(DialogErrorType dialogErrorType, final Activity activity, final long postId,final boolean isLikeAPI,final int position){
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        likeFeed(postId,activity, isLikeAPI, position);
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
        void onSuccess(boolean isLiked,int posInOriginalList);
    }
}
