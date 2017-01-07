package com.sabkuchfresh.retrofit.model;

/**
 * Created by socomo on 12/27/16.
 */
public enum SlotViewType {
    SLOT_TIME(0),
    SLOT_DAY(1),
    DIVIDER(2),
    HEADER(3),
    SLOT_STATUS(4),
    FEED(5)
    ;

    private int ordinal;

    SlotViewType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
