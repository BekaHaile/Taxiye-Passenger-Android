package product.clicklabs.jugnoo.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.datastructure.FareStructure;

/**
 * Created by shankar on 3/19/16.
 */
public class VehicleType {

	@SerializedName("vehicle_type")
	@Expose
	private Integer vehicleType;
	@SerializedName("region_id")
	@Expose
	private Integer regionId;
	@SerializedName("region_name")
	@Expose
	private String regionName;
	@SerializedName("icon_set")
	@Expose
	private String iconSet;

	private VehicleIconSet vehicleIconSet;

	private FareStructure fareStructure;

	@SerializedName("eta")
	@Expose
	private String eta;

	public VehicleType(){
		this.vehicleType = Constants.VEHICLE_AUTO;
		this.regionId = Constants.VEHICLE_AUTO;
		this.regionName = "Auto";
		this.iconSet = VehicleIconSet.AUTO.getName();
		this.vehicleIconSet = VehicleIconSet.AUTO;
		this.eta = "-";
		this.fareStructure = JSONParser.getFareStructure();
	}


	public Integer getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}

	public FareStructure getFareStructure() {
		return fareStructure;
	}

	public void setFareStructure(FareStructure fareStructure) {
		this.fareStructure = fareStructure;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getEta() {
		if(eta == null){
			return "-";
		} else {
			return eta;
		}
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	public String getIconSet() {
		return iconSet;
	}

	public void setIconSet(String iconSet) {
		this.iconSet = iconSet;
	}

	public VehicleIconSet getVehicleIconSet() {
		return vehicleIconSet;
	}

	public void setVehicleIconSet(VehicleIconSet vehicleIconSet) {
		this.vehicleIconSet = vehicleIconSet;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
}