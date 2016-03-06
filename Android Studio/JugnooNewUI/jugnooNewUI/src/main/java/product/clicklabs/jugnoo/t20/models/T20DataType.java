package product.clicklabs.jugnoo.t20.models;

/**
 * Created by shankar on 1/21/16.
 */
public enum T20DataType {

	SCHEDULE(1),
	SELECTION(2),
	TEAM(3)
	;


	private int ordinal;

	T20DataType(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
