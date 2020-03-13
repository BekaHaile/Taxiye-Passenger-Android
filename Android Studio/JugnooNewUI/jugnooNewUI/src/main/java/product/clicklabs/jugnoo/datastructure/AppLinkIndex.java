package product.clicklabs.jugnoo.datastructure;

public enum AppLinkIndex {

	INVITE_AND_EARN(0), // opens Invite and Earn Screen

	JUGNOO_CASH(1), // opens Jugnoo Cash Screen

	PROMOTIONS(2), // opens Promotions Screen

	RIDE_HISTORY(3), // opens Ride History Screen

	FARE_DETAILS(4), // opens Fare Details Screen

	SUPPORT(5), // opens Support Screen

	ABOUT(6), // opens About Screen

	ACCOUNT(7), // opens Account Screen

	NOTIFICATION_CENTER(8), // opens Notification center Screen,

	GAME_PAGE(9), // opens Game Screen (eg. T20)

	FRESH_PAGE(10), // opens Fresh Screen

	MEAL_PAGE(11), // open Meal Screen

	DELIVERY_PAGE(12), // open Delivery Screen

	AUTO_PAGE(13), // open Auto Screen

	GROCERY_PAGE(14), // open Grocery Screen

	CHAT_PAGE(15), // open Chat page

	MENUS_PAGE(16), // open Menus Screen

	PAY_PAGE(17), // open Pay Screen

	SUBSCRIPTION_PLAN_OPTION_SCREEN(18), //18 Subscription_Plan_Option_Screen

	JUGNOO_STAR(19), //19 Jugnoo_Star_Screen

	WALLET_TRANSACTIONS(20), //20 Jugnoo_Star_Screen

	OPEN_COUPONS_DIALOG(21), //open coupons dialog

	FEED_PAGE(22), // open Feed Screen\

	OPEN_JEANIE(23), // open Jeanie

	PROS_PAGE(24), // open Pros Screen

	DELIVERY_CUSTOMER_PAGE(25), // open Delivery Customer Screen

	FATAFAT_PAY_VIA_CHAT(26), // open Fatafat chat pay

	FUGU_SUPPORT(27),

	TICKET_SUPPORT(28),

	REINVITE_USERS(29),

	;

	private int ordinal;

	AppLinkIndex(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
