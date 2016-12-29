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
}