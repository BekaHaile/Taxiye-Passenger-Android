package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo on 1/5/16.
 */
public enum PriorityTipCategory {

    NO_PRIORITY_DIALOG(0),
    LOW_PRIORITY_DIALOG(1),
    HIGH_PRIORITY_DIALOG(2);

    private int ordinal;

    PriorityTipCategory(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
