package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 31/03/17.
 */

public class NotificationDatum {

	@SerializedName("post_id")
	@Expose
	private int postId;
	@SerializedName("notification_id")
	@Expose
	private int notificationId;
	@SerializedName("notification_text")
	@Expose
	private String notificationText;
	@SerializedName("user_image")
	@Expose
	private String userImage;
	@SerializedName("updated_at")
	@Expose
	private String updatedAt;
	@SerializedName("activity_type")
	@Expose
	private int activityType;
	@SerializedName("is_read")
	@Expose
	private int isRead;

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public boolean isRead() {
		return isRead == 1;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public static final int ACTIVITY_TYPE_LIKE = 1;
	public static final int ACTIVITY_TYPE_COMMENT = 2;

}
