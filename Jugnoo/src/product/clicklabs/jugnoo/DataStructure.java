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
	
	int sNo;
	String name;
	LatLng latLng;
	
	public FavoriteLocation(int sNo, String name, LatLng latLng){
		this.sNo = sNo;
		this.name = name;
		this.latLng = latLng;
	}
	
}


class DriverInfo{
	
	String userId, name, image, carImage, phoneNumber;
	LatLng latLng;
	
	String distanceToReach, durationToReach;
	
	public DriverInfo(String userId, double latitude, double longitude){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
	}
	
	public DriverInfo(String userId, double latitude, double longitude, String name, String image, String carImage, String phoneNumber){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public String toString() {
		return  "Name: " + name + "\n" +
				"PhoneNumber: "+ phoneNumber;
	}
	
	
}


class CustomerInfo{
	String userId, name, image, phoneNumber, rating;
	
	public CustomerInfo(String userId, String name, String image, String phoneNumber, String rating){
		this.userId = userId;
		this.name = name;
		this.image = image;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
	}
}



class FriendInfo implements Comparable<FriendInfo> {
	
	String fbId, fbName, fbImage;
	int flag;
	boolean tick;
	
	public FriendInfo(String fbId, String fbName, int flag){
		this.fbId = fbId;
		this.fbName = fbName;
		this.fbImage = "http://graph.facebook.com/"+fbId+"/picture?width=160&height=160";
		this.flag = flag;
		this.tick = false;
	}
	
	public FriendInfo(String fbId, String fbName, String image){
		this.fbId = fbId;
		this.fbName = fbName;
		this.fbImage = image;
		this.flag = 0;
		this.tick = false;
	}
	
	@Override
	public int compareTo(FriendInfo another) {
		return this.fbName.toLowerCase().compareTo(another.fbName.toLowerCase());
	}
	
}



class UserData{
	String accessToken, userName, userImage, id;
	
	public UserData(String accessToken, String userName, String userImage, String id){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.id = id;
	}
}



class DriverRideRequest{
	
	String engagementId, customerId;
	LatLng latLng;
	
	public DriverRideRequest(String engagementId, String customerId, LatLng latLng){
		this.engagementId = engagementId;
		this.customerId = customerId;
		this.latLng = latLng;
	}
	
	@Override
	public String toString() {
		return engagementId + " " + customerId + " " + latLng;
	}
	
}

class Booking{
	
	String id, fromLocation, toLocation, fare, distance, time;
	
	public Booking(String id, String fromLocation, String toLocation, String fare, String distance, String time){
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.fare = fare;
		this.distance = distance;
		this.time = time;
	}
	
}


class LanguageInfo{
	
	String name, displayName;
	boolean selected;
	
	public LanguageInfo(String name, String displayName, boolean selected){
		this.name = name;
		this.displayName = displayName;
		this.selected = selected;
	}
	
}


class DriverType{
	
	String driverTypeId, driverTypeName;
	boolean selected;
	
	public DriverType(String driverTypeId, String driverTypeName){
		this.driverTypeId = driverTypeId;
		this.driverTypeName = driverTypeName;
		this.selected = false;
	}
	
}




enum PassengerScreenMode{
	P_INITIAL, P_ASSIGNING, P_SEARCH, P_REQUEST_FINAL, P_IN_RIDE, P_RIDE_END
}

enum UserMode{
	PASSENGER, DRIVER
}

enum JugnooDriverMode{
	ON, OFF
}

enum DriverScreenMode{
	D_INITIAL, D_REQUEST_ACCEPT, D_START_RIDE, D_IN_RIDE , D_RIDE_END
}


enum AppMode{
	NORMAL, DEBUG
}

enum ExceptionalDriver{
	YES, NO
}


