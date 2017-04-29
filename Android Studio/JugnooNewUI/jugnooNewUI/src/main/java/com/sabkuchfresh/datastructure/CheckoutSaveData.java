package com.sabkuchfresh.datastructure;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;

/**
 * Created by shankar on 10/27/16.
 */

public class CheckoutSaveData {

	@SerializedName("paymentMode")
	@Expose
	private Integer paymentMode;
	@SerializedName("specialInstructions")
	@Expose
	private String specialInstructions;
	@SerializedName("address")
	@Expose
	private String address;
	@SerializedName("latitude")
	@Expose
	private Double latitude;
	@SerializedName("longitude")
	@Expose
	private Double longitude;
	@SerializedName("addressId")
	@Expose
	private Integer addressId;
	@SerializedName("addressType")
	@Expose
	private String addressType;
	@SerializedName("isDefault")
	@Expose
	private Boolean isDefault;
	@SerializedName("restaurant_id")
	@Expose
	private int restaurantId;

	public CheckoutSaveData(Integer paymentMode, String specialInstructions, String address,
							LatLng latLng, Integer addressId, String addressType) {
		this.paymentMode = paymentMode;
		this.specialInstructions = specialInstructions;
		this.address = address;
		this.latitude = latLng.latitude;
		this.longitude = latLng.longitude;
		this.addressId = addressId;
		this.addressType = addressType;
		isDefault = false;
	}

	public CheckoutSaveData() {
		this.paymentMode = MyApplication.getInstance().getWalletCore().getDefaultPaymentOption().getOrdinal();
		this.specialInstructions = "";
		this.address = "";
		this.latitude = Data.latitude;
		this.longitude = Data.longitude;
		this.addressId = 0;
		this.addressType = "";
		isDefault = true;
	}

	public Integer getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(Integer paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public Boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean aDefault) {
		isDefault = aDefault;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}
}
