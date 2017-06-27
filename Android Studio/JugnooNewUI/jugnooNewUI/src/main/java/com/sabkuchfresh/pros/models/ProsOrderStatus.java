package com.sabkuchfresh.pros.models;

/**
 * Created by shankar on 27/06/17.
 */

public enum ProsOrderStatus {
	UPCOMING(0, "Booking Accepted"),
	ACCEPTED(7, "Pro Assigned"),
	STARTED(1, "Job Started"),
	ENDED(2, "Job Finished")
	;

	private int ordinal;
	private String message;
	ProsOrderStatus(int ordinal, String message){
		this.ordinal = ordinal;
		this.message = message;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getMessage() {
		return message;
	}
}
