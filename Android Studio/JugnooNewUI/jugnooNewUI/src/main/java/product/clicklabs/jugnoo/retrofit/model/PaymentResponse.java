package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

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

	}

	public class PaymentData {

		@SerializedName("flag")
		@Expose
		private Integer flag;
		@SerializedName("jugnoo_balance")
		@Expose
		private Integer jugnooBalance;
		@SerializedName("remaining")
		@Expose
		private Integer remaining;
		@SerializedName("jugnoo_deducted")
		@Expose
		private Integer jugnooDeducted;
		@SerializedName("paytm_deducted")
		@Expose
		private Integer paytmDeducted;
		@SerializedName("mobikwik_deducted")
		@Expose
		private Integer mobikwikDeducted;
		@SerializedName("freecharge_deducted")
		@Expose
		private Integer freechargeDeducted;
		@SerializedName("debt_added")
		@Expose
		private Integer debtAdded;
		@SerializedName("real_money_ratio")
		@Expose
		private Integer realMoneyRatio;
		@SerializedName("message")
		@Expose
		private String message;

		public Integer getFlag() {
			return flag;
		}

		public void setFlag(Integer flag) {
			this.flag = flag;
		}

		public Integer getJugnooBalance() {
			return jugnooBalance;
		}

		public void setJugnooBalance(Integer jugnooBalance) {
			this.jugnooBalance = jugnooBalance;
		}

		public Integer getRemaining() {
			return remaining;
		}

		public void setRemaining(Integer remaining) {
			this.remaining = remaining;
		}

		public Integer getJugnooDeducted() {
			return jugnooDeducted;
		}

		public void setJugnooDeducted(Integer jugnooDeducted) {
			this.jugnooDeducted = jugnooDeducted;
		}

		public Integer getPaytmDeducted() {
			return paytmDeducted;
		}

		public void setPaytmDeducted(Integer paytmDeducted) {
			this.paytmDeducted = paytmDeducted;
		}

		public Integer getMobikwikDeducted() {
			return mobikwikDeducted;
		}

		public void setMobikwikDeducted(Integer mobikwikDeducted) {
			this.mobikwikDeducted = mobikwikDeducted;
		}

		public Integer getFreechargeDeducted() {
			return freechargeDeducted;
		}

		public void setFreechargeDeducted(Integer freechargeDeducted) {
			this.freechargeDeducted = freechargeDeducted;
		}

		public Integer getDebtAdded() {
			return debtAdded;
		}

		public void setDebtAdded(Integer debtAdded) {
			this.debtAdded = debtAdded;
		}

		public Integer getRealMoneyRatio() {
			return realMoneyRatio;
		}

		public void setRealMoneyRatio(Integer realMoneyRatio) {
			this.realMoneyRatio = realMoneyRatio;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
}
