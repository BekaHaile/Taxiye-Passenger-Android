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
	@SerializedName("quantity")
	@Expose
	private Integer quantity;
	@SerializedName("totalPrice")
	@Expose
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
			ItemSelected io = (ItemSelected)o;
			if(io.restaurantItemId.equals(restaurantItemId)){
				for(CustomizeItemSelected customizeItemSelected : io.getCustomizeItemSelectedList()){
					if(!getCustomizeItemSelectedList().contains(customizeItemSelected)){
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

	public Integer getQuantity() {
		if(quantity == null){
			quantity = 0;
		}
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

	public Double getTotalPrice() {
		return totalPrice;
	}
}