package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class CustomizeItem {

	@SerializedName("customize_id")
	@Expose
	private Integer customizeId;
	@SerializedName("customize_item_name")
	@Expose
	private String customizeItemName;
	@SerializedName("customize_options")
	@Expose
	private List<CustomizeOption> customizeOptions = null;
	@SerializedName("is_check_box")
	@Expose
	private Integer isCheckBox;
	@SerializedName("customize_item_limit")
	@Expose
	private Integer customizeItemLimit;
	@SerializedName("customize_item_lower_limit")
	@Expose
	private Integer customizeItemLowerLimit;


	public Integer getCustomizeId() {
		return customizeId;
	}

	public void setCustomizeId(Integer customizeId) {
		this.customizeId = customizeId;
	}

	public String getCustomizeItemName() {
		return customizeItemName;
	}

	public Integer getCustomizeItemUpperLimit() {
		return customizeItemLimit == null || customizeItemLimit == 0 ? Integer.MAX_VALUE : customizeItemLimit;
	}

	public Integer getCustomizeItemLowerLimit() {
		return customizeItemLowerLimit == null ? 0 : customizeItemLowerLimit;
	}

	public void setCustomizeItemName(String customizeItemName) {
		this.customizeItemName = customizeItemName;
	}

	public List<CustomizeOption> getCustomizeOptions() {
		if(customizeOptions == null){
			customizeOptions = new ArrayList<>();
		}
		return customizeOptions;
	}

	public void setCustomizeOptions(List<CustomizeOption> customizeOptions) {
		this.customizeOptions = customizeOptions;
	}

	public Integer getIsCheckBox() {
		if(isCheckBox == null) {
			return 0;
		}
		return isCheckBox;
	}

	public void setIsCheckBox(Integer isCheckBox) {
		this.isCheckBox = isCheckBox;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CustomizeItem){
			return ((CustomizeItem)o).customizeId.equals(customizeId);
		} else {
			return false;
		}
	}

}