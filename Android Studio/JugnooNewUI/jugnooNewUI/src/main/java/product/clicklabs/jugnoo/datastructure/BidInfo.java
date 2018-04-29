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
	@SerializedName("driver_rating")
	@Expose
	private double rating;

	public BidInfo(int engagementId, double bidValue, String currency, double distance, double rating) {
		this.engagementId = engagementId;
		this.bidValue = bidValue;
		this.currency = currency;
		this.distance = distance;
		this.rating = rating;
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
}
