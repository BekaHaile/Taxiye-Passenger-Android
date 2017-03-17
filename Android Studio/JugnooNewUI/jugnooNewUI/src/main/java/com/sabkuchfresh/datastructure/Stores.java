package com.sabkuchfresh.datastructure;

/**
 * Created by ankit on 17/03/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public static class DeliveryStore {

        @SerializedName("vendor_id")
        @Expose
        private Integer vendorId;
        @SerializedName("vendor_name")
        @Expose
        private String vendorName;
        @SerializedName("vendor_address")
        @Expose
        private String vendorAddress;
        @SerializedName("vendor_phone")
        @Expose
        private String vendorPhone;
        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("store_id")
        @Expose
        private Integer storeId;
        @SerializedName("delivery_charges")
        @Expose
        private Integer deliveryCharges;
        @SerializedName("min_amount")
        @Expose
        private Integer minAmount;
        @SerializedName("dynamic_delivery_charges")
        @Expose
        private Integer dynamicDeliveryCharges;
        @SerializedName("min_delivery_charges")
        @Expose
        private Integer minDeliveryCharges;
        @SerializedName("max_delivery_charges")
        @Expose
        private Integer maxDeliveryCharges;
        @SerializedName("default_delivery_charges")
        @Expose
        private Integer defaultDeliveryCharges;
        @SerializedName("operational_cost")
        @Expose
        private Integer operationalCost;
        @SerializedName("threshold_distance")
        @Expose
        private Integer thresholdDistance;
        @SerializedName("midnight_delivery_charges")
        @Expose
        private Integer midnightDeliveryCharges;
        @SerializedName("is_selected")
        @Expose
        private Integer isSelected;

        public Integer getVendorId() {
            return vendorId;
        }

        public void setVendorId(Integer vendorId) {
            this.vendorId = vendorId;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getVendorAddress() {
            return vendorAddress;
        }

        public void setVendorAddress(String vendorAddress) {
            this.vendorAddress = vendorAddress;
        }

        public String getVendorPhone() {
            return vendorPhone;
        }

        public void setVendorPhone(String vendorPhone) {
            this.vendorPhone = vendorPhone;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public Integer getDeliveryCharges() {
            return deliveryCharges;
        }

        public void setDeliveryCharges(Integer deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        public Integer getMinAmount() {
            return minAmount;
        }

        public void setMinAmount(Integer minAmount) {
            this.minAmount = minAmount;
        }

        public Integer getDynamicDeliveryCharges() {
            return dynamicDeliveryCharges;
        }

        public void setDynamicDeliveryCharges(Integer dynamicDeliveryCharges) {
            this.dynamicDeliveryCharges = dynamicDeliveryCharges;
        }

        public Integer getMinDeliveryCharges() {
            return minDeliveryCharges;
        }

        public void setMinDeliveryCharges(Integer minDeliveryCharges) {
            this.minDeliveryCharges = minDeliveryCharges;
        }

        public Integer getMaxDeliveryCharges() {
            return maxDeliveryCharges;
        }

        public void setMaxDeliveryCharges(Integer maxDeliveryCharges) {
            this.maxDeliveryCharges = maxDeliveryCharges;
        }

        public Integer getDefaultDeliveryCharges() {
            return defaultDeliveryCharges;
        }

        public void setDefaultDeliveryCharges(Integer defaultDeliveryCharges) {
            this.defaultDeliveryCharges = defaultDeliveryCharges;
        }

        public Integer getOperationalCost() {
            return operationalCost;
        }

        public void setOperationalCost(Integer operationalCost) {
            this.operationalCost = operationalCost;
        }

        public Integer getThresholdDistance() {
            return thresholdDistance;
        }

        public void setThresholdDistance(Integer thresholdDistance) {
            this.thresholdDistance = thresholdDistance;
        }

        public Integer getMidnightDeliveryCharges() {
            return midnightDeliveryCharges;
        }

        public void setMidnightDeliveryCharges(Integer midnightDeliveryCharges) {
            this.midnightDeliveryCharges = midnightDeliveryCharges;
        }

        public Integer getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(Integer isSelected) {
            this.isSelected = isSelected;
        }
    }

}
