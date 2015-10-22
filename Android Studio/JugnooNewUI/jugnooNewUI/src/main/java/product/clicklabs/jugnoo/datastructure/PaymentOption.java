package product.clicklabs.jugnoo.datastructure;

public enum PaymentOption {
	CASH(2),
	PAYTM(1)
	;

	private int ordinal;

	PaymentOption(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
