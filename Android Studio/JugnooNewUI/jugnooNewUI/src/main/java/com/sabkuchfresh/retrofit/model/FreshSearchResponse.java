package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 23/01/17.
 */

public class FreshSearchResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("item_info")
    @Expose
    private List<SuperCategoriesData.SuperCategory> superCategories = null;
    @SerializedName("delivery_stores")
    @Expose
    private List<DeliveryStore> deliveryStores = new ArrayList<DeliveryStore>();

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

    public List<SuperCategoriesData.SuperCategory> getSuperCategories() {
        return superCategories;
    }

    public void setSuperCategories(List<SuperCategoriesData.SuperCategory> superCategories) {
        this.superCategories = superCategories;
    }

    public List<DeliveryStore> getDeliveryStores() {
        return deliveryStores;
    }

    public void setDeliveryStores(List<DeliveryStore> deliveryStores) {
        this.deliveryStores = deliveryStores;
    }
}
