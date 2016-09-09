package product.clicklabs.jugnoo.datastructure;

public enum ProductType {
	AUTO(1),
	FRESH(2),
	MEALS(3),
	GROCERY(4),
	NOT_SURE(-1)
	;

	private int ordinal;

	private ProductType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
