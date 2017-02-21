package product.clicklabs.jugnoo.datastructure;

/**
 * Created by ankit on 18/02/17.
 */

public enum StarPurchaseType {
    PURCHARE(0),
    UPGRADE(1),
    RENEW(2);

    private int ordinal;

    private StarPurchaseType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

}
