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
	@SerializedName("delivery_charges_after_threshold")
	@Expose
	private Double deliveryChargesAfterThreshold;
	@SerializedName("minimum_order_amount")
	@Expose
	private Double minimumOrderAmount;

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

	public Double getDeliveryChargesAfterThreshold() {
		return deliveryChargesAfterThreshold;
	}

	public void setDeliveryChargesAfterThreshold(Double deliveryChargesAfterThreshold) {
		this.deliveryChargesAfterThreshold = deliveryChargesAfterThreshold;
	}

	public Double getMinimumOrderAmount() {
		return minimumOrderAmount;
	}

	public void setMinimumOrderAmount(Double minimumOrderAmount) {
		this.minimumOrderAmount = minimumOrderAmount;
	}


	public Double getApplicableDeliveryCharges(int type, double subTotalAmount){
		double charges = 0;
		if(type == AppConstant.ApplicationType.MENUS){
			if(subTotalAmount < getMinimumOrderAmount()){
				charges = getDeliveryChargesBeforeThreshold();
			} else {
				charges = getDeliveryChargesAfterThreshold();
			}
		} else {
			if(subTotalAmount < getMinAmount()) {
				charges = getDeliveryCharges();
			}
		}
		return charges;
	}
}