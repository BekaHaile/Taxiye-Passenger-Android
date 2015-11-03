package product.clicklabs.jugnoo.datastructure;

public class RideInfo {

	public String pickupAddress;
	public String dropAddress;
	public double amount;
	public double distance;
	public double rideTime, waitTime;
	public String date;
	public int isRatedBefore;
	public int driverId;
	public int engagementId;
	public int isCancelledRide;

	public RideInfo(String pickupAddress, String dropAddress, double amount, double distance, double rideTime, double waitTime, String date,
					int isRatedBefore, int driverId, int engagementId, int isCancelledRide) {
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.amount = amount;
		this.distance = distance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
		this.date = date;
		this.isRatedBefore = isRatedBefore;
		this.driverId = driverId;
		this.engagementId = engagementId;

		if(this.waitTime > -1) {
			this.rideTime = this.rideTime + this.waitTime;
		}
		this.isCancelledRide = isCancelledRide;
	}

	@Override
	public String toString() {
		return pickupAddress + " " + dropAddress + " " + amount + " " + distance + " " + date;
	}

}
