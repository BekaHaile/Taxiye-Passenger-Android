package product.clicklabs.jugnoo.wallet.models;

/**
 * Created by socomo20 on 11/20/15.
 */
public enum WalletAddMoneyState {
	INIT(-1),
	SUCCESS(1),
	FAILURE(0);

	private int ordinal;

	WalletAddMoneyState(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
