package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 02/04/17.
 */

public class FeedNotificationsResponse extends FeedCommonResponse{

	@SerializedName("notification")
	@Expose
	private List<NotificationDatum> notificationData;

	public List<NotificationDatum> getNotificationData() {
		return notificationData;
	}

	public void setNotificationData(List<NotificationDatum> notificationData) {
		this.notificationData = notificationData;
	}
}
