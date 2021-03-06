package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum ViewType {

	LIST_VIEW(1),
	TEXT_BOX(2),
	CALL_DRIVER(3),
	TEXT_VIEW(4),
	CALL_JUGNOO(5),
	CALL_DRIVER_AND_JUGNOO(6)

	;


	private int ordinal;

	ViewType(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
