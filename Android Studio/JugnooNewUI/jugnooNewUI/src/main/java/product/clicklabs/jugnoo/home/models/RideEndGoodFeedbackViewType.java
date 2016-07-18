package product.clicklabs.jugnoo.home.models;

/**
 * Created by ankit on 7/17/16.
 */
public enum RideEndGoodFeedbackViewType {
    RIDE_END_IMAGE_1(1),
    RIDE_END_IMAGE_2(2),
    RIDE_END_IMAGE_3(3),
    RIDE_END_IMAGE_4(4),
    RIDE_END_GIF(5),
    RIDE_END_NONE(6);


    private int ordinal;

    RideEndGoodFeedbackViewType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
