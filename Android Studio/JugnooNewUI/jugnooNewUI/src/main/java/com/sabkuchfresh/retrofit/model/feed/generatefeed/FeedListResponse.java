
package com.sabkuchfresh.retrofit.model.feed.generatefeed;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedListResponse {

    @SerializedName("flag")
    @Expose
    private int flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("feeds")
    @Expose
    private List<FeedDetail> feeds = null;

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

    public List<FeedDetail> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<FeedDetail> feeds) {
        this.feeds = feeds;
    }


    public String getError() {
        return error;
    }
}
