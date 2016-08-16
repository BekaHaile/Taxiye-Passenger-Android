package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.adapters.FreshCheckoutAdapter;
import com.sabkuchfresh.utils.DateOperations;

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


    /**
	 * For hearder
	 */

	@SerializedName("ctotal")
	@Expose
	private String ctotal;

	public String getCdelivery() {
		return cdelivery;
	}

	public void setCdelivery(String cdelivery) {
		this.cdelivery = cdelivery;
	}

	@SerializedName("cdelivery")
	@Expose
	private String cdelivery;
	@SerializedName("camount")
	@Expose
	private String camount;

	public String getIsdelivery() {
		return isdelivery;
	}

	public void setIsdelivery(String isdelivery) {
		this.isdelivery = isdelivery;
	}

	@SerializedName("isdelivery")
	@Expose
	private String isdelivery;

	public String getCtotal() {
		return ctotal;
	}

	public void setCtotal(String ctotal) {
		this.ctotal = ctotal;
	}


	public String getCamount() {
		return camount;
	}

	public void setCamount(String camount) {
		this.camount = camount;
	}


	public String getCaddress() {
		return caddress;
	}

	public void setCaddress(String caddress) {
		this.caddress = caddress;
	}

	@SerializedName("caddress")
	@Expose
	private String caddress;




	private FreshCheckoutAdapter.SlotViewType slotViewType;
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

	public FreshCheckoutAdapter.SlotViewType getSlotViewType() {
		return slotViewType;
	}

	public void setSlotViewType(FreshCheckoutAdapter.SlotViewType slotViewType) {
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
