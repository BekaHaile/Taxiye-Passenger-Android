package com.sabkuchfresh.retrofit.model.feed;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Parminder Saini on 10/10/17.
 */

public class DynamicDeliveryResponse extends FeedCommonResponse {

    @SerializedName("delivery_charges")
    private DeliveryCharges deliveryCharges;

    @SerializedName("discount")
    private ArrayList<ReferalCode> referalCode;


    public ArrayList<ReferalCode>  getReferalCode() {
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
        @SerializedName("currency_code")
        String currencyCode;
        @SerializedName("currency")
        String currency;

        public String getCurrencyCode() {
            return currencyCode;
        }

        public String getCurrency() {
            return currency;
        }

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
        @SerializedName("promo_name")
        private String referalName;

        @SerializedName("promo_id")
        private Integer id;

        @SerializedName("message_to_display")
        private String message;

        @SerializedName("is_promo_applied")
        private int isPromoApplied;

        public String getReferalName() {
            return referalName;
        }

        public Integer getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public boolean isPromoApplied(){
            return isPromoApplied ==1;
        }

        public void setIsPromoApplied(int isPromoApplied) {
            this.isPromoApplied = isPromoApplied;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
