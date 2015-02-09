package product.clicklabs.jugnoo.driver.datastructure;

public class RideInfo {
	
	public String id;
	public String fromLocation;
	public String toLocation;
	public String fare;
	public String distance;
	public String dateTime;
	public String balance;
	public int couponUsed;
	
	public RideInfo(String id, String fromLocation, String toLocation, String fare, String distance, String time, 
			String balance, int couponUsed){
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.fare = fare;
		this.distance = distance;
		this.dateTime = time;
		this.couponUsed = couponUsed;
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return fromLocation + " " + toLocation + " " + fare + " " + distance + " " + dateTime;
	}
	
}
