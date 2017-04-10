package com.sabkuchfresh.feed.models.feedcitiesresponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public class FeedCity {

    @SerializedName("city_name")
    private String cityName;

    public FeedCity(String cityName) {
        this.cityName=cityName;
    }

    public String getCityName() {
        return cityName;
    }

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
