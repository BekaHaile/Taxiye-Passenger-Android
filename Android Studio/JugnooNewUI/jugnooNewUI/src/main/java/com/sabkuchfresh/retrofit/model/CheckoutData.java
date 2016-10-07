package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/9/16.
 */
public class CheckoutData {

    @SerializedName("delivery_addresses")
    @Expose
    private List<DeliveryAddress> deliveryAddresses = new ArrayList<DeliveryAddress>();

	@SerializedName("last_address")
	@Expose
	private String lastAddress;

	@SerializedName("last_address_id")
	@Expose
	private Integer lastAddressId = 0;
	@SerializedName("last_address_type")
	@Expose
	private String lastAddressType;

    public String getLastAddressLatitude() {
        return lastAddressLatitude;
    }

    public void setLastAddressLatitude(String lastAddressLatitude) {
        this.lastAddressLatitude = lastAddressLatitude;
    }

    @SerializedName("delivery_latitude")
    @Expose
    private String lastAddressLatitude;

    public String getLastAddressLongitude() {
        return lastAddressLongitude;
    }

    public void setLastAddressLongitude(String lastAddressLongitude) {
        this.lastAddressLongitude = lastAddressLongitude;
    }

    @SerializedName("delivery_longitude")
    @Expose
    private String lastAddressLongitude;

	@SerializedName("delivery_slots")
	@Expose
	private List<DeliverySlot> deliverySlots = new ArrayList<DeliverySlot>();

	/**
	 *
	 * @return
	 * The lastAddress
	 */
	public String getLastAddress() {
		return lastAddress;
	}

	/**
	 *
	 * @param lastAddress
	 * The last_address
	 */
	public void setLastAddress(String lastAddress) {
		this.lastAddress = lastAddress;
	}

	/**
	 *
	 * @return
	 * The deliverySlots
	 */
	public List<DeliverySlot> getDeliverySlots() {
		return deliverySlots;
	}

	/**
	 *
	 * @param deliverySlots
	 * The delivery_slots
	 */
	public void setDeliverySlots(List<DeliverySlot> deliverySlots) {
		this.deliverySlots = deliverySlots;
	}

    /**
     *
     * @return
     * The deliveryAddresses
     */
    public List<DeliveryAddress> getDeliveryAddresses() {
        return deliveryAddresses;
    }

    /**
     *
     * @param deliveryAddresses
     * The delivery_addresses
     */
    public void setDeliveryAddresses(List<DeliveryAddress> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

	public Integer getLastAddressId() {
		return lastAddressId;
	}

	public void setLastAddressId(Integer lastAddressId) {
		this.lastAddressId = lastAddressId;
	}

	public String getLastAddressType() {
		return lastAddressType;
	}

	public void setLastAddressType(String lastAddressType) {
		this.lastAddressType = lastAddressType;
	}
}
