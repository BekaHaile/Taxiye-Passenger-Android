package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.utils.Utils;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber;
	public LatLng latLng;
	public int freeRide;
	
	public String promoName = Data.NO_PROMO_APPLIED, cancelRideThrashHoldTime, poolRideStatusString;
	private String eta = "10";
	private double fareFixed;
	private int preferredPaymentMode, isPooledRide;
	private double bearing;

	private Schedule scheduleT20;

	private int vehicleType, cancellationCharges;
	private ArrayList<Integer> regionIds = new ArrayList<>();
	private VehicleIconSet vehicleIconSet;
	private ArrayList<String> fellowRiders = new ArrayList<>();
	
	public DriverInfo(String userId){
		this.userId = userId;
	}

	//for drivers to show in free state
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, double bearing, int vehicleType, ArrayList<Integer> regionIds){
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
		this.vehicleType = vehicleType;
		this.regionIds = regionIds;
	}

	//for engagement
	public DriverInfo(String userId, double latitude, double longitude,
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, String promoName, String eta, double fareFixed, int preferredPaymentMode, Schedule scheduleT20,
					  int vehicleType, String iconSet, String cancelRideThrashHoldTime, int cancellationCharges, int isPooledRide,
					  String poolRideStatusString, ArrayList<String> fellowRiders, double bearing){
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
		this.scheduleT20 = scheduleT20;
		this.vehicleType = vehicleType;
		this.vehicleIconSet = new HomeUtil().getVehicleIconSet(iconSet);
		this.cancelRideThrashHoldTime = cancelRideThrashHoldTime;
		this.cancellationCharges = cancellationCharges;
		this.isPooledRide = isPooledRide;
		this.poolRideStatusString = poolRideStatusString;
		this.fellowRiders = fellowRiders;
		this.bearing = bearing;
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

	public Schedule getScheduleT20() {
		return scheduleT20;
	}

	public void setScheduleT20(Schedule scheduleT20) {
		this.scheduleT20 = scheduleT20;
	}

	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}

	public VehicleIconSet getVehicleIconSet() {
		return vehicleIconSet;
	}

	public void setVehicleIconSet(VehicleIconSet vehicleIconSet) {
		this.vehicleIconSet = vehicleIconSet;
	}

	public ArrayList<Integer> getRegionIds() {
		return regionIds;
	}

	public void setRegionIds(ArrayList<Integer> regionIds) {
		this.regionIds = regionIds;
	}

	public String getCancelRideThrashHoldTime() {
		return cancelRideThrashHoldTime;
	}

	public void setCancelRideThrashHoldTime(String cancelRideThrashHoldTime) {
		this.cancelRideThrashHoldTime = cancelRideThrashHoldTime;
	}

	public int getCancellationCharges() {
		return cancellationCharges;
	}

	public void setCancellationCharges(int cancellationCharges) {
		this.cancellationCharges = cancellationCharges;
	}

	public int getIsPooledRide() {
		return isPooledRide;
	}

	public void setIsPooledRide(int isPooledRide) {
		this.isPooledRide = isPooledRide;
	}

	public String getPoolRideStatusString() {
		return poolRideStatusString;
	}

	public void setPoolRideStatusString(String poolRideStatusString) {
		this.poolRideStatusString = poolRideStatusString;
	}

	public ArrayList<String> getFellowRiders() {
		return fellowRiders;
	}

	public void setFellowRiders(ArrayList<String> fellowRiders) {
		this.fellowRiders = fellowRiders;
	}


}
