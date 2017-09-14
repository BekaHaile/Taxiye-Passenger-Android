package product.clicklabs.jugnoo.datastructure;

public enum ProductType {
	AUTO(1),
	FRESH(2),
	MEALS(3),
	GROCERY(4),
	MENUS(5),
	PAY(6),
	FEED(7),
	PROS(8),
	DELIVERY_CUSTOMER(9),
	NOT_SURE(-1)
	;

	private int ordinal;

	ProductType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
