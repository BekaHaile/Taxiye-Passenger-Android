package product.clicklabs.jugnoo.rentals.models;

public enum GpsLockStatus {

    LOCK(0),
    UNLOCK(1),
    REQ_LOCK(2),
    REQ_UNLOCK(3),
    LOCK_FAILED(4),
    UNLOCK_FAILED(5),
    REQ_END_RIDE_LOCK(6),
    END_RIDE_LOCK(7),
    END_RIDE_LOCK_FAILED(8)
    ;

    private int ordinal;

    GpsLockStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }


}
