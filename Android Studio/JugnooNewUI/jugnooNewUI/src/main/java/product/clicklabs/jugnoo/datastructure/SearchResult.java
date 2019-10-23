package product.clicklabs.jugnoo.datastructure;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.utils.Utils;

public class SearchResult {
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("address")
	@Expose
	private String address;
	@SerializedName("placeId")
	@Expose
	private String placeId;
	@SerializedName("latitude")
	@Expose
	private Double latitude;
	@SerializedName("longitude")
	@Expose
	private Double longitude;
	@SerializedName("thirdPartyAttributions")
	@Expose
	private CharSequence thirdPartyAttributions;
	@SerializedName("time")
	@Expose
	private long time;
	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("is_confirmed")
	@Expose
	private Integer isConfirmed = 0;
	@SerializedName("freq")
	@Expose
	private Integer freq = 0;

	private Type type = Type.SEARCHED;
	
	public SearchResult(String name, String address, String placeId, double latitude, double longitude){
		this.name = name;
		this.address = address;
		this.placeId = placeId;
		this.latitude = latitude;
		this.longitude = longitude;

		thirdPartyAttributions = null;
		time = System.currentTimeMillis();
	}

	public SearchResult(String name, String address, String placeId, double latitude, double longitude, int id, int isConfirmed, int freq){
		this.name = name;
		this.address = address;
		this.placeId = placeId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.id = id;
		this.isConfirmed = isConfirmed;
		this.freq = freq;

		thirdPartyAttributions = null;
		time = System.currentTimeMillis();
	}

	public SearchResult(Integer id){
		this.id = id;
	}


	@Override
	public boolean equals(Object o) {
		try{
			return (((SearchResult)o).id.equals(this.id));
		} catch(Exception e){
			return false;
		}
	}

	public String getName() {
		if(!TextUtils.isEmpty(name) && ("home".equalsIgnoreCase(name.toLowerCase()) || "work".equalsIgnoreCase(name.toLowerCase()))){
			return Utils.firstCharCapital(name);
		}
		return getNameForText();
	}

	public String getNameForText() {
		if(!TextUtils.isEmpty(name)){
			return Utils.firstCharCapital(name);
		} else {
			return address;
		}
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
		if(latitude != null && longitude != null) {
			return new LatLng(latitude, longitude);
		} else{
			return new LatLng(Data.latitude, Data.longitude);
		}
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Integer getId() {
		if(id != null) {
			return id;
		} else{
			return 0;
		}
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(Integer isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}

	public enum Type{
		SEARCHED, LAST_SAVED, HOME, WORK, SAVED, RECENT, USED;
	}

	public boolean isRecentAddress(){
		return TextUtils.isEmpty(getName()) || getId() == 0;
	}

	public int getPlaceRequestCode(){
		if(name.equalsIgnoreCase(Constants.TYPE_HOME)){
			return Constants.REQUEST_CODE_ADD_HOME;
		} else if(name.equalsIgnoreCase(Constants.TYPE_WORK)){
			return Constants.REQUEST_CODE_ADD_WORK;
		} else {
			return Constants.REQUEST_CODE_ADD_NEW_LOCATION;
		}
	}

}