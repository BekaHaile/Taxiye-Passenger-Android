package product.clicklabs.jugnoo.rentals.models;

public enum RentalInRideValue {

    END_RIDE(8),
    UNLOCK_RIDE(6),
    LOCK_RIDE(4)
    ;

    private int ordinal;

    RentalInRideValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

}


