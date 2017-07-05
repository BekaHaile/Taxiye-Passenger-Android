package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 1/12/17.
 */

public class Tax {
	public Tax(){}
	public Tax(String key, Double value) {
		this.key = key;
		this.value = value;
	}

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("value")
	@Expose
	private Double value;
	@SerializedName("percent_value")
	@Expose
	private Double percentValue;
	@SerializedName("is_percent")
	@Expose
	private int isPercent;
	@SerializedName("subtract_discount")
	@Expose
	private int subtractDiscount;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}


	public double getCalculatedValue(double subTotal, double discount) {
		if (percentValue != null) {
			if (isPercent == 1) {
				return (subTotal - (subtractDiscount == 1 ? discount : 0)) * (percentValue-1);
			} else {
				return percentValue;
			}
		} else {
			return value;
		}
	}
}