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
	
	int userId;
	String name, image, carImage, phoneNumber;
	LatLng latLng;
	
	public DriverInfo(int userId, double latitude, double longitude){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
	}
	
	public DriverInfo(int userId, double latitude, double longitude, String name, String image, String carImage, String phoneNumber){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
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



class UserData{
	String accessToken, userName, userImage;
	
	public UserData(String accessToken, String userName, String userImage){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
	}
}


enum PassengerScreenMode{
	P_INITIAL, P_ASSIGNING, P_SEARCH, P_BEFORE_REQUEST_FINAL, P_REQUEST_FINAL, P_IN_RIDE, P_RIDE_END
}

enum UserMode{
	PASSENGER, DRIVER
}

enum DriverScreenMode{
	D_INITIAL, D_REQUEST_ACCEPT, D_START_RIDE, D_IN_RIDE , D_RIDE_END
}
