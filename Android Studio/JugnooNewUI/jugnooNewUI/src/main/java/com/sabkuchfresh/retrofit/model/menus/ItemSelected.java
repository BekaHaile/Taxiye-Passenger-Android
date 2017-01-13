package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class ItemSelected {
	@SerializedName("restaurant_item_id")
	@Expose
	private Integer restaurantItemId;
	@SerializedName("customize_items")
	@Expose
	private List<CustomizeItemSelected> customizeItemSelectedList;

	private Integer quantity;
	private Double totalPrice;

	public Integer getRestaurantItemId() {
		return restaurantItemId;
	}

	public void setRestaurantItemId(Integer restaurantItemId) {
		this.restaurantItemId = restaurantItemId;
	}

	public List<CustomizeItemSelected> getCustomizeItemSelectedList() {
		if(customizeItemSelectedList == null){
			customizeItemSelectedList = new ArrayList<>();
		}
		return customizeItemSelectedList;
	}

	public void setCustomizeItemSelectedList(List<CustomizeItemSelected> customizeItemSelectedList) {
		this.customizeItemSelectedList = customizeItemSelectedList;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ItemSelected){
			return ((ItemSelected)o).restaurantItemId.equals(restaurantItemId);
		} else {
			return false;
		}
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getTotalPriceWithQuantity() {
		return totalPrice * ((double) quantity);
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
}