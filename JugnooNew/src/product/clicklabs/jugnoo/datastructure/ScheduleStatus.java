package product.clicklabs.jugnoo.datastructure;

public enum ScheduleStatus {
	IN_PROCESS(0), 
	CONFIRMED(1),
	CANCELED(2)
	;

	private int ordinal;

	private ScheduleStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
