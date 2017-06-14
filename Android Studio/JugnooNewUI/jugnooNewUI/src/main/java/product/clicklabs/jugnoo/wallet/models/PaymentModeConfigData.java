package product.clicklabs.jugnoo.wallet.models;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.PaymentOption;

/**
 * Created by shankar on 7/8/16.
 */
public class PaymentModeConfigData {

	private String name;
	private int enabled;
	private int paymentOption;
	private int priority;
	private String offerText, displayName, upiHandle;

	public PaymentModeConfigData(String name, int enabled, String offerText, String displayName, String upiHandle){
		this.name = name;
		this.enabled = enabled;
		this.offerText = offerText;
		this.displayName = displayName;
		this.upiHandle = upiHandle;
		if(Constants.KEY_PAYTM.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.PAYTM.getOrdinal();
		}
		else if(Constants.KEY_MOBIKWIK.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.MOBIKWIK.getOrdinal();
		}
		else if(Constants.KEY_FREECHARGE.equalsIgnoreCase(name)) {
			paymentOption = PaymentOption.FREECHARGE.getOrdinal();
		}
		else if(Constants.KEY_JUGNOO_CASH.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.CASH.getOrdinal();
		}
		else if(Constants.KEY_JUGNOO_PAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.JUGNOO_PAY.getOrdinal();
		}
		else if(Constants.KEY_RAZORPAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.RAZOR_PAY.getOrdinal();
		}
		else if(Constants.KEY_UPI_RAZORPAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.UPI_RAZOR_PAY.getOrdinal();
		}else if(Constants.KEY_ICICI_UPI.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.ICICI_UPI.getOrdinal();
		}
		this.priority = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(int paymentOption) {
		this.paymentOption = paymentOption;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void incrementPriority(){
		this.priority = this.priority + 1;
	}

	public String getOfferText() {
		return offerText;
	}

	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUpiHandle() {
		return upiHandle;
	}

	public void setUpiHandle(String upiHandle) {
		this.upiHandle = upiHandle;
	}
}
