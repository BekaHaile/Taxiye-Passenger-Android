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
	
	String distanceToReach = "0", durationToReach = "";
	
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
	
	String engagementId, customerId, address, startTime;
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
	
	@Override
	public String toString() {
		return fromLocation + " " + toLocation + " " + fare + " " + distance + " " + time;
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
	RIDE_CANCELLED_BY_DRIVER(109),
	REQUEST_REJECTED(110),
	REQUEST_TIMEOUT(111),
	REQUEST_CANCELLED(112),
	SESSION_TIMEOUT(113),
	RIDE_STARTED(114),
	RIDE_ENDED(115),
	WAITING(116),
	USER_OFFLINE(130),
	NO_ACTIVE_SESSION(131),
	ENGAGEMENT_DATA(132),
	ACTIVE_REQUESTS(133),
	COUPONS(140)
	;

	private int ordinal;

	private ApiResponseFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}






enum EngagementStatus {
	
	REQUESTED(0),
	// request has been sent
	ACCEPTED(1),
	// request has been accepted by the driver
	STARTED(2),
	// ride has started
	ENDED(3),
	// ride has ended
	REJECTED_BY_DRIVER(4),
	// request rejected by driver
	CANCELLED_BY_CUSTOMER(5),
	 // request cancelled by customer
	TIMEOUT(6),
	// request timed out
	ACCEPTED_BY_OTHER_DRIVER(7),
	// request was accepted by another driver
	ACCEPTED_THEN_REJECTED(8),
	// request was accepted and then rejected
	CLOSED(9),
	// request was closed when the driver accepted other request
	CANCELLED_ACCEPTED_REQUEST(10);
	// request was cancelled after it was accepted by a driver

	
	
	private int ordinal;

	private EngagementStatus(int ordinal) {
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
	WAITING_ENDED(10),
	CHANGE_STATE(20)
	;

	private int ordinal;

	private PushFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}


enum CouponStatus {
	EXPIRED(0), 
	ACTIVE(1),
	REDEEMED(2)
	;

	private int ordinal;

	private CouponStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}


class CouponInfo{
	
	int type, status;
	String title, subtitle, description, image, redeemedOn, expiryDate;
	double discountPrecent, maximumDiscountableValue;
	int count;
	boolean enabled;
	
	public CouponInfo(int type, int status, String title, String subtitle, String description, String image, 
			String redeemedOn, String expiryDate, double discountPrecent, double maximumDiscountableValue){
		this.type = type;
		this.status = status;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.image = image;
		this.redeemedOn = redeemedOn;
		this.expiryDate = expiryDate;
		this.discountPrecent = discountPrecent;
		this.maximumDiscountableValue = maximumDiscountableValue;
		this.count = 1;
		this.enabled = true;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((CouponInfo)o).type == this.type){
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

