package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.fresh.adapters.FreshDeliverySlotsAdapter;
import product.clicklabs.jugnoo.utils.DateOperations;

/**
 * Created by shankar on 4/9/16.
 */
public class Slot {

	@SerializedName("day_id")
	@Expose
	private Integer dayId;
	@SerializedName("day")
	@Expose
	private String day;
	@SerializedName("delivery_slot_id")
	@Expose
	private Integer deliverySlotId;
	@SerializedName("threshold_time")
	@Expose
	private String thresholdTime;
	@SerializedName("start_time")
	@Expose
	private String startTime;
	@SerializedName("end_time")
	@Expose
	private String endTime;

	private FreshDeliverySlotsAdapter.SlotViewType slotViewType;
	private String dayName;
	private boolean enabled = true;

	/**
	 *
	 * @return
	 * The dayId
	 */
	public Integer getDayId() {
		return dayId;
	}

	/**
	 *
	 * @param dayId
	 * The day_id
	 */
	public void setDayId(Integer dayId) {
		this.dayId = dayId;
	}

	/**
	 *
	 * @return
	 * The day
	 */
	public String getDay() {
		return day;
	}

	/**
	 *
	 * @param day
	 * The day
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 *
	 * @return
	 * The deliverySlotId
	 */
	public Integer getDeliverySlotId() {
		return deliverySlotId;
	}

	/**
	 *
	 * @param deliverySlotId
	 * The delivery_slot_id
	 */
	public void setDeliverySlotId(Integer deliverySlotId) {
		this.deliverySlotId = deliverySlotId;
	}

	/**
	 *
	 * @return
	 * The thresholdTime
	 */
	public String getThresholdTime() {
		return thresholdTime;
	}

	/**
	 *
	 * @param thresholdTime
	 * The threshold_time
	 */
	public void setThresholdTime(String thresholdTime) {
		this.thresholdTime = thresholdTime;
	}

	/**
	 *
	 * @return
	 * The startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	public long getThresholdTimeSeconds() {
		return DateOperations.getDayTimeSeconds(getThresholdTime());
	}

	/**
	 *
	 * @param startTime
	 * The start_time
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 *
	 * @return
	 * The endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 *
	 * @param endTime
	 * The end_time
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public FreshDeliverySlotsAdapter.SlotViewType getSlotViewType() {
		return slotViewType;
	}

	public void setSlotViewType(FreshDeliverySlotsAdapter.SlotViewType slotViewType) {
		this.slotViewType = slotViewType;
	}

	public String getDayName() {
		return dayName;
	}

	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
