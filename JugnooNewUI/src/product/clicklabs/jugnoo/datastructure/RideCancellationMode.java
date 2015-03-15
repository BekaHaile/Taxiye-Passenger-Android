package product.clicklabs.jugnoo.datastructure;

public enum RideCancellationMode {
	CURRENT_RIDE(1),
	SCHEDULE_RIDE(2)
	;

	private int ordinal;

	private RideCancellationMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
