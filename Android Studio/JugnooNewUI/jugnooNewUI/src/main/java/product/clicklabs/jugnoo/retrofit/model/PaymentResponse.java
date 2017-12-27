package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

/**
 * Created by shankar on 14/10/17.
 */

public class PaymentResponse extends FeedCommonResponse{


	@SerializedName("data")
	@Expose
	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Data {

		@SerializedName("engagement_id")
		@Expose
		private Integer engagementId;
		@SerializedName("user_id")
		@Expose
		private Integer userId;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("payment_data")
		@Expose
		private PaymentData paymentData;
		@SerializedName("razorpay_payment_object")
		@Expose
		private RazorpayData razorpayData;
		@SerializedName("icici")
		@Expose
		private PlaceOrderResponse.IciciUpi icici;

		public PlaceOrderResponse.IciciUpi getIcici() {
			return icici;
		}

		public Integer getEngagementId() {
			return engagementId;
		}

		public void setEngagementId(Integer engagementId) {
			this.engagementId = engagementId;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public PaymentData getPaymentData() {
			return paymentData;
		}

		public void setPaymentData(PaymentData paymentData) {
			this.paymentData = paymentData;
		}

		public RazorpayData getRazorpayData() {
			return razorpayData;
		}

		public void setRazorpayData(RazorpayData razorpayData) {
			this.razorpayData = razorpayData;
		}
	}

	public class PaymentData {

		@SerializedName("flag")
		@Expose
		private int flag;
		@SerializedName("jugnoo_balance")
		@Expose
		private double jugnooBalance;
		@SerializedName("amount")
		@Expose
		private double amount;
		@SerializedName("remaining")
		@Expose
		private double remaining;
		@SerializedName("jugnoo_deducted")
		@Expose
		private double jugnooDeducted;
		@SerializedName("paytm_deducted")
		@Expose
		private double paytmDeducted;
		@SerializedName("mobikwik_deducted")
		@Expose
		private double mobikwikDeducted;
		@SerializedName("freecharge_deducted")
		@Expose
		private double freechargeDeducted;
		@SerializedName("debt_added")
		@Expose
		private double debtAdded;
		@SerializedName("real_money_ratio")
		@Expose
		private double realMoneyRatio;
		@SerializedName("message")
		@Expose
		private String message;

		public double getAmount() {
			return amount;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public double getJugnooBalance() {
			return jugnooBalance;
		}

		public void setJugnooBalance(double jugnooBalance) {
			this.jugnooBalance = jugnooBalance;
		}

		public double getRemaining() {
			return remaining;
		}

		public void setRemaining(double remaining) {
			this.remaining = remaining;
		}

		public double getJugnooDeducted() {
			return jugnooDeducted;
		}

		public void setJugnooDeducted(double jugnooDeducted) {
			this.jugnooDeducted = jugnooDeducted;
		}

		public double getPaytmDeducted() {
			return paytmDeducted;
		}

		public void setPaytmDeducted(double paytmDeducted) {
			this.paytmDeducted = paytmDeducted;
		}

		public double getMobikwikDeducted() {
			return mobikwikDeducted;
		}

		public void setMobikwikDeducted(double mobikwikDeducted) {
			this.mobikwikDeducted = mobikwikDeducted;
		}

		public double getFreechargeDeducted() {
			return freechargeDeducted;
		}

		public void setFreechargeDeducted(double freechargeDeducted) {
			this.freechargeDeducted = freechargeDeducted;
		}

		public double getDebtAdded() {
			return debtAdded;
		}

		public void setDebtAdded(double debtAdded) {
			this.debtAdded = debtAdded;
		}

		public double getRealMoneyRatio() {
			return realMoneyRatio;
		}

		public void setRealMoneyRatio(double realMoneyRatio) {
			this.realMoneyRatio = realMoneyRatio;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	public class RazorpayData {

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
		@SerializedName("amount")
		@Expose
		private double amount;
		@SerializedName("currency")
		@Expose
		private String currency;
		@SerializedName("name")
		@Expose
		private String name;

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

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
