package product.clicklabs.jugnoo.datastructure;

public enum LinkedWalletStatus {
	NO_WALLET(0),
	PAYTM_WALLET_ADDED(1),
	PAYTM_WALLET_ERROR(-1)
	;

	private int ordinal;

	private LinkedWalletStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
