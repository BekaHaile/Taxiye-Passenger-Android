package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 26/10/16.
 */

public class RecentOrder {

    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("expected_delivery_date")
    @Expose
    private String expectedDeliveryDate;
    @SerializedName("end_time")
    @Expose
    private String endTime;

    /**
     *
     * @return
     * The orderId
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     *
     * @param orderId
     * The order_id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The expectedDeliveryDate
     */
    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    /**
     *
     * @param expectedDeliveryDate
     * The expected_delivery_date
     */
    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    /**
     *
     * @return
     * The endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime
     * The end_time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
