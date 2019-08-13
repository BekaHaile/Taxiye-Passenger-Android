package product.clicklabs.jugnoo.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetFetchUserDriverResponse {

    @SerializedName("user_id")
    @Expose
    public int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    @SerializedName("driver_id")
    @Expose
    public int driverId;

    @SerializedName("type")
    @Expose
    public int type;

    @SerializedName("driver_name")
    @Expose
    public String driverName;

    @SerializedName("vehicle_type")
    @Expose
    public int vehicleType;

    @SerializedName("avg_rating")
    @Expose
    public double avgRating;

}
