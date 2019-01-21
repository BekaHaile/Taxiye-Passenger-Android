package com.fugu.agent.Util;

/**
 * Created by ankit on 31/05/17.
 */

public enum TypingMode {
    TYPED(0),
    TYPING_START(1),
    TYPING_STOP(2);

    private int ordinal;

    TypingMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
