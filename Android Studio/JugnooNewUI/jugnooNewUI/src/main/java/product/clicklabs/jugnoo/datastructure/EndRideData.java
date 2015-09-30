package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

public class EndRideData {
	
	public String engagementId, driverName, driverCarNumber,
		pickupAddress, dropAddress,
		pickupTime, dropTime;
	public double fare, luggageCharge, convenienceCharge, discount, paidUsingWallet, toPay,
		distance, rideTime, waitTime, baseFare, fareFactor;
	public int waitingChargesApplicable;
	public ArrayList<DiscountType> discountTypes;
	
	public EndRideData(String engagementId, String driverName, String driverCarNumber, String pickupAddress, String dropAddress, String pickupTime, String dropTime,
			double fare, double luggageCharge, double convenienceCharge, double discount, double paidUsingWallet,
					   double toPay, double distance, double rideTime, double waitTime, double baseFare, double fareFactor,
					   ArrayList<DiscountType> discountTypes, int waitingChargesApplicable){
		this.engagementId = engagementId;
		this.driverName = driverName;
		this.driverCarNumber = driverCarNumber;
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
	}
	
}
