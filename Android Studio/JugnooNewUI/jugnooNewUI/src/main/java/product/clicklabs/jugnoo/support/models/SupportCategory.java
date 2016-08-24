package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum SupportCategory {

	MAIN_MENU(1),

	RIDE_MENU(2),
	RIDE_CANCELLED_USER_MENU(3),
	RIDE_CANCELLED_DRIVER_MENU(4),

	FRESH_PENDING(5),
	FRESH_COMPLETED(6),
	FRESH_CANCELLED(7),

	MEALS_PENDING(8),
	MEALS_COMPLETED(9),
	MEALS_CANCELLED(10),
	;


	private int ordinal;

	SupportCategory(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
