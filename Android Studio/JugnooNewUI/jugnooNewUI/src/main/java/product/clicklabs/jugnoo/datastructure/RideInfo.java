package product.clicklabs.jugnoo.datastructure;

public class RideInfo {
	
	public String pickupAddress;
	public String dropAddress;
	public double amount;
	public double distance;
	public double rideTime;
	public String date;
    public int isRatedBefore;
	public int driverId;
	public int engagementId;

	public RideInfo(String pickupAddress, String dropAddress, double amount, double distance, double rideTime, String date, int isRatedBefore, int driverId, int engagementId){
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.amount = amount;
		this.distance = distance;
		this.rideTime = rideTime;
		this.date = date;
        this.isRatedBefore = isRatedBefore;
        this.driverId = driverId;
		this.engagementId = engagementId;
	}
	
	@Override
	public String toString() {
		return pickupAddress + " " + dropAddress + " " + amount + " " + distance + " " + date;
	}
	
}
