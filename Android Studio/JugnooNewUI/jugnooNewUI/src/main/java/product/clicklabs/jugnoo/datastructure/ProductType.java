package product.clicklabs.jugnoo.datastructure;

public enum ProductType {
	AUTO(0),
	FRESH(1),
	MEALS(2)
	;

	private int ordinal;

	private ProductType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
