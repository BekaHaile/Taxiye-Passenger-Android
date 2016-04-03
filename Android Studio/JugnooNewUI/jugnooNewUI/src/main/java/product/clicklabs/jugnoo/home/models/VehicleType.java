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

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("region_id")
	@Expose
	private Integer regionId;
	@SerializedName("icon_set")
	@Expose
	private String iconSet;

	private VehicleIconSet vehicleIconSet;

	private FareStructure fareStructure;
	private String eta;

	public VehicleType(){
		this.id = Constants.VEHICLE_AUTO;
		this.name = "Auto";
		this.regionId = Constants.VEHICLE_AUTO;
		this.iconSet = VehicleIconSet.AUTO.getName();
		this.vehicleIconSet = VehicleIconSet.AUTO;
		this.eta = "-";
		this.fareStructure = JSONParser.getFareStructure();
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FareStructure getFareStructure() {
		return fareStructure;
	}

	public void setFareStructure(FareStructure fareStructure) {
		this.fareStructure = fareStructure;
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