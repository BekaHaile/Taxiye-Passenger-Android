package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.utils.MapUtils;

/**
 * Created by clicklabs on 6/13/15.
 */
public class RidePath {

    public long ridePathId;
    public double sourceLatitude;
    public double sourceLongitude;
    public double destinationLatitude;
    public double destinationLongitude;

    public RidePath(long ridePathId, double sourceLatitude, double sourceLongitude, double destinationLatitude, double destinationLongitude) {
        this.ridePathId = ridePathId;
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    public LatLng getSourceLatLng(){
        return new LatLng(sourceLatitude, sourceLongitude);
    }

    public LatLng getDestinationLatLng(){
        return new LatLng(destinationLatitude, destinationLongitude);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RidePath && ridePathId == ((RidePath) obj).ridePathId && MapUtils.distance(getSourceLatLng(), ((RidePath) obj).getSourceLatLng()) <= 10 && MapUtils.distance(getDestinationLatLng(), ((RidePath) obj).getDestinationLatLng()) <= 10;
    }
}
