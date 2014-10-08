package product.clicklabs.jugnoo;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public interface AppInterruptHandler {
	
	public void onNewRideRequest();
	
	public void onCancelRideRequest(boolean acceptedByOtherDriver);
	
	public void onRideRequestTimeout();
	
	
	
	public void refreshDriverLocations();
	
	public void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	public void onNoDriversAvailablePushRecieved(String logMessage);
	
	public void startRideForCustomer(final int flag);
	
	public void customerEndRideInterrupt();
	
}
