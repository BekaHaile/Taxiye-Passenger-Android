package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;

/**
 * Created by socomo on 12/23/16.
 */

public class VendorMenuResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("support_contact")
    @Expose
    private String supportContact;
    @SerializedName("delivery_info")
    @Expose
    private DeliveryInfo deliveryInfo;
    @SerializedName("sorted_by")
    @Expose
    private Integer sortedBy;
    @SerializedName("show_message")
    @Expose
    private Integer showMessage;

    @SerializedName("charges")
    @Expose
    private List<Charges> charges = null;

    @SerializedName("promotion_info")
    @Expose
    private MenusPromotionInfo menusPromotionInfo;
    @SerializedName("restaurant_info")
    @Expose
    private MenusResponse.Vendor vendor;
    @SerializedName("out_of_radius")
    @Expose
    private int restuarantOutOfRadius;

    @SerializedName("images_list")
    @Expose
    private ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode = MyApplication.getInstance().getString(R.string.default_currency);
    @SerializedName("currency")
    @Expose
    private String currency = MyApplication.getInstance().getString(R.string.default_currency);

    public void setReviewImages(ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages) {
        this.reviewImages = reviewImages;
    }

    public ArrayList<FetchFeedbackResponse.ReviewImage> getReviewImages() {
        return reviewImages;
    }

    public boolean getRestuarantOutOfRadius() {
        return restuarantOutOfRadius == 1;
    }

    public MenusPromotionInfo getMenusPromotionInfo() {
        return menusPromotionInfo;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getSupportContact() {
        return supportContact;
    }

    public void setSupportContact(String supportContact) {
        this.supportContact = supportContact;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Integer getSortedBy() {
        return sortedBy;
    }

    public void setSortedBy(Integer sortedBy) {
        this.sortedBy = sortedBy;
    }

    public Integer getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(Integer showMessage) {
        this.showMessage = showMessage;
    }

    public List<Charges> getCharges() {
        return charges;
    }

    public void setCharges(List<Charges> charges) {
        this.charges = charges;
    }

    public MenusResponse.Vendor getVendor() {
        return vendor;
    }

    public void setVendor(MenusResponse.Vendor vendor) {
        this.vendor = vendor;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrency() {
        return currency;
    }


    public class DeliveryInfo {

        @SerializedName("delivery_charges_before_threshold")
        @Expose
        private Double deliveryChargesBeforeThreshold;
        @SerializedName("delivery_charges_after_threshold")
        @Expose
        private Double deliveryChargesAfterThreshold;
        @SerializedName("minimum_order_amount")
        @Expose
        private Double minimumOrderAmount;

        public Double getDeliveryChargesBeforeThreshold() {
            return deliveryChargesBeforeThreshold;
        }

        public void setDeliveryChargesBeforeThreshold(Double deliveryChargesBeforeThreshold) {
            this.deliveryChargesBeforeThreshold = deliveryChargesBeforeThreshold;
        }

        public Double getDeliveryChargesAfterThreshold() {
            return deliveryChargesAfterThreshold;
        }

        public void setDeliveryChargesAfterThreshold(Double deliveryChargesAfterThreshold) {
            this.deliveryChargesAfterThreshold = deliveryChargesAfterThreshold;
        }

        public Double getMinimumOrderAmount() {
            return minimumOrderAmount;
        }

        public void setMinimumOrderAmount(Double minimumOrderAmount) {
            this.minimumOrderAmount = minimumOrderAmount;
        }

    }

    public class MenusPromotionInfo{
            @SerializedName("promotion_text")
            private String promoText;
            @SerializedName("promotion_tnc")
            private String promoTC;

        public String getPromoText() {
            return promoText;
        }

        public String getPromoTC() {
            return promoTC;
        }
    }

}