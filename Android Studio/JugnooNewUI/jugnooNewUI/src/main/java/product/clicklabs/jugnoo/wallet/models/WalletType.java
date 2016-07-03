package product.clicklabs.jugnoo.wallet.models;

public enum WalletType {
	PAYTM(2),
	MOBIKWIK(3);

	private int ordinal;

	WalletType(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
