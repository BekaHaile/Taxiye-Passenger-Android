package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jugnoo.pay.models.SendMoneyResponse;

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

	@SerializedName("payment_object")
	@Expose
	private SendMoneyResponse.TxnDetails paymentObject;

	@SerializedName("subscription_message")
	@Expose
	private SubscriptionMessage subscriptionMessage;
	@SerializedName("referral_popup_content")
	@Expose
	private ReferralPopupContent referralPopupContent;

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

	public SendMoneyResponse.TxnDetails getPaymentObject() {
		return paymentObject;
	}

	public void setPaymentObject(SendMoneyResponse.TxnDetails paymentObject) {
		this.paymentObject = paymentObject;
	}

	public SubscriptionMessage getSubscriptionMessage() {
		return subscriptionMessage;
	}

	public void setSubscriptionMessage(SubscriptionMessage subscriptionMessage) {
		this.subscriptionMessage = subscriptionMessage;
	}

	public ReferralPopupContent getReferralPopupContent() {
		return referralPopupContent;
	}

	public void setReferralPopupContent(ReferralPopupContent referralPopupContent) {
		this.referralPopupContent = referralPopupContent;
	}

	public class SubscriptionMessage {

		@SerializedName("heading")
		@Expose
		private String heading;
		@SerializedName("content")
		@Expose
		private String content;
		@SerializedName("link_text")
		@Expose
		private String linkText;

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getLinkText() {
			return linkText;
		}

		public void setLinkText(String linkText) {
			this.linkText = linkText;
		}

	}

	public class ReferralPopupContent{
		@SerializedName("button_id")
		@Expose
		private Integer buttonId;
		@SerializedName("image_url")
		@Expose
		private String imageUrl;
		@SerializedName("heading")
		@Expose
		private String heading;
		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("button_text")
		@Expose
		private String buttonText;

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getButtonText() {
			return buttonText;
		}

		public void setButtonText(String buttonText) {
			this.buttonText = buttonText;
		}

		public Integer getButtonId() {
			if(buttonId == null){
				buttonId = 0;
			}
			return buttonId;
		}

		public void setButtonId(Integer buttonId) {
			this.buttonId = buttonId;
		}
	}

}
