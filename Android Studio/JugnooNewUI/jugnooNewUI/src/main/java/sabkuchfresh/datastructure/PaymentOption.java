package sabkuchfresh.datastructure;

public enum PaymentOption {
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
