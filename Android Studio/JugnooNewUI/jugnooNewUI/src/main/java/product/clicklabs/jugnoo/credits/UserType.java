package product.clicklabs.jugnoo.credits;

public enum UserType {
    CUSTOMER(0),
    DRIVER(1)
    ;

    private int ordinal;

    private UserType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
