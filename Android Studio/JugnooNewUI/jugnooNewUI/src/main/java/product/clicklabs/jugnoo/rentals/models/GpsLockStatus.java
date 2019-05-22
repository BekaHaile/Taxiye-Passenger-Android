package product.clicklabs.jugnoo.rentals.models;

public enum GpsLockStatus {

    LOCK(0),
    UNLOCK(1),
    REQ_START_RIDE_UNLOCK(2),
    START_RIDE_UNLOCK(3),
    REQ_IN_RIDE_LOCK(4),
    IN_RIDE_LOCK(5),
    REQ_IN_RIDE_UNLOCK(6),
    IN_RIDE_UNLOCK(7),
    REQ_END_RIDE_LOCK(8),
    END_RIDE_LOCK(9),
    END_RIDE_FAILED(10),
    LOCK_FAILED(11),
    UNLOCK_FAILED(12);





//    REQ_START_RIDE_UNLOCK (0),
//    START_RIDE_UNLOCK (1),
//    START_RIDE_UNLOCK_FAILED (2),
//    REQ_IN_RIDE_LOCK (3),
//    IN_RIDE_LOCK (4),
//    IN_RIDE_LOCK_FAILED (5),
//    REQ_IN_RIDE_UNLOCK ( 6),
//    IN_RIDE_UNLOCK ( 7),
//    IN_RIDE_UNLOCK_FAILED ( 8),
//    REQ_END_RIDE_LOCK ( 9),
//    END_RIDE_LOCK ( 10),
//    END_RIDE_FAILED ( 11);

    private int ordinal;

    GpsLockStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }


}
