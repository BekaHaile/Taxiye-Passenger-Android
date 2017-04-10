package com.sabkuchfresh.feed.models.feedcitiesresponse;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public class FeedCityResponse extends FeedCommonResponse {

    @SerializedName("trending_cities")
    private ArrayList<FeedCity> trendingCities;


    @SerializedName("cities")
    private ArrayList<FeedCity> totalCities;


    public ArrayList<FeedCity> getTrendingCities() {
        return trendingCities;
    }

    public ArrayList<FeedCity> getTotalCities() {
        return totalCities;
    }
}
