package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;


public class NegativeWalletBalanceResponse extends FeedCommonResponse{


	@SerializedName("payment_data")
	@Expose
	private PaymentData paymentData;

	public PaymentData getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(PaymentData data) {
		this.paymentData = data;
	}


	public class PaymentData {
		@SerializedName("html_form")
		@Expose
		private String link;
		@SerializedName("checkout_form")
		@Expose
		private String checkoutForm;
		@SerializedName("reference_id")
		@Expose
		private Integer referenceId;

		public Integer getReferenceId() {
			return referenceId;
		}

		public void setReferenceId(Integer referenceId) {
			this.referenceId = referenceId;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getCheckoutForm() {
			return checkoutForm;
		}

		public void setCheckoutForm(String checkoutForm) {
			this.checkoutForm = checkoutForm;
		}
	}
}
