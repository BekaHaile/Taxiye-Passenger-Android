package com.sabkuchfresh.commoncalls;

import com.sabkuchfresh.feed.models.FeedNotificationsResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For updating notification read status
 */
public class ApiFeedNotificationUpdate {

	public void hit(final int notificationId) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_NOTIFICATION_ID, String.valueOf(notificationId));
				params.put(Constants.KEY_IS_READ, String.valueOf(1));

				new HomeUtil().putDefaultParams(params);
				RestClient.getFeedApiService().updateNotification(params, new retrofit.Callback<FeedNotificationsResponse>() {
					@Override
					public void success(FeedNotificationsResponse notificationsResponse, Response response) {
//						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
						} catch (Exception exception) {
							exception.printStackTrace();
						}
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

}
