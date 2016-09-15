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
    REFER_A_DRIVER("refer_a_driver"),
    SUPPORT("support"),
    ABOUT("about"),
    OFFERS("offers"),
    FRESH("fresh"),
    MEALS("meals"),
    GROCERY("grocery"),
    DELIVERY("delivery");

    private String tag;

    MenuInfoTags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
