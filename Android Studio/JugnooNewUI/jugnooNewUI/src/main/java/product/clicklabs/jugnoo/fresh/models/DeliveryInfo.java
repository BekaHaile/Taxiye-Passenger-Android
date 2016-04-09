package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/9/16.
 */
public class DeliveryInfo {

	@SerializedName("delivery_charges")
	@Expose
	private Double deliveryCharges;
	@SerializedName("min_amount")
	@Expose
	private Double minAmount;

	/**
	 *
	 * @return
	 * The deliveryCharges
	 */
	public Double getDeliveryCharges() {
		return deliveryCharges;
	}

	/**
	 *
	 * @param deliveryCharges
	 * The delivery_charges
	 */
	public void setDeliveryCharges(Double deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	/**
	 *
	 * @return
	 * The minAmount
	 */
	public Double getMinAmount() {
		return minAmount;
	}

	/**
	 *
	 * @param minAmount
	 * The min_amount
	 */
	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}

}