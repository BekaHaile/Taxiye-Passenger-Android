package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;

/**
 * Created by shankar on 3/19/16.
 */
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
	@SerializedName("device_token")
	@Expose
	private String deviceToken;
	@SerializedName("external_id")
	@Expose
	private String externalId;
	@Expose
	@SerializedName("region_ids")
	private List<Integer> regionIds = new ArrayList<>();
	@SerializedName("distance")
	@Expose
	private Double distance;
	@SerializedName("rating")
	@Expose
	private Double rating;
	@SerializedName("bearing")
	@Expose
	private Double bearing;
	@SerializedName("audit_status")
	@Expose
	private String brandingStatus;
	@SerializedName("operator_id")
	@Expose
	private int operatorId;
	@SerializedName("payment_method")
	@Expose
	private int paymentMethod;
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

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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

	public List<Integer> getRegionIds() {
		if(regionIds == null){
			regionIds = new ArrayList<>();
			regionIds.add(Constants.VEHICLE_AUTO);
		}
		return regionIds;
	}

	public void setRegionIds(List<Integer> regionIds) {
		this.regionIds = regionIds;
	}

	public String getBrandingStatus() {
		if(brandingStatus == null){
			brandingStatus = "";
		}
		return brandingStatus;
	}

	public void setBrandingStatus(String brandingStatus) {
		this.brandingStatus = brandingStatus;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(int paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}