package product.clicklabs.jugnoo.datastructure;

/**
 * Created by clicklabs on 6/13/15.
 */
public class RidePath {

    public int ridePathId;
    public Double sourceLatitude;
    public Double sourceLongitude;
    public Double destinationLatitude;
    public Double destinationLongitude;

    public RidePath(int ridePathId, Double sourceLatitude,Double sourceLongitude, Double
            destinationLatitude, Double destinationLongitude) {
        this.ridePathId = ridePathId;
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    public int getRidePathId() {
        return ridePathId;
    }

    public void setRidePathId(int ridePathId) {
        this.ridePathId = ridePathId;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(Double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }


}
