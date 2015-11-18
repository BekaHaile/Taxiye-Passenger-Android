package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo20 on 11/4/15.
 */
public enum PaytmPaymentState {

	INIT(-1),
	SUCCESS(1),
	FAILURE(0);

	private int ordinal;

	PaytmPaymentState(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
