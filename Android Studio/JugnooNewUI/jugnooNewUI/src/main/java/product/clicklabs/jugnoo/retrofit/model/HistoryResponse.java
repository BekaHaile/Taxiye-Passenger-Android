package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 19/08/16.
 */

public class HistoryResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<>();
    @SerializedName("history_size")
    @Expose
    private Integer historySize;

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return The data
     */
    public List<Datum> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Datum> data) {
        this.data = data;
    }

    /**
     * @return The historySize
     */
    public Integer getHistorySize() {
        return historySize;
    }

    /**
     * @param historySize The history_size
     */
    public void setHistorySize(Integer historySize) {
        this.historySize = historySize;
    }


    public class Datum {

        @SerializedName("pickup_address")
        @Expose
        private String pickupAddress;
        @SerializedName("drop_address")
        @Expose
        private String dropAddress;
        @SerializedName("driver_id")
        @Expose
        private Integer driverId = 0;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("wait_time")
        @Expose
        private Integer waitTime = -1;
        @SerializedName("is_cancelled_ride")
        @Expose
        private Integer isCancelledRide = 0;
        @SerializedName("ride_time")
        @Expose
        private Integer rideTime;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("product_type")
        @Expose
        private Integer productType;
        @SerializedName("customer_fare_factor")
        @Expose
        private Integer customerFareFactor;
        @SerializedName("cancellation_charges")
        @Expose
        private Integer cancellationCharges;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("is_rated_before")
        @Expose
        private Integer isRatedBefore;
        @SerializedName("engagement_id")
        @Expose
        private Integer engagementId = 0;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("store_id")
        @Expose
        private Integer storeId;
        @SerializedName("order_amount")
        @Expose
        private Double orderAmount;
        @SerializedName("jugnoo_deducted")
        @Expose
        private Integer jugnooDeducted;
        @SerializedName("paytm_deducted")
        @Expose
        private Integer paytmDeducted;
        @SerializedName("discount")
        @Expose
        private Integer discount;
        @SerializedName("delivery_charges")
        @Expose
        private Integer deliveryCharges;
        @SerializedName("delivery_address")
        @Expose
        private String deliveryAddress;
        @SerializedName("order_refund_amount")
        @Expose
        private Integer orderRefundAmount;
        @SerializedName("expected_delivery_date")
        @Expose
        private String expectedDeliveryDate;
        @SerializedName("start_time")
        @Expose
        private String startTime;
        @SerializedName("pending_feedback")
        @Expose
        private Integer pendingFeedback;
        @SerializedName("end_time")
        @Expose
        private String endTime;
        @SerializedName("payment_mode")
        @Expose
        private Integer paymentMode;
        @SerializedName("order_status")
        @Expose
        private String orderStatus;
        @SerializedName("order_status_color")
        @Expose
        private String orderStatusColor;
        @SerializedName("order_time")
        @Expose
        private String orderTime;
        @SerializedName("cancellable")
        @Expose
        private Integer cancellable;
        @SerializedName("can_reorder")
        @Expose
        private Integer canReorder;
        @SerializedName("order_items")
        @Expose
        private List<OrderItem> orderItems = new ArrayList<>();
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("question_type")
        @Expose
        private Integer questionType;
        @SerializedName("client_id")
        @Expose
        private String clientId;

        /**
         * @return The pickupAddress
         */
        public String getPickupAddress() {
            return pickupAddress;
        }

        /**
         * @param pickupAddress The pickup_address
         */
        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        /**
         * @return The dropAddress
         */
        public String getDropAddress() {
            return dropAddress;
        }

        /**
         * @param dropAddress The drop_address
         */
        public void setDropAddress(String dropAddress) {
            this.dropAddress = dropAddress;
        }

        /**
         * @return The driverId
         */
        public Integer getDriverId() {
            return driverId;
        }

        /**
         * @param driverId The driver_id
         */
        public void setDriverId(Integer driverId) {
            this.driverId = driverId;
        }

        /**
         * @return The distance
         */
        public Double getDistance() {
            return distance;
        }

        /**
         * @param distance The distance
         */
        public void setDistance(Double distance) {
            this.distance = distance;
        }

        /**
         * @return The waitTime
         */
        public Integer getWaitTime() {
            return waitTime;
        }

        /**
         * @param waitTime The wait_time
         */
        public void setWaitTime(Integer waitTime) {
            this.waitTime = waitTime;
        }

        /**
         * @return The isCancelledRide
         */
        public Integer getIsCancelledRide() {
            return isCancelledRide;
        }

        /**
         * @param isCancelledRide The is_cancelled_ride
         */
        public void setIsCancelledRide(Integer isCancelledRide) {
            this.isCancelledRide = isCancelledRide;
        }

        /**
         * @return The rideTime
         */
        public Integer getRideTime() {
            return rideTime;
        }

        /**
         * @param rideTime The ride_time
         */
        public void setRideTime(Integer rideTime) {
            this.rideTime = rideTime;
        }

        /**
         * @return The amount
         */
        public Integer getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        /**
         * @return The createdAt
         */
        public String getCreatedAt() {
            return createdAt;
        }

        /**
         * @param createdAt The created_at
         */
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        /**
         * @return The productType
         */
        public Integer getProductType() {
            return productType;
        }

        /**
         * @param productType The product_type
         */
        public void setProductType(Integer productType) {
            this.productType = productType;
        }

        /**
         * @return The customerFareFactor
         */
        public Integer getCustomerFareFactor() {
            return customerFareFactor;
        }

        /**
         * @param customerFareFactor The customer_fare_factor
         */
        public void setCustomerFareFactor(Integer customerFareFactor) {
            this.customerFareFactor = customerFareFactor;
        }

        /**
         * @return The cancellationCharges
         */
        public Integer getCancellationCharges() {
            return cancellationCharges;
        }

        /**
         * @param cancellationCharges The cancellation_charges
         */
        public void setCancellationCharges(Integer cancellationCharges) {
            this.cancellationCharges = cancellationCharges;
        }

        /**
         * @return The date
         */
        public String getDate() {
            return date;
        }

        /**
         * @param date The date
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * @return The isRatedBefore
         */
        public Integer getIsRatedBefore() {
            return isRatedBefore;
        }

        /**
         * @param isRatedBefore The is_rated_before
         */
        public void setIsRatedBefore(Integer isRatedBefore) {
            this.isRatedBefore = isRatedBefore;
        }

        /**
         * @return The engagementId
         */
        public Integer getEngagementId() {
            return engagementId;
        }

        /**
         * @param engagementId The engagement_id
         */
        public void setEngagementId(Integer engagementId) {
            this.engagementId = engagementId;
        }

        /**
         * @return The userId
         */
        public Integer getUserId() {
            return userId;
        }

        /**
         * @param userId The user_id
         */
        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        /**
         * @return The orderId
         */
        public Integer getOrderId() {
            return orderId;
        }

        /**
         * @param orderId The order_id
         */
        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        /**
         * @return The storeId
         */
        public Integer getStoreId() {
            return storeId;
        }

        /**
         * @param storeId The store_id
         */
        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        /**
         * @return The orderAmount
         */
        public Double getOrderAmount() {
            return orderAmount;
        }

        /**
         * @param orderAmount The order_amount
         */
        public void setOrderAmount(Double orderAmount) {
            this.orderAmount = orderAmount;
        }

        /**
         * @return The jugnooDeducted
         */
        public Integer getJugnooDeducted() {
            return jugnooDeducted;
        }

        /**
         * @param jugnooDeducted The jugnoo_deducted
         */
        public void setJugnooDeducted(Integer jugnooDeducted) {
            this.jugnooDeducted = jugnooDeducted;
        }

        /**
         * @return The paytmDeducted
         */
        public Integer getPaytmDeducted() {
            return paytmDeducted;
        }

        /**
         * @param paytmDeducted The paytm_deducted
         */
        public void setPaytmDeducted(Integer paytmDeducted) {
            this.paytmDeducted = paytmDeducted;
        }

        /**
         * @return The discount
         */
        public Integer getDiscount() {
            return discount;
        }

        /**
         * @param discount The discount
         */
        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        /**
         * @return The deliveryCharges
         */
        public Integer getDeliveryCharges() {
            return deliveryCharges;
        }

        /**
         * @param deliveryCharges The delivery_charges
         */
        public void setDeliveryCharges(Integer deliveryCharges) {
            this.deliveryCharges = deliveryCharges;
        }

        /**
         * @return The deliveryAddress
         */
        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        /**
         * @param deliveryAddress The delivery_address
         */
        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        /**
         * @return The orderRefundAmount
         */
        public Integer getOrderRefundAmount() {
            return orderRefundAmount;
        }

        /**
         * @param orderRefundAmount The order_refund_amount
         */
        public void setOrderRefundAmount(Integer orderRefundAmount) {
            this.orderRefundAmount = orderRefundAmount;
        }

        /**
         * @return The expectedDeliveryDate
         */
        public String getExpectedDeliveryDate() {
            return expectedDeliveryDate;
        }

        /**
         * @param expectedDeliveryDate The expected_delivery_date
         */
        public void setExpectedDeliveryDate(String expectedDeliveryDate) {
            this.expectedDeliveryDate = expectedDeliveryDate;
        }

        /**
         * @return The startTime
         */
        public String getStartTime() {
            return startTime;
        }

        /**
         * @param startTime The start_time
         */
        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        /**
         * @return The pendingFeedback
         */
        public Integer getPendingFeedback() {
            return pendingFeedback;
        }

        /**
         * @param pendingFeedback The pending_feedback
         */
        public void setPendingFeedback(Integer pendingFeedback) {
            this.pendingFeedback = pendingFeedback;
        }

        /**
         * @return The endTime
         */
        public String getEndTime() {
            return endTime;
        }

        /**
         * @param endTime The end_time
         */
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        /**
         * @return The paymentMode
         */
        public Integer getPaymentMode() {
            return paymentMode;
        }

        /**
         * @param paymentMode The payment_mode
         */
        public void setPaymentMode(Integer paymentMode) {
            this.paymentMode = paymentMode;
        }

        /**
         * @return The orderStatus
         */
        public String getOrderStatus() {
            return orderStatus;
        }

        /**
         * @param orderStatus The order_status
         */
        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        /**
         * @return The orderStatusColor
         */
        public String getOrderStatusColor() {
            return orderStatusColor;
        }

        /**
         * @param orderStatusColor The order_status_color
         */
        public void setOrderStatusColor(String orderStatusColor) {
            this.orderStatusColor = orderStatusColor;
        }

        /**
         * @return The orderTime
         */
        public String getOrderTime() {
            return orderTime;
        }

        /**
         * @param orderTime The order_time
         */
        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        /**
         * @return The cancellable
         */
        public Integer getCancellable() {
            return cancellable;
        }

        /**
         * @param cancellable The cancellable
         */
        public void setCancellable(Integer cancellable) {
            this.cancellable = cancellable;
        }

        /**
         * @return The canReorder
         */
        public Integer getCanReorder() {
            return canReorder;
        }

        /**
         * @param canReorder The can_reorder
         */
        public void setCanReorder(Integer canReorder) {
            this.canReorder = canReorder;
        }

        /**
         * @return The orderItems
         */
        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        /**
         * @param orderItems The order_items
         */
        public void setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
        }

        /**
         * @return The question
         */
        public String getQuestion() {
            return question;
        }

        /**
         * @param question The question
         */
        public void setQuestion(String question) {
            this.question = question;
        }

        /**
         * @return The questionType
         */
        public Integer getQuestionType() {
            return questionType;
        }

        /**
         * @param questionType The question_type
         */
        public void setQuestionType(Integer questionType) {
            this.questionType = questionType;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }


    public class OrderItem {

        @SerializedName("sub_item_id")
        @Expose
        private Integer subItemId;
        @SerializedName("item_amount")
        @Expose
        private Double itemAmount;
        @SerializedName("item_refund_amount")
        @Expose
        private Integer itemRefundAmount;
        @SerializedName("item_quantity")
        @Expose
        private Integer itemQuantity;
        @SerializedName("item_name")
        @Expose
        private String itemName;
        @SerializedName("unit")
        @Expose
        private String unit;

        /**
         * @return The subItemId
         */
        public Integer getSubItemId() {
            return subItemId;
        }

        /**
         * @param subItemId The sub_item_id
         */
        public void setSubItemId(Integer subItemId) {
            this.subItemId = subItemId;
        }

        /**
         * @return The itemAmount
         */
        public Double getItemAmount() {
            return itemAmount;
        }

        /**
         * @param itemAmount The item_amount
         */
        public void setItemAmount(Double itemAmount) {
            this.itemAmount = itemAmount;
        }

        /**
         * @return The itemRefundAmount
         */
        public Integer getItemRefundAmount() {
            return itemRefundAmount;
        }

        /**
         * @param itemRefundAmount The item_refund_amount
         */
        public void setItemRefundAmount(Integer itemRefundAmount) {
            this.itemRefundAmount = itemRefundAmount;
        }

        /**
         * @return The itemQuantity
         */
        public Integer getItemQuantity() {
            return itemQuantity;
        }

        /**
         * @param itemQuantity The item_quantity
         */
        public void setItemQuantity(Integer itemQuantity) {
            this.itemQuantity = itemQuantity;
        }

        /**
         * @return The itemName
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * @param itemName The item_name
         */
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        /**
         * @return The unit
         */
        public String getUnit() {
            return unit;
        }

        /**
         * @param unit The unit
         */
        public void setUnit(String unit) {
            this.unit = unit;
        }

    }
}
