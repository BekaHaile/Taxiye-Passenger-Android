package product.clicklabs.jugnoo.rentals.models;

public enum RentalRideStatus {

    // Requested For End Ride
    END_RIDE_REQUESTED(1),

    // Ride ongoing
    ONGOING(2)
    ;

    private int ordinal;

    RentalRideStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
