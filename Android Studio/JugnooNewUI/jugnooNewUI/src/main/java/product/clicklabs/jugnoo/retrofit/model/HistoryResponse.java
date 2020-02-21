package product.clicklabs.jugnoo.retrofit.model;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.pros.models.ProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;
import com.sabkuchfresh.retrofit.model.menus.Charges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.wallet.WalletCore;
import product.clicklabs.jugnoo.retrofit.model.Deliveries;
/**
 * Created by gurmail on 19/08/16.
 */

public class HistoryResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName(value = "data", alternate = "order_history")
    @Expose
    private List<Datum> data = new ArrayList<>();
    @SerializedName("history_size")
    @Expose
    private Integer historySize;
    @SerializedName("recent_orders_possible_status")
    @Expose
    private ArrayList<String> recentOrdersPossibleStatus = new ArrayList<>();
    @SerializedName("recent_orders_possible_fatafat_status")
    @Expose
    private ArrayList<String> recentOrdersPossibleFatafatStatus = new ArrayList<String>();

    public ArrayList<String> getRecentOrdersPossibleFatafatStatus() {
        return recentOrdersPossibleFatafatStatus;
    }
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

        @SerializedName("fugu_channel_name")
        private String fuguChannelName;
        @SerializedName("fugu_channel_id")
        private String fuguChannelId;
        @SerializedName("fugu_tags")
        private ArrayList<String> fuguTags;
        @SerializedName("pickup_address")
        @Expose
        private String pickupAddress;
        @SerializedName("drop_address")
        @Expose
        private String dropAddress;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("is_cancelled_ride")
        @Expose
        private Integer isCancelledRide = 0;
        @SerializedName("ride_time")
        @Expose
        private Double rideTime;
        @SerializedName("amount")
        @Expose
        private double amount;
        @SerializedName("product_type")
        @Expose
        private Integer productType;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("engagement_id")
        @Expose
        private Integer engagementId = 0;
        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("store_id")
        @Expose
        private Integer storeId;
        @SerializedName("discounted_amount")
        @Expose
        private double discountedAmount;
        @SerializedName("order_amount")
        @Expose
        private double orderAmount;
        @SerializedName("discount")
        @Expose
        private double discount;
        @SerializedName("sub_total")
        @Expose
        private double subTotal;
        @SerializedName("jugnoo_deducted")
        @Expose
        private double jugnooDeducted;
        @SerializedName("wallet_deducted")
        @Expose
        private double walletDeducted;
        @SerializedName("charges")
        @Expose
        private List<Charges> charges;
        @SerializedName("delivery_address")
        @Expose
        private String deliveryAddress;
        @SerializedName("refund_amount")
        @Expose
        private double refundAmount;
        @SerializedName("expected_delivery_date")
        @Expose
        private String expectedDeliveryDate;
        @SerializedName("start_time")
        @Expose
        private String startTime;
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
        @SerializedName("order_delivery_time")
        @Expose
        private String orderDeliveryTime;
        @SerializedName("cancellable")
        @Expose
        private int cancellable = 0;
        @SerializedName("can_reorder")
        @Expose
        private int canReorder = 0;
        @SerializedName("order_items")
        @Expose
        private List<OrderItem> orderItems = new ArrayList<>();
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
        @SerializedName("icon_set")
        @Expose
        private String iconSet;
        @SerializedName("history_icon")
        @Expose
        private String historyIcon;
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
        @SerializedName("restaurant_name")
        @Expose
        private String restaurantName;
        @SerializedName("restaurant_address")
        @Expose
        private String restaurantAddress;
        @SerializedName("restaurant_phone_no")
        @Expose
        private String restaurantPhoneNo;
        @SerializedName("address_id")
        @Expose
        private Integer addressId;
        @SerializedName("payable_amount")
        @Expose
        private double payableAmount;
        @SerializedName("note")
        @Expose
        private String note;
        @SerializedName("show_cancellation_reasons")
        @Expose
        private Integer showCancellationReasons;
        @SerializedName("other_payment_mode_text")
        @Expose
        private String otherPaymentModeText;
        @SerializedName("order_adjustment")
        @Expose
        private double orderAdjustment;
        @SerializedName("live_tracking")
        @Expose
        private LiveTracking liveTracking;

        @SerializedName("bill_summary")
        private ArrayList<BillSummaryModel> billSummary;

        @SerializedName("delivery_charges")
        private double deliveryCharges;

        public double getDeliveryCharges() {
            return deliveryCharges;
        }

        public ArrayList<BillSummaryModel> getBillSummary() {
            return billSummary;
        }

        // pros data
        @SerializedName("job_id")
        @Expose
        private int jobId;
        @SerializedName("job_time")
        @Expose
        private String jobTime;
        @SerializedName("job_status")
        @Expose
        private int jobStatus;
        @SerializedName("job_description")
        @Expose
        private String jobDescription;
        @SerializedName("job_address")
        @Expose
        private String jobAddress;
        @SerializedName("job_pickup_datetime")
        @Expose
        private String jobPickupDatetime;
        @SerializedName("job_delivery_datetime")
        @Expose
        private String jobDeliveryDatetime;
        @SerializedName("fields")
        @Expose
        private ProsOrderStatusResponse.Fields fields;

        @SerializedName("from_address")
        @Expose
        private String fromAddress;
        @SerializedName("from_latitude")
        @Expose
        private double fromLatitude;
        @SerializedName("from_longitude")
        @Expose
        private double fromLongitude;
        @SerializedName("to_address")
        @Expose
        private String toAddress;
        @SerializedName("to_latitude")
        @Expose
        private double toLatitude;
        @SerializedName("to_longitude")
        @Expose
        private double toLongitude;
        @SerializedName("details")
        @Expose
        private String details;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("delivery_time")
        @Expose
        private String deliveryTime;
        @SerializedName("status")
        private int orderStatusIndex;
        @SerializedName("category")
        private int category;
        @SerializedName("is_paid")
        private int isPaid;
        @SerializedName("currency")
        private String currency;
        @SerializedName("distance_unit")
        private String distanceUnit;
        @SerializedName("currency_code")
        private String currencyCode;
        @Expose
        @SerializedName("pending_feedback")
        private int pendingFeedback = 0;

        @Expose
        @SerializedName("feedback_info")
        private LoginResponse.FeedbackData feedbackData;

        @Expose
        @SerializedName("driver_name")
        private String driveName;

        public boolean isPendingFeedback() {
            return pendingFeedback == 1;
        }

        public LoginResponse.FeedbackData getFeedBackData() {
            return feedbackData;
        }

        public String getDriveName() {
            return driveName;
        }

        public void setDriveName(String driveName) {
            this.driveName = driveName;
        }

        @Expose
        @SerializedName("deliveries")
        private ArrayList<product.clicklabs.jugnoo.retrofit.model.Deliveries> deliveries;

        @Expose
        @SerializedName("is_corporate")
        private String is_corporate;

        @Expose
        @SerializedName("driver_id")
        private String driver_id;

        @Expose
        @SerializedName("order_created_at")
        private String order_created_at;

        public String getDriverName() {
            return driveName;
        }

        public String getOrder_created_at() {
            return order_created_at;
        }

        public String getDriver_id() {
            return driver_id;
        }

        @SerializedName("card_details")
        private List<CardDetails> cardDetails;
        private ArrayList<DiscountType> stripeCardsAmount;

        public ArrayList<DiscountType> getStripeCardsAmount() {
            ArrayList<DiscountType> stripeCardsAmountTemp = new ArrayList<>();
            if (getCardDetails() != null) {
                for (int i = 0; i < getCardDetails().size(); i++) {
                    DiscountType discountType = new DiscountType(WalletCore.getStripeCardDisplayString(MyApplication.getInstance(),
                            getCardDetails().get(i).getLast4()),
                            getCardDetails().get(i).getAmountPaid(),
                            getCardDetails().get(i).getId());
                    if (discountType.value > 0) {
                        int index = stripeCardsAmountTemp.indexOf(discountType);
                        if (index > -1) {
                            stripeCardsAmountTemp.get(index).setValue(stripeCardsAmountTemp.get(index).getValue() + discountType.value);
                        } else {
                            stripeCardsAmountTemp.add(discountType);
                        }
                    }
                }
            }
            return stripeCardsAmountTemp;
        }

        public List<CardDetails> getCardDetails() {
            return cardDetails;
        }

        public int getIsPaid() {
            return isPaid;
        }

        public int getCategory() {
            return category;
        }

        public int getOrderStatusIndex() {
            return orderStatusIndex;
        }

        public String getFuguChannelName() {
            return fuguChannelName;
        }

        public String getFuguChannelId() {
            return fuguChannelId;
        }

        public ArrayList<String> getFuguTags() {
            return fuguTags;
        }

        @SerializedName("charges_break_down")
        ArrayList<HashMap<String,Double>> popupData ;

        @SerializedName(value = "user_images")
        @Expose
        private OrderImageTypes imagesTypes;

        @SerializedName(value = "vehicle_info")
        @Expose
        private VehicleInfo vehicleInfo;

        public VehicleInfo getVehicleInfo() {
            return vehicleInfo;
        }

        public OrderImageTypes getImages() {
            return imagesTypes;
        }

        public String getOrderDeliveryTime() {
            return orderDeliveryTime;
        }

        /**
         * Getter for popupData
         *
         * @return : Returns the value of popupData
         */
        public ArrayList<HashMap<String, Double>> getPopupData() {
            return popupData;
        }

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
        public double getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(double amount) {
            this.amount = amount;
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
        public double getOrderAmount() {
            return orderAmount;
        }

        /**
         * @param orderAmount The order_amount
         */
        public void setOrderAmount(double orderAmount) {
            this.orderAmount = orderAmount;
        }

        /**
         * @return The jugnooDeducted
         */
        public double getJugnooDeducted() {
            return jugnooDeducted;
        }

        /**
         * @param jugnooDeducted The jugnoo_deducted
         */
        public void setJugnooDeducted(double jugnooDeducted) {
            this.jugnooDeducted = jugnooDeducted;
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
        public int getCancellable() {
            return cancellable;
        }

        /**
         * @param cancellable The cancellable
         */
        public void setCancellable(int cancellable) {
            this.cancellable = cancellable;
        }

        /**
         * @return The canReorder
         */
        public int getCanReorder() {
            return canReorder;
        }

        /**
         * @param canReorder The can_reorder
         */
        public void setCanReorder(int canReorder) {
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

        public double getWalletDeducted() {
            return walletDeducted;
        }

        public void setWalletDeducted(double walletDeducted) {
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


        public Integer getAddressId() {
            if(addressId == null){
                return 0;
            }
            return addressId;
        }

        public void setAddressId(Integer addressId) {
            this.addressId = addressId;
        }


        public double getPayableAmount() {
            return payableAmount;
        }

        public void setPayableAmount(double payableAmount) {
            this.payableAmount = payableAmount;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public Integer getShowCancellationReasons() {
            if(showCancellationReasons == null){
                showCancellationReasons = 0;
            }
            return showCancellationReasons;
        }

        public void setShowCancellationReasons(Integer showCancellationReasons) {
            this.showCancellationReasons = showCancellationReasons;
        }

        public String getOtherPaymentModeText() {
            return otherPaymentModeText;
        }

        public void setOtherPaymentModeText(String otherPaymentModeText) {
            this.otherPaymentModeText = otherPaymentModeText;
        }

        public double getDiscountedAmount() {
            return discountedAmount;
        }

        public void setDiscountedAmount(double discountedAmount) {
            this.discountedAmount = discountedAmount;
        }

        public double getSubTotal() {
            return subTotal;
        }

        public void setSubTotal(double subTotal) {
            this.subTotal = subTotal;
        }

        public List<Charges> getCharges() {
            return charges;
        }

        public void setCharges(List<Charges> charges) {
            this.charges = charges;
        }

        public double getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(double refundAmount) {
            this.refundAmount = refundAmount;
        }

        public double getOrderAdjustment() {
            return orderAdjustment;
        }

        public void setOrderAdjustment(double orderAdjustment) {
            this.orderAdjustment = orderAdjustment;
        }


        public LiveTracking getLiveTracking() {
            return liveTracking;
        }

        public int getJobId() {
            return jobId;
        }

        public void setJobId(int jobId) {
            this.jobId = jobId;
        }

        public String getJobTime() {
            return jobTime;
        }

        public void setJobTime(String jobTime) {
            this.jobTime = jobTime;
        }

        public int getJobStatus() {
            return jobStatus;
        }

        public int getJobStatusColorRes() {
            if(jobStatus == ProsOrderStatus.FAILED.getOrdinal()
                    || jobStatus == ProsOrderStatus.CANCEL.getOrdinal()
                    || jobStatus == ProsOrderStatus.DELETED.getOrdinal()
                    || jobStatus == ProsOrderStatus.IGNORED.getOrdinal()){
                return R.color.red_status;
            } else {
                return R.color.green_status;
            }
        }

        public void setJobStatus(int jobStatus) {
            this.jobStatus = jobStatus;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public String getJobNameSplitted() {
            String[] arr = jobDescription.split("\\:\\ ");
            return arr[0];
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public String getJobAddress() {
            return jobAddress;
        }

        public void setJobAddress(String jobAddress) {
            this.jobAddress = jobAddress;
        }

        public String getJobPickupDatetime() {
            return jobPickupDatetime;
        }

        public void setJobPickupDatetime(String jobPickupDatetime) {
            this.jobPickupDatetime = jobPickupDatetime;
        }

        public String getJobDeliveryDatetime() {
            return jobDeliveryDatetime;
        }

        public void setJobDeliveryDatetime(String jobDeliveryDatetime) {
            this.jobDeliveryDatetime = jobDeliveryDatetime;
        }

        public Pair<String, String> getProductNameAndJobAmount(){
            String productName = getJobNameSplitted(), jobAmount = "";
            if(getFields() != null) {
                for (ProsOrderStatusResponse.CustomField customField : getFields().getCustomField()) {
                    if (customField.getLabel().equalsIgnoreCase(Constants.KEY_PRODUCT_NAME)) {
                        productName = customField.getData();
                    } else if (customField.getLabel().equalsIgnoreCase(Constants.KEY_JOB_AMOUNT)
                            && !TextUtils.isEmpty(customField.getFleetData())) {
                        jobAmount = customField.getFleetData();
                    }
                }
            }
            return new Pair<>(productName, jobAmount);
        }

        public ProsOrderStatusResponse.Fields getFields() {
            return fields;
        }

        public void setFields(ProsOrderStatusResponse.Fields fields) {
            this.fields = fields;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public double getFromLatitude() {
            return fromLatitude;
        }

        public void setFromLatitude(double fromLatitude) {
            this.fromLatitude = fromLatitude;
        }

        public double getFromLongitude() {
            return fromLongitude;
        }

        public void setFromLongitude(double fromLongitude) {
            this.fromLongitude = fromLongitude;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public double getToLatitude() {
            return toLatitude;
        }

        public void setToLatitude(double toLatitude) {
            this.toLatitude = toLatitude;
        }

        public double getToLongitude() {
            return toLongitude;
        }

        public void setToLongitude(double toLongitude) {
            this.toLongitude = toLongitude;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(String deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getIconSet() {
            return iconSet;
        }

        public void setIconSet(String iconSet) {
            this.iconSet = iconSet;
        }

        public String getDistanceUnit() {
            return distanceUnit;
        }

        public void setDistanceUnit(String distanceUnit) {
            this.distanceUnit = distanceUnit;
        }

        public String getHistoryIcon() {
            return historyIcon;
        }

        public void setHistoryIcon(String historyIcon) {
            this.historyIcon = historyIcon;
        }

        public ArrayList<product.clicklabs.jugnoo.retrofit.model.Deliveries> getDeliveries() {
            return deliveries;
        }

        public void setDeliveries(final ArrayList<Deliveries> deliveries) {
            this.deliveries = deliveries;
        }

        public boolean isCorporate() {
            return is_corporate != null && is_corporate.equals("1");
        }

        public void setIs_corporate(final String is_corporate) {
            this.is_corporate = is_corporate;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }
    }


    public class OrderItem {

        @SerializedName("sub_item_id")
        @Expose
        private Integer subItemId;
        @SerializedName("item_amount")
        @Expose
        private Double itemAmount;
        @SerializedName("item_quantity")
        @Expose
        private Integer itemQuantity;
        @SerializedName("item_name")
        @Expose
        private String itemName;
        @SerializedName("item_unit_price")
        @Expose
        private double itemUnitPrice;
        @SerializedName("item_cancelled")
        @Expose
        private Integer itemCancelled;
        @SerializedName("customisations")
        @Expose
        private String customisations;
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

        public String getCustomisations() {
            return customisations;
        }

        public void setCustomisations(String customisations) {
            this.customisations = customisations;
        }

        public double getItemUnitPrice() {
            return itemUnitPrice;
        }

        public void setItemUnitPrice(double itemUnitPrice) {
            this.itemUnitPrice = itemUnitPrice;
        }
    }


    public class LiveTracking{
        @SerializedName("pickup_latitude")
        @Expose
        private double pickupLatitude;
        @SerializedName("pickup_longitude")
        @Expose
        private double pickupLongitude;
        @SerializedName("delivery_latitude")
        @Expose
        private double deliveryLatitude;
        @SerializedName("delivery_longitude")
        @Expose
        private double deliveryLongitude;
        @SerializedName("show_live_tracking")
        @Expose
        private int showLiveTracking;
        @SerializedName("delivery_id")
        @Expose
        private int deliveryId;
        @SerializedName("show_delivery_route")
        @Expose
        private int showDeliveryRoute;
        @SerializedName("driver_phone_no")
        @Expose
        private String driverPhoneNo;
        @SerializedName("track_delivery_message")
        @Expose
        private String trackDeliveryMessage;

        public double getPickupLatitude() {
            return pickupLatitude;
        }

        public double getPickupLongitude() {
            return pickupLongitude;
        }

        public int getShowLiveTracking() {
            return showLiveTracking;
        }

        public int getDeliveryId() {
            return deliveryId;
        }

        public int getShowDeliveryRoute() {
            return showDeliveryRoute;
        }

        public String getDriverPhoneNo() {
            return driverPhoneNo;
        }

        public String getTrackDeliveryMessage() {
            return trackDeliveryMessage;
        }

        public double getDeliveryLatitude() {
            return deliveryLatitude;
        }

        public double getDeliveryLongitude() {
            return deliveryLongitude;
        }

    }

    public class OrderImageTypes {

        @SerializedName("delivery")
        @Expose
        private ArrayList<OrderImages> deliveryImages;

        @SerializedName("pickup")
        @Expose
        private ArrayList<OrderImages> pickupImages;

        public ArrayList<OrderImages> getDeliveryImages() {
            return deliveryImages;
        }

        public ArrayList<OrderImages> getPickupImages() {
            return pickupImages;
        }
    }

    public class VehicleInfo {

        @SerializedName("image")
        @Expose
        private String image;

        public String getImage() {
            return image;
        }
    }


    public class OrderImages {

        @SerializedName("image")
        @Expose
        private String image = "";

        @SerializedName("delivery_id")
        @Expose
        private int delivery_id;

        @SerializedName("order_id")
        @Expose
        private int order_id;

        @SerializedName("type")
        @Expose
        private int type;

        @SerializedName("source")
        @Expose
        private int source ;

        private int deliveryNo = -1;

        public String getImage() {
            return image;
        }

        public int getDelivery_id() {
            return delivery_id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public int getType() {
            return type;
        }

        public int getSource() {
            return source;
        }

        public void setDeliveryNo(final int deliveryNo) {
            this.deliveryNo = deliveryNo;
        }

        public int getDeliveryNo() {
            return deliveryNo;
        }
    }
}
