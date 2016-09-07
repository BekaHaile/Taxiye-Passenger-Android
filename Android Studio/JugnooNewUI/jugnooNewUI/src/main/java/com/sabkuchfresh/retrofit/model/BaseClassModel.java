package com.sabkuchfresh.retrofit.model;

/**
 * Created by gurmail on 14/07/16.
 */
public abstract class BaseClassModel {

//    public Integer subItemId;
//    public String subItemName;
//    public String subItemImage;
//    public Double price;
//    public String baseUnit;
//    public Integer stock;

    public abstract int getSubItemId();

    public abstract void setSubItemId(int subItemId);

    public abstract String getSubItemName();

    public abstract void setSubItemName(String subItemName);

    public abstract String getSubItemImage();

    public abstract void setSubItemImage(String subItemImage);

    public abstract Double getPrice();

    public abstract void setPrice(Double price);

    public abstract String getBaseUnit();

    public abstract void setBaseUnit(String baseUnit);

    public abstract Integer getStock();

    public abstract void setStock(Integer stock);

    public abstract Integer getSubItemQuantitySelected();
}
