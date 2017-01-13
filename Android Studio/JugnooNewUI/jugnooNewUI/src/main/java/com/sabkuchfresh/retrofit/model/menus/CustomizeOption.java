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
	private Integer customizePrice;
	@SerializedName("additional_cost")
	@Expose
	private Integer additionalCost;

	private Integer isCustomizeItem;
	private Integer customizeItemPos;
	private Integer isItem;
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

	public Integer getCustomizePrice() {
		return customizePrice;
	}

	public void setCustomizePrice(Integer customizePrice) {
		this.customizePrice = customizePrice;
	}

	public Integer getAdditionalCost() {
		return additionalCost;
	}

	public void setAdditionalCost(Integer additionalCost) {
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