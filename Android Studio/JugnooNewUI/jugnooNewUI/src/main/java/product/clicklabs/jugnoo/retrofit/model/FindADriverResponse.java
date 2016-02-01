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
    private Integer eta;
    @SerializedName("fare_factor")
    @Expose
    private Double fareFactor;
    @SerializedName("priority_tip_category")
    @Expose
    private Integer priorityTipCategory;


    @SerializedName("far_away_city")
    @Expose
    private String farAwayCity;

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
    public Integer getEta() {
        return eta;
    }

    /**
     * @param eta The eta
     */
    public void setEta(Integer eta) {
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

    public class Driver {

        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("phone_no")
        @Expose
        private String phoneNo;
        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("longitude")
        @Expose
        private Double longitude;
        @SerializedName("vehicle_type")
        @Expose
        private Integer vehicleType;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("rating")
        @Expose
        private Double rating;
        @SerializedName("bearing")
        @Expose
        private Double bearing;

        /**
         * @return The userId
         */
        public Integer getUserId() {
            return userId;
        }

        /**
         * @param userId The user_id
         */
        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        /**
         * @return The userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @param userName The user_name
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * @return The phoneNo
         */
        public String getPhoneNo() {
            return phoneNo;
        }

        /**
         * @param phoneNo The phone_no
         */
        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        /**
         * @return The latitude
         */
        public Double getLatitude() {
            return latitude;
        }

        /**
         * @param latitude The latitude
         */
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        /**
         * @return The longitude
         */
        public Double getLongitude() {
            return longitude;
        }

        /**
         * @param longitude The longitude
         */
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        /**
         * @return The vehicleType
         */
        public Integer getVehicleType() {
            return vehicleType;
        }

        /**
         * @param vehicleType The vehicle_type
         */
        public void setVehicleType(Integer vehicleType) {
            this.vehicleType = vehicleType;
        }

        /**
         * @return The distance
         */
        public Double getDistance() {
            return distance;
        }

        /**
         * @param distance The distance
         */
        public void setDistance(Double distance) {
            this.distance = distance;
        }

        /**
         * @return The rating
         */
        public Double getRating() {
            return rating;
        }

        /**
         * @param rating The rating
         */
        public void setRating(Double rating) {
            this.rating = rating;
        }

        public Double getBearing() {
            return bearing;
        }

        public void setBearing(Double bearing) {
            this.bearing = bearing;
        }
    }

}
