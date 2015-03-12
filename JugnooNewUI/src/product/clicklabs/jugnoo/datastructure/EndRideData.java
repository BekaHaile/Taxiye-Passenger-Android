package product.clicklabs.jugnoo.datastructure;

public class EndRideData {
	
	public String engagementId, 
	pickupAddress, dropAddress, 
	pickupTime, dropTime;
	public double fare, discount, paidUsingWallet, toPay, 
	distance, rideTime;
	
	public EndRideData(String engagementId, String pickupAddress, String dropAddress, String pickupTime, String dropTime,
			double fare, double discount, double paidUsingWallet, double toPay, double distance, double rideTime){
		this.engagementId = engagementId;
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.pickupTime = pickupTime;
		this.dropTime = dropTime;
		
		this.fare = fare;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		
		this.distance = distance;
		this.rideTime = rideTime;
	}
	
}
