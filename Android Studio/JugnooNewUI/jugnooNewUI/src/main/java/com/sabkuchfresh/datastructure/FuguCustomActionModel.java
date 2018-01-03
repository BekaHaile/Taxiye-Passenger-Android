package com.sabkuchfresh.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/21/17.
 */

public class FuguCustomActionModel {

    @SerializedName("action_type")
    private String actionType;

    @SerializedName("reference")
    private String reference;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("total_amount")
    private double amount;

    @SerializedName("deepindex")
    private Integer deepIndex;

    public double getAmount() {
        return amount;
    }

    public String getActionType() {
        return actionType;
    }

    public String getReference() {
        return reference;
    }

    public int getOrderId() {
        return orderId;
    }

    public Integer getDeepIndex() {
        return deepIndex;
    }
}
