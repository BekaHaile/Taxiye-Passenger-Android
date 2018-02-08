package com.sabkuchfresh.retrofit.model.menus;

import android.text.TextUtils;

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
	@SerializedName("display_price")
	@Expose
	private String displayPrice;
	@SerializedName("taxes")
	@Expose
	private List<Tax> taxes = null;
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
	@SerializedName("subCategoryId")
	@Expose
	private int subCategoryId;
	@SerializedName("subCategoryPos")
	@Expose
	private Integer subCategoryPos;
	@SerializedName("categoryPos")
	@Expose
	private Integer categoryPos;
	@SerializedName("itemPos")
	@Expose
	private Integer itemPos;
	@SerializedName("old_price")
	@Expose
	private Double oldPrice;
	@SerializedName("offer_text")
	@Expose
	private String offerText;
	@SerializedName("item_image")
	@Expose
	private String itemImage;
	@SerializedName("item_image_compressed")
	@Expose
	private String itemImageCompressed;
	@SerializedName("itemSelectedList")
	@Expose
	private List<ItemSelected> itemSelectedList;

	private Boolean expanded = false;

	public boolean isActive() {
		return isActive == null || isActive == 1;
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

	public List<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Tax> taxes) {
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
			if(getIsSubCategory() == 1){
				return ((Item)o).subCategoryId == subCategoryId;
			} else {
				return ((Item) o).restaurantItemId.equals(restaurantItemId);
			}
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

	public Double getSuperTotalPrice(){
		double total = 0;
		for (ItemSelected itemSelected : getItemSelectedList()) {
			total = total + itemSelected.getTotalPriceWithQuantity();
		}
		return total;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public Integer getCategoryPos() {
		return categoryPos;
	}

	public void setCategoryPos(Integer categoryPos) {
		this.categoryPos = categoryPos;
	}

	public String getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(String displayPrice) {
		this.displayPrice = displayPrice;
	}


	public double getCustomizeItemsSelectedTotalPriceForItemSelected(ItemSelected itemSelected){
		double totalPrice = getPrice();
		for(CustomizeItem customizeItem : getCustomizeItem()) {
			CustomizeItemSelected customizeItemSelected = getCustomizeItemSelected(customizeItem, false, itemSelected);
			for(CustomizeOption customizeOption : customizeItem.getCustomizeOptions()){
				if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
					totalPrice = totalPrice + customizeOption.getCustomizePrice();
				}
			}
		}
		return totalPrice;
	}

	public CustomizeItemSelected getCustomizeItemSelected(CustomizeItem customizeItem, boolean addSelected, ItemSelected itemSelected){
		CustomizeItemSelected customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId());
		int index = itemSelected.getCustomizeItemSelectedList().indexOf(customizeItemSelected);
		if(index > -1){
			customizeItemSelected = itemSelected.getCustomizeItemSelectedList().get(index);
		} else if(addSelected) {
			itemSelected.getCustomizeItemSelectedList().add(customizeItemSelected);
		}
		return customizeItemSelected;
	}

	public int getSubCategoryId() {
		return subCategoryId;
	}

	public void generateCustomizeText(ItemSelected itemSelected){
		if(TextUtils.isEmpty(itemSelected.getCustomizeText())
				&& itemSelected.getCustomizeItemSelectedList().size() > 0){
			StringBuilder sb = new StringBuilder();
			for(CustomizeItemSelected customizeItemSelected : itemSelected.getCustomizeItemSelectedList()){
				CustomizeItem customizeItem = new CustomizeItem();
				customizeItem.setCustomizeId(customizeItemSelected.getCustomizeId());
				int index = getCustomizeItem().indexOf(customizeItem);
				if(index > -1){
					customizeItem = getCustomizeItem().get(index);
					StringBuilder sbOp = new StringBuilder();
					for(Integer option : customizeItemSelected.getCustomizeOptions()){
						CustomizeOption customizeOption = new CustomizeOption();
						customizeOption.setCustomizeOptionId(option);
						int index1 = customizeItem.getCustomizeOptions().indexOf(customizeOption);
						if(index1 > -1){
							customizeOption = customizeItem.getCustomizeOptions().get(index1);
							if(sbOp.length() > 0){
								sbOp.append(", ");
							}
							sbOp.append(customizeOption.getCustomizeOptionName());
						}
					}
					if(sb.length() > 0){
						sb.append("\n");
					}
					sb.append(customizeItem.getCustomizeItemName()).append(": ").append(sbOp);
				}
			}
			itemSelected.setCustomizeText(sb.toString());
		}
	}

	public Double getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(Double oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getOfferText() {
		return offerText;
	}

	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}

	public String getItemImage() {
		return itemImage;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public String getItemImageCompressed() {
		return itemImageCompressed;
	}
}