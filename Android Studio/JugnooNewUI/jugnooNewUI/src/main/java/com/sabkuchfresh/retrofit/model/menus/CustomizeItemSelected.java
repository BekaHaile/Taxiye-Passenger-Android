package com.sabkuchfresh.retrofit.model.menus;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class CustomizeItemSelected{
	@SerializedName(value = "customize_id",alternate = "id")
	@Expose
	private Integer customizeId;
	@SerializedName("customize_options")
	@Expose
	private List<Integer> customizeOptions;


	private int lowerLimit = 0;
	private int upperLimit = Integer.MAX_VALUE;
	private String name = "";


	@SerializedName("options")
	private List<CustomiseOptionsId> options;

	public CustomizeItemSelected(){
		Log.i("TAG", "CustomizeItemSelected: "+getCustomizeId());
	}

	public CustomizeItemSelected(Integer customizeId, String name, int lowerLimit, int upperLimit){
		this.customizeId = customizeId;
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.name = name;
	}

	public Integer getCustomizeId() {
		return customizeId;
	}

	public void setCustomizeId(Integer customizeId) {
		this.customizeId = customizeId;
	}

	public List<Integer> getCustomizeOptions() {
		return getCustomizeOptions(true);
	}

	public List<CustomiseOptionsId> getOptions() {
		return options;
	}

	public List<Integer> getCustomizeOptions(boolean createNew) {
		if(createNew && customizeOptions == null){
			customizeOptions = new ArrayList<>();
		}
		return customizeOptions;
	}

	public void setCustomizeOptions(List<Integer> customizeOptions) {
		this.customizeOptions = customizeOptions;
	}

	public int getLowerLimit() {
		return lowerLimit;
	}

	public int getUpperLimit() {
		return upperLimit;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CustomizeItemSelected){
			CustomizeItemSelected cso = (CustomizeItemSelected)o;
			if(customizeOptions == null || cso.customizeOptions == null){
				return cso.customizeId.equals(customizeId);
			} else {
				if (cso.customizeId.equals(customizeId)) {
					if(cso.getCustomizeOptions().size() != getCustomizeOptions().size()){
						return false;
					}
					for (Integer option : cso.getCustomizeOptions()) {
						if (!getCustomizeOptions().contains(option)) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}
