package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;

public class EndRideData {
	
	public String engagementId, driverName, driverCarNumber, driverImage,
		pickupAddress, dropAddress,
		pickupTime, dropTime;
	public double fare, luggageCharge, convenienceCharge, discount, paidUsingWallet, toPay,
		distance, rideTime, waitTime, baseFare, fareFactor, finalFare;
	public double paidUsingPaytm;
	public int waitingChargesApplicable;
	public ArrayList<DiscountType> discountTypes;
	private String rideDate, phoneNumber, tripTotal;
	private int vehicleType;
	private VehicleIconSet vehicleIconSet;
	
	public EndRideData(String engagementId, String driverName, String driverCarNumber, String driverImage,
					   String pickupAddress, String dropAddress, String pickupTime, String dropTime,
			double fare, double luggageCharge, double convenienceCharge, double discount, double paidUsingWallet,
					   double toPay, double distance, double rideTime, double waitTime, double baseFare, double fareFactor,
					   ArrayList<DiscountType> discountTypes, int waitingChargesApplicable, double paidUsingPaytm,
					   String rideDate, String phoneNumber, String tripTotal, int vehicleType, String iconSet){
		this.engagementId = engagementId;
		this.driverName = driverName;
		this.driverCarNumber = driverCarNumber.toUpperCase(Locale.ENGLISH);
		this.driverImage = driverImage;
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.pickupTime = pickupTime;
		this.dropTime = dropTime;

		this.fare = fare;
		this.luggageCharge = luggageCharge;
		this.convenienceCharge = convenienceCharge;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		this.paidUsingPaytm = paidUsingPaytm;
		
		this.distance = distance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
		this.baseFare = baseFare;
        this.fareFactor = fareFactor;
		this.discountTypes = new ArrayList<>();
		this.discountTypes.addAll(discountTypes);
		discountTypes.clear();

		if(this.rideTime > -1){
			if(this.waitTime > -1){
				this.rideTime = this.rideTime + this.waitTime;
			}
		}
		this.waitingChargesApplicable = waitingChargesApplicable;
		if(this.waitingChargesApplicable == 0 && this.waitTime > 0){
			this.waitingChargesApplicable = 1;
		}

		this.finalFare = this.fare + this.luggageCharge + this.convenienceCharge - this.discount;

		this.rideDate = rideDate;
		this.phoneNumber = phoneNumber;
		this.tripTotal = tripTotal;
		this.vehicleType = vehicleType;
		this.vehicleIconSet = new HomeUtil().getVehicleIconSet(iconSet);
	}


	public void setDriverNameCarName(String driverName, String driverCarNumber){
		this.driverName = driverName;
		this.driverCarNumber = driverCarNumber;
	}

	public String getRideDate() {
		return rideDate;
	}

	public void setRideDate(String rideDate) {
		this.rideDate = rideDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTripTotal() {
		return tripTotal;
	}

	public void setTripTotal(String tripTotal) {
		this.tripTotal = tripTotal;
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
}
