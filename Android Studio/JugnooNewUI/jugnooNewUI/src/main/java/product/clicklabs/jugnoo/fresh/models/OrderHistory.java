package product.clicklabs.jugnoo.fresh.models;

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
}