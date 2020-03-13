package product.clicklabs.jugnoo.home;

import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import org.json.JSONObject;

import product.clicklabs.jugnoo.promotion.models.Promo;

public interface AppInterruptHandler {
	
	void refreshDriverLocations();
	
	void onChangeStatePushReceived();
	
	void onCancelCompleted();
	
	void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	void onNoDriversAvailablePushRecieved(String logMessage, int requestType);
	
	void startRideForCustomer(int flag, String message, PlaceOrderResponse.ReferralPopupContent popupContent);
	
	void customerEndRideInterrupt(String engagementId, Promo promo);
	
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

	void onNoDriverHelpPushReceived(JSONObject jsonObject);
}
