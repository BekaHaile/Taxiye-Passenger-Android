package com.sabkuchfresh.retrofit.model.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

public class NearbyDriversResponse extends FeedCommonResponse {

    @SerializedName("agents_list")
    @Expose
    private ArrayList<NearbyDriver> nearbyDriversList;

    @SerializedName("vehicles_info")
    @Expose
    private ArrayList<VehicleInfo> vehiclesInfoList;

    public ArrayList<NearbyDriver> getNearbyDriversList() {
        return nearbyDriversList;
    }

    public ArrayList<VehicleInfo> getVehiclesInfoList() {
        return vehiclesInfoList;
    }


}
