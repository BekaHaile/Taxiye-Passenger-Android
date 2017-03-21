package com.sabkuchfresh.datastructure;

/**
 * Created by ankit on 17/03/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.retrofit.model.DeliveryStore;

import java.util.List;

public class Stores {

    @SerializedName("delivery_stores")
    @Expose
    private List<DeliveryStore> deliveryStores = null;

    public List<DeliveryStore> getDeliveryStores() {
        return deliveryStores;
    }

    public void setDeliveryStores(List<DeliveryStore> deliveryStores) {
        this.deliveryStores = deliveryStores;
    }


}
