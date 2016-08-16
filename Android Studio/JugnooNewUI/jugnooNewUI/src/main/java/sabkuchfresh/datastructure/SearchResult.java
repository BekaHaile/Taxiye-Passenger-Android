package sabkuchfresh.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SearchResult implements Serializable {
	
	public String name, address;
	public LatLng latLng;
	private CharSequence thirdPartyAttributions;
	
	public SearchResult(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
		this.thirdPartyAttributions = null;
	}

	public void setThirdPartyAttributions(CharSequence thirdPartyAttributions){
		this.thirdPartyAttributions = thirdPartyAttributions;
	}

	public CharSequence getThirdPartyAttributions(){
		return thirdPartyAttributions;
	}
	
}