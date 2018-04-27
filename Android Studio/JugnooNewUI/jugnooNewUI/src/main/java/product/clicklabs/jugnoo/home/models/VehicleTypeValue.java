package product.clicklabs.jugnoo.home.models;

/**
 * Created by Shankar on 6/23/16.
 */
public enum VehicleTypeValue {
    AUTOS(1),
    BIKES(2),
    TAXI(3),
    HELICOPTER(4),
    ERICKSHAW(5),
    TRANSPORT(6)

    ;


    private int ordinal;

    VehicleTypeValue(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
