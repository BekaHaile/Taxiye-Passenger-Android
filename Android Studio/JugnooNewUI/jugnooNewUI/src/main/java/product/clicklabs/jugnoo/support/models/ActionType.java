package product.clicklabs.jugnoo.support.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum ActionType {

	NEXT_LEVEL(1),
	INAPP_CALL(2),
	GENERATE_FRESHDESK_TICKET(3),
	;


	private int ordinal;

	ActionType(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
