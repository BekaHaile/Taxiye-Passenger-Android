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

    public List<SubscriptionData.UserSubscription> getUserSubscriptions() {
        return userSubscriptions;
    }

    public void setUserSubscriptions(List<SubscriptionData.UserSubscription> userSubscriptions) {
        this.userSubscriptions = userSubscriptions;
    }


}


