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


	@SerializedName("options")
	private List<CustomiseOptionsId> options;

	public CustomizeItemSelected(){
		Log.i("TAG", "CustomizeItemSelected: "+getCustomizeId());
	}

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
