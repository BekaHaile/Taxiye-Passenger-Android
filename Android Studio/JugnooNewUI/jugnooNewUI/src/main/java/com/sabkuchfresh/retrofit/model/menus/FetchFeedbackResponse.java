package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
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
	@SerializedName("review_image_limit")
	@Expose
	private int reviewImageLimit;
	@SerializedName("share_text_self")
	@Expose
	private String shareTextSelf;
	@SerializedName("share_text_other")
	@Expose
	private String shareTextOther;
	@SerializedName("like_is_enabled")
	@Expose
	private Integer likeIsEnabled;
	@SerializedName("share_is_enabled")
	@Expose
	private Integer shareIsEnabled;
	@SerializedName("restaurant_info")
	@Expose
	private RestaurantInfo restaurantInfo;
	@SerializedName("has_already_rated")
	@Expose
	private int hasAlreadyRated;
	@SerializedName("review_count")
	private long reviewCount;

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

	public int getReviewImageLimit() {
		return reviewImageLimit;
	}

	public void setReviewImageLimit(int reviewImageLimit) {
		this.reviewImageLimit = reviewImageLimit;
	}

	public String getShareTextSelf() {
		return shareTextSelf;
	}

	public void setShareTextSelf(String shareTextSelf) {
		this.shareTextSelf = shareTextSelf;
	}

	public String getShareTextOther() {
		return shareTextOther;
	}

	public void setShareTextOther(String shareTextOther) {
		this.shareTextOther = shareTextOther;
	}

	public Integer getLikeIsEnabled() {
		if(likeIsEnabled == null){
			likeIsEnabled = 1;
		}
		return likeIsEnabled;
	}

	public void setLikeIsEnabled(Integer likeIsEnabled) {
		this.likeIsEnabled = likeIsEnabled;
	}

	public Integer getShareIsEnabled() {
		if(shareIsEnabled == null){
			shareIsEnabled = 1;
		}
		return shareIsEnabled;
	}

	public void setShareIsEnabled(Integer shareIsEnabled) {
		this.shareIsEnabled = shareIsEnabled;
	}

	public RestaurantInfo getRestaurantInfo() {
		return restaurantInfo;
	}

	public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
		this.restaurantInfo = restaurantInfo;
	}

	public int getHasAlreadyRated() {
		return hasAlreadyRated;
	}

	public void setHasAlreadyRated(int hasAlreadyRated) {
		this.hasAlreadyRated = hasAlreadyRated;
	}

	public long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(long reviewCount) {
		this.reviewCount = reviewCount;
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
		private boolean expanded;
		@SerializedName("post_time")
		@Expose
		private String postTime;

		@SerializedName("reply")
		@Expose
		private String reply;
		@SerializedName("reply_time")
		@Expose
		private String replyTime;


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

		public boolean isExpanded() {
			return expanded;
		}

		public void setExpanded(boolean expanded) {
			this.expanded = expanded;
		}

		public String getPostTime() {
			return postTime;
		}

		public String getReply() {
			return reply;
		}

		public void setReply(String reply) {
			this.reply = reply;
		}

		public String getReplyTime() {
			return replyTime;
		}

		public void setReplyTime(String replyTime) {
			this.replyTime = replyTime;
		}
	}


	public static class ReviewImage implements Serializable{
		@SerializedName(value = "url", alternate = "image")
		@Expose
		private String url;
		@SerializedName("thumbnail")
		@Expose
		private String thumbnail;

		@SerializedName("id")
		@Expose
		private String id;
		@SerializedName("height")
		private Integer height;

		@SerializedName("width")
		private Integer width;


		private boolean isRestaurantImage;

		public ReviewImage(String url, String thumbnail) {
			this.url=url;
			this.thumbnail=thumbnail;
		}


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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public boolean isRestaurantImage() {
			return isRestaurantImage;
		}

		public void setIsRestaurantImage(boolean restaurantImage) {
			isRestaurantImage = restaurantImage;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}
	}

	public class RestaurantInfo{
		@SerializedName("rating")
		private double rating;
		@SerializedName("review_count")
		private long reviewCount;




		public double getRating() {
			return rating;
		}

		public void setRating(double rating) {
			this.rating = rating;
		}

		public long getReviewCount() {
			return reviewCount;
		}

		public void setReviewCount(long reviewCount) {
			this.reviewCount = reviewCount;
		}
	}


}
