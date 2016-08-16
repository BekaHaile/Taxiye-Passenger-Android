package sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 14/07/16.
 */
public class FatafatCartModel extends BaseClassModel {

    @SerializedName("sub_item_id")
    @Expose
    private Integer subItemId;
    @SerializedName("sub_item_name")
    @Expose
    private String subItemName;
    @SerializedName("sub_item_image")
    @Expose
    private String subItemImage;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("base_unit")
    @Expose
    private String baseUnit;
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
    private String bannertextColor;
    @SerializedName("banner_color")
    @Expose
    private String bannerColor;
    @SerializedName("offer_text")
    @Expose
    private String offerText;
    @SerializedName("old_price")
    @Expose
    private String oldPrice;

    private Integer subItemQuantitySelected;

    @Override
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

    public void setSubItemId(Integer subItemId) {
        this.subItemId = subItemId;
    }

    public Integer getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Integer priorityId) {
        this.priorityId = priorityId;
    }

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }

    public String getBannertextColor() {
        return bannertextColor;
    }

    public void setBannertextColor(String bannertextColor) {
        this.bannertextColor = bannertextColor;
    }

    public String getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(String bannerColor) {
        this.bannerColor = bannerColor;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }




    @Override
    public int getSubItemId() {
        return subItemId;
    }

    @Override
    public void setSubItemId(int subItemId) {
        this.subItemId = subItemId;
    }

    @Override
    public String getSubItemName() {
        return subItemName;
    }

    @Override
    public void setSubItemName(String subItemName) {
        this.subItemName = subItemName;
    }

    @Override
    public String getSubItemImage() {
        return subItemImage;
    }

    @Override
    public void setSubItemImage(String subItemImage) {
        this.subItemImage = subItemImage;
    }

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String getBaseUnit() {
        return baseUnit;
    }

    @Override
    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    @Override
    public Integer getStock() {
        return stock;
    }

    @Override
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
