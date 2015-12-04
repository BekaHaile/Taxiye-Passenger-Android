package product.clicklabs.jugnoo;

import org.json.JSONObject;

public interface AppInterruptHandler {
	
	void refreshDriverLocations();
	
	void onChangeStatePushReceived();
	
	void onCancelCompleted();
	
	void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	void onNoDriversAvailablePushRecieved(String logMessage);
	
	void startRideForCustomer(final int flag);
	
	void customerEndRideInterrupt(String engagementId);
	
	void onAfterRideFeedbackSubmitted(int givenRating, boolean skipped);
	
	void onJugnooCashAddedByDriver(double jugnooBalance, String message);

    void onDriverArrived(String logMessage);

    void refreshOnPendingCallsDone();

    void onEmergencyContactVerified(int emergencyContactId);

	void showDialog(String message);

}
