package product.clicklabs.jugnoo.home.models;

/**
 * Created by ankit on 6/23/16.
 */
public enum PokestopTypeValue {
    NORMAL(1),
    GYM(2);


    private int ordinal;

    PokestopTypeValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
