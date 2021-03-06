package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 1/12/17.
 */

public class CustomizeOption {

	@SerializedName("customize_option_id")
	@Expose
	private Integer customizeOptionId;
	@SerializedName("customize_option_name")
	@Expose
	private String customizeOptionName;
	@SerializedName("is_default")
	@Expose
	private Integer isDefault;
	@SerializedName("customize_price")
	@Expose
	private Double customizePrice;
	@SerializedName("additional_cost")
	@Expose
	private Double additionalCost;
	@SerializedName("isCustomizeItem")
	@Expose
	private Integer isCustomizeItem;
	@SerializedName("customizeItemPos")
	@Expose
	private Integer customizeItemPos;
	@SerializedName("isItem")
	@Expose
	private Integer isItem;
	@SerializedName("isMultiSelect")
	@Expose
	private Integer isMultiSelect;

	public String getCustomizeOptionName() {
		return customizeOptionName;
	}

	public void setCustomizeOptionName(String customizeOptionName) {
		this.customizeOptionName = customizeOptionName;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public Double getCustomizePrice() {
		return customizePrice;
	}

	public void setCustomizePrice(Double customizePrice) {
		this.customizePrice = customizePrice;
	}

	public Double getAdditionalCost() {
		return additionalCost;
	}

	public void setAdditionalCost(Double additionalCost) {
		this.additionalCost = additionalCost;
	}

	public Integer getCustomizeOptionId() {
		return customizeOptionId;
	}

	public void setCustomizeOptionId(Integer customizeOptionId) {
		this.customizeOptionId = customizeOptionId;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CustomizeOption){
			return ((CustomizeOption)o).customizeOptionId.equals(customizeOptionId);
		} else {
			return false;
		}
	}

	public Integer getIsCustomizeItem() {
		if(isCustomizeItem == null){
			isCustomizeItem = 0;
		}
		return isCustomizeItem;
	}

	public void setIsCustomizeItem(Integer isCustomizeItem) {
		this.isCustomizeItem = isCustomizeItem;
	}

	public Integer getIsItem() {
		if(isItem == null){
			isItem = 0;
		}
		return isItem;
	}

	public void setIsItem(Integer isItem) {
		this.isItem = isItem;
	}

	public Integer getIsMultiSelect() {
		if(isMultiSelect == null){
			isMultiSelect = 0;
		}
		return isMultiSelect;
	}

	public void setIsMultiSelect(Integer isMultiSelect) {
		this.isMultiSelect = isMultiSelect;
	}

	public Integer getCustomizeItemPos() {
		if(customizeItemPos == null){
			customizeItemPos = 0;
		}
		return customizeItemPos;
	}

	public void setCustomizeItemPos(Integer customizeItemPos) {
		this.customizeItemPos = customizeItemPos;
	}
}