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
    @SerializedName("recent_orders_possible_status")
    @Expose
    private ArrayList<String> recentOrdersPossibleStatus = new ArrayList<>();

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

    public ArrayList<String> getRecentOrdersPossibleStatus() {
        return recentOrdersPossibleStatus;
    }

    public void setRecentOrdersPossibleStatus(ArrayList<String> recentOrdersPossibleStatus) {
        this.recentOrdersPossibleStatus = recentOrdersPossibleStatus;
    }

    public class Datum {

        @SerializedName("pickup_address")
        @Expose
        private String pickupAddress;
        @SerializedName("drop_address")
        @Expose
        private String dropAddress;
        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("driver_id")
        @Expose
        private Integer driverId = 0;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("wait_time")
        @Expose
        private Double waitTime = -1d;
        @SerializedName("is_cancelled_ride")
        @Expose
        private Integer isCancelledRide = 0;
        @SerializedName("ride_time")
        @Expose
        private Double rideTime;
        @SerializedName("amount")
        @Expose
        private Double amount;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("product_type")
        @Expose
        private Integer productType;
        @SerializedName("customer_fare_factor")
        @Expose
        private Double customerFareFactor;
        @SerializedName("cancellation_charges")
        @Expose
        private Double cancellationCharges;
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


        @SerializedName("original_order_amount")
        @Expose
        private Double originalOrderAmount;

        @SerializedName("order_item_amount_sum")
        @Expose
        private Double orderItemAmountSum;

        @SerializedName("order_amount")
        @Expose
        private Double orderAmount;

        @SerializedName("order_billable_amount")
        @Expose
        private Double orderBillableAmount;

        @SerializedName("order_payble_amount")
        @Expose
        private Double orderPaybleAmount;



        @SerializedName("jugnoo_deducted")
        @Expose
        private Double jugnooDeducted;
        @SerializedName("paytm_deducted")
        @Expose
        private Double paytmDeducted;
        @SerializedName("wallet_deducted")
        @Expose
        private Double walletDeducted;
        @SerializedName("discount")
        @Expose
        private Double discount;
        @SerializedName("delivery_charges")
        @Expose
        private Double deliveryCharges;
        @SerializedName("delivery_address")
        @Expose
        private String deliveryAddress;
        @SerializedName("order_refund_amount")
        @Expose
        private Double orderRefundAmount;
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
        @SerializedName("order_tracking_index")
        @Expose
        private Integer orderTrackingIndex;
        @SerializedName("order_status_int")
        @Expose
        private Integer orderStatusInt;
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
        private Integer cancellable = 0;
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
        @SerializedName("autos_status")
        @Expose
        private Integer autosStatus;
        @SerializedName("autos_status_color")
        @Expose
        private String autosStatusColor;
        @SerializedName("autos_status_text")
        @Expose
        private String autosStatusText;
        @SerializedName("support_category")
        @Expose
        private Integer supportCategory;

        @SerializedName("vehicle_type")
        @Expose
        private Integer vehicleType;
        @SerializedName("ride_type")
        @Expose
        private Integer rideType;
        @SerializedName("support_number")
        @Expose
        private String supportNumber;
        @SerializedName("phone_no")
        @Expose
        private String phoneNo;
        @SerializedName("delivery_address_type")
        @Expose
        private String deliveryAddressType;

        @SerializedName("restaurant_id")
        @Expose
        private Integer restaurantId;
        @SerializedName("restaurant_name")
        @Expose
        private String restaurantName;
        @SerializedName("restaurant_address")
        @Expose
        private String restaurantAddress;
        @SerializedName("restaurant_phone_no")
        @Expose
        private String restaurantPhoneNo;
        @SerializedName("service_tax")
        @Expose
        private Double serviceTax;
        @SerializedName("value_added_tax")
        @Expose
        private Double valueAddedTax;
        @SerializedName("packing_charges")
        @Expose
        private Double packingCharges;
        @SerializedName("delivery_latitude")
        @Expose
        private Double deliveryLatitude;
        @SerializedName("delivery_longitude")
        @Expose
        private Double deliveryLongitude;
        @SerializedName("address_id")
        @Expose
        private Integer addressId;

        @SerializedName("old_amount")
        @Expose
        private Double oldAmount;
        @SerializedName("new_amount")
        @Expose
        private Double newAmount;
        @SerializedName("total_amount")
        @Expose
        private Double totalAmount;
        @SerializedName("payable_amount")
        @Expose
        private Double payableAmount;
        @SerializedName("total_amount_info_text")
        @Expose
        private String totalAmountInfoText;

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
        public Double getWaitTime() {
            return waitTime;
        }

        /**
         * @param waitTime The wait_time
         */
        public void setWaitTime(Double waitTime) {
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

        public Integer getOrderTrackingIndex() {
            return orderTrackingIndex;
        }

        public void setOrderTrackingIndex(Integer orderTrackingIndex) {
            this.orderTrackingIndex = orderTrackingIndex;
        }

        /**
         * @return The rideTime
         */
        public Double getRideTime() {
            return rideTime;
        }

        /**
         * @param rideTime The ride_time
         */
        public void setRideTime(Double rideTime) {
            this.rideTime = rideTime;
        }

        /**
         * @return The amount
         */
        public Double getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(Double amount) {
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

        public Integer getOrderStatusInt() {
            return orderStatusInt;
        }

        public void setOrderStatusInt(Integer orderStatusInt) {
            this.orderStatusInt = orderStatusInt;
        }

        /**
         * @return The customerFareFactor
         */
        public Double getCustomerFareFactor() {
            return customerFareFactor;
        }

        /**
         * @param customerFareFactor The customer_fare_factor
         */
        public void setCustomerFareFactor(Double customerFareFactor) {
            this.customerFareFactor = customerFareFactor;
        }

        /**
         * @return The cancellationCharges
         */
        public Double getCancellationCharges() {
            return cancellationCharges;
        }

        /**
         * @param cancellationCharges The cancellation_charges
         */
        public void setCancellationCharges(Double cancellationCharges) {
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

        public Double getOriginalOrderAmount() {
            return originalOrderAmount;
        }

        public void setOriginalOrderAmount(Double originalOrderAmount) {
            this.originalOrderAmount = originalOrderAmount;
        }

        public Double getOrderBillableAmount() {
            return orderBillableAmount;
        }

        public void setOrderBillableAmount(Double orderBillableAmount) {
            this.orderBillableAmount = orderBillableAmount;
        }


        public Double getOrderPaybleAmount() {
            return orderPaybleAmount;
        }

        public void setOrderPaybleAmount(Double orderPaybleAmount) {
            this.orderPaybleAmount = orderPaybleAmount;
        }


        public Double getOrderItemAmountSum() {
            return orderItemAmountSum;
        }

        public void setOrderItemAmountSum(Double orderItemAmountSum) {
            this.orderItemAmountSum = orderItemAmountSum;
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
        public Double getJugnooDeducted() {
            return jugnooDeducted;
        }

        /**
         * @param jugnooDeducted The jugnoo_deducted
         */
        public void setJugnooDeducted(Double jugnooDeducted) {
            this.jugnooDeducted = jugnooDeducted;
        }

        /**
         * @return The paytmDeducted
         */
        public Double getPaytmDeducted() {
            return paytmDeducted;
        }

        /**
         * @param paytmDeducted The paytm_deducted
         */
        public void setPaytmDeducted(Double paytmDeducted) {
            this.paytmDeducted = paytmDeducted;
        }

        /**
         * @return The discount
         */
        public Double getDiscount() {
            return discount;
        }

        /**
         * @param discount The discount
         */
        public void setDiscount(Double discount) {
            this.discount = discount;
        }

        /**
         * @return The deliveryCharges
         */
        public Double getDeliveryCharges() {
            return deliveryCharges;
        }

        /**
         * @param deliveryCharges The delivery_charges
         */
        public void setDeliveryCharges(Double deliveryCharges) {
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
        public Double getOrderRefundAmount() {
            if(orderRefundAmount != null){
                return orderRefundAmount;
            } else {
                return 0d;
            }
        }

        /**
         * @param orderRefundAmount The order_refund_amount
         */
        public void setOrderRefundAmount(Double orderRefundAmount) {
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
            if(cancellable != null){
                return cancellable;
            } else {
                return 0;
            }
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
            if(canReorder != null){
                return canReorder;
            } else {
                return 0;
            }
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

        public Integer getAutosStatus() {
            return autosStatus;
        }

        public void setAutosStatus(Integer autosStatus) {
            this.autosStatus = autosStatus;
        }

        public Integer getSupportCategory() {
            if(supportCategory != null) {
                return supportCategory;
            } else {
                return 0;
            }
        }

        public void setSupportCategory(Integer supportCategory) {
            this.supportCategory = supportCategory;
        }

        public String getAutosStatusColor() {
            return autosStatusColor;
        }

        public void setAutosStatusColor(String autosStatusColor) {
            this.autosStatusColor = autosStatusColor;
        }

        public String getAutosStatusText() {
            return autosStatusText;
        }

        public void setAutosStatusText(String autosStatusText) {
            this.autosStatusText = autosStatusText;
        }

        public Double getWalletDeducted() {
            return walletDeducted;
        }

        public void setWalletDeducted(Double walletDeducted) {
            this.walletDeducted = walletDeducted;
        }

        public Integer getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(Integer vehicleType) {
            this.vehicleType = vehicleType;
        }

        public Integer getRideType() {
            return rideType;
        }

        public void setRideType(Integer rideType) {
            this.rideType = rideType;
        }

        public String getSupportNumber() {
            return supportNumber;
        }

        public void setSupportNumber(String supportNumber) {
            this.supportNumber = supportNumber;
        }

        public String getPhoneNo() {
            String phone = null;
            if(phoneNo != null && !phoneNo.equalsIgnoreCase("")) {
                phone = phoneNo;
            } else{
                phone = getSupportNumber();
            }
            return phone;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getDeliveryAddressType() {
            if(deliveryAddressType == null){
                return "";
            }
            return deliveryAddressType;
        }

        public void setDeliveryAddressType(String deliveryAddressType) {
            this.deliveryAddressType = deliveryAddressType;
        }


      /*  public int getSubAmountValue() {
            return subAmountValue;
        }

        public void setSubAmountValue(int subAmountValue) {
            this.subAmountValue = subAmountValue;
        }


        public String getTotalAmountValue() {
            return totalAmountValue;
        }

        public void setTotalAmountValue(String totalAmountValue) {
            this.totalAmountValue = totalAmountValue;
        }*/


        public Integer getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(Integer restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public String getRestaurantAddress() {
            return restaurantAddress;
        }

        public void setRestaurantAddress(String restaurantAddress) {
            this.restaurantAddress = restaurantAddress;
        }

        public String getRestaurantPhoneNo() {
            return restaurantPhoneNo;
        }

        public void setRestaurantPhoneNo(String restaurantPhoneNo) {
            this.restaurantPhoneNo = restaurantPhoneNo;
        }

        public Double getServiceTax() {
            if(serviceTax != null){
                return serviceTax;
            } else {
                return 0d;
            }
        }

        public void setServiceTax(Double serviceTax) {
            this.serviceTax = serviceTax;
        }

        public Double getValueAddedTax() {
            if(valueAddedTax != null){
                return valueAddedTax;
            } else {
                return 0d;
            }
        }

        public void setValueAddedTax(Double valueAddedTax) {
            this.valueAddedTax = valueAddedTax;
        }

        public Double getPackingCharges() {
            if(packingCharges != null){
                return packingCharges;
            } else {
                return 0d;
            }
        }

        public void setPackingCharges(Double packingCharges) {
            this.packingCharges = packingCharges;
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

        public Integer getAddressId() {
            if(addressId == null){
                return 0;
            }
            return addressId;
        }

        public void setAddressId(Integer addressId) {
            this.addressId = addressId;
        }

        public Integer getCityId() {
            if(cityId == null){
                cityId = -1;
            }
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public Double getOldAmount() {
            return oldAmount;
        }

        public void setOldAmount(Double oldAmount) {
            this.oldAmount = oldAmount;
        }

        public Double getNewAmount() {
            return newAmount;
        }

        public void setNewAmount(Double newAmount) {
            this.newAmount = newAmount;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Double getPayableAmount() {
            return payableAmount;
        }

        public void setPayableAmount(Double payableAmount) {
            this.payableAmount = payableAmount;
        }

        public String getTotalAmountInfoText() {
            return totalAmountInfoText;
        }

        public void setTotalAmountInfoText(String totalAmountInfoText) {
            this.totalAmountInfoText = totalAmountInfoText;
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
        @SerializedName("unit_amount")
        @Expose
        private Double unitAmount;
        @SerializedName("item_cancelled")
        @Expose
        private Integer itemCancelled;
        @SerializedName("customisations")
        @Expose
        private String customisations;
        @SerializedName("sub_item_image")
        @Expose
        private String subItemImage;
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


        public Integer getItemCancelled() {
            if(itemCancelled == null){
                return 0;
            }
            return itemCancelled;
        }

        /**
         * @param itemCancelled The item_amount
         */
        public void setItemCancelled(Integer itemCancelled) {
            this.itemCancelled = itemCancelled;
        }

        public Double getUnitAmount() {
            if(unitAmount != null){
                return unitAmount;
            } else {
                return 0.0;
            }
        }

        public void setUnitAmount(Double unitAmount) {
            this.unitAmount = unitAmount;
        }

        public String getCustomisations() {
            return customisations;
        }

        public void setCustomisations(String customisations) {
            this.customisations = customisations;
        }

        public String getSubItemImage() {
            return subItemImage;
        }

        public void setSubItemImage(String subItemImage) {
            this.subItemImage = subItemImage;
        }
    }
}
