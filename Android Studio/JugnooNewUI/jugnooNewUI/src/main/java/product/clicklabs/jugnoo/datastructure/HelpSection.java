package product.clicklabs.jugnoo.datastructure;

import product.clicklabs.jugnoo.R;

public enum HelpSection {
	MAIL_US(-2, R.string.send_us_email),
	CALL_US(-1, R.string.call_us),
	
	ABOUT(0, R.string.about),
	FAQ(1, R.string.faqs),
	PRIVACY(2, R.string.privacy_policy),
	TERMS(3, R.string.terms_of_use),
	FARE_DETAILS(4, R.string.rate_card),
	SCHEDULES_TNC(5, R.string.terms_of_schedule),
	WALLET_PROMOTIONS(7, R.string.wallet_promotions)
	;

	private int ordinal;
	private int nameRes;

	private HelpSection(int ordinal, int nameRes) {
		this.ordinal = ordinal;
		this.nameRes = nameRes;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	public int getName() {
		return nameRes;
	}
}
