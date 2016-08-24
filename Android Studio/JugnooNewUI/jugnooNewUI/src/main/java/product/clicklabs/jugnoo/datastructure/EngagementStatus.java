package product.clicklabs.jugnoo.datastructure;

public enum EngagementStatus {
	
	REQUESTED(0),
	// request has been sent
	ACCEPTED(1),
	// request has been accepted by the driver
	STARTED(2),
	// ride has started
	ENDED(3),
	// ride has ended
	REJECTED_BY_DRIVER(4),
	// request rejected by driver
	CANCELLED_BY_CUSTOMER(5),
	 // request cancelled by customer
	TIMEOUT(6),
	// request timed out
	ACCEPTED_BY_OTHER_DRIVER(7),
	// request was accepted by another driver
	ACCEPTED_THEN_REJECTED(8),
	// request was accepted and then rejected
	CLOSED(9),
	// request was closed when the driver accepted other request
	CANCELLED_ACCEPTED_REQUEST(10),
	// request was cancelled after it was accepted by a driver
    ARRIVED(14),
	// ride cancelled by customer
	RIDE_CANCELLED_BY_CUSTOMER(13)

	;


//	"REQUESTED",                   0
//			"ACCEPTED",                    1
//			"STARTED",                     2
//			"ENDED",                       3
//			"REJECTED_BY_DRIVER",          4
//			"CANCELLED_BY_CUSTOMER",       5
//			"TIMEOUT",                     6
//			"ACCEPTED_BY_OTHER_DRIVER",    7
//			"ACCEPTED_THEN_REJECTED",      8
//			"CLOSED",                      9
//			"RIDE_ENDED_BY_CRONE",         11
//
//			"RIDE_CANCELLED_BY_CUSTOMER",  13
//			"CANCELLED_ACCEPTED_REQUEST",  10
//			"ARRIVED",                     14
//			"NON_TIMEDOUT",                15

	
	private int ordinal;

	private EngagementStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
