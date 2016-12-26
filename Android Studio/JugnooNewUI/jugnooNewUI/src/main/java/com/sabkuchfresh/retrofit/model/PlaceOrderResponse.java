package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/11/16.
 */
public class PlaceOrderResponse {

	@SerializedName("order_id")
	@Expose
	private Integer orderId;
	@SerializedName("payment_mode")
	@Expose
	private String paymentMode;
	@SerializedName("amount")
	@Expose
	private Double amount;
	@SerializedName("status")
	@Expose
	private Integer status;
	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
//	@SerializedName("payment_object")
//	@Expose
//	private Integer orderId;

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
	 * The paymentMode
	 */
	public String getPaymentMode() {
		return paymentMode;
	}

	/**
	 *
	 * @param paymentMode
	 * The payment_mode
	 */
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	/**
	 *
	 * @return
	 * The amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 *
	 * @param amount
	 * The amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
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
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
