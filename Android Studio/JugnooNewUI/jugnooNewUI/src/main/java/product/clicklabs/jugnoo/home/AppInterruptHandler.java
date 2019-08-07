package product.clicklabs.jugnoo.home;

import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import org.json.JSONObject;

public interface AppInterruptHandler {
	
	void refreshDriverLocations();
	
	void onChangeStatePushReceived();
	
	void onCancelCompleted();
	
	void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	void onNoDriversAvailablePushRecieved(String logMessage);
	
	void startRideForCustomer(int flag, String message, PlaceOrderResponse.ReferralPopupContent popupContent);
	
	void customerEndRideInterrupt(String engagementId);
	
	void onAfterRideFeedbackSubmitted(int givenRating);
	
	void onJugnooCashAddedByDriver(double jugnooBalance, String message);

    void onDriverArrived(String logMessage);

    void refreshOnPendingCallsDone();

    void onEmergencyContactVerified(int emergencyContactId);

	void showDialog(String message);

	void onShowDialogPush();

	void onDisplayMessagePushReceived();

	void onUpdatePoolRideStatus(JSONObject jsonObject);

	void updateGpsLockStatus(int gpsLockStatus);
}
