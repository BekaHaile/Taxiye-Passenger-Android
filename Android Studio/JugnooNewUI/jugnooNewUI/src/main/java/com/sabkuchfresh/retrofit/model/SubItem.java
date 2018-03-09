package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shankar on 4/6/16.
 */
public class SubItem implements Serializable,Cloneable{
    @SerializedName("delivery_time_text")
    @Expose
    private String deliveryTimeText;
    @SerializedName("sub_item_id")
    @Expose
    private Integer subItemId;
    @SerializedName("sub_item_name")
    @Expose
    private String subItemName;
    @SerializedName("sub_item_image")
    @Expose
    private String subItemImage;
    @SerializedName("base_unit")
    @Expose
    private String baseUnit;
    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("actual_price")
    @Expose
    private Double actualPrice;
    @SerializedName("old_price")
    @Expose
    private String oldPrice;
    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("priority_id")
    @Expose
    private Integer priorityId;
    @SerializedName("banner_text")
    @Expose
    private String bannerText;
    @SerializedName("banner_text_color")
    @Expose
    private String bannerTextColor;
    @SerializedName("banner_color")
    @Expose
    private String bannerColor;
    @SerializedName("offer_text")
    @Expose
    private String offerText;
    @SerializedName("item_short_desc")
    @Expose
    private String itemShortDesc;
    @SerializedName("item_large_desc")
    @Expose
    private String itemLargeDesc;
    @SerializedName("is_veg")
    @Expose
    private Integer isVeg;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("order_start")
    @Expose
    private String orderStart;
    @SerializedName("sub_item_desc")
    @Expose
    private String subItemDesc = "";
    @SerializedName("order_end")
    @Expose
    private String orderEnd;
    @SerializedName("vendor_id")
    @Expose
    private Integer vendorId;
    @SerializedName("vendor_name")
    @Expose
    private String vendorName;
    @SerializedName("vendor_phone")
    @Expose
    private String vendorPhone;
    @SerializedName("can_order")
    @Expose
    private Integer canOrder;
    @SerializedName("subItemQuantitySelected")
    @Expose
    private Integer subItemQuantitySelected;
    @SerializedName("earliest_delivery_message")
    @Expose
    private String earliestDeliveryMessage;
    @SerializedName("like_count")
    @Expose
    private double likeCount;
    @SerializedName("is_liked_by_user")
    @Expose
    private int isLiked;

    private boolean isLikeAPIInProgress;

    public void setLikeAPIInProgress(boolean likeAPIInProgress) {
        isLikeAPIInProgress = likeAPIInProgress;
    }

    public boolean isLikeAPIInProgress() {
        return isLikeAPIInProgress;
    }


    public void setIsLiked(boolean isLiked) {

        this.isLiked = isLiked?1:0;
    }

    /**
     *
     * @return
     * The subItemId
     */
    public Integer getcanOrder() {
        return canOrder;
    }

    /**
     *
     * @param canOrder
     * The can_order
     */
    public void setcanOrder(Integer canOrder) {
        this.canOrder = canOrder;
    }

    /**
     *
     * @return
     * The subItemId
     */
    public Integer getSubItemId() {
        return subItemId;
    }

    /**
     *
     * @param subItemId
     * The sub_item_id
     */
    public void setSubItemId(Integer subItemId) {
        this.subItemId = subItemId;
    }

    /**
     *
     * @return
     * The subItemName
     */
    public String getSubItemName() {
        return subItemName;
    }

    /**
     *
     * @param subItemName
     * The sub_item_name
     */
    public void setSubItemName(String subItemName) {
        this.subItemName = subItemName;
    }

    /**
     *
     * @return
     * The subItemImage
     */
    public String getSubItemImage() {
        return subItemImage;
    }

    /**
     *
     * @param subItemImage
     * The sub_item_image
     */
    public void setSubItemImage(String subItemImage) {
        this.subItemImage = subItemImage;
    }

    /**
     *
     * @return
     * The baseUnit
     */
    public String getBaseUnit() {
        return baseUnit;
    }

    /**
     *
     * @param baseUnit
     * The base_unit
     */
    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    /**
     *
     * @return
     * The price
     */
    public Double getPrice() {
        if(price == null){
            price = 0d;
        }
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The oldPrice
     */
    public String getOldPrice() {
        return oldPrice;
    }

    /**
     *
     * @param oldPrice
     * The old_price
     */
    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    /**
     *
     * @return
     * The stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     *
     * @param stock
     * The stock
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     *
     * @return
     * The priorityId
     */
    public Integer getPriorityId() {
        return priorityId;
    }

    /**
     *
     * @param priorityId
     * The priority_id
     */
    public void setPriorityId(Integer priorityId) {
        this.priorityId = priorityId;
    }

    /**
     *
     * @return
     * The bannerText
     */
    public String getBannerText() {
        return bannerText;
    }

    /**
     *
     * @param bannerText
     * The banner_text
     */
    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }

    /**
     *
     * @return
     * The bannerTextColor
     */
    public String getBannerTextColor() {
        return bannerTextColor;
    }

    /**
     *
     * @param bannerTextColor
     * The banner_text_color
     */
    public void setBannerTextColor(String bannerTextColor) {
        this.bannerTextColor = bannerTextColor;
    }

    /**
     *
     * @return
     * The bannerColor
     */
    public String getBannerColor() {
        return bannerColor;
    }

    /**
     *
     * @param bannerColor
     * The banner_color
     */
    public void setBannerColor(String bannerColor) {
        this.bannerColor = bannerColor;
    }

    /**
     *
     * @return
     * The offerText
     */
    public String getOfferText() {
        return offerText;
    }

    /**
     *
     * @param offerText
     * The offer_text
     */
    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    /**
     *
     * @return
     * The itemShortDesc
     */
    public String getItemShortDesc() {
        return itemShortDesc;
    }

    /**
     *
     * @param itemShortDesc
     * The item_short_desc
     */
    public void setItemShortDesc(String itemShortDesc) {
        this.itemShortDesc = itemShortDesc;
    }

    /**
     *
     * @return
     * The itemLargeDesc
     */
    public String getItemLargeDesc() {
        return itemLargeDesc;
    }

    /**
     *
     * @param itemLargeDesc
     * The item_large_desc
     */
    public void setItemLargeDesc(String itemLargeDesc) {
        this.itemLargeDesc = itemLargeDesc;
    }

    /**
     *
     * @return
     * The isVeg
     */
    public Integer getIsVeg() {
        if(isVeg == null){
            return 1;
        } else {
            return isVeg;
        }
    }

    /**
     *
     * @param isVeg
     * The is_veg
     */
    public void setIsVeg(Integer isVeg) {
        this.isVeg = isVeg;
    }

    /**
     *
     * @return
     * The groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     *
     * @param groupId
     * The group_id
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     *
     * @return
     * The orderStart
     */
    public String getOrderStart() {
        return orderStart;
    }

    /**
     *
     * @param orderStart
     * The order_start
     */
    public void setOrderStart(String orderStart) {
        this.orderStart = orderStart;
    }

    /**
     *
     * @return
     * The orderEnd
     */
    public String getOrderEnd() {
        return orderEnd;
    }

    /**
     *
     * @param orderEnd
     * The order_end
     */
    public void setOrderEnd(String orderEnd) {
        this.orderEnd = orderEnd;
    }

    /**
     *
     * @return
     * The vendorId
     */
    public Integer getVendorId() {
        return vendorId;
    }

    /**
     *
     * @param vendorId
     * The vendor_id
     */
    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    /**
     *
     * @return
     * The vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     *
     * @param vendorName
     * The vendor_name
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     *
     * @return
     * The vendorPhone
     */
    public String getVendorPhone() {
        return vendorPhone;
    }

    /**
     *
     * @param vendorPhone
     * The vendor_phone
     */
    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public Integer getSubItemQuantitySelected() {
        if(subItemQuantitySelected == null){
            return 0;
        } else {
            return subItemQuantitySelected;
        }
    }

    public void setSubItemQuantitySelected(Integer subItemQuantitySelected) {
        this.subItemQuantitySelected = subItemQuantitySelected;
    }

    public String getSubItemDesc() {
        return subItemDesc;
    }

    public void setSubItemDesc(String subItemDesc) {
        this.subItemDesc = subItemDesc;
    }

    public String getDeliveryTimeText() {
        return deliveryTimeText;
    }

    public void setDeliveryTimeText(String deliveryTimeText) {
        this.deliveryTimeText = deliveryTimeText;
    }

    public String getEarliestDeliveryMessage() {
        return earliestDeliveryMessage;
    }

    public void setEarliestDeliveryMessage(String earliestDeliveryMessage) {
        this.earliestDeliveryMessage = earliestDeliveryMessage;
    }

    public Double getActualPrice() {
        return actualPrice;
    }

    public int getLikeCount() {
        return (int) likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getIsLiked() {
        return isLiked>0 ;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
