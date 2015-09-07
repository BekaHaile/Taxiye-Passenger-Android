package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

public class EndRideData {
	
	public String engagementId, 
		pickupAddress, dropAddress,
		pickupTime, dropTime;
	public double fare, luggageCharge, convenienceCharge, discount, paidUsingWallet, toPay,
		distance, rideTime, baseFare, fareFactor;
	public ArrayList<DiscountType> discountTypes;
	
	public EndRideData(String engagementId, String pickupAddress, String dropAddress, String pickupTime, String dropTime,
			double fare, double luggageCharge, double convenienceCharge, double discount, double paidUsingWallet, double toPay, double distance, double rideTime, double baseFare, double fareFactor,
					   ArrayList<DiscountType> discountTypes){
		this.engagementId = engagementId;
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
		this.baseFare = baseFare;
        this.fareFactor = fareFactor;
		this.discountTypes = new ArrayList<>();
		this.discountTypes.addAll(discountTypes);
		discountTypes.clear();
	}
	
}
