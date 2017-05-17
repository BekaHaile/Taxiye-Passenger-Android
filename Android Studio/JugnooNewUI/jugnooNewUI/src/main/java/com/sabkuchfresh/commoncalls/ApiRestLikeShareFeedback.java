package com.sabkuchfresh.commoncalls;

import android.app.Activity;

import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For sending ack to server after liking or sharing feedback/review of a particular restaurant
 */
public class ApiRestLikeShareFeedback {

	private final String TAG = ApiRestLikeShareFeedback.class.getSimpleName();

	private Activity activity;

	public ApiRestLikeShareFeedback(Activity activity){
		this.activity = activity;
	}

	/**
	 * Hit the API /like_share_feedback
	 * @param restaurantId  id of restaurant opened
	 * @param feedbackId id of feedback/review
	 * @param action LIKE or SHARE
	 */
	public void hit(int restaurantId, int feedbackId, String action, final Callback callback, final int position) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				/**
				 * Silent API, no loader shown
				 */
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(restaurantId));
				params.put(Constants.KEY_FEEDBACK_ID, String.valueOf(feedbackId));
				params.put(Constants.KEY_ACTION, action);

				new HomeUtil().putDefaultParams(params);
				RestClient.getMenusApiService().customerLikeShareFeedback(params, new retrofit.Callback<FetchFeedbackResponse>() {
					@Override
					public void success(FetchFeedbackResponse settleUserDebt, Response response) {
						try {
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, settleUserDebt.getFlag(),
									settleUserDebt.getError(), settleUserDebt.getMessage())) {
								if(settleUserDebt.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()
										&& settleUserDebt.getReview() != null && settleUserDebt.getReview().size() > 0){
									callback.onSuccess(settleUserDebt.getReview().get(0), position);
								} else {
									callback.onFailure();
									/**
									 * Failure case would only revert like share count
									 */
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							callback.onFailure();
						}
					}

					@Override
					public void failure(RetrofitError error) {
						callback.onFailure();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public interface Callback{
		void onSuccess(FetchFeedbackResponse.Review review, int position);
		void onFailure();
	}

}
