package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;

import com.sabkuchfresh.feed.models.FeedNotificationsResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For fetching feed notifications
 */
public class ApiFeedNotifications {

	private final String TAG = ApiFeedNotifications.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiFeedNotifications(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(final SwipeRefreshLayout swipeRefreshLayout) {
		try {
			if(MyApplication.getInstance().isOnline()) {

				swipeRefreshLayout.setRefreshing(true);

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

				new HomeUtil().putDefaultParams(params);
				RestClient.getFeedApiService().getNotifications(params, new retrofit.Callback<FeedNotificationsResponse>() {
					@Override
					public void success(FeedNotificationsResponse notificationsResponse, Response response) {
//						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						swipeRefreshLayout.setRefreshing(false);
						try {
							String message = notificationsResponse.getMessage();
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, notificationsResponse.getFlag(),
									notificationsResponse.getError(), notificationsResponse.getMessage())) {
								if(notificationsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
									callback.onSuccess(notificationsResponse);
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
						swipeRefreshLayout.setRefreshing(false);
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
		void onSuccess(FeedNotificationsResponse feedNotificationsResponse);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}
