package product.clicklabs.jugnoo.retrofit.model;

/**
 * Created by socomo on 1/8/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class FindADriverResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("drivers")
    @Expose
    private List<Driver> drivers = new ArrayList<Driver>();
    @SerializedName("eta")
    @Expose
    private Double eta;
    @SerializedName("fare_factor")
    @Expose
    private Double fareFactor;
    @SerializedName("priority_tip_category")
    @Expose
    private Integer priorityTipCategory;
    @SerializedName("far_away_city")
    @Expose
    private String farAwayCity;
    @SerializedName("coupons")
    @Expose
    private List<Coupon> coupons = new ArrayList<Coupon>();
    @SerializedName("promotions")
    @Expose
    private List<Promotion> promotions = new ArrayList<Promotion>();
    @SerializedName("fare_structure")
    @Expose
    private List<FareStructure> fareStructure = new ArrayList<FareStructure>();


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
     * @return The eta
     */
    public Double getEta() {
        return eta;
    }

    /**
     * @param eta The eta
     */
    public void setEta(Double eta) {
        this.eta = eta;
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
    public List<Coupon> getCoupons() {
        return coupons;
    }

    /**
     *
     * @param coupons
     * The coupons
     */
    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    /**
     *
     * @return
     * The promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
    }

    /**
     *
     * @param promotions
     * The promotions
     */
    public void setPromotions(List<Promotion> promotions) {
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





}
