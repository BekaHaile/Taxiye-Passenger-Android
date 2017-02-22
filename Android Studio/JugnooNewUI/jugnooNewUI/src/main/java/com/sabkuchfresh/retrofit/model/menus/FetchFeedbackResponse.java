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
	@SerializedName("review")
	@Expose
	private List<Review> review = null;

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

	public List<Review> getReview() {
		return review;
	}

	public void setReview(List<Review> review) {
		this.review = review;
	}

	public class Review {

		@SerializedName("feedback_id")
		@Expose
		private Integer feedbackId;
		@SerializedName("order_id")
		@Expose
		private Integer orderId;
		@SerializedName("rating")
		@Expose
		private Double rating;
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
		@SerializedName("date")
		@Expose
		private String date;
		@SerializedName("like_count")
		@Expose
		private Integer likeCount;
		@SerializedName("share_count")
		@Expose
		private Integer shareCount;
		@SerializedName("is_liked")
		@Expose
		private Integer isLiked;
		@SerializedName("is_shared")
		@Expose
		private Integer isShared;
		@SerializedName("is_editable")
		@Expose
		private Integer isEditable;
		@SerializedName("images")
		@Expose
		private List<ReviewImage> images;

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public Double getRating() {
			return (rating == null || rating < 1d) ? null : Math.round(rating * 10.0) / 10.0;
		}

		public void setRating(Double rating) {
			this.rating = rating;
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

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public Integer getLikeCount() {
			if(likeCount == null){
				likeCount = 0;
			}
			return likeCount;
		}

		public void setLikeCount(Integer likeCount) {
			this.likeCount = likeCount;
		}

		public Integer getShareCount() {
			if(shareCount == null){
				shareCount = 0;
			}
			return shareCount;
		}

		public void setShareCount(Integer shareCount) {
			this.shareCount = shareCount;
		}

		public Integer getIsLiked() {
			if(isLiked == null){
				isLiked = 0;
			}
			return isLiked;
		}

		public void setIsLiked(Integer isLiked) {
			this.isLiked = isLiked;
		}

		public Integer getIsShared() {
			if(isShared == null){
				isShared = 0;
			}
			return isShared;
		}

		public void setIsShared(Integer isShared) {
			this.isShared = isShared;
		}

		public Integer getIsEditable() {
			if(isEditable == null){
				isEditable = 0;
			}
			return isEditable;
		}

		public void setIsEditable(Integer isEditable) {
			this.isEditable = isEditable;
		}

		public List<ReviewImage> getImages() {
			return images;
		}

		public void setImages(List<ReviewImage> images) {
			this.images = images;
		}

		public Integer getFeedbackId() {
			return feedbackId;
		}

		public void setFeedbackId(Integer feedbackId) {
			this.feedbackId = feedbackId;
		}
	}


	public static class ReviewImage{
		@SerializedName("url")
		@Expose
		private String url;
		@SerializedName("thumbnail")
		@Expose
		private String thumbnail;

		@SerializedName("id")
		@Expose
		private String id;


		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
	}
}
