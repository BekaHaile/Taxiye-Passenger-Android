package com.sabkuchfresh.feed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Parminder Saini on 15/05/17.
 */

public class HandleSuggestionsResponse extends FeedCommonResponse {

    @Expose
    @SerializedName("handleSuggestions")
    private   ArrayList<String> handleSuggestions;


    public ArrayList<String> getHandleSuggestions() {
        return handleSuggestions;
    }
}
