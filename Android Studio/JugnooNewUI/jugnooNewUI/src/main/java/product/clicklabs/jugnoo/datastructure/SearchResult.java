package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SearchResult implements Serializable{
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("address")
	@Expose
	private String address;
	@SerializedName("latLng")
	@Expose
	private LatLng latLng;
	@SerializedName("thirdPartyAttributions")
	@Expose
	private CharSequence thirdPartyAttributions;
	@SerializedName("time")
	@Expose
	private long time;
	
	public SearchResult(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
		this.thirdPartyAttributions = null;
		time = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setThirdPartyAttributions(CharSequence thirdPartyAttributions){
		this.thirdPartyAttributions = thirdPartyAttributions;
	}

	public CharSequence getThirdPartyAttributions(){
		return thirdPartyAttributions;
	}
	
}