package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import android.view.View;

import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

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
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * For fetching feedback reviews for a particular restaurant
 */
public class ApiRestaurantFetchFeedback {

	private final String TAG = ApiRestaurantFetchFeedback.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiRestaurantFetchFeedback(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(int restaurantId, final boolean scrollToTop, final ProgressWheel progressWheel, int limit) {
		try {
			if(MyApplication.getInstance().isOnline()) {

				if(progressWheel == null) {
					DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				} else {
					progressWheel.setVisibility(View.VISIBLE);
					progressWheel.spin();
				}

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(restaurantId));
				if(limit == 1) {
					params.put(Constants.KEY_LIMIT, "1");
				}

				new HomeUtil().putDefaultParams(params);
				RestClient.getMenusApiService().restaurantFetchFeedbacks(params, new retrofit.Callback<FetchFeedbackResponse>() {
					@Override
					public void success(FetchFeedbackResponse feedbackResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						if(progressWheel != null) {
							progressWheel.setVisibility(View.GONE);
							progressWheel.stopSpinning();
						}
						try {
							String message = feedbackResponse.getMessage();
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(),
									feedbackResponse.getError(), feedbackResponse.getMessage())) {
								if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
									callback.onSuccess(feedbackResponse, scrollToTop);
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						if(progressWheel != null) {
							progressWheel.setVisibility(View.GONE);
							progressWheel.stopSpinning();
						}
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						callback.onRetry(view);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
						callback.onNoRetry(view);
					}
				});
	}


	public interface Callback{
		void onSuccess(FetchFeedbackResponse fetchFeedbackResponse, boolean scrollToTop);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}
