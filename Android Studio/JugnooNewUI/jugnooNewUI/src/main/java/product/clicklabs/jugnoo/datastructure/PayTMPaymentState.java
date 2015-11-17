package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo20 on 11/4/15.
 */
public enum PayTMPaymentState {

	INIT(-1),
	SUCCESS(1),
	FAILURE(0);

	private int ordinal;

	PayTMPaymentState(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
