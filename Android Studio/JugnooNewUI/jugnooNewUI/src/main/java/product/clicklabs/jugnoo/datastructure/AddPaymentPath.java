package product.clicklabs.jugnoo.datastructure;

public enum AddPaymentPath {
	WALLET(0),
	PAYTM_RECHARGE(1),
	ADD_PAYTM(2);

	private int ordinal;

	AddPaymentPath(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
