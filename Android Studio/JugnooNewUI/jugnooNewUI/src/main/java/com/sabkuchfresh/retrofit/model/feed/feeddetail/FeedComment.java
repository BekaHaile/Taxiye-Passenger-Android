
package com.sabkuchfresh.retrofit.model.feed.feeddetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedComment {

    @SerializedName("comment_content")
    @Expose
    private String commentContent;
    @SerializedName("user_name")
    @Expose
    private String userName;

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

}
