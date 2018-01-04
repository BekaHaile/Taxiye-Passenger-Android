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

    @SerializedName("referal_code")
    private ReferalCode referalCode;


    public ReferalCode getReferalCode() {
        return referalCode;
    }

    public DeliveryCharges getDeliveryCharges() {
        return deliveryCharges;
    }

    public class DeliveryCharges {

        @SerializedName("delivery_charges_inst")
        ArrayList<HashMap<String,Double>> popupData ;

        @SerializedName("estimated_distance")
        String estimatedDistance;

        @SerializedName("estimated_charges")
        double estimatedCharges;

        @SerializedName("delivery_text")
        String deliveryLabel;

        @SerializedName("tnc")
        String tandC;

        public String getTandC() {
            return tandC;
        }

        public String getDeliveryLabel() {
            return deliveryLabel;
        }

        public ArrayList<HashMap<String, Double>> getPopupData() {
            return popupData;
        }

        public String getEstimatedDistance() {
            return estimatedDistance;
        }

        public double getEstimatedCharges() {
            return estimatedCharges;
        }
    }

    public class ReferalCode{
        @SerializedName("referal_name")
        private String referalName;

        @SerializedName("referal_id")
        private Integer id;

        @SerializedName("message")
        private String message;

        @SerializedName("is_error")
        private int isError;

        public String getReferalName() {
            return referalName;
        }

        public Integer getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public boolean isError(){
            return isError==1;
        }
    }
}
