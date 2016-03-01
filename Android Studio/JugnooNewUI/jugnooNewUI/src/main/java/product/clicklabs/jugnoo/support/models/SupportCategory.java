package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum SupportCategory {

	MAIN_MENU(1),
	RIDE_MENU(2)
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
