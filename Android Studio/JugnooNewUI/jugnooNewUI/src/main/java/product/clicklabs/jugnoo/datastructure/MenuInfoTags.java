package product.clicklabs.jugnoo.datastructure;

/**
 * Created by Ankit on 4/30/16.
 */
public enum MenuInfoTags {
    GAME("game"),
    GET_A_RIDE("get_a_ride"),
    FREE_RIDES("free_rides"),
    WALLET("wallet"),
    INBOX("inbox"),
    JUGNOO_FRESH("jugnoo_fresh"),
//    PROMOTIONS("promotions"),
    HISTORY("history"),
    SCHEDULED_RIDES("scheduled_rides"),
    REFER_A_DRIVER("refer_a_driver"),
    SUPPORT("support"),
    ABOUT("about"),
    OFFERS("offers"),
    FRESH("fresh"),
    MEALS("meals"),
    GROCERY("grocery"),
    MENUS("menus"),
    PAY("pay"),
    FAVOURITE_DRIVERS("favourite_drivers"),
    BLOCKED_DRIVERS("blocked_drivers"),
    FEED("feed"),
    PROS("pros"),
    DELIVERY("delivery"),
    JUGNOO_STAR("jugnoo_star"),
    SIGNUP_TUTORIAL("signup_tutorial"),
    FUGU_SUPPORT("chat_with_jugnoo"),
    DELIVERY_CUSTOMER("delivery_customer"),
    EMAIL_SUPPORT("email_support"),
    CHANGE_LOCALE("change_locale"),
    CALL_SUPPORT("call_support"),
    TICKET_SUPPORT("ticket_support"),
    FARE_DETAILS("fare_details"),
    FREE_RIDES_NEW("free_rides_new"),
    CUSTOMER_TO_CUSTOMER("customer_to_customer_wallet"),
    ;

    private String tag;

    MenuInfoTags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
