package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.utils.AppConstant;

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

	@SerializedName("delivery_charges_before_threshold")
	@Expose
	private Double deliveryChargesBeforeThreshold;
	@SerializedName("minimum_order_amount")
	@Expose
	private Double minimumOrderAmount;
	@SerializedName("dynamic_delivery_charges")
	@Expose
	private Integer dynamicDeliveryCharges;

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

	public Double getDeliveryChargesBeforeThreshold() {
		return deliveryChargesBeforeThreshold;
	}

	public void setDeliveryChargesBeforeThreshold(Double deliveryChargesBeforeThreshold) {
		this.deliveryChargesBeforeThreshold = deliveryChargesBeforeThreshold;
	}

	public Double getMinimumOrderAmount() {
		return minimumOrderAmount;
	}

	public void setMinimumOrderAmount(Double minimumOrderAmount) {
		this.minimumOrderAmount = minimumOrderAmount;
	}

	public Integer getDynamicDeliveryCharges() {
		if(dynamicDeliveryCharges == null){
			dynamicDeliveryCharges = 0;
		}
		return dynamicDeliveryCharges;
	}

	public void setDynamicDeliveryCharges(Integer dynamicDeliveryCharges) {
		this.dynamicDeliveryCharges = dynamicDeliveryCharges;
	}

	public Double getApplicableDeliveryCharges(int type, double subTotalAmount){
		double charges = 0;
		if(type == AppConstant.ApplicationType.MENUS){
			charges = getDeliveryChargesBeforeThreshold();
		} else {
			if(subTotalAmount < getMinAmount()) {
				charges = getDeliveryCharges();
			}
		}
		return charges;
	}
}