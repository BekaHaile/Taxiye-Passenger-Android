package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating;
	public LatLng latLng;
	
	public String distanceToReach = "0", durationToReach = "";
	
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
