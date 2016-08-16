package sabkuchfresh.retrofit.model;//package com.sabkuchfresh.retrofit.model;
//
///**
// * Created by socomo on 1/8/16.
// */
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class ShowPromotionsResponse {
//
//    @SerializedName("flag")
//    @Expose
//    private Integer flag;
//    @SerializedName("coupons")
//    @Expose
//    private List<Coupon> coupons = new ArrayList<Coupon>();
//    @SerializedName("promotions")
//    @Expose
//    private List<Promotion> promotions = new ArrayList<Promotion>();
//    @SerializedName("dynamic_factor")
//    @Expose
//    private String dynamicFactor;
//    @SerializedName("fare_structure")
//    @Expose
//    private List<FareStructure> fareStructure = new ArrayList<FareStructure>();
//
//    /**
//     *
//     * @return
//     * The flag
//     */
//    public Integer getFlag() {
//        return flag;
//    }
//
//    /**
//     *
//     * @param flag
//     * The flag
//     */
//    public void setFlag(Integer flag) {
//        this.flag = flag;
//    }
//
//    /**
//     *
//     * @return
//     * The coupons
//     */
//    public List<Coupon> getCoupons() {
//        return coupons;
//    }
//
//    /**
//     *
//     * @param coupons
//     * The coupons
//     */
//    public void setCoupons(List<Coupon> coupons) {
//        this.coupons = coupons;
//    }
//
//    /**
//     *
//     * @return
//     * The promotions
//     */
//    public List<Promotion> getPromotions() {
//        return promotions;
//    }
//
//    /**
//     *
//     * @param promotions
//     * The promotions
//     */
//    public void setPromotions(List<Promotion> promotions) {
//        this.promotions = promotions;
//    }
//
//    /**
//     *
//     * @return
//     * The dynamicFactor
//     */
//    public String getDynamicFactor() {
//        return dynamicFactor;
//    }
//
//    /**
//     *
//     * @param dynamicFactor
//     * The dynamic_factor
//     */
//    public void setDynamicFactor(String dynamicFactor) {
//        this.dynamicFactor = dynamicFactor;
//    }
//
//    /**
//     *
//     * @return
//     * The fareStructure
//     */
//    public List<FareStructure> getFareStructure() {
//        return fareStructure;
//    }
//
//    /**
//     *
//     * @param fareStructure
//     * The fare_structure
//     */
//    public void setFareStructure(List<FareStructure> fareStructure) {
//        this.fareStructure = fareStructure;
//    }
//
//    public class Coupon {
//
//        @SerializedName("coupon_id")
//        @Expose
//        private Integer couponId;
//        @SerializedName("title")
//        @Expose
//        private String title;
//        @SerializedName("subtitle")
//        @Expose
//        private String subtitle;
//        @SerializedName("description")
//        @Expose
//        private String description;
//        @SerializedName("coupon_type")
//        @Expose
//        private Integer couponType;
//        @SerializedName("type")
//        @Expose
//        private Integer type;
//        @SerializedName("discount_percentage")
//        @Expose
//        private Integer discountPercentage;
//        @SerializedName("discount_maximum")
//        @Expose
//        private Integer discountMaximum;
//        @SerializedName("discount")
//        @Expose
//        private Integer discount;
//        @SerializedName("maximum")
//        @Expose
//        private Integer maximum;
//        @SerializedName("start_time")
//        @Expose
//        private String startTime;
//        @SerializedName("end_time")
//        @Expose
//        private String endTime;
//        @SerializedName("image")
//        @Expose
//        private String image;
//        @SerializedName("account_id")
//        @Expose
//        private Integer accountId;
//        @SerializedName("redeemed_on")
//        @Expose
//        private String redeemedOn;
//        @SerializedName("status")
//        @Expose
//        private Integer status;
//        @SerializedName("expiry_date")
//        @Expose
//        private String expiryDate;
//
//        /**
//         *
//         * @return
//         * The couponId
//         */
//        public Integer getCouponId() {
//            return couponId;
//        }
//
//        /**
//         *
//         * @param couponId
//         * The coupon_id
//         */
//        public void setCouponId(Integer couponId) {
//            this.couponId = couponId;
//        }
//
//        /**
//         *
//         * @return
//         * The title
//         */
//        public String getTitle() {
//            return title;
//        }
//
//        /**
//         *
//         * @param title
//         * The title
//         */
//        public void setTitle(String title) {
//            this.title = title;
//        }
//
//        /**
//         *
//         * @return
//         * The subtitle
//         */
//        public String getSubtitle() {
//            return subtitle;
//        }
//
//        /**
//         *
//         * @param subtitle
//         * The subtitle
//         */
//        public void setSubtitle(String subtitle) {
//            this.subtitle = subtitle;
//        }
//
//        /**
//         *
//         * @return
//         * The description
//         */
//        public String getDescription() {
//            return description;
//        }
//
//        /**
//         *
//         * @param description
//         * The description
//         */
//        public void setDescription(String description) {
//            this.description = description;
//        }
//
//        /**
//         *
//         * @return
//         * The couponType
//         */
//        public Integer getCouponType() {
//            return couponType;
//        }
//
//        /**
//         *
//         * @param couponType
//         * The coupon_type
//         */
//        public void setCouponType(Integer couponType) {
//            this.couponType = couponType;
//        }
//
//        /**
//         *
//         * @return
//         * The type
//         */
//        public Integer getType() {
//            return type;
//        }
//
//        /**
//         *
//         * @param type
//         * The type
//         */
//        public void setType(Integer type) {
//            this.type = type;
//        }
//
//        /**
//         *
//         * @return
//         * The discountPercentage
//         */
//        public Integer getDiscountPercentage() {
//            return discountPercentage;
//        }
//
//        /**
//         *
//         * @param discountPercentage
//         * The discount_percentage
//         */
//        public void setDiscountPercentage(Integer discountPercentage) {
//            this.discountPercentage = discountPercentage;
//        }
//
//        /**
//         *
//         * @return
//         * The discountMaximum
//         */
//        public Integer getDiscountMaximum() {
//            return discountMaximum;
//        }
//
//        /**
//         *
//         * @param discountMaximum
//         * The discount_maximum
//         */
//        public void setDiscountMaximum(Integer discountMaximum) {
//            this.discountMaximum = discountMaximum;
//        }
//
//        /**
//         *
//         * @return
//         * The discount
//         */
//        public Integer getDiscount() {
//            return discount;
//        }
//
//        /**
//         *
//         * @param discount
//         * The discount
//         */
//        public void setDiscount(Integer discount) {
//            this.discount = discount;
//        }
//
//        /**
//         *
//         * @return
//         * The maximum
//         */
//        public Integer getMaximum() {
//            return maximum;
//        }
//
//        /**
//         *
//         * @param maximum
//         * The maximum
//         */
//        public void setMaximum(Integer maximum) {
//            this.maximum = maximum;
//        }
//
//        /**
//         *
//         * @return
//         * The startTime
//         */
//        public String getStartTime() {
//            return startTime;
//        }
//
//        /**
//         *
//         * @param startTime
//         * The start_time
//         */
//        public void setStartTime(String startTime) {
//            this.startTime = startTime;
//        }
//
//        /**
//         *
//         * @return
//         * The endTime
//         */
//        public String getEndTime() {
//            return endTime;
//        }
//
//        /**
//         *
//         * @param endTime
//         * The end_time
//         */
//        public void setEndTime(String endTime) {
//            this.endTime = endTime;
//        }
//
//        /**
//         *
//         * @return
//         * The image
//         */
//        public String getImage() {
//            return image;
//        }
//
//        /**
//         *
//         * @param image
//         * The image
//         */
//        public void setImage(String image) {
//            this.image = image;
//        }
//
//        /**
//         *
//         * @return
//         * The accountId
//         */
//        public Integer getAccountId() {
//            return accountId;
//        }
//
//        /**
//         *
//         * @param accountId
//         * The account_id
//         */
//        public void setAccountId(Integer accountId) {
//            this.accountId = accountId;
//        }
//
//        /**
//         *
//         * @return
//         * The redeemedOn
//         */
//        public String getRedeemedOn() {
//            return redeemedOn;
//        }
//
//        /**
//         *
//         * @param redeemedOn
//         * The redeemed_on
//         */
//        public void setRedeemedOn(String redeemedOn) {
//            this.redeemedOn = redeemedOn;
//        }
//
//        /**
//         *
//         * @return
//         * The status
//         */
//        public Integer getStatus() {
//            return status;
//        }
//
//        /**
//         *
//         * @param status
//         * The status
//         */
//        public void setStatus(Integer status) {
//            this.status = status;
//        }
//
//        /**
//         *
//         * @return
//         * The expiryDate
//         */
//        public String getExpiryDate() {
//            return expiryDate;
//        }
//
//        /**
//         *
//         * @param expiryDate
//         * The expiry_date
//         */
//        public void setExpiryDate(String expiryDate) {
//            this.expiryDate = expiryDate;
//        }
//
//    }
//
//    public class FareStructure {
//
//        @SerializedName("fare_fixed")
//        @Expose
//        private Double fareFixed;
//        @SerializedName("fare_per_km")
//        @Expose
//        private Double farePerKm;
//        @SerializedName("fare_threshold_distance")
//        @Expose
//        private Double fareThresholdDistance;
//        @SerializedName("fare_per_min")
//        @Expose
//        private Double farePerMin;
//        @SerializedName("fare_threshold_time")
//        @Expose
//        private Double fareThresholdTime;
//        @SerializedName("fare_per_waiting_min")
//        @Expose
//        private Double farePerWaitingMin;
//        @SerializedName("fare_threshold_waiting_time")
//        @Expose
//        private Double fareThresholdWaitingTime;
//        @SerializedName("start_time")
//        @Expose
//        private String startTime;
//        @SerializedName("end_time")
//        @Expose
//        private String endTime;
//        @SerializedName("convenience_charge")
//        @Expose
//        private Double convenienceCharge;
//
//        /**
//         *
//         * @return
//         * The fareFixed
//         */
//        public Double getFareFixed() {
//            return fareFixed;
//        }
//
//        /**
//         *
//         * @param fareFixed
//         * The fare_fixed
//         */
//        public void setFareFixed(Double fareFixed) {
//            this.fareFixed = fareFixed;
//        }
//
//        /**
//         *
//         * @return
//         * The farePerKm
//         */
//        public Double getFarePerKm() {
//            return farePerKm;
//        }
//
//        /**
//         *
//         * @param farePerKm
//         * The fare_per_km
//         */
//        public void setFarePerKm(Double farePerKm) {
//            this.farePerKm = farePerKm;
//        }
//
//        /**
//         *
//         * @return
//         * The fareThresholdDistance
//         */
//        public Double getFareThresholdDistance() {
//            return fareThresholdDistance;
//        }
//
//        /**
//         *
//         * @param fareThresholdDistance
//         * The fare_threshold_distance
//         */
//        public void setFareThresholdDistance(Double fareThresholdDistance) {
//            this.fareThresholdDistance = fareThresholdDistance;
//        }
//
//        /**
//         *
//         * @return
//         * The farePerMin
//         */
//        public Double getFarePerMin() {
//            return farePerMin;
//        }
//
//        /**
//         *
//         * @param farePerMin
//         * The fare_per_min
//         */
//        public void setFarePerMin(Double farePerMin) {
//            this.farePerMin = farePerMin;
//        }
//
//        /**
//         *
//         * @return
//         * The fareThresholdTime
//         */
//        public Double getFareThresholdTime() {
//            return fareThresholdTime;
//        }
//
//        /**
//         *
//         * @param fareThresholdTime
//         * The fare_threshold_time
//         */
//        public void setFareThresholdTime(Double fareThresholdTime) {
//            this.fareThresholdTime = fareThresholdTime;
//        }
//
//        /**
//         *
//         * @return
//         * The farePerWaitingMin
//         */
//        public Double getFarePerWaitingMin() {
//            return farePerWaitingMin;
//        }
//
//        /**
//         *
//         * @param farePerWaitingMin
//         * The fare_per_waiting_min
//         */
//        public void setFarePerWaitingMin(Double farePerWaitingMin) {
//            this.farePerWaitingMin = farePerWaitingMin;
//        }
//
//        /**
//         *
//         * @return
//         * The fareThresholdWaitingTime
//         */
//        public Double getFareThresholdWaitingTime() {
//            return fareThresholdWaitingTime;
//        }
//
//        /**
//         *
//         * @param fareThresholdWaitingTime
//         * The fare_threshold_waiting_time
//         */
//        public void setFareThresholdWaitingTime(Double fareThresholdWaitingTime) {
//            this.fareThresholdWaitingTime = fareThresholdWaitingTime;
//        }
//
//        /**
//         *
//         * @return
//         * The startTime
//         */
//        public String getStartTime() {
//            return startTime;
//        }
//
//        /**
//         *
//         * @param startTime
//         * The start_time
//         */
//        public void setStartTime(String startTime) {
//            this.startTime = startTime;
//        }
//
//        /**
//         *
//         * @return
//         * The endTime
//         */
//        public String getEndTime() {
//            return endTime;
//        }
//
//        /**
//         *
//         * @param endTime
//         * The end_time
//         */
//        public void setEndTime(String endTime) {
//            this.endTime = endTime;
//        }
//
//        public Double getConvenienceCharge() {
//            return convenienceCharge;
//        }
//
//        public void setConvenienceCharge(Double convenienceCharge) {
//            this.convenienceCharge = convenienceCharge;
//        }
//    }
//
//    public class Promotion {
//
//        @SerializedName("promo_id")
//        @Expose
//        private Integer promoId;
//        @SerializedName("title")
//        @Expose
//        private String title;
//        @SerializedName("max_allowed")
//        @Expose
//        private Integer maxAllowed;
//        @SerializedName("terms_n_conds")
//        @Expose
//        private String termsNConds;
//
//        /**
//         *
//         * @return
//         * The promoId
//         */
//        public Integer getPromoId() {
//            return promoId;
//        }
//
//        /**
//         *
//         * @param promoId
//         * The promo_id
//         */
//        public void setPromoId(Integer promoId) {
//            this.promoId = promoId;
//        }
//
//        /**
//         *
//         * @return
//         * The title
//         */
//        public String getTitle() {
//            return title;
//        }
//
//        /**
//         *
//         * @param title
//         * The title
//         */
//        public void setTitle(String title) {
//            this.title = title;
//        }
//
//        /**
//         *
//         * @return
//         * The maxAllowed
//         */
//        public Integer getMaxAllowed() {
//            return maxAllowed;
//        }
//
//        /**
//         *
//         * @param maxAllowed
//         * The max_allowed
//         */
//        public void setMaxAllowed(Integer maxAllowed) {
//            this.maxAllowed = maxAllowed;
//        }
//
//        /**
//         *
//         * @return
//         * The termsNConds
//         */
//        public String getTermsNConds() {
//            return termsNConds;
//        }
//
//        /**
//         *
//         * @param termsNConds
//         * The terms_n_conds
//         */
//        public void setTermsNConds(String termsNConds) {
//            this.termsNConds = termsNConds;
//        }
//
//    }
//}
