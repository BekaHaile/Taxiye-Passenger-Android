package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class CustomizeItemSelected{
	@SerializedName("customize_id")
	@Expose
	private Integer customizeId;
	@SerializedName("customize_options")
	@Expose
	private List<Integer> customizeOptions;

	public CustomizeItemSelected(Integer customizeId){
		this.customizeId = customizeId;
	}

	public Integer getCustomizeId() {
		return customizeId;
	}

	public void setCustomizeId(Integer customizeId) {
		this.customizeId = customizeId;
	}

	public List<Integer> getCustomizeOptions() {
		if(customizeOptions == null){
			customizeOptions = new ArrayList<>();
		}
		return customizeOptions;
	}

	public void setCustomizeOptions(List<Integer> customizeOptions) {
		this.customizeOptions = customizeOptions;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CustomizeItemSelected){
			CustomizeItemSelected cso = (CustomizeItemSelected)o;
			if(cso.customizeId.equals(customizeId)){
				for(Integer option : cso.getCustomizeOptions()){
					if(!getCustomizeOptions().contains(option)){
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
