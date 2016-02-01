package product.clicklabs.jugnoo.datastructure;

public class FareStructure {
	public double fixedFare;
	public double thresholdDistance;
	public double farePerKm;
	public double farePerMin;
	public double freeMinutes;
    public double farePerWaitingMin;
    public double fareThresholdWaitingTime;

    public double fareFactor;
	public double convenienceCharge;

    private boolean fromServer;


    public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes, double farePerWaitingMin, double fareThresholdWaitingTime,
						 double convenienceCharge, boolean fromServer){
        this.fixedFare = fixedFare;
        this.thresholdDistance = thresholdDistance;
        this.farePerKm = farePerKm;
        this.farePerMin = farePerMin;
        this.freeMinutes = freeMinutes;
        this.farePerWaitingMin = farePerWaitingMin;
        this.fareThresholdWaitingTime = fareThresholdWaitingTime;
        this.fareFactor = 1;
		this.convenienceCharge = convenienceCharge;

        this.fromServer = fromServer;
    }

    public double calculateFare(double totalDistanceInKm, double totalTimeInMin, double totalWaitTimeInMin){
        totalTimeInMin = totalTimeInMin - freeMinutes;
        if(totalTimeInMin < 0){
            totalTimeInMin = 0;
        }
        double fareOfRideTime = totalTimeInMin * farePerMin;

        totalWaitTimeInMin = totalWaitTimeInMin - fareThresholdWaitingTime;
        if(totalWaitTimeInMin < 0){
            totalWaitTimeInMin = 0;
        }
        double fareOfWaitTime = totalWaitTimeInMin * farePerWaitingMin;

        double fare = fareOfRideTime + fareOfWaitTime + fixedFare + ((totalDistanceInKm <= thresholdDistance) ? (0) : ((totalDistanceInKm - thresholdDistance) * farePerKm));
        fare = fare * fareFactor;
		fare = fare + convenienceCharge;
        fare = Math.round(fare);
        return fare;
    }

    public boolean getIsFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }
}
