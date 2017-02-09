package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 2/9/17.
 */

public class FetchFeedbackResponse {
	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("reviews")
	@Expose
	private List<Review> reviews = null;

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public class Review {

		@SerializedName("order_id")
		@Expose
		private Integer orderId;
		@SerializedName("rating")
		@Expose
		private Double rating;
		@SerializedName("review_title")
		@Expose
		private String reviewTitle;
		@SerializedName("tags")
		@Expose
		private String tags;
		@SerializedName("review_desc")
		@Expose
		private String reviewDesc;
		@SerializedName("color")
		@Expose
		private String color;
		@SerializedName("rating_flag")
		@Expose
		private Integer ratingFlag;
		@SerializedName("user_name")
		@Expose
		private String userName;
		@SerializedName("user_image")
		@Expose
		private String userImage;

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public Double getRating() {
			return rating;
		}

		public void setRating(Double rating) {
			this.rating = rating;
		}

		public String getReviewTitle() {
			return reviewTitle;
		}

		public void setReviewTitle(String reviewTitle) {
			this.reviewTitle = reviewTitle;
		}

		public String getTags() {
			return tags;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public String getReviewDesc() {
			return reviewDesc;
		}

		public void setReviewDesc(String reviewDesc) {
			this.reviewDesc = reviewDesc;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public Integer getRatingFlag() {
			return ratingFlag;
		}

		public void setRatingFlag(Integer ratingFlag) {
			this.ratingFlag = ratingFlag;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserImage() {
			return userImage;
		}

		public void setUserImage(String userImage) {
			this.userImage = userImage;
		}

	}
}
