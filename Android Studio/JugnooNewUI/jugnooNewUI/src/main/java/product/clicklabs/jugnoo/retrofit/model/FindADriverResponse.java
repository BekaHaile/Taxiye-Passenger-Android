package product.clicklabs.jugnoo.retrofit.model;

/**
 * Created by socomo on 1/8/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.models.Region;


public class FindADriverResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("drivers")
    @Expose
    private List<Driver> drivers = new ArrayList<Driver>();
    @SerializedName("fare_factor")
    @Expose
    private Double fareFactor;
    @SerializedName("driver_fare_factor")
    @Expose
    private Double driverFareFactor;
    @SerializedName("priority_tip_category")
    @Expose
    private Integer priorityTipCategory;
    @SerializedName("far_away_city")
    @Expose
    private String farAwayCity;
    @SerializedName("coupons")
    @Expose
    private List<CouponInfo> coupons = new ArrayList<>();
    @SerializedName("promotions")
    @Expose
    private List<PromotionInfo> promotions = new ArrayList<>();
    @SerializedName("fare_structure")
    @Expose
    private List<FareStructure> fareStructure = new ArrayList<FareStructure>();
    @SerializedName("regions")
    @Expose
    private List<Region> regions = new ArrayList<>();

    @SerializedName("campaigns")
    @Expose
    private Campaigns campaigns;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("fresh_enabled")
    @Expose
    private Integer freshEnabled = 0;
    @SerializedName("meals_enabled")
    @Expose
    private Integer mealsEnabled = 0;
    @SerializedName("delivery_enabled")
    @Expose
    private Integer deliveryEnabled = 0;

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

    public List<CouponInfo> getDeliveryCoupons() {
        return deliveryCoupons;
    }

    public void setDeliveryCoupons(List<CouponInfo> deliveryCoupons) {
        this.deliveryCoupons = deliveryCoupons;
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



    /**
     *
     * @return
     * The campaigns
     */
    public Campaigns getCampaigns() {
        return campaigns;
    }

    /**
     *
     * @param campaigns
     * The campaigns
     */
    public void setCampaigns(Campaigns campaigns) {
        this.campaigns = campaigns;
    }

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return The drivers
     */
    public List<Driver> getDrivers() {
        return drivers;
    }

    /**
     * @param drivers The drivers
     */
    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    /**
     * @return The fareFactor
     */
    public Double getFareFactor() {
        return fareFactor;
    }

    /**
     * @param fareFactor The fare_factor
     */
    public void setFareFactor(Double fareFactor) {
        this.fareFactor = fareFactor;
    }

    /**
     * @return The priorityTipCategory
     */
    public Integer getPriorityTipCategory() {
        return priorityTipCategory;
    }

    /**
     * @param priorityTipCategory The priority_tip_category
     */
    public void setPriorityTipCategory(Integer priorityTipCategory) {
        this.priorityTipCategory = priorityTipCategory;
    }

    public String getFarAwayCity() {
        return farAwayCity;
    }

    public void setFarAwayCity(String farAwayCity) {
        this.farAwayCity = farAwayCity;
    }

    /**
     *
     * @return
     * The coupons
     */
    public List<CouponInfo> getCoupons() {
        return coupons;
    }

    /**
     *
     * @param coupons
     * The coupons
     */
    public void setCoupons(List<CouponInfo> coupons) {
        this.coupons = coupons;
    }

    /**
     *
     * @return
     * The promotions
     */
    public List<PromotionInfo> getPromotions() {
        return promotions;
    }

    /**
     *
     * @param promotions
     * The promotions
     */
    public void setPromotions(List<PromotionInfo> promotions) {
        this.promotions = promotions;
    }

    /**
     *
     * @return
     * The fareStructure
     */
    public List<FareStructure> getFareStructure() {
        return fareStructure;
    }

    /**
     *
     * @param fareStructure
     * The fare_structure
     */
    public void setFareStructure(List<FareStructure> fareStructure) {
        this.fareStructure = fareStructure;
    }


    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public Double getDriverFareFactor() {
        return driverFareFactor;
    }

    public void setDriverFareFactor(Double driverFareFactor) {
        this.driverFareFactor = driverFareFactor;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getDeliveryEnabled() {
        return deliveryEnabled;
    }

    public void setDeliveryEnabled(Integer deliveryEnabled) {
        this.deliveryEnabled = deliveryEnabled;
    }

    public Integer getFreshEnabled() {
        return freshEnabled;
    }

    public void setFreshEnabled(Integer freshEnabled) {
        this.freshEnabled = freshEnabled;
    }

    public Integer getMealsEnabled() {
        return mealsEnabled;
    }

    public void setMealsEnabled(Integer mealsEnabled) {
        this.mealsEnabled = mealsEnabled;
    }
}
