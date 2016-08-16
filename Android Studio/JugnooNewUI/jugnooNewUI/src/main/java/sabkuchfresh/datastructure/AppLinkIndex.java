package sabkuchfresh.datastructure;

public enum AppLinkIndex {

	HISTORY(1), // opens History Screen

	NOTIFICATION_CENTER(2), // opens Inbox Screen,

	INVITE_AND_EARN(3), // opens Invite and Earn Screen

	JUGNOO_CASH(4), // opens Jugnoo Cash Screen

	PLAY_STORE(5), // opens Play store app

	ACCOUNT(6), // opens Account Screen

	SUPPORT(7), // opens Support Screen

	FATAFAT_PAGE(8), // opens Fatafat Screen

	MEALS_PAGE(9) // opens Fresh Screen

	;

	private int ordinal;

	AppLinkIndex(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
