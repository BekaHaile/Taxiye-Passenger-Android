package product.clicklabs.jugnoo.home.models;

public enum VehicleId {
	AUTO(1),
	BIKE(2)
	;

	private int ordinal;

	private VehicleId(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
