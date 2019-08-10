package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BidInfo {

	@SerializedName("engagement_id")
	@Expose
	private int engagementId;
	@SerializedName("bid_value")
	@Expose
	private double bidValue;
	@SerializedName("currency")
	@Expose
	private String currency;
	@SerializedName("accept_distance")
	@Expose
	private double distance;
	@SerializedName("accept_distance_text")
	@Expose
	private String acceptDistanceText;
	@SerializedName("driver_rating")
	@Expose
	private double rating;
	@SerializedName("created_at")
	@Expose
	private String createdAt;
	@SerializedName("driver_image")
	@Expose
	private String driverImage;
	@SerializedName("driver_name")
	@Expose
	private String driverName;
	@SerializedName("vehicle_name")
	@Expose
	private String vehicleName;
	@SerializedName("driver_eta")
	@Expose
	private String eta;

	public BidInfo(int engagementId, double bidValue, String currency, double distance,String acceptDistanceText, double rating,
				   String createdAt, String driverImage, String driverName, String vehicleName, String eta) {
		this.engagementId = engagementId;
		this.bidValue = bidValue;
		this.currency = currency;
		this.distance = distance;
		this.rating = rating;
		this.acceptDistanceText = acceptDistanceText;
		this.createdAt = createdAt;
		this.driverImage = driverImage;
		this.driverName = driverName;
		this.vehicleName = vehicleName;
		this.eta = eta;
	}

	public int getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(int engagementId) {
		this.engagementId = engagementId;
	}

	public double getBidValue() {
		return bidValue;
	}

	public void setBidValue(double bidValue) {
		this.bidValue = bidValue;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}


	public String getAcceptDistanceText() {
		return acceptDistanceText;
	}

	public void setAcceptDistanceText(String acceptDistanceText) {
		this.acceptDistanceText = acceptDistanceText;
	}

	public String getDriverImage() {
		return driverImage;
	}

	public void setDriverImage(String driverImage) {
		this.driverImage = driverImage;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getEta() {
		return eta;
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BidInfo && ((BidInfo)obj).getEngagementId() == getEngagementId();
	}
}
