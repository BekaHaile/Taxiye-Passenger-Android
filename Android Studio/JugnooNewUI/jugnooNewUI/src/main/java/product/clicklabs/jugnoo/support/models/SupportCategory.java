package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum SupportCategory {

	MAIN_MENU(1),

	RIDE_MENU(2),
	RIDE_CANCELLED_USER_MENU(3),
	RIDE_CANCELLED_DRIVER_MENU(4),

	FRESH_COMPLETED(5),
	FRESH_PENDING(6),
	FRESH_CANCELLED(7),

	MEALS_COMPLETED(8),
	MEALS_PENDING(9),
	MEALS_CANCELLED(10),


	RIDE_ACCEPT(11),
	RIDE_ARRIVED(12),
	RIDE_STARTED(13),

	GROCERY(14),
	GROCERY_PENDING(15),
	GROCERY_CANCELLED(16),

	MENUS(17),
	MENUS_PENDING(18),
	MENUS_CANCELLED(19),

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
