package com.sabkuchfresh.retrofit.model.feed;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Parminder Saini on 10/10/17.
 */

public class DynamicDeliveryResponse extends FeedCommonResponse {

    @SerializedName("delivery_charges")
    private DeliveryCharges deliveryCharges;


    public DeliveryCharges getDeliveryCharges() {
        return deliveryCharges;
    }

    public class DeliveryCharges {

        @SerializedName("delivery_charges_inst")
        ArrayList<HashMap<String,Double>> popupData ;

        @SerializedName("estimated_distance")
        double estimatedDistance;

        @SerializedName("estimated_charges")
        double estimatedCharges;

        @SerializedName("delivery_text")
        String deliveryLabel;

        public String getDeliveryLabel() {
            return deliveryLabel;
        }

        public ArrayList<HashMap<String, Double>> getPopupData() {
            return popupData;
        }

        public double getEstimatedDistance() {
            return estimatedDistance;
        }

        public double getEstimatedCharges() {
            return estimatedCharges;
        }
    }
}
