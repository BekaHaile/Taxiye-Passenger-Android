package product.clicklabs.jugnoo.home.models;

/**
 * Created by ankit on 6/9/16.
 */
public enum RideEndFragmentMode {
    INVOICE(0)
    ;

    private int ordinal;

    RideEndFragmentMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
