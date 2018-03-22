package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 3/19/16.
 */
public class FareStructure {

	@SerializedName("fare_fixed")
	@Expose
	private Double fareFixed;
	@SerializedName("fare_per_km")
	@Expose
	private Double farePerKm;
	@SerializedName("fare_threshold_distance")
	@Expose
	private Double fareThresholdDistance;
	@SerializedName("fare_per_min")
	@Expose
	private Double farePerMin;
	@SerializedName("fare_threshold_time")
	@Expose
	private Double fareThresholdTime;
	@SerializedName("fare_per_waiting_min")
	@Expose
	private Double farePerWaitingMin;
	@SerializedName("fare_threshold_waiting_time")
	@Expose
	private Double fareThresholdWaitingTime;
	@SerializedName("start_time")
	@Expose
	private String startTime;
	@SerializedName("end_time")
	@Expose
	private String endTime;
	@SerializedName("convenience_charge")
	@Expose
	private Double convenienceCharge;
	@SerializedName("vehicle_type")
	@Expose
	private Integer vehicleType;
	@SerializedName("ride_type")
	@Expose
	private Integer rideType;
	@SerializedName("display_base_fare")
	@Expose
	private String displayBaseFare;
	@SerializedName("display_fare_text")
	@Expose
	private String displayFareText;
	@SerializedName("operator_id")
	@Expose
	private int operatorId;

	/**
	 *
	 * @return
	 * The fareFixed
	 */
	public Double getFareFixed() {
		return fareFixed;
	}

	/**
	 *
	 * @param fareFixed
	 * The fare_fixed
	 */
	public void setFareFixed(Double fareFixed) {
		this.fareFixed = fareFixed;
	}

	/**
	 *
	 * @return
	 * The farePerKm
	 */
	public Double getFarePerKm() {
		return farePerKm;
	}

	/**
	 *
	 * @param farePerKm
	 * The fare_per_km
	 */
	public void setFarePerKm(Double farePerKm) {
		this.farePerKm = farePerKm;
	}

	/**
	 *
	 * @return
	 * The fareThresholdDistance
	 */
	public Double getFareThresholdDistance() {
		return fareThresholdDistance;
	}

	/**
	 *
	 * @param fareThresholdDistance
	 * The fare_threshold_distance
	 */
	public void setFareThresholdDistance(Double fareThresholdDistance) {
		this.fareThresholdDistance = fareThresholdDistance;
	}

	/**
	 *
	 * @return
	 * The farePerMin
	 */
	public Double getFarePerMin() {
		return farePerMin;
	}

	/**
	 *
	 * @param farePerMin
	 * The fare_per_min
	 */
	public void setFarePerMin(Double farePerMin) {
		this.farePerMin = farePerMin;
	}

	/**
	 *
	 * @return
	 * The fareThresholdTime
	 */
	public Double getFareThresholdTime() {
		return fareThresholdTime;
	}

	/**
	 *
	 * @param fareThresholdTime
	 * The fare_threshold_time
	 */
	public void setFareThresholdTime(Double fareThresholdTime) {
		this.fareThresholdTime = fareThresholdTime;
	}

	/**
	 *
	 * @return
	 * The farePerWaitingMin
	 */
	public Double getFarePerWaitingMin() {
		return farePerWaitingMin;
	}

	/**
	 *
	 * @param farePerWaitingMin
	 * The fare_per_waiting_min
	 */
	public void setFarePerWaitingMin(Double farePerWaitingMin) {
		this.farePerWaitingMin = farePerWaitingMin;
	}

	/**
	 *
	 * @return
	 * The fareThresholdWaitingTime
	 */
	public Double getFareThresholdWaitingTime() {
		return fareThresholdWaitingTime;
	}

	/**
	 *
	 * @param fareThresholdWaitingTime
	 * The fare_threshold_waiting_time
	 */
	public void setFareThresholdWaitingTime(Double fareThresholdWaitingTime) {
		this.fareThresholdWaitingTime = fareThresholdWaitingTime;
	}

	/**
	 *
	 * @return
	 * The startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 *
	 * @param startTime
	 * The start_time
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 *
	 * @return
	 * The endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 *
	 * @param endTime
	 * The end_time
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Double getConvenienceCharge() {
		return convenienceCharge;
	}

	public void setConvenienceCharge(Double convenienceCharge) {
		this.convenienceCharge = convenienceCharge;
	}

	public Integer getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Integer getRideType() {
		return rideType;
	}

	public void setRideType(Integer rideType) {
		this.rideType = rideType;
	}

	public String getDisplayBaseFare() {
		return displayBaseFare;
	}

	public void setDisplayBaseFare(String displayBaseFare) {
		this.displayBaseFare = displayBaseFare;
	}


	public String getDisplayFareText() {
		return displayFareText;
	}

	public void setDisplayFareText(String displayFareText) {
		this.displayFareText = displayFareText;
	}


	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
}