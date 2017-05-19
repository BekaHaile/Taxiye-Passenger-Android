
package com.sabkuchfresh.retrofit.model.feed.generatefeed;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.io.Serializable;
import java.util.ArrayList;

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
    private ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages;
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
    @SerializedName("is_commented_by_user")
    @Expose
    private int isCommentedByUser;

    public boolean isLikeAPIInProgress() {
        return isLikeAPIInProgress;
    }

    private boolean isLikeAPIInProgress;

    public boolean isLiked() {
        return likeCount > 0 && isLiked==1;
    }

    @SerializedName("star_color")
    private String ratingColor;


    @SerializedName("is_post_editable")
    private boolean isPostEditable;


    @SerializedName("is_anonymous")
    private int isAnonymousPost;

    @SerializedName("owner_user_id")
    private long ownerId;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("comment_content")
    @Expose
    private String commentContent;

    @SerializedName("user_image_color")
    @Expose
    private String userImageColor;


    @SerializedName("latest_comment")
    private LatestComment latestComment;

    public LatestComment getLatestComment() {
        return latestComment;
    }

    private transient Drawable owenerImageDrawable;
    private transient Drawable userImageDrawable;



    public boolean isAnonymousPost() {
        return isAnonymousPost==1;
    }


    public long getOwnerId() {
        return ownerId;
    }

    public boolean isPostEditable() {
        return isPostEditable;
    }

    public void setPostEditable(boolean postEditable) {
        isPostEditable = postEditable;
    }

    public String getRatingColor() {
        return ratingColor;
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

    public ArrayList<FetchFeedbackResponse.ReviewImage>  getReviewImages() {
        return reviewImages;
    }

    public void setReviewImages(ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages) {
        this.reviewImages = reviewImages;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Drawable getOwnerImageDrawable() {
        return owenerImageDrawable;
    }

    public void setOwnerImageDrawable(Drawable owenerImageDrawable) {
        this.owenerImageDrawable = owenerImageDrawable;
    }

    public int getIsCommentedByUser() {
        return isCommentedByUser;
    }

    public void setIsCommentedByUser(int isCommentedByUser) {
        this.isCommentedByUser = isCommentedByUser;
    }

    public Drawable getUserImageDrawable() {
        return userImageDrawable;
    }

    public void setUserImageDrawable(Drawable userImageDrawable) {
        this.userImageDrawable = userImageDrawable;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getUserImageColor() {
        return userImageColor;
    }

    public void setIsLikeAPIInProgress(boolean isLikeAPIInProgress) {
        this.isLikeAPIInProgress = isLikeAPIInProgress;
    }

    public enum FeedType{
        POST(" posted "),
        REVIEW(" reviewed "),
        LIKE_ON_POST(" liked "),
        LIKE_ON_REVIEW (" liked "),
        COMMENT_ON_POST(" replied on "),
        COMMENT_ON_REVIEW(" replied on ");

        private String value;
        FeedType(String value) {
            this.value=value;
        }
        public String getValue(){
            return value;
        }



    }


    public   class LatestComment implements Serializable{


        @SerializedName("comment")
        private String comment ;

        @SerializedName("user_image")
        private String userPic;


        @SerializedName("user_name")
        private String commentedBy;

        @SerializedName("user_image_color")
        private String commentedByColor;

        public LatestComment(String comment, String commentedBy) {
            this.comment = comment;
            this.commentedBy = commentedBy;
        }

        private transient Drawable userImageDrawable;

        public Drawable getUserImageDrawable() {
            return userImageDrawable;
        }

        public void setUserImageDrawable(Drawable userImageDrawable) {
            this.userImageDrawable = userImageDrawable;
        }


        public String getCommentedBy() {
            return commentedBy;
        }

        public String getComment() {
            return comment;
        }

        public String getUserPic() {
            return userPic;
        }

        public String getCommentedByColor() {
            return commentedByColor;
        }

    }

}
