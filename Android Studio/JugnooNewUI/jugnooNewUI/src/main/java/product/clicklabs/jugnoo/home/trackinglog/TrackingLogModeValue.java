package product.clicklabs.jugnoo.home.trackinglog;

/**
 * Created by Shankar on 6/23/16.
 */
public enum TrackingLogModeValue {
    RESET("reset"),
    MOVE("move"),
    ;


    private String mode;

    TrackingLogModeValue(String mode) {
        this.mode = mode;
    }

    public String getOrdinal() {
        return mode;
    }

}
