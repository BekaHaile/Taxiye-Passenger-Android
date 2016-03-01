package product.clicklabs.jugnoo.datastructure;

public enum DialogErrorType {
	NO_NET(0),
	CONNECTION_LOST(1),
	SERVER_ERROR(2)
	;

	private int ordinal;

	DialogErrorType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
