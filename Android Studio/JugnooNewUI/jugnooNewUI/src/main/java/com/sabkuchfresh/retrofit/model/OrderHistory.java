package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/11/16.
 */
public class OrderHistory{

	@SerializedName("order_id")
	@Expose
	private Integer orderId;
    @SerializedName("store_id")
    @Expose
    private Integer storeId = 1;
	@SerializedName("order_amount")
	@Expose
	private Double orderAmount;
	@SerializedName("delivery_address")
	@Expose
	private String deliveryAddress;
	@SerializedName("order_refund_amount")
	@Expose
	private Double orderRefundAmount;
	@SerializedName("cancellable")
	@Expose
	private Integer cancellable;
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
	@SerializedName("order_items")
	@Expose
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();
	@SerializedName("delivery_charges")
	@Expose
	private Double deliveryCharges;
	@SerializedName("start_time")
	@Expose
	private String startTime;
	@SerializedName("end_time")
	@Expose
	private String endTime;
	@SerializedName("expected_delivery_date")
	@Expose
	private String expectedDeliveryDate;

    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("question_type")
    @Expose
    private Integer questionType;
    @SerializedName("pending_feedback")
    @Expose
    private Integer pendingFeedback;
    @SerializedName("jugnoo_deducted")
    @Expose
    private Double jugnooDeducted;
    @SerializedName("paytm_deducted")
    @Expose
    private Double paytmDeducted;
    @SerializedName("discount")
    @Expose
    private Double discount;
	@SerializedName("can_reorder")
    @Expose
    private Integer canReorder;
	@SerializedName("delivery_latitude")
	@Expose
	private Double deliveryLatitude;
	@SerializedName("delivery_longitude")
	@Expose
	private Double deliveryLongitude;


    public Double getJugnooDeducted() {
        return jugnooDeducted;
    }

    public void setJugnooDeducted(Double jugnooDeducted) {
        this.jugnooDeducted = jugnooDeducted;
    }

    public Double getPaytmDeducted() {
        return paytmDeducted;
    }

    public void setPaytmDeducted(Double paytmDeducted) {
        this.paytmDeducted = paytmDeducted;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public Integer getPendingFeedback() {
        return pendingFeedback;
    }

    public void setPendingFeedback(Integer pendingFeedback) {
        this.pendingFeedback = pendingFeedback;
    }



    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }


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
	 * The orderAmount
	 */
	public Double getOrderAmount() {
		return orderAmount;
	}

	/**
	 *
	 * @param orderAmount
	 * The order_amount
	 */
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	/**
	 *
	 * @return
	 * The deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 *
	 * @param deliveryAddress
	 * The delivery_address
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Integer getCancellable() {
		return cancellable;
	}

	public void setCancellable(Integer cancellable) {
		this.cancellable = cancellable;
	}

	/**
	 *
	 * @return
	 * The orderRefundAmount
	 */
	public Double getOrderRefundAmount() {
		return orderRefundAmount;
	}

	/**
	 *
	 * @param orderRefundAmount
	 * The order_refund_amount
	 */
	public void setOrderRefundAmount(Double orderRefundAmount) {
		this.orderRefundAmount = orderRefundAmount;
	}


	/**
	 *
	 * @return
	 * The paymentMode
	 */
	public Integer getPaymentMode() {
		return paymentMode;
	}

	/**
	 *
	 * @param paymentMode
	 * The payment_mode
	 */
	public void setPaymentMode(Integer paymentMode) {
		this.paymentMode = paymentMode;
	}

	/**
	 *
	 * @return
	 * The orderStatus
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 *
	 * @param orderStatus
	 * The order_status
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 *
	 * @return
	 * The orderStatusColor
	 */
	public String getOrderStatusColor() {
		return orderStatusColor;
	}

	/**
	 *
	 * @param orderStatusColor
	 * The order_status_color
	 */
	public void setOrderStatusColor(String orderStatusColor) {
		this.orderStatusColor = orderStatusColor;
	}

	/**
	 *
	 * @return
	 * The orderTime
	 */
	public String getOrderTime() {
		return orderTime;
	}

	/**
	 *
	 * @param orderTime
	 * The order_time
	 */
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	/**
	 *
	 * @return
	 * The orderItems
	 */
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 *
	 * @param orderItems
	 * The order_items
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public Double getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(Double deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(String expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public Integer getCanReorder() {
		return canReorder;
	}

	public void setCanReorder(Integer canReorder) {
		this.canReorder = canReorder;
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
}