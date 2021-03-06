package product.clicklabs.jugnoo.driver;

import org.json.JSONObject;

public interface AppInterruptHandler {
	
	public void onNewRideRequest();
	
	public void onCancelRideRequest(String engagementId, boolean acceptedByOtherDriver);
	
	public void onRideRequestTimeout(String engagementId);
	
	public void onManualDispatchPushReceived();
	
	public void onStationChangedPushReceived();
	
	public void onCustomerCashDone();
	
	public void onCashAddedToWalletByCustomer(String userId, double balance);
	
	
	
	
	public void refreshDriverLocations();
	
	public void onChangeStatePushReceived();
	
	public void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	public void onNoDriversAvailablePushRecieved(String logMessage);
	
	public void startRideForCustomer(final int flag);
	
	public void customerEndRideInterrupt(JSONObject jObj);
	
	
}
