
package com.sabkuchfresh.retrofit.model.feed.generatefeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedDetail implements Serializable {

    @SerializedName("post_id")
    @Expose
    private long postId;
    @SerializedName("city")
    @Expose
    private int city;
    @SerializedName("like_count")
    @Expose
    private int likeCount;
    @SerializedName("comment_count")
    @Expose
    private int commentCount;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("star_count")
    @Expose
    private Double starCount;
    @SerializedName("feed_type")
    @Expose
    private int feedType;
    @SerializedName("feed_type_string")
    @Expose
    private FeedType feedTypeString;


    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("owner_name")
    @Expose
    private String ownerName;
    @SerializedName("owner_image")
    @Expose
    private String ownerImage;
    @SerializedName("restaurant_id")
    @Expose
    private Integer restaurantId;
    @SerializedName("restaurant_name")
    @Expose
    private String restaurantName;
    @SerializedName("restaurant_image")
    @Expose
    private String restaurantImage;
    @SerializedName("restaurant_address")
    @Expose
    private String restaurantAddress;
    @SerializedName("activity_done_on")
    @Expose
    private String activityDoneOn;
    @SerializedName("is_liked_by_user")
    private int isLiked;

    public boolean isLiked() {
        return isLiked==1;
    }

    public void setLiked(boolean liked) {
            this.isLiked=liked?1:0;
    }

    public String getActivityDoneOn() {
        return activityDoneOn;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getStarCount() {
        return starCount;
    }

    public void setStarCount(Double starCount) {
        this.starCount = starCount;
    }

    public FeedType getFeedType() {
        return feedTypeString;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public Integer getRestaurantId() {
        if(restaurantId == null){
            restaurantId = 0;
        }
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public enum FeedType{
        POST(" posted "),
        REVIEW(" reviewed "),
        LIKE_ON_POST(" liked "),
        LIKE_ON_REVIEW (" liked "),
        COMMENT_ON_POST(" commented on "),
        COMMENT_ON_REVIEW(" commented on  ");

        private String value;
        FeedType(String value) {
            this.value=value;
        }
        public String getValue(){
            return value;
        }


    }

}
