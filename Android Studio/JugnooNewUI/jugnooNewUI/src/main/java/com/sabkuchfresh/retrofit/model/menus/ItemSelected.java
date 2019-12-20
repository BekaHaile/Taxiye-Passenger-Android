package com.sabkuchfresh.retrofit.model.menus;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class ItemSelected {
	@SerializedName(value = "restaurant_item_id",alternate = {"item_id"})
	@Expose
	private Integer restaurantItemId;
	@SerializedName(value = "customize_items",alternate = {"customisations_json"})
	@Expose
	private List<CustomizeItemSelected> customizeItemSelectedList;
	@SerializedName(value = "quantity",alternate = {"item_quantity"})
	@Expose
	private Integer quantity;
	@SerializedName(value = "totalPrice",alternate = {"item_amount"})
	@Expose
	private Double totalPrice;
	@SerializedName("customizeText")
	@Expose
	private String customizeText;

	@SerializedName("item_notes")
	private String itemInstructions = "";

	public String getItemInstructions() {
		return itemInstructions;
	}

	public void setItemInstructions(final String itemInstructions) {
		if (itemInstructions == null) this.itemInstructions = "";
		else this.itemInstructions = itemInstructions;
	}

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
				if(io.getCustomizeItemSelectedList().size() != getCustomizeItemSelectedList().size()){
					return false;
				}
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

	public String getCustomizeText() {
		return customizeText;
	}

	public void setCustomizeText(String customizeText) {
		this.customizeText = customizeText;
	}
}