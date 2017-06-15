package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jugnoo.pay.models.SendMoneyResponse;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.SubscriptionData;

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

	@SerializedName("razorpay_payment_object")
	@Expose
	private RazorPaymentObject razorPaymentObject;

	@SerializedName("subscription_message")
	@Expose
	private SubscriptionMessage subscriptionMessage;
	@SerializedName("referral_popup_content")
	@Expose
	private ReferralPopupContent referralPopupContent;

	@SerializedName("subscription_data")
	@Expose
	private SubscriptionDataPlaceOrder subscriptionDataPlaceOrder;
	@SerializedName("order_placed_message")
	@Expose
	private String orderPlacedMessage;
	@SerializedName("icici")
	@Expose
	private IciciUpi icici;

	public IciciUpi getIcici() {
		return icici;
	}

	public class IciciUpi {
		@SerializedName("expiration_time")
		private long expirationTime;


		@SerializedName("polling_time")
		private long pollingTime;

		@SerializedName("reason_list")
		private ArrayList<String> reasonList;

		@SerializedName("jugnoo_vpa")
		private String jugnooVpa;

		public long getExpirationTime() {
			return expirationTime;
		}

		public long getPollingTime() {
			return pollingTime;
		}

		public ArrayList<String> getReasonList() {
			return reasonList;
		}

		public String getJugnooVpa() {
			return jugnooVpa;
		}
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



	public SubscriptionDataPlaceOrder getSubscriptionDataPlaceOrder() {
		return subscriptionDataPlaceOrder;
	}

	public void setSubscriptionDataPlaceOrder(SubscriptionDataPlaceOrder subscriptionDataPlaceOrder) {
		this.subscriptionDataPlaceOrder = subscriptionDataPlaceOrder;
	}

	public RazorPaymentObject getRazorPaymentObject() {
		return razorPaymentObject;
	}

	public void setRazorPaymentObject(RazorPaymentObject razorPaymentObject) {
		this.razorPaymentObject = razorPaymentObject;
	}

	public String getOrderPlacedMessage() {
		return orderPlacedMessage;
	}

	public void setOrderPlacedMessage(String orderPlacedMessage) {
		this.orderPlacedMessage = orderPlacedMessage;
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

		private Integer shown;

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

		public Integer getShown() {
			if(shown == null){
				shown = 0;
			}
			return shown;
		}

		public void setShown(Integer shown) {
			this.shown = shown;
		}
	}

	public class SubscriptionDataPlaceOrder {
		@SerializedName("cancellation_charges_popup_text_line1")
		@Expose
		private String cancellationChargesPopupTextLine1;
		@SerializedName("cancellation_charges_popup_text_line2")
		@Expose
		private String cancellationChargesPopupTextLine2;
		@SerializedName("user_subscriptions")
		@Expose
		private List<SubscriptionData.UserSubscription> userSubscriptions = null;

		public List<SubscriptionData.UserSubscription> getUserSubscriptions() {
			return userSubscriptions;
		}

		public void setUserSubscriptions(List<SubscriptionData.UserSubscription> userSubscriptions) {
			this.userSubscriptions = userSubscriptions;
		}

		public String getCancellationChargesPopupTextLine1() {
			return cancellationChargesPopupTextLine1;
		}

		public void setCancellationChargesPopupTextLine1(String cancellationChargesPopupTextLine1) {
			this.cancellationChargesPopupTextLine1 = cancellationChargesPopupTextLine1;
		}

		public String getCancellationChargesPopupTextLine2() {
			return cancellationChargesPopupTextLine2;
		}

		public void setCancellationChargesPopupTextLine2(String cancellationChargesPopupTextLine2) {
			this.cancellationChargesPopupTextLine2 = cancellationChargesPopupTextLine2;
		}

	}


	public class RazorPaymentObject{
		@SerializedName("order_id")
		@Expose
		private String orderId;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("user_email")
		@Expose
		private String userEmail;
		@SerializedName("description")
		@Expose
		private String description;
		@SerializedName("auth_order_id")
		@Expose
		private Integer authOrderId;

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Integer getAuthOrderId() {
			return authOrderId;
		}

		public void setAuthOrderId(Integer authOrderId) {
			this.authOrderId = authOrderId;
		}
	}

}
