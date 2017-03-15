
package com.sabkuchfresh.retrofit.model.feed.feeddetail;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

public class FeedDetailResponse {

    @SerializedName("flag")
    @Expose
    private int flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    @SerializedName("post_details")
    @Expose
    private FeedDetail postDetails;
    @SerializedName("comments")
    @Expose
    private List<FeedComment> feedComments = null;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FeedDetail getPostDetails() {
        return postDetails;
    }

    public List<FeedComment> getFeedComments() {
        return feedComments;
    }

    public void setFeedComments(List<FeedComment> feedComments) {
        this.feedComments = feedComments;
    }

}
