package product.clicklabs.jugnoo.datastructure;

public enum LinkedWalletStatus {
	NO_WALLET(0),
	PAYTM_WALLET_ADDED(1),
	PAYTM_WALLET_ERROR(-1),
	MOBIKWIK_WALLET_ADDED(2),
	FREECHARGE_WALLET_ADDED(3)
	;

	private int ordinal;

	LinkedWalletStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
