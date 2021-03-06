package product.clicklabs.jugnoo.datastructure;

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
	ORDER_DISPATCH(51),
	STATUS_CHANGED(52),
	CHAT_MESSAGE(53),
	MENUS_STATUS(54),
	MENUS_STATUS_SILENT(55),
	CHANGE_PORT(60),
	PAYMENT_RECEIVED(70),
    DRIVER_ARRIVED(72),
    EMERGENCY_CONTACT_VERIFIED(80),
	OTP_VERIFIED_BY_CALL(81),
	CLEAR_ALL_MESSAGE(82),
	DELETE_NOTIFICATION_ID(83),
	INITIATE_PAYTM_RECHARGE(84),
	CUSTOMER_EMERGENCY_LOCATION(86),
	SYNC_PARA(87),
	UPDATE_POOL_RIDE_STATUS(88),
	REFRESH_PAY_DATA(90),
	BID_RECEIVED(92),
	MPESA_PAYMENT_SUCCESS(93),
	MPESA_PAYMENT_FAILURE(94),
	UPLOAD_CONTACTS_ERROR(103),
	DRIVER_ETA(104),
	PROS_STATUS_SILENT(56),
	SHOW_NOTIFICATION_WITH_DEEPLINK(57),
	UNLOCK_BLE_DEVICE(180),
	NO_DRIVER_FOUND_HELP(113),
	END_STOP(115);

	private int ordinal;

	PushFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
	public int getKOrdinal() {
		return ordinal;
	}
}
