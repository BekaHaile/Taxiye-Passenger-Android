package com.sabkuchfresh.home;

/**
 * Created by ankit on 04/11/16.
 */

public enum OrderStatus {
    ORDER_COMPLETED(2),
    CASH_RECEIVED(6),
    RETURN(7)
    ;

    private int ordinal;

    private OrderStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
