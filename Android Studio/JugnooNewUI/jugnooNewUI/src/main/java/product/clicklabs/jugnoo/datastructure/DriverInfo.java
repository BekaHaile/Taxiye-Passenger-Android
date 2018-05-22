package product.clicklabs.jugnoo.datastructure;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.Utils;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber, brandingStatus;
	public LatLng latLng;
	public int freeRide;
	
	public String promoName = Data.NO_PROMO_APPLIED, cancelRideThrashHoldTime, poolRideStatusString;
	private String eta = "10";
	private double fareFixed;
	private int preferredPaymentMode, isPooledRide, chatEnabled;
	private double bearing;

	private Schedule scheduleT20;

	private int vehicleType, cancellationCharges;
	private ArrayList<Integer> regionIds = new ArrayList<>();
	private VehicleIconSet vehicleIconSet;
	private ArrayList<String> fellowRiders = new ArrayList<>();
	private int operatorId;
	private String currency;
	private int paymentMethod;

	public DriverInfo(String userId){
		this.userId = userId;
	}

	//for drivers to show in free state
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, double bearing, int vehicleType, ArrayList<Integer> regionIds, String brandingStatus, int operatorId,
					  int paymentMethod){
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
		this.brandingStatus = brandingStatus;
		this.operatorId = operatorId;
		this.paymentMethod = paymentMethod;
	}

	//for engagement
	public DriverInfo(String userId, double latitude, double longitude,
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, String promoName, String eta, double fareFixed, int preferredPaymentMode, Schedule scheduleT20,
					  int vehicleType, String iconSet, String cancelRideThrashHoldTime, int cancellationCharges, int isPooledRide,
					  String poolRideStatusString, ArrayList<String> fellowRiders, double bearing, int chatEnabled, int operatorId, String currency){
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
		this.chatEnabled = chatEnabled;
		this.operatorId = operatorId;
		this.currency = currency;
	}

	//for last ride data
	public DriverInfo(String userId, String name, String image, String carImage, String carNumber, int operatorId){
		this.userId = userId;
		this.latLng = new LatLng(0, 0);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = "";
		this.rating = "4";
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = 0;
		this.operatorId = operatorId;
	}

	public void setEta(String eta){
        this.eta = eta;
	}

	public String getEta(){
		return this.eta;
	}



	@Override
	public String toString() {
		return  "Id: " + userId + "\n" +
				"Branding Status: "+ brandingStatus;
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

	public void setVehicleIconSet(String iconSet) {
		this.vehicleIconSet = new HomeUtil().getVehicleIconSet(iconSet);
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


	public int getChatEnabled() {
		return chatEnabled;
	}

	public void setChatEnabled(int chatEnabled) {
		this.chatEnabled = chatEnabled;
	}

	public Bitmap getMarkerBitmap(Activity activity, ASSL assl){
		if(vehicleIconSet == VehicleIconSet.ERICKSHAW){
			return CustomMapMarkerCreator.createMarkerBitmapForResource(activity, assl,
					vehicleIconSet.getIconMarker(), 34f, 52f);
		} else {
			return CustomMapMarkerCreator
					.createMarkerBitmapForResource(activity, assl, vehicleIconSet.getIconMarker());
		}
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(int paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public enum PaymentMethod{
		CASH(1),
		BOTH(2); // default

		private int ordinal;
		PaymentMethod(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}
}
