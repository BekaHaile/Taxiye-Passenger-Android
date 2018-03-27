package com.sabkuchfresh.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 3/16/18.
 */

public class SearchSuggestion {

    @SerializedName("line_1")
    private String text;

    @SerializedName("line_1_color")
    private String textColor;

    @SerializedName("search_item_id")
    private int searchItemId;

    @SerializedName("restaurant_id")
    private int vendorId;

    @SerializedName("category_id")
    private int categoryId = -1;

    @SerializedName("sub_category_id")
    private int subcategoryId = -1;

    @SerializedName("restaurant_item_id")
    private int itemId = -1;

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(final int vendorId) {
        this.vendorId = vendorId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(final int subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public int getSearchItemId() {
        return searchItemId;
    }

    public void setSearchItemId(final int searchItemId) {
        this.searchItemId = searchItemId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(final String textColor) {
        this.textColor = textColor;
    }
}
