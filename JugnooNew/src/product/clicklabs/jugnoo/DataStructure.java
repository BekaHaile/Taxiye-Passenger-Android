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
	
	String userId, name, image, carImage, phoneNumber, rating;
	LatLng latLng;
	
	String distanceToReach, durationToReach;
	
	public DriverInfo(String userId, double latitude, double longitude){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
	}
	
	public DriverInfo(String userId, double latitude, double longitude, String name, String image, String carImage, String phoneNumber, String rating){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
	}
	
	@Override
	public String toString() {
		return  "Name: " + name + "\n" +
				"Phone Number: "+ phoneNumber;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverInfo)o).userId.equalsIgnoreCase(this.userId)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
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
	
	String engagementId, customerId, startTime, address;
	LatLng latLng;
	
	public DriverRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address){
		this.engagementId = engagementId;
		this.customerId = customerId;
		this.latLng = latLng;
		this.startTime = startTime;
		this.address = address;
	}
	
	@Override
	public String toString() {
		return engagementId + " " + customerId + " " + latLng + " " + startTime;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverRideRequest)o).engagementId.equalsIgnoreCase(engagementId)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return super.equals(o);
	}
	
}

class RideInfo{
	
	String id, fromLocation, toLocation, fare, distance, time;
	
	public RideInfo(String id, String fromLocation, String toLocation, String fare, String distance, String time){
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.fare = fare;
		this.distance = distance;
		this.time = time;
	}
	
}

class MissedRideInfo{
	String engagementId, pickupLocationAddress, timestamp, customerName, customerDistance;
	public MissedRideInfo(String engagementId, String pickupLocationAddress, String timestamp, String customerName, String customerDistance){
		this.engagementId = engagementId;
		this.pickupLocationAddress = pickupLocationAddress;
		this.timestamp = timestamp;
		this.customerName = customerName;
		this.customerDistance = customerDistance;
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




enum ApiResponseFlags {
	
	PARAMETER_MISSING(100),
	INVALID_ACCESS_TOKEN(101),
	ERROR_IN_EXECUTION(102),
	ASSIGNING_DRIVERS(105), 
	NO_DRIVERS_AVAILABLE(106),
	RIDE_ACCEPTED(107),
	RIDE_ACCEPTED_BY_OTHER_DRIVER(108),
	REQUEST_REJECTED(109),
	REQUEST_TIMEOUT(110),
	REQUEST_CANCELLED(111),
	SESSION_TIMEOUT(112),
	RIDE_STARTED(113),
	RIDE_ENDED(114),
	WAITING(115)
	;

	private int ordinal;

	private ApiResponseFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}



enum PushFlags {
	REQUEST(0), 
	REQUEST_TIMEOUT(1),
	REQUEST_CANCELLED(2),
	RIDE_STARTED(3),
	RIDE_ENDED(4),
	RIDE_ACCEPTED(5),
	RIDE_ACCEPTED_BY_OTHER_DRIVER(6),
	RIDE_REJECTED_BY_DRIVER(7),
	NO_DRIVERS_AVAILABLE(8),
	WAITING_STARTED(9),
	WAITING_ENDED(10);

	private int ordinal;

	private PushFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
