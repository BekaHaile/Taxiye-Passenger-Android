package product.clicklabs.jugnoo.datastructure;

public enum PaymentOption {
	CASH(1),
	PAYTM(2),
	MOBIKWIK(3),
    FREECHARGE(4),
	JUGNOO_PAY(5), // payment gateway
	RAZOR_PAY(6),
	UPI_RAZOR_PAY(786),
	ICICI_UPI(7),
	MPESA(8),
	STRIPE_CARDS(9),
	ACCEPT_CARD(11),
	PAY_STACK_CARD(12),
	CORPORATE(18);

	private int ordinal;

	PaymentOption(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
