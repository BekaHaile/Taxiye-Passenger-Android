package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 6/15/15.
 */
public enum CommonFlags {

    GET_ORDERTRACK_SCREEN(1),
    GET_ORDER_RATING_SCREEN(2),

    LOW_PRIORITY_POPUP(101),
    MEDIUM_PRIORITY_POPUP(102),
    HIGH_PRIORITY_POPUP(103),

    PAYMENT_BACKPRESSED(200),
    PAYMENT_SUCCESS(201),
    PAYMENT_FAILED(202),
    PAYMENT_ERROR(203)
    ;

    private int ordinal;

    private CommonFlags(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
