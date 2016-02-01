package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.utils.Utils;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber;
	public LatLng latLng;
	public int freeRide;
	
	public String promoName = Data.NO_PROMO_APPLIED;
	private String eta = "10";
	private double fareFixed;
	private int preferredPaymentMode;
	private double bearing;
	
	public DriverInfo(String userId){
		this.userId = userId;
	}

	//for drivers to show in free state
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, double bearing){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = freeRide;
		this.bearing = bearing;
	}

	//for engagement
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, String promoName, String eta, double fareFixed, int preferredPaymentMode){
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
		this.fareFixed = fareFixed;
		this.preferredPaymentMode = preferredPaymentMode;
	}

	//for last ride data
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
//        try {
//            if(Integer.parseInt(eta) < 10){
//                eta = "0"+eta;
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
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
		} catch(Exception e){
		}
		return false;
	}


	public double getFareFixed() {
		return fareFixed;
	}

	public String getFareFixedStr(){
		return Utils.getMoneyDecimalFormat().format(getFareFixed());
	}

	public int getPreferredPaymentMode() {
		return preferredPaymentMode;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}
}
