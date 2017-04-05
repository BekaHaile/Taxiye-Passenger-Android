
package com.sabkuchfresh.retrofit.model.feed.feeddetail;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedComment {

    @SerializedName("comment_content")
    @Expose
    private String commentContent;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("activity_id")
    @Expose
    private long activityId;
    @SerializedName("is_comment_editable")
    @Expose
    private boolean canEdit;

    private transient Drawable drawable;

    @SerializedName("color")
    @Expose
    private String color;

    public boolean canEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public long getActivityId() {
        return activityId;
    }



    public String getCommentContent() {
        return commentContent;
    }

    @SerializedName("commented_on")
    @Expose
    private String timeCreated;

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
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


    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
