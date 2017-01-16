package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class Item {

	@SerializedName("price")
	@Expose
	private Double price;
	@SerializedName("taxes")
	@Expose
	private List<Taxes> taxes = null;
	@SerializedName("is_active")
	@Expose
	private Integer isActive;
	@SerializedName("is_veg")
	@Expose
	private Integer isVeg;
	@SerializedName("restaurant_item_id")
	@Expose
	private Integer restaurantItemId;
	@SerializedName("item_name")
	@Expose
	private String itemName;
	@SerializedName("customize_id_arr")
	@Expose
	private List<Integer> customizeIdArr = null;
	@SerializedName("customize_item")
	@Expose
	private List<CustomizeItem> customizeItem = null;
	@SerializedName("item_details")
	@Expose
	private String itemDetails;
	@SerializedName("isSubCategory")
	@Expose
	private Integer isSubCategory;
	@SerializedName("subCategoryPos")
	@Expose
	private Integer subCategoryPos;
	@SerializedName("itemPos")
	@Expose
	private Integer itemPos;
	@SerializedName("itemSelectedList")
	@Expose
	private List<ItemSelected> itemSelectedList;

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getIsVeg() {
		return isVeg;
	}

	public void setIsVeg(Integer isVeg) {
		this.isVeg = isVeg;
	}

	public Integer getRestaurantItemId() {
		return restaurantItemId;
	}

	public void setRestaurantItemId(Integer restaurantItemId) {
		this.restaurantItemId = restaurantItemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public List<Integer> getCustomizeIdArr() {
		return customizeIdArr;
	}

	public void setCustomizeIdArr(List<Integer> customizeIdArr) {
		this.customizeIdArr = customizeIdArr;
	}

	public List<CustomizeItem> getCustomizeItem() {
		if(customizeItem == null){
			customizeItem = new ArrayList<>();
		}
		return customizeItem;
	}

	public void setCustomizeItem(List<CustomizeItem> customizeItem) {
		this.customizeItem = customizeItem;
	}

	public Integer getIsSubCategory() {
		if(isSubCategory == null){
			return 0;
		}
		return isSubCategory;
	}

	public void setIsSubCategory(Integer isSubCategory) {
		this.isSubCategory = isSubCategory;
	}

	public List<ItemSelected> getItemSelectedList() {
		if(itemSelectedList == null){
			itemSelectedList = new ArrayList<>();
		}
		return itemSelectedList;
	}

	public void setItemSelectedList(List<ItemSelected> itemSelectedList) {
		this.itemSelectedList = itemSelectedList;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Taxes> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Taxes> taxes) {
		this.taxes = taxes;
	}

	public String getItemDetails() {
		return itemDetails;
	}

	public void setItemDetails(String itemDetails) {
		this.itemDetails = itemDetails;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Item){
			return ((Item)o).restaurantItemId.equals(restaurantItemId);
		} else {
			return false;
		}
	}

	public Integer getSubCategoryPos() {
		if(subCategoryPos == null){
			subCategoryPos = -1;
		}
		return subCategoryPos;
	}

	public void setSubCategoryPos(Integer subCategoryPos) {
		this.subCategoryPos = subCategoryPos;
	}

	public Integer getItemPos() {
		return itemPos;
	}

	public void setItemPos(Integer itemPos) {
		this.itemPos = itemPos;
	}

	public Integer getTotalQuantity(){
		int total = 0;
		for (ItemSelected itemSelected : getItemSelectedList()) {
			total = total + itemSelected.getQuantity();
		}
		return total;
	}
}