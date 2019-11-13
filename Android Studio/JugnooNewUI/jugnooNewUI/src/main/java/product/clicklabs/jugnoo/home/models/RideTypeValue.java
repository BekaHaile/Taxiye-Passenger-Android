package product.clicklabs.jugnoo.home.models;

/**
 * Created by ankit on 6/23/16.
 */
public enum RideTypeValue {
    NORMAL(0),
    POOL(2),

    // todo BIKE_RENTAL ride type
    BIKE_RENTAL(5)
    ;


    private int ordinal;

    RideTypeValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
