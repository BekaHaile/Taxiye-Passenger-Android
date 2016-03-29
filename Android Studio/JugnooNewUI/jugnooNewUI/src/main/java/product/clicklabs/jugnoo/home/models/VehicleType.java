package product.clicklabs.jugnoo.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

	private FareStructure fareStructure;
	private String eta;

	public VehicleType(Integer id, String name){
		this.id = id;
		this.name = name;
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
}