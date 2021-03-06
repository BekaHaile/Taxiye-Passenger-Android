package product.clicklabs.jugnoo.driver.datastructure;

public enum PushFlags {
	REQUEST(0), 
	REQUEST_TIMEOUT(1),
	REQUEST_CANCELLED(2),
	RIDE_STARTED(3),
	RIDE_ENDED(4),
	RIDE_ACCEPTED(5),
	RIDE_ACCEPTED_BY_OTHER_DRIVER(6),
	RIDE_REJECTED_BY_DRIVER(7),
	NO_DRIVERS_AVAILABLE(8),
	WAITING_STARTED(9),
	WAITING_ENDED(10),
	CHANGE_STATE(20),
	DISPLAY_MESSAGE(21),
	TOGGLE_LOCATION_UPDATES(22),
	MANUAL_ENGAGEMENT(23),
	HEARTBEAT(40),
	STATION_CHANGED(50),
	CHANGE_PORT(60),
	UPDATE_CUSTOMER_BALANCE(71)
	;

	private int ordinal;

	private PushFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
