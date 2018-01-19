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
    @SerializedName("is_razorpay_enabled")
    @Expose
    private int isRazorpayEnabled = -1;
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
    @SerializedName("nearbyPickupRegions")
    @Expose
    private NearbyPickupRegions nearbyPickupRegions;
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
    @SerializedName("grocery_enabled")
    @Expose
    private Integer groceryEnabled = 0;
    @SerializedName("menus_enabled")
    @Expose
    private Integer menusEnabled = 0;
    @SerializedName("delivery_customer_enabled")
    @Expose
    private Integer deliveryCustomerEnabled = 0;
    @SerializedName("pay_enabled")
    @Expose
    private Integer payEnabled = 0;
    @SerializedName("feed_enabled")
    @Expose
    private Integer feedEnabled = 0;
    @SerializedName("autos_enabled")
    @Expose
    private Integer autosEnabled ;
    @SerializedName("pros_enabled")
    @Expose
    private Integer prosEnabled = 0;
    @SerializedName("integrated_jugnoo_enabled")
    @Expose
    private Integer integratedJugnooEnabled;
    @SerializedName("game_predict_url")
    @Expose
    private String gamePredictUrl = "";
    @SerializedName("topup_card_enabled")
    @Expose
    private Integer topupCardEnabled;


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
    @SerializedName("menus_coupons")
    @Expose
    private List<CouponInfo> menusCoupons = new ArrayList<>();
    @SerializedName("delivery_customer_promotions")
    @Expose
    private List<PromotionInfo> deliveryCustomerPromotions = new ArrayList<>();
    @SerializedName("delivery_customer_coupons")
    @Expose
    private List<CouponInfo> deliveryCustomerCoupons = new ArrayList<>();
    @SerializedName("pay_promotions")
    @Expose
    private List<PromotionInfo> payPromotions = new ArrayList<>();
    @SerializedName("pay_coupons")
    @Expose
    private List<CouponInfo> payCoupons = new ArrayList<>();
    @SerializedName("points_of_interest")
    @Expose
    private List<FetchUserAddressResponse.Address> pointsOfInterestAddresses = new ArrayList<FetchUserAddressResponse.Address>();

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

    public NearbyPickupRegions getNearbyPickupRegions() {
        return nearbyPickupRegions;
    }

    public void setNearbyPickupRegions(NearbyPickupRegions nearbyPickupRegions) {
        this.nearbyPickupRegions = nearbyPickupRegions;
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

    public Integer getGroceryEnabled() {
        return groceryEnabled;
    }

    public void setGroceryEnabled(Integer groceryEnabled) {
        this.groceryEnabled = groceryEnabled;
    }

    public Integer getMenusEnabled() {
        return menusEnabled;
    }
    public Integer getDeliveryCustomerEnabled() {
        return deliveryCustomerEnabled;
    }

    public void setMenusEnabled(Integer menusEnabled) {
        this.menusEnabled = menusEnabled;
    }

    public String getGamePredictUrl() {
        return gamePredictUrl;
    }

    public void setGamePredictUrl(String gamePredictUrl) {
        this.gamePredictUrl = gamePredictUrl;
    }

    public Integer getIntegratedJugnooEnabled() {
        return integratedJugnooEnabled;
    }

    public void setIntegratedJugnooEnabled(Integer integratedJugnooEnabled) {
        this.integratedJugnooEnabled = integratedJugnooEnabled;
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

    public Integer getTopupCardEnabled() {
        return topupCardEnabled;
    }

    public void setTopupCardEnabled(Integer topupCardEnabled) {
        this.topupCardEnabled = topupCardEnabled;
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

    public List<PromotionInfo> getDeliveryCustomerPromotions() {
        return deliveryCustomerPromotions;
    }

    public void setDeliveryCustomerPromotions(List<PromotionInfo> deliveryCustomerPromotions) {
        this.deliveryCustomerPromotions = deliveryCustomerPromotions;
    }

    public List<CouponInfo> getDeliveryCustomerCoupons() {
        return deliveryCustomerCoupons;
    }

    public void setDeliveryCustomerCoupons(List<CouponInfo> deliveryCustomerCoupons) {
        this.deliveryCustomerCoupons = deliveryCustomerCoupons;
    }

    public Integer getPayEnabled() {
        return payEnabled;
    }

    public void setPayEnabled(Integer payEnabled) {
        this.payEnabled = payEnabled;
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


    public List<FetchUserAddressResponse.Address> getPointsOfInterestAddresses() {
        return pointsOfInterestAddresses;
    }

    public void setPointsOfInterestAddresses(List<FetchUserAddressResponse.Address> pointsOfInterestAddresses) {
        this.pointsOfInterestAddresses = pointsOfInterestAddresses;
    }

    public Integer getFeedEnabled() {
        if(feedEnabled == null){
            feedEnabled = 0;
        }
        return feedEnabled;
    }

    public void setFeedEnabled(Integer feedEnabled) {
        this.feedEnabled = feedEnabled;
    }

    public Integer getProsEnabled() {
        return prosEnabled;
    }

    public void setProsEnabled(Integer prosEnabled) {
        this.prosEnabled = prosEnabled;
    }

    public int getIsRazorpayEnabled() {
        return isRazorpayEnabled;
    }

    public void setIsRazorpayEnabled(int isRazorpayEnabled) {
        this.isRazorpayEnabled = isRazorpayEnabled;
    }

    public Integer getAutosEnabled() {
        return autosEnabled;
    }
}
