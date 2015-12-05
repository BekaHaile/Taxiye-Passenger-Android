package product.clicklabs.jugnoo.datastructure;

public enum PaymentOption {
	NO(0),
	CASH(1),
	PAYTM(2)
	;

	private int ordinal;

	PaymentOption(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
