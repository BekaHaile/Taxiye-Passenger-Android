package product.clicklabs.jugnoo;

import com.google.android.gms.maps.model.LatLng;

public interface DriverGetRequestPush {

	public void showRideRequest(String dEngagementId, String dCustomerId, LatLng rideLatLng);
	
}
