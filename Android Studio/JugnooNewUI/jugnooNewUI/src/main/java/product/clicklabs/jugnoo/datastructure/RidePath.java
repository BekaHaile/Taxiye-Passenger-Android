package product.clicklabs.jugnoo.datastructure;

/**
 * Created by clicklabs on 6/13/15.
 */
public class RidePath {

    public int ridePathId;
    public double sourceLatitude;
    public double sourceLongitude;
    public double destinationLatitude;
    public double destinationLongitude;

    public RidePath(int ridePathId, double sourceLatitude, double sourceLongitude, double destinationLatitude, double destinationLongitude) {
        this.ridePathId = ridePathId;
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

}
