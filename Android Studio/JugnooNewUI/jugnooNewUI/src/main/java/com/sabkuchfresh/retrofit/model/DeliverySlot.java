package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/9/16.
 */
public class DeliverySlot {

	@SerializedName("day_id")
	@Expose
	private Integer dayId;
	@SerializedName("day_name")
	@Expose
	private String dayName;
	@SerializedName("slots")
	@Expose
	private List<Slot> slots = new ArrayList<Slot>();

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
	 * The dayName
	 */
	public String getDayName() {
		return dayName;
	}

	/**
	 *
	 * @param dayName
	 * The day_name
	 */
	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	/**
	 *
	 * @return
	 * The slots
	 */
	public List<Slot> getSlots() {
		return slots;
	}

	/**
	 *
	 * @param slots
	 * The slots
	 */
	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}

}
