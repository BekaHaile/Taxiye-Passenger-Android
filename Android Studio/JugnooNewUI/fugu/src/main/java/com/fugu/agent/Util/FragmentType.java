package com.fugu.agent.Util;

/**
 * Created by ankit on 31/05/17.
 */

public enum FragmentType {
    ALL_CHAT(1),
    MY_CHAT(2),
    USER_CHAT(3);

    private int ordinal;

    FragmentType(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
