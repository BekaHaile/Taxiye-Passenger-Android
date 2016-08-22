package product.clicklabs.jugnoo.datastructure;

public enum ProductType {
	AUTO(1),
	FRESH(2),
	MEALS(3)
	;

	private int ordinal;

	private ProductType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
