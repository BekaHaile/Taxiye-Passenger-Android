package com.sabkuchfresh.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 3/27/18.
 */
public class VendorDirectSearch {

    @SerializedName("line_1")
    private String line1;

    @SerializedName("line_1_color")
    private String line1Color;

    @SerializedName("line_2_start")
    private String line2Start;

    @SerializedName("line_2_start_color")
    private String line2StartColor;

    @SerializedName("line_2_end")
    private String line2End;

    @SerializedName("line_2_end_color")
    private String line2EndColor;

    @SerializedName("restaurant_id")
    private int vendorId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("sub_category_id")
    private int subcategoryId;

    @SerializedName("restaurant_item_id")
    private int itemId;

    public String getLine1() {
        return line1;
    }

    public String getLine1Color() {
        return line1Color;
    }

    public String getLine2Start() {
        return line2Start;
    }

    public String getLine2StartColor() {
        return line2StartColor;
    }

    public String getLine2End() {
        return line2End;
    }

    public String getLine2EndColor() {
        return line2EndColor;
    }

    public void setLine1(final String line1) {
        this.line1 = line1;
    }

    public void setLine1Color(final String line1Color) {
        this.line1Color = line1Color;
    }

    public void setLine2Start(final String line2Start) {
        this.line2Start = line2Start;
    }

    public void setLine2StartColor(final String line2StartColor) {
        this.line2StartColor = line2StartColor;
    }

    public void setLine2End(final String line2End) {
        this.line2End = line2End;
    }

    public void setLine2EndColor(final String line2EndColor) {
        this.line2EndColor = line2EndColor;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

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
}
