package com.sabkuchfresh.retrofit.model;

/**
 * Created by ankit on 02/01/17.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.datastructure.SubscriptionData;

public class PurchaseSubscriptionResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("cancellation_charges_popup_text_line1")
    @Expose
    private String cancellationChargesPopupTextLine1;
    @SerializedName("cancellation_charges_popup_text_line2")
    @Expose
    private String cancellationChargesPopupTextLine2;
    @SerializedName("user_subscriptions")
    @Expose
    private List<SubscriptionData.UserSubscription> userSubscriptions = null;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCancellationChargesPopupTextLine1() {
        return cancellationChargesPopupTextLine1;
    }

    public void setCancellationChargesPopupTextLine1(String cancellationChargesPopupTextLine1) {
        this.cancellationChargesPopupTextLine1 = cancellationChargesPopupTextLine1;
    }

    public String getCancellationChargesPopupTextLine2() {
        return cancellationChargesPopupTextLine2;
    }

    public void setCancellationChargesPopupTextLine2(String cancellationChargesPopupTextLine2) {
        this.cancellationChargesPopupTextLine2 = cancellationChargesPopupTextLine2;
    }

    public List<SubscriptionData.UserSubscription> getUserSubscriptions() {
        return userSubscriptions;
    }

    public void setUserSubscriptions(List<SubscriptionData.UserSubscription> userSubscriptions) {
        this.userSubscriptions = userSubscriptions;
    }


}


