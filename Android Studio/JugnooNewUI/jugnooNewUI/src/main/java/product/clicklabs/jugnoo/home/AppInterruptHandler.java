package product.clicklabs.jugnoo.home;

import org.json.JSONObject;

public interface AppInterruptHandler {
	
	void refreshDriverLocations();
	
	void onChangeStatePushReceived();
	
	void onCancelCompleted();
	
	void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	void onNoDriversAvailablePushRecieved(String logMessage);
	
	void startRideForCustomer(final int flag, String message);
	
	void customerEndRideInterrupt(String engagementId);
	
	void onAfterRideFeedbackSubmitted(int givenRating, boolean skipped);
	
	void onJugnooCashAddedByDriver(double jugnooBalance, String message);

    void onDriverArrived(String logMessage);

    void refreshOnPendingCallsDone();

    void onEmergencyContactVerified(int emergencyContactId);

	void showDialog(String message);

	void onPaytmRechargePush(JSONObject jObj);

	void onShowDialogPush();

	void onDisplayMessagePushReceived();
}
