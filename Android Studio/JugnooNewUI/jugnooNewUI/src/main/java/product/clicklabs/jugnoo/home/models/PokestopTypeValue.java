package product.clicklabs.jugnoo.home.models;

/**
 * Created by ankit on 6/23/16.
 */
public enum PokestopTypeValue {
    NORMAL(0),
    GYM(1);


    private int ordinal;

    PokestopTypeValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
