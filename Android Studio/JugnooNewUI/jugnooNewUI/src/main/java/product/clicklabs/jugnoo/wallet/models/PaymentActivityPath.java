package product.clicklabs.jugnoo.wallet.models;

public enum PaymentActivityPath {
	WALLET(0),
	WALLET_ADD_MONEY(1),
	ADD_WALLET(2);

	private int ordinal;

	PaymentActivityPath(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
