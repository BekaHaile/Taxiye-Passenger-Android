package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import product.clicklabs.jugnoo.Data;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber;
	public LatLng latLng;
	public int freeRide;
	
	public String promoName = Data.NO_PROMO_APPLIED;
	private String eta = "10";
	
	public DriverInfo(String userId){
		this.userId = userId;
	}
	
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = freeRide;
	}
	
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, String promoName, String eta){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = freeRide;
		if(!"".equalsIgnoreCase(promoName)){
			this.promoName = promoName;
		}
		if(!"".equalsIgnoreCase(eta)){
			setEta(eta);
		}
	}
	
	public DriverInfo(String userId, String name, String image, String carImage, String carNumber){
		this.userId = userId;
		this.latLng = new LatLng(0, 0);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = "";
		this.rating = "4";
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = 0;
	}

	public void setEta(String eta){
        try {
            if(Integer.parseInt(eta) < 10){
                eta = "0"+eta;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.eta = eta;
	}

	public String getEta(){
		return this.eta;
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
