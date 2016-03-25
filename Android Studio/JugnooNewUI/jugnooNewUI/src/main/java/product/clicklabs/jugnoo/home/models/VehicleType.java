package product.clicklabs.jugnoo.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 3/19/16.
 */
public class VehicleType {

	@SerializedName("vehicle_id")
	@Expose
	private Integer vehicleId;
	@SerializedName("vehicle_name")
	@Expose
	private String vehicleName;

	public VehicleType(Integer vehicleId, String vehicleName){
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
	}

	public Integer getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Integer vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}
}