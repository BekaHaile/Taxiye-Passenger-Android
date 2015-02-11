package product.clicklabs.jugnoo.driver.datastructure;

public class RideInfo {
	
	public String id;
	public String fromLocation;
	public String toLocation;
	public String fare;
	public String customerPaid;
	public String balance;
	public String subsidy;
	public String distance;
	public String rideTime;
	public String waitTime;
	public String dateTime;
	public int couponUsed;
	
	public RideInfo(String id, String fromLocation, String toLocation, 
			String fare, String customerPaid, String balance, String subsidy, 
			String distance, String rideTime, String waitTime, String dateTime, 
			int couponUsed){
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		
		this.fare = fare;
		this.customerPaid = customerPaid;
		this.balance = balance;
		this.subsidy = subsidy;
		
		
		this.distance = distance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
		this.dateTime = dateTime;
		
		this.couponUsed = couponUsed;
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return id + " " + dateTime;
	}
	
}
