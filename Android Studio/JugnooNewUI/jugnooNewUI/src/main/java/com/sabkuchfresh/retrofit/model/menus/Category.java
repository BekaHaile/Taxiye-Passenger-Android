package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 1/12/17.
 */

public class Category {

	@SerializedName("category_id")
	@Expose
	private Integer categoryId;
	@SerializedName("category_name")
	@Expose
	private String categoryName;
	@SerializedName("category_image")
	@Expose
	private String categoryImage;
	@SerializedName("subcategories")
	@Expose
	private List<Subcategory> subcategories = null;
	@SerializedName("vat_percent")
	@Expose
	private Double vatPercent;
	@SerializedName("items")
	@Expose
	private List<Item> items = null;
	@SerializedName("categoryPos")
	private int categoryPos;
	@SerializedName("vegItemsCount")
	private int vegItemsCount;

	/**
	 * constructor for category search
	 * @param categoryId
	 * @param categoryName
	 * @param categoryPos
	 */
	public Category(Integer categoryId, String categoryName, int categoryPos){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryPos = categoryPos;
	}


	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryImage() {
		return categoryImage;
	}

	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	public List<Subcategory> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<Subcategory> subcategories) {
		this.subcategories = subcategories;
	}

	public Double getVatPercent() {
		return vatPercent;
	}

	public void setVatPercent(Double vatPercent) {
		this.vatPercent = vatPercent;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Category){
			return ((Category)o).categoryId.equals(categoryId);
		} else {
			return false;
		}
	}

	public int getCategoryPos() {
		return categoryPos;
	}

	public void setCategoryPos(int categoryPos) {
		this.categoryPos = categoryPos;
	}

	public int getVegItemsCount() {
		return vegItemsCount;
	}

	public void setVegItemsCount(int vegItemsCount) {
		this.vegItemsCount = vegItemsCount;
	}
}