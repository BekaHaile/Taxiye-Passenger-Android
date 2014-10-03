package product.clicklabs.jugnoo;

import com.google.android.gms.maps.model.LatLng;

public interface AppInterruptHandler {

	public void customerEndRideInterrupt();
	
	public void startRideForCustomer(final int flag);
	
	public void changeRideRequest(String dEngagementId, String dCustomerId, LatLng rideLatLng, boolean add);
	
	public void driverStartRideInterrupt();
	
	public void refreshDriverLocations();
	
	
	public void requestRideInterrupt(int switchCase);
	
	public void apiStart(final int driverPos);
	
	public void apiEnd();
	
	public void apiInterrupted();
	
	public void onNoDriversAvailablePushRecieved(String logMessage);
	
}
