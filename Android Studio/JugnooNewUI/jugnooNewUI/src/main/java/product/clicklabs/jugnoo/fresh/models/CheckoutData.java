package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/9/16.
 */
public class CheckoutData {

	@SerializedName("last_address")
	@Expose
	private String lastAddress;
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

}
