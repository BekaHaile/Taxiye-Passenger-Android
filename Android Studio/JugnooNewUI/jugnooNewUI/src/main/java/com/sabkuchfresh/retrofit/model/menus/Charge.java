package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class Charge {

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("type")
	@Expose
	private Integer type;
	@SerializedName("is_percent")
	@Expose
	private Integer isPercent;
	@SerializedName("value")
	@Expose
	private String value;
	@SerializedName("included_values")
	@Expose
	private List<Integer> includedValues = null;
	@SerializedName("force_show")
	@Expose
	private Integer forceShow;
	@SerializedName("default")
	@Expose
	private Integer defaultValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsPercent() {
		return isPercent;
	}

	public void setIsPercent(Integer isPercent) {
		this.isPercent = isPercent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Integer> getIncludedValues() {
		return includedValues;
	}

	public void setIncludedValues(List<Integer> includedValues) {
		this.includedValues = includedValues;
	}

	public Integer getForceShow() {
		return forceShow;
	}

	public void setForceShow(Integer forceShow) {
		this.forceShow = forceShow;
	}

	public Integer getDefault() {
		return defaultValue;
	}

	public void setDefault(Integer defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Charge){
			return ((Charge)o).id.equals(id);
		} else {
			return false;
		}
	}
}