package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SearchResult implements Serializable{
	
	public String name, address;
	private String attr;
	public LatLng latLng;
	
	public SearchResult(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
		this.attr = "";
	}

	public void setAttr(CharSequence attr){
		if(attr == null){
			this.attr = "";
		}
		else{
			this.attr = attr.toString();
		}
	}

	public String getAttr(){
		return attr;
	}
	
}