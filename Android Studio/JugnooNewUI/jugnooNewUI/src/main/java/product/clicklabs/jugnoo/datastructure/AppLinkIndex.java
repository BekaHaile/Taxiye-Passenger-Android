package product.clicklabs.jugnoo.datastructure;

public enum AppLinkIndex {

	INVITE_AND_EARN(0), // opens Invite and Earn Screen

	JUGNOO_CASH(1), // opens Jugnoo Cash Screen

	PROMOTIONS(2), // opens Promotions Screen

	RIDE_HISTORY(3), // opens Ride History Screen

	FARE_DETAILS(4), // opens Fare Details Screen

	SUPPORT(5), // opens Support Screen

	ABOUT(6), // opens About Screen

	ACCOUNT(7), // opens Account Screen

	NOTIFICATION_CENTER(8), // opens Notification center Screen,

	GAME_PAGE(9), // opens Game Screen (eg. T20)

	FRESH_PAGE(10) // opens Fresh Screen

	;

	private int ordinal;

	AppLinkIndex(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
