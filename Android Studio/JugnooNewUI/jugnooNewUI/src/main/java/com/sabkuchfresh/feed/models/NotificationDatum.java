package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 31/03/17.
 */

public class NotificationDatum {

	@SerializedName("id")
	@Expose
	private int id;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("user_image")
	@Expose
	private String userImage;
	@SerializedName("time")
	@Expose
	private String time;
	@SerializedName("is_comment")
	@Expose
	private int isComment;
	@SerializedName("is_like")
	@Expose
	private int isLike;
	@SerializedName("is_read")
	@Expose
	private int isRead;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isComment() {
		return isComment == 1;
	}

	public void setIsComment(int isComment) {
		this.isComment = isComment;
	}

	public boolean isLike() {
		return isLike == 1;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}

	public boolean isRead() {
		return isRead == 1;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
}
