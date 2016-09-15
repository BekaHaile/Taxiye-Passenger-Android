package product.clicklabs.jugnoo.home.models;

/**
 * Created by Shankar on 6/23/16.
 */
public enum AppRatingTypeValue {
    NEUTRAL(0),
    ACCEPTED(1),
    REJECTED(2),
    NEVER(3),
    ;


    private int ordinal;

    AppRatingTypeValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

}
