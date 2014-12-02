package product.clicklabs.jugnoo.datastructure;

public enum HelpSection {
	MAIL_US(-2),
	CALL_US(-1),
	
	ABOUT(0), 
	FAQ(1),
	PRIVACY(2),
	TERMS(3),
	FARE_DETAILS(4)
	;

	private int ordinal;

	private HelpSection(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
