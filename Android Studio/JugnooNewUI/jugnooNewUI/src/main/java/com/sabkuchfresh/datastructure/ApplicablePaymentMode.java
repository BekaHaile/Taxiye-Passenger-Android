package com.sabkuchfresh.datastructure;

/**
 * Created by shankar on 11/22/16.
 */

public enum ApplicablePaymentMode {
	CASH(0),
	ONLINE(1),
	BOTH(2)
	;

	private int ordinal;
	ApplicablePaymentMode(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
