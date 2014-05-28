package product.clicklabs.jugnoo;

import com.google.android.gms.maps.model.LatLng;

public interface DriverChangeRideRequest {

	public void changeRideRequest(String dEngagementId, String dCustomerId, LatLng rideLatLng, boolean add);
	
}
