package com.sabkuchfresh.pros.models;

/**
 * Created by shankar on 27/06/17.
 */

public enum ProsOrderStatus {
	UPCOMING(0),
	ACCEPTED(7),
	STARTED(1),
	ENDED(2),
	ARRIVED(4),
	UNASSIGNED(6)
	;

	private int ordinal;
	ProsOrderStatus(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}



//	define(exports.jobStatus, "UPCOMING", 0);
//	define(exports.jobStatus, "STARTED", 1);
//	define(exports.jobStatus, "ENDED", 2);
//	define(exports.jobStatus, "FAILED", 3);
//	define(exports.jobStatus, "ARRIVED", 4);
//	define(exports.jobStatus, "PARTIAL", 5);
//	define(exports.jobStatus, "UNASSIGNED", 6);
//	define(exports.jobStatus, "ACCEPTED", 7);
//	define(exports.jobStatus, "DECLINE", 8);
//	define(exports.jobStatus, "CANCEL", 9);
//	define(exports.jobStatus, "DELETED", 10);
//	define(exports.jobStatus, "IGNORED", 11);
//	define(exports.jobStatus, "SEEN_BY_AGENT", 12);
}
