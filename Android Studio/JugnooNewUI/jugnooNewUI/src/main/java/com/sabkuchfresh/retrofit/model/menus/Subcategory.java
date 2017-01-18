package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class Subcategory {

	@SerializedName("subcategory_id")
	@Expose
	private Integer subcategoryId;
	@SerializedName("subcategory_name")
	@Expose
	private String subcategoryName;
	@SerializedName("subcategory_items")
	@Expose
	private String subcategoryItems;
	@SerializedName("items")
	@Expose
	private List<Item> items = null;

	public Integer getSubcategoryId() {
		return subcategoryId;
	}

	public void setSubcategoryId(Integer subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	public String getSubcategoryName() {
		return subcategoryName;
	}

	public void setSubcategoryName(String subcategoryName) {
		this.subcategoryName = subcategoryName;
	}

	public String getSubcategoryItems() {
		return subcategoryItems;
	}

	public void setSubcategoryItems(String subcategoryItems) {
		this.subcategoryItems = subcategoryItems;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Subcategory){
			return ((Subcategory)o).subcategoryId.equals(subcategoryId);
		} else {
			return false;
		}
	}
}