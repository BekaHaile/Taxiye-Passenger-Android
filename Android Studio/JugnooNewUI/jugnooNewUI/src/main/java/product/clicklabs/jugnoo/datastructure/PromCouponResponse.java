package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 24/08/16.
 */
public class PromCouponResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("coupons")
    @Expose
    private List<CouponInfo> coupons = new ArrayList<>();
    @SerializedName("promotions")
    @Expose
    private List<PromotionInfo> promotions = new ArrayList<>();
    @SerializedName("common_promotions")
    @Expose
    private List<PromotionInfo> commonPromotions = new ArrayList<>();
    @SerializedName("common_coupons")
    @Expose
    private List<CouponInfo> commonCoupons = new ArrayList<>();
    @SerializedName("autos_promotions")
    @Expose
    private List<PromotionInfo> autosPromotions = new ArrayList<>();
    @SerializedName("autos_coupons")
    @Expose
    private List<CouponInfo> autosCoupons = new ArrayList<>();
    @SerializedName("fresh_promotions")
    @Expose
    private List<PromotionInfo> freshPromotions = new ArrayList<>();
    @SerializedName("fresh_coupons")
    @Expose
    private List<CouponInfo> freshCoupons = new ArrayList<>();
    @SerializedName("meals_promotions")
    @Expose
    private List<PromotionInfo> mealsPromotions = new ArrayList<>();
    @SerializedName("meals_coupons")
    @Expose
    private List<CouponInfo> mealsCoupons = new ArrayList<>();
    @SerializedName("delivery_promotions")
    @Expose
    private List<PromotionInfo> deliveryPromotions = new ArrayList<>();
    @SerializedName("delivery_coupons")
    @Expose
    private List<CouponInfo> deliveryCoupons = new ArrayList<>();
    @SerializedName("grocery_promotions")
    @Expose
    private List<PromotionInfo> groceryPromotions = new ArrayList<>();
    @SerializedName("grocery_coupons")
    @Expose
    private List<CouponInfo> groceryCoupons = new ArrayList<>();
    @SerializedName("menus_promotions")
    @Expose
    private List<PromotionInfo> menusPromotions = new ArrayList<>();

    public List<PromotionInfo> getDeliveryCustomerPromotions() {
        return deliveryCustomerPromotions;
    }

    @SerializedName("delivery_customer_promotions")
    @Expose

    private List<PromotionInfo> deliveryCustomerPromotions = new ArrayList<>();
    @SerializedName("menus_coupons")
    @Expose
    private List<CouponInfo> menusCoupons = new ArrayList<>();
    @SerializedName("delivery_customer_coupons")
    @Expose
    private List<CouponInfo> deliveryCustomerCoupons = new ArrayList<>();
    @SerializedName("pay_promotions")
    @Expose
    private List<PromotionInfo> payPromotions = new ArrayList<>();
    @SerializedName("pay_coupons")
    @Expose
    private List<CouponInfo> payCoupons = new ArrayList<>();
    @SerializedName("head")
    @Expose
    private String head;
    @SerializedName("invite_message")
    @Expose
    private String inviteMessage;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public List<CouponInfo> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponInfo> coupons) {
        this.coupons = coupons;
    }

    public List<PromotionInfo> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionInfo> promotions) {
        this.promotions = promotions;
    }

    public List<PromotionInfo> getCommonPromotions() {
        return commonPromotions;
    }

    public void setCommonPromotions(List<PromotionInfo> commonPromotions) {
        this.commonPromotions = commonPromotions;
    }

    public List<CouponInfo> getCommonCoupons() {
        return commonCoupons;
    }

    public void setCommonCoupons(List<CouponInfo> commonCoupons) {
        this.commonCoupons = commonCoupons;
    }

    public List<PromotionInfo> getAutosPromotions() {
        return autosPromotions;
    }

    public void setAutosPromotions(List<PromotionInfo> autosPromotions) {
        this.autosPromotions = autosPromotions;
    }

    public List<CouponInfo> getAutosCoupons() {
        return autosCoupons;
    }

    public void setAutosCoupons(List<CouponInfo> autosCoupons) {
        this.autosCoupons = autosCoupons;
    }

    public List<PromotionInfo> getFreshPromotions() {
        return freshPromotions;
    }

    public void setFreshPromotions(List<PromotionInfo> freshPromotions) {
        this.freshPromotions = freshPromotions;
    }

    public List<CouponInfo> getFreshCoupons() {
        return freshCoupons;
    }

    public void setFreshCoupons(List<CouponInfo> freshCoupons) {
        this.freshCoupons = freshCoupons;
    }

    public List<PromotionInfo> getMealsPromotions() {
        return mealsPromotions;
    }

    public void setMealsPromotions(List<PromotionInfo> mealsPromotions) {
        this.mealsPromotions = mealsPromotions;
    }

    public List<CouponInfo> getMealsCoupons() {
        return mealsCoupons;
    }

    public void setMealsCoupons(List<CouponInfo> mealsCoupons) {
        this.mealsCoupons = mealsCoupons;
    }

    public List<PromotionInfo> getDeliveryPromotions() {
        return deliveryPromotions;
    }

    public void setDeliveryPromotions(List<PromotionInfo> deliveryPromotions) {
        this.deliveryPromotions = deliveryPromotions;
    }

    public List<CouponInfo> getDeliveryCoupons() {
        return deliveryCoupons;
    }

    public void setDeliveryCoupons(List<CouponInfo> deliveryCoupons) {
        this.deliveryCoupons = deliveryCoupons;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public void setInviteMessage(String inviteMessage) {
        this.inviteMessage = inviteMessage;
    }

    public List<PromotionInfo> getGroceryPromotions() {
        return groceryPromotions;
    }

    public void setGroceryPromotions(List<PromotionInfo> groceryPromotions) {
        this.groceryPromotions = groceryPromotions;
    }

    public List<CouponInfo> getGroceryCoupons() {
        return groceryCoupons;
    }

    public void setGroceryCoupons(List<CouponInfo> groceryCoupons) {
        this.groceryCoupons = groceryCoupons;
    }

    public List<PromotionInfo> getMenusPromotions() {
        return menusPromotions;
    }

    public void setMenusPromotions(List<PromotionInfo> menusPromotions) {
        this.menusPromotions = menusPromotions;
    }

    public List<CouponInfo> getMenusCoupons() {
        return menusCoupons;
    }

    public void setMenusCoupons(List<CouponInfo> menusCoupons) {
        this.menusCoupons = menusCoupons;
    }

    public List<PromotionInfo> getPayPromotions() {
        return payPromotions;
    }

    public void setPayPromotions(List<PromotionInfo> payPromotions) {
        this.payPromotions = payPromotions;
    }

    public List<CouponInfo> getPayCoupons() {
        return payCoupons;
    }

    public void setPayCoupons(List<CouponInfo> payCoupons) {
        this.payCoupons = payCoupons;
    }

    public List<CouponInfo> getDeliveryCustomerCoupons() {
        return deliveryCustomerCoupons;
    }

}
