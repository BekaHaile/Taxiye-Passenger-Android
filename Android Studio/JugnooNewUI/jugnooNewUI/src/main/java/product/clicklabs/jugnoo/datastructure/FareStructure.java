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
	public double convenienceCharges;


    public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes, double farePerWaitingMin, double fareThresholdWaitingTime,
						 double convenienceCharges){
        this.fixedFare = fixedFare;
        this.thresholdDistance = thresholdDistance;
        this.farePerKm = farePerKm;
        this.farePerMin = farePerMin;
        this.freeMinutes = freeMinutes;
        this.farePerWaitingMin = farePerWaitingMin;
        this.fareThresholdWaitingTime = fareThresholdWaitingTime;
        this.fareFactor = 1;
		this.convenienceCharges = convenienceCharges;
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
		fare = fare + convenienceCharges;
        fare = Math.round(fare);
        return fare;
    }

}
