package product.clicklabs.jugnoo;

import com.google.android.gms.maps.model.LatLng;

public class DataStructure {

}

class SearchResult{
	
	String name, address;
	LatLng latLng;
	
	public SearchResult(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
	}
	
}



class FavoriteLocation{
	
	String name, address;
	LatLng latLng;
	
	public FavoriteLocation(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
	}
	
}


class DriverInfo{
	
	String name, address;
	LatLng latLng;
	
	public DriverInfo(String name, String address, LatLng latLng){
		this.name = name;
		this.address = address;
		this.latLng = latLng;
	}
	
}



class FriendInfo{
	
	String name, image;
	boolean tick;
	
	public FriendInfo(String name, String image){
		this.name = name;
		this.image = image;
		this.tick = false;
	}
	
}

