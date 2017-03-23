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
	@SerializedName("cityId")
	@Expose
	private Integer cityId;
	@SerializedName("vendor_id")
	@Expose
	private Integer vendorId;
	@SerializedName("vendor_name")
	@Expose
	private String vendorName;
	@SerializedName("vendor_address")
	@Expose
	private String vendorAddress;
	@SerializedName("vendor_phone")
	@Expose
	private String vendorPhone;
	@SerializedName("store_id")
	@Expose
	private Integer storeId;

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

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
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

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorAddress() {
		return vendorAddress;
	}

	public void setVendorAddress(String vendorAddress) {
		this.vendorAddress = vendorAddress;
	}

	public String getVendorPhone() {
		return vendorPhone;
	}

	public void setVendorPhone(String vendorPhone) {
		this.vendorPhone = vendorPhone;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
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