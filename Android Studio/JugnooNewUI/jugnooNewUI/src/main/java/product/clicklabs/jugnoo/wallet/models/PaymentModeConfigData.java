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

	public PaymentModeConfigData(String name, int enabled){
		this.name = name;
		this.enabled = enabled;
		if(Constants.KEY_PAYTM.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.PAYTM.getOrdinal();
		}
		else if(Constants.KEY_MOBIKWIK.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.MOBIKWIK.getOrdinal();
		}
		else if(Constants.KEY_JUGNOO_CASH.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.CASH.getOrdinal();
		}
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
}
