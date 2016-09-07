package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/11/16.
 */
public class OrderItem {

	@SerializedName("sub_item_id")
	@Expose
	private Integer subItemId;
	@SerializedName("item_amount")
	@Expose
	private Double itemAmount;
	@SerializedName("item_refund_amount")
	@Expose
	private Double itemRefundAmount;
	@SerializedName("item_status")
	@Expose
	private Integer itemStatus;
	@SerializedName("item_quantity")
	@Expose
	private Integer itemQuantity;
	@SerializedName("item_name")
	@Expose
	private String itemName;
	@SerializedName("unit")
	@Expose
	private String unit;

	/**
	 *
	 * @return
	 * The subItemId
	 */
	public Integer getSubItemId() {
		return subItemId;
	}

	/**
	 *
	 * @param subItemId
	 * The sub_item_id
	 */
	public void setSubItemId(Integer subItemId) {
		this.subItemId = subItemId;
	}

	/**
	 *
	 * @return
	 * The itemAmount
	 */
	public Double getItemAmount() {
		return itemAmount;
	}

	/**
	 *
	 * @param itemAmount
	 * The item_amount
	 */
	public void setItemAmount(Double itemAmount) {
		this.itemAmount = itemAmount;
	}

	/**
	 *
	 * @return
	 * The itemRefundAmount
	 */
	public Double getItemRefundAmount() {
		return itemRefundAmount;
	}

	/**
	 *
	 * @param itemRefundAmount
	 * The item_refund_amount
	 */
	public void setItemRefundAmount(Double itemRefundAmount) {
		this.itemRefundAmount = itemRefundAmount;
	}

	/**
	 *
	 * @return
	 * The itemStatus
	 */
	public Integer getItemStatus() {
		return itemStatus;
	}

	/**
	 *
	 * @param itemStatus
	 * The item_status
	 */
	public void setItemStatus(Integer itemStatus) {
		this.itemStatus = itemStatus;
	}

	/**
	 *
	 * @return
	 * The itemQuantity
	 */
	public Integer getItemQuantity() {
		return itemQuantity;
	}

	/**
	 *
	 * @param itemQuantity
	 * The item_quantity
	 */
	public void setItemQuantity(Integer itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	/**
	 *
	 * @return
	 * The itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 *
	 * @param itemName
	 * The item_name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
