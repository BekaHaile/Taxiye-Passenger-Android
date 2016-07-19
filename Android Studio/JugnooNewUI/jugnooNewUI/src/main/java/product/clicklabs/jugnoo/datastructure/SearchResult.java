package product.clicklabs.jugnoo.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

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
	@SerializedName("placeId")
	@Expose
	private String placeId;
	
	public SearchResult(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
		this.thirdPartyAttributions = null;
		time = System.currentTimeMillis();
	}

	public SearchResult(String name, String address, String placeId){
		this.name = name;
		this.address = address;
		this.placeId = placeId;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if(((SearchResult)o).name.equalsIgnoreCase(this.name)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
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

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

}