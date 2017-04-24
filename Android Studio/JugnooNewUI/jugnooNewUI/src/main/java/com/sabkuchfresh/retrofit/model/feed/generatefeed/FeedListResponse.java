
package com.sabkuchfresh.retrofit.model.feed.generatefeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    @SerializedName("add_post_text")
    @Expose
    private String addPostText;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("count_notification")
    @Expose
    private int countNotification;
    @SerializedName("handle_name")
    @Expose
    private String handleName;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @SerializedName("pageCount")
    @Expose
    private int pageCount;

    public int getCountRecords() {
        return countRecords;
    }

    public void setCountRecords(int countRecords) {
        this.countRecords = countRecords;
    }

    @SerializedName("count_records")
    @Expose
    private int countRecords;


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

    public String getAddPostText() {
        return addPostText;
    }

    public void setAddPostText(String addPostText) {
        this.addPostText = addPostText;
    }

    public String getCity() {
        return cityName;
    }

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public int getCountNotification() {
        return countNotification;
    }

    public void setCountNotification(int countNotification) {
        this.countNotification = countNotification;
    }
}
