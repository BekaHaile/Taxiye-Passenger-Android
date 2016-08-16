package sabkuchfresh.datastructure;

public enum PassengerScreenMode {
	P_INITIAL(0),
	P_SEARCH(1),
	P_ASSIGNING(2),
	P_REQUEST_FINAL(3),
	P_DRIVER_ARRIVED(4),
	P_IN_RIDE(5),
	P_RIDE_END(6);

	private int ordinal;

	PassengerScreenMode(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
