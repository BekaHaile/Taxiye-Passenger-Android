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
    @SerializedName("order_status_text")
    @Expose
    private String orderStatusText;
    @SerializedName("pickup_latitude")
    @Expose
    private Double pickupLatitude;
    @SerializedName("pickup_longitude")
    @Expose
    private Double pickupLongitude;
    @SerializedName("delivery_latitude")
    @Expose
    private Double deliveryLatitude;
    @SerializedName("delivery_longitude")
    @Expose
    private Double deliveryLongitude;
    @SerializedName("show_live_tracking")
    @Expose
    private Integer showLiveTracking;
    @SerializedName("delivery_id")
    @Expose
    private Integer deliveryId;
    @SerializedName("restaurant_name")
    @Expose
    private String restaurantName;
    @SerializedName("order_amount")
    @Expose
    private Double orderAmount;
    @SerializedName("show_delivery_route")
    @Expose
    private int showDeliveryRoute;
    @SerializedName("driver_phone_no")
    @Expose
    private String driverPhoneNo;
    @SerializedName("track_delivery_message")
    @Expose
    private String trackDeliveryMessage;

    @SerializedName("delivery_not_done")
    @Expose
    private boolean deliveryNotDone;
    @SerializedName("delivery_not_done_msg")
    @Expose
    private String deliveryNotDoneMsg;
    @SerializedName("restaurant_number")
    @Expose
    private String restaurantNumber;
    @SerializedName("delivery_confirmation")
    @Expose
    private int deliveryConfirmation = -1;
    @SerializedName("delivery_confirmation_msg")
    @Expose
    private String deliveryConfirmationMsg;
    @SerializedName("support_id")
    @Expose
    private int supportId;
    @SerializedName("support_title")
    @Expose
    private String supportTitle;
    @SerializedName("support_text")
    @Expose
    private String supportText;




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

    public String getOrderStatusText() {
        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public Integer getShowLiveTracking() {
        if(showLiveTracking == null){
            showLiveTracking = 0;
        }
        return showLiveTracking;
    }

    public void setShowLiveTracking(Integer showLiveTracking) {
        this.showLiveTracking = showLiveTracking;
    }

    public Integer getDeliveryId() {
        if(deliveryId == null){
            deliveryId = 0;
        }
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getShowDeliveryRoute() {
        return showDeliveryRoute;
    }

    public void setShowDeliveryRoute(int showDeliveryRoute) {
        this.showDeliveryRoute = showDeliveryRoute;
    }

    public String getDriverPhoneNo() {
        return driverPhoneNo;
    }

    public void setDriverPhoneNo(String driverPhoneNo) {
        this.driverPhoneNo = driverPhoneNo;
    }

    public String getTrackDeliveryMessage() {
        return trackDeliveryMessage;
    }

    public void setTrackDeliveryMessage(String trackDeliveryMessage) {
        this.trackDeliveryMessage = trackDeliveryMessage;
    }

    public boolean isDeliveryNotDone() {
        return deliveryNotDone;
    }

    public String getDeliveryNotDoneMsg() {
        return deliveryNotDoneMsg;
    }

    public String getRestaurantNumber() {
        return restaurantNumber;
    }

    public int getSupportId() {
        return supportId;
    }

    public String getSupportTitle() {
        return supportTitle;
    }

    public String getSupportText() {
        return supportText;
    }

    public int getDeliveryConfirmation() {
        return deliveryConfirmation;
    }

    public void setDeliveryConfirmation(int deliveryConfirmation) {
        this.deliveryConfirmation = deliveryConfirmation;
    }

    public String getDeliveryConfirmationMsg() {
        return deliveryConfirmationMsg;
    }

    public void setDeliveryConfirmationMsg(String deliveryConfirmationMsg) {
        this.deliveryConfirmationMsg = deliveryConfirmationMsg;
    }
}
